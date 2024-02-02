package com.github.jaiimageio.plugins.tiff;

import java.util.HashMap;
import java.util.Map;

public class TIFFTag {
   public static final int TIFF_BYTE = 1;
   public static final int TIFF_ASCII = 2;
   public static final int TIFF_SHORT = 3;
   public static final int TIFF_LONG = 4;
   public static final int TIFF_RATIONAL = 5;
   public static final int TIFF_SBYTE = 6;
   public static final int TIFF_UNDEFINED = 7;
   public static final int TIFF_SSHORT = 8;
   public static final int TIFF_SLONG = 9;
   public static final int TIFF_SRATIONAL = 10;
   public static final int TIFF_FLOAT = 11;
   public static final int TIFF_DOUBLE = 12;
   public static final int TIFF_IFD_POINTER = 13;
   public static final int MIN_DATATYPE = 1;
   public static final int MAX_DATATYPE = 13;
   private static final int[] sizeOfType = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 4};
   int number;
   String name;
   int dataTypes;
   TIFFTagSet tagSet;
   Map valueNames;

   public TIFFTag(String name, int number, int dataTypes, TIFFTagSet tagSet) {
      this.tagSet = null;
      this.valueNames = null;
      this.name = name;
      this.number = number;
      this.dataTypes = dataTypes;
      this.tagSet = tagSet;
   }

   public TIFFTag(String name, int number, int dataTypes) {
      this(name, number, dataTypes, (TIFFTagSet)null);
   }

   public static int getSizeOfType(int dataType) {
      if (dataType >= 1 && dataType <= 13) {
         return sizeOfType[dataType];
      } else {
         throw new IllegalArgumentException("dataType out of range!");
      }
   }

   public String getName() {
      return this.name;
   }

   public int getNumber() {
      return this.number;
   }

   public int getDataTypes() {
      return this.dataTypes;
   }

   public boolean isDataTypeOK(int dataType) {
      if (dataType >= 1 && dataType <= 13) {
         return (this.dataTypes & 1 << dataType) != 0;
      } else {
         throw new IllegalArgumentException("datatype not in range!");
      }
   }

   public TIFFTagSet getTagSet() {
      return this.tagSet;
   }

   public boolean isIFDPointer() {
      return this.tagSet != null || (this.dataTypes & 8192) != 0;
   }

   public boolean hasValueNames() {
      return this.valueNames != null;
   }

   protected void addValueName(int value, String name) {
      if (this.valueNames == null) {
         this.valueNames = new HashMap();
      }

      this.valueNames.put(new Integer(value), name);
   }

   public String getValueName(int value) {
      return this.valueNames == null ? null : (String)this.valueNames.get(new Integer(value));
   }
}
