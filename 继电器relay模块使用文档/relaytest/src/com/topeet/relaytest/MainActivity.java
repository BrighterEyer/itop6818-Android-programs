package com.topeet.relaytest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	relay relay = new relay();
	
	private Button relay_on;
	private Button relay_off;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relay_on = (Button)findViewById(R.id.button1);
        relay_off = (Button)findViewById(R.id.button2);	
        Operation.execRootCmd("chmod  777 /dev/relay_ctl");
        relay.Open();
        
        relay_on.setOnClickListener(new manager());
        relay_off.setOnClickListener(new manager());
    }

    class manager implements OnClickListener{
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				relay.Ioctl(1, 1);
				break;
			case R.id.button2:
				relay.Ioctl(0, 0);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	static {
        System.loadLibrary("relay");
	}
	
}
