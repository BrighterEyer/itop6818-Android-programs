package com.snake.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

import com.snake.R;

/*
 * Android的大多数控件都是继承自View的，因此在自定义控件时一般也是继承View类，
 * 但是对于高效的，游戏级别的绘图，或者是播放器等要求比较高的地方，普通的View类就有点吃不开了，这个时候就要用到SurfaceView类。
 * 因为比较高级，单纯一个继承自SurfaceView类是不行的，必须实现一个SurfaceHolder.Callback接口来指明SurfaceView创建、改变、删除时的回调方法，
 * 并且在SurfaceView中通过一个SurfaceHolder对象来控制SurfaceView。
 * 如果将该SurfaceView作为某个Activity的全屏View，
 * 则直接调用setContentView(new MyView());就好了；
 * 但若是作为屏幕View的一部份，就应该修改对应的layout XML文件了，添加类似的布局代码：
    <com.example.MyView
    android:id="@+id/sv"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    />
 * 其中com.example.fq.MyView为对应自定义类的全名。
 * 
 * 由于默认的XML文件解析方法是调用View的View(Context , AttributeSet )构造函数构造View，
 * 因此你的自定义SurfaceView中也应该有一个参数为(Context , AttributeSet )的构造函数，
 * 并且在构造函数中执行父类的对应函数super( Context , AttributeSet )。
 * 在绘图时，必须首先用Canvas c=holder.lockCanvas();锁定并获得画布，
 * 随后进行绘制，再调用holder.unlockCanvasAndPost(c);将绘制内容进行呈现
 */

/*
 * 如果你的游戏不吃CPU，用View就比较好，符合标准Android操作方式，由系统决定刷新surface的时机。
 * 但如果很不幸的，你做不到不让你的程序吃CPU，你就只好使用SurfaceView来强制刷新surface了，不然系统的UI进程很可能抢不过你那些吃CPU的线程.
 * 当然其实不止这两种方法来刷新Surface的，这两种只是纯java应用比较常见的方法。
 * SurfaceView和View最本质的区别在于，surfaceView是在一个新起的单独线程中可以重新绘制画面而View必须在UI的主线程中更新画面。
 * 那么在UI的主线程中更新画面 可能会引发问题，比如你更新画面的时间过长，那么你的主UI线程会被你正在画的函数阻塞。那么将无法响应按键，触屏等消息。
 * 当使用surfaceView 由于是在新的线程中更新画面所以不会阻塞你的UI主线程。但这也带来了另外一个问题，就是事件同步。比如你触屏了一下，你需要surfaceView中 thread处理，一般就需要有一个event queue的设计来保存touch event，这会稍稍复杂一点，因为涉及到线程同步。
 * 所以基于以上，根据游戏特点，一般分成两类。
 * 1 被动更新画面的。比如棋类，这种用view就好了。因为画面的更新是依赖于 onTouch 来更新，可以直接使用 invalidate。 因为这种情况下，这一次Touch和下一次的Touch需要的时间比较长些，不会产生影响。
 * 2 主动更新。比如一个人在一直跑动。这就需要一个单独的thread不停的重绘人的状态，避免阻塞main UI thread。所以显然view不合适，需要surfaceView来控制。
 */

/*
 * android的手机的back键默认行为是finish处于前台的Activity的即Activity的状态为Destroy状态，再次启动该Activity是从onCreate开始的。 
 * 而Home键默认是stop前台的Activity即状态为onStop而不是Destroy,若再次启动它，则是从OnResume开始的，即会保持上次Activityd的状态。 
 * back键也有例外的，按back键不会关闭Activity的，比如播放音乐，按了back键之后仍可以继续播放音乐，这是Music这支ap已经重写了back键的事件处理。 
 */
public class BaseView extends SurfaceView implements Callback {

	SurfaceHolder holder ; //控制对象
	
	// x、y轴的图片数
	int xCount = 16;
	int yCount = 22;

	// 游戏棋盘
	int[][] map ;//= new int[xCount][yCount];
	// 图片大小
	int iconSize;
	int t = 1;
	// 图片
	Bitmap[] icons = new Bitmap[6];
	// 蛇的存在路径
	List<Snake> path = new ArrayList<Snake>();
	int direction;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int TOP = 2;
	public static final int BOTTOM = 3;
	
	boolean run = false;
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取holder   
		holder = getHolder();
		holder.addCallback(this);
		
