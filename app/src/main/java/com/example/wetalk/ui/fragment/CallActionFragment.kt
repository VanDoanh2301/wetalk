package com.example.wetalk.ui.fragment

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.codewithkael.firebasevideocall.service.MainServiceRepository
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentCallActionBinding
import com.example.wetalk.repository.FireBaseRepository
import com.example.wetalk.service.MainService
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.util.convertToHumanTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [CallActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CallActionFragment : Fragment(),  MainService.EndCallListener {
    private var target: String? = null
    private var isVideoCall = false
    private var isCaller = false

    private var isMicrophoneMuted = false
    private var isCameraMuted = false
    private var isSpeakerMode = true
    private var isScreenCasting = false
    private var _binding : FragmentCallActionBinding ? =null
    private val binding get() =  _binding!!
    @Inject lateinit var serviceRepository: MainServiceRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            target = it.getString("target")
            isVideoCall = it.getBoolean("isVideoCall")
            isCaller = it.getBoolean("isCaller")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCallActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init() {
        binding.apply {
            callTitleTv.text = "In call with $target"
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..3600) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        //convert this int to human readable time
                        callTimerTv.text = i.convertToHumanTime()
                    }
                }
            }
            if (!isVideoCall){
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false

            }
            MainService.remoteSurfaceView = remoteView
            MainService.localSurfaceView = localView
            serviceRepository.setupViews(isVideoCall,isCaller,target!!)

            endCallButton.setOnClickListener {
                serviceRepository.sendEndCall()
            }

            switchCameraButton.setOnClickListener {
                serviceRepository.switchCamera()
            }
        }
        setupMicToggleClicked()
        setupCameraToggleClicked()
//        setupToggleAudioDevice()
        setupScreenCasting()
        MainService.endCallListener = this
    }
    private fun setupCameraToggleClicked(){
        binding.apply {
            toggleCameraButton.setOnClickListener {
                if (!isCameraMuted){
                    serviceRepository.toggleVideo(true)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
                }else{
                    serviceRepository.toggleVideo(false)
                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
                }

                isCameraMuted = !isCameraMuted
            }
        }
    }
    private fun setupScreenCasting() {
        binding.apply {
            screenShareButton.setOnClickListener {
                if (!isScreenCasting){
                    //we have to start casting
                    AlertDialog.Builder(requireContext())
                        .setTitle("Screen Casting")
                        .setMessage("You sure to start casting ?")
                        .setPositiveButton("Yes"){dialog,_ ->
                            //start screen casting process
                            startScreenCapture()
                            dialog.dismiss()
                        }.setNegativeButton("No") {dialog,_ ->
                            dialog.dismiss()
                        }.create().show()
                }else{
                    //we have to end screen casting
                    isScreenCasting = false
                    updateUiToScreenCaptureIsOff()
                    serviceRepository.toggleScreenShare(false)
                }
            }

        }
    }
    private fun updateUiToScreenCaptureIsOff() {
        binding.apply {
            localView.isVisible = true
            switchCameraButton.isVisible = true
            toggleCameraButton.isVisible = true
            screenShareButton.setImageResource(R.drawable.ic_screen_share)
        }
    }
    private fun startScreenCapture() {
        val mediaProjectionManager = (activity as MainActivity).getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager

        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()


    }
    private fun setupMicToggleClicked(){
        binding.apply {
            toggleMicrophoneButton.setOnClickListener {
                if (!isMicrophoneMuted){
                    //we should mute our mic
                    //1. send a command to repository
                    serviceRepository.toggleAudio(true)
                    //2. update ui to mic is muted
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
                }else{
                    //we should set it back to normal
                    //1. send a command to repository to make it back to normal status
                    serviceRepository.toggleAudio(false)
                    //2. update ui
                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
                }
                isMicrophoneMuted = !isMicrophoneMuted
            }
        }
    }
    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(target: String, isVideoCall: Boolean, isCaller: Boolean) =
            CallActionFragment().apply {
                arguments = Bundle().apply {
                    putString("target", target)
                    putBoolean("isVideoCall", isVideoCall)
                    putBoolean("isCaller", isCaller)

                }
            }
    }

    override fun onCallEnded() {
        (activity as MainActivity).onBackPressed()
    }
}