package com.example.newadc;



import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	
	/* add by cym 20140618 */
	adc adc = new adc();
	
	private Button read0;
	private Button read1;
	private Button read2;
	private Button read3;
	private Button read4;
	private Button read5;
	private Button read6;
	private Button read7;
	//String rxIdCode = "";
	private EditText ET0;
	private EditText ET1;
	private EditText ET2;
	private EditText ET3;
	private EditText ET4;
	private EditText ET5;
	private EditText ET6;
	private EditText ET7;
	
	private TextView tv;
	
	
	//private String rxIdCode = "";
	private String str;		
	private int[] RfidBuffer=new int[20];
	private int RfidRxCount=0;
	/* end add */
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        /* add by cym 20140618 */
		read0 = (Button)findViewById(R.id.button0);
		ET0 = (EditText)findViewById(R.id.et0);
		read1 = (Button)findViewById(R.id.button1);
		ET1 = (EditText)findViewById(R.id.et1);
		read2 = (Button)findViewById(R.id.button2);
		ET2 = (EditText)findViewById(R.id.et2);
		read3 = (Button)findViewById(R.id.button3);
		ET3 = (EditText)findViewById(R.id.et3);
		read4 = (Button)findViewById(R.id.button4);
		ET4 = (EditText)findViewById(R.id.et4);
		read5 = (Button)findViewById(R.id.button5);
		ET5 = (EditText)findViewById(R.id.et5);
		
		read6 = (Button)findViewById(R.id.button6);
		ET6 = (EditText)findViewById(R.id.et6);
		
		read7 = (Button)findViewById(R.id.button7);
		ET7 = (EditText)findViewById(R.id.et7);
		
		tv=(TextView) findViewById(R.id.tv);
		
		
		read0.setOnClickListener(new manager());
		read1.setOnClickListener(new manager());
		read2.setOnClickListener(new manager());
		read3.setOnClickListener(new manager());
		read4.setOnClickListener(new manager());
		read5.setOnClickListener(new manager());
		read6.setOnClickListener(new manager());		
		read7.setOnClickListener(new manager());
		
		tv.setText("read0获取滑动变阻器的电压值;\nread2获取CPU 的温度；" +
				"\nread34 在端子J38 引出，\nread1567引出到连接器，\n都处于悬空状态。");
		
		/* end add */
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    class manager implements OnClickListener{
		public void onClick(View v) {
			
						
			switch (v.getId()) {
			case R.id.button0:	
				;
				ET0.setText(getOpen(0));
				break;
			case R.id.button1:	
				;
				ET1.setText(getOpen(1));
				break;
			case R.id.button2:	
				;
				ET2.setText(getOpen(2));
				break;
			case R.id.button3:	
				;
				ET3.setText(getOpen(3));
				break;
			case R.id.button4:	
				;
				ET4.setText(getOpen(4));
				break;
			case R.id.button5:	
				;
				ET5.setText(getOpen(5));
				break;
			case R.id.button6:	
				;
				ET6.setText(getOpen(6));
				break;
			case R.id.button7:	
				;
				ET7.setText(getOpen(7));
				break;
			}
			adc.Close();
		}
	}
    
    
    public String getOpen(int num)
    {
    	adc.Open(num);
		int[] RX= adc.Read();
		String rxIdCode="";
		if(RX != null)
		{
			System.arraycopy(RX, 0, RfidBuffer, RfidRxCount, RX.length);
		}
		int i;
		for(i=0; i<5; i++)
		{
			str = Integer.toHexString(RfidBuffer[i]);					
			rxIdCode += str.substring(1);
		}
		return rxIdCode;
    }
    
    /* add by cym 20140617 */
	static {
        System.loadLibrary("newadc");
	}
	/* end add */
}