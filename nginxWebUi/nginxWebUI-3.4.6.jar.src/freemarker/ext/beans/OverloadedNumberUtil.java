/*      */ package freemarker.ext.beans;
/*      */ 
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class OverloadedNumberUtil
/*      */ {
/*      */   static final int BIG_MANTISSA_LOSS_PRICE = 40000;
/*      */   private static final long MAX_DOUBLE_OR_LONG = 9007199254740992L;
/*      */   private static final long MIN_DOUBLE_OR_LONG = -9007199254740992L;
/*      */   private static final int MAX_DOUBLE_OR_LONG_LOG_2 = 53;
/*      */   private static final int MAX_FLOAT_OR_INT = 16777216;
/*      */   private static final int MIN_FLOAT_OR_INT = -16777216;
/*      */   private static final int MAX_FLOAT_OR_INT_LOG_2 = 24;
/*      */   private static final double LOWEST_ABOVE_ZERO = 1.0E-6D;
/*      */   private static final double HIGHEST_BELOW_ONE = 0.999999D;
/*      */   
/*      */   static Number addFallbackType(Number num, int typeFlags) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual getClass : ()Ljava/lang/Class;
/*      */     //   4: astore_2
/*      */     //   5: aload_2
/*      */     //   6: ldc java/math/BigDecimal
/*      */     //   8: if_acmpne -> 50
/*      */     //   11: aload_0
/*      */     //   12: checkcast java/math/BigDecimal
/*      */     //   15: astore_3
/*      */     //   16: iload_1
/*      */     //   17: sipush #316
/*      */     //   20: iand
/*      */     //   21: ifeq -> 48
/*      */     //   24: iload_1
/*      */     //   25: sipush #704
/*      */     //   28: iand
/*      */     //   29: ifeq -> 48
/*      */     //   32: aload_3
/*      */     //   33: invokestatic isIntegerBigDecimal : (Ljava/math/BigDecimal;)Z
/*      */     //   36: ifeq -> 48
/*      */     //   39: new freemarker/ext/beans/OverloadedNumberUtil$IntegerBigDecimal
/*      */     //   42: dup
/*      */     //   43: aload_3
/*      */     //   44: invokespecial <init> : (Ljava/math/BigDecimal;)V
/*      */     //   47: areturn
/*      */     //   48: aload_3
/*      */     //   49: areturn
/*      */     //   50: aload_2
/*      */     //   51: ldc java/lang/Integer
/*      */     //   53: if_acmpne -> 130
/*      */     //   56: aload_0
/*      */     //   57: invokevirtual intValue : ()I
/*      */     //   60: istore_3
/*      */     //   61: iload_1
/*      */     //   62: iconst_4
/*      */     //   63: iand
/*      */     //   64: ifeq -> 93
/*      */     //   67: iload_3
/*      */     //   68: bipush #127
/*      */     //   70: if_icmpgt -> 93
/*      */     //   73: iload_3
/*      */     //   74: bipush #-128
/*      */     //   76: if_icmplt -> 93
/*      */     //   79: new freemarker/ext/beans/OverloadedNumberUtil$IntegerOrByte
/*      */     //   82: dup
/*      */     //   83: aload_0
/*      */     //   84: checkcast java/lang/Integer
/*      */     //   87: iload_3
/*      */     //   88: i2b
/*      */     //   89: invokespecial <init> : (Ljava/lang/Integer;B)V
/*      */     //   92: areturn
/*      */     //   93: iload_1
/*      */     //   94: bipush #8
/*      */     //   96: iand
/*      */     //   97: ifeq -> 128
/*      */     //   100: iload_3
/*      */     //   101: sipush #32767
/*      */     //   104: if_icmpgt -> 128
/*      */     //   107: iload_3
/*      */     //   108: sipush #-32768
/*      */     //   111: if_icmplt -> 128
/*      */     //   114: new freemarker/ext/beans/OverloadedNumberUtil$IntegerOrShort
/*      */     //   117: dup
/*      */     //   118: aload_0
/*      */     //   119: checkcast java/lang/Integer
/*      */     //   122: iload_3
/*      */     //   123: i2s
/*      */     //   124: invokespecial <init> : (Ljava/lang/Integer;S)V
/*      */     //   127: areturn
/*      */     //   128: aload_0
/*      */     //   129: areturn
/*      */     //   130: aload_2
/*      */     //   131: ldc java/lang/Long
/*      */     //   133: if_acmpne -> 255
/*      */     //   136: aload_0
/*      */     //   137: invokevirtual longValue : ()J
/*      */     //   140: lstore_3
/*      */     //   141: iload_1
/*      */     //   142: iconst_4
/*      */     //   143: iand
/*      */     //   144: ifeq -> 178
/*      */     //   147: lload_3
/*      */     //   148: ldc2_w 127
/*      */     //   151: lcmp
/*      */     //   152: ifgt -> 178
/*      */     //   155: lload_3
/*      */     //   156: ldc2_w -128
/*      */     //   159: lcmp
/*      */     //   160: iflt -> 178
/*      */     //   163: new freemarker/ext/beans/OverloadedNumberUtil$LongOrByte
/*      */     //   166: dup
/*      */     //   167: aload_0
/*      */     //   168: checkcast java/lang/Long
/*      */     //   171: lload_3
/*      */     //   172: l2i
/*      */     //   173: i2b
/*      */     //   174: invokespecial <init> : (Ljava/lang/Long;B)V
/*      */     //   177: areturn
/*      */     //   178: iload_1
/*      */     //   179: bipush #8
/*      */     //   181: iand
/*      */     //   182: ifeq -> 216
/*      */     //   185: lload_3
/*      */     //   186: ldc2_w 32767
/*      */     //   189: lcmp
/*      */     //   190: ifgt -> 216
/*      */     //   193: lload_3
/*      */     //   194: ldc2_w -32768
/*      */     //   197: lcmp
/*      */     //   198: iflt -> 216
/*      */     //   201: new freemarker/ext/beans/OverloadedNumberUtil$LongOrShort
/*      */     //   204: dup
/*      */     //   205: aload_0
/*      */     //   206: checkcast java/lang/Long
/*      */     //   209: lload_3
/*      */     //   210: l2i
/*      */     //   211: i2s
/*      */     //   212: invokespecial <init> : (Ljava/lang/Long;S)V
/*      */     //   215: areturn
/*      */     //   216: iload_1
/*      */     //   217: bipush #16
/*      */     //   219: iand
/*      */     //   220: ifeq -> 253
/*      */     //   223: lload_3
/*      */     //   224: ldc2_w 2147483647
/*      */     //   227: lcmp
/*      */     //   228: ifgt -> 253
/*      */     //   231: lload_3
/*      */     //   232: ldc2_w -2147483648
/*      */     //   235: lcmp
/*      */     //   236: iflt -> 253
/*      */     //   239: new freemarker/ext/beans/OverloadedNumberUtil$LongOrInteger
/*      */     //   242: dup
/*      */     //   243: aload_0
/*      */     //   244: checkcast java/lang/Long
/*      */     //   247: lload_3
/*      */     //   248: l2i
/*      */     //   249: invokespecial <init> : (Ljava/lang/Long;I)V
/*      */     //   252: areturn
/*      */     //   253: aload_0
/*      */     //   254: areturn
/*      */     //   255: aload_2
/*      */     //   256: ldc java/lang/Double
/*      */     //   258: if_acmpne -> 655
/*      */     //   261: aload_0
/*      */     //   262: invokevirtual doubleValue : ()D
/*      */     //   265: dstore_3
/*      */     //   266: iload_1
/*      */     //   267: sipush #316
/*      */     //   270: iand
/*      */     //   271: ifne -> 277
/*      */     //   274: goto -> 618
/*      */     //   277: dload_3
/*      */     //   278: ldc2_w 9.007199254740992E15
/*      */     //   281: dcmpl
/*      */     //   282: ifgt -> 618
/*      */     //   285: dload_3
/*      */     //   286: ldc2_w -9.007199254740992E15
/*      */     //   289: dcmpg
/*      */     //   290: ifge -> 296
/*      */     //   293: goto -> 618
/*      */     //   296: aload_0
/*      */     //   297: invokevirtual longValue : ()J
/*      */     //   300: lstore #5
/*      */     //   302: dload_3
/*      */     //   303: lload #5
/*      */     //   305: l2d
/*      */     //   306: dsub
/*      */     //   307: dstore #7
/*      */     //   309: dload #7
/*      */     //   311: dconst_0
/*      */     //   312: dcmpl
/*      */     //   313: ifne -> 322
/*      */     //   316: iconst_1
/*      */     //   317: istore #9
/*      */     //   319: goto -> 398
/*      */     //   322: dload #7
/*      */     //   324: dconst_0
/*      */     //   325: dcmpl
/*      */     //   326: ifle -> 365
/*      */     //   329: dload #7
/*      */     //   331: ldc2_w 1.0E-6
/*      */     //   334: dcmpg
/*      */     //   335: ifge -> 344
/*      */     //   338: iconst_0
/*      */     //   339: istore #9
/*      */     //   341: goto -> 398
/*      */     //   344: dload #7
/*      */     //   346: ldc2_w 0.999999
/*      */     //   349: dcmpl
/*      */     //   350: ifle -> 618
/*      */     //   353: iconst_0
/*      */     //   354: istore #9
/*      */     //   356: lload #5
/*      */     //   358: lconst_1
/*      */     //   359: ladd
/*      */     //   360: lstore #5
/*      */     //   362: goto -> 398
/*      */     //   365: dload #7
/*      */     //   367: ldc2_w -1.0E-6
/*      */     //   370: dcmpl
/*      */     //   371: ifle -> 380
/*      */     //   374: iconst_0
/*      */     //   375: istore #9
/*      */     //   377: goto -> 398
/*      */     //   380: dload #7
/*      */     //   382: ldc2_w -0.999999
/*      */     //   385: dcmpg
/*      */     //   386: ifge -> 618
/*      */     //   389: iconst_0
/*      */     //   390: istore #9
/*      */     //   392: lload #5
/*      */     //   394: lconst_1
/*      */     //   395: lsub
/*      */     //   396: lstore #5
/*      */     //   398: iload_1
/*      */     //   399: iconst_4
/*      */     //   400: iand
/*      */     //   401: ifeq -> 438
/*      */     //   404: lload #5
/*      */     //   406: ldc2_w 127
/*      */     //   409: lcmp
/*      */     //   410: ifgt -> 438
/*      */     //   413: lload #5
/*      */     //   415: ldc2_w -128
/*      */     //   418: lcmp
/*      */     //   419: iflt -> 438
/*      */     //   422: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrByte
/*      */     //   425: dup
/*      */     //   426: aload_0
/*      */     //   427: checkcast java/lang/Double
/*      */     //   430: lload #5
/*      */     //   432: l2i
/*      */     //   433: i2b
/*      */     //   434: invokespecial <init> : (Ljava/lang/Double;B)V
/*      */     //   437: areturn
/*      */     //   438: iload_1
/*      */     //   439: bipush #8
/*      */     //   441: iand
/*      */     //   442: ifeq -> 479
/*      */     //   445: lload #5
/*      */     //   447: ldc2_w 32767
/*      */     //   450: lcmp
/*      */     //   451: ifgt -> 479
/*      */     //   454: lload #5
/*      */     //   456: ldc2_w -32768
/*      */     //   459: lcmp
/*      */     //   460: iflt -> 479
/*      */     //   463: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrShort
/*      */     //   466: dup
/*      */     //   467: aload_0
/*      */     //   468: checkcast java/lang/Double
/*      */     //   471: lload #5
/*      */     //   473: l2i
/*      */     //   474: i2s
/*      */     //   475: invokespecial <init> : (Ljava/lang/Double;S)V
/*      */     //   478: areturn
/*      */     //   479: iload_1
/*      */     //   480: bipush #16
/*      */     //   482: iand
/*      */     //   483: ifeq -> 560
/*      */     //   486: lload #5
/*      */     //   488: ldc2_w 2147483647
/*      */     //   491: lcmp
/*      */     //   492: ifgt -> 560
/*      */     //   495: lload #5
/*      */     //   497: ldc2_w -2147483648
/*      */     //   500: lcmp
/*      */     //   501: iflt -> 560
/*      */     //   504: lload #5
/*      */     //   506: l2i
/*      */     //   507: istore #10
/*      */     //   509: iload_1
/*      */     //   510: bipush #64
/*      */     //   512: iand
/*      */     //   513: ifeq -> 546
/*      */     //   516: iload #10
/*      */     //   518: ldc -16777216
/*      */     //   520: if_icmplt -> 546
/*      */     //   523: iload #10
/*      */     //   525: ldc 16777216
/*      */     //   527: if_icmpgt -> 546
/*      */     //   530: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrIntegerOrFloat
/*      */     //   533: dup
/*      */     //   534: aload_0
/*      */     //   535: checkcast java/lang/Double
/*      */     //   538: iload #10
/*      */     //   540: invokespecial <init> : (Ljava/lang/Double;I)V
/*      */     //   543: goto -> 559
/*      */     //   546: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrInteger
/*      */     //   549: dup
/*      */     //   550: aload_0
/*      */     //   551: checkcast java/lang/Double
/*      */     //   554: iload #10
/*      */     //   556: invokespecial <init> : (Ljava/lang/Double;I)V
/*      */     //   559: areturn
/*      */     //   560: iload_1
/*      */     //   561: bipush #32
/*      */     //   563: iand
/*      */     //   564: ifeq -> 618
/*      */     //   567: iload #9
/*      */     //   569: ifeq -> 586
/*      */     //   572: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrLong
/*      */     //   575: dup
/*      */     //   576: aload_0
/*      */     //   577: checkcast java/lang/Double
/*      */     //   580: lload #5
/*      */     //   582: invokespecial <init> : (Ljava/lang/Double;J)V
/*      */     //   585: areturn
/*      */     //   586: lload #5
/*      */     //   588: ldc2_w -2147483648
/*      */     //   591: lcmp
/*      */     //   592: iflt -> 618
/*      */     //   595: lload #5
/*      */     //   597: ldc2_w 2147483647
/*      */     //   600: lcmp
/*      */     //   601: ifgt -> 618
/*      */     //   604: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrLong
/*      */     //   607: dup
/*      */     //   608: aload_0
/*      */     //   609: checkcast java/lang/Double
/*      */     //   612: lload #5
/*      */     //   614: invokespecial <init> : (Ljava/lang/Double;J)V
/*      */     //   617: areturn
/*      */     //   618: iload_1
/*      */     //   619: bipush #64
/*      */     //   621: iand
/*      */     //   622: ifeq -> 653
/*      */     //   625: dload_3
/*      */     //   626: ldc2_w -3.4028234663852886E38
/*      */     //   629: dcmpl
/*      */     //   630: iflt -> 653
/*      */     //   633: dload_3
/*      */     //   634: ldc2_w 3.4028234663852886E38
/*      */     //   637: dcmpg
/*      */     //   638: ifgt -> 653
/*      */     //   641: new freemarker/ext/beans/OverloadedNumberUtil$DoubleOrFloat
/*      */     //   644: dup
/*      */     //   645: aload_0
/*      */     //   646: checkcast java/lang/Double
/*      */     //   649: invokespecial <init> : (Ljava/lang/Double;)V
/*      */     //   652: areturn
/*      */     //   653: aload_0
/*      */     //   654: areturn
/*      */     //   655: aload_2
/*      */     //   656: ldc java/lang/Float
/*      */     //   658: if_acmpne -> 944
/*      */     //   661: aload_0
/*      */     //   662: invokevirtual floatValue : ()F
/*      */     //   665: fstore_3
/*      */     //   666: iload_1
/*      */     //   667: sipush #316
/*      */     //   670: iand
/*      */     //   671: ifne -> 677
/*      */     //   674: goto -> 942
/*      */     //   677: fload_3
/*      */     //   678: ldc 1.6777216E7
/*      */     //   680: fcmpl
/*      */     //   681: ifgt -> 942
/*      */     //   684: fload_3
/*      */     //   685: ldc -1.6777216E7
/*      */     //   687: fcmpg
/*      */     //   688: ifge -> 694
/*      */     //   691: goto -> 942
/*      */     //   694: aload_0
/*      */     //   695: invokevirtual intValue : ()I
/*      */     //   698: istore #4
/*      */     //   700: fload_3
/*      */     //   701: iload #4
/*      */     //   703: i2f
/*      */     //   704: fsub
/*      */     //   705: f2d
/*      */     //   706: dstore #5
/*      */     //   708: dload #5
/*      */     //   710: dconst_0
/*      */     //   711: dcmpl
/*      */     //   712: ifne -> 721
/*      */     //   715: iconst_1
/*      */     //   716: istore #7
/*      */     //   718: goto -> 805
/*      */     //   721: iload #4
/*      */     //   723: bipush #-128
/*      */     //   725: if_icmplt -> 942
/*      */     //   728: iload #4
/*      */     //   730: bipush #127
/*      */     //   732: if_icmpgt -> 942
/*      */     //   735: dload #5
/*      */     //   737: dconst_0
/*      */     //   738: dcmpl
/*      */     //   739: ifle -> 775
/*      */     //   742: dload #5
/*      */     //   744: ldc2_w 1.0E-5
/*      */     //   747: dcmpg
/*      */     //   748: ifge -> 757
/*      */     //   751: iconst_0
/*      */     //   752: istore #7
/*      */     //   754: goto -> 805
/*      */     //   757: dload #5
/*      */     //   759: ldc2_w 0.99999
/*      */     //   762: dcmpl
/*      */     //   763: ifle -> 942
/*      */     //   766: iconst_0
/*      */     //   767: istore #7
/*      */     //   769: iinc #4, 1
/*      */     //   772: goto -> 805
/*      */     //   775: dload #5
/*      */     //   777: ldc2_w -1.0E-5
/*      */     //   780: dcmpl
/*      */     //   781: ifle -> 790
/*      */     //   784: iconst_0
/*      */     //   785: istore #7
/*      */     //   787: goto -> 805
/*      */     //   790: dload #5
/*      */     //   792: ldc2_w -0.99999
/*      */     //   795: dcmpg
/*      */     //   796: ifge -> 942
/*      */     //   799: iconst_0
/*      */     //   800: istore #7
/*      */     //   802: iinc #4, -1
/*      */     //   805: iload_1
/*      */     //   806: iconst_4
/*      */     //   807: iand
/*      */     //   808: ifeq -> 840
/*      */     //   811: iload #4
/*      */     //   813: bipush #127
/*      */     //   815: if_icmpgt -> 840
/*      */     //   818: iload #4
/*      */     //   820: bipush #-128
/*      */     //   822: if_icmplt -> 840
/*      */     //   825: new freemarker/ext/beans/OverloadedNumberUtil$FloatOrByte
/*      */     //   828: dup
/*      */     //   829: aload_0
/*      */     //   830: checkcast java/lang/Float
/*      */     //   833: iload #4
/*      */     //   835: i2b
/*      */     //   836: invokespecial <init> : (Ljava/lang/Float;B)V
/*      */     //   839: areturn
/*      */     //   840: iload_1
/*      */     //   841: bipush #8
/*      */     //   843: iand
/*      */     //   844: ifeq -> 878
/*      */     //   847: iload #4
/*      */     //   849: sipush #32767
/*      */     //   852: if_icmpgt -> 878
/*      */     //   855: iload #4
/*      */     //   857: sipush #-32768
/*      */     //   860: if_icmplt -> 878
/*      */     //   863: new freemarker/ext/beans/OverloadedNumberUtil$FloatOrShort
/*      */     //   866: dup
/*      */     //   867: aload_0
/*      */     //   868: checkcast java/lang/Float
/*      */     //   871: iload #4
/*      */     //   873: i2s
/*      */     //   874: invokespecial <init> : (Ljava/lang/Float;S)V
/*      */     //   877: areturn
/*      */     //   878: iload_1
/*      */     //   879: bipush #16
/*      */     //   881: iand
/*      */     //   882: ifeq -> 899
/*      */     //   885: new freemarker/ext/beans/OverloadedNumberUtil$FloatOrInteger
/*      */     //   888: dup
/*      */     //   889: aload_0
/*      */     //   890: checkcast java/lang/Float
/*      */     //   893: iload #4
/*      */     //   895: invokespecial <init> : (Ljava/lang/Float;I)V
/*      */     //   898: areturn
/*      */     //   899: iload_1
/*      */     //   900: bipush #32
/*      */     //   902: iand
/*      */     //   903: ifeq -> 942
/*      */     //   906: iload #7
/*      */     //   908: ifeq -> 927
/*      */     //   911: new freemarker/ext/beans/OverloadedNumberUtil$FloatOrInteger
/*      */     //   914: dup
/*      */     //   915: aload_0
/*      */     //   916: checkcast java/lang/Float
/*      */     //   919: iload #4
/*      */     //   921: invokespecial <init> : (Ljava/lang/Float;I)V
/*      */     //   924: goto -> 941
/*      */     //   927: new freemarker/ext/beans/OverloadedNumberUtil$FloatOrByte
/*      */     //   930: dup
/*      */     //   931: aload_0
/*      */     //   932: checkcast java/lang/Float
/*      */     //   935: iload #4
/*      */     //   937: i2b
/*      */     //   938: invokespecial <init> : (Ljava/lang/Float;B)V
/*      */     //   941: areturn
/*      */     //   942: aload_0
/*      */     //   943: areturn
/*      */     //   944: aload_2
/*      */     //   945: ldc java/lang/Byte
/*      */     //   947: if_acmpne -> 952
/*      */     //   950: aload_0
/*      */     //   951: areturn
/*      */     //   952: aload_2
/*      */     //   953: ldc java/lang/Short
/*      */     //   955: if_acmpne -> 997
/*      */     //   958: aload_0
/*      */     //   959: invokevirtual shortValue : ()S
/*      */     //   962: istore_3
/*      */     //   963: iload_1
/*      */     //   964: iconst_4
/*      */     //   965: iand
/*      */     //   966: ifeq -> 995
/*      */     //   969: iload_3
/*      */     //   970: bipush #127
/*      */     //   972: if_icmpgt -> 995
/*      */     //   975: iload_3
/*      */     //   976: bipush #-128
/*      */     //   978: if_icmplt -> 995
/*      */     //   981: new freemarker/ext/beans/OverloadedNumberUtil$ShortOrByte
/*      */     //   984: dup
/*      */     //   985: aload_0
/*      */     //   986: checkcast java/lang/Short
/*      */     //   989: iload_3
/*      */     //   990: i2b
/*      */     //   991: invokespecial <init> : (Ljava/lang/Short;B)V
/*      */     //   994: areturn
/*      */     //   995: aload_0
/*      */     //   996: areturn
/*      */     //   997: aload_2
/*      */     //   998: ldc java/math/BigInteger
/*      */     //   1000: if_acmpne -> 1196
/*      */     //   1003: iload_1
/*      */     //   1004: sipush #252
/*      */     //   1007: iand
/*      */     //   1008: ifeq -> 1194
/*      */     //   1011: aload_0
/*      */     //   1012: checkcast java/math/BigInteger
/*      */     //   1015: astore_3
/*      */     //   1016: aload_3
/*      */     //   1017: invokevirtual bitLength : ()I
/*      */     //   1020: istore #4
/*      */     //   1022: iload_1
/*      */     //   1023: iconst_4
/*      */     //   1024: iand
/*      */     //   1025: ifeq -> 1044
/*      */     //   1028: iload #4
/*      */     //   1030: bipush #7
/*      */     //   1032: if_icmpgt -> 1044
/*      */     //   1035: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrByte
/*      */     //   1038: dup
/*      */     //   1039: aload_3
/*      */     //   1040: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1043: areturn
/*      */     //   1044: iload_1
/*      */     //   1045: bipush #8
/*      */     //   1047: iand
/*      */     //   1048: ifeq -> 1067
/*      */     //   1051: iload #4
/*      */     //   1053: bipush #15
/*      */     //   1055: if_icmpgt -> 1067
/*      */     //   1058: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrShort
/*      */     //   1061: dup
/*      */     //   1062: aload_3
/*      */     //   1063: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1066: areturn
/*      */     //   1067: iload_1
/*      */     //   1068: bipush #16
/*      */     //   1070: iand
/*      */     //   1071: ifeq -> 1090
/*      */     //   1074: iload #4
/*      */     //   1076: bipush #31
/*      */     //   1078: if_icmpgt -> 1090
/*      */     //   1081: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrInteger
/*      */     //   1084: dup
/*      */     //   1085: aload_3
/*      */     //   1086: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1089: areturn
/*      */     //   1090: iload_1
/*      */     //   1091: bipush #32
/*      */     //   1093: iand
/*      */     //   1094: ifeq -> 1113
/*      */     //   1097: iload #4
/*      */     //   1099: bipush #63
/*      */     //   1101: if_icmpgt -> 1113
/*      */     //   1104: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrLong
/*      */     //   1107: dup
/*      */     //   1108: aload_3
/*      */     //   1109: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1112: areturn
/*      */     //   1113: iload_1
/*      */     //   1114: bipush #64
/*      */     //   1116: iand
/*      */     //   1117: ifeq -> 1152
/*      */     //   1120: iload #4
/*      */     //   1122: bipush #24
/*      */     //   1124: if_icmple -> 1143
/*      */     //   1127: iload #4
/*      */     //   1129: bipush #25
/*      */     //   1131: if_icmpne -> 1152
/*      */     //   1134: aload_3
/*      */     //   1135: invokevirtual getLowestSetBit : ()I
/*      */     //   1138: bipush #24
/*      */     //   1140: if_icmplt -> 1152
/*      */     //   1143: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrFloat
/*      */     //   1146: dup
/*      */     //   1147: aload_3
/*      */     //   1148: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1151: areturn
/*      */     //   1152: iload_1
/*      */     //   1153: sipush #128
/*      */     //   1156: iand
/*      */     //   1157: ifeq -> 1192
/*      */     //   1160: iload #4
/*      */     //   1162: bipush #53
/*      */     //   1164: if_icmple -> 1183
/*      */     //   1167: iload #4
/*      */     //   1169: bipush #54
/*      */     //   1171: if_icmpne -> 1192
/*      */     //   1174: aload_3
/*      */     //   1175: invokevirtual getLowestSetBit : ()I
/*      */     //   1178: bipush #53
/*      */     //   1180: if_icmplt -> 1192
/*      */     //   1183: new freemarker/ext/beans/OverloadedNumberUtil$BigIntegerOrDouble
/*      */     //   1186: dup
/*      */     //   1187: aload_3
/*      */     //   1188: invokespecial <init> : (Ljava/math/BigInteger;)V
/*      */     //   1191: areturn
/*      */     //   1192: aload_0
/*      */     //   1193: areturn
/*      */     //   1194: aload_0
/*      */     //   1195: areturn
/*      */     //   1196: aload_0
/*      */     //   1197: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #96	-> 0
/*      */     //   #97	-> 5
/*      */     //   #101	-> 11
/*      */     //   #102	-> 16
/*      */     //   #104	-> 33
/*      */     //   #105	-> 39
/*      */     //   #109	-> 48
/*      */     //   #111	-> 50
/*      */     //   #112	-> 56
/*      */     //   #119	-> 61
/*      */     //   #120	-> 79
/*      */     //   #121	-> 93
/*      */     //   #122	-> 114
/*      */     //   #124	-> 128
/*      */     //   #126	-> 130
/*      */     //   #127	-> 136
/*      */     //   #128	-> 141
/*      */     //   #129	-> 163
/*      */     //   #130	-> 178
/*      */     //   #131	-> 201
/*      */     //   #132	-> 216
/*      */     //   #133	-> 239
/*      */     //   #135	-> 253
/*      */     //   #137	-> 255
/*      */     //   #138	-> 261
/*      */     //   #142	-> 266
/*      */     //   #145	-> 277
/*      */     //   #147	-> 296
/*      */     //   #148	-> 302
/*      */     //   #150	-> 309
/*      */     //   #151	-> 316
/*      */     //   #152	-> 322
/*      */     //   #153	-> 329
/*      */     //   #154	-> 338
/*      */     //   #155	-> 344
/*      */     //   #156	-> 353
/*      */     //   #157	-> 356
/*      */     //   #162	-> 365
/*      */     //   #163	-> 374
/*      */     //   #164	-> 380
/*      */     //   #165	-> 389
/*      */     //   #166	-> 392
/*      */     //   #174	-> 398
/*      */     //   #176	-> 422
/*      */     //   #177	-> 438
/*      */     //   #179	-> 463
/*      */     //   #180	-> 479
/*      */     //   #182	-> 504
/*      */     //   #183	-> 509
/*      */     //   #186	-> 560
/*      */     //   #187	-> 567
/*      */     //   #188	-> 572
/*      */     //   #192	-> 586
/*      */     //   #193	-> 604
/*      */     //   #204	-> 618
/*      */     //   #205	-> 641
/*      */     //   #208	-> 653
/*      */     //   #210	-> 655
/*      */     //   #211	-> 661
/*      */     //   #215	-> 666
/*      */     //   #218	-> 677
/*      */     //   #220	-> 694
/*      */     //   #221	-> 700
/*      */     //   #223	-> 708
/*      */     //   #224	-> 715
/*      */     //   #226	-> 721
/*      */     //   #227	-> 735
/*      */     //   #228	-> 742
/*      */     //   #229	-> 751
/*      */     //   #230	-> 757
/*      */     //   #231	-> 766
/*      */     //   #232	-> 769
/*      */     //   #237	-> 775
/*      */     //   #238	-> 784
/*      */     //   #239	-> 790
/*      */     //   #240	-> 799
/*      */     //   #241	-> 802
/*      */     //   #252	-> 805
/*      */     //   #253	-> 825
/*      */     //   #254	-> 840
/*      */     //   #255	-> 863
/*      */     //   #256	-> 878
/*      */     //   #257	-> 885
/*      */     //   #258	-> 899
/*      */     //   #260	-> 906
/*      */     //   #268	-> 942
/*      */     //   #269	-> 944
/*      */     //   #270	-> 950
/*      */     //   #271	-> 952
/*      */     //   #272	-> 958
/*      */     //   #273	-> 963
/*      */     //   #274	-> 981
/*      */     //   #276	-> 995
/*      */     //   #278	-> 997
/*      */     //   #279	-> 1003
/*      */     //   #282	-> 1011
/*      */     //   #283	-> 1016
/*      */     //   #284	-> 1022
/*      */     //   #285	-> 1035
/*      */     //   #286	-> 1044
/*      */     //   #287	-> 1058
/*      */     //   #288	-> 1067
/*      */     //   #289	-> 1081
/*      */     //   #290	-> 1090
/*      */     //   #291	-> 1104
/*      */     //   #292	-> 1113
/*      */     //   #295	-> 1135
/*      */     //   #296	-> 1143
/*      */     //   #297	-> 1152
/*      */     //   #300	-> 1175
/*      */     //   #301	-> 1183
/*      */     //   #303	-> 1192
/*      */     //   #307	-> 1194
/*      */     //   #311	-> 1196
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   16	34	3	n	Ljava/math/BigDecimal;
/*      */     //   61	69	3	pn	I
/*      */     //   141	114	3	pn	J
/*      */     //   319	3	9	exact	Z
/*      */     //   341	3	9	exact	Z
/*      */     //   356	9	9	exact	Z
/*      */     //   377	3	9	exact	Z
/*      */     //   509	51	10	intN	I
/*      */     //   302	316	5	longN	J
/*      */     //   309	309	7	diff	D
/*      */     //   392	226	9	exact	Z
/*      */     //   266	389	3	doubleN	D
/*      */     //   718	3	7	exact	Z
/*      */     //   754	3	7	exact	Z
/*      */     //   769	6	7	exact	Z
/*      */     //   787	3	7	exact	Z
/*      */     //   700	242	4	intN	I
/*      */     //   708	234	5	diff	D
/*      */     //   802	140	7	exact	Z
/*      */     //   666	278	3	floatN	F
/*      */     //   963	34	3	pn	S
/*      */     //   1016	178	3	biNum	Ljava/math/BigInteger;
/*      */     //   1022	172	4	bitLength	I
/*      */     //   0	1198	0	num	Ljava/lang/Number;
/*      */     //   0	1198	1	typeFlags	I
/*      */     //   5	1193	2	numClass	Ljava/lang/Class;
/*      */   }
/*      */   
/*      */   static interface ByteSource
/*      */   {
/*      */     Byte byteValue();
/*      */   }
/*      */   
/*      */   static interface ShortSource
/*      */   {
/*      */     Short shortValue();
/*      */   }
/*      */   
/*      */   static interface IntegerSource
/*      */   {
/*      */     Integer integerValue();
/*      */   }
/*      */   
/*      */   static interface LongSource
/*      */   {
/*      */     Long longValue();
/*      */   }
/*      */   
/*      */   static interface FloatSource
/*      */   {
/*      */     Float floatValue();
/*      */   }
/*      */   
/*      */   static interface DoubleSource
/*      */   {
/*      */     Double doubleValue();
/*      */   }
/*      */   
/*      */   static interface BigIntegerSource
/*      */   {
/*      */     BigInteger bigIntegerValue();
/*      */   }
/*      */   
/*      */   static interface BigDecimalSource
/*      */   {
/*      */     BigDecimal bigDecimalValue();
/*      */   }
/*      */   
/*      */   static abstract class NumberWithFallbackType
/*      */     extends Number
/*      */     implements Comparable
/*      */   {
/*      */     protected abstract Number getSourceNumber();
/*      */     
/*      */     public int intValue() {
/*  337 */       return getSourceNumber().intValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  342 */       return getSourceNumber().longValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public float floatValue() {
/*  347 */       return getSourceNumber().floatValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public double doubleValue() {
/*  352 */       return getSourceNumber().doubleValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  357 */       return getSourceNumber().byteValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  362 */       return getSourceNumber().shortValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  367 */       return getSourceNumber().hashCode();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  372 */       if (obj != null && getClass() == obj.getClass()) {
/*  373 */         return getSourceNumber().equals(((NumberWithFallbackType)obj).getSourceNumber());
/*      */       }
/*  375 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String toString() {
/*  381 */       return getSourceNumber().toString();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int compareTo(Object o) {
/*  388 */       Number n = getSourceNumber();
/*  389 */       if (n instanceof Comparable) {
/*  390 */         return ((Comparable<Object>)n).compareTo(o);
/*      */       }
/*  392 */       throw new ClassCastException(n.getClass().getName() + " is not Comparable.");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final class IntegerBigDecimal
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final BigDecimal n;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     IntegerBigDecimal(BigDecimal n) {
/*  410 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  415 */       return this.n;
/*      */     }
/*      */     
/*      */     public BigInteger bigIntegerValue() {
/*  419 */       return this.n.toBigInteger();
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class LongOrSmallerInteger
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Long n;
/*      */     
/*      */     protected LongOrSmallerInteger(Long n) {
/*  429 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  434 */       return this.n;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  439 */       return this.n.longValue();
/*      */     }
/*      */   }
/*      */   
/*      */   static class LongOrByte
/*      */     extends LongOrSmallerInteger
/*      */   {
/*      */     private final byte w;
/*      */     
/*      */     LongOrByte(Long n, byte w) {
/*  449 */       super(n);
/*  450 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  455 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static class LongOrShort
/*      */     extends LongOrSmallerInteger
/*      */   {
/*      */     private final short w;
/*      */     
/*      */     LongOrShort(Long n, short w) {
/*  465 */       super(n);
/*  466 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  471 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static class LongOrInteger
/*      */     extends LongOrSmallerInteger
/*      */   {
/*      */     private final int w;
/*      */     
/*      */     LongOrInteger(Long n, int w) {
/*  481 */       super(n);
/*  482 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  487 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class IntegerOrSmallerInteger
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Integer n;
/*      */     
/*      */     protected IntegerOrSmallerInteger(Integer n) {
/*  497 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  502 */       return this.n;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  507 */       return this.n.intValue();
/*      */     }
/*      */   }
/*      */   
/*      */   static class IntegerOrByte
/*      */     extends IntegerOrSmallerInteger
/*      */   {
/*      */     private final byte w;
/*      */     
/*      */     IntegerOrByte(Integer n, byte w) {
/*  517 */       super(n);
/*  518 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  523 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static class IntegerOrShort
/*      */     extends IntegerOrSmallerInteger
/*      */   {
/*      */     private final short w;
/*      */     
/*      */     IntegerOrShort(Integer n, short w) {
/*  533 */       super(n);
/*  534 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  539 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static class ShortOrByte
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Short n;
/*      */     private final byte w;
/*      */     
/*      */     protected ShortOrByte(Short n, byte w) {
/*  550 */       this.n = n;
/*  551 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  556 */       return this.n;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  561 */       return this.n.shortValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  566 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class DoubleOrWholeNumber
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Double n;
/*      */     
/*      */     protected DoubleOrWholeNumber(Double n) {
/*  576 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  581 */       return this.n;
/*      */     }
/*      */ 
/*      */     
/*      */     public double doubleValue() {
/*  586 */       return this.n.doubleValue();
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrByte
/*      */     extends DoubleOrWholeNumber
/*      */   {
/*      */     private final byte w;
/*      */     
/*      */     DoubleOrByte(Double n, byte w) {
/*  596 */       super(n);
/*  597 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  602 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  607 */       return (short)this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  612 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  617 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrShort
/*      */     extends DoubleOrWholeNumber
/*      */   {
/*      */     private final short w;
/*      */     
/*      */     DoubleOrShort(Double n, short w) {
/*  627 */       super(n);
/*  628 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  633 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  638 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  643 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrIntegerOrFloat
/*      */     extends DoubleOrWholeNumber
/*      */   {
/*      */     private final int w;
/*      */     
/*      */     DoubleOrIntegerOrFloat(Double n, int w) {
/*  653 */       super(n);
/*  654 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  659 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  664 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrInteger
/*      */     extends DoubleOrWholeNumber
/*      */   {
/*      */     private final int w;
/*      */     
/*      */     DoubleOrInteger(Double n, int w) {
/*  674 */       super(n);
/*  675 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  680 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  685 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrLong
/*      */     extends DoubleOrWholeNumber
/*      */   {
/*      */     private final long w;
/*      */     
/*      */     DoubleOrLong(Double n, long w) {
/*  695 */       super(n);
/*  696 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  701 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class DoubleOrFloat
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Double n;
/*      */     
/*      */     DoubleOrFloat(Double n) {
/*  711 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     public float floatValue() {
/*  716 */       return this.n.floatValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public double doubleValue() {
/*  721 */       return this.n.doubleValue();
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  726 */       return this.n;
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class FloatOrWholeNumber
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     private final Float n;
/*      */     
/*      */     FloatOrWholeNumber(Float n) {
/*  736 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  741 */       return this.n;
/*      */     }
/*      */ 
/*      */     
/*      */     public float floatValue() {
/*  746 */       return this.n.floatValue();
/*      */     }
/*      */   }
/*      */   
/*      */   static final class FloatOrByte
/*      */     extends FloatOrWholeNumber
/*      */   {
/*      */     private final byte w;
/*      */     
/*      */     FloatOrByte(Float n, byte w) {
/*  756 */       super(n);
/*  757 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte byteValue() {
/*  762 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  767 */       return (short)this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  772 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  777 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class FloatOrShort
/*      */     extends FloatOrWholeNumber
/*      */   {
/*      */     private final short w;
/*      */     
/*      */     FloatOrShort(Float n, short w) {
/*  787 */       super(n);
/*  788 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public short shortValue() {
/*  793 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  798 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  803 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class FloatOrInteger
/*      */     extends FloatOrWholeNumber
/*      */   {
/*      */     private final int w;
/*      */     
/*      */     FloatOrInteger(Float n, int w) {
/*  813 */       super(n);
/*  814 */       this.w = w;
/*      */     }
/*      */ 
/*      */     
/*      */     public int intValue() {
/*  819 */       return this.w;
/*      */     }
/*      */ 
/*      */     
/*      */     public long longValue() {
/*  824 */       return this.w;
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class BigIntegerOrPrimitive
/*      */     extends NumberWithFallbackType
/*      */   {
/*      */     protected final BigInteger n;
/*      */     
/*      */     BigIntegerOrPrimitive(BigInteger n) {
/*  834 */       this.n = n;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Number getSourceNumber() {
/*  839 */       return this.n;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrByte
/*      */     extends BigIntegerOrPrimitive
/*      */   {
/*      */     BigIntegerOrByte(BigInteger n) {
/*  847 */       super(n);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrShort
/*      */     extends BigIntegerOrPrimitive
/*      */   {
/*      */     BigIntegerOrShort(BigInteger n) {
/*  855 */       super(n);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrInteger
/*      */     extends BigIntegerOrPrimitive
/*      */   {
/*      */     BigIntegerOrInteger(BigInteger n) {
/*  863 */       super(n);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrLong
/*      */     extends BigIntegerOrPrimitive
/*      */   {
/*      */     BigIntegerOrLong(BigInteger n) {
/*  871 */       super(n);
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class BigIntegerOrFPPrimitive
/*      */     extends BigIntegerOrPrimitive
/*      */   {
/*      */     BigIntegerOrFPPrimitive(BigInteger n) {
/*  879 */       super(n);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public float floatValue() {
/*  885 */       return (float)this.n.longValue();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public double doubleValue() {
/*  891 */       return this.n.longValue();
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrFloat
/*      */     extends BigIntegerOrFPPrimitive
/*      */   {
/*      */     BigIntegerOrFloat(BigInteger n) {
/*  899 */       super(n);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BigIntegerOrDouble
/*      */     extends BigIntegerOrFPPrimitive
/*      */   {
/*      */     BigIntegerOrDouble(BigInteger n) {
/*  907 */       super(n);
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
/*      */   static int getArgumentConversionPrice(Class<IntegerBigDecimal> fromC, Class<Integer> toC) {
/*  942 */     if (toC == fromC)
/*  943 */       return 0; 
/*  944 */     if (toC == Integer.class) {
/*  945 */       if (fromC == IntegerBigDecimal.class) return 31003; 
/*  946 */       if (fromC == BigDecimal.class) return 41003; 
/*  947 */       if (fromC == Long.class) return Integer.MAX_VALUE; 
/*  948 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/*  949 */       if (fromC == Float.class) return Integer.MAX_VALUE; 
/*  950 */       if (fromC == Byte.class) return 10003; 
/*  951 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/*  952 */       if (fromC == LongOrInteger.class) return 21003; 
/*  953 */       if (fromC == DoubleOrFloat.class) return Integer.MAX_VALUE; 
/*  954 */       if (fromC == DoubleOrIntegerOrFloat.class) return 22003; 
/*  955 */       if (fromC == DoubleOrInteger.class) return 22003; 
/*  956 */       if (fromC == DoubleOrLong.class) return Integer.MAX_VALUE; 
/*  957 */       if (fromC == IntegerOrByte.class) return 0; 
/*  958 */       if (fromC == DoubleOrByte.class) return 22003; 
/*  959 */       if (fromC == LongOrByte.class) return 21003; 
/*  960 */       if (fromC == Short.class) return 10003; 
/*  961 */       if (fromC == LongOrShort.class) return 21003; 
/*  962 */       if (fromC == ShortOrByte.class) return 10003; 
/*  963 */       if (fromC == FloatOrInteger.class) return 21003; 
/*  964 */       if (fromC == FloatOrByte.class) return 21003; 
/*  965 */       if (fromC == FloatOrShort.class) return 21003; 
/*  966 */       if (fromC == BigIntegerOrInteger.class) return 16003; 
/*  967 */       if (fromC == BigIntegerOrLong.class) return Integer.MAX_VALUE; 
/*  968 */       if (fromC == BigIntegerOrDouble.class) return Integer.MAX_VALUE; 
/*  969 */       if (fromC == BigIntegerOrFloat.class) return Integer.MAX_VALUE; 
/*  970 */       if (fromC == BigIntegerOrByte.class) return 16003; 
/*  971 */       if (fromC == IntegerOrShort.class) return 0; 
/*  972 */       if (fromC == DoubleOrShort.class) return 22003; 
/*  973 */       if (fromC == BigIntegerOrShort.class) return 16003; 
/*  974 */       return Integer.MAX_VALUE;
/*  975 */     }  if (toC == Long.class) {
/*  976 */       if (fromC == Integer.class) return 10004; 
/*  977 */       if (fromC == IntegerBigDecimal.class) return 31004; 
/*  978 */       if (fromC == BigDecimal.class) return 41004; 
/*  979 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/*  980 */       if (fromC == Float.class) return Integer.MAX_VALUE; 
/*  981 */       if (fromC == Byte.class) return 10004; 
/*  982 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/*  983 */       if (fromC == LongOrInteger.class) return 0; 
/*  984 */       if (fromC == DoubleOrFloat.class) return Integer.MAX_VALUE; 
/*  985 */       if (fromC == DoubleOrIntegerOrFloat.class) return 21004; 
/*  986 */       if (fromC == DoubleOrInteger.class) return 21004; 
/*  987 */       if (fromC == DoubleOrLong.class) return 21004; 
/*  988 */       if (fromC == IntegerOrByte.class) return 10004; 
/*  989 */       if (fromC == DoubleOrByte.class) return 21004; 
/*  990 */       if (fromC == LongOrByte.class) return 0; 
/*  991 */       if (fromC == Short.class) return 10004; 
/*  992 */       if (fromC == LongOrShort.class) return 0; 
/*  993 */       if (fromC == ShortOrByte.class) return 10004; 
/*  994 */       if (fromC == FloatOrInteger.class) return 21004; 
/*  995 */       if (fromC == FloatOrByte.class) return 21004; 
/*  996 */       if (fromC == FloatOrShort.class) return 21004; 
/*  997 */       if (fromC == BigIntegerOrInteger.class) return 15004; 
/*  998 */       if (fromC == BigIntegerOrLong.class) return 15004; 
/*  999 */       if (fromC == BigIntegerOrDouble.class) return Integer.MAX_VALUE; 
/* 1000 */       if (fromC == BigIntegerOrFloat.class) return Integer.MAX_VALUE; 
/* 1001 */       if (fromC == BigIntegerOrByte.class) return 15004; 
/* 1002 */       if (fromC == IntegerOrShort.class) return 10004; 
/* 1003 */       if (fromC == DoubleOrShort.class) return 21004; 
/* 1004 */       if (fromC == BigIntegerOrShort.class) return 15004; 
/* 1005 */       return Integer.MAX_VALUE;
/* 1006 */     }  if (toC == Double.class) {
/* 1007 */       if (fromC == Integer.class) return 20007; 
/* 1008 */       if (fromC == IntegerBigDecimal.class) return 32007; 
/* 1009 */       if (fromC == BigDecimal.class) return 32007; 
/* 1010 */       if (fromC == Long.class) return 30007; 
/* 1011 */       if (fromC == Float.class) return 10007; 
/* 1012 */       if (fromC == Byte.class) return 20007; 
/* 1013 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/* 1014 */       if (fromC == LongOrInteger.class) return 21007; 
/* 1015 */       if (fromC == DoubleOrFloat.class) return 0; 
/* 1016 */       if (fromC == DoubleOrIntegerOrFloat.class) return 0; 
/* 1017 */       if (fromC == DoubleOrInteger.class) return 0; 
/* 1018 */       if (fromC == DoubleOrLong.class) return 0; 
/* 1019 */       if (fromC == IntegerOrByte.class) return 20007; 
/* 1020 */       if (fromC == DoubleOrByte.class) return 0; 
/* 1021 */       if (fromC == LongOrByte.class) return 21007; 
/* 1022 */       if (fromC == Short.class) return 20007; 
/* 1023 */       if (fromC == LongOrShort.class) return 21007; 
/* 1024 */       if (fromC == ShortOrByte.class) return 20007; 
/* 1025 */       if (fromC == FloatOrInteger.class) return 10007; 
/* 1026 */       if (fromC == FloatOrByte.class) return 10007; 
/* 1027 */       if (fromC == FloatOrShort.class) return 10007; 
/* 1028 */       if (fromC == BigIntegerOrInteger.class) return 20007; 
/* 1029 */       if (fromC == BigIntegerOrLong.class) return 30007; 
/* 1030 */       if (fromC == BigIntegerOrDouble.class) return 20007; 
/* 1031 */       if (fromC == BigIntegerOrFloat.class) return 20007; 
/* 1032 */       if (fromC == BigIntegerOrByte.class) return 20007; 
/* 1033 */       if (fromC == IntegerOrShort.class) return 20007; 
/* 1034 */       if (fromC == DoubleOrShort.class) return 0; 
/* 1035 */       if (fromC == BigIntegerOrShort.class) return 20007; 
/* 1036 */       return Integer.MAX_VALUE;
/* 1037 */     }  if (toC == Float.class) {
/* 1038 */       if (fromC == Integer.class) return 30006; 
/* 1039 */       if (fromC == IntegerBigDecimal.class) return 33006; 
/* 1040 */       if (fromC == BigDecimal.class) return 33006; 
/* 1041 */       if (fromC == Long.class) return 40006; 
/* 1042 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/* 1043 */       if (fromC == Byte.class) return 20006; 
/* 1044 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/* 1045 */       if (fromC == LongOrInteger.class) return 30006; 
/* 1046 */       if (fromC == DoubleOrFloat.class) return 30006; 
/* 1047 */       if (fromC == DoubleOrIntegerOrFloat.class) return 23006; 
/* 1048 */       if (fromC == DoubleOrInteger.class) return 30006; 
/* 1049 */       if (fromC == DoubleOrLong.class) return 40006; 
/* 1050 */       if (fromC == IntegerOrByte.class) return 24006; 
/* 1051 */       if (fromC == DoubleOrByte.class) return 23006; 
/* 1052 */       if (fromC == LongOrByte.class) return 24006; 
/* 1053 */       if (fromC == Short.class) return 20006; 
/* 1054 */       if (fromC == LongOrShort.class) return 24006; 
/* 1055 */       if (fromC == ShortOrByte.class) return 20006; 
/* 1056 */       if (fromC == FloatOrInteger.class) return 0; 
/* 1057 */       if (fromC == FloatOrByte.class) return 0; 
/* 1058 */       if (fromC == FloatOrShort.class) return 0; 
/* 1059 */       if (fromC == BigIntegerOrInteger.class) return 30006; 
/* 1060 */       if (fromC == BigIntegerOrLong.class) return 40006; 
/* 1061 */       if (fromC == BigIntegerOrDouble.class) return 40006; 
/* 1062 */       if (fromC == BigIntegerOrFloat.class) return 24006; 
/* 1063 */       if (fromC == BigIntegerOrByte.class) return 24006; 
/* 1064 */       if (fromC == IntegerOrShort.class) return 24006; 
/* 1065 */       if (fromC == DoubleOrShort.class) return 23006; 
/* 1066 */       if (fromC == BigIntegerOrShort.class) return 24006; 
/* 1067 */       return Integer.MAX_VALUE;
/* 1068 */     }  if (toC == Byte.class) {
/* 1069 */       if (fromC == Integer.class) return Integer.MAX_VALUE; 
/* 1070 */       if (fromC == IntegerBigDecimal.class) return 35001; 
/* 1071 */       if (fromC == BigDecimal.class) return 45001; 
/* 1072 */       if (fromC == Long.class) return Integer.MAX_VALUE; 
/* 1073 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/* 1074 */       if (fromC == Float.class) return Integer.MAX_VALUE; 
/* 1075 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/* 1076 */       if (fromC == LongOrInteger.class) return Integer.MAX_VALUE; 
/* 1077 */       if (fromC == DoubleOrFloat.class) return Integer.MAX_VALUE; 
/* 1078 */       if (fromC == DoubleOrIntegerOrFloat.class) return Integer.MAX_VALUE; 
/* 1079 */       if (fromC == DoubleOrInteger.class) return Integer.MAX_VALUE; 
/* 1080 */       if (fromC == DoubleOrLong.class) return Integer.MAX_VALUE; 
/* 1081 */       if (fromC == IntegerOrByte.class) return 22001; 
/* 1082 */       if (fromC == DoubleOrByte.class) return 25001; 
/* 1083 */       if (fromC == LongOrByte.class) return 23001; 
/* 1084 */       if (fromC == Short.class) return Integer.MAX_VALUE; 
/* 1085 */       if (fromC == LongOrShort.class) return Integer.MAX_VALUE; 
/* 1086 */       if (fromC == ShortOrByte.class) return 21001; 
/* 1087 */       if (fromC == FloatOrInteger.class) return Integer.MAX_VALUE; 
/* 1088 */       if (fromC == FloatOrByte.class) return 23001; 
/* 1089 */       if (fromC == FloatOrShort.class) return Integer.MAX_VALUE; 
/* 1090 */       if (fromC == BigIntegerOrInteger.class) return Integer.MAX_VALUE; 
/* 1091 */       if (fromC == BigIntegerOrLong.class) return Integer.MAX_VALUE; 
/* 1092 */       if (fromC == BigIntegerOrDouble.class) return Integer.MAX_VALUE; 
/* 1093 */       if (fromC == BigIntegerOrFloat.class) return Integer.MAX_VALUE; 
/* 1094 */       if (fromC == BigIntegerOrByte.class) return 18001; 
/* 1095 */       if (fromC == IntegerOrShort.class) return Integer.MAX_VALUE; 
/* 1096 */       if (fromC == DoubleOrShort.class) return Integer.MAX_VALUE; 
/* 1097 */       if (fromC == BigIntegerOrShort.class) return Integer.MAX_VALUE; 
/* 1098 */       return Integer.MAX_VALUE;
/* 1099 */     }  if (toC == Short.class) {
/* 1100 */       if (fromC == Integer.class) return Integer.MAX_VALUE; 
/* 1101 */       if (fromC == IntegerBigDecimal.class) return 34002; 
/* 1102 */       if (fromC == BigDecimal.class) return 44002; 
/* 1103 */       if (fromC == Long.class) return Integer.MAX_VALUE; 
/* 1104 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/* 1105 */       if (fromC == Float.class) return Integer.MAX_VALUE; 
/* 1106 */       if (fromC == Byte.class) return 10002; 
/* 1107 */       if (fromC == BigInteger.class) return Integer.MAX_VALUE; 
/* 1108 */       if (fromC == LongOrInteger.class) return Integer.MAX_VALUE; 
/* 1109 */       if (fromC == DoubleOrFloat.class) return Integer.MAX_VALUE; 
/* 1110 */       if (fromC == DoubleOrIntegerOrFloat.class) return Integer.MAX_VALUE; 
/* 1111 */       if (fromC == DoubleOrInteger.class) return Integer.MAX_VALUE; 
/* 1112 */       if (fromC == DoubleOrLong.class) return Integer.MAX_VALUE; 
/* 1113 */       if (fromC == IntegerOrByte.class) return 21002; 
/* 1114 */       if (fromC == DoubleOrByte.class) return 24002; 
/* 1115 */       if (fromC == LongOrByte.class) return 22002; 
/* 1116 */       if (fromC == LongOrShort.class) return 22002; 
/* 1117 */       if (fromC == ShortOrByte.class) return 0; 
/* 1118 */       if (fromC == FloatOrInteger.class) return Integer.MAX_VALUE; 
/* 1119 */       if (fromC == FloatOrByte.class) return 22002; 
/* 1120 */       if (fromC == FloatOrShort.class) return 22002; 
/* 1121 */       if (fromC == BigIntegerOrInteger.class) return Integer.MAX_VALUE; 
/* 1122 */       if (fromC == BigIntegerOrLong.class) return Integer.MAX_VALUE; 
/* 1123 */       if (fromC == BigIntegerOrDouble.class) return Integer.MAX_VALUE; 
/* 1124 */       if (fromC == BigIntegerOrFloat.class) return Integer.MAX_VALUE; 
/* 1125 */       if (fromC == BigIntegerOrByte.class) return 17002; 
/* 1126 */       if (fromC == IntegerOrShort.class) return 21002; 
/* 1127 */       if (fromC == DoubleOrShort.class) return 24002; 
/* 1128 */       if (fromC == BigIntegerOrShort.class) return 17002; 
/* 1129 */       return Integer.MAX_VALUE;
/* 1130 */     }  if (toC == BigDecimal.class) {
/* 1131 */       if (fromC == Integer.class) return 20008; 
/* 1132 */       if (fromC == IntegerBigDecimal.class) return 0; 
/* 1133 */       if (fromC == Long.class) return 20008; 
/* 1134 */       if (fromC == Double.class) return 20008; 
/* 1135 */       if (fromC == Float.class) return 20008; 
/* 1136 */       if (fromC == Byte.class) return 20008; 
/* 1137 */       if (fromC == BigInteger.class) return 10008; 
/* 1138 */       if (fromC == LongOrInteger.class) return 20008; 
/* 1139 */       if (fromC == DoubleOrFloat.class) return 20008; 
/* 1140 */       if (fromC == DoubleOrIntegerOrFloat.class) return 20008; 
/* 1141 */       if (fromC == DoubleOrInteger.class) return 20008; 
/* 1142 */       if (fromC == DoubleOrLong.class) return 20008; 
/* 1143 */       if (fromC == IntegerOrByte.class) return 20008; 
/* 1144 */       if (fromC == DoubleOrByte.class) return 20008; 
/* 1145 */       if (fromC == LongOrByte.class) return 20008; 
/* 1146 */       if (fromC == Short.class) return 20008; 
/* 1147 */       if (fromC == LongOrShort.class) return 20008; 
/* 1148 */       if (fromC == ShortOrByte.class) return 20008; 
/* 1149 */       if (fromC == FloatOrInteger.class) return 20008; 
/* 1150 */       if (fromC == FloatOrByte.class) return 20008; 
/* 1151 */       if (fromC == FloatOrShort.class) return 20008; 
/* 1152 */       if (fromC == BigIntegerOrInteger.class) return 10008; 
/* 1153 */       if (fromC == BigIntegerOrLong.class) return 10008; 
/* 1154 */       if (fromC == BigIntegerOrDouble.class) return 10008; 
/* 1155 */       if (fromC == BigIntegerOrFloat.class) return 10008; 
/* 1156 */       if (fromC == BigIntegerOrByte.class) return 10008; 
/* 1157 */       if (fromC == IntegerOrShort.class) return 20008; 
/* 1158 */       if (fromC == DoubleOrShort.class) return 20008; 
/* 1159 */       if (fromC == BigIntegerOrShort.class) return 10008; 
/* 1160 */       return Integer.MAX_VALUE;
/* 1161 */     }  if (toC == BigInteger.class) {
/* 1162 */       if (fromC == Integer.class) return 10005; 
/* 1163 */       if (fromC == IntegerBigDecimal.class) return 10005; 
/* 1164 */       if (fromC == BigDecimal.class) return 40005; 
/* 1165 */       if (fromC == Long.class) return 10005; 
/* 1166 */       if (fromC == Double.class) return Integer.MAX_VALUE; 
/* 1167 */       if (fromC == Float.class) return Integer.MAX_VALUE; 
/* 1168 */       if (fromC == Byte.class) return 10005; 
/* 1169 */       if (fromC == LongOrInteger.class) return 10005; 
/* 1170 */       if (fromC == DoubleOrFloat.class) return Integer.MAX_VALUE; 
/* 1171 */       if (fromC == DoubleOrIntegerOrFloat.class) return 21005; 
/* 1172 */       if (fromC == DoubleOrInteger.class) return 21005; 
/* 1173 */       if (fromC == DoubleOrLong.class) return 21005; 
/* 1174 */       if (fromC == IntegerOrByte.class) return 10005; 
/* 1175 */       if (fromC == DoubleOrByte.class) return 21005; 
/* 1176 */       if (fromC == LongOrByte.class) return 10005; 
/* 1177 */       if (fromC == Short.class) return 10005; 
/* 1178 */       if (fromC == LongOrShort.class) return 10005; 
/* 1179 */       if (fromC == ShortOrByte.class) return 10005; 
/* 1180 */       if (fromC == FloatOrInteger.class) return 25005; 
/* 1181 */       if (fromC == FloatOrByte.class) return 25005; 
/* 1182 */       if (fromC == FloatOrShort.class) return 25005; 
/* 1183 */       if (fromC == BigIntegerOrInteger.class) return 0; 
/* 1184 */       if (fromC == BigIntegerOrLong.class) return 0; 
/* 1185 */       if (fromC == BigIntegerOrDouble.class) return 0; 
/* 1186 */       if (fromC == BigIntegerOrFloat.class) return 0; 
/* 1187 */       if (fromC == BigIntegerOrByte.class) return 0; 
/* 1188 */       if (fromC == IntegerOrShort.class) return 10005; 
/* 1189 */       if (fromC == DoubleOrShort.class) return 21005; 
/* 1190 */       if (fromC == BigIntegerOrShort.class) return 0; 
/* 1191 */       return Integer.MAX_VALUE;
/*      */     } 
/*      */     
/* 1194 */     return Integer.MAX_VALUE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int compareNumberTypeSpecificity(Class<Integer> c1, Class<Long> c2) {
/* 1201 */     c1 = ClassUtil.primitiveClassToBoxingClass(c1);
/* 1202 */     c2 = ClassUtil.primitiveClassToBoxingClass(c2);
/*      */     
/* 1204 */     if (c1 == c2) return 0;
/*      */     
/* 1206 */     if (c1 == Integer.class) {
/* 1207 */       if (c2 == Long.class) return 1; 
/* 1208 */       if (c2 == Double.class) return 4; 
/* 1209 */       if (c2 == Float.class) return 3; 
/* 1210 */       if (c2 == Byte.class) return -2; 
/* 1211 */       if (c2 == Short.class) return -1; 
/* 1212 */       if (c2 == BigDecimal.class) return 5; 
/* 1213 */       if (c2 == BigInteger.class) return 2; 
/* 1214 */       return 0;
/*      */     } 
/* 1216 */     if (c1 == Long.class) {
/* 1217 */       if (c2 == Integer.class) return -1; 
/* 1218 */       if (c2 == Double.class) return 3; 
/* 1219 */       if (c2 == Float.class) return 2; 
/* 1220 */       if (c2 == Byte.class) return -3; 
/* 1221 */       if (c2 == Short.class) return -2; 
/* 1222 */       if (c2 == BigDecimal.class) return 4; 
/* 1223 */       if (c2 == BigInteger.class) return 1; 
/* 1224 */       return 0;
/*      */     } 
/* 1226 */     if (c1 == Double.class) {
/* 1227 */       if (c2 == Integer.class) return -4; 
/* 1228 */       if (c2 == Long.class) return -3; 
/* 1229 */       if (c2 == Float.class) return -1; 
/* 1230 */       if (c2 == Byte.class) return -6; 
/* 1231 */       if (c2 == Short.class) return -5; 
/* 1232 */       if (c2 == BigDecimal.class) return 1; 
/* 1233 */       if (c2 == BigInteger.class) return -2; 
/* 1234 */       return 0;
/*      */     } 
/* 1236 */     if (c1 == Float.class) {
/* 1237 */       if (c2 == Integer.class) return -3; 
/* 1238 */       if (c2 == Long.class) return -2; 
/* 1239 */       if (c2 == Double.class) return 1; 
/* 1240 */       if (c2 == Byte.class) return -5; 
/* 1241 */       if (c2 == Short.class) return -4; 
/* 1242 */       if (c2 == BigDecimal.class) return 2; 
/* 1243 */       if (c2 == BigInteger.class) return -1; 
/* 1244 */       return 0;
/*      */     } 
/* 1246 */     if (c1 == Byte.class) {
/* 1247 */       if (c2 == Integer.class) return 2; 
/* 1248 */       if (c2 == Long.class) return 3; 
/* 1249 */       if (c2 == Double.class) return 6; 
/* 1250 */       if (c2 == Float.class) return 5; 
/* 1251 */       if (c2 == Short.class) return 1; 
/* 1252 */       if (c2 == BigDecimal.class) return 7; 
/* 1253 */       if (c2 == BigInteger.class) return 4; 
/* 1254 */       return 0;
/*      */     } 
/* 1256 */     if (c1 == Short.class) {
/* 1257 */       if (c2 == Integer.class) return 1; 
/* 1258 */       if (c2 == Long.class) return 2; 
/* 1259 */       if (c2 == Double.class) return 5; 
/* 1260 */       if (c2 == Float.class) return 4; 
/* 1261 */       if (c2 == Byte.class) return -1; 
/* 1262 */       if (c2 == BigDecimal.class) return 6; 
/* 1263 */       if (c2 == BigInteger.class) return 3; 
/* 1264 */       return 0;
/*      */     } 
/* 1266 */     if (c1 == BigDecimal.class) {
/* 1267 */       if (c2 == Integer.class) return -5; 
/* 1268 */       if (c2 == Long.class) return -4; 
/* 1269 */       if (c2 == Double.class) return -1; 
/* 1270 */       if (c2 == Float.class) return -2; 
/* 1271 */       if (c2 == Byte.class) return -7; 
/* 1272 */       if (c2 == Short.class) return -6; 
/* 1273 */       if (c2 == BigInteger.class) return -3; 
/* 1274 */       return 0;
/*      */     } 
/* 1276 */     if (c1 == BigInteger.class) {
/* 1277 */       if (c2 == Integer.class) return -2; 
/* 1278 */       if (c2 == Long.class) return -1; 
/* 1279 */       if (c2 == Double.class) return 2; 
/* 1280 */       if (c2 == Float.class) return 1; 
/* 1281 */       if (c2 == Byte.class) return -4; 
/* 1282 */       if (c2 == Short.class) return -3; 
/* 1283 */       if (c2 == BigDecimal.class) return 3; 
/* 1284 */       return 0;
/*      */     } 
/* 1286 */     return 0;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedNumberUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */