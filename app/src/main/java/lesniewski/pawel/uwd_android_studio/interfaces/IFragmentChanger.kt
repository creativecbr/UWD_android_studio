package lesniewski.pawel.uwd_android_studio.interfaces

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface IFragmentChanger{

    fun ChangeFragment(fragmentManager: FragmentManager, fragment: Int, secondFragment: Fragment) {

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(fragment, secondFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}