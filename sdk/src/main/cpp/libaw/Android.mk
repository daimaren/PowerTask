LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CFLAGS += -std=c99
LOCAL_LDLIBS += -llog -landroid

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(MY_APP_LIB_FAAC_INCLUDE_PATH)
LOCAL_C_INCLUDES += $(MY_APP_LIB_RTMP_INCLUDE_PATH)
LOCAL_C_INCLUDES += $(MY_APP_LIB_X264_INCLUDE_PATH)
LOCAL_C_INCLUDES += $(MY_APP_LIB_YUV_INCLUDE_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/common
LOCAL_C_INCLUDES += $(LOCAL_PATH)/pushStream/encoder
LOCAL_C_INCLUDES += $(LOCAL_PATH)/pushStream/flv
LOCAL_C_INCLUDES += $(LOCAL_PATH)/pushStream/rtmp

LOCAL_SRC_FILES := \
    $(LOCAL_PATH)/aw_jni.cpp \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/common/*.c))  \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/encoder/*.c))  \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/flv/*.c)) \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/rtmp/*.c)) \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/*.c)) \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/*.cpp)) \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/api/*.cpp)) \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/pushStream/base/*.cpp))
LOCAL_STATIC_LIBRARIES := librtmp libx264 libfaac
LOCAL_MODULE := aw
include $(BUILD_SHARED_LIBRARY)
