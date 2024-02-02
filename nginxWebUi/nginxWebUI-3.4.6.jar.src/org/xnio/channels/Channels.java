/*      */ package org.xnio.channels;
/*      */ 
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOError;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Locale;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.Option;
/*      */ import org.xnio.XnioIoThread;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Channels
/*      */ {
/*      */   private static final FileChannel NULL_FILE_CHANNEL;
/*      */   
/*      */   public static void flushBlocking(SuspendableWriteChannel channel) throws IOException {
/*   63 */     while (!channel.flush()) {
/*   64 */       channel.awaitWritable();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean flushBlocking(SuspendableWriteChannel channel, long time, TimeUnit unit) throws IOException {
/*   82 */     if (channel.flush()) {
/*   83 */       return true;
/*      */     }
/*   85 */     long remaining = unit.toNanos(time);
/*   86 */     long now = System.nanoTime();
/*      */     
/*      */     while (true) {
/*   89 */       channel.awaitWritable(remaining, TimeUnit.NANOSECONDS);
/*      */ 
/*      */       
/*   92 */       if (channel.flush()) {
/*   93 */         return true;
/*      */       }
/*      */ 
/*      */       
/*   97 */       if ((remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) <= 0L) {
/*   98 */         return false;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void shutdownWritesBlocking(SuspendableWriteChannel channel) throws IOException {
/*  111 */     channel.shutdownWrites();
/*  112 */     flushBlocking(channel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean shutdownWritesBlocking(SuspendableWriteChannel channel, long time, TimeUnit unit) throws IOException {
/*  128 */     channel.shutdownWrites();
/*  129 */     return flushBlocking(channel, time, unit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.WritableByteChannel & SuspendableWriteChannel> int writeBlocking(C channel, ByteBuffer buffer) throws IOException {
/*  145 */     int t = 0;
/*  146 */     while (buffer.hasRemaining()) {
/*  147 */       int res = channel.write(buffer);
/*  148 */       if (res == 0) {
/*  149 */         ((SuspendableWriteChannel)channel).awaitWritable(); continue;
/*      */       } 
/*  151 */       t += res;
/*      */     } 
/*      */     
/*  154 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.WritableByteChannel & SuspendableWriteChannel> int writeBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
/*  171 */     long remaining = unit.toNanos(time);
/*  172 */     long now = System.nanoTime();
/*  173 */     int t = 0;
/*  174 */     while (buffer.hasRemaining() && remaining > 0L) {
/*  175 */       int res = channel.write(buffer);
/*  176 */       if (res == 0) {
/*  177 */         ((SuspendableWriteChannel)channel).awaitWritable(remaining, TimeUnit.NANOSECONDS);
/*  178 */         remaining -= Math.max(-now + (now = System.nanoTime()), 0L);
/*      */       } 
/*  180 */       t += res;
/*      */     } 
/*      */     
/*  183 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.GatheringByteChannel & SuspendableWriteChannel> long writeBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
/*  200 */     long t = 0L;
/*  201 */     while (Buffers.hasRemaining((Buffer[])buffers, offs, len)) {
/*  202 */       long res = channel.write(buffers, offs, len);
/*  203 */       if (res == 0L) {
/*  204 */         ((SuspendableWriteChannel)channel).awaitWritable(); continue;
/*      */       } 
/*  206 */       t += res;
/*      */     } 
/*      */     
/*  209 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.GatheringByteChannel & SuspendableWriteChannel> long writeBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
/*  228 */     long remaining = unit.toNanos(time);
/*  229 */     long now = System.nanoTime();
/*  230 */     long t = 0L;
/*  231 */     while (Buffers.hasRemaining((Buffer[])buffers, offs, len) && remaining > 0L) {
/*  232 */       long res = channel.write(buffers, offs, len);
/*  233 */       if (res == 0L) {
/*  234 */         ((SuspendableWriteChannel)channel).awaitWritable(remaining, TimeUnit.NANOSECONDS);
/*  235 */         remaining -= Math.max(-now + (now = System.nanoTime()), 0L);
/*      */       } 
/*  237 */       t += res;
/*      */     } 
/*      */     
/*  240 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends WritableMessageChannel> void sendBlocking(C channel, ByteBuffer buffer) throws IOException {
/*  253 */     while (!channel.send(buffer)) {
/*  254 */       channel.awaitWritable();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends WritableMessageChannel> boolean sendBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload #4
/*      */     //   2: lload_2
/*      */     //   3: invokevirtual toNanos : (J)J
/*      */     //   6: lstore #5
/*      */     //   8: invokestatic nanoTime : ()J
/*      */     //   11: lstore #7
/*      */     //   13: lload #5
/*      */     //   15: lconst_0
/*      */     //   16: lcmp
/*      */     //   17: ifle -> 65
/*      */     //   20: aload_0
/*      */     //   21: aload_1
/*      */     //   22: invokeinterface send : (Ljava/nio/ByteBuffer;)Z
/*      */     //   27: ifne -> 63
/*      */     //   30: aload_0
/*      */     //   31: lload #5
/*      */     //   33: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   36: invokeinterface awaitWritable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   41: lload #5
/*      */     //   43: lload #7
/*      */     //   45: lneg
/*      */     //   46: invokestatic nanoTime : ()J
/*      */     //   49: dup2
/*      */     //   50: lstore #7
/*      */     //   52: ladd
/*      */     //   53: lconst_0
/*      */     //   54: invokestatic max : (JJ)J
/*      */     //   57: lsub
/*      */     //   58: lstore #5
/*      */     //   60: goto -> 13
/*      */     //   63: iconst_1
/*      */     //   64: ireturn
/*      */     //   65: iconst_0
/*      */     //   66: ireturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #272	-> 0
/*      */     //   #273	-> 8
/*      */     //   #274	-> 13
/*      */     //   #275	-> 20
/*      */     //   #276	-> 30
/*      */     //   #277	-> 41
/*      */     //   #279	-> 63
/*      */     //   #282	-> 65
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	67	0	channel	Lorg/xnio/channels/WritableMessageChannel;
/*      */     //   0	67	1	buffer	Ljava/nio/ByteBuffer;
/*      */     //   0	67	2	time	J
/*      */     //   0	67	4	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   8	59	5	remaining	J
/*      */     //   13	54	7	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	67	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends WritableMessageChannel> void sendBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
/*  297 */     while (!channel.send(buffers, offs, len)) {
/*  298 */       channel.awaitWritable();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends WritableMessageChannel> boolean sendBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload #6
/*      */     //   2: lload #4
/*      */     //   4: invokevirtual toNanos : (J)J
/*      */     //   7: lstore #7
/*      */     //   9: invokestatic nanoTime : ()J
/*      */     //   12: lstore #9
/*      */     //   14: lload #7
/*      */     //   16: lconst_0
/*      */     //   17: lcmp
/*      */     //   18: ifle -> 68
/*      */     //   21: aload_0
/*      */     //   22: aload_1
/*      */     //   23: iload_2
/*      */     //   24: iload_3
/*      */     //   25: invokeinterface send : ([Ljava/nio/ByteBuffer;II)Z
/*      */     //   30: ifne -> 66
/*      */     //   33: aload_0
/*      */     //   34: lload #7
/*      */     //   36: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   39: invokeinterface awaitWritable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   44: lload #7
/*      */     //   46: lload #9
/*      */     //   48: lneg
/*      */     //   49: invokestatic nanoTime : ()J
/*      */     //   52: dup2
/*      */     //   53: lstore #9
/*      */     //   55: ladd
/*      */     //   56: lconst_0
/*      */     //   57: invokestatic max : (JJ)J
/*      */     //   60: lsub
/*      */     //   61: lstore #7
/*      */     //   63: goto -> 14
/*      */     //   66: iconst_1
/*      */     //   67: ireturn
/*      */     //   68: iconst_0
/*      */     //   69: ireturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #318	-> 0
/*      */     //   #319	-> 9
/*      */     //   #320	-> 14
/*      */     //   #321	-> 21
/*      */     //   #322	-> 33
/*      */     //   #323	-> 44
/*      */     //   #325	-> 66
/*      */     //   #328	-> 68
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	70	0	channel	Lorg/xnio/channels/WritableMessageChannel;
/*      */     //   0	70	1	buffers	[Ljava/nio/ByteBuffer;
/*      */     //   0	70	2	offs	I
/*      */     //   0	70	3	len	I
/*      */     //   0	70	4	time	J
/*      */     //   0	70	6	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   9	61	7	remaining	J
/*      */     //   14	56	9	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	70	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableByteChannel & SuspendableReadChannel> int readBlocking(C channel, ByteBuffer buffer) throws IOException {
/*      */     int res;
/*  344 */     while ((res = channel.read(buffer)) == 0 && buffer.hasRemaining()) {
/*  345 */       ((SuspendableReadChannel)channel).awaitReadable();
/*      */     }
/*  347 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableByteChannel & SuspendableReadChannel> int readBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   7: istore #5
/*      */     //   9: iload #5
/*      */     //   11: ifeq -> 17
/*      */     //   14: iload #5
/*      */     //   16: ireturn
/*      */     //   17: aload #4
/*      */     //   19: lload_2
/*      */     //   20: invokevirtual toNanos : (J)J
/*      */     //   23: lstore #6
/*      */     //   25: invokestatic nanoTime : ()J
/*      */     //   28: lstore #8
/*      */     //   30: aload_1
/*      */     //   31: invokevirtual hasRemaining : ()Z
/*      */     //   34: ifeq -> 97
/*      */     //   37: lload #6
/*      */     //   39: lconst_0
/*      */     //   40: lcmp
/*      */     //   41: ifle -> 97
/*      */     //   44: aload_0
/*      */     //   45: checkcast org/xnio/channels/SuspendableReadChannel
/*      */     //   48: lload #6
/*      */     //   50: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   53: invokeinterface awaitReadable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   58: aload_0
/*      */     //   59: aload_1
/*      */     //   60: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*      */     //   65: istore #5
/*      */     //   67: iload #5
/*      */     //   69: ifeq -> 75
/*      */     //   72: iload #5
/*      */     //   74: ireturn
/*      */     //   75: lload #6
/*      */     //   77: lload #8
/*      */     //   79: lneg
/*      */     //   80: invokestatic nanoTime : ()J
/*      */     //   83: dup2
/*      */     //   84: lstore #8
/*      */     //   86: ladd
/*      */     //   87: lconst_0
/*      */     //   88: invokestatic max : (JJ)J
/*      */     //   91: lsub
/*      */     //   92: lstore #6
/*      */     //   94: goto -> 30
/*      */     //   97: iload #5
/*      */     //   99: ireturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #365	-> 0
/*      */     //   #366	-> 9
/*      */     //   #367	-> 14
/*      */     //   #369	-> 17
/*      */     //   #370	-> 25
/*      */     //   #371	-> 30
/*      */     //   #373	-> 44
/*      */     //   #376	-> 58
/*      */     //   #377	-> 67
/*      */     //   #378	-> 72
/*      */     //   #382	-> 75
/*      */     //   #384	-> 97
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	100	0	channel	Ljava/nio/channels/ReadableByteChannel;
/*      */     //   0	100	1	buffer	Ljava/nio/ByteBuffer;
/*      */     //   0	100	2	time	J
/*      */     //   0	100	4	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   9	91	5	res	I
/*      */     //   25	75	6	remaining	J
/*      */     //   30	70	8	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	100	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.ScatteringByteChannel & SuspendableReadChannel> long readBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
/*      */     long res;
/*  402 */     while ((res = channel.read(buffers, offs, len)) == 0L) {
/*  403 */       ((SuspendableReadChannel)channel).awaitReadable();
/*      */     }
/*  405 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends java.nio.channels.ScatteringByteChannel & SuspendableReadChannel> long readBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: iload_2
/*      */     //   3: iload_3
/*      */     //   4: invokeinterface read : ([Ljava/nio/ByteBuffer;II)J
/*      */     //   9: lstore #7
/*      */     //   11: lload #7
/*      */     //   13: lconst_0
/*      */     //   14: lcmp
/*      */     //   15: ifeq -> 21
/*      */     //   18: lload #7
/*      */     //   20: lreturn
/*      */     //   21: aload #6
/*      */     //   23: lload #4
/*      */     //   25: invokevirtual toNanos : (J)J
/*      */     //   28: lstore #9
/*      */     //   30: invokestatic nanoTime : ()J
/*      */     //   33: lstore #11
/*      */     //   35: aload_1
/*      */     //   36: iload_2
/*      */     //   37: iload_3
/*      */     //   38: invokestatic hasRemaining : ([Ljava/nio/Buffer;II)Z
/*      */     //   41: ifeq -> 108
/*      */     //   44: lload #9
/*      */     //   46: lconst_0
/*      */     //   47: lcmp
/*      */     //   48: ifle -> 108
/*      */     //   51: aload_0
/*      */     //   52: checkcast org/xnio/channels/SuspendableReadChannel
/*      */     //   55: lload #9
/*      */     //   57: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   60: invokeinterface awaitReadable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   65: aload_0
/*      */     //   66: aload_1
/*      */     //   67: iload_2
/*      */     //   68: iload_3
/*      */     //   69: invokeinterface read : ([Ljava/nio/ByteBuffer;II)J
/*      */     //   74: lstore #7
/*      */     //   76: lload #7
/*      */     //   78: lconst_0
/*      */     //   79: lcmp
/*      */     //   80: ifeq -> 86
/*      */     //   83: lload #7
/*      */     //   85: lreturn
/*      */     //   86: lload #9
/*      */     //   88: lload #11
/*      */     //   90: lneg
/*      */     //   91: invokestatic nanoTime : ()J
/*      */     //   94: dup2
/*      */     //   95: lstore #11
/*      */     //   97: ladd
/*      */     //   98: lconst_0
/*      */     //   99: invokestatic max : (JJ)J
/*      */     //   102: lsub
/*      */     //   103: lstore #9
/*      */     //   105: goto -> 35
/*      */     //   108: lload #7
/*      */     //   110: lreturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #426	-> 0
/*      */     //   #427	-> 11
/*      */     //   #428	-> 18
/*      */     //   #430	-> 21
/*      */     //   #431	-> 30
/*      */     //   #432	-> 35
/*      */     //   #434	-> 51
/*      */     //   #437	-> 65
/*      */     //   #438	-> 76
/*      */     //   #439	-> 83
/*      */     //   #443	-> 86
/*      */     //   #445	-> 108
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	111	0	channel	Ljava/nio/channels/ScatteringByteChannel;
/*      */     //   0	111	1	buffers	[Ljava/nio/ByteBuffer;
/*      */     //   0	111	2	offs	I
/*      */     //   0	111	3	len	I
/*      */     //   0	111	4	time	J
/*      */     //   0	111	6	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   11	100	7	res	J
/*      */     //   30	81	9	remaining	J
/*      */     //   35	76	11	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	111	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableMessageChannel> int receiveBlocking(C channel, ByteBuffer buffer) throws IOException {
/*      */     int res;
/*  461 */     while ((res = channel.receive(buffer)) == 0) {
/*  462 */       channel.awaitReadable();
/*      */     }
/*  464 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableMessageChannel> int receiveBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: invokeinterface receive : (Ljava/nio/ByteBuffer;)I
/*      */     //   7: istore #5
/*      */     //   9: iload #5
/*      */     //   11: ifeq -> 17
/*      */     //   14: iload #5
/*      */     //   16: ireturn
/*      */     //   17: aload #4
/*      */     //   19: lload_2
/*      */     //   20: invokevirtual toNanos : (J)J
/*      */     //   23: lstore #6
/*      */     //   25: invokestatic nanoTime : ()J
/*      */     //   28: lstore #8
/*      */     //   30: aload_1
/*      */     //   31: invokevirtual hasRemaining : ()Z
/*      */     //   34: ifeq -> 94
/*      */     //   37: lload #6
/*      */     //   39: lconst_0
/*      */     //   40: lcmp
/*      */     //   41: ifle -> 94
/*      */     //   44: aload_0
/*      */     //   45: lload #6
/*      */     //   47: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   50: invokeinterface awaitReadable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   55: aload_0
/*      */     //   56: aload_1
/*      */     //   57: invokeinterface receive : (Ljava/nio/ByteBuffer;)I
/*      */     //   62: istore #5
/*      */     //   64: iload #5
/*      */     //   66: ifeq -> 72
/*      */     //   69: iload #5
/*      */     //   71: ireturn
/*      */     //   72: lload #6
/*      */     //   74: lload #8
/*      */     //   76: lneg
/*      */     //   77: invokestatic nanoTime : ()J
/*      */     //   80: dup2
/*      */     //   81: lstore #8
/*      */     //   83: ladd
/*      */     //   84: lconst_0
/*      */     //   85: invokestatic max : (JJ)J
/*      */     //   88: lsub
/*      */     //   89: lstore #6
/*      */     //   91: goto -> 30
/*      */     //   94: iload #5
/*      */     //   96: ireturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #482	-> 0
/*      */     //   #483	-> 9
/*      */     //   #484	-> 14
/*      */     //   #486	-> 17
/*      */     //   #487	-> 25
/*      */     //   #488	-> 30
/*      */     //   #490	-> 44
/*      */     //   #493	-> 55
/*      */     //   #494	-> 64
/*      */     //   #495	-> 69
/*      */     //   #499	-> 72
/*      */     //   #501	-> 94
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	97	0	channel	Lorg/xnio/channels/ReadableMessageChannel;
/*      */     //   0	97	1	buffer	Ljava/nio/ByteBuffer;
/*      */     //   0	97	2	time	J
/*      */     //   0	97	4	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   9	88	5	res	I
/*      */     //   25	72	6	remaining	J
/*      */     //   30	67	8	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	97	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableMessageChannel> long receiveBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
/*      */     long res;
/*  519 */     while ((res = channel.receive(buffers, offs, len)) == 0L) {
/*  520 */       channel.awaitReadable();
/*      */     }
/*  522 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ReadableMessageChannel> long receiveBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: iload_2
/*      */     //   3: iload_3
/*      */     //   4: invokeinterface receive : ([Ljava/nio/ByteBuffer;II)J
/*      */     //   9: lstore #7
/*      */     //   11: lload #7
/*      */     //   13: lconst_0
/*      */     //   14: lcmp
/*      */     //   15: ifeq -> 21
/*      */     //   18: lload #7
/*      */     //   20: lreturn
/*      */     //   21: aload #6
/*      */     //   23: lload #4
/*      */     //   25: invokevirtual toNanos : (J)J
/*      */     //   28: lstore #9
/*      */     //   30: invokestatic nanoTime : ()J
/*      */     //   33: lstore #11
/*      */     //   35: aload_1
/*      */     //   36: iload_2
/*      */     //   37: iload_3
/*      */     //   38: invokestatic hasRemaining : ([Ljava/nio/Buffer;II)Z
/*      */     //   41: ifeq -> 105
/*      */     //   44: lload #9
/*      */     //   46: lconst_0
/*      */     //   47: lcmp
/*      */     //   48: ifle -> 105
/*      */     //   51: aload_0
/*      */     //   52: lload #9
/*      */     //   54: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
/*      */     //   57: invokeinterface awaitReadable : (JLjava/util/concurrent/TimeUnit;)V
/*      */     //   62: aload_0
/*      */     //   63: aload_1
/*      */     //   64: iload_2
/*      */     //   65: iload_3
/*      */     //   66: invokeinterface receive : ([Ljava/nio/ByteBuffer;II)J
/*      */     //   71: lstore #7
/*      */     //   73: lload #7
/*      */     //   75: lconst_0
/*      */     //   76: lcmp
/*      */     //   77: ifeq -> 83
/*      */     //   80: lload #7
/*      */     //   82: lreturn
/*      */     //   83: lload #9
/*      */     //   85: lload #11
/*      */     //   87: lneg
/*      */     //   88: invokestatic nanoTime : ()J
/*      */     //   91: dup2
/*      */     //   92: lstore #11
/*      */     //   94: ladd
/*      */     //   95: lconst_0
/*      */     //   96: invokestatic max : (JJ)J
/*      */     //   99: lsub
/*      */     //   100: lstore #9
/*      */     //   102: goto -> 35
/*      */     //   105: lload #7
/*      */     //   107: lreturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #542	-> 0
/*      */     //   #543	-> 11
/*      */     //   #544	-> 18
/*      */     //   #546	-> 21
/*      */     //   #547	-> 30
/*      */     //   #548	-> 35
/*      */     //   #550	-> 51
/*      */     //   #551	-> 62
/*      */     //   #552	-> 73
/*      */     //   #553	-> 80
/*      */     //   #557	-> 83
/*      */     //   #559	-> 105
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   0	108	0	channel	Lorg/xnio/channels/ReadableMessageChannel;
/*      */     //   0	108	1	buffers	[Ljava/nio/ByteBuffer;
/*      */     //   0	108	2	offs	I
/*      */     //   0	108	3	len	I
/*      */     //   0	108	4	time	J
/*      */     //   0	108	6	unit	Ljava/util/concurrent/TimeUnit;
/*      */     //   11	97	7	res	J
/*      */     //   30	78	9	remaining	J
/*      */     //   35	73	11	now	J
/*      */     // Local variable type table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	108	0	channel	TC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ConnectedChannel, A extends AcceptingChannel<C>> C acceptBlocking(A channel) throws IOException {
/*      */     C accepted;
/*  575 */     while ((accepted = (C)channel.accept()) == null) {
/*  576 */       channel.awaitAcceptable();
/*      */     }
/*  578 */     return accepted;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ConnectedChannel, A extends AcceptingChannel<C>> C acceptBlocking(A channel, long time, TimeUnit unit) throws IOException {
/*  595 */     C accepted = channel.accept();
/*  596 */     if (accepted == null) {
/*  597 */       channel.awaitAcceptable(time, unit);
/*  598 */       return channel.accept();
/*      */     } 
/*  600 */     return accepted;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void transferBlocking(StreamSinkChannel destination, FileChannel source, long startPosition, long count) throws IOException {
/*  614 */     long remaining = count;
/*      */     
/*  616 */     while (remaining > 0L) {
/*  617 */       long res; while ((res = destination.transferFrom(source, startPosition, remaining)) == 0L) {
/*      */         try {
/*  619 */           destination.awaitWritable();
/*  620 */         } catch (InterruptedIOException e) {
/*  621 */           long bytes = count - remaining;
/*  622 */           if (bytes > 2147483647L) {
/*  623 */             e.bytesTransferred = -1; continue;
/*      */           } 
/*  625 */           e.bytesTransferred = (int)bytes;
/*      */         } 
/*      */       } 
/*      */       
/*  629 */       remaining -= res;
/*  630 */       startPosition += res;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void transferBlocking(FileChannel destination, StreamSourceChannel source, long startPosition, long count) throws IOException {
/*  644 */     long remaining = count;
/*      */     
/*  646 */     while (remaining > 0L) {
/*  647 */       long res; while ((res = source.transferTo(startPosition, remaining, destination)) == 0L) {
/*      */         try {
/*  649 */           source.awaitReadable();
/*  650 */         } catch (InterruptedIOException e) {
/*  651 */           long bytes = count - remaining;
/*  652 */           if (bytes > 2147483647L) {
/*  653 */             e.bytesTransferred = -1; continue;
/*      */           } 
/*  655 */           e.bytesTransferred = (int)bytes;
/*      */         } 
/*      */       } 
/*      */       
/*  659 */       remaining -= res;
/*  660 */       startPosition += res;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long transferBlocking(StreamSinkChannel destination, StreamSourceChannel source, ByteBuffer throughBuffer, long count) throws IOException {
/*  675 */     long t = 0L;
/*      */     
/*  677 */     while (t < count) {
/*      */       long res; try {
/*  679 */         while ((res = source.transferTo(count, throughBuffer, destination)) == 0L) {
/*  680 */           if (throughBuffer.hasRemaining()) {
/*  681 */             writeBlocking(destination, throughBuffer); continue;
/*      */           } 
/*  683 */           source.awaitReadable();
/*      */         } 
/*      */         
/*  686 */         t += res;
/*  687 */       } catch (InterruptedIOException e) {
/*  688 */         int transferred = e.bytesTransferred;
/*  689 */         t += transferred;
/*  690 */         if (transferred < 0 || t > 2147483647L) {
/*  691 */           e.bytesTransferred = -1;
/*      */         } else {
/*  693 */           e.bytesTransferred = (int)t;
/*      */         } 
/*  695 */         throw e;
/*      */       } 
/*  697 */       if (res == -1L) {
/*  698 */         return (t == 0L) ? -1L : t;
/*      */       }
/*      */     } 
/*  701 */     return t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CloseableChannel> void setCloseListener(T channel, ChannelListener<? super T> listener) {
/*  713 */     ChannelListener.Setter<? extends T> setter = (ChannelListener.Setter)channel.getCloseSetter();
/*  714 */     setter.set(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends AcceptingChannel<?>> void setAcceptListener(T channel, ChannelListener<? super T> listener) {
/*  726 */     ChannelListener.Setter<? extends T> setter = channel.getAcceptSetter();
/*  727 */     setter.set(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableReadChannel> void setReadListener(T channel, ChannelListener<? super T> listener) {
/*  739 */     ChannelListener.Setter<? extends T> setter = (ChannelListener.Setter)channel.getReadSetter();
/*  740 */     setter.set(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableWriteChannel> void setWriteListener(T channel, ChannelListener<? super T> listener) {
/*  752 */     ChannelListener.Setter<? extends T> setter = (ChannelListener.Setter)channel.getWriteSetter();
/*  753 */     setter.set(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteChannel wrapByteChannel(final ByteChannel original) {
/*  763 */     return new ByteChannel() {
/*      */         public int read(ByteBuffer dst) throws IOException {
/*  765 */           return original.read(dst);
/*      */         }
/*      */         
/*      */         public boolean isOpen() {
/*  769 */           return original.isOpen();
/*      */         }
/*      */         
/*      */         public void close() throws IOException {
/*  773 */           original.close();
/*      */         }
/*      */         
/*      */         public int write(ByteBuffer src) throws IOException {
/*  777 */           return original.write(src);
/*      */         }
/*      */         
/*      */         public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  781 */           return original.write(srcs, offset, length);
/*      */         }
/*      */         
/*      */         public long write(ByteBuffer[] srcs) throws IOException {
/*  785 */           return original.write(srcs);
/*      */         }
/*      */         
/*      */         public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*  789 */           return original.read(dsts, offset, length);
/*      */         }
/*      */         
/*      */         public long read(ByteBuffer[] dsts) throws IOException {
/*  793 */           return original.read(dsts);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getOption(Configurable configurable, Option<T> option, T defaultValue) {
/*      */     try {
/*  810 */       T value = configurable.getOption(option);
/*  811 */       return (value == null) ? defaultValue : value;
/*  812 */     } catch (IOException e) {
/*  813 */       return defaultValue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean getOption(Configurable configurable, Option<Boolean> option, boolean defaultValue) {
/*      */     try {
/*  828 */       Boolean value = configurable.<Boolean>getOption(option);
/*  829 */       return (value == null) ? defaultValue : value.booleanValue();
/*  830 */     } catch (IOException e) {
/*  831 */       return defaultValue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getOption(Configurable configurable, Option<Integer> option, int defaultValue) {
/*      */     try {
/*  846 */       Integer value = configurable.<Integer>getOption(option);
/*  847 */       return (value == null) ? defaultValue : value.intValue();
/*  848 */     } catch (IOException e) {
/*  849 */       return defaultValue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getOption(Configurable configurable, Option<Long> option, long defaultValue) {
/*      */     try {
/*  864 */       Long value = configurable.<Long>getOption(option);
/*  865 */       return (value == null) ? defaultValue : value.longValue();
/*  866 */     } catch (IOException e) {
/*  867 */       return defaultValue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> T unwrap(Class<T> targetType, Channel channel) {
/*      */     while (true) {
/*  882 */       if (channel == null)
/*  883 */         return null; 
/*  884 */       if (targetType.isInstance(channel))
/*  885 */         return targetType.cast(channel); 
/*  886 */       if (channel instanceof WrappedChannel) {
/*  887 */         channel = ((WrappedChannel<Channel>)channel).getChannel(); continue;
/*      */       }  break;
/*  889 */     }  return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  895 */   private static final ByteBuffer DRAIN_BUFFER = ByteBuffer.allocateDirect(16384);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long drain(StreamSourceChannel channel, long count) throws IOException {
/*  906 */     long total = 0L;
/*      */     
/*  908 */     ByteBuffer buffer = null;
/*      */     while (true) {
/*  910 */       if (count == 0L) return total; 
/*  911 */       if (NULL_FILE_CHANNEL != null) {
/*  912 */         long lres; while (count > 0L && (
/*  913 */           lres = channel.transferTo(0L, count, NULL_FILE_CHANNEL)) != 0L) {
/*      */ 
/*      */           
/*  916 */           total += lres;
/*  917 */           count -= lres;
/*      */         } 
/*      */         
/*  920 */         if (total > 0L) return total; 
/*      */       } 
/*  922 */       if (buffer == null) buffer = DRAIN_BUFFER.duplicate(); 
/*  923 */       if (buffer.limit() > count) buffer.limit((int)count); 
/*  924 */       int ires = channel.read(buffer);
/*  925 */       buffer.clear();
/*  926 */       switch (ires) { case -1:
/*  927 */           return (total == 0L) ? -1L : total;
/*  928 */         case 0: return total; }
/*  929 */        total += ires; count -= ires;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long drain(ReadableByteChannel channel, long count) throws IOException {
/*  943 */     if (channel instanceof StreamSourceChannel) {
/*  944 */       return drain((StreamSourceChannel)channel, count);
/*      */     }
/*  946 */     long total = 0L;
/*      */     
/*  948 */     ByteBuffer buffer = null;
/*      */     while (true) {
/*  950 */       if (count == 0L) return total; 
/*  951 */       if (NULL_FILE_CHANNEL != null) {
/*  952 */         long lres; while (count > 0L && (
/*  953 */           lres = NULL_FILE_CHANNEL.transferFrom(channel, 0L, count)) != 0L) {
/*      */ 
/*      */           
/*  956 */           total += lres;
/*  957 */           count -= lres;
/*      */         } 
/*      */         
/*  960 */         if (total > 0L) return total; 
/*      */       } 
/*  962 */       if (buffer == null) buffer = DRAIN_BUFFER.duplicate(); 
/*  963 */       if (buffer.limit() > count) buffer.limit((int)count); 
/*  964 */       int ires = channel.read(buffer);
/*  965 */       buffer.clear();
/*  966 */       switch (ires) { case -1:
/*  967 */           return (total == 0L) ? -1L : total;
/*  968 */         case 0: return total; }
/*  969 */        total += ires; count -= ires;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long drain(FileChannel channel, long position, long count) throws IOException {
/*  986 */     if (channel instanceof StreamSourceChannel) {
/*  987 */       return drain((StreamSourceChannel)channel, count);
/*      */     }
/*  989 */     long total = 0L;
/*      */     
/*  991 */     ByteBuffer buffer = null;
/*      */     while (true) {
/*  993 */       if (count == 0L) return total; 
/*  994 */       if (NULL_FILE_CHANNEL != null) {
/*  995 */         long lres; while (count > 0L && (
/*  996 */           lres = channel.transferTo(position, count, NULL_FILE_CHANNEL)) != 0L) {
/*      */ 
/*      */           
/*  999 */           total += lres;
/* 1000 */           count -= lres;
/*      */         } 
/*      */         
/* 1003 */         if (total > 0L) return total; 
/*      */       } 
/* 1005 */       if (buffer == null) buffer = DRAIN_BUFFER.duplicate(); 
/* 1006 */       if (buffer.limit() > count) buffer.limit((int)count); 
/* 1007 */       int ires = channel.read(buffer);
/* 1008 */       buffer.clear();
/* 1009 */       switch (ires) { case -1:
/* 1010 */           return (total == 0L) ? -1L : total;
/* 1011 */         case 0: return total; }
/* 1012 */        total += ires;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void resumeReadsAsync(final SuspendableReadChannel channel) {
/* 1025 */     XnioIoThread ioThread = channel.getIoThread();
/* 1026 */     if (ioThread == Thread.currentThread()) {
/* 1027 */       channel.resumeReads();
/*      */     } else {
/* 1029 */       ioThread.execute(new Runnable() {
/*      */             public void run() {
/* 1031 */               channel.resumeReads();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void resumeWritesAsync(final SuspendableWriteChannel channel) {
/* 1044 */     XnioIoThread ioThread = channel.getIoThread();
/* 1045 */     if (ioThread == Thread.currentThread()) {
/* 1046 */       channel.resumeWrites();
/*      */     } else {
/* 1048 */       ioThread.execute(new Runnable() {
/*      */             public void run() {
/* 1050 */               channel.resumeWrites();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int writeFinalBasic(StreamSinkChannel channel, ByteBuffer src) throws IOException {
/* 1066 */     int res = channel.write(src);
/* 1067 */     if (!src.hasRemaining()) {
/* 1068 */       channel.shutdownWrites();
/*      */     }
/* 1070 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long writeFinalBasic(StreamSinkChannel channel, ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 1085 */     long res = channel.write(srcs, offset, length);
/* 1086 */     if (!Buffers.hasRemaining((Buffer[])srcs, offset, length)) {
/* 1087 */       channel.shutdownWrites();
/*      */     }
/* 1089 */     return res;
/*      */   }
/*      */   
/*      */   static {
/* 1093 */     NULL_FILE_CHANNEL = AccessController.<FileChannel>doPrivileged(new PrivilegedAction<FileChannel>() {
/*      */           public FileChannel run() {
/* 1095 */             String osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
/*      */             try {
/* 1097 */               if (osName.contains("windows")) {
/* 1098 */                 return (new FileOutputStream("NUL:")).getChannel();
/*      */               }
/* 1100 */               return (new FileOutputStream("/dev/null")).getChannel();
/*      */             }
/* 1102 */             catch (FileNotFoundException e) {
/* 1103 */               throw new IOError(e);
/*      */             } 
/*      */           }
/*      */         });
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\Channels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */