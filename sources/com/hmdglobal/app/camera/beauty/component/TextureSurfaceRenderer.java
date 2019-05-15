package com.hmdglobal.app.camera.beauty.component;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.util.Log;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public abstract class TextureSurfaceRenderer implements Runnable {
    public static String LOG_TAG = TextureSurfaceRenderer.class.getSimpleName();
    private EGL10 egl;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    protected int height;
    private boolean running = false;
    protected final SurfaceTexture surfaceTexture;
    protected int width;

    public abstract void deinitGLComponents();

    public abstract boolean draw();

    public abstract SurfaceTexture getSurfaceTexture();

    public abstract void initGLComponents();

    public TextureSurfaceRenderer(SurfaceTexture surfaceTexture, int width, int height) {
        this.surfaceTexture = surfaceTexture;
        this.width = width;
        this.height = height;
        this.running = true;
        new Thread(this).start();
    }

    private void initEGL() {
        this.egl = (EGL10) EGLContext.getEGL();
        this.eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.egl.eglInitialize(this.eglDisplay, new int[2]);
        EGLConfig eglConfig = chooseEglConfig();
        this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, eglConfig, this.surfaceTexture, null);
        this.eglContext = createContext(this.egl, this.eglDisplay, eglConfig);
        try {
            StringBuilder stringBuilder;
            if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("GL error:");
                stringBuilder.append(GLUtils.getEGLErrorString(this.egl.eglGetError()));
                throw new RuntimeException(stringBuilder.toString());
            } else if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("GL Make current Error");
                stringBuilder.append(GLUtils.getEGLErrorString(this.egl.eglGetError()));
                throw new RuntimeException(stringBuilder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("OpenGL init OK. start draw1...");
        initEGL();
        initGLComponents();
        Log.d(LOG_TAG, "OpenGL init OK. start draw3...");
        while (this.running) {
            if (draw()) {
                this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            }
        }
        deinitGLComponents();
        deinitEGL();
    }

    private void deinitEGL() {
        this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
        this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.egl.eglTerminate(this.eglDisplay);
        Log.d(LOG_TAG, "OpenGL deinit OK.");
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
    }

    private EGLConfig chooseEglConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!this.egl.eglChooseConfig(this.eglDisplay, getAttributes(), configs, 1, configsCount)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to choose config:");
            stringBuilder.append(GLUtils.getEGLErrorString(this.egl.eglGetError()));
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (configsCount[0] > 0) {
            return configs[0];
        } else {
            return null;
        }
    }

    private int[] getAttributes() {
        return new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344};
    }

    public void onPause() {
        this.running = false;
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        super.finalize();
        this.running = false;
    }
}
