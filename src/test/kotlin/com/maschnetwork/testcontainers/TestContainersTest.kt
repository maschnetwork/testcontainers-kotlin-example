package com.maschnetwork.testcontainers

import com.maschnetwork.testcontainers.api.Team
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestContainersTest {

	@LocalServerPort
	protected var port: Int = 0


	@Test
	fun `should return 201 when creating a team`() {
		val response = givenPostRequestSuccess("something")

		val team = response.`as`(Team::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED.value())
		assertThat(team.name).isEqualTo("something")
	}



	@Test
	fun `should get teams from cache when team is present`() {
		givenPostRequestSuccess("something")

		val response = given()
				.port(port)
				.get("/api/teams/something")

		val team = response.`as`(Team::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
		assertThat(team.name).isEqualTo("something")
	}

	private fun givenPostRequestSuccess(name : String = "test"): Response {
		return given()
				.contentType(ContentType.JSON)
				.port(port)
				.body(mapOf("name" to name))
				.post("/api/teams")
	}



}
