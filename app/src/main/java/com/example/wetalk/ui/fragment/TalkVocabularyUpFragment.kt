package com.example.wetalk.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.databinding.FragmentTalkVocabularyUpBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.TalkDialogTag
import com.example.wetalk.ui.customview.TalkBodyEditView
import com.example.wetalk.ui.viewmodels.TalkVocabularyViewModel
import com.example.wetalk.util.Task
import com.example.wetalk.util.Utils
import com.example.wetalk.util.helper.FileHelper
import com.example.wetalk.util.helper.KeyboardHeightProvider
import com.example.wetalk.util.helper.permission_utils.Func
import com.example.wetalk.util.helper.permission_utils.PermissionUtil


/**
 * A simple [Fragment] subclass.
 * Use the [TalkVocabularyUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkVocabularyUpFragment : Fragment() {

    private val viewModel : TalkVocabularyViewModel by viewModels()
    private lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private lateinit var videoLocal: VideoLocal
    private lateinit var talkBodyEditView: TalkBodyEditView
    private lateinit var menuCallback: MenuCallback
    private var _binding: FragmentTalkVocabularyUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog
    private val paths = ArrayList<String>()
    private lateinit var talkImageItems: ArrayList<StorageImageItem>
    private lateinit var mRequestObject: PermissionUtil.PermissionRequestObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkVocabularyUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        talkBodyEditView = binding.bodyView
        keyboardHeightProvider = KeyboardHeightProvider(requireActivity());
        videoLocal = VideoLocal(
            -1, System.currentTimeMillis(), "",
            VideoBody(ArrayList<VideoBodyItem>()), "", 1
        )


        lifecycleScope.launchWhenStarted {
            viewModel.videoLocal.collect {
                // Update UI with the videoLocal data
                talkBodyEditView.preview((activity as MainActivity), it ?: videoLocal)
            }

        }
        lifecycleScope.launchWhenResumed {
            viewModel.talkImageItems.collect { talkImageItems ->
                talkBodyEditView.addImage(talkImageItems)
            }
        }

        binding.imgRecord.setOnClickListener {
            val i: Intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(i, 1111)
        }
        onRecord()
        onCallBack()
        onFolder();
        onBack();

    }

    private fun onBack() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun onFolder() {
        binding.imgOpen.setOnClickListener{
                mRequestObject = PermissionUtil.with(activity as MainActivity)
                    .request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .onAllGranted(object : Func() {
                        override fun call() {
                            Utils.hideKeyboard(activity)
                            menuCallback.clearCursor()
                            menuCallback.scrollBottom()
                            BaseFragment.add(
                                activity as MainActivity,
                                TalkSelectVideoFragment.newInstance().setVideoTask(
                                    object : Task<ArrayList<StorageImageItem>> {
                                        override fun callback(result: ArrayList<StorageImageItem>) {
                                            menuCallback.addMedia(result)
                                        }
                                    }
                                )
                            )

                        }
                    }).ask(12)
        }
        binding.imgOpenTag.setOnClickListener {
            showDialogTag()
        }
    }


    private fun onCallBack() {
        menuCallback = object : MenuCallback {
            override fun addMedia(result: ArrayList<StorageImageItem>) {
                FileHelper.checkAndCopyImage(
                    activity as MainActivity,
                    result,
                    object : Task<ArrayList<StorageImageItem>> {
                        override fun callback(result: ArrayList<StorageImageItem>) {
//                            talkBodyEditView.addImage(result)
                            viewModel.addImageItems(result)
                        }
                    })
            }

            override fun addTag() {}
            override fun list(lineType: Int) {}
            override fun clearCursor() {}
            override fun scrollBottom() {}
        }
    }

    /** Permission open camera */
    private fun onRecord() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                111
            )
        } else {
            binding.imgRecord.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        paths.clear()
        if (requestCode == 1111) {
            paths.add(data!!.data.toString())
            talkImageItems = ArrayList<StorageImageItem>()
            for (devicePath in paths) {
                talkImageItems.add(
                    StorageImageItem(
                        true,
                        devicePath,
                        devicePath,
                        if (paths.size > 2) 25 else 45,
                        0
                    )
                )
            }
        }
//        talkBodyEditView.addImage(talkImageItems)
        viewModel.addImageItems(talkImageItems)
    }

    interface MenuCallback {
        fun addMedia(diaryImageItems: ArrayList<StorageImageItem>)
        fun addTag()
        fun list(lineType: Int)
        fun clearCursor()
        fun scrollBottom()
    }

    private fun showDialogTag() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val dataList = ArrayList<String>()
        dataList.add("Danh từ")
        dataList.add("Động từ")
        dataList.add("Tính từ")
        dataList.add("Trạng từ")

        val talkDialogTag = object : TalkDialogTag(requireContext()) {
            override fun OnClickItemTag(position: Int) {
                val item = dataList[position]
                binding.tvName.text = "# $item"
                videoLocal.videoTag = binding.tvName.text.toString()
                alertDialog.dismiss()
            }
        }
        talkDialogTag.setData(dataList)
        recyclerView.adapter =talkDialogTag
        builder.setView(recyclerView)
        builder.setPositiveButton("Đóng", null)
        alertDialog = builder.create()
        alertDialog.show()
    }
    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

}