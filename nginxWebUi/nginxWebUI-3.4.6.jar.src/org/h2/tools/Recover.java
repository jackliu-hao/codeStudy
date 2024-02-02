/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.SequenceInputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.h2.engine.MetaRecord;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.MVStoreTool;
/*     */ import org.h2.mvstore.StreamStore;
/*     */ import org.h2.mvstore.db.LobStorageMap;
/*     */ import org.h2.mvstore.db.ValueDataType;
/*     */ import org.h2.mvstore.tx.TransactionMap;
/*     */ import org.h2.mvstore.tx.TransactionStore;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.mvstore.type.StringDataType;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.FileLister;
/*     */ import org.h2.store.FileStore;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.SmallLRUCache;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.TempFileDeleter;
/*     */ import org.h2.util.Tool;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueCollectionBase;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.lob.LobData;
/*     */ import org.h2.value.lob.LobDataDatabase;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Recover
/*     */   extends Tool
/*     */   implements DataHandler
/*     */ {
/*     */   private String databaseName;
/*     */   private int storageId;
/*     */   private String storageName;
/*     */   private int recordLength;
/*     */   private int valueId;
/*     */   private boolean trace;
/*     */   private ArrayList<MetaRecord> schema;
/*     */   private HashSet<Integer> objectIdSet;
/*     */   private HashMap<Integer, String> tableMap;
/*     */   private HashMap<String, String> columnTypeMap;
/*     */   private boolean lobMaps;
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/* 102 */     (new Recover()).runTool(paramVarArgs);
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
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/* 119 */     String str1 = ".";
/* 120 */     String str2 = null;
/* 121 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/* 122 */       String str = paramVarArgs[b];
/* 123 */       if ("-dir".equals(str))
/* 124 */       { str1 = paramVarArgs[++b]; }
/* 125 */       else if ("-db".equals(str))
/* 126 */       { str2 = paramVarArgs[++b]; }
/* 127 */       else if ("-trace".equals(str))
/* 128 */       { this.trace = true; }
/* 129 */       else { if (str.equals("-help") || str.equals("-?")) {
/* 130 */           showUsage();
/*     */           return;
/*     */         } 
/* 133 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/* 136 */     process(str1, str2);
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
/*     */   public static InputStream readBlobMap(Connection paramConnection, long paramLong1, long paramLong2) throws SQLException {
/* 149 */     final PreparedStatement prep = paramConnection.prepareStatement("SELECT DATA FROM INFORMATION_SCHEMA.LOB_BLOCKS WHERE LOB_ID = ? AND SEQ = ? AND ? > 0");
/*     */ 
/*     */     
/* 152 */     preparedStatement.setLong(1, paramLong1);
/*     */ 
/*     */     
/* 155 */     preparedStatement.setLong(3, paramLong2);
/* 156 */     return new SequenceInputStream(new Enumeration<InputStream>()
/*     */         {
/*     */           private int seq;
/*     */           
/* 160 */           private byte[] data = fetch();
/*     */           
/*     */           private byte[] fetch() {
/*     */             try {
/* 164 */               prep.setInt(2, this.seq++);
/* 165 */               ResultSet resultSet = prep.executeQuery();
/* 166 */               if (resultSet.next()) {
/* 167 */                 return resultSet.getBytes(1);
/*     */               }
/* 169 */               return null;
/* 170 */             } catch (SQLException sQLException) {
/* 171 */               throw DbException.convert(sQLException);
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean hasMoreElements() {
/* 177 */             return (this.data != null);
/*     */           }
/*     */ 
/*     */           
/*     */           public InputStream nextElement() {
/* 182 */             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.data);
/* 183 */             this.data = fetch();
/* 184 */             return byteArrayInputStream;
/*     */           }
/*     */         });
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
/*     */   public static Reader readClobMap(Connection paramConnection, long paramLong1, long paramLong2) throws Exception {
/* 200 */     InputStream inputStream = readBlobMap(paramConnection, paramLong1, paramLong2);
/* 201 */     return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
/*     */   }
/*     */   
/*     */   private void trace(String paramString) {
/* 205 */     if (this.trace) {
/* 206 */       this.out.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   private void traceError(String paramString, Throwable paramThrowable) {
/* 211 */     this.out.println(paramString + ": " + paramThrowable.toString());
/* 212 */     if (this.trace) {
/* 213 */       paramThrowable.printStackTrace(this.out);
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
/*     */   public static void execute(String paramString1, String paramString2) throws SQLException {
/*     */     try {
/* 226 */       (new Recover()).process(paramString1, paramString2);
/* 227 */     } catch (DbException dbException) {
/* 228 */       throw DbException.toSQLException(dbException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void process(String paramString1, String paramString2) {
/* 233 */     ArrayList arrayList = FileLister.getDatabaseFiles(paramString1, paramString2, true);
/* 234 */     if (arrayList.isEmpty()) {
/* 235 */       printNoDatabaseFilesFound(paramString1, paramString2);
/*     */     }
/* 237 */     for (String str : arrayList) {
/* 238 */       if (str.endsWith(".mv.db")) {
/* 239 */         String str1 = str.substring(0, str.length() - ".mv.db"
/* 240 */             .length());
/* 241 */         try (PrintWriter null = getWriter(str, ".txt")) {
/* 242 */           MVStoreTool.dump(str, printWriter, true);
/* 243 */           MVStoreTool.info(str, printWriter);
/*     */         } 
/* 245 */         try (PrintWriter null = getWriter(str1 + ".h2.db", ".sql")) {
/* 246 */           dumpMVStoreFile(printWriter, str);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private PrintWriter getWriter(String paramString1, String paramString2) {
/* 253 */     paramString1 = paramString1.substring(0, paramString1.length() - 3);
/* 254 */     String str = paramString1 + paramString2;
/* 255 */     trace("Created file: " + str);
/*     */     try {
/* 257 */       return new PrintWriter(IOUtils.getBufferedWriter(
/* 258 */             FileUtils.newOutputStream(str, false)));
/* 259 */     } catch (IOException iOException) {
/* 260 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getSQL(StringBuilder paramStringBuilder, String paramString, Value paramValue) {
/* 265 */     if (paramValue instanceof ValueLob) {
/* 266 */       ValueLob valueLob = (ValueLob)paramValue;
/* 267 */       LobData lobData = valueLob.getLobData();
/* 268 */       if (lobData instanceof LobDataDatabase) {
/* 269 */         long l2; String str; LobDataDatabase lobDataDatabase = (LobDataDatabase)lobData;
/* 270 */         int i = paramValue.getValueType();
/* 271 */         long l1 = lobDataDatabase.getLobId();
/*     */ 
/*     */         
/* 274 */         if (i == 7) {
/* 275 */           l2 = valueLob.octetLength();
/* 276 */           str = "BLOB";
/* 277 */           paramStringBuilder.append("READ_BLOB");
/*     */         } else {
/* 279 */           l2 = valueLob.charLength();
/* 280 */           str = "CLOB";
/* 281 */           paramStringBuilder.append("READ_CLOB");
/*     */         } 
/* 283 */         if (this.lobMaps) {
/* 284 */           paramStringBuilder.append("_MAP");
/*     */         } else {
/* 286 */           paramStringBuilder.append("_DB");
/*     */         } 
/* 288 */         this.columnTypeMap.put(paramString, str);
/* 289 */         paramStringBuilder.append('(').append(l1).append(", ").append(l2).append(')');
/*     */         return;
/*     */       } 
/*     */     } 
/* 293 */     paramValue.getSQL(paramStringBuilder, 4);
/*     */   }
/*     */   
/*     */   private void setDatabaseName(String paramString) {
/* 297 */     this.databaseName = paramString;
/*     */   }
/*     */   
/*     */   private void dumpMVStoreFile(PrintWriter paramPrintWriter, String paramString) {
/* 301 */     paramPrintWriter.println("-- MVStore");
/* 302 */     String str = getClass().getName();
/* 303 */     paramPrintWriter.println("CREATE ALIAS IF NOT EXISTS READ_BLOB_MAP FOR '" + str + ".readBlobMap';");
/* 304 */     paramPrintWriter.println("CREATE ALIAS IF NOT EXISTS READ_CLOB_MAP FOR '" + str + ".readClobMap';");
/* 305 */     resetSchema();
/* 306 */     setDatabaseName(paramString.substring(0, paramString.length() - ".mv.db"
/* 307 */           .length()));
/*     */     
/* 309 */     try (MVStore null = (new MVStore.Builder()).fileName(paramString).recoveryMode().readOnly().open()) {
/* 310 */       dumpLobMaps(paramPrintWriter, mVStore);
/* 311 */       paramPrintWriter.println("-- Layout");
/* 312 */       dumpLayout(paramPrintWriter, mVStore);
/* 313 */       paramPrintWriter.println("-- Meta");
/* 314 */       dumpMeta(paramPrintWriter, mVStore);
/* 315 */       paramPrintWriter.println("-- Types");
/* 316 */       dumpTypes(paramPrintWriter, mVStore);
/* 317 */       paramPrintWriter.println("-- Tables");
/* 318 */       TransactionStore transactionStore = new TransactionStore(mVStore, (DataType)new ValueDataType());
/*     */       try {
/* 320 */         transactionStore.init();
/* 321 */       } catch (Throwable throwable) {
/* 322 */         writeError(paramPrintWriter, throwable);
/*     */       } 
/*     */ 
/*     */       
/* 326 */       for (String str1 : mVStore.getMapNames()) {
/* 327 */         if (!str1.startsWith("table.")) {
/*     */           continue;
/*     */         }
/* 330 */         String str2 = str1.substring("table.".length());
/* 331 */         if (Integer.parseInt(str2) == 0) {
/* 332 */           TransactionMap transactionMap = transactionStore.begin().openMap(str1);
/* 333 */           Iterator<Long> iterator = transactionMap.keyIterator(null);
/* 334 */           while (iterator.hasNext()) {
/* 335 */             Long long_ = iterator.next();
/* 336 */             Row row = (Row)transactionMap.get(long_);
/*     */             try {
/* 338 */               writeMetaRow(row);
/* 339 */             } catch (Throwable throwable) {
/* 340 */               writeError(paramPrintWriter, throwable);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 347 */       writeSchemaSET(paramPrintWriter);
/* 348 */       paramPrintWriter.println("---- Table Data ----");
/* 349 */       for (String str1 : mVStore.getMapNames()) {
/* 350 */         if (!str1.startsWith("table.")) {
/*     */           continue;
/*     */         }
/* 353 */         String str2 = str1.substring("table.".length());
/* 354 */         if (Integer.parseInt(str2) == 0) {
/*     */           continue;
/*     */         }
/* 357 */         TransactionMap transactionMap = transactionStore.begin().openMap(str1);
/* 358 */         Iterator<Object> iterator = transactionMap.keyIterator(null);
/* 359 */         boolean bool = false;
/* 360 */         while (iterator.hasNext()) {
/* 361 */           Value[] arrayOfValue; Object object = iterator.next();
/* 362 */           Object object1 = transactionMap.get(object);
/*     */           
/* 364 */           if (object1 instanceof Row) {
/* 365 */             arrayOfValue = ((Row)object1).getValueList();
/* 366 */             this.recordLength = arrayOfValue.length;
/*     */           } else {
/* 368 */             arrayOfValue = ((ValueCollectionBase)object1).getList();
/* 369 */             this.recordLength = arrayOfValue.length - 1;
/*     */           } 
/* 371 */           if (!bool) {
/* 372 */             setStorage(Integer.parseInt(str2));
/*     */             
/* 374 */             StringBuilder stringBuilder1 = new StringBuilder();
/* 375 */             for (this.valueId = 0; this.valueId < this.recordLength; this.valueId++) {
/* 376 */               String str3 = this.storageName + "." + this.valueId;
/* 377 */               stringBuilder1.setLength(0);
/* 378 */               getSQL(stringBuilder1, str3, arrayOfValue[this.valueId]);
/*     */             } 
/* 380 */             createTemporaryTable(paramPrintWriter);
/* 381 */             bool = true;
/*     */           } 
/* 383 */           StringBuilder stringBuilder = new StringBuilder();
/* 384 */           stringBuilder.append("INSERT INTO O_").append(str2)
/* 385 */             .append(" VALUES(");
/* 386 */           for (this.valueId = 0; this.valueId < this.recordLength; this.valueId++) {
/* 387 */             if (this.valueId > 0) {
/* 388 */               stringBuilder.append(", ");
/*     */             }
/* 390 */             String str3 = this.storageName + "." + this.valueId;
/* 391 */             getSQL(stringBuilder, str3, arrayOfValue[this.valueId]);
/*     */           } 
/* 393 */           stringBuilder.append(");");
/* 394 */           paramPrintWriter.println(stringBuilder.toString());
/*     */         } 
/*     */       } 
/* 397 */       writeSchema(paramPrintWriter);
/* 398 */       paramPrintWriter.println("DROP ALIAS READ_BLOB_MAP;");
/* 399 */       paramPrintWriter.println("DROP ALIAS READ_CLOB_MAP;");
/* 400 */       paramPrintWriter.println("DROP TABLE IF EXISTS INFORMATION_SCHEMA.LOB_BLOCKS;");
/* 401 */     } catch (Throwable throwable) {
/* 402 */       writeError(paramPrintWriter, throwable);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void dumpLayout(PrintWriter paramPrintWriter, MVStore paramMVStore) {
/* 407 */     MVMap mVMap = paramMVStore.getLayoutMap();
/* 408 */     for (Map.Entry entry : mVMap.entrySet()) {
/* 409 */       paramPrintWriter.println("-- " + (String)entry.getKey() + " = " + (String)entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static void dumpMeta(PrintWriter paramPrintWriter, MVStore paramMVStore) {
/* 414 */     MVMap mVMap = paramMVStore.getMetaMap();
/* 415 */     for (Map.Entry entry : mVMap.entrySet()) {
/* 416 */       paramPrintWriter.println("-- " + (String)entry.getKey() + " = " + (String)entry.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void dumpTypes(PrintWriter paramPrintWriter, MVStore paramMVStore) {
/* 423 */     MVMap.Builder builder = (new MVMap.Builder()).keyType((DataType)StringDataType.INSTANCE).valueType((DataType)new MetaType(null, null));
/* 424 */     MVMap mVMap = paramMVStore.openMap("_", (MVMap.MapBuilder)builder);
/* 425 */     for (Map.Entry entry : mVMap.entrySet()) {
/* 426 */       paramPrintWriter.println("-- " + (String)entry.getKey() + " = " + entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private void dumpLobMaps(PrintWriter paramPrintWriter, MVStore paramMVStore) {
/* 431 */     this.lobMaps = paramMVStore.hasMap("lobData");
/* 432 */     if (!this.lobMaps) {
/*     */       return;
/*     */     }
/* 435 */     TransactionStore transactionStore = new TransactionStore(paramMVStore);
/* 436 */     MVMap mVMap1 = LobStorageMap.openLobDataMap(transactionStore);
/* 437 */     StreamStore streamStore = new StreamStore((Map)mVMap1);
/* 438 */     MVMap mVMap2 = LobStorageMap.openLobMap(transactionStore);
/* 439 */     paramPrintWriter.println("-- LOB");
/* 440 */     paramPrintWriter.println("CREATE TABLE IF NOT EXISTS INFORMATION_SCHEMA.LOB_BLOCKS(LOB_ID BIGINT, SEQ INT, DATA VARBINARY, PRIMARY KEY(LOB_ID, SEQ));");
/*     */ 
/*     */ 
/*     */     
/* 444 */     boolean bool = false;
/* 445 */     label34: for (Map.Entry entry : mVMap2.entrySet()) {
/* 446 */       long l = ((Long)entry.getKey()).longValue();
/* 447 */       LobStorageMap.BlobMeta blobMeta = (LobStorageMap.BlobMeta)entry.getValue();
/* 448 */       byte[] arrayOfByte1 = blobMeta.streamStoreId;
/* 449 */       InputStream inputStream = streamStore.get(arrayOfByte1);
/* 450 */       char c = ' ';
/* 451 */       byte[] arrayOfByte2 = new byte[c];
/*     */       try {
/* 453 */         for (byte b = 0;; b++) {
/* 454 */           int i = IOUtils.readFully(inputStream, arrayOfByte2, arrayOfByte2.length);
/* 455 */           if (i > 0) {
/* 456 */             paramPrintWriter.print("INSERT INTO INFORMATION_SCHEMA.LOB_BLOCKS VALUES(" + l + ", " + b + ", X'");
/*     */             
/* 458 */             paramPrintWriter.print(StringUtils.convertBytesToHex(arrayOfByte2, i));
/* 459 */             paramPrintWriter.println("');");
/*     */           } 
/* 461 */           if (i != c) {
/*     */             continue label34;
/*     */           }
/*     */         } 
/* 465 */       } catch (IOException iOException) {
/* 466 */         writeError(paramPrintWriter, iOException);
/* 467 */         bool = true;
/*     */       } 
/*     */     } 
/* 470 */     paramPrintWriter.println("-- lobMap.size: " + mVMap2.sizeAsLong());
/* 471 */     paramPrintWriter.println("-- lobData.size: " + mVMap1.sizeAsLong());
/*     */     
/* 473 */     if (bool) {
/* 474 */       paramPrintWriter.println("-- lobMap");
/* 475 */       for (Long long_ : mVMap2.keyList()) {
/* 476 */         LobStorageMap.BlobMeta blobMeta = (LobStorageMap.BlobMeta)mVMap2.get(long_);
/* 477 */         byte[] arrayOfByte = blobMeta.streamStoreId;
/* 478 */         paramPrintWriter.println("--     " + long_ + " " + StreamStore.toString(arrayOfByte));
/*     */       } 
/* 480 */       paramPrintWriter.println("-- lobData");
/* 481 */       for (Long long_ : mVMap1.keyList()) {
/* 482 */         paramPrintWriter.println("--     " + long_ + " len " + ((byte[])mVMap1.get(long_)).length);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private String setStorage(int paramInt) {
/* 488 */     this.storageId = paramInt;
/* 489 */     this.storageName = "O_" + Integer.toString(paramInt).replace('-', 'M');
/* 490 */     return this.storageName;
/*     */   }
/*     */   
/*     */   private void writeMetaRow(Row paramRow) {
/* 494 */     MetaRecord metaRecord = new MetaRecord((SearchRow)paramRow);
/* 495 */     int i = metaRecord.getObjectType();
/* 496 */     if (i == 1 && metaRecord.getSQL().startsWith("CREATE PRIMARY KEY ")) {
/*     */       return;
/*     */     }
/* 499 */     this.schema.add(metaRecord);
/* 500 */     if (i == 0) {
/* 501 */       this.tableMap.put(Integer.valueOf(metaRecord.getId()), extractTableOrViewName(metaRecord.getSQL()));
/*     */     }
/*     */   }
/*     */   
/*     */   private void resetSchema() {
/* 506 */     this.schema = new ArrayList<>();
/* 507 */     this.objectIdSet = new HashSet<>();
/* 508 */     this.tableMap = new HashMap<>();
/* 509 */     this.columnTypeMap = new HashMap<>();
/*     */   }
/*     */   
/*     */   private void writeSchemaSET(PrintWriter paramPrintWriter) {
/* 513 */     paramPrintWriter.println("---- Schema SET ----");
/* 514 */     for (MetaRecord metaRecord : this.schema) {
/* 515 */       if (metaRecord.getObjectType() == 6) {
/* 516 */         String str = metaRecord.getSQL();
/* 517 */         paramPrintWriter.println(str + ";");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeSchema(PrintWriter paramPrintWriter) {
/* 523 */     paramPrintWriter.println("---- Schema ----");
/* 524 */     Collections.sort(this.schema);
/* 525 */     for (MetaRecord metaRecord : this.schema) {
/* 526 */       if (metaRecord.getObjectType() != 6 && 
/* 527 */         !isSchemaObjectTypeDelayed(metaRecord)) {
/*     */ 
/*     */         
/* 530 */         String str = metaRecord.getSQL();
/* 531 */         paramPrintWriter.println(str + ";");
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 537 */     boolean bool = false;
/* 538 */     for (Map.Entry<Integer, String> entry : this.tableMap.entrySet()) {
/* 539 */       Integer integer = (Integer)entry.getKey();
/* 540 */       String str = (String)entry.getValue();
/* 541 */       if (this.objectIdSet.contains(integer) && 
/* 542 */         isLobTable(str)) {
/* 543 */         setStorage(integer.intValue());
/* 544 */         paramPrintWriter.println("DELETE FROM " + str + ";");
/* 545 */         paramPrintWriter.println("INSERT INTO " + str + " SELECT * FROM " + this.storageName + ";");
/* 546 */         if (str.equals("INFORMATION_SCHEMA.LOBS") || str
/* 547 */           .equalsIgnoreCase("\"INFORMATION_SCHEMA\".\"LOBS\"")) {
/* 548 */           paramPrintWriter.println("UPDATE " + str + " SET `TABLE` = " + -2 + ";");
/*     */           
/* 550 */           bool = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 555 */     for (Map.Entry<Integer, String> entry : this.tableMap.entrySet()) {
/* 556 */       Integer integer = (Integer)entry.getKey();
/* 557 */       String str = (String)entry.getValue();
/* 558 */       if (this.objectIdSet.contains(integer)) {
/* 559 */         setStorage(integer.intValue());
/* 560 */         if (isLobTable(str)) {
/*     */           continue;
/*     */         }
/* 563 */         paramPrintWriter.println("INSERT INTO " + str + " SELECT * FROM " + this.storageName + ";");
/*     */       } 
/*     */     } 
/* 566 */     for (Integer integer : this.objectIdSet) {
/* 567 */       setStorage(integer.intValue());
/* 568 */       paramPrintWriter.println("DROP TABLE " + this.storageName + ";");
/*     */     } 
/* 570 */     if (bool) {
/* 571 */       paramPrintWriter.println("DELETE FROM INFORMATION_SCHEMA.LOBS WHERE `TABLE` = -2;");
/*     */     }
/*     */     
/* 574 */     ArrayList<String> arrayList = new ArrayList();
/* 575 */     for (MetaRecord metaRecord : this.schema) {
/* 576 */       if (isSchemaObjectTypeDelayed(metaRecord)) {
/* 577 */         String str = metaRecord.getSQL();
/*     */         
/* 579 */         if (metaRecord.getObjectType() == 5 && str.endsWith("NOCHECK") && str
/* 580 */           .contains(" FOREIGN KEY") && str.contains("REFERENCES ")) {
/* 581 */           arrayList.add(str); continue;
/*     */         } 
/* 583 */         paramPrintWriter.println(str + ';');
/*     */       } 
/*     */     } 
/*     */     
/* 587 */     for (String str : arrayList) {
/* 588 */       paramPrintWriter.println(str + ';');
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isLobTable(String paramString) {
/* 593 */     return (paramString.startsWith("INFORMATION_SCHEMA.LOB") || paramString.startsWith("\"INFORMATION_SCHEMA\".\"LOB") || paramString
/* 594 */       .startsWith("\"information_schema\".\"lob"));
/*     */   }
/*     */   
/*     */   private static boolean isSchemaObjectTypeDelayed(MetaRecord paramMetaRecord) {
/* 598 */     switch (paramMetaRecord.getObjectType()) {
/*     */       case 1:
/*     */       case 4:
/*     */       case 5:
/* 602 */         return true;
/*     */     } 
/* 604 */     return false;
/*     */   }
/*     */   
/*     */   private void createTemporaryTable(PrintWriter paramPrintWriter) {
/* 608 */     if (!this.objectIdSet.contains(Integer.valueOf(this.storageId))) {
/* 609 */       this.objectIdSet.add(Integer.valueOf(this.storageId));
/* 610 */       paramPrintWriter.write("CREATE TABLE ");
/* 611 */       paramPrintWriter.write(this.storageName);
/* 612 */       paramPrintWriter.write(40);
/* 613 */       for (byte b = 0; b < this.recordLength; b++) {
/* 614 */         if (b > 0) {
/* 615 */           paramPrintWriter.print(", ");
/*     */         }
/* 617 */         paramPrintWriter.write(67);
/* 618 */         paramPrintWriter.print(b);
/* 619 */         paramPrintWriter.write(32);
/* 620 */         String str = this.columnTypeMap.get(this.storageName + "." + b);
/* 621 */         paramPrintWriter.write((str == null) ? "VARCHAR" : str);
/*     */       } 
/* 623 */       paramPrintWriter.println(");");
/* 624 */       paramPrintWriter.flush();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String extractTableOrViewName(String paramString) {
/* 629 */     int i = paramString.indexOf(" TABLE ");
/* 630 */     int j = paramString.indexOf(" VIEW ");
/* 631 */     if (i > 0 && j > 0) {
/* 632 */       if (i < j) {
/* 633 */         j = -1;
/*     */       } else {
/* 635 */         i = -1;
/*     */       } 
/*     */     }
/* 638 */     if (j > 0) {
/* 639 */       paramString = paramString.substring(j + " VIEW ".length());
/* 640 */     } else if (i > 0) {
/* 641 */       paramString = paramString.substring(i + " TABLE ".length());
/*     */     } else {
/* 643 */       return "UNKNOWN";
/*     */     } 
/* 645 */     if (paramString.startsWith("IF NOT EXISTS ")) {
/* 646 */       paramString = paramString.substring("IF NOT EXISTS ".length());
/*     */     }
/* 648 */     boolean bool = false;
/*     */     
/* 650 */     for (byte b = 0; b < paramString.length(); b++) {
/* 651 */       char c = paramString.charAt(b);
/* 652 */       if (c == '"') {
/* 653 */         bool = !bool ? true : false;
/* 654 */       } else if (!bool && (c <= ' ' || c == '(')) {
/* 655 */         paramString = paramString.substring(0, b);
/* 656 */         return paramString;
/*     */       } 
/*     */     } 
/* 659 */     return "UNKNOWN";
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeError(PrintWriter paramPrintWriter, Throwable paramThrowable) {
/* 664 */     if (paramPrintWriter != null) {
/* 665 */       paramPrintWriter.println("// error: " + paramThrowable);
/*     */     }
/* 667 */     traceError("Error", paramThrowable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabasePath() {
/* 675 */     return this.databaseName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileStore openFile(String paramString1, String paramString2, boolean paramBoolean) {
/* 683 */     return FileStore.open(this, paramString1, "rw");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkPowerOff() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkWritingAllowed() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxLengthInplaceLob() {
/* 707 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getLobSyncObject() {
/* 715 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SmallLRUCache<String, String[]> getLobFileListCache() {
/* 723 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TempFileDeleter getTempFileDeleter() {
/* 731 */     return TempFileDeleter.getInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LobStorageInterface getLobStorage() {
/* 739 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readLob(long paramLong1, byte[] paramArrayOfbyte1, long paramLong2, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
/* 747 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompareMode getCompareMode() {
/* 752 */     return CompareMode.getInstance(null, 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Recover.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */