package lesniewski.pawel.uwd_android_studio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import java.io.Serializable

class ServerMechanics() : AppCompatActivity(), Serializable {

    lateinit var devices: ArrayList<String>
    private lateinit var amount: String
    lateinit var pairedList: ListView
    lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_mechanics)
        getDevices()
        setListAdapter()

    }

    private fun setListAdapter() {
        pairedList = findViewById<ListView>(R.id.approvingPlayersList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices)
        pairedList.adapter = adapter
    }

    private fun getDevices() {

        val int: Intent = intent
        amount = int.getStringExtra("amount")!!
        devices = int.getSerializableExtra("devices") as ArrayList<String>
    }
}