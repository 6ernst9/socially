package com.robert.ernst.account;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AccountControllerTests extends BaseIntegrationTest{
	@Test
	void getAccountByIdSucceeds() {
		stubFindByIdMethod();

		webTestClient.get()
				.uri("/get-account/1")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-FI-SY-PATH", "account.get-account")
				.headers(getDefaultHeaders())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("username123")
				.jsonPath("$.email").isEqualTo("mock_email123@mock.com")
				.jsonPath("$.fullName").isEqualTo("Jane Doe")
				.jsonPath("$.phoneNumber").isEqualTo("07xxxxxxxx");
	}

	@Test
	void getAccountByIdFails() {
		stubFindByIdMethodError();

		webTestClient.get()
				.uri("/get-account/1")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-FI-SY-PATH", "account.get-account")
				.headers(getDefaultHeaders())
				.exchange()
				.expectStatus().is5xxServerError()
				.expectBody()
				.jsonPath("$.message").isEqualTo("User not found");
	}

	@Test
	void getAccountByIdFailsIfPathIsAbsent() {
		webTestClient.get()
				.uri("/get-account/1")
				.accept(MediaType.APPLICATION_JSON)
				.headers(getDefaultHeaders())
				.exchange()
				.expectStatus().is5xxServerError()
				.expectBody()
				.jsonPath("$.message").isEqualTo("path cannot be null");
	}

	@Test
	void searchByTermSucceeds(){
		stubFindAllMethod();

		webTestClient.get()
				.uri("/search-by-term/cris")
				.accept(MediaType.APPLICATION_JSON)
				.header("X-FI-SY-PATH", "account.search-by-term")
				.headers(getDefaultHeaders())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[0].username").isEqualTo("cristi123")
				.jsonPath("$[1].username").isEqualTo("robert123");
	}
}
