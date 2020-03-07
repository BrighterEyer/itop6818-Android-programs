package can.example.app;

import can.hardware.canFrame;
import can.hardware.hardwareControl;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import android.util.Log;

public class canTestActivity extends Activity {
	/** Called when the activity is first created. */
	EditText mReception;
	private ReadThread mReadThread = null;
	private canFrame mcanFrame, scanFrame;
	private int CanId = 123;
	private char[] sendtext;
	private byte[] sendbytes;
	private volatile SharedPreferences prefs;
	private volatile String displayf;
	private volatile String autoclearf;
	private volatile int baudratef;
	byte[] recbytes = new byte[50];

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				mcanFrame = hardwareControl.canRead(scanFrame, 1);
				onDataReceived(mcanFrame);

			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		initSetting();
		initCan();
		mReadThread = new ReadThread();
		mReadThread.start();
	}

	private void initSetting() {
		// TODO Auto-generated method stub
		prefs = getSharedPreferences("can.example.app_preferences",
				MODE_PRIVATE);
		displayf = prefs.getString("DisplayF", "");
		baudratef = Integer.decode(prefs.getString("CANBAUDRATE", "125000"));
		autoclearf = prefs.getString("AutoClear", "");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.can);

		scanFrame = new canFrame();
		mReception = (EditText) findViewById(R.id.canReception);
		EditText Emission = (EditText) findViewById(R.id.canEmission);
		Emission.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				int i, j;
				CharSequence t = v.getText();
				if (displayf.equals("hex")) {
					if (t.length() > 16) {
						Toast.makeText(canTestActivity.this,
								"Each frame of data should less than 8 bytes!",
								Toast.LENGTH_LONG).show();
						j = 16;
					} else {
						j = t.length();
					}
				} else {
					if (t.length() > 8) {
						Toast.makeText(canTestActivity.this,
								"Each frame of data should less than 8 bytes!",
								Toast.LENGTH_LONG).show();
						j = 8;
					} else {
						j = t.length();
					}
				}
				
				if((event != null) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
				{
					switch(event.getAction()){
						case KeyEvent.ACTION_UP:
							sendtext = new char[j];
							for (i = 0; i < j; i++) {
								sendtext[i] = t.charAt(i);
							}

							prepareData();

							//v.setText("");

							if (sendData() == false) {
								Toast.makeText(canTestActivity.this,
										"Please enter 0~9,a~f,A~F,without space.",
										Toast.LENGTH_SHORT).show();
							}
						
							return true;
						default:
							return true;
					}
				}

				return false;
			}
		});

		final Button buttonSetup = (Button) findViewById(R.id.canSet);
		final Button buttonClose = (Button) findViewById(R.id.quit);
		buttonSetup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mReadThread != null)
					while (mReadThread.isAlive())
						mReadThread.interrupt();
				startActivityForResult(new Intent(canTestActivity.this,
						CanPreferences.class), 0);
			}
		});
		buttonClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mReadThread != null)
					while (mReadThread.isAlive())
						mReadThread.interrupt();
				canTestActivity.this.finish();

			}
		});
		initSetting();
		hardwareControl.openCan();
		initCan();
		mReadThread = new ReadThread();
		mReadThread.start();
	}

	protected void onDataReceived(final canFrame mcanFrame2) {
		runOnUiThread(new Runnable() {

			public void run() {
				if (mReception != null) {
					if (mcanFrame2.can_dlc != 0) {
						recData(mcanFrame2);
					}

				}
			}
		});
	}

	private void recData(final canFrame mcanFrame3) {
		int length = mcanFrame3.can_dlc;
		int i = 0;
		if ((autoclearf.equals("yes")) && (mReception.length() > 1000)) {
			mReception.setText(null);
		}
		if (displayf.equals("hex")) {
			String hexStr = "";
			for (i = 0; i < length; i++) {
				// recbytes[i]=(byte) mcanFrame3.data.charAt(i);
				recbytes[i] = (byte) mcanFrame3.recdata[i];
			}
			hexStr = Hex.getHexString(recbytes, mcanFrame3.can_dlc);
			mReception.append("canid:" + String.valueOf(mcanFrame3.can_id)
					+ " length:" + String.valueOf(length) + " data:" + hexStr
					+ "\n");
		} else {
			mReception.append("canid:" + String.valueOf(mcanFrame3.can_id)
					+ " length:" + String.valueOf(length) + " data:");
			mReception.append(new String(mcanFrame3.recdata, 0,
					mcanFrame3.can_dlc) + "\n");
		}
	}

	public void initCan() {
		hardwareControl.initCan(baudratef);
	}

	private void prepareData() {
		if (sendtext != null) {
			if (displayf.equals("hex")) {
				String tmp = new String(sendtext);
				sendbytes = Hex.strToHexBytes(tmp); // dec bytes
			} else {
				sendbytes = new String(sendtext).getBytes();
			}
		} else {
		}
	}

	private boolean sendData() {
		if ((sendtext != null) && (sendbytes != null)) {
			hardwareControl.canWrite(CanId, sendbytes);
			return true;
		} else if ((sendtext != null) && (sendbytes == null)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			while (mReadThread.isAlive())
				// ensure thread
				mReadThread.interrupt();

		super.onDestroy();
	}
}
