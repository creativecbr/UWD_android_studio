package lesniewski.pawel.uwd_android_studio.fragmentsService


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

import lesniewski.pawel.uwd_android_studio.R
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger


class HowToPlay : Fragment(R.layout.fragment_how_to_play),
    IFragmentChanger
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_how_to_play, container, false)

        val btn = v.findViewById<View>(R.id.backButton) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager,
                R.id.fragmentMenu,
                MainMenu()
            )
        }


        return v
    }


}