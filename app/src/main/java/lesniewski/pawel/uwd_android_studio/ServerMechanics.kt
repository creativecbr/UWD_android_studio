package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_mechanics.*
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.tools.Constants.AMOUNT_OF_ANSWERS_ON_START
import lesniewski.pawel.uwd_android_studio.tools.Constants.NOT_STARTED
import lesniewski.pawel.uwd_android_studio.tools.Constants.STATE_MESSAGE_RECEIVED
import java.io.Serializable
import kotlin.properties.Delegates

class ServerMechanics() : AppCompatActivity(), Serializable, IBluetoothConnectionManager {

    var serverSocket : BluetoothServerSocket? = null
    private var TAG = "Server Mechanics -------- "
    lateinit var ROOM_NAME: String
    lateinit var bAdapter: BluetoothAdapter
    lateinit var devices: ArrayList<String>
    lateinit var amount: String
    lateinit var adapter: ArrayAdapter<String>
    lateinit var gameInfoBar: TextView
    lateinit var readyBtn: Button
    lateinit var clientsModel : ArrayList<ClientListenerModel>
    lateinit var questionPool: TextView

    var questions = arrayListOf("Co zabiło Michela Jacksona?", "Gdzie ma Putin Demokrację?", "Co nie zmieści się w ustach Sashy Gray?")
    var answers = arrayListOf("Popek Monster", "Heisenberg", "Nic", "Ojciec Mateusz", "Szczątki Tupolewa", "Srający kot", "Ale ale aleksandra", "Zdjęcie Tatiany Okupnik", "Jądro ziemi ale też moje", "Ozzy Osborn","Odpowiedź 1","Odpowiedź 2","Odpowiedź 3","Odpowiedź 4","Odpowiedź 5","Odpowiedź 6","Odpowiedź 7","Odpowiedź 8","Odpowiedź 9","Odpowiedź 10","Odpowiedź 11" )

    var gameState = NOT_STARTED
    //TODO
    //Class ClientListenerModel
    //init with socket, every clientmodel has write to write to him, and listening on other thread
    //based on SendReceive


    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_mechanics)
        getDevicesAndViews()
        setListAdapter()
        waitingForPlayers()
        //startListeningPlayers()
        implementListeners()

    }

    @ExperimentalStdlibApi
    private fun startListeningPlayers() {
       for( client in clientsModel)
            {
                client.run()
            }
    }

    private fun implementListeners() {
        readyBtn.setOnClickListener{
            when(gameState){
                NOT_STARTED ->
                {
                    sendQuestion()
                    sendAnswers(AMOUNT_OF_ANSWERS_ON_START)
                }
            }
        }
    }

    private fun sendAnswers(howManyTimes: Int) {
        var cnt = howManyTimes
        var message = "ANSWERS:"
        for(client in clientsModel) {
            while (cnt > 0) {
                val answerNumber = (0..answers.size).random()
                message += answers[answerNumber] + ":"
                answers.removeAt(answerNumber)
                cnt--
            }
            client.writeToClient(message)

        }

    }

    private fun sendQuestion() {

        val questionNumber = (0..questions.size).random()

        for(client in clientsModel)
        {
            client.writeToClient(questions[questionNumber])

        }
        questions.removeAt(questionNumber)
    }

    var handler: Handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            STATE_MESSAGE_RECEIVED -> {
                val readBuff = msg.obj as ByteArray
                val tempMsg = String(msg.obj as ByteArray, 0, msg.arg1)
                //received data, storage in tempMsg
            }
        }
        true
    })

    @ExperimentalStdlibApi
    @SuppressLint("SetTextI18n")
    private fun waitingForPlayers()    {
        gameInfoBar.text = resources.getString(R.string.gameStatus) + resources.getString(R.string.waitingForPlayers)
        questionPool.text = "Proszę czekać.."
        this.serverSocket = getServerSocket(ROOM_NAME)

        Thread(Runnable{

            var limit = amount.toInt()
            var cnt = 0

            while(limit > 0)
            {
                try
                {
                    val tmpSocket: BluetoothSocket = this.serverSocket!!.accept()

                    clientsModel.add(ClientListenerModel(tmpSocket, handler))
                    cnt++
                    limit--

                    runOnUiThread(Runnable{
                        this@ServerMechanics.gameInfoBar.text = resources.getString(R.string.gameStatus) + " " + resources.getString(R.string.connected) + " " + cnt.toString() + "/" + amount

                    })

                }
                catch (e: java.lang.Exception)
                {
                    Log.d(TAG, "Cant accept any connection")
                    break
                }
            }

            runOnUiThread(Runnable{
                this@ServerMechanics.readyBtn.isEnabled = true
                this@ServerMechanics.readyBtn.text = resources.getString(R.string.startGame)
            })
        }).start()

    }


    private fun setListAdapter() {

        bAdapter = BluetoothAdapter.getDefaultAdapter()

    }

    private fun getDevicesAndViews() {

        gameInfoBar = findViewById<TextView>(R.id.gameInfoBar)
        readyBtn = findViewById<Button>(R.id.readyBtn)
        questionPool = findViewById<TextView>(R.id.questionPool)
        clientsModel = arrayListOf<ClientListenerModel>()

        val int: Intent = intent
        amount = int.getStringExtra("amount")!!
        ROOM_NAME = int.getStringExtra("roomName")!!
    }
}