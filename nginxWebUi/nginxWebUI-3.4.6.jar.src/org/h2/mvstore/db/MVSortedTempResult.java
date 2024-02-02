/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.Cursor;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.result.ResultExternal;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MVSortedTempResult
/*     */   extends MVTempResult
/*     */ {
/*     */   private final boolean distinct;
/*     */   private final int[] distinctIndexes;
/*     */   private final int[] indexes;
/*     */   private final MVMap<ValueRow, Long> map;
/*     */   private MVMap<ValueRow, Value> index;
/*     */   private ValueDataType orderedDistinctOnType;
/*     */   private Cursor<ValueRow, Long> cursor;
/*     */   private Value[] current;
/*     */   private long valueCount;
/*     */   
/*     */   private MVSortedTempResult(MVSortedTempResult paramMVSortedTempResult) {
/*  95 */     super(paramMVSortedTempResult);
/*  96 */     this.distinct = paramMVSortedTempResult.distinct;
/*  97 */     this.distinctIndexes = paramMVSortedTempResult.distinctIndexes;
/*  98 */     this.indexes = paramMVSortedTempResult.indexes;
/*  99 */     this.map = paramMVSortedTempResult.map;
/* 100 */     this.rowCount = paramMVSortedTempResult.rowCount;
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
/*     */   MVSortedTempResult(Database paramDatabase, Expression[] paramArrayOfExpression, boolean paramBoolean, int[] paramArrayOfint, int paramInt1, int paramInt2, SortOrder paramSortOrder) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: aload_2
/*     */     //   3: iload #5
/*     */     //   5: iload #6
/*     */     //   7: invokespecial <init> : (Lorg/h2/engine/Database;[Lorg/h2/expression/Expression;II)V
/*     */     //   10: aload_0
/*     */     //   11: iload_3
/*     */     //   12: putfield distinct : Z
/*     */     //   15: aload_0
/*     */     //   16: aload #4
/*     */     //   18: putfield distinctIndexes : [I
/*     */     //   21: iload #6
/*     */     //   23: newarray int
/*     */     //   25: astore #8
/*     */     //   27: aload #7
/*     */     //   29: ifnull -> 207
/*     */     //   32: iload #6
/*     */     //   34: newarray int
/*     */     //   36: astore #9
/*     */     //   38: aload #7
/*     */     //   40: invokevirtual getQueryColumnIndexes : ()[I
/*     */     //   43: astore #10
/*     */     //   45: aload #10
/*     */     //   47: arraylength
/*     */     //   48: istore #11
/*     */     //   50: new java/util/BitSet
/*     */     //   53: dup
/*     */     //   54: invokespecial <init> : ()V
/*     */     //   57: astore #12
/*     */     //   59: iconst_0
/*     */     //   60: istore #13
/*     */     //   62: iload #13
/*     */     //   64: iload #11
/*     */     //   66: if_icmpge -> 133
/*     */     //   69: aload #10
/*     */     //   71: iload #13
/*     */     //   73: iaload
/*     */     //   74: istore #14
/*     */     //   76: getstatic org/h2/mvstore/db/MVSortedTempResult.$assertionsDisabled : Z
/*     */     //   79: ifne -> 100
/*     */     //   82: aload #12
/*     */     //   84: iload #14
/*     */     //   86: invokevirtual get : (I)Z
/*     */     //   89: ifeq -> 100
/*     */     //   92: new java/lang/AssertionError
/*     */     //   95: dup
/*     */     //   96: invokespecial <init> : ()V
/*     */     //   99: athrow
/*     */     //   100: aload #12
/*     */     //   102: iload #14
/*     */     //   104: invokevirtual set : (I)V
/*     */     //   107: aload #9
/*     */     //   109: iload #13
/*     */     //   111: iload #14
/*     */     //   113: iastore
/*     */     //   114: aload #8
/*     */     //   116: iload #13
/*     */     //   118: aload #7
/*     */     //   120: invokevirtual getSortTypes : ()[I
/*     */     //   123: iload #13
/*     */     //   125: iaload
/*     */     //   126: iastore
/*     */     //   127: iinc #13, 1
/*     */     //   130: goto -> 62
/*     */     //   133: iconst_0
/*     */     //   134: istore #13
/*     */     //   136: iload #11
/*     */     //   138: istore #14
/*     */     //   140: iload #14
/*     */     //   142: iload #6
/*     */     //   144: if_icmpge -> 172
/*     */     //   147: aload #12
/*     */     //   149: iload #13
/*     */     //   151: invokevirtual nextClearBit : (I)I
/*     */     //   154: istore #13
/*     */     //   156: aload #9
/*     */     //   158: iload #14
/*     */     //   160: iload #13
/*     */     //   162: iastore
/*     */     //   163: iinc #13, 1
/*     */     //   166: iinc #14, 1
/*     */     //   169: goto -> 140
/*     */     //   172: iconst_0
/*     */     //   173: istore #14
/*     */     //   175: iload #14
/*     */     //   177: iload #6
/*     */     //   179: if_icmpge -> 201
/*     */     //   182: aload #9
/*     */     //   184: iload #14
/*     */     //   186: iaload
/*     */     //   187: iload #14
/*     */     //   189: if_icmpeq -> 195
/*     */     //   192: goto -> 204
/*     */     //   195: iinc #14, 1
/*     */     //   198: goto -> 175
/*     */     //   201: aconst_null
/*     */     //   202: astore #9
/*     */     //   204: goto -> 210
/*     */     //   207: aconst_null
/*     */     //   208: astore #9
/*     */     //   210: aload_0
/*     */     //   211: aload #9
/*     */     //   213: putfield indexes : [I
/*     */     //   216: new org/h2/mvstore/db/ValueDataType
/*     */     //   219: dup
/*     */     //   220: aload_1
/*     */     //   221: aload_1
/*     */     //   222: aload #8
/*     */     //   224: invokestatic addNullOrdering : (Lorg/h2/engine/Database;[I)[I
/*     */     //   227: invokespecial <init> : (Lorg/h2/engine/Database;[I)V
/*     */     //   230: astore #10
/*     */     //   232: aload #9
/*     */     //   234: ifnull -> 304
/*     */     //   237: aload #9
/*     */     //   239: arraylength
/*     */     //   240: istore #11
/*     */     //   242: iload #11
/*     */     //   244: anewarray org/h2/value/TypeInfo
/*     */     //   247: astore #12
/*     */     //   249: iconst_0
/*     */     //   250: istore #13
/*     */     //   252: iload #13
/*     */     //   254: iload #11
/*     */     //   256: if_icmpge -> 280
/*     */     //   259: aload #12
/*     */     //   261: iload #13
/*     */     //   263: aload_2
/*     */     //   264: aload #9
/*     */     //   266: iload #13
/*     */     //   268: iaload
/*     */     //   269: aaload
/*     */     //   270: invokevirtual getType : ()Lorg/h2/value/TypeInfo;
/*     */     //   273: aastore
/*     */     //   274: iinc #13, 1
/*     */     //   277: goto -> 252
/*     */     //   280: aload #10
/*     */     //   282: getstatic org/h2/result/RowFactory$DefaultRowFactory.INSTANCE : Lorg/h2/result/RowFactory$DefaultRowFactory;
/*     */     //   285: aload_1
/*     */     //   286: aload_1
/*     */     //   287: invokevirtual getCompareMode : ()Lorg/h2/value/CompareMode;
/*     */     //   290: aload_1
/*     */     //   291: aload #12
/*     */     //   293: aconst_null
/*     */     //   294: iconst_0
/*     */     //   295: invokevirtual createRowFactory : (Lorg/h2/engine/CastDataProvider;Lorg/h2/value/CompareMode;Lorg/h2/store/DataHandler;[Lorg/h2/value/Typed;[Lorg/h2/table/IndexColumn;Z)Lorg/h2/result/RowFactory;
/*     */     //   298: invokevirtual setRowFactory : (Lorg/h2/result/RowFactory;)V
/*     */     //   301: goto -> 324
/*     */     //   304: aload #10
/*     */     //   306: getstatic org/h2/result/RowFactory$DefaultRowFactory.INSTANCE : Lorg/h2/result/RowFactory$DefaultRowFactory;
/*     */     //   309: aload_1
/*     */     //   310: aload_1
/*     */     //   311: invokevirtual getCompareMode : ()Lorg/h2/value/CompareMode;
/*     */     //   314: aload_1
/*     */     //   315: aload_2
/*     */     //   316: aconst_null
/*     */     //   317: iconst_0
/*     */     //   318: invokevirtual createRowFactory : (Lorg/h2/engine/CastDataProvider;Lorg/h2/value/CompareMode;Lorg/h2/store/DataHandler;[Lorg/h2/value/Typed;[Lorg/h2/table/IndexColumn;Z)Lorg/h2/result/RowFactory;
/*     */     //   321: invokevirtual setRowFactory : (Lorg/h2/result/RowFactory;)V
/*     */     //   324: new org/h2/mvstore/MVMap$Builder
/*     */     //   327: dup
/*     */     //   328: invokespecial <init> : ()V
/*     */     //   331: aload #10
/*     */     //   333: invokevirtual keyType : (Lorg/h2/mvstore/type/DataType;)Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   336: getstatic org/h2/mvstore/type/LongDataType.INSTANCE : Lorg/h2/mvstore/type/LongDataType;
/*     */     //   339: invokevirtual valueType : (Lorg/h2/mvstore/type/DataType;)Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   342: astore #11
/*     */     //   344: aload_0
/*     */     //   345: aload_0
/*     */     //   346: getfield store : Lorg/h2/mvstore/MVStore;
/*     */     //   349: ldc 'tmp'
/*     */     //   351: aload #11
/*     */     //   353: invokevirtual openMap : (Ljava/lang/String;Lorg/h2/mvstore/MVMap$MapBuilder;)Lorg/h2/mvstore/MVMap;
/*     */     //   356: putfield map : Lorg/h2/mvstore/MVMap;
/*     */     //   359: iload_3
/*     */     //   360: ifeq -> 370
/*     */     //   363: iload #6
/*     */     //   365: iload #5
/*     */     //   367: if_icmpne -> 375
/*     */     //   370: aload #4
/*     */     //   372: ifnull -> 561
/*     */     //   375: aload #4
/*     */     //   377: ifnull -> 426
/*     */     //   380: aload #4
/*     */     //   382: arraylength
/*     */     //   383: istore #12
/*     */     //   385: iload #12
/*     */     //   387: anewarray org/h2/value/TypeInfo
/*     */     //   390: astore #13
/*     */     //   392: iconst_0
/*     */     //   393: istore #14
/*     */     //   395: iload #14
/*     */     //   397: iload #12
/*     */     //   399: if_icmpge -> 423
/*     */     //   402: aload #13
/*     */     //   404: iload #14
/*     */     //   406: aload_2
/*     */     //   407: aload #4
/*     */     //   409: iload #14
/*     */     //   411: iaload
/*     */     //   412: aaload
/*     */     //   413: invokevirtual getType : ()Lorg/h2/value/TypeInfo;
/*     */     //   416: aastore
/*     */     //   417: iinc #14, 1
/*     */     //   420: goto -> 395
/*     */     //   423: goto -> 465
/*     */     //   426: iload #5
/*     */     //   428: istore #12
/*     */     //   430: iload #12
/*     */     //   432: anewarray org/h2/value/TypeInfo
/*     */     //   435: astore #13
/*     */     //   437: iconst_0
/*     */     //   438: istore #14
/*     */     //   440: iload #14
/*     */     //   442: iload #12
/*     */     //   444: if_icmpge -> 465
/*     */     //   447: aload #13
/*     */     //   449: iload #14
/*     */     //   451: aload_2
/*     */     //   452: iload #14
/*     */     //   454: aaload
/*     */     //   455: invokevirtual getType : ()Lorg/h2/value/TypeInfo;
/*     */     //   458: aastore
/*     */     //   459: iinc #14, 1
/*     */     //   462: goto -> 440
/*     */     //   465: new org/h2/mvstore/db/ValueDataType
/*     */     //   468: dup
/*     */     //   469: aload_1
/*     */     //   470: iload #12
/*     */     //   472: newarray int
/*     */     //   474: invokespecial <init> : (Lorg/h2/engine/Database;[I)V
/*     */     //   477: astore #14
/*     */     //   479: aload #14
/*     */     //   481: getstatic org/h2/result/RowFactory$DefaultRowFactory.INSTANCE : Lorg/h2/result/RowFactory$DefaultRowFactory;
/*     */     //   484: aload_1
/*     */     //   485: aload_1
/*     */     //   486: invokevirtual getCompareMode : ()Lorg/h2/value/CompareMode;
/*     */     //   489: aload_1
/*     */     //   490: aload #13
/*     */     //   492: aconst_null
/*     */     //   493: iconst_0
/*     */     //   494: invokevirtual createRowFactory : (Lorg/h2/engine/CastDataProvider;Lorg/h2/value/CompareMode;Lorg/h2/store/DataHandler;[Lorg/h2/value/Typed;[Lorg/h2/table/IndexColumn;Z)Lorg/h2/result/RowFactory;
/*     */     //   497: invokevirtual setRowFactory : (Lorg/h2/result/RowFactory;)V
/*     */     //   500: aload #4
/*     */     //   502: ifnull -> 522
/*     */     //   505: aload #7
/*     */     //   507: ifnull -> 522
/*     */     //   510: aload_0
/*     */     //   511: aload #10
/*     */     //   513: dup_x1
/*     */     //   514: putfield orderedDistinctOnType : Lorg/h2/mvstore/db/ValueDataType;
/*     */     //   517: astore #15
/*     */     //   519: goto -> 527
/*     */     //   522: getstatic org/h2/mvstore/db/NullValueDataType.INSTANCE : Lorg/h2/mvstore/db/NullValueDataType;
/*     */     //   525: astore #15
/*     */     //   527: new org/h2/mvstore/MVMap$Builder
/*     */     //   530: dup
/*     */     //   531: invokespecial <init> : ()V
/*     */     //   534: aload #14
/*     */     //   536: invokevirtual keyType : (Lorg/h2/mvstore/type/DataType;)Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   539: aload #15
/*     */     //   541: invokevirtual valueType : (Lorg/h2/mvstore/type/DataType;)Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   544: astore #16
/*     */     //   546: aload_0
/*     */     //   547: aload_0
/*     */     //   548: getfield store : Lorg/h2/mvstore/MVStore;
/*     */     //   551: ldc 'idx'
/*     */     //   553: aload #16
/*     */     //   555: invokevirtual openMap : (Ljava/lang/String;Lorg/h2/mvstore/MVMap$MapBuilder;)Lorg/h2/mvstore/MVMap;
/*     */     //   558: putfield index : Lorg/h2/mvstore/MVMap;
/*     */     //   561: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #125	-> 0
/*     */     //   #126	-> 10
/*     */     //   #127	-> 15
/*     */     //   #128	-> 21
/*     */     //   #130	-> 27
/*     */     //   #135	-> 32
/*     */     //   #136	-> 38
/*     */     //   #137	-> 45
/*     */     //   #139	-> 50
/*     */     //   #140	-> 59
/*     */     //   #141	-> 69
/*     */     //   #142	-> 76
/*     */     //   #143	-> 100
/*     */     //   #144	-> 107
/*     */     //   #145	-> 114
/*     */     //   #140	-> 127
/*     */     //   #152	-> 133
/*     */     //   #153	-> 136
/*     */     //   #154	-> 147
/*     */     //   #155	-> 156
/*     */     //   #156	-> 163
/*     */     //   #153	-> 166
/*     */     //   #164	-> 172
/*     */     //   #165	-> 182
/*     */     //   #167	-> 192
/*     */     //   #164	-> 195
/*     */     //   #174	-> 201
/*     */     //   #176	-> 204
/*     */     //   #178	-> 207
/*     */     //   #180	-> 210
/*     */     //   #181	-> 216
/*     */     //   #182	-> 232
/*     */     //   #183	-> 237
/*     */     //   #184	-> 242
/*     */     //   #185	-> 249
/*     */     //   #186	-> 259
/*     */     //   #185	-> 274
/*     */     //   #188	-> 280
/*     */     //   #190	-> 301
/*     */     //   #191	-> 304
/*     */     //   #194	-> 324
/*     */     //   #195	-> 339
/*     */     //   #196	-> 344
/*     */     //   #197	-> 359
/*     */     //   #200	-> 375
/*     */     //   #201	-> 380
/*     */     //   #202	-> 385
/*     */     //   #203	-> 392
/*     */     //   #204	-> 402
/*     */     //   #203	-> 417
/*     */     //   #207	-> 426
/*     */     //   #208	-> 430
/*     */     //   #209	-> 437
/*     */     //   #210	-> 447
/*     */     //   #209	-> 459
/*     */     //   #213	-> 465
/*     */     //   #214	-> 479
/*     */     //   #217	-> 500
/*     */     //   #218	-> 510
/*     */     //   #220	-> 522
/*     */     //   #222	-> 527
/*     */     //   #223	-> 541
/*     */     //   #224	-> 546
/*     */     //   #226	-> 561
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
/*     */   public int addRow(Value[] paramArrayOfValue) {
/* 230 */     assert this.parent == null;
/* 231 */     ValueRow valueRow = getKey(paramArrayOfValue);
/* 232 */     if (this.distinct || this.distinctIndexes != null) {
/* 233 */       if (this.distinctIndexes != null) {
/* 234 */         int i = this.distinctIndexes.length;
/* 235 */         Value[] arrayOfValue = new Value[i];
/* 236 */         for (byte b = 0; b < i; b++) {
/* 237 */           arrayOfValue[b] = paramArrayOfValue[this.distinctIndexes[b]];
/*     */         }
/* 239 */         ValueRow valueRow1 = ValueRow.get(arrayOfValue);
/* 240 */         if (this.orderedDistinctOnType == null) {
/* 241 */           if (this.index.putIfAbsent(valueRow1, ValueNull.INSTANCE) != null) {
/* 242 */             return this.rowCount;
/*     */           }
/*     */         } else {
/* 245 */           ValueRow valueRow2 = (ValueRow)this.index.get(valueRow1);
/* 246 */           if (valueRow2 == null) {
/* 247 */             this.index.put(valueRow1, valueRow);
/* 248 */           } else if (this.orderedDistinctOnType.compare((Value)valueRow2, (Value)valueRow) > 0) {
/* 249 */             this.map.remove(valueRow2);
/* 250 */             this.rowCount--;
/* 251 */             this.index.put(valueRow1, valueRow);
/*     */           } else {
/* 253 */             return this.rowCount;
/*     */           } 
/*     */         } 
/* 256 */       } else if (this.visibleColumnCount != this.resultColumnCount) {
/* 257 */         ValueRow valueRow1 = ValueRow.get(Arrays.<Value>copyOf(paramArrayOfValue, this.visibleColumnCount));
/* 258 */         if (this.index.putIfAbsent(valueRow1, ValueNull.INSTANCE) != null) {
/* 259 */           return this.rowCount;
/*     */         }
/*     */       } 
/*     */       
/* 263 */       if (this.map.putIfAbsent(valueRow, Long.valueOf(1L)) == null) {
/* 264 */         this.rowCount++;
/*     */       }
/*     */     } else {
/*     */       
/* 268 */       Long long_ = (Long)this.map.putIfAbsent(valueRow, Long.valueOf(1L));
/* 269 */       if (long_ != null)
/*     */       {
/* 271 */         this.map.put(valueRow, Long.valueOf(long_.longValue() + 1L));
/*     */       }
/* 273 */       this.rowCount++;
/*     */     } 
/* 275 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Value[] paramArrayOfValue) {
/* 281 */     if (this.parent != null) {
/* 282 */       return this.parent.contains(paramArrayOfValue);
/*     */     }
/* 284 */     assert this.distinct;
/* 285 */     if (this.visibleColumnCount != this.resultColumnCount) {
/* 286 */       return this.index.containsKey(ValueRow.get(paramArrayOfValue));
/*     */     }
/* 288 */     return this.map.containsKey(getKey(paramArrayOfValue));
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized ResultExternal createShallowCopy() {
/* 293 */     if (this.parent != null) {
/* 294 */       return this.parent.createShallowCopy();
/*     */     }
/* 296 */     if (this.closed) {
/* 297 */       return null;
/*     */     }
/* 299 */     this.childCount++;
/* 300 */     return new MVSortedTempResult(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ValueRow getKey(Value[] paramArrayOfValue) {
/* 311 */     if (this.indexes != null) {
/* 312 */       Value[] arrayOfValue = new Value[this.indexes.length];
/* 313 */       for (byte b = 0; b < this.indexes.length; b++) {
/* 314 */         arrayOfValue[b] = paramArrayOfValue[this.indexes[b]];
/*     */       }
/* 316 */       paramArrayOfValue = arrayOfValue;
/*     */     } 
/* 318 */     return ValueRow.get(paramArrayOfValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Value[] getValue(Value[] paramArrayOfValue) {
/* 329 */     if (this.indexes != null) {
/* 330 */       Value[] arrayOfValue = new Value[this.indexes.length];
/* 331 */       for (byte b = 0; b < this.indexes.length; b++) {
/* 332 */         arrayOfValue[this.indexes[b]] = paramArrayOfValue[b];
/*     */       }
/* 334 */       paramArrayOfValue = arrayOfValue;
/*     */     } 
/* 336 */     return paramArrayOfValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] next() {
/* 341 */     if (this.cursor == null) {
/* 342 */       this.cursor = this.map.cursor(null);
/* 343 */       this.current = null;
/* 344 */       this.valueCount = 0L;
/*     */     } 
/*     */     
/* 347 */     if (--this.valueCount > 0L)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 352 */       return this.current;
/*     */     }
/* 354 */     if (!this.cursor.hasNext()) {
/*     */       
/* 356 */       this.current = null;
/* 357 */       return null;
/*     */     } 
/*     */     
/* 360 */     this.current = getValue(((ValueRow)this.cursor.next()).getList());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 365 */     this.valueCount = ((Long)this.cursor.getValue()).longValue();
/* 366 */     return this.current;
/*     */   }
/*     */ 
/*     */   
/*     */   public int removeRow(Value[] paramArrayOfValue) {
/* 371 */     assert this.parent == null && this.distinct;
/* 372 */     if (this.visibleColumnCount != this.resultColumnCount) {
/* 373 */       throw DbException.getUnsupportedException("removeRow()");
/*     */     }
/*     */     
/* 376 */     if (this.map.remove(getKey(paramArrayOfValue)) != null) {
/* 377 */       this.rowCount--;
/*     */     }
/* 379 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 384 */     this.cursor = null;
/* 385 */     this.current = null;
/* 386 */     this.valueCount = 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVSortedTempResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */