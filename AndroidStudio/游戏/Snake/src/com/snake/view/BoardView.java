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
import android.util.Log;
import android.view.View;

import com.snake.R;

public class BoardView extends View {

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
	
	public void setPath(Point p, Point before) {
		Snake s = new Snake();
		s.setBefore(before);
		s.setPoint(p);
		path.add(s);
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		calIconSize();
		Resources r = getResources();
		loadBitmaps(0, r.getDrawable(R.drawable.shake_brick), iconSize);
		loadBitmaps(1, r.getDrawable(R.drawable.shake_head), iconSize);
		loadBitmaps(2, r.getDrawable(R.drawable.shake_body), iconSize - 2);
		loadBitmaps(3, r.getDrawable(R.drawable.shake_tail1), iconSize / 2 + 2);
		loadBitmaps(4, r.getDrawable(R.drawable.shake_tail2), iconSize / 2 - 2);
		loadBitmaps(5, r.getDrawable(R.drawable.shake_food), iconSize / 2 + 4);
	}

	// 计算图片大小
	private void calIconSize() {
		// 获取手机屏幕分辨率的类
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		Log.e("eeee", ""+dm.widthPixels);
		Log.e("eeeeee", ""+((Activity)this.getContext()).getWindowManager().getDefaultDisplay().getWidth());
		Log.e("eeee", ""+dm.heightPixels);
		Log.e("eeeeee", ""+((Activity)this.getContext()).getWindowManager().getDefaultDisplay().getHeight());
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

	@Override
	protected void onDraw(Canvas canvas) {
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
