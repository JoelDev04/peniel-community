package id.jostudios.penielcommunity.Objects

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import id.jostudios.penielcommunity.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.security.auth.callback.Callback

object System {
    public const val DEBUG_TAG: String = "JOEL_DEBUG";
    public const val APP_VERSION: String = "24.06.1";

    public var dialogLoading: Dialog? = null;

    public fun debug(msg: String) {
        Log.d(DEBUG_TAG, msg);
    }

    public fun setToast(activity: Activity, msg: String, length: Int = Toast.LENGTH_SHORT) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(activity.applicationContext, msg, length).show();
        }
    }
    public fun setToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, msg, length).show();
        }
    }

    public fun showLoadingDialog(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            dialogLoading = Dialog(context);
            dialogLoading?.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogLoading?.setContentView(R.layout.dialog_loading);
            dialogLoading?.setCancelable(false);

            var progressLoading: ProgressBar = dialogLoading?.findViewById(R.id.progress_loading)!!;
            progressLoading.isActivated = true;
            progressLoading.isVisible = true;

            dialogLoading?.show();
        }
    }

    public fun dialogMessageBox(context: Context, title: String, message: String, onClose: () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            var dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_popup_message);
            dialog.setCancelable(false);

            var textBtnClose = dialog.findViewById<TextView>(R.id.text_btn_close);
            var textTitle = dialog.findViewById<TextView>(R.id.text_dialog_title);
            var textMessage = dialog.findViewById<TextView>(R.id.text_dialog_message);

            textTitle.text = title;
            textMessage.text = message;

            textBtnClose.setOnClickListener {
                onClose();
                dialog.dismiss();
            }

            dialog.create();
            dialog.show();
        }
    }

    public fun dialogMessageBox(context: Context, title: String, message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            var dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_popup_message);
            dialog.setCancelable(false);

            var textBtnClose = dialog.findViewById<TextView>(R.id.text_btn_close);
            var textTitle = dialog.findViewById<TextView>(R.id.text_dialog_title);
            var textMessage = dialog.findViewById<TextView>(R.id.text_dialog_message);

            textTitle.text = title;
            textMessage.text = message;

            textBtnClose.setOnClickListener {
                dialog.dismiss();
            }

            dialog.create();
            dialog.show();
        }
    }

    public fun destroyLoadingDialog() {
        GlobalScope.launch(Dispatchers.Main) {
            if (dialogLoading == null) {
                System.debug("Loading dialog is doesn't exists!");
                return@launch;
            }

            dialogLoading?.dismiss();
        }
    }

    public fun moveActivity(current: Activity, targetClass: Class<*>?) {
        GlobalScope.launch(Dispatchers.Main) {
            var targetIntent = Intent(current.applicationContext, targetClass);

            current.startActivity(targetIntent);
            current.finish();
        }
    }
}