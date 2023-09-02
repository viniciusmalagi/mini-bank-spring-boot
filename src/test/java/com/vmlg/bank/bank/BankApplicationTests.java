package com.vmlg.bank.bank;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.vmlg.bank.bank.domain.user.UserType;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.dtos.UserDTO;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BankApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	<T> StatusAssertions testWebClientPost(String uriPost, T dto){
		return webTestClient.post()
		.uri(uriPost)
		.bodyValue(dto)
		.exchange()
		.expectStatus();
	}

	void testCreateUserSuccess(UserDTO userDTO) {
		testWebClientPost("/users", userDTO)
		.isCreated()
		.expectBody()
		.jsonPath("$").exists()
		.jsonPath("$.length()").isEqualTo(8)
		.jsonPath("$.firstName").isEqualTo(userDTO.firstName())
		.jsonPath("$.lastName").isEqualTo(userDTO.lastName())
		.jsonPath("$.document").isEqualTo(userDTO.document())
		.jsonPath("$.balance").isEqualTo(userDTO.balance())
		.jsonPath("$.email").isEqualTo(userDTO.email())
		.jsonPath("$.password").isEqualTo(userDTO.password())
		.jsonPath("$.userType").isEqualTo(userDTO.userType().toString());
	}

	void testCreateDuplicateUserFailure(UserDTO firstUserDTO, UserDTO secondUserDTO){
		testCreateUserSuccess(firstUserDTO);
		testWebClientPost("/users", secondUserDTO).isBadRequest();
	}

	UUID testGetUUIDByRequest(String uri, UserDTO userDTO){
		String requestUuid = (String) testWebClientPost(uri, userDTO)
		.isCreated()
		.expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
		.returnResult()
		.getResponseBody().get("id");
		return UUID.fromString(requestUuid);
	}

	TransactionDTO testCreateTransactionDTO(BigDecimal amount,UserDTO senderDto, UserDTO receiverDto){
		UUID senderUuid = testGetUUIDByRequest(
			"/users", senderDto
		);
		UUID receiverUuid = testGetUUIDByRequest(
			"/users", receiverDto
		);
		return new TransactionDTO(
			amount,
			senderUuid, receiverUuid
		);
	}

	StatusAssertions testPostTransaction(TransactionDTO transactionDTO){
		return testWebClientPost(
			"/transactions", transactionDTO
		);
	}

	@Test
	void testCreateCommonUserSuccess() {
		var commonUserDTO = new UserDTO(
			"John", "Eschi",
			"100200300", new BigDecimal(122),
			"john.eschi@user.com",
			"passwd",
			UserType.COMMON
		);
		testCreateUserSuccess(commonUserDTO);
	}

	@Test
	void testCreateCommonUserFailure() {
		var commonUserDTO = new UserDTO(
			"", "",
			"", new BigDecimal(1),
			"",
			"",
			UserType.COMMON
		);
		testWebClientPost("/users", commonUserDTO).is5xxServerError();
	}

	@Test
	void testCreateMerchantUserSuccess() {
		var merchantUserDTO = new UserDTO(
			"Dynamo", "Enterprise",
			"100200300400", new BigDecimal(5000),
			"dynamo@user.com",
			"passwd",
			UserType.MERCHANT
		);
		testCreateUserSuccess(merchantUserDTO);
	}

	@Test
	void testCreateUserWithDuplicateEmailFailure() {
		var firstUserDTO = new UserDTO(
			"Christian", "Henderson",
			"700800900", new BigDecimal(5000),
			"same@user.com",
			"passwd",
			UserType.COMMON
		);
		var secondUserDTO = new UserDTO(
			"Thales", "Beagle",
			"100150300", new BigDecimal(20),
			"same@user.com",
			"passwd",
			UserType.COMMON
		);
		testCreateDuplicateUserFailure(firstUserDTO, secondUserDTO);
	}

	@Test
	void testCreateUserWithDuplicateDocumentFailure() {
		var firstUserDTO = new UserDTO(
			"Andrea", "Buzzer",
			"200300400", new BigDecimal(5000),
			"andrea.buzzer@user.com",
			"passwd",
			UserType.COMMON
		);
		var secondUserDTO = new UserDTO(
			"Eliza", "Turner",
			"200300400", new BigDecimal(20),
			"eliza.turner@user.com",
			"passwd",
			UserType.COMMON
		);
		testCreateDuplicateUserFailure(firstUserDTO, secondUserDTO);
	}

	@Test
	void testCreateTransactionCommonToMerchantSuccess(){
		UserDTO senderDto = new UserDTO(
			"Gabriel", "Winchester", "300200100",
			new BigDecimal(100), "gab@user.com",
			"passwd", UserType.COMMON
		);
		UserDTO receiverDto = new UserDTO(
				"Monica", "Lau", "900200300",
				new BigDecimal(10), "monica@user.com",
				"passwd", UserType.COMMON
		);
		BigDecimal amount = new BigDecimal(75);
		TransactionDTO transactionDTO = testCreateTransactionDTO(amount, senderDto, receiverDto);
		testPostTransaction(transactionDTO)
		.isOk()
		.expectBody()
		.jsonPath("$").exists()
		.jsonPath("$.length()").isEqualTo(5)
		.jsonPath("$.amount").isEqualTo(transactionDTO.value())
		.jsonPath("$.sender.id").isEqualTo(transactionDTO.senderId().toString())
		.jsonPath("$.receiver.id").isEqualTo(transactionDTO.receiverId().toString());
	}

	@Test
	void testCreateTransactionMerchantToCommonFailure(){
		UserDTO senderDto = new UserDTO(
			"Bronx", "United", "12345678",
			new BigDecimal(1000), "bronx@user.com",
			"passwd", UserType.MERCHANT
		);
		UserDTO receiverDto = new UserDTO(
				"Laura", "Lee", "90009000",
				new BigDecimal(155), "laura@user.com",
				"passwd", UserType.COMMON
		);
		BigDecimal amount = new BigDecimal(250);
		TransactionDTO transactionDTO = testCreateTransactionDTO(amount, senderDto, receiverDto);
		testPostTransaction(transactionDTO)
		.is5xxServerError();
	}

	@Test
	void testCreateTransactionWithAnInsufficientBalanceFailure(){
		UserDTO senderDto = new UserDTO(
			"Louis", "Kimchi", "55556666",
			new BigDecimal(5000), "louis@user.com",
			"passwd", UserType.COMMON
		);
		UserDTO receiverDto = new UserDTO(
				"Erick", "Pool", "80008000",
				new BigDecimal(155), "erickp@user.com",
				"passwd", UserType.COMMON
		);
		BigDecimal amount = new BigDecimal(5250);
		TransactionDTO transactionDTO = testCreateTransactionDTO(amount, senderDto, receiverDto);
		testPostTransaction(transactionDTO)
		.is5xxServerError();
	}
}
