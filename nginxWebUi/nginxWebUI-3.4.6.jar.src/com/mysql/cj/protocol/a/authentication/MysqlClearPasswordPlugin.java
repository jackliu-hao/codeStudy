/*    */ package com.mysql.cj.protocol.a.authentication;
/*    */ 
/*    */ import com.mysql.cj.callback.MysqlCallback;
/*    */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*    */ import com.mysql.cj.callback.UsernameCallback;
/*    */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.Protocol;
/*    */ import com.mysql.cj.protocol.a.NativeConstants;
/*    */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*    */ import com.mysql.cj.util.StringUtils;
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
/*    */ public class MysqlClearPasswordPlugin
/*    */   implements AuthenticationPlugin<NativePacketPayload>
/*    */ {
/* 46 */   public static String PLUGIN_NAME = "mysql_clear_password";
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
/* 67 */     return true;
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
/* 85 */     String encoding = this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding();
/* 86 */     NativePacketPayload bresp = new NativePacketPayload(StringUtils.getBytes((this.password != null) ? this.password : "", encoding));
/*    */     
/* 88 */     bresp.setPosition(bresp.getPayloadLength());
/* 89 */     bresp.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/* 90 */     bresp.setPosition(0);
/*    */     
/* 92 */     toServer.add(bresp);
/* 93 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\MysqlClearPasswordPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */