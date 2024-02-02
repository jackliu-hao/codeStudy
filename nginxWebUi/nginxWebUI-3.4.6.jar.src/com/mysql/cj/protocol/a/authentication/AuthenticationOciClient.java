/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.RSAException;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.ExportControlled;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.oracle.bmc.ConfigFileReader;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Paths;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.util.Base64;
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
/*     */ 
/*     */ 
/*     */ public class AuthenticationOciClient
/*     */   implements AuthenticationPlugin<NativePacketPayload>
/*     */ {
/*  59 */   public static String PLUGIN_NAME = "authentication_oci_client";
/*     */   
/*  61 */   private String sourceOfAuthData = PLUGIN_NAME;
/*     */   
/*  63 */   protected Protocol<NativePacketPayload> protocol = null;
/*  64 */   private MysqlCallbackHandler usernameCallbackHandler = null;
/*  65 */   private String fingerprint = null;
/*  66 */   private RSAPrivateKey privateKey = null;
/*     */ 
/*     */   
/*     */   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
/*  70 */     this.protocol = prot;
/*  71 */     this.usernameCallbackHandler = cbh;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  76 */     this.fingerprint = null;
/*  77 */     this.privateKey = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/*  82 */     reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocolPluginName() {
/*  87 */     return PLUGIN_NAME;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean requiresConfidentiality() {
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReusable() {
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/* 102 */     if (user == null && this.usernameCallbackHandler != null)
/*     */     {
/* 104 */       this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(System.getProperty("user.name")));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSourceOfAuthData(String sourceOfAuthData) {
/* 110 */     this.sourceOfAuthData = sourceOfAuthData;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/* 115 */     toServer.clear();
/*     */     
/* 117 */     if (!this.sourceOfAuthData.equals(PLUGIN_NAME) || fromServer.getPayloadLength() == 0) {
/*     */ 
/*     */       
/* 120 */       toServer.add(new NativePacketPayload(0));
/* 121 */       return true;
/*     */     } 
/*     */     
/* 124 */     initializePrivateKey();
/*     */     
/* 126 */     byte[] nonce = fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF);
/* 127 */     byte[] signature = ExportControlled.sign(nonce, this.privateKey);
/* 128 */     if (signature == null) {
/* 129 */       signature = new byte[0];
/*     */     }
/* 131 */     String payload = String.format("{\"fingerprint\":\"%s\", \"signature\":\"%s\"}", new Object[] { this.fingerprint, Base64.getEncoder().encodeToString(signature) });
/* 132 */     toServer.add(new NativePacketPayload(payload.getBytes(Charset.defaultCharset())));
/* 133 */     return true;
/*     */   }
/*     */   private void initializePrivateKey() {
/*     */     ConfigFileReader.ConfigFile configFile;
/* 137 */     if (this.privateKey != null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 144 */       String configFilePath = this.protocol.getPropertySet().getStringProperty(PropertyKey.ociConfigFile.getKeyName()).getStringValue();
/* 145 */       if (StringUtils.isNullOrEmpty(configFilePath)) {
/* 146 */         configFile = ConfigFileReader.parseDefault();
/* 147 */       } else if (Files.exists(Paths.get(configFilePath, new String[0]), new java.nio.file.LinkOption[0])) {
/* 148 */         configFile = ConfigFileReader.parse(configFilePath);
/*     */       } else {
/* 150 */         throw ExceptionFactory.createException("configuration file does not exist");
/*     */       } 
/* 152 */     } catch (NoClassDefFoundError e) {
/* 153 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.SdkNotFound"), e);
/* 154 */     } catch (IOException e) {
/* 155 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileError"), e);
/*     */     } 
/* 157 */     this.fingerprint = configFile.get("fingerprint");
/* 158 */     if (StringUtils.isNullOrEmpty(this.fingerprint)) {
/* 159 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileMissingEntry"));
/*     */     }
/* 161 */     String keyFilePath = configFile.get("key_file");
/* 162 */     if (StringUtils.isNullOrEmpty(keyFilePath)) {
/* 163 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.OciConfigFileMissingEntry"));
/*     */     }
/*     */     
/*     */     try {
/* 167 */       String key = new String(Files.readAllBytes(Paths.get(keyFilePath, new String[0])), Charset.defaultCharset());
/* 168 */       this.privateKey = ExportControlled.decodeRSAPrivateKey(key);
/* 169 */     } catch (IOException e) {
/* 170 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.PrivateKeyNotFound"), e);
/* 171 */     } catch (RSAException|IllegalArgumentException e) {
/* 172 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationOciClientPlugin.PrivateKeyNotValid"), e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\AuthenticationOciClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */