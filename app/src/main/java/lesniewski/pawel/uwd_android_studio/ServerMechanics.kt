package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import java.io.InputStream
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class ServerMechanics() : AppCompatActivity(), Serializable, IBluetoothConnectionManager {

    var serverSocket : BluetoothServerSocket? = null
    private var TAG = "Server Mechanics -------- "
    lateinit var ROOM_NAME: String
    lateinit var bAdapter: BluetoothAdapter
    lateinit var devices: ArrayList<String>
    lateinit var amount: String
    lateinit var pairedList: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var gameInfoBar: TextView
    //lateinit var clientsModel : ArrayList<ClientListenerModel>
    //TODO
    //Class ClientListenerModel
    //init with socket, every clientmodel has write to write to him, and listening on other thread
    //based on SendReceive


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_mechanics)
        getDevicesAndViews()
        setListAdapter()
        waitingForPlayers()

    }

    @SuppressLint("SetTextI18n")
    private fun waitingForPlayers()    {
        gameInfoBar.text = resources.getString(R.string.gameStatus) + resources.getString(R.string.waitingForPlayers)
        serverSocket = getServerSocket(ROOM_NAME)

        Thread(Runnable{

            var limit = amount.toInt()
            var cnt = 0

            while(limit > 0)
            {
                try
                {
                    val tmpSocket: BluetoothSocket = serverSocket!!.accept()
                    //clientsModel.add(ClientListenerModel(tmpSocket))
                    cnt++
                    limit--

                    runOnUiThread(Runnable{
                        this@ServerMechanics.gameInfoBar.text = resources.getString(R.string.gameStatus) + resources.getString(R.string.connected) + cnt.toString() + " / " + amount
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
            }
        })

    }


    private fun setListAdapter() {

        bAdapter = BluetoothAdapter.getDefaultAdapter()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices)
        pairedList.adapter = adapter
    }

    private fun getDevicesAndViews() {
        pairedList = findViewById<ListView>(R.id.approvingPlayersList)
        gameInfoBar = findViewById<TextView>(R.id.gameInfoBar)

        val int: Intent = intent
        amount = int.getStringExtra("amount")!!
        devices = int.getSerializableExtra("devices") as ArrayList<String>
        ROOM_NAME = int.getStringExtra("roomName")!!
    }
}