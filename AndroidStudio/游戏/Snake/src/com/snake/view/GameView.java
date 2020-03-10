package com.snake.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.snake.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.snake.DalScore;
import com.snake.Score;

public class GameView extends BaseView implements OnTouchListener {

	public static final int WIN = 1;
	public static final int LOSE = 0;
	public static final int PAUSE = 2;
	public static final int PLAY = 3;
	public static final int STOP = 4;

	OnStateListener stateListener = null;
	OnFoodListener foodListener = null;
	OnTimeListener timeListener = null;

	public int delyTime = 300;

	public boolean isTen = false;
	
	RefreshTime refresh;

	int Mode;
	int totalfoodNum = 10;
	int foodNum = 0;
	int level = 1;
	int second = 0;
	int score = 0 ;

	boolean isLose = false;
	boolean isStop = false;

	private GestureDetector gd;

	Point head = new Point();
	Point tail = new Point();
	Point food = new Point();
	List<Point> pathList = new ArrayList<Point>();

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		 this.setOnTouchListener(this);
		 this.setLongClickable(true);
				
		 gd = new GestureDetector(context, new myGestureListener());
		 gd.setIsLongpressEnabled(true);
	}

	public void countScore(int time, int foodNum){
		score += foodNum ;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setOnStateChangedListener(OnStateListener l) {
		stateListener = l;
	}

	public void setMode() {
		stateListener.OnStateChanged(Mode);
	}

	public void setOnFoodChangedListener(OnFoodListener l) {
		foodListener = l;
	}

	public void setFoodNum() {
		foodListener.OnFoodChangedListener(foodNum);
	}

	public void setOnTimeChangedListener(OnTimeListener l) {
		timeListener = l;
	}

	public void win() {
		stop();
		countScore(second, foodNum);
		Mode = WIN;
		setMode();
	}

	class RefreshTime extends Thread {

		@Override
		public void run() {
			while (Mode == PLAY) {
				timeListener.OnTimeChangedListener(second);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				second++;
			}
		}
	}

	public void nextPlay() {
		path.clear();
		pathList.clear();
		level++;
		foodNum = 0;
		second = 0;
		totalfoodNum = totalfoodNum * level;
		initMap();
		Mode = PLAY;
		setMode();
		isStop = false;
		isLose = false;
		handler.post(play);
		refresh = new RefreshTime();
		refresh.start();
	}

	public void playGame() {
		System.out.println("mode" +Mode);
		if (Mode != PAUSE) {
			path.clear();
			pathList.clear();
			initMap();
			foodNum = 0;
			second = 0;
			score = 0;
		}
		Mode = PLAY;
		setMode();
		isTen = false;
		isStop = false;
		isLose = false;
		handler.post(play);
		refresh = new RefreshTime();
		refresh.start();
	}

	public void initMap() {
		pathList.clear();
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				map[x][y] = 0;
			}
		}

		direction = LEFT;

		head.x = xCount / 2;
		head.y = yCount / 2;
		map[head.x][head.y] = 1;
		pathList.add(head);

		tail.x = head.x + 1;
		tail.y = head.y;
		map[tail.x][tail.y] = 3;
		pathList.add(tail);
		System.out.println("init");
		createFood();

