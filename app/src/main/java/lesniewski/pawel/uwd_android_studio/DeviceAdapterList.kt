package lesniewski.pawel.uwd_android_studio


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class DeviceListAdapter(
    context: Context,
    tvResourceId: Int,
    private val devices: ArrayList<BluetoothDevice>
) :
    ArrayAdapter<BluetoothDevice?>(context, tvResourceId, devices as ArrayList<BluetoothDevice?>)
{
    private val mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mViewResourceId: Int = tvResourceId
    private val mDevices: ArrayList<BluetoothDevice> = devices

    @SuppressLint("ViewHolder")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val cv: View? = mLayoutInflater.inflate(mViewResourceId, null)

        val device = devices[position]
        val deviceName = cv?.findViewById<View>(R.id.tvDeviceName) as TextView
        val deviceAddress = cv.findViewById<View>(R.id.tvDeviceAddress) as TextView

        // cutting [UWD] from name and upper case
        deviceName.text = device.name.substring(5, device.name.length).toUpperCase(Locale.ROOT)
        deviceAddress.text = device.address

        return cv
    }

}