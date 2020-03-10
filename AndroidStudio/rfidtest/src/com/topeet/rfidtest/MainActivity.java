package com.topeet.rfidtest;

import com.topeet.rfidtest.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	/****************************************/
	String rxIdCode = "";
	String tag = "spirfid";
	
	private EditText ET1;
	private Button RECV;
	private Button SEND;
	
	rfid cardreader = new rfid();
	
	/****************************************/
	private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	public static String toHexString(byte[] d, int s, int n) {
		final char[] ret = new char[n * 2];
		final int e = s + n;

		int x = 0;
		for (int i = s; i < e; ++i) {
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}
	/****************************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/********************************************/
		ET1 = (EditText)findViewById(R.id.edit1);
        RECV = (Button)findViewById(R.id.recv1);
        SEND = (Button)findViewById(R.id.send1);
        cardreader.Open();	
        //ET1.append( toHexString(ret, 0, ret.length));	 
        //ET1.append("");
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
				byte[] RX = cardreader.ReadCardNum();
		        
				if(RX != null)
				{
					ET1.append( toHexString(RX, 0, RX.length));	
					ET1.append( "\n");
				}
				else
				{
					ET1.append( "no card found!\n");
				}
				break;
				
			//clear
			case R.id.send1:
				Log.d(tag,"clear start ..."); 
	 				
				//CharSequence tx = ET1.getText();
				
				//int[] text = new int[tx.length()];
				
               //for (i=0; i<tx.length(); i++) 
               //{
               //        text[i] = tx.charAt(i);
               //}
				
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
        System.loadLibrary("rfid");
	}
}
