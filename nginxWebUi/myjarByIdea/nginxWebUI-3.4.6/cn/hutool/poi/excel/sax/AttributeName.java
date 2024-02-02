package cn.hutool.poi.excel.sax;

import org.xml.sax.Attributes;

public enum AttributeName {
   r,
   s,
   t;

   public boolean match(String attributeName) {
      return this.name().equals(attributeName);
   }

   public String getValue(Attributes attributes) {
      return attributes.getValue(this.name());
   }
}
