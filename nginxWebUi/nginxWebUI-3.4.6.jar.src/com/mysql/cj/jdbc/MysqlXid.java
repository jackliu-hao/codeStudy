/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import javax.transaction.xa.Xid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlXid
/*     */   implements Xid
/*     */ {
/*  39 */   int hash = 0;
/*     */   
/*     */   byte[] myBqual;
/*     */   
/*     */   int myFormatId;
/*     */   
/*     */   byte[] myGtrid;
/*     */   
/*     */   public MysqlXid(byte[] gtrid, byte[] bqual, int formatId) {
/*  48 */     this.myGtrid = gtrid;
/*  49 */     this.myBqual = bqual;
/*  50 */     this.myFormatId = formatId;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object another) {
/*  56 */     if (another instanceof Xid) {
/*  57 */       Xid anotherAsXid = (Xid)another;
/*     */       
/*  59 */       if (this.myFormatId != anotherAsXid.getFormatId()) {
/*  60 */         return false;
/*     */       }
/*     */       
/*  63 */       byte[] otherBqual = anotherAsXid.getBranchQualifier();
/*  64 */       byte[] otherGtrid = anotherAsXid.getGlobalTransactionId();
/*     */       
/*  66 */       if (otherGtrid != null && otherGtrid.length == this.myGtrid.length) {
/*  67 */         int length = otherGtrid.length;
/*     */         int i;
/*  69 */         for (i = 0; i < length; i++) {
/*  70 */           if (otherGtrid[i] != this.myGtrid[i]) {
/*  71 */             return false;
/*     */           }
/*     */         } 
/*     */         
/*  75 */         if (otherBqual != null && otherBqual.length == this.myBqual.length) {
/*  76 */           length = otherBqual.length;
/*     */           
/*  78 */           for (i = 0; i < length; i++) {
/*  79 */             if (otherBqual[i] != this.myBqual[i]) {
/*  80 */               return false;
/*     */             }
/*     */           } 
/*     */         } else {
/*  84 */           return false;
/*     */         } 
/*     */         
/*  87 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBranchQualifier() {
/*  96 */     return this.myBqual;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFormatId() {
/* 101 */     return this.myFormatId;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getGlobalTransactionId() {
/* 106 */     return this.myGtrid;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int hashCode() {
/* 111 */     if (this.hash == 0) {
/* 112 */       for (int i = 0; i < this.myGtrid.length; i++) {
/* 113 */         this.hash = 33 * this.hash + this.myGtrid[i];
/*     */       }
/*     */     }
/*     */     
/* 117 */     return this.hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlXid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */