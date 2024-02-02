/*    */ package org.apache.http.params;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CodingErrorAction;
/*    */ import org.apache.http.config.ConnectionConfig;
/*    */ import org.apache.http.config.MessageConstraints;
/*    */ import org.apache.http.config.SocketConfig;
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
/*    */ 
/*    */ @Deprecated
/*    */ public final class HttpParamConfig
/*    */ {
/*    */   public static SocketConfig getSocketConfig(HttpParams params) {
/* 49 */     return SocketConfig.custom().setSoTimeout(params.getIntParameter("http.socket.timeout", 0)).setSoReuseAddress(params.getBooleanParameter("http.socket.reuseaddr", false)).setSoKeepAlive(params.getBooleanParameter("http.socket.keepalive", false)).setSoLinger(params.getIntParameter("http.socket.linger", -1)).setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true)).build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static MessageConstraints getMessageConstraints(HttpParams params) {
/* 59 */     return MessageConstraints.custom().setMaxHeaderCount(params.getIntParameter("http.connection.max-header-count", -1)).setMaxLineLength(params.getIntParameter("http.connection.max-line-length", -1)).build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ConnectionConfig getConnectionConfig(HttpParams params) {
/* 66 */     MessageConstraints messageConstraints = getMessageConstraints(params);
/* 67 */     String csname = (String)params.getParameter("http.protocol.element-charset");
/* 68 */     return ConnectionConfig.custom().setCharset((csname != null) ? Charset.forName(csname) : null).setMalformedInputAction((CodingErrorAction)params.getParameter("http.malformed.input.action")).setMalformedInputAction((CodingErrorAction)params.getParameter("http.unmappable.input.action")).setMessageConstraints(messageConstraints).build();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpParamConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */