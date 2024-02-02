package org.apache.commons.compress.harmony.unpack200;

import java.util.List;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantPoolEntry;

public class SegmentConstantPool {
   private final CpBands bands;
   private final SegmentConstantPoolArrayCache arrayCache = new SegmentConstantPoolArrayCache();
   public static final int ALL = 0;
   public static final int UTF_8 = 1;
   public static final int CP_INT = 2;
   public static final int CP_FLOAT = 3;
   public static final int CP_LONG = 4;
   public static final int CP_DOUBLE = 5;
   public static final int CP_STRING = 6;
   public static final int CP_CLASS = 7;
   public static final int SIGNATURE = 8;
   public static final int CP_DESCR = 9;
   public static final int CP_FIELD = 10;
   public static final int CP_METHOD = 11;
   public static final int CP_IMETHOD = 12;
   protected static final String REGEX_MATCH_ALL = ".*";
   protected static final String INITSTRING = "<init>";
   protected static final String REGEX_MATCH_INIT = "^<init>.*";

   public SegmentConstantPool(CpBands bands) {
      this.bands = bands;
   }

   public ClassFileEntry getValue(int cp, long value) throws Pack200Exception {
      int index = (int)value;
      if (index == -1) {
         return null;
      } else if (index < 0) {
         throw new Pack200Exception("Cannot have a negative range");
      } else if (cp == 1) {
         return this.bands.cpUTF8Value(index);
      } else if (cp == 2) {
         return this.bands.cpIntegerValue(index);
      } else if (cp == 3) {
         return this.bands.cpFloatValue(index);
      } else if (cp == 4) {
         return this.bands.cpLongValue(index);
      } else if (cp == 5) {
         return this.bands.cpDoubleValue(index);
      } else if (cp == 6) {
         return this.bands.cpStringValue(index);
      } else if (cp == 7) {
         return this.bands.cpClassValue(index);
      } else if (cp == 8) {
         return this.bands.cpSignatureValue(index);
      } else if (cp == 9) {
         return this.bands.cpNameAndTypeValue(index);
      } else {
         throw new Error("Tried to get a value I don't know about: " + cp);
      }
   }

   public ConstantPoolEntry getClassSpecificPoolEntry(int cp, long desiredIndex, String desiredClassName) throws Pack200Exception {
      int index = (int)desiredIndex;
      int realIndex = true;
      String[] array = null;
      if (cp == 10) {
         array = this.bands.getCpFieldClass();
      } else if (cp == 11) {
         array = this.bands.getCpMethodClass();
      } else {
         if (cp != 12) {
            throw new Error("Don't know how to handle " + cp);
         }

         array = this.bands.getCpIMethodClass();
      }

      int realIndex = this.matchSpecificPoolEntryIndex(array, desiredClassName, index);
      return this.getConstantPoolEntry(cp, (long)realIndex);
   }

   public ConstantPoolEntry getClassPoolEntry(String name) {
      String[] classes = this.bands.getCpClass();
      int index = this.matchSpecificPoolEntryIndex(classes, name, 0);
      if (index == -1) {
         return null;
      } else {
         try {
            return this.getConstantPoolEntry(7, (long)index);
         } catch (Pack200Exception var5) {
            throw new Error("Error getting class pool entry");
         }
      }
   }

   public ConstantPoolEntry getInitMethodPoolEntry(int cp, long value, String desiredClassName) throws Pack200Exception {
      int realIndex = true;
      String desiredRegex = "^<init>.*";
      if (cp != 11) {
         throw new Error("Nothing but CP_METHOD can be an <init>");
      } else {
         int realIndex = this.matchSpecificPoolEntryIndex(this.bands.getCpMethodClass(), this.bands.getCpMethodDescriptor(), desiredClassName, "^<init>.*", (int)value);
         return this.getConstantPoolEntry(cp, (long)realIndex);
      }
   }

   protected int matchSpecificPoolEntryIndex(String[] nameArray, String compareString, int desiredIndex) {
      return this.matchSpecificPoolEntryIndex(nameArray, nameArray, compareString, ".*", desiredIndex);
   }

   protected int matchSpecificPoolEntryIndex(String[] primaryArray, String[] secondaryArray, String primaryCompareString, String secondaryCompareRegex, int desiredIndex) {
      int instanceCount = -1;
      List indexList = this.arrayCache.indexesForArrayKey(primaryArray, primaryCompareString);
      if (indexList.isEmpty()) {
         return -1;
      } else {
         for(int index = 0; index < indexList.size(); ++index) {
            int arrayIndex = (Integer)indexList.get(index);
            if (regexMatches(secondaryCompareRegex, secondaryArray[arrayIndex])) {
               ++instanceCount;
               if (instanceCount == desiredIndex) {
                  return arrayIndex;
               }
            }
         }

         return -1;
      }
   }

   protected static boolean regexMatches(String regexString, String compareString) {
      if (".*".equals(regexString)) {
         return true;
      } else if ("^<init>.*".equals(regexString)) {
         return compareString.length() < "<init>".length() ? false : "<init>".equals(compareString.substring(0, "<init>".length()));
      } else {
         throw new Error("regex trying to match a pattern I don't know: " + regexString);
      }
   }

   public ConstantPoolEntry getConstantPoolEntry(int cp, long value) throws Pack200Exception {
      int index = (int)value;
      if (index == -1) {
         return null;
      } else if (index < 0) {
         throw new Pack200Exception("Cannot have a negative range");
      } else if (cp == 1) {
         return this.bands.cpUTF8Value(index);
      } else if (cp == 2) {
         return this.bands.cpIntegerValue(index);
      } else if (cp == 3) {
         return this.bands.cpFloatValue(index);
      } else if (cp == 4) {
         return this.bands.cpLongValue(index);
      } else if (cp == 5) {
         return this.bands.cpDoubleValue(index);
      } else if (cp == 6) {
         return this.bands.cpStringValue(index);
      } else if (cp == 7) {
         return this.bands.cpClassValue(index);
      } else if (cp == 8) {
         throw new Error("I don't know what to do with signatures yet");
      } else if (cp == 9) {
         throw new Error("I don't know what to do with descriptors yet");
      } else if (cp == 10) {
         return this.bands.cpFieldValue(index);
      } else if (cp == 11) {
         return this.bands.cpMethodValue(index);
      } else if (cp == 12) {
         return this.bands.cpIMethodValue(index);
      } else {
         throw new Error("Get value incomplete");
      }
   }
}
