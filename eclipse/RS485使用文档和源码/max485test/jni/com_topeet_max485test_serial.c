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
#include "com_topeet_max485test_serial.h"

#undef  TCSAFLUSH
#define TCSAFLUSH  TCSETSF
#ifndef _TERMIOS_H_
#define _TERMIOS_H_
#endif

int fd=0;

int set_opt(int fd,int nSpeed, int nBits, char nEvent, int nStop)
{
	struct termios newtio,oldtio;
	if  ( tcgetattr( fd,&oldtio)  !=  0) { 
		perror("SetupSerial 1");
		return -1;
	}
	bzero( &newtio, sizeof( newtio ) );
	newtio.c_cflag  |=  CLOCAL | CREAD;
	newtio.c_cflag &= ~CSIZE;
#if 0
	switch( nBits )
	{
	case 7:
		newtio.c_cflag |= CS7;
		break;
	case 8:
		newtio.c_cflag |= CS8;
		break;
	}
#else

	newtio.c_cflag |= CS8;
#endif

#if 0	
	switch( nEvent )
	{
	case 'O':
		newtio.c_cflag |= PARENB;
		newtio.c_cflag |= PARODD;
		newtio.c_iflag |= (INPCK | ISTRIP);
		break;
	case 'E': 
		newtio.c_iflag |= (INPCK | ISTRIP);
		newtio.c_cflag |= PARENB;
		newtio.c_cflag &= ~PARODD;
		break;
	case 'N':  
		newtio.c_cflag &= ~PARENB;
		break;
	}
#else
	newtio.c_cflag &= ~PARODD;
#endif

	switch( nSpeed )
	{
	case 2400:
		cfsetispeed(&newtio, B2400);
		cfsetospeed(&newtio, B2400);
		break;
	case 4800:
		cfsetispeed(&newtio, B4800);
		cfsetospeed(&newtio, B4800);
		break;
	case 9600:
		cfsetispeed(&newtio, B9600);
		cfsetospeed(&newtio, B9600);
		break;
	case 38400:
		cfsetispeed(&newtio, B38400);
    cfsetospeed(&newtio, B38400);
		break;
	case 115200:
		cfsetispeed(&newtio, B115200);
		cfsetospeed(&newtio, B115200);
		break;
	case 460800:
		cfsetispeed(&newtio, B460800);
		cfsetospeed(&newtio, B460800);
		break;
	default:
		cfsetispeed(&newtio, B9600);
		cfsetospeed(&newtio, B9600);
		break;
	}

	newtio.c_cflag |= B38400;
#if 0
	if( nStop == 1 )
		newtio.c_cflag &=  ~CSTOPB;
	else if ( nStop == 2 )
		newtio.c_cflag |=  CSTOPB;
#else
	newtio.c_cflag &=  ~CSTOPB;
#endif
	newtio.c_cc[VTIME]  = 0;
	newtio.c_cc[VMIN] = 0;
	tcflush(fd,TCIFLUSH);
	if((tcsetattr(fd,TCSANOW,&newtio))!=0)
	{
		perror("com set error");
		return -1;
	}
//	printf("set done!\n\r");
	return 0;
}

/*
 * Class:     com_topeet_max485test_serial
 * Method:    Open
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_topeet_max485test_serial_Open
  (JNIEnv *env, jobject obj, jint Port, jint Rate, jint nBits, jchar nEvent, jint nStop)
{
  if(fd <= 0)
  {
		if(0 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttySAC0");
			fd=open("/dev/ttySAC0",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else if(1 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttySAC1");
			fd=open("/dev/ttySAC1",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else if(2 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttySAC2");
			fd=open("/dev/ttySAC2",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else if(3 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttySAC3");
			fd=open("/dev/ttySAC3",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else if(4 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttyUSB0");
			fd=open("/dev/ttyUSB0",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else if(5 == Port)
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "open fd /dev/ttyUSB1");
			fd=open("/dev/ttyUSB1",O_RDWR|O_NDELAY|O_NOCTTY);
		}
		else
		{
			__android_log_print(ANDROID_LOG_INFO, "serial", "Parameter Error serial not found");
			fd = 0;
			return -1;
		}
		
		set_opt(fd, Rate, nBits, nEvent, nStop);
		
#if 0	
		if(fd > 0)
		{	
			__android_log_print(ANDROID_LOG_INFO, "serial", "serial open ok fd=%d", fd);                
			// disable echo on serial ports                    
			struct termios  ios;                    
			tcgetattr( fd, &ios );                    	
			ios.c_oflag &=~(INLCR|IGNCR|ICRNL);
	  	ios.c_oflag &=~(ONLCR|OCRNL);  
			ios.c_iflag &= ~(ICRNL | IXON);			
			ios.c_iflag &= ~(INLCR|IGNCR|ICRNL);
			ios.c_iflag &=~(ONLCR|OCRNL);
			tcflush(fd,TCIFLUSH);
			
			if(Rate == 2400){cfsetospeed(&ios, B2400);  cfsetispeed(&ios, B2400);}
			if(Rate == 4800){cfsetospeed(&ios, B4800);  cfsetispeed(&ios, B4800);}
			if(Rate == 9600){cfsetospeed(&ios, B9600);  cfsetispeed(&ios, B9600);}
			if(Rate == 19200){cfsetospeed(&ios, B19200);  cfsetispeed(&ios, B19200);}
			if(Rate == 38400){cfsetospeed(&ios, B38400);  cfsetispeed(&ios, B38400);}
			if(Rate == 57600){cfsetospeed(&ios, B57600);  cfsetispeed(&ios, B57600);}
			if(Rate == 115200){cfsetospeed(&ios, B115200);  cfsetispeed(&ios, B115200);}
			
			ios.c_cflag |= (CLOCAL | CREAD);
			ios.c_cflag &= ~PARENB;
			ios.c_cflag &= ~CSTOPB;
			ios.c_cflag &= ~CSIZE; 
			ios.c_cflag |= CS8; 
			ios.c_lflag = 0;  
			tcsetattr( fd, TCSANOW, &ios );
		}
#endif
  }

	return fd;
}

/*
 * Class:     com_topeet_max485test_serial
 * Method:    Close
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_topeet_max485test_serial_Close
  (JNIEnv *env, jobject obj)
  {
	if(fd > 0)close(fd);
  }

  
/*
 * Class:     com_topeet_max485test_serial
 * Method:    Read
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_topeet_max485test_serial_Read
  (JNIEnv *env, jobject obj)
 {
		unsigned char buffer[512];
		int BufToJava[512];
		int len = 0, i = 0;
		
		memset(buffer, 0, sizeof(buffer));
		memset(BufToJava, 0, sizeof(BufToJava));
		
		len=read(fd, buffer, 512);
	
		if(len <= 0)return NULL;	
	
		for(i=0; i<len; i++)
		{
			printf("%x", buffer[i]);
			BufToJava[i] = buffer[i];
		}
		
		jintArray array = (*env)-> NewIntArray(env, len); 
		(*env)->SetIntArrayRegion(env, array, 0, len, BufToJava);
	
		return array;	
  }
  
/*
 * Class:     com_topeet_max485test_serial
 * Method:    Read
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_topeet_max485test_serial_Write
  (JNIEnv *env, jobject obj, jintArray buf, jint buflen)
 {
	jsize len = buflen;
	
	if(len <= 0) 
		return -1;

	jintArray array = (*env)-> NewIntArray(env, len);  

	if(array == NULL){array=NULL;return -1;}

	jint *body = (*env)->GetIntArrayElements(env, buf, 0);

	jint i = 0;
	unsigned char num[len];
	
	for (; i <len; i++) 
		num[i] = body[i];
	
	write(fd, num, len);

	array = NULL;

	return 0;	
  }
  


