/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.xnio.Pooled;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FramingMessageSourceConduit
/*    */   extends AbstractSourceConduit<StreamSourceConduit>
/*    */   implements MessageSourceConduit
/*    */ {
/*    */   private final Pooled<ByteBuffer> receiveBuffer;
/*    */   private boolean ready;
/*    */   
/*    */   public FramingMessageSourceConduit(StreamSourceConduit next, Pooled<ByteBuffer> receiveBuffer) {
/* 45 */     super(next);
/* 46 */     this.receiveBuffer = receiveBuffer;
/*    */   }
/*    */   
/*    */   public void resumeReads() {
/* 50 */     if (this.ready) { this.next.wakeupReads(); } else { this.next.resumeReads(); }
/*    */   
/*    */   }
/*    */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 54 */     if (!this.ready) this.next.awaitReadable(time, timeUnit); 
/*    */   }
/*    */   
/*    */   public void awaitReadable() throws IOException {
/* 58 */     if (!this.ready) this.next.awaitReadable(); 
/*    */   }
/*    */   
/*    */   public void terminateReads() throws IOException {
/* 62 */     this.receiveBuffer.free();
/* 63 */     this.next.terminateReads();
/*    */   }
/*    */   
/*    */   public int receive(ByteBuffer dst) throws IOException {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: getfield receiveBuffer : Lorg/xnio/Pooled;
/*    */     //   4: invokeinterface getResource : ()Ljava/lang/Object;
/*    */     //   9: checkcast java/nio/ByteBuffer
/*    */     //   12: astore_2
/*    */     //   13: aload_0
/*    */     //   14: getfield next : Lorg/xnio/conduits/Conduit;
/*    */     //   17: checkcast org/xnio/conduits/StreamSourceConduit
/*    */     //   20: aload_2
/*    */     //   21: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*    */     //   26: istore_3
/*    */     //   27: iload_3
/*    */     //   28: ifgt -> 13
/*    */     //   31: aload_2
/*    */     //   32: invokevirtual position : ()I
/*    */     //   35: iconst_4
/*    */     //   36: if_icmpge -> 56
/*    */     //   39: iload_3
/*    */     //   40: iconst_m1
/*    */     //   41: if_icmpne -> 49
/*    */     //   44: aload_2
/*    */     //   45: invokevirtual clear : ()Ljava/nio/Buffer;
/*    */     //   48: pop
/*    */     //   49: aload_0
/*    */     //   50: iconst_0
/*    */     //   51: putfield ready : Z
/*    */     //   54: iload_3
/*    */     //   55: ireturn
/*    */     //   56: aload_2
/*    */     //   57: invokevirtual flip : ()Ljava/nio/Buffer;
/*    */     //   60: pop
/*    */     //   61: aload_2
/*    */     //   62: invokevirtual getInt : ()I
/*    */     //   65: istore #4
/*    */     //   67: iload #4
/*    */     //   69: iflt -> 83
/*    */     //   72: iload #4
/*    */     //   74: aload_2
/*    */     //   75: invokevirtual capacity : ()I
/*    */     //   78: iconst_4
/*    */     //   79: isub
/*    */     //   80: if_icmple -> 100
/*    */     //   83: aload_2
/*    */     //   84: iconst_4
/*    */     //   85: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   88: pop
/*    */     //   89: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*    */     //   92: iload #4
/*    */     //   94: invokeinterface recvInvalidMsgLength : (I)Ljava/io/IOException;
/*    */     //   99: athrow
/*    */     //   100: aload_2
/*    */     //   101: invokevirtual remaining : ()I
/*    */     //   104: iload #4
/*    */     //   106: if_icmpge -> 176
/*    */     //   109: iload_3
/*    */     //   110: iconst_m1
/*    */     //   111: if_icmpne -> 122
/*    */     //   114: aload_2
/*    */     //   115: invokevirtual clear : ()Ljava/nio/Buffer;
/*    */     //   118: pop
/*    */     //   119: goto -> 128
/*    */     //   122: aload_2
/*    */     //   123: iconst_4
/*    */     //   124: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   127: pop
/*    */     //   128: aload_0
/*    */     //   129: iconst_0
/*    */     //   130: putfield ready : Z
/*    */     //   133: iload_3
/*    */     //   134: istore #5
/*    */     //   136: iload_3
/*    */     //   137: iconst_m1
/*    */     //   138: if_icmpeq -> 173
/*    */     //   141: aload_2
/*    */     //   142: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   145: pop
/*    */     //   146: aload_2
/*    */     //   147: invokevirtual position : ()I
/*    */     //   150: iconst_4
/*    */     //   151: if_icmplt -> 173
/*    */     //   154: aload_2
/*    */     //   155: invokevirtual position : ()I
/*    */     //   158: iconst_4
/*    */     //   159: aload_2
/*    */     //   160: iconst_0
/*    */     //   161: invokevirtual getInt : (I)I
/*    */     //   164: iadd
/*    */     //   165: if_icmplt -> 173
/*    */     //   168: aload_0
/*    */     //   169: iconst_1
/*    */     //   170: putfield ready : Z
/*    */     //   173: iload #5
/*    */     //   175: ireturn
/*    */     //   176: aload_1
/*    */     //   177: invokevirtual hasRemaining : ()Z
/*    */     //   180: ifeq -> 232
/*    */     //   183: iload #4
/*    */     //   185: aload_1
/*    */     //   186: aload_2
/*    */     //   187: invokestatic copy : (ILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
/*    */     //   190: istore #5
/*    */     //   192: iload_3
/*    */     //   193: iconst_m1
/*    */     //   194: if_icmpeq -> 229
/*    */     //   197: aload_2
/*    */     //   198: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   201: pop
/*    */     //   202: aload_2
/*    */     //   203: invokevirtual position : ()I
/*    */     //   206: iconst_4
/*    */     //   207: if_icmplt -> 229
/*    */     //   210: aload_2
/*    */     //   211: invokevirtual position : ()I
/*    */     //   214: iconst_4
/*    */     //   215: aload_2
/*    */     //   216: iconst_0
/*    */     //   217: invokevirtual getInt : (I)I
/*    */     //   220: iadd
/*    */     //   221: if_icmplt -> 229
/*    */     //   224: aload_0
/*    */     //   225: iconst_1
/*    */     //   226: putfield ready : Z
/*    */     //   229: iload #5
/*    */     //   231: ireturn
/*    */     //   232: aload_2
/*    */     //   233: iload #4
/*    */     //   235: invokestatic skip : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   238: pop
/*    */     //   239: iconst_0
/*    */     //   240: istore #5
/*    */     //   242: iload_3
/*    */     //   243: iconst_m1
/*    */     //   244: if_icmpeq -> 279
/*    */     //   247: aload_2
/*    */     //   248: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   251: pop
/*    */     //   252: aload_2
/*    */     //   253: invokevirtual position : ()I
/*    */     //   256: iconst_4
/*    */     //   257: if_icmplt -> 279
/*    */     //   260: aload_2
/*    */     //   261: invokevirtual position : ()I
/*    */     //   264: iconst_4
/*    */     //   265: aload_2
/*    */     //   266: iconst_0
/*    */     //   267: invokevirtual getInt : (I)I
/*    */     //   270: iadd
/*    */     //   271: if_icmplt -> 279
/*    */     //   274: aload_0
/*    */     //   275: iconst_1
/*    */     //   276: putfield ready : Z
/*    */     //   279: iload #5
/*    */     //   281: ireturn
/*    */     //   282: astore #6
/*    */     //   284: iload_3
/*    */     //   285: iconst_m1
/*    */     //   286: if_icmpeq -> 321
/*    */     //   289: aload_2
/*    */     //   290: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   293: pop
/*    */     //   294: aload_2
/*    */     //   295: invokevirtual position : ()I
/*    */     //   298: iconst_4
/*    */     //   299: if_icmplt -> 321
/*    */     //   302: aload_2
/*    */     //   303: invokevirtual position : ()I
/*    */     //   306: iconst_4
/*    */     //   307: aload_2
/*    */     //   308: iconst_0
/*    */     //   309: invokevirtual getInt : (I)I
/*    */     //   312: iadd
/*    */     //   313: if_icmplt -> 321
/*    */     //   316: aload_0
/*    */     //   317: iconst_1
/*    */     //   318: putfield ready : Z
/*    */     //   321: aload #6
/*    */     //   323: athrow
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #67	-> 0
/*    */     //   #70	-> 13
/*    */     //   #71	-> 27
/*    */     //   #72	-> 31
/*    */     //   #73	-> 39
/*    */     //   #74	-> 44
/*    */     //   #76	-> 49
/*    */     //   #77	-> 54
/*    */     //   #79	-> 56
/*    */     //   #81	-> 61
/*    */     //   #82	-> 67
/*    */     //   #83	-> 83
/*    */     //   #84	-> 89
/*    */     //   #86	-> 100
/*    */     //   #87	-> 109
/*    */     //   #88	-> 114
/*    */     //   #90	-> 122
/*    */     //   #92	-> 128
/*    */     //   #94	-> 133
/*    */     //   #103	-> 136
/*    */     //   #104	-> 141
/*    */     //   #105	-> 146
/*    */     //   #107	-> 168
/*    */     //   #94	-> 173
/*    */     //   #96	-> 176
/*    */     //   #97	-> 183
/*    */     //   #103	-> 192
/*    */     //   #104	-> 197
/*    */     //   #105	-> 202
/*    */     //   #107	-> 224
/*    */     //   #97	-> 229
/*    */     //   #99	-> 232
/*    */     //   #100	-> 239
/*    */     //   #103	-> 242
/*    */     //   #104	-> 247
/*    */     //   #105	-> 252
/*    */     //   #107	-> 274
/*    */     //   #100	-> 279
/*    */     //   #103	-> 282
/*    */     //   #104	-> 289
/*    */     //   #105	-> 294
/*    */     //   #107	-> 316
/*    */     //   #110	-> 321
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   67	215	4	length	I
/*    */     //   0	324	0	this	Lorg/xnio/conduits/FramingMessageSourceConduit;
/*    */     //   0	324	1	dst	Ljava/nio/ByteBuffer;
/*    */     //   13	311	2	receiveBuffer	Ljava/nio/ByteBuffer;
/*    */     //   27	297	3	res	I
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   61	136	282	finally
/*    */     //   176	192	282	finally
/*    */     //   232	242	282	finally
/*    */     //   282	284	282	finally
/*    */   }
/*    */   
/*    */   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: getfield receiveBuffer : Lorg/xnio/Pooled;
/*    */     //   4: invokeinterface getResource : ()Ljava/lang/Object;
/*    */     //   9: checkcast java/nio/ByteBuffer
/*    */     //   12: astore #4
/*    */     //   14: aload_0
/*    */     //   15: getfield next : Lorg/xnio/conduits/Conduit;
/*    */     //   18: checkcast org/xnio/conduits/StreamSourceConduit
/*    */     //   21: aload #4
/*    */     //   23: invokeinterface read : (Ljava/nio/ByteBuffer;)I
/*    */     //   28: istore #5
/*    */     //   30: iload #5
/*    */     //   32: ifgt -> 14
/*    */     //   35: aload #4
/*    */     //   37: invokevirtual position : ()I
/*    */     //   40: iconst_4
/*    */     //   41: if_icmpge -> 65
/*    */     //   44: iload #5
/*    */     //   46: iconst_m1
/*    */     //   47: if_icmpne -> 56
/*    */     //   50: aload #4
/*    */     //   52: invokevirtual clear : ()Ljava/nio/Buffer;
/*    */     //   55: pop
/*    */     //   56: aload_0
/*    */     //   57: iconst_0
/*    */     //   58: putfield ready : Z
/*    */     //   61: iload #5
/*    */     //   63: i2l
/*    */     //   64: lreturn
/*    */     //   65: aload #4
/*    */     //   67: invokevirtual flip : ()Ljava/nio/Buffer;
/*    */     //   70: pop
/*    */     //   71: aload #4
/*    */     //   73: invokevirtual getInt : ()I
/*    */     //   76: istore #6
/*    */     //   78: iload #6
/*    */     //   80: iflt -> 95
/*    */     //   83: iload #6
/*    */     //   85: aload #4
/*    */     //   87: invokevirtual capacity : ()I
/*    */     //   90: iconst_4
/*    */     //   91: isub
/*    */     //   92: if_icmple -> 113
/*    */     //   95: aload #4
/*    */     //   97: iconst_4
/*    */     //   98: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   101: pop
/*    */     //   102: getstatic org/xnio/_private/Messages.msg : Lorg/xnio/_private/Messages;
/*    */     //   105: iload #6
/*    */     //   107: invokeinterface recvInvalidMsgLength : (I)Ljava/io/IOException;
/*    */     //   112: athrow
/*    */     //   113: aload #4
/*    */     //   115: invokevirtual remaining : ()I
/*    */     //   118: iload #6
/*    */     //   120: if_icmpge -> 200
/*    */     //   123: iload #5
/*    */     //   125: iconst_m1
/*    */     //   126: if_icmpne -> 138
/*    */     //   129: aload #4
/*    */     //   131: invokevirtual clear : ()Ljava/nio/Buffer;
/*    */     //   134: pop
/*    */     //   135: goto -> 145
/*    */     //   138: aload #4
/*    */     //   140: iconst_4
/*    */     //   141: invokestatic unget : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   144: pop
/*    */     //   145: aload_0
/*    */     //   146: iconst_0
/*    */     //   147: putfield ready : Z
/*    */     //   150: iload #5
/*    */     //   152: i2l
/*    */     //   153: lstore #7
/*    */     //   155: iload #5
/*    */     //   157: iconst_m1
/*    */     //   158: if_icmpeq -> 197
/*    */     //   161: aload #4
/*    */     //   163: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   166: pop
/*    */     //   167: aload #4
/*    */     //   169: invokevirtual position : ()I
/*    */     //   172: iconst_4
/*    */     //   173: if_icmplt -> 197
/*    */     //   176: aload #4
/*    */     //   178: invokevirtual position : ()I
/*    */     //   181: iconst_4
/*    */     //   182: aload #4
/*    */     //   184: iconst_0
/*    */     //   185: invokevirtual getInt : (I)I
/*    */     //   188: iadd
/*    */     //   189: if_icmplt -> 197
/*    */     //   192: aload_0
/*    */     //   193: iconst_1
/*    */     //   194: putfield ready : Z
/*    */     //   197: lload #7
/*    */     //   199: lreturn
/*    */     //   200: aload_1
/*    */     //   201: iload_2
/*    */     //   202: iload_3
/*    */     //   203: invokestatic hasRemaining : ([Ljava/nio/Buffer;II)Z
/*    */     //   206: ifeq -> 267
/*    */     //   209: iload #6
/*    */     //   211: aload_1
/*    */     //   212: iload_2
/*    */     //   213: iload_3
/*    */     //   214: aload #4
/*    */     //   216: invokestatic copy : (I[Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;)I
/*    */     //   219: i2l
/*    */     //   220: lstore #7
/*    */     //   222: iload #5
/*    */     //   224: iconst_m1
/*    */     //   225: if_icmpeq -> 264
/*    */     //   228: aload #4
/*    */     //   230: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   233: pop
/*    */     //   234: aload #4
/*    */     //   236: invokevirtual position : ()I
/*    */     //   239: iconst_4
/*    */     //   240: if_icmplt -> 264
/*    */     //   243: aload #4
/*    */     //   245: invokevirtual position : ()I
/*    */     //   248: iconst_4
/*    */     //   249: aload #4
/*    */     //   251: iconst_0
/*    */     //   252: invokevirtual getInt : (I)I
/*    */     //   255: iadd
/*    */     //   256: if_icmplt -> 264
/*    */     //   259: aload_0
/*    */     //   260: iconst_1
/*    */     //   261: putfield ready : Z
/*    */     //   264: lload #7
/*    */     //   266: lreturn
/*    */     //   267: aload #4
/*    */     //   269: iload #6
/*    */     //   271: invokestatic skip : (Ljava/nio/Buffer;I)Ljava/nio/Buffer;
/*    */     //   274: pop
/*    */     //   275: lconst_0
/*    */     //   276: lstore #7
/*    */     //   278: iload #5
/*    */     //   280: iconst_m1
/*    */     //   281: if_icmpeq -> 320
/*    */     //   284: aload #4
/*    */     //   286: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   289: pop
/*    */     //   290: aload #4
/*    */     //   292: invokevirtual position : ()I
/*    */     //   295: iconst_4
/*    */     //   296: if_icmplt -> 320
/*    */     //   299: aload #4
/*    */     //   301: invokevirtual position : ()I
/*    */     //   304: iconst_4
/*    */     //   305: aload #4
/*    */     //   307: iconst_0
/*    */     //   308: invokevirtual getInt : (I)I
/*    */     //   311: iadd
/*    */     //   312: if_icmplt -> 320
/*    */     //   315: aload_0
/*    */     //   316: iconst_1
/*    */     //   317: putfield ready : Z
/*    */     //   320: lload #7
/*    */     //   322: lreturn
/*    */     //   323: astore #9
/*    */     //   325: iload #5
/*    */     //   327: iconst_m1
/*    */     //   328: if_icmpeq -> 367
/*    */     //   331: aload #4
/*    */     //   333: invokevirtual compact : ()Ljava/nio/ByteBuffer;
/*    */     //   336: pop
/*    */     //   337: aload #4
/*    */     //   339: invokevirtual position : ()I
/*    */     //   342: iconst_4
/*    */     //   343: if_icmplt -> 367
/*    */     //   346: aload #4
/*    */     //   348: invokevirtual position : ()I
/*    */     //   351: iconst_4
/*    */     //   352: aload #4
/*    */     //   354: iconst_0
/*    */     //   355: invokevirtual getInt : (I)I
/*    */     //   358: iadd
/*    */     //   359: if_icmplt -> 367
/*    */     //   362: aload_0
/*    */     //   363: iconst_1
/*    */     //   364: putfield ready : Z
/*    */     //   367: aload #9
/*    */     //   369: athrow
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #114	-> 0
/*    */     //   #117	-> 14
/*    */     //   #118	-> 30
/*    */     //   #119	-> 35
/*    */     //   #120	-> 44
/*    */     //   #121	-> 50
/*    */     //   #123	-> 56
/*    */     //   #124	-> 61
/*    */     //   #126	-> 65
/*    */     //   #128	-> 71
/*    */     //   #129	-> 78
/*    */     //   #130	-> 95
/*    */     //   #131	-> 102
/*    */     //   #133	-> 113
/*    */     //   #134	-> 123
/*    */     //   #135	-> 129
/*    */     //   #137	-> 138
/*    */     //   #139	-> 145
/*    */     //   #141	-> 150
/*    */     //   #150	-> 155
/*    */     //   #151	-> 161
/*    */     //   #152	-> 167
/*    */     //   #154	-> 192
/*    */     //   #141	-> 197
/*    */     //   #143	-> 200
/*    */     //   #144	-> 209
/*    */     //   #150	-> 222
/*    */     //   #151	-> 228
/*    */     //   #152	-> 234
/*    */     //   #154	-> 259
/*    */     //   #144	-> 264
/*    */     //   #146	-> 267
/*    */     //   #147	-> 275
/*    */     //   #150	-> 278
/*    */     //   #151	-> 284
/*    */     //   #152	-> 290
/*    */     //   #154	-> 315
/*    */     //   #147	-> 320
/*    */     //   #150	-> 323
/*    */     //   #151	-> 331
/*    */     //   #152	-> 337
/*    */     //   #154	-> 362
/*    */     //   #157	-> 367
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   78	245	6	length	I
/*    */     //   0	370	0	this	Lorg/xnio/conduits/FramingMessageSourceConduit;
/*    */     //   0	370	1	dsts	[Ljava/nio/ByteBuffer;
/*    */     //   0	370	2	offs	I
/*    */     //   0	370	3	len	I
/*    */     //   14	356	4	receiveBuffer	Ljava/nio/ByteBuffer;
/*    */     //   30	340	5	res	I
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   71	155	323	finally
/*    */     //   200	222	323	finally
/*    */     //   267	278	323	finally
/*    */     //   323	325	323	finally
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\FramingMessageSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */