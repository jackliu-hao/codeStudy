package com.sun.jna.platform.win32;

public class SspiUtil {
   public static class ManagedSecBufferDesc extends Sspi.SecBufferDesc {
      private final Sspi.SecBuffer[] secBuffers;

      public ManagedSecBufferDesc(int type, byte[] token) {
         this.secBuffers = new Sspi.SecBuffer[]{new Sspi.SecBuffer(type, token)};
         this.pBuffers = this.secBuffers[0].getPointer();
         this.cBuffers = this.secBuffers.length;
      }

      public ManagedSecBufferDesc(int type, int tokenSize) {
         this.secBuffers = new Sspi.SecBuffer[]{new Sspi.SecBuffer(type, tokenSize)};
         this.pBuffers = this.secBuffers[0].getPointer();
         this.cBuffers = this.secBuffers.length;
      }

      public ManagedSecBufferDesc(int bufferCount) {
         this.cBuffers = bufferCount;
         this.secBuffers = (Sspi.SecBuffer[])((Sspi.SecBuffer[])(new Sspi.SecBuffer()).toArray(bufferCount));
         this.pBuffers = this.secBuffers[0].getPointer();
         this.cBuffers = this.secBuffers.length;
      }

      public Sspi.SecBuffer getBuffer(int idx) {
         return this.secBuffers[idx];
      }

      public void write() {
         Sspi.SecBuffer[] var1 = this.secBuffers;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Sspi.SecBuffer sb = var1[var3];
            sb.write();
         }

         this.writeField("ulVersion");
         this.writeField("pBuffers");
         this.writeField("cBuffers");
      }

      public void read() {
         Sspi.SecBuffer[] var1 = this.secBuffers;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Sspi.SecBuffer sb = var1[var3];
            sb.read();
         }

      }
   }
}
