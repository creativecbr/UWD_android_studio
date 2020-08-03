package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_waiting_for_players.*
import java.io.InputStream
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class ServerWaitingForPlayers : AppCompatActivity(), Serializable
{
    private var TAG = "Server Waiting for players -------- "
    private var ROOM_NAME = "---"
    private var PLAYER_LIMIT = 1
    private val APP_UUID = UUID.fromString("3b4c7719-3738-4234-94a3-22d72dbb8a74")
    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val REQUEST_CODE_ENABLE_DISCOVERABILTY: Int = 2
    private val DISCOVERABLE_DURATION = 120 // in seconds


    val STATE_LISTENING = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3
    val STATE_CONNECTION_FAILED = 4
    val STATE_MESSAGE_RECEIVED = 5


    lateinit var refreshButton: Button
    lateinit var nextButton: Button
    lateinit var backButton: Button
    lateinit var refreshListButton: Button
    lateinit var discoverableButton: Button
    lateinit var connectedInfo: TextView
    lateinit var devicesArray: ArrayList<BluetoothDevice>
    lateinit var pairedList: ListView
    lateinit var bAdapter: BluetoothAdapter
    var strings = ArrayList<String>()
    lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_waiting_for_players)

        findAllViews()
        showRoomNameAndPlayerLimit()
        limitPlayersAmount(1)
        registerReceivers()
        discoverabilityButtonService()
        startClientSocketListening()



        refreshListButton.setOnClickListener{
/*
            val bt : Set<BluetoothDevice> = bAdapter.bondedDevices
            strings.clear()
            if(bt.isNotEmpty())
            {
                for(device in bt)
                {
                    strings.add(device.name)
                }
            }*/

            strings.add("chujostwo")
            adapter.notifyDataSetChanged()

        }

    }

    private fun registerReceivers() {

        val statusBondedFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(statusBondedBReceiver, statusBondedFilter)

    }

    private val scanModeReceiver = object: BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(p0: Context?, intent: Intent?) {
            val action = intent?.action
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
            {
                val modeValue =
                    intent?.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)

                if(modeValue == BluetoothAdapter.SCAN_MODE_CONNECTABLE)
                {
                    discoverableButton.text = resources.getString(R.string.refreshAgain)
                    discoverableButton.isEnabled = true
                }
                else if(modeValue == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                {
                    discoverableButton.text =  resources.getString(R.string.discoverableNow)
                    discoverableButton.isEnabled = false
                }
                else if(modeValue == BluetoothAdapter.SCAN_MODE_NONE)
                {
                    discoverableButton.text  = resources.getString(R.string.refreshButton)
                    discoverableButton.isEnabled = true
                }
                else
                {
                    Toast.makeText(this@ServerWaitingForPlayers, resources.getString(R.string.problemWithDiscoverability), Toast.LENGTH_LONG).show()
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
                    connectedInfo.text = "polaczono"
                }
                // creating a bone
                if (mDevice.bondState == BluetoothDevice.BOND_BONDING) {
                    connectedInfo.text = "łączenie"
                }
                // breaking a bond
                if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                    connectedInfo.text = "brak połączenia"
                }
            }
        }
    }
    private fun discoverabilityButtonService() {

        discoverableButton.setOnClickListener{


            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DISCOVERABILTY)

            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            registerReceiver(scanModeReceiver, intentFilter)



        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?) {

        when(requestCode)
        {
            REQUEST_CODE_ENABLE_DISCOVERABILTY ->
                if (resultCode == Activity.RESULT_CANCELED)
                {
                    Toast.makeText(this, "Musisz być widoczny, aby inni gracze dołączyli do ciebie.", Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun limitPlayersAmount(limit: Int) {
        PLAYER_LIMIT = limit
    }


    private fun findAllViews() {
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        nextButton = findViewById<Button>(R.id.refreshListButton)
        refreshButton = findViewById<Button>(R.id.discoverableButton)
        pairedList = findViewById<ListView>(R.id.pairedDevicesList)
        connectedInfo = findViewById<TextView>(R.id.connectedInfo)
        discoverableButton = findViewById<Button>(R.id.discoverableButton)
        refreshListButton = findViewById<Button>(R.id.refreshListButton)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, strings)
        pairedList.adapter = adapter
    }


    @SuppressLint("SetTextI18n")
    fun showRoomNameAndPlayerLimit()
    {
        val extras = intent.extras
        var name = ""
        if (extras != null) {
            name = extras.getString("roomName").toString()
        }
        ROOM_NAME = name

        showRoomName.text=name
        maxPlayersInfo.text= resources.getString(R.string.maxPlayersInfo) + PLAYER_LIMIT.toString()
    }

    override fun onDestroy() {

        super.onDestroy()
        unregisterReceiver(scanModeReceiver)
        unregisterReceiver(statusBondedBReceiver)
        bAdapter.cancelDiscovery();
    }

    @SuppressLint("SetTextI18n")
    private fun startClientSocketListening() {

        val clientCollector = CollectingPlayers()
        clientCollector.start()

    }



    inner class CollectingPlayers() : Thread()
    {

        var serverSocket : BluetoothServerSocket? = null

        init{
            try{
                serverSocket = bAdapter.listenUsingRfcommWithServiceRecord(ROOM_NAME, APP_UUID)
            }
            catch (e: Exception)
            {
                println("Server socket listening set error.")
            }
        }


        @ExperimentalStdlibApi
        @SuppressLint("SetTextI18n")
        override fun run() {
            val devices: ArrayList<String> = ArrayList()
            var limit = PLAYER_LIMIT
            var cnt = 0

            while(limit>0)
            {
                try
                {
                    val tmpSocket:BluetoothSocket = serverSocket!!.accept()
                    val tmpModel = ReceiveNameOfDevice(tmpSocket)
                    devices.add(tmpModel)
                    cnt++
                    limit--

                    runOnUiThread(Runnable{
                        this@ServerWaitingForPlayers.connectedInfo.text = resources.getString(R.string.connectedPlayersInfo) + cnt.toString()
                    })


                }
                catch (e: java.lang.Exception)
                {
                    Log.d(TAG, "Cant accept any connection")
                }
            }
            if(serverSocket != null)
            {
                serverSocket!!.close()
                val intent = Intent(this@ServerWaitingForPlayers, ServerMechanics::class.java)
                intent.putExtra("devices", devices)
                intent.putExtra("amount", PLAYER_LIMIT.toString())
                startActivity(intent)
            }

        }
    }

    @ExperimentalStdlibApi
    private fun ReceiveNameOfDevice(socket: BluetoothSocket): String {
        val tempIn : InputStream
        val buffer = ByteArray(1024)
        var bytes = 0

        try{
            tempIn =  socket.inputStream

            while (bytes == 0)
            {
                try {
                    bytes = tempIn.read(buffer)
                    strings.add(buffer.decodeToString())

                    runOnUiThread(Runnable {
                        this@ServerWaitingForPlayers.adapter.notifyDataSetChanged()
                    })

                } catch (e: Exception) {
                    Log.d(TAG, "Cant read data from buffer.")
                }
            }
        }
        catch (e: Exception){
            Log.d(TAG, "Cant create input stream.")
        }
        return buffer.decodeToString()
    }
}