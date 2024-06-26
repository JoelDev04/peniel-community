package id.jostudios.penielcommunity.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import id.jostudios.penielcommunity.Activities.DiakoniaActivity
import id.jostudios.penielcommunity.Activities.UploadFeedActivity
import id.jostudios.penielcommunity.Adapters.FeedAdapter
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedFragment(): Fragment() {

    private lateinit var mainView: View;

    private lateinit var swipeRefresh: SwipeRefreshLayout;
    private lateinit var recylerFeed: RecyclerView;
    private lateinit var floatBtnUpload: FloatingActionButton;

    //private lateinit var dbOps: DatabaseOps;

    private lateinit var homeFragment: HomeFragment;
    private lateinit var auth: FirebaseAuth;

    private val mainViewModel: MainViewModel by activityViewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialize(inflater, container);

        GlobalScope.launch(Dispatchers.IO) {
            try {
                System.showLoadingDialog(requireActivity());
                Thread.sleep(1000);
                loadFeedData();
                System.destroyLoadingDialog();

                val globalAppVersion = DatabaseHelper.getGlobalAppVersion();

                val onClose = fun() {
                    requireActivity().finish();
                }

                if (System.APP_VERSION != globalAppVersion) {
                    System.dialogMessageBox( requireActivity(), "Update!", "Tolong update aplikasi terlebih dahulu!", onClose);
                }

            }
            catch (e: Exception) {
                System.destroyLoadingDialog();
                System.dialogMessageBox(requireActivity(), "Error", e.message.toString());
            }
        }

        widgetHandler();

        return mainView;
    }

    private fun initialize(inflater: LayoutInflater, container: ViewGroup?) {
        mainView = inflater.inflate(R.layout.fragment_feed, container, false);

        swipeRefresh = mainView.findViewById(R.id.swipe_refresh);
        recylerFeed = mainView.findViewById(R.id.recycler_feed);
        floatBtnUpload = mainView.findViewById(R.id.btn_upload_feed);

        homeFragment = HomeFragment();

        auth = GlobalState.auth!!;
        auth.updateCurrentUser(GlobalState.firebaseUser!!);

        FirebaseHelper.setAuth(auth);
    }

    private fun widgetHandler() {
        floatBtnUpload.setOnClickListener {
            var uploadIntent = Intent(requireContext(), UploadFeedActivity::class.java);

            startActivity(uploadIntent);
        }

        swipeRefresh.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.IO) {
                loadFeedData();

                withContext(Dispatchers.Main) {
                    swipeRefresh.isRefreshing = false;
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadFeedData() {
        System.debug("Auth ID : ${auth.currentUser?.uid}");

        if (auth.currentUser == null) {
            System.destroyLoadingDialog();
            throw Exception("User not logged in");
        }

        try {
            val feedList = DatabaseHelper.getFeeds();
            feedList.remove(feedList.find { it.feedID == 100.toLong()});

            feedList.sortByDescending { it.uploadTime }

            //System.setToast(requireContext(), "Feed Size : ${feedList.size}");

            if (feedList.size <= 0) {
                //System.setToast(requireContext(), "Tidak ada feed yang di temukan!", Toast.LENGTH_LONG);
                val onDialogClose = fun() {
                    System.setToast(requireContext(), "Silahkan refresh atau kembali lagi beberapa saat kemudian!", Toast.LENGTH_LONG);
                    val textKosong: TextView = mainView.findViewById(R.id.text_kosong);

                    textKosong.visibility = View.VISIBLE;
                }

                System.dialogMessageBox(requireActivity(), "Info", "Tidak ada feed yang di temukan!", onDialogClose);
            }
            else {
                val textKosong: TextView = mainView.findViewById(R.id.text_kosong);

                textKosong.visibility = View.GONE;
            }

            val adapter = FeedAdapter(feedList);
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged();

                recylerFeed.adapter = adapter;
                recylerFeed.layoutManager = LinearLayoutManager(requireActivity());
            }
        }
        catch (e: Exception) {
            if (e.message?.contains("Permission denied")!!) {
                System.dialogMessageBox(requireActivity(), "Error", "Kembali ke home dahulu..");
                return;
            }
            System.dialogMessageBox(requireActivity(), "Error", e.message.toString());
        }
    }
}