#ifndef LOG_HPP
# define LOG_HPP

#include <android/log.h>

#ifndef LOG_TAG
# define LOG_TAG "App_Native_Log"
#endif

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif