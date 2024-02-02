/*     */ package org.xnio.sasl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
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
/*     */ final class SaslClientWrapper
/*     */   extends SaslWrapper
/*     */ {
/*     */   private final SaslClient saslClient;
/*     */   
/*     */   SaslClientWrapper(SaslClient saslClient) {
/* 157 */     this.saslClient = saslClient;
/*     */   }
/*     */   
/*     */   public byte[] wrap(byte[] bytes, int off, int len) throws SaslException {
/* 161 */     return this.saslClient.wrap(bytes, off, len);
/*     */   }
/*     */   
/*     */   public byte[] unwrap(byte[] bytes, int off, int len) throws SaslException {
/* 165 */     return this.saslClient.unwrap(bytes, off, len);
/*     */   }
/*     */   
/*     */   public byte[] wrap(ByteBuffer source) throws SaslException {
/* 169 */     return SaslUtils.wrap(this.saslClient, source);
/*     */   }
/*     */   
/*     */   public byte[] unwrap(ByteBuffer source) throws SaslException {
/* 173 */     return SaslUtils.unwrap(this.saslClient, source);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslClientWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */