#include <jni.h>
#include <string>

//In java, a unicode char will be encoded using 2 bytes (utf16).
//so jstring will container characters utf16.
//std::string in c++ is essentially a string of bytes, not characters,
//so if we want to pass jstring from JNI to c++, we have convert utf16 to bytes

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes",
                                                "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr,
                                                                       getBytes,
                                                                       env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ccvalidator_MainActivity_CCValidator( JNIEnv* env,
                                                       jobject /* this */,
                                                       jstring UserInputCC)
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
