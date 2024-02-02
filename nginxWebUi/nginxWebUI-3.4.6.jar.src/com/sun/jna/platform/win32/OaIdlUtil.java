/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import java.lang.reflect.Array;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OaIdlUtil
/*     */ {
/*     */   public static Object toPrimitiveArray(OaIdl.SAFEARRAY sa, boolean destruct) {
/* 105 */     Pointer dataPointer = sa.accessData(); try {
/*     */       Object sourceArray; Variant.VARIANT variant;
/* 107 */       int dimensions = sa.getDimensionCount();
/* 108 */       int[] elements = new int[dimensions];
/* 109 */       int[] cumElements = new int[dimensions];
/* 110 */       int varType = sa.getVarType().intValue();
/*     */       int i;
/* 112 */       for (i = 0; i < dimensions; i++) {
/* 113 */         elements[i] = sa.getUBound(i) - sa.getLBound(i) + 1;
/*     */       }
/*     */       
/* 116 */       for (i = dimensions - 1; i >= 0; i--) {
/* 117 */         if (i == dimensions - 1) {
/* 118 */           cumElements[i] = 1;
/*     */         } else {
/* 120 */           cumElements[i] = cumElements[i + 1] * elements[i + 1];
/*     */         } 
/*     */       } 
/*     */       
/* 124 */       if (dimensions == 0) {
/* 125 */         throw new IllegalArgumentException("Supplied Array has no dimensions.");
/*     */       }
/*     */       
/* 128 */       int elementCount = cumElements[0] * elements[0];
/*     */ 
/*     */       
/* 131 */       switch (varType) {
/*     */         case 16:
/*     */         case 17:
/* 134 */           sourceArray = dataPointer.getByteArray(0L, elementCount);
/*     */           break;
/*     */         case 2:
/*     */         case 11:
/*     */         case 18:
/* 139 */           sourceArray = dataPointer.getShortArray(0L, elementCount);
/*     */           break;
/*     */         case 3:
/*     */         case 10:
/*     */         case 19:
/*     */         case 22:
/*     */         case 23:
/* 146 */           sourceArray = dataPointer.getIntArray(0L, elementCount);
/*     */           break;
/*     */         case 4:
/* 149 */           sourceArray = dataPointer.getFloatArray(0L, elementCount);
/*     */           break;
/*     */         case 5:
/*     */         case 7:
/* 153 */           sourceArray = dataPointer.getDoubleArray(0L, elementCount);
/*     */           break;
/*     */         case 8:
/* 156 */           sourceArray = dataPointer.getPointerArray(0L, elementCount);
/*     */           break;
/*     */         case 12:
/* 159 */           variant = new Variant.VARIANT(dataPointer);
/* 160 */           sourceArray = variant.toArray(elementCount);
/*     */           break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 168 */           throw new IllegalStateException("Type not supported: " + varType);
/*     */       } 
/*     */       
/* 171 */       Object targetArray = Array.newInstance(Object.class, elements);
/* 172 */       toPrimitiveArray(sourceArray, targetArray, elements, cumElements, varType, new int[0]);
/* 173 */       return targetArray;
/*     */     } finally {
/* 175 */       sa.unaccessData();
/* 176 */       if (destruct) {
/* 177 */         sa.destroy();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void toPrimitiveArray(Object dataArray, Object targetArray, int[] elements, int[] cumElements, int varType, int[] currentIdx) {
/* 183 */     int dimIdx = currentIdx.length;
/* 184 */     int[] subIdx = new int[currentIdx.length + 1];
/* 185 */     System.arraycopy(currentIdx, 0, subIdx, 0, dimIdx);
/* 186 */     for (int i = 0; i < elements[dimIdx]; i++) {
/* 187 */       subIdx[dimIdx] = i;
/* 188 */       if (dimIdx == elements.length - 1) {
/* 189 */         Variant.VARIANT holder; int offset = 0;
/* 190 */         for (int j = 0; j < dimIdx; j++) {
/* 191 */           offset += cumElements[j] * currentIdx[j];
/*     */         }
/* 193 */         offset += subIdx[dimIdx];
/* 194 */         int targetPos = subIdx[dimIdx];
/* 195 */         switch (varType) {
/*     */           case 11:
/* 197 */             Array.set(targetArray, targetPos, Boolean.valueOf((Array.getShort(dataArray, offset) != 0)));
/*     */             break;
/*     */           case 16:
/*     */           case 17:
/* 201 */             Array.set(targetArray, targetPos, Byte.valueOf(Array.getByte(dataArray, offset)));
/*     */             break;
/*     */           case 2:
/*     */           case 18:
/* 205 */             Array.set(targetArray, targetPos, Short.valueOf(Array.getShort(dataArray, offset)));
/*     */             break;
/*     */           case 3:
/*     */           case 19:
/*     */           case 22:
/*     */           case 23:
/* 211 */             Array.set(targetArray, targetPos, Integer.valueOf(Array.getInt(dataArray, offset)));
/*     */             break;
/*     */           case 10:
/* 214 */             Array.set(targetArray, targetPos, new WinDef.SCODE(Array.getInt(dataArray, offset)));
/*     */             break;
/*     */           case 4:
/* 217 */             Array.set(targetArray, targetPos, Float.valueOf(Array.getFloat(dataArray, offset)));
/*     */             break;
/*     */           case 5:
/* 220 */             Array.set(targetArray, targetPos, Double.valueOf(Array.getDouble(dataArray, offset)));
/*     */             break;
/*     */           case 7:
/* 223 */             Array.set(targetArray, targetPos, (new OaIdl.DATE(Array.getDouble(dataArray, offset))).getAsJavaDate());
/*     */             break;
/*     */           case 8:
/* 226 */             Array.set(targetArray, targetPos, (new WTypes.BSTR((Pointer)Array.get(dataArray, offset))).getValue());
/*     */             break;
/*     */           case 12:
/* 229 */             holder = (Variant.VARIANT)Array.get(dataArray, offset);
/* 230 */             switch (holder.getVarType().intValue()) {
/*     */               case 0:
/*     */               case 1:
/* 233 */                 Array.set(targetArray, targetPos, null);
/*     */                 break;
/*     */               case 11:
/* 236 */                 Array.set(targetArray, targetPos, Boolean.valueOf(holder.booleanValue()));
/*     */                 break;
/*     */               case 16:
/*     */               case 17:
/* 240 */                 Array.set(targetArray, targetPos, Byte.valueOf(holder.byteValue()));
/*     */                 break;
/*     */               case 2:
/*     */               case 18:
/* 244 */                 Array.set(targetArray, targetPos, Short.valueOf(holder.shortValue()));
/*     */                 break;
/*     */               case 3:
/*     */               case 19:
/*     */               case 22:
/*     */               case 23:
/* 250 */                 Array.set(targetArray, targetPos, Integer.valueOf(holder.intValue()));
/*     */                 break;
/*     */               case 10:
/* 253 */                 Array.set(targetArray, targetPos, new WinDef.SCODE(holder.intValue()));
/*     */                 break;
/*     */               case 4:
/* 256 */                 Array.set(targetArray, targetPos, Float.valueOf(holder.floatValue()));
/*     */                 break;
/*     */               case 5:
/* 259 */                 Array.set(targetArray, targetPos, Double.valueOf(holder.doubleValue()));
/*     */                 break;
/*     */               case 7:
/* 262 */                 Array.set(targetArray, targetPos, holder.dateValue());
/*     */                 break;
/*     */               case 8:
/* 265 */                 Array.set(targetArray, targetPos, holder.stringValue());
/*     */                 break;
/*     */             } 
/* 268 */             throw new IllegalStateException("Type not supported: " + holder.getVarType().intValue());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           default:
/* 277 */             throw new IllegalStateException("Type not supported: " + varType);
/*     */         } 
/*     */       } else {
/* 280 */         toPrimitiveArray(dataArray, Array.get(targetArray, i), elements, cumElements, varType, subIdx);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\OaIdlUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */