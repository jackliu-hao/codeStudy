package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import java.lang.reflect.Array;

public abstract class OaIdlUtil {
   public static Object toPrimitiveArray(OaIdl.SAFEARRAY sa, boolean destruct) {
      Pointer dataPointer = sa.accessData();

      Object var10;
      try {
         int dimensions = sa.getDimensionCount();
         int[] elements = new int[dimensions];
         int[] cumElements = new int[dimensions];
         int varType = sa.getVarType().intValue();

         int elementCount;
         for(elementCount = 0; elementCount < dimensions; ++elementCount) {
            elements[elementCount] = sa.getUBound(elementCount) - sa.getLBound(elementCount) + 1;
         }

         for(elementCount = dimensions - 1; elementCount >= 0; --elementCount) {
            if (elementCount == dimensions - 1) {
               cumElements[elementCount] = 1;
            } else {
               cumElements[elementCount] = cumElements[elementCount + 1] * elements[elementCount + 1];
            }
         }

         if (dimensions == 0) {
            throw new IllegalArgumentException("Supplied Array has no dimensions.");
         }

         elementCount = cumElements[0] * elements[0];
         Object sourceArray;
         switch (varType) {
            case 2:
            case 11:
            case 18:
               sourceArray = dataPointer.getShortArray(0L, elementCount);
               break;
            case 3:
            case 10:
            case 19:
            case 22:
            case 23:
               sourceArray = dataPointer.getIntArray(0L, elementCount);
               break;
            case 4:
               sourceArray = dataPointer.getFloatArray(0L, elementCount);
               break;
            case 5:
            case 7:
               sourceArray = dataPointer.getDoubleArray(0L, elementCount);
               break;
            case 6:
            case 9:
            case 13:
            case 14:
            case 15:
            case 20:
            case 21:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            default:
               throw new IllegalStateException("Type not supported: " + varType);
            case 8:
               sourceArray = dataPointer.getPointerArray(0L, elementCount);
               break;
            case 12:
               Variant.VARIANT variant = new Variant.VARIANT(dataPointer);
               sourceArray = variant.toArray(elementCount);
               break;
            case 16:
            case 17:
               sourceArray = dataPointer.getByteArray(0L, elementCount);
         }

         Object targetArray = Array.newInstance(Object.class, elements);
         toPrimitiveArray(sourceArray, targetArray, elements, cumElements, varType, new int[0]);
         var10 = targetArray;
      } finally {
         sa.unaccessData();
         if (destruct) {
            sa.destroy();
         }

      }

      return var10;
   }

   private static void toPrimitiveArray(Object dataArray, Object targetArray, int[] elements, int[] cumElements, int varType, int[] currentIdx) {
      int dimIdx = currentIdx.length;
      int[] subIdx = new int[currentIdx.length + 1];
      System.arraycopy(currentIdx, 0, subIdx, 0, dimIdx);

      for(int i = 0; i < elements[dimIdx]; ++i) {
         subIdx[dimIdx] = i;
         if (dimIdx != elements.length - 1) {
            toPrimitiveArray(dataArray, Array.get(targetArray, i), elements, cumElements, varType, subIdx);
         } else {
            int offset = 0;

            int targetPos;
            for(targetPos = 0; targetPos < dimIdx; ++targetPos) {
               offset += cumElements[targetPos] * currentIdx[targetPos];
            }

            offset += subIdx[dimIdx];
            targetPos = subIdx[dimIdx];
            switch (varType) {
               case 2:
               case 18:
                  Array.set(targetArray, targetPos, Array.getShort(dataArray, offset));
                  break;
               case 3:
               case 19:
               case 22:
               case 23:
                  Array.set(targetArray, targetPos, Array.getInt(dataArray, offset));
                  break;
               case 4:
                  Array.set(targetArray, targetPos, Array.getFloat(dataArray, offset));
                  break;
               case 5:
                  Array.set(targetArray, targetPos, Array.getDouble(dataArray, offset));
                  break;
               case 6:
               case 9:
               case 13:
               case 14:
               case 15:
               case 20:
               case 21:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 36:
               default:
                  throw new IllegalStateException("Type not supported: " + varType);
               case 7:
                  Array.set(targetArray, targetPos, (new OaIdl.DATE(Array.getDouble(dataArray, offset))).getAsJavaDate());
                  break;
               case 8:
                  Array.set(targetArray, targetPos, (new WTypes.BSTR((Pointer)Array.get(dataArray, offset))).getValue());
                  break;
               case 10:
                  Array.set(targetArray, targetPos, new WinDef.SCODE((long)Array.getInt(dataArray, offset)));
                  break;
               case 11:
                  Array.set(targetArray, targetPos, Array.getShort(dataArray, offset) != 0);
                  break;
               case 12:
                  Variant.VARIANT holder = (Variant.VARIANT)Array.get(dataArray, offset);
                  switch (holder.getVarType().intValue()) {
                     case 0:
                     case 1:
                        Array.set(targetArray, targetPos, (Object)null);
                        continue;
                     case 2:
                     case 18:
                        Array.set(targetArray, targetPos, holder.shortValue());
                        continue;
                     case 3:
                     case 19:
                     case 22:
                     case 23:
                        Array.set(targetArray, targetPos, holder.intValue());
                        continue;
                     case 4:
                        Array.set(targetArray, targetPos, holder.floatValue());
                        continue;
                     case 5:
                        Array.set(targetArray, targetPos, holder.doubleValue());
                        continue;
                     case 6:
                     case 9:
                     case 12:
                     case 13:
                     case 14:
                     case 15:
                     case 20:
                     case 21:
                     default:
                        throw new IllegalStateException("Type not supported: " + holder.getVarType().intValue());
                     case 7:
                        Array.set(targetArray, targetPos, holder.dateValue());
                        continue;
                     case 8:
                        Array.set(targetArray, targetPos, holder.stringValue());
                        continue;
                     case 10:
                        Array.set(targetArray, targetPos, new WinDef.SCODE((long)holder.intValue()));
                        continue;
                     case 11:
                        Array.set(targetArray, targetPos, holder.booleanValue());
                        continue;
                     case 16:
                     case 17:
                        Array.set(targetArray, targetPos, holder.byteValue());
                        continue;
                  }
               case 16:
               case 17:
                  Array.set(targetArray, targetPos, Array.getByte(dataArray, offset));
            }
         }
      }

   }
}
