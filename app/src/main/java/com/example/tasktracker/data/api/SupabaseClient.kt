package com.example.tasktracker.data.api

import com.example.tasktracker.utils.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = Constants.SUPABASE_URL,
        supabaseKey = Constants.SUPABASE_KEY
    ) {
        install(Auth)       // Модуль аутентификации
        install(Postgrest)  // Модуль работы с PostgreSQL
        install(Realtime)   // Модуль real-time обновлений
    }

    fun getPostgrest() = client.postgrest
    fun getAuth() = client.auth
}
