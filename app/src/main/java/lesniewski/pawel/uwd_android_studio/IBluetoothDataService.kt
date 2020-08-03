package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream

interface IBluetoothDataService {

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