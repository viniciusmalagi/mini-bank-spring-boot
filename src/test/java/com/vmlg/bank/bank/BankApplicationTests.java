package com.vmlg.bank.bank;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.vmlg.bank.bank.domain.user.UserType;
import com.vmlg.bank.bank.dtos.AuthenticationDTO;
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
	<T> StatusAssertions testWebClientPostAuth(String uriPost, T dto, String token){
		return webTestClient.post()
		.uri(uriPost)
		.headers(http -> http.setBearerAuth(token))
		.bodyValue(dto)
		.exchange()
		.expectStatus();
	}

	StatusAssertions testWebClientGetAuth(String uriGet, String token){
		return webTestClient.get()
		.uri(uriGet)
		.headers(http -> http.setBearerAuth(token))
		.exchange()
		.expectStatus();
	}

	void testCreateUserSuccess(UserDTO userDTO) {
		testWebClientPost("/auth/register", userDTO)
		.isCreated()
		.expectBody()
		.jsonPath("$").exists()
		.jsonPath("$.length()").isEqualTo(14)
		.jsonPath("$.firstName").isEqualTo(userDTO.firstName())
		.jsonPath("$.lastName").isEqualTo(userDTO.lastName())
		.jsonPath("$.document").isEqualTo(userDTO.document())
		.jsonPath("$.balance").isEqualTo(userDTO.balance())
		.jsonPath("$.email").isEqualTo(userDTO.email())
		.jsonPath("$.userType").isEqualTo(userDTO.userType().toString());
	}

	void testCreateDuplicateUserFailure(UserDTO firstUserDTO, UserDTO secondUserDTO){
		testCreateUserSuccess(firstUserDTO);
		testWebClientPost("/auth/register", secondUserDTO).isBadRequest();
	}

	<T> String testGetAttributeByRequest(String uri, String attr, T dto){
		String attribute = (String) testWebClientPost(uri, dto)
		.is2xxSuccessful()
		.expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
		.returnResult()
		.getResponseBody().get(attr);
		return attribute;
	}

	UUID testGetUUIDByRequest(String uri, UserDTO userDTO){
		return UUID.fromString(testGetAttributeByRequest(uri, "id", userDTO));
		// String requestUuid = (String) testWebClientPost(uri, userDTO)
		// .isCreated()
		// .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
		// .returnResult()
		// .getResponseBody().get("id");
		// return UUID.fromString(requestUuid);
	}
	String testGetTokenByRequest(String uri, AuthenticationDTO authenticationDTO){
		return testGetAttributeByRequest(uri, "token", authenticationDTO);
		// String requestToken = (String) testWebClientPost(uri, authenticationDTO)
		// .isCreated()
		// .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {})
		// .returnResult()
		// .getResponseBody().get("token");
		// return requestToken;
	}

	TransactionDTO testCreateTransactionDTO(BigDecimal amount,UserDTO senderDto, UserDTO receiverDto){
		UUID senderUuid = testGetUUIDByRequest(
			"/auth/register", senderDto
		);
		UUID receiverUuid = testGetUUIDByRequest(
			"/auth/register", receiverDto
		);
		return new TransactionDTO(
			amount,
			senderUuid, receiverUuid
		);
	}

	StatusAssertions testPostTransaction(TransactionDTO transactionDTO, String token){
		return testWebClientPostAuth(
			"/transactions", transactionDTO, token
		);
	}

	StatusAssertions testLogin(AuthenticationDTO authenticationDTO){
		return testWebClientPost("/auth/login", authenticationDTO);
	}

	StatusAssertions testGetUsers(UserDTO userDTO) {
		testCreateUserSuccess(userDTO);
		AuthenticationDTO userAuthDto = new AuthenticationDTO(userDTO.email(), userDTO.password());
		String token = testGetTokenByRequest("/auth/login", userAuthDto);
		return testWebClientGetAuth("/users", token);
	}

	Pair<StatusAssertions, TransactionDTO> testCreateTransaction(UserDTO senderDto, UserDTO receiverDto, BigDecimal amount){
		TransactionDTO transactionDTO = testCreateTransactionDTO(amount, senderDto, receiverDto);
		AuthenticationDTO senderAuthDto = new AuthenticationDTO(senderDto.email(), senderDto.password());
		String senderToken = testGetTokenByRequest("auth/login", senderAuthDto);
		return Pair.of(testPostTransaction(transactionDTO, senderToken), transactionDTO);
	}

	// StatusAssertions testCreateTransaction(UserDTO senderDto, UserDTO receiverDto, BigDecimal amount){
	// 	TransactionDTO transactionDTO = testCreateTransactionDTO(amount, senderDto, receiverDto);
	// 	AuthenticationDTO senderAuthDto = new AuthenticationDTO(senderDto.email(), senderDto.password());
	// 	String senderToken = testGetTokenByRequest("auth/login", senderAuthDto);
	// 	return testPostTransaction(transactionDTO, senderToken);
	// }

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
			"", new BigDecimal(10),
			"",
			"",
			UserType.COMMON
		);
		testWebClientPost("/auth/register", commonUserDTO).is5xxServerError();
	}

	@Test
	void testCreateCommonUserWithNegativeBalanceFailure() {
		var commonUserDTO = new UserDTO(
			"Michael", "Batista",
			"90007600", new BigDecimal(-200),
			"michael@user.com",
			"passwd",
			UserType.COMMON
		);
		testWebClientPost("/auth/register", commonUserDTO).is5xxServerError();
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
	void testCreateAdminUserSuccess() {
		var adminUserDTO = new UserDTO(
			"Admin", "Admin",
			"00001001", new BigDecimal(5000),
			"root@admin.com",
			"root",
			UserType.ADMIN
		);
		testCreateUserSuccess(adminUserDTO);
	}

	@Test
	void testGetUsersByAdminUserSuccess() {
		var adminUserDTO = new UserDTO(
			"root", "root",
			"10001001", new BigDecimal(5000),
			"root@root.com",
			"root",
			UserType.ADMIN
		);
		testGetUsers(adminUserDTO).is2xxSuccessful();
	}

	@Test
	void testGetUsersByAdminUserFailure() {
		var adminUserDTO = new UserDTO(
			"Maria", "Lagos",
			"12301001", new BigDecimal(2000),
			"marial@root.com",
			"password",
			UserType.COMMON
		);
		testGetUsers(adminUserDTO).is4xxClientError();
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
	void testLoginSuccess(){
		UserDTO userDTO = new UserDTO(
			"Leon", "Gucci", "809821745",
			new BigDecimal(500), "leon@user.com",
			"passwd", UserType.COMMON
		);
		AuthenticationDTO authenticationDTO = new AuthenticationDTO(
			userDTO.email(), userDTO.password()
		);
		testCreateUserSuccess(userDTO);
		testLogin(authenticationDTO)
		.isOk()
		.expectBody()
		.jsonPath("$").exists()
		.jsonPath("$.length()").isEqualTo(1)
		.jsonPath("$.token").exists();
	}

	@Test
	void testLoginFailure(){
		AuthenticationDTO authenticationDTO = new AuthenticationDTO(
			"piter@user.com", "myPassword"
		);
		testLogin(authenticationDTO)
		.is5xxServerError();
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
		Pair<StatusAssertions, TransactionDTO> pair = testCreateTransaction(senderDto, receiverDto, new BigDecimal(75));
		pair.getFirst().isOk()
		.expectBody()
		.jsonPath("$").exists()
		.jsonPath("$.length()").isEqualTo(5)
		.jsonPath("$.amount").isEqualTo(pair.getSecond().value())
		.jsonPath("$.sender.id").isEqualTo(pair.getSecond().senderId().toString())
		.jsonPath("$.receiver.id").isEqualTo(pair.getSecond().receiverId().toString());
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
		testCreateTransaction(senderDto, receiverDto, new BigDecimal(250))
		.getFirst()
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
		testCreateTransaction(senderDto, receiverDto, new BigDecimal(5250))
		.getFirst()
		.is5xxServerError();
	}

	@Test
	void testCreateTransactionWithAnInsufficientNegativeBalanceFailure(){
		UserDTO senderDto = new UserDTO(
			"Lays", "Peterson", "30001000",
			new BigDecimal(500.50), "lays@user.com",
			"passwd", UserType.COMMON
		);
		UserDTO receiverDto = new UserDTO(
				"Robson", "Peri", "800056000",
				new BigDecimal(3000), "robson@user.com",
				"passwd", UserType.MERCHANT
		);
		testCreateTransaction(senderDto, receiverDto, new BigDecimal(-50))
		.getFirst()
		.is5xxServerError();
	}
}
