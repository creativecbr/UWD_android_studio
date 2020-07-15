package lesniewski.pawel.uwd_android_studio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_waiting_for_players.*

class ServerWaitingForPlayers : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_waiting_for_players)

        showRoomName()
    }




    fun showRoomName()
    {
        val extras = intent.extras
        var name = ""
        if (extras != null) {
            name = extras.getString("roomName").toString()
        }
        showRoomName.text=name
    }
}