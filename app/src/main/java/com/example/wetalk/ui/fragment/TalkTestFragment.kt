package com.example.wetalk.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.wetalk.R
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.data.local.QuestionType
import com.example.wetalk.data.local.Test
import com.example.wetalk.data.local.TestQuest
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.databinding.FragmentTalkTestBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.MenuPracticeAdapter
import com.example.wetalk.ui.adapter.ViewPagerPracticeAdapter
import com.example.wetalk.ui.viewmodels.TalkTestViewModel
import com.example.wetalk.util.DialogClose
import com.example.wetalk.util.OnUpdateListener
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTestFragment : Fragment(), OnUpdateListener<TestQuest> {
    private var _binding: FragmentTalkTestBinding? =null
    private val binding get() = _binding!!
    private var currentIndex = 0
    private var practiceQuests: ArrayList<PracticeQuest>? = null
    private var id = 0
    private lateinit var test:Test
    private var questions:ArrayList<Question> ? =null
    private var testQuests: ArrayList<TestQuest> = ArrayList()
    private val viewModel : TalkTestViewModel by viewModels()
    private var viewPager: ViewPager? = null
    private lateinit var menuTestHardAdapter: MenuPracticeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTestBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getInt("id", -1)!!

        lifecycleScope.launchWhenStarted {
            viewModel.getAllQuestionByTopicId(id)
            viewModel.questions.collect {
                when(it) {
                    is Resource.Success -> {
                       val questions = it.data!!.data
                        for (q in questions) {
                            val questionType = QuestionType(
                                question = q.content,
                                answer_a = q.answers[0].content,
                                answer_b = q.answers[1].content,
                                answer_c = q.answers[2].content,
                                answer_d = q.answers[3].content,
                                answer_correct = q.answers.find { it.correct }?.content ?: "",
                                explain = q.explanation,
                                image = q.imageLocation,
                                video = q.videoLocation
                            )
                            Log.d("Quest", questionType.toString())
                            val testQuest = TestQuest(
                                question = questionType,
                                answer = ""
                            )
                            testQuests.add(testQuest)
                            test = Test(
                                total = testQuests.size,
                                correct = 0,
                                check = 0,
                                completed = 0
                            )
                            initPagerHome();
                        }
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }


    private fun initPagerHome() {
        viewPager = binding.mainContent.viewpager
        val adapter = ViewPagerPracticeAdapter(
            (activity as MainActivity).supportFragmentManager,
            0,
            testQuests!!)
        binding.mainContent.viewpager.addOnPageChangeListener(viewPagerPageChangeListener)
        binding.mainContent.viewpager.adapter = adapter
        menuTestHardAdapter = MenuPracticeAdapter(requireContext(), testQuests!!)
        menuTestHardAdapter.setClickListener(object : MenuPracticeAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                binding.mainContent.viewpager.postDelayed(Runnable {
                    currentIndex = position
                    binding.mainContent.viewpager.setCurrentItem(position, false)
                }, 0)
            }
        })
        binding.mainContent.iconNext.setOnClickListener(View.OnClickListener {
            if (currentIndex < testQuests!!.size - 1) {
                currentIndex++
                binding.mainContent.viewpager.postDelayed(Runnable {
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true) }, 0)
            }
        })
        binding.mainContent.iconPrevious.setOnClickListener(View.OnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                binding.mainContent.viewpager.postDelayed(Runnable {
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true) }, 0)
            }
        })
        binding.mainContent.bottomTitle.text = "Question " + 1 + " / " + testQuests.size
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            if (currentIndex != position) {
//                showSpotLight()
            }
            currentIndex = position
            menuTestHardAdapter.setIndexSelection(position)
            binding.mainContent.bottomTitle.text = "QuestionType " + (position + 1) + " / " + practiceQuests!!.size
            menuTestHardAdapter.setCurrent_selected(position)
            if (currentIndex == 0) {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#ffffff"))
            }
            if (currentIndex == practiceQuests!!.size - 1) {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#ffffff"))
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    fun updateTestCurrentIndex(questionDetail: TestQuest?, index: Int) {
        (activity as MainActivity).runOnUiThread { if (menuTestHardAdapter != null) menuTestHardAdapter.notifyItemChanged(index) }

        var countCorrect = 0
        var countChecked = 0
        for (i in testQuests.indices) {
            val detail = testQuests[i]
            if (detail.answer != null && !detail.answer.equals("")) {
                countChecked += 1
            }
            if (detail.answer != null && !detail.answer.equals("")) {
                if (detail.answer.equals(detail.question.answer_correct)) {
                    countCorrect = countCorrect + 1
                }
            }
        }
        test.check = countChecked
        test.correct = countCorrect

        if (test.completed == 0) {
            if (countChecked == testQuests.size) {
                test.completed = 1
                binding.mainContent.rightTitle.text = "Kết Quả"
                DialogClose.Builder(requireContext())
                    .title("Kết quả")
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .content("Kết thúc kiểm tra")
                    .doneText("View Result")
                    .onDone { finishTest() }
                    .show()
            } else if (index == testQuests.size - 1) {
                binding.mainContent.rightTitle.text = "Kết Thúc"
                if (countChecked < testQuests.size) {
                    DialogClose.Builder(requireContext())
                        .title("Kết quả")
                        .content("Kết thúc kiểm tra")
                        .doneText("Đi đến phần review")
                        .onDone {
                            for (i in testQuests.indices) {
                                val detail = testQuests.get(i)
                                if (detail.answer == null || detail.answer.equals("")
                                ) {
                                    val currentIndexTemp: Int = i
                                    viewPager!!.post {
                                        currentIndex = currentIndexTemp
                                        binding.mainContent.bottomTitle.text =
                                            "Question " + (currentIndex + 1) + " / " + testQuests.size
                                        viewPager!!.setCurrentItem(currentIndex, false)
                                    }
                                    break
                                }
                            }
                        }
                        .show()
                }
            } else if ((index + 1) % 10 == 0 && index > 0) {
                DialogClose.Builder(requireContext())
                    .title("Review question")
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .content("Would you like to review the overview of the questions you have done from questions " + (index - 8) + " to " + (index + 1) + "?")
                    .doneText("Review the questions (" + (index - 8) + " to " + (index + 1) + ")")
                    .onDone {
                    }
                    .show()
            } else {
                binding.mainContent.rightTitle.text = "Kết Thúc"
            }
        }
    }
    private fun finishTest() {

    }
    override fun updateData(result: TestQuest, index: Int) {
        Log.d("Dataaa",result.toString())
    }
}