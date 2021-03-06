package com.topeet.max485test;


import com.topeet.max485test.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.os.Message;

public class MainActivity extends Activity {

	/****************************************/
	String rxIdCode = "";
	String tag = "serial test";
	
	private EditText ET1;
	private Button RECV;
	private Button SEND;
	
	serial com3 = new serial();
	
	max485 max485_io = new max485();
	/****************************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/******************************/
		ET1 = (EditText)findViewById(R.id.edit1);
        RECV = (Button)findViewById(R.id.recv1);
        SEND = (Button)findViewById(R.id.send1);
        
        com3.Open(1, 38400, 8, 'E', 1);
        
        max485_io.Open();
        
        /* 初始设置成接收状态 */
        max485_io.Ioctl(0, 0);
        
        RECV.setOnClickListener(new manager());
        
        SEND.setOnClickListener(new manager());
		/******************************/
	}

	/***********************************************/
	/* 串口接收数据的时候先调用它 */
	void prepare_to_recv()
	{
		max485_io.Ioctl(0, 0);
	}
	
	/* 串口发送数据的时候先调用它 */
	void prepare_to_send()
	{
		max485_io.Ioctl(0, 1);
	}
	
	 class manager implements OnClickListener{
 		public void onClick(View v) {
 			String rxIdCode = "";
 			String str;
 			
 			int i;
 			switch (v.getId()) {
 			//recvive
 			case R.id.recv1:
 				
 				Log.d(tag,"recv start ...");
 				
 				max485_io.Ioctl(0, 1);
 				int[] buf = {0xED, 0x11, 0x01, 0x01, 0x46, 0x43, 0xEE};
 				com3.Write(buf, 7);
 				
 				
 				/* 串口接收数据的时候先调用它 */
 				max485_io.Ioctl(0, 0);
 				
 				int[] RX = com3.Read();
 				
				if(RX == null)return;
				
				ET1.append(new String(RX, 0, RX.length));
				
 				break;
 				
 			//send
 			case R.id.send1:
 				Log.d(tag,"send start ...");
 				
 				/* 串口发送数据的时候先调用它 */
 				max485_io.Ioctl(0, 1);
 				
 				CharSequence tx = ET1.getText();
 				
 				int[] text = new int[tx.length()];
 				
                for (i=0; i<tx.length(); i++) 
                {
                        text[i] = tx.charAt(i);
                }

 				
 				com3.Write(text, tx.length());
 				
 				ET1.setText("");
 				
 				/* 发送完成，设置成接收状态 */
 				max485_io.Ioctl(0, 0);
 			}
 		}
 	}
	 /***********************************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/* add by cym 20140617 */
	static {
        System.loadLibrary("max485test");
        System.loadLibrary("max485test_io");
	}
	/* end add */

}
