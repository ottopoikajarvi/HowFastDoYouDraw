package com.mobComp2020.howfastdoyoudraw

import androidx.room.*

@Entity(tableName = "highScores")
data class HighScore(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "score") var score: Int,
    @ColumnInfo(name = "difficulty") var difficulty: Int
)

@Dao
interface HighScoreDao {
    @Insert
    fun insert(highScore: HighScore)

    @Query("SELECT * FROM highScores WHERE difficulty = :diff ORDER BY score DESC")
    fun getHighScores(diff: Int?): List<HighScore>
}