package id.jostudios.penielcommunity.ViewModels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private var m_currentFragment: MutableStateFlow<Fragment?> = MutableStateFlow(null);

    public fun currentFragment(): StateFlow<Fragment?> = m_currentFragment.asStateFlow();

    public fun setCurrentFragment(fragment: Fragment) {
        if (fragment == currentFragment().value) { return; }

        m_currentFragment.value = fragment;
    }
}