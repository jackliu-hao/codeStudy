/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import org.bouncycastle.crypto.CipherParameters;
/*    */ import org.bouncycastle.crypto.Mac;
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
/*    */ public class BCMacEngine
/*    */   implements MacEngine
/*    */ {
/*    */   private Mac mac;
/*    */   
/*    */   public BCMacEngine(Mac mac, CipherParameters params) {
/* 27 */     init(mac, params);
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
/*    */   public BCMacEngine init(Mac mac, CipherParameters params) {
/* 40 */     mac.init(params);
/* 41 */     this.mac = mac;
/* 42 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Mac getMac() {
/* 51 */     return this.mac;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] in, int inOff, int len) {
/* 56 */     this.mac.update(in, inOff, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] doFinal() {
/* 61 */     byte[] result = new byte[getMacLength()];
/* 62 */     this.mac.doFinal(result, 0);
/* 63 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 68 */     this.mac.reset();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMacLength() {
/* 73 */     return this.mac.getMacSize();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 78 */     return this.mac.getAlgorithmName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\BCMacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */