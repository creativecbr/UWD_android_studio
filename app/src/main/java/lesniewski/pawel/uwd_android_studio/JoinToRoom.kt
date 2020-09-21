package lesniewski.pawel.uwd_android_studio


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_NONE
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.tools.Constants.DISCOVERABLE_DURATION
import lesniewski.pawel.uwd_android_studio.tools.Constants.REQUEST_CODE_ENABLE_DISCOVERABILTY
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList


class JoinToRoom : AppCompatActivity(), AdapterView.OnItemClickListener, IBluetoothConnectionManager {

    var TAG: String = "JoinToRoom: "

    lateinit var scanButton: Button
    lateinit var nextButton: Button
    lateinit var lvDevicesList: ListView
    lateinit var connectStatus: TextView
    var btDevices = ArrayList<BluetoothDevice>()
    var bAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var deviceListAdapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_room)

        findByViews()
        setOnClicks()
        registerReceivers()

    }

    private fun registerReceivers() {
        val statusBondedFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(statusBondedBReceiver, statusBondedFilter)
    }

    private fun setOnClicks()
    {

        this.nextButton.setOnClickListener {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DISCOVERABILTY)
        }

        this.scanButton.setOnClickListener {

            btDevices.clear()
            val mDeviceListAdapter =
                DeviceListAdapter(this, R.layout.device_adapter_view, this@JoinToRoom.btDevices)
            lvDevicesList.adapter = mDeviceListAdapter

            if (this.bAdapter.isDiscovering)
            {
                this.bAdapter.cancelDiscovery()
                checkBTPermissionsAndStartDiscovery()

            }
            if (!this.bAdapter.isDiscovering)
            {
                checkBTPermissionsAndStartDiscovery()
            }
        }
    }

    private fun findByViews() {
        this.scanButton = findViewById(R.id.scanButton)
        this.lvDevicesList = findViewById(R.id.approvingPlayersList)
        this.connectStatus = findViewById(R.id.connectStatus)
        this.nextButton = findViewById(R.id.refreshListButton)
        lvDevicesList.onItemClickListener = this
    }

    private fun checkBTPermissionsAndStartDiscovery()
    {
        checkBTPermissions();
        this.bAdapter.startDiscovery()

        val findingIntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(this.scanReceiver, findingIntentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(scanReceiver)
        unregisterReceiver(statusBondedBReceiver)
        bAdapter.cancelDiscovery();
        super.onDestroy()
    }

    private val scanReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    if (!btDevices.contains((device))) {
                        if (device.name != null) {
                            if (device.name.contains("[UWD]")) {
                                btDevices.add(device)
                                val mDeviceListAdapter = DeviceListAdapter(
                                    ctx!!,
                                    R.layout.device_adapter_view,
                                    this@JoinToRoom.btDevices
                                )
                                lvDevicesList.adapter = mDeviceListAdapter
                            }
                        }
                    }
                }
            }
        }
    }

    private val statusBondedBReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val mDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // bonded already
                if (mDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                    connectStatus.text =
                        resources.getString(R.string.connectStatusBar) + " połączono"
                    sendNameToServer(mDevice)

                }
                // creating a bone
                if (mDevice.bondState == BluetoothDevice.BOND_BONDING) {
                    connectStatus.text =
                        resources.getString(R.string.connectStatusBar) + " łączenie"
                }
                // breaking a bond
                if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                    connectStatus.text =
                        resources.getString(R.string.connectStatusBar) + " brak połączenia"
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
        if(btDevices[i].bondState == BOND_NONE)
            btDevices[i].createBond()
        else
            sendNameToServer(btDevices[i])

    }

    private fun sendNameToServer(device: BluetoothDevice) {

        val socket = connectToServer(device)
        if(socket != null && socket.isConnected)
        {
            if(sendStringToGameServerSocket(socket, bAdapter.name))
            {
                val inte = Intent(this@JoinToRoom, ClientMechanics::class.java)
                inte.putExtra("server", device)
                socket.close()
                startActivity(inte)

            }
        }
    }


}






