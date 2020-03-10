package com.zl.layout;

import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private ToggleButton WebButton=null;
	private TextView WebText=null;
	private ToggleButton GpsButton=null;
	private TextView gpsText=null;
	private SeekBar soundBar=null;
	private SeekBar brightBar=null;
	private ToggleButton VoiceButton=null;
	private MediaPlayer mediaPlayer01;
	public  AudioManager audiomanage;
	private int maxVolume, currentVolume;
	private int volume=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		GpsButton=(ToggleButton)findViewById(R.id.GpsBtn);
		WebButton=(ToggleButton)findViewById(R.id.WebBtn);
		gpsText=(TextView)findViewById(R.id.GpsText);
		WebText=(TextView)findViewById(R.id.WebText);
		soundBar=(SeekBar)findViewById(R.id.soundBar);
		brightBar=(SeekBar)findViewById(R.id.brightBar);
		VoiceButton=(ToggleButton)findViewById(R.id.VoiceBtn);
		VoiceButton.setChecked(true);
		mediaPlayer01=new MediaPlayer();
		audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);   //获取系统最大音量  
		soundBar.setMax(maxVolume);   //拖动条最高值与系统最大声匹配
		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值  
		soundBar.setProgress(currentVolume); 
		soundBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值  
				soundBar.setProgress(currentVolume); 
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		brightBar.setMax(255);
		int normal=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
		brightBar.setProgress(normal);
		brightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int tmpInt=brightBar.getProgress();
				if(tmpInt<80){
					tmpInt=80;
				}
				Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, tmpInt);
				tmpInt = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
				WindowManager.LayoutParams wl = getWindow().getAttributes();
				float tmpFloat = (float) tmpInt / 255; 
				if (tmpFloat > 0 && tmpFloat <= 1) { 
					 wl.screenBrightness = tmpFloat; 
				}
				getWindow().setAttributes(wl);
				
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
		});
		
		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		VoiceButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//				VoiceButton.setChecked(true);
				if(VoiceButton.isChecked()){
					audioManager.setStreamMute(AudioManager.STREAM_MUSIC , false);
				}
				else
				{
					audioManager.setStreamMute(AudioManager.STREAM_MUSIC , true);
				}
				
			}
			
		});
		
		
		
		boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER );
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if(gpsEnabled)
		{
			GpsButton.setChecked(true);
			gpsText.setText("已开启");
		}
		else
		{
			GpsButton.setChecked(false);
			gpsText.setText("已关闭");
		}
		if(info != null && info.isAvailable())
		{
			WebButton.setChecked(true);
			WebText.setText("已连接");
			
		}
		else
		{
			WebButton.setChecked(false);
			WebText.setText("已断开");
		}
		GpsButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent gpsIntent = new Intent(
		                "android.settings.LOCATION_SOURCE_SETTINGS");
				gpsIntent.addCategory(Intent.CATEGORY_DEFAULT);
		        startActivityForResult(gpsIntent,1);
			}
		});
		WebButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				Intent webIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivityForResult( webIntent , 2);
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1)
		{
			boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER );
			if(gpsEnabled)
			{
				GpsButton.setChecked(true);
				gpsText.setText("已开启");
			}
			else
			{
				GpsButton.setChecked(false);
				gpsText.setText("已关闭");
			}
		}
		else if(requestCode==2)
		{
			ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cwjManager.getActiveNetworkInfo();
			if(info != null && info.isAvailable())
			{
				WebButton.setChecked(true);
				WebText.setText("已连接");
				
			}
			else
			{
				WebButton.setChecked(false);
				WebText.setText("已断开");
			}
		}
    }
	
}
