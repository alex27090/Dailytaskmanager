package com.dailytask.manager.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dailytask.manager.data.local.dao.CategoryDao
import com.dailytask.manager.data.local.dao.TaskDao
import com.dailytask.manager.data.local.entity.CategoryEntity
import com.dailytask.manager.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "daily_task_db"

        // Default categories seeded on first launch
        private val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(name = "Personal",  colorHex = "#FFB3C6", iconName = "ic_cat_personal"),
            CategoryEntity(name = "Work",      colorHex = "#B5D5FF", iconName = "ic_cat_work"),
            CategoryEntity(name = "Health",    colorHex = "#B5F0C0", iconName = "ic_cat_health"),
            CategoryEntity(name = "Shopping",  colorHex = "#FFE5B3", iconName = "ic_cat_shopping"),
            CategoryEntity(name = "Study",     colorHex = "#E5B3FF", iconName = "ic_cat_study"),
        )

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: android.content.Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }

        fun buildDatabase(context: android.content.Context): TaskDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default categories on first create
                        CoroutineScope(Dispatchers.IO).launch {
                            DEFAULT_CATEGORIES.forEach { category ->
                                db.execSQL(
                                    "INSERT INTO categories (name, color_hex, icon_name, created_at) VALUES (?, ?, ?, ?)",
                                    arrayOf(category.name, category.colorHex, category.iconName, System.currentTimeMillis())
                                )
                            }
                        }
                    }
                })
                .build()
        }
    }
}


