package com.example.wetalk.ui.fragment

import android.animation.Animator
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.BaseViewAnimator
import com.daimajia.androidanimations.library.YoYo
import com.example.wetalk.R
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.data.local.TestQuest
import com.example.wetalk.databinding.TalkItemTestBinding
import com.example.wetalk.util.BounceInCustomAnimator
import com.example.wetalk.util.Utils
import com.rey.material.drawable.RippleDrawable
import com.example.wetalk.util.OnUpdateListener




class PracticeFragment : Fragment() {
    private var _binding: TalkItemTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var question: TestQuest
    private var choose1 = false
    private var choose2 = false
    private var choose3 = false
    private var choose4 = false
    private var question_index = 0
    private var isFinish = false
    private var total_question = 0
    private var isActive = false

    companion object {
        fun init(
            questionNew: TestQuest,
            question_index: Int,
            total_question: Int
        ): PracticeFragment {
            val testQuestFragment: PracticeFragment =
                PracticeFragment()
            val mBundle = Bundle()
            mBundle.putParcelable("question", questionNew)
            mBundle.putInt("question_index", question_index)
            mBundle.putInt("total_question", total_question)
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
        question = (requireArguments().getParcelable<Parcelable>("question") as TestQuest)!!
        question_index = requireArguments().getInt("question_index")
        total_question = requireArguments().getInt("total_question")


        initView()
        onResult();
    }

    private fun initView() {
        binding.apply {
            if(question.question.question.equals(""))  {
                textViewTitle.text = "Đây là kí hiệu gì ?"
            } else {
                textViewTitle.text = question.question.question
            }

            if (question.question.explain != null && question.question.explain.isNotEmpty()
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    txExplain.text = (Html.fromHtml(
                        question.question.explain,
                        Html.FROM_HTML_MODE_COMPACT,
                        null,
                        null
                    ).toString() + "").trim { it <= ' ' }
                } else {
                    txExplain.text = (Html.fromHtml(question.question.explain, null, null)
                        .toString() + "").trim { it <= ' ' }
                }
            }
            if (question.question.image != null && question.question.image.isNotEmpty()
            ) {
                imgQuestion.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(Uri.parse(question.question.image)
                    )
                    .into(imgQuestion)
            } else {
                imgQuestion.visibility = View.GONE
            }
            //check so luong dap an
            if (question.question.answer_d != null && question.question.answer_d.isNotEmpty()
            ) {
                textOption4.text = question.question.answer_d
                rltView4.visibility = View.VISIBLE
            } else {
                rltView4.visibility = View.GONE
            }
            if (question.question.answer_c != null && question.question.answer_c.isNotEmpty()
            ) {
                textOption3.text = question.question.answer_c
                rltView3.visibility = View.VISIBLE
            } else {
                rltView3.visibility = View.GONE
            }
            if (question.question.answer_b != null && question.question.answer_b.isNotEmpty()
            ) {
                textOption2.text = question.question.answer_b
                rltView2.visibility = View.VISIBLE
            } else {
                rltView2.visibility = View.GONE
            }
            if (question.question.answer_a != null && question.question.answer_a.isNotEmpty()
            ) {
                textOption1.text = question.question.answer_a
                rltView1.visibility = View.VISIBLE
            } else {
                rltView1.visibility = View.GONE
            }
            choose1 = question.answer == question.question.answer_a
            choose2 = question.answer == question.question.answer_b
            choose3 = question.answer == question.question.answer_c
            choose4 = question.answer == question.question.answer_d


            checkResult(false)
            disableClick()
            //an ket qua di
            rltView1.setOnClickListener(View.OnClickListener {
                if (question.answer != null && question.answer.isNotEmpty()) {
                    disableClick()
                    return@OnClickListener
                }
                if (textOption1.getCurrentTextColor() == requireContext().getColor(R.color.white)) {
                    choose1 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption1)
                    checkResult(true)
                } else {
                    choose1 = true
                    choose2 = false
                    choose3 = false
                    choose4 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption1)
                    checkResult(true)
                }
            })
            rltView2.setOnClickListener(View.OnClickListener {
                if (question.answer != null && question.answer.isNotEmpty()) {
                    disableClick()
                    return@OnClickListener
                }
                if (textOption2.getCurrentTextColor() == requireContext().getColor(R.color.white)) {
                    choose2 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption2)
                    checkResult(true)
                } else {
                    choose2 = true
                    choose1 = false
                    choose3 = false
                    choose4 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption2)
                    checkResult(true)
                }
            })
            rltView3.setOnClickListener(View.OnClickListener {
                if (question.answer != null && question.answer.isNotEmpty()) {
                    disableClick()
                    return@OnClickListener
                }
                if (textOption3.getCurrentTextColor() == requireContext().getColor(R.color.white)) {
                    choose3 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption3)
                    checkResult(true)
                } else {
                    choose3 = true
                    choose4 = false
                    choose1 = false
                    choose2 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption3)
                    checkResult(true)
                }
            })
            rltView4.setOnClickListener(View.OnClickListener {
                if (question.answer != null && question.answer.isNotEmpty()) {
                    disableClick()
                    return@OnClickListener
                }
                if (textOption4.getCurrentTextColor() == requireContext().getColor(R.color.white)) {
                    choose4 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption4)
                    checkResult(true)
                } else {
                    choose4 = true
                    choose2 = false
                    choose1 = false
                    choose3 = false
                    YoYo.with(BounceInCustomAnimator() as BaseViewAnimator)
                        .duration(250)
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {}
                            override fun onAnimationEnd(animator: Animator) {}
                            override fun onAnimationCancel(animator: Animator) {}
                            override fun onAnimationRepeat(animator: Animator) {}
                        })
                        .playOn(textOption4)
                    checkResult(true)
                }
            })

        }
    }

    fun checkResult(action: Boolean) {
        binding.apply {
            if (choose1 || choose2 || choose3 || choose4) {
                var questionAns = ""
                if (choose1) questionAns = question.question.answer_a
                if (choose2) questionAns = question.question.answer_b
                if (choose3) questionAns = question.question.answer_c
                if (choose4) questionAns = question.question.answer_d
                question.answer = questionAns
                disableClick()
            } else {
                question.answer = ""
            }
            if (isFinish == true) {
                disableClick()
            }

            textOption1.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption1.setBackgroundResource(R.drawable.border_textview)
            textOption1.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption1.setBackgroundResource(R.drawable.border_textview)

            textOption2.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption2.setBackgroundResource(R.drawable.border_textview)
            textOption2.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption2.setBackgroundResource(R.drawable.border_textview)

            textOption3.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption3.setBackgroundResource(R.drawable.border_textview)
            textOption3.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption3.setBackgroundResource(R.drawable.border_textview)

            textOption4.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption4.setBackgroundResource(R.drawable.border_textview)
            textOption4.setTextColor(
                Utils.getColorFromAttr(
                    requireContext(),
                    R.attr.color_text_item_setting
                )
            )
            textOption4.setBackgroundResource(R.drawable.border_textview)

            if (!isFinish) {
                if (action) {
                    disableClick()
                }
                if (question!!.answer != null && question.answer.isNotEmpty() && question.answer == question.question
                        .answer_correct
                ) {

                    if (question.answer == question.question.answer_a) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_b) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_c) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_d) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_done)
                    }

                    if (question.question.explain != null && question.question
                            .explain.isNotEmpty()
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txExplain.text = Html.fromHtml(
                                question.question.explain,
                                Html.FROM_HTML_MODE_COMPACT,
                                null,
                                null
                            )
                        } else {
                            txExplain.text = Html.fromHtml(
                                question.question.explain,
                                null,
                                null
                            )
                        }
                        lnExplain.visibility = View.VISIBLE
                    } else {
                        lnExplain.visibility = View.GONE
                    }
                } else if (question.answer != null && question.answer.isNotEmpty()
                ) {


                    if (question.answer == question.question.answer_a) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_b) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_c) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_d) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.question.answer_correct == question.question.answer_a

                    ) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_b

                    ) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_c

                    ) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_d

                    ) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.explain != null && question.question.explain.isNotEmpty()
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txExplain.text = Html.fromHtml(
                                question.question.explain,
                                Html.FROM_HTML_MODE_COMPACT,
                                null,
                                null
                            )
                        } else {
                            txExplain.text = Html.fromHtml(
                                question.question.explain,
                                null,
                                null
                            )
                        }
                        lnExplain.visibility = View.VISIBLE
                    } else {
                        lnExplain.visibility = View.GONE
                    }
                }
                if (action) {
                    val talkTestFragment = parentFragment as? TalkTestFragment
                    talkTestFragment?.updateTestCurrentIndex(question, question_index)
                }
            } else {
                if (question.question.explain != null && question.question
                        .explain.isNotEmpty()
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txExplain.setText(
                            Html.fromHtml(
                                question.question.explain,
                                Html.FROM_HTML_MODE_COMPACT,
                                null,
                                null
                            )
                        )
                    } else {
                        txExplain.setText(
                            Html.fromHtml(
                                question.question.explain,
                                null,
                                null
                            )
                        )
                    }
                    lnExplain.visibility = View.VISIBLE
                } else {
                    lnExplain.visibility = View.GONE
                }

                if (question.answer != null && !question.answer
                        .isEmpty() && question.answer == question.question.answer_correct
                ) {


                    if (question.answer == question.question.answer_a) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_b) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_c) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_d) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_done)
                    }
                } else if (question.answer != null && question.answer.isNotEmpty()
                ) {


                    if (question.question.answer_correct == question.question.answer_a

                    ) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_b

                    ) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_c

                    ) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_d

                    ) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.answer == question.question.answer_a) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_b) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_c) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                    if (question.answer == question.question.answer_d) {

                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_failed)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_failed)
                    }
                } else {
                    if (question.question.answer_correct == question.question.answer_a

                    ) {

                        textOption1.setTextColor(requireContext().getColor(R.color.white))
                        textOption1.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex1.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex1.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_b

                    ) {

                        textOption2.setTextColor(requireContext().getColor(R.color.white))
                        textOption2.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex2.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex2.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_c

                    ) {

                        textOption3.setTextColor(requireContext().getColor(R.color.white))
                        textOption3.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex3.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex3.setBackgroundResource(R.drawable.border_textview_done)
                    }
                    if (question.question.answer_correct == question.question.answer_d

                    ) {
                        textOption4.setTextColor(requireContext().getColor(R.color.white))
                        textOption4.setBackgroundResource(R.drawable.border_textview_done)
                        tnIndex4.setTextColor(requireContext().getColor(R.color.white))
                        tnIndex4.setBackgroundResource(R.drawable.border_textview_done)
                    }
                }
            }
        }
    }

    fun disableClick() {
        binding.apply {
            if (isFinish == false) {
                return
            } else {
                var rippleDrawable = RippleDrawable.Builder()
                    .cornerRadius(0)
                    .backgroundAnimDuration(300)
                    .rippleColor(requireActivity().getColor(R.color.transparent))
                    .backgroundColor(requireActivity().getColor(R.color.transparent))
                    .build()
                rltView1.setBackgroundDrawable(rippleDrawable)
                rippleDrawable = RippleDrawable.Builder()
                    .cornerRadius(0)
                    .backgroundAnimDuration(300)
                    .rippleColor(requireActivity().getColor(R.color.transparent))
                    .backgroundColor(requireActivity().getColor(R.color.transparent))
                    .build()
                rltView2.setBackgroundDrawable(rippleDrawable)
                rippleDrawable = RippleDrawable.Builder()
                    .cornerRadius(0)
                    .backgroundAnimDuration(300)
                    .rippleColor(requireActivity().getColor(R.color.transparent))
                    .backgroundColor(requireActivity().getColor(R.color.transparent))
                    .build()
                rltView3.setBackgroundDrawable(rippleDrawable)
                rippleDrawable = RippleDrawable.Builder()
                    .cornerRadius(0)
                    .backgroundAnimDuration(300)
                    .rippleColor(requireActivity().getColor(R.color.transparent))
                    .backgroundColor(requireActivity().getColor(R.color.transparent))
                    .build()
                rltView4.setBackgroundDrawable(rippleDrawable)
            }
        }
    }

    private fun onResult() {

    }
}