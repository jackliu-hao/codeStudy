/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio._private.Messages;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class FramedMessageChannel
/*     */   extends TranslatingSuspendableChannel<ConnectedMessageChannel, ConnectedStreamChannel>
/*     */   implements ConnectedMessageChannel
/*     */ {
/*  46 */   private static final Logger log = Logger.getLogger("org.xnio.channels.framed");
/*     */   
/*     */   private final Pooled<ByteBuffer> receiveBuffer;
/*     */   
/*     */   private final Pooled<ByteBuffer> transmitBuffer;
/*  51 */   private final Object readLock = new Object();
/*  52 */   private final Object writeLock = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedMessageChannel(ConnectedStreamChannel channel, ByteBuffer receiveBuffer, ByteBuffer transmitBuffer) {
/*  62 */     super(channel);
/*  63 */     this.receiveBuffer = Buffers.pooledWrapper(receiveBuffer);
/*  64 */     this.transmitBuffer = Buffers.pooledWrapper(transmitBuffer);
/*  65 */     log.tracef("Created new framed message channel around %s, receive buffer %s, transmit buffer %s", channel, receiveBuffer, transmitBuffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedMessageChannel(ConnectedStreamChannel channel, Pooled<ByteBuffer> receiveBuffer, Pooled<ByteBuffer> transmitBuffer) {
/*  76 */     super(channel);
/*  77 */     this.receiveBuffer = receiveBuffer;
/*  78 */     this.transmitBuffer = transmitBuffer;
/*  79 */     log.tracef("Created new framed message channel around %s, receive buffer %s, transmit buffer %s", channel, receiveBuffer, transmitBuffer);
/*     */   }
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
/*     */   public int receive(ByteBuffer buffer) throws IOException {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield readLock : Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: invokevirtual isReadShutDown : ()Z
/*     */     //   11: ifeq -> 18
/*     */     //   14: iconst_m1
/*     */     //   15: aload_2
/*     */     //   16: monitorexit
/*     */     //   17: ireturn
/*     */     //   18: aload_0
/*     */     //   19: getfield receiveBuffer : Lorg/xnio/Pooled;
/*     */     //   22: invokeinterface getResource : ()Ljava/lang/Object;
/*     */     //   27: checkcast java/nio/ByteBuffer
/*     */     //   30: astore_3
/*     */     //   31: aload_0
/*     */     //   32: getfield channel : Lorg/xnio/channels/SuspendableChannel;
/*     */     //   35: checkcast org/xnio/channels/ConnectedStreamChannel
/*     */     //   38: astore #5
/*     */     //   40: aload #5
/*     */     //   42: aload_3
/*     */     //   43: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*     */     //   48: istore #4
/*     */     //   50: iload #4
/*     */     //   52: ifgt -> 40
/*     */     //   55: aload_3
/*     */     //   56: invokevirtual position : ()I
/*     */     //   59: iconst_4
/*     */     //   60: if_icmpge -> 95
/*     */     //   63: iload #4
/*     */     //   65: iconst_m1
/*     */     //   66: if_icmpne -> 74
/*     */     //   69: aload_3
/*     */     //   70: invokevirtual clear : ()Ljava/nio/Buffer;
/*     */     //   73: pop
/*     */     //   74: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   77: ldc 'Did not read a length'
/*     */     //   79: iconst_0
/*     */     //   80: anewarray java/lang/Object
/*     */     //   83: invokevirtual tracef : (Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   86: aload_0
/*     */     //   87: invokevirtual clearReadReady : ()V
/*     */     //   90: iload #4
/*     */     //   92: aload_2
/*     */     //   93: monitorexit
/*     */     //   94: ireturn
/*     */     //   95: aload_3
/*     */     //   96: invokevirtual flip : ()Ljava/nio/Buffer;
/*     */     //   99: pop
/*     */     //   100: aload_3
/*     */     //   101: invokevirtual getInt : ()I
/*     */     //   104: istore #6
/*     */     //   106: iload #6
/*     */     //   108: iflt -> 122
/*     */     //   111: iload #6
/*     */     //   113: aload_3
/*     */     //   114: invokevirtual capacity : ()I
/*     */     //   117: iconst_4
/*     */     //   118: isub
/*     */     //   119: if_icmple -> 139
/*     */     //   122: aload_3
/*     */     //   123: iconst_4
/*     */     //   124: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   127: pop
/*     */     //   128: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*     */     //   131: iload #6
/*     */     //   133: invokeinterface recvInvalidMsgLength : (I)Ljava/io/IOException;
/*     */     //   138: athrow
/*     */     //   139: aload_3
/*     */     //   140: invokevirtual remaining : ()I
/*     */     //   143: iload #6
/*     */     //   145: if_icmpge -> 230
/*     */     //   148: iload #4
/*     */     //   150: iconst_m1
/*     */     //   151: if_icmpne -> 162
/*     */     //   154: aload_3
/*     */     //   155: invokevirtual clear : ()Ljava/nio/Buffer;
/*     */     //   158: pop
/*     */     //   159: goto -> 173
/*     */     //   162: aload_3
/*     */     //   163: iconst_4
/*     */     //   164: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   167: pop
/*     */     //   168: aload_3
/*     */     //   169: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   172: pop
/*     */     //   173: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   176: ldc 'Did not read enough bytes for a full message'
/*     */     //   178: iconst_0
/*     */     //   179: anewarray java/lang/Object
/*     */     //   182: invokevirtual tracef : (Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   185: aload_0
/*     */     //   186: invokevirtual clearReadReady : ()V
/*     */     //   189: iload #4
/*     */     //   191: istore #7
/*     */     //   193: iload #4
/*     */     //   195: iconst_m1
/*     */     //   196: if_icmpeq -> 225
/*     */     //   199: aload_3
/*     */     //   200: invokevirtual position : ()I
/*     */     //   203: iconst_4
/*     */     //   204: if_icmplt -> 225
/*     */     //   207: aload_3
/*     */     //   208: invokevirtual position : ()I
/*     */     //   211: iconst_4
/*     */     //   212: aload_3
/*     */     //   213: iconst_0
/*     */     //   214: invokevirtual getInt : (I)I
/*     */     //   217: iadd
/*     */     //   218: if_icmplt -> 225
/*     */     //   221: aload_0
/*     */     //   222: invokevirtual setReadReady : ()V
/*     */     //   225: aload_2
/*     */     //   226: monitorexit
/*     */     //   227: iload #7
/*     */     //   229: ireturn
/*     */     //   230: aload_1
/*     */     //   231: invokevirtual hasRemaining : ()Z
/*     */     //   234: ifeq -> 261
/*     */     //   237: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   240: ldc 'Copying message from %s into %s'
/*     */     //   242: aload_3
/*     */     //   243: aload_1
/*     */     //   244: invokevirtual tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*     */     //   247: aload_1
/*     */     //   248: aload_3
/*     */     //   249: iload #6
/*     */     //   251: invokestatic slice : (Ljava/nio/ByteBuffer;I)Ljava/nio/ByteBuffer;
/*     */     //   254: invokestatic copy : (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
/*     */     //   257: pop
/*     */     //   258: goto -> 278
/*     */     //   261: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   264: ldc 'Not copying message from %s into full buffer %s'
/*     */     //   266: aload_3
/*     */     //   267: aload_1
/*     */     //   268: invokevirtual tracef : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*     */     //   271: aload_3
/*     */     //   272: iload #6
/*     */     //   274: invokestatic skip : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   277: pop
/*     */     //   278: aload_3
/*     */     //   279: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   282: pop
/*     */     //   283: iload #6
/*     */     //   285: istore #7
/*     */     //   287: iload #4
/*     */     //   289: iconst_m1
/*     */     //   290: if_icmpeq -> 319
/*     */     //   293: aload_3
/*     */     //   294: invokevirtual position : ()I
/*     */     //   297: iconst_4
/*     */     //   298: if_icmplt -> 319
/*     */     //   301: aload_3
/*     */     //   302: invokevirtual position : ()I
/*     */     //   305: iconst_4
/*     */     //   306: aload_3
/*     */     //   307: iconst_0
/*     */     //   308: invokevirtual getInt : (I)I
/*     */     //   311: iadd
/*     */     //   312: if_icmplt -> 319
/*     */     //   315: aload_0
/*     */     //   316: invokevirtual setReadReady : ()V
/*     */     //   319: aload_2
/*     */     //   320: monitorexit
/*     */     //   321: iload #7
/*     */     //   323: ireturn
/*     */     //   324: astore #8
/*     */     //   326: iload #4
/*     */     //   328: iconst_m1
/*     */     //   329: if_icmpeq -> 358
/*     */     //   332: aload_3
/*     */     //   333: invokevirtual position : ()I
/*     */     //   336: iconst_4
/*     */     //   337: if_icmplt -> 358
/*     */     //   340: aload_3
/*     */     //   341: invokevirtual position : ()I
/*     */     //   344: iconst_4
/*     */     //   345: aload_3
/*     */     //   346: iconst_0
/*     */     //   347: invokevirtual getInt : (I)I
/*     */     //   350: iadd
/*     */     //   351: if_icmplt -> 358
/*     */     //   354: aload_0
/*     */     //   355: invokevirtual setReadReady : ()V
/*     */     //   358: aload #8
/*     */     //   360: athrow
/*     */     //   361: astore #9
/*     */     //   363: aload_2
/*     */     //   364: monitorexit
/*     */     //   365: aload #9
/*     */     //   367: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #84	-> 0
/*     */     //   #85	-> 7
/*     */     //   #86	-> 14
/*     */     //   #88	-> 18
/*     */     //   #90	-> 31
/*     */     //   #92	-> 40
/*     */     //   #93	-> 50
/*     */     //   #94	-> 55
/*     */     //   #95	-> 63
/*     */     //   #96	-> 69
/*     */     //   #98	-> 74
/*     */     //   #99	-> 86
/*     */     //   #101	-> 90
/*     */     //   #103	-> 95
/*     */     //   #105	-> 100
/*     */     //   #106	-> 106
/*     */     //   #107	-> 122
/*     */     //   #108	-> 128
/*     */     //   #110	-> 139
/*     */     //   #111	-> 148
/*     */     //   #112	-> 154
/*     */     //   #114	-> 162
/*     */     //   #115	-> 168
/*     */     //   #117	-> 173
/*     */     //   #118	-> 185
/*     */     //   #120	-> 189
/*     */     //   #133	-> 193
/*     */     //   #134	-> 199
/*     */     //   #136	-> 221
/*     */     //   #120	-> 227
/*     */     //   #122	-> 230
/*     */     //   #123	-> 237
/*     */     //   #124	-> 247
/*     */     //   #126	-> 261
/*     */     //   #127	-> 271
/*     */     //   #130	-> 278
/*     */     //   #131	-> 283
/*     */     //   #133	-> 287
/*     */     //   #134	-> 293
/*     */     //   #136	-> 315
/*     */     //   #131	-> 321
/*     */     //   #133	-> 324
/*     */     //   #134	-> 332
/*     */     //   #136	-> 354
/*     */     //   #139	-> 358
/*     */     //   #140	-> 361
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   106	218	6	length	I
/*     */     //   31	330	3	receiveBuffer	Ljava/nio/ByteBuffer;
/*     */     //   50	311	4	res	I
/*     */     //   40	321	5	channel	Lorg/xnio/channels/ConnectedStreamChannel;
/*     */     //   0	368	0	this	Lorg/xnio/channels/FramedMessageChannel;
/*     */     //   0	368	1	buffer	Ljava/nio/ByteBuffer;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	17	361	finally
/*     */     //   18	94	361	finally
/*     */     //   95	227	361	finally
/*     */     //   100	193	324	finally
/*     */     //   230	287	324	finally
/*     */     //   230	321	361	finally
/*     */     //   324	326	324	finally
/*     */     //   324	365	361	finally
/*     */   }
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
/*     */   public long receive(ByteBuffer[] buffers) throws IOException {
/* 145 */     return receive(buffers, 0, buffers.length);
/*     */   }
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
/*     */   public long receive(ByteBuffer[] buffers, int offs, int len) throws IOException {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield readLock : Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore #4
/*     */     //   7: monitorenter
/*     */     //   8: aload_0
/*     */     //   9: invokevirtual isReadShutDown : ()Z
/*     */     //   12: ifeq -> 22
/*     */     //   15: ldc2_w -1
/*     */     //   18: aload #4
/*     */     //   20: monitorexit
/*     */     //   21: lreturn
/*     */     //   22: aload_0
/*     */     //   23: getfield receiveBuffer : Lorg/xnio/Pooled;
/*     */     //   26: invokeinterface getResource : ()Ljava/lang/Object;
/*     */     //   31: checkcast java/nio/ByteBuffer
/*     */     //   34: astore #5
/*     */     //   36: aload_0
/*     */     //   37: getfield channel : Lorg/xnio/channels/SuspendableChannel;
/*     */     //   40: checkcast org/xnio/channels/ConnectedStreamChannel
/*     */     //   43: astore #7
/*     */     //   45: aload #7
/*     */     //   47: aload #5
/*     */     //   49: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*     */     //   54: istore #6
/*     */     //   56: iload #6
/*     */     //   58: ifgt -> 45
/*     */     //   61: aload #5
/*     */     //   63: invokevirtual position : ()I
/*     */     //   66: iconst_4
/*     */     //   67: if_icmpge -> 105
/*     */     //   70: iload #6
/*     */     //   72: iconst_m1
/*     */     //   73: if_icmpne -> 82
/*     */     //   76: aload #5
/*     */     //   78: invokevirtual clear : ()Ljava/nio/Buffer;
/*     */     //   81: pop
/*     */     //   82: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   85: ldc 'Did not read a length'
/*     */     //   87: iconst_0
/*     */     //   88: anewarray java/lang/Object
/*     */     //   91: invokevirtual tracef : (Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   94: aload_0
/*     */     //   95: invokevirtual clearReadReady : ()V
/*     */     //   98: iload #6
/*     */     //   100: i2l
/*     */     //   101: aload #4
/*     */     //   103: monitorexit
/*     */     //   104: lreturn
/*     */     //   105: aload #5
/*     */     //   107: invokevirtual flip : ()Ljava/nio/Buffer;
/*     */     //   110: pop
/*     */     //   111: aload #5
/*     */     //   113: invokevirtual getInt : ()I
/*     */     //   116: istore #8
/*     */     //   118: iload #8
/*     */     //   120: iflt -> 135
/*     */     //   123: iload #8
/*     */     //   125: aload #5
/*     */     //   127: invokevirtual capacity : ()I
/*     */     //   130: iconst_4
/*     */     //   131: isub
/*     */     //   132: if_icmple -> 153
/*     */     //   135: aload #5
/*     */     //   137: iconst_4
/*     */     //   138: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   141: pop
/*     */     //   142: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*     */     //   145: iload #8
/*     */     //   147: invokeinterface recvInvalidMsgLength : (I)Ljava/io/IOException;
/*     */     //   152: athrow
/*     */     //   153: aload #5
/*     */     //   155: invokevirtual remaining : ()I
/*     */     //   158: iload #8
/*     */     //   160: if_icmpge -> 253
/*     */     //   163: iload #6
/*     */     //   165: iconst_m1
/*     */     //   166: if_icmpne -> 178
/*     */     //   169: aload #5
/*     */     //   171: invokevirtual clear : ()Ljava/nio/Buffer;
/*     */     //   174: pop
/*     */     //   175: goto -> 191
/*     */     //   178: aload #5
/*     */     //   180: iconst_4
/*     */     //   181: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   184: pop
/*     */     //   185: aload #5
/*     */     //   187: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   190: pop
/*     */     //   191: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   194: ldc 'Did not read enough bytes for a full message'
/*     */     //   196: iconst_0
/*     */     //   197: anewarray java/lang/Object
/*     */     //   200: invokevirtual tracef : (Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   203: aload_0
/*     */     //   204: invokevirtual clearReadReady : ()V
/*     */     //   207: iload #6
/*     */     //   209: i2l
/*     */     //   210: lstore #9
/*     */     //   212: iload #6
/*     */     //   214: iconst_m1
/*     */     //   215: if_icmpeq -> 247
/*     */     //   218: aload #5
/*     */     //   220: invokevirtual position : ()I
/*     */     //   223: iconst_4
/*     */     //   224: if_icmplt -> 247
/*     */     //   227: aload #5
/*     */     //   229: invokevirtual position : ()I
/*     */     //   232: iconst_4
/*     */     //   233: aload #5
/*     */     //   235: iconst_0
/*     */     //   236: invokevirtual getInt : (I)I
/*     */     //   239: iadd
/*     */     //   240: if_icmplt -> 247
/*     */     //   243: aload_0
/*     */     //   244: invokevirtual setReadReady : ()V
/*     */     //   247: aload #4
/*     */     //   249: monitorexit
/*     */     //   250: lload #9
/*     */     //   252: lreturn
/*     */     //   253: aload_1
/*     */     //   254: invokestatic hasRemaining : ([Ljava/nio/Buffer;)Z
/*     */     //   257: ifeq -> 287
/*     */     //   260: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   263: ldc 'Copying message from %s into multiple buffers'
/*     */     //   265: aload #5
/*     */     //   267: invokevirtual tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*     */     //   270: aload_1
/*     */     //   271: iload_2
/*     */     //   272: iload_3
/*     */     //   273: aload #5
/*     */     //   275: iload #8
/*     */     //   277: invokestatic slice : (Ljava/nio/ByteBuffer;I)Ljava/nio/ByteBuffer;
/*     */     //   280: invokestatic copy : ([Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;)I
/*     */     //   283: pop
/*     */     //   284: goto -> 305
/*     */     //   287: getstatic org/xnio/channels/FramedMessageChannel.log : Lorg/jboss/logging/Logger;
/*     */     //   290: ldc 'Not copying message from %s into multiple full buffers'
/*     */     //   292: aload #5
/*     */     //   294: invokevirtual tracef : (Ljava/lang/String;Ljava/lang/Object;)V
/*     */     //   297: aload #5
/*     */     //   299: iload #8
/*     */     //   301: invokestatic skip : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*     */     //   304: pop
/*     */     //   305: aload #5
/*     */     //   307: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*     */     //   310: pop
/*     */     //   311: iload #8
/*     */     //   313: i2l
/*     */     //   314: lstore #9
/*     */     //   316: iload #6
/*     */     //   318: iconst_m1
/*     */     //   319: if_icmpeq -> 351
/*     */     //   322: aload #5
/*     */     //   324: invokevirtual position : ()I
/*     */     //   327: iconst_4
/*     */     //   328: if_icmplt -> 351
/*     */     //   331: aload #5
/*     */     //   333: invokevirtual position : ()I
/*     */     //   336: iconst_4
/*     */     //   337: aload #5
/*     */     //   339: iconst_0
/*     */     //   340: invokevirtual getInt : (I)I
/*     */     //   343: iadd
/*     */     //   344: if_icmplt -> 351
/*     */     //   347: aload_0
/*     */     //   348: invokevirtual setReadReady : ()V
/*     */     //   351: aload #4
/*     */     //   353: monitorexit
/*     */     //   354: lload #9
/*     */     //   356: lreturn
/*     */     //   357: astore #11
/*     */     //   359: iload #6
/*     */     //   361: iconst_m1
/*     */     //   362: if_icmpeq -> 394
/*     */     //   365: aload #5
/*     */     //   367: invokevirtual position : ()I
/*     */     //   370: iconst_4
/*     */     //   371: if_icmplt -> 394
/*     */     //   374: aload #5
/*     */     //   376: invokevirtual position : ()I
/*     */     //   379: iconst_4
/*     */     //   380: aload #5
/*     */     //   382: iconst_0
/*     */     //   383: invokevirtual getInt : (I)I
/*     */     //   386: iadd
/*     */     //   387: if_icmplt -> 394
/*     */     //   390: aload_0
/*     */     //   391: invokevirtual setReadReady : ()V
/*     */     //   394: aload #11
/*     */     //   396: athrow
/*     */     //   397: astore #12
/*     */     //   399: aload #4
/*     */     //   401: monitorexit
/*     */     //   402: aload #12
/*     */     //   404: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #150	-> 0
/*     */     //   #151	-> 8
/*     */     //   #152	-> 15
/*     */     //   #154	-> 22
/*     */     //   #156	-> 36
/*     */     //   #158	-> 45
/*     */     //   #159	-> 56
/*     */     //   #160	-> 61
/*     */     //   #161	-> 70
/*     */     //   #162	-> 76
/*     */     //   #164	-> 82
/*     */     //   #165	-> 94
/*     */     //   #166	-> 98
/*     */     //   #168	-> 105
/*     */     //   #170	-> 111
/*     */     //   #171	-> 118
/*     */     //   #172	-> 135
/*     */     //   #173	-> 142
/*     */     //   #175	-> 153
/*     */     //   #176	-> 163
/*     */     //   #177	-> 169
/*     */     //   #179	-> 178
/*     */     //   #180	-> 185
/*     */     //   #182	-> 191
/*     */     //   #183	-> 203
/*     */     //   #185	-> 207
/*     */     //   #198	-> 212
/*     */     //   #199	-> 218
/*     */     //   #201	-> 243
/*     */     //   #185	-> 250
/*     */     //   #187	-> 253
/*     */     //   #188	-> 260
/*     */     //   #189	-> 270
/*     */     //   #191	-> 287
/*     */     //   #192	-> 297
/*     */     //   #195	-> 305
/*     */     //   #196	-> 311
/*     */     //   #198	-> 316
/*     */     //   #199	-> 322
/*     */     //   #201	-> 347
/*     */     //   #196	-> 354
/*     */     //   #198	-> 357
/*     */     //   #199	-> 365
/*     */     //   #201	-> 390
/*     */     //   #204	-> 394
/*     */     //   #205	-> 397
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   118	239	8	length	I
/*     */     //   36	361	5	receiveBuffer	Ljava/nio/ByteBuffer;
/*     */     //   56	341	6	res	I
/*     */     //   45	352	7	channel	Lorg/xnio/channels/ConnectedStreamChannel;
/*     */     //   0	405	0	this	Lorg/xnio/channels/FramedMessageChannel;
/*     */     //   0	405	1	buffers	[Ljava/nio/ByteBuffer;
/*     */     //   0	405	2	offs	I
/*     */     //   0	405	3	len	I
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   8	21	397	finally
/*     */     //   22	104	397	finally
/*     */     //   105	250	397	finally
/*     */     //   111	212	357	finally
/*     */     //   253	316	357	finally
/*     */     //   253	354	397	finally
/*     */     //   357	359	357	finally
/*     */     //   357	402	397	finally
/*     */   }
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
/*     */   protected void shutdownReadsAction(boolean writeComplete) throws IOException {
/* 209 */     synchronized (this.readLock) {
/* 210 */       log.tracef("Shutting down reads on %s", this);
/*     */       try {
/* 212 */         ((ByteBuffer)this.receiveBuffer.getResource()).clear();
/* 213 */       } catch (Throwable throwable) {}
/*     */       
/*     */       try {
/* 216 */         this.receiveBuffer.free();
/* 217 */       } catch (Throwable throwable) {}
/*     */     } 
/*     */     
/* 220 */     this.channel.shutdownReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean send(ByteBuffer buffer) throws IOException {
/* 225 */     synchronized (this.writeLock) {
/* 226 */       if (isWriteShutDown()) {
/* 227 */         throw Messages.msg.writeShutDown();
/*     */       }
/* 229 */       if (!buffer.hasRemaining()) {
/* 230 */         return true;
/*     */       }
/* 232 */       ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
/* 233 */       int remaining = buffer.remaining();
/* 234 */       if (remaining > transmitBuffer.capacity() - 4) {
/* 235 */         throw Messages.msg.txMsgTooLarge();
/*     */       }
/* 237 */       log.tracef("Accepting %s into %s", buffer, transmitBuffer);
/* 238 */       if (transmitBuffer.remaining() < 4 + remaining && !doFlushBuffer()) {
/* 239 */         log.tracef("Insufficient room to accept %s into %s", buffer, transmitBuffer);
/* 240 */         return false;
/*     */       } 
/* 242 */       transmitBuffer.putInt(remaining);
/* 243 */       transmitBuffer.put(buffer);
/* 244 */       log.tracef("Accepted a message into %s", transmitBuffer);
/*     */       
/* 246 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean send(ByteBuffer[] buffers) throws IOException {
/* 252 */     return send(buffers, 0, buffers.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean send(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 257 */     synchronized (this.writeLock) {
/* 258 */       if (isWriteShutDown()) {
/* 259 */         throw Messages.msg.writeShutDown();
/*     */       }
/* 261 */       if (!Buffers.hasRemaining((Buffer[])buffers, offs, len)) {
/* 262 */         return true;
/*     */       }
/* 264 */       ByteBuffer transmitBuffer = (ByteBuffer)this.transmitBuffer.getResource();
/* 265 */       long remaining = Buffers.remaining((Buffer[])buffers, offs, len);
/* 266 */       if (remaining > transmitBuffer.capacity() - 4L) {
/* 267 */         throw Messages.msg.txMsgTooLarge();
/*     */       }
/* 269 */       log.tracef("Accepting multiple buffers into %s", transmitBuffer);
/* 270 */       if (transmitBuffer.remaining() < 4L + remaining && !doFlushBuffer()) {
/* 271 */         log.tracef("Insufficient room to accept multiple buffers into %s", transmitBuffer);
/* 272 */         return false;
/*     */       } 
/* 274 */       transmitBuffer.putInt((int)remaining);
/* 275 */       Buffers.copy(transmitBuffer, buffers, offs, len);
/* 276 */       log.tracef("Accepted a message into %s", transmitBuffer);
/* 277 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer buffer) throws IOException {
/* 283 */     if (send(buffer)) {
/* 284 */       shutdownWrites();
/* 285 */       return true;
/*     */     } 
/* 287 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
/* 292 */     if (send(buffers)) {
/* 293 */       shutdownWrites();
/* 294 */       return true;
/*     */     } 
/* 296 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 301 */     if (send(buffers, offs, len)) {
/* 302 */       shutdownWrites();
/* 303 */       return true;
/*     */     } 
/* 305 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean flushAction(boolean shutDown) throws IOException {
/* 309 */     synchronized (this.writeLock) {
/* 310 */       return (doFlushBuffer() && this.channel.flush());
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void shutdownWritesComplete(boolean readShutDown) throws IOException {
/* 315 */     synchronized (this.writeLock) {
/* 316 */       log.tracef("Finished shutting down writes on %s", this);
/*     */       try {
/* 318 */         this.transmitBuffer.free();
/* 319 */       } catch (Throwable throwable) {}
/*     */     } 
/* 321 */     this.channel.shutdownWrites();
/*     */   }
/*     */   
/*     */   private boolean doFlushBuffer() throws IOException {
/* 325 */     assert Thread.holdsLock(this.writeLock);
/* 326 */     ByteBuffer buffer = (ByteBuffer)this.transmitBuffer.getResource();
/* 327 */     buffer.flip();
/*     */     try {
/* 329 */       while (buffer.hasRemaining()) {
/* 330 */         int res = this.channel.write(buffer);
/* 331 */         if (res == 0) {
/* 332 */           log.tracef("Did not fully flush %s", this);
/* 333 */           return false;
/*     */         } 
/*     */       } 
/* 336 */       log.tracef("Fully flushed %s", this);
/* 337 */       return true;
/*     */     } finally {
/* 339 */       buffer.compact();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean doFlush() throws IOException {
/* 344 */     return (doFlushBuffer() && this.channel.flush());
/*     */   }
/*     */   
/*     */   protected void closeAction(boolean readShutDown, boolean writeShutDown) throws IOException {
/* 348 */     boolean error = false;
/* 349 */     if (!writeShutDown) {
/* 350 */       synchronized (this.writeLock) {
/*     */         try {
/* 352 */           if (!doFlush()) error = true; 
/* 353 */         } catch (Throwable t) {
/* 354 */           error = true;
/*     */         } 
/*     */         try {
/* 357 */           this.transmitBuffer.free();
/* 358 */         } catch (Throwable throwable) {}
/*     */       } 
/*     */     }
/*     */     
/* 362 */     if (!readShutDown) {
/* 363 */       synchronized (this.readLock) {
/*     */         try {
/* 365 */           this.receiveBuffer.free();
/* 366 */         } catch (Throwable throwable) {}
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 371 */       if (error) throw Messages.msg.unflushedData(); 
/* 372 */       this.channel.close();
/*     */     } finally {
/* 374 */       IoUtils.safeClose(this.channel);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 380 */     return this.channel.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 385 */     return this.channel.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 390 */     return this.channel.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 395 */     return this.channel.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectedStreamChannel getChannel() {
/* 404 */     return this.channel;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\FramedMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */