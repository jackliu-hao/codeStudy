package com.github.jaiimageio.impl.plugins.tiff;

public class TIFFElementInfo {
   String[] childNames;
   String[] attributeNames;
   int childPolicy;
   int minChildren = 0;
   int maxChildren = Integer.MAX_VALUE;
   int objectValueType = 0;
   Class objectClass = null;
   Object objectDefaultValue = null;
   Object[] objectEnumerations = null;
   Comparable objectMinValue = null;
   Comparable objectMaxValue = null;
   int objectArrayMinLength = 0;
   int objectArrayMaxLength = 0;

   public TIFFElementInfo(String[] childNames, String[] attributeNames, int childPolicy) {
      this.childNames = childNames;
      this.attributeNames = attributeNames;
      this.childPolicy = childPolicy;
   }
}
