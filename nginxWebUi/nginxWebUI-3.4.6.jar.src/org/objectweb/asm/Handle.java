/*     */ package org.objectweb.asm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Handle
/*     */ {
/*     */   private final int tag;
/*     */   private final String owner;
/*     */   private final String name;
/*     */   private final String descriptor;
/*     */   private final boolean isInterface;
/*     */   
/*     */   @Deprecated
/*     */   public Handle(int tag, String owner, String name, String descriptor) {
/*  76 */     this(tag, owner, name, descriptor, (tag == 9));
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
/*     */   public Handle(int tag, String owner, String name, String descriptor, boolean isInterface) {
/*  99 */     this.tag = tag;
/* 100 */     this.owner = owner;
/* 101 */     this.name = name;
/* 102 */     this.descriptor = descriptor;
/* 103 */     this.isInterface = isInterface;
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
/*     */   public int getTag() {
/* 115 */     return this.tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOwner() {
/* 124 */     return this.owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 133 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDesc() {
/* 142 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInterface() {
/* 151 */     return this.isInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 156 */     if (object == this) {
/* 157 */       return true;
/*     */     }
/* 159 */     if (!(object instanceof Handle)) {
/* 160 */       return false;
/*     */     }
/* 162 */     Handle handle = (Handle)object;
/* 163 */     return (this.tag == handle.tag && this.isInterface == handle.isInterface && this.owner
/*     */       
/* 165 */       .equals(handle.owner) && this.name
/* 166 */       .equals(handle.name) && this.descriptor
/* 167 */       .equals(handle.descriptor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 172 */     return this.tag + (
/* 173 */       this.isInterface ? 64 : 0) + this.owner
/* 174 */       .hashCode() * this.name.hashCode() * this.descriptor.hashCode();
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
/*     */   public String toString() {
/* 187 */     return this.owner + '.' + this.name + this.descriptor + " (" + this.tag + (this.isInterface ? " itf" : "") + ')';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Handle.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */