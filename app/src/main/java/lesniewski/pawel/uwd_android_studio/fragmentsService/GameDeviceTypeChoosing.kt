package lesniewski.pawel.uwd_android_studio.fragmentsService


import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import lesniewski.pawel.uwd_android_studio.*


class GameDeviceTypeChoosing : Fragment(),
    IFragmentChanger {

    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val DISCOVERABLE_DURATION: Int = 0
    private val REQUEST_CODE_ENABLE_DISCOVERABILTY: Int = 2
    lateinit var bAdapter:BluetoothAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val v = inflater.inflate(R.layout.fragment_game_device_type_choosing, container, false)

        val btn = v.findViewById<View>(R.id.backButton) as Button
        btn.setOnClickListener{
            ChangeFragment(activity!!.supportFragmentManager,
                R.id.fragmentMenu,
                GameMainMenu()
            )
        }

        val create_layer = v.findViewById(R.id.createRoomLayout) as LinearLayout
        create_layer.setOnClickListener{
            val intent = Intent(activity, CreateRoom::class.java)
            startActivity(intent)
        }


        val join_layer = v.findViewById(R.id.joinRoomLayout) as LinearLayout
        join_layer.setOnClickListener{

            checkIsBluetoothOnAndGoToClientActivity()
        }

        return v
    }

    private fun checkIsBluetoothOnAndGoToClientActivity()
    {
        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if(!bAdapter.isEnabled)
        {
            val blInt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(blInt, REQUEST_CODE_ENABLE_BT)
        }
        else
        {
            val intent = Intent(activity, JoinToRoom::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?) {

        when(requestCode)
        {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode != AppCompatActivity.RESULT_OK)
                {
                    Toast.makeText(this.context, "Najpierw musisz zezwolić na uruchomienie bluetooth!", Toast.LENGTH_LONG).show()
                }
                else
                {
                    val intent = Intent(activity, JoinToRoom::class.java)
                    startActivity(intent)
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}