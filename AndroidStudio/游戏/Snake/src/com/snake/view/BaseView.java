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
 * Android�Ĵ�����ؼ����Ǽ̳���View�ģ�������Զ���ؼ�ʱһ��Ҳ�Ǽ̳�View�࣬
 * ���Ƕ��ڸ�Ч�ģ���Ϸ����Ļ�ͼ�������ǲ�������Ҫ��Ƚϸߵĵط�����ͨ��View����е�Բ����ˣ����ʱ���Ҫ�õ�SurfaceView�ࡣ
 * ��Ϊ�Ƚϸ߼�������һ���̳���SurfaceView���ǲ��еģ�����ʵ��һ��SurfaceHolder.Callback�ӿ���ָ��SurfaceView�������ı䡢ɾ��ʱ�Ļص�������
 * ������SurfaceView��ͨ��һ��SurfaceHolder����������SurfaceView��
 * �������SurfaceView��Ϊĳ��Activity��ȫ��View��
 * ��ֱ�ӵ���setContentView(new MyView());�ͺ��ˣ�
 * ��������Ϊ��ĻView��һ���ݣ���Ӧ���޸Ķ�Ӧ��layout XML�ļ��ˣ�������ƵĲ��ִ��룺
    <com.example.MyView
    android:id="@+id/sv"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    />
 * ����com.example.fq.MyViewΪ��Ӧ�Զ������ȫ����
 * 
 * ����Ĭ�ϵ�XML�ļ����������ǵ���View��View(Context , AttributeSet )���캯������View��
 * �������Զ���SurfaceView��ҲӦ����һ������Ϊ(Context , AttributeSet )�Ĺ��캯����
 * �����ڹ��캯����ִ�и���Ķ�Ӧ����super( Context , AttributeSet )��
 * �ڻ�ͼʱ������������Canvas c=holder.lockCanvas();��������û�����
 * �����л��ƣ��ٵ���holder.unlockCanvasAndPost(c);���������ݽ��г���
 */

/*
 * ��������Ϸ����CPU����View�ͱȽϺã����ϱ�׼Android������ʽ����ϵͳ����ˢ��surface��ʱ����
 * ������ܲ��ҵģ���������������ĳ����CPU�����ֻ��ʹ��SurfaceView��ǿ��ˢ��surface�ˣ���Ȼϵͳ��UI���̺ܿ�������������Щ��CPU���߳�.
 * ��Ȼ��ʵ��ֹ�����ַ�����ˢ��Surface�ģ�������ֻ�Ǵ�javaӦ�ñȽϳ����ķ�����
 * SurfaceView��View��ʵ��������ڣ�surfaceView����һ������ĵ����߳��п������»��ƻ����View������UI�����߳��и��»��档
 * ��ô��UI�����߳��и��»��� ���ܻ��������⣬��������»����ʱ���������ô�����UI�̻߳ᱻ�����ڻ��ĺ�����������ô���޷���Ӧ��������������Ϣ��
 * ��ʹ��surfaceView ���������µ��߳��и��»������Բ����������UI���̡߳�����Ҳ����������һ�����⣬�����¼�ͬ���������㴥����һ�£�����ҪsurfaceView�� thread����һ�����Ҫ��һ��event queue�����������touch event��������Ը���һ�㣬��Ϊ�漰���߳�ͬ����
 * ���Ի������ϣ�������Ϸ�ص㣬һ��ֳ����ࡣ
 * 1 �������»���ġ��������࣬������view�ͺ��ˡ���Ϊ����ĸ����������� onTouch �����£�����ֱ��ʹ�� invalidate�� ��Ϊ��������£���һ��Touch����һ�ε�Touch��Ҫ��ʱ��Ƚϳ�Щ���������Ӱ�졣
 * 2 �������¡�����һ������һֱ�ܶ��������Ҫһ��������thread��ͣ���ػ��˵�״̬����������main UI thread��������Ȼview�����ʣ���ҪsurfaceView�����ơ�
 */

