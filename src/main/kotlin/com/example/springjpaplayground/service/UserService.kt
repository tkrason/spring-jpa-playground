package com.example.springjpaplayground.service

import com.example.springjpaplayground.entity.User
import com.example.springjpaplayground.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository,
) {

    @Transactional
    fun createMockUser() {
        val user = User(name = "John", surname = "Doe")
        userRepository.save(user)
    }

    @Transactional
    fun createMockUsers() {
        val listOfUsers = (0..99).map {
            User(name = "$it", surname = "surname - $it")
        }

        // With usage of spring.jpa.properties.hibernate.jdbc.batch_size=50
        // preparing 4 JDBC statements;
        // executing 3 JDBC statements; <-- getting sequence from DB
        // executing 2 JDBC batches;
        userRepository.saveAll(listOfUsers)
    }
}
