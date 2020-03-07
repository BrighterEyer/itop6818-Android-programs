LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := rfid
LOCAL_SRC_FILES := com_topeet_rfidtest_rfid.c
LOCAL_LDLIBS += -llog 
LOCAL_LDLIBS +=-lm
include $(BUILD_SHARED_LIBRARY)
