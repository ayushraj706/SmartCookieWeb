package com.cookiegames.smartcookie.utils

import android.webkit.CookieManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

object CookieHelper {

    /**
     * Instagram ki cookies ko Puppeteer-friendly JSON array mein badalta h.
     * Ismein substring ka use kiya h taaki value ke andar ka '=' sign problem na kare.
     */
    fun getInstaCookiesJson(): String {
        val cookieManager = CookieManager.getInstance()
        // Instagram ka pura cookie string uthao
        val cookieString = cookieManager.getCookie("https://www.instagram.com")
        
        val jsonArray = JSONArray()

        if (!cookieString.isNullOrEmpty()) {
            try {
                // Cookies semicolon (;) se alag hoti hain
                val cookies = cookieString.split(";")
                
                for (cookie in cookies) {
                    if (cookie.contains("=")) {
                        // Pehle '=' se pehle wala 'Name' aur uske baad wala sab 'Value'
                        val name = cookie.substringBefore("=").trim()
                        val value = cookie.substringAfter("=").trim()
                        
                        if (name.isNotEmpty()) {
                            val cookieObject = JSONObject()
                            cookieObject.put("name", name)
                            cookieObject.put("value", value)
                            cookieObject.put("domain", ".instagram.com")
                            cookieObject.put("path", "/")
                            cookieObject.put("httpOnly", false)
                            cookieObject.put("secure", true)
                            
                            jsonArray.put(cookieObject)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Agar koi cookie nahi mili toh khali array '[]' return karega
        return jsonArray.toString()
    }
}
