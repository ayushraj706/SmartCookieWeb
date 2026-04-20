package com.cookiegames.smartcookie

import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem // Naya import
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast // Notification ke liye
import com.cookiegames.smartcookie.browser.activity.BrowserActivity
import com.cookiegames.smartcookie.utils.CookieHelper // Humne jo helper banaya tha
import io.reactivex.Completable

class MainActivity : BrowserActivity() {

    @Suppress("DEPRECATION")
    public override fun updateCookiePreference(): Completable = Completable.fromAction {
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this@MainActivity)
        }
        cookieManager.setAcceptCookie(userPreferences.cookiesEnabled)
    }

    // --- 🛠️ STEP 1: Menu Button Enable Karo ---
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Purana return false hata kar ye dalo taaki menu dikhe
        menu.add(0, 999, 0, "Sync to Ghost Engine") 
        return true 
    }

    // --- 🛠️ STEP 2: Button Click Hone Par Kya Hoga ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 999) {
            performGhostSync()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performGhostSync() {
        val cookiesJson = CookieHelper.getInstaCookiesJson()
        
        if (cookiesJson == "[]") {
            Toast.makeText(this, "Pehle Instagram Login karo bhai!", Toast.LENGTH_SHORT).show()
        } else {
            // Yahan hum Firebase wala logic dalenge (Next step mein)
            // Abhi ke liye sirf message dikhayega
            Toast.makeText(this, "Ghost Engine Syncing...", Toast.LENGTH_LONG).show()
            
            // TODO: firebaseDatabase.child("insta_session").setValue(cookiesJson)
            println("Debug JSON: $cookiesJson") 
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
