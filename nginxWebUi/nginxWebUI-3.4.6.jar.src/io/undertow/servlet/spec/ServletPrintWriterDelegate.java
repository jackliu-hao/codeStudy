/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Locale;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ServletPrintWriterDelegate
/*     */   extends PrintWriter
/*     */ {
/*     */   private ServletPrintWriterDelegate() {
/*  35 */     super((OutputStream)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   private static final Unsafe UNSAFE = getUnsafe();
/*     */   private ServletPrintWriter servletPrintWriter;
/*     */   
/*     */   public static ServletPrintWriterDelegate newInstance(ServletPrintWriter servletPrintWriter) {
/*     */     ServletPrintWriterDelegate delegate;
/*  46 */     if (System.getSecurityManager() == null) {
/*     */       try {
/*  48 */         delegate = (ServletPrintWriterDelegate)UNSAFE.allocateInstance(ServletPrintWriterDelegate.class);
/*  49 */       } catch (InstantiationException e) {
/*  50 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } else {
/*  53 */       delegate = AccessController.<ServletPrintWriterDelegate>doPrivileged(new PrivilegedAction<ServletPrintWriterDelegate>()
/*     */           {
/*     */             public ServletPrintWriterDelegate run() {
/*     */               try {
/*  57 */                 return (ServletPrintWriterDelegate)ServletPrintWriterDelegate.UNSAFE.allocateInstance(ServletPrintWriterDelegate.class);
/*  58 */               } catch (InstantiationException e) {
/*  59 */                 throw new RuntimeException(e);
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*  64 */     delegate.setServletPrintWriter(servletPrintWriter);
/*  65 */     return delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServletPrintWriter(ServletPrintWriter servletPrintWriter) {
/*  71 */     this.servletPrintWriter = servletPrintWriter;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() {
/*  76 */     this.servletPrintWriter.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  81 */     this.servletPrintWriter.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkError() {
/*  86 */     return this.servletPrintWriter.checkError();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int c) {
/*  91 */     this.servletPrintWriter.write(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(char[] buf, int off, int len) {
/*  96 */     this.servletPrintWriter.write(buf, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(char[] buf) {
/* 101 */     this.servletPrintWriter.write(buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(String s, int off, int len) {
/* 106 */     this.servletPrintWriter.write(s, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(String s) {
/* 111 */     this.servletPrintWriter.write((s == null) ? "null" : s);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(boolean b) {
/* 116 */     this.servletPrintWriter.print(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(char c) {
/* 121 */     this.servletPrintWriter.print(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(int i) {
/* 126 */     this.servletPrintWriter.print(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(long l) {
/* 131 */     this.servletPrintWriter.print(l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(float f) {
/* 136 */     this.servletPrintWriter.print(f);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(double d) {
/* 141 */     this.servletPrintWriter.print(d);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(char[] s) {
/* 146 */     this.servletPrintWriter.print(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(String s) {
/* 151 */     this.servletPrintWriter.print(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(Object obj) {
/* 156 */     this.servletPrintWriter.print(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println() {
/* 161 */     this.servletPrintWriter.println();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(boolean x) {
/* 166 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(char x) {
/* 171 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(int x) {
/* 176 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(long x) {
/* 181 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(float x) {
/* 186 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(double x) {
/* 191 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(char[] x) {
/* 196 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(String x) {
/* 201 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(Object x) {
/* 206 */     this.servletPrintWriter.println(x);
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter printf(String format, Object... args) {
/* 211 */     this.servletPrintWriter.printf(format, args);
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter printf(Locale l, String format, Object... args) {
/* 217 */     this.servletPrintWriter.printf(l, format, args);
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter format(String format, Object... args) {
/* 223 */     this.servletPrintWriter.format(format, args);
/* 224 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter format(Locale l, String format, Object... args) {
/* 229 */     this.servletPrintWriter.format(l, format, args);
/* 230 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter append(CharSequence csq) {
/* 235 */     this.servletPrintWriter.append(csq);
/* 236 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter append(CharSequence csq, int start, int end) {
/* 241 */     this.servletPrintWriter.append(csq, start, end);
/* 242 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter append(char c) {
/* 247 */     this.servletPrintWriter.append(c);
/* 248 */     return this;
/*     */   }
/*     */   
/*     */   private static Unsafe getUnsafe() {
/* 252 */     if (System.getSecurityManager() != null) {
/* 253 */       return AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*     */             public Unsafe run() {
/* 255 */               return ServletPrintWriterDelegate.getUnsafe0();
/*     */             }
/*     */           });
/*     */     }
/* 259 */     return getUnsafe0();
/*     */   }
/*     */   
/*     */   private static Unsafe getUnsafe0() {
/*     */     try {
/* 264 */       Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
/* 265 */       theUnsafe.setAccessible(true);
/* 266 */       return (Unsafe)theUnsafe.get(null);
/* 267 */     } catch (Throwable t) {
/* 268 */       throw new RuntimeException("JDK did not allow accessing unsafe", t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletPrintWriterDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */