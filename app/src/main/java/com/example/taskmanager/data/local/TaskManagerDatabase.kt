package com.example.taskmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskmanager.data.local.dao.GroupProjectDao
import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.GroupProjectEntity
import com.example.taskmanager.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, GroupProjectEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskManagerDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun groupProjectDao(): GroupProjectDao

    companion object {
        @Volatile
        private var INSTANCE: TaskManagerDatabase? = null

        // Standard singleton pattern — one DB instance per process, thread-safe via
        // double-checked locking with @Volatile.
        fun getInstance(context: Context): TaskManagerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskManagerDatabase::class.java,
                    "taskmanager_db"
                )
                    // Wipes and rebuilds on schema change instead of crashing.
                    // Fine for a portfolio/learning project; for production you'd
                    // write real Migration objects to preserve user data.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
