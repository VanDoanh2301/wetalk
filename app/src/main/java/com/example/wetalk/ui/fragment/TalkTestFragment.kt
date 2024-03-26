package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.wetalk.R
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.data.local.QuestionType
import com.example.wetalk.data.local.TestTopic
import com.example.wetalk.data.local.TestQuest
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.databinding.FragmentTalkTestBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.MenuPracticeAdapter
import com.example.wetalk.ui.adapter.ViewPagerPracticeAdapter
import com.example.wetalk.ui.viewmodels.TestTopicViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTestFragment : Fragment() {
    private var _binding: FragmentTalkTestBinding? = null
    private val binding get() = _binding!!
    private var currentIndex = 0
    private var practiceQuests: ArrayList<PracticeQuest>? = null
    private var id = 0
    private lateinit var test: TestTopic
    private var questions: ArrayList<Question>? = null
    private var testQuests: ArrayList<TestQuest> = ArrayList()
    private val viewModel: TestTopicViewModel by viewModels()
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
        val questionSize = QuestionSize(1, 10, id)
        binding.mainContent.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.getAllQuestionPageByTopicId(questionSize)
            viewModel.questions.collect {
                when (it) {
                    is Resource.Success -> {
                        val questions = it.data!!.data
                        if (questions != null) {
                            for (q in questions) {
                                val answerA = if (q.answers.size > 0) q.answers[0].content else null
                                val answerB = if (q.answers.size > 1) q.answers[1].content else null
                                val answerC = if (q.answers.size > 2 && q.answers[2] != null) q.answers[2].content else null
                                val answerD = if (q.answers.size > 3 && q.answers[3] != null) q.answers[3].content else null

                                val questionType = QuestionType(
                                    question = q.content,
                                    answer_a = answerA,
                                    answer_b = answerB,
                                    answer_c = answerC,
                                    answer_d = answerD,
                                    answer_correct = q.answers.find { it.correct }?.content ?: "",
                                    explain = q.explanation ?: "",
                                    image = q.imageLocation ?: "",
                                    video = q.videoLocation ?: ""
                                )
                                Log.d("Quest", questionType.toString())
                                val testQuest = TestQuest(
                                    question = questionType,
                                    answer = ""
                                )
                                testQuests.add(testQuest)
                                test = TestTopic(
                                    total = testQuests.size,
                                    correct = 0,
                                    check = 0,
                                    completed = 0
                                )
                                initPagerHome();

                            }
                        }

                        else {
                            val questionType = QuestionType(
                                question = "Đây là chữ gì ?",
                                answer_a = "A",
                                answer_b = "C",
                                answer_c = "D",
                                answer_d = "F",
                                answer_correct = "A",
                                explain = "",
                                image = "",
                                video = "https://firebasestorage.googleapis.com/v0/b/wetalk-12b8f.appspot.com/o/videos%2FA.mp4?alt=media&token=c977e65b-36ca-4cf1-95c7-ae33ee315fcd"
                            )
                            val testQuest = TestQuest(
                                question = questionType,
                                answer = ""
                            )

                            val question_1 = QuestionType(
                                question = "Đây là chữ gì ?",
                                answer_a = "O",
                                answer_b = "A",
                                answer_c = "Dấu huyền",
                                answer_d = "Dấu chấm",
                                answer_correct = "A",
                                explain = "",
                                image = "https://png.pngtree.com/thumb_back/fw800/background/20220916/pngtree-the-use-of-sign-language-by-individuals-who-are-deaf-and-mute-the-letter-a-in-english-photo-image_48619804.jpg",
                                video = ""
                            )
                            val test_1 = TestQuest(
                                question = question_1,
                                answer = ""
                            )
                            testQuests.add(test_1)
                            testQuests.add(testQuest)
                            test = TestTopic(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).supportFragmentManager.setFragmentResultListener(
            "requestKeyFromChild",
            this
        )
        { requestKey, bundle ->
            val result = bundle.getInt("index")
            val question = bundle.getParcelable<TestQuest>("question")!!
            updateTestCurrentIndex(question, result)

        }

    }

    private fun initPagerHome() {
        binding.mainContent.rightTitle.setOnClickListener {
            finishTest()
        }
        viewPager = binding.mainContent.viewpager
        val adapter = ViewPagerPracticeAdapter(
            (activity as MainActivity).supportFragmentManager,
            0,
            testQuests!!
        )
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
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true)
                }, 0)
            }
        })
        binding.mainContent.iconPrevious.setOnClickListener(View.OnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                binding.mainContent.viewpager.postDelayed(Runnable {
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true)
                }, 0)
            }
        })
        binding.mainContent.bottomTitle.text = "Câu hỏi " + 1 + " / " + testQuests.size
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            if (currentIndex != position) {

            }
            currentIndex = position
            menuTestHardAdapter.setIndexSelection(position)
            binding.mainContent.bottomTitle.text =
                "Câu hỏi " + (position + 1) + " / " + testQuests!!.size
            menuTestHardAdapter.setCurrent_selected(position)
            if (currentIndex == 0) {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#ffffff"))
            }
            if (currentIndex == testQuests!!.size - 1) {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#ffffff"))
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun updateTestCurrentIndex(testQuest: TestQuest, index: Int) {
        testQuests[index] = testQuest

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
    }



    private fun finishTest() {
       val bundle = bundleOf(
           "test" to test,
           "testQuest" to testQuests
       )
        findNavController().navigate(R.id.action_talkTestFragment_to_testResultFragment, bundle)


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}