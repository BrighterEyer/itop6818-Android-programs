package lsk.game;

import system.LAGraphicsUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ScreenTest1 extends LAScreen
{
	private LAImage imageBack,tmp_imageBack,imageForward;

	private LAGraphics tmp_graphics;
	
	private int count,rs,cs,col,row,width,height;

	private int blocks[];

	private boolean isEvent;

	public ScreenTest1(String file1,String file2,int row,int col)
	{
		this.col = col;
		this.row = row;
		this.imageBack = LAGraphicsUtils.resizeImage(getLAImage(file1),this.getWidth(),this.getHeight());		
		this.width = imageBack.getWidth();		
		this.height = imageBack.getHeight();
		this.rs = width / row;//分拆后的宽度
		this.cs = height / col;//分拆后的高度
		this.tmp_imageBack = new LAImage(width,height+cs);
		this.tmp_graphics = tmp_imageBack.getLAGraphics();
		this.count = col * row;
		this.blocks = new int[count];
		this.imageForward = getLAImage(file2);
		for(int i = 0;i < count;i++)
		{
			blocks[i] = i;
		}
		rndBlocks();
	}

	//随机生成拼图内容
	private void rndBlocks()
	{
		tmp_graphics.drawImage(imageBack,0,0);
		for(int i = 0;i < (count * row);i++)
		{
			int x1 = (int)((double) row * Math.random());
			int y1 = (int)((double) col * Math.random());
			int x2 = (int)((double) row * Math.random());
			int y2 = (int)((double) col * Math.random());
			copy(x1,y1,0,col);
			copy(x2,y2,x1,y1);
			copy(0,col,x2,y2);
			int j1 = blocks[y1*row + x1];
			blocks[y1*row + x1] = blocks[y2*row + x2];
			blocks[y2*row + x2] = j1;
		}
	}

	//复制拼图中图片块
	private void copy(int x1,int y1,int x2,int y2)
	{
		tmp_graphics.copyArea(x1 * rs,y1 * cs,rs,cs,(x2 - x1)*rs,(y2 - y1)*cs);
	}

	//绘制拼图
	public void draw(LAGraphics g)
	{
		if(!isEvent)//首次加载
		{
			g.drawImage(tmp_imageBack,0,0);
			int i,j;
			for(i=0;i<row;i++)
			{
				for(j=0;j<col;j++)
				{
					g.drawRect(i*rs,j*cs,rs,cs);
				}
			}
		}
		if(isEvent && imageForward != null)
		{
			g.drawImage(imageBack,0,0);
			g.drawImage(imageForward,0,0);
			tmp_graphics.dispose();
		}
	}

	//点击触摸屏
	public boolean onTouchDown(MotionEvent e)
	{
		if(isEvent)
		{
			return isEvent;
		}
		int x = (int)(e.getX() / rs);
		int y = (int)(e.getY() / cs);
		copy(0,0,0,col);
		copy(x,y,0,0);
		copy(0,col,x,y);
		int no = blocks[0];
		blocks[0] = blocks[y * row + x];
		blocks[y * row + x] = no;
		int index;
		for(index = 0;index < count;index++)
		{
			if(blocks[index] != index)
			{
				break;
			}
		}		
		if(index == count)
		{
			isEvent = true;			
		}
		return isEvent;
	}

	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		return false;
	}

	public boolean onKeyUp(int keyCode,KeyEvent e)
	{
		return false;
	}

	public boolean onTouchMove(MotionEvent e)
	{
		return false;
	}

	public boolean onTouchUp(MotionEvent e)
	{
		return false;
	}

	


}