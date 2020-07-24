package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import java.util.*

class DeviceListAdapter(
    context: Context,
    tvResourceId: Int,
    private val mDevices: ArrayList<BluetoothDevice>
) :
    ArrayAdapter<BluetoothDevice?>(context, tvResourceId, mDevices) {
    private val mLayoutInflater: LayoutInflater
    private val mViewResourceId: Int

    init {
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mViewResourceId = tvResourceId
    }
}

// do przeubodwy n.p. tutoriala Discover Devices
//https://www.youtube.com/watch?v=hv_-tX1VwXE&list=PLgCYzUzKIBE8KHMzpp6JITZ2JxTgWqDH2&index=3