package lsk.game;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity
{
	private LAGameView view;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		view = new LAGameView(this);
		view.setScreen(new ScreenTest1("backimage1.jpg","over.png",4,4));
		view.setShowFPS(true);		
		view.mainLoop();
	}

	protected void onPause()
	{
		if(view != null)
		{
			view.setRunning(false);
		}
		super.onPause();
	}
	
	protected void onStop()
	{
		if(view != null)
		{
			view.setRunning(false);
		}
		super.onStop();
	}
	
	protected void onDestroy()
	{
		try
		{
			if(view != null)
			{
				view.setRunning(false);
				Thread.sleep(16);
			}
			super.onDestroy();
		}catch(Exception e){}
	}


}