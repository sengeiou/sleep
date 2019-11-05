LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := JNICrc
LOCAL_SRC_FILES := com_zhuoting_health_util_Crc16Util.cpp

include $(BUILD_SHARED_LIBRARY)