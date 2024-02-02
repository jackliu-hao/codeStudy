package org.noear.solon.core.handle;

public enum Endpoint {
   before(0),
   main(1),
   after(2);

   public final int code;

   private Endpoint(int code) {
      this.code = code;
   }
}
