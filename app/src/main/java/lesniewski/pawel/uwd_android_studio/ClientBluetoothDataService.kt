package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import lesniewski.pawel.uwd_android_studio.fragmentsService.GameShowQuestion
import java.io.Serializable

class ClientBluetoothDataService() : AppCompatActivity(), IFragmentChanger, IBluetoothDataService, Serializable {

    lateinit var readyBtn: Button
    lateinit var btSocket: BluetoothSocket
    lateinit var btDevice: BluetoothDevice


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_mechanics)
        findHandlers()
        connectToServer()

        readyBtn.setOnClickListener{
            ChangeFragment( supportFragmentManager, R.id.fragmentClientMechanics, GameShowQuestion())

        }

    }

    private fun connectToServer() {
        /*
        try {
            btSocket = device.createRfcommSocketToServiceRecord(APP_UUID)
            socket.connect()

            connected = true

        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }


    private fun findHandlers() {
        readyBtn = findViewById<Button>(R.id.readyBtn)

        val extras = intent.extras
        if (extras != null) {
            btDevice = extras.getParcelable<BluetoothDevice>("server")!!
        }

    }


}