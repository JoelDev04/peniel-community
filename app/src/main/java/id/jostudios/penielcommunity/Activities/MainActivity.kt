package id.jostudios.penielcommunity.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.jostudios.penielcommunity.Fragments.FeedFragment
import id.jostudios.penielcommunity.Fragments.HomeFragment
import id.jostudios.penielcommunity.Fragments.SettingsFragment
import id.jostudios.penielcommunity.Helpers.DataHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.currentFragment().collect { fragment ->

                if (fragment == null) { return@collect; }

                withContext(Dispatchers.Main) {
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
        val user = GlobalState.currentUser!!;

        val auth = FirebaseHelper.getAuth();
        auth.updateCurrentUser(GlobalState.firebaseUser!!);

        viewModel.setAuth(auth);
        FirebaseHelper.setAuth(auth);

        GlobalState.auth = auth;

        System.debug("UserData : ${user}");
    }
}