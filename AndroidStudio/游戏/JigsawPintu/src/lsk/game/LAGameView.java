package lsk.game;

import system.LAGraphicsUtils;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//主view
public class LAGameView extends SurfaceView implements SurfaceHolder.Callback
{
	private static final long MAX_INTERVAL = 1000L;//间隔时间

	private static final int fpsX = 5;

	private static final int fpsY = 20;

	private transient int width,height;

	private transient boolean start,isFPS,running;

	private transient long maxFrames,curTime,startTime,offsetTime,curFPS,calcInterval;

	private transient double frameCount;
	
	private LAHandler handler;

	private SurfaceHolder surfaceHolder;

	private CanvasThread canvasThread;
	
	private LAGraphics canvasGraphics;

	private LAImage screenImage;

	private Rect rect;

	public LAGameView(Activity activity)
	{
		this(activity,false);
	}

	public LAGameView(Activity activity,boolean isLandScape)
	{
		super(activity.getApplicationContext());
		LASystem.gc();
		LASystem.setSystemHandler(activity,this);
		this.handler = LASystem.getSystemHandler();
		this.handler.setFullScreen();
		this.handler.setLandScape(isLandScape);
		this.setOnCreateContextMenuListener(handler);
		this.setOnFocusChangeListener(handler);
		this.setOnLongClickListener(handler);
		this.setOnKeyListener(handler);
		this.setOnClickListener(handler);
		this.setOnTouchListener(handler);
		this.screenImage = new LAImage(width = handler.getWidth(),height = handler.getHeight());
		this.rect = new Rect(0,0,width,height);
		System.out.println("width=" + width + ",height=" + height);
		this.canvasThread = new CanvasThread();
		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setSizeFromLayout();
		this.setRunning(true);
		this.setFPS(LASystem.DEFAULT_MAX_FPS);
		this.canvasGraphics = screenImage.getLAGraphics();
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
	}

	class CanvasThread extends Thread
	{
		public void run()
		{
			final LTimerContext timerContext = new LTimerContext();
			timerContext.setMillisTime(startTime = System.currentTimeMillis());
			ILAScreen screen = null;
			Canvas canvas = null;
			do
			{
				if(!start)
				{
					continue;
				}				
				screen = handler.getScreen();
				canvasGraphics.drawClear();
				screen.createUI(canvasGraphics);
				curTime = System.currentTimeMillis();
				timerContext.setSinceLastUpdateTime(curTime-timerContext.getMillisTime());
				timerContext.setMillisSleepTime((offsetTime - timerContext
					.getSinceLastUpdateTime())-timerContext.getMillisOverSleepTime());
				if(timerContext.getMillisSleepTime() > 0)//如果每帧的间隔太短，则休眠时间
				{
					try
					{
						Thread.sleep(timerContext.getMillisSleepTime());
					}catch(InterruptedException e){}
					timerContext.setMillisOverSleepTime((System.currentTimeMillis()) - curTime);
				}else
				{
					timerContext.setMillisOverSleepTime(0L);
				}
				timerContext.setMillisTime(System.currentTimeMillis());
				screen.runTimer(timerContext);
				if(isFPS)
				{
					tickFrames();
					canvasGraphics.setColor(Color.WHITE);
					canvasGraphics.setAntiAlias(true);
					canvasGraphics.drawString(("FPS:"+curFPS).intern(),fpsX,fpsY);
					canvasGraphics.setAntiAlias(false);
				}
				canvas = surfaceHolder.lockCanvas(rect);
				canvas.drawBitmap(screenImage.getBitmap(),0,0,null);
				surfaceHolder.unlockCanvasAndPost(canvas);
				try
				{
					Thread.sleep(30);
				}catch(InterruptedException e){}
				LASystem.gc(10000,1);
			}while(running);
			destroyView();
		}

		
		private void tickFrames()
		{
			frameCount++;
			calcInterval += offsetTime;
			if(calcInterval >= MAX_INTERVAL)//如果计满100帧,就是说跑100次线程为100帧，100帧除以耗时为每秒的帧速率
			{
				long timeNow = System.currentTimeMillis();//获取当前时间
				long realElapsedTime = timeNow - startTime;//获取100帧的间隔时间
				//计算帧速率
				curFPS = (long)((frameCount / realElapsedTime) * MAX_INTERVAL);
				frameCount = 0L;//清空计数器
				calcInterval = 0L;
				startTime = timeNow;//为start重新复制
			}
		}		


	}
	
	
	public void destroyView()
	{
		if(canvasThread != null)
		{
			canvasThread = null;
		}
		LAGraphicsUtils.destroyImages();	
		LASystem.gc();
	}

	public void mainLoop()
	{
		this.handler.getActivity().setContentView(this);
		this.startPaint();
	}

	public void mianStop()
	{
		this.endPaint();
	}
	
	public void startPaint()
	{
		this.start = true;
	}

	public void endPaint()
	{
		this.start = false;
	}

	public Thread getCanvasThread()
	{
		return canvasThread;
	}

	public void setScreen(ILAScreen screen)
	{
		this.handler.setScreen(screen);
	}

	public boolean getRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}	

	public void setFPS(long frames)
	{
		this.maxFrames = frames;
		this.offsetTime = (long)(1.0 / maxFrames * MAX_INTERVAL);
	}

	public long getMaxFPS()
	{
		return this.maxFrames;
	}

	public void setShowFPS(boolean isFPS)
	{
		this.isFPS = isFPS;
	}

	public LAHandler getLHandler()
	{
		return handler;
	}
	
	public void surfaceChanged(SurfaceHolder holder,int format,int width,int height)
	{
		holder.setFixedSize(width,height);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		canvasThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		boolean result = true;
		setRunning(false);
		while(result)
		{
			try
			{
				//canvasThread.join();
				canvasThread = null;
				result = false;
			}catch(Exception e){}		
		}	
	}
	

}