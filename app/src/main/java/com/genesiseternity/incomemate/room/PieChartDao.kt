package com.genesiseternity.incomemate.room

import androidx.room.*
import com.genesiseternity.incomemate.room.entities.PieChartEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PieChartDao {
    @Query("SELECT * FROM " + PieChartEntity.TABLE_NAME)
    fun getAllPieChartData(): Flowable<List<PieChartEntity>>

    @Query("SELECT * FROM " + PieChartEntity.TABLE_NAME + " ORDER BY id_page ASC")
    fun getAllSortedPieChartData(): Flowable<List<PieChartEntity>>

    @Insert
    fun insertPieChartData(pieChartEntities: PieChartEntity): Completable

    @Update
    fun updatePieChartData(pieChartEntities: PieChartEntity): Completable

    @Delete
    fun deletePieChartData(pieChartEntities: PieChartEntity): Completable

    @Query("DELETE FROM " + PieChartEntity.TABLE_NAME + " WHERE id_page = :idPage")
    fun deletePieChartData(idPage: Int): Completable

    @Query("DELETE FROM " + PieChartEntity.TABLE_NAME)
    fun deleteAllPieChartData(): Completable
}