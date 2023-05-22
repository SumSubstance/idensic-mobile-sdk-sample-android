package com.sumsub.idensic.screen.signin

import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.otaliastudios.cameraview.CameraView
import com.sumsub.idensic.App
import com.sumsub.idensic.R
import com.sumsub.idensic.screen.base.BaseFragment

class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {

    private lateinit var cvCamera: CameraView
    private lateinit var scanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            activity?.finish()
        }

        val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        scanner = BarcodeScanning.getClient(options)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cvCamera = view.findViewById(R.id.camera)
        initCamera()

    }

    private fun initCamera() {

        cvCamera.setLifecycleOwner(this)
        cvCamera.addFrameProcessor { frame ->
            processImage(frame.format, frame.rotationToView, frame.size.width, frame.size.height, frame.getData() as ByteArray)
        }

    }

    private fun processImage(format: Int, rotation: Int, width: Int, height: Int, data: ByteArray) {
        val image = InputImage.fromByteArray(data, width, height, rotation, format)
        scanner.process(image).addOnSuccessListener { barcodes ->
            barcodes.forEach { barcode ->
                try {
                    val loginData = Gson().fromJson(String(Base64.decode(barcode.rawValue, Base64.NO_WRAP)), LoginData::class.java)
                    prefManager.setUrl(loginData.url)
                    prefManager.setToken(loginData.t)
                    prefManager.setSandbox(loginData.isSandBox)
                    prefManager.setClientId(loginData.clientId)
                    findNavController().navigate(R.id.action_sign_in_to_main)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        scanner.close()
        super.onDestroy()
    }

    override fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
}