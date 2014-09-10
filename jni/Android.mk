LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -O2

LOCAL_MODULE    := NativeDrawer
LOCAL_SRC_FILES := NativeDrawer.cpp

include $(BUILD_SHARED_LIBRARY)
