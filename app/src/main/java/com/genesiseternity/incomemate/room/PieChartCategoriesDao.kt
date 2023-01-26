package com.genesiseternity.incomemate.room

import androidx.room.*
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface PieChartCategoriesDao {

    /*
    @Query("SELECT * FROM PieChartCategories LEFT JOIN" +
            " PieCharts ON PieChartCategories.id = PieCharts.id AND" +
            " PieCharts.id = :id")
    fun getPieChartAndCategoryData(id: Int): Flowable<Map<PieChartCategoriesEntity, PieChartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(pieChartEntities: PieChartEntity): Completable
     */

    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " WHERE id = :id")
    fun getPieChartCategoryById(id: Int): Single<List<PieChartCategoriesEntity>>

    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " WHERE id_page = :id_page")
    fun getPieChartCategoryByIdPage(id_page: Int): Single<List<PieChartCategoriesEntity>>

    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME)
    fun getAllPieChartCategoriesData(): Single<List<PieChartCategoriesEntity>>

    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " ORDER BY id ASC")
    fun getAllSortedPieChartCategoriesData(): Single<List<PieChartCategoriesEntity>>

    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " ORDER BY id_page DESC")
    fun getSortedDescPieChartCategoriesData(): Single<List<PieChartCategoriesEntity>>

    //@Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " WHERE id_page = :id_page" + " ORDER BY id ASC")
    //fun getCurrentIdPageSortedPieChartCategoriesData(id_page: Int): Single<List<PieChartCategoriesEntity>>


    @Insert
    fun insertPieChartCategoriesData(pieChartCategoriesEntities: PieChartCategoriesEntity): Completable

    @Update
    fun updatePieChartCategoriesData(pieChartCategoriesEntities: PieChartCategoriesEntity): Completable

    @Delete
    fun deletePieChartCategoriesData(pieChartCategoriesEntities: PieChartCategoriesEntity): Completable

    @Query("DELETE FROM " + PieChartCategoriesEntity.TABLE_NAME + " WHERE id = :id")
    fun deleteCurrentPieChartCategoriesData(id: Int): Completable

    @Query("DELETE FROM " + PieChartCategoriesEntity.TABLE_NAME)
    fun deleteAllPieChartCategoriesData(): Completable


    @Query("SELECT * FROM " + PieChartCategoriesEntity.TABLE_NAME + " ORDER BY id_page DESC")
    fun getSortedPieChartCategoriesData(): List<PieChartCategoriesEntity>



    @Query("UPDATE " + PieChartCategoriesEntity.TABLE_NAME + " SET id_currency_account = :idCurrencyAccount WHERE id = :id")
    fun updateIdCurrencyAccount(id: Int, idCurrencyAccount: Int): Completable

}