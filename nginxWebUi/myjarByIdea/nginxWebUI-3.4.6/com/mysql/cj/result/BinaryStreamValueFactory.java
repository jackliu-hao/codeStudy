package com.mysql.cj.result;

import com.mysql.cj.conf.PropertySet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BinaryStreamValueFactory extends DefaultValueFactory<InputStream> {
   public BinaryStreamValueFactory(PropertySet pset) {
      super(pset);
   }

   public InputStream createFromBytes(byte[] bytes, int offset, int length, Field f) {
      return new ByteArrayInputStream(bytes, offset, length);
   }

   public String getTargetTypeName() {
      return InputStream.class.getName();
   }
}
