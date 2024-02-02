/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONStrFormatter
/*     */ {
/*     */   private static final String SPACE = "    ";
/*     */   private static final char NEW_LINE = '\n';
/*     */   
/*     */   public static String format(String json) {
/*     */     // Byte code:
/*     */     //   0: new java/lang/StringBuilder
/*     */     //   3: dup
/*     */     //   4: invokespecial <init> : ()V
/*     */     //   7: astore_1
/*     */     //   8: aconst_null
/*     */     //   9: astore_2
/*     */     //   10: iconst_0
/*     */     //   11: istore_3
/*     */     //   12: aload_0
/*     */     //   13: invokevirtual length : ()I
/*     */     //   16: istore #4
/*     */     //   18: iconst_0
/*     */     //   19: istore #5
/*     */     //   21: iconst_0
/*     */     //   22: istore #7
/*     */     //   24: iload #7
/*     */     //   26: iload #4
/*     */     //   28: if_icmpge -> 377
/*     */     //   31: aload_0
/*     */     //   32: iload #7
/*     */     //   34: invokevirtual charAt : (I)C
/*     */     //   37: istore #6
/*     */     //   39: bipush #34
/*     */     //   41: iload #6
/*     */     //   43: if_icmpeq -> 53
/*     */     //   46: bipush #39
/*     */     //   48: iload #6
/*     */     //   50: if_icmpne -> 126
/*     */     //   53: aconst_null
/*     */     //   54: aload_2
/*     */     //   55: if_acmpne -> 67
/*     */     //   58: iload #6
/*     */     //   60: invokestatic valueOf : (C)Ljava/lang/Character;
/*     */     //   63: astore_2
/*     */     //   64: goto -> 90
/*     */     //   67: iload_3
/*     */     //   68: ifeq -> 76
/*     */     //   71: iconst_0
/*     */     //   72: istore_3
/*     */     //   73: goto -> 90
/*     */     //   76: aload_2
/*     */     //   77: iload #6
/*     */     //   79: invokestatic valueOf : (C)Ljava/lang/Character;
/*     */     //   82: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   85: ifeq -> 90
/*     */     //   88: aconst_null
/*     */     //   89: astore_2
/*     */     //   90: iload #7
/*     */     //   92: iconst_1
/*     */     //   93: if_icmple -> 116
/*     */     //   96: aload_0
/*     */     //   97: iload #7
/*     */     //   99: iconst_1
/*     */     //   100: isub
/*     */     //   101: invokevirtual charAt : (I)C
/*     */     //   104: bipush #58
/*     */     //   106: if_icmpne -> 116
/*     */     //   109: aload_1
/*     */     //   110: bipush #32
/*     */     //   112: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   115: pop
/*     */     //   116: aload_1
/*     */     //   117: iload #6
/*     */     //   119: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   122: pop
/*     */     //   123: goto -> 371
/*     */     //   126: bipush #92
/*     */     //   128: iload #6
/*     */     //   130: if_icmpne -> 165
/*     */     //   133: aconst_null
/*     */     //   134: aload_2
/*     */     //   135: if_acmpeq -> 158
/*     */     //   138: iload_3
/*     */     //   139: ifne -> 146
/*     */     //   142: iconst_1
/*     */     //   143: goto -> 147
/*     */     //   146: iconst_0
/*     */     //   147: istore_3
/*     */     //   148: aload_1
/*     */     //   149: iload #6
/*     */     //   151: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   154: pop
/*     */     //   155: goto -> 371
/*     */     //   158: aload_1
/*     */     //   159: iload #6
/*     */     //   161: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   164: pop
/*     */     //   165: aconst_null
/*     */     //   166: aload_2
/*     */     //   167: if_acmpeq -> 180
/*     */     //   170: aload_1
/*     */     //   171: iload #6
/*     */     //   173: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   176: pop
/*     */     //   177: goto -> 371
/*     */     //   180: iload #6
/*     */     //   182: bipush #91
/*     */     //   184: if_icmpeq -> 194
/*     */     //   187: iload #6
/*     */     //   189: bipush #123
/*     */     //   191: if_icmpne -> 260
/*     */     //   194: iload #7
/*     */     //   196: iconst_1
/*     */     //   197: if_icmple -> 230
/*     */     //   200: aload_0
/*     */     //   201: iload #7
/*     */     //   203: iconst_1
/*     */     //   204: isub
/*     */     //   205: invokevirtual charAt : (I)C
/*     */     //   208: bipush #58
/*     */     //   210: if_icmpne -> 230
/*     */     //   213: aload_1
/*     */     //   214: bipush #10
/*     */     //   216: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   219: pop
/*     */     //   220: aload_1
/*     */     //   221: iload #5
/*     */     //   223: invokestatic indent : (I)Ljava/lang/String;
/*     */     //   226: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   229: pop
/*     */     //   230: aload_1
/*     */     //   231: iload #6
/*     */     //   233: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   236: pop
/*     */     //   237: aload_1
/*     */     //   238: bipush #10
/*     */     //   240: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   243: pop
/*     */     //   244: iinc #5, 1
/*     */     //   247: aload_1
/*     */     //   248: iload #5
/*     */     //   250: invokestatic indent : (I)Ljava/lang/String;
/*     */     //   253: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   256: pop
/*     */     //   257: goto -> 371
/*     */     //   260: iload #6
/*     */     //   262: bipush #93
/*     */     //   264: if_icmpeq -> 274
/*     */     //   267: iload #6
/*     */     //   269: bipush #125
/*     */     //   271: if_icmpne -> 304
/*     */     //   274: aload_1
/*     */     //   275: bipush #10
/*     */     //   277: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   280: pop
/*     */     //   281: iinc #5, -1
/*     */     //   284: aload_1
/*     */     //   285: iload #5
/*     */     //   287: invokestatic indent : (I)Ljava/lang/String;
/*     */     //   290: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   293: pop
/*     */     //   294: aload_1
/*     */     //   295: iload #6
/*     */     //   297: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   300: pop
/*     */     //   301: goto -> 371
/*     */     //   304: iload #6
/*     */     //   306: bipush #44
/*     */     //   308: if_icmpne -> 338
/*     */     //   311: aload_1
/*     */     //   312: iload #6
/*     */     //   314: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   317: pop
/*     */     //   318: aload_1
/*     */     //   319: bipush #10
/*     */     //   321: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   324: pop
/*     */     //   325: aload_1
/*     */     //   326: iload #5
/*     */     //   328: invokestatic indent : (I)Ljava/lang/String;
/*     */     //   331: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   334: pop
/*     */     //   335: goto -> 371
/*     */     //   338: iload #7
/*     */     //   340: iconst_1
/*     */     //   341: if_icmple -> 364
/*     */     //   344: aload_0
/*     */     //   345: iload #7
/*     */     //   347: iconst_1
/*     */     //   348: isub
/*     */     //   349: invokevirtual charAt : (I)C
/*     */     //   352: bipush #58
/*     */     //   354: if_icmpne -> 364
/*     */     //   357: aload_1
/*     */     //   358: bipush #32
/*     */     //   360: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   363: pop
/*     */     //   364: aload_1
/*     */     //   365: iload #6
/*     */     //   367: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   370: pop
/*     */     //   371: iinc #7, 1
/*     */     //   374: goto -> 24
/*     */     //   377: aload_1
/*     */     //   378: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   381: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #31	-> 0
/*     */     //   #33	-> 8
/*     */     //   #34	-> 10
/*     */     //   #35	-> 12
/*     */     //   #36	-> 18
/*     */     //   #38	-> 21
/*     */     //   #39	-> 31
/*     */     //   #41	-> 39
/*     */     //   #42	-> 53
/*     */     //   #44	-> 58
/*     */     //   #45	-> 67
/*     */     //   #47	-> 71
/*     */     //   #48	-> 76
/*     */     //   #50	-> 88
/*     */     //   #53	-> 90
/*     */     //   #54	-> 109
/*     */     //   #57	-> 116
/*     */     //   #58	-> 123
/*     */     //   #61	-> 126
/*     */     //   #62	-> 133
/*     */     //   #64	-> 138
/*     */     //   #65	-> 148
/*     */     //   #66	-> 155
/*     */     //   #68	-> 158
/*     */     //   #72	-> 165
/*     */     //   #74	-> 170
/*     */     //   #75	-> 177
/*     */     //   #79	-> 180
/*     */     //   #81	-> 194
/*     */     //   #82	-> 213
/*     */     //   #83	-> 220
/*     */     //   #85	-> 230
/*     */     //   #87	-> 237
/*     */     //   #89	-> 244
/*     */     //   #90	-> 247
/*     */     //   #92	-> 257
/*     */     //   #96	-> 260
/*     */     //   #98	-> 274
/*     */     //   #100	-> 281
/*     */     //   #101	-> 284
/*     */     //   #103	-> 294
/*     */     //   #109	-> 301
/*     */     //   #113	-> 304
/*     */     //   #114	-> 311
/*     */     //   #115	-> 318
/*     */     //   #116	-> 325
/*     */     //   #117	-> 335
/*     */     //   #120	-> 338
/*     */     //   #121	-> 357
/*     */     //   #125	-> 364
/*     */     //   #38	-> 371
/*     */     //   #128	-> 377
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   39	338	6	key	C
/*     */     //   24	353	7	i	I
/*     */     //   0	382	0	json	Ljava/lang/String;
/*     */     //   8	374	1	result	Ljava/lang/StringBuilder;
/*     */     //   10	372	2	wrapChar	Ljava/lang/Character;
/*     */     //   12	370	3	isEscapeMode	Z
/*     */     //   18	364	4	length	I
/*     */     //   21	361	5	number	I
/*     */   }
/*     */   
/*     */   private static String indent(int number) {
/* 138 */     return StrUtil.repeat("    ", number);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONStrFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */