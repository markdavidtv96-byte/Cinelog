package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY watchedDate DESC, createdAt DESC")
    fun getAllMovies(): Flow<List<MovieJournalEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Long): MovieJournalEntity?

    @Query("SELECT * FROM movies WHERE status = 'WATCHED' ORDER BY watchedDate DESC")
    fun getWatchedMovies(): Flow<List<MovieJournalEntity>>

    @Query("SELECT * FROM movies WHERE status = 'WANT_TO_WATCH' ORDER BY createdAt DESC")
    fun getWatchlistMovies(): Flow<List<MovieJournalEntity>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY rating DESC, watchedDate DESC")
    fun getFavoriteMovies(): Flow<List<MovieJournalEntity>>

    @Query("SELECT * FROM movies WHERE journalEntry != '' ORDER BY watchedDate DESC")
    fun getJournalEntries(): Flow<List<MovieJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieJournalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieJournalEntity>)

    @Update
    suspend fun updateMovie(movie: MovieJournalEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieJournalEntity)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: Long)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int
}
