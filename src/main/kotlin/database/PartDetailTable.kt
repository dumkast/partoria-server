package com.partoria.database

import org.jetbrains.exposed.dao.id.IntIdTable

object PartDetailTable : IntIdTable("part_details") {
    val partId = integer("part_id").references(PartTable.id)
    val specification = varchar("specification", 100)
    val value = text("value")
}