/*     */ package org.h2.value;
/*     */ 
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
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public final class ValueBlob
/*     */   extends ValueLob
/*     */ {
/*     */   public static ValueBlob createSmall(byte[] paramArrayOfbyte) {
/*  45 */     return new ValueBlob((LobData)new LobDataInMemory(paramArrayOfbyte), paramArrayOfbyte.length);
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
/*     */   public static ValueBlob createTempBlob(InputStream paramInputStream, long paramLong, DataHandler paramDataHandler) {
/*     */     try {
/*     */       byte[] arrayOfByte;
/*  61 */       long l = Long.MAX_VALUE;
/*  62 */       if (paramLong >= 0L && paramLong < l) {
/*  63 */         l = paramLong;
/*     */       }
/*  65 */       int i = ValueLob.getBufferSize(paramDataHandler, l);
/*     */       
/*  67 */       if (i >= Integer.MAX_VALUE) {
/*  68 */         arrayOfByte = IOUtils.readBytesAndClose(paramInputStream, -1);
/*  69 */         i = arrayOfByte.length;
/*     */       } else {
/*  71 */         arrayOfByte = Utils.newBytes(i);
/*  72 */         i = IOUtils.readFully(paramInputStream, arrayOfByte, i);
/*     */       } 
/*  74 */       if (i <= paramDataHandler.getMaxLengthInplaceLob()) {
/*  75 */         return createSmall(Utils.copyBytes(arrayOfByte, i));
/*     */       }
/*  77 */       return createTemporary(paramDataHandler, arrayOfByte, i, paramInputStream, l);
/*  78 */     } catch (IOException iOException) {
/*  79 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ValueBlob createTemporary(DataHandler paramDataHandler, byte[] paramArrayOfbyte, int paramInt, InputStream paramInputStream, long paramLong) throws IOException {
/*  88 */     String str = ValueLob.createTempLobFileName(paramDataHandler);
/*  89 */     FileStore fileStore = paramDataHandler.openFile(str, "rw", false);
/*  90 */     fileStore.autoDelete();
/*  91 */     long l = 0L;
/*  92 */     try (FileStoreOutputStream null = new FileStoreOutputStream(fileStore, null)) {
/*     */       do {
/*  94 */         l += paramInt;
/*  95 */         fileStoreOutputStream.write(paramArrayOfbyte, 0, paramInt);
/*  96 */         paramLong -= paramInt;
/*  97 */         if (paramLong <= 0L) {
/*     */           break;
/*     */         }
/* 100 */         paramInt = ValueLob.getBufferSize(paramDataHandler, paramLong);
/* 101 */         paramInt = IOUtils.readFully(paramInputStream, paramArrayOfbyte, paramInt);
/* 102 */       } while (paramInt > 0);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 107 */     return new ValueBlob((LobData)new LobDataFile(paramDataHandler, str, fileStore), l);
/*     */   }
/*     */   
/*     */   public ValueBlob(LobData paramLobData, long paramLong) {
/* 111 */     super(paramLobData, paramLong, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 116 */     return 7;
/*     */   }
/*     */   
/*     */   public String getString() {
/*     */     String str;
/* 121 */     long l = this.charLength;
/* 122 */     if (l >= 0L) {
/* 123 */       if (l > 1048576L) {
/* 124 */         throw getStringTooLong(l);
/*     */       }
/* 126 */       return readString((int)l);
/*     */     } 
/*     */     
/* 129 */     if (this.octetLength > 3145728L) {
/* 130 */       throw getStringTooLong(charLength());
/*     */     }
/*     */     
/* 133 */     if (this.lobData instanceof LobDataInMemory) {
/* 134 */       str = new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8);
/*     */     } else {
/* 136 */       str = readString(2147483647);
/*     */     } 
/* 138 */     this.charLength = l = str.length();
/* 139 */     if (l > 1048576L) {
/* 140 */       throw getStringTooLong(l);
/*     */     }
/* 142 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] getBytesInternal() {
/* 147 */     if (this.octetLength > 1048576L) {
/* 148 */       throw getBinaryTooLong(this.octetLength);
/*     */     }
/* 150 */     return readBytes((int)this.octetLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() {
/* 155 */     return this.lobData.getInputStream(this.octetLength);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(long paramLong1, long paramLong2) {
/* 160 */     long l = this.octetLength;
/* 161 */     return rangeInputStream(this.lobData.getInputStream(l), paramLong1, paramLong2, l);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(long paramLong1, long paramLong2) {
/* 166 */     return rangeReader(getReader(), paramLong1, paramLong2, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 171 */     if (paramValue == this) {
/* 172 */       return 0;
/*     */     }
/* 174 */     ValueBlob valueBlob = (ValueBlob)paramValue;
/* 175 */     LobData lobData1 = this.lobData, lobData2 = valueBlob.lobData;
/* 176 */     if (lobData1.getClass() == lobData2.getClass()) {
/* 177 */       if (lobData1 instanceof LobDataInMemory)
/* 178 */         return Bits.compareNotNullUnsigned(((LobDataInMemory)lobData1).getSmall(), ((LobDataInMemory)lobData2)
/* 179 */             .getSmall()); 
/* 180 */       if (lobData1 instanceof LobDataDatabase) {
/* 181 */         if (((LobDataDatabase)lobData1).getLobId() == ((LobDataDatabase)lobData2).getLobId()) {
/* 182 */           return 0;
/*     */         }
/* 184 */       } else if (lobData1 instanceof LobDataFetchOnDemand && (
/* 185 */         (LobDataFetchOnDemand)lobData1).getLobId() == ((LobDataFetchOnDemand)lobData2).getLobId()) {
/* 186 */         return 0;
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     return compare(this, valueBlob);
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
/*     */   private static int compare(ValueBlob paramValueBlob1, ValueBlob paramValueBlob2) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield octetLength : J
/*     */     //   4: aload_1
/*     */     //   5: getfield octetLength : J
/*     */     //   8: invokestatic min : (JJ)J
/*     */     //   11: lstore_2
/*     */     //   12: aload_0
/*     */     //   13: invokevirtual getInputStream : ()Ljava/io/InputStream;
/*     */     //   16: astore #4
/*     */     //   18: aconst_null
/*     */     //   19: astore #5
/*     */     //   21: aload_1
/*     */     //   22: invokevirtual getInputStream : ()Ljava/io/InputStream;
/*     */     //   25: astore #6
/*     */     //   27: aconst_null
/*     */     //   28: astore #7
/*     */     //   30: sipush #512
/*     */     //   33: newarray byte
/*     */     //   35: astore #8
/*     */     //   37: sipush #512
/*     */     //   40: newarray byte
/*     */     //   42: astore #9
/*     */     //   44: lload_2
/*     */     //   45: ldc2_w 512
/*     */     //   48: lcmp
/*     */     //   49: iflt -> 190
/*     */     //   52: aload #4
/*     */     //   54: aload #8
/*     */     //   56: sipush #512
/*     */     //   59: invokestatic readFully : (Ljava/io/InputStream;[BI)I
/*     */     //   62: sipush #512
/*     */     //   65: if_icmpne -> 84
/*     */     //   68: aload #6
/*     */     //   70: aload #9
/*     */     //   72: sipush #512
/*     */     //   75: invokestatic readFully : (Ljava/io/InputStream;[BI)I
/*     */     //   78: sipush #512
/*     */     //   81: if_icmpeq -> 90
/*     */     //   84: ldc 'Invalid LOB'
/*     */     //   86: invokestatic getUnsupportedException : (Ljava/lang/String;)Lorg/h2/message/DbException;
/*     */     //   89: athrow
/*     */     //   90: aload #8
/*     */     //   92: aload #9
/*     */     //   94: invokestatic compareNotNullUnsigned : ([B[B)I
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
/*     */     //   379: if_icmpeq -> 477
/*     */     //   382: iload #10
/*     */     //   384: sipush #255
/*     */     //   387: iand
/*     */     //   388: iload #11
/*     */     //   390: sipush #255
/*     */     //   393: iand
/*     */     //   394: if_icmpge -> 401
/*     */     //   397: iconst_m1
/*     */     //   398: goto -> 402
/*     */     //   401: iconst_1
/*     */     //   402: istore #12
/*     */     //   404: aload #6
/*     */     //   406: ifnull -> 439
/*     */     //   409: aload #7
/*     */     //   411: ifnull -> 434
/*     */     //   414: aload #6
/*     */     //   416: invokevirtual close : ()V
/*     */     //   419: goto -> 439
/*     */     //   422: astore #13
/*     */     //   424: aload #7
/*     */     //   426: aload #13
/*     */     //   428: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   431: goto -> 439
/*     */     //   434: aload #6
/*     */     //   436: invokevirtual close : ()V
/*     */     //   439: aload #4
/*     */     //   441: ifnull -> 474
/*     */     //   444: aload #5
/*     */     //   446: ifnull -> 469
/*     */     //   449: aload #4
/*     */     //   451: invokevirtual close : ()V
/*     */     //   454: goto -> 474
/*     */     //   457: astore #13
/*     */     //   459: aload #5
/*     */     //   461: aload #13
/*     */     //   463: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   466: goto -> 474
/*     */     //   469: aload #4
/*     */     //   471: invokevirtual close : ()V
/*     */     //   474: iload #12
/*     */     //   476: ireturn
/*     */     //   477: goto -> 190
/*     */     //   480: astore #8
/*     */     //   482: aload #8
/*     */     //   484: astore #7
/*     */     //   486: aload #8
/*     */     //   488: athrow
/*     */     //   489: astore #14
/*     */     //   491: aload #6
/*     */     //   493: ifnull -> 526
/*     */     //   496: aload #7
/*     */     //   498: ifnull -> 521
/*     */     //   501: aload #6
/*     */     //   503: invokevirtual close : ()V
/*     */     //   506: goto -> 526
/*     */     //   509: astore #15
/*     */     //   511: aload #7
/*     */     //   513: aload #15
/*     */     //   515: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   518: goto -> 526
/*     */     //   521: aload #6
/*     */     //   523: invokevirtual close : ()V
/*     */     //   526: aload #14
/*     */     //   528: athrow
/*     */     //   529: astore #6
/*     */     //   531: aload #6
/*     */     //   533: astore #5
/*     */     //   535: aload #6
/*     */     //   537: athrow
/*     */     //   538: astore #16
/*     */     //   540: aload #4
/*     */     //   542: ifnull -> 575
/*     */     //   545: aload #5
/*     */     //   547: ifnull -> 570
/*     */     //   550: aload #4
/*     */     //   552: invokevirtual close : ()V
/*     */     //   555: goto -> 575
/*     */     //   558: astore #17
/*     */     //   560: aload #5
/*     */     //   562: aload #17
/*     */     //   564: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
/*     */     //   567: goto -> 575
/*     */     //   570: aload #4
/*     */     //   572: invokevirtual close : ()V
/*     */     //   575: aload #16
/*     */     //   577: athrow
/*     */     //   578: astore #4
/*     */     //   580: aload #4
/*     */     //   582: invokestatic convert : (Ljava/lang/Throwable;)Lorg/h2/message/DbException;
/*     */     //   585: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #203	-> 0
/*     */     //   #204	-> 12
/*     */     //   #205	-> 30
/*     */     //   #206	-> 37
/*     */     //   #207	-> 44
/*     */     //   #208	-> 52
/*     */     //   #209	-> 75
/*     */     //   #210	-> 84
/*     */     //   #212	-> 90
/*     */     //   #213	-> 99
/*     */     //   #214	-> 104
/*     */     //   #229	-> 108
/*     */     //   #214	-> 178
/*     */     //   #207	-> 181
/*     */     //   #218	-> 190
/*     */     //   #219	-> 204
/*     */     //   #220	-> 209
/*     */     //   #229	-> 221
/*     */     //   #220	-> 291
/*     */     //   #222	-> 294
/*     */     //   #223	-> 299
/*     */     //   #229	-> 302
/*     */     //   #223	-> 372
/*     */     //   #225	-> 375
/*     */     //   #226	-> 382
/*     */     //   #229	-> 404
/*     */     //   #226	-> 474
/*     */     //   #228	-> 477
/*     */     //   #204	-> 480
/*     */     //   #229	-> 489
/*     */     //   #204	-> 529
/*     */     //   #229	-> 538
/*     */     //   #230	-> 580
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	178	578	java/io/IOException
/*     */     //   21	143	529	java/lang/Throwable
/*     */     //   21	143	538	finally
/*     */     //   30	108	480	java/lang/Throwable
/*     */     //   30	108	489	finally
/*     */     //   118	123	126	java/lang/Throwable
/*     */     //   153	158	161	java/lang/Throwable
/*     */     //   181	221	480	java/lang/Throwable
/*     */     //   181	221	489	finally
/*     */     //   181	256	529	java/lang/Throwable
/*     */     //   181	256	538	finally
/*     */     //   181	291	578	java/io/IOException
/*     */     //   231	236	239	java/lang/Throwable
/*     */     //   266	271	274	java/lang/Throwable
/*     */     //   294	302	480	java/lang/Throwable
/*     */     //   294	302	489	finally
/*     */     //   294	337	529	java/lang/Throwable
/*     */     //   294	337	538	finally
/*     */     //   294	372	578	java/io/IOException
/*     */     //   312	317	320	java/lang/Throwable
/*     */     //   347	352	355	java/lang/Throwable
/*     */     //   375	404	480	java/lang/Throwable
/*     */     //   375	404	489	finally
/*     */     //   375	439	529	java/lang/Throwable
/*     */     //   375	439	538	finally
/*     */     //   375	474	578	java/io/IOException
/*     */     //   414	419	422	java/lang/Throwable
/*     */     //   449	454	457	java/lang/Throwable
/*     */     //   477	480	480	java/lang/Throwable
/*     */     //   477	491	489	finally
/*     */     //   477	529	529	java/lang/Throwable
/*     */     //   477	540	538	finally
/*     */     //   477	578	578	java/io/IOException
/*     */     //   501	506	509	java/lang/Throwable
/*     */     //   550	555	558	java/lang/Throwable
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
/* 236 */     if ((paramInt & 0x2) != 0 && (!(this.lobData instanceof LobDataInMemory) || this.octetLength > SysProperties.MAX_TRACE_DATA_LENGTH)) {
/*     */       
/* 238 */       paramStringBuilder.append("CAST(REPEAT(CHAR(0), ").append(this.octetLength).append(") AS BINARY VARYING");
/* 239 */       LobDataDatabase lobDataDatabase = (LobDataDatabase)this.lobData;
/* 240 */       paramStringBuilder.append(" /* table: ").append(lobDataDatabase.getTableId()).append(" id: ").append(lobDataDatabase.getLobId())
/* 241 */         .append(" */)");
/*     */     }
/* 243 */     else if ((paramInt & 0x6) == 0) {
/* 244 */       paramStringBuilder.append("CAST(X'");
/* 245 */       StringUtils.convertBytesToHex(paramStringBuilder, getBytesNoCopy()).append("' AS BINARY LARGE OBJECT(")
/* 246 */         .append(this.octetLength).append("))");
/*     */     } else {
/* 248 */       paramStringBuilder.append("X'");
/* 249 */       StringUtils.convertBytesToHex(paramStringBuilder, getBytesNoCopy()).append('\'');
/*     */     } 
/*     */     
/* 252 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ValueBlob convertPrecision(long paramLong) {
/*     */     ValueBlob valueBlob;
/* 263 */     if (this.octetLength <= paramLong) {
/* 264 */       return this;
/*     */     }
/*     */     
/* 267 */     DataHandler dataHandler = this.lobData.getDataHandler();
/* 268 */     if (dataHandler != null) {
/* 269 */       valueBlob = createTempBlob(getInputStream(), paramLong, dataHandler);
/*     */     } else {
/*     */       try {
/* 272 */         valueBlob = createSmall(IOUtils.readBytesAndClose(getInputStream(), MathUtils.convertLongToInt(paramLong)));
/* 273 */       } catch (IOException iOException) {
/* 274 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/*     */     } 
/* 277 */     return valueBlob;
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueLob copy(DataHandler paramDataHandler, int paramInt) {
/* 282 */     if (this.lobData instanceof LobDataInMemory) {
/* 283 */       byte[] arrayOfByte = ((LobDataInMemory)this.lobData).getSmall();
/* 284 */       if (arrayOfByte.length > paramDataHandler.getMaxLengthInplaceLob()) {
/* 285 */         LobStorageInterface lobStorageInterface = paramDataHandler.getLobStorage();
/* 286 */         ValueBlob valueBlob = lobStorageInterface.createBlob(getInputStream(), this.octetLength);
/* 287 */         ValueLob valueLob = valueBlob.copy(paramDataHandler, paramInt);
/* 288 */         valueBlob.remove();
/* 289 */         return valueLob;
/*     */       } 
/* 291 */       return this;
/* 292 */     }  if (this.lobData instanceof LobDataDatabase) {
/* 293 */       return paramDataHandler.getLobStorage().copyLob(this, paramInt);
/*     */     }
/* 295 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long charLength() {
/* 301 */     long l = this.charLength;
/* 302 */     if (l < 0L) {
/* 303 */       if (this.lobData instanceof LobDataInMemory) {
/* 304 */         l = (new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8)).length();
/*     */       } else {
/* 306 */         try (Reader null = getReader()) {
/* 307 */           l = 0L;
/*     */           while (true) {
/* 309 */             l += reader.skip(Long.MAX_VALUE);
/* 310 */             if (reader.read() < 0) {
/*     */               break;
/*     */             }
/* 313 */             l++;
/*     */           } 
/* 315 */         } catch (IOException iOException) {
/* 316 */           throw DbException.convertIOException(iOException, null);
/*     */         } 
/*     */       } 
/* 319 */       this.charLength = l;
/*     */     } 
/* 321 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public long octetLength() {
/* 326 */     return this.octetLength;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBlob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */