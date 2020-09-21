package lesniewski.pawel.uwd_android_studio.tools

import java.util.*

object Constants {

    val APP_UUID = UUID.fromString("3b4c7719-3738-4234-94a3-22d72dbb8a74")


    const val PLAYER_LIMIT = 1
    const val REQUEST_CODE_ENABLE_BT: Int = 1
    const val REQUEST_CODE_ENABLE_DISCOVERABILTY: Int = 2
    const val DISCOVERABLE_DURATION = 120 // in seconds
    const val AMOUNT_OF_ANSWERS_ON_START = 3

    const val STATE_LISTENING = 1
    const val STATE_CONNECTING = 2
    const val STATE_CONNECTED = 3
    const val STATE_CONNECTION_FAILED = 4
    const val STATE_MESSAGE_RECEIVED = 5

    const val NOT_STARTED = 0

    const val SERVER_SEND_QUESTION_AND_FIRST_ANSWERS = 2
    const val SERVER_CHOOSING_ANSWER = 3
    const val CLIENT_WAITING_FOR_QUESTION_AND_ANSWERS = 4
    const val CLIENT_FIRST_ANSWERS_AND_QUESTION_RECEIVED = 5
    const val CLIENT_CHOOSING_NOW = 6
    const val END_GAME = 1000


    //TODO LIST
    //back button should be stick to bottom line in every activities
    //when client get cards, start timer, server dont control this
}