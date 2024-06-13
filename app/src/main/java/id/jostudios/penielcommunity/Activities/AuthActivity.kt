package id.jostudios.penielcommunity.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Helpers.EncryptorHelper
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.AuthViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel;

    private lateinit var editInputToken: EditText;
    private lateinit var buttonAuth: Button;
    private lateinit var imageLogo: ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        initialize();
        controlHandler();
        stateHandler();
        loadLoginState();
    }

    private fun initialize() {
        viewModel = ViewModelProvider( this)[AuthViewModel::class.java];

        editInputToken = findViewById(R.id.edit_input_token);
        buttonAuth = findViewById(R.id.button_auth);
        imageLogo = findViewById(R.id.image_logo);
    }

    private fun controlHandler() {
        buttonAuth.setOnClickListener {
            viewModel.setAuthToken(editInputToken.text.toString());
        }

        imageLogo.setOnClickListener {
            createAuthToken();
        }
    }

    private fun loadLoginState() {
        if (GlobalState.isAuth) {
            viewModel.setAuthToken(GlobalState.token);
            //viewModel.validateToken(applicationContext);
        }
    }

    private fun stateHandler() {
        GlobalScope.launch {
            viewModel.getAuthToken().collect {
                if (TextUtils.isEmpty(it)) {
                    System.setToast(this@AuthActivity, "Tolong masukan token autentikasi!");
                    return@collect;
                }

                if (viewModel.validateToken(this@AuthActivity)) {
                    System.setToast(applicationContext, "Autentikasi token berhasil!");
                    System.moveActivity(this@AuthActivity, LoginActivity::class.java);
                }

                editInputToken.setText("");
            }
        }
    }

    private fun createAuthToken() {
        var demoDBinfo = "appid=1:121860213510:android:ea3032152c723db7792c87\n" +
                "dburl=https://demoproject-fdf87-default-rtdb.asia-southeast1.firebasedatabase.app/\n" +
                "apikey=AIzaSyBFP3VFcU-iwxSyAFfie3BXlraCqxOn2J8\n" +
                "storage=demoproject-fdf87.appspot.com";

        var realDBinfo = "appid=1:880528269580:android:6ff4438c0384be6586a929\n" +
                "dburl=https://penielcommunity-9f5ea-default-rtdb.asia-southeast1.firebasedatabase.app/\n" +
                "apikey=AIzaSyADYuUTTyZYlCNQY841JLlwfJaONPWFBks";

        val encryptDemo = EncryptorHelper.Encrypt(demoDBinfo);
        val encryptReal = EncryptorHelper.Encrypt(realDBinfo);

        System.debug("Demo Token :${encryptDemo}");
        System.debug("Real Token :${encryptReal}");
    }
}