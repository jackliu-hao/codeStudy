package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public abstract class AbstractExpandedDecoder {
   private final BitArray information;
   private final GeneralAppIdDecoder generalDecoder;

   AbstractExpandedDecoder(BitArray information) {
      this.information = information;
      this.generalDecoder = new GeneralAppIdDecoder(information);
   }

   protected final BitArray getInformation() {
      return this.information;
   }

   protected final GeneralAppIdDecoder getGeneralDecoder() {
      return this.generalDecoder;
   }

   public abstract String parseInformation() throws NotFoundException, FormatException;

   public static AbstractExpandedDecoder createDecoder(BitArray var0) {
      // $FF: Couldn't be decompiled
   }
}
