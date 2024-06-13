package id.jostudios.penielcommunity.ViewModels

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.jostudios.penielcommunity.Activities.MainActivity
import id.jostudios.penielcommunity.Helpers.AuthHelper
import id.jostudios.penielcommunity.Helpers.DataHelper
import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class LoginViewModel: ViewModel() {
    private var m_name: MutableStateFlow<String> = MutableStateFlow("");
    private var m_password: MutableStateFlow<String> = MutableStateFlow("");
    private var m_isLogin: MutableStateFlow<Boolean> = MutableStateFlow(false);

    public fun name() = m_name.asStateFlow();
    public fun password() = m_password.asStateFlow();
    public fun isLogin() = m_isLogin.asStateFlow();

    public fun setName(value: String) { m_name.value = value; }
    public fun setPassword(value: String) { m_password.value = value; }

    public suspend fun loginUser(context: Context) {
        val name = name().value;
        val password = password().value;

        if (!checkInputs(name, password, context)) {
            return;
        }

        System.showLoadingDialog(context);

        Thread.sleep(2000);

        AuthHelper.login(name, password);

        saveLogin(context);
    }

    public suspend fun createUser(context: Context) {
        val name = name().value;
        val password = password().value;

        if (!checkInputs(name, password, context)) {
            return;
        }

        System.showLoadingDialog(context);

        Thread.sleep(2000);

        AuthHelper.createAccount(name, password);
    }

    private fun saveLogin(context: Context) {
        var model = SaveModel();
        model.token = GlobalState.token;
        model.credential = GlobalState.currentCredential;
        model.user = GlobalState.currentUser;

        System.debug("Save Model : ${model}");

        var dataHandler = DataHelper(context);
        dataHandler.saveData(model);
    }

//    public fun createUser(activity: Activity): LiveData<Boolean>? {
//        var name = name();
//        var password = password();
//
//        val result = MutableLiveData<Boolean>(false);
//
//        if (!checkInputs(name.value, password.value, activity)) {
//            result.value = false;
//            return result;
//        }
//
//        var id = Date().time + name.hashCode();
//
//        var dbRef = FirebaseOps.getDBReference();
//        var dbOps = DatabaseOps(dbRef, activity);
//
//        System.debug("Name : ${name.value}");
//        System.debug("Password : ${password.value}");
//        System.debug("ID : ${id}");
//
//        var userModel = UserModel(
//            id = id,
//            name = name.value,
//            displayName = name.value,
//            bornDate = 0,
//            phoneNumber = "0",
//            emailAddress = ""
//        );
//
//        var credentialModel = CredentialModel(
//            id = id,
//            name = name.value,
//            password = password.value
//        )
//
//        viewModelScope.launch(Dispatchers.IO) {
//            var dbResult = dbOps.addUser(userModel);
//
//            if (dbResult.isSuccess == false) {
//                result.postValue(false);
//                System.setToast(activity, dbResult.message);
//                return@launch;
//            }
//
//            dbResult = dbOps.addCredential(credentialModel);
//
//            if (dbResult.isSuccess == false) {
//                result.postValue(false);
//                System.setToast(activity, dbResult.message);
//                return@launch;
//            }
//
//            withContext(Dispatchers.Main) {
//                System.setToast(activity, "Berhasil di tambahkan!");
//                result.postValue(true);
//            }
//        }

//       return null;
//    }

    private fun checkInputs(name: String, password: String, context: Context): Boolean {
        if (TextUtils.isEmpty(name)) {
            System.setToast(context, "Tolong masukan nama");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            System.setToast(context, "Tolong masukan password");
            return false;
        }

        return true;
    }

}