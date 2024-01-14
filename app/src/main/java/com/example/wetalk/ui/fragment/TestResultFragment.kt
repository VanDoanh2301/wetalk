package com.example.wetalk.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.data.local.Test
import com.example.wetalk.databinding.FragmentTestResultBinding


class TestResultFragment : Fragment() {
    private var _binding: FragmentTestResultBinding? = null
    public val binding get() = _binding!!
    private lateinit var test: Test
    private var isSuccess = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgHome.setOnClickListener {
            findNavController().navigate(R.id.action_testResultFragment_to_talkTopicFragment)
            findNavController().popBackStack(R.id.talkTopicFragment, false)
        }
        initValue()
        initView()

    }
    private fun initValue() {
        test = arguments?.getParcelable<Test>("test")!!
        isSuccess = test.correct >= ((test.total / 2) + 1)

    }

    private fun initView() {
        val progressValue: Int = test.correct * 100 / test.total
        binding.tvPercent.text = "$progressValue%"
        binding.progress.progress = progressValue
        binding.tvCorrect.text = test.correct.toString()
        binding.tvIncorrect.setText((test.total - test.correct).toString() + "")
        binding.tvTotalQuestion.text = test.total.toString()
        if (isSuccess) {
            binding.tvStatus.text = "Chúc mừng"
            binding.tvDetailStatus.text = "Bạn đã hoàn thành $progressValue%"
        } else {
            binding.tvStatus.text = "Thất Bại"
            binding.tvDetailStatus.text =  "Bạn đã hoàn thành $progressValue%"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestResultFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}