LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := max485test
LOCAL_SRC_FILES := com_topeet_max485test_serial.c
LOCAL_LDLIBS += -llog 
LOCAL_LDLIBS +=-lm
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := max485test_io
LOCAL_SRC_FILES := com_topeet_max485test_max485.c
LOCAL_LDLIBS += -llog 
LOCAL_LDLIBS +=-lm
include $(BUILD_SHARED_LIBRARY)