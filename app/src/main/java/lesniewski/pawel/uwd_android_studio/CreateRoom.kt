package lesniewski.pawel.uwd_android_studio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_create_room.*

class CreateRoom : AppCompatActivity(), IFragmentChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        var btn = findViewById<View>(R.id.backButton) as Button
        btn.setOnClickListener{
            finish()
        }

        btn = findViewById<View>(R.id.nextButton) as Button
        btn.setOnClickListener{
            val intent = Intent(this, ServerWaitingForPlayers::class.java)
            intent.putExtra("roomName", inputRoomName.text.toString())
            startActivity(intent)
        }

    }




}