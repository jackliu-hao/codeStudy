/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InvalidProtocolBufferException
/*     */   extends IOException
/*     */ {
/*     */   private static final long serialVersionUID = -1616151763072450476L;
/*  43 */   private MessageLite unfinishedMessage = null;
/*     */   
/*     */   public InvalidProtocolBufferException(String description) {
/*  46 */     super(description);
/*     */   }
/*     */   
/*     */   public InvalidProtocolBufferException(IOException e) {
/*  50 */     super(e.getMessage(), e);
/*     */   }
/*     */   
/*     */   public InvalidProtocolBufferException(String description, IOException e) {
/*  54 */     super(description, e);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InvalidProtocolBufferException setUnfinishedMessage(MessageLite unfinishedMessage) {
/*  64 */     this.unfinishedMessage = unfinishedMessage;
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageLite getUnfinishedMessage() {
/*  72 */     return this.unfinishedMessage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IOException unwrapIOException() {
/*  80 */     return (getCause() instanceof IOException) ? (IOException)getCause() : this;
/*     */   }
/*     */   
/*     */   static InvalidProtocolBufferException truncatedMessage() {
/*  84 */     return new InvalidProtocolBufferException("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either that the input has been truncated or that an embedded message misreported its own length.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidProtocolBufferException negativeSize() {
/*  92 */     return new InvalidProtocolBufferException("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidProtocolBufferException malformedVarint() {
/*  98 */     return new InvalidProtocolBufferException("CodedInputStream encountered a malformed varint.");
/*     */   }
/*     */   
/*     */   static InvalidProtocolBufferException invalidTag() {
/* 102 */     return new InvalidProtocolBufferException("Protocol message contained an invalid tag (zero).");
/*     */   }
/*     */   
/*     */   static InvalidProtocolBufferException invalidEndTag() {
/* 106 */     return new InvalidProtocolBufferException("Protocol message end-group tag did not match expected tag.");
/*     */   }
/*     */ 
/*     */   
/*     */   static InvalidWireTypeException invalidWireType() {
/* 111 */     return new InvalidWireTypeException("Protocol message tag had invalid wire type.");
/*     */   }
/*     */   
/*     */   public static class InvalidWireTypeException
/*     */     extends InvalidProtocolBufferException
/*     */   {
/*     */     private static final long serialVersionUID = 3283890091615336259L;
/*     */     
/*     */     public InvalidWireTypeException(String description) {
/* 120 */       super(description);
/*     */     }
/*     */   }
/*     */   
/*     */   static InvalidProtocolBufferException recursionLimitExceeded() {
/* 125 */     return new InvalidProtocolBufferException("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidProtocolBufferException sizeLimitExceeded() {
/* 131 */     return new InvalidProtocolBufferException("Protocol message was too large.  May be malicious.  Use CodedInputStream.setSizeLimit() to increase the size limit.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static InvalidProtocolBufferException parseFailure() {
/* 137 */     return new InvalidProtocolBufferException("Failed to parse the message.");
/*     */   }
/*     */   
/*     */   static InvalidProtocolBufferException invalidUtf8() {
/* 141 */     return new InvalidProtocolBufferException("Protocol message had invalid UTF-8.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\InvalidProtocolBufferException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */