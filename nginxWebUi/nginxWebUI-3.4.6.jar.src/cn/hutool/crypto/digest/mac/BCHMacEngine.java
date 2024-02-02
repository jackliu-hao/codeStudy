/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import org.bouncycastle.crypto.CipherParameters;
/*    */ import org.bouncycastle.crypto.Digest;
/*    */ import org.bouncycastle.crypto.Mac;
/*    */ import org.bouncycastle.crypto.macs.HMac;
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
/*    */ public class BCHMacEngine
/*    */   extends BCMacEngine
/*    */ {
/*    */   public BCHMacEngine(Digest digest, byte[] key, byte[] iv) {
/* 30 */     this(digest, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(key), iv));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BCHMacEngine(Digest digest, byte[] key) {
/* 41 */     this(digest, (CipherParameters)new KeyParameter(key));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BCHMacEngine(Digest digest, CipherParameters params) {
/* 52 */     this(new HMac(digest), params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BCHMacEngine(HMac mac, CipherParameters params) {
/* 63 */     super((Mac)mac, params);
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
/*    */   
/*    */   public BCHMacEngine init(Digest digest, CipherParameters params) {
/* 76 */     return (BCHMacEngine)init((Mac)new HMac(digest), params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\BCHMacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */