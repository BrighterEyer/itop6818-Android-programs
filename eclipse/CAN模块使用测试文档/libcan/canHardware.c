/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include "can.h"
#include <stdio.h>
#include "android/log.h"
#include <stdlib.h>
#include <sys/socket.h>
#include <cutils/properties.h>


#define SOCK_RAW 3

#ifndef PF_CAN
#define PF_CAN 29
#endif

#ifndef AF_CAN
#define AF_CAN PF_CAN
#endif


static const char *TAG="canHardware";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define ADC_SET_CHANNEL _IOW('S', 0x0c, unsigned long)
int serialfd=-1;
int canfd=-1;
int adcfd =-1;
struct sockaddr_can addr;
static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Can
 */
JNIEXPORT void JNICALL Java_can_hardware_hardwareControl_initCan
  (JNIEnv *env, jobject thiz, jint baudrate)
{

	char ip_cmd[256] = {'\0'};
	
	/* Check arguments */
	switch (baudrate)
	{
		case 5000   :
		case 10000  :
		case 20000  :
		case 50000  :
		case 100000 :
		case 125000 :
			LOGI("Change Can Bus Speed is %d",baudrate);
		break;
		default:
			LOGI("Can Bus Speed is %d.if it do not work,try 5000~125000",baudrate);
	}

	/* Configure device */
	if(baudrate!=0)
	{
		char str_baudrate[16];
		sprintf(str_baudrate,"%d",baudrate);
		property_set("can.baudrate", str_baudrate); 
	}

//	sprintf(ip_cmd,"ip link set can0 up type can bitrate %d triple-sampling on",baudrate);
//
//	LOGI("Calling ip cmd :  %s", ip_cmd);
//	system("ifconfig can0 down");
//	sleep(2);
//	system(ip_cmd);
//	sleep(2);
//	system("ifconfig can0 up");
//	sleep(2);
//	LOGI("CAN devices status");
//	system("ip -details link show can0");


//	sprintf(ip_cmd,"can.sh %d",baudrate);
//	system(ip_cmd);
//	system("echo hello world");
//
//	sprintf(try_cmd,"echo nihao shijie %d",baudrate);
//	system(try_cmd);

	
}



/*
 * Can
 */
JNIEXPORT jint JNICALL Java_can_hardware_hardwareControl_openCan
  (JNIEnv *env, jobject thiz)
{

	struct ifreq ifr;

       			
        

	/* Opening device */
	canfd = socket(PF_CAN,SOCK_RAW,CAN_RAW);


	if(canfd==-1)
	{
		LOGE("Can Write Without Open"); 
		return   0;
	}

	strcpy((char *)(ifr.ifr_name),"can0");
	ioctl(canfd,SIOCGIFINDEX,&ifr);



	addr.can_family = AF_CAN;
	addr.can_ifindex = ifr.ifr_ifindex;
	bind(canfd,(struct sockaddr*)&addr,sizeof(addr));



	return canfd;
}

JNIEXPORT jint JNICALL Java_can_hardware_hardwareControl_canWrite
  (JNIEnv *env, jobject thiz, jint canId,jbyteArray data)
{

	int nbytes;
	int i=0;
	int size=0;
	struct can_frame frame;
	char* tmpdata;

	jboolean iscopy;
	size = (*env)->GetArrayLength(env,data);

	tmpdata = (char*)(*env)->GetByteArrayElements(env,data,0);
	if(tmpdata==NULL)
	{
		LOGE("canHardware_canWrite:jbyteArray argument error!");
		return -1;
	}

	for(i=0;i<size;i++){
		frame.data[i]=tmpdata[i];
	}
	frame.can_id = canId;
//	frame.can_dlc = strlen(frame.data);
	frame.can_dlc = size;

	nbytes = sendto(canfd,&frame,sizeof(struct can_frame),0,(struct sockaddr*)&addr,sizeof(addr));

	LOGD("write nbytes=%d",nbytes);
	(*env)->ReleaseByteArrayElements(env,data,(jbyte *)tmpdata,0);
	//(*env)->DeleteLocalRef(env,tmpdata);
		return nbytes;
}

JNIEXPORT jobject JNICALL Java_can_hardware_hardwareControl_canRead
  (JNIEnv *env, jobject thiz,jobject obj,jint time)
{

	unsigned long nbytes,len;

	struct can_frame frame;
	int k=0;
	jstring   jstr; 
	
	//char temp[256];
	jbyte* temp;
	temp = (jbyte*)calloc(256,sizeof(jbyte));

	fd_set rfds;
	int retval;
	struct timeval tv;
        tv.tv_sec = time;  		
        tv.tv_usec = 0;

	 bzero(temp,256);
	if(canfd==-1){
		LOGE("Can Read Without Open");
		frame.can_id=0;
		frame.can_dlc=0;
	}else{
		FD_ZERO(&rfds);
		FD_SET(canfd, &rfds);
		retval = select(canfd+1 , &rfds, NULL, NULL, &tv);
		if (retval == -1){
			LOGE("Can Read slect error");
			frame.can_dlc=0;
			frame.can_id=0;
		}else if (retval){
			LOGE("retval has value");
			nbytes = recvfrom(canfd,&frame,sizeof(struct can_frame),0,(struct sockaddr *)&addr,&len);
		
			for(k=0;k<frame.can_dlc;k++)
			{
				temp[k]=frame.data[k];
			}
			temp[k]=0;

		}else{
			frame.can_dlc=0;
			frame.can_id=0;
		}

	}

	jbyteArray array = (*env)->NewByteArray(env,frame.can_dlc);
	(*env)->SetByteArrayRegion(env,array,0,frame.can_dlc,temp);
 

    jclass objectClass = (*env)->FindClass(env,"can/hardware/canFrame");
	if(objectClass==NULL)
	{
		LOGE("canHardware_canRead: find class can/hardware/canFrame error !");
		free(temp);
		return NULL;
	}
    jfieldID id = (*env)->GetFieldID(env,objectClass,"can_id","I");
    jfieldID leng = (*env)->GetFieldID(env,objectClass,"can_dlc","C");
	jfieldID rdata = (*env)->GetFieldID(env,objectClass,"recdata","[B");

	if(obj==NULL)
	{
		LOGE("canHardware_canRead: jobject argument error !");
		free(temp);
		return NULL;
	}
	if(id!=NULL)
	{
		(*env)->SetIntField(env,obj,id,frame.can_id);
	}
	else
	{
		LOGE("canHardware_canRead: can_id is null !");
	}

	if(leng!=NULL)
	{
		(*env)->SetCharField(env,obj,leng,frame.can_dlc);
	}
	else
	{
		 LOGE("canHardware_canRead: can_dlc is null !");
	}

	if(rdata!=NULL)
	{
		(*env)->SetObjectField(env,obj,rdata,array);
	}
	else
	{
		LOGE("canHardware_canRead: recdata is null !");
	}

	free(temp);
	(*env)->DeleteLocalRef(env, objectClass);

	return   obj;

}

JNIEXPORT void JNICALL Java_can_hardware_hardwareControl_closeCan
  (JNIEnv *env, jobject thiz)
{

	if(canfd!=-1)
		close(canfd);
	canfd=-1;
	LOGD("close can0");
}


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    
    jint result = -1;


    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}