		//初始化图片资源
		calIconSize();
		Resources r = getResources();
		loadBitmaps(0, r.getDrawable(R.drawable.shake_brick), iconSize);
		loadBitmaps(1, r.getDrawable(R.drawable.shake_head), iconSize);
		loadBitmaps(2, r.getDrawable(R.drawable.shake_body), iconSize - 2);
		loadBitmaps(3, r.getDrawable(R.drawable.shake_tail1), iconSize / 2 + 2);
		loadBitmaps(4, r.getDrawable(R.drawable.shake_tail2), iconSize / 2 - 2);
		loadBitmaps(5, r.getDrawable(R.drawable.shake_food), iconSize / 2 + 4);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// SurfaceView改变时的回调方法

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// SurfaceView创建时的回调方法
		run = true;
		new DrawThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// SurfaceView删除时的回调方法
		run = false;
	}
	
	class DrawThread extends Thread{

		@Override
		public void run() {
			while(run){
				Canvas canvas = holder.lockCanvas(null);//获取画布   
				doDraw(canvas);   
				holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像   
			}
			
		}
	}
	
	public void setPath(Point p, Point before) {
		Snake s = new Snake();
		s.setBefore(before);
		s.setPoint(p);
		path.add(s);
	}
	
	// 计算图片大小
	private void calIconSize() {
		// 获取手机屏幕分辨率的类
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		// 获得手机的宽带和高度像素单位为px
		iconSize = dm.widthPixels / (xCount);
		yCount = dm.heightPixels / iconSize ;
		map = new int[xCount][yCount];
		yCount = yCount - 3;
	}

	// 载入图片
	public void loadBitmaps(int key, Drawable d, int w) {
		Bitmap bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, w, w);
		d.draw(canvas);
		icons[key] = bitmap;
	}

	protected void doDraw(Canvas canvas) {
		/**
		 * 绘制棋盘的所有图标
		 */
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				Point p = indextoScreen(x, y);
				canvas.drawBitmap(icons[0], p.x, p.y, null);
				if (map[x][y] == 3) {
					if(path.size()==0){
						canvas.drawBitmap(icons[3], p.x, p.y + iconSize / 4 - 1,
								null);
						canvas.drawBitmap(icons[4], p.x + iconSize / 2 + 1, p.y
								+ iconSize / 4 + 2, null);
					}else{
						if (path.get(path.size() - 1).getPoint().x < path.get(
								path.size() - 1).getBefore().x) {
							//右
							canvas.drawBitmap(icons[4], p.x + 2, p.y
									+ iconSize / 4 + 2, null);
							canvas.drawBitmap(icons[3], p.x+iconSize/2, p.y + iconSize / 4 - 1,
									null);
						}
						if (path.get(path.size() - 1).getPoint().x > path.get(
								path.size() - 1).getBefore().x) {
							//左
							canvas.drawBitmap(icons[3], p.x, p.y + iconSize / 4 - 1,
									null);
							canvas.drawBitmap(icons[4], p.x + iconSize / 2 + 1, p.y
									+ iconSize / 4 + 1, null);
						}
						if (path.get(path.size() - 1).getPoint().y < path.get(
								path.size() - 1).getBefore().y) {
							//下
							canvas.drawBitmap(icons[4], p.x + iconSize / 4 + 1, p.y
									+ 1, null);
							canvas.drawBitmap(icons[3], p.x+ iconSize / 4 - 1, p.y + iconSize/2 ,
									null);
						}
						if (path.get(path.size() - 1).getPoint().y > path.get(
								path.size() - 1).getBefore().y) {
							//上
							canvas.drawBitmap(icons[3], p.x+ iconSize / 4 - 1, p.y ,
									null);
							canvas.drawBitmap(icons[4], p.x  + iconSize / 4 + 2, p.y
									+ 3*iconSize / 4 - 2, null);
						}
					}
				} else if (map[x][y] == 2) {
					canvas.drawBitmap(icons[2], p.x + 1, p.y + 1, null);
				} else if (map[x][y] == 5) {
					canvas.drawBitmap(icons[5], p.x + iconSize / 4 - 2, p.y
							+ iconSize / 4 - 2, null);
				} else if(map[x][y]==1){
					if(direction==RIGHT){
						canvas.drawBitmap(icons[map[x][y]], p.x, p.y, null);
					}else if(direction==TOP){
						canvas.drawBitmap(rotate(icons[map[x][y]], -90, iconSize), p.x, p.y, null);
					}else if(direction==BOTTOM){
						canvas.drawBitmap(rotate(icons[map[x][y]], 90, iconSize), p.x, p.y, null);
					}else{
						canvas.drawBitmap(rotate(icons[map[x][y]], 180, iconSize), p.x, p.y, null);
					}
				}
			}
		}
		for(int x = 0 ; x < xCount ; x++){
			for(int y = yCount+1; y < yCount+3;y++){
				Point p = indextoScreen(x, y);
				canvas.drawBitmap(icons[0], p.x, p.y, null);
			}
		}
	}
	
	public Bitmap rotate(Bitmap bitmap,float degrees,int width){
		// createa matrix for the manipulation  
		Matrix matrix = new Matrix();
		// rotate the Bitmap  
		matrix.postRotate(degrees);
		// recreate the new Bitmap  
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, width, matrix, true);
		return newBitmap;
	}

	public Point indextoScreen(int x, int y) {

		return new Point(x * iconSize, y * iconSize);
	}

	public Point screenToindex(int x, int y) {
		int ix = x / iconSize;
		int iy = y / iconSize;
		if (ix < xCount && iy < yCount) {
			return new Point(ix, iy);
		} else {
			return new Point(0, 0);
		}
	}
}