/*
 * android���ֻ���back��Ĭ����Ϊ��finish����ǰ̨��Activity�ļ�Activity��״̬ΪDestroy״̬���ٴ�������Activity�Ǵ�onCreate��ʼ�ġ� 
 * ��Home��Ĭ����stopǰ̨��Activity��״̬ΪonStop������Destroy,���ٴ������������Ǵ�OnResume��ʼ�ģ����ᱣ���ϴ�Activityd��״̬�� 
 * back��Ҳ������ģ���back������ر�Activity�ģ����粥�����֣�����back��֮���Կ��Լ����������֣�����Music��֧ap�Ѿ���д��back�����¼����� 
 */
public class BaseView extends SurfaceView implements Callback {

	SurfaceHolder holder ; //���ƶ���
	
	// x��y���ͼƬ��
	int xCount = 16;
	int yCount = 22;

	// ��Ϸ����
	int[][] map ;//= new int[xCount][yCount];
	// ͼƬ��С
	int iconSize;
	int t = 1;
	// ͼƬ
	Bitmap[] icons = new Bitmap[6];
	// �ߵĴ���·��
	List<Snake> path = new ArrayList<Snake>();
	int direction;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int TOP = 2;
	public static final int BOTTOM = 3;
	
	boolean run = false;
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//��ȡholder   
		holder = getHolder();
		holder.addCallback(this);
		
		//��ʼ��ͼƬ��Դ
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
		// SurfaceView�ı�ʱ�Ļص�����

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// SurfaceView����ʱ�Ļص�����
		run = true;
		new DrawThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// SurfaceViewɾ��ʱ�Ļص�����
		run = false;
	}
	
	class DrawThread extends Thread{

		@Override
		public void run() {
			while(run){
				Canvas canvas = holder.lockCanvas(null);//��ȡ����   
				doDraw(canvas);   
				holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ��   
			}
			
		}
	}
	
	public void setPath(Point p, Point before) {
		Snake s = new Snake();
		s.setBefore(before);
		s.setPoint(p);
		path.add(s);
	}
	
	// ����ͼƬ��С
	private void calIconSize() {
		// ��ȡ�ֻ���Ļ�ֱ��ʵ���
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		// ����ֻ��Ŀ���͸߶����ص�λΪpx
		iconSize = dm.widthPixels / (xCount);
		yCount = dm.heightPixels / iconSize ;
		map = new int[xCount][yCount];
		yCount = yCount - 3;
	}

	// ����ͼƬ
	public void loadBitmaps(int key, Drawable d, int w) {
		Bitmap bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, w, w);
		d.draw(canvas);
		icons[key] = bitmap;
	}

	protected void doDraw(Canvas canvas) {
		/**
		 * �������̵�����ͼ��
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
							//��
							canvas.drawBitmap(icons[4], p.x + 2, p.y
									+ iconSize / 4 + 2, null);
							canvas.drawBitmap(icons[3], p.x+iconSize/2, p.y + iconSize / 4 - 1,
									null);
						}
						if (path.get(path.size() - 1).getPoint().x > path.get(
								path.size() - 1).getBefore().x) {
							//��
							canvas.drawBitmap(icons[3], p.x, p.y + iconSize / 4 - 1,
									null);
							canvas.drawBitmap(icons[4], p.x + iconSize / 2 + 1, p.y
									+ iconSize / 4 + 1, null);
						}
						if (path.get(path.size() - 1).getPoint().y < path.get(
								path.size() - 1).getBefore().y) {
							//��
							canvas.drawBitmap(icons[4], p.x + iconSize / 4 + 1, p.y
									+ 1, null);
							canvas.drawBitmap(icons[3], p.x+ iconSize / 4 - 1, p.y + iconSize/2 ,
									null);
						}
						if (path.get(path.size() - 1).getPoint().y > path.get(
								path.size() - 1).getBefore().y) {
							//��
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
