package com.example.newadc;

public class adc {
	public native int       Open(int channel);
    public native int       Close();
    public native int       Ioctl(int num, int en);//�������û���õ�
    public native int[]     Read();
}
