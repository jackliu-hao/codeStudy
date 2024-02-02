/*    */ package com.mysql.cj.sasl;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.InvalidParameterException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.Provider;
/*    */ import java.security.ProviderException;
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
/*    */ public final class ScramShaSaslProvider
/*    */   extends Provider
/*    */ {
/*    */   private static final long serialVersionUID = 866717063477857937L;
/*    */   private static final String INFO = "MySQL Connector/J SASL provider (implements client mechanisms for MYSQLCJ-SCRAM-SHA-1 and MYSQLCJ-SCRAM-SHA-256)";
/*    */   
/*    */   private static final class ProviderService
/*    */     extends Provider.Service
/*    */   {
/*    */     public ProviderService(Provider provider, String type, String algorithm, String className) {
/* 51 */       super(provider, type, algorithm, className, null, null);
/*    */     }
/*    */ 
/*    */     
/*    */     public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
/* 56 */       String type = getType();
/* 57 */       if (constructorParameter != null) {
/* 58 */         throw new InvalidParameterException("constructorParameter not used with " + type + " engines");
/*    */       }
/*    */       
/* 61 */       String algorithm = getAlgorithm();
/* 62 */       if (type.equals("SaslClientFactory")) {
/* 63 */         if (algorithm.equals("MYSQLCJ-SCRAM-SHA-1")) {
/* 64 */           return new ScramShaSaslClientFactory();
/*    */         }
/* 66 */         if (algorithm.equals("MYSQLCJ-SCRAM-SHA-256")) {
/* 67 */           return new ScramShaSaslClientFactory();
/*    */         }
/*    */       } 
/* 70 */       throw new ProviderException("No implementation for " + algorithm + " " + type);
/*    */     }
/*    */   }
/*    */   
/*    */   public ScramShaSaslProvider() {
/* 75 */     super("MySQLScramShaSasl", 1.0D, "MySQL Connector/J SASL provider (implements client mechanisms for MYSQLCJ-SCRAM-SHA-1 and MYSQLCJ-SCRAM-SHA-256)");
/*    */     
/* 77 */     AccessController.doPrivileged(() -> {
/*    */           putService(new ProviderService(this, "SaslClientFactory", "MYSQLCJ-SCRAM-SHA-1", ScramShaSaslClientFactory.class.getName()));
/*    */           putService(new ProviderService(this, "SaslClientFactory", "MYSQLCJ-SCRAM-SHA-256", ScramShaSaslClientFactory.class.getName()));
/*    */           return null;
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\sasl\ScramShaSaslProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */