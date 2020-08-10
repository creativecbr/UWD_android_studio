package lesniewski.pawel.uwd_android_studio.interfaces

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.widget.Button
import android.widget.TextView
import lesniewski.pawel.uwd_android_studio.ClientMechanics
import lesniewski.pawel.uwd_android_studio.R
import lesniewski.pawel.uwd_android_studio.tools.Constants
import lesniewski.pawel.uwd_android_studio.tools.Constants.APP_UUID
import java.io.InputStream
import java.io.OutputStream

interface IBluetoothConnectionManager {

    fun connectToServer(device: BluetoothDevice): BluetoothSocket? {
        return try {
            val btSocket = device.createRfcommSocketToServiceRecord(APP_UUID)
            btSocket.connect()
            btSocket

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getServerSocket(roomName: String): BluetoothServerSocket? {

        var serverSocket : BluetoothServerSocket? = null

        try{
            serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(roomName, APP_UUID)

        }
        catch (e: Exception)
        {
            println("Server socket listening set error.")
        }

        return serverSocket

    }

    fun sendStringToGameServerSocket(socket: BluetoothSocket, name: String) : Boolean {

           val tempOut: OutputStream

        return try {
            val buffer = name.toByteArray()
            tempOut = socket.outputStream
            tempOut?.write(buffer)!!
            true
        } catch(e: Exception) {
            Log.d(TAG_,"Error until sending!")
            false
        }

    }

    @ExperimentalStdlibApi
    fun receiveStringFromSocket(socket: BluetoothSocket): String?
    {
        val tempIn : InputStream
        val buffer = ByteArray(1024)
        var bytes = 0

        try{
            tempIn =  socket.inputStream

            while (bytes == 0)
            {
                try {
                    bytes = tempIn.read(buffer)

                } catch (e: Exception) {
                    Log.d(TAG_, "Cant read data from buffer.")
                }
            }
        }
        catch (e: Exception){
            Log.d(TAG_, "Cant create input stream.")
        }

        return buffer.decodeToString()

    }

    class StatusBondedReceiver(val connectedInfo: TextView): BroadcastReceiver() {
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

    class ScanModeReceiver(val discoverableButton: Button, val resources: Resources): BroadcastReceiver()
    {
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
                        Log.d(TAG_, "problem with discoverability")
                    }
                }
        }
    }

    companion object {

        val TAG_: String
            get() = "BluetoothConnectionManager: "
    }

    class SendReceive(private val bluetoothSocket: BluetoothSocket) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?

        override fun run()
        {
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true)
            {
                try {
                    if (inputStream != null)
                    {
                        bytes = inputStream.read(buffer)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun write(bytes: ByteArray?)
        {
            try {
                outputStream?.write(bytes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        init {
            var tempIn: InputStream? = null
            var tempOut: OutputStream? = null
            try {
                tempIn = bluetoothSocket.inputStream
                tempOut = bluetoothSocket.outputStream
            } catch (e: Exception) {
                e.printStackTrace()
            }
            inputStream = tempIn
            outputStream = tempOut
        }
    }
}