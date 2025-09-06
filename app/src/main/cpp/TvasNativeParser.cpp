#include <jni.h>
#include <string>
#include <vector>

#include "includes/Base64Decoder.hpp"
#include "includes/TvasParser.hpp"
#include "includes/GpsPoint.hpp"

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_mocklocation_data_parser_TvasNativeParser_parseNative(
        JNIEnv* env,
        jobject,
        jstring input) {

    if (!input) return nullptr;
    const char* base64Str = env->GetStringUTFChars(input, nullptr);
    std::string base64Cpp(base64Str);
    env->ReleaseStringUTFChars(input, base64Str);

    std::vector<GpsPoint> gpsPoints;
    try {
        TvasParser parser;
        gpsPoints = parser.parse(base64Cpp);
    } catch (const std::exception& e) {
        return nullptr;
    }

    jclass gpsClass = env->FindClass("com/example/mocklocation/domain/entity/GpsPoint");
    if (!gpsClass) return nullptr;

    jmethodID gpsCtor = env->GetMethodID(
            gpsClass,
            "<init>",
            "(Ljava/lang/String;JDDDFFFFFFIII)V"
    );
    if (!gpsCtor) return nullptr;

    jobjectArray result = env->NewObjectArray(gpsPoints.size(), gpsClass, nullptr);
    for (size_t i = 0; i < gpsPoints.size(); ++i) {
        const GpsPoint& p = gpsPoints[i];

        jobject gpsObj = env->NewObject(
                gpsClass,
                gpsCtor,
                env->NewStringUTF(p.provider.c_str()),
                static_cast<jlong>(p.timestamp),
                static_cast<jdouble>(p.latitude),
                static_cast<jdouble>(p.longitude),
                static_cast<jdouble>(p.altitude),
                static_cast<jfloat>(p.hAcc),
                static_cast<jfloat>(p.vAcc),
                static_cast<jfloat>(p.sAcc),
                static_cast<jfloat>(p.speed),
                static_cast<jfloat>(p.bearing),
                static_cast<jfloat>(p.bAcc),
                static_cast<jint>(p.satellites),
                static_cast<jint>(p.maxCn0),
                static_cast<jint>(p.meanCn0)
        );

        env->SetObjectArrayElement(result, i, gpsObj);
        env->DeleteLocalRef(gpsObj);
    }

    return result;
}