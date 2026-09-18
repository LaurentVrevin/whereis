package com.laurentvrevin.wheris.data.local.database.template

import androidx.room.Dao
import androidx.room.Query
import com.laurentvrevin.wheris.data.local.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao : BaseDao<TemplateEntity> {
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: Int)
}
