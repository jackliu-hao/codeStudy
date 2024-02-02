package ch.qos.logback.classic.spi;

public class STEUtil {
   static int UNUSED_findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElement[] otherSTEArray) {
      if (otherSTEArray == null) {
         return 0;
      } else {
         int steIndex = steArray.length - 1;
         int parentIndex = otherSTEArray.length - 1;

         int count;
         for(count = 0; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEArray[parentIndex]); --parentIndex) {
            ++count;
            --steIndex;
         }

         return count;
      }
   }

   static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] otherSTEPArray) {
      if (otherSTEPArray == null) {
         return 0;
      } else {
         int steIndex = steArray.length - 1;
         int parentIndex = otherSTEPArray.length - 1;

         int count;
         for(count = 0; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEPArray[parentIndex].ste); --parentIndex) {
            ++count;
            --steIndex;
         }

         return count;
      }
   }
}
