package id.jostudios.penielcommunity.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.jostudios.penielcommunity.Fragments.FeedFragment
import id.jostudios.penielcommunity.Fragments.HomeFragment
import id.jostudios.penielcommunity.Fragments.SettingsFragment
import id.jostudios.penielcommunity.Helpers.AccountHelper
import id.jostudios.penielcommunity.Helpers.AuthHelper
import id.jostudios.penielcommunity.Helpers.DataHelper
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Helpers.StorageHelper
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.MainViewModel
import id.jostudios.penielcommunity.Worker.FeedUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel;

    private lateinit var homeFragment: HomeFragment;
    private lateinit var feedFragment: FeedFragment;
    private lateinit var settingsFragment: SettingsFragment;

    private lateinit var frameFragmentContainer: FrameLayout;
    private lateinit var bottomNavBar: BottomNavigationView;

    private var lastFragment: Fragment? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize();

        stateHandler();
        widgetHandler();

        loadUserData();

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100);
                System.setToast(applicationContext, "Beri aplikasi perijinan untuk notifikasi!");
                finish();
                return;
            }
        }

        try {
            val feedUpdaterWorker = PeriodicWorkRequest.Builder(
                FeedUpdater::class.java,
                15,
                TimeUnit.MINUTES
            ).build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "FeedUpdater",
                ExistingPeriodicWorkPolicy.KEEP,
                feedUpdaterWorker
            )
        } catch (e: Exception) {
            System.dialogMessageBox(this, "Error", e.message.toString());
        }


        viewModel.setCurrentFragment(homeFragment);
        bottomNavBar.selectedItemId = R.id.menu_home;
    }

    private fun initialize() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java];

        homeFragment = HomeFragment();
        feedFragment = FeedFragment();
        settingsFragment = SettingsFragment();

        frameFragmentContainer = findViewById(R.id.frame_fragment_container);
        bottomNavBar = findViewById(R.id.bottom_nav_handler);
    }

    private fun stateHandler() {
        viewModel.currentFragment().observe(this@MainActivity) { fragment ->

            if (fragment == null) { return@observe; }

            if (lastFragment == null) {
                setFragment(fragment, R.anim.anim_left_to_right, R.anim.anim_right_to_left);
            }
            if (fragment == feedFragment) {
                setFragment(fragment, R.anim.anim_left_to_right, R.anim.anim_mid_to_right);
            }
            if (fragment == settingsFragment) {
                setFragment(fragment, R.anim.anim_right_to_left, R.anim.anim_mid_to_left);
            }
            if (fragment == homeFragment && lastFragment == feedFragment) {
                setFragment(fragment, R.anim.anim_right_to_left, R.anim.anim_mid_to_left);
            }
            if (fragment == homeFragment && lastFragment == settingsFragment) {
                setFragment(fragment, R.anim.anim_left_to_right, R.anim.anim_mid_to_right);
            }
        }
    }

    private fun widgetHandler() {
        bottomNavBar.setOnItemSelectedListener {
            var itemId = it.itemId;

            when(itemId) {
                R.id.menu_feed -> {
                    viewModel.setCurrentFragment(feedFragment);
                }

                R.id.menu_home -> {
                    viewModel.setCurrentFragment(homeFragment);
                }

                R.id.menu_settings -> {
                    viewModel.setCurrentFragment(settingsFragment);
                }
            }

            true;
        }
    }

    private fun setFragment(targetFragment: Fragment, @AnimRes @AnimatorRes enter: Int, @AnimRes @AnimatorRes exit: Int) {
        val fragSupport = supportFragmentManager;
        val fragTransaction = fragSupport.beginTransaction();

        fragTransaction.apply {
            setCustomAnimations(enter, exit);
            replace(frameFragmentContainer.id, targetFragment);
            lastFragment = targetFragment;
            commit();
        }
    }

    private fun loadUserData() {
        val user = GlobalState.currentUser;

        GlobalScope.launch {
            val globalAppVersion = DatabaseHelper.getGlobalAppVersion();

            val onClose = fun() {
                finish();
            }

            if (System.APP_VERSION != globalAppVersion) {
                System.dialogMessageBox(this@MainActivity, "Update!", "Tolong update aplikasi terlebih dahulu!", onClose);
            }
        }

        if (GlobalState.currentUser == null) {
            System.setToast(applicationContext, "Login di tolak! Silahkan login ulang.");
            val accHelper = AccountHelper(this);
            accHelper.logoutUser();
        }

        val auth = FirebaseHelper.getAuth();
        auth.updateCurrentUser(GlobalState.firebaseUser!!);

        viewModel.setAuth(auth);
        FirebaseHelper.setAuth(auth);

        GlobalState.auth = auth;

        System.debug("UserData : ${user}");
    }
}