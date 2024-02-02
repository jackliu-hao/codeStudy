/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.Security;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ 
/*     */ public class CachingSha2PasswordPlugin
/*     */   extends Sha256PasswordPlugin
/*     */ {
/*  50 */   public static String PLUGIN_NAME = "caching_sha2_password";
/*     */   
/*     */   public enum AuthStage {
/*  53 */     FAST_AUTH_SEND_SCRAMBLE, FAST_AUTH_READ_RESULT, FAST_AUTH_COMPLETE, FULL_AUTH;
/*     */   }
/*     */   
/*  56 */   private AuthStage stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
/*     */ 
/*     */   
/*     */   public void init(Protocol<NativePacketPayload> prot) {
/*  60 */     super.init(prot);
/*  61 */     this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  66 */     this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/*  71 */     this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
/*  72 */     super.destroy();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocolPluginName() {
/*  77 */     return PLUGIN_NAME;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/*  82 */     toServer.clear();
/*     */     
/*  84 */     if (this.password == null || this.password.length() == 0 || fromServer == null) {
/*     */       
/*  86 */       NativePacketPayload bresp = new NativePacketPayload(new byte[] { 0 });
/*  87 */       toServer.add(bresp);
/*     */     } else {
/*     */       
/*     */       try {
/*  91 */         if (this.stage == AuthStage.FAST_AUTH_SEND_SCRAMBLE) {
/*     */           
/*  93 */           this.seed = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
/*  94 */           toServer.add(new NativePacketPayload(Security.scrambleCachingSha2(
/*  95 */                   StringUtils.getBytes(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()), this.seed
/*  96 */                   .getBytes())));
/*  97 */           this.stage = AuthStage.FAST_AUTH_READ_RESULT;
/*  98 */           return true;
/*     */         } 
/* 100 */         if (this.stage == AuthStage.FAST_AUTH_READ_RESULT) {
/* 101 */           int fastAuthResult = fromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, 1)[0];
/* 102 */           switch (fastAuthResult) {
/*     */             case 3:
/* 104 */               this.stage = AuthStage.FAST_AUTH_COMPLETE;
/* 105 */               return true;
/*     */             case 4:
/* 107 */               this.stage = AuthStage.FULL_AUTH;
/*     */               break;
/*     */             default:
/* 110 */               throw ExceptionFactory.createException("Unknown server response after fast auth.", this.protocol.getExceptionInterceptor());
/*     */           } 
/*     */         
/*     */         } 
/* 114 */         if (this.protocol.getSocketConnection().isSSLEstablished()) {
/*     */ 
/*     */           
/* 117 */           NativePacketPayload bresp = new NativePacketPayload(StringUtils.getBytes(this.password, this.protocol.getServerSession().getCharsetSettings().getPasswordCharacterEncoding()));
/* 118 */           bresp.setPosition(bresp.getPayloadLength());
/* 119 */           bresp.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/* 120 */           bresp.setPosition(0);
/* 121 */           toServer.add(bresp);
/*     */         }
/* 123 */         else if (this.serverRSAPublicKeyFile.getValue() != null) {
/*     */           
/* 125 */           NativePacketPayload bresp = new NativePacketPayload(encryptPassword());
/* 126 */           toServer.add(bresp);
/*     */         } else {
/*     */           
/* 129 */           if (!((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()).booleanValue()) {
/* 130 */             throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("Sha256PasswordPlugin.2"), this.protocol
/* 131 */                 .getExceptionInterceptor());
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 136 */           if (this.publicKeyRequested && fromServer.getPayloadLength() > 21) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 141 */             this.publicKeyString = fromServer.readString(NativeConstants.StringSelfDataType.STRING_TERM, null);
/* 142 */             NativePacketPayload bresp = new NativePacketPayload(encryptPassword());
/* 143 */             toServer.add(bresp);
/* 144 */             this.publicKeyRequested = false;
/*     */           } else {
/*     */             
/* 147 */             NativePacketPayload bresp = new NativePacketPayload(new byte[] { 2 });
/* 148 */             toServer.add(bresp);
/* 149 */             this.publicKeyRequested = true;
/*     */           } 
/*     */         } 
/* 152 */       } catch (CJException|java.security.DigestException e) {
/* 153 */         throw ExceptionFactory.createException(e.getMessage(), e, this.protocol.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] encryptPassword() {
/* 161 */     if (this.protocol.versionMeetsMinimum(8, 0, 5)) {
/* 162 */       return super.encryptPassword();
/*     */     }
/* 164 */     return encryptPassword("RSA/ECB/PKCS1Padding");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\CachingSha2PasswordPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */