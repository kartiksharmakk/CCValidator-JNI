package com.example.ccvalidator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ccvalidator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.CCInput.addTextChangedListener(CreditCardNumberFormattingTextWatcher())
        binding.CCInput.setOnClickListener{
            binding.resultText.text=""
        }
        binding.button.setOnClickListener {
            if(binding.CCInput.text.toString().length == 19) {
                var finalResult= CCValidator(binding.CCInput.text.toString().replace("\\s".toRegex(), ""))
                binding.resultText.text = finalResult
                Toast.makeText(applicationContext,
                    finalResult, Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(applicationContext,
                    "Please Enter only 16 digit Numbers", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * A native method that is implemented by the 'ccvalidator' native library,
     * which is packaged with this application.
     */
    external fun CCValidator(ccnumber:String):String

    class CreditCardNumberFormattingTextWatcher : TextWatcher {
        private var current = ""

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString() != current) {
                val userInput = s.toString().replace(nonDigits,"")
                if (userInput.length <= 16) {
                    current = userInput.chunked(4).joinToString(" ")
                    s.filters = arrayOfNulls<InputFilter>(0)
                }
                s.replace(0, s.length, current, 0, current.length)
            }
        }

        companion object {
            private val nonDigits = Regex("[^\\d]")
        }
    }
    companion object {
        // Used to load the 'ccvalidator' library on application startup.
        init {
            System.loadLibrary("ccvalidator")
        }
    }
}