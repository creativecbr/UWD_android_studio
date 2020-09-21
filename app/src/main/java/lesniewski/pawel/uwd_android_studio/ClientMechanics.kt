package lesniewski.pawel.uwd_android_studio

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import lesniewski.pawel.uwd_android_studio.fragmentsService.ShowQuestion
import lesniewski.pawel.uwd_android_studio.interfaces.IBluetoothConnectionManager
import lesniewski.pawel.uwd_android_studio.interfaces.IFragmentChanger
import lesniewski.pawel.uwd_android_studio.models.CardModel
import lesniewski.pawel.uwd_android_studio.tools.Constants.CLIENT_CHOOSING_NOW
import lesniewski.pawel.uwd_android_studio.tools.Constants.CLIENT_FIRST_ANSWERS_AND_QUESTION_RECEIVED

import lesniewski.pawel.uwd_android_studio.tools.Constants.CLIENT_WAITING_FOR_QUESTION_AND_ANSWERS
import lesniewski.pawel.uwd_android_studio.tools.Constants.END_GAME
import lesniewski.pawel.uwd_android_studio.tools.Constants.NOT_STARTED
import java.io.Serializable


class ClientMechanics() : AppCompatActivity(), IFragmentChanger,
    IBluetoothConnectionManager, Serializable {

    lateinit var readyBtn: Button
    lateinit var btSocket: BluetoothSocket

    private val lock = java.lang.Object()

    var gameState: Int = NOT_STARTED
    var btDevice: BluetoothDevice? = null
    var messageFromServer = ""
    lateinit var viewPager: ViewPager
    lateinit var cardAdapter: CardModelAdapter
    var cardModels = ArrayList<CardModel>()
    var currentQuestion = ""
    var currentAnswers = arrayListOf<String>()

    //TODO
    //synchronizacja na gameState na wszelki wpyadek, lock


    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_client_mechanics)
        findHandlers()


        readyBtn.setOnClickListener{

            when(gameState)
            {
                NOT_STARTED ->
                {
                    cntToServer()
                    startListeningFromServer()
                    startGame()
                    turnOffButton()
                    setButton(resources.getString(R.string.waitForQuestionsAndAnswers))
                    gameState = CLIENT_WAITING_FOR_QUESTION_AND_ANSWERS

                }
            }

            /*
            ChangeFragment(supportFragmentManager, R.id.fragmentClientMechanics, ShowQuestion())

            cardModels.add(CardModel("abecadło"))
            cardModels.add(CardModel("ciąża"))
            cardModels.add(CardModel("jazda z ziomem"))
            cardModels.add(CardModel("jerycho"))

            cardAdapter = CardModelAdapter(cardModels, this)


            viewPager.adapter = cardAdapter
            //var pagerStack = ViewPagerStack()
            //viewPager.setPageTransformer(true, pagerStack)
            viewPager.setPadding(220,0,220,0)
            */
        }

    }

    private fun startGame()
    {
        Thread(Runnable{

            while (gameState != END_GAME)
                when(gameState){
                    CLIENT_FIRST_ANSWERS_AND_QUESTION_RECEIVED ->
                    {
                        decodeAnswersAndQuestion()
                        showQuestion()
                        showAnswers()
                        gameState = CLIENT_CHOOSING_NOW

                        setButton(resources.getString(R.string.chooseAnswer))
                        turnOnButton()
                    }
                }
        }).start()
    }

    private fun showAnswers() {

        for(ans in currentAnswers)
        {
            cardModels.add(CardModel(ans))
            cardAdapter = CardModelAdapter(cardModels, this)
            viewPager.adapter = cardAdapter
            viewPager.setPadding(220,0,220,0)
        }
    }

    private fun showQuestion() {

        var bun = Bundle()
        bun.putString("que",currentQuestion)

        val questionFragment = ShowQuestion()
        questionFragment.arguments = bun

        ChangeFragment( supportFragmentManager, R.id.fragmentClientMechanics, questionFragment)

    }

    private fun decodeAnswersAndQuestion()
    {
        val messageArray = messageFromServer.split(",").toTypedArray()
        currentQuestion = messageArray[1]

        for(i in 2..messageArray.size-2)
        {
            currentAnswers.add(messageArray[i])
        }

    }


    @ExperimentalStdlibApi
    private fun startListeningFromServer() {
        var inputStream = btSocket.inputStream

        Thread(Runnable {

            val buffer = ByteArray(1024)
            var bytes: Int
            while (true)
            {
                try {
                    if (inputStream != null)
                    {
                        bytes = inputStream.read(buffer)
                        if(bytes!=0)
                        {
                            messageFromServer = buffer.decodeToString()

                            if(messageFromServer.contains("FAAQ"))
                                gameState = CLIENT_FIRST_ANSWERS_AND_QUESTION_RECEIVED
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }).start()
    }
/*
    private class ViewPagerStack : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            if (position >= 0) {
                page.setScaleX(0.7f - 0.05f * position)
                page.setScaleY(0.7f)
                page.setTranslationX(-page.getWidth() * position + 35 * position)
                page.setTranslationY(35 * position)
            }
        }
    }
    */
    private fun setButton(str: String)
    {
        readyBtn.text = str
    }
    private fun turnOffButton()
    {
        readyBtn.isClickable = false
        readyBtn.isActivated = false
    }
    private fun turnOnButton()
    {
        readyBtn.isClickable = true
        readyBtn.isActivated = true
    }

    private fun cntToServer() {

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