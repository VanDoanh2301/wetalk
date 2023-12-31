package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.databinding.TalkItemTestBinding

class PracticeFragment : Fragment() {
    private var _binding: TalkItemTestBinding? =null
    private val binding get() = _binding!!
    private var choose1:Boolean =false
    private var choose2:Boolean = false
    private  var choose3:Boolean = false
    private var choose4:Boolean = false
    private var question_index = 0
    private var isFinish = false
    private var total_question = 0
    private var isActive = false


    companion object {
        fun init(
            questionNew: PracticeQuest,
            question_index: Int
        ): PracticeFragment {
            val testQuestFragment: PracticeFragment =
                PracticeFragment()
            val mBundle = Bundle()
            mBundle.putParcelable("questionType", questionNew)
            mBundle.putInt("question_index", question_index)
            testQuestFragment.arguments = mBundle
            return testQuestFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TalkItemTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        questionType = (requireArguments().getParcelable<Parcelable>("questionType") as PracticeQuest?)!!
        question_index = requireArguments().getInt("question_index")
        total_question = requireArguments().getInt("total_question")

        onResult();
    }

    private fun onResult() {

    }
}