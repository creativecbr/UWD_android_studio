package lesniewski.pawel.uwd_android_studio


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_game_about_author.*



class GameAboutAuthor : Fragment(R.layout.fragment_game_about_author), IFragmentChanger
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_game_about_author, container, false)

        val btn = v.findViewById<View>(R.id.backButton) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager, R.id.fragmentMenu, GameMainMenu())
        }


        return v
    }


}