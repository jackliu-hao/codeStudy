package com.google.zxing.pdf417.decoder;

import com.google.zxing.pdf417.PDF417Common;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class BarcodeValue {
   private final Map<Integer, Integer> values = new HashMap();

   void setValue(int value) {
      Integer confidence;
      if ((confidence = (Integer)this.values.get(value)) == null) {
         confidence = 0;
      }

      confidence = confidence + 1;
      this.values.put(value, confidence);
   }

   int[] getValue() {
      int maxConfidence = -1;
      Collection<Integer> result = new ArrayList();
      Iterator var3 = this.values.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry;
         if ((Integer)(entry = (Map.Entry)var3.next()).getValue() > maxConfidence) {
            maxConfidence = (Integer)entry.getValue();
            result.clear();
            result.add(entry.getKey());
         } else if ((Integer)entry.getValue() == maxConfidence) {
            result.add(entry.getKey());
         }
      }

      return PDF417Common.toIntArray(result);
   }

   public Integer getConfidence(int value) {
      return (Integer)this.values.get(value);
   }
}
