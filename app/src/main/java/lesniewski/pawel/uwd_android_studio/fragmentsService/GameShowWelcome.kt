package lesniewski.pawel.uwd_android_studio.fragmentsService

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lesniewski.pawel.uwd_android_studio.R

class GameShowWelcome : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_game_show_welcome, container, false)

        //TODO
        //dots should be in motion
        return v
    }

}