package com.example.proyectofinal.interfaces



import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.model.*
import com.google.gson.GsonBuilder

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Define all API endpoints for the application.
 * Ajusta los paths (@GET/@POST) según las rutas definidas en tus controladores Spring.
 */
interface ApiService {

    // --- AnotacionPersonal ---
    @GET("/anotaciones")
    suspend fun getAllAnotaciones(): List<AnotacionPersonal>

    @POST("/anotaciones")
    suspend fun createAnotacion(@Body anotacion: AnotacionPersonal): AnotacionPersonal

    @PUT("/anotaciones/{id}")
    suspend fun updateAnotacion(
        @Path("id") id: Int,
        @Body anotacion: AnotacionPersonal
    ): AnotacionPersonal

    @DELETE("/anotaciones/{id}")
    suspend fun deleteAnotacion(@Path("id") id: Int): Response<Unit>

    // --- NotaRapida ---
    @GET("/notasrapidas")
    suspend fun getAllNotas(): List<NotaRapida>

    @POST("/notasrapidas")
    suspend fun createNota(@Body nota: NotaRapida): NotaRapida

    @PUT("/notasrapidas/{id}")
    suspend fun updateNota(
        @Path("id") id: NotaRapida,
        @Body nota: NotaRapida
    ): NotaRapida

    @DELETE("/notasrapidas/{id}")
    suspend fun deleteNota(@Path("id") id: Int): Response<Unit>

    // --- Objetivo ---
    @GET("/objetivos")
    suspend fun getAllObjetivos(): List<Objetivo>

    @POST("/objetivos")
    suspend fun createObjetivo(@Body objetivo: Objetivo): Objetivo

    @PUT("/objetivos/{id}")
    suspend fun updateObjetivo(
        @Path("id") id: Int,
        @Body objetivo: Objetivo
    ): Objetivo

    @DELETE("/objetivos/{id}")
    suspend fun deleteObjetivo(@Path("id") id: Int): Response<Unit>

    // --- ProgresoObjetivo ---
    @GET("/progresos")
    suspend fun getAllProgresos(): List<ProgresoObjetivo>

    @POST("/progresos")
    suspend fun createProgreso(@Body progreso: ProgresoObjetivo): ProgresoObjetivo

    @PUT("/progresos/{id}")
    suspend fun updateProgreso(
        @Path("id") id: Int,
        @Body progreso: ProgresoObjetivo
    ): ProgresoObjetivo

    @DELETE("/progresos/{id}")
    suspend fun deleteProgreso(@Path("id") id: Int): Response<Unit>

    // --- ResumenProgreso ---
    @GET("/resumenes")
    suspend fun getAllResumenes(): List<ResumenProgreso>

    @POST("/resumenes")
    suspend fun createResumen(@Body resumen: ResumenProgreso): ResumenProgreso

    @PUT("/resumenes/{id}")
    suspend fun updateResumen(
        @Path("id") id: Int,
        @Body resumen: ResumenProgreso
    ): ResumenProgreso

    @DELETE("/resumenes/{id}")
    suspend fun deleteResumen(@Path("id") id: Int): Response<Unit>

    // --- Usuario ---
    @GET("/usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @POST("/usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Usuario

    @PUT("/usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Int,
        @Body usuario: Usuario
    ): Usuario

    @DELETE("/usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>

    @POST("/api/login")
    suspend fun login(@Body credenciales: Usuario): Usuario

}




@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val apiService: ApiService by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}