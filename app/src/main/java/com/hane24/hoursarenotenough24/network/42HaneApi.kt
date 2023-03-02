package com.hane24.hoursarenotenough24.network

import com.hane24.hoursarenotenough24.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = BuildConfig.BASE_URL

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface hane42Api {
    @GET("/user/login/islogin")
    suspend fun isLogin(
        @Header("Authorization") token: String?
    ): Response<String?>

    @GET("/v1/tag-log/maininfo")
    suspend fun getMainInfo(
        @Header("Authorization") token: String?
    ): MainInfo

    @GET("/v1/tag-log/getTagPerDay")
    suspend fun getInOutInfoPerDay(
        @Header("Authorization") token: String?,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): InOutTimeContainer

    @GET("/v1/tag-log/getTagPerMonth")
    suspend fun getInOutInfoPerMonth(
        @Header("Authorization") token: String?,
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): InOutTimeContainer

    @GET("/v1/tag-log/getAllTagPerMonth")
    suspend fun getAllTagPerMonth(
        @Header("Authorization") token: String?,
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): InOutTimeContainer

    @GET("/v1/tag-log/accumulationTimes")
    suspend fun getAccumulationTime(
        @Header("Authorization") token: String?,
    ): AccumulationTimeInfo

    @GET("/v1/statistics/get_cadet_per_cluster")
    suspend fun getCadetPerCluster(
        @Header("Authorization") token: String?,
    ): List<ClusterPopulationInfo>

    @GET("/v1/reissue")
    suspend fun getReissueState(
        @Header("Authorization") token: String?,
    ): ReissueState

    @POST("/v1/reissue/request")
    suspend fun postReissueRequest(
        @Header("Authorization") token: String?,
    ): ReissueRequestResult

    @PATCH("/v1/reissue/finish")
    suspend fun patchReissueFinish(
        @Header("Authorization") token: String?,
    ): ReissueRequestResult
}

object Hane42Apis {
    val hane42ApiService by lazy { retrofit.create(hane42Api::class.java) }
}