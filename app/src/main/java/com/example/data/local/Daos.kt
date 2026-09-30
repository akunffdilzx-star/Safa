package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'MEMBER' ORDER BY createdAt DESC")
    fun getAllMembers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("UPDATE users SET geminiApiKey = :apiKey WHERE id = :userId")
    suspend fun updateApiKey(userId: Long, apiKey: String)

    @Query("UPDATE users SET executionCount = executionCount + :increment WHERE id = :userId")
    suspend fun incrementExecution(userId: Long, increment: Int = 1)

    @Query("SELECT COUNT(*) FROM users WHERE role = 'MEMBER'")
    fun getMemberCount(): Flow<Int>
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY number ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY number ASC")
    suspend fun getQuestionsList(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE status != 'SOLVED' ORDER BY number ASC")
    suspend fun getUnsolvedQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions")
    suspend fun clearAll()

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE questions SET status = 'PENDING', detectedAnswer = '', filledAnswer = '', confidence = 0.0, reasoning = ''")
    suspend fun resetAnswers()
}

@Dao
interface ExecutionLogDao {
    @Query("SELECT * FROM execution_logs ORDER BY id DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<ExecutionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ExecutionLogEntity): Long

    @Query("DELETE FROM execution_logs")
    suspend fun clearLogs()
}
