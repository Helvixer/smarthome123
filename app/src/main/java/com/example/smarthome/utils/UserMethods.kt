package com.example.smarthome.utils

import android.content.SharedPreferences
import com.example.smarthome.dataClasses.Home
import com.example.smarthome.dataClasses.HomeInsert
import com.example.smarthome.dataClasses.UserInsert
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest

class UserMethods {

    suspend fun Auth(mail : String, pass : String) : Boolean{
        try {
            SBobj.getClient1().gotrue.loginWith(Email) {
                email = mail
                password = pass
            }
            return true
        }catch (_:Exception){
            return false
        }
    }

    suspend fun SignUp(mail : String, pass : String) : Boolean{
        try {
            SBobj.getClient1().gotrue.signUpWith(Email) {
                email = mail
                password = pass
            }
            return true
        }catch (_:Exception){
            return false
        }
    }

    suspend fun logout(){
        SBobj.getClient1().gotrue.logout()
    }

    suspend fun getUser() : UserInfo?{
        val result : UserInfo? = try{
            SBobj.getClient1().gotrue.retrieveUserForCurrentSession()
        } catch (_:Exception){
            null
        }
        return result
    }

    fun saveUser(sPref : SharedPreferences.Editor, mail : String, pass : String){
        sPref.putString("email", mail)
        sPref.putString("pass", pass)
        sPref.apply()
    }

    fun saveCode(sPref : SharedPreferences.Editor, code : String){
        sPref.putString("code", code)
        sPref.apply()
    }

    suspend fun getUsername() : String {
        return SBobj.getClient1().postgrest["user"].select().decodeSingle<UserInsert>().username
    }

    suspend fun getAvatar() : String? {
        return SBobj.getClient1().postgrest["user"].select().decodeSingle<UserInsert>().avatar
    }

    suspend fun getHome() : Home?{
        return try{
            SBobj.getClient1().postgrest["home"].select(){
                Home::profile_id eq getUser()!!.id
            }.decodeSingle<Home>()
        }catch (_:Exception){
            null
        }
    }

    suspend fun addHome(user_id : String, adress : String){
        val homeInsert = HomeInsert(adress, user_id)
        SBobj.getClient1().postgrest["home"].insert(homeInsert)
    }

}