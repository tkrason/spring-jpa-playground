package com.example.springjpaplayground.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
@NamedEntityGraph(
    name = "with-tags",
    attributeNodes = [NamedAttributeNode(value = "tags")],
)
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    var name: String,
    var surname: String,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var tags: MutableSet<TagEntity> = mutableSetOf(),
)
