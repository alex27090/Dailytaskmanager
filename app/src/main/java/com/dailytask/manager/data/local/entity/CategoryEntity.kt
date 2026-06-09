package com.dailytask.manager.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#FFB3C6",   // default pastel pink

    @ColumnInfo(name = "icon_name")
    val iconName: String = "ic_category_default",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)


