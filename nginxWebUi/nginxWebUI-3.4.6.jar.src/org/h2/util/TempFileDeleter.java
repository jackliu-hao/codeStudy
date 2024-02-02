/*     */ package org.h2.util;
/*     */ 
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TempFileDeleter
/*     */ {
/*  23 */   private final ReferenceQueue<Object> queue = new ReferenceQueue();
/*  24 */   private final HashMap<PhantomReference<?>, Object> refMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TempFileDeleter getInstance() {
/*  31 */     return new TempFileDeleter();
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
/*     */   public synchronized Reference<?> addFile(Object paramObject1, Object paramObject2) {
/*  43 */     if (!(paramObject1 instanceof String) && !(paramObject1 instanceof AutoCloseable)) {
/*  44 */       throw DbException.getUnsupportedException("Unsupported resource " + paramObject1);
/*     */     }
/*  46 */     IOUtils.trace("TempFileDeleter.addFile", (paramObject1 instanceof String) ? (String)paramObject1 : "-", paramObject2);
/*     */     
/*  48 */     PhantomReference<?> phantomReference = new PhantomReference(paramObject2, this.queue);
/*  49 */     this.refMap.put(phantomReference, paramObject1);
/*  50 */     deleteUnused();
/*  51 */     return phantomReference;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void deleteFile(Reference<?> paramReference, Object paramObject) {
/*  62 */     if (paramReference != null) {
/*  63 */       Object object = this.refMap.remove(paramReference);
/*  64 */       if (object != null) {
/*  65 */         if (SysProperties.CHECK && 
/*  66 */           paramObject != null && !object.equals(paramObject)) {
/*  67 */           throw DbException.getInternalError("f2:" + object + " f:" + paramObject);
/*     */         }
/*     */         
/*  70 */         paramObject = object;
/*     */       } 
/*     */     } 
/*  73 */     if (paramObject instanceof String) {
/*  74 */       String str = (String)paramObject;
/*  75 */       if (FileUtils.exists(str)) {
/*     */         try {
/*  77 */           IOUtils.trace("TempFileDeleter.deleteFile", str, null);
/*  78 */           FileUtils.tryDelete(str);
/*  79 */         } catch (Exception exception) {}
/*     */       
/*     */       }
/*     */     }
/*  83 */     else if (paramObject instanceof AutoCloseable) {
/*  84 */       AutoCloseable autoCloseable = (AutoCloseable)paramObject;
/*     */       try {
/*  86 */         IOUtils.trace("TempFileDeleter.deleteCloseable", "-", null);
/*  87 */         autoCloseable.close();
/*  88 */       } catch (Exception exception) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteAll() {
/*  98 */     for (Object object : new ArrayList(this.refMap.values())) {
/*  99 */       deleteFile(null, object);
/*     */     }
/* 101 */     deleteUnused();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteUnused() {
/* 108 */     while (this.queue != null) {
/* 109 */       Reference<?> reference = this.queue.poll();
/* 110 */       if (reference == null) {
/*     */         break;
/*     */       }
/* 113 */       deleteFile(reference, null);
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
/*     */   public void stopAutoDelete(Reference<?> paramReference, Object paramObject) {
/* 125 */     IOUtils.trace("TempFileDeleter.stopAutoDelete", (paramObject instanceof String) ? (String)paramObject : "-", paramReference);
/*     */     
/* 127 */     if (paramReference != null) {
/* 128 */       Object object = this.refMap.remove(paramReference);
/* 129 */       if (SysProperties.CHECK && (
/* 130 */         object == null || !object.equals(paramObject))) {
/* 131 */         throw DbException.getInternalError("f2:" + object + ' ' + ((object == null) ? "" : object) + " f:" + paramObject);
/*     */       }
/*     */     } 
/*     */     
/* 135 */     deleteUnused();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\TempFileDeleter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */