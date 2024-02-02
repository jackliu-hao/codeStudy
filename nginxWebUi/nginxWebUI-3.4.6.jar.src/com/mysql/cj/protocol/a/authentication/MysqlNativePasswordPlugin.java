/*    */ package com.mysql.cj.protocol.a.authentication;
/*    */ 
/*    */ import com.mysql.cj.callback.MysqlCallback;
/*    */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*    */ import com.mysql.cj.callback.UsernameCallback;
/*    */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.Protocol;
/*    */ import com.mysql.cj.protocol.Security;
/*    */ import com.mysql.cj.protocol.a.NativeConstants;
/*    */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*    */ import java.util.List;
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
/*    */ public class MysqlNativePasswordPlugin
/*    */   implements AuthenticationPlugin<NativePacketPayload>
/*    */ {
/* 46 */   public static String PLUGIN_NAME = "mysql_native_password";
/*    */   
/* 48 */   private Protocol<NativePacketPayload> protocol = null;
/* 49 */   private MysqlCallbackHandler usernameCallbackHandler = null;
/* 50 */   private String password = null;
/*    */ 
/*    */   
/*    */   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
/* 54 */     this.protocol = prot;
/* 55 */     this.usernameCallbackHandler = cbh;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 59 */     this.password = null;
/*    */   }
/*    */   
/*    */   public String getProtocolPluginName() {
/* 63 */     return PLUGIN_NAME;
/*    */   }
/*    */   
/*    */   public boolean requiresConfidentiality() {
/* 67 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isReusable() {
/* 71 */     return true;
/*    */   }
/*    */   
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 75 */     this.password = password;
/* 76 */     if (user == null && this.usernameCallbackHandler != null)
/*    */     {
/* 78 */       this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name")));
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/* 83 */     toServer.clear();
/*    */     
/* 85 */     NativePacketPayload bresp = null;
/*    */     
/* 87 */     String pwd = this.password;
/*    */     
/* 89 */     if (fromServer == null || pwd == null || pwd.length() == 0) {
/* 90 */       bresp = new NativePacketPayload(new byte[0]);
/*    */     } else {
/* 92 */       bresp = new NativePacketPayload(Security.scramble411(pwd, fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_TERM), this.protocol
/* 93 */             .getServerSession().getCharsetSettings().getPasswordCharacterEncoding()));
/*    */     } 
/* 95 */     toServer.add(bresp);
/*    */     
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\MysqlNativePasswordPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */