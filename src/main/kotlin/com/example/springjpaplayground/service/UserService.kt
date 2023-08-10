package com.example.springjpaplayground.service

import com.example.springjpaplayground.model.Tag
import com.example.springjpaplayground.model.TagColor
import com.example.springjpaplayground.model.User
import com.example.springjpaplayground.model.toEntity
import com.example.springjpaplayground.model.toModel
import com.example.springjpaplayground.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository,
) {

    @Transactional
    fun createMockUser(): User {
        val user = User(
            id = null,
            firstName = "John",
            surname = "Doe",
            tags = setOf(
                Tag(id = null, color = TagColor.entries.random(), name = "xxx"),
            ),
        )

        return userRepository.save(user.toEntity()).toModel()
    }

    @Transactional
    fun createMockUsers(): List<User> {
        val listOfUsers = (0..99).map {
            User(
                id = null,
                firstName = "$it",
                surname = "surname - $it",
                tags = setOf(Tag(id = null, color = TagColor.entries.random(), name = "xxx")),
            )
        }

        // With usage of spring.jpa.properties.hibernate.jdbc.batch_size=50
        // preparing 4 JDBC statements;
        // executing 3 JDBC statements; <-- getting sequence from DB
        // executing 2 JDBC batches;
        return userRepository.saveAll(listOfUsers.map { it.toEntity() }).map { it.toModel() }
    }
}
