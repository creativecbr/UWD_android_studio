package lesniewski.pawel.uwd_android_studio


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class JoinToRoom : AppCompatActivity() {

    var TAG:String="JoinToRoom"
    private var DISCOVERABLE_DURATION = 0
    private var REQUEST_CODE_ENABLE_DISCOVERABILTY = 2
    lateinit var scanButton: Button
    lateinit var nextButton: Button
    lateinit var lvDevicesList: ListView
    lateinit var connectStatus: TextView
    var btDevices = ArrayList<BluetoothDevice>()
    var bAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var deviceListAdapter : DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_room)

        this.scanButton = findViewById(R.id.scanButton)
        this.lvDevicesList = findViewById(R.id.foundDevicesList)
        this.connectStatus = findViewById(R.id.connectStatus)
        this.nextButton = findViewById(R.id.nextButton)

        this.nextButton.setOnClickListener{

        }

        this.scanButton.setOnClickListener{

            if(this.bAdapter.isDiscovering)
            {
                this.bAdapter.cancelDiscovery()
                checkBTPermissions();
                this.bAdapter.startDiscovery()

                val findingIntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(this.scanReceiver, findingIntentFilter)

            }
            if(!this.bAdapter.isDiscovering)
            {
                checkBTPermissions();
                bAdapter.startDiscovery()
                val findingIntentFilter  = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(this.scanReceiver, findingIntentFilter)
            }
        }

    }

    override fun onDestroy() {

        super.onDestroy()
        unregisterReceiver(scanReceiver)
        bAdapter.cancelDiscovery();
    }

    private val scanReceiver = object: BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val action: String? = intent?.action
            connectStatus.text = "mam"
            if(BluetoothDevice.ACTION_FOUND == action)
            {

                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {

                    if(!btDevices.contains(device))
                    {
                        btDevices.add(device)
                        val mDeviceListAdapter = DeviceListAdapter(ctx!!, R.layout.device_adapter_view, this@JoinToRoom.btDevices)
                        lvDevicesList.adapter = mDeviceListAdapter
                    }
                }
            }
        }
    }


    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck =
                checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1001
                ) //Any number
            }
        } else {
            Log.d(
                TAG,
                "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP."
            )
        }
    }





}

