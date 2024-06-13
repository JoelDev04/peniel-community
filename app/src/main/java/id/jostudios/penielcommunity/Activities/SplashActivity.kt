package id.jostudios.penielcommunity.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.jostudios.penielcommunity.Helpers.DataHelper
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.statusBarColor = resources.getColor(R.color.white);

        loadSystem();
    }

    private fun loadSystem() {
        GlobalScope.launch {
            var dataHelper = DataHelper(applicationContext);
            dataHelper.loadData();
            Thread.sleep(3000);
            System.moveActivity(this@SplashActivity, AuthActivity::class.java);
        }
    }
}