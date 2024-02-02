/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringReader;
/*     */ import java.lang.instrument.Instrumentation;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class Profiler
/*     */   implements Runnable
/*     */ {
/*     */   private static Instrumentation instrumentation;
/*  36 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
/*     */   
/*     */   private static final int MAX_ELEMENTS = 1000;
/*  39 */   public int interval = 2;
/*  40 */   public int depth = 48;
/*     */   
/*     */   public boolean paused;
/*     */   
/*     */   public boolean sumClasses;
/*     */   public boolean sumMethods;
/*     */   private int pid;
/*  47 */   private final String[] ignoreLines = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson.,"
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     .split(",");
/*  55 */   private final String[] ignorePackages = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson"
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  62 */     .split(",");
/*  63 */   private final String[] ignoreThreads = "java.lang.Object.wait,java.lang.Thread.dumpThreads,java.lang.Thread.getThreads,java.lang.Thread.sleep,java.lang.UNIXProcess.waitForProcessExit,java.net.PlainDatagramSocketImpl.receive0,java.net.PlainSocketImpl.accept,java.net.PlainSocketImpl.socketAccept,java.net.SocketInputStream.socketRead,java.net.SocketOutputStream.socketWrite,org.eclipse.jetty.io.nio.SelectorManager$SelectSet.doSelect,sun.awt.windows.WToolkit.eventLoop,sun.misc.Unsafe.park,sun.nio.ch.EPollArrayWrapper.epollWait,sun.nio.ch.KQueueArrayWrapper.kevent0,sun.nio.ch.ServerSocketChannelImpl.accept,dalvik.system.VMStack.getThreadStackTrace,dalvik.system.NativeStart.run"
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
/*  82 */     .split(",");
/*     */   
/*     */   private volatile boolean stop;
/*  85 */   private final HashMap<String, Integer> counts = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   private final HashMap<String, Integer> summary = new HashMap<>();
/*     */   
/*  94 */   private int minCount = 1;
/*     */   
/*     */   private int total;
/*     */   
/*     */   private Thread thread;
/*     */   
/*     */   private long start;
/*     */   
/*     */   private long time;
/*     */   
/*     */   private int threadDumps;
/*     */ 
/*     */   
/*     */   public static void premain(String paramString, Instrumentation paramInstrumentation) {
/* 108 */     instrumentation = paramInstrumentation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Instrumentation getInstrumentation() {
/* 117 */     return instrumentation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String... paramVarArgs) {
/* 128 */     (new Profiler()).run(paramVarArgs);
/*     */   }
/*     */   
/*     */   private void run(String... paramVarArgs) {
/* 132 */     if (paramVarArgs.length == 0) {
/* 133 */       System.out.println("Show profiling data");
/* 134 */       System.out.println("Usage: java " + getClass().getName() + " <pid> | <stackTraceFileNames>");
/*     */       
/* 136 */       System.out.println("Processes:");
/* 137 */       String str = exec(new String[] { "jps", "-l" });
/* 138 */       System.out.println(str);
/*     */       return;
/*     */     } 
/* 141 */     this.start = System.nanoTime();
/* 142 */     if (paramVarArgs[0].matches("\\d+")) {
/* 143 */       this.pid = Integer.parseInt(paramVarArgs[0]);
/* 144 */       long l = 0L;
/*     */       while (true) {
/* 146 */         tick();
/* 147 */         long l1 = System.nanoTime();
/* 148 */         if (l1 - l > TimeUnit.SECONDS.toNanos(5L)) {
/* 149 */           this.time = System.nanoTime() - this.start;
/* 150 */           System.out.println(getTopTraces(3));
/* 151 */           l = l1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     try {
/* 156 */       for (String str : paramVarArgs) {
/* 157 */         if (str.startsWith("-")) {
/* 158 */           if ("-classes".equals(str)) {
/* 159 */             this.sumClasses = true;
/* 160 */           } else if ("-methods".equals(str)) {
/* 161 */             this.sumMethods = true;
/* 162 */           } else if ("-packages".equals(str)) {
/* 163 */             this.sumClasses = false;
/* 164 */             this.sumMethods = false;
/*     */           } else {
/* 166 */             throw new IllegalArgumentException(str);
/*     */           } 
/*     */         } else {
/*     */           
/* 170 */           Path path = Paths.get(str, new String[0]);
/* 171 */           try (BufferedReader null = Files.newBufferedReader(path)) {
/* 172 */             LineNumberReader lineNumberReader = new LineNumberReader(bufferedReader); String str1;
/* 173 */             while ((str1 = lineNumberReader.readLine()) != null) {
/* 174 */               if (str1.startsWith("Full thread dump")) {
/* 175 */                 this.threadDumps++;
/*     */               }
/*     */             } 
/*     */           } 
/* 179 */           try (BufferedReader null = Files.newBufferedReader(path)) {
/* 180 */             processList(readStackTrace(new LineNumberReader(bufferedReader)));
/*     */           } 
/*     */         } 
/* 183 */       }  System.out.println(getTopTraces(5));
/* 184 */     } catch (IOException iOException) {
/* 185 */       throw new RuntimeException(iOException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static List<Object[]> getRunnableStackTraces() {
/* 190 */     ArrayList<StackTraceElement[]> arrayList = new ArrayList();
/* 191 */     Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
/* 192 */     for (Map.Entry<Thread, StackTraceElement> entry : map.entrySet()) {
/* 193 */       Thread thread = (Thread)entry.getKey();
/* 194 */       if (thread.getState() != Thread.State.RUNNABLE) {
/*     */         continue;
/*     */       }
/* 197 */       StackTraceElement[] arrayOfStackTraceElement = (StackTraceElement[])entry.getValue();
/* 198 */       if (arrayOfStackTraceElement == null || arrayOfStackTraceElement.length == 0) {
/*     */         continue;
/*     */       }
/* 201 */       arrayList.add(arrayOfStackTraceElement);
/*     */     } 
/* 203 */     return (List)arrayList;
/*     */   }
/*     */   
/*     */   private static List<Object[]> readRunnableStackTraces(int paramInt) {
/*     */     try {
/* 208 */       String str = exec(new String[] { "jstack", Integer.toString(paramInt) });
/* 209 */       LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(str));
/*     */       
/* 211 */       return readStackTrace(lineNumberReader);
/* 212 */     } catch (IOException iOException) {
/* 213 */       throw new RuntimeException(iOException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<Object[]> readStackTrace(LineNumberReader paramLineNumberReader) throws IOException {
/* 219 */     ArrayList<String[]> arrayList = new ArrayList();
/*     */     while (true) {
/* 221 */       String str = paramLineNumberReader.readLine();
/* 222 */       if (str == null) {
/*     */         break;
/*     */       }
/* 225 */       if (!str.startsWith("\"")) {
/*     */         continue;
/*     */       }
/*     */       
/* 229 */       str = paramLineNumberReader.readLine();
/* 230 */       if (str == null) {
/*     */         break;
/*     */       }
/* 233 */       str = str.trim();
/* 234 */       if (!str.startsWith("java.lang.Thread.State: RUNNABLE")) {
/*     */         continue;
/*     */       }
/* 237 */       ArrayList<String> arrayList1 = new ArrayList();
/*     */       while (true) {
/* 239 */         str = paramLineNumberReader.readLine();
/* 240 */         if (str == null) {
/*     */           break;
/*     */         }
/* 243 */         str = str.trim();
/* 244 */         if (str.startsWith("- ")) {
/*     */           continue;
/*     */         }
/* 247 */         if (!str.startsWith("at ")) {
/*     */           break;
/*     */         }
/* 250 */         str = StringUtils.trimSubstring(str, 3);
/* 251 */         arrayList1.add(str);
/*     */       } 
/* 253 */       if (!arrayList1.isEmpty()) {
/* 254 */         String[] arrayOfString = arrayList1.<String>toArray(new String[0]);
/* 255 */         arrayList.add(arrayOfString);
/*     */       } 
/*     */     } 
/* 258 */     return (List)arrayList;
/*     */   }
/*     */   
/*     */   private static String exec(String... paramVarArgs) {
/* 262 */     ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
/* 263 */     ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
/*     */     try {
/* 265 */       Process process = Runtime.getRuntime().exec(paramVarArgs);
/* 266 */       copyInThread(process.getInputStream(), byteArrayOutputStream2);
/* 267 */       copyInThread(process.getErrorStream(), byteArrayOutputStream1);
/* 268 */       process.waitFor();
/* 269 */       String str = Utils10.byteArrayOutputStreamToString(byteArrayOutputStream1, StandardCharsets.UTF_8);
/* 270 */       if (str.length() > 0) {
/* 271 */         throw new RuntimeException(str);
/*     */       }
/* 273 */       return Utils10.byteArrayOutputStreamToString(byteArrayOutputStream2, StandardCharsets.UTF_8);
/* 274 */     } catch (Exception exception) {
/* 275 */       throw new RuntimeException(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void copyInThread(final InputStream in, final OutputStream out) {
/* 281 */     (new Thread("Profiler stream copy")
/*     */       {
/*     */         public void run() {
/* 284 */           byte[] arrayOfByte = new byte[4096];
/*     */           try {
/*     */             while (true) {
/* 287 */               int i = in.read(arrayOfByte, 0, arrayOfByte.length);
/* 288 */               if (i < 0) {
/*     */                 break;
/*     */               }
/* 291 */               out.write(arrayOfByte, 0, i);
/*     */             } 
/* 293 */           } catch (Exception exception) {
/* 294 */             throw new RuntimeException(exception);
/*     */           } 
/*     */         }
/* 297 */       }).start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profiler startCollecting() {
/* 306 */     this.thread = new Thread(this, "Profiler");
/* 307 */     this.thread.setDaemon(true);
/* 308 */     this.thread.start();
/* 309 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profiler stopCollecting() {
/* 318 */     this.stop = true;
/* 319 */     if (this.thread != null) {
/*     */       try {
/* 321 */         this.thread.join();
/* 322 */       } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */       
/* 325 */       this.thread = null;
/*     */     } 
/* 327 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/* 332 */     this.start = System.nanoTime();
/* 333 */     while (!this.stop) {
/*     */       try {
/* 335 */         tick();
/* 336 */       } catch (Throwable throwable) {
/*     */         break;
/*     */       } 
/*     */     } 
/* 340 */     this.time = System.nanoTime() - this.start;
/*     */   }
/*     */   private void tick() {
/*     */     List<Object[]> list;
/* 344 */     if (this.interval > 0) {
/* 345 */       if (this.paused) {
/*     */         return;
/*     */       }
/*     */       try {
/* 349 */         Thread.sleep(this.interval, 0);
/* 350 */       } catch (Exception exception) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 356 */     if (this.pid != 0) {
/* 357 */       list = readRunnableStackTraces(this.pid);
/*     */     } else {
/* 359 */       list = getRunnableStackTraces();
/*     */     } 
/* 361 */     this.threadDumps++;
/* 362 */     processList(list);
/*     */   }
/*     */   
/*     */   private void processList(List<Object[]> paramList) {
/* 366 */     for (Object[] arrayOfObject : paramList) {
/* 367 */       if (startsWithAny(arrayOfObject[0].toString(), this.ignoreThreads)) {
/*     */         continue;
/*     */       }
/* 370 */       StringBuilder stringBuilder = new StringBuilder();
/*     */       
/* 372 */       String str = null;
/* 373 */       boolean bool = false;
/* 374 */       for (byte b1 = 0, b2 = 0; b2 < arrayOfObject.length && b1 < this.depth; b2++) {
/* 375 */         String str1 = arrayOfObject[b2].toString();
/* 376 */         if (!str1.equals(str) && !startsWithAny(str1, this.ignoreLines)) {
/* 377 */           str = str1;
/* 378 */           stringBuilder.append("at ").append(str1).append(LINE_SEPARATOR);
/* 379 */           if (!bool && !startsWithAny(str1, this.ignorePackages)) {
/* 380 */             bool = true;
/* 381 */             byte b = 0;
/* 382 */             for (; b < str1.length(); b++) {
/* 383 */               char c = str1.charAt(b);
/* 384 */               if (c == '(' || Character.isUpperCase(c)) {
/*     */                 break;
/*     */               }
/*     */             } 
/* 388 */             if (b > 0 && str1.charAt(b - 1) == '.') {
/* 389 */               b--;
/*     */             }
/* 391 */             if (this.sumClasses) {
/* 392 */               int i = str1.indexOf('.', b + 1);
/* 393 */               b = (i >= 0) ? i : b;
/*     */             } 
/* 395 */             if (this.sumMethods) {
/* 396 */               int i = str1.indexOf('(', b + 1);
/* 397 */               b = (i >= 0) ? i : b;
/*     */             } 
/* 399 */             String str2 = str1.substring(0, b);
/* 400 */             increment(this.summary, str2, 0);
/*     */           } 
/* 402 */           b1++;
/*     */         } 
/*     */       } 
/* 405 */       if (stringBuilder.length() > 0) {
/* 406 */         this.minCount = increment(this.counts, stringBuilder.toString().trim(), this.minCount);
/* 407 */         this.total++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean startsWithAny(String paramString, String[] paramArrayOfString) {
/* 413 */     for (String str : paramArrayOfString) {
/* 414 */       if (str.length() > 0 && paramString.startsWith(str)) {
/* 415 */         return true;
/*     */       }
/*     */     } 
/* 418 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int increment(HashMap<String, Integer> paramHashMap, String paramString, int paramInt) {
/* 423 */     Integer integer = paramHashMap.get(paramString);
/* 424 */     if (integer == null) {
/* 425 */       paramHashMap.put(paramString, Integer.valueOf(1));
/*     */     } else {
/* 427 */       paramHashMap.put(paramString, Integer.valueOf(integer.intValue() + 1));
/*     */     } 
/* 429 */     while (paramHashMap.size() > 1000) {
/*     */       
/* 431 */       for (Iterator<Map.Entry> iterator = paramHashMap.entrySet().iterator(); iterator.hasNext(); ) {
/* 432 */         Map.Entry entry = iterator.next();
/* 433 */         if (((Integer)entry.getValue()).intValue() <= paramInt) {
/* 434 */           iterator.remove();
/*     */         }
/*     */       } 
/* 437 */       if (paramHashMap.size() > 1000) {
/* 438 */         paramInt++;
/*     */       }
/*     */     } 
/* 441 */     return paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTop(int paramInt) {
/* 451 */     stopCollecting();
/* 452 */     return getTopTraces(paramInt);
/*     */   }
/*     */   
/*     */   private String getTopTraces(int paramInt) {
/* 456 */     StringBuilder stringBuilder = new StringBuilder();
/* 457 */     stringBuilder.append("Profiler: top ").append(paramInt).append(" stack trace(s) of ");
/* 458 */     if (this.time > 0L) {
/* 459 */       stringBuilder.append(" of ").append(TimeUnit.NANOSECONDS.toMillis(this.time)).append(" ms");
/*     */     }
/* 461 */     if (this.threadDumps > 0) {
/* 462 */       stringBuilder.append(" of ").append(this.threadDumps).append(" thread dumps");
/*     */     }
/* 464 */     stringBuilder.append(":").append(LINE_SEPARATOR);
/* 465 */     if (this.counts.size() == 0) {
/* 466 */       stringBuilder.append("(none)").append(LINE_SEPARATOR);
/*     */     }
/* 468 */     HashMap<String, Integer> hashMap = new HashMap<>(this.counts);
/* 469 */     appendTop(stringBuilder, hashMap, paramInt, this.total, false);
/* 470 */     stringBuilder.append("summary:").append(LINE_SEPARATOR);
/* 471 */     hashMap = new HashMap<>(this.summary);
/* 472 */     appendTop(stringBuilder, hashMap, paramInt, this.total, true);
/* 473 */     stringBuilder.append('.');
/* 474 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void appendTop(StringBuilder paramStringBuilder, HashMap<String, Integer> paramHashMap, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 479 */     byte b = 0; int i = 0; while (true) {
/* 480 */       int j = 0;
/* 481 */       Map.Entry<String, Integer> entry = null;
/* 482 */       for (Map.Entry<String, Integer> entry1 : paramHashMap.entrySet()) {
/* 483 */         if (((Integer)entry1.getValue()).intValue() > j) {
/* 484 */           entry = entry1;
/* 485 */           j = ((Integer)entry1.getValue()).intValue();
/*     */         } 
/*     */       } 
/* 488 */       if (entry == null) {
/*     */         break;
/*     */       }
/* 491 */       paramHashMap.remove(entry.getKey());
/* 492 */       if (++b >= paramInt1) {
/* 493 */         if (((Integer)entry.getValue()).intValue() < i) {
/*     */           break;
/*     */         }
/* 496 */         i = ((Integer)entry.getValue()).intValue();
/*     */       } 
/* 498 */       int k = ((Integer)entry.getValue()).intValue();
/* 499 */       int m = 100 * k / Math.max(paramInt2, 1);
/* 500 */       if (paramBoolean) {
/* 501 */         if (m > 1)
/* 502 */           paramStringBuilder.append(m)
/* 503 */             .append("%: ").append(entry.getKey())
/* 504 */             .append(LINE_SEPARATOR); 
/*     */         continue;
/*     */       } 
/* 507 */       paramStringBuilder.append(k).append('/').append(paramInt2).append(" (")
/* 508 */         .append(m)
/* 509 */         .append("%):").append(LINE_SEPARATOR)
/* 510 */         .append(entry.getKey())
/* 511 */         .append(LINE_SEPARATOR);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Profiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */