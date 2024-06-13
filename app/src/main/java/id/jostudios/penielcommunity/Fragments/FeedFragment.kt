package id.jostudios.penielcommunity.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.jostudios.penielcommunity.Activities.UploadFeedActivity
import id.jostudios.penielcommunity.Adapters.FeedAdapter
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedFragment(private var mainViewModel: MainViewModel): Fragment() {

    private lateinit var mainView: View;

    private lateinit var swipeRefresh: SwipeRefreshLayout;
    private lateinit var recylerFeed: RecyclerView;
    private lateinit var floatBtnUpload: FloatingActionButton;

    //private lateinit var dbOps: DatabaseOps;

    private lateinit var homeFragment: HomeFragment;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialize(inflater, container);
        GlobalScope.launch(Dispatchers.IO) {
            try {
                System.showLoadingDialog(requireActivity());
                loadFeedData();
                System.destroyLoadingDialog();
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

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java];
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

        var feedList = DatabaseHelper.getFeeds();

        if (feedList.size <= 0) {

            return;
        }

        feedList.sortByDescending {
            it.uploadTime
        };

        var adapter = FeedAdapter(feedList);

        withContext(Dispatchers.Main) {
            adapter.notifyDataSetChanged();

            recylerFeed.layoutManager = LinearLayoutManager(requireActivity());
            recylerFeed.adapter = adapter;
        }
    }
}