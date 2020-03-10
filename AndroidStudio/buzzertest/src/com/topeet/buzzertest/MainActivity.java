package com.topeet.buzzertest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import com.topeet.buzzertesta.R;

class buzzer {
	public native int       Open();
    public native int       Close();
    public native int       Ioctl(int num, int en);
}

public class MainActivity extends Activity {

	/* add by cym 20140618 */
	buzzer buzzer = new buzzer();
	
	private Button start;
	private Button stop;
	/* end add */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* add by cym 20140618 */
		start = (Button)findViewById(R.id.button1);
		stop = (Button)findViewById(R.id.button2);
		
		buzzer.Open();
		
		start.setOnClickListener(new manager());
		stop.setOnClickListener(new manager());
		/* end add */
	}
	
	class manager implements OnClickListener{
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				buzzer.Ioctl(0, 1);
				break;
			case R.id.button2:
				buzzer.Ioctl(0, 0);
				break;
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* add by cym 20140617 */
	static {
        System.loadLibrary("buzzer");
	}
	/* end add */
}
