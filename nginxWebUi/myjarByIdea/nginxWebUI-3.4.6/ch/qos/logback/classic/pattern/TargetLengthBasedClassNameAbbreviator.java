package ch.qos.logback.classic.pattern;

public class TargetLengthBasedClassNameAbbreviator implements Abbreviator {
   final int targetLength;

   public TargetLengthBasedClassNameAbbreviator(int targetLength) {
      this.targetLength = targetLength;
   }

   public String abbreviate(String fqClassName) {
      StringBuilder buf = new StringBuilder(this.targetLength);
      if (fqClassName == null) {
         throw new IllegalArgumentException("Class name may not be null");
      } else {
         int inLen = fqClassName.length();
         if (inLen < this.targetLength) {
            return fqClassName;
         } else {
            int[] dotIndexesArray = new int[16];
            int[] lengthArray = new int[17];
            int dotCount = computeDotIndexes(fqClassName, dotIndexesArray);
            if (dotCount == 0) {
               return fqClassName;
            } else {
               this.computeLengthArray(fqClassName, dotIndexesArray, lengthArray, dotCount);

               for(int i = 0; i <= dotCount; ++i) {
                  if (i == 0) {
                     buf.append(fqClassName.substring(0, lengthArray[i] - 1));
                  } else {
                     buf.append(fqClassName.substring(dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]));
                  }
               }

               return buf.toString();
            }
         }
      }
   }

   static int computeDotIndexes(String className, int[] dotArray) {
      int dotCount = 0;
      int k = 0;

      while(true) {
         k = className.indexOf(46, k);
         if (k == -1 || dotCount >= 16) {
            return dotCount;
         }

         dotArray[dotCount] = k;
         ++dotCount;
         ++k;
      }
   }

   void computeLengthArray(String className, int[] dotArray, int[] lengthArray, int dotCount) {
      int toTrim = className.length() - this.targetLength;

      int i;
      for(i = 0; i < dotCount; ++i) {
         int previousDotPosition = -1;
         if (i > 0) {
            previousDotPosition = dotArray[i - 1];
         }

         int available = dotArray[i] - previousDotPosition - 1;
         int len = available < 1 ? available : 1;
         if (toTrim > 0) {
            len = available < 1 ? available : 1;
         } else {
            len = available;
         }

         toTrim -= available - len;
         lengthArray[i] = len + 1;
      }

      i = dotCount - 1;
      lengthArray[dotCount] = className.length() - dotArray[i];
   }

   static void printArray(String msg, int[] ia) {
      System.out.print(msg);

      for(int i = 0; i < ia.length; ++i) {
         if (i == 0) {
            System.out.print(ia[i]);
         } else {
            System.out.print(", " + ia[i]);
         }
      }

      System.out.println();
   }
}
