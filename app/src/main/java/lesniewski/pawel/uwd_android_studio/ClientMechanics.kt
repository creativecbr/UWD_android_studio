package lesniewski.pawel.uwd_android_studio

import android.animation.ArgbEvaluator
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import lesniewski.pawel.uwd_android_studio.fragmentsService.ShowQuestion
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger
import lesniewski.pawel.uwd_android_studio.models.CardModel
import java.io.Serializable


class ClientMechanics() : AppCompatActivity(), IFragmentChanger,
    IBluetoothConnectionManager, Serializable {

    lateinit var readyBtn: Button
    lateinit var btSocket: BluetoothSocket
    var btDevice: BluetoothDevice? = null
    lateinit var viewPager: ViewPager
    lateinit var cardAdapter: CardModelAdapter
    var cardModels = ArrayList<CardModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_mechanics)
        findHandlers()
        connectToServer()

        readyBtn.setOnClickListener{
            ChangeFragment( supportFragmentManager, R.id.fragmentClientMechanics, ShowQuestion())

            cardModels.add(CardModel("abecadło"))
            cardModels.add(CardModel("ciąża"))
            cardModels.add(CardModel("jazda z ziomem"))
            cardModels.add(CardModel("jerycho"))

            cardAdapter = CardModelAdapter(cardModels, this)


            viewPager.adapter = cardAdapter
            viewPager.setPadding(220,0,220,0)

        }

    }

    private fun connectToServer() {
        if(btDevice != null)
        {
            btSocket = connectToServer(btDevice!!)!!
        }
    }


    private fun findHandlers() {
        readyBtn = findViewById<Button>(R.id.readyBtn)
        viewPager = findViewById(R.id.viewPager)

        val extras = intent.extras
        btDevice = if (extras != null) {
            extras.getParcelable<BluetoothDevice>("server")!!
        } else {
            null
        }

    }


}