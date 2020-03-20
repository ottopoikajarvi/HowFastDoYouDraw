package com.mobComp2020.howfastdoyoudraw

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HighScore::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun highScoreDao() : HighScoreDao
}