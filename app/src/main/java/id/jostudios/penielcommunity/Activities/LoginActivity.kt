package id.jostudios.penielcommunity.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import id.jostudios.penielcommunity.Helpers.EncryptorHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel;

    private lateinit var editInputName: EditText;
    private lateinit var editInputPassword: EditText;
    private lateinit var buttonLogin: Button;
    private lateinit var imageLogoLogin: ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        System.debug("Login page!");

        initialize();
        controlHandler();
        loadLoginState();
    }

    override fun onDestroy() {
        var auth = FirebaseHelper.getAuth();
        auth.signOut();

        super.onDestroy()
    }

    private fun initialize() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java];

        editInputName = findViewById(R.id.edit_input_name);
        editInputPassword = findViewById(R.id.edit_input_password);
        buttonLogin = findViewById(R.id.button_login);
        imageLogoLogin = findViewById(R.id.image_logo_login);
    }

    private fun loadLoginState() {
        if (GlobalState.isLogin) {
            viewModel.setName(GlobalState.currentUser?.name!!);
            viewModel.setPassword(GlobalState.currentCredential?.password!!);

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    viewModel.loginUser(this@LoginActivity);
                    System.moveActivity(this@LoginActivity, MainActivity::class.java)
                    System.destroyLoadingDialog();
                } catch (e: Exception) {
                    System.dialogMessageBox(this@LoginActivity, "Error", e.message.toString());
                    System.debug(e.message.toString());
                    System.destroyLoadingDialog();
                }
            }
        }
    }

    private fun controlHandler() {
        var debugCounter = 0;

        editInputName.addTextChangedListener {
            viewModel.setName(it.toString());
        }

        editInputPassword.addTextChangedListener {
            var securePassword = EncryptorHelper.Hash(it.toString())!!;
            viewModel.setPassword(securePassword);
        }

        buttonLogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    viewModel.loginUser(this@LoginActivity);
                    System.moveActivity(this@LoginActivity, MainActivity::class.java)
                    System.destroyLoadingDialog();
                } catch (e: Exception) {
                    System.dialogMessageBox(this@LoginActivity, "Error", e.message.toString());
                    System.debug(e.message.toString());
                    System.destroyLoadingDialog();
                }
            }
        }

        imageLogoLogin.setOnClickListener {
            debugCounter += 1;

            if (debugCounter == 5) {
                debugCounter = 0;

                GlobalScope.launch {
                    try {
                        viewModel.createUser(this@LoginActivity);

                        withContext(Dispatchers.Main) {
                            System.dialogMessageBox(this@LoginActivity, "Success", "Account successfully created!");
                            System.destroyLoadingDialog();
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            System.dialogMessageBox(
                                this@LoginActivity,
                                "Error",
                                e.message.toString()
                            );
                            System.debug(e.message.toString());
                            System.destroyLoadingDialog();
                        }
                    }

                }
            }
        }
    }
}