package lesniewski.pawel.uwd_android_studio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import lesniewski.pawel.uwd_android_studio.fragmentsService.MainMenu
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger
import lesniewski.pawel.uwd_android_studio.services.BluetoothNameService


class MainActivity : AppCompatActivity(), IFragmentChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ChangeFragment(supportFragmentManager, R.id.fragmentMenu,
            MainMenu()
        )
        StartBluetoothNameService()
        //make it on top

    }

    private fun StartBluetoothNameService() {
        val intent = Intent(this, BluetoothNameService::class.java)
        startService(intent)
    }


}

