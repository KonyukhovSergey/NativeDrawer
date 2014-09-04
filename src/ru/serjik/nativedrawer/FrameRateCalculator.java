package ru.serjik.nativedrawer;

import android.os.SystemClock;

public class FrameRateCalculator
{
	private FrameRateUpdateInterface frameRateUpdateInterface = null;

	private int calcTimePeriod;
	private int framesCount = 0;
	private long lastTime;
	private long frameStartTime;
	private float averageFrameRate = 0;
	private long renderTime = 0;

	private String frameString = new String("calc");

	public FrameRateCalculator(FrameRateUpdateInterface frameRateUpdateInterface, int calcTimePeriod)
	{
		this.frameRateUpdateInterface = frameRateUpdateInterface;
		this.calcTimePeriod = calcTimePeriod;
		lastTime = SystemClock.elapsedRealtime();
	}
	
	public FrameRateCalculator(FrameRateUpdateInterface frameRateUpdateInterface)
	{
		this(frameRateUpdateInterface, 3000);
	}
	
	public FrameRateCalculator()
	{
		this(null);
	}

	public void frameBegin()
	{
		frameStartTime = SystemClock.elapsedRealtime();
	}

	public String frameDone()
	{
		framesCount++;

		long currentTime = SystemClock.elapsedRealtime();
		long period = currentTime - lastTime;
		renderTime += currentTime - frameStartTime;

		if (period > calcTimePeriod)
		{
			averageFrameRate = (float) (framesCount * 1000) / (float) period;

			lastTime = currentTime;

			frameString = String.format("%4.1f %d", averageFrameRate, renderTime / framesCount);

			framesCount = 0;
			renderTime = 0;

			if (frameRateUpdateInterface != null)
			{
				frameRateUpdateInterface.onFrameRateUpdate(this);
			}
		}

		return frameString;
	}

	public float frameRate()
	{
		return averageFrameRate;
	}

	public String frameString()
	{
		return frameString;
	}
	
	public void setFrameRateUpdateInterface(FrameRateUpdateInterface frameRateUpdateInterface)
	{
		this.frameRateUpdateInterface = frameRateUpdateInterface;
	}
	
	public interface FrameRateUpdateInterface
	{
		void onFrameRateUpdate(FrameRateCalculator frameRateCalculator);
	}
}
