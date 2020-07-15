package lesniewski.pawel.uwd_android_studio


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class GameRoomChoosing : Fragment(), IFragmentChanger {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_game_room_choosing, container, false)

        val btn = v.findViewById<View>(R.id.backButton) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager, R.id.fragmentMenu, GameMainMenu())
        }

        val app_layer = v.findViewById(R.id.createRoomLayout) as LinearLayout
        app_layer.setOnClickListener{
            val intent = Intent(activity, CreateRoom::class.java)
            startActivity(intent)
        }

        return v
    }




}