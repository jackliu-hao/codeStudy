/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.BCIRenumberedAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionTableEntry;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
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
/*     */ public class BcBands
/*     */   extends BandSet
/*     */ {
/*     */   private byte[][][] methodByteCodePacked;
/*     */   private int[] bcCaseCount;
/*     */   private int[] bcCaseValue;
/*     */   private int[] bcByte;
/*     */   private int[] bcLocal;
/*     */   private int[] bcShort;
/*     */   private int[] bcLabel;
/*     */   private int[] bcIntRef;
/*     */   private int[] bcFloatRef;
/*     */   private int[] bcLongRef;
/*     */   private int[] bcDoubleRef;
/*     */   private int[] bcStringRef;
/*     */   private int[] bcClassRef;
/*     */   private int[] bcFieldRef;
/*     */   private int[] bcMethodRef;
/*     */   private int[] bcIMethodRef;
/*     */   private int[] bcThisField;
/*     */   private int[] bcSuperField;
/*     */   private int[] bcThisMethod;
/*     */   private int[] bcSuperMethod;
/*     */   private int[] bcInitRef;
/*     */   private int[] bcEscRef;
/*     */   private int[] bcEscRefSize;
/*     */   private int[] bcEscSize;
/*     */   private int[][] bcEscByte;
/*     */   private List wideByteCodes;
/*     */   
/*     */   public BcBands(Segment segment) {
/*  79 */     super(segment);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception {
/*  90 */     AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
/*  91 */     int classCount = this.header.getClassCount();
/*  92 */     long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
/*     */     
/*  94 */     int bcCaseCountCount = 0;
/*  95 */     int bcByteCount = 0;
/*  96 */     int bcShortCount = 0;
/*  97 */     int bcLocalCount = 0;
/*  98 */     int bcLabelCount = 0;
/*  99 */     int bcIntRefCount = 0;
/* 100 */     int bcFloatRefCount = 0;
/* 101 */     int bcLongRefCount = 0;
/* 102 */     int bcDoubleRefCount = 0;
/* 103 */     int bcStringRefCount = 0;
/* 104 */     int bcClassRefCount = 0;
/* 105 */     int bcFieldRefCount = 0;
/* 106 */     int bcMethodRefCount = 0;
/* 107 */     int bcIMethodRefCount = 0;
/* 108 */     int bcThisFieldCount = 0;
/* 109 */     int bcSuperFieldCount = 0;
/* 110 */     int bcThisMethodCount = 0;
/* 111 */     int bcSuperMethodCount = 0;
/* 112 */     int bcInitRefCount = 0;
/* 113 */     int bcEscCount = 0;
/* 114 */     int bcEscRefCount = 0;
/*     */     
/* 116 */     AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
/*     */     
/* 118 */     AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
/*     */ 
/*     */     
/* 121 */     this.methodByteCodePacked = new byte[classCount][][];
/* 122 */     int bcParsed = 0;
/*     */     
/* 124 */     List<Boolean> switchIsTableSwitch = new ArrayList();
/* 125 */     this.wideByteCodes = new ArrayList();
/* 126 */     for (int c = 0; c < classCount; c++) {
/* 127 */       int numberOfMethods = (methodFlags[c]).length;
/* 128 */       this.methodByteCodePacked[c] = new byte[numberOfMethods][];
/* 129 */       for (int m = 0; m < numberOfMethods; m++) {
/* 130 */         long methodFlag = methodFlags[c][m];
/* 131 */         if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
/* 132 */           ByteArrayOutputStream codeBytes = new ByteArrayOutputStream();
/*     */           byte code;
/* 134 */           while ((code = (byte)(0xFF & in.read())) != -1) {
/* 135 */             codeBytes.write(code);
/*     */           }
/* 137 */           this.methodByteCodePacked[c][m] = codeBytes.toByteArray();
/* 138 */           bcParsed += (this.methodByteCodePacked[c][m]).length;
/* 139 */           int[] codes = new int[(this.methodByteCodePacked[c][m]).length]; int j;
/* 140 */           for (j = 0; j < codes.length; j++) {
/* 141 */             codes[j] = this.methodByteCodePacked[c][m][j] & 0xFF;
/*     */           }
/* 143 */           for (j = 0; j < (this.methodByteCodePacked[c][m]).length; j++) {
/* 144 */             int nextInstruction, codePacked = 0xFF & this.methodByteCodePacked[c][m][j];
/* 145 */             switch (codePacked) {
/*     */               case 16:
/*     */               case 188:
/* 148 */                 bcByteCount++;
/*     */                 break;
/*     */               case 17:
/* 151 */                 bcShortCount++;
/*     */                 break;
/*     */               case 18:
/*     */               case 19:
/* 155 */                 bcStringRefCount++;
/*     */                 break;
/*     */               case 234:
/*     */               case 237:
/* 159 */                 bcIntRefCount++;
/*     */                 break;
/*     */               case 235:
/*     */               case 238:
/* 163 */                 bcFloatRefCount++;
/*     */                 break;
/*     */               case 197:
/* 166 */                 bcByteCount++;
/*     */               
/*     */               case 187:
/*     */               case 189:
/*     */               case 192:
/*     */               case 193:
/*     */               case 233:
/*     */               case 236:
/* 174 */                 bcClassRefCount++;
/*     */                 break;
/*     */               case 20:
/* 177 */                 bcLongRefCount++;
/*     */                 break;
/*     */               case 239:
/* 180 */                 bcDoubleRefCount++;
/*     */                 break;
/*     */               case 169:
/* 183 */                 bcLocalCount++;
/*     */                 break;
/*     */               case 167:
/*     */               case 168:
/*     */               case 200:
/*     */               case 201:
/* 189 */                 bcLabelCount++;
/*     */                 break;
/*     */               case 170:
/* 192 */                 switchIsTableSwitch.add(Boolean.valueOf(true));
/* 193 */                 bcCaseCountCount++;
/* 194 */                 bcLabelCount++;
/*     */                 break;
/*     */               case 171:
/* 197 */                 switchIsTableSwitch.add(Boolean.valueOf(false));
/* 198 */                 bcCaseCountCount++;
/* 199 */                 bcLabelCount++;
/*     */                 break;
/*     */               case 178:
/*     */               case 179:
/*     */               case 180:
/*     */               case 181:
/* 205 */                 bcFieldRefCount++;
/*     */                 break;
/*     */               case 182:
/*     */               case 183:
/*     */               case 184:
/* 210 */                 bcMethodRefCount++;
/*     */                 break;
/*     */               case 185:
/* 213 */                 bcIMethodRefCount++;
/*     */                 break;
/*     */               case 202:
/*     */               case 203:
/*     */               case 204:
/*     */               case 205:
/*     */               case 209:
/*     */               case 210:
/*     */               case 211:
/*     */               case 212:
/* 223 */                 bcThisFieldCount++;
/*     */                 break;
/*     */               case 206:
/*     */               case 207:
/*     */               case 208:
/*     */               case 213:
/*     */               case 214:
/*     */               case 215:
/* 231 */                 bcThisMethodCount++;
/*     */                 break;
/*     */               case 216:
/*     */               case 217:
/*     */               case 218:
/*     */               case 219:
/*     */               case 223:
/*     */               case 224:
/*     */               case 225:
/*     */               case 226:
/* 241 */                 bcSuperFieldCount++;
/*     */                 break;
/*     */               case 220:
/*     */               case 221:
/*     */               case 222:
/*     */               case 227:
/*     */               case 228:
/*     */               case 229:
/* 249 */                 bcSuperMethodCount++;
/*     */                 break;
/*     */               case 132:
/* 252 */                 bcLocalCount++;
/* 253 */                 bcByteCount++;
/*     */                 break;
/*     */               case 196:
/* 256 */                 nextInstruction = 0xFF & this.methodByteCodePacked[c][m][j + 1];
/* 257 */                 this.wideByteCodes.add(Integer.valueOf(nextInstruction));
/* 258 */                 if (nextInstruction == 132) {
/* 259 */                   bcLocalCount++;
/* 260 */                   bcShortCount++;
/* 261 */                 } else if (endsWithLoad(nextInstruction) || endsWithStore(nextInstruction) || nextInstruction == 169) {
/*     */                   
/* 263 */                   bcLocalCount++;
/*     */                 } else {
/* 265 */                   this.segment.log(2, "Found unhandled " + 
/* 266 */                       ByteCode.getByteCode(nextInstruction));
/*     */                 } 
/* 268 */                 j++;
/*     */                 break;
/*     */               case 230:
/*     */               case 231:
/*     */               case 232:
/* 273 */                 bcInitRefCount++;
/*     */                 break;
/*     */               case 253:
/* 276 */                 bcEscRefCount++;
/*     */                 break;
/*     */               case 254:
/* 279 */                 bcEscCount++;
/*     */                 break;
/*     */               default:
/* 282 */                 if (endsWithLoad(codePacked) || endsWithStore(codePacked)) {
/* 283 */                   bcLocalCount++; break;
/* 284 */                 }  if (startsWithIf(codePacked)) {
/* 285 */                   bcLabelCount++;
/*     */                 }
/*     */                 break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 293 */     this.bcCaseCount = decodeBandInt("bc_case_count", in, Codec.UNSIGNED5, bcCaseCountCount);
/* 294 */     int bcCaseValueCount = 0;
/* 295 */     for (int i = 0; i < this.bcCaseCount.length; i++) {
/* 296 */       boolean isTableSwitch = ((Boolean)switchIsTableSwitch.get(i)).booleanValue();
/* 297 */       if (isTableSwitch) {
/* 298 */         bcCaseValueCount++;
/*     */       } else {
/* 300 */         bcCaseValueCount += this.bcCaseCount[i];
/*     */       } 
/*     */     } 
/* 303 */     this.bcCaseValue = decodeBandInt("bc_case_value", in, Codec.DELTA5, bcCaseValueCount);
/*     */ 
/*     */ 
/*     */     
/* 307 */     for (int index = 0; index < bcCaseCountCount; index++) {
/* 308 */       bcLabelCount += this.bcCaseCount[index];
/*     */     }
/* 310 */     this.bcByte = decodeBandInt("bc_byte", in, Codec.BYTE1, bcByteCount);
/* 311 */     this.bcShort = decodeBandInt("bc_short", in, Codec.DELTA5, bcShortCount);
/* 312 */     this.bcLocal = decodeBandInt("bc_local", in, Codec.UNSIGNED5, bcLocalCount);
/* 313 */     this.bcLabel = decodeBandInt("bc_label", in, Codec.BRANCH5, bcLabelCount);
/* 314 */     this.bcIntRef = decodeBandInt("bc_intref", in, Codec.DELTA5, bcIntRefCount);
/* 315 */     this.bcFloatRef = decodeBandInt("bc_floatref", in, Codec.DELTA5, bcFloatRefCount);
/* 316 */     this.bcLongRef = decodeBandInt("bc_longref", in, Codec.DELTA5, bcLongRefCount);
/* 317 */     this.bcDoubleRef = decodeBandInt("bc_doubleref", in, Codec.DELTA5, bcDoubleRefCount);
/* 318 */     this.bcStringRef = decodeBandInt("bc_stringref", in, Codec.DELTA5, bcStringRefCount);
/* 319 */     this.bcClassRef = decodeBandInt("bc_classref", in, Codec.UNSIGNED5, bcClassRefCount);
/* 320 */     this.bcFieldRef = decodeBandInt("bc_fieldref", in, Codec.DELTA5, bcFieldRefCount);
/* 321 */     this.bcMethodRef = decodeBandInt("bc_methodref", in, Codec.UNSIGNED5, bcMethodRefCount);
/* 322 */     this.bcIMethodRef = decodeBandInt("bc_imethodref", in, Codec.DELTA5, bcIMethodRefCount);
/* 323 */     this.bcThisField = decodeBandInt("bc_thisfield", in, Codec.UNSIGNED5, bcThisFieldCount);
/* 324 */     this.bcSuperField = decodeBandInt("bc_superfield", in, Codec.UNSIGNED5, bcSuperFieldCount);
/* 325 */     this.bcThisMethod = decodeBandInt("bc_thismethod", in, Codec.UNSIGNED5, bcThisMethodCount);
/* 326 */     this.bcSuperMethod = decodeBandInt("bc_supermethod", in, Codec.UNSIGNED5, bcSuperMethodCount);
/* 327 */     this.bcInitRef = decodeBandInt("bc_initref", in, Codec.UNSIGNED5, bcInitRefCount);
/* 328 */     this.bcEscRef = decodeBandInt("bc_escref", in, Codec.UNSIGNED5, bcEscRefCount);
/* 329 */     this.bcEscRefSize = decodeBandInt("bc_escrefsize", in, Codec.UNSIGNED5, bcEscRefCount);
/* 330 */     this.bcEscSize = decodeBandInt("bc_escsize", in, Codec.UNSIGNED5, bcEscCount);
/* 331 */     this.bcEscByte = decodeBandInt("bc_escbyte", in, Codec.BYTE1, this.bcEscSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void unpack() throws Pack200Exception {
/* 336 */     int classCount = this.header.getClassCount();
/* 337 */     long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
/* 338 */     int[] codeMaxNALocals = this.segment.getClassBands().getCodeMaxNALocals();
/* 339 */     int[] codeMaxStack = this.segment.getClassBands().getCodeMaxStack();
/* 340 */     ArrayList[][] methodAttributes = this.segment.getClassBands().getMethodAttributes();
/* 341 */     String[][] methodDescr = this.segment.getClassBands().getMethodDescr();
/*     */     
/* 343 */     AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
/*     */     
/* 345 */     AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
/*     */     
/* 347 */     AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
/*     */     
/* 349 */     AttributeLayout staticModifier = attributeDefinitionMap.getAttributeLayout("ACC_STATIC", 2);
/*     */ 
/*     */     
/* 352 */     int[] wideByteCodeArray = new int[this.wideByteCodes.size()];
/* 353 */     for (int index = 0; index < wideByteCodeArray.length; index++) {
/* 354 */       wideByteCodeArray[index] = ((Integer)this.wideByteCodes.get(index)).intValue();
/*     */     }
/* 356 */     OperandManager operandManager = new OperandManager(this.bcCaseCount, this.bcCaseValue, this.bcByte, this.bcShort, this.bcLocal, this.bcLabel, this.bcIntRef, this.bcFloatRef, this.bcLongRef, this.bcDoubleRef, this.bcStringRef, this.bcClassRef, this.bcFieldRef, this.bcMethodRef, this.bcIMethodRef, this.bcThisField, this.bcSuperField, this.bcThisMethod, this.bcSuperMethod, this.bcInitRef, wideByteCodeArray);
/*     */ 
/*     */     
/* 359 */     operandManager.setSegment(this.segment);
/*     */     
/* 361 */     int i = 0;
/* 362 */     ArrayList<List> orderedCodeAttributes = this.segment.getClassBands().getOrderedCodeAttributes();
/* 363 */     int codeAttributeIndex = 0;
/*     */ 
/*     */     
/* 366 */     int[] handlerCount = this.segment.getClassBands().getCodeHandlerCount();
/* 367 */     int[][] handlerStartPCs = this.segment.getClassBands().getCodeHandlerStartP();
/* 368 */     int[][] handlerEndPCs = this.segment.getClassBands().getCodeHandlerEndPO();
/* 369 */     int[][] handlerCatchPCs = this.segment.getClassBands().getCodeHandlerCatchPO();
/* 370 */     int[][] handlerClassTypes = this.segment.getClassBands().getCodeHandlerClassRCN();
/*     */     
/* 372 */     boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
/* 373 */     boolean[] codeHasFlags = this.segment.getClassBands().getCodeHasAttributes();
/*     */     
/* 375 */     for (int c = 0; c < classCount; c++) {
/* 376 */       int numberOfMethods = (methodFlags[c]).length;
/* 377 */       for (int m = 0; m < numberOfMethods; m++) {
/* 378 */         long methodFlag = methodFlags[c][m];
/* 379 */         if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
/* 380 */           List<Attribute> currentAttributes; int maxStack = codeMaxStack[i];
/* 381 */           int maxLocal = codeMaxNALocals[i];
/* 382 */           if (!staticModifier.matches(methodFlag)) {
/* 383 */             maxLocal++;
/*     */           }
/*     */           
/* 386 */           maxLocal += SegmentUtils.countInvokeInterfaceArgs(methodDescr[c][m]);
/* 387 */           String[] cpClass = this.segment.getCpBands().getCpClass();
/* 388 */           operandManager.setCurrentClass(cpClass[this.segment.getClassBands().getClassThisInts()[c]]);
/* 389 */           operandManager.setSuperClass(cpClass[this.segment.getClassBands().getClassSuperInts()[c]]);
/* 390 */           List<ExceptionTableEntry> exceptionTable = new ArrayList();
/* 391 */           if (handlerCount != null) {
/* 392 */             for (int n = 0; n < handlerCount[i]; n++) {
/* 393 */               int handlerClass = handlerClassTypes[i][n] - 1;
/* 394 */               CPClass cpHandlerClass = null;
/* 395 */               if (handlerClass != -1)
/*     */               {
/*     */ 
/*     */                 
/* 399 */                 cpHandlerClass = this.segment.getCpBands().cpClassValue(handlerClass);
/*     */               }
/* 401 */               ExceptionTableEntry entry = new ExceptionTableEntry(handlerStartPCs[i][n], handlerEndPCs[i][n], handlerCatchPCs[i][n], cpHandlerClass);
/*     */               
/* 403 */               exceptionTable.add(entry);
/*     */             } 
/*     */           }
/* 406 */           CodeAttribute codeAttr = new CodeAttribute(maxStack, maxLocal, this.methodByteCodePacked[c][m], this.segment, operandManager, exceptionTable);
/*     */           
/* 408 */           ArrayList<Attribute> methodAttributesList = methodAttributes[c][m];
/*     */           
/* 410 */           int indexForCodeAttr = 0;
/* 411 */           for (int j = 0; j < methodAttributesList.size(); j++) {
/* 412 */             Attribute attribute = methodAttributesList.get(j);
/* 413 */             if (!(attribute instanceof NewAttribute) || ((NewAttribute)attribute)
/* 414 */               .getLayoutIndex() >= 15) {
/*     */               break;
/*     */             }
/* 417 */             indexForCodeAttr++;
/*     */           } 
/* 419 */           methodAttributesList.add(indexForCodeAttr, codeAttr);
/* 420 */           codeAttr.renumber(codeAttr.byteCodeOffsets);
/*     */           
/* 422 */           if (allCodeHasFlags) {
/* 423 */             currentAttributes = orderedCodeAttributes.get(i);
/* 424 */           } else if (codeHasFlags[i]) {
/* 425 */             currentAttributes = orderedCodeAttributes.get(codeAttributeIndex);
/* 426 */             codeAttributeIndex++;
/*     */           } else {
/* 428 */             currentAttributes = Collections.EMPTY_LIST;
/*     */           } 
/* 430 */           for (int k = 0; k < currentAttributes.size(); k++) {
/* 431 */             Attribute currentAttribute = currentAttributes.get(k);
/* 432 */             codeAttr.addAttribute(currentAttribute);
/*     */             
/* 434 */             if (currentAttribute.hasBCIRenumbering()) {
/* 435 */               ((BCIRenumberedAttribute)currentAttribute).renumber(codeAttr.byteCodeOffsets);
/*     */             }
/*     */           } 
/* 438 */           i++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean startsWithIf(int codePacked) {
/* 445 */     return ((codePacked >= 153 && codePacked <= 166) || codePacked == 198 || codePacked == 199);
/*     */   }
/*     */   
/*     */   private boolean endsWithLoad(int codePacked) {
/* 449 */     return (codePacked >= 21 && codePacked <= 25);
/*     */   }
/*     */   
/*     */   private boolean endsWithStore(int codePacked) {
/* 453 */     return (codePacked >= 54 && codePacked <= 58);
/*     */   }
/*     */   
/*     */   public byte[][][] getMethodByteCodePacked() {
/* 457 */     return this.methodByteCodePacked;
/*     */   }
/*     */   
/*     */   public int[] getBcCaseCount() {
/* 461 */     return this.bcCaseCount;
/*     */   }
/*     */   
/*     */   public int[] getBcCaseValue() {
/* 465 */     return this.bcCaseValue;
/*     */   }
/*     */   
/*     */   public int[] getBcByte() {
/* 469 */     return this.bcByte;
/*     */   }
/*     */   
/*     */   public int[] getBcClassRef() {
/* 473 */     return this.bcClassRef;
/*     */   }
/*     */   
/*     */   public int[] getBcDoubleRef() {
/* 477 */     return this.bcDoubleRef;
/*     */   }
/*     */   
/*     */   public int[] getBcFieldRef() {
/* 481 */     return this.bcFieldRef;
/*     */   }
/*     */   
/*     */   public int[] getBcFloatRef() {
/* 485 */     return this.bcFloatRef;
/*     */   }
/*     */   
/*     */   public int[] getBcIMethodRef() {
/* 489 */     return this.bcIMethodRef;
/*     */   }
/*     */   
/*     */   public int[] getBcInitRef() {
/* 493 */     return this.bcInitRef;
/*     */   }
/*     */   
/*     */   public int[] getBcIntRef() {
/* 497 */     return this.bcIntRef;
/*     */   }
/*     */   
/*     */   public int[] getBcLabel() {
/* 501 */     return this.bcLabel;
/*     */   }
/*     */   
/*     */   public int[] getBcLocal() {
/* 505 */     return this.bcLocal;
/*     */   }
/*     */   
/*     */   public int[] getBcLongRef() {
/* 509 */     return this.bcLongRef;
/*     */   }
/*     */   
/*     */   public int[] getBcMethodRef() {
/* 513 */     return this.bcMethodRef;
/*     */   }
/*     */   
/*     */   public int[] getBcShort() {
/* 517 */     return this.bcShort;
/*     */   }
/*     */   
/*     */   public int[] getBcStringRef() {
/* 521 */     return this.bcStringRef;
/*     */   }
/*     */   
/*     */   public int[] getBcSuperField() {
/* 525 */     return this.bcSuperField;
/*     */   }
/*     */   
/*     */   public int[] getBcSuperMethod() {
/* 529 */     return this.bcSuperMethod;
/*     */   }
/*     */   
/*     */   public int[] getBcThisField() {
/* 533 */     return this.bcThisField;
/*     */   }
/*     */   
/*     */   public int[] getBcThisMethod() {
/* 537 */     return this.bcThisMethod;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\BcBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */