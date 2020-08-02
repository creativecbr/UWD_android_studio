package lesniewski.pawel.uwd_android_studio


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_game_main_menu.*


class GameMainMenu : Fragment(), IFragmentChanger {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_game_main_menu, container, false)

        var btn = v.findViewById<View>(R.id.button4) as Button
        btn.setOnClickListener{
            //ChangeFragment(activity!!.supportFragmentManager, R.id.fragmentMenu, GameAboutAuthor())
            val intent = Intent(activity, GameClientMechanics::class.java)
            startActivity(intent)
        }

        btn = v.findViewById<View>(R.id.button3) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager, R.id.fragmentMenu, GameHowToPlay())
        }

        btn = v.findViewById<View>(R.id.button1) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager, R.id.fragmentMenu, GameDeviceTypeChoosing())
        }

        return v
    }

}