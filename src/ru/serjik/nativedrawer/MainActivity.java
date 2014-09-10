package ru.serjik.nativedrawer;

import js.jni.code.NativeCalls;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.util.Log;

public class MainActivity extends Activity
{
	private GLSurfaceView view;
	private Screen screen;
	private volatile boolean isPaused = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.v("test", "res = " + NativeCalls.test(1, 3));

		view = new GLSurfaceView(this);
		screen = new Screen(256, false);
		view.setRenderer(screen);
		view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		view.postDelayed(updater, 50);

		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					if (isPaused)
					{
						SystemClock.sleep(250);
					}
					else
					{
						NativeCalls.draw(screen.buffer, screen.w, screen.h);
						SystemClock.sleep(40);
					}
				}
			}
		});
		t.start();

		setContentView(view);
	}

	private Runnable updater = new Runnable()
	{
		@Override
		public void run()
		{
			view.requestRender();
			view.postDelayed(this, 40);
		}
	};

	@Override
	protected void onPause()
	{
		view.onPause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		view.onResume();
		super.onResume();
	}
}
