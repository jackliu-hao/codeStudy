package cn.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;

public class BCMacEngine implements MacEngine {
   private org.bouncycastle.crypto.Mac mac;

   public BCMacEngine(org.bouncycastle.crypto.Mac mac, CipherParameters params) {
      this.init(mac, params);
   }

   public BCMacEngine init(org.bouncycastle.crypto.Mac mac, CipherParameters params) {
      mac.init(params);
      this.mac = mac;
      return this;
   }

   public org.bouncycastle.crypto.Mac getMac() {
      return this.mac;
   }

   public void update(byte[] in, int inOff, int len) {
      this.mac.update(in, inOff, len);
   }

   public byte[] doFinal() {
      byte[] result = new byte[this.getMacLength()];
      this.mac.doFinal(result, 0);
      return result;
   }

   public void reset() {
      this.mac.reset();
   }

   public int getMacLength() {
      return this.mac.getMacSize();
   }

   public String getAlgorithm() {
      return this.mac.getAlgorithmName();
   }
}
