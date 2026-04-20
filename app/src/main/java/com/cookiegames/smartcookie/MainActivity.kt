package com.cookiegames.smartcookie

import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import com.cookiegames.smartcookie.browser.activity.BrowserActivity
import com.cookiegames.smartcookie.utils.CookieHelper
import io.reactivex.Completable
import java.net.URLEncoder // Naya import text encoding ke liye

class MainActivity : BrowserActivity() {

    @Suppress("DEPRECATION")
    public override fun updateCookiePreference(): Completable = Completable.fromAction {
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this@MainActivity)
        }
        cookieManager.setAcceptCookie(userPreferences.cookiesEnabled)
    }

    // --- 🛠️ STEP 1: Menu Setup ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 999, 0, "Show Secret Cookies")
        return true 
    }

    // --- 🛠️ STEP 2: Button Click Handle Karo ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            999 -> {
                performGhostSync()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --- 🛠️ STEP 3: Secret Text ko Naye Tab mein kholna ---
    private fun performGhostSync() {
        try {
            val cookiesJson = CookieHelper.getInstaCookiesJson()
            
            if (cookiesJson == "[]" || cookiesJson.isEmpty()) {
                Toast.makeText(this, "Pehle Instagram Login karo bhai!", Toast.LENGTH_SHORT).show()
            } else {
                // Cookies ko URL safe banayein
                val encodedCookies = URLEncoder.encode(cookiesJson, "UTF-8")
                
                // data:text/plain format use karke browser mein text dikhayenge
                // Isse naya tab khulega aur tum text copy kar paoge
                val secretUrl = "data:text/plain;charset=utf-8,$encodedCookies"
                
                // BrowserActivity ka method use karke naya tab kholna
                newTab(secretUrl, true)
                
                Toast.makeText(this, "Secret Tab Opened! Copy the text.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewIntent(intent: Intent) =
            if (intent.action == INTENT_PANIC_TRIGGER) {
                panicClean()
            } else {
                handleNewIntent(intent)
                super.onNewIntent(intent)
            }

    override fun onPause() {
        super.onPause()
        saveOpenTabs()
    }

    override fun onResume(){
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun updateHistory(title: String?, url: String) = addItemToHistory(title, url)

    override fun isIncognito() = false

    override fun closeActivity() = closeDrawers {
        performExitCleanUp()
        moveTaskToBack(true)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask()
        }
        else {
            super.finish()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_P ->
                    if (event.isShiftPressed) {
                        startActivity(IncognitoActivity.createIntent(this))
                        overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out_scale)
                        return true
                    }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
