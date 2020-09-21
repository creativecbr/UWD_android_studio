package lesniewski.pawel.uwd_android_studio.fragmentsService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_show_question.*
import lesniewski.pawel.uwd_android_studio.R


class ShowQuestion : Fragment() {

    lateinit var questionTextPool: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_show_question, container, false)

        getQuestionPool()
        if(arguments?.getString("que") != null)
        {
           questionTextPool.text = arguments?.getString("que")
        }
        return v
    }

    private fun getQuestionPool() {
        questionTextPool = view!!.findViewById<View>(R.id.questionTv) as TextView
    }
}

