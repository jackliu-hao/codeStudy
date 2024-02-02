/*     */ package com.warrenstrange.googleauth;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import org.apache.http.client.utils.URIBuilder;
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
/*     */ public final class GoogleAuthenticatorQRGenerator
/*     */ {
/*     */   private static final String TOTP_URI_FORMAT = "https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=0";
/*     */   
/*     */   private static String internalURLEncode(String s) {
/*     */     try {
/*  66 */       return URLEncoder.encode(s, "UTF-8");
/*     */     }
/*  68 */     catch (UnsupportedEncodingException e) {
/*     */       
/*  70 */       throw new IllegalArgumentException("UTF-8 encoding is not supported by URLEncoder.", e);
/*     */     } 
/*     */   }
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
/*     */   private static String formatLabel(String issuer, String accountName) {
/*  92 */     if (accountName == null || accountName.trim().length() == 0)
/*     */     {
/*  94 */       throw new IllegalArgumentException("Account name must not be empty.");
/*     */     }
/*     */     
/*  97 */     StringBuilder sb = new StringBuilder();
/*     */     
/*  99 */     if (issuer != null) {
/*     */       
/* 101 */       if (issuer.contains(":"))
/*     */       {
/* 103 */         throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
/*     */       }
/*     */       
/* 106 */       sb.append(issuer);
/* 107 */       sb.append(":");
/*     */     } 
/*     */     
/* 110 */     sb.append(accountName);
/*     */     
/* 112 */     return sb.toString();
/*     */   }
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
/*     */   public static String getOtpAuthURL(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
/* 141 */     return String.format("https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=0", new Object[] {
/*     */           
/* 143 */           internalURLEncode(getOtpAuthTotpURL(issuer, accountName, credentials))
/*     */         });
/*     */   }
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
/*     */   public static String getOtpAuthTotpURL(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
/* 172 */     URIBuilder uri = (new URIBuilder()).setScheme("otpauth").setHost("totp").setPath("/" + formatLabel(issuer, accountName)).setParameter("secret", credentials.getKey());
/*     */     
/* 174 */     if (issuer != null) {
/*     */       
/* 176 */       if (issuer.contains(":"))
/*     */       {
/* 178 */         throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
/*     */       }
/*     */       
/* 181 */       uri.setParameter("issuer", issuer);
/*     */     } 
/*     */     
/* 184 */     GoogleAuthenticatorConfig config = credentials.getConfig();
/* 185 */     uri.setParameter("algorithm", getAlgorithmName(config.getHmacHashFunction()));
/* 186 */     uri.setParameter("digits", String.valueOf(config.getCodeDigits()));
/* 187 */     uri.setParameter("period", String.valueOf((int)(config.getTimeStepSizeInMillis() / 1000L)));
/*     */     
/* 189 */     return uri.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getAlgorithmName(HmacHashFunction hashFunction) {
/* 194 */     switch (hashFunction) {
/*     */       
/*     */       case HmacSHA1:
/* 197 */         return "SHA1";
/*     */       
/*     */       case HmacSHA256:
/* 200 */         return "SHA256";
/*     */       
/*     */       case HmacSHA512:
/* 203 */         return "SHA512";
/*     */     } 
/*     */     
/* 206 */     throw new IllegalArgumentException(String.format("Unknown algorithm %s", new Object[] { hashFunction }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\GoogleAuthenticatorQRGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */