package ru.serjik.nativedrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import js.jni.code.NativeCalls;

import ru.serjik.nativedrawer.FrameRateCalculator.FrameRateUpdateInterface;
import android.opengl.GLSurfaceView.Renderer;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;

public class Screen implements Renderer, FrameRateUpdateInterface
{
	public ByteBuffer buffer;
	private FloatBuffer quad;
	private int textureWidth;
	private int textureHeight;
	private int textureID;

	public int w, h;
	private boolean isSmooth;

	private FrameRateCalculator frc;

	private int format = GL10.GL_UNSIGNED_BYTE;

	private Random rnd = new Random();

	public Screen(int size, boolean isSmooth)
	{
		textureWidth = size;
		textureHeight = size * 2;
		buffer = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4);
		this.isSmooth = isSmooth;

		quad = ByteBuffer.allocateDirect(4 * (2 + 2) * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

		frc = new FrameRateCalculator(this);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		textureID = createTextureID(gl);

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_FOG);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.v("screen", "w " + width + " h " + height);

		w = textureWidth;
		h = Math.round((float) (height * textureWidth) / (float) width);

		Log.v("pix ", "w " + w + " h " + h);

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, 1, 1, 0, 1, -1);

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);

		if (isSmooth)
		{
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		}
		else
		{
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		}

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, textureWidth, textureHeight, 0, GL10.GL_RGBA, format,
				buffer);

		updateQuad();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		quad.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 16, quad);
		quad.position(2);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 16, quad);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		frc.frameBegin();
		
		//NativeCalls.draw(buffer, w, h);
		
		gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, w, h, GL10.GL_RGBA, format, buffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		frc.frameDone();
	}

	private int createTextureID(GL10 gl)
	{
		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		return ids[0];
	}

	private void updateQuad()
	{
		float r = (float) h / (float) textureHeight;

		quad.position(0);

		quad.put(0);
		quad.put(0);
		quad.put(0);
		quad.put(0);

		quad.put(1);
		quad.put(0);
		quad.put(1);
		quad.put(0);

		quad.put(1);
		quad.put(1);
		quad.put(1);
		quad.put(r);

		quad.put(0);
		quad.put(1);
		quad.put(0);
		quad.put(r);

		quad.position(0);
	}

	@Override
	public void onFrameRateUpdate(FrameRateCalculator frameRateCalculator)
	{
		Log.v("screen", frameRateCalculator.frameString());
	}
}
