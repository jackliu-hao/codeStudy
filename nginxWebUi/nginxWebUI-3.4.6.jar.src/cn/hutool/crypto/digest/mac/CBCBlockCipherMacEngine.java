/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import java.security.Key;
/*    */ import org.bouncycastle.crypto.BlockCipher;
/*    */ import org.bouncycastle.crypto.CipherParameters;
/*    */ import org.bouncycastle.crypto.Mac;
/*    */ import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
/*    */ import org.bouncycastle.crypto.params.KeyParameter;
/*    */ import org.bouncycastle.crypto.params.ParametersWithIV;
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
/*    */ public class CBCBlockCipherMacEngine
/*    */   extends BCMacEngine
/*    */ {
/*    */   public CBCBlockCipherMacEngine(BlockCipher digest, int macSizeInBits, Key key, byte[] iv) {
/* 31 */     this(digest, macSizeInBits, key.getEncoded(), iv);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine(BlockCipher digest, int macSizeInBits, byte[] key, byte[] iv) {
/* 43 */     this(digest, macSizeInBits, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(key), iv));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, Key key) {
/* 54 */     this(cipher, macSizeInBits, key.getEncoded());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, byte[] key) {
/* 65 */     this(cipher, macSizeInBits, (CipherParameters)new KeyParameter(key));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, CipherParameters params) {
/* 76 */     this(new CBCBlockCipherMac(cipher, macSizeInBits), params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine(CBCBlockCipherMac mac, CipherParameters params) {
/* 86 */     super((Mac)mac, params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CBCBlockCipherMacEngine init(BlockCipher cipher, CipherParameters params) {
/* 98 */     return (CBCBlockCipherMacEngine)init((Mac)new CBCBlockCipherMac(cipher), params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\CBCBlockCipherMacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */