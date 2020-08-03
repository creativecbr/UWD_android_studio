package lesniewski.pawel.uwd_android_studio

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import lesniewski.pawel.uwd_android_studio.fragmentsService.GameHowToPlay
import lesniewski.pawel.uwd_android_studio.fragmentsService.GameShowQuestion

class ClientMechanics() : AppCompatActivity(), IFragmentChanger {

    lateinit var readyBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_mechanics)
        findViews()

        readyBtn.setOnClickListener{
            ChangeFragment( supportFragmentManager, R.id.fragmentClientMechanics, GameShowQuestion())

        }

    }



    private fun findViews() {
        readyBtn = findViewById<Button>(R.id.readyBtn)

    }


}