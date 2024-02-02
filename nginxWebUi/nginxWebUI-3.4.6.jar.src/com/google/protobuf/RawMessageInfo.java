/*     */ package com.google.protobuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class RawMessageInfo
/*     */   implements MessageInfo
/*     */ {
/*     */   private final MessageLite defaultInstance;
/*     */   private final String info;
/*     */   private final Object[] objects;
/*     */   private final int flags;
/*     */   
/*     */   RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
/* 180 */     this.defaultInstance = defaultInstance;
/* 181 */     this.info = info;
/* 182 */     this.objects = objects;
/* 183 */     int position = 0;
/* 184 */     int value = info.charAt(position++);
/* 185 */     if (value < 55296) {
/* 186 */       this.flags = value;
/*     */     } else {
/* 188 */       int result = value & 0x1FFF;
/* 189 */       int shift = 13;
/* 190 */       while ((value = info.charAt(position++)) >= 55296) {
/* 191 */         result |= (value & 0x1FFF) << shift;
/* 192 */         shift += 13;
/*     */       } 
/* 194 */       this.flags = result | value << shift;
/*     */     } 
/*     */   }
/*     */   
/*     */   String getStringInfo() {
/* 199 */     return this.info;
/*     */   }
/*     */   
/*     */   Object[] getObjects() {
/* 203 */     return this.objects;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageLite getDefaultInstance() {
/* 208 */     return this.defaultInstance;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtoSyntax getSyntax() {
/* 213 */     return ((this.flags & 0x1) == 1) ? ProtoSyntax.PROTO2 : ProtoSyntax.PROTO3;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMessageSetWireFormat() {
/* 218 */     return ((this.flags & 0x2) == 2);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\RawMessageInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */