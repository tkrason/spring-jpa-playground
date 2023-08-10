package com.example.springjpaplayground.model

import com.example.springjpaplayground.entity.UserEntity
import java.util.UUID

data class User(
    val id: UUID?,
    val firstName: String,
    val surname: String,
    val tags: Set<Tag>,
)

fun User.toEntity(): UserEntity {
    val userEntity = UserEntity(id, name = firstName, surname)
    userEntity.tags = tags.mapTo(mutableSetOf()) { it.toEntity(userEntity) }
    return userEntity
}

fun UserEntity.toModel() = User(
    id = id,
    firstName = name,
    surname = surname,
    tags = tags.map { it.toModel() }.toSet(),
)
