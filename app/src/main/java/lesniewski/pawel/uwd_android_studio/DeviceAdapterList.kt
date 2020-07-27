package lesniewski.pawel.uwd_android_studio


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

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
        var cv: View? = mLayoutInflater.inflate(mViewResourceId, null)

        val device = devices[position]
        val deviceName = cv?.findViewById<View>(R.id.tvDeviceName) as TextView
        val deviceAdress = cv.findViewById<View>(R.id.tvDeviceAddress) as TextView

        deviceName.text = device.name
        deviceAdress.text = device.address

        return cv
    }

}