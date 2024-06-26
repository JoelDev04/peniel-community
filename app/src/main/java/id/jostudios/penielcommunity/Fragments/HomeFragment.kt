package id.jostudios.penielcommunity.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import id.jostudios.penielcommunity.Activities.DiakoniaActivity
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.Helpers.AccountHelper
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.StorageHelper
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var mainView: View;

    private lateinit var imgBtnNotification: ImageView;
    private lateinit var imgBtnLogout: ImageView;

    private lateinit var imgFeedDisplay: ImageView;

    private lateinit var accountOps: AccountHelper;

    private lateinit var btnDiakonia: Button;

    private lateinit var textDisplayName: TextView;
    private lateinit var imgProfilePicture: ImageView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialize(inflater, container);
        widgetHandler();
        loadUserProfile();
        loadBanner();

        return mainView;
    }

    private fun initialize(inflater: LayoutInflater, container: ViewGroup?) {
        mainView = inflater.inflate(R.layout.fragment_home, container, false);

        imgBtnNotification = mainView.findViewById(R.id.img_btn_notification);
        imgBtnLogout = mainView.findViewById(R.id.img_btn_logout);
        imgFeedDisplay = mainView.findViewById(R.id.img_banner_display);

        accountOps = AccountHelper(requireActivity());

        btnDiakonia = mainView.findViewById(R.id.btn_diakonia);

        textDisplayName = mainView.findViewById(R.id.text_display_name);
        imgProfilePicture = mainView.findViewById(R.id.img_profile_author);
    }

    private fun widgetHandler() {
        imgBtnNotification.setOnClickListener {
            System.setToast(requireActivity(), "Mengecek notifikasi");
        }

        imgBtnLogout.setOnClickListener {
            System.setToast(requireActivity(), "Keluar dari akun dan server!");

            accountOps.logoutUser();
        }

        btnDiakonia.setOnClickListener {
            val intent = Intent(requireContext(), DiakoniaActivity::class.java);

            startActivity(intent);
        }
    }

    private fun loadUserProfile() {
        val user = GlobalState.currentUser;

        if (user == null) {
            System.dialogMessageBox(requireActivity(), "Error", "User tidak ditemukan! Silahkan login ulang.");

            val accountHelper = AccountHelper(requireActivity());
            accountHelper.logoutUser();

            return;
        }

        textDisplayName.text = user.displayName;

        GlobalScope.launch {
            val profilePicture = StorageHelper.getUserProfilePicture(user.profilePicture);
            val globalAppVersion = DatabaseHelper.getGlobalAppVersion();

            val onClose = fun() {
                requireActivity().finish();
            }

            if (System.APP_VERSION != globalAppVersion) {
                System.dialogMessageBox( requireActivity(), "Update!", "Tolong update aplikasi terlebih dahulu!", onClose);
            }

            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(profilePicture)
                    .into(imgProfilePicture);
            }
        }
    }

    private fun loadBanner() {
        GlobalScope.launch(Dispatchers.IO) {

            try {
                val currentBanner = DatabaseHelper.getCurrentBanner()!!;
                val bannerUri = StorageHelper.getBanner(currentBanner);

                val bannerW = 1920;
                val bannerH = 823;

                val reqOpts = RequestOptions
                    .centerCropTransform()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(bannerUri)
                        .apply(reqOpts)
                        .into(imgFeedDisplay);
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    System.dialogMessageBox(requireActivity(), "Error", e.message.toString());
                }
            }
        }
    }
}