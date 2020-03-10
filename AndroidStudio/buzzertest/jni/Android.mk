LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := buzzer
LOCAL_SRC_FILES := com_topeet_buzzertest_buzzer.c
LOCAL_LDLIBS += -llog 
LOCAL_LDLIBS +=-lm
include $(BUILD_SHARED_LIBRARY)