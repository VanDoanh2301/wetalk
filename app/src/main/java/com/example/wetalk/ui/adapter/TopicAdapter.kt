import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemTopicBinding
import com.example.wetalk.util.ROLE_USER
import com.example.wetalk.util.SharedPreferencesUtils

class TopicAdapter(var context: Context) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    private var listTest: List<TopicRequest> = emptyList()
    private var mClickListener: ItemClickListener? = null

    inner class ViewHolder(var binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root), OnCreateContextMenuListener {

        init {
            if (SharedPreferencesUtils.getString(ROLE_USER).equals("ADMIN")) {
                binding.lnView.setOnCreateContextMenuListener(this)
            }

        }

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

        override fun onCreateContextMenu(
            p0: ContextMenu?,
            p1: View?,
            p2: ContextMenu.ContextMenuInfo?
        ) {
            p0?.add(bindingAdapterPosition, 0, 0, "Chỉnh sửa")
            p0?.add(bindingAdapterPosition, 1, 0, "Xóa")
        }

    }
    fun addItem(item: TopicRequest) {
        val newList = listTest.toMutableList()
        newList.add(item)
        listTest = newList.toList()
        notifyItemInserted(listTest.size - 1)
    }
    fun updateItem(position: Int, newItem: TopicRequest) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList[position] = newItem
            listTest = newList.toList()
            notifyItemChanged(position)
        }
    }
    fun removeItem(position: Int) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList.removeAt(position)
            listTest = newList.toList()
            notifyItemRemoved(position)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTopicBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTest!!.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var testItem: TopicRequest = listTest!![position]
        holder.bind(position, testItem)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    fun getItemSelect(position: Int): TopicRequest {
        return listTest[position]
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun submitList(newList: List<TopicRequest>) {
        val diffCallback = ResultPregnancyDiffCallback(listTest, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listTest = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<TopicRequest>,
        private val newList: List<TopicRequest>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có đại diện cho cùng một mục hay không
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có nội dung giống nhau hay không
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}