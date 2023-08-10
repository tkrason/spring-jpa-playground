package com.example.springjpaplayground.model

import com.example.springjpaplayground.entity.TagEntity
import com.example.springjpaplayground.entity.UserEntity
import java.util.UUID

data class Tag(
    val id: UUID?,
    val color: TagColor,
    val name: String,
)

enum class TagColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    WHITE,
    BLACK,
}

fun Tag.toEntity(user: UserEntity) = TagEntity(
    id = id,
    color = color,
    name = name,
    user = user,
)

fun TagEntity.toModel() = Tag(
    id = id,
    color = color,
    name = name,
)
