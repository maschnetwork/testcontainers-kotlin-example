package com.maschnetwork.testcontainers

import com.maschnetwork.testcontainers.config.RedisInitializer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.GenericContainer

fun main(args: Array<String>) {
    val application = createSpringApplication()
    application?.addInitializers(RedisInitializer())
    application?.run(*args)
}

fun createSpringApplication(): SpringApplication? {
    return SpringApplication(TestContainersApplication::class.java)
}


