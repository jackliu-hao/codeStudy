/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.util.Collection;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.result.ResultExternal;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.TempFileDeleter;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MVTempResult
/*     */   implements ResultExternal
/*     */ {
/*     */   private final Database database;
/*     */   final MVStore store;
/*     */   final Expression[] expressions;
/*     */   final int visibleColumnCount;
/*     */   final int resultColumnCount;
/*     */   int rowCount;
/*     */   final MVTempResult parent;
/*     */   int childCount;
/*     */   boolean closed;
/*     */   private final TempFileDeleter tempFileDeleter;
/*     */   private final CloseImpl closeable;
/*     */   private final Reference<?> fileRef;
/*     */   
/*     */   private static final class CloseImpl
/*     */     implements AutoCloseable
/*     */   {
/*     */     private final MVStore store;
/*     */     private final String fileName;
/*     */     
/*     */     CloseImpl(MVStore param1MVStore, String param1String) {
/*  48 */       this.store = param1MVStore;
/*  49 */       this.fileName = param1String;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws Exception {
/*  54 */       this.store.closeImmediately();
/*  55 */       FileUtils.tryDelete(this.fileName);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ResultExternal of(Database paramDatabase, Expression[] paramArrayOfExpression, boolean paramBoolean, int[] paramArrayOfint, int paramInt1, int paramInt2, SortOrder paramSortOrder) {
/*  82 */     return (paramBoolean || paramArrayOfint != null || paramSortOrder != null) ? new MVSortedTempResult(paramDatabase, paramArrayOfExpression, paramBoolean, paramArrayOfint, paramInt1, paramInt2, paramSortOrder) : new MVPlainTempResult(paramDatabase, paramArrayOfExpression, paramInt1, paramInt2);
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
/*     */   MVTempResult(MVTempResult paramMVTempResult) {
/* 152 */     this.parent = paramMVTempResult;
/* 153 */     this.database = paramMVTempResult.database;
/* 154 */     this.store = paramMVTempResult.store;
/* 155 */     this.expressions = paramMVTempResult.expressions;
/* 156 */     this.visibleColumnCount = paramMVTempResult.visibleColumnCount;
/* 157 */     this.resultColumnCount = paramMVTempResult.resultColumnCount;
/* 158 */     this.tempFileDeleter = null;
/* 159 */     this.closeable = null;
/* 160 */     this.fileRef = null;
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
/*     */   MVTempResult(Database paramDatabase, Expression[] paramArrayOfExpression, int paramInt1, int paramInt2) {
/* 176 */     this.database = paramDatabase;
/*     */     try {
/* 178 */       String str = FileUtils.createTempFile("h2tmp", ".temp.db", true);
/* 179 */       MVStore.Builder builder = (new MVStore.Builder()).fileName(str).cacheSize(0).autoCommitDisabled();
/* 180 */       byte[] arrayOfByte = paramDatabase.getFileEncryptionKey();
/* 181 */       if (arrayOfByte != null) {
/* 182 */         builder.encryptionKey(Store.decodePassword(arrayOfByte));
/*     */       }
/* 184 */       this.store = builder.open();
/* 185 */       this.expressions = paramArrayOfExpression;
/* 186 */       this.visibleColumnCount = paramInt1;
/* 187 */       this.resultColumnCount = paramInt2;
/* 188 */       this.tempFileDeleter = paramDatabase.getTempFileDeleter();
/* 189 */       this.closeable = new CloseImpl(this.store, str);
/* 190 */       this.fileRef = this.tempFileDeleter.addFile(this.closeable, this);
/* 191 */     } catch (IOException iOException) {
/* 192 */       throw DbException.convert(iOException);
/*     */     } 
/* 194 */     this.parent = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int addRows(Collection<Value[]> paramCollection) {
/* 199 */     for (Value[] arrayOfValue : paramCollection) {
/* 200 */       addRow(arrayOfValue);
/*     */     }
/* 202 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void close() {
/* 207 */     if (this.closed) {
/*     */       return;
/*     */     }
/* 210 */     this.closed = true;
/* 211 */     if (this.parent != null) {
/* 212 */       this.parent.closeChild();
/*     */     }
/* 214 */     else if (this.childCount == 0) {
/* 215 */       delete();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized void closeChild() {
/* 221 */     if (--this.childCount == 0 && this.closed) {
/* 222 */       delete();
/*     */     }
/*     */   }
/*     */   
/*     */   private void delete() {
/* 227 */     this.tempFileDeleter.deleteFile(this.fileRef, this.closeable);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVTempResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */