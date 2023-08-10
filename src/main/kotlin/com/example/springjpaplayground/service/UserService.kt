package com.example.springjpaplayground.service

import com.example.springjpaplayground.entity.UserEntity
import com.example.springjpaplayground.model.Tag
import com.example.springjpaplayground.model.TagColor
import com.example.springjpaplayground.model.User
import com.example.springjpaplayground.model.toEntity
import com.example.springjpaplayground.model.toModel
import com.example.springjpaplayground.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

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

        // executing 2 JDBC batches;
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
        // preparing 2 JDBC statements;
        // executing 0 JDBC statements; <-- no need for generators trip to DB when using UUID
        // executing 4 JDBC batches; (2*50 users, 2*50 tags)
        return userRepository.saveAll(listOfUsers.map { it.toEntity() }).map { it.toModel() }
    }

    // executing 1 JDBC statements; <-- we get user + tags in one request
    fun findById(userId: UUID) = userRepository.findOneFullById(userId)?.toModel() ?: error("Not found")

    // executing 1 JDBC statements;
    // testing view projections, where only subset of data is needed
    // Note: It's view only, Hibernate is not managing this view projection, so changes are not persisted
    fun findSurnameById(userId: UUID) = userRepository.findSurnameViewById(userId) ?: error("Not found")

    @Transactional
    fun updateUserAndUserTags(user: User): User {
        checkNotNull(user.id)
        val userEntity = userRepository.findById(user.id).getOrNull() ?: error("User does not exist!")

        userEntity.apply {
            updateProperties(user = user)
            updateExistingTags(newTags = user.tags)
            addTagsWithNullId(newTags = user.tags)
        }

        return userRepository.findOneFullById(user.id)?.toModel() ?: error("User does not exist!")
    }
}

private fun UserEntity.updateProperties(user: User) = apply {
    name = user.firstName
    surname = user.surname
}

private fun UserEntity.updateExistingTags(newTags: Set<Tag>) {
    val alreadyExistingTagsById = tags.associateBy { it.id!! }
    newTags.forEach { updatedTag ->
        val alreadyExistingTag = alreadyExistingTagsById[updatedTag.id]
        alreadyExistingTag?.apply {
            color = updatedTag.color
            name = updatedTag.name
        }
    }
}

private fun UserEntity.addTagsWithNullId(newTags: Set<Tag>) {
    val tagsWithoutId = newTags
        .filter { it.id == null }
        .map { it.toEntity(user = this) }

    tags.addAll(tagsWithoutId)
}