//		invalidate();
	}

	public void createFood() {
		Random random = new Random();
		food.x = random.nextInt(xCount - 1);
		food.y = random.nextInt(yCount - 1);
		while (map[food.x][food.y] != 0) {
			food.x = random.nextInt(xCount - 1);
			food.y = random.nextInt(yCount - 1);
		}
		System.out.println("createfood: <" + food.x + "," + food.y + ">");
		map[food.x][food.y] = 5;
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (path.size() != 0) {
//			int x = (int) event.getX();
//			int y = (int) event.getY();
//			Point p = screenToindex(x, y);
//			x = path.get(0).getPoint().x;
//			y = path.get(0).getPoint().y;
//			if (direction == LEFT || direction == RIGHT) {
//				if (p.y >= y) {
//					direction = BOTTOM;
//				} else {
//					direction = TOP;
//				}
//			} else {
//				if (p.x >= x) {
//					direction = RIGHT;
//				} else {
//					direction = LEFT;
//				}
//			}
//		}
//
//		return super.onTouchEvent(event);
//	}

	public void lose() {
//		path.clear();
//		pathList.clear();
		countScore(second, foodNum);
		Mode = LOSE;
		rank();
		handler.removeCallbacks(play);

	}

	public void rank(){
		final DalScore dalScore = new DalScore(getContext());
		ArrayList<Score> scores = dalScore.queryData();
		for(int i = 0 ; i < scores.size() ; i ++){
			int s = scores.get(i).getScore();
			final int id = scores.get(i).getId();
			if(s<score){
				isTen = true;
				final Score ss = new Score();
				ss.setScore(score);
				ss.setTime(second);
				
				final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
				View v = View.inflate(getContext(), R.layout.rankinput, null);
				final EditText et = (EditText) v.findViewById(R.id.nameInput);
				Button btn = (Button) v.findViewById(R.id.submit);
				btn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ss.setName(et.getText().toString());
						if(et.getText().toString().equals("")||et.getText().toString()==null){
							ss.setName("匿名");
						}
						dalScore.insert(id, ss);
						System.out.println(et.getText().toString());
						setMode();
						dialog.dismiss();
					}
					
				});
				
				dialog.setView(v);
				dialog.setTitle("恭喜进入前十！");
				dialog.show();
				return;
			}
		}
		isTen = false;
		setMode();
		
	}
	
	public void stop() {
//		path.clear();
//		pathList.clear();
		Mode = STOP;
		setMode();
		handler.removeCallbacks(play);

	}

	public void pause() {
		Mode = PAUSE;
		setMode();
		handler.removeCallbacks(play);

	}

	Handler handler = new Handler();
	Runnable play = new Runnable() {

		@Override
		public void run() {
			Point b = new Point(100, 100);
			path.clear();
			for (int i = 0; i < pathList.size(); i++) {
				Point p = new Point(pathList.get(i));
				setPath(p, b);
				b.x = p.x;
				b.y = p.y;
			}
			pathList.clear();

			move(path.get(0), direction);
//			System.out.println(0);
			
			if (!isLose) {
				for (int i = 1; i < path.size(); i++) {
//					System.out.println(i);
					int xbefore = path.get(i).getBefore().x;
					int ybefore = path.get(i).getBefore().y;
					
					if (i != path.size() - 1) {
						map[xbefore][ybefore] = 2;
					} else {
						map[xbefore][ybefore] = 3;
					}
					
					int x = path.get(path.size()-1).getPoint().x;
					int y = path.get(path.size()-1).getPoint().y;
					if ((i==path.size()-1)&&x < xCount && x >= 0 && y < yCount && y >= 0) {
						map[x][y] = 0;
					}
					path.get(i).setPoint(path.get(i).getBefore());
					path.get(i).setBefore(path.get(i - 1).getPoint());
					pathList.add(path.get(i).getPoint());
				}

//				GameView.this.invalidate();

				handler.postDelayed(play, delyTime);
			} else {
				System.out.println("stop");
				lose();
			}
//			if (foodNum == totalfoodNum) {
//				win();
//			}
		}

		public synchronized void move(Snake s, int direction) {
			Point p = s.getPoint();
			int x = p.x;
			int y = p.y;
			int temp = map[x][y];
			switch (direction) {
			case LEFT:
				if ((x - 1) >= 0) {
					if (map[x - 1][y] != 0 && map[x - 1][y] != 5) {
						isStop = true;
						isLose = true;
					} else {
						map[x - 1][y] = temp;
//						map[x][y] = 0;
						p.x--;
					}
				} else {
					isStop = true;
					isLose = true;
				}
				break;
			case RIGHT:
				if (x + 1 < xCount) {
					if (map[x + 1][y] != 0 && map[x + 1][y] != 5) {
						isStop = true;
						isLose = true;
					} else {
						map[x + 1][y] = temp;
//						map[x][y] = 0;
						p.x++;
					}
				} else {
					isStop = true;
					isLose = true;
				}
				break;
			case TOP:
				if (y - 1 >= 0) {
					if (map[x][y - 1] != 0 && map[x][y - 1] != 5) {
						isStop = true;
						isLose = true;
					} else {
						map[x][y - 1] = temp;
//						map[x][y] = 0;
						p.y--;
					}
				} else {
					isStop = true;
					isLose = true;
				}
				break;
			case BOTTOM:
				if (y + 1 < yCount) {
					if (map[x][y + 1] != 0 && map[x][y + 1] != 5) {
						isStop = true;
						isLose = true;
					} else {
						map[x][y + 1] = temp;
//						map[x][y] = 0;
						p.y++;
					}
				} else {
					isStop = true;
					isLose = true;
					// System.out.println("8");
				}
				break;
			}
			pathList.add(p);
			 System.out.println("head--->from<"+x+","+y+">to<"+p.x+","+p.y+">");
			 System.out.println("map[x][y]="+map[x][y]);
			 System.out.println("map[p.x][p.y]="+map[p.x][p.y]);
			if (p.x == food.x && p.y == food.y) {
				System.out.println("eat");
				foodNum++;
				setFoodNum();
				createFood();
//				map[++food.x][food.y] = 5;
				Point b1 = path.get(path.size() - 1).getBefore();
				Point p1 = path.get(path.size() - 1).getPoint();
				if (b1.x > p1.x) {
					setPath(new Point(p1.x - 1, p1.y), p1);
				} else if (b1.x < p1.x) {
					setPath(new Point(p1.x + 1, p1.y), p1);
				} else if (b1.y > p1.y) {
					setPath(new Point(p1.x, p1.y - 1), p1);
				} else if (b1.y < p1.y) {
					setPath(new Point(p1.x, p1.y + 1), p1);
				}
			}

		}

	};

	class myGestureListener implements GestureDetector.OnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 10;

		// private static final int SWIPE_MAX_OFF_PATH = 250;

		// private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.GestureDetector$SimpleOnGestureListener#onFling(android
		 *      .view.MotionEvent, android.view.MotionEvent, float, float)
		 *      e1：第1个是 ACTION_DOWN MotionEvent 按下的动作
		 * 
		 * @e2：后一个是ACTION_UP MotionEvent 抬起的动作(这里要看下备注5的解释)
		 * 
		 * @velocityX：X轴上的移动速度，像素/秒
		 * 
		 * @velocityY：Y轴上的移动速度，像素/秒
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// System.out.println("onFling"+direction);
			// System.out.println("e1:<" + e1.getX() + "," + e1.getY() + ">");
			// System.out.println("e2:<" + e2.getX() + "," + e2.getY() + ">");
			if (direction == LEFT || direction == RIGHT) {
				// System.out.println("bt");
				if (e1.getY() - e2.getY() >= SWIPE_MIN_DISTANCE) {
					// && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					direction = TOP;
//					System.out.println("onFling:TOP");
				} else {
					// && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					direction = BOTTOM;
//					System.out.println("onFling:BOTTOM");
				}
			} else {
				// System.out.println("lr");
				if (e1.getX() - e2.getX() >= SWIPE_MIN_DISTANCE) {
					// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					direction = LEFT;
//					System.out.println("onFling:LEFT");
				} else {
					// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					direction = RIGHT;
//					System.out.println("onFling:RIGHT");
				}
			}
			return true;

		}

		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float arg2,
				float arg3) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return false;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gd.onTouchEvent(event);
	}
}
