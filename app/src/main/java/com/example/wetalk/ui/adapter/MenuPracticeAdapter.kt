package com.example.wetalk.ui.adapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.R
import com.example.wetalk.data.local.TestQuest
import com.example.wetalk.databinding.ItemQuestionResultBinding
import com.example.wetalk.util.FileConfigUtils


class MenuPracticeAdapter(var context: Context, var data:ArrayList<TestQuest>) :
    RecyclerView.Adapter<MenuPracticeAdapter.ViewHolder>() {


    private var indexSelection = 0

    private var current_selected = 0

    private var isFinishTest = false

    private var mClickListener: ItemClickListener? =null

    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener= itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setFinishTest(finishTest: Boolean) {
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionResultBinding.inflate(LayoutInflater.from(context)
            , parent
            , false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position == indexSelection)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    inner class ViewHolder(private val binding: ItemQuestionResultBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {


        fun bind(item: TestQuest, isSelected: Boolean) {
            binding.apply {
                tvNumberQuest.text = (adapterPosition + 1).toString()

                if (item.answer != null && item.answer.isNotEmpty()) {
                    rltAnswer.visibility = View.VISIBLE
                    if (item.answer.equals(item.question.answer_correct)) {
                        rltAnswer.setBackgroundResource(R.drawable.b2_grid_item_question_bg_success)
                        tvAnswer.text = convertAnswer(item)
                    } else {
                        rltAnswer.setBackgroundResource(R.drawable.b2_grid_item_question_bg_error)
                        tvAnswer.text = convertAnswer(item)
                    }
                } else {
                    rltAnswer.visibility = View.GONE
                }

                if (position == current_selected) {
                    rltView.setBackgroundResource(R.drawable.right_item_selected_stroke)
                    tvtTitle.setTextColor(Color.parseColor("#2F8DFE"))
                } else {
                    rltView.setBackgroundResource(R.drawable.right_item_selected_none)
                    tvtTitle.setTextColor(
                        FileConfigUtils.getColorFromAttr(
                            context,
                            R.color.white
                        )
                    )
                }
            }
        }

        private fun convertAnswer(questionNew: TestQuest): String? {
            return if (questionNew.answer== questionNew.question.answer_a) {
                "A"
            } else if (questionNew.answer == questionNew.question.answer_b) {
                "B"
            } else if (questionNew.answer== questionNew.question.answer_c) {
                "C"
            } else if (questionNew.answer == questionNew.question.answer_d) {
                "D"
            } else {
                ""
            }
        }

        override fun onClick(p0: View?) {
            mClickListener?.onItemClick(p0, adapterPosition)
        }
    }

    fun setCurrent_selected(current_selected: Int) {
        this.current_selected = current_selected
    }

    fun setIndexSelection(indexSelection: Int) {
        notifyItemChanged(current_selected)
        current_selected = indexSelection
        notifyItemChanged(current_selected)
    }
}

