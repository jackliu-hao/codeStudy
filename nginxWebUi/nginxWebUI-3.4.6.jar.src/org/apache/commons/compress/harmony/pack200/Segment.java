/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.objectweb.asm.AnnotationVisitor;
/*     */ import org.objectweb.asm.Attribute;
/*     */ import org.objectweb.asm.ClassVisitor;
/*     */ import org.objectweb.asm.FieldVisitor;
/*     */ import org.objectweb.asm.Label;
/*     */ import org.objectweb.asm.MethodVisitor;
/*     */ import org.objectweb.asm.Type;
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
/*     */ public class Segment
/*     */   implements ClassVisitor
/*     */ {
/*     */   private SegmentHeader segmentHeader;
/*     */   private CpBands cpBands;
/*     */   private AttributeDefinitionBands attributeDefinitionBands;
/*     */   private IcBands icBands;
/*     */   private ClassBands classBands;
/*     */   private BcBands bcBands;
/*     */   private FileBands fileBands;
/*  50 */   private final SegmentFieldVisitor fieldVisitor = new SegmentFieldVisitor();
/*  51 */   private final SegmentMethodVisitor methodVisitor = new SegmentMethodVisitor();
/*     */ 
/*     */ 
/*     */   
/*     */   private Pack200ClassReader currentClassReader;
/*     */ 
/*     */   
/*     */   private PackingOptions options;
/*     */ 
/*     */   
/*     */   private boolean stripDebug;
/*     */ 
/*     */   
/*     */   private Attribute[] nonStandardAttributePrototypes;
/*     */ 
/*     */ 
/*     */   
/*     */   public void pack(Archive.SegmentUnit segmentUnit, OutputStream out, PackingOptions options) throws IOException, Pack200Exception {
/*  69 */     this.options = options;
/*  70 */     this.stripDebug = options.isStripDebug();
/*  71 */     int effort = options.getEffort();
/*  72 */     this.nonStandardAttributePrototypes = options.getUnknownAttributePrototypes();
/*     */     
/*  74 */     PackingUtils.log("Start to pack a new segment with " + segmentUnit.fileListSize() + " files including " + segmentUnit
/*  75 */         .classListSize() + " classes");
/*     */     
/*  77 */     PackingUtils.log("Initialize a header for the segment");
/*  78 */     this.segmentHeader = new SegmentHeader();
/*  79 */     this.segmentHeader.setFile_count(segmentUnit.fileListSize());
/*  80 */     this.segmentHeader.setHave_all_code_flags(!this.stripDebug);
/*  81 */     if (!options.isKeepDeflateHint()) {
/*  82 */       this.segmentHeader.setDeflate_hint("true".equals(options.getDeflateHint()));
/*     */     }
/*     */     
/*  85 */     PackingUtils.log("Setup constant pool bands for the segment");
/*  86 */     this.cpBands = new CpBands(this, effort);
/*     */     
/*  88 */     PackingUtils.log("Setup attribute definition bands for the segment");
/*  89 */     this.attributeDefinitionBands = new AttributeDefinitionBands(this, effort, this.nonStandardAttributePrototypes);
/*     */     
/*  91 */     PackingUtils.log("Setup internal class bands for the segment");
/*  92 */     this.icBands = new IcBands(this.segmentHeader, this.cpBands, effort);
/*     */     
/*  94 */     PackingUtils.log("Setup class bands for the segment");
/*  95 */     this.classBands = new ClassBands(this, segmentUnit.classListSize(), effort, this.stripDebug);
/*     */     
/*  97 */     PackingUtils.log("Setup byte code bands for the segment");
/*  98 */     this.bcBands = new BcBands(this.cpBands, this, effort);
/*     */     
/* 100 */     PackingUtils.log("Setup file bands for the segment");
/* 101 */     this.fileBands = new FileBands(this.cpBands, this.segmentHeader, options, segmentUnit, effort);
/*     */     
/* 103 */     processClasses(segmentUnit, this.nonStandardAttributePrototypes);
/*     */     
/* 105 */     this.cpBands.finaliseBands();
/* 106 */     this.attributeDefinitionBands.finaliseBands();
/* 107 */     this.icBands.finaliseBands();
/* 108 */     this.classBands.finaliseBands();
/* 109 */     this.bcBands.finaliseBands();
/* 110 */     this.fileBands.finaliseBands();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 116 */     ByteArrayOutputStream bandsOutputStream = new ByteArrayOutputStream();
/*     */     
/* 118 */     PackingUtils.log("Packing...");
/* 119 */     int finalNumberOfClasses = this.classBands.numClassesProcessed();
/* 120 */     this.segmentHeader.setClass_count(finalNumberOfClasses);
/* 121 */     this.cpBands.pack(bandsOutputStream);
/* 122 */     if (finalNumberOfClasses > 0) {
/* 123 */       this.attributeDefinitionBands.pack(bandsOutputStream);
/* 124 */       this.icBands.pack(bandsOutputStream);
/* 125 */       this.classBands.pack(bandsOutputStream);
/* 126 */       this.bcBands.pack(bandsOutputStream);
/*     */     } 
/* 128 */     this.fileBands.pack(bandsOutputStream);
/*     */     
/* 130 */     ByteArrayOutputStream headerOutputStream = new ByteArrayOutputStream();
/* 131 */     this.segmentHeader.pack(headerOutputStream);
/*     */     
/* 133 */     headerOutputStream.writeTo(out);
/* 134 */     bandsOutputStream.writeTo(out);
/*     */     
/* 136 */     segmentUnit.addPackedByteAmount(headerOutputStream.size());
/* 137 */     segmentUnit.addPackedByteAmount(bandsOutputStream.size());
/*     */     
/* 139 */     PackingUtils.log("Wrote total of " + segmentUnit.getPackedByteAmount() + " bytes");
/* 140 */     PackingUtils.log("Transmitted " + segmentUnit.fileListSize() + " files of " + segmentUnit.getByteAmount() + " input bytes in a segment of " + segmentUnit
/* 141 */         .getPackedByteAmount() + " bytes");
/*     */   }
/*     */   
/*     */   private void processClasses(Archive.SegmentUnit segmentUnit, Attribute[] attributes) throws Pack200Exception {
/* 145 */     this.segmentHeader.setClass_count(segmentUnit.classListSize());
/* 146 */     for (Iterator<Pack200ClassReader> iterator = segmentUnit.getClassList().iterator(); iterator.hasNext(); ) {
/* 147 */       Pack200ClassReader classReader = iterator.next();
/* 148 */       this.currentClassReader = classReader;
/* 149 */       int flags = 0;
/* 150 */       if (this.stripDebug) {
/* 151 */         flags |= 0x2;
/*     */       }
/*     */       try {
/* 154 */         classReader.accept(this, attributes, flags);
/* 155 */       } catch (PassException pe) {
/*     */ 
/*     */         
/* 158 */         this.classBands.removeCurrentClass();
/* 159 */         String name = classReader.getFileName();
/* 160 */         this.options.addPassFile(name);
/* 161 */         this.cpBands.addCPUtf8(name);
/* 162 */         boolean found = false;
/* 163 */         for (Iterator<Archive.PackingFile> iterator2 = segmentUnit.getFileList().iterator(); iterator2.hasNext(); ) {
/* 164 */           Archive.PackingFile file = iterator2.next();
/* 165 */           if (file.getName().equals(name)) {
/* 166 */             found = true;
/* 167 */             file.setContents(classReader.b);
/*     */             break;
/*     */           } 
/*     */         } 
/* 171 */         if (!found) {
/* 172 */           throw new Pack200Exception("Error passing file " + name);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
/* 181 */     this.bcBands.setCurrentClass(name, superName);
/* 182 */     this.segmentHeader.addMajorVersion(version);
/* 183 */     this.classBands.addClass(version, access, name, signature, superName, interfaces);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitSource(String source, String debug) {
/* 188 */     if (!this.stripDebug) {
/* 189 */       this.classBands.addSourceFile(source);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitOuterClass(String owner, String name, String desc) {
/* 195 */     this.classBands.addEnclosingMethod(owner, name, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 201 */     return new SegmentAnnotationVisitor(0, desc, visible);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitAttribute(Attribute attribute) {
/* 206 */     if (attribute.isUnknown()) {
/* 207 */       String action = this.options.getUnknownAttributeAction();
/* 208 */       if (action.equals("pass")) {
/* 209 */         passCurrentClass();
/* 210 */       } else if (action.equals("error")) {
/* 211 */         throw new Error("Unknown attribute encountered");
/*     */       } 
/* 213 */     } else if (attribute instanceof NewAttribute) {
/* 214 */       NewAttribute newAttribute = (NewAttribute)attribute;
/* 215 */       if (newAttribute.isUnknown(0)) {
/* 216 */         String action = this.options.getUnknownClassAttributeAction(newAttribute.type);
/* 217 */         if (action.equals("pass")) {
/* 218 */           passCurrentClass();
/* 219 */         } else if (action.equals("error")) {
/* 220 */           throw new Error("Unknown attribute encountered");
/*     */         } 
/*     */       } 
/* 223 */       this.classBands.addClassAttribute(newAttribute);
/*     */     } else {
/* 225 */       throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitInnerClass(String name, String outerName, String innerName, int flags) {
/* 231 */     this.icBands.addInnerClass(name, outerName, innerName, flags);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FieldVisitor visitField(int flags, String name, String desc, String signature, Object value) {
/* 237 */     this.classBands.addField(flags, name, desc, signature, value);
/* 238 */     return this.fieldVisitor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MethodVisitor visitMethod(int flags, String name, String desc, String signature, String[] exceptions) {
/* 244 */     this.classBands.addMethod(flags, name, desc, signature, exceptions);
/* 245 */     return this.methodVisitor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 250 */     this.classBands.endOfClass();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class SegmentMethodVisitor
/*     */     implements MethodVisitor
/*     */   {
/*     */     public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 262 */       return new Segment.SegmentAnnotationVisitor(2, desc, visible);
/*     */     }
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitAnnotationDefault() {
/* 267 */       return new Segment.SegmentAnnotationVisitor(2);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitAttribute(Attribute attribute) {
/* 272 */       if (attribute.isUnknown()) {
/* 273 */         String action = Segment.this.options.getUnknownAttributeAction();
/* 274 */         if (action.equals("pass")) {
/* 275 */           Segment.this.passCurrentClass();
/* 276 */         } else if (action.equals("error")) {
/* 277 */           throw new Error("Unknown attribute encountered");
/*     */         } 
/* 279 */       } else if (attribute instanceof NewAttribute) {
/* 280 */         NewAttribute newAttribute = (NewAttribute)attribute;
/* 281 */         if (attribute.isCodeAttribute()) {
/* 282 */           if (newAttribute.isUnknown(3)) {
/* 283 */             String action = Segment.this.options.getUnknownCodeAttributeAction(newAttribute.type);
/* 284 */             if (action.equals("pass")) {
/* 285 */               Segment.this.passCurrentClass();
/* 286 */             } else if (action.equals("error")) {
/* 287 */               throw new Error("Unknown attribute encountered");
/*     */             } 
/*     */           } 
/* 290 */           Segment.this.classBands.addCodeAttribute(newAttribute);
/*     */         } else {
/* 292 */           if (newAttribute.isUnknown(2)) {
/* 293 */             String action = Segment.this.options.getUnknownMethodAttributeAction(newAttribute.type);
/* 294 */             if (action.equals("pass")) {
/* 295 */               Segment.this.passCurrentClass();
/* 296 */             } else if (action.equals("error")) {
/* 297 */               throw new Error("Unknown attribute encountered");
/*     */             } 
/*     */           } 
/* 300 */           Segment.this.classBands.addMethodAttribute(newAttribute);
/*     */         } 
/*     */       } else {
/* 303 */         throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitCode() {
/* 309 */       Segment.this.classBands.addCode();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitLabel(Label label) {
/* 321 */       Segment.this.bcBands.visitLabel(label);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitLineNumber(int line, Label start) {
/* 326 */       if (!Segment.this.stripDebug) {
/* 327 */         Segment.this.classBands.addLineNumber(line, start);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
/* 334 */       if (!Segment.this.stripDebug) {
/* 335 */         Segment.this.classBands.addLocalVariable(name, desc, signature, start, end, index);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitMaxs(int maxStack, int maxLocals) {
/* 341 */       Segment.this.classBands.addMaxStack(maxStack, maxLocals);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
/* 347 */       return new Segment.SegmentAnnotationVisitor(2, parameter, desc, visible);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
/* 352 */       Segment.this.classBands.addHandler(start, end, handler, type);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitEnd() {
/* 357 */       Segment.this.classBands.endOfMethod();
/* 358 */       Segment.this.bcBands.visitEnd();
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/* 363 */       Segment.this.bcBands.visitFieldInsn(opcode, owner, name, desc);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitIincInsn(int var, int increment) {
/* 368 */       Segment.this.bcBands.visitIincInsn(var, increment);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitInsn(int opcode) {
/* 373 */       Segment.this.bcBands.visitInsn(opcode);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitIntInsn(int opcode, int operand) {
/* 378 */       Segment.this.bcBands.visitIntInsn(opcode, operand);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitJumpInsn(int opcode, Label label) {
/* 383 */       Segment.this.bcBands.visitJumpInsn(opcode, label);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitLdcInsn(Object cst) {
/* 388 */       Segment.this.bcBands.visitLdcInsn(cst);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/* 393 */       Segment.this.bcBands.visitLookupSwitchInsn(dflt, keys, labels);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/* 398 */       Segment.this.bcBands.visitMethodInsn(opcode, owner, name, desc);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitMultiANewArrayInsn(String desc, int dimensions) {
/* 403 */       Segment.this.bcBands.visitMultiANewArrayInsn(desc, dimensions);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
/* 408 */       Segment.this.bcBands.visitTableSwitchInsn(min, max, dflt, labels);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitTypeInsn(int opcode, String type) {
/* 413 */       Segment.this.bcBands.visitTypeInsn(opcode, type);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitVarInsn(int opcode, int var) {
/* 418 */       Segment.this.bcBands.visitVarInsn(opcode, var);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassBands getClassBands() {
/* 424 */     return this.classBands;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class SegmentAnnotationVisitor
/*     */     implements AnnotationVisitor
/*     */   {
/* 432 */     private int context = -1;
/* 433 */     private int parameter = -1;
/*     */     
/*     */     private String desc;
/*     */     private boolean visible;
/* 437 */     private final List nameRU = new ArrayList();
/* 438 */     private final List T = new ArrayList();
/* 439 */     private final List values = new ArrayList();
/* 440 */     private final List caseArrayN = new ArrayList();
/* 441 */     private final List nestTypeRS = new ArrayList();
/* 442 */     private final List nestNameRU = new ArrayList();
/* 443 */     private final List nestPairN = new ArrayList();
/*     */     
/*     */     public SegmentAnnotationVisitor(int context, String desc, boolean visible) {
/* 446 */       this.context = context;
/* 447 */       this.desc = desc;
/* 448 */       this.visible = visible;
/*     */     }
/*     */     
/*     */     public SegmentAnnotationVisitor(int context) {
/* 452 */       this.context = context;
/*     */     }
/*     */ 
/*     */     
/*     */     public SegmentAnnotationVisitor(int context, int parameter, String desc, boolean visible) {
/* 457 */       this.context = context;
/* 458 */       this.parameter = parameter;
/* 459 */       this.desc = desc;
/* 460 */       this.visible = visible;
/*     */     }
/*     */ 
/*     */     
/*     */     public void visit(String name, Object value) {
/* 465 */       if (name == null) {
/* 466 */         name = "";
/*     */       }
/* 468 */       this.nameRU.add(name);
/* 469 */       Segment.this.addValueAndTag(value, this.T, this.values);
/*     */     }
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitAnnotation(String name, String desc) {
/* 474 */       this.T.add("@");
/* 475 */       if (name == null) {
/* 476 */         name = "";
/*     */       }
/* 478 */       this.nameRU.add(name);
/* 479 */       this.nestTypeRS.add(desc);
/* 480 */       this.nestPairN.add(Integer.valueOf(0));
/* 481 */       return new AnnotationVisitor()
/*     */         {
/*     */           public void visit(String name, Object value) {
/* 484 */             Integer numPairs = Segment.SegmentAnnotationVisitor.this.nestPairN.remove(Segment.SegmentAnnotationVisitor.this.nestPairN.size() - 1);
/* 485 */             Segment.SegmentAnnotationVisitor.this.nestPairN.add(Integer.valueOf(numPairs.intValue() + 1));
/* 486 */             Segment.SegmentAnnotationVisitor.this.nestNameRU.add(name);
/* 487 */             Segment.this.addValueAndTag(value, Segment.SegmentAnnotationVisitor.this.T, Segment.SegmentAnnotationVisitor.this.values);
/*     */           }
/*     */ 
/*     */           
/*     */           public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
/* 492 */             throw new RuntimeException("Not yet supported");
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public AnnotationVisitor visitArray(String arg0) {
/* 498 */             throw new RuntimeException("Not yet supported");
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void visitEnd() {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void visitEnum(String name, String desc, String value) {
/* 508 */             Integer numPairs = Segment.SegmentAnnotationVisitor.this.nestPairN.remove(Segment.SegmentAnnotationVisitor.this.nestPairN.size() - 1);
/* 509 */             Segment.SegmentAnnotationVisitor.this.nestPairN.add(Integer.valueOf(numPairs.intValue() + 1));
/* 510 */             Segment.SegmentAnnotationVisitor.this.T.add("e");
/* 511 */             Segment.SegmentAnnotationVisitor.this.nestNameRU.add(name);
/* 512 */             Segment.SegmentAnnotationVisitor.this.values.add(desc);
/* 513 */             Segment.SegmentAnnotationVisitor.this.values.add(value);
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitArray(String name) {
/* 520 */       this.T.add("[");
/* 521 */       if (name == null) {
/* 522 */         name = "";
/*     */       }
/* 524 */       this.nameRU.add(name);
/* 525 */       this.caseArrayN.add(Integer.valueOf(0));
/* 526 */       return new Segment.ArrayVisitor(this.caseArrayN, this.T, this.nameRU, this.values);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitEnd() {
/* 531 */       if (this.desc == null) {
/* 532 */         Segment.this.classBands.addAnnotationDefault(this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
/*     */       }
/* 534 */       else if (this.parameter != -1) {
/* 535 */         Segment.this.classBands.addParameterAnnotation(this.parameter, this.desc, this.visible, this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
/*     */       } else {
/*     */         
/* 538 */         Segment.this.classBands.addAnnotation(this.context, this.desc, this.visible, this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitEnum(String name, String desc, String value) {
/* 545 */       this.T.add("e");
/* 546 */       if (name == null) {
/* 547 */         name = "";
/*     */       }
/* 549 */       this.nameRU.add(name);
/* 550 */       this.values.add(desc);
/* 551 */       this.values.add(value);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ArrayVisitor
/*     */     implements AnnotationVisitor {
/*     */     private final int indexInCaseArrayN;
/*     */     private final List caseArrayN;
/*     */     private final List values;
/*     */     private final List nameRU;
/*     */     private final List T;
/*     */     
/*     */     public ArrayVisitor(List caseArrayN, List T, List nameRU, List values) {
/* 564 */       this.caseArrayN = caseArrayN;
/* 565 */       this.T = T;
/* 566 */       this.nameRU = nameRU;
/* 567 */       this.values = values;
/* 568 */       this.indexInCaseArrayN = caseArrayN.size() - 1;
/*     */     }
/*     */ 
/*     */     
/*     */     public void visit(String name, Object value) {
/* 573 */       Integer numCases = this.caseArrayN.remove(this.indexInCaseArrayN);
/* 574 */       this.caseArrayN.add(this.indexInCaseArrayN, Integer.valueOf(numCases.intValue() + 1));
/* 575 */       if (name == null) {
/* 576 */         name = "";
/*     */       }
/* 578 */       Segment.this.addValueAndTag(value, this.T, this.values);
/*     */     }
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
/* 583 */       throw new RuntimeException("Not yet supported");
/*     */     }
/*     */ 
/*     */     
/*     */     public AnnotationVisitor visitArray(String name) {
/* 588 */       this.T.add("[");
/* 589 */       if (name == null) {
/* 590 */         name = "";
/*     */       }
/* 592 */       this.nameRU.add(name);
/* 593 */       this.caseArrayN.add(Integer.valueOf(0));
/* 594 */       return new ArrayVisitor(this.caseArrayN, this.T, this.nameRU, this.values);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitEnd() {}
/*     */ 
/*     */     
/*     */     public void visitEnum(String name, String desc, String value) {
/* 603 */       Integer numCases = this.caseArrayN.remove(this.caseArrayN.size() - 1);
/* 604 */       this.caseArrayN.add(Integer.valueOf(numCases.intValue() + 1));
/* 605 */       this.T.add("e");
/* 606 */       this.values.add(desc);
/* 607 */       this.values.add(value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class SegmentFieldVisitor
/*     */     implements FieldVisitor
/*     */   {
/*     */     public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
/* 619 */       return new Segment.SegmentAnnotationVisitor(1, desc, visible);
/*     */     }
/*     */ 
/*     */     
/*     */     public void visitAttribute(Attribute attribute) {
/* 624 */       if (attribute.isUnknown()) {
/* 625 */         String action = Segment.this.options.getUnknownAttributeAction();
/* 626 */         if (action.equals("pass")) {
/* 627 */           Segment.this.passCurrentClass();
/* 628 */         } else if (action.equals("error")) {
/* 629 */           throw new Error("Unknown attribute encountered");
/*     */         } 
/* 631 */       } else if (attribute instanceof NewAttribute) {
/* 632 */         NewAttribute newAttribute = (NewAttribute)attribute;
/* 633 */         if (newAttribute.isUnknown(1)) {
/* 634 */           String action = Segment.this.options.getUnknownFieldAttributeAction(newAttribute.type);
/* 635 */           if (action.equals("pass")) {
/* 636 */             Segment.this.passCurrentClass();
/* 637 */           } else if (action.equals("error")) {
/* 638 */             throw new Error("Unknown attribute encountered");
/*     */           } 
/*     */         } 
/* 641 */         Segment.this.classBands.addFieldAttribute(newAttribute);
/*     */       } else {
/* 643 */         throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitEnd() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void addValueAndTag(Object value, List<String> T, List<Object> values) {
/* 654 */     if (value instanceof Integer) {
/* 655 */       T.add("I");
/* 656 */       values.add(value);
/* 657 */     } else if (value instanceof Double) {
/* 658 */       T.add("D");
/* 659 */       values.add(value);
/* 660 */     } else if (value instanceof Float) {
/* 661 */       T.add("F");
/* 662 */       values.add(value);
/* 663 */     } else if (value instanceof Long) {
/* 664 */       T.add("J");
/* 665 */       values.add(value);
/* 666 */     } else if (value instanceof Byte) {
/* 667 */       T.add("B");
/* 668 */       values.add(Integer.valueOf(((Byte)value).intValue()));
/* 669 */     } else if (value instanceof Character) {
/* 670 */       T.add("C");
/* 671 */       values.add(Integer.valueOf(((Character)value).charValue()));
/* 672 */     } else if (value instanceof Short) {
/* 673 */       T.add("S");
/* 674 */       values.add(Integer.valueOf(((Short)value).intValue()));
/* 675 */     } else if (value instanceof Boolean) {
/* 676 */       T.add("Z");
/* 677 */       values.add(Integer.valueOf(((Boolean)value).booleanValue() ? 1 : 0));
/* 678 */     } else if (value instanceof String) {
/* 679 */       T.add("s");
/* 680 */       values.add(value);
/* 681 */     } else if (value instanceof Type) {
/* 682 */       T.add("c");
/* 683 */       values.add(((Type)value).toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean lastConstantHadWideIndex() {
/* 688 */     return this.currentClassReader.lastConstantHadWideIndex();
/*     */   }
/*     */   
/*     */   public CpBands getCpBands() {
/* 692 */     return this.cpBands;
/*     */   }
/*     */   
/*     */   public SegmentHeader getSegmentHeader() {
/* 696 */     return this.segmentHeader;
/*     */   }
/*     */   
/*     */   public AttributeDefinitionBands getAttrBands() {
/* 700 */     return this.attributeDefinitionBands;
/*     */   }
/*     */   
/*     */   public IcBands getIcBands() {
/* 704 */     return this.icBands;
/*     */   }
/*     */   
/*     */   public Pack200ClassReader getCurrentClassReader() {
/* 708 */     return this.currentClassReader;
/*     */   }
/*     */   
/*     */   private void passCurrentClass() {
/* 712 */     throw new PassException();
/*     */   }
/*     */   
/*     */   public static class PassException extends RuntimeException {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Segment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */