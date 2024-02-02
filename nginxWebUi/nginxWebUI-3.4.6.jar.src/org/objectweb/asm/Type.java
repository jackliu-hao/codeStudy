/*     */ package org.objectweb.asm;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
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
/*     */ public final class Type
/*     */ {
/*     */   public static final int VOID = 0;
/*     */   public static final int BOOLEAN = 1;
/*     */   public static final int CHAR = 2;
/*     */   public static final int BYTE = 3;
/*     */   public static final int SHORT = 4;
/*     */   public static final int INT = 5;
/*     */   public static final int FLOAT = 6;
/*     */   public static final int LONG = 7;
/*     */   public static final int DOUBLE = 8;
/*     */   public static final int ARRAY = 9;
/*     */   public static final int OBJECT = 10;
/*     */   public static final int METHOD = 11;
/*     */   private static final int INTERNAL = 12;
/*     */   private static final String PRIMITIVE_DESCRIPTORS = "VZCBSIFJD";
/*  85 */   public static final Type VOID_TYPE = new Type(0, "VZCBSIFJD", 0, 1);
/*     */ 
/*     */   
/*  88 */   public static final Type BOOLEAN_TYPE = new Type(1, "VZCBSIFJD", 1, 2);
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final Type CHAR_TYPE = new Type(2, "VZCBSIFJD", 2, 3);
/*     */ 
/*     */   
/*  95 */   public static final Type BYTE_TYPE = new Type(3, "VZCBSIFJD", 3, 4);
/*     */ 
/*     */   
/*  98 */   public static final Type SHORT_TYPE = new Type(4, "VZCBSIFJD", 4, 5);
/*     */ 
/*     */   
/* 101 */   public static final Type INT_TYPE = new Type(5, "VZCBSIFJD", 5, 6);
/*     */ 
/*     */   
/* 104 */   public static final Type FLOAT_TYPE = new Type(6, "VZCBSIFJD", 6, 7);
/*     */ 
/*     */   
/* 107 */   public static final Type LONG_TYPE = new Type(7, "VZCBSIFJD", 7, 8);
/*     */ 
/*     */   
/* 110 */   public static final Type DOUBLE_TYPE = new Type(8, "VZCBSIFJD", 8, 9);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int sort;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String valueBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int valueBegin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int valueEnd;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Type(int sort, String valueBuffer, int valueBegin, int valueEnd) {
/* 160 */     this.sort = sort;
/* 161 */     this.valueBuffer = valueBuffer;
/* 162 */     this.valueBegin = valueBegin;
/* 163 */     this.valueEnd = valueEnd;
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
/*     */ 
/*     */   
/*     */   public static Type getType(String typeDescriptor) {
/* 177 */     return getTypeInternal(typeDescriptor, 0, typeDescriptor.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getType(Class<?> clazz) {
/* 187 */     if (clazz.isPrimitive()) {
/* 188 */       if (clazz == int.class)
/* 189 */         return INT_TYPE; 
/* 190 */       if (clazz == void.class)
/* 191 */         return VOID_TYPE; 
/* 192 */       if (clazz == boolean.class)
/* 193 */         return BOOLEAN_TYPE; 
/* 194 */       if (clazz == byte.class)
/* 195 */         return BYTE_TYPE; 
/* 196 */       if (clazz == char.class)
/* 197 */         return CHAR_TYPE; 
/* 198 */       if (clazz == short.class)
/* 199 */         return SHORT_TYPE; 
/* 200 */       if (clazz == double.class)
/* 201 */         return DOUBLE_TYPE; 
/* 202 */       if (clazz == float.class)
/* 203 */         return FLOAT_TYPE; 
/* 204 */       if (clazz == long.class) {
/* 205 */         return LONG_TYPE;
/*     */       }
/* 207 */       throw new AssertionError();
/*     */     } 
/*     */     
/* 210 */     return getType(getDescriptor(clazz));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getType(Constructor<?> constructor) {
/* 221 */     return getType(getConstructorDescriptor(constructor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getType(Method method) {
/* 231 */     return getType(getMethodDescriptor(method));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getElementType() {
/* 241 */     int numDimensions = getDimensions();
/* 242 */     return getTypeInternal(this.valueBuffer, this.valueBegin + numDimensions, this.valueEnd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getObjectType(String internalName) {
/* 252 */     return new Type(
/* 253 */         (internalName.charAt(0) == '[') ? 9 : 12, internalName, 0, internalName.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getMethodType(String methodDescriptor) {
/* 264 */     return new Type(11, methodDescriptor, 0, methodDescriptor.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getMethodType(Type returnType, Type... argumentTypes) {
/* 275 */     return getType(getMethodDescriptor(returnType, argumentTypes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type[] getArgumentTypes() {
/* 285 */     return getArgumentTypes(getDescriptor());
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
/*     */   
/*     */   public static Type[] getArgumentTypes(String methodDescriptor) {
/* 298 */     int numArgumentTypes = 0;
/*     */     
/* 300 */     int currentOffset = 1;
/*     */     
/* 302 */     while (methodDescriptor.charAt(currentOffset) != ')') {
/* 303 */       while (methodDescriptor.charAt(currentOffset) == '[') {
/* 304 */         currentOffset++;
/*     */       }
/* 306 */       if (methodDescriptor.charAt(currentOffset++) == 'L') {
/*     */         
/* 308 */         int semiColumnOffset = methodDescriptor.indexOf(';', currentOffset);
/* 309 */         currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
/*     */       } 
/* 311 */       numArgumentTypes++;
/*     */     } 
/*     */ 
/*     */     
/* 315 */     Type[] argumentTypes = new Type[numArgumentTypes];
/*     */     
/* 317 */     currentOffset = 1;
/*     */     
/* 319 */     int currentArgumentTypeIndex = 0;
/* 320 */     while (methodDescriptor.charAt(currentOffset) != ')') {
/* 321 */       int currentArgumentTypeOffset = currentOffset;
/* 322 */       while (methodDescriptor.charAt(currentOffset) == '[') {
/* 323 */         currentOffset++;
/*     */       }
/* 325 */       if (methodDescriptor.charAt(currentOffset++) == 'L') {
/*     */         
/* 327 */         int semiColumnOffset = methodDescriptor.indexOf(';', currentOffset);
/* 328 */         currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
/*     */       } 
/* 330 */       argumentTypes[currentArgumentTypeIndex++] = 
/* 331 */         getTypeInternal(methodDescriptor, currentArgumentTypeOffset, currentOffset);
/*     */     } 
/* 333 */     return argumentTypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type[] getArgumentTypes(Method method) {
/* 343 */     Class<?>[] classes = method.getParameterTypes();
/* 344 */     Type[] types = new Type[classes.length];
/* 345 */     for (int i = classes.length - 1; i >= 0; i--) {
/* 346 */       types[i] = getType(classes[i]);
/*     */     }
/* 348 */     return types;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getReturnType() {
/* 358 */     return getReturnType(getDescriptor());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getReturnType(String methodDescriptor) {
/* 368 */     return getTypeInternal(methodDescriptor, 
/* 369 */         getReturnTypeOffset(methodDescriptor), methodDescriptor.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getReturnType(Method method) {
/* 379 */     return getType(method.getReturnType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getReturnTypeOffset(String methodDescriptor) {
/* 390 */     int currentOffset = 1;
/*     */     
/* 392 */     while (methodDescriptor.charAt(currentOffset) != ')') {
/* 393 */       while (methodDescriptor.charAt(currentOffset) == '[') {
/* 394 */         currentOffset++;
/*     */       }
/* 396 */       if (methodDescriptor.charAt(currentOffset++) == 'L') {
/*     */         
/* 398 */         int semiColumnOffset = methodDescriptor.indexOf(';', currentOffset);
/* 399 */         currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
/*     */       } 
/*     */     } 
/* 402 */     return currentOffset + 1;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private static Type getTypeInternal(String descriptorBuffer, int descriptorBegin, int descriptorEnd) {
/* 417 */     switch (descriptorBuffer.charAt(descriptorBegin)) {
/*     */       case 'V':
/* 419 */         return VOID_TYPE;
/*     */       case 'Z':
/* 421 */         return BOOLEAN_TYPE;
/*     */       case 'C':
/* 423 */         return CHAR_TYPE;
/*     */       case 'B':
/* 425 */         return BYTE_TYPE;
/*     */       case 'S':
/* 427 */         return SHORT_TYPE;
/*     */       case 'I':
/* 429 */         return INT_TYPE;
/*     */       case 'F':
/* 431 */         return FLOAT_TYPE;
/*     */       case 'J':
/* 433 */         return LONG_TYPE;
/*     */       case 'D':
/* 435 */         return DOUBLE_TYPE;
/*     */       case '[':
/* 437 */         return new Type(9, descriptorBuffer, descriptorBegin, descriptorEnd);
/*     */       case 'L':
/* 439 */         return new Type(10, descriptorBuffer, descriptorBegin + 1, descriptorEnd - 1);
/*     */       case '(':
/* 441 */         return new Type(11, descriptorBuffer, descriptorBegin, descriptorEnd);
/*     */     } 
/* 443 */     throw new IllegalArgumentException();
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
/*     */   
/*     */   public String getClassName() {
/*     */     StringBuilder stringBuilder;
/*     */     int i;
/* 458 */     switch (this.sort) {
/*     */       case 0:
/* 460 */         return "void";
/*     */       case 1:
/* 462 */         return "boolean";
/*     */       case 2:
/* 464 */         return "char";
/*     */       case 3:
/* 466 */         return "byte";
/*     */       case 4:
/* 468 */         return "short";
/*     */       case 5:
/* 470 */         return "int";
/*     */       case 6:
/* 472 */         return "float";
/*     */       case 7:
/* 474 */         return "long";
/*     */       case 8:
/* 476 */         return "double";
/*     */       case 9:
/* 478 */         stringBuilder = new StringBuilder(getElementType().getClassName());
/* 479 */         for (i = getDimensions(); i > 0; i--) {
/* 480 */           stringBuilder.append("[]");
/*     */         }
/* 482 */         return stringBuilder.toString();
/*     */       case 10:
/*     */       case 12:
/* 485 */         return this.valueBuffer.substring(this.valueBegin, this.valueEnd).replace('/', '.');
/*     */     } 
/* 487 */     throw new AssertionError();
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
/*     */   public String getInternalName() {
/* 499 */     return this.valueBuffer.substring(this.valueBegin, this.valueEnd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getInternalName(Class<?> clazz) {
/* 510 */     return clazz.getName().replace('.', '/');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescriptor() {
/* 519 */     if (this.sort == 10)
/* 520 */       return this.valueBuffer.substring(this.valueBegin - 1, this.valueEnd + 1); 
/* 521 */     if (this.sort == 12) {
/* 522 */       return 'L' + this.valueBuffer.substring(this.valueBegin, this.valueEnd) + ';';
/*     */     }
/* 524 */     return this.valueBuffer.substring(this.valueBegin, this.valueEnd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDescriptor(Class<?> clazz) {
/* 535 */     StringBuilder stringBuilder = new StringBuilder();
/* 536 */     appendDescriptor(clazz, stringBuilder);
/* 537 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getConstructorDescriptor(Constructor<?> constructor) {
/* 547 */     StringBuilder stringBuilder = new StringBuilder();
/* 548 */     stringBuilder.append('(');
/* 549 */     Class<?>[] parameters = constructor.getParameterTypes();
/* 550 */     for (Class<?> parameter : parameters) {
/* 551 */       appendDescriptor(parameter, stringBuilder);
/*     */     }
/* 553 */     return stringBuilder.append(")V").toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMethodDescriptor(Type returnType, Type... argumentTypes) {
/* 564 */     StringBuilder stringBuilder = new StringBuilder();
/* 565 */     stringBuilder.append('(');
/* 566 */     for (Type argumentType : argumentTypes) {
/* 567 */       argumentType.appendDescriptor(stringBuilder);
/*     */     }
/* 569 */     stringBuilder.append(')');
/* 570 */     returnType.appendDescriptor(stringBuilder);
/* 571 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMethodDescriptor(Method method) {
/* 581 */     StringBuilder stringBuilder = new StringBuilder();
/* 582 */     stringBuilder.append('(');
/* 583 */     Class<?>[] parameters = method.getParameterTypes();
/* 584 */     for (Class<?> parameter : parameters) {
/* 585 */       appendDescriptor(parameter, stringBuilder);
/*     */     }
/* 587 */     stringBuilder.append(')');
/* 588 */     appendDescriptor(method.getReturnType(), stringBuilder);
/* 589 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendDescriptor(StringBuilder stringBuilder) {
/* 598 */     if (this.sort == 10) {
/* 599 */       stringBuilder.append(this.valueBuffer, this.valueBegin - 1, this.valueEnd + 1);
/* 600 */     } else if (this.sort == 12) {
/* 601 */       stringBuilder.append('L').append(this.valueBuffer, this.valueBegin, this.valueEnd).append(';');
/*     */     } else {
/* 603 */       stringBuilder.append(this.valueBuffer, this.valueBegin, this.valueEnd);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void appendDescriptor(Class<?> clazz, StringBuilder stringBuilder) {
/* 614 */     Class<?> currentClass = clazz;
/* 615 */     while (currentClass.isArray()) {
/* 616 */       stringBuilder.append('[');
/* 617 */       currentClass = currentClass.getComponentType();
/*     */     } 
/* 619 */     if (currentClass.isPrimitive()) {
/*     */       char descriptor;
/* 621 */       if (currentClass == int.class) {
/* 622 */         descriptor = 'I';
/* 623 */       } else if (currentClass == void.class) {
/* 624 */         descriptor = 'V';
/* 625 */       } else if (currentClass == boolean.class) {
/* 626 */         descriptor = 'Z';
/* 627 */       } else if (currentClass == byte.class) {
/* 628 */         descriptor = 'B';
/* 629 */       } else if (currentClass == char.class) {
/* 630 */         descriptor = 'C';
/* 631 */       } else if (currentClass == short.class) {
/* 632 */         descriptor = 'S';
/* 633 */       } else if (currentClass == double.class) {
/* 634 */         descriptor = 'D';
/* 635 */       } else if (currentClass == float.class) {
/* 636 */         descriptor = 'F';
/* 637 */       } else if (currentClass == long.class) {
/* 638 */         descriptor = 'J';
/*     */       } else {
/* 640 */         throw new AssertionError();
/*     */       } 
/* 642 */       stringBuilder.append(descriptor);
/*     */     } else {
/* 644 */       stringBuilder.append('L').append(getInternalName(currentClass)).append(';');
/*     */     } 
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
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSort() {
/* 660 */     return (this.sort == 12) ? 10 : this.sort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDimensions() {
/* 670 */     int numDimensions = 1;
/* 671 */     while (this.valueBuffer.charAt(this.valueBegin + numDimensions) == '[') {
/* 672 */       numDimensions++;
/*     */     }
/* 674 */     return numDimensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 684 */     switch (this.sort) {
/*     */       case 0:
/* 686 */         return 0;
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 9:
/*     */       case 10:
/*     */       case 12:
/* 696 */         return 1;
/*     */       case 7:
/*     */       case 8:
/* 699 */         return 2;
/*     */     } 
/* 701 */     throw new AssertionError();
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
/*     */ 
/*     */   
/*     */   public int getArgumentsAndReturnSizes() {
/* 715 */     return getArgumentsAndReturnSizes(getDescriptor());
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
/*     */   
/*     */   public static int getArgumentsAndReturnSizes(String methodDescriptor) {
/* 728 */     int argumentsSize = 1;
/*     */     
/* 730 */     int currentOffset = 1;
/* 731 */     int currentChar = methodDescriptor.charAt(currentOffset);
/*     */     
/* 733 */     while (currentChar != 41) {
/* 734 */       if (currentChar == 74 || currentChar == 68) {
/* 735 */         currentOffset++;
/* 736 */         argumentsSize += 2;
/*     */       } else {
/* 738 */         while (methodDescriptor.charAt(currentOffset) == '[') {
/* 739 */           currentOffset++;
/*     */         }
/* 741 */         if (methodDescriptor.charAt(currentOffset++) == 'L') {
/*     */           
/* 743 */           int semiColumnOffset = methodDescriptor.indexOf(';', currentOffset);
/* 744 */           currentOffset = Math.max(currentOffset, semiColumnOffset + 1);
/*     */         } 
/* 746 */         argumentsSize++;
/*     */       } 
/* 748 */       currentChar = methodDescriptor.charAt(currentOffset);
/*     */     } 
/* 750 */     currentChar = methodDescriptor.charAt(currentOffset + 1);
/* 751 */     if (currentChar == 86) {
/* 752 */       return argumentsSize << 2;
/*     */     }
/* 754 */     int returnSize = (currentChar == 74 || currentChar == 68) ? 2 : 1;
/* 755 */     return argumentsSize << 2 | returnSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getOpcode(int opcode) {
/* 771 */     if (opcode == 46 || opcode == 79) {
/* 772 */       switch (this.sort) {
/*     */         case 1:
/*     */         case 3:
/* 775 */           return opcode + 5;
/*     */         case 2:
/* 777 */           return opcode + 6;
/*     */         case 4:
/* 779 */           return opcode + 7;
/*     */         case 5:
/* 781 */           return opcode;
/*     */         case 6:
/* 783 */           return opcode + 2;
/*     */         case 7:
/* 785 */           return opcode + 1;
/*     */         case 8:
/* 787 */           return opcode + 3;
/*     */         case 9:
/*     */         case 10:
/*     */         case 12:
/* 791 */           return opcode + 4;
/*     */         case 0:
/*     */         case 11:
/* 794 */           throw new UnsupportedOperationException();
/*     */       } 
/* 796 */       throw new AssertionError();
/*     */     } 
/*     */     
/* 799 */     switch (this.sort) {
/*     */       case 0:
/* 801 */         if (opcode != 172) {
/* 802 */           throw new UnsupportedOperationException();
/*     */         }
/* 804 */         return 177;
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 810 */         return opcode;
/*     */       case 6:
/* 812 */         return opcode + 2;
/*     */       case 7:
/* 814 */         return opcode + 1;
/*     */       case 8:
/* 816 */         return opcode + 3;
/*     */       case 9:
/*     */       case 10:
/*     */       case 12:
/* 820 */         if (opcode != 21 && opcode != 54 && opcode != 172) {
/* 821 */           throw new UnsupportedOperationException();
/*     */         }
/* 823 */         return opcode + 4;
/*     */       case 11:
/* 825 */         throw new UnsupportedOperationException();
/*     */     } 
/* 827 */     throw new AssertionError();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 844 */     if (this == object) {
/* 845 */       return true;
/*     */     }
/* 847 */     if (!(object instanceof Type)) {
/* 848 */       return false;
/*     */     }
/* 850 */     Type other = (Type)object;
/* 851 */     if (((this.sort == 12) ? true : this.sort) != ((other.sort == 12) ? true : other.sort)) {
/* 852 */       return false;
/*     */     }
/* 854 */     int begin = this.valueBegin;
/* 855 */     int end = this.valueEnd;
/* 856 */     int otherBegin = other.valueBegin;
/* 857 */     int otherEnd = other.valueEnd;
/*     */     
/* 859 */     if (end - begin != otherEnd - otherBegin) {
/* 860 */       return false;
/*     */     }
/* 862 */     for (int i = begin, j = otherBegin; i < end; i++, j++) {
/* 863 */       if (this.valueBuffer.charAt(i) != other.valueBuffer.charAt(j)) {
/* 864 */         return false;
/*     */       }
/*     */     } 
/* 867 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 877 */     int hashCode = 13 * ((this.sort == 12) ? 10 : this.sort);
/* 878 */     if (this.sort >= 9) {
/* 879 */       for (int i = this.valueBegin, end = this.valueEnd; i < end; i++) {
/* 880 */         hashCode = 17 * (hashCode + this.valueBuffer.charAt(i));
/*     */       }
/*     */     }
/* 883 */     return hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 893 */     return getDescriptor();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Type.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */