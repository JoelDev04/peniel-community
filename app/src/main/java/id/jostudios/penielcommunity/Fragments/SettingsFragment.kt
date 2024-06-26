package id.jostudios.penielcommunity.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        checkUpdate();

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private fun checkUpdate() {
//        GlobalScope.launch {
//            val globalAppVersion = DatabaseHelper.getGlobalAppVersion();
//
//            val onClose = fun() {
//                requireActivity().finish();
//            }
//
//            if (System.APP_VERSION != globalAppVersion) {
//                System.dialogMessageBox( requireActivity(), "Update!", "Tolong update aplikasi terlebih dahulu!", onClose);
//            }
//        }
    }
}