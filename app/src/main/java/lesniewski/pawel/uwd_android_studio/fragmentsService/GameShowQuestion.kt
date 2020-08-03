package lesniewski.pawel.uwd_android_studio.fragmentsService

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import lesniewski.pawel.uwd_android_studio.CreateRoom
import lesniewski.pawel.uwd_android_studio.GameMainMenu
import lesniewski.pawel.uwd_android_studio.R



class GameShowQuestion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_game_show_question, container, false)



        return v
    }
}

