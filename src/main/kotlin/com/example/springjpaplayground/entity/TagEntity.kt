package com.example.springjpaplayground.entity

import com.example.springjpaplayground.model.TagColor
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tags")
data class TagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Enumerated(value = EnumType.STRING)
    val color: TagColor,
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity,
)
