package com.asiapay.samsungpay.demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.asiapay.sdk.PaySDK
import com.asiapay.sdk.enums.EnvBase
import com.asiapay.sdk.integration.PaymentResponse
import com.asiapay.sdk.model.Data
import com.asiapay.sdk.model.PayData
import com.asiapay.sdk.model.PayResult
import com.simplr.rad.dev.databinding.FragmentFirstBinding
import kotlin.random.Random


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var paySDK: PaySDK
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.press.setOnClickListener {
            try {
                paySDK = PaySDK(requireContext().applicationContext)
                var payData = PayData()

                Log.d("Pay SDK", "Start Pay")
                paySDK.setPayConfig(
                    requireActivity(),
                    payData,
                    "85007538",
                    EnvBase.EnvType.SANDBOX,
                    EnvBase.PayGate.PAYDOLLAR
                )
                paySDK.setPayData(
                    "1",
                    EnvBase.Currency.MYR,
                    EnvBase.PayType.NORMAL_PAYMENT,
                    Random.nextDouble().toString(),
                    "TouchnGo",
                    EnvBase.PayChannel.WEBVIEW,
                    EnvBase.Language.ENGLISH,
                    ""
                )

                payData.isShowCloseButton = true
                payData.showToolbar = true
                payData.webViewClosePrompt = "Do you really want to close this page ?"
                payData.resultPage = "T"
                val extraDataHosted = mutableMapOf<String, String>()
                extraDataHosted["deeplink"] = "3"
                extraDataHosted["redirect"] = "30"
                payData.extraData = extraDataHosted

                val factory = com.asiapay.sdk.integration.xecure3ds.Factory()
                val configParameters = factory.newConfigParameters()
                val uiCustomization = factory.newUiCustomization()

                val toolbarCustomization = factory.newToolbarCustomization()
                toolbarCustomization?.apply {
                    headerText = "Payment Page"
                    backgroundColor = "#ff8000"
                    textColor = "#ffffff"
                    buttonText = "Close"
                    textFontName = "pacifico.ttf"
                    uiCustomization.toolbarCustomization = this
                }

                payData.configParameters = configParameters
                payData.uiCustomization = uiCustomization
// payData.activity = requireActivity()

                paySDK.requestData = payData
//                paySDK.process()
                paySDK.responseHandler(object : PaymentResponse() {
                    override fun getResponse(payResult: PayResult?) {
                        Log.d("Pay SDK", "getResponse: " + (payResult?.isSuccess ?: ""))
                    }

                    override fun onError(data: Data?) {
                        Log.d("Pay SDK", "getResponse error: " + (data?.getError() ?: ""))
                    }
                })
                paySDK.process()
            } catch (e: Exception) {
                Log.d("TAG", "onViewCreated: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}