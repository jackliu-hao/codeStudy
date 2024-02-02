package com.github.jaiimageio.impl.plugins.tiff;

public class TIFFAttrInfo {
   int valueType = 1;
   int dataType;
   boolean isRequired = false;
   String defaultValue = null;
   String[] enumerations = null;
   String minValue = null;
   String maxValue = null;
   int listMinLength = 0;
   int listMaxLength = Integer.MAX_VALUE;
}
