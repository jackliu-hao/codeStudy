package org.noear.solon.data.tran;

public enum TranIsolation {
   unspecified(-1),
   read_uncommitted(1),
   read_committed(2),
   repeatable_read(4),
   serializable(8);

   public final int level;

   private TranIsolation(int level) {
      this.level = level;
   }
}
