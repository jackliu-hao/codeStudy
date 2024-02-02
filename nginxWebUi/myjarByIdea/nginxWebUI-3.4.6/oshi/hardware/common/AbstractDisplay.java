package oshi.hardware.common;

import java.util.Arrays;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.util.EdidUtil;

@Immutable
public abstract class AbstractDisplay implements Display {
   private final byte[] edid;

   protected AbstractDisplay(byte[] edid) {
      this.edid = Arrays.copyOf(edid, edid.length);
   }

   public byte[] getEdid() {
      return Arrays.copyOf(this.edid, this.edid.length);
   }

   public String toString() {
      return EdidUtil.toString(this.edid);
   }
}
