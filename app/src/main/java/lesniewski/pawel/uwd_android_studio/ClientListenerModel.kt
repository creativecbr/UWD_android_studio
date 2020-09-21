package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.InputStream
import java.io.OutputStream

class ClientListenerModel(
    var socket: BluetoothSocket,
    handler: Handler
): Thread() {

    private var TAG = "Client Listener Model"
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    var buffer = ByteArray(1024)
    var bytes = 0


    init{
        try{
            outputStream = socket.outputStream
            inputStream = socket.inputStream
        }
        catch(e: Exception){
            Log.d(TAG, "error while getting input/output stream")
        }
    }

    @ExperimentalStdlibApi
    override fun run()
    {
        while(true)
        {
            try{
                bytes = inputStream!!.read(buffer)
                var tmp = buffer.decodeToString()

           } catch(e: Exception)
            {
                Log.d(TAG, "Error while getting bytes from inputStream")
            }
        }
    }

    fun writeToClient(message: String)
    {

        val bytes = message.toByteArray()
        try {
            outputStream!!.write(bytes)
        } catch (e: Exception) {
            Log.d(TAG, "error while sending byte to client")
        }
    }

}