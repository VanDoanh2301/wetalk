

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.model.objectmodel.Topic
import com.example.wetalk.databinding.ItemTopicBinding

class TalkTopicAdapter(var context: Context) :
    RecyclerView.Adapter<TalkTopicAdapter.ViewHolder>() {
    private var listTest: ArrayList<Topic> ? = null
    private var mClickListener: ItemClickListener? = null

    inner class ViewHolder(var binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, item: Topic) {
            binding.apply {
                tvTitle.text = item.content;
                lnView.setOnClickListener {
                    if (mClickListener != null) {
                        mClickListener!!.onItemClick(position)
                    }
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTopicBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTest!!.size ?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var testItem:Topic = listTest!![position]
        holder.bind(position, testItem)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun notifyData(dataList: ArrayList<Topic>) {
        this.listTest = dataList
        notifyDataSetChanged()
    }
}