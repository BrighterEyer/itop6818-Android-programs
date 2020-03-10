package com.topeet.serialtest;


import com.topeet.serialtest.R;

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
	/****************************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/********************************************/
		ET1 = (EditText)findViewById(R.id.edit1);
        RECV = (Button)findViewById(R.id.recv1);
        SEND = (Button)findViewById(R.id.send1);
       
        
        //       com3.Open(6, 115200);  //4418
        com3.Open(1, 115200);	 //6818
        
        
        RECV.setOnClickListener(new manager());
        
        SEND.setOnClickListener(new manager());
		/*********************************************/
        
	}

	/***********************************************/
	 class manager implements OnClickListener{
 		public void onClick(View v) {
 			String rxIdCode = "";
 			String str;
 			
 			int i;
 			switch (v.getId()) {
 			//recvive
 			case R.id.recv1:
 				
 				Log.d(tag,"recv start ...");
 			
 				int[] RX = com3.Read();
 				
				if(RX == null)return;
				
				ET1.append(new String(RX, 0, RX.length));
				
 				break;
 				
 			//send
 			case R.id.send1:
 				Log.d(tag,"send start ..."); 
 				
 				CharSequence tx = ET1.getText();
 				
 				int[] text = new int[tx.length()];
 				
                for (i=0; i<tx.length(); i++) 
                {
                        text[i] = tx.charAt(i);
                }

 				
 				com3.Write(text, tx.length());
 				
 				ET1.setText("");
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
	static {
        System.loadLibrary("serialtest");
	}
	
}
