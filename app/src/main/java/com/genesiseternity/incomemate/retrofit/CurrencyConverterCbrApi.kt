package com.genesiseternity.incomemate.retrofit

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CurrencyConverterCbrApi {
    //@GET("23/10/2022")
    //@GET("/users/2")
    //Single<CurrencyCbrModel> getCurrencyDate()

    //@GET("/users/")
    //Single<List<CurrencyCbrModel>> getCurrencyDate()

    //@GET("{date}")
    //Single<CurrencyCbrModel> getCurrentCurrencyDate(@Path("date") String date)

    @GET("/daily_json.js")
    fun getLastCurrencyDate(): Single<CurrencyCbrModel>
}