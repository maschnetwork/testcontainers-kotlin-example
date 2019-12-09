package com.maschnetwork.testcontainers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class TestContainersApplication

fun main(args: Array<String>) {
	runApplication<TestContainersApplication>(*args)
}
