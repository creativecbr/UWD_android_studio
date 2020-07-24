package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JoinToRoom : AppCompatActivity() {

    var TAG:String="aplka"
    lateinit var scanButton: Button
    lateinit var lvDevicesList: ListView
    lateinit var connectStatus: TextView
    var BTDevices = ArrayList<BluetoothDevice>()
    var arrayAdapter: ArrayAdapter<BluetoothDevice>? = null
    var bAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_room)

        scanButton = findViewById(R.id.scanButton)
        lvDevicesList = findViewById(R.id.foundDevicesList)
        connectStatus = findViewById(R.id.connectStatus)

        arrayAdapter =  ArrayAdapter<BluetoothDevice>(
            this,
            android.R.layout.simple_list_item_1,
            BTDevices
        )
        lvDevicesList.adapter = arrayAdapter

        scanButton.setOnClickListener {

            if(bAdapter.isDiscovering)
            {
                bAdapter.cancelDiscovery()
                bAdapter.startDiscovery()
                var discoverIntentFilter:IntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(scanReceiver, discoverIntentFilter)
            }
            if(!bAdapter.isDiscovering)
            {
                bAdapter.startDiscovery()
                var discoverIntentFilter:IntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(scanReceiver, discoverIntentFilter)
            }
        }

    }

    val scanReceiver = object: BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(ctx: Context?, intent: Intent?) {
            var action: String? = intent?.action
            Log.d(TAG, "action found")
            if(BluetoothDevice.ACTION_FOUND == action)
            {

                var device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    scanButton.text = device.name
                    BTDevices.add(device)
                }
                arrayAdapter?.notifyDataSetChanged()
            }

        }

    }


}