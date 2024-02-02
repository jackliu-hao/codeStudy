package org.noear.solon.data.tran;

public enum TranPolicy {
   required(1),
   requires_new(2),
   nested(3),
   mandatory(4),
   supports(5),
   not_supported(6),
   never(7);

   public final int code;

   private TranPolicy(int code) {
      this.code = code;
   }
}
