package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wetalk.databinding.TalkPlayFragmentBinding
import com.example.wetalk.ui.activity.MainActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TalkPlayFragment : Fragment() {
    private var path: String? = null
    private var i = 0
    private var _binding: TalkPlayFragmentBinding? = null
    private val binding get() = _binding!!
    private var exoPlayer: SimpleExoPlayer ? =null
    fun setPath(
        path: String,
        i: Int
    ): TalkPlayFragment {
        this.path = path
        this.i = i
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TalkPlayFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        initializePlayer()
    }
    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        binding.videoView.player = exoPlayer

        path?.let {
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
        }
    }

    companion object {
        fun newInstance(): TalkPlayFragment {
            val args = Bundle()
            val fragment =
                TalkPlayFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

}