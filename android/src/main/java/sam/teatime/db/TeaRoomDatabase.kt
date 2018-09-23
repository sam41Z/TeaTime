package sam.teatime.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch
import sam.teatime.db.dao.TeaDao
import sam.teatime.db.entities.Infusion
import sam.teatime.db.entities.Tea

@Database(entities = [(Tea::class), (Infusion::class)], version = 1)
abstract class TeaRoomDatabase : RoomDatabase() {
    abstract fun teaDao(): TeaDao

    companion object {
        @Volatile
        private var INSTANCE: TeaRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TeaRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                // Create database here
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TeaRoomDatabase::class.java,
                        "tea_database"
                )
                        .addCallback(TeaDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class TeaDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.teaDao())
                }
            }
        }

        fun populateDatabase(teaDao: TeaDao) {
            teaDao.deleteAll()
            var tea = Tea(0, "Green")
            teaDao.insert(tea)
            tea = Tea(1, "Oolong")
            teaDao.insert(tea)
            tea = Tea(2, "Black")
            teaDao.insert(tea)
        }
    }
}