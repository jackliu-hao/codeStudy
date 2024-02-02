package io.undertow.servlet.spec;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import sun.misc.Unsafe;

public final class ServletPrintWriterDelegate extends PrintWriter {
   private static final Unsafe UNSAFE = getUnsafe();
   private ServletPrintWriter servletPrintWriter;

   private ServletPrintWriterDelegate() {
      super((OutputStream)null);
   }

   public static ServletPrintWriterDelegate newInstance(ServletPrintWriter servletPrintWriter) {
      ServletPrintWriterDelegate delegate;
      if (System.getSecurityManager() == null) {
         try {
            delegate = (ServletPrintWriterDelegate)UNSAFE.allocateInstance(ServletPrintWriterDelegate.class);
         } catch (InstantiationException var3) {
            throw new RuntimeException(var3);
         }
      } else {
         delegate = (ServletPrintWriterDelegate)AccessController.doPrivileged(new PrivilegedAction<ServletPrintWriterDelegate>() {
            public ServletPrintWriterDelegate run() {
               try {
                  return (ServletPrintWriterDelegate)ServletPrintWriterDelegate.UNSAFE.allocateInstance(ServletPrintWriterDelegate.class);
               } catch (InstantiationException var2) {
                  throw new RuntimeException(var2);
               }
            }
         });
      }

      delegate.setServletPrintWriter(servletPrintWriter);
      return delegate;
   }

   public void setServletPrintWriter(ServletPrintWriter servletPrintWriter) {
      this.servletPrintWriter = servletPrintWriter;
   }

   public void flush() {
      this.servletPrintWriter.flush();
   }

   public void close() {
      this.servletPrintWriter.close();
   }

   public boolean checkError() {
      return this.servletPrintWriter.checkError();
   }

   public void write(int c) {
      this.servletPrintWriter.write(c);
   }

   public void write(char[] buf, int off, int len) {
      this.servletPrintWriter.write(buf, off, len);
   }

   public void write(char[] buf) {
      this.servletPrintWriter.write(buf);
   }

   public void write(String s, int off, int len) {
      this.servletPrintWriter.write(s, off, len);
   }

   public void write(String s) {
      this.servletPrintWriter.write(s == null ? "null" : s);
   }

   public void print(boolean b) {
      this.servletPrintWriter.print(b);
   }

   public void print(char c) {
      this.servletPrintWriter.print(c);
   }

   public void print(int i) {
      this.servletPrintWriter.print(i);
   }

   public void print(long l) {
      this.servletPrintWriter.print(l);
   }

   public void print(float f) {
      this.servletPrintWriter.print(f);
   }

   public void print(double d) {
      this.servletPrintWriter.print(d);
   }

   public void print(char[] s) {
      this.servletPrintWriter.print(s);
   }

   public void print(String s) {
      this.servletPrintWriter.print(s);
   }

   public void print(Object obj) {
      this.servletPrintWriter.print(obj);
   }

   public void println() {
      this.servletPrintWriter.println();
   }

   public void println(boolean x) {
      this.servletPrintWriter.println(x);
   }

   public void println(char x) {
      this.servletPrintWriter.println(x);
   }

   public void println(int x) {
      this.servletPrintWriter.println(x);
   }

   public void println(long x) {
      this.servletPrintWriter.println(x);
   }

   public void println(float x) {
      this.servletPrintWriter.println(x);
   }

   public void println(double x) {
      this.servletPrintWriter.println(x);
   }

   public void println(char[] x) {
      this.servletPrintWriter.println(x);
   }

   public void println(String x) {
      this.servletPrintWriter.println(x);
   }

   public void println(Object x) {
      this.servletPrintWriter.println(x);
   }

   public PrintWriter printf(String format, Object... args) {
      this.servletPrintWriter.printf(format, args);
      return this;
   }

   public PrintWriter printf(Locale l, String format, Object... args) {
      this.servletPrintWriter.printf(l, format, args);
      return this;
   }

   public PrintWriter format(String format, Object... args) {
      this.servletPrintWriter.format(format, args);
      return this;
   }

   public PrintWriter format(Locale l, String format, Object... args) {
      this.servletPrintWriter.format(l, format, args);
      return this;
   }

   public PrintWriter append(CharSequence csq) {
      this.servletPrintWriter.append(csq);
      return this;
   }

   public PrintWriter append(CharSequence csq, int start, int end) {
      this.servletPrintWriter.append(csq, start, end);
      return this;
   }

   public PrintWriter append(char c) {
      this.servletPrintWriter.append(c);
      return this;
   }

   private static Unsafe getUnsafe() {
      return System.getSecurityManager() != null ? (Unsafe)AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
         public Unsafe run() {
            return ServletPrintWriterDelegate.getUnsafe0();
         }
      }) : getUnsafe0();
   }

   private static Unsafe getUnsafe0() {
      try {
         Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
         theUnsafe.setAccessible(true);
         return (Unsafe)theUnsafe.get((Object)null);
      } catch (Throwable var1) {
         throw new RuntimeException("JDK did not allow accessing unsafe", var1);
      }
   }
}
