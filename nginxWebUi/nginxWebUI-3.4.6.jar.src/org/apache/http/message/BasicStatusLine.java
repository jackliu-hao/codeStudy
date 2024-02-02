/*     */ package org.apache.http.message;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicStatusLine
/*     */   implements StatusLine, Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2443303766890459269L;
/*     */   private final ProtocolVersion protoVersion;
/*     */   private final int statusCode;
/*     */   private final String reasonPhrase;
/*     */   
/*     */   public BasicStatusLine(ProtocolVersion version, int statusCode, String reasonPhrase) {
/*  71 */     this.protoVersion = (ProtocolVersion)Args.notNull(version, "Version");
/*  72 */     this.statusCode = Args.notNegative(statusCode, "Status code");
/*  73 */     this.reasonPhrase = reasonPhrase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatusCode() {
/*  80 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/*  85 */     return this.protoVersion;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReasonPhrase() {
/*  90 */     return this.reasonPhrase;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     return BasicLineFormatter.INSTANCE.formatStatusLine((CharArrayBuffer)null, this).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 101 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicStatusLine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */