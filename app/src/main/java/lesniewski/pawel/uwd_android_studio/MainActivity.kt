package lesniewski.pawel.uwd_android_studio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(), IFragmentChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ChangeFragment(supportFragmentManager, R.id.fragmentMenu, GameMainMenu())

    }



}

interface IFragmentChanger{

    fun ChangeFragment(fragmentManager: FragmentManager, fragment: Int, secondFragment: Fragment) {

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(fragment, secondFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}