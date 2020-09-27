package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.getDefaultAdapter
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_room.*
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger
import lesniewski.pawel.uwd_android_studio.tools.Constants.REQUEST_CODE_ENABLE_BT
import lesniewski.pawel.uwd_android_studio.tools.PlayerLimitFilter


class CreateRoom : AppCompatActivity(), IFragmentChanger {


    private var bluetoothOn = false
    lateinit var playerLimitBar: EditText
    lateinit var inputRoomName: EditText
    lateinit var bAdapter:BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_create_room)
      getViews()

      // back button service
      var btn = findViewById<View>(R.id.backButton) as Button
      btn.setOnClickListener{
          finish()
      }

      //send input button service
      btn = findViewById<View>(R.id.nextButton) as Button
      btn.setOnClickListener{
          bluetoothRequestIfDisabled()
      }



  }

    private fun getViews() {
        bAdapter = getDefaultAdapter()
        playerLimitBar = findViewById<EditText>(R.id.playerLimit)
        playerLimitBar.filters = arrayOf<InputFilter>(PlayerLimitFilter("1","5"))
        inputRoomName = findViewById<EditText>(R.id.inputRoomName)
    }

    private fun goToServerWaitingActivity()
  {
      val roomName =  getInputRoomName()
      val limit = getPlayersLimit()

      bAdapter.name = "[UWD]$roomName"
      val intent = Intent(this, ServerWaitingForPlayers::class.java)
      intent.putExtra("roomName", roomName)
      intent.putExtra("playersLimit", limit)
      startActivity(intent)
  }

    private fun getPlayersLimit(): String {

        val limit = playerLimitBar.text.toString()
        return if(limit!="")
            limit
        else
            "1"
    }

    private fun getInputRoomName(): String {

        val name = inputRoomName.text.toString()

        return if(name!="")
            name
        else
            "XVC_STUDIO"
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