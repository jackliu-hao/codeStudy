/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractAttachable
/*     */   implements Attachable
/*     */ {
/*     */   private Map<AttachmentKey<?>, Object> attachments;
/*     */   
/*     */   public <T> T getAttachment(AttachmentKey<T> key) {
/*  42 */     if (key == null || this.attachments == null) {
/*  43 */       return null;
/*     */     }
/*  45 */     return (T)this.attachments.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
/*  53 */     if (key == null || this.attachments == null) {
/*  54 */       return Collections.emptyList();
/*     */     }
/*  56 */     List<T> list = (List<T>)this.attachments.get(key);
/*  57 */     if (list == null) {
/*  58 */       return Collections.emptyList();
/*     */     }
/*  60 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T putAttachment(AttachmentKey<T> key, T value) {
/*  68 */     if (key == null) {
/*  69 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("key");
/*     */     }
/*  71 */     if (this.attachments == null) {
/*  72 */       this.attachments = createAttachmentMap();
/*     */     }
/*  74 */     return (T)this.attachments.put(key, value);
/*     */   }
/*     */   
/*     */   protected Map<AttachmentKey<?>, Object> createAttachmentMap() {
/*  78 */     return new IdentityHashMap<>(5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T removeAttachment(AttachmentKey<T> key) {
/*  86 */     if (key == null || this.attachments == null) {
/*  87 */       return null;
/*     */     }
/*  89 */     return (T)this.attachments.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
/*  97 */     if (key != null) {
/*  98 */       if (this.attachments == null) {
/*  99 */         this.attachments = createAttachmentMap();
/*     */       }
/* 101 */       Map<AttachmentKey<?>, Object> attachments = this.attachments;
/* 102 */       AttachmentList<T> list = (AttachmentList<T>)attachments.get(key);
/* 103 */       if (list == null) {
/* 104 */         AttachmentList<T> newList = new AttachmentList<>(((ListAttachmentKey)key).getValueClass());
/* 105 */         attachments.put(key, newList);
/* 106 */         newList.add(value);
/*     */       } else {
/* 108 */         list.add(value);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\AbstractAttachable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */