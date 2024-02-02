/*     */ package org.xnio.sasl;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
/*     */ import javax.security.sasl.SaslServer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SaslWrapper
/*     */ {
/*     */   public abstract byte[] wrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SaslException;
/*     */   
/*     */   public final byte[] wrap(byte[] bytes) throws SaslException {
/*  55 */     return unwrap(bytes, 0, bytes.length);
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
/*     */   public abstract byte[] wrap(ByteBuffer paramByteBuffer) throws SaslException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract byte[] unwrap(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SaslException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final byte[] unwrap(byte[] bytes) throws SaslException {
/*  86 */     return unwrap(bytes, 0, bytes.length);
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
/*     */   public abstract byte[] unwrap(ByteBuffer paramByteBuffer) throws SaslException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void wrap(ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 113 */     destination.put(wrap(source));
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
/*     */   public final void unwrap(ByteBuffer destination, ByteBuffer source) throws SaslException {
/* 129 */     destination.put(wrap(source));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SaslWrapper create(SaslClient saslClient) {
/* 139 */     return new SaslClientWrapper(saslClient);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SaslWrapper create(SaslServer saslServer) {
/* 149 */     return new SaslServerWrapper(saslServer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */