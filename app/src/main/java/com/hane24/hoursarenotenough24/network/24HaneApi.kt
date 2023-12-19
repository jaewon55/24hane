package com.hane24.hoursarenotenough24.network

import com.hane24.hoursarenotenough24.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = BuildConfig.BASE_URL

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface Hane24Api {
    @GET("/user/login/islogin")
    suspend fun isLogin(
        @Header("Authorization") token: String?
    ): Response<String?>

    @GET("/v2/tag-log/maininfo")
    suspend fun getMainInfo(
        @Header("Authorization") token: String?
    ): MainInfo

    @GET("/v3/tag-log/getAllTagPerMonth")
    suspend fun getAllTagPerMonth(
        @Header("Authorization") token: String?,
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): GetAllTagPerMonthDto

    @GET("/v2/tag-log/accumulationTimes")
    suspend fun getAccumulationTime(
        @Header("Authorization") token: String?,
    ): AccumulationTimeInfo

    @GET("/v2/statistics/get_cadet_per_cluster")
    suspend fun getCadetPerCluster(
        @Header("Authorization") token: String?,
    ): List<ClusterPopulationInfo>

    @GET("/v2/reissue")
    suspend fun getReissueState(
        @Header("Authorization") token: String?,
    ): ReissueState

    @POST("/v2/reissue/request")
    suspend fun postReissueRequest(
        @Header("Authorization") token: String?,
    ): ReissueRequestResult

    @PATCH("/v2/reissue/finish")
    suspend fun patchReissueFinish(
        @Header("Authorization") token: String?,
    ): ReissueRequestResult
}

object Hane24Apis {
    val hane24ApiService: Hane24Api by lazy { retrofit.create(Hane24Api::class.java) }
}