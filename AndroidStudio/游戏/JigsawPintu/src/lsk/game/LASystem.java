package lsk.game;

import java.util.Random;

import android.app.Activity;
import android.view.View;

public class LASystem
{
	public static final Random random = new Random();

	public static final String encoding = "UTF-8";

	public static final int DEFAULT_MAX_FPS = 100;

	private static LAHandler handler;	

	public static void setSystemHandler(Activity activity,View view)
	{
		handler = new LAHandler(activity,view);
	}

	public static LAHandler getSystemHandler()
	{
		return handler;
	}

	//申请回收系统资源
	public static final void gc()
	{
		System.gc();
	}

	//以指定概率使用gc回收系统资源
	public static final void gc(final long rand)
	{
		gc(100,rand);
	}

	//以指定范围内的指定概率执行gc
	public static final void gc(final int size,final long rand)
	{
		if(rand > size)
		{
			throw new RuntimeException(("GC random probability "+ rand + " > " + size).intern());
		}
		if(LASystem.random.nextInt(size) <= rand)
		{
			LASystem.gc();
		}
	}


}