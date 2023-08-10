package com.example.springjpaplayground.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class HelloWorldController {

    @GetMapping("/hello-world")
    fun helloWorld() = "Hello world"
}
