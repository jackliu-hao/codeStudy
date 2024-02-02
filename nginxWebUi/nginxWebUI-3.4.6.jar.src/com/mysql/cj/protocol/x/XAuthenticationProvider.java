/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.AuthenticationProvider;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.xdevapi.XDevAPIError;
/*     */ import java.util.Arrays;
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
/*     */ public class XAuthenticationProvider
/*     */   implements AuthenticationProvider<XMessage>
/*     */ {
/*     */   XProtocol protocol;
/*  52 */   private PropertyDefinitions.AuthMech authMech = null;
/*  53 */   private XMessageBuilder messageBuilder = new XMessageBuilder();
/*     */ 
/*     */   
/*     */   public void init(Protocol<XMessage> prot, PropertySet propertySet, ExceptionInterceptor exceptionInterceptor) {
/*  57 */     this.protocol = (XProtocol)prot;
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(String userName, String password, String database) {
/*  62 */     changeUser(userName, password, database);
/*     */   }
/*     */   
/*     */   public void changeUser(String userName, String password, String database) {
/*     */     List<PropertyDefinitions.AuthMech> tryAuthMech;
/*  67 */     boolean overTLS = ((XServerCapabilities)this.protocol.getServerSession().getCapabilities()).getTls();
/*  68 */     RuntimeProperty<PropertyDefinitions.AuthMech> authMechProp = this.protocol.getPropertySet().getEnumProperty(PropertyKey.xdevapiAuth);
/*     */     
/*  70 */     if (overTLS || authMechProp.isExplicitlySet()) {
/*  71 */       tryAuthMech = Arrays.asList(new PropertyDefinitions.AuthMech[] { (PropertyDefinitions.AuthMech)authMechProp.getValue() });
/*     */     } else {
/*  73 */       tryAuthMech = Arrays.asList(new PropertyDefinitions.AuthMech[] { PropertyDefinitions.AuthMech.MYSQL41, PropertyDefinitions.AuthMech.SHA256_MEMORY });
/*     */     } 
/*     */     
/*  76 */     XProtocolError capturedAuthErr = null;
/*  77 */     for (PropertyDefinitions.AuthMech am : tryAuthMech) {
/*  78 */       this.authMech = am; try {
/*     */         byte[] nonce; byte[] salt;
/*  80 */         switch (this.authMech) {
/*     */           case SHA256_MEMORY:
/*  82 */             this.protocol.send(this.messageBuilder.buildSha256MemoryAuthStart(), 0);
/*  83 */             nonce = this.protocol.readAuthenticateContinue();
/*  84 */             this.protocol.send(this.messageBuilder.buildSha256MemoryAuthContinue(userName, password, nonce, database), 0);
/*     */             break;
/*     */           case MYSQL41:
/*  87 */             this.protocol.send(this.messageBuilder.buildMysql41AuthStart(), 0);
/*  88 */             salt = this.protocol.readAuthenticateContinue();
/*  89 */             this.protocol.send(this.messageBuilder.buildMysql41AuthContinue(userName, password, salt, database), 0);
/*     */             break;
/*     */           case PLAIN:
/*  92 */             if (overTLS) {
/*  93 */               this.protocol.send(this.messageBuilder.buildPlainAuthStart(userName, password, database), 0); break;
/*     */             } 
/*  95 */             throw new XProtocolError("PLAIN authentication is not allowed via unencrypted connection.");
/*     */ 
/*     */           
/*     */           case EXTERNAL:
/*  99 */             this.protocol.send(this.messageBuilder.buildExternalAuthStart(database), 0);
/*     */             break;
/*     */           default:
/* 102 */             throw new WrongArgumentException("Unknown authentication mechanism '" + this.authMech + "'.");
/*     */         } 
/* 104 */       } catch (CJCommunicationsException e) {
/* 105 */         if (capturedAuthErr != null && e.getCause() instanceof java.nio.channels.ClosedChannelException)
/*     */         {
/* 107 */           throw capturedAuthErr;
/*     */         }
/* 109 */         throw e;
/*     */       } 
/*     */       
/*     */       try {
/* 113 */         this.protocol.readAuthenticateOk();
/*     */         
/* 115 */         capturedAuthErr = null;
/*     */         break;
/* 117 */       } catch (XProtocolError e) {
/* 118 */         if (e.getErrorCode() != 1045) {
/* 119 */           throw e;
/*     */         }
/* 121 */         capturedAuthErr = e;
/*     */       } 
/*     */     } 
/*     */     
/* 125 */     if (capturedAuthErr != null) {
/* 126 */       if (tryAuthMech.size() == 1) {
/* 127 */         throw capturedAuthErr;
/*     */       }
/*     */       
/* 130 */       String errMsg = "Authentication failed using " + StringUtils.joinWithSerialComma(tryAuthMech) + ", check username and password or try a secure connection";
/*     */       
/* 132 */       XDevAPIError ex = new XDevAPIError(errMsg, (Throwable)capturedAuthErr);
/* 133 */       ex.setVendorCode(capturedAuthErr.getErrorCode());
/* 134 */       ex.setSQLState(capturedAuthErr.getSQLState());
/* 135 */       throw ex;
/*     */     } 
/*     */     
/* 138 */     this.protocol.afterHandshake();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XAuthenticationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */