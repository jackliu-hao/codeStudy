/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.FileStore;
/*     */ import org.h2.store.FileStoreOutputStream;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.store.RangeReader;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.lob.LobData;
/*     */ import org.h2.value.lob.LobDataDatabase;
/*     */ import org.h2.value.lob.LobDataFetchOnDemand;
/*     */ import org.h2.value.lob.LobDataFile;
/*     */ import org.h2.value.lob.LobDataInMemory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueClob
/*     */   extends ValueLob
/*     */ {
/*     */   public static ValueClob createSmall(byte[] paramArrayOfbyte) {
/*  46 */     return new ValueClob((LobData)new LobDataInMemory(paramArrayOfbyte), paramArrayOfbyte.length, (new String(paramArrayOfbyte, StandardCharsets.UTF_8))
/*  47 */         .length());
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
/*     */   public static ValueClob createSmall(byte[] paramArrayOfbyte, long paramLong) {
/*  61 */     return new ValueClob((LobData)new LobDataInMemory(paramArrayOfbyte), paramArrayOfbyte.length, paramLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueClob createSmall(String paramString) {
/*  72 */     byte[] arrayOfByte = paramString.getBytes(StandardCharsets.UTF_8);
/*  73 */     return new ValueClob((LobData)new LobDataInMemory(arrayOfByte), arrayOfByte.length, paramString.length());
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
/*     */   public static ValueClob createTempClob(Reader paramReader, long paramLong, DataHandler paramDataHandler) {
/*     */     RangeReader rangeReader;
/*     */     BufferedReader bufferedReader;
/*  88 */     if (paramLong >= 0L) {
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  93 */         rangeReader = new RangeReader(paramReader, 0L, paramLong);
/*  94 */       } catch (IOException iOException) {
/*  95 */         throw DbException.convert(iOException);
/*     */       } 
/*     */     }
/*     */     
/*  99 */     if (rangeReader instanceof BufferedReader) {
/* 100 */       bufferedReader = (BufferedReader)rangeReader;
/*     */     } else {
/* 102 */       bufferedReader = new BufferedReader((Reader)rangeReader, 4096);
/*     */     }  try {
/*     */       char[] arrayOfChar;
/* 105 */       long l = Long.MAX_VALUE;
/* 106 */       if (paramLong >= 0L && paramLong < l) {
/* 107 */         l = paramLong;
/*     */       }
/* 109 */       int i = ValueLob.getBufferSize(paramDataHandler, l);
/*     */       
/* 111 */       if (i >= Integer.MAX_VALUE) {
/* 112 */         String str = IOUtils.readStringAndClose(bufferedReader, -1);
/* 113 */         arrayOfChar = str.toCharArray();
/* 114 */         i = arrayOfChar.length;
/*     */       } else {
/* 116 */         arrayOfChar = new char[i];
/* 117 */         bufferedReader.mark(i);
/* 118 */         i = IOUtils.readFully(bufferedReader, arrayOfChar, i);
/*     */       } 
/* 120 */       if (i <= paramDataHandler.getMaxLengthInplaceLob()) {
/* 121 */         return createSmall(new String(arrayOfChar, 0, i));
/*     */       }
/* 123 */       bufferedReader.reset();
/* 124 */       return createTemporary(paramDataHandler, bufferedReader, l);
/* 125 */     } catch (IOException iOException) {
/* 126 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ValueClob createTemporary(DataHandler paramDataHandler, Reader paramReader, long paramLong) throws IOException {
/* 134 */     String str = ValueLob.createTempLobFileName(paramDataHandler);
/* 135 */     FileStore fileStore = paramDataHandler.openFile(str, "rw", false);
/* 136 */     fileStore.autoDelete();
/*     */     
/* 138 */     long l1 = 0L, l2 = 0L;
/* 139 */     try (FileStoreOutputStream null = new FileStoreOutputStream(fileStore, null)) {
/* 140 */       char[] arrayOfChar = new char[4096];
/*     */       while (true) {
/* 142 */         int i = ValueLob.getBufferSize(paramDataHandler, paramLong);
/* 143 */         i = IOUtils.readFully(paramReader, arrayOfChar, i);
/* 144 */         if (i == 0) {
/*     */           break;
/*     */         }
/*     */         
/* 148 */         byte[] arrayOfByte = (new String(arrayOfChar, 0, i)).getBytes(StandardCharsets.UTF_8);
/* 149 */         fileStoreOutputStream.write(arrayOfByte);
/* 150 */         l1 += arrayOfByte.length;
/* 151 */         l2 += i;
/*     */       } 
/*     */     } 
/* 154 */     return new ValueClob((LobData)new LobDataFile(paramDataHandler, str, fileStore), l1, l2);
/*     */   }
/*     */   
/*     */   public ValueClob(LobData paramLobData, long paramLong1, long paramLong2) {
/* 158 */     super(paramLobData, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 163 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 168 */     if (this.charLength > 1048576L) {
/* 169 */       throw getStringTooLong(this.charLength);
/*     */     }
/* 171 */     if (this.lobData instanceof LobDataInMemory) {
/* 172 */       return new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8);
/*     */     }
/* 174 */     return readString((int)this.charLength);
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] getBytesInternal() {
/* 179 */     long l = this.octetLength;
/* 180 */     if (l >= 0L) {
/* 181 */       if (l > 1048576L) {
/* 182 */         throw getBinaryTooLong(l);
/*     */       }
/* 184 */       return readBytes((int)l);
/*     */     } 
/* 186 */     if (this.octetLength > 1048576L) {
/* 187 */       throw getBinaryTooLong(octetLength());
/*     */     }
/* 189 */     byte[] arrayOfByte = readBytes(2147483647);
/* 190 */     this.octetLength = l = arrayOfByte.length;
/* 191 */     if (l > 1048576L) {
/* 192 */       throw getBinaryTooLong(l);
/*     */     }
/* 194 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() {
/* 199 */     return this.lobData.getInputStream(-1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(long paramLong1, long paramLong2) {
/* 204 */     return rangeInputStream(this.lobData.getInputStream(-1L), paramLong1, paramLong2, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(long paramLong1, long paramLong2) {
/* 209 */     return rangeReader(getReader(), paramLong1, paramLong2, this.charLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 214 */     if (paramValue == this) {
/* 215 */       return 0;
/*     */     }
/* 217 */     ValueClob valueClob = (ValueClob)paramValue;
/* 218 */     LobData lobData1 = this.lobData, lobData2 = valueClob.lobData;
/* 219 */     if (lobData1.getClass() == lobData2.getClass()) {
/* 220 */       if (lobData1 instanceof LobDataInMemory)
/* 221 */         return Integer.signum(getString().compareTo(valueClob.getString())); 
/* 222 */       if (lobData1 instanceof LobDataDatabase) {
/* 223 */         if (((LobDataDatabase)lobData1).getLobId() == ((LobDataDatabase)lobData2).getLobId()) {
/* 224 */           return 0;
/*     */         }
/* 226 */       } else if (lobData1 instanceof LobDataFetchOnDemand && (
/* 227 */         (LobDataFetchOnDemand)lobData1).getLobId() == ((LobDataFetchOnDemand)lobData2).getLobId()) {
/* 228 */         return 0;
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     return compare(this, valueClob);
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
/*     */   private static int compare(ValueClob paramValueClob1, ValueClob paramValueClob2) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield charLength : J
/*     */     //   4: aload_1
/*     */     //   5: getfield charLength : J
/*     */     //   8: invokestatic min : (JJ)J
/*     */     //   11: lstore_2
/*     */     //   12: aload_0
/*     */     //   13: invokevirtual getReader : ()Ljava/io/Reader;
/*     */     //   16: astore #4
/*     */     //   18: aconst_null
/*     */     //   19: astore #5
/*     */     //   21: aload_1
/*     */     //   22: invokevirtual getReader : ()Ljava/io/Reader;
/*     */     //   25: astore #6
/*     */     //   27: aconst_null
/*     */     //   28: astore #7
/*     */     //   30: sipush #512
/*     */     //   33: newarray char
/*     */     //   35: astore #8
/*     */     //   37: sipush #512
/*     */     //   40: newarray char
/*     */     //   42: astore #9
/*     */     //   44: lload_2
/*     */     //   45: ldc2_w 512
/*     */     //   48: lcmp
/*     */     //   49: iflt -> 190
/*     */     //   52: aload #4
/*     */     //   54: aload #8
/*     */     //   56: sipush #512
/*     */     //   59: invokestatic readFully : (Ljava/io/Reader;[CI)I
/*     */     //   62: sipush #512
/*     */     //   65: if_icmpne -> 84
/*     */     //   68: aload #6
/*     */     //   70: aload #9
/*     */     //   72: sipush #512
/*     */     //   75: invokestatic readFully : (Ljava/io/Reader;[CI)I
/*     */     //   78: sipush #512
/*     */     //   81: if_icmpeq -> 90
/*     */     //   84: ldc 'Invalid LOB'
/*     */     //   86: invokestatic getUnsupportedException : (Ljava/lang/String;)Lorg/h2/message/DbException;
/*     */     //   89: athrow
/*     */     //   90: aload #8
/*     */     //   92: aload #9
/*     */     //   94: invokestatic compareNotNull : ([C[C)I
/*     */     //   97: istore #10
/*     */     //   99: iload #10
/*     */     //   101: ifeq -> 181
/*     */     //   104: iload #10
/*     */     //   106: istore #11
/*     */     //   108: aload #6
/*     */     //   110: ifnull -> 143
/*     */     //   113: aload #7
/*     */     //   115: ifnull -> 138
/*     */     //   118: aload #6
/*     */     //   120: invokevirtual close : ()V
/*     */     //   123: goto -> 143
/*     */     //   126: astore #12
/*     */     //   128: aload #7
/*     */     //   130: aload #12
/*     */     //   132: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   135: goto -> 143
/*     */     //   138: aload #6
/*     */     //   140: invokevirtual close : ()V
/*     */     //   143: aload #4
/*     */     //   145: ifnull -> 178
/*     */     //   148: aload #5
/*     */     //   150: ifnull -> 173
/*     */     //   153: aload #4
/*     */     //   155: invokevirtual close : ()V
/*     */     //   158: goto -> 178
/*     */     //   161: astore #12
/*     */     //   163: aload #5
/*     */     //   165: aload #12
/*     */     //   167: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   170: goto -> 178
/*     */     //   173: aload #4
/*     */     //   175: invokevirtual close : ()V
/*     */     //   178: iload #11
/*     */     //   180: ireturn
/*     */     //   181: lload_2
/*     */     //   182: ldc2_w 512
/*     */     //   185: lsub
/*     */     //   186: lstore_2
/*     */     //   187: goto -> 44
/*     */     //   190: aload #4
/*     */     //   192: invokevirtual read : ()I
/*     */     //   195: istore #10
/*     */     //   197: aload #6
/*     */     //   199: invokevirtual read : ()I
/*     */     //   202: istore #11
/*     */     //   204: iload #10
/*     */     //   206: ifge -> 294
/*     */     //   209: iload #11
/*     */     //   211: ifge -> 218
/*     */     //   214: iconst_0
/*     */     //   215: goto -> 219
/*     */     //   218: iconst_m1
/*     */     //   219: istore #12
/*     */     //   221: aload #6
/*     */     //   223: ifnull -> 256
/*     */     //   226: aload #7
/*     */     //   228: ifnull -> 251
/*     */     //   231: aload #6
/*     */     //   233: invokevirtual close : ()V
/*     */     //   236: goto -> 256
/*     */     //   239: astore #13
/*     */     //   241: aload #7
/*     */     //   243: aload #13
/*     */     //   245: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   248: goto -> 256
/*     */     //   251: aload #6
/*     */     //   253: invokevirtual close : ()V
/*     */     //   256: aload #4
/*     */     //   258: ifnull -> 291
/*     */     //   261: aload #5
/*     */     //   263: ifnull -> 286
/*     */     //   266: aload #4
/*     */     //   268: invokevirtual close : ()V
/*     */     //   271: goto -> 291
/*     */     //   274: astore #13
/*     */     //   276: aload #5
/*     */     //   278: aload #13
/*     */     //   280: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   283: goto -> 291
/*     */     //   286: aload #4
/*     */     //   288: invokevirtual close : ()V
/*     */     //   291: iload #12
/*     */     //   293: ireturn
/*     */     //   294: iload #11
/*     */     //   296: ifge -> 375
/*     */     //   299: iconst_1
/*     */     //   300: istore #12
/*     */     //   302: aload #6
/*     */     //   304: ifnull -> 337
/*     */     //   307: aload #7
/*     */     //   309: ifnull -> 332
/*     */     //   312: aload #6
/*     */     //   314: invokevirtual close : ()V
/*     */     //   317: goto -> 337
/*     */     //   320: astore #13
/*     */     //   322: aload #7
/*     */     //   324: aload #13
/*     */     //   326: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   329: goto -> 337
/*     */     //   332: aload #6
/*     */     //   334: invokevirtual close : ()V
/*     */     //   337: aload #4
/*     */     //   339: ifnull -> 372
/*     */     //   342: aload #5
/*     */     //   344: ifnull -> 367
/*     */     //   347: aload #4
/*     */     //   349: invokevirtual close : ()V
/*     */     //   352: goto -> 372
/*     */     //   355: astore #13
/*     */     //   357: aload #5
/*     */     //   359: aload #13
/*     */     //   361: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   364: goto -> 372
/*     */     //   367: aload #4
/*     */     //   369: invokevirtual close : ()V
/*     */     //   372: iload #12
/*     */     //   374: ireturn
/*     */     //   375: iload #10
/*     */     //   377: iload #11
/*     */     //   379: if_icmpeq -> 469
/*     */     //   382: iload #10
/*     */     //   384: iload #11
/*     */     //   386: if_icmpge -> 393
/*     */     //   389: iconst_m1
/*     */     //   390: goto -> 394
/*     */     //   393: iconst_1
/*     */     //   394: istore #12
/*     */     //   396: aload #6
/*     */     //   398: ifnull -> 431
/*     */     //   401: aload #7
/*     */     //   403: ifnull -> 426
/*     */     //   406: aload #6
/*     */     //   408: invokevirtual close : ()V
/*     */     //   411: goto -> 431
/*     */     //   414: astore #13
/*     */     //   416: aload #7
/*     */     //   418: aload #13
/*     */     //   420: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   423: goto -> 431
/*     */     //   426: aload #6
/*     */     //   428: invokevirtual close : ()V
/*     */     //   431: aload #4
/*     */     //   433: ifnull -> 466
/*     */     //   436: aload #5
/*     */     //   438: ifnull -> 461
/*     */     //   441: aload #4
/*     */     //   443: invokevirtual close : ()V
/*     */     //   446: goto -> 466
/*     */     //   449: astore #13
/*     */     //   451: aload #5
/*     */     //   453: aload #13
/*     */     //   455: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   458: goto -> 466
/*     */     //   461: aload #4
/*     */     //   463: invokevirtual close : ()V
/*     */     //   466: iload #12
/*     */     //   468: ireturn
/*     */     //   469: goto -> 190
/*     */     //   472: astore #8
/*     */     //   474: aload #8
/*     */     //   476: astore #7
/*     */     //   478: aload #8
/*     */     //   480: athrow
/*     */     //   481: astore #14
/*     */     //   483: aload #6
/*     */     //   485: ifnull -> 518
/*     */     //   488: aload #7
/*     */     //   490: ifnull -> 513
/*     */     //   493: aload #6
/*     */     //   495: invokevirtual close : ()V
/*     */     //   498: goto -> 518
/*     */     //   501: astore #15
/*     */     //   503: aload #7
/*     */     //   505: aload #15
/*     */     //   507: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   510: goto -> 518
/*     */     //   513: aload #6
/*     */     //   515: invokevirtual close : ()V
/*     */     //   518: aload #14
/*     */     //   520: athrow
/*     */     //   521: astore #6
/*     */     //   523: aload #6
/*     */     //   525: astore #5
/*     */     //   527: aload #6
/*     */     //   529: athrow
/*     */     //   530: astore #16
/*     */     //   532: aload #4
/*     */     //   534: ifnull -> 567
/*     */     //   537: aload #5
/*     */     //   539: ifnull -> 562
/*     */     //   542: aload #4
/*     */     //   544: invokevirtual close : ()V
/*     */     //   547: goto -> 567
/*     */     //   550: astore #17
/*     */     //   552: aload #5
/*     */     //   554: aload #17
/*     */     //   556: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   559: goto -> 567
/*     */     //   562: aload #4
/*     */     //   564: invokevirtual close : ()V
/*     */     //   567: aload #16
/*     */     //   569: athrow
/*     */     //   570: astore #4
/*     */     //   572: aload #4
/*     */     //   574: invokestatic convert : (Ljava/lang/Throwable;)Lorg/h2/message/DbException;
/*     */     //   577: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #245	-> 0
/*     */     //   #246	-> 12
/*     */     //   #247	-> 30
/*     */     //   #248	-> 37
/*     */     //   #249	-> 44
/*     */     //   #250	-> 52
/*     */     //   #251	-> 75
/*     */     //   #252	-> 84
/*     */     //   #254	-> 90
/*     */     //   #255	-> 99
/*     */     //   #256	-> 104
/*     */     //   #271	-> 108
/*     */     //   #256	-> 178
/*     */     //   #249	-> 181
/*     */     //   #260	-> 190
/*     */     //   #261	-> 204
/*     */     //   #262	-> 209
/*     */     //   #271	-> 221
/*     */     //   #262	-> 291
/*     */     //   #264	-> 294
/*     */     //   #265	-> 299
/*     */     //   #271	-> 302
/*     */     //   #265	-> 372
/*     */     //   #267	-> 375
/*     */     //   #268	-> 382
/*     */     //   #271	-> 396
/*     */     //   #268	-> 466
/*     */     //   #270	-> 469
/*     */     //   #246	-> 472
/*     */     //   #271	-> 481
/*     */     //   #246	-> 521
/*     */     //   #271	-> 530
/*     */     //   #272	-> 572
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	178	570	java/io/IOException
/*     */     //   21	143	521	java/lang/Throwable
/*     */     //   21	143	530	finally
/*     */     //   30	108	472	java/lang/Throwable
/*     */     //   30	108	481	finally
/*     */     //   118	123	126	java/lang/Throwable
/*     */     //   153	158	161	java/lang/Throwable
/*     */     //   181	221	472	java/lang/Throwable
/*     */     //   181	221	481	finally
/*     */     //   181	256	521	java/lang/Throwable
/*     */     //   181	256	530	finally
/*     */     //   181	291	570	java/io/IOException
/*     */     //   231	236	239	java/lang/Throwable
/*     */     //   266	271	274	java/lang/Throwable
/*     */     //   294	302	472	java/lang/Throwable
/*     */     //   294	302	481	finally
/*     */     //   294	337	521	java/lang/Throwable
/*     */     //   294	337	530	finally
/*     */     //   294	372	570	java/io/IOException
/*     */     //   312	317	320	java/lang/Throwable
/*     */     //   347	352	355	java/lang/Throwable
/*     */     //   375	396	472	java/lang/Throwable
/*     */     //   375	396	481	finally
/*     */     //   375	431	521	java/lang/Throwable
/*     */     //   375	431	530	finally
/*     */     //   375	466	570	java/io/IOException
/*     */     //   406	411	414	java/lang/Throwable
/*     */     //   441	446	449	java/lang/Throwable
/*     */     //   469	472	472	java/lang/Throwable
/*     */     //   469	483	481	finally
/*     */     //   469	521	521	java/lang/Throwable
/*     */     //   469	532	530	finally
/*     */     //   469	570	570	java/io/IOException
/*     */     //   493	498	501	java/lang/Throwable
/*     */     //   542	547	550	java/lang/Throwable
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
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 278 */     if ((paramInt & 0x2) != 0 && (!(this.lobData instanceof LobDataInMemory) || this.charLength > SysProperties.MAX_TRACE_DATA_LENGTH)) {
/*     */       
/* 280 */       paramStringBuilder.append("SPACE(").append(this.charLength);
/* 281 */       LobDataDatabase lobDataDatabase = (LobDataDatabase)this.lobData;
/* 282 */       paramStringBuilder.append(" /* table: ").append(lobDataDatabase.getTableId()).append(" id: ").append(lobDataDatabase.getLobId())
/* 283 */         .append(" */)");
/*     */     }
/* 285 */     else if ((paramInt & 0x6) == 0) {
/* 286 */       StringUtils.quoteStringSQL(paramStringBuilder.append("CAST("), getString()).append(" AS CHARACTER LARGE OBJECT(")
/* 287 */         .append(this.charLength).append("))");
/*     */     } else {
/* 289 */       StringUtils.quoteStringSQL(paramStringBuilder, getString());
/*     */     } 
/*     */     
/* 292 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ValueClob convertPrecision(long paramLong) {
/*     */     ValueClob valueClob;
/* 303 */     if (this.charLength <= paramLong) {
/* 304 */       return this;
/*     */     }
/*     */     
/* 307 */     DataHandler dataHandler = this.lobData.getDataHandler();
/* 308 */     if (dataHandler != null) {
/* 309 */       valueClob = createTempClob(getReader(), paramLong, dataHandler);
/*     */     } else {
/*     */       try {
/* 312 */         valueClob = createSmall(IOUtils.readStringAndClose(getReader(), MathUtils.convertLongToInt(paramLong)));
/* 313 */       } catch (IOException iOException) {
/* 314 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/*     */     } 
/* 317 */     return valueClob;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueLob copy(DataHandler paramDataHandler, int paramInt) {
/* 322 */     if (this.lobData instanceof LobDataInMemory) {
/* 323 */       byte[] arrayOfByte = ((LobDataInMemory)this.lobData).getSmall();
/* 324 */       if (arrayOfByte.length > paramDataHandler.getMaxLengthInplaceLob()) {
/* 325 */         LobStorageInterface lobStorageInterface = paramDataHandler.getLobStorage();
/* 326 */         ValueClob valueClob = lobStorageInterface.createClob(getReader(), this.charLength);
/* 327 */         ValueLob valueLob = valueClob.copy(paramDataHandler, paramInt);
/* 328 */         valueClob.remove();
/* 329 */         return valueLob;
/*     */       } 
/* 331 */       return this;
/* 332 */     }  if (this.lobData instanceof LobDataDatabase) {
/* 333 */       return paramDataHandler.getLobStorage().copyLob(this, paramInt);
/*     */     }
/* 335 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long charLength() {
/* 341 */     return this.charLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public long octetLength() {
/* 346 */     long l = this.octetLength;
/* 347 */     if (l < 0L) {
/* 348 */       if (this.lobData instanceof LobDataInMemory) {
/* 349 */         l = (((LobDataInMemory)this.lobData).getSmall()).length;
/*     */       } else {
/* 351 */         try (InputStream null = getInputStream()) {
/* 352 */           l = 0L;
/*     */           while (true) {
/* 354 */             l += inputStream.skip(Long.MAX_VALUE);
/* 355 */             if (inputStream.read() < 0) {
/*     */               break;
/*     */             }
/* 358 */             l++;
/*     */           } 
/* 360 */         } catch (IOException iOException) {
/* 361 */           throw DbException.convertIOException(iOException, null);
/*     */         } 
/*     */       } 
/* 364 */       this.octetLength = l;
/*     */     } 
/* 366 */     return l;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueClob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */