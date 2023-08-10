package com.example.springjpaplayground

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestSpringJpaPlaygroundApplication

fun main(args: Array<String>) {
    fromApplication<SpringJpaPlaygroundApplication>().with(TestSpringJpaPlaygroundApplication::class).run(*args)
}
