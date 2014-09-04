package js.jni.code;

import java.nio.ByteBuffer;

public class NativeCalls
{
	public static native int test(int a,int b);
	
	public static native void draw(ByteBuffer buffer, int width, int height);

	static
	{
		System.loadLibrary("NativeDrawer");
	}
}
