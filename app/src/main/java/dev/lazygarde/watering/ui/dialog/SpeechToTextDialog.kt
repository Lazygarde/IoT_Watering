package dev.lazygarde.watering.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.lazygarde.watering.R
import dev.lazygarde.watering.databinding.DialogSpeechToTextBinding

class SpeechToTextDialog : BottomSheetDialogFragment() {
    private lateinit var binding : DialogSpeechToTextBinding
    var onSpeechToTextResult: ((String) -> Unit)? = null
    private lateinit var speechRecognizer: SpeechRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogSpeechToTextBinding.inflate(inflater)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }
        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle?) {
                onSpeechToTextResult?.invoke(p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: "")
                dismiss()
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
                binding.tvContent.text = p1?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
            }

        })
        speechRecognizer.startListening(speechRecognizerIntent)

    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

}