package com.maschnetwork.testcontainers

import com.maschnetwork.testcontainers.api.Team
import com.maschnetwork.testcontainers.config.RedisInitializer
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.GenericContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [RedisInitializer::class])
class TestContainersTest {

	@LocalServerPort
	protected var port: Int = 0

	@Autowired
	private lateinit var redisTemplate : RedisTemplate<String, Team>

	@AfterEach
	fun clearRedis() {
		redisTemplate.connectionFactory?.connection?.flushAll()
	}

	@Test
	fun `should return 201 when creating a team`() {
		val response = givenPostRequestSuccess("something")

		assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED.value())
	}

	@Test
	fun `should get team from cache`() {
		givenPostRequestSuccess("something")

		val response = given()
				.port(port)
				.get("/api/teams/something")

		val team = response.`as`(Team::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
		assertThat(team.name).isEqualTo("something")
	}

	@Test
	fun `should get no team from cache`() {
		val response = given()
				.port(port)
				.get("/api/teams/something")


		assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value())
	}

	private fun givenPostRequestSuccess(name : String = "test"): Response {
		return given()
				.contentType(ContentType.JSON)
				.port(port)
				.body(mapOf("name" to name))
				.post("/api/teams")
	}
}
