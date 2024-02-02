package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface OpenGL32 extends StdCallLibrary {
   OpenGL32 INSTANCE = (OpenGL32)Native.load("opengl32", OpenGL32.class);

   String glGetString(int var1);

   WinDef.HGLRC wglCreateContext(WinDef.HDC var1);

   WinDef.HGLRC wglGetCurrentContext();

   boolean wglMakeCurrent(WinDef.HDC var1, WinDef.HGLRC var2);

   boolean wglDeleteContext(WinDef.HGLRC var1);

   Pointer wglGetProcAddress(String var1);
}
