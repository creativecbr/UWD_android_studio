package lesniewski.pawel.uwd_android_studio.fragmentsService


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import lesniewski.pawel.uwd_android_studio.ClientMechanics
import lesniewski.pawel.uwd_android_studio.R
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger

//starting class with menu options included in fragment
class MainMenu : Fragment(),
    IFragmentChanger {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        val v = inflater.inflate(R.layout.fragment_main_menu, container, false)

        var btn = v.findViewById<View>(R.id.button4) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager,
                R.id.fragmentMenu, AboutAuthor())
        }

        btn = v.findViewById<View>(R.id.button3) as Button
        btn.setOnClickListener{
            /*ChangeFragment(activity!!.supportFragmentManager,
                R.id.fragmentMenu,
                GameHowToPlay()
            )*/
            val intent = Intent(activity, ClientMechanics::class.java)
            startActivity(intent)
        }

        btn = v.findViewById<View>(R.id.button1) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager,
                R.id.fragmentMenu,
                SelectDeviceType()
            )
        }

        return v
    }

}