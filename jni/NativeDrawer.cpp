#include <jni.h>
#include <stdlib.h>
#include <math.h>

extern "C"
{
JNIEXPORT jint JNICALL Java_js_jni_code_NativeCalls_test(JNIEnv *env, jclass, jint a, jint b)
{
	return a + b;
}
JNIEXPORT void JNICALL Java_js_jni_code_NativeCalls_draw(JNIEnv *env, jclass, jobject buffer, jint w, jint h);
}

float rndf()
{
	return (float) (rand() & 0xffffff) / (float) 0xffffff;
}
struct Vector4D
{
	float a;
	float b;
	float c;
	float d;
	void init()
	{
		a = 0;
		b = 0;
		c = 0;
		d = 0;
	}
	void init(float a, float b, float c, float d)
	{
		this->a = a;
		this->b = b;
		this->c = c;
		this->d = d;
	}
	void init(Vector4D &v)
	{
		this->a = v.a;
		this->b = v.b;
		this->c = v.c;
		this->d = v.d;
	}
	void plus(float a, float b, float c, float d)
	{
		this->a += a;
		this->b += b;
		this->c += c;
		this->d += d;
	}
	void plus(Vector4D &v)
	{
		this->a += v.a;
		this->b += v.b;
		this->c += v.c;
		this->d += v.d;
	}
	void plus(Vector4D &v, float s)
	{
		this->a += v.a * s;
		this->b += v.b * s;
		this->c += v.c * s;
		this->d += v.d * s;
	}
	void scale(float s)
	{
		a *= s;
		b *= s;
		c *= s;
		d *= s;
	}
	void minus(Vector4D &a, Vector4D &b)
	{
		this->a = a.a - b.a;
		this->b = a.b - b.b;
		this->c = a.c - b.c;
		this->d = a.d - b.d;
	}
	float quadLen()
	{
		return a * a + b * b + c * c + d * d;
	}
};

struct StrangeState
{
	Vector4D p;
	Vector4D e;
	Vector4D v;
	Vector4D d;
	void init()
	{
		p.init(-0.966918f, 2.879879f, 0.765145f, 0.744728f);
		e.init(p);
		v.init();
	}
	void tick()
	{
		d.minus(e, p);
		if (d.quadLen() < 0.0001)
		{
			e.init(-0.966918f - 0.75f + 0.9f * rndf(), 2.879879f - 0.0f + 1.0f * rndf(),
					0.765145f - 1.7f + 1.6f * rndf(), 0.744728f - 0.5f + 0.6f * rndf());
			//__android_log_print(ANDROID_LOG_VERBOSE, "StrangeAttractor", "new position");
		}
		v.plus(d, 0.01);
		p.plus(v, 0.01);
		v.scale(0.99);
	}
};
StrangeState s;
bool firstCall = true;
float time = 0;
int length = 1024 * 16;

int color(float r, float g, float b)
{
	return ((int) (b * 255) << 16) | ((int) (g * 255) << 8) | ((int) (r * 255));
}


struct Screen
{
	int w, h;
	unsigned char* s;

	void init(unsigned char* s, int w, int h)
	{
		this->s = s;
		this->w = w;
		this->h = h;
	}

	void set(int x, int y, int c)
	{
		*((int*) s + y * w + x) = c;
	}

	void set(int x, int y, unsigned char r, unsigned char g, unsigned char b)
	{
		unsigned char* p = s + ((x + y * w) << 2);
		*p = ((short) *p * 3 + (short) r) >> 2;
		p++;
		*p = ((short) *p * 3 + (short) g) >> 2;
		p++;
		*p = ((short) *p * 3 + (short) b) >> 2;
		p++;
	}
	void fade(unsigned char v)
	{
		unsigned char* pos = s, *end = s + w * h * 4;

		while (pos < end)
		{
			*pos = *pos >= v ? *pos - v : 0;
			pos++;
			*pos = *pos >= v ? *pos - v : 0;
			pos++;
			*pos = *pos >= v ? *pos - v : 0;
			pos++;
			pos++;
		}
	}
};

Screen scr;
float pi = 3.1415962f;

JNIEXPORT void JNICALL Java_js_jni_code_NativeCalls_draw(JNIEnv *env, jclass, jobject buffer, jint w, jint h)
{
	unsigned char * scrp = (unsigned char*) env->GetDirectBufferAddress(buffer);

	scr.init(scrp, w, h);

	if (firstCall)
	{
		s.init();
		firstCall = false;
	}
	else
	{
		s.tick();
	}
	float x = 10.0f;
	float y = 10.0f;

	time += 0.01;

	unsigned char r, g, b;

	int c = color(0.5f + 0.5f * sinf(time), 0.5f + 0.5f * sinf(time + 0.667f * pi),
			0.5f + 0.5f * sinf(time + 0.667f * 2.0f * pi));
	r = c & 0xff;
	g = (c >> 8) & 0xff;
	b = (c >> 16) & 0xff;

	for (int i = 0; i < length; i++)
	{
		float xnew = sinf(y * s.p.b) + s.p.c * sinf(x * s.p.b);
		float ynew = sinf(x * s.p.a) + s.p.d * sinf(y * s.p.a);
		x = xnew;
		y = ynew;

		//scr.set((x + 2) * w * 0.25f, (y + 2) * h * 0.25f, c);
		scr.set((x + 2) * w * 0.25f, (y + 2) * h * 0.25f, r, g, b);

	}

	scr.fade(1);
}
