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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_waiting_for_players.*
import java.util.*
import kotlin.collections.ArrayList


class ServerWaitingForPlayers : AppCompatActivity()
{
    private var TAG = "akcja!"
    private var ROOM_NAME = "---"
    private var PLAYER_LIMIT = 6
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
        limitPlayersAmount(6)
        registerReceivers()
        discoverabilityButtonService()

        refreshListButton.setOnClickListener{

            var bt : Set<BluetoothDevice> = bAdapter.bondedDevices

            if(bt.isNotEmpty())
            {
                for(device in bt)
                {
                    strings.add(device.name)
                }
            }
            val arAp = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                strings
            )
            pairedList.adapter = arAp
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
                if (mDevice!!.bondState == BluetoothDevice.BOND_BONDING) {
                    connectedInfo.text = "łączenie"
                }
                // breaking a bond
                if (mDevice!!.bondState == BluetoothDevice.BOND_NONE) {
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

    inner class CollectingPlayers(var serverSocket: BluetoothServerSocket): Thread()
    {
        init{
            try{
                serverSocket = bAdapter.listenUsingRfcommWithServiceRecord(ROOM_NAME, APP_UUID)
            }
            catch (e: Exception)
            {
                println("Server socket listening set error.")
            }
        }

        @SuppressLint("SetTextI18n")
        override fun run() {
            val sockets: ArrayList<BluetoothSocket> = ArrayList()
            var limit = PLAYER_LIMIT
            var cnt = 0

            while(limit>0)
            {
                val tmpSocket:BluetoothSocket = serverSocket.accept()
                sockets.add(tmpSocket)
                connectedInfo.text = resources.getString(R.string.connectedPlayersInfo) + cnt.toString()


                //getting data from client on tmpSocket
                //add received data to string list and notify

                cnt++
                limit--
            }

        }



    }
}