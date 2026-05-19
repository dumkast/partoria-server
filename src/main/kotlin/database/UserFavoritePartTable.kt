package com.partoria.database

import org.jetbrains.exposed.sql.Table

object UserFavoritePartTable : Table("user_favorite_parts") {
    val userId = integer("user_id").references(UserTable.id)
    val partId = integer("part_id").references(PartTable.id)
    val addedAt = varchar("added_at", 50)

    override val primaryKey = PrimaryKey(userId, partId)
}