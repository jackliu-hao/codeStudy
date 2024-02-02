/*    */ package com.google.zxing.common;
/*    */ 
/*    */ import com.google.zxing.DecodeHintType;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Map;
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
/*    */ public final class StringUtils
/*    */ {
/* 32 */   private static final String PLATFORM_DEFAULT_ENCODING = Charset.defaultCharset().name();
/*    */   public static final String SHIFT_JIS = "SJIS";
/*    */   public static final String GB2312 = "GB2312";
/*    */   private static final String EUC_JP = "EUC_JP";
/*    */   private static final String UTF8 = "UTF8";
/*    */   private static final String ISO88591 = "ISO8859_1";
/* 38 */   private static final boolean ASSUME_SHIFT_JIS = ("SJIS"
/* 39 */     .equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING) || "EUC_JP"
/* 40 */     .equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING));
/*    */   
/*    */   public static String guessEncoding(byte[] bytes, Map<DecodeHintType, ?> hints) {
/*    */     // Byte code:
/*    */     //   0: aload_1
/*    */     //   1: ifnull -> 29
/*    */     //   4: aload_1
/*    */     //   5: getstatic com/google/zxing/DecodeHintType.CHARACTER_SET : Lcom/google/zxing/DecodeHintType;
/*    */     //   8: invokeinterface containsKey : (Ljava/lang/Object;)Z
/*    */     //   13: ifeq -> 29
/*    */     //   16: aload_1
/*    */     //   17: getstatic com/google/zxing/DecodeHintType.CHARACTER_SET : Lcom/google/zxing/DecodeHintType;
/*    */     //   20: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*    */     //   25: invokevirtual toString : ()Ljava/lang/String;
/*    */     //   28: areturn
/*    */     //   29: aload_0
/*    */     //   30: arraylength
/*    */     //   31: istore_2
/*    */     //   32: iconst_1
/*    */     //   33: istore_3
/*    */     //   34: iconst_1
/*    */     //   35: istore #4
/*    */     //   37: iconst_1
/*    */     //   38: istore #5
/*    */     //   40: iconst_0
/*    */     //   41: istore #6
/*    */     //   43: iconst_0
/*    */     //   44: istore #7
/*    */     //   46: iconst_0
/*    */     //   47: istore #8
/*    */     //   49: iconst_0
/*    */     //   50: istore #9
/*    */     //   52: iconst_0
/*    */     //   53: istore #10
/*    */     //   55: iconst_0
/*    */     //   56: istore #11
/*    */     //   58: iconst_0
/*    */     //   59: istore #12
/*    */     //   61: iconst_0
/*    */     //   62: istore #13
/*    */     //   64: iconst_0
/*    */     //   65: istore #14
/*    */     //   67: iconst_0
/*    */     //   68: istore #15
/*    */     //   70: iconst_0
/*    */     //   71: istore #16
/*    */     //   73: aload_0
/*    */     //   74: arraylength
/*    */     //   75: iconst_3
/*    */     //   76: if_icmple -> 107
/*    */     //   79: aload_0
/*    */     //   80: iconst_0
/*    */     //   81: baload
/*    */     //   82: bipush #-17
/*    */     //   84: if_icmpne -> 107
/*    */     //   87: aload_0
/*    */     //   88: iconst_1
/*    */     //   89: baload
/*    */     //   90: bipush #-69
/*    */     //   92: if_icmpne -> 107
/*    */     //   95: aload_0
/*    */     //   96: iconst_2
/*    */     //   97: baload
/*    */     //   98: bipush #-65
/*    */     //   100: if_icmpne -> 107
/*    */     //   103: iconst_1
/*    */     //   104: goto -> 108
/*    */     //   107: iconst_0
/*    */     //   108: istore #17
/*    */     //   110: iconst_0
/*    */     //   111: istore #18
/*    */     //   113: iload #18
/*    */     //   115: iload_2
/*    */     //   116: if_icmpge -> 453
/*    */     //   119: iload_3
/*    */     //   120: ifne -> 133
/*    */     //   123: iload #4
/*    */     //   125: ifne -> 133
/*    */     //   128: iload #5
/*    */     //   130: ifeq -> 453
/*    */     //   133: aload_0
/*    */     //   134: iload #18
/*    */     //   136: baload
/*    */     //   137: sipush #255
/*    */     //   140: iand
/*    */     //   141: istore #19
/*    */     //   143: iload #5
/*    */     //   145: ifeq -> 239
/*    */     //   148: iload #6
/*    */     //   150: ifle -> 168
/*    */     //   153: iload #19
/*    */     //   155: sipush #128
/*    */     //   158: iand
/*    */     //   159: ifeq -> 236
/*    */     //   162: iinc #6, -1
/*    */     //   165: goto -> 239
/*    */     //   168: iload #19
/*    */     //   170: sipush #128
/*    */     //   173: iand
/*    */     //   174: ifeq -> 239
/*    */     //   177: iload #19
/*    */     //   179: bipush #64
/*    */     //   181: iand
/*    */     //   182: ifeq -> 236
/*    */     //   185: iinc #6, 1
/*    */     //   188: iload #19
/*    */     //   190: bipush #32
/*    */     //   192: iand
/*    */     //   193: ifne -> 202
/*    */     //   196: iinc #7, 1
/*    */     //   199: goto -> 239
/*    */     //   202: iinc #6, 1
/*    */     //   205: iload #19
/*    */     //   207: bipush #16
/*    */     //   209: iand
/*    */     //   210: ifne -> 219
/*    */     //   213: iinc #8, 1
/*    */     //   216: goto -> 239
/*    */     //   219: iinc #6, 1
/*    */     //   222: iload #19
/*    */     //   224: bipush #8
/*    */     //   226: iand
/*    */     //   227: ifne -> 236
/*    */     //   230: iinc #9, 1
/*    */     //   233: goto -> 239
/*    */     //   236: iconst_0
/*    */     //   237: istore #5
/*    */     //   239: iload_3
/*    */     //   240: ifeq -> 298
/*    */     //   243: iload #19
/*    */     //   245: bipush #127
/*    */     //   247: if_icmple -> 263
/*    */     //   250: iload #19
/*    */     //   252: sipush #160
/*    */     //   255: if_icmpge -> 263
/*    */     //   258: iconst_0
/*    */     //   259: istore_3
/*    */     //   260: goto -> 298
/*    */     //   263: iload #19
/*    */     //   265: sipush #159
/*    */     //   268: if_icmple -> 298
/*    */     //   271: iload #19
/*    */     //   273: sipush #192
/*    */     //   276: if_icmplt -> 295
/*    */     //   279: iload #19
/*    */     //   281: sipush #215
/*    */     //   284: if_icmpeq -> 295
/*    */     //   287: iload #19
/*    */     //   289: sipush #247
/*    */     //   292: if_icmpne -> 298
/*    */     //   295: iinc #16, 1
/*    */     //   298: iload #4
/*    */     //   300: ifeq -> 447
/*    */     //   303: iload #10
/*    */     //   305: ifle -> 342
/*    */     //   308: iload #19
/*    */     //   310: bipush #64
/*    */     //   312: if_icmplt -> 330
/*    */     //   315: iload #19
/*    */     //   317: bipush #127
/*    */     //   319: if_icmpeq -> 330
/*    */     //   322: iload #19
/*    */     //   324: sipush #252
/*    */     //   327: if_icmple -> 336
/*    */     //   330: iconst_0
/*    */     //   331: istore #4
/*    */     //   333: goto -> 447
/*    */     //   336: iinc #10, -1
/*    */     //   339: goto -> 447
/*    */     //   342: iload #19
/*    */     //   344: sipush #128
/*    */     //   347: if_icmpeq -> 366
/*    */     //   350: iload #19
/*    */     //   352: sipush #160
/*    */     //   355: if_icmpeq -> 366
/*    */     //   358: iload #19
/*    */     //   360: sipush #239
/*    */     //   363: if_icmple -> 372
/*    */     //   366: iconst_0
/*    */     //   367: istore #4
/*    */     //   369: goto -> 447
/*    */     //   372: iload #19
/*    */     //   374: sipush #160
/*    */     //   377: if_icmple -> 411
/*    */     //   380: iload #19
/*    */     //   382: sipush #224
/*    */     //   385: if_icmpge -> 411
/*    */     //   388: iinc #11, 1
/*    */     //   391: iconst_0
/*    */     //   392: istore #13
/*    */     //   394: iinc #12, 1
/*    */     //   397: iload #12
/*    */     //   399: iload #14
/*    */     //   401: if_icmple -> 447
/*    */     //   404: iload #12
/*    */     //   406: istore #14
/*    */     //   408: goto -> 447
/*    */     //   411: iload #19
/*    */     //   413: bipush #127
/*    */     //   415: if_icmple -> 441
/*    */     //   418: iinc #10, 1
/*    */     //   421: iconst_0
/*    */     //   422: istore #12
/*    */     //   424: iinc #13, 1
/*    */     //   427: iload #13
/*    */     //   429: iload #15
/*    */     //   431: if_icmple -> 447
/*    */     //   434: iload #13
/*    */     //   436: istore #15
/*    */     //   438: goto -> 447
/*    */     //   441: iconst_0
/*    */     //   442: istore #12
/*    */     //   444: iconst_0
/*    */     //   445: istore #13
/*    */     //   447: iinc #18, 1
/*    */     //   450: goto -> 113
/*    */     //   453: iload #5
/*    */     //   455: ifeq -> 466
/*    */     //   458: iload #6
/*    */     //   460: ifle -> 466
/*    */     //   463: iconst_0
/*    */     //   464: istore #5
/*    */     //   466: iload #4
/*    */     //   468: ifeq -> 479
/*    */     //   471: iload #10
/*    */     //   473: ifle -> 479
/*    */     //   476: iconst_0
/*    */     //   477: istore #4
/*    */     //   479: iload #5
/*    */     //   481: ifeq -> 503
/*    */     //   484: iload #17
/*    */     //   486: ifne -> 500
/*    */     //   489: iload #7
/*    */     //   491: iload #8
/*    */     //   493: iadd
/*    */     //   494: iload #9
/*    */     //   496: iadd
/*    */     //   497: ifle -> 503
/*    */     //   500: ldc 'UTF8'
/*    */     //   502: areturn
/*    */     //   503: iload #4
/*    */     //   505: ifeq -> 529
/*    */     //   508: getstatic com/google/zxing/common/StringUtils.ASSUME_SHIFT_JIS : Z
/*    */     //   511: ifne -> 526
/*    */     //   514: iload #14
/*    */     //   516: iconst_3
/*    */     //   517: if_icmpge -> 526
/*    */     //   520: iload #15
/*    */     //   522: iconst_3
/*    */     //   523: if_icmplt -> 529
/*    */     //   526: ldc 'SJIS'
/*    */     //   528: areturn
/*    */     //   529: iload_3
/*    */     //   530: ifeq -> 565
/*    */     //   533: iload #4
/*    */     //   535: ifeq -> 565
/*    */     //   538: iload #14
/*    */     //   540: iconst_2
/*    */     //   541: if_icmpne -> 550
/*    */     //   544: iload #11
/*    */     //   546: iconst_2
/*    */     //   547: if_icmpeq -> 559
/*    */     //   550: iload #16
/*    */     //   552: bipush #10
/*    */     //   554: imul
/*    */     //   555: iload_2
/*    */     //   556: if_icmplt -> 562
/*    */     //   559: ldc 'SJIS'
/*    */     //   561: areturn
/*    */     //   562: ldc 'ISO8859_1'
/*    */     //   564: areturn
/*    */     //   565: iload_3
/*    */     //   566: ifeq -> 572
/*    */     //   569: ldc 'ISO8859_1'
/*    */     //   571: areturn
/*    */     //   572: iload #4
/*    */     //   574: ifeq -> 580
/*    */     //   577: ldc 'SJIS'
/*    */     //   579: areturn
/*    */     //   580: iload #5
/*    */     //   582: ifeq -> 588
/*    */     //   585: ldc 'UTF8'
/*    */     //   587: areturn
/*    */     //   588: getstatic com/google/zxing/common/StringUtils.PLATFORM_DEFAULT_ENCODING : Ljava/lang/String;
/*    */     //   591: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #52	-> 0
/*    */     //   #53	-> 16
/*    */     //   #57	-> 29
/*    */     //   #58	-> 32
/*    */     //   #59	-> 34
/*    */     //   #60	-> 37
/*    */     //   #61	-> 40
/*    */     //   #63	-> 43
/*    */     //   #64	-> 46
/*    */     //   #65	-> 49
/*    */     //   #66	-> 52
/*    */     //   #68	-> 55
/*    */     //   #70	-> 58
/*    */     //   #71	-> 61
/*    */     //   #72	-> 64
/*    */     //   #73	-> 67
/*    */     //   #76	-> 70
/*    */     //   #78	-> 73
/*    */     //   #83	-> 110
/*    */     //   #84	-> 113
/*    */     //   #87	-> 133
/*    */     //   #90	-> 143
/*    */     //   #91	-> 148
/*    */     //   #92	-> 153
/*    */     //   #95	-> 162
/*    */     //   #97	-> 168
/*    */     //   #98	-> 177
/*    */     //   #101	-> 185
/*    */     //   #102	-> 188
/*    */     //   #103	-> 196
/*    */     //   #105	-> 202
/*    */     //   #106	-> 205
/*    */     //   #107	-> 213
/*    */     //   #109	-> 219
/*    */     //   #110	-> 222
/*    */     //   #111	-> 230
/*    */     //   #113	-> 236
/*    */     //   #124	-> 239
/*    */     //   #125	-> 243
/*    */     //   #126	-> 258
/*    */     //   #127	-> 263
/*    */     //   #128	-> 271
/*    */     //   #129	-> 295
/*    */     //   #139	-> 298
/*    */     //   #140	-> 303
/*    */     //   #141	-> 308
/*    */     //   #142	-> 330
/*    */     //   #144	-> 336
/*    */     //   #146	-> 342
/*    */     //   #147	-> 366
/*    */     //   #148	-> 372
/*    */     //   #149	-> 388
/*    */     //   #150	-> 391
/*    */     //   #151	-> 394
/*    */     //   #152	-> 397
/*    */     //   #153	-> 404
/*    */     //   #155	-> 411
/*    */     //   #156	-> 418
/*    */     //   #158	-> 421
/*    */     //   #159	-> 424
/*    */     //   #160	-> 427
/*    */     //   #161	-> 434
/*    */     //   #165	-> 441
/*    */     //   #166	-> 444
/*    */     //   #85	-> 447
/*    */     //   #171	-> 453
/*    */     //   #172	-> 463
/*    */     //   #174	-> 466
/*    */     //   #175	-> 476
/*    */     //   #179	-> 479
/*    */     //   #180	-> 500
/*    */     //   #183	-> 503
/*    */     //   #184	-> 526
/*    */     //   #191	-> 529
/*    */     //   #192	-> 538
/*    */     //   #197	-> 565
/*    */     //   #198	-> 569
/*    */     //   #200	-> 572
/*    */     //   #201	-> 577
/*    */     //   #203	-> 580
/*    */     //   #204	-> 585
/*    */     //   #207	-> 588
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	592	0	bytes	[B
/*    */     //   0	592	1	hints	Ljava/util/Map;
/*    */     //   32	560	2	length	I
/*    */     //   34	558	3	canBeISO88591	Z
/*    */     //   37	555	4	canBeShiftJIS	Z
/*    */     //   40	552	5	canBeUTF8	Z
/*    */     //   43	549	6	utf8BytesLeft	I
/*    */     //   46	546	7	utf2BytesChars	I
/*    */     //   49	543	8	utf3BytesChars	I
/*    */     //   52	540	9	utf4BytesChars	I
/*    */     //   55	537	10	sjisBytesLeft	I
/*    */     //   58	534	11	sjisKatakanaChars	I
/*    */     //   61	531	12	sjisCurKatakanaWordLength	I
/*    */     //   64	528	13	sjisCurDoubleBytesWordLength	I
/*    */     //   67	525	14	sjisMaxKatakanaWordLength	I
/*    */     //   70	522	15	sjisMaxDoubleBytesWordLength	I
/*    */     //   73	519	16	isoHighOther	I
/*    */     //   110	482	17	utf8bom	Z
/*    */     //   113	340	18	i	I
/*    */     //   143	304	19	value	I
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	592	1	hints	Ljava/util/Map<Lcom/google/zxing/DecodeHintType;*>;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\StringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */