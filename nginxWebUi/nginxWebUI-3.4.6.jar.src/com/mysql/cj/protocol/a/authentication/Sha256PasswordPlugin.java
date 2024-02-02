/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.ExportControlled;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.Security;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Sha256PasswordPlugin
/*     */   implements AuthenticationPlugin<NativePacketPayload>
/*     */ {
/*  60 */   public static String PLUGIN_NAME = "sha256_password";
/*     */   
/*  62 */   protected Protocol<NativePacketPayload> protocol = null;
/*  63 */   protected MysqlCallbackHandler usernameCallbackHandler = null;
/*  64 */   protected String password = null;
/*  65 */   protected String seed = null;
/*     */   protected boolean publicKeyRequested = false;
/*  67 */   protected String publicKeyString = null;
/*  68 */   protected RuntimeProperty<String> serverRSAPublicKeyFile = null;
/*     */ 
/*     */   
/*     */   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
/*  72 */     this.protocol = prot;
/*  73 */     this.usernameCallbackHandler = cbh;
/*  74 */     this.serverRSAPublicKeyFile = this.protocol.getPropertySet().getStringProperty(PropertyKey.serverRSAPublicKeyFile);
/*     */     
/*  76 */     String pkURL = (String)this.serverRSAPublicKeyFile.getValue();
/*  77 */     if (pkURL != null) {
/*  78 */       this.publicKeyString = readRSAKey(pkURL, this.protocol.getPropertySet(), this.protocol.getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroy() {
/*  83 */     this.password = null;
/*  84 */     this.seed = null;
/*  85 */     this.publicKeyRequested = false;
/*     */   }
/*     */   
/*     */   public String getProtocolPluginName() {
/*  89 */     return PLUGIN_NAME;
/*     */   }
/*     */   
/*     */   public boolean requiresConfidentiality() {
/*  93 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isReusable() {
/*  97 */     return true;
/*     */   }
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/* 101 */     this.password = password;
/* 102 */     if (user == null && this.usernameCallbackHandler != null)
/*     */     {
/* 104 */       this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name")));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/* 109 */     toServer.clear();
/*     */     
/* 111 */     if (this.password == null || this.password.length() == 0 || fromServer == null) {
/*     */       
/* 113 */       NativePacketPayload bresp = new NativePacketPayload(new byte[] { 0 });
/* 114 */       toServer.add(bresp);
/*     */     } else {
/*     */       
/*     */       try {
/* 118 */         if (this.protocol.getSocketConnection().isSSLEstablished()) {
/*     */ 
/*     */           
/* 121 */           NativePacketPayload bresp = new NativePacketPayload(StringUtils.getBytes(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()));
/* 122 */           bresp.setPosition(bresp.getPayloadLength());
/* 123 */           bresp.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/* 124 */           bresp.setPosition(0);
/* 125 */           toServer.add(bresp);
/*     */         }
/* 127 */         else if (this.serverRSAPublicKeyFile.getValue() != null) {
/*     */           
/* 129 */           this.seed = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
/* 130 */           NativePacketPayload bresp = new NativePacketPayload(encryptPassword());
/* 131 */           toServer.add(bresp);
/*     */         } else {
/*     */           
/* 134 */           if (!((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()).booleanValue()) {
/* 135 */             throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("Sha256PasswordPlugin.2"), this.protocol
/* 136 */                 .getExceptionInterceptor());
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 141 */           if (this.publicKeyRequested && fromServer.getPayloadLength() > 21) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 146 */             this.publicKeyString = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
/* 147 */             NativePacketPayload bresp = new NativePacketPayload(encryptPassword());
/* 148 */             toServer.add(bresp);
/* 149 */             this.publicKeyRequested = false;
/*     */           } else {
/*     */             
/* 152 */             this.seed = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
/* 153 */             NativePacketPayload bresp = new NativePacketPayload(new byte[] { 1 });
/* 154 */             toServer.add(bresp);
/* 155 */             this.publicKeyRequested = true;
/*     */           } 
/*     */         } 
/* 158 */       } catch (CJException e) {
/* 159 */         throw ExceptionFactory.createException(e.getMessage(), e, this.protocol.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/* 162 */     return true;
/*     */   }
/*     */   
/*     */   protected byte[] encryptPassword() {
/* 166 */     return encryptPassword("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
/*     */   }
/*     */   
/*     */   protected byte[] encryptPassword(String transformation) {
/* 170 */     byte[] input = null;
/*     */     
/* 172 */     (new byte[1])[0] = 0; input = (this.password != null) ? StringUtils.getBytesNullTerminated(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()) : new byte[1];
/*     */     
/* 174 */     byte[] mysqlScrambleBuff = new byte[input.length];
/* 175 */     Security.xorString(input, mysqlScrambleBuff, this.seed.getBytes(), input.length);
/* 176 */     return ExportControlled.encryptWithRSAPublicKey(mysqlScrambleBuff, ExportControlled.decodeRSAPublicKey(this.publicKeyString), transformation);
/*     */   }
/*     */   
/*     */   protected static String readRSAKey(String pkPath, PropertySet propertySet, ExceptionInterceptor exceptionInterceptor) {
/* 180 */     String res = null;
/* 181 */     byte[] fileBuf = new byte[2048];
/*     */     
/* 183 */     BufferedInputStream fileIn = null;
/*     */     
/*     */     try {
/* 186 */       File f = new File(pkPath);
/* 187 */       String canonicalPath = f.getCanonicalPath();
/* 188 */       fileIn = new BufferedInputStream(new FileInputStream(canonicalPath));
/*     */       
/* 190 */       int bytesRead = 0;
/*     */       
/* 192 */       StringBuilder sb = new StringBuilder();
/* 193 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 194 */         sb.append(StringUtils.toAsciiString(fileBuf, 0, bytesRead));
/*     */       }
/* 196 */       res = sb.toString();
/*     */     }
/* 198 */     catch (IOException ioEx) {
/*     */ 
/*     */ 
/*     */       
/* 202 */       (new Object[1])[0] = ""; (new Object[1])[0] = "'" + pkPath + "'"; throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Sha256PasswordPlugin.0", ((Boolean)propertySet.getBooleanProperty(PropertyKey.paranoid).getValue()).booleanValue() ? new Object[1] : new Object[1]), exceptionInterceptor);
/*     */     }
/*     */     finally {
/*     */       
/* 206 */       if (fileIn != null) {
/*     */         try {
/* 208 */           fileIn.close();
/* 209 */         } catch (IOException e) {
/* 210 */           throw ExceptionFactory.createException(Messages.getString("Sha256PasswordPlugin.1"), e, exceptionInterceptor);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 215 */     return res;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\Sha256PasswordPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */