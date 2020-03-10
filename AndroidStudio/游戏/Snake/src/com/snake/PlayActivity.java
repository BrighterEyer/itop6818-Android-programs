package com.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.snake.view.GameView;
import com.snake.view.OnFoodListener;
import com.snake.view.OnStateListener;
import com.snake.view.OnTimeListener;

public class PlayActivity extends Activity {
	/** Called when the activity is first created. */
	GameView gameview;
	boolean menuDisplay = false;
	PopupWindow pw;
	TextView timer;
	TextView foodNumtv;

	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int EXIT = 2;
	
	DalScore dalScore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		init();

		dalScore = new DalScore(this);
	}

	public void init() {
		timer = (TextView) findViewById(R.id.timer);
		foodNumtv = (TextView) findViewById(R.id.foodnum);
		System.out.println("onCreate");
		gameview = (GameView) findViewById(R.id.gameview);
		gameview.setOnStateChangedListener(new OnStateListener() {

			@Override
			public void OnStateChanged(int state) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog;
				switch (state) {
				case GameView.WIN:
					dialog = new AlertDialog.Builder(PlayActivity.this);
					dialog.setTitle("恭喜！！！  得分为：" + gameview.getScore())
							.setPositiveButton("下一关", new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									foodNumtv.setText("0");
									gameview.nextPlay();
								}

							}).setNegativeButton("退出", new OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									exitDialog();
								}
							}).show();
					break;
				case GameView.LOSE:
					if(gameview.isTen){
						showRank();
					}
					if(dialog1==null || !dialog1.isShowing()){
						dialog = new AlertDialog.Builder(PlayActivity.this);
						dialog.setTitle("失败！！！  得分为：" + gameview.getScore())
								.setPositiveButton("重新开始", new OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										foodNumtv.setText("0");
										gameview.playGame();
									}

								}).setNegativeButton("退出", new OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
										exitDialog();
									}
								}).show();
					}
					break;
				}
			}

		});
		gameview.setOnFoodChangedListener(new OnFoodListener() {

			@Override
			public void OnFoodChangedListener(int foodnum) {
				// TODO Auto-generated method stub
				foodNumtv.setText("" + foodnum);
			}

		});
		gameview.setOnTimeChangedListener(new OnTimeListener() {

			@Override
			public void OnTimeChangedListener(int time) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.arg1 = time;
				handler.sendMessage(msg);
			}

		});
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int time = msg.arg1;
			int second = time % 60;
			int minute = time / 60;
			String minuteStr = "";
			String secondStr = "";
			if (minute < 10) {
				minuteStr = "0" + minute;
			} else {
				minuteStr = "" + minute;
			}
			if (second < 10) {
				secondStr = "0" + second;
			} else {
				secondStr = "" + second;
			}
			// System.out.println(minute);
			// System.out.println(second);
			String format = getResources().getString(R.string.time);
			String txt = String.format(format, minuteStr, secondStr);
			timer.setText(txt);
			super.handleMessage(msg);
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gameview.pause();
			exitDialog();
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			gameview.pause();
			openPopwindow();
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			gameview.pause();
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		gameview.playGame();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		gameview.stop();
		super.onDestroy();
	}

	AlertDialog dialog1 = null;
	public void showRank(){
		dialog1 = new AlertDialog.Builder(
				PlayActivity.this).create();
		dialog1.setTitle("排行榜");
		ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		data = preData();
		SimpleAdapter adapter = new SimpleAdapter(
				getApplicationContext(), data, R.layout.list,
				new String[] { "rank", "name", "score", "time" },
				new int[] { R.id.rank, R.id.name, R.id.score,
						R.id.time});
		ListView lv = new ListView(dialog1.getContext());
		lv.setAdapter(adapter);
		dialog1.setView(lv);
		dialog1.setButton("确定", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
			}
			
		});
		dialog1.show();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("onPause");
		gameview.pause();
		super.onPause();
	}

	public void openPopwindow() {
		gameview.pause();
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup view = (ViewGroup) inflater
				.inflate(R.layout.menu, null, true);
		Button play = (Button) view.findViewById(R.id.play);
		Button pause = (Button) view.findViewById(R.id.query);
		Button exit = (Button) view.findViewById(R.id.exit);
		android.view.View.OnClickListener l = new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.play:
					gameview.playGame();
					if (pw != null && pw.isShowing())
						pw.dismiss();
					break;
				case R.id.query:
					gameview.pause();
					showRank();
					break;
				case R.id.exit:
					finish();
					exitDialog();
					break;
				}
			}

		};
		play.setOnClickListener(l);
		pause.setOnClickListener(l);
		exit.setOnClickListener(l);

		pw = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.showAtLocation(findViewById(R.id.playLayout), Gravity.CENTER, 0, 0);
		pw.update();
	}

	protected ArrayList<Map<String, Object>> preData() {
		ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		ArrayList<Score> scores = new ArrayList<Score>();
		scores = dalScore.queryData();
		if(scores==null||scores.size()==0){
			Score s1 = new Score();
			s1.setName("匿名");
			s1.setScore(0);
			s1.setTime(0);
			for(int i = 0 ; i < 10 ; i++ ){
				dalScore.insertData(s1);
//				System.out.println("insert");
			}
			
			scores = dalScore.queryData();
		}
			Map<String,Object> map1 = new HashMap<String, Object>();
			map1.put("rank", "排行");
			map1.put("name", "匿名");
			map1.put("score", "0");	
			map1.put("time", "0");
			data.add(map1);
			
		for(int i = 0 ; i < scores.size() ; i++){
			Map<String,Object> map = new HashMap<String, Object>();
			Score s = scores.get(i);
			map.put("rank", s.getId());
			map.put("name", s.getName());
			map.put("score", s.getScore());
			
			int second = s.getTime() % 60;
			int minute = s.getTime() / 60;
			String minuteStr = "";
			String secondStr = "";
			if (minute < 10) {
				minuteStr = "0" + minute;
			} else {
				minuteStr = "" + minute;
			}
			if (second < 10) {
				secondStr = "0" + second;
			} else {
				secondStr = "" + second;
			}
			String time = minuteStr+"分"+secondStr+"秒";
			
			map.put("time", time);
			data.add(map);
		}
		return data;
	}

	// @Override
	// public boolean onMenuOpened(int featureId, Menu menu) {
	// gameview.pause();
	// return super.onMenuOpened(featureId, menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// super.onOptionsItemSelected(item);
	// switch(item.getItemId()){
	// case PLAY:
	// gameview.playGame();
	// break;
	// case PAUSE:
	// gameview.pause();
	// openPopwindow();
	// break;
	// case EXIT:
	// exitDialog();
	// break;
	// }
	//		
	// return true;
	// }
	public void exitDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(PlayActivity.this);
		dialog.setTitle("小提示").setCancelable(false).setMessage("确定退出游戏？")
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						gameview.stop();
						finish();
					}

				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						gameview.playGame();
					}
				}).show();
	}

}