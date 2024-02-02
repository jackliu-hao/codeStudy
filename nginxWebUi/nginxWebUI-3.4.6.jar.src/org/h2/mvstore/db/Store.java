/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.h2.command.ddl.CreateTableData;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.FileStore;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.MVStoreException;
/*     */ import org.h2.mvstore.MVStoreTool;
/*     */ import org.h2.mvstore.tx.Transaction;
/*     */ import org.h2.mvstore.tx.TransactionStore;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.store.InDoubtTransaction;
/*     */ import org.h2.store.fs.FileChannelInputStream;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Store
/*     */ {
/*     */   static char[] decodePassword(byte[] paramArrayOfbyte) {
/*  49 */     char[] arrayOfChar = new char[paramArrayOfbyte.length / 2];
/*  50 */     for (byte b = 0; b < arrayOfChar.length; b++) {
/*  51 */       arrayOfChar[b] = (char)((paramArrayOfbyte[b + b] & 0xFF) << 16 | paramArrayOfbyte[b + b + 1] & 0xFF);
/*     */     }
/*  53 */     return arrayOfChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   private final ConcurrentHashMap<String, MVTable> tableMap = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final MVStore mvStore;
/*     */ 
/*     */ 
/*     */   
/*     */   private final TransactionStore transactionStore;
/*     */ 
/*     */ 
/*     */   
/*     */   private long statisticsStart;
/*     */ 
/*     */   
/*     */   private int temporaryMapId;
/*     */ 
/*     */   
/*     */   private final boolean encrypted;
/*     */ 
/*     */   
/*     */   private final String fileName;
/*     */ 
/*     */ 
/*     */   
/*     */   public Store(Database paramDatabase) {
/*  86 */     byte[] arrayOfByte = paramDatabase.getFileEncryptionKey();
/*  87 */     String str = paramDatabase.getDatabasePath();
/*  88 */     MVStore.Builder builder = new MVStore.Builder();
/*  89 */     boolean bool = false;
/*  90 */     if (str != null) {
/*  91 */       String str1 = str + ".mv.db";
/*  92 */       MVStoreTool.compactCleanUp(str1);
/*  93 */       builder.fileName(str1);
/*  94 */       builder.pageSplitSize(paramDatabase.getPageSize());
/*  95 */       if (paramDatabase.isReadOnly()) {
/*  96 */         builder.readOnly();
/*     */       } else {
/*     */         
/*  99 */         boolean bool1 = FileUtils.exists(str1);
/* 100 */         if (!bool1 || FileUtils.canWrite(str1)) {
/*     */ 
/*     */           
/* 103 */           String str2 = FileUtils.getParent(str1);
/* 104 */           FileUtils.createDirectories(str2);
/*     */         } 
/* 106 */         int i = (paramDatabase.getSettings()).autoCompactFillRate;
/* 107 */         if (i <= 100) {
/* 108 */           builder.autoCompactFillRate(i);
/*     */         }
/*     */       } 
/* 111 */       if (arrayOfByte != null) {
/* 112 */         bool = true;
/* 113 */         builder.encryptionKey(decodePassword(arrayOfByte));
/*     */       } 
/* 115 */       if ((paramDatabase.getSettings()).compressData) {
/* 116 */         builder.compress();
/*     */         
/* 118 */         builder.pageSplitSize(65536);
/*     */       } 
/* 120 */       builder.backgroundExceptionHandler((paramThread, paramThrowable) -> paramDatabase.setBackgroundException(DbException.convert(paramThrowable)));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 125 */       builder.autoCommitDisabled();
/*     */     } 
/* 127 */     this.encrypted = bool;
/*     */     try {
/* 129 */       this.mvStore = builder.open();
/* 130 */       FileStore fileStore = this.mvStore.getFileStore();
/* 131 */       this.fileName = (fileStore != null) ? fileStore.getFileName() : null;
/* 132 */       if (!(paramDatabase.getSettings()).reuseSpace) {
/* 133 */         this.mvStore.setReuseSpace(false);
/*     */       }
/* 135 */       this.mvStore.setVersionsToKeep(0);
/* 136 */       this
/*     */         
/* 138 */         .transactionStore = new TransactionStore(this.mvStore, new MetaType(paramDatabase, this.mvStore.backgroundExceptionHandler), (DataType)new ValueDataType(paramDatabase, null), paramDatabase.getLockTimeout());
/* 139 */     } catch (MVStoreException mVStoreException) {
/* 140 */       throw convertMVStoreException(mVStoreException);
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
/*     */   DbException convertMVStoreException(MVStoreException paramMVStoreException) {
/* 152 */     switch (paramMVStoreException.getErrorCode()) {
/*     */       case 4:
/* 154 */         throw DbException.get(90098, paramMVStoreException, new String[] { this.fileName });
/*     */       case 6:
/* 156 */         if (this.encrypted) {
/* 157 */           throw DbException.get(90049, paramMVStoreException, new String[] { this.fileName });
/*     */         }
/* 159 */         throw DbException.get(90030, paramMVStoreException, new String[] { this.fileName });
/*     */       case 7:
/* 161 */         throw DbException.get(90020, paramMVStoreException, new String[] { this.fileName });
/*     */       case 1:
/*     */       case 2:
/* 164 */         throw DbException.get(90028, paramMVStoreException, new String[] { this.fileName });
/*     */     } 
/* 166 */     throw DbException.get(50000, paramMVStoreException, new String[] { paramMVStoreException.getMessage() });
/*     */   }
/*     */ 
/*     */   
/*     */   public MVStore getMvStore() {
/* 171 */     return this.mvStore;
/*     */   }
/*     */   
/*     */   public TransactionStore getTransactionStore() {
/* 175 */     return this.transactionStore;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MVTable getTable(String paramString) {
/* 185 */     return this.tableMap.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MVTable createTable(CreateTableData paramCreateTableData) {
/*     */     try {
/* 196 */       MVTable mVTable = new MVTable(paramCreateTableData, this);
/* 197 */       this.tableMap.put(mVTable.getMapName(), mVTable);
/* 198 */       return mVTable;
/* 199 */     } catch (MVStoreException mVStoreException) {
/* 200 */       throw convertMVStoreException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTable(MVTable paramMVTable) {
/*     */     try {
/* 211 */       this.tableMap.remove(paramMVTable.getMapName());
/* 212 */     } catch (MVStoreException mVStoreException) {
/* 213 */       throw convertMVStoreException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {
/* 221 */     FileStore fileStore = this.mvStore.getFileStore();
/* 222 */     if (fileStore == null || fileStore.isReadOnly()) {
/*     */       return;
/*     */     }
/* 225 */     if (!this.mvStore.compact(50, 4194304)) {
/* 226 */       this.mvStore.commit();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeImmediately() {
/* 234 */     if (!this.mvStore.isClosed()) {
/* 235 */       this.mvStore.closeImmediately();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTemporaryMaps(BitSet paramBitSet) {
/* 245 */     for (String str : this.mvStore.getMapNames()) {
/* 246 */       if (str.startsWith("temp.")) {
/* 247 */         this.mvStore.removeMap(str); continue;
/* 248 */       }  if (str.startsWith("table.") || str.startsWith("index.")) {
/* 249 */         int i = StringUtils.parseUInt31(str, str.indexOf('.') + 1, str.length());
/* 250 */         if (!paramBitSet.get(i)) {
/* 251 */           this.mvStore.removeMap(str);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String nextTemporaryMapName() {
/* 263 */     return "temp." + this.temporaryMapId++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepareCommit(SessionLocal paramSessionLocal, String paramString) {
/* 273 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 274 */     transaction.setName(paramString);
/* 275 */     transaction.prepare();
/* 276 */     this.mvStore.commit();
/*     */   }
/*     */   
/*     */   public ArrayList<InDoubtTransaction> getInDoubtTransactions() {
/* 280 */     List list = this.transactionStore.getOpenTransactions();
/* 281 */     ArrayList<MVInDoubtTransaction> arrayList = Utils.newSmallArrayList();
/* 282 */     for (Transaction transaction : list) {
/* 283 */       if (transaction.getStatus() == 2) {
/* 284 */         arrayList.add(new MVInDoubtTransaction(this.mvStore, transaction));
/*     */       }
/*     */     } 
/* 287 */     return (ArrayList)arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCacheSize(int paramInt) {
/* 296 */     this.mvStore.setCacheSize(Math.max(1, paramInt / 1024));
/*     */   }
/*     */   
/*     */   public InputStream getInputStream() {
/* 300 */     FileChannel fileChannel = this.mvStore.getFileStore().getEncryptedFile();
/* 301 */     if (fileChannel == null) {
/* 302 */       fileChannel = this.mvStore.getFileStore().getFile();
/*     */     }
/* 304 */     return (InputStream)new FileChannelInputStream(fileChannel, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() {
/* 311 */     flush();
/* 312 */     this.mvStore.sync();
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
/*     */   public void compactFile(int paramInt) {
/* 325 */     this.mvStore.compactFile(paramInt);
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
/*     */   public void close(int paramInt) {
/*     */     try {
/* 342 */       FileStore fileStore = this.mvStore.getFileStore();
/* 343 */       if (!this.mvStore.isClosed() && fileStore != null) {
/* 344 */         boolean bool = (paramInt == -1) ? true : false;
/* 345 */         if (fileStore.isReadOnly()) {
/* 346 */           bool = false;
/*     */         } else {
/* 348 */           this.transactionStore.close();
/*     */         } 
/* 350 */         if (bool) {
/* 351 */           paramInt = 0;
/*     */         }
/*     */         
/* 354 */         this.mvStore.close(paramInt);
/*     */         
/* 356 */         String str = fileStore.getFileName();
/* 357 */         if (bool && FileUtils.exists(str))
/*     */         {
/*     */           
/* 360 */           MVStoreTool.compact(str, true);
/*     */         }
/*     */       } 
/* 363 */     } catch (MVStoreException mVStoreException) {
/* 364 */       int i = mVStoreException.getErrorCode();
/* 365 */       if (i != 2)
/*     */       {
/* 367 */         if (i == 6);
/*     */       }
/*     */       
/* 370 */       this.mvStore.closeImmediately();
/* 371 */       throw DbException.get(90028, mVStoreException, new String[] { "Closing" });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void statisticsStart() {
/* 379 */     FileStore fileStore = this.mvStore.getFileStore();
/* 380 */     this.statisticsStart = (fileStore == null) ? 0L : fileStore.getReadCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Integer> statisticsEnd() {
/* 389 */     HashMap<Object, Object> hashMap = new HashMap<>();
/* 390 */     FileStore fileStore = this.mvStore.getFileStore();
/* 391 */     boolean bool = (fileStore == null) ? false : (int)(fileStore.getReadCount() - this.statisticsStart);
/* 392 */     hashMap.put("reads", Integer.valueOf(bool));
/* 393 */     return (Map)hashMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\Store.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */