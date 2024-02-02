package cn.hutool.core.net;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalPortGenerater implements Serializable {
   private static final long serialVersionUID = 1L;
   private final AtomicInteger alternativePort;

   public LocalPortGenerater(int beginPort) {
      this.alternativePort = new AtomicInteger(beginPort);
   }

   public int generate() {
      int validPort;
      for(validPort = this.alternativePort.get(); !NetUtil.isUsableLocalPort(validPort); validPort = this.alternativePort.incrementAndGet()) {
      }

      return validPort;
   }
}
