package lesniewski.pawel.uwd_android_studio.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.IBinder


class BluetoothNameService : Service() {

    private lateinit var originalName: String
    var bAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate() {
        super.onCreate()
        originalName = bAdapter.name

    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        println("onTaskRemoved called")
        super.onTaskRemoved(rootIntent)
        bAdapter.name = originalName
        this.stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}