package cu.jaco.transito.repositories.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cu.jaco.transito.BuildConfig
import cu.jaco.transito.repositories.database.dao.InventoryDao
import cu.jaco.transito.repositories.database.models.Inventory

@Database(
    entities = [
        Inventory::class
    ],
    views = [],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        internal fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, BuildConfig.DATABASE
                        )
//                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
//                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE  licenses  RENAME TO licenses_old")
//                database.execSQL("""
//                CREATE TABLE licenses (
//                    id INTEGER PRIMARY KEY NOT NULL,
//                    number TEXT NOT NULL,
//                    datetime INTEGER NOT NULL ,
//                    license TEXT NOT NULL ,
//                    datetimeEnd INTEGER NOT NULL ,
//                    typeLicense INTEGER NOT NULL ,
//                    transferSms TEXT NOT NULL ,
//                    licenceGeneratedMode INTEGER NOT NULL
//                )
//                """.trimIndent())
            }
        }

    }
}