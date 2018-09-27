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
import sam.teatime.State
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
            var infusions = arrayOf(
                    Infusion(0, 0, 180),
                    Infusion(0, 1, 210),
                    Infusion(0, 2, 240),
                    Infusion(0, 3, 270),
                    Infusion(0, 4, 300),
                    Infusion(0, 5, 345),
                    Infusion(0, 6, 390),
                    Infusion(0, 7, 435)
            )
            teaDao.insert(tea)
            infusions.forEach { infusion -> teaDao.insert(infusion) }

            tea = Tea(1, "Oolong")
            infusions = arrayOf(
                    Infusion(1, 0, 240),
                    Infusion(1, 1, 270),
                    Infusion(1, 2, 300),
                    Infusion(1, 3, 345),
                    Infusion(1, 4, 390),
                    Infusion(1, 5, 435),
                    Infusion(1, 6, 480),
                    Infusion(1, 7, 525)
            )
            teaDao.insert(tea)
            infusions.forEach { infusion -> teaDao.insert(infusion) }

            tea = Tea(2, "Black")
            infusions = arrayOf(
                    Infusion(2, 0, 210),
                    Infusion(2, 1, 240),
                    Infusion(2, 2, 270),
                    Infusion(2, 3, 300),
                    Infusion(2, 4, 345),
                    Infusion(2, 5, 390),
                    Infusion(2, 6, 435),
                    Infusion(2, 7, 480)
            )
            teaDao.insert(tea)
            infusions.forEach { infusion -> teaDao.insert(infusion) }
            tea = Tea(3, "Test")
            infusions = arrayOf(
                    Infusion(3, 0, 10),
                    Infusion(3, 1, 30),
                    Infusion(3, 2, 60)
            )
            teaDao.insert(tea)
            infusions.forEach { infusion -> teaDao.insert(infusion) }

            State.lastSelectedTeaId = 1
            State.lastSelectedInfusionIndex = 0
        }
    }
}