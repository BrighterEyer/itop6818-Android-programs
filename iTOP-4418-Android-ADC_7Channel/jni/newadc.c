#include "com_example_newadc_adc.h"
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <stdint.h>
#include <termios.h>
#include <android/log.h>
#include <sys/ioctl.h>


#undef  TCSAFLUSH
#define TCSAFLUSH  TCSETSF
#ifndef _TERMIOS_H_
#define _TERMIOS_H_
#endif


int fd=0;
FILE *fp;
/*
 * Class:     com_example_newadc_adc
 * Method:    Open
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_example_newadc_adc_Open
  (JNIEnv *ent , jobject obj, jint channel)
{
	if(fd<=0)
			if(0 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage0_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage0_raw", "rt");
					}
			else if(1 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage1_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage1_raw", "rt");
					}
			else if(2 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage2_raw");
				fp =fopen("/sys/bus/iio/devices/iio:device0/in_voltage2_raw", "rt");
					}
			else if(3 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage3_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage3_raw", "rt");
					}
			else if(4 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage4_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage4_raw", "rt");
					}
			else if(5 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage5_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage5_raw", "rt");
					}
			else if(6 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage6_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage6_raw", "rt");
					}
			else if(7 == channel)
					{
					//__android_log_print(ANDROID_LOG_INFO, "adc", "open fd /sys/bus/iio/devices/iio:device0/in_voltage7_raw");
				fp = fopen("/sys/bus/iio/devices/iio:device0/in_voltage7_raw", "rt");
					}
			else
					{
					//__android_log_print(ANDROID_LOG_INFO, "serial", "Parameter Error serial not found");
				fd = 0;
					return -1;
					}

	  return fd;
}

/*
 * Class:     com_example_newadc_adc
 * Method:    Close
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_newadc_adc_Close
  (JNIEnv *ent, jobject obj)
{
	if(fp!=NULL)
	{
		fclose(fp);
	}

}

/*
 * Class:     com_example_newadc_adc
 * Method:    Ioctl
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_example_newadc_adc_Ioctl
  (JNIEnv *ent , jobject obj,jint en, jint num)
{
	ioctl(fd, en, num);
}

/*
 * Class:     com_example_newadc_adc
 * Method:    Read
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_example_newadc_adc_Read
(JNIEnv *env, jobject obj)
{
	        unsigned char buffer[512];
			int BufToJava[512];
			int len=0, i=0;
			//FILE *fp;
			int ret=0;
			memset(buffer,0,sizeof(buffer));
			memset(BufToJava,0,sizeof(BufToJava));
			if(fp==NULL){
					ret = -1;//这句没有什么用
					return NULL;
				}
			len=fread(buffer,1,512,fp);
			if(len<=0)return NULL;
			for(i=0;i<len;i++)
			{
				printf("%x", buffer[i]);
				BufToJava[i] = buffer[i];
			}
			jintArray array = (*env)-> NewIntArray(env, len);
			(*env)->SetIntArrayRegion(env,array, 0, len, BufToJava);

			return array;
}