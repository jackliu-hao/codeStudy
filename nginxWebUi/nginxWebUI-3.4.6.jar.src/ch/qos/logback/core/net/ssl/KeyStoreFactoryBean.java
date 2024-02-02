/*     */ package ch.qos.logback.core.net.ssl;
/*     */ 
/*     */ import ch.qos.logback.core.util.LocationUtil;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
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
/*     */ public class KeyStoreFactoryBean
/*     */ {
/*     */   private String location;
/*     */   private String provider;
/*     */   private String type;
/*     */   private String password;
/*     */   
/*     */   public KeyStore createKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
/*  56 */     if (getLocation() == null) {
/*  57 */       throw new IllegalArgumentException("location is required");
/*     */     }
/*     */     
/*  60 */     InputStream inputStream = null;
/*     */     try {
/*  62 */       URL url = LocationUtil.urlForResource(getLocation());
/*  63 */       inputStream = url.openStream();
/*  64 */       KeyStore keyStore = newKeyStore();
/*  65 */       keyStore.load(inputStream, getPassword().toCharArray());
/*  66 */       return keyStore;
/*  67 */     } catch (NoSuchProviderException ex) {
/*  68 */       throw new NoSuchProviderException("no such keystore provider: " + getProvider());
/*  69 */     } catch (NoSuchAlgorithmException ex) {
/*  70 */       throw new NoSuchAlgorithmException("no such keystore type: " + getType());
/*  71 */     } catch (FileNotFoundException ex) {
/*  72 */       throw new KeyStoreException(getLocation() + ": file not found");
/*  73 */     } catch (Exception ex) {
/*  74 */       throw new KeyStoreException(getLocation() + ": " + ex.getMessage(), ex);
/*     */     } finally {
/*     */       try {
/*  77 */         if (inputStream != null) {
/*  78 */           inputStream.close();
/*     */         }
/*  80 */       } catch (IOException ex) {
/*  81 */         ex.printStackTrace(System.err);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private KeyStore newKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
/*  92 */     return (getProvider() != null) ? KeyStore.getInstance(getType(), getProvider()) : KeyStore.getInstance(getType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLocation() {
/* 100 */     return this.location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocation(String location) {
/* 110 */     this.location = location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/* 119 */     if (this.type == null) {
/* 120 */       return "JKS";
/*     */     }
/* 122 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(String type) {
/* 133 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProvider() {
/* 141 */     return this.provider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProvider(String provider) {
/* 150 */     this.provider = provider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 159 */     if (this.password == null) {
/* 160 */       return "changeit";
/*     */     }
/* 162 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(String password) {
/* 170 */     this.password = password;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\KeyStoreFactoryBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */