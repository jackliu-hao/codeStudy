/*     */ package org.objectweb.asm;
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
/*     */ public final class TypePath
/*     */ {
/*     */   public static final int ARRAY_ELEMENT = 0;
/*     */   public static final int INNER_TYPE = 1;
/*     */   public static final int WILDCARD_BOUND = 2;
/*     */   public static final int TYPE_ARGUMENT = 3;
/*     */   private final byte[] typePathContainer;
/*     */   private final int typePathOffset;
/*     */   
/*     */   TypePath(byte[] typePathContainer, int typePathOffset) {
/*  73 */     this.typePathContainer = typePathContainer;
/*  74 */     this.typePathOffset = typePathOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLength() {
/*  84 */     return this.typePathContainer[this.typePathOffset];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStep(int index) {
/*  96 */     return this.typePathContainer[this.typePathOffset + 2 * index + 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStepArgument(int index) {
/* 108 */     return this.typePathContainer[this.typePathOffset + 2 * index + 2];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TypePath fromString(String typePath) {
/* 120 */     if (typePath == null || typePath.length() == 0) {
/* 121 */       return null;
/*     */     }
/* 123 */     int typePathLength = typePath.length();
/* 124 */     ByteVector output = new ByteVector(typePathLength);
/* 125 */     output.putByte(0);
/* 126 */     int typePathIndex = 0;
/* 127 */     while (typePathIndex < typePathLength) {
/* 128 */       char c = typePath.charAt(typePathIndex++);
/* 129 */       if (c == '[') {
/* 130 */         output.put11(0, 0); continue;
/* 131 */       }  if (c == '.') {
/* 132 */         output.put11(1, 0); continue;
/* 133 */       }  if (c == '*') {
/* 134 */         output.put11(2, 0); continue;
/* 135 */       }  if (c >= '0' && c <= '9') {
/* 136 */         int typeArg = c - 48;
/* 137 */         while (typePathIndex < typePathLength) {
/* 138 */           c = typePath.charAt(typePathIndex++);
/* 139 */           if (c >= '0' && c <= '9') {
/* 140 */             typeArg = typeArg * 10 + c - 48; continue;
/* 141 */           }  if (c == ';') {
/*     */             break;
/*     */           }
/* 144 */           throw new IllegalArgumentException();
/*     */         } 
/*     */         
/* 147 */         output.put11(3, typeArg); continue;
/*     */       } 
/* 149 */       throw new IllegalArgumentException();
/*     */     } 
/*     */     
/* 152 */     output.data[0] = (byte)(output.length / 2);
/* 153 */     return new TypePath(output.data, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 163 */     int length = getLength();
/* 164 */     StringBuilder result = new StringBuilder(length * 2);
/* 165 */     for (int i = 0; i < length; i++) {
/* 166 */       switch (getStep(i)) {
/*     */         case 0:
/* 168 */           result.append('[');
/*     */           break;
/*     */         case 1:
/* 171 */           result.append('.');
/*     */           break;
/*     */         case 2:
/* 174 */           result.append('*');
/*     */           break;
/*     */         case 3:
/* 177 */           result.append(getStepArgument(i)).append(';');
/*     */           break;
/*     */         default:
/* 180 */           throw new AssertionError();
/*     */       } 
/*     */     } 
/* 183 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void put(TypePath typePath, ByteVector output) {
/* 194 */     if (typePath == null) {
/* 195 */       output.putByte(0);
/*     */     } else {
/* 197 */       int length = typePath.typePathContainer[typePath.typePathOffset] * 2 + 1;
/* 198 */       output.putByteArray(typePath.typePathContainer, typePath.typePathOffset, length);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\TypePath.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */