package com.topeet.ledtest;





import com.topeet.ledtest.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.EditText;

public class MainActivity extends Activity {

	/* add by cym 20140617 */
	led led = new led();
	
	private Button led1_on;
	private Button led1_off;

	/* end add */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* add by cym 20140617 */
		led1_on = (Button)findViewById(R.id.button1);
		led1_off = (Button)findViewById(R.id.button2);

		
		led.Open();
		
		led1_on.setOnClickListener(new manager());
		led1_off.setOnClickListener(new manager());

		/* end add */
	}

	class manager implements OnClickListener{
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				led.Ioctl(0, 1);
				led.Ioctl(1, 1);
				break;
			case R.id.button2:
				led.Ioctl(0, 0);
				led.Ioctl(1, 0);
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
        System.loadLibrary("led");
	}
	/* end add */
}
