package com.maschnetwork.testcontainers

import com.maschnetwork.testcontainers.api.Team
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.GenericContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [TestContainersTest.Initializer::class])
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


	companion object {
		val redisContainer = object : GenericContainer<Nothing>("redis:3-alpine") {
			init {
				withExposedPorts(6379)
			}
		}
	}

	internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
		override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
			redisContainer.start()

			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					configurableApplicationContext, "spring.redis.host=${redisContainer.containerIpAddress}")

			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					configurableApplicationContext, "spring.redis.port=${redisContainer.firstMappedPort}")
		}
	}

}
