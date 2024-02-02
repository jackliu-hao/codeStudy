package org.h2.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.Thread.State;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Profiler implements Runnable {
   private static Instrumentation instrumentation;
   private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
   private static final int MAX_ELEMENTS = 1000;
   public int interval = 2;
   public int depth = 48;
   public boolean paused;
   public boolean sumClasses;
   public boolean sumMethods;
   private int pid;
   private final String[] ignoreLines = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson.,".split(",");
   private final String[] ignorePackages = "java,sun,com.sun.,com.google.common.,com.mongodb.,org.bson".split(",");
   private final String[] ignoreThreads = "java.lang.Object.wait,java.lang.Thread.dumpThreads,java.lang.Thread.getThreads,java.lang.Thread.sleep,java.lang.UNIXProcess.waitForProcessExit,java.net.PlainDatagramSocketImpl.receive0,java.net.PlainSocketImpl.accept,java.net.PlainSocketImpl.socketAccept,java.net.SocketInputStream.socketRead,java.net.SocketOutputStream.socketWrite,org.eclipse.jetty.io.nio.SelectorManager$SelectSet.doSelect,sun.awt.windows.WToolkit.eventLoop,sun.misc.Unsafe.park,sun.nio.ch.EPollArrayWrapper.epollWait,sun.nio.ch.KQueueArrayWrapper.kevent0,sun.nio.ch.ServerSocketChannelImpl.accept,dalvik.system.VMStack.getThreadStackTrace,dalvik.system.NativeStart.run".split(",");
   private volatile boolean stop;
   private final HashMap<String, Integer> counts = new HashMap();
   private final HashMap<String, Integer> summary = new HashMap();
   private int minCount = 1;
   private int total;
   private Thread thread;
   private long start;
   private long time;
   private int threadDumps;

   public static void premain(String var0, Instrumentation var1) {
      instrumentation = var1;
   }

   public static Instrumentation getInstrumentation() {
      return instrumentation;
   }

   public static void main(String... var0) {
      (new Profiler()).run(var0);
   }

   private void run(String... var1) {
      if (var1.length == 0) {
         System.out.println("Show profiling data");
         System.out.println("Usage: java " + this.getClass().getName() + " <pid> | <stackTraceFileNames>");
         System.out.println("Processes:");
         String var40 = exec("jps", "-l");
         System.out.println(var40);
      } else {
         this.start = System.nanoTime();
         if (var1[0].matches("\\d+")) {
            this.pid = Integer.parseInt(var1[0]);
            long var39 = 0L;

            while(true) {
               long var41;
               do {
                  this.tick();
                  var41 = System.nanoTime();
               } while(var41 - var39 <= TimeUnit.SECONDS.toNanos(5L));

               this.time = System.nanoTime() - this.start;
               System.out.println(this.getTopTraces(3));
               var39 = var41;
            }
         }

         try {
            String[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               if (var5.startsWith("-")) {
                  if ("-classes".equals(var5)) {
                     this.sumClasses = true;
                  } else if ("-methods".equals(var5)) {
                     this.sumMethods = true;
                  } else {
                     if (!"-packages".equals(var5)) {
                        throw new IllegalArgumentException(var5);
                     }

                     this.sumClasses = false;
                     this.sumMethods = false;
                  }
               } else {
                  Path var6 = Paths.get(var5);
                  BufferedReader var7 = Files.newBufferedReader(var6);
                  Throwable var8 = null;

                  try {
                     LineNumberReader var9 = new LineNumberReader(var7);

                     String var10;
                     while((var10 = var9.readLine()) != null) {
                        if (var10.startsWith("Full thread dump")) {
                           ++this.threadDumps;
                        }
                     }
                  } catch (Throwable var36) {
                     var8 = var36;
                     throw var36;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var33) {
                              var8.addSuppressed(var33);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }

                  var7 = Files.newBufferedReader(var6);
                  var8 = null;

                  try {
                     this.processList(readStackTrace(new LineNumberReader(var7)));
                  } catch (Throwable var34) {
                     var8 = var34;
                     throw var34;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var32) {
                              var8.addSuppressed(var32);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }
               }
            }

            System.out.println(this.getTopTraces(5));
         } catch (IOException var38) {
            throw new RuntimeException(var38);
         }
      }
   }

   private static List<Object[]> getRunnableStackTraces() {
      ArrayList var0 = new ArrayList();
      Map var1 = Thread.getAllStackTraces();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Thread var4 = (Thread)var3.getKey();
         if (var4.getState() == State.RUNNABLE) {
            StackTraceElement[] var5 = (StackTraceElement[])var3.getValue();
            if (var5 != null && var5.length != 0) {
               var0.add(var5);
            }
         }
      }

      return var0;
   }

   private static List<Object[]> readRunnableStackTraces(int var0) {
      try {
         String var1 = exec("jstack", Integer.toString(var0));
         LineNumberReader var2 = new LineNumberReader(new StringReader(var1));
         return readStackTrace(var2);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   private static List<Object[]> readStackTrace(LineNumberReader var0) throws IOException {
      ArrayList var1 = new ArrayList();

      while(true) {
         String var2 = var0.readLine();
         if (var2 == null) {
            break;
         }

         if (var2.startsWith("\"")) {
            var2 = var0.readLine();
            if (var2 == null) {
               break;
            }

            var2 = var2.trim();
            if (var2.startsWith("java.lang.Thread.State: RUNNABLE")) {
               ArrayList var3 = new ArrayList();

               while(true) {
                  var2 = var0.readLine();
                  if (var2 == null) {
                     break;
                  }

                  var2 = var2.trim();
                  if (!var2.startsWith("- ")) {
                     if (!var2.startsWith("at ")) {
                        break;
                     }

                     var2 = StringUtils.trimSubstring(var2, 3);
                     var3.add(var2);
                  }
               }

               if (!var3.isEmpty()) {
                  String[] var4 = (String[])var3.toArray(new String[0]);
                  var1.add(var4);
               }
            }
         }
      }

      return var1;
   }

   private static String exec(String... var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         Process var3 = Runtime.getRuntime().exec(var0);
         copyInThread(var3.getInputStream(), var2);
         copyInThread(var3.getErrorStream(), var1);
         var3.waitFor();
         String var4 = Utils10.byteArrayOutputStreamToString(var1, StandardCharsets.UTF_8);
         if (var4.length() > 0) {
            throw new RuntimeException(var4);
         } else {
            return Utils10.byteArrayOutputStreamToString(var2, StandardCharsets.UTF_8);
         }
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   private static void copyInThread(final InputStream var0, final OutputStream var1) {
      (new Thread("Profiler stream copy") {
         public void run() {
            byte[] var1x = new byte[4096];

            try {
               while(true) {
                  int var2 = var0.read(var1x, 0, var1x.length);
                  if (var2 < 0) {
                     return;
                  }

                  var1.write(var1x, 0, var2);
               }
            } catch (Exception var3) {
               throw new RuntimeException(var3);
            }
         }
      }).start();
   }

   public Profiler startCollecting() {
      this.thread = new Thread(this, "Profiler");
      this.thread.setDaemon(true);
      this.thread.start();
      return this;
   }

   public Profiler stopCollecting() {
      this.stop = true;
      if (this.thread != null) {
         try {
            this.thread.join();
         } catch (InterruptedException var2) {
         }

         this.thread = null;
      }

      return this;
   }

   public void run() {
      this.start = System.nanoTime();

      while(!this.stop) {
         try {
            this.tick();
         } catch (Throwable var2) {
            break;
         }
      }

      this.time = System.nanoTime() - this.start;
   }

   private void tick() {
      if (this.interval > 0) {
         if (this.paused) {
            return;
         }

         try {
            Thread.sleep((long)this.interval, 0);
         } catch (Exception var2) {
         }
      }

      List var1;
      if (this.pid != 0) {
         var1 = readRunnableStackTraces(this.pid);
      } else {
         var1 = getRunnableStackTraces();
      }

      ++this.threadDumps;
      this.processList(var1);
   }

   private void processList(List<Object[]> var1) {
      Iterator var2 = var1.iterator();

      while(true) {
         Object[] var3;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (Object[])var2.next();
         } while(startsWithAny(var3[0].toString(), this.ignoreThreads));

         StringBuilder var4 = new StringBuilder();
         String var5 = null;
         boolean var6 = false;
         int var7 = 0;

         for(int var8 = 0; var8 < var3.length && var7 < this.depth; ++var8) {
            String var9 = var3[var8].toString();
            if (!var9.equals(var5) && !startsWithAny(var9, this.ignoreLines)) {
               var5 = var9;
               var4.append("at ").append(var9).append(LINE_SEPARATOR);
               if (!var6 && !startsWithAny(var9, this.ignorePackages)) {
                  var6 = true;

                  int var10;
                  int var11;
                  for(var10 = 0; var10 < var9.length(); ++var10) {
                     var11 = var9.charAt(var10);
                     if (var11 == 40 || Character.isUpperCase((char)var11)) {
                        break;
                     }
                  }

                  if (var10 > 0 && var9.charAt(var10 - 1) == '.') {
                     --var10;
                  }

                  if (this.sumClasses) {
                     var11 = var9.indexOf(46, var10 + 1);
                     var10 = var11 >= 0 ? var11 : var10;
                  }

                  if (this.sumMethods) {
                     var11 = var9.indexOf(40, var10 + 1);
                     var10 = var11 >= 0 ? var11 : var10;
                  }

                  String var12 = var9.substring(0, var10);
                  increment(this.summary, var12, 0);
               }

               ++var7;
            }
         }

         if (var4.length() > 0) {
            this.minCount = increment(this.counts, var4.toString().trim(), this.minCount);
            ++this.total;
         }
      }
   }

   private static boolean startsWithAny(String var0, String[] var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var5.length() > 0 && var0.startsWith(var5)) {
            return true;
         }
      }

      return false;
   }

   private static int increment(HashMap<String, Integer> var0, String var1, int var2) {
      Integer var3 = (Integer)var0.get(var1);
      if (var3 == null) {
         var0.put(var1, 1);
      } else {
         var0.put(var1, var3 + 1);
      }

      while(var0.size() > 1000) {
         Iterator var4 = var0.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            if ((Integer)var5.getValue() <= var2) {
               var4.remove();
            }
         }

         if (var0.size() > 1000) {
            ++var2;
         }
      }

      return var2;
   }

   public String getTop(int var1) {
      this.stopCollecting();
      return this.getTopTraces(var1);
   }

   private String getTopTraces(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("Profiler: top ").append(var1).append(" stack trace(s) of ");
      if (this.time > 0L) {
         var2.append(" of ").append(TimeUnit.NANOSECONDS.toMillis(this.time)).append(" ms");
      }

      if (this.threadDumps > 0) {
         var2.append(" of ").append(this.threadDumps).append(" thread dumps");
      }

      var2.append(":").append(LINE_SEPARATOR);
      if (this.counts.size() == 0) {
         var2.append("(none)").append(LINE_SEPARATOR);
      }

      HashMap var3 = new HashMap(this.counts);
      appendTop(var2, var3, var1, this.total, false);
      var2.append("summary:").append(LINE_SEPARATOR);
      var3 = new HashMap(this.summary);
      appendTop(var2, var3, var1, this.total, true);
      var2.append('.');
      return var2.toString();
   }

   private static void appendTop(StringBuilder var0, HashMap<String, Integer> var1, int var2, int var3, boolean var4) {
      int var5 = 0;
      int var6 = 0;

      while(true) {
         int var7 = 0;
         Map.Entry var8 = null;
         Iterator var9 = var1.entrySet().iterator();

         while(var9.hasNext()) {
            Map.Entry var10 = (Map.Entry)var9.next();
            if ((Integer)var10.getValue() > var7) {
               var8 = var10;
               var7 = (Integer)var10.getValue();
            }
         }

         if (var8 == null) {
            break;
         }

         var1.remove(var8.getKey());
         ++var5;
         if (var5 >= var2) {
            if ((Integer)var8.getValue() < var6) {
               break;
            }

            var6 = (Integer)var8.getValue();
         }

         int var12 = (Integer)var8.getValue();
         int var11 = 100 * var12 / Math.max(var3, 1);
         if (var4) {
            if (var11 > 1) {
               var0.append(var11).append("%: ").append((String)var8.getKey()).append(LINE_SEPARATOR);
            }
         } else {
            var0.append(var12).append('/').append(var3).append(" (").append(var11).append("%):").append(LINE_SEPARATOR).append((String)var8.getKey()).append(LINE_SEPARATOR);
         }
      }

   }
}
