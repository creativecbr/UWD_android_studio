package lesniewski.pawel.uwd_android_studio

import android.R.string
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_waiting_for_players.*


class ServerWaitingForPlayers : AppCompatActivity()
{
    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private var bluetoothOn = false
    lateinit var bAdapter: BluetoothAdapter
    lateinit var strings: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_waiting_for_players)
        showRoomName()

        bAdapter = BluetoothAdapter.getDefaultAdapter()
        val nextButton = findViewById<Button>(R.id.nextButton)
        val refreshButton = findViewById<Button>(R.id.refreshButton)

        strings = getBoundedDevices()

        var pairedList = findViewById<ListView>(R.id.pairedDevicesList)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            strings
        )
        pairedList.adapter = adapter


        refreshButton.setOnClickListener{
            var tmp = getBoundedDevices()
            for (device in tmp)
                if(!strings.contains(device))
                    strings.add(device)

            adapter.notifyDataSetChanged()
        }
    }

    private fun getBoundedDevices(): ArrayList<String> {

        var bt = bAdapter.bondedDevices
        val strings: ArrayList<String> = ArrayList<String>()

        if(bt.size > 0)
        {
            for (device in bt) {
                strings.add(device.name)
            }
        }
        else
        {
            nextButton.isEnabled = false
        }

        return strings
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