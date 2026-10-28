package com.example.lab10.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EspacioApiService {

    @GET("api/espacios/")
    suspend fun selectEspacios(): ArrayList<EspacioModel>

    @GET("api/espacios/{id}/")
    suspend fun selectEspacio(@Path("id") id: String): Response<EspacioModel>

    @Headers("Content-Type: application/json")
    @POST("api/espacios/")
    suspend fun insertEspacio(@Body espacio: EspacioModel): Response<EspacioModel>

    @PUT("api/espacios/{id}/")
    suspend fun updateEspacio(@Path("id") id: String, @Body espacio: EspacioModel): Response<EspacioModel>

    @DELETE("api/espacios/{id}/")
    suspend fun deleteEspacio(@Path("id") id: String): Response<EspacioModel>
}