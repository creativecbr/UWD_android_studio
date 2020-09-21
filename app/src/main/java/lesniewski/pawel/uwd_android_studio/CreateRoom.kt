package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.getDefaultAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_room.*
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger
import lesniewski.pawel.uwd_android_studio.tools.Constants.REQUEST_CODE_ENABLE_BT
import java.util.*

class CreateRoom : AppCompatActivity(), IFragmentChanger {


    private var bluetoothOn = false
    lateinit var bAdapter:BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_create_room)
      bAdapter = getDefaultAdapter()

      // back button service
      var btn = findViewById<View>(R.id.backButton) as Button
      btn.setOnClickListener{
          finish()
      }

      //send input button service
      btn = findViewById<View>(R.id.refreshListButton) as Button
      btn.setOnClickListener{
          bluetoothRequestIfDisabled()

      }

  }
  private fun goToServerWaitingActivity()
  {
      val roomName =  getInputRoomName()

      bAdapter.name = "[UWD]$roomName"
      val intent = Intent(this, ServerWaitingForPlayers::class.java)
      intent.putExtra("roomName", roomName)
      startActivity(intent)
  }

    private fun getInputRoomName(): String {
        val tmp = inputRoomName.text.toString()
        if(tmp!="")
            return tmp
        else
            return "room_997"
    }
  private fun bluetoothRequestIfDisabled()
  {
      if(!bAdapter.isEnabled)
      {
          val blInt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
          startActivityForResult(blInt, REQUEST_CODE_ENABLE_BT)
      }
      else
          goToServerWaitingActivity()

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?) {

      when(requestCode)
      {
          REQUEST_CODE_ENABLE_BT ->
              if (resultCode != RESULT_OK)
              {
                  Toast.makeText(this, "Najpierw musisz zezwolić na uruchomienie bluetooth!", Toast.LENGTH_LONG).show()
              }
              else
                  goToServerWaitingActivity()
      }
      super.onActivityResult(requestCode, resultCode, data)
  }




}