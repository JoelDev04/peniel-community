package id.jostudios.penielcommunity.ViewModels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private var m_currentFragment: MutableStateFlow<Fragment?> = MutableStateFlow(null);

    private var m_auth: MutableStateFlow<FirebaseAuth?> = MutableStateFlow(null);

    public fun currentFragment(): StateFlow<Fragment?> = m_currentFragment.asStateFlow();
    public fun auth(): StateFlow<FirebaseAuth?> = m_auth.asStateFlow();

    public fun setCurrentFragment(fragment: Fragment) {
        if (fragment == currentFragment().value) { return; }

        m_currentFragment.value = fragment;
    }

    public fun setAuth(auth: FirebaseAuth) {
        m_auth.value = auth;
    }
}