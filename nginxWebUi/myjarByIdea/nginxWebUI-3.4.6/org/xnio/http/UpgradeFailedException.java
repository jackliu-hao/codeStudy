package org.xnio.http;

import java.io.IOException;

public class UpgradeFailedException extends IOException {
   private static final long serialVersionUID = 3835377492285694932L;

   public UpgradeFailedException() {
   }

   public UpgradeFailedException(String msg) {
      super(msg);
   }

   public UpgradeFailedException(Throwable cause) {
      super(cause);
   }

   public UpgradeFailedException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
