package ru.serjik.nativedrawer;

import js.jni.code.NativeCalls;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class MainActivity extends Activity
{
	private GLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Log.v("test", "res = " + NativeCalls.test(1, 3));

		view = new GLSurfaceView(this);
		view.setRenderer(new Screen(512, true));
		view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		view.postDelayed(updater, 50);

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
