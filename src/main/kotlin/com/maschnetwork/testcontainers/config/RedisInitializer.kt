package com.maschnetwork.testcontainers.config

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.GenericContainer

internal class RedisInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val redisContainer = object : GenericContainer<Nothing>("redis:3-alpine") {
        init {
            withExposedPorts(6379)
        }
    }

    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        val env = configurableApplicationContext.environment
        env.propertySources.addFirst(
            MapPropertySource(
                "testcontainers", getProperties()
            )
        )
    }

    private fun getProperties(): Map<String, String> {
        redisContainer.start()
        return mapOf(
            "spring.redis.host" to redisContainer.containerIpAddress,
            "spring.redis.port" to redisContainer.firstMappedPort.toString()
        )
    }
}