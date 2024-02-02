/*      */ package org.apache.commons.compress.harmony.unpack200;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import org.apache.commons.compress.harmony.pack200.Codec;
/*      */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantValueAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.DeprecatedAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.EnclosingMethodAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionsAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.LineNumberTableAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTableAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTypeTableAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.SignatureAttribute;
/*      */ import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassBands
/*      */   extends BandSet
/*      */ {
/*      */   private int[] classFieldCount;
/*      */   private long[] classFlags;
/*      */   private long[] classAccessFlags;
/*      */   private int[][] classInterfacesInts;
/*      */   private int[] classMethodCount;
/*      */   private int[] classSuperInts;
/*      */   private String[] classThis;
/*      */   private int[] classThisInts;
/*      */   private ArrayList[] classAttributes;
/*      */   private int[] classVersionMajor;
/*      */   private int[] classVersionMinor;
/*      */   private IcTuple[][] icLocal;
/*      */   private List[] codeAttributes;
/*      */   private int[] codeHandlerCount;
/*      */   private int[] codeMaxNALocals;
/*      */   private int[] codeMaxStack;
/*      */   private ArrayList[][] fieldAttributes;
/*      */   private String[][] fieldDescr;
/*      */   private int[][] fieldDescrInts;
/*      */   private long[][] fieldFlags;
/*      */   private long[][] fieldAccessFlags;
/*      */   private ArrayList[][] methodAttributes;
/*      */   private String[][] methodDescr;
/*      */   private int[][] methodDescrInts;
/*      */   private long[][] methodFlags;
/*      */   private long[][] methodAccessFlags;
/*      */   private final AttributeLayoutMap attrMap;
/*      */   private final CpBands cpBands;
/*      */   private final SegmentOptions options;
/*      */   private final int classCount;
/*      */   private int[] methodAttrCalls;
/*      */   private int[][] codeHandlerStartP;
/*      */   private int[][] codeHandlerEndPO;
/*      */   private int[][] codeHandlerCatchPO;
/*      */   private int[][] codeHandlerClassRCN;
/*      */   private boolean[] codeHasAttributes;
/*      */   
/*      */   public ClassBands(Segment segment) {
/*  123 */     super(segment);
/*  124 */     this.attrMap = segment.getAttrDefinitionBands().getAttributeDefinitionMap();
/*  125 */     this.cpBands = segment.getCpBands();
/*  126 */     this.classCount = this.header.getClassCount();
/*  127 */     this.options = this.header.getOptions();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void read(InputStream in) throws IOException, Pack200Exception {
/*  138 */     int classCount = this.header.getClassCount();
/*  139 */     this.classThisInts = decodeBandInt("class_this", in, Codec.DELTA5, classCount);
/*  140 */     this.classThis = getReferences(this.classThisInts, this.cpBands.getCpClass());
/*  141 */     this.classSuperInts = decodeBandInt("class_super", in, Codec.DELTA5, classCount);
/*  142 */     int[] classInterfaceLengths = decodeBandInt("class_interface_count", in, Codec.DELTA5, classCount);
/*  143 */     this.classInterfacesInts = decodeBandInt("class_interface", in, Codec.DELTA5, classInterfaceLengths);
/*  144 */     this.classFieldCount = decodeBandInt("class_field_count", in, Codec.DELTA5, classCount);
/*  145 */     this.classMethodCount = decodeBandInt("class_method_count", in, Codec.DELTA5, classCount);
/*  146 */     parseFieldBands(in);
/*  147 */     parseMethodBands(in);
/*  148 */     parseClassAttrBands(in);
/*  149 */     parseCodeBands(in);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void unpack() {}
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseFieldBands(InputStream in) throws IOException, Pack200Exception {
/*  159 */     this.fieldDescrInts = decodeBandInt("field_descr", in, Codec.DELTA5, this.classFieldCount);
/*  160 */     this.fieldDescr = getReferences(this.fieldDescrInts, this.cpBands.getCpDescriptor());
/*  161 */     parseFieldAttrBands(in);
/*      */   }
/*      */   
/*      */   private void parseFieldAttrBands(InputStream in) throws IOException, Pack200Exception {
/*  165 */     this.fieldFlags = parseFlags("field_flags", in, this.classFieldCount, Codec.UNSIGNED5, this.options.hasFieldFlagsHi());
/*  166 */     int fieldAttrCount = SegmentUtils.countBit16(this.fieldFlags);
/*  167 */     int[] fieldAttrCounts = decodeBandInt("field_attr_count", in, Codec.UNSIGNED5, fieldAttrCount);
/*  168 */     int[][] fieldAttrIndexes = decodeBandInt("field_attr_indexes", in, Codec.UNSIGNED5, fieldAttrCounts);
/*  169 */     int callCount = getCallCount(fieldAttrIndexes, this.fieldFlags, 1);
/*  170 */     int[] fieldAttrCalls = decodeBandInt("field_attr_calls", in, Codec.UNSIGNED5, callCount);
/*      */ 
/*      */     
/*  173 */     this.fieldAttributes = new ArrayList[this.classCount][];
/*  174 */     for (int i = 0; i < this.classCount; i++) {
/*  175 */       this.fieldAttributes[i] = new ArrayList[(this.fieldFlags[i]).length];
/*  176 */       for (int m = 0; m < (this.fieldFlags[i]).length; m++) {
/*  177 */         this.fieldAttributes[i][m] = new ArrayList();
/*      */       }
/*      */     } 
/*      */     
/*  181 */     AttributeLayout constantValueLayout = this.attrMap.getAttributeLayout("ConstantValue", 1);
/*      */     
/*  183 */     int constantCount = SegmentUtils.countMatches(this.fieldFlags, constantValueLayout);
/*  184 */     int[] field_constantValue_KQ = decodeBandInt("field_ConstantValue_KQ", in, Codec.UNSIGNED5, constantCount);
/*      */     
/*  186 */     int constantValueIndex = 0;
/*      */     
/*  188 */     AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 1);
/*      */     
/*  190 */     int signatureCount = SegmentUtils.countMatches(this.fieldFlags, signatureLayout);
/*  191 */     int[] fieldSignatureRS = decodeBandInt("field_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
/*  192 */     int signatureIndex = 0;
/*      */     
/*  194 */     AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 1);
/*      */ 
/*      */     
/*  197 */     for (int j = 0; j < this.classCount; j++) {
/*  198 */       for (int m = 0; m < (this.fieldFlags[j]).length; m++) {
/*  199 */         long flag = this.fieldFlags[j][m];
/*  200 */         if (deprecatedLayout.matches(flag)) {
/*  201 */           this.fieldAttributes[j][m].add(new DeprecatedAttribute());
/*      */         }
/*  203 */         if (constantValueLayout.matches(flag)) {
/*      */           
/*  205 */           long result = field_constantValue_KQ[constantValueIndex];
/*  206 */           String desc = this.fieldDescr[j][m];
/*  207 */           int colon = desc.indexOf(':');
/*  208 */           String type = desc.substring(colon + 1);
/*  209 */           if (type.equals("B") || type.equals("S") || type.equals("C") || type.equals("Z")) {
/*  210 */             type = "I";
/*      */           }
/*  212 */           ClassFileEntry value = constantValueLayout.getValue(result, type, this.cpBands.getConstantPool());
/*  213 */           this.fieldAttributes[j][m].add(new ConstantValueAttribute(value));
/*  214 */           constantValueIndex++;
/*      */         } 
/*  216 */         if (signatureLayout.matches(flag)) {
/*      */           
/*  218 */           long result = fieldSignatureRS[signatureIndex];
/*  219 */           String desc = this.fieldDescr[j][m];
/*  220 */           int colon = desc.indexOf(':');
/*  221 */           String type = desc.substring(colon + 1);
/*  222 */           CPUTF8 value = (CPUTF8)signatureLayout.getValue(result, type, this.cpBands.getConstantPool());
/*  223 */           this.fieldAttributes[j][m].add(new SignatureAttribute(value));
/*  224 */           signatureIndex++;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  229 */     int backwardsCallsUsed = parseFieldMetadataBands(in, fieldAttrCalls);
/*      */ 
/*      */     
/*  232 */     int backwardsCallIndex = backwardsCallsUsed;
/*  233 */     int limit = this.options.hasFieldFlagsHi() ? 62 : 31;
/*  234 */     AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
/*  235 */     int[] counts = new int[limit + 1];
/*  236 */     List[] otherAttributes = new List[limit + 1]; int k;
/*  237 */     for (k = 0; k < limit; k++) {
/*  238 */       AttributeLayout layout = this.attrMap.getAttributeLayout(k, 1);
/*  239 */       if (layout != null && !layout.isDefaultLayout()) {
/*  240 */         otherLayouts[k] = layout;
/*  241 */         counts[k] = SegmentUtils.countMatches(this.fieldFlags, layout);
/*      */       } 
/*      */     } 
/*  244 */     for (k = 0; k < counts.length; k++) {
/*  245 */       if (counts[k] > 0) {
/*  246 */         NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[k]);
/*  247 */         otherAttributes[k] = bands.parseAttributes(in, counts[k]);
/*  248 */         int numBackwardsCallables = otherLayouts[k].numBackwardsCallables();
/*  249 */         if (numBackwardsCallables > 0) {
/*  250 */           int[] backwardsCalls = new int[numBackwardsCallables];
/*  251 */           System.arraycopy(fieldAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
/*  252 */           bands.setBackwardsCalls(backwardsCalls);
/*  253 */           backwardsCallIndex += numBackwardsCallables;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  259 */     for (k = 0; k < this.classCount; k++) {
/*  260 */       for (int m = 0; m < (this.fieldFlags[k]).length; m++) {
/*  261 */         long flag = this.fieldFlags[k][m];
/*  262 */         int othersAddedAtStart = 0;
/*  263 */         for (int n = 0; n < otherLayouts.length; n++) {
/*  264 */           if (otherLayouts[n] != null && otherLayouts[n].matches(flag)) {
/*      */             
/*  266 */             if (otherLayouts[n].getIndex() < 15) {
/*  267 */               this.fieldAttributes[k][m].add(othersAddedAtStart++, otherAttributes[n].get(0));
/*      */             } else {
/*  269 */               this.fieldAttributes[k][m].add(otherAttributes[n].get(0));
/*      */             } 
/*  271 */             otherAttributes[n].remove(0);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void parseMethodBands(InputStream in) throws IOException, Pack200Exception {
/*  279 */     this.methodDescrInts = decodeBandInt("method_descr", in, Codec.MDELTA5, this.classMethodCount);
/*  280 */     this.methodDescr = getReferences(this.methodDescrInts, this.cpBands.getCpDescriptor());
/*  281 */     parseMethodAttrBands(in);
/*      */   }
/*      */   
/*      */   private void parseMethodAttrBands(InputStream in) throws IOException, Pack200Exception {
/*  285 */     this.methodFlags = parseFlags("method_flags", in, this.classMethodCount, Codec.UNSIGNED5, this.options.hasMethodFlagsHi());
/*  286 */     int methodAttrCount = SegmentUtils.countBit16(this.methodFlags);
/*  287 */     int[] methodAttrCounts = decodeBandInt("method_attr_count", in, Codec.UNSIGNED5, methodAttrCount);
/*  288 */     int[][] methodAttrIndexes = decodeBandInt("method_attr_indexes", in, Codec.UNSIGNED5, methodAttrCounts);
/*  289 */     int callCount = getCallCount(methodAttrIndexes, this.methodFlags, 2);
/*  290 */     this.methodAttrCalls = decodeBandInt("method_attr_calls", in, Codec.UNSIGNED5, callCount);
/*      */ 
/*      */     
/*  293 */     this.methodAttributes = new ArrayList[this.classCount][];
/*  294 */     for (int i = 0; i < this.classCount; i++) {
/*  295 */       this.methodAttributes[i] = new ArrayList[(this.methodFlags[i]).length];
/*  296 */       for (int m = 0; m < (this.methodFlags[i]).length; m++) {
/*  297 */         this.methodAttributes[i][m] = new ArrayList();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  302 */     AttributeLayout methodExceptionsLayout = this.attrMap.getAttributeLayout("Exceptions", 2);
/*      */     
/*  304 */     int count = SegmentUtils.countMatches(this.methodFlags, methodExceptionsLayout);
/*  305 */     int[] numExceptions = decodeBandInt("method_Exceptions_n", in, Codec.UNSIGNED5, count);
/*  306 */     int[][] methodExceptionsRS = decodeBandInt("method_Exceptions_RC", in, Codec.UNSIGNED5, numExceptions);
/*      */ 
/*      */     
/*  309 */     AttributeLayout methodSignatureLayout = this.attrMap.getAttributeLayout("Signature", 2);
/*      */     
/*  311 */     int count1 = SegmentUtils.countMatches(this.methodFlags, methodSignatureLayout);
/*  312 */     int[] methodSignatureRS = decodeBandInt("method_signature_RS", in, Codec.UNSIGNED5, count1);
/*      */     
/*  314 */     AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 2);
/*      */ 
/*      */ 
/*      */     
/*  318 */     int methodExceptionsIndex = 0;
/*  319 */     int methodSignatureIndex = 0;
/*  320 */     for (int j = 0; j < this.methodAttributes.length; j++) {
/*  321 */       for (int m = 0; m < (this.methodAttributes[j]).length; m++) {
/*  322 */         long flag = this.methodFlags[j][m];
/*  323 */         if (methodExceptionsLayout.matches(flag)) {
/*  324 */           int n = numExceptions[methodExceptionsIndex];
/*  325 */           int[] exceptions = methodExceptionsRS[methodExceptionsIndex];
/*  326 */           CPClass[] exceptionClasses = new CPClass[n];
/*  327 */           for (int i1 = 0; i1 < n; i1++) {
/*  328 */             exceptionClasses[i1] = this.cpBands.cpClassValue(exceptions[i1]);
/*      */           }
/*  330 */           this.methodAttributes[j][m].add(new ExceptionsAttribute(exceptionClasses));
/*  331 */           methodExceptionsIndex++;
/*      */         } 
/*  333 */         if (methodSignatureLayout.matches(flag)) {
/*      */           
/*  335 */           long result = methodSignatureRS[methodSignatureIndex];
/*  336 */           String desc = this.methodDescr[j][m];
/*  337 */           int colon = desc.indexOf(':');
/*  338 */           String type = desc.substring(colon + 1);
/*      */ 
/*      */           
/*  341 */           if (type.equals("B") || type.equals("H")) {
/*  342 */             type = "I";
/*      */           }
/*  344 */           CPUTF8 value = (CPUTF8)methodSignatureLayout.getValue(result, type, this.cpBands
/*  345 */               .getConstantPool());
/*  346 */           this.methodAttributes[j][m].add(new SignatureAttribute(value));
/*  347 */           methodSignatureIndex++;
/*      */         } 
/*  349 */         if (deprecatedLayout.matches(flag)) {
/*  350 */           this.methodAttributes[j][m].add(new DeprecatedAttribute());
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  356 */     int backwardsCallsUsed = parseMethodMetadataBands(in, this.methodAttrCalls);
/*      */ 
/*      */     
/*  359 */     int backwardsCallIndex = backwardsCallsUsed;
/*  360 */     int limit = this.options.hasMethodFlagsHi() ? 62 : 31;
/*  361 */     AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
/*  362 */     int[] counts = new int[limit + 1];
/*  363 */     List[] otherAttributes = new List[limit + 1]; int k;
/*  364 */     for (k = 0; k < limit; k++) {
/*  365 */       AttributeLayout layout = this.attrMap.getAttributeLayout(k, 2);
/*  366 */       if (layout != null && !layout.isDefaultLayout()) {
/*  367 */         otherLayouts[k] = layout;
/*  368 */         counts[k] = SegmentUtils.countMatches(this.methodFlags, layout);
/*      */       } 
/*      */     } 
/*  371 */     for (k = 0; k < counts.length; k++) {
/*  372 */       if (counts[k] > 0) {
/*  373 */         NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[k]);
/*  374 */         otherAttributes[k] = bands.parseAttributes(in, counts[k]);
/*  375 */         int numBackwardsCallables = otherLayouts[k].numBackwardsCallables();
/*  376 */         if (numBackwardsCallables > 0) {
/*  377 */           int[] backwardsCalls = new int[numBackwardsCallables];
/*  378 */           System.arraycopy(this.methodAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
/*  379 */           bands.setBackwardsCalls(backwardsCalls);
/*  380 */           backwardsCallIndex += numBackwardsCallables;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  386 */     for (k = 0; k < this.methodAttributes.length; k++) {
/*  387 */       for (int m = 0; m < (this.methodAttributes[k]).length; m++) {
/*  388 */         long flag = this.methodFlags[k][m];
/*  389 */         int othersAddedAtStart = 0;
/*  390 */         for (int n = 0; n < otherLayouts.length; n++) {
/*  391 */           if (otherLayouts[n] != null && otherLayouts[n].matches(flag)) {
/*      */             
/*  393 */             if (otherLayouts[n].getIndex() < 15) {
/*  394 */               this.methodAttributes[k][m].add(othersAddedAtStart++, otherAttributes[n].get(0));
/*      */             } else {
/*  396 */               this.methodAttributes[k][m].add(otherAttributes[n].get(0));
/*      */             } 
/*  398 */             otherAttributes[n].remove(0);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private int getCallCount(int[][] methodAttrIndexes, long[][] flags, int context) throws Pack200Exception {
/*  407 */     int callCount = 0;
/*  408 */     for (int i = 0; i < methodAttrIndexes.length; i++) {
/*  409 */       for (int k = 0; k < (methodAttrIndexes[i]).length; k++) {
/*  410 */         int index = methodAttrIndexes[i][k];
/*  411 */         AttributeLayout layout = this.attrMap.getAttributeLayout(index, context);
/*  412 */         callCount += layout.numBackwardsCallables();
/*      */       } 
/*      */     } 
/*  415 */     int layoutsUsed = 0; int j;
/*  416 */     for (j = 0; j < flags.length; j++) {
/*  417 */       for (int k = 0; k < (flags[j]).length; k++) {
/*  418 */         layoutsUsed = (int)(layoutsUsed | flags[j][k]);
/*      */       }
/*      */     } 
/*  421 */     for (j = 0; j < 26; j++) {
/*  422 */       if ((layoutsUsed & 1 << j) != 0) {
/*  423 */         AttributeLayout layout = this.attrMap.getAttributeLayout(j, context);
/*  424 */         callCount += layout.numBackwardsCallables();
/*      */       } 
/*      */     } 
/*  427 */     return callCount;
/*      */   }
/*      */   
/*      */   private void parseClassAttrBands(InputStream in) throws IOException, Pack200Exception {
/*  431 */     String[] cpUTF8 = this.cpBands.getCpUTF8();
/*  432 */     String[] cpClass = this.cpBands.getCpClass();
/*      */ 
/*      */     
/*  435 */     this.classAttributes = new ArrayList[this.classCount];
/*  436 */     for (int i = 0; i < this.classCount; i++) {
/*  437 */       this.classAttributes[i] = new ArrayList();
/*      */     }
/*      */     
/*  440 */     this.classFlags = parseFlags("class_flags", in, this.classCount, Codec.UNSIGNED5, this.options.hasClassFlagsHi());
/*  441 */     int classAttrCount = SegmentUtils.countBit16(this.classFlags);
/*  442 */     int[] classAttrCounts = decodeBandInt("class_attr_count", in, Codec.UNSIGNED5, classAttrCount);
/*  443 */     int[][] classAttrIndexes = decodeBandInt("class_attr_indexes", in, Codec.UNSIGNED5, classAttrCounts);
/*  444 */     int callCount = getCallCount(classAttrIndexes, new long[][] { this.classFlags }, 0);
/*  445 */     int[] classAttrCalls = decodeBandInt("class_attr_calls", in, Codec.UNSIGNED5, callCount);
/*      */     
/*  447 */     AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 0);
/*      */ 
/*      */     
/*  450 */     AttributeLayout sourceFileLayout = this.attrMap.getAttributeLayout("SourceFile", 0);
/*      */     
/*  452 */     int sourceFileCount = SegmentUtils.countMatches(this.classFlags, sourceFileLayout);
/*  453 */     int[] classSourceFile = decodeBandInt("class_SourceFile_RUN", in, Codec.UNSIGNED5, sourceFileCount);
/*      */ 
/*      */     
/*  456 */     AttributeLayout enclosingMethodLayout = this.attrMap.getAttributeLayout("EnclosingMethod", 0);
/*  457 */     int enclosingMethodCount = SegmentUtils.countMatches(this.classFlags, enclosingMethodLayout);
/*  458 */     int[] enclosingMethodRC = decodeBandInt("class_EnclosingMethod_RC", in, Codec.UNSIGNED5, enclosingMethodCount);
/*      */     
/*  460 */     int[] enclosingMethodRDN = decodeBandInt("class_EnclosingMethod_RDN", in, Codec.UNSIGNED5, enclosingMethodCount);
/*      */ 
/*      */     
/*  463 */     AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 0);
/*      */     
/*  465 */     int signatureCount = SegmentUtils.countMatches(this.classFlags, signatureLayout);
/*  466 */     int[] classSignature = decodeBandInt("class_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
/*      */     
/*  468 */     int backwardsCallsUsed = parseClassMetadataBands(in, classAttrCalls);
/*      */     
/*  470 */     AttributeLayout innerClassLayout = this.attrMap.getAttributeLayout("InnerClasses", 0);
/*      */     
/*  472 */     int innerClassCount = SegmentUtils.countMatches(this.classFlags, innerClassLayout);
/*  473 */     int[] classInnerClassesN = decodeBandInt("class_InnerClasses_N", in, Codec.UNSIGNED5, innerClassCount);
/*  474 */     int[][] classInnerClassesRC = decodeBandInt("class_InnerClasses_RC", in, Codec.UNSIGNED5, classInnerClassesN);
/*      */     
/*  476 */     int[][] classInnerClassesF = decodeBandInt("class_InnerClasses_F", in, Codec.UNSIGNED5, classInnerClassesN);
/*      */     
/*  478 */     int flagsCount = 0;
/*  479 */     for (int j = 0; j < classInnerClassesF.length; j++) {
/*  480 */       for (int n = 0; n < (classInnerClassesF[j]).length; n++) {
/*  481 */         if (classInnerClassesF[j][n] != 0) {
/*  482 */           flagsCount++;
/*      */         }
/*      */       } 
/*      */     } 
/*  486 */     int[] classInnerClassesOuterRCN = decodeBandInt("class_InnerClasses_outer_RCN", in, Codec.UNSIGNED5, flagsCount);
/*      */     
/*  488 */     int[] classInnerClassesNameRUN = decodeBandInt("class_InnerClasses_name_RUN", in, Codec.UNSIGNED5, flagsCount);
/*      */ 
/*      */     
/*  491 */     AttributeLayout versionLayout = this.attrMap.getAttributeLayout("class-file version", 0);
/*      */     
/*  493 */     int versionCount = SegmentUtils.countMatches(this.classFlags, versionLayout);
/*  494 */     int[] classFileVersionMinorH = decodeBandInt("class_file_version_minor_H", in, Codec.UNSIGNED5, versionCount);
/*      */     
/*  496 */     int[] classFileVersionMajorH = decodeBandInt("class_file_version_major_H", in, Codec.UNSIGNED5, versionCount);
/*      */     
/*  498 */     if (versionCount > 0) {
/*  499 */       this.classVersionMajor = new int[this.classCount];
/*  500 */       this.classVersionMinor = new int[this.classCount];
/*      */     } 
/*  502 */     int defaultVersionMajor = this.header.getDefaultClassMajorVersion();
/*  503 */     int defaultVersionMinor = this.header.getDefaultClassMinorVersion();
/*      */ 
/*      */     
/*  506 */     int backwardsCallIndex = backwardsCallsUsed;
/*  507 */     int limit = this.options.hasClassFlagsHi() ? 62 : 31;
/*  508 */     AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
/*  509 */     int[] counts = new int[limit + 1];
/*  510 */     List[] otherAttributes = new List[limit + 1]; int k;
/*  511 */     for (k = 0; k < limit; k++) {
/*  512 */       AttributeLayout layout = this.attrMap.getAttributeLayout(k, 0);
/*  513 */       if (layout != null && !layout.isDefaultLayout()) {
/*  514 */         otherLayouts[k] = layout;
/*  515 */         counts[k] = SegmentUtils.countMatches(this.classFlags, layout);
/*      */       } 
/*      */     } 
/*  518 */     for (k = 0; k < counts.length; k++) {
/*  519 */       if (counts[k] > 0) {
/*  520 */         NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[k]);
/*  521 */         otherAttributes[k] = bands.parseAttributes(in, counts[k]);
/*  522 */         int numBackwardsCallables = otherLayouts[k].numBackwardsCallables();
/*  523 */         if (numBackwardsCallables > 0) {
/*  524 */           int[] backwardsCalls = new int[numBackwardsCallables];
/*  525 */           System.arraycopy(classAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
/*  526 */           bands.setBackwardsCalls(backwardsCalls);
/*  527 */           backwardsCallIndex += numBackwardsCallables;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  533 */     int sourceFileIndex = 0;
/*  534 */     int enclosingMethodIndex = 0;
/*  535 */     int signatureIndex = 0;
/*  536 */     int innerClassIndex = 0;
/*  537 */     int innerClassC2NIndex = 0;
/*  538 */     int versionIndex = 0;
/*  539 */     this.icLocal = new IcTuple[this.classCount][];
/*  540 */     for (int m = 0; m < this.classCount; m++) {
/*  541 */       long flag = this.classFlags[m];
/*  542 */       if (deprecatedLayout.matches(this.classFlags[m])) {
/*  543 */         this.classAttributes[m].add(new DeprecatedAttribute());
/*      */       }
/*  545 */       if (sourceFileLayout.matches(flag)) {
/*  546 */         CPUTF8 cPUTF8; long result = classSourceFile[sourceFileIndex];
/*  547 */         ClassFileEntry value = sourceFileLayout.getValue(result, this.cpBands.getConstantPool());
/*  548 */         if (value == null) {
/*      */           
/*  550 */           String className = this.classThis[m].substring(this.classThis[m].lastIndexOf('/') + 1);
/*  551 */           className = className.substring(className.lastIndexOf('.') + 1);
/*      */ 
/*      */           
/*  554 */           char[] chars = className.toCharArray();
/*  555 */           int index = -1;
/*  556 */           for (int i1 = 0; i1 < chars.length; i1++) {
/*  557 */             if (chars[i1] <= '-') {
/*  558 */               index = i1;
/*      */               break;
/*      */             } 
/*      */           } 
/*  562 */           if (index > -1) {
/*  563 */             className = className.substring(0, index);
/*      */           }
/*      */           
/*  566 */           cPUTF8 = this.cpBands.cpUTF8Value(className + ".java", true);
/*      */         } 
/*  568 */         this.classAttributes[m].add(new SourceFileAttribute(cPUTF8));
/*  569 */         sourceFileIndex++;
/*      */       } 
/*  571 */       if (enclosingMethodLayout.matches(flag)) {
/*  572 */         CPClass theClass = this.cpBands.cpClassValue(enclosingMethodRC[enclosingMethodIndex]);
/*  573 */         CPNameAndType theMethod = null;
/*  574 */         if (enclosingMethodRDN[enclosingMethodIndex] != 0) {
/*  575 */           theMethod = this.cpBands.cpNameAndTypeValue(enclosingMethodRDN[enclosingMethodIndex] - 1);
/*      */         }
/*  577 */         this.classAttributes[m].add(new EnclosingMethodAttribute(theClass, theMethod));
/*  578 */         enclosingMethodIndex++;
/*      */       } 
/*  580 */       if (signatureLayout.matches(flag)) {
/*  581 */         long result = classSignature[signatureIndex];
/*  582 */         CPUTF8 value = (CPUTF8)signatureLayout.getValue(result, this.cpBands.getConstantPool());
/*  583 */         this.classAttributes[m].add(new SignatureAttribute(value));
/*  584 */         signatureIndex++;
/*      */       } 
/*  586 */       if (innerClassLayout.matches(flag)) {
/*      */ 
/*      */         
/*  589 */         this.icLocal[m] = new IcTuple[classInnerClassesN[innerClassIndex]];
/*  590 */         for (int i1 = 0; i1 < (this.icLocal[m]).length; i1++) {
/*  591 */           int icTupleCIndex = classInnerClassesRC[innerClassIndex][i1];
/*  592 */           int icTupleC2Index = -1;
/*  593 */           int icTupleNIndex = -1;
/*      */           
/*  595 */           String icTupleC = cpClass[icTupleCIndex];
/*  596 */           int icTupleF = classInnerClassesF[innerClassIndex][i1];
/*  597 */           String icTupleC2 = null;
/*  598 */           String icTupleN = null;
/*      */           
/*  600 */           if (icTupleF != 0) {
/*  601 */             icTupleC2Index = classInnerClassesOuterRCN[innerClassC2NIndex];
/*  602 */             icTupleNIndex = classInnerClassesNameRUN[innerClassC2NIndex];
/*  603 */             icTupleC2 = cpClass[icTupleC2Index];
/*  604 */             icTupleN = cpUTF8[icTupleNIndex];
/*  605 */             innerClassC2NIndex++;
/*      */           } else {
/*      */             
/*  608 */             IcBands icBands = this.segment.getIcBands();
/*  609 */             IcTuple[] icAll = icBands.getIcTuples();
/*  610 */             for (int i2 = 0; i2 < icAll.length; i2++) {
/*  611 */               if (icAll[i2].getC().equals(icTupleC)) {
/*  612 */                 icTupleF = icAll[i2].getF();
/*  613 */                 icTupleC2 = icAll[i2].getC2();
/*  614 */                 icTupleN = icAll[i2].getN();
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/*  620 */           IcTuple icTuple = new IcTuple(icTupleC, icTupleF, icTupleC2, icTupleN, icTupleCIndex, icTupleC2Index, icTupleNIndex, i1);
/*      */           
/*  622 */           this.icLocal[m][i1] = icTuple;
/*      */         } 
/*  624 */         innerClassIndex++;
/*      */       } 
/*  626 */       if (versionLayout.matches(flag)) {
/*  627 */         this.classVersionMajor[m] = classFileVersionMajorH[versionIndex];
/*  628 */         this.classVersionMinor[m] = classFileVersionMinorH[versionIndex];
/*  629 */         versionIndex++;
/*  630 */       } else if (this.classVersionMajor != null) {
/*      */         
/*  632 */         this.classVersionMajor[m] = defaultVersionMajor;
/*  633 */         this.classVersionMinor[m] = defaultVersionMinor;
/*      */       } 
/*      */       
/*  636 */       for (int n = 0; n < otherLayouts.length; n++) {
/*  637 */         if (otherLayouts[n] != null && otherLayouts[n].matches(flag)) {
/*      */           
/*  639 */           this.classAttributes[m].add(otherAttributes[n].get(0));
/*  640 */           otherAttributes[n].remove(0);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void parseCodeBands(InputStream in) throws Pack200Exception, IOException {
/*  647 */     AttributeLayout layout = this.attrMap.getAttributeLayout("Code", 2);
/*      */ 
/*      */     
/*  650 */     int codeCount = SegmentUtils.countMatches(this.methodFlags, layout);
/*  651 */     int[] codeHeaders = decodeBandInt("code_headers", in, Codec.BYTE1, codeCount);
/*      */     
/*  653 */     boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
/*  654 */     if (!allCodeHasFlags) {
/*  655 */       this.codeHasAttributes = new boolean[codeCount];
/*      */     }
/*  657 */     int codeSpecialHeader = 0;
/*  658 */     for (int i = 0; i < codeCount; i++) {
/*  659 */       if (codeHeaders[i] == 0) {
/*  660 */         codeSpecialHeader++;
/*  661 */         if (!allCodeHasFlags) {
/*  662 */           this.codeHasAttributes[i] = true;
/*      */         }
/*      */       } 
/*      */     } 
/*  666 */     int[] codeMaxStackSpecials = decodeBandInt("code_max_stack", in, Codec.UNSIGNED5, codeSpecialHeader);
/*  667 */     int[] codeMaxNALocalsSpecials = decodeBandInt("code_max_na_locals", in, Codec.UNSIGNED5, codeSpecialHeader);
/*      */     
/*  669 */     int[] codeHandlerCountSpecials = decodeBandInt("code_handler_count", in, Codec.UNSIGNED5, codeSpecialHeader);
/*      */ 
/*      */     
/*  672 */     this.codeMaxStack = new int[codeCount];
/*  673 */     this.codeMaxNALocals = new int[codeCount];
/*  674 */     this.codeHandlerCount = new int[codeCount];
/*  675 */     int special = 0;
/*  676 */     for (int j = 0; j < codeCount; j++) {
/*  677 */       int header = 0xFF & codeHeaders[j];
/*  678 */       if (header < 0) {
/*  679 */         throw new IllegalStateException("Shouldn't get here");
/*      */       }
/*  681 */       if (header == 0) {
/*  682 */         this.codeMaxStack[j] = codeMaxStackSpecials[special];
/*  683 */         this.codeMaxNALocals[j] = codeMaxNALocalsSpecials[special];
/*  684 */         this.codeHandlerCount[j] = codeHandlerCountSpecials[special];
/*  685 */         special++;
/*  686 */       } else if (header <= 144) {
/*  687 */         this.codeMaxStack[j] = (header - 1) % 12;
/*  688 */         this.codeMaxNALocals[j] = (header - 1) / 12;
/*  689 */         this.codeHandlerCount[j] = 0;
/*  690 */       } else if (header <= 208) {
/*  691 */         this.codeMaxStack[j] = (header - 145) % 8;
/*  692 */         this.codeMaxNALocals[j] = (header - 145) / 8;
/*  693 */         this.codeHandlerCount[j] = 1;
/*  694 */       } else if (header <= 255) {
/*  695 */         this.codeMaxStack[j] = (header - 209) % 7;
/*  696 */         this.codeMaxNALocals[j] = (header - 209) / 7;
/*  697 */         this.codeHandlerCount[j] = 2;
/*      */       } else {
/*  699 */         throw new IllegalStateException("Shouldn't get here either");
/*      */       } 
/*      */     } 
/*  702 */     this.codeHandlerStartP = decodeBandInt("code_handler_start_P", in, Codec.BCI5, this.codeHandlerCount);
/*  703 */     this.codeHandlerEndPO = decodeBandInt("code_handler_end_PO", in, Codec.BRANCH5, this.codeHandlerCount);
/*  704 */     this.codeHandlerCatchPO = decodeBandInt("code_handler_catch_PO", in, Codec.BRANCH5, this.codeHandlerCount);
/*  705 */     this.codeHandlerClassRCN = decodeBandInt("code_handler_class_RCN", in, Codec.UNSIGNED5, this.codeHandlerCount);
/*      */     
/*  707 */     int codeFlagsCount = allCodeHasFlags ? codeCount : codeSpecialHeader;
/*      */     
/*  709 */     this.codeAttributes = new List[codeFlagsCount];
/*  710 */     for (int k = 0; k < this.codeAttributes.length; k++) {
/*  711 */       this.codeAttributes[k] = new ArrayList();
/*      */     }
/*  713 */     parseCodeAttrBands(in, codeFlagsCount);
/*      */   }
/*      */ 
/*      */   
/*      */   private void parseCodeAttrBands(InputStream in, int codeFlagsCount) throws IOException, Pack200Exception {
/*  718 */     long[] codeFlags = parseFlags("code_flags", in, codeFlagsCount, Codec.UNSIGNED5, this.segment
/*  719 */         .getSegmentHeader().getOptions().hasCodeFlagsHi());
/*  720 */     int codeAttrCount = SegmentUtils.countBit16(codeFlags);
/*  721 */     int[] codeAttrCounts = decodeBandInt("code_attr_count", in, Codec.UNSIGNED5, codeAttrCount);
/*  722 */     int[][] codeAttrIndexes = decodeBandInt("code_attr_indexes", in, Codec.UNSIGNED5, codeAttrCounts);
/*  723 */     int callCount = 0;
/*  724 */     for (int i = 0; i < codeAttrIndexes.length; i++) {
/*  725 */       for (int m = 0; m < (codeAttrIndexes[i]).length; m++) {
/*  726 */         int index = codeAttrIndexes[i][m];
/*  727 */         AttributeLayout layout = this.attrMap.getAttributeLayout(index, 3);
/*  728 */         callCount += layout.numBackwardsCallables();
/*      */       } 
/*      */     } 
/*  731 */     int[] codeAttrCalls = decodeBandInt("code_attr_calls", in, Codec.UNSIGNED5, callCount);
/*      */ 
/*      */     
/*  734 */     AttributeLayout lineNumberTableLayout = this.attrMap.getAttributeLayout("LineNumberTable", 3);
/*  735 */     int lineNumberTableCount = SegmentUtils.countMatches(codeFlags, lineNumberTableLayout);
/*  736 */     int[] lineNumberTableN = decodeBandInt("code_LineNumberTable_N", in, Codec.UNSIGNED5, lineNumberTableCount);
/*      */     
/*  738 */     int[][] lineNumberTableBciP = decodeBandInt("code_LineNumberTable_bci_P", in, Codec.BCI5, lineNumberTableN);
/*      */     
/*  740 */     int[][] lineNumberTableLine = decodeBandInt("code_LineNumberTable_line", in, Codec.UNSIGNED5, lineNumberTableN);
/*      */ 
/*      */ 
/*      */     
/*  744 */     AttributeLayout localVariableTableLayout = this.attrMap.getAttributeLayout("LocalVariableTable", 3);
/*      */     
/*  746 */     AttributeLayout localVariableTypeTableLayout = this.attrMap.getAttributeLayout("LocalVariableTypeTable", 3);
/*      */     
/*  748 */     int lengthLocalVariableNBand = SegmentUtils.countMatches(codeFlags, localVariableTableLayout);
/*  749 */     int[] localVariableTableN = decodeBandInt("code_LocalVariableTable_N", in, Codec.UNSIGNED5, lengthLocalVariableNBand);
/*      */     
/*  751 */     int[][] localVariableTableBciP = decodeBandInt("code_LocalVariableTable_bci_P", in, Codec.BCI5, localVariableTableN);
/*      */     
/*  753 */     int[][] localVariableTableSpanO = decodeBandInt("code_LocalVariableTable_span_O", in, Codec.BRANCH5, localVariableTableN);
/*      */     
/*  755 */     CPUTF8[][] localVariableTableNameRU = parseCPUTF8References("code_LocalVariableTable_name_RU", in, Codec.UNSIGNED5, localVariableTableN);
/*      */     
/*  757 */     CPUTF8[][] localVariableTableTypeRS = parseCPSignatureReferences("code_LocalVariableTable_type_RS", in, Codec.UNSIGNED5, localVariableTableN);
/*      */     
/*  759 */     int[][] localVariableTableSlot = decodeBandInt("code_LocalVariableTable_slot", in, Codec.UNSIGNED5, localVariableTableN);
/*      */ 
/*      */     
/*  762 */     int lengthLocalVariableTypeTableNBand = SegmentUtils.countMatches(codeFlags, localVariableTypeTableLayout);
/*      */     
/*  764 */     int[] localVariableTypeTableN = decodeBandInt("code_LocalVariableTypeTable_N", in, Codec.UNSIGNED5, lengthLocalVariableTypeTableNBand);
/*      */     
/*  766 */     int[][] localVariableTypeTableBciP = decodeBandInt("code_LocalVariableTypeTable_bci_P", in, Codec.BCI5, localVariableTypeTableN);
/*      */     
/*  768 */     int[][] localVariableTypeTableSpanO = decodeBandInt("code_LocalVariableTypeTable_span_O", in, Codec.BRANCH5, localVariableTypeTableN);
/*      */     
/*  770 */     CPUTF8[][] localVariableTypeTableNameRU = parseCPUTF8References("code_LocalVariableTypeTable_name_RU", in, Codec.UNSIGNED5, localVariableTypeTableN);
/*      */     
/*  772 */     CPUTF8[][] localVariableTypeTableTypeRS = parseCPSignatureReferences("code_LocalVariableTypeTable_type_RS", in, Codec.UNSIGNED5, localVariableTypeTableN);
/*      */     
/*  774 */     int[][] localVariableTypeTableSlot = decodeBandInt("code_LocalVariableTypeTable_slot", in, Codec.UNSIGNED5, localVariableTypeTableN);
/*      */ 
/*      */ 
/*      */     
/*  778 */     int backwardsCallIndex = 0;
/*  779 */     int limit = this.options.hasCodeFlagsHi() ? 62 : 31;
/*  780 */     AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
/*  781 */     int[] counts = new int[limit + 1];
/*  782 */     List[] otherAttributes = new List[limit + 1]; int j;
/*  783 */     for (j = 0; j < limit; j++) {
/*  784 */       AttributeLayout layout = this.attrMap.getAttributeLayout(j, 3);
/*  785 */       if (layout != null && !layout.isDefaultLayout()) {
/*  786 */         otherLayouts[j] = layout;
/*  787 */         counts[j] = SegmentUtils.countMatches(codeFlags, layout);
/*      */       } 
/*      */     } 
/*  790 */     for (j = 0; j < counts.length; j++) {
/*  791 */       if (counts[j] > 0) {
/*  792 */         NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[j]);
/*  793 */         otherAttributes[j] = bands.parseAttributes(in, counts[j]);
/*  794 */         int numBackwardsCallables = otherLayouts[j].numBackwardsCallables();
/*  795 */         if (numBackwardsCallables > 0) {
/*  796 */           int[] backwardsCalls = new int[numBackwardsCallables];
/*  797 */           System.arraycopy(codeAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
/*  798 */           bands.setBackwardsCalls(backwardsCalls);
/*  799 */           backwardsCallIndex += numBackwardsCallables;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  804 */     int lineNumberIndex = 0;
/*  805 */     int lvtIndex = 0;
/*  806 */     int lvttIndex = 0;
/*  807 */     for (int k = 0; k < codeFlagsCount; k++) {
/*  808 */       if (lineNumberTableLayout.matches(codeFlags[k])) {
/*  809 */         LineNumberTableAttribute lnta = new LineNumberTableAttribute(lineNumberTableN[lineNumberIndex], lineNumberTableBciP[lineNumberIndex], lineNumberTableLine[lineNumberIndex]);
/*      */         
/*  811 */         lineNumberIndex++;
/*  812 */         this.codeAttributes[k].add(lnta);
/*      */       } 
/*  814 */       if (localVariableTableLayout.matches(codeFlags[k])) {
/*  815 */         LocalVariableTableAttribute lvta = new LocalVariableTableAttribute(localVariableTableN[lvtIndex], localVariableTableBciP[lvtIndex], localVariableTableSpanO[lvtIndex], localVariableTableNameRU[lvtIndex], localVariableTableTypeRS[lvtIndex], localVariableTableSlot[lvtIndex]);
/*      */ 
/*      */ 
/*      */         
/*  819 */         lvtIndex++;
/*  820 */         this.codeAttributes[k].add(lvta);
/*      */       } 
/*  822 */       if (localVariableTypeTableLayout.matches(codeFlags[k])) {
/*  823 */         LocalVariableTypeTableAttribute lvtta = new LocalVariableTypeTableAttribute(localVariableTypeTableN[lvttIndex], localVariableTypeTableBciP[lvttIndex], localVariableTypeTableSpanO[lvttIndex], localVariableTypeTableNameRU[lvttIndex], localVariableTypeTableTypeRS[lvttIndex], localVariableTypeTableSlot[lvttIndex]);
/*      */ 
/*      */ 
/*      */         
/*  827 */         lvttIndex++;
/*  828 */         this.codeAttributes[k].add(lvtta);
/*      */       } 
/*      */       
/*  831 */       for (int m = 0; m < otherLayouts.length; m++) {
/*  832 */         if (otherLayouts[m] != null && otherLayouts[m].matches(codeFlags[k])) {
/*      */           
/*  834 */           this.codeAttributes[k].add(otherAttributes[m].get(0));
/*  835 */           otherAttributes[m].remove(0);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int parseFieldMetadataBands(InputStream in, int[] fieldAttrCalls) throws Pack200Exception, IOException {
/*  844 */     int backwardsCallsUsed = 0;
/*  845 */     String[] RxA = { "RVA", "RIA" };
/*      */ 
/*      */     
/*  848 */     AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 1);
/*      */     
/*  850 */     AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 1);
/*      */     
/*  852 */     int rvaCount = SegmentUtils.countMatches(this.fieldFlags, rvaLayout);
/*  853 */     int riaCount = SegmentUtils.countMatches(this.fieldFlags, riaLayout);
/*  854 */     int[] RxACount = { rvaCount, riaCount };
/*  855 */     int[] backwardsCalls = { 0, 0 };
/*  856 */     if (rvaCount > 0) {
/*  857 */       backwardsCalls[0] = fieldAttrCalls[0];
/*  858 */       backwardsCallsUsed++;
/*  859 */       if (riaCount > 0) {
/*  860 */         backwardsCalls[1] = fieldAttrCalls[1];
/*  861 */         backwardsCallsUsed++;
/*      */       } 
/*  863 */     } else if (riaCount > 0) {
/*  864 */       backwardsCalls[1] = fieldAttrCalls[0];
/*  865 */       backwardsCallsUsed++;
/*      */     } 
/*  867 */     MetadataBandGroup[] mb = parseMetadata(in, RxA, RxACount, backwardsCalls, "field");
/*  868 */     List rvaAttributes = mb[0].getAttributes();
/*  869 */     List riaAttributes = mb[1].getAttributes();
/*  870 */     int rvaAttributesIndex = 0;
/*  871 */     int riaAttributesIndex = 0;
/*  872 */     for (int i = 0; i < this.fieldFlags.length; i++) {
/*  873 */       for (int j = 0; j < (this.fieldFlags[i]).length; j++) {
/*  874 */         if (rvaLayout.matches(this.fieldFlags[i][j])) {
/*  875 */           this.fieldAttributes[i][j].add(rvaAttributes.get(rvaAttributesIndex++));
/*      */         }
/*  877 */         if (riaLayout.matches(this.fieldFlags[i][j])) {
/*  878 */           this.fieldAttributes[i][j].add(riaAttributes.get(riaAttributesIndex++));
/*      */         }
/*      */       } 
/*      */     } 
/*  882 */     return backwardsCallsUsed;
/*      */   }
/*      */ 
/*      */   
/*      */   private MetadataBandGroup[] parseMetadata(InputStream in, String[] RxA, int[] RxACount, int[] backwardsCallCounts, String contextName) throws IOException, Pack200Exception {
/*  887 */     MetadataBandGroup[] mbg = new MetadataBandGroup[RxA.length];
/*  888 */     for (int i = 0; i < RxA.length; i++) {
/*  889 */       mbg[i] = new MetadataBandGroup(RxA[i], this.cpBands);
/*  890 */       String rxa = RxA[i];
/*  891 */       if (rxa.indexOf('P') >= 0) {
/*  892 */         (mbg[i]).param_NB = decodeBandInt(contextName + "_" + rxa + "_param_NB", in, Codec.BYTE1, RxACount[i]);
/*      */       }
/*  894 */       int pairCount = 0;
/*  895 */       if (!rxa.equals("AD")) {
/*  896 */         (mbg[i]).anno_N = decodeBandInt(contextName + "_" + rxa + "_anno_N", in, Codec.UNSIGNED5, RxACount[i]);
/*  897 */         (mbg[i]).type_RS = parseCPSignatureReferences(contextName + "_" + rxa + "_type_RS", in, Codec.UNSIGNED5, (mbg[i]).anno_N);
/*      */         
/*  899 */         (mbg[i]).pair_N = decodeBandInt(contextName + "_" + rxa + "_pair_N", in, Codec.UNSIGNED5, (mbg[i]).anno_N);
/*  900 */         for (int m = 0; m < (mbg[i]).pair_N.length; m++) {
/*  901 */           for (int n = 0; n < ((mbg[i]).pair_N[m]).length; n++) {
/*  902 */             pairCount += (mbg[i]).pair_N[m][n];
/*      */           }
/*      */         } 
/*      */         
/*  906 */         (mbg[i]).name_RU = parseCPUTF8References(contextName + "_" + rxa + "_name_RU", in, Codec.UNSIGNED5, pairCount);
/*      */       } else {
/*      */         
/*  909 */         pairCount = RxACount[i];
/*      */       } 
/*  911 */       (mbg[i]).T = decodeBandInt(contextName + "_" + rxa + "_T", in, Codec.BYTE1, pairCount + backwardsCallCounts[i]);
/*      */       
/*  913 */       int ICount = 0, DCount = 0, FCount = 0, JCount = 0, cCount = 0, eCount = 0, sCount = 0, arrayCount = 0;
/*  914 */       int atCount = 0;
/*  915 */       for (int j = 0; j < (mbg[i]).T.length; j++) {
/*  916 */         char c = (char)(mbg[i]).T[j];
/*  917 */         switch (c) {
/*      */           case 'B':
/*      */           case 'C':
/*      */           case 'I':
/*      */           case 'S':
/*      */           case 'Z':
/*  923 */             ICount++;
/*      */             break;
/*      */           case 'D':
/*  926 */             DCount++;
/*      */             break;
/*      */           case 'F':
/*  929 */             FCount++;
/*      */             break;
/*      */           case 'J':
/*  932 */             JCount++;
/*      */             break;
/*      */           case 'c':
/*  935 */             cCount++;
/*      */             break;
/*      */           case 'e':
/*  938 */             eCount++;
/*      */             break;
/*      */           case 's':
/*  941 */             sCount++;
/*      */             break;
/*      */           case '[':
/*  944 */             arrayCount++;
/*      */             break;
/*      */           case '@':
/*  947 */             atCount++;
/*      */             break;
/*      */         } 
/*      */       } 
/*  951 */       (mbg[i]).caseI_KI = parseCPIntReferences(contextName + "_" + rxa + "_caseI_KI", in, Codec.UNSIGNED5, ICount);
/*  952 */       (mbg[i]).caseD_KD = parseCPDoubleReferences(contextName + "_" + rxa + "_caseD_KD", in, Codec.UNSIGNED5, DCount);
/*      */       
/*  954 */       (mbg[i]).caseF_KF = parseCPFloatReferences(contextName + "_" + rxa + "_caseF_KF", in, Codec.UNSIGNED5, FCount);
/*      */       
/*  956 */       (mbg[i]).caseJ_KJ = parseCPLongReferences(contextName + "_" + rxa + "_caseJ_KJ", in, Codec.UNSIGNED5, JCount);
/*  957 */       (mbg[i]).casec_RS = parseCPSignatureReferences(contextName + "_" + rxa + "_casec_RS", in, Codec.UNSIGNED5, cCount);
/*      */       
/*  959 */       (mbg[i]).caseet_RS = parseReferences(contextName + "_" + rxa + "_caseet_RS", in, Codec.UNSIGNED5, eCount, this.cpBands
/*  960 */           .getCpSignature());
/*  961 */       (mbg[i]).caseec_RU = parseReferences(contextName + "_" + rxa + "_caseec_RU", in, Codec.UNSIGNED5, eCount, this.cpBands
/*  962 */           .getCpUTF8());
/*  963 */       (mbg[i]).cases_RU = parseCPUTF8References(contextName + "_" + rxa + "_cases_RU", in, Codec.UNSIGNED5, sCount);
/*  964 */       (mbg[i]).casearray_N = decodeBandInt(contextName + "_" + rxa + "_casearray_N", in, Codec.UNSIGNED5, arrayCount);
/*      */       
/*  966 */       (mbg[i]).nesttype_RS = parseCPUTF8References(contextName + "_" + rxa + "_nesttype_RS", in, Codec.UNSIGNED5, atCount);
/*      */       
/*  968 */       (mbg[i]).nestpair_N = decodeBandInt(contextName + "_" + rxa + "_nestpair_N", in, Codec.UNSIGNED5, atCount);
/*  969 */       int nestPairCount = 0;
/*  970 */       for (int k = 0; k < (mbg[i]).nestpair_N.length; k++) {
/*  971 */         nestPairCount += (mbg[i]).nestpair_N[k];
/*      */       }
/*  973 */       (mbg[i]).nestname_RU = parseCPUTF8References(contextName + "_" + rxa + "_nestname_RU", in, Codec.UNSIGNED5, nestPairCount);
/*      */     } 
/*      */     
/*  976 */     return mbg;
/*      */   }
/*      */ 
/*      */   
/*      */   private int parseMethodMetadataBands(InputStream in, int[] methodAttrCalls) throws Pack200Exception, IOException {
/*  981 */     int backwardsCallsUsed = 0;
/*  982 */     String[] RxA = { "RVA", "RIA", "RVPA", "RIPA", "AD" };
/*  983 */     int[] rxaCounts = { 0, 0, 0, 0, 0 };
/*      */ 
/*      */     
/*  986 */     AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 2);
/*  987 */     AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 2);
/*      */     
/*  989 */     AttributeLayout rvpaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleParameterAnnotations", 2);
/*      */     
/*  991 */     AttributeLayout ripaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleParameterAnnotations", 2);
/*      */     
/*  993 */     AttributeLayout adLayout = this.attrMap.getAttributeLayout("AnnotationDefault", 2);
/*      */     
/*  995 */     AttributeLayout[] rxaLayouts = { rvaLayout, riaLayout, rvpaLayout, ripaLayout, adLayout };
/*      */     
/*  997 */     for (int i = 0; i < rxaLayouts.length; i++) {
/*  998 */       rxaCounts[i] = SegmentUtils.countMatches(this.methodFlags, rxaLayouts[i]);
/*      */     }
/* 1000 */     int[] backwardsCalls = new int[5];
/* 1001 */     int methodAttrIndex = 0;
/* 1002 */     for (int j = 0; j < backwardsCalls.length; j++) {
/* 1003 */       if (rxaCounts[j] > 0) {
/* 1004 */         backwardsCallsUsed++;
/* 1005 */         backwardsCalls[j] = methodAttrCalls[methodAttrIndex];
/* 1006 */         methodAttrIndex++;
/*      */       } else {
/* 1008 */         backwardsCalls[j] = 0;
/*      */       } 
/*      */     } 
/* 1011 */     MetadataBandGroup[] mbgs = parseMetadata(in, RxA, rxaCounts, backwardsCalls, "method");
/* 1012 */     List[] attributeLists = new List[RxA.length];
/* 1013 */     int[] attributeListIndexes = new int[RxA.length]; int k;
/* 1014 */     for (k = 0; k < mbgs.length; k++) {
/* 1015 */       attributeLists[k] = mbgs[k].getAttributes();
/* 1016 */       attributeListIndexes[k] = 0;
/*      */     } 
/* 1018 */     for (k = 0; k < this.methodFlags.length; k++) {
/* 1019 */       for (int m = 0; m < (this.methodFlags[k]).length; m++) {
/* 1020 */         for (int n = 0; n < rxaLayouts.length; n++) {
/* 1021 */           if (rxaLayouts[n].matches(this.methodFlags[k][m])) {
/* 1022 */             attributeListIndexes[n] = attributeListIndexes[n] + 1; this.methodAttributes[k][m].add(attributeLists[n].get(attributeListIndexes[n]));
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1027 */     return backwardsCallsUsed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int parseClassMetadataBands(InputStream in, int[] classAttrCalls) throws Pack200Exception, IOException {
/* 1041 */     int numBackwardsCalls = 0;
/* 1042 */     String[] RxA = { "RVA", "RIA" };
/*      */ 
/*      */     
/* 1045 */     AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 0);
/*      */     
/* 1047 */     AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 0);
/* 1048 */     int rvaCount = SegmentUtils.countMatches(this.classFlags, rvaLayout);
/* 1049 */     int riaCount = SegmentUtils.countMatches(this.classFlags, riaLayout);
/* 1050 */     int[] RxACount = { rvaCount, riaCount };
/* 1051 */     int[] backwardsCalls = { 0, 0 };
/* 1052 */     if (rvaCount > 0) {
/* 1053 */       numBackwardsCalls++;
/* 1054 */       backwardsCalls[0] = classAttrCalls[0];
/* 1055 */       if (riaCount > 0) {
/* 1056 */         numBackwardsCalls++;
/* 1057 */         backwardsCalls[1] = classAttrCalls[1];
/*      */       } 
/* 1059 */     } else if (riaCount > 0) {
/* 1060 */       numBackwardsCalls++;
/* 1061 */       backwardsCalls[1] = classAttrCalls[0];
/*      */     } 
/* 1063 */     MetadataBandGroup[] mbgs = parseMetadata(in, RxA, RxACount, backwardsCalls, "class");
/* 1064 */     List rvaAttributes = mbgs[0].getAttributes();
/* 1065 */     List riaAttributes = mbgs[1].getAttributes();
/* 1066 */     int rvaAttributesIndex = 0;
/* 1067 */     int riaAttributesIndex = 0;
/* 1068 */     for (int i = 0; i < this.classFlags.length; i++) {
/* 1069 */       if (rvaLayout.matches(this.classFlags[i])) {
/* 1070 */         this.classAttributes[i].add(rvaAttributes.get(rvaAttributesIndex++));
/*      */       }
/* 1072 */       if (riaLayout.matches(this.classFlags[i])) {
/* 1073 */         this.classAttributes[i].add(riaAttributes.get(riaAttributesIndex++));
/*      */       }
/*      */     } 
/* 1076 */     return numBackwardsCalls;
/*      */   }
/*      */   
/*      */   public ArrayList[] getClassAttributes() {
/* 1080 */     return this.classAttributes;
/*      */   }
/*      */   
/*      */   public int[] getClassFieldCount() {
/* 1084 */     return this.classFieldCount;
/*      */   }
/*      */   
/*      */   public long[] getRawClassFlags() {
/* 1088 */     return this.classFlags;
/*      */   }
/*      */   
/*      */   public long[] getClassFlags() throws Pack200Exception {
/* 1092 */     if (this.classAccessFlags == null) {
/* 1093 */       long mask = 32767L; int i;
/* 1094 */       for (i = 0; i < 16; i++) {
/* 1095 */         AttributeLayout layout = this.attrMap.getAttributeLayout(i, 0);
/* 1096 */         if (layout != null && !layout.isDefaultLayout()) {
/* 1097 */           mask &= (1 << i ^ 0xFFFFFFFF);
/*      */         }
/*      */       } 
/* 1100 */       this.classAccessFlags = new long[this.classFlags.length];
/* 1101 */       for (i = 0; i < this.classFlags.length; i++) {
/* 1102 */         this.classAccessFlags[i] = this.classFlags[i] & mask;
/*      */       }
/*      */     } 
/* 1105 */     return this.classAccessFlags;
/*      */   }
/*      */   
/*      */   public int[][] getClassInterfacesInts() {
/* 1109 */     return this.classInterfacesInts;
/*      */   }
/*      */   
/*      */   public int[] getClassMethodCount() {
/* 1113 */     return this.classMethodCount;
/*      */   }
/*      */   
/*      */   public int[] getClassSuperInts() {
/* 1117 */     return this.classSuperInts;
/*      */   }
/*      */   
/*      */   public int[] getClassThisInts() {
/* 1121 */     return this.classThisInts;
/*      */   }
/*      */   
/*      */   public int[] getCodeMaxNALocals() {
/* 1125 */     return this.codeMaxNALocals;
/*      */   }
/*      */   
/*      */   public int[] getCodeMaxStack() {
/* 1129 */     return this.codeMaxStack;
/*      */   }
/*      */   
/*      */   public ArrayList[][] getFieldAttributes() {
/* 1133 */     return this.fieldAttributes;
/*      */   }
/*      */   
/*      */   public int[][] getFieldDescrInts() {
/* 1137 */     return this.fieldDescrInts;
/*      */   }
/*      */   
/*      */   public int[][] getMethodDescrInts() {
/* 1141 */     return this.methodDescrInts;
/*      */   }
/*      */   
/*      */   public long[][] getFieldFlags() throws Pack200Exception {
/* 1145 */     if (this.fieldAccessFlags == null) {
/* 1146 */       long mask = 32767L; int i;
/* 1147 */       for (i = 0; i < 16; i++) {
/* 1148 */         AttributeLayout layout = this.attrMap.getAttributeLayout(i, 1);
/* 1149 */         if (layout != null && !layout.isDefaultLayout()) {
/* 1150 */           mask &= (1 << i ^ 0xFFFFFFFF);
/*      */         }
/*      */       } 
/* 1153 */       this.fieldAccessFlags = new long[this.fieldFlags.length][];
/* 1154 */       for (i = 0; i < this.fieldFlags.length; i++) {
/* 1155 */         this.fieldAccessFlags[i] = new long[(this.fieldFlags[i]).length];
/* 1156 */         for (int j = 0; j < (this.fieldFlags[i]).length; j++) {
/* 1157 */           this.fieldAccessFlags[i][j] = this.fieldFlags[i][j] & mask;
/*      */         }
/*      */       } 
/*      */     } 
/* 1161 */     return this.fieldAccessFlags;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList getOrderedCodeAttributes() {
/* 1172 */     ArrayList<ArrayList<Attribute>> orderedAttributeList = new ArrayList(this.codeAttributes.length);
/* 1173 */     for (int classIndex = 0; classIndex < this.codeAttributes.length; classIndex++) {
/* 1174 */       ArrayList<Attribute> currentAttributes = new ArrayList(this.codeAttributes[classIndex].size());
/* 1175 */       for (int attributeIndex = 0; attributeIndex < this.codeAttributes[classIndex].size(); attributeIndex++) {
/* 1176 */         Attribute attribute = this.codeAttributes[classIndex].get(attributeIndex);
/* 1177 */         currentAttributes.add(attribute);
/*      */       } 
/* 1179 */       orderedAttributeList.add(currentAttributes);
/*      */     } 
/* 1181 */     return orderedAttributeList;
/*      */   }
/*      */   
/*      */   public ArrayList[][] getMethodAttributes() {
/* 1185 */     return this.methodAttributes;
/*      */   }
/*      */   
/*      */   public String[][] getMethodDescr() {
/* 1189 */     return this.methodDescr;
/*      */   }
/*      */   
/*      */   public long[][] getMethodFlags() throws Pack200Exception {
/* 1193 */     if (this.methodAccessFlags == null) {
/* 1194 */       long mask = 32767L; int i;
/* 1195 */       for (i = 0; i < 16; i++) {
/* 1196 */         AttributeLayout layout = this.attrMap.getAttributeLayout(i, 2);
/* 1197 */         if (layout != null && !layout.isDefaultLayout()) {
/* 1198 */           mask &= (1 << i ^ 0xFFFFFFFF);
/*      */         }
/*      */       } 
/* 1201 */       this.methodAccessFlags = new long[this.methodFlags.length][];
/* 1202 */       for (i = 0; i < this.methodFlags.length; i++) {
/* 1203 */         this.methodAccessFlags[i] = new long[(this.methodFlags[i]).length];
/* 1204 */         for (int j = 0; j < (this.methodFlags[i]).length; j++) {
/* 1205 */           this.methodAccessFlags[i][j] = this.methodFlags[i][j] & mask;
/*      */         }
/*      */       } 
/*      */     } 
/* 1209 */     return this.methodAccessFlags;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] getClassVersionMajor() {
/* 1219 */     return this.classVersionMajor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] getClassVersionMinor() {
/* 1229 */     return this.classVersionMinor;
/*      */   }
/*      */   
/*      */   public int[] getCodeHandlerCount() {
/* 1233 */     return this.codeHandlerCount;
/*      */   }
/*      */   
/*      */   public int[][] getCodeHandlerCatchPO() {
/* 1237 */     return this.codeHandlerCatchPO;
/*      */   }
/*      */   
/*      */   public int[][] getCodeHandlerClassRCN() {
/* 1241 */     return this.codeHandlerClassRCN;
/*      */   }
/*      */   
/*      */   public int[][] getCodeHandlerEndPO() {
/* 1245 */     return this.codeHandlerEndPO;
/*      */   }
/*      */   
/*      */   public int[][] getCodeHandlerStartP() {
/* 1249 */     return this.codeHandlerStartP;
/*      */   }
/*      */   
/*      */   public IcTuple[][] getIcLocal() {
/* 1253 */     return this.icLocal;
/*      */   }
/*      */   
/*      */   public boolean[] getCodeHasAttributes() {
/* 1257 */     return this.codeHasAttributes;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\ClassBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */