package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_waiting_for_players.*
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.tools.Constants.CONNECT_UUID
import lesniewski.pawel.uwd_android_studio.tools.Constants.DISCOVERABLE_DURATION
import lesniewski.pawel.uwd_android_studio.tools.Constants.REQUEST_CODE_ENABLE_DISCOVERABILTY
import java.io.Serializable
import kotlin.collections.ArrayList


class ServerWaitingForPlayers : AppCompatActivity(), Serializable, IBluetoothConnectionManager
{
    private var TAG = "Server Waiting for players -------- "
    var ROOM_NAME = ""
    var PLAYER_LIMIT = 0

    lateinit var refreshButton: Button
    lateinit var nextButton: Button
    lateinit var backButton: Button
    lateinit var refreshListButton: Button
    lateinit var discoverableButton: Button
    lateinit var connectedInfo: TextView
    lateinit var pairedList: ListView
    lateinit var bAdapter: BluetoothAdapter
    lateinit var adapter: ArrayAdapter<String>
    lateinit var scanModeReceiver: IBluetoothConnectionManager.ScanModeReceiver
    lateinit var bondedReceiver: IBluetoothConnectionManager.StatusBondedReceiver
    var devices = ArrayList<String>()

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_waiting_for_players)

        findAllViews()
        showRoomNameAndPlayerLimit()

        //TODO
        //selector for players amount
        //limitPlayersAmount(1)

        registerReceivers()
        setOnClicks()
        discoverabilityButtonService()
        clientsCollecting()





    }

    private fun setOnClicks() {
        refreshListButton.setOnClickListener{


        }
    }

    private fun registerReceivers() {

        val statusBondedFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        bondedReceiver =IBluetoothConnectionManager.StatusBondedReceiver(connectedInfo)
        registerReceiver(bondedReceiver, statusBondedFilter)

    }
/*
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

    private val statusBondedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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
    */

    private fun discoverabilityButtonService() {

        discoverableButton.setOnClickListener{

            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DISCOVERABILTY)

            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            scanModeReceiver = IBluetoothConnectionManager.ScanModeReceiver(discoverableButton, resources)
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
       // PLAYER_LIMIT = limit
    }


    private fun findAllViews() {
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        nextButton = findViewById<Button>(R.id.nextButton)
        refreshButton = findViewById<Button>(R.id.discoverableButton)
        pairedList = findViewById<ListView>(R.id.pairedDevicesList)
        connectedInfo = findViewById<TextView>(R.id.connectedInfo)
        discoverableButton = findViewById<Button>(R.id.discoverableButton)
        refreshListButton = findViewById<Button>(R.id.nextButton)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices)
        pairedList.adapter = adapter
    }


    @SuppressLint("SetTextI18n")
    fun showRoomNameAndPlayerLimit()
    {
        val extras = intent.extras
        var name = ""
        var limit = 0
        if (extras != null) {
            name = extras.getString("roomName").toString()
            limit = extras.getString("playersLimit")!!.toInt()

            ROOM_NAME = name
            PLAYER_LIMIT = limit

            showRoomName.text = name
            maxPlayersInfo.text =
                resources.getString(R.string.maxPlayersInfo) + PLAYER_LIMIT.toString()
        }
        else{
            Log.d(TAG, "Cant transfer extras with room name and limit.")
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        unregisterReceiver(scanModeReceiver)
        unregisterReceiver(bondedReceiver)
        bAdapter.cancelDiscovery();
    }

    @ExperimentalStdlibApi
    @SuppressLint("SetTextI18n")
    private fun clientsCollecting() {

        val serverSocket = getServerSocket(ROOM_NAME, CONNECT_UUID)

        var limit = PLAYER_LIMIT
        var cnt = 0

        if(serverSocket != null) {

            Thread(Runnable {

                while (limit > 0) {
                    try {
                        val tmpSocket: BluetoothSocket = serverSocket.accept()

                        if (tmpSocket.isConnected) {
                            Log.d(TAG, "CONNECTED: found device number " + (cnt+1).toString())
                        }
                        cnt++
                        limit--
                        val tmpModel = receiveStringFromSocket(tmpSocket)
                        devices.add(tmpModel!!)

                        runOnUiThread(Runnable {
                            this@ServerWaitingForPlayers.connectedInfo.text =
                                resources.getString(R.string.connectedPlayersInfo) + tmpModel//cnt.toString()
                        })


                    } catch (e: java.lang.Exception) {
                        Log.d(TAG, "Cant accept any connection")
                        break
                    }
                }

                 serverSocket.close() //v.f.5
                 val intent = Intent(this@ServerWaitingForPlayers, ServerMechanics::class.java)
                 intent.putExtra("amount", PLAYER_LIMIT.toString())
                 intent.putExtra("roomName", ROOM_NAME)
                 startActivity(intent)


            }).start()
        }
    }
}