/*    */ package org.apache.http.message;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.http.ProtocolVersion;
/*    */ import org.apache.http.RequestLine;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.CharArrayBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class BasicRequestLine
/*    */   implements RequestLine, Cloneable, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 2810581718468737193L;
/*    */   private final ProtocolVersion protoversion;
/*    */   private final String method;
/*    */   private final String uri;
/*    */   
/*    */   public BasicRequestLine(String method, String uri, ProtocolVersion version) {
/* 56 */     this.method = (String)Args.notNull(method, "Method");
/* 57 */     this.uri = (String)Args.notNull(uri, "URI");
/* 58 */     this.protoversion = (ProtocolVersion)Args.notNull(version, "Version");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 63 */     return this.method;
/*    */   }
/*    */ 
/*    */   
/*    */   public ProtocolVersion getProtocolVersion() {
/* 68 */     return this.protoversion;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUri() {
/* 73 */     return this.uri;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     return BasicLineFormatter.INSTANCE.formatRequestLine((CharArrayBuffer)null, this).toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object clone() throws CloneNotSupportedException {
/* 84 */     return super.clone();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicRequestLine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */