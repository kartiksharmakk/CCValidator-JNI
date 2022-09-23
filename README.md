# Android Implementation

https://user-images.githubusercontent.com/77577353/190901064-5452edec-996e-4a63-9abd-4afe9d9d8f32.mp4


<br />

# Luhn 's Algorithm C++ Jni Code:
(https://github.com/kartiksharmakk/CreditCardValidator-JNI/blob/main/app/src/main/cpp/native-lib.cpp)
```cpp
Java_com_example_ccvalidator_MainActivity_CCValidator( JNIEnv* env,jobject /* this */,jstring UserInputCC)
{
    std::string ccNumber= jstring2string(env,UserInputCC);
    std::string finalResult;
    uint32_t len = ccNumber.length();
    int32_t doubleEvenSum = 0;
    int32_t dbl;
    int32_t i;
    for (i = len - 2; i >= 0; i = i - 2) {
        dbl = ((ccNumber[i] - 48) * 2);
        if (dbl > 9) {
            dbl = (dbl / 10) + (dbl % 10);
        }
        doubleEvenSum += dbl;
    }
    for (i = len - 1; i >= 0; i = i - 2) {
        doubleEvenSum += (ccNumber[i] - 48);
    }
    finalResult=doubleEvenSum % 10 == 0 ? "Valid !" : "Invalid !";
    return env->NewStringUTF(finalResult.c_str());
}
```
# Android code which interlinks with Jni 
(https://github.com/kartiksharmakk/CreditCardValidator-JNI/blob/main/app/src/main/java/com/example/ccvalidator/MainActivity.kt)

```kt
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
            //As we are using Credit Card Formating we have extra 3 characters as spaces so checking with 19 instead of 16 
            if(binding.CCInput.text.toString().length == 19) {
                //Regex trims all spaces inside our string and sends it to our C++ Code for execution
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

```