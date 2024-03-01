

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.databinding.ItemTopicBinding

class TopicAdapter(var context: Context) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    private var listTest: ArrayList<TopicRequest> ? = null
    private var mClickListener: ItemClickListener? = null

    inner class ViewHolder(var binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, item: TopicRequest) {
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
        var testItem:TopicRequest = listTest!![position]
        holder.bind(position, testItem)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun notifyData(dataList: ArrayList<TopicRequest>) {
        this.listTest = dataList
        notifyDataSetChanged()
    }
}