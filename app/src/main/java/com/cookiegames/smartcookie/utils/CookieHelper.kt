package com.cookiegames.smartcookie.utils

import android.webkit.CookieManager
import org.json.JSONArray
import org.json.JSONObject

object CookieHelper {

    /**
     * Instagram ki cookies ko Puppeteer-friendly JSON array mein badalta h
     */
    fun getInstaCookiesJson(): String {
        val cookieManager = CookieManager.getInstance()
        // Instagram ka pura cookie string uthao
        val cookieString = cookieManager.getCookie("https://www.instagram.com")
        
        val jsonArray = JSONArray()

        if (cookieString != null && cookieString.isNotEmpty()) {
            // Cookies semicolon (;) se alag hoti hain
            val cookies = cookieString.split(";")
            
            for (cookie in cookies) {
                val parts = cookie.split("=")
                if (parts.size >= 2) {
                    val name = parts[0].trim()
                    val value = parts[1].trim()
                    
                    // Puppeteer format ke hisaab se object taiyar karo
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
        
        return jsonArray.toString()
    }
}
