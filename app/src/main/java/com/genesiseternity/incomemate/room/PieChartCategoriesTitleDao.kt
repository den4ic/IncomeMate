package com.genesiseternity.incomemate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface PieChartCategoriesTitleDao {

    @Query("SELECT * FROM " + PieChartCategoriesTitleEntity.TABLE_NAME)
    fun getAllPieChartCategoriesTitleData(): Single<List<PieChartCategoriesTitleEntity>>

    @Insert
    fun insertPieChartCategoriesTitleData(vararg pieChartCategoriesTitleEntities: PieChartCategoriesTitleEntity): Completable

    @Update
    fun updatePieChartCategoriesTitleData(pieChartCategoriesTitleEntities: PieChartCategoriesTitleEntity): Completable

    @Query("DELETE FROM " + PieChartCategoriesTitleEntity.TABLE_NAME)
    fun deleteAllPieChartCategoriesTitleData(): Completable


    @Query("SELECT * FROM " + PieChartCategoriesTitleEntity.TABLE_NAME)
    fun getAllCategoriesTitleData(): List<PieChartCategoriesTitleEntity>

    @Query("DELETE FROM " + PieChartCategoriesTitleEntity.TABLE_NAME + " WHERE id = :id")
    fun deleteCurrentPieChartCategoriesTitleData(id: Int): Completable
}