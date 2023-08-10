package com.example.springjpaplayground.controller

import com.example.springjpaplayground.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class UserController @Autowired constructor(
    val userService: UserService,
) {
    @PostMapping("/mock-user")
    fun createMockUser() = userService.createMockUser()

    @PostMapping("/mock-users")
    fun createMockUsers() = userService.createMockUsers()
}
