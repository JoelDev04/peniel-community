package id.jostudios.penielcommunity.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.Helpers.AccountHelper
import id.jostudios.penielcommunity.R

class HomeFragment : Fragment() {
    private lateinit var mainView: View;

    private lateinit var imgBtnNotification: ImageView;
    private lateinit var imgBtnLogout: ImageView;

    private lateinit var imgFeedDisplay: ImageView;

    private lateinit var accountOps: AccountHelper;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialize(inflater, container);
        widgetHandler();
        loadBanner();

        return mainView;
    }

    private fun initialize(inflater: LayoutInflater, container: ViewGroup?) {
        mainView = inflater.inflate(R.layout.fragment_home, container, false);

        imgBtnNotification = mainView.findViewById(R.id.img_btn_notification);
        imgBtnLogout = mainView.findViewById(R.id.img_btn_logout);
        imgFeedDisplay = mainView.findViewById(R.id.img_banner_display);

        accountOps = AccountHelper(requireActivity());
    }

    private fun widgetHandler() {
        imgBtnNotification.setOnClickListener {
            System.setToast(requireActivity(), "Mengecek notifikasi");
        }

        imgBtnLogout.setOnClickListener {
            System.setToast(requireActivity(), "Keluar dari akun dan server!");

            accountOps.logoutUser();
        }
    }

    private fun loadBanner() {
//        val storageOps = StorageOps(FirebaseOps.getStorageReference());
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val bannerUri = storageOps.getBanner("Untitled.png");
//            val bannerW = 1280;
//            val bannerH = 720;
//
//            val reqOpts = RequestOptions
//                .centerCropTransform()
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
//
//            withContext(Dispatchers.Main) {
//                Glide.with(requireContext())
//                    .load(bannerUri)
//                    .apply(reqOpts)
//                    .into(imgFeedDisplay);
//            }
//        }
    }
}