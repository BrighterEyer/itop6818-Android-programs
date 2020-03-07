package com.topeet.max485test;

public class max485 {
	public native int       Open();
    public native int       Close();
    public native int       Ioctl(int num, int en);
}
