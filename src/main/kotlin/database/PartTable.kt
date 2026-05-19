package com.partoria.database

import org.jetbrains.exposed.dao.id.IntIdTable

object PartTable : IntIdTable("computer_parts") {
    val name = varchar("name", 255)
    val category = varchar("category", 100)
    val brand = varchar("brand", 100)
    val price = double("price")
    val specs = text("specs")
    val imageUrl = varchar("image_url", 500)
    val releaseYear = integer("release_year")
}