/*    */ package org.noear.solon.boot.prop;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.boot.ServerSignalProps;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpSignalProps
/*    */   implements ServerSignalProps
/*    */ {
/*    */   private String name;
/*    */   private int port;
/*    */   private String host;
/*    */   
/*    */   public String getName() {
/* 18 */     return this.name;
/*    */   }
/*    */   
/*    */   public int getPort() {
/* 22 */     return this.port;
/*    */   }
/*    */   
/*    */   public String getHost() {
/* 26 */     return this.host;
/*    */   }
/*    */   
/*    */   public HttpSignalProps() {
/* 30 */     this.name = Solon.cfg().get("server.http.name");
/* 31 */     this.port = Solon.cfg().getInt("server.http.port", 0);
/* 32 */     this.host = Solon.cfg().get("server.http.host");
/*    */     
/* 34 */     if (this.port < 1) {
/* 35 */       this.port = Solon.cfg().serverPort();
/*    */     }
/*    */     
/* 38 */     if (Utils.isEmpty(this.host))
/* 39 */       this.host = Solon.cfg().serverHost(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\prop\HttpSignalProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */