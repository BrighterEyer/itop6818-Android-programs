package com.topeet.max485test;

public class serial {
	
	public native int 	Open(int Port, int Rate, int nBits, char nEvent, int nStop);
	public native int 	Close();
	public native int[]	Read();
	public native int	Write(int[] buffer,int len);

}
