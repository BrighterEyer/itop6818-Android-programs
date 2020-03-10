LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := newadc
LOCAL_SRC_FILES := newadc.c

include $(BUILD_SHARED_LIBRARY)