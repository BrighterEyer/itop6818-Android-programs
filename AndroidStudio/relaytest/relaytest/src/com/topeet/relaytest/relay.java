package com.topeet.relaytest;

public class relay {
	public native int       Open();
    public native int       Close();
    public native int       Ioctl(int num, int en);
}
