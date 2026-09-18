package com.laurentvrevin.wheris.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.laurentvrevin.wheris.core.database.entity.TemplateEntity

@Dao
interface TemplateDao : BaseDao<TemplateEntity> {
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: Int)
}
