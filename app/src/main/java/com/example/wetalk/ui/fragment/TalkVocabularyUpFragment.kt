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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.databinding.FragmentTalkVocabularyUpBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.customview.TalkBodyEditView
import com.example.wetalk.util.Task
import com.example.wetalk.util.helper.FileHelper
import com.example.wetalk.util.helper.KeyboardHeightProvider


/**
 * A simple [Fragment] subclass.
 * Use the [TalkVocabularyUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkVocabularyUpFragment : Fragment() {

    private lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private lateinit var videoLocal: VideoLocal
    private lateinit var talkBodyEditView: TalkBodyEditView
    private lateinit var menuCallback: MenuCallback
    private var _binding : FragmentTalkVocabularyUpBinding? =null
    private val binding get() = _binding!!
    private val paths = ArrayList<String>()
    private lateinit var talkImageItems:ArrayList<StorageImageItem>

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


        videoLocal =  VideoLocal(
            -1, System.currentTimeMillis(), "",
            VideoBody(ArrayList<VideoBodyItem>()), "", 1)

        talkBodyEditView.preview((activity as MainActivity), videoLocal)
        binding.imgRecord.setOnClickListener {
            val i:Intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
            startActivityForResult(i, 1111)
        }
        onRecord()
        onCallBack()
        initCamera();

    }

    private fun initCamera() {

    }

    private fun onCallBack() {
        menuCallback = object : MenuCallback {
            override fun addMedia(result:ArrayList<StorageImageItem>) {
                FileHelper.checkAndCopyImage(
                    requireActivity(),
                    result,
                    object : Task<ArrayList<StorageImageItem>> {
                        override fun callback(result:ArrayList<StorageImageItem>) {
                            talkBodyEditView.addImage(result)
                        }
                    })
            }

            override fun addTag() {}
            override fun list(lineType: Int) {}
            override fun clearCursor() {}
            override fun scrollBottom() {}
        }
    }

    private fun onRecord() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 111)
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
        talkBodyEditView.addImage(talkImageItems)
    }
    interface MenuCallback {
        fun addMedia(diaryImageItems: ArrayList<StorageImageItem>)
        fun addTag()
        fun list(lineType: Int)
        fun clearCursor()
        fun scrollBottom()
    }


}