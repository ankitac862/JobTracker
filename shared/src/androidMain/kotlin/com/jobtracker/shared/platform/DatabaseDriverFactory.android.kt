package com.jobtracker.shared.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jobtracker.shared.database.JobTrackerDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(
            schema = JobTrackerDatabase.Schema,
            context = context,
            name = "jobtracker.db"
        )
        
        // Run migrations to add needsSync columns if they don't exist
        migrateIfNeeded(driver)
        
        return driver
    }
    
    private fun migrateIfNeeded(driver: SqlDriver) {
        // Check and add needsSync column to each table if missing
        val tables = listOf("applications", "tasks", "contacts", "interviews", "statusHistory")
        
        for (table in tables) {
            try {
                // Check if column exists by querying PRAGMA
                val cursor = driver.executeQuery(
                    identifier = null,
                    sql = "PRAGMA table_info($table)",
                    mapper = { cursor ->
                        val columns = mutableListOf<String>()
                        while (cursor.next().value) {
                            cursor.getString(1)?.let { columns.add(it) }
                        }
                        app.cash.sqldelight.db.QueryResult.Value(columns)
                    },
                    parameters = 0
                )
                
                val columns = cursor.value
                if (!columns.contains("needsSync")) {
                    driver.execute(
                        identifier = null,
                        sql = "ALTER TABLE $table ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1",
                        parameters = 0
                    )
                }
            } catch (e: Exception) {
                // Table might not exist yet (fresh install), ignore
            }
        }
    }
}

