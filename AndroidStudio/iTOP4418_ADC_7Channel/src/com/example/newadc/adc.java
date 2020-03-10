package com.example.newadc;

public class adc {
	public native int       Open(int channel);
    public native int       Close();
    public native int       Ioctl(int num, int en);//这个方法没有用到
    public native int[]     Read();
}
