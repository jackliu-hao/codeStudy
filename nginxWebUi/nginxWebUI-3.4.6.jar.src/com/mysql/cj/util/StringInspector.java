/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class StringInspector
/*     */ {
/*     */   private static final int NON_COMMENTS_MYSQL_VERSION_REF_LENGTH = 5;
/*  51 */   private String source = null;
/*  52 */   private String openingMarkers = null;
/*  53 */   private String closingMarkers = null;
/*  54 */   private String overridingMarkers = null;
/*  55 */   private Set<SearchMode> defaultSearchMode = null;
/*     */   
/*  57 */   private int srcLen = 0;
/*  58 */   private int pos = 0;
/*  59 */   private int stopAt = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean escaped = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean inMysqlBlock = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringInspector(String source, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/*  84 */     this(source, 0, openingMarkers, closingMarkers, overridingMarkers, searchMode);
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
/*     */   public StringInspector(String source, int startingPosition, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/* 110 */     if (source == null) {
/* 111 */       throw new IllegalArgumentException(Messages.getString("StringInspector.1"));
/*     */     }
/*     */     
/* 114 */     this.source = source;
/* 115 */     this.openingMarkers = openingMarkers;
/* 116 */     this.closingMarkers = closingMarkers;
/* 117 */     this.overridingMarkers = overridingMarkers;
/* 118 */     this.defaultSearchMode = searchMode;
/*     */     
/* 120 */     if (this.defaultSearchMode.contains(SearchMode.SKIP_BETWEEN_MARKERS)) {
/* 121 */       if (this.openingMarkers == null || this.closingMarkers == null || this.openingMarkers.length() != this.closingMarkers.length()) {
/* 122 */         throw new IllegalArgumentException(Messages.getString("StringInspector.2", new String[] { this.openingMarkers, this.closingMarkers }));
/*     */       }
/* 124 */       if (this.overridingMarkers == null) {
/* 125 */         throw new IllegalArgumentException(Messages.getString("StringInspector.3", new String[] { this.overridingMarkers, this.openingMarkers }));
/*     */       }
/* 127 */       for (char c : this.overridingMarkers.toCharArray()) {
/* 128 */         if (this.openingMarkers.indexOf(c) == -1) {
/* 129 */           throw new IllegalArgumentException(Messages.getString("StringInspector.3", new String[] { this.overridingMarkers, this.openingMarkers }));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 134 */     this.srcLen = source.length();
/* 135 */     this.pos = 0;
/* 136 */     this.stopAt = this.srcLen;
/*     */     
/* 138 */     setStartPosition(startingPosition);
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
/*     */   public int setStartPosition(int pos) {
/* 151 */     if (pos < 0) {
/* 152 */       throw new IllegalArgumentException(Messages.getString("StringInspector.4"));
/*     */     }
/* 154 */     if (pos > this.stopAt) {
/* 155 */       throw new IllegalArgumentException(Messages.getString("StringInspector.5"));
/*     */     }
/* 157 */     int prevPos = this.pos;
/* 158 */     this.pos = pos;
/* 159 */     resetEscaped();
/* 160 */     this.inMysqlBlock = false;
/* 161 */     return prevPos;
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
/*     */   public int setStopPosition(int pos) {
/* 173 */     if (pos < 0) {
/* 174 */       throw new IllegalArgumentException(Messages.getString("StringInspector.6"));
/*     */     }
/* 176 */     if (pos > this.srcLen) {
/* 177 */       throw new IllegalArgumentException(Messages.getString("StringInspector.7"));
/*     */     }
/* 179 */     int prevPos = this.stopAt;
/* 180 */     this.stopAt = pos;
/* 181 */     return prevPos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 188 */     this.pos = 0;
/* 189 */     this.stopAt = this.srcLen;
/* 190 */     this.escaped = false;
/* 191 */     this.inMysqlBlock = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getChar() {
/* 201 */     if (this.pos >= this.stopAt) {
/* 202 */       return Character.MIN_VALUE;
/*     */     }
/* 204 */     return this.source.charAt(this.pos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPosition() {
/* 214 */     return this.pos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int incrementPosition() {
/* 225 */     return incrementPosition(this.defaultSearchMode);
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
/*     */   public int incrementPosition(Set<SearchMode> searchMode) {
/* 238 */     if (this.pos >= this.stopAt) {
/* 239 */       return -1;
/*     */     }
/* 241 */     if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && getChar() == '\\') {
/* 242 */       this.escaped = !this.escaped;
/* 243 */     } else if (this.escaped) {
/* 244 */       this.escaped = false;
/*     */     } 
/* 246 */     return ++this.pos;
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
/*     */   public int incrementPosition(int by) {
/* 259 */     return incrementPosition(by, this.defaultSearchMode);
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
/*     */   public int incrementPosition(int by, Set<SearchMode> searchMode) {
/* 274 */     for (int i = 0; i < by; i++) {
/* 275 */       if (incrementPosition(searchMode) == -1) {
/* 276 */         return -1;
/*     */       }
/*     */     } 
/* 279 */     return this.pos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resetEscaped() {
/* 287 */     this.escaped = false;
/* 288 */     if (this.defaultSearchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE)) {
/* 289 */       for (int i = this.pos - 1; i >= 0 && 
/* 290 */         this.source.charAt(i) == '\\'; i--)
/*     */       {
/*     */         
/* 293 */         this.escaped = !this.escaped;
/*     */       }
/*     */     }
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
/*     */   public int indexOfNextChar() {
/* 308 */     return indexOfNextChar(this.defaultSearchMode);
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
/*     */   private int indexOfNextChar(Set<SearchMode> searchMode) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield source : Ljava/lang/String;
/*     */     //   4: ifnonnull -> 9
/*     */     //   7: iconst_m1
/*     */     //   8: ireturn
/*     */     //   9: aload_0
/*     */     //   10: getfield pos : I
/*     */     //   13: aload_0
/*     */     //   14: getfield stopAt : I
/*     */     //   17: if_icmplt -> 22
/*     */     //   20: iconst_m1
/*     */     //   21: ireturn
/*     */     //   22: iconst_0
/*     */     //   23: istore_2
/*     */     //   24: aload_0
/*     */     //   25: getfield source : Ljava/lang/String;
/*     */     //   28: aload_0
/*     */     //   29: getfield pos : I
/*     */     //   32: invokevirtual charAt : (I)C
/*     */     //   35: istore_3
/*     */     //   36: aload_0
/*     */     //   37: getfield pos : I
/*     */     //   40: iconst_1
/*     */     //   41: iadd
/*     */     //   42: aload_0
/*     */     //   43: getfield srcLen : I
/*     */     //   46: if_icmpge -> 65
/*     */     //   49: aload_0
/*     */     //   50: getfield source : Ljava/lang/String;
/*     */     //   53: aload_0
/*     */     //   54: getfield pos : I
/*     */     //   57: iconst_1
/*     */     //   58: iadd
/*     */     //   59: invokevirtual charAt : (I)C
/*     */     //   62: goto -> 66
/*     */     //   65: iconst_0
/*     */     //   66: istore #4
/*     */     //   68: aload_0
/*     */     //   69: getfield pos : I
/*     */     //   72: aload_0
/*     */     //   73: getfield stopAt : I
/*     */     //   76: if_icmpge -> 1426
/*     */     //   79: iload_3
/*     */     //   80: istore_2
/*     */     //   81: iload #4
/*     */     //   83: istore_3
/*     */     //   84: aload_0
/*     */     //   85: getfield pos : I
/*     */     //   88: iconst_2
/*     */     //   89: iadd
/*     */     //   90: aload_0
/*     */     //   91: getfield srcLen : I
/*     */     //   94: if_icmpge -> 113
/*     */     //   97: aload_0
/*     */     //   98: getfield source : Ljava/lang/String;
/*     */     //   101: aload_0
/*     */     //   102: getfield pos : I
/*     */     //   105: iconst_2
/*     */     //   106: iadd
/*     */     //   107: invokevirtual charAt : (I)C
/*     */     //   110: goto -> 114
/*     */     //   113: iconst_0
/*     */     //   114: istore #4
/*     */     //   116: iconst_0
/*     */     //   117: istore #5
/*     */     //   119: aload_1
/*     */     //   120: getstatic com/mysql/cj/util/SearchMode.ALLOW_BACKSLASH_ESCAPE : Lcom/mysql/cj/util/SearchMode;
/*     */     //   123: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   128: ifeq -> 138
/*     */     //   131: aload_0
/*     */     //   132: getfield escaped : Z
/*     */     //   135: ifne -> 142
/*     */     //   138: iconst_1
/*     */     //   139: goto -> 143
/*     */     //   142: iconst_0
/*     */     //   143: istore #6
/*     */     //   145: iload #6
/*     */     //   147: ifeq -> 270
/*     */     //   150: aload_1
/*     */     //   151: getstatic com/mysql/cj/util/SearchMode.SKIP_BETWEEN_MARKERS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   154: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   159: ifeq -> 270
/*     */     //   162: aload_0
/*     */     //   163: getfield openingMarkers : Ljava/lang/String;
/*     */     //   166: iload_2
/*     */     //   167: invokevirtual indexOf : (I)I
/*     */     //   170: iconst_m1
/*     */     //   171: if_icmpeq -> 270
/*     */     //   174: aload_0
/*     */     //   175: aload_1
/*     */     //   176: invokespecial indexOfClosingMarker : (Ljava/util/Set;)I
/*     */     //   179: pop
/*     */     //   180: aload_0
/*     */     //   181: getfield pos : I
/*     */     //   184: aload_0
/*     */     //   185: getfield stopAt : I
/*     */     //   188: if_icmplt -> 204
/*     */     //   191: aload_0
/*     */     //   192: dup
/*     */     //   193: getfield pos : I
/*     */     //   196: iconst_1
/*     */     //   197: isub
/*     */     //   198: putfield pos : I
/*     */     //   201: goto -> 1408
/*     */     //   204: aload_0
/*     */     //   205: getfield pos : I
/*     */     //   208: iconst_1
/*     */     //   209: iadd
/*     */     //   210: aload_0
/*     */     //   211: getfield srcLen : I
/*     */     //   214: if_icmpge -> 233
/*     */     //   217: aload_0
/*     */     //   218: getfield source : Ljava/lang/String;
/*     */     //   221: aload_0
/*     */     //   222: getfield pos : I
/*     */     //   225: iconst_1
/*     */     //   226: iadd
/*     */     //   227: invokevirtual charAt : (I)C
/*     */     //   230: goto -> 234
/*     */     //   233: iconst_0
/*     */     //   234: istore_3
/*     */     //   235: aload_0
/*     */     //   236: getfield pos : I
/*     */     //   239: iconst_2
/*     */     //   240: iadd
/*     */     //   241: aload_0
/*     */     //   242: getfield srcLen : I
/*     */     //   245: if_icmpge -> 264
/*     */     //   248: aload_0
/*     */     //   249: getfield source : Ljava/lang/String;
/*     */     //   252: aload_0
/*     */     //   253: getfield pos : I
/*     */     //   256: iconst_2
/*     */     //   257: iadd
/*     */     //   258: invokevirtual charAt : (I)C
/*     */     //   261: goto -> 265
/*     */     //   264: iconst_0
/*     */     //   265: istore #4
/*     */     //   267: goto -> 1408
/*     */     //   270: iload #6
/*     */     //   272: ifeq -> 495
/*     */     //   275: aload_1
/*     */     //   276: getstatic com/mysql/cj/util/SearchMode.SKIP_BLOCK_COMMENTS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   279: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   284: ifeq -> 495
/*     */     //   287: iload_2
/*     */     //   288: bipush #47
/*     */     //   290: if_icmpne -> 495
/*     */     //   293: iload_3
/*     */     //   294: bipush #42
/*     */     //   296: if_icmpne -> 495
/*     */     //   299: iload #4
/*     */     //   301: bipush #33
/*     */     //   303: if_icmpeq -> 495
/*     */     //   306: iload #4
/*     */     //   308: bipush #43
/*     */     //   310: if_icmpeq -> 495
/*     */     //   313: aload_0
/*     */     //   314: dup
/*     */     //   315: getfield pos : I
/*     */     //   318: iconst_1
/*     */     //   319: iadd
/*     */     //   320: putfield pos : I
/*     */     //   323: aload_0
/*     */     //   324: dup
/*     */     //   325: getfield pos : I
/*     */     //   328: iconst_1
/*     */     //   329: iadd
/*     */     //   330: dup_x1
/*     */     //   331: putfield pos : I
/*     */     //   334: aload_0
/*     */     //   335: getfield stopAt : I
/*     */     //   338: if_icmpge -> 395
/*     */     //   341: aload_0
/*     */     //   342: getfield source : Ljava/lang/String;
/*     */     //   345: aload_0
/*     */     //   346: getfield pos : I
/*     */     //   349: invokevirtual charAt : (I)C
/*     */     //   352: bipush #42
/*     */     //   354: if_icmpne -> 323
/*     */     //   357: aload_0
/*     */     //   358: getfield pos : I
/*     */     //   361: iconst_1
/*     */     //   362: iadd
/*     */     //   363: aload_0
/*     */     //   364: getfield srcLen : I
/*     */     //   367: if_icmpge -> 386
/*     */     //   370: aload_0
/*     */     //   371: getfield source : Ljava/lang/String;
/*     */     //   374: aload_0
/*     */     //   375: getfield pos : I
/*     */     //   378: iconst_1
/*     */     //   379: iadd
/*     */     //   380: invokevirtual charAt : (I)C
/*     */     //   383: goto -> 387
/*     */     //   386: iconst_0
/*     */     //   387: bipush #47
/*     */     //   389: if_icmpeq -> 395
/*     */     //   392: goto -> 323
/*     */     //   395: aload_0
/*     */     //   396: getfield pos : I
/*     */     //   399: aload_0
/*     */     //   400: getfield stopAt : I
/*     */     //   403: if_icmplt -> 419
/*     */     //   406: aload_0
/*     */     //   407: dup
/*     */     //   408: getfield pos : I
/*     */     //   411: iconst_1
/*     */     //   412: isub
/*     */     //   413: putfield pos : I
/*     */     //   416: goto -> 429
/*     */     //   419: aload_0
/*     */     //   420: dup
/*     */     //   421: getfield pos : I
/*     */     //   424: iconst_1
/*     */     //   425: iadd
/*     */     //   426: putfield pos : I
/*     */     //   429: aload_0
/*     */     //   430: getfield pos : I
/*     */     //   433: iconst_1
/*     */     //   434: iadd
/*     */     //   435: aload_0
/*     */     //   436: getfield srcLen : I
/*     */     //   439: if_icmpge -> 458
/*     */     //   442: aload_0
/*     */     //   443: getfield source : Ljava/lang/String;
/*     */     //   446: aload_0
/*     */     //   447: getfield pos : I
/*     */     //   450: iconst_1
/*     */     //   451: iadd
/*     */     //   452: invokevirtual charAt : (I)C
/*     */     //   455: goto -> 459
/*     */     //   458: iconst_0
/*     */     //   459: istore_3
/*     */     //   460: aload_0
/*     */     //   461: getfield pos : I
/*     */     //   464: iconst_2
/*     */     //   465: iadd
/*     */     //   466: aload_0
/*     */     //   467: getfield srcLen : I
/*     */     //   470: if_icmpge -> 489
/*     */     //   473: aload_0
/*     */     //   474: getfield source : Ljava/lang/String;
/*     */     //   477: aload_0
/*     */     //   478: getfield pos : I
/*     */     //   481: iconst_2
/*     */     //   482: iadd
/*     */     //   483: invokevirtual charAt : (I)C
/*     */     //   486: goto -> 490
/*     */     //   489: iconst_0
/*     */     //   490: istore #4
/*     */     //   492: goto -> 1408
/*     */     //   495: iload #6
/*     */     //   497: ifeq -> 840
/*     */     //   500: aload_1
/*     */     //   501: getstatic com/mysql/cj/util/SearchMode.SKIP_LINE_COMMENTS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   504: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   509: ifeq -> 840
/*     */     //   512: iload_2
/*     */     //   513: bipush #45
/*     */     //   515: if_icmpne -> 555
/*     */     //   518: iload_3
/*     */     //   519: bipush #45
/*     */     //   521: if_icmpne -> 555
/*     */     //   524: iload #4
/*     */     //   526: invokestatic isWhitespace : (C)Z
/*     */     //   529: ifne -> 561
/*     */     //   532: iload #4
/*     */     //   534: bipush #59
/*     */     //   536: if_icmpne -> 543
/*     */     //   539: iconst_1
/*     */     //   540: goto -> 544
/*     */     //   543: iconst_0
/*     */     //   544: dup
/*     */     //   545: istore #5
/*     */     //   547: ifne -> 561
/*     */     //   550: iload #4
/*     */     //   552: ifeq -> 561
/*     */     //   555: iload_2
/*     */     //   556: bipush #35
/*     */     //   558: if_icmpne -> 840
/*     */     //   561: iload #5
/*     */     //   563: ifeq -> 652
/*     */     //   566: aload_0
/*     */     //   567: dup
/*     */     //   568: getfield pos : I
/*     */     //   571: iconst_1
/*     */     //   572: iadd
/*     */     //   573: putfield pos : I
/*     */     //   576: aload_0
/*     */     //   577: dup
/*     */     //   578: getfield pos : I
/*     */     //   581: iconst_1
/*     */     //   582: iadd
/*     */     //   583: putfield pos : I
/*     */     //   586: aload_0
/*     */     //   587: getfield pos : I
/*     */     //   590: iconst_1
/*     */     //   591: iadd
/*     */     //   592: aload_0
/*     */     //   593: getfield srcLen : I
/*     */     //   596: if_icmpge -> 615
/*     */     //   599: aload_0
/*     */     //   600: getfield source : Ljava/lang/String;
/*     */     //   603: aload_0
/*     */     //   604: getfield pos : I
/*     */     //   607: iconst_1
/*     */     //   608: iadd
/*     */     //   609: invokevirtual charAt : (I)C
/*     */     //   612: goto -> 616
/*     */     //   615: iconst_0
/*     */     //   616: istore_3
/*     */     //   617: aload_0
/*     */     //   618: getfield pos : I
/*     */     //   621: iconst_2
/*     */     //   622: iadd
/*     */     //   623: aload_0
/*     */     //   624: getfield srcLen : I
/*     */     //   627: if_icmpge -> 646
/*     */     //   630: aload_0
/*     */     //   631: getfield source : Ljava/lang/String;
/*     */     //   634: aload_0
/*     */     //   635: getfield pos : I
/*     */     //   638: iconst_2
/*     */     //   639: iadd
/*     */     //   640: invokevirtual charAt : (I)C
/*     */     //   643: goto -> 647
/*     */     //   646: iconst_0
/*     */     //   647: istore #4
/*     */     //   649: goto -> 1408
/*     */     //   652: aload_0
/*     */     //   653: dup
/*     */     //   654: getfield pos : I
/*     */     //   657: iconst_1
/*     */     //   658: iadd
/*     */     //   659: dup_x1
/*     */     //   660: putfield pos : I
/*     */     //   663: aload_0
/*     */     //   664: getfield stopAt : I
/*     */     //   667: if_icmpge -> 697
/*     */     //   670: aload_0
/*     */     //   671: getfield source : Ljava/lang/String;
/*     */     //   674: aload_0
/*     */     //   675: getfield pos : I
/*     */     //   678: invokevirtual charAt : (I)C
/*     */     //   681: dup
/*     */     //   682: istore_2
/*     */     //   683: bipush #10
/*     */     //   685: if_icmpeq -> 697
/*     */     //   688: iload_2
/*     */     //   689: bipush #13
/*     */     //   691: if_icmpeq -> 697
/*     */     //   694: goto -> 652
/*     */     //   697: aload_0
/*     */     //   698: getfield pos : I
/*     */     //   701: aload_0
/*     */     //   702: getfield stopAt : I
/*     */     //   705: if_icmplt -> 721
/*     */     //   708: aload_0
/*     */     //   709: dup
/*     */     //   710: getfield pos : I
/*     */     //   713: iconst_1
/*     */     //   714: isub
/*     */     //   715: putfield pos : I
/*     */     //   718: goto -> 1408
/*     */     //   721: aload_0
/*     */     //   722: getfield pos : I
/*     */     //   725: iconst_1
/*     */     //   726: iadd
/*     */     //   727: aload_0
/*     */     //   728: getfield srcLen : I
/*     */     //   731: if_icmpge -> 750
/*     */     //   734: aload_0
/*     */     //   735: getfield source : Ljava/lang/String;
/*     */     //   738: aload_0
/*     */     //   739: getfield pos : I
/*     */     //   742: iconst_1
/*     */     //   743: iadd
/*     */     //   744: invokevirtual charAt : (I)C
/*     */     //   747: goto -> 751
/*     */     //   750: iconst_0
/*     */     //   751: istore_3
/*     */     //   752: iload_2
/*     */     //   753: bipush #13
/*     */     //   755: if_icmpne -> 805
/*     */     //   758: iload_3
/*     */     //   759: bipush #10
/*     */     //   761: if_icmpne -> 805
/*     */     //   764: aload_0
/*     */     //   765: dup
/*     */     //   766: getfield pos : I
/*     */     //   769: iconst_1
/*     */     //   770: iadd
/*     */     //   771: putfield pos : I
/*     */     //   774: aload_0
/*     */     //   775: getfield pos : I
/*     */     //   778: iconst_1
/*     */     //   779: iadd
/*     */     //   780: aload_0
/*     */     //   781: getfield srcLen : I
/*     */     //   784: if_icmpge -> 803
/*     */     //   787: aload_0
/*     */     //   788: getfield source : Ljava/lang/String;
/*     */     //   791: aload_0
/*     */     //   792: getfield pos : I
/*     */     //   795: iconst_1
/*     */     //   796: iadd
/*     */     //   797: invokevirtual charAt : (I)C
/*     */     //   800: goto -> 804
/*     */     //   803: iconst_0
/*     */     //   804: istore_3
/*     */     //   805: aload_0
/*     */     //   806: getfield pos : I
/*     */     //   809: iconst_2
/*     */     //   810: iadd
/*     */     //   811: aload_0
/*     */     //   812: getfield srcLen : I
/*     */     //   815: if_icmpge -> 834
/*     */     //   818: aload_0
/*     */     //   819: getfield source : Ljava/lang/String;
/*     */     //   822: aload_0
/*     */     //   823: getfield pos : I
/*     */     //   826: iconst_2
/*     */     //   827: iadd
/*     */     //   828: invokevirtual charAt : (I)C
/*     */     //   831: goto -> 835
/*     */     //   834: iconst_0
/*     */     //   835: istore #4
/*     */     //   837: goto -> 1408
/*     */     //   840: iload #6
/*     */     //   842: ifeq -> 1068
/*     */     //   845: aload_1
/*     */     //   846: getstatic com/mysql/cj/util/SearchMode.SKIP_HINT_BLOCKS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   849: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   854: ifeq -> 1068
/*     */     //   857: iload_2
/*     */     //   858: bipush #47
/*     */     //   860: if_icmpne -> 1068
/*     */     //   863: iload_3
/*     */     //   864: bipush #42
/*     */     //   866: if_icmpne -> 1068
/*     */     //   869: iload #4
/*     */     //   871: bipush #43
/*     */     //   873: if_icmpne -> 1068
/*     */     //   876: aload_0
/*     */     //   877: dup
/*     */     //   878: getfield pos : I
/*     */     //   881: iconst_1
/*     */     //   882: iadd
/*     */     //   883: putfield pos : I
/*     */     //   886: aload_0
/*     */     //   887: dup
/*     */     //   888: getfield pos : I
/*     */     //   891: iconst_1
/*     */     //   892: iadd
/*     */     //   893: putfield pos : I
/*     */     //   896: aload_0
/*     */     //   897: dup
/*     */     //   898: getfield pos : I
/*     */     //   901: iconst_1
/*     */     //   902: iadd
/*     */     //   903: dup_x1
/*     */     //   904: putfield pos : I
/*     */     //   907: aload_0
/*     */     //   908: getfield stopAt : I
/*     */     //   911: if_icmpge -> 968
/*     */     //   914: aload_0
/*     */     //   915: getfield source : Ljava/lang/String;
/*     */     //   918: aload_0
/*     */     //   919: getfield pos : I
/*     */     //   922: invokevirtual charAt : (I)C
/*     */     //   925: bipush #42
/*     */     //   927: if_icmpne -> 896
/*     */     //   930: aload_0
/*     */     //   931: getfield pos : I
/*     */     //   934: iconst_1
/*     */     //   935: iadd
/*     */     //   936: aload_0
/*     */     //   937: getfield srcLen : I
/*     */     //   940: if_icmpge -> 959
/*     */     //   943: aload_0
/*     */     //   944: getfield source : Ljava/lang/String;
/*     */     //   947: aload_0
/*     */     //   948: getfield pos : I
/*     */     //   951: iconst_1
/*     */     //   952: iadd
/*     */     //   953: invokevirtual charAt : (I)C
/*     */     //   956: goto -> 960
/*     */     //   959: iconst_0
/*     */     //   960: bipush #47
/*     */     //   962: if_icmpeq -> 968
/*     */     //   965: goto -> 896
/*     */     //   968: aload_0
/*     */     //   969: getfield pos : I
/*     */     //   972: aload_0
/*     */     //   973: getfield stopAt : I
/*     */     //   976: if_icmplt -> 992
/*     */     //   979: aload_0
/*     */     //   980: dup
/*     */     //   981: getfield pos : I
/*     */     //   984: iconst_1
/*     */     //   985: isub
/*     */     //   986: putfield pos : I
/*     */     //   989: goto -> 1002
/*     */     //   992: aload_0
/*     */     //   993: dup
/*     */     //   994: getfield pos : I
/*     */     //   997: iconst_1
/*     */     //   998: iadd
/*     */     //   999: putfield pos : I
/*     */     //   1002: aload_0
/*     */     //   1003: getfield pos : I
/*     */     //   1006: iconst_1
/*     */     //   1007: iadd
/*     */     //   1008: aload_0
/*     */     //   1009: getfield srcLen : I
/*     */     //   1012: if_icmpge -> 1031
/*     */     //   1015: aload_0
/*     */     //   1016: getfield source : Ljava/lang/String;
/*     */     //   1019: aload_0
/*     */     //   1020: getfield pos : I
/*     */     //   1023: iconst_1
/*     */     //   1024: iadd
/*     */     //   1025: invokevirtual charAt : (I)C
/*     */     //   1028: goto -> 1032
/*     */     //   1031: iconst_0
/*     */     //   1032: istore_3
/*     */     //   1033: aload_0
/*     */     //   1034: getfield pos : I
/*     */     //   1037: iconst_2
/*     */     //   1038: iadd
/*     */     //   1039: aload_0
/*     */     //   1040: getfield srcLen : I
/*     */     //   1043: if_icmpge -> 1062
/*     */     //   1046: aload_0
/*     */     //   1047: getfield source : Ljava/lang/String;
/*     */     //   1050: aload_0
/*     */     //   1051: getfield pos : I
/*     */     //   1054: iconst_2
/*     */     //   1055: iadd
/*     */     //   1056: invokevirtual charAt : (I)C
/*     */     //   1059: goto -> 1063
/*     */     //   1062: iconst_0
/*     */     //   1063: istore #4
/*     */     //   1065: goto -> 1408
/*     */     //   1068: iload #6
/*     */     //   1070: ifeq -> 1295
/*     */     //   1073: aload_1
/*     */     //   1074: getstatic com/mysql/cj/util/SearchMode.SKIP_MYSQL_MARKERS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   1077: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1082: ifeq -> 1295
/*     */     //   1085: iload_2
/*     */     //   1086: bipush #47
/*     */     //   1088: if_icmpne -> 1295
/*     */     //   1091: iload_3
/*     */     //   1092: bipush #42
/*     */     //   1094: if_icmpne -> 1295
/*     */     //   1097: iload #4
/*     */     //   1099: bipush #33
/*     */     //   1101: if_icmpne -> 1295
/*     */     //   1104: aload_0
/*     */     //   1105: dup
/*     */     //   1106: getfield pos : I
/*     */     //   1109: iconst_1
/*     */     //   1110: iadd
/*     */     //   1111: putfield pos : I
/*     */     //   1114: aload_0
/*     */     //   1115: dup
/*     */     //   1116: getfield pos : I
/*     */     //   1119: iconst_1
/*     */     //   1120: iadd
/*     */     //   1121: putfield pos : I
/*     */     //   1124: iload #4
/*     */     //   1126: bipush #33
/*     */     //   1128: if_icmpne -> 1224
/*     */     //   1131: iconst_0
/*     */     //   1132: istore #7
/*     */     //   1134: iload #7
/*     */     //   1136: iconst_5
/*     */     //   1137: if_icmpge -> 1187
/*     */     //   1140: aload_0
/*     */     //   1141: getfield pos : I
/*     */     //   1144: iconst_1
/*     */     //   1145: iadd
/*     */     //   1146: iload #7
/*     */     //   1148: iadd
/*     */     //   1149: aload_0
/*     */     //   1150: getfield srcLen : I
/*     */     //   1153: if_icmpge -> 1187
/*     */     //   1156: aload_0
/*     */     //   1157: getfield source : Ljava/lang/String;
/*     */     //   1160: aload_0
/*     */     //   1161: getfield pos : I
/*     */     //   1164: iconst_1
/*     */     //   1165: iadd
/*     */     //   1166: iload #7
/*     */     //   1168: iadd
/*     */     //   1169: invokevirtual charAt : (I)C
/*     */     //   1172: invokestatic isDigit : (C)Z
/*     */     //   1175: ifne -> 1181
/*     */     //   1178: goto -> 1187
/*     */     //   1181: iinc #7, 1
/*     */     //   1184: goto -> 1134
/*     */     //   1187: iload #7
/*     */     //   1189: iconst_5
/*     */     //   1190: if_icmpne -> 1224
/*     */     //   1193: aload_0
/*     */     //   1194: dup
/*     */     //   1195: getfield pos : I
/*     */     //   1198: iconst_5
/*     */     //   1199: iadd
/*     */     //   1200: putfield pos : I
/*     */     //   1203: aload_0
/*     */     //   1204: getfield pos : I
/*     */     //   1207: aload_0
/*     */     //   1208: getfield stopAt : I
/*     */     //   1211: if_icmplt -> 1224
/*     */     //   1214: aload_0
/*     */     //   1215: aload_0
/*     */     //   1216: getfield stopAt : I
/*     */     //   1219: iconst_1
/*     */     //   1220: isub
/*     */     //   1221: putfield pos : I
/*     */     //   1224: aload_0
/*     */     //   1225: getfield pos : I
/*     */     //   1228: iconst_1
/*     */     //   1229: iadd
/*     */     //   1230: aload_0
/*     */     //   1231: getfield srcLen : I
/*     */     //   1234: if_icmpge -> 1253
/*     */     //   1237: aload_0
/*     */     //   1238: getfield source : Ljava/lang/String;
/*     */     //   1241: aload_0
/*     */     //   1242: getfield pos : I
/*     */     //   1245: iconst_1
/*     */     //   1246: iadd
/*     */     //   1247: invokevirtual charAt : (I)C
/*     */     //   1250: goto -> 1254
/*     */     //   1253: iconst_0
/*     */     //   1254: istore_3
/*     */     //   1255: aload_0
/*     */     //   1256: getfield pos : I
/*     */     //   1259: iconst_2
/*     */     //   1260: iadd
/*     */     //   1261: aload_0
/*     */     //   1262: getfield srcLen : I
/*     */     //   1265: if_icmpge -> 1284
/*     */     //   1268: aload_0
/*     */     //   1269: getfield source : Ljava/lang/String;
/*     */     //   1272: aload_0
/*     */     //   1273: getfield pos : I
/*     */     //   1276: iconst_2
/*     */     //   1277: iadd
/*     */     //   1278: invokevirtual charAt : (I)C
/*     */     //   1281: goto -> 1285
/*     */     //   1284: iconst_0
/*     */     //   1285: istore #4
/*     */     //   1287: aload_0
/*     */     //   1288: iconst_1
/*     */     //   1289: putfield inMysqlBlock : Z
/*     */     //   1292: goto -> 1408
/*     */     //   1295: aload_0
/*     */     //   1296: getfield inMysqlBlock : Z
/*     */     //   1299: ifeq -> 1384
/*     */     //   1302: iload #6
/*     */     //   1304: ifeq -> 1384
/*     */     //   1307: aload_1
/*     */     //   1308: getstatic com/mysql/cj/util/SearchMode.SKIP_MYSQL_MARKERS : Lcom/mysql/cj/util/SearchMode;
/*     */     //   1311: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1316: ifeq -> 1384
/*     */     //   1319: iload_2
/*     */     //   1320: bipush #42
/*     */     //   1322: if_icmpne -> 1384
/*     */     //   1325: iload_3
/*     */     //   1326: bipush #47
/*     */     //   1328: if_icmpne -> 1384
/*     */     //   1331: aload_0
/*     */     //   1332: dup
/*     */     //   1333: getfield pos : I
/*     */     //   1336: iconst_1
/*     */     //   1337: iadd
/*     */     //   1338: putfield pos : I
/*     */     //   1341: iload #4
/*     */     //   1343: istore_3
/*     */     //   1344: aload_0
/*     */     //   1345: getfield pos : I
/*     */     //   1348: iconst_2
/*     */     //   1349: iadd
/*     */     //   1350: aload_0
/*     */     //   1351: getfield srcLen : I
/*     */     //   1354: if_icmpge -> 1373
/*     */     //   1357: aload_0
/*     */     //   1358: getfield source : Ljava/lang/String;
/*     */     //   1361: aload_0
/*     */     //   1362: getfield pos : I
/*     */     //   1365: iconst_2
/*     */     //   1366: iadd
/*     */     //   1367: invokevirtual charAt : (I)C
/*     */     //   1370: goto -> 1374
/*     */     //   1373: iconst_0
/*     */     //   1374: istore #4
/*     */     //   1376: aload_0
/*     */     //   1377: iconst_0
/*     */     //   1378: putfield inMysqlBlock : Z
/*     */     //   1381: goto -> 1408
/*     */     //   1384: aload_1
/*     */     //   1385: getstatic com/mysql/cj/util/SearchMode.SKIP_WHITE_SPACE : Lcom/mysql/cj/util/SearchMode;
/*     */     //   1388: invokeinterface contains : (Ljava/lang/Object;)Z
/*     */     //   1393: ifeq -> 1403
/*     */     //   1396: iload_2
/*     */     //   1397: invokestatic isWhitespace : (C)Z
/*     */     //   1400: ifne -> 1408
/*     */     //   1403: aload_0
/*     */     //   1404: getfield pos : I
/*     */     //   1407: ireturn
/*     */     //   1408: aload_0
/*     */     //   1409: iconst_0
/*     */     //   1410: putfield escaped : Z
/*     */     //   1413: aload_0
/*     */     //   1414: dup
/*     */     //   1415: getfield pos : I
/*     */     //   1418: iconst_1
/*     */     //   1419: iadd
/*     */     //   1420: putfield pos : I
/*     */     //   1423: goto -> 68
/*     */     //   1426: iconst_m1
/*     */     //   1427: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #323	-> 0
/*     */     //   #324	-> 7
/*     */     //   #327	-> 9
/*     */     //   #328	-> 20
/*     */     //   #331	-> 22
/*     */     //   #332	-> 24
/*     */     //   #333	-> 36
/*     */     //   #335	-> 68
/*     */     //   #336	-> 79
/*     */     //   #337	-> 81
/*     */     //   #338	-> 84
/*     */     //   #340	-> 116
/*     */     //   #341	-> 119
/*     */     //   #343	-> 145
/*     */     //   #345	-> 174
/*     */     //   #346	-> 180
/*     */     //   #347	-> 191
/*     */     //   #350	-> 204
/*     */     //   #351	-> 235
/*     */     //   #354	-> 270
/*     */     //   #357	-> 313
/*     */     //   #358	-> 323
/*     */     //   #359	-> 380
/*     */     //   #362	-> 395
/*     */     //   #363	-> 406
/*     */     //   #365	-> 419
/*     */     //   #369	-> 429
/*     */     //   #370	-> 460
/*     */     //   #372	-> 495
/*     */     //   #373	-> 526
/*     */     //   #375	-> 561
/*     */     //   #377	-> 566
/*     */     //   #378	-> 576
/*     */     //   #380	-> 586
/*     */     //   #381	-> 617
/*     */     //   #384	-> 652
/*     */     //   #387	-> 697
/*     */     //   #388	-> 708
/*     */     //   #391	-> 721
/*     */     //   #392	-> 752
/*     */     //   #394	-> 764
/*     */     //   #395	-> 774
/*     */     //   #397	-> 805
/*     */     //   #401	-> 840
/*     */     //   #403	-> 876
/*     */     //   #404	-> 886
/*     */     //   #405	-> 896
/*     */     //   #406	-> 953
/*     */     //   #409	-> 968
/*     */     //   #410	-> 979
/*     */     //   #412	-> 992
/*     */     //   #416	-> 1002
/*     */     //   #417	-> 1033
/*     */     //   #419	-> 1068
/*     */     //   #421	-> 1104
/*     */     //   #422	-> 1114
/*     */     //   #423	-> 1124
/*     */     //   #425	-> 1131
/*     */     //   #426	-> 1134
/*     */     //   #427	-> 1140
/*     */     //   #428	-> 1178
/*     */     //   #426	-> 1181
/*     */     //   #431	-> 1187
/*     */     //   #432	-> 1193
/*     */     //   #433	-> 1203
/*     */     //   #434	-> 1214
/*     */     //   #440	-> 1224
/*     */     //   #441	-> 1255
/*     */     //   #443	-> 1287
/*     */     //   #445	-> 1295
/*     */     //   #447	-> 1331
/*     */     //   #449	-> 1341
/*     */     //   #450	-> 1344
/*     */     //   #452	-> 1376
/*     */     //   #454	-> 1384
/*     */     //   #456	-> 1403
/*     */     //   #460	-> 1408
/*     */     //   #335	-> 1413
/*     */     //   #463	-> 1426
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   1134	90	7	i	I
/*     */     //   119	1294	5	dashDashCommentImmediateEnd	Z
/*     */     //   145	1268	6	checkSkipConditions	Z
/*     */     //   0	1428	0	this	Lcom/mysql/cj/util/StringInspector;
/*     */     //   0	1428	1	searchMode	Ljava/util/Set;
/*     */     //   24	1404	2	c0	C
/*     */     //   36	1392	3	c1	C
/*     */     //   68	1360	4	c2	C
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	1428	1	searchMode	Ljava/util/Set<Lcom/mysql/cj/util/SearchMode;>;
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
/*     */   private int indexOfClosingMarker(Set<SearchMode> searchMode) {
/* 476 */     if (this.source == null) {
/* 477 */       return -1;
/*     */     }
/*     */     
/* 480 */     if (this.pos >= this.stopAt) {
/* 481 */       return -1;
/*     */     }
/*     */     
/* 484 */     char c0 = this.source.charAt(this.pos);
/* 485 */     int markerIndex = this.openingMarkers.indexOf(c0);
/* 486 */     if (markerIndex == -1)
/*     */     {
/* 488 */       return this.pos;
/*     */     }
/*     */     
/* 491 */     int nestedMarkersCount = 0;
/* 492 */     char openingMarker = c0;
/* 493 */     char closingMarker = this.closingMarkers.charAt(markerIndex);
/* 494 */     boolean outerIsAnOverridingMarker = (this.overridingMarkers.indexOf(openingMarker) != -1);
/* 495 */     while (++this.pos < this.stopAt && ((c0 = this.source.charAt(this.pos)) != closingMarker || nestedMarkersCount != 0)) {
/* 496 */       if (!outerIsAnOverridingMarker && this.overridingMarkers.indexOf(c0) != -1) {
/*     */         
/* 498 */         int overridingMarkerIndex = this.openingMarkers.indexOf(c0);
/* 499 */         int overridingNestedMarkersCount = 0;
/* 500 */         char overridingOpeningMarker = c0;
/* 501 */         char overridingClosingMarker = this.closingMarkers.charAt(overridingMarkerIndex);
/* 502 */         while (++this.pos < this.stopAt && ((c0 = this.source.charAt(this.pos)) != overridingClosingMarker || overridingNestedMarkersCount != 0)) {
/*     */           
/* 504 */           if (c0 == overridingOpeningMarker) {
/* 505 */             overridingNestedMarkersCount++; continue;
/* 506 */           }  if (c0 == overridingClosingMarker) {
/* 507 */             overridingNestedMarkersCount--; continue;
/* 508 */           }  if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && c0 == '\\') {
/* 509 */             this.pos++;
/*     */           }
/*     */         } 
/*     */         
/* 513 */         if (this.pos >= this.stopAt)
/* 514 */           this.pos--;  continue;
/*     */       } 
/* 516 */       if (c0 == openingMarker) {
/* 517 */         nestedMarkersCount++; continue;
/* 518 */       }  if (c0 == closingMarker) {
/* 519 */         nestedMarkersCount--; continue;
/* 520 */       }  if (searchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE) && c0 == '\\') {
/* 521 */         this.pos++;
/*     */       }
/*     */     } 
/*     */     
/* 525 */     return this.pos;
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
/*     */   public int indexOfNextAlphanumericChar() {
/* 538 */     if (this.source == null) {
/* 539 */       return -1;
/*     */     }
/*     */     
/* 542 */     if (this.pos >= this.stopAt) {
/* 543 */       return -1;
/*     */     }
/*     */     
/* 546 */     Set<SearchMode> searchMode = this.defaultSearchMode;
/* 547 */     if (!this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
/* 548 */       searchMode = EnumSet.copyOf(this.defaultSearchMode);
/* 549 */       searchMode.add(SearchMode.SKIP_WHITE_SPACE);
/*     */     } 
/*     */     
/* 552 */     while (this.pos < this.stopAt) {
/* 553 */       int prevPos = this.pos;
/* 554 */       if (indexOfNextChar(searchMode) == -1) {
/* 555 */         return -1;
/*     */       }
/* 557 */       if (Character.isLetterOrDigit(this.source.charAt(this.pos))) {
/* 558 */         return this.pos;
/*     */       }
/* 560 */       if (this.pos == prevPos)
/*     */       {
/* 562 */         incrementPosition(searchMode);
/*     */       }
/*     */     } 
/* 565 */     return -1;
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
/*     */   public int indexOfNextNonWsChar() {
/* 578 */     if (this.source == null) {
/* 579 */       return -1;
/*     */     }
/*     */     
/* 582 */     if (this.pos >= this.stopAt) {
/* 583 */       return -1;
/*     */     }
/*     */     
/* 586 */     Set<SearchMode> searchMode = this.defaultSearchMode;
/* 587 */     if (!this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
/* 588 */       searchMode = EnumSet.copyOf(this.defaultSearchMode);
/* 589 */       searchMode.add(SearchMode.SKIP_WHITE_SPACE);
/*     */     } 
/*     */     
/* 592 */     return indexOfNextChar(searchMode);
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
/*     */   public int indexOfNextWsChar() {
/* 605 */     if (this.source == null) {
/* 606 */       return -1;
/*     */     }
/*     */     
/* 609 */     if (this.pos >= this.stopAt) {
/* 610 */       return -1;
/*     */     }
/*     */     
/* 613 */     Set<SearchMode> searchMode = this.defaultSearchMode;
/* 614 */     if (this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
/* 615 */       searchMode = EnumSet.copyOf(this.defaultSearchMode);
/* 616 */       searchMode.remove(SearchMode.SKIP_WHITE_SPACE);
/*     */     } 
/*     */     
/* 619 */     while (this.pos < this.stopAt) {
/* 620 */       int prevPos = this.pos;
/* 621 */       if (indexOfNextChar(searchMode) == -1) {
/* 622 */         return -1;
/*     */       }
/* 624 */       if (Character.isWhitespace(this.source.charAt(this.pos))) {
/* 625 */         return this.pos;
/*     */       }
/* 627 */       if (this.pos == prevPos)
/*     */       {
/* 629 */         incrementPosition(searchMode);
/*     */       }
/*     */     } 
/* 632 */     return -1;
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
/*     */   public int indexOfIgnoreCase(String searchFor) {
/* 645 */     return indexOfIgnoreCase(searchFor, this.defaultSearchMode);
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
/*     */   public int indexOfIgnoreCase(String searchFor, Set<SearchMode> searchMode) {
/* 660 */     if (searchFor == null) {
/* 661 */       return -1;
/*     */     }
/*     */     
/* 664 */     int searchForLength = searchFor.length();
/* 665 */     int localStopAt = this.srcLen - searchForLength + 1;
/* 666 */     if (localStopAt > this.stopAt) {
/* 667 */       localStopAt = this.stopAt;
/*     */     }
/*     */     
/* 670 */     if (this.pos >= localStopAt || searchForLength == 0) {
/* 671 */       return -1;
/*     */     }
/*     */ 
/*     */     
/* 675 */     char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
/* 676 */     char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
/*     */     
/* 678 */     Set<SearchMode> localSearchMode = searchMode;
/* 679 */     if (Character.isWhitespace(firstCharOfSearchForLc) && this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
/*     */       
/* 681 */       localSearchMode = EnumSet.copyOf(this.defaultSearchMode);
/* 682 */       localSearchMode.remove(SearchMode.SKIP_WHITE_SPACE);
/*     */     } 
/*     */     
/* 685 */     while (this.pos < localStopAt) {
/* 686 */       if (indexOfNextChar(localSearchMode) == -1) {
/* 687 */         return -1;
/*     */       }
/*     */       
/* 690 */       if (StringUtils.isCharEqualIgnoreCase(getChar(), firstCharOfSearchForUc, firstCharOfSearchForLc) && 
/* 691 */         StringUtils.regionMatchesIgnoreCase(this.source, this.pos, searchFor)) {
/* 692 */         return this.pos;
/*     */       }
/*     */       
/* 695 */       incrementPosition(localSearchMode);
/*     */     } 
/*     */     
/* 698 */     return -1;
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
/*     */   public int indexOfIgnoreCase(String... searchFor) {
/* 716 */     if (searchFor == null) {
/* 717 */       return -1;
/*     */     }
/*     */     
/* 720 */     int searchForLength = 0;
/* 721 */     for (String searchForPart : searchFor) {
/* 722 */       searchForLength += searchForPart.length();
/*     */     }
/*     */     
/* 725 */     if (searchForLength == 0) {
/* 726 */       return -1;
/*     */     }
/*     */     
/* 729 */     int searchForWordsCount = searchFor.length;
/* 730 */     searchForLength += (searchForWordsCount > 0) ? (searchForWordsCount - 1) : 0;
/* 731 */     int localStopAt = this.srcLen - searchForLength + 1;
/* 732 */     if (localStopAt > this.stopAt) {
/* 733 */       localStopAt = this.stopAt;
/*     */     }
/*     */     
/* 736 */     if (this.pos >= localStopAt) {
/* 737 */       return -1;
/*     */     }
/*     */     
/* 740 */     Set<SearchMode> searchMode1 = this.defaultSearchMode;
/* 741 */     if (Character.isWhitespace(searchFor[0].charAt(0)) && this.defaultSearchMode.contains(SearchMode.SKIP_WHITE_SPACE)) {
/*     */       
/* 743 */       searchMode1 = EnumSet.copyOf(this.defaultSearchMode);
/* 744 */       searchMode1.remove(SearchMode.SKIP_WHITE_SPACE);
/*     */     } 
/*     */ 
/*     */     
/* 748 */     Set<SearchMode> searchMode2 = EnumSet.copyOf(this.defaultSearchMode);
/* 749 */     searchMode2.add(SearchMode.SKIP_WHITE_SPACE);
/* 750 */     searchMode2.remove(SearchMode.SKIP_BETWEEN_MARKERS);
/*     */     
/* 752 */     while (this.pos < localStopAt) {
/* 753 */       int positionOfFirstWord = indexOfIgnoreCase(searchFor[0], searchMode1);
/* 754 */       if (positionOfFirstWord == -1 || positionOfFirstWord >= localStopAt) {
/* 755 */         return -1;
/*     */       }
/*     */       
/* 758 */       int startingPositionForNextWord = incrementPosition(searchFor[0].length(), searchMode2);
/* 759 */       int wc = 0;
/* 760 */       boolean match = true;
/* 761 */       while (++wc < searchForWordsCount && match) {
/* 762 */         if (indexOfNextChar(searchMode2) == -1 || startingPositionForNextWord == this.pos || 
/* 763 */           !StringUtils.regionMatchesIgnoreCase(this.source, this.pos, searchFor[wc])) {
/*     */           
/* 765 */           match = false; continue;
/*     */         } 
/* 767 */         startingPositionForNextWord = incrementPosition(searchFor[wc].length(), searchMode2);
/*     */       } 
/*     */ 
/*     */       
/* 771 */       if (match) {
/* 772 */         setStartPosition(positionOfFirstWord);
/* 773 */         return positionOfFirstWord;
/*     */       } 
/*     */     } 
/*     */     
/* 777 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String stripCommentsAndHints() {
/* 787 */     reset();
/*     */     
/* 789 */     Set<SearchMode> searchMode = EnumSet.of(SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_HINT_BLOCKS);
/* 790 */     if (this.defaultSearchMode.contains(SearchMode.ALLOW_BACKSLASH_ESCAPE)) {
/* 791 */       searchMode.add(SearchMode.ALLOW_BACKSLASH_ESCAPE);
/*     */     }
/*     */     
/* 794 */     StringBuilder noCommsStr = new StringBuilder(this.source.length());
/* 795 */     while (this.pos < this.stopAt) {
/* 796 */       int prevPos = this.pos;
/* 797 */       if (indexOfNextChar(searchMode) == -1) {
/* 798 */         return noCommsStr.toString();
/*     */       }
/*     */       
/* 801 */       if (!this.escaped && this.openingMarkers.indexOf(getChar()) != -1) {
/*     */         
/* 803 */         int idxOpMrkr = this.pos;
/* 804 */         if (indexOfClosingMarker(searchMode) < this.srcLen) {
/* 805 */           incrementPosition(searchMode);
/*     */         }
/* 807 */         noCommsStr.append(this.source, idxOpMrkr, this.pos); continue;
/*     */       } 
/* 809 */       if (this.pos - prevPos > 1 && 
/* 810 */         prevPos > 0 && !Character.isWhitespace(this.source.charAt(prevPos - 1)) && !Character.isWhitespace(this.source.charAt(this.pos))) {
/* 811 */         noCommsStr.append(" ");
/*     */       }
/*     */       
/* 814 */       noCommsStr.append(getChar());
/* 815 */       incrementPosition(searchMode);
/*     */     } 
/*     */ 
/*     */     
/* 819 */     return noCommsStr.toString();
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
/*     */   public List<String> split(String delimiter, boolean trim) {
/* 833 */     if (delimiter == null) {
/* 834 */       throw new IllegalArgumentException(Messages.getString("StringInspector.8"));
/*     */     }
/*     */     
/* 837 */     reset();
/*     */     
/* 839 */     int startPos = 0;
/* 840 */     List<String> splitParts = new ArrayList<>();
/* 841 */     while (indexOfIgnoreCase(delimiter) != -1) {
/* 842 */       indexOfIgnoreCase(delimiter);
/* 843 */       String part = this.source.substring(startPos, this.pos);
/* 844 */       if (trim) {
/* 845 */         part = part.trim();
/*     */       }
/* 847 */       splitParts.add(part);
/* 848 */       startPos = incrementPosition(delimiter.length());
/*     */     } 
/*     */ 
/*     */     
/* 852 */     String token = this.source.substring(startPos);
/* 853 */     if (trim) {
/* 854 */       token = token.trim();
/*     */     }
/* 856 */     splitParts.add(token);
/*     */     
/* 858 */     return splitParts;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\StringInspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */