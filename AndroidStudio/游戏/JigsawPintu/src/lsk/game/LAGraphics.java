package lsk.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class LAGraphics
{
	private Bitmap bitmap;

	private Canvas canvas;

	private Paint paint;

	private Rect clip;

	public LAGraphics(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.canvas = new Canvas(bitmap);
		this.canvas.clipRect(0,0,bitmap.getWidth(),bitmap.getHeight());	
		this.canvas.save(Canvas.CLIP_SAVE_FLAG);
		this.paint = new Paint();
		this.clip = canvas.getClipBounds();//单元逻辑映象处理器
	}

	public void drawImage(LAImage img,int x,int y)
	{
		canvas.drawBitmap(img.getBitmap(),x,y,paint);
	}

	public void drawImage(LAImage img,int x,int y,int w,int h,int x1,int y1,int w1,int h1)
	{
		canvas.drawBitmap(img.getBitmap(),new Rect(x1,y1,w1,h1),new Rect(x,y,w,h),paint);
	}

	public void drawLine(int x1,int y1,int x2,int y2)
	{
		if(x1 > x2)
		{
			x1++;
		}else
		{
			x2++;
		}
		if(y1 > y2)
		{
			y1++;
		}else
		{
			y2++;
		}
		canvas.drawLine(x1,y1,x2,y2,paint);
	}
	
	public void drawRect(int x,int y,int width,int height)
	{
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(x,y,x+width,y+height,paint);
	}

	public void drawString(String str,int x,int y)
	{
		canvas.drawText(str,x,y,paint);
	}

	public void drawClear()
	{
		paint.setColor(Color.BLACK);
		canvas.drawColor(Color.BLACK);
	}

	public void fillRect(int x,int y,int width,int height)
	{
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x,y,x + width,y + height,paint);
	}
	
	
	public void copyArea(int x,int y,int width,int height,int dx,int dy)
	{	
		Bitmap copy = Bitmap.createBitmap(bitmap,x,y,width,height);
		canvas.drawBitmap(copy,x+dx,y+dy,null);
	}

	public void clipRect(int x,int y,int width,int height)
	{
		canvas.clipRect(x,y,x + width,y + height);
		clip = canvas.getClipBounds();
	}

	public void setClip(int x,int y,int width,int height)
	{
		if(x == clip.left && x + width == clip.right && y == clip.top
			&& y + height == clip.bottom)
		{
			return;
		}
		if(x < clip.left || x + width > clip.right || y < clip.top || 
			 y + height > clip.bottom)
		{
			canvas.restore();
			canvas.save(Canvas.CLIP_SAVE_FLAG);
		}
		clip.left = x;
		clip.top = y;
		clip.right = x + width;
		clip.bottom = y + height;
		canvas.clipRect(clip);
	}

	//绘制椭圆
	public void drawOval(int x,int y,int width,int height)
	{
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawOval(new RectF(x,y,x + width,y + height),paint);
	}

	//绘制多边形
	public void drawPolygon(int[] xpoints,int[] ypoints,int npoints)
	{
		canvas.drawLine(xpoints[npoints - 1],ypoints[npoints - 1],xpoints[0],ypoints[0],paint);
		int i;
		for(i = 0;i < npoints - 1;i++)
		{
			canvas.drawLine(xpoints[i],ypoints[i],xpoints[i+1],ypoints[i+1],paint);
		}
	}

	public void dispose()
	{
		paint = null;
		canvas = null;
	}
	
	//24-31 位表示 0xff，16-23 位表示红色，8-15 位表示绿色，0-7 位表示蓝色
	public void clearRect(int x,int y,int width,int height)
	{
		canvas.clipRect(x,y,x + width,y + height);
		canvas.drawARGB(0xff,0xff,0xff,0xff);
	}

	public int getClipHeight()
	{
		return clip.bottom - clip.top;
	}

	public int getClipWidth()
	{
		return clip.right - clip.left;
	}

	public int getClipX()
	{
		return clip.left;
	}

	public int getClipY()
	{
		return clip.top;
	}

	public int getColor()
	{
		return paint.getColor();
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public Paint getPaint()
	{
		return paint;
	}

	public Rect getClip()
	{
		return clip;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setAntiAlias(boolean flag)
	{
		paint.setAntiAlias(flag);
	}

	public void setAlphaValue(int alpha)
	{
		paint.setAlpha(alpha);
	}

	//颜色透明度
	public void setAlpha(float alpha)
	{
		setAlphaValue((int)(255 * alpha));
	}
	
	/**用指定的组合 RGB 值创建一种不透明的 sRGB 颜色，此 sRGB 值的 16-23 位表示红色分量，
	 * 8-15 位表示绿色分量，0-7 位表示蓝色分量。绘制时实际使用的颜色取决于从给出的可用于
	 * 特定输出设备的颜色空间中找到的最匹配的颜色。alpha 值的默认值为 255
	 */
	public void setColor(int rgb)
	{
		paint.setColor(rgb);
	}

	

}