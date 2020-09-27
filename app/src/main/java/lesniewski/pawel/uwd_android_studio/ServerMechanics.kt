package lesniewski.pawel.uwd_android_studio

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_server_mechanics.*
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.models.CardModel
import lesniewski.pawel.uwd_android_studio.tools.Constants.AMOUNT_OF_ANSWERS_ON_START
import lesniewski.pawel.uwd_android_studio.tools.Constants.END_GAME
import lesniewski.pawel.uwd_android_studio.tools.Constants.GAME_UUID
import lesniewski.pawel.uwd_android_studio.tools.Constants.NOT_STARTED

import lesniewski.pawel.uwd_android_studio.tools.Constants.SERVER_CHOOSING_ANSWER
import lesniewski.pawel.uwd_android_studio.tools.Constants.SERVER_SEND_QUESTION_AND_FIRST_ANSWERS
import lesniewski.pawel.uwd_android_studio.tools.Constants.STATE_MESSAGE_RECEIVED
import java.io.Serializable
import kotlin.properties.Delegates

class ServerMechanics() : AppCompatActivity(), Serializable, IBluetoothConnectionManager {

    var serverSocket : BluetoothServerSocket? = null
    private var TAG = "Server Mechanics -------- "
    lateinit var ROOM_NAME: String
    lateinit var bAdapter: BluetoothAdapter
    lateinit var devices: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>
    lateinit var gameInfoBar: TextView
    lateinit var readyBtn: Button
    lateinit var clientsModel : ArrayList<ClientListenerModel>
    lateinit var questionPool: TextView
    lateinit var cardAdapter: CardModelAdapter

    var cardModels = ArrayList<CardModel>()
    var amount: Int = 0
    var answers = arrayListOf<String>()
    var currentQuestion = ""
    var DBquestions = arrayListOf("Co zabiło Michela Jacksona?", "Gdzie ma Putin Demokrację?", "Co nie zmieści się w ustach Sashy Gray?")
    var DBanswers = arrayListOf("Popek Monster", "Heisenberg", "Nic", "Ojciec Mateusz", "Szczątki Tupolewa", "Srający kot", "Ale ale aleksandra", "Zdjęcie Tatiany Okupnik", "Jądro ziemi ale też moje", "Ozzy Osborn","Odpowiedź 1","Odpowiedź 2","Odpowiedź 3","Odpowiedź 4","Odpowiedź 5","Odpowiedź 6","Odpowiedź 7","Odpowiedź 8","Odpowiedź 9","Odpowiedź 10","Odpowiedź 11" )

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
        getServerSocketForGame()
        setListAdapter()
        waitingForPlayers()
        implementListeners()

    }

    private fun getServerSocketForGame() {
        this.serverSocket = getServerSocket(ROOM_NAME, GAME_UUID)
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
                    startGame()
                    randQuestion()
                    sendQuestionAndAnswers(AMOUNT_OF_ANSWERS_ON_START)
                    setButton(resources.getString(R.string.waitForAnswers))
                    turnOffButton()
                    gameState = SERVER_SEND_QUESTION_AND_FIRST_ANSWERS
                }
            }
        }
    }

    private fun setButton(str: String)
    {
        readyBtn.text = str
    }
    private fun turnOffButton()
    {
        readyBtn.isClickable = false
        readyBtn.isActivated = false
    }

    private fun startGame() {

        Thread(Runnable {

            while(gameState!= END_GAME)
            {
                when(gameState){
                    SERVER_SEND_QUESTION_AND_FIRST_ANSWERS ->
                    {
                        if(allClientsGetAnswer())
                        {
                            getClientsAnswers()
                            clearClientsAnswers()
                            showAnswers()
                            setButton(resources.getString(R.string.chooseAnswer))
                            gameState = SERVER_CHOOSING_ANSWER
                        }
                    }
                }
            }

        }).start()
    }

    private fun showAnswers()
    {
        for(ans in answers)
        {
            cardModels.add(CardModel(ans))
        }
        cardAdapter = CardModelAdapter(cardModels, this)
        viewPager.adapter = cardAdapter
        viewPager.setPadding(220,0,220,0)
    }

    private fun clearClientsAnswers() {

        for(client in clientsModel)
        {
            client.answer = ""
        }
    }

    private fun getClientsAnswers(){

        for(client in clientsModel)
        {
            answers.add(client.answer)
        }
    }

    private fun allClientsGetAnswer() : Boolean
    {
        var readyAnswersCounter = 0
        for(client in clientsModel)
        {
            if(client.answer != "")
            {
                readyAnswersCounter++
            }
        }

        return amount.toInt() == readyAnswersCounter
    }

    private fun sendQuestionAndAnswers(howManyTimes: Int) {
        var cnt = howManyTimes
        var message = ""
        for(client in clientsModel)
        {
            message = "FAAQ," + currentQuestion
            while (cnt > 0)
            {
                val answerNumber = (0..DBanswers.size).random()
                message += "," + DBanswers[answerNumber]
                DBanswers.removeAt(answerNumber)
                cnt--
            }
            message+=","
            client.writeToClient(message)

        }

    }

    private fun randQuestion() {

        val questionNumber = (0..DBquestions.size-1).random()
        currentQuestion = DBquestions[questionNumber]
        DBquestions.removeAt(questionNumber)
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
        Toast.makeText(this, "waiting", Toast.LENGTH_LONG).show()

        Thread(Runnable{


            var limit = amount.toInt()
            var cnt = 0

            while(limit > 0)
            {
                try
                {
                    runOnUiThread(Runnable{
                        Toast.makeText(this, "Już można potwierdzać obecność!", Toast.LENGTH_LONG).show()
                    })
                    val tmpSocket: BluetoothSocket = this.serverSocket!!.accept()

                    clientsModel.add(ClientListenerModel(tmpSocket))
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

            startListeningPlayers()

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
        amount = int.getStringExtra("amount")!!.toInt()
        ROOM_NAME = int.getStringExtra("roomName")!!
    }
}