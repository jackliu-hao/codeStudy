/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.objectweb.asm.Label;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BcBands
/*     */   extends BandSet
/*     */ {
/*     */   private final CpBands cpBands;
/*     */   private final Segment segment;
/*     */   private final IntList bcCodes;
/*     */   private final IntList bcCaseCount;
/*     */   private final IntList bcCaseValue;
/*     */   private final IntList bcByte;
/*     */   private final IntList bcShort;
/*     */   private final IntList bcLocal;
/*     */   private final List bcLabel;
/*     */   private final List bcIntref;
/*     */   private final List bcFloatRef;
/*     */   private final List bcLongRef;
/*     */   private final List bcDoubleRef;
/*     */   private final List bcStringRef;
/*     */   private final List bcClassRef;
/*     */   private final List bcFieldRef;
/*     */   private final List bcMethodRef;
/*     */   private final List bcIMethodRef;
/*     */   private List bcThisField;
/*     */   
/*     */   public BcBands(CpBands cpBands, Segment segment, int effort) {
/*  37 */     super(effort, segment.getSegmentHeader());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  42 */     this.bcCodes = new IntList();
/*  43 */     this.bcCaseCount = new IntList();
/*  44 */     this.bcCaseValue = new IntList();
/*  45 */     this.bcByte = new IntList();
/*  46 */     this.bcShort = new IntList();
/*  47 */     this.bcLocal = new IntList();
/*  48 */     this.bcLabel = new ArrayList();
/*  49 */     this.bcIntref = new ArrayList();
/*  50 */     this.bcFloatRef = new ArrayList();
/*  51 */     this.bcLongRef = new ArrayList();
/*  52 */     this.bcDoubleRef = new ArrayList();
/*  53 */     this.bcStringRef = new ArrayList();
/*  54 */     this.bcClassRef = new ArrayList();
/*  55 */     this.bcFieldRef = new ArrayList();
/*  56 */     this.bcMethodRef = new ArrayList();
/*  57 */     this.bcIMethodRef = new ArrayList();
/*  58 */     this.bcThisField = new ArrayList();
/*  59 */     this.bcSuperField = new ArrayList();
/*  60 */     this.bcThisMethod = new ArrayList();
/*  61 */     this.bcSuperMethod = new ArrayList();
/*  62 */     this.bcInitRef = new ArrayList();
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
/*  77 */     this.bciRenumbering = new IntList();
/*  78 */     this.labelsToOffsets = new HashMap<>();
/*     */ 
/*     */     
/*  81 */     this.bcLabelRelativeOffsets = new IntList();
/*     */     this.cpBands = cpBands;
/*     */     this.segment = segment; } private final List bcSuperField; private List bcThisMethod; private List bcSuperMethod; private List bcInitRef; private String currentClass; private String superClass; private String currentNewClass; private static final int MULTIANEWARRAY = 197; private static final int ALOAD_0 = 42; private static final int WIDE = 196; private static final int INVOKEINTERFACE = 185; private static final int TABLESWITCH = 170; private static final int IINC = 132; private static final int LOOKUPSWITCH = 171; private static final int endMarker = 255; private final IntList bciRenumbering; private final Map labelsToOffsets; private int byteCodeOffset; private int renumberedOffset; private final IntList bcLabelRelativeOffsets; public void setCurrentClass(String name, String superName) {
/*  84 */     this.currentClass = name;
/*  85 */     this.superClass = superName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finaliseBands() {
/*  93 */     this.bcThisField = getIndexInClass(this.bcThisField);
/*  94 */     this.bcThisMethod = getIndexInClass(this.bcThisMethod);
/*  95 */     this.bcSuperMethod = getIndexInClass(this.bcSuperMethod);
/*  96 */     this.bcInitRef = getIndexInClassForConstructor(this.bcInitRef);
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 101 */     PackingUtils.log("Writing byte code bands...");
/* 102 */     byte[] encodedBand = encodeBandInt("bcCodes", this.bcCodes.toArray(), Codec.BYTE1);
/* 103 */     out.write(encodedBand);
/* 104 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCodes[" + this.bcCodes.size() + "]");
/*     */     
/* 106 */     encodedBand = encodeBandInt("bcCaseCount", this.bcCaseCount.toArray(), Codec.UNSIGNED5);
/* 107 */     out.write(encodedBand);
/* 108 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseCount[" + this.bcCaseCount.size() + "]");
/*     */     
/* 110 */     encodedBand = encodeBandInt("bcCaseValue", this.bcCaseValue.toArray(), Codec.DELTA5);
/* 111 */     out.write(encodedBand);
/* 112 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseValue[" + this.bcCaseValue.size() + "]");
/*     */     
/* 114 */     encodedBand = encodeBandInt("bcByte", this.bcByte.toArray(), Codec.BYTE1);
/* 115 */     out.write(encodedBand);
/* 116 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcByte[" + this.bcByte.size() + "]");
/*     */     
/* 118 */     encodedBand = encodeBandInt("bcShort", this.bcShort.toArray(), Codec.DELTA5);
/* 119 */     out.write(encodedBand);
/* 120 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcShort[" + this.bcShort.size() + "]");
/*     */     
/* 122 */     encodedBand = encodeBandInt("bcLocal", this.bcLocal.toArray(), Codec.UNSIGNED5);
/* 123 */     out.write(encodedBand);
/* 124 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLocal[" + this.bcLocal.size() + "]");
/*     */     
/* 126 */     encodedBand = encodeBandInt("bcLabel", integerListToArray(this.bcLabel), Codec.BRANCH5);
/* 127 */     out.write(encodedBand);
/* 128 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLabel[" + this.bcLabel.size() + "]");
/*     */     
/* 130 */     encodedBand = encodeBandInt("bcIntref", cpEntryListToArray(this.bcIntref), Codec.DELTA5);
/* 131 */     out.write(encodedBand);
/* 132 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIntref[" + this.bcIntref.size() + "]");
/*     */     
/* 134 */     encodedBand = encodeBandInt("bcFloatRef", cpEntryListToArray(this.bcFloatRef), Codec.DELTA5);
/* 135 */     out.write(encodedBand);
/* 136 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFloatRef[" + this.bcFloatRef.size() + "]");
/*     */     
/* 138 */     encodedBand = encodeBandInt("bcLongRef", cpEntryListToArray(this.bcLongRef), Codec.DELTA5);
/* 139 */     out.write(encodedBand);
/* 140 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLongRef[" + this.bcLongRef.size() + "]");
/*     */     
/* 142 */     encodedBand = encodeBandInt("bcDoubleRef", cpEntryListToArray(this.bcDoubleRef), Codec.DELTA5);
/* 143 */     out.write(encodedBand);
/* 144 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcDoubleRef[" + this.bcDoubleRef.size() + "]");
/*     */     
/* 146 */     encodedBand = encodeBandInt("bcStringRef", cpEntryListToArray(this.bcStringRef), Codec.DELTA5);
/* 147 */     out.write(encodedBand);
/* 148 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcStringRef[" + this.bcStringRef.size() + "]");
/*     */     
/* 150 */     encodedBand = encodeBandInt("bcClassRef", cpEntryOrNullListToArray(this.bcClassRef), Codec.UNSIGNED5);
/* 151 */     out.write(encodedBand);
/* 152 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcClassRef[" + this.bcClassRef.size() + "]");
/*     */     
/* 154 */     encodedBand = encodeBandInt("bcFieldRef", cpEntryListToArray(this.bcFieldRef), Codec.DELTA5);
/* 155 */     out.write(encodedBand);
/* 156 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFieldRef[" + this.bcFieldRef.size() + "]");
/*     */     
/* 158 */     encodedBand = encodeBandInt("bcMethodRef", cpEntryListToArray(this.bcMethodRef), Codec.UNSIGNED5);
/* 159 */     out.write(encodedBand);
/* 160 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcMethodRef[" + this.bcMethodRef.size() + "]");
/*     */     
/* 162 */     encodedBand = encodeBandInt("bcIMethodRef", cpEntryListToArray(this.bcIMethodRef), Codec.DELTA5);
/* 163 */     out.write(encodedBand);
/* 164 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIMethodRef[" + this.bcIMethodRef.size() + "]");
/*     */     
/* 166 */     encodedBand = encodeBandInt("bcThisField", integerListToArray(this.bcThisField), Codec.UNSIGNED5);
/* 167 */     out.write(encodedBand);
/* 168 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisField[" + this.bcThisField.size() + "]");
/*     */     
/* 170 */     encodedBand = encodeBandInt("bcSuperField", integerListToArray(this.bcSuperField), Codec.UNSIGNED5);
/* 171 */     out.write(encodedBand);
/* 172 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperField[" + this.bcSuperField.size() + "]");
/*     */     
/* 174 */     encodedBand = encodeBandInt("bcThisMethod", integerListToArray(this.bcThisMethod), Codec.UNSIGNED5);
/* 175 */     out.write(encodedBand);
/* 176 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisMethod[" + this.bcThisMethod.size() + "]");
/*     */     
/* 178 */     encodedBand = encodeBandInt("bcSuperMethod", integerListToArray(this.bcSuperMethod), Codec.UNSIGNED5);
/* 179 */     out.write(encodedBand);
/* 180 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperMethod[" + this.bcSuperMethod.size() + "]");
/*     */     
/* 182 */     encodedBand = encodeBandInt("bcInitRef", integerListToArray(this.bcInitRef), Codec.UNSIGNED5);
/* 183 */     out.write(encodedBand);
/* 184 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcInitRef[" + this.bcInitRef.size() + "]");
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
/*     */   private List getIndexInClass(List<CPMethodOrField> cPMethodOrFieldList) {
/* 196 */     List<Integer> indices = new ArrayList(cPMethodOrFieldList.size());
/* 197 */     for (int i = 0; i < cPMethodOrFieldList.size(); i++) {
/* 198 */       CPMethodOrField cpMF = cPMethodOrFieldList.get(i);
/* 199 */       indices.add(Integer.valueOf(cpMF.getIndexInClass()));
/*     */     } 
/* 201 */     return indices;
/*     */   }
/*     */   
/*     */   private List getIndexInClassForConstructor(List<CPMethodOrField> cPMethodList) {
/* 205 */     List<Integer> indices = new ArrayList(cPMethodList.size());
/* 206 */     for (int i = 0; i < cPMethodList.size(); i++) {
/* 207 */       CPMethodOrField cpMF = cPMethodList.get(i);
/* 208 */       indices.add(Integer.valueOf(cpMF.getIndexInClassForConstructor()));
/*     */     } 
/* 210 */     return indices;
/*     */   }
/*     */   public void visitEnd() {
/*     */     int i;
/* 214 */     for (i = 0; i < this.bciRenumbering.size(); i++) {
/* 215 */       if (this.bciRenumbering.get(i) == -1) {
/* 216 */         this.bciRenumbering.remove(i);
/* 217 */         this.bciRenumbering.add(i, ++this.renumberedOffset);
/*     */       } 
/*     */     } 
/* 220 */     if (this.renumberedOffset != 0) {
/* 221 */       if (this.renumberedOffset + 1 != this.bciRenumbering.size()) {
/* 222 */         throw new RuntimeException("Mistake made with renumbering");
/*     */       }
/* 224 */       for (i = this.bcLabel.size() - 1; i >= 0; i--) {
/* 225 */         Object label = this.bcLabel.get(i);
/* 226 */         if (label instanceof Integer) {
/*     */           break;
/*     */         }
/* 229 */         if (label instanceof Label) {
/* 230 */           this.bcLabel.remove(i);
/* 231 */           Integer offset = (Integer)this.labelsToOffsets.get(label);
/* 232 */           int relativeOffset = this.bcLabelRelativeOffsets.get(i);
/* 233 */           this.bcLabel.add(i, 
/* 234 */               Integer.valueOf(this.bciRenumbering.get(offset.intValue()) - this.bciRenumbering.get(relativeOffset)));
/*     */         } 
/*     */       } 
/* 237 */       this.bcCodes.add(255);
/* 238 */       this.segment.getClassBands().doBciRenumbering(this.bciRenumbering, this.labelsToOffsets);
/* 239 */       this.bciRenumbering.clear();
/* 240 */       this.labelsToOffsets.clear();
/* 241 */       this.byteCodeOffset = 0;
/* 242 */       this.renumberedOffset = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void visitLabel(Label label) {
/* 247 */     this.labelsToOffsets.put(label, Integer.valueOf(this.byteCodeOffset));
/*     */   }
/*     */   
/*     */   public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/* 251 */     this.byteCodeOffset += 3;
/* 252 */     updateRenumbering();
/* 253 */     boolean aload_0 = false;
/* 254 */     if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
/* 255 */       this.bcCodes.remove(this.bcCodes.size() - 1);
/* 256 */       aload_0 = true;
/*     */     } 
/* 258 */     CPMethodOrField cpField = this.cpBands.getCPField(owner, name, desc);
/* 259 */     if (aload_0) {
/* 260 */       opcode += 7;
/*     */     }
/* 262 */     if (owner.equals(this.currentClass)) {
/* 263 */       opcode += 24;
/* 264 */       this.bcThisField.add(cpField);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 269 */       if (aload_0) {
/* 270 */         opcode -= 7;
/* 271 */         this.bcCodes.add(42);
/*     */       } 
/*     */ 
/*     */       
/* 275 */       this.bcFieldRef.add(cpField);
/*     */     } 
/* 277 */     aload_0 = false;
/* 278 */     this.bcCodes.add(opcode);
/*     */   }
/*     */   
/*     */   private void updateRenumbering() {
/* 282 */     if (this.bciRenumbering.isEmpty()) {
/* 283 */       this.bciRenumbering.add(0);
/*     */     }
/* 285 */     this.renumberedOffset++;
/* 286 */     for (int i = this.bciRenumbering.size(); i < this.byteCodeOffset; i++) {
/* 287 */       this.bciRenumbering.add(-1);
/*     */     }
/* 289 */     this.bciRenumbering.add(this.renumberedOffset);
/*     */   }
/*     */   
/*     */   public void visitIincInsn(int var, int increment) {
/* 293 */     if (var > 255 || increment > 255) {
/* 294 */       this.byteCodeOffset += 6;
/* 295 */       this.bcCodes.add(196);
/* 296 */       this.bcCodes.add(132);
/* 297 */       this.bcLocal.add(var);
/* 298 */       this.bcShort.add(increment);
/*     */     } else {
/* 300 */       this.byteCodeOffset += 3;
/* 301 */       this.bcCodes.add(132);
/* 302 */       this.bcLocal.add(var);
/* 303 */       this.bcByte.add(increment & 0xFF);
/*     */     } 
/* 305 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitInsn(int opcode) {
/* 309 */     if (opcode >= 202) {
/* 310 */       throw new RuntimeException("Non-standard bytecode instructions not supported");
/*     */     }
/* 312 */     this.bcCodes.add(opcode);
/* 313 */     this.byteCodeOffset++;
/* 314 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitIntInsn(int opcode, int operand) {
/* 318 */     switch (opcode) {
/*     */       case 17:
/* 320 */         this.bcCodes.add(opcode);
/* 321 */         this.bcShort.add(operand);
/* 322 */         this.byteCodeOffset += 3;
/*     */         break;
/*     */       case 16:
/*     */       case 188:
/* 326 */         this.bcCodes.add(opcode);
/* 327 */         this.bcByte.add(operand & 0xFF);
/* 328 */         this.byteCodeOffset += 2; break;
/*     */     } 
/* 330 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitJumpInsn(int opcode, Label label) {
/* 334 */     this.bcCodes.add(opcode);
/* 335 */     this.bcLabel.add(label);
/* 336 */     this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
/* 337 */     this.byteCodeOffset += 3;
/* 338 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitLdcInsn(Object cst) {
/* 342 */     CPConstant constant = this.cpBands.getConstant(cst);
/* 343 */     if (this.segment.lastConstantHadWideIndex() || constant instanceof CPLong || constant instanceof CPDouble) {
/* 344 */       this.byteCodeOffset += 3;
/* 345 */       if (constant instanceof CPInt) {
/* 346 */         this.bcCodes.add(237);
/* 347 */         this.bcIntref.add(constant);
/* 348 */       } else if (constant instanceof CPFloat) {
/* 349 */         this.bcCodes.add(238);
/* 350 */         this.bcFloatRef.add(constant);
/* 351 */       } else if (constant instanceof CPLong) {
/* 352 */         this.bcCodes.add(20);
/* 353 */         this.bcLongRef.add(constant);
/* 354 */       } else if (constant instanceof CPDouble) {
/* 355 */         this.bcCodes.add(239);
/* 356 */         this.bcDoubleRef.add(constant);
/* 357 */       } else if (constant instanceof CPString) {
/* 358 */         this.bcCodes.add(19);
/* 359 */         this.bcStringRef.add(constant);
/* 360 */       } else if (constant instanceof CPClass) {
/* 361 */         this.bcCodes.add(236);
/* 362 */         this.bcClassRef.add(constant);
/*     */       } else {
/* 364 */         throw new RuntimeException("Constant should not be null");
/*     */       } 
/*     */     } else {
/* 367 */       this.byteCodeOffset += 2;
/* 368 */       if (constant instanceof CPInt) {
/* 369 */         this.bcCodes.add(234);
/* 370 */         this.bcIntref.add(constant);
/* 371 */       } else if (constant instanceof CPFloat) {
/* 372 */         this.bcCodes.add(235);
/* 373 */         this.bcFloatRef.add(constant);
/* 374 */       } else if (constant instanceof CPString) {
/* 375 */         this.bcCodes.add(18);
/* 376 */         this.bcStringRef.add(constant);
/* 377 */       } else if (constant instanceof CPClass) {
/* 378 */         this.bcCodes.add(233);
/* 379 */         this.bcClassRef.add(constant);
/*     */       } 
/*     */     } 
/* 382 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/* 386 */     this.bcCodes.add(171);
/* 387 */     this.bcLabel.add(dflt);
/* 388 */     this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
/* 389 */     this.bcCaseCount.add(keys.length);
/* 390 */     for (int i = 0; i < labels.length; i++) {
/* 391 */       this.bcCaseValue.add(keys[i]);
/* 392 */       this.bcLabel.add(labels[i]);
/* 393 */       this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
/*     */     } 
/* 395 */     int padding = ((this.byteCodeOffset + 1) % 4 == 0) ? 0 : (4 - (this.byteCodeOffset + 1) % 4);
/* 396 */     this.byteCodeOffset += 1 + padding + 8 + 8 * keys.length;
/* 397 */     updateRenumbering();
/*     */   } public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/*     */     boolean aload_0;
/*     */     CPMethodOrField cpIMethod;
/* 401 */     this.byteCodeOffset += 3;
/* 402 */     switch (opcode) {
/*     */       case 182:
/*     */       case 183:
/*     */       case 184:
/* 406 */         aload_0 = false;
/* 407 */         if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
/* 408 */           this.bcCodes.remove(this.bcCodes.size() - 1);
/* 409 */           aload_0 = true;
/* 410 */           opcode += 7;
/*     */         } 
/* 412 */         if (owner.equals(this.currentClass)) {
/* 413 */           opcode += 24;
/*     */ 
/*     */           
/* 416 */           if (name.equals("<init>") && opcode == 207) {
/* 417 */             opcode = 230;
/* 418 */             this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } else {
/* 420 */             this.bcThisMethod.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } 
/* 422 */         } else if (owner.equals(this.superClass)) {
/* 423 */           opcode += 38;
/*     */           
/* 425 */           if (name.equals("<init>") && opcode == 221) {
/* 426 */             opcode = 231;
/* 427 */             this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } else {
/* 429 */             this.bcSuperMethod.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } 
/*     */         } else {
/* 432 */           if (aload_0) {
/* 433 */             opcode -= 7;
/* 434 */             this.bcCodes.add(42);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 439 */           if (name.equals("<init>") && opcode == 183 && owner.equals(this.currentNewClass)) {
/* 440 */             opcode = 232;
/* 441 */             this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } else {
/* 443 */             this.bcMethodRef.add(this.cpBands.getCPMethod(owner, name, desc));
/*     */           } 
/*     */         } 
/* 446 */         this.bcCodes.add(opcode);
/*     */         break;
/*     */       case 185:
/* 449 */         this.byteCodeOffset += 2;
/* 450 */         cpIMethod = this.cpBands.getCPIMethod(owner, name, desc);
/* 451 */         this.bcIMethodRef.add(cpIMethod);
/* 452 */         this.bcCodes.add(185);
/*     */         break;
/*     */     } 
/* 455 */     updateRenumbering();
/*     */   }
/*     */   
/*     */   public void visitMultiANewArrayInsn(String desc, int dimensions) {
/* 459 */     this.byteCodeOffset += 4;
/* 460 */     updateRenumbering();
/* 461 */     this.bcCodes.add(197);
/* 462 */     this.bcClassRef.add(this.cpBands.getCPClass(desc));
/* 463 */     this.bcByte.add(dimensions & 0xFF);
/*     */   }
/*     */   
/*     */   public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
/* 467 */     this.bcCodes.add(170);
/* 468 */     this.bcLabel.add(dflt);
/* 469 */     this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
/* 470 */     this.bcCaseValue.add(min);
/* 471 */     int count = labels.length;
/* 472 */     this.bcCaseCount.add(count);
/* 473 */     for (int i = 0; i < count; i++) {
/* 474 */       this.bcLabel.add(labels[i]);
/* 475 */       this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
/*     */     } 
/* 477 */     int padding = (this.byteCodeOffset % 4 == 0) ? 0 : (4 - this.byteCodeOffset % 4);
/* 478 */     this.byteCodeOffset += padding + 12 + 4 * labels.length;
/* 479 */     updateRenumbering();
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitTypeInsn(int opcode, String type) {
/* 484 */     this.byteCodeOffset += 3;
/* 485 */     updateRenumbering();
/* 486 */     this.bcCodes.add(opcode);
/* 487 */     this.bcClassRef.add(this.cpBands.getCPClass(type));
/* 488 */     if (opcode == 187) {
/* 489 */       this.currentNewClass = type;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitVarInsn(int opcode, int var) {
/* 495 */     if (var > 255) {
/* 496 */       this.byteCodeOffset += 4;
/* 497 */       this.bcCodes.add(196);
/* 498 */       this.bcCodes.add(opcode);
/* 499 */       this.bcLocal.add(var);
/* 500 */     } else if (var > 3 || opcode == 169) {
/* 501 */       this.byteCodeOffset += 2;
/* 502 */       this.bcCodes.add(opcode);
/* 503 */       this.bcLocal.add(var);
/*     */     } else {
/* 505 */       this.byteCodeOffset++;
/* 506 */       switch (opcode) {
/*     */         case 21:
/*     */         case 54:
/* 509 */           this.bcCodes.add(opcode + 5 + var);
/*     */           break;
/*     */         case 22:
/*     */         case 55:
/* 513 */           this.bcCodes.add(opcode + 8 + var);
/*     */           break;
/*     */         case 23:
/*     */         case 56:
/* 517 */           this.bcCodes.add(opcode + 11 + var);
/*     */           break;
/*     */         case 24:
/*     */         case 57:
/* 521 */           this.bcCodes.add(opcode + 14 + var);
/*     */           break;
/*     */         case 25:
/*     */         case 58:
/* 525 */           this.bcCodes.add(opcode + 17 + var);
/*     */           break;
/*     */       } 
/*     */     } 
/* 529 */     updateRenumbering();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\BcBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */