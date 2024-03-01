package com.example.wetalk.ui.fragment

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.wetalk.R
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.databinding.FragmentTalkSelectVideoBinding
import com.example.wetalk.ui.adapter.VideoChooseAdapter
import com.example.wetalk.util.Task
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSelectVideoDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkSelectVideoDialogFragment : BaseDialogFragment() {
    private var videoTask: Task<ArrayList<StorageImageItem>>? = null
    private var _binding: FragmentTalkSelectVideoBinding? =null
    private val binding get() =  _binding!!
    private val paths = ArrayList<String>()
    private lateinit var videoChooseAdapter: VideoChooseAdapter
    fun getPaths(): ArrayList<String> {
        return paths
    }
    companion object {
        fun newInstance(): TalkSelectVideoDialogFragment {
            val args = Bundle()
            val fragment = TalkSelectVideoDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
    fun setVideoTask(videoTask: Task<ArrayList<StorageImageItem>>): TalkSelectVideoDialogFragment{
        this.videoTask = videoTask
        return this
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkSelectVideoBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBack();
        initViewTab();

    }
    private fun onBack() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    private fun initViewTab() {
        binding.viewPager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return 2
            }

            override fun instantiateItem(collection: ViewGroup, position: Int): Any {
                var resId = 0
                when (position) {
                    0 -> resId = R.id.rcv_image
                    1 -> resId = R.id.rcv_video
                }
                return view!!.findViewById(resId)
            }

            override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
                return arg0 === arg1
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                // No super
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return if (position == 0) {
                    "Image"
                } else {
                    "Video"
                }
            }
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        initVideo()


    }
    private fun initVideo() {
        val cvSave = binding.cvSave
        binding.cvSave.visibility = View.GONE
        val image = getGalleryPhotos(requireActivity())
        val video = getGalleryVideos(requireActivity())
        val rcvImage: RecyclerView = binding.rcvImage
        rcvImage.layoutManager = GridLayoutManager(activity, 4)
        videoChooseAdapter = object : VideoChooseAdapter (requireContext(), image) {
            override fun OnItemClick(i: Int, path: String?) {
                if (paths.contains(path)) {
                    paths.remove(path)
                } else if (paths.size > 4) {
                    Toast.makeText(
                        activity,
                        context!!.resources.getString(R.string.max_5_filed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!File(path).canRead()) {
                    Toast.makeText(activity, path, Toast.LENGTH_SHORT).show()
                } else {
                    paths.add(path!!)
                }
                cvSave.visibility = if (paths.size > 0) View.VISIBLE else View.GONE
                videoChooseAdapter.notifyData(paths)
                notifyItemChanged(i)
            }
        }
        rcvImage.adapter = videoChooseAdapter
        val rcvVideo: RecyclerView = binding.rcvVideo
        rcvVideo.layoutManager = GridLayoutManager(activity, 4)
        rcvVideo.adapter = object : VideoChooseAdapter(requireActivity(), video) {
            override fun OnItemClick(i: Int, path: String?) {
                if (paths.contains(path)) {
                    paths.remove(path)
                } else if (paths.size > 4) {
                    Toast.makeText(
                        activity,
                        context!!.resources.getString(R.string.max_5_filed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    paths.add(path!!)
                }
                cvSave.visibility = if (paths.size > 0) View.VISIBLE else View.GONE
                notifyItemChanged(i)
            }
        }
        /** Save Video is selected */
        cvSave.setOnClickListener {
            val videoImageItems: ArrayList<StorageImageItem> =
                java.util.ArrayList<StorageImageItem>()
            for (devicePath in paths) {
                if (image.contains(devicePath)) {
                    videoImageItems.add(
                        StorageImageItem(
                            false,
                            "",
                            devicePath,
                            if (paths.size > 2) 25 else 45,
                            0
                        )
                    )
                } else if (video.contains(devicePath)) {
                    videoImageItems.add(
                        StorageImageItem(
                            true,
                            "",
                            devicePath,
                            if (paths.size > 2) 25 else 45,
                            0
                        )
                    )
                }
            }
            videoTask!!.callback(videoImageItems)
            requireActivity().onBackPressed()
        }
    }

    private fun getGalleryPhotos(context: Context):ArrayList<String> {
        val pictures = java.util.ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
        val cursorPhotos = context.contentResolver.query(uri, columns, null, null, orderBy)
        if (cursorPhotos != null && cursorPhotos.moveToFirst()) {
            do {
                val path = cursorPhotos.getString(0)
                pictures.add(path)
            } while (cursorPhotos.moveToNext())
            cursorPhotos.close()
        }
        pictures.reverse()
        return pictures
    }

    private fun getGalleryVideos(context: Context): ArrayList<String> {
        val pictures = java.util.ArrayList<String>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID)
        val orderBy = MediaStore.Video.Media._ID
        val cursorVideos = context.contentResolver.query(uri, columns, null, null, orderBy)
        if (cursorVideos != null && cursorVideos.moveToFirst()) {
            do {
                val path = cursorVideos.getString(0)
                pictures.add(path)
            } while (cursorVideos.moveToNext())
            cursorVideos.close()
        }
        pictures.reverse()
        return pictures
    }
    override val layoutId: Int
        get() = R.id.talkSelectVideoFragment
}