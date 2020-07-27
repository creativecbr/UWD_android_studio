package lesniewski.pawel.uwd_android_studio


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class JoinToRoom : AppCompatActivity(), AdapterView.OnItemClickListener {

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
        this.nextButton = findViewById(R.id.refreshListButton)
        lvDevicesList.onItemClickListener = this

      // to remove
        connectStatus.text = bAdapter.address


        val statusBondedFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(statusBondedBReceiver, statusBondedFilter)

        this.nextButton.setOnClickListener{
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DISCOVERABILTY)
        }

        this.scanButton.setOnClickListener{

            btDevices.clear()

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

                     btDevices.add(device)
                     val mDeviceListAdapter = DeviceListAdapter(ctx!!, R.layout.device_adapter_view, this@JoinToRoom.btDevices)
                     lvDevicesList.adapter = mDeviceListAdapter

                }
            }
        }
    }

    private val statusBondedBReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val mDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//case1:        // bonded already
                if (mDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                    connectStatus.text = "polaczono"
                }
                // creating a bone
                if (mDevice!!.bondState == BluetoothDevice.BOND_BONDING) {
                    connectStatus.text = "łączenie"
                }
                // breaking a bond
                if (mDevice!!.bondState == BluetoothDevice.BOND_NONE) {
                    connectStatus.text = "brak połączenia"
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

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, i: Int, l: Long) {

        bAdapter.cancelDiscovery()

        var dN = btDevices[i].name
        var dA = btDevices[i].alias
        var dAd = btDevices[i].address

        btDevices[i].createBond()

    }


}

