/*      */ package org.apache.commons.compress.harmony.pack200;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.objectweb.asm.Label;
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
/*      */   private final CpBands cpBands;
/*      */   private final AttributeDefinitionBands attrBands;
/*      */   private final CPClass[] class_this;
/*      */   private final CPClass[] class_super;
/*      */   private final CPClass[][] class_interface;
/*      */   private final int[] class_interface_count;
/*      */   private final int[] major_versions;
/*      */   private final long[] class_flags;
/*      */   private int[] class_attr_calls;
/*   53 */   private final List classSourceFile = new ArrayList();
/*   54 */   private final List classEnclosingMethodClass = new ArrayList();
/*   55 */   private final List classEnclosingMethodDesc = new ArrayList();
/*   56 */   private final List classSignature = new ArrayList();
/*      */   
/*   58 */   private final IntList classFileVersionMinor = new IntList();
/*   59 */   private final IntList classFileVersionMajor = new IntList();
/*      */   
/*      */   private final int[] class_field_count;
/*      */   private final CPNameAndType[][] field_descr;
/*      */   private final long[][] field_flags;
/*      */   private int[] field_attr_calls;
/*   65 */   private final List fieldConstantValueKQ = new ArrayList();
/*   66 */   private final List fieldSignature = new ArrayList();
/*      */   
/*      */   private final int[] class_method_count;
/*      */   private final CPNameAndType[][] method_descr;
/*      */   private final long[][] method_flags;
/*      */   private int[] method_attr_calls;
/*   72 */   private final List methodSignature = new ArrayList();
/*   73 */   private final IntList methodExceptionNumber = new IntList();
/*   74 */   private final List methodExceptionClasses = new ArrayList();
/*      */   
/*      */   private int[] codeHeaders;
/*   77 */   private final IntList codeMaxStack = new IntList();
/*   78 */   private final IntList codeMaxLocals = new IntList();
/*   79 */   private final IntList codeHandlerCount = new IntList();
/*   80 */   private final List codeHandlerStartP = new ArrayList();
/*   81 */   private final List codeHandlerEndPO = new ArrayList();
/*   82 */   private final List codeHandlerCatchPO = new ArrayList();
/*   83 */   private final List codeHandlerClass = new ArrayList();
/*   84 */   private final List codeFlags = new ArrayList();
/*      */   private int[] code_attr_calls;
/*   86 */   private final IntList codeLineNumberTableN = new IntList();
/*   87 */   private final List codeLineNumberTableBciP = new ArrayList();
/*   88 */   private final IntList codeLineNumberTableLine = new IntList();
/*   89 */   private final IntList codeLocalVariableTableN = new IntList();
/*   90 */   private final List codeLocalVariableTableBciP = new ArrayList();
/*   91 */   private final List codeLocalVariableTableSpanO = new ArrayList();
/*   92 */   private final List codeLocalVariableTableNameRU = new ArrayList();
/*   93 */   private final List codeLocalVariableTableTypeRS = new ArrayList();
/*   94 */   private final IntList codeLocalVariableTableSlot = new IntList();
/*   95 */   private final IntList codeLocalVariableTypeTableN = new IntList();
/*   96 */   private final List codeLocalVariableTypeTableBciP = new ArrayList();
/*   97 */   private final List codeLocalVariableTypeTableSpanO = new ArrayList();
/*   98 */   private final List codeLocalVariableTypeTableNameRU = new ArrayList();
/*   99 */   private final List codeLocalVariableTypeTableTypeRS = new ArrayList();
/*  100 */   private final IntList codeLocalVariableTypeTableSlot = new IntList();
/*      */   
/*      */   private final MetadataBandGroup class_RVA_bands;
/*      */   
/*      */   private final MetadataBandGroup class_RIA_bands;
/*      */   private final MetadataBandGroup field_RVA_bands;
/*      */   private final MetadataBandGroup field_RIA_bands;
/*      */   private final MetadataBandGroup method_RVA_bands;
/*      */   private final MetadataBandGroup method_RIA_bands;
/*      */   private final MetadataBandGroup method_RVPA_bands;
/*      */   private final MetadataBandGroup method_RIPA_bands;
/*      */   private final MetadataBandGroup method_AD_bands;
/*  112 */   private final List classAttributeBands = new ArrayList();
/*  113 */   private final List methodAttributeBands = new ArrayList();
/*  114 */   private final List fieldAttributeBands = new ArrayList();
/*  115 */   private final List codeAttributeBands = new ArrayList();
/*      */   
/*  117 */   private final List tempFieldFlags = new ArrayList();
/*  118 */   private final List tempFieldDesc = new ArrayList();
/*  119 */   private final List tempMethodFlags = new ArrayList();
/*  120 */   private final List tempMethodDesc = new ArrayList();
/*      */   
/*      */   private TempParamAnnotation tempMethodRVPA;
/*      */   
/*      */   private TempParamAnnotation tempMethodRIPA;
/*      */   private boolean anySyntheticClasses = false;
/*      */   private boolean anySyntheticFields = false;
/*      */   private boolean anySyntheticMethods = false;
/*      */   private final Segment segment;
/*  129 */   private final Map classReferencesInnerClass = new HashMap<>();
/*      */   
/*      */   private final boolean stripDebug;
/*  132 */   private int index = 0;
/*      */   
/*  134 */   private int numMethodArgs = 0;
/*      */   
/*      */   private int[] class_InnerClasses_N;
/*      */   private CPClass[] class_InnerClasses_RC;
/*      */   private int[] class_InnerClasses_F;
/*      */   private List classInnerClassesOuterRCN;
/*      */   private List classInnerClassesNameRUN;
/*      */   
/*      */   public ClassBands(Segment segment, int numClasses, int effort, boolean stripDebug) throws IOException {
/*  143 */     super(effort, segment.getSegmentHeader());
/*  144 */     this.stripDebug = stripDebug;
/*  145 */     this.segment = segment;
/*  146 */     this.cpBands = segment.getCpBands();
/*  147 */     this.attrBands = segment.getAttrBands();
/*  148 */     this.class_this = new CPClass[numClasses];
/*  149 */     this.class_super = new CPClass[numClasses];
/*  150 */     this.class_interface_count = new int[numClasses];
/*  151 */     this.class_interface = new CPClass[numClasses][];
/*  152 */     this.class_field_count = new int[numClasses];
/*  153 */     this.class_method_count = new int[numClasses];
/*  154 */     this.field_descr = new CPNameAndType[numClasses][];
/*  155 */     this.field_flags = new long[numClasses][];
/*  156 */     this.method_descr = new CPNameAndType[numClasses][];
/*  157 */     this.method_flags = new long[numClasses][];
/*  158 */     for (int i = 0; i < numClasses; i++) {
/*  159 */       this.field_flags[i] = new long[0];
/*  160 */       this.method_flags[i] = new long[0];
/*      */     } 
/*      */     
/*  163 */     this.major_versions = new int[numClasses];
/*  164 */     this.class_flags = new long[numClasses];
/*      */     
/*  166 */     this.class_RVA_bands = new MetadataBandGroup("RVA", 0, this.cpBands, this.segmentHeader, effort);
/*  167 */     this.class_RIA_bands = new MetadataBandGroup("RIA", 0, this.cpBands, this.segmentHeader, effort);
/*  168 */     this.field_RVA_bands = new MetadataBandGroup("RVA", 1, this.cpBands, this.segmentHeader, effort);
/*  169 */     this.field_RIA_bands = new MetadataBandGroup("RIA", 1, this.cpBands, this.segmentHeader, effort);
/*  170 */     this.method_RVA_bands = new MetadataBandGroup("RVA", 2, this.cpBands, this.segmentHeader, effort);
/*      */     
/*  172 */     this.method_RIA_bands = new MetadataBandGroup("RIA", 2, this.cpBands, this.segmentHeader, effort);
/*      */     
/*  174 */     this.method_RVPA_bands = new MetadataBandGroup("RVPA", 2, this.cpBands, this.segmentHeader, effort);
/*      */     
/*  176 */     this.method_RIPA_bands = new MetadataBandGroup("RIPA", 2, this.cpBands, this.segmentHeader, effort);
/*      */     
/*  178 */     this.method_AD_bands = new MetadataBandGroup("AD", 2, this.cpBands, this.segmentHeader, effort);
/*      */     
/*  180 */     createNewAttributeBands();
/*      */   }
/*      */   
/*      */   private void createNewAttributeBands() throws IOException {
/*  184 */     List classAttributeLayouts = this.attrBands.getClassAttributeLayouts();
/*  185 */     for (Iterator<AttributeDefinitionBands.AttributeDefinition> iterator = classAttributeLayouts.iterator(); iterator.hasNext(); ) {
/*  186 */       AttributeDefinitionBands.AttributeDefinition def = iterator.next();
/*  187 */       this.classAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
/*      */     } 
/*  189 */     List methodAttributeLayouts = this.attrBands.getMethodAttributeLayouts();
/*  190 */     for (Iterator<AttributeDefinitionBands.AttributeDefinition> iterator1 = methodAttributeLayouts.iterator(); iterator1.hasNext(); ) {
/*  191 */       AttributeDefinitionBands.AttributeDefinition def = iterator1.next();
/*  192 */       this.methodAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
/*      */     } 
/*  194 */     List fieldAttributeLayouts = this.attrBands.getFieldAttributeLayouts();
/*  195 */     for (Iterator<AttributeDefinitionBands.AttributeDefinition> iterator2 = fieldAttributeLayouts.iterator(); iterator2.hasNext(); ) {
/*  196 */       AttributeDefinitionBands.AttributeDefinition def = iterator2.next();
/*  197 */       this.fieldAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
/*      */     } 
/*  199 */     List codeAttributeLayouts = this.attrBands.getCodeAttributeLayouts();
/*  200 */     for (Iterator<AttributeDefinitionBands.AttributeDefinition> iterator3 = codeAttributeLayouts.iterator(); iterator3.hasNext(); ) {
/*  201 */       AttributeDefinitionBands.AttributeDefinition def = iterator3.next();
/*  202 */       this.codeAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void addClass(int major, int flags, String className, String signature, String superName, String[] interfaces) {
/*  208 */     this.class_this[this.index] = this.cpBands.getCPClass(className);
/*  209 */     this.class_super[this.index] = this.cpBands.getCPClass(superName);
/*  210 */     this.class_interface_count[this.index] = interfaces.length;
/*  211 */     this.class_interface[this.index] = new CPClass[interfaces.length];
/*  212 */     for (int i = 0; i < interfaces.length; i++) {
/*  213 */       this.class_interface[this.index][i] = this.cpBands.getCPClass(interfaces[i]);
/*      */     }
/*  215 */     this.major_versions[this.index] = major;
/*  216 */     this.class_flags[this.index] = flags;
/*  217 */     if (!this.anySyntheticClasses && (flags & 0x1000) != 0 && this.segment
/*  218 */       .getCurrentClassReader().hasSyntheticAttributes()) {
/*  219 */       this.cpBands.addCPUtf8("Synthetic");
/*  220 */       this.anySyntheticClasses = true;
/*      */     } 
/*  222 */     if ((flags & 0x20000) != 0) {
/*  223 */       flags &= 0xFFFDFFFF;
/*  224 */       flags |= 0x100000;
/*      */     } 
/*  226 */     if (signature != null) {
/*  227 */       this.class_flags[this.index] = this.class_flags[this.index] | 0x80000L;
/*  228 */       this.classSignature.add(this.cpBands.getCPSignature(signature));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void currentClassReferencesInnerClass(CPClass inner) {
/*  233 */     if (this.index < this.class_this.length) {
/*  234 */       CPClass currentClass = this.class_this[this.index];
/*  235 */       if (currentClass != null && !currentClass.equals(inner) && 
/*  236 */         !isInnerClassOf(currentClass.toString(), inner)) {
/*  237 */         Set<CPClass> referencedInnerClasses = (Set)this.classReferencesInnerClass.get(currentClass);
/*  238 */         if (referencedInnerClasses == null) {
/*  239 */           referencedInnerClasses = new HashSet();
/*  240 */           this.classReferencesInnerClass.put(currentClass, referencedInnerClasses);
/*      */         } 
/*  242 */         referencedInnerClasses.add(inner);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isInnerClassOf(String possibleInner, CPClass possibleOuter) {
/*  248 */     if (isInnerClass(possibleInner)) {
/*  249 */       String superClassName = possibleInner.substring(0, possibleInner.lastIndexOf('$'));
/*  250 */       if (superClassName.equals(possibleOuter.toString())) {
/*  251 */         return true;
/*      */       }
/*  253 */       return isInnerClassOf(superClassName, possibleOuter);
/*      */     } 
/*  255 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isInnerClass(String possibleInner) {
/*  259 */     return (possibleInner.indexOf('$') != -1);
/*      */   }
/*      */   
/*      */   public void addField(int flags, String name, String desc, String signature, Object value) {
/*  263 */     flags &= 0xFFFF;
/*  264 */     this.tempFieldDesc.add(this.cpBands.getCPNameAndType(name, desc));
/*  265 */     if (signature != null) {
/*  266 */       this.fieldSignature.add(this.cpBands.getCPSignature(signature));
/*  267 */       flags |= 0x80000;
/*      */     } 
/*  269 */     if ((flags & 0x20000) != 0) {
/*  270 */       flags &= 0xFFFDFFFF;
/*  271 */       flags |= 0x100000;
/*      */     } 
/*  273 */     if (value != null) {
/*  274 */       this.fieldConstantValueKQ.add(this.cpBands.getConstant(value));
/*  275 */       flags |= 0x20000;
/*      */     } 
/*  277 */     if (!this.anySyntheticFields && (flags & 0x1000) != 0 && this.segment
/*  278 */       .getCurrentClassReader().hasSyntheticAttributes()) {
/*  279 */       this.cpBands.addCPUtf8("Synthetic");
/*  280 */       this.anySyntheticFields = true;
/*      */     } 
/*  282 */     this.tempFieldFlags.add(Long.valueOf(flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void finaliseBands() {
/*  290 */     int defaultMajorVersion = this.segmentHeader.getDefaultMajorVersion();
/*  291 */     for (int i = 0; i < this.class_flags.length; i++) {
/*  292 */       int major = this.major_versions[i];
/*  293 */       if (major != defaultMajorVersion) {
/*  294 */         this.class_flags[i] = this.class_flags[i] | 0x1000000L;
/*  295 */         this.classFileVersionMajor.add(major);
/*  296 */         this.classFileVersionMinor.add(0);
/*      */       } 
/*      */     } 
/*      */     
/*  300 */     this.codeHeaders = new int[this.codeHandlerCount.size()];
/*  301 */     int removed = 0;
/*  302 */     for (int j = 0; j < this.codeHeaders.length; j++) {
/*  303 */       int numHandlers = this.codeHandlerCount.get(j - removed);
/*  304 */       int maxLocals = this.codeMaxLocals.get(j - removed);
/*  305 */       int maxStack = this.codeMaxStack.get(j - removed);
/*  306 */       if (numHandlers == 0) {
/*  307 */         int header = maxLocals * 12 + maxStack + 1;
/*  308 */         if (header < 145 && maxStack < 12) {
/*  309 */           this.codeHeaders[j] = header;
/*      */         }
/*  311 */       } else if (numHandlers == 1) {
/*  312 */         int header = maxLocals * 8 + maxStack + 145;
/*  313 */         if (header < 209 && maxStack < 8) {
/*  314 */           this.codeHeaders[j] = header;
/*      */         }
/*  316 */       } else if (numHandlers == 2) {
/*  317 */         int header = maxLocals * 7 + maxStack + 209;
/*  318 */         if (header < 256 && maxStack < 7) {
/*  319 */           this.codeHeaders[j] = header;
/*      */         }
/*      */       } 
/*  322 */       if (this.codeHeaders[j] != 0) {
/*      */ 
/*      */         
/*  325 */         this.codeHandlerCount.remove(j - removed);
/*  326 */         this.codeMaxLocals.remove(j - removed);
/*  327 */         this.codeMaxStack.remove(j - removed);
/*  328 */         removed++;
/*  329 */       } else if (!this.segment.getSegmentHeader().have_all_code_flags()) {
/*  330 */         this.codeFlags.add(Long.valueOf(0L));
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  335 */     IntList innerClassesN = new IntList();
/*  336 */     List<IcBands.IcTuple> icLocal = new ArrayList(); int k;
/*  337 */     for (k = 0; k < this.class_this.length; k++) {
/*  338 */       CPClass cpClass = this.class_this[k];
/*  339 */       Set referencedInnerClasses = (Set)this.classReferencesInnerClass.get(cpClass);
/*  340 */       if (referencedInnerClasses != null) {
/*  341 */         int innerN = 0;
/*  342 */         List innerClasses = this.segment.getIcBands().getInnerClassesForOuter(cpClass.toString());
/*  343 */         if (innerClasses != null) {
/*  344 */           for (Iterator iterator5 = innerClasses.iterator(); iterator5.hasNext();) {
/*  345 */             referencedInnerClasses.remove(((IcBands.IcTuple)iterator5.next()).C);
/*      */           }
/*      */         }
/*  348 */         for (Iterator<CPClass> iterator4 = referencedInnerClasses.iterator(); iterator4.hasNext(); ) {
/*  349 */           CPClass inner = iterator4.next();
/*  350 */           IcBands.IcTuple icTuple = this.segment.getIcBands().getIcTuple(inner);
/*  351 */           if (icTuple != null && !icTuple.isAnonymous()) {
/*      */             
/*  353 */             icLocal.add(icTuple);
/*  354 */             innerN++;
/*      */           } 
/*      */         } 
/*  357 */         if (innerN != 0) {
/*  358 */           innerClassesN.add(innerN);
/*  359 */           this.class_flags[k] = this.class_flags[k] | 0x800000L;
/*      */         } 
/*      */       } 
/*      */     } 
/*  363 */     this.class_InnerClasses_N = innerClassesN.toArray();
/*  364 */     this.class_InnerClasses_RC = new CPClass[icLocal.size()];
/*  365 */     this.class_InnerClasses_F = new int[icLocal.size()];
/*  366 */     this.classInnerClassesOuterRCN = new ArrayList();
/*  367 */     this.classInnerClassesNameRUN = new ArrayList();
/*  368 */     for (k = 0; k < this.class_InnerClasses_RC.length; k++) {
/*  369 */       IcBands.IcTuple icTuple = icLocal.get(k);
/*  370 */       this.class_InnerClasses_RC[k] = icTuple.C;
/*  371 */       if (icTuple.C2 == null && icTuple.N == null) {
/*  372 */         this.class_InnerClasses_F[k] = 0;
/*      */       } else {
/*  374 */         if (icTuple.F == 0) {
/*  375 */           this.class_InnerClasses_F[k] = 65536;
/*      */         } else {
/*  377 */           this.class_InnerClasses_F[k] = icTuple.F;
/*      */         } 
/*  379 */         this.classInnerClassesOuterRCN.add(icTuple.C2);
/*  380 */         this.classInnerClassesNameRUN.add(icTuple.N);
/*      */       } 
/*      */     } 
/*      */     
/*  384 */     IntList classAttrCalls = new IntList();
/*  385 */     IntList fieldAttrCalls = new IntList();
/*  386 */     IntList methodAttrCalls = new IntList();
/*  387 */     IntList codeAttrCalls = new IntList();
/*      */     
/*  389 */     if (this.class_RVA_bands.hasContent()) {
/*  390 */       classAttrCalls.add(this.class_RVA_bands.numBackwardsCalls());
/*      */     }
/*  392 */     if (this.class_RIA_bands.hasContent()) {
/*  393 */       classAttrCalls.add(this.class_RIA_bands.numBackwardsCalls());
/*      */     }
/*  395 */     if (this.field_RVA_bands.hasContent()) {
/*  396 */       fieldAttrCalls.add(this.field_RVA_bands.numBackwardsCalls());
/*      */     }
/*  398 */     if (this.field_RIA_bands.hasContent()) {
/*  399 */       fieldAttrCalls.add(this.field_RIA_bands.numBackwardsCalls());
/*      */     }
/*  401 */     if (this.method_RVA_bands.hasContent()) {
/*  402 */       methodAttrCalls.add(this.method_RVA_bands.numBackwardsCalls());
/*      */     }
/*  404 */     if (this.method_RIA_bands.hasContent()) {
/*  405 */       methodAttrCalls.add(this.method_RIA_bands.numBackwardsCalls());
/*      */     }
/*  407 */     if (this.method_RVPA_bands.hasContent()) {
/*  408 */       methodAttrCalls.add(this.method_RVPA_bands.numBackwardsCalls());
/*      */     }
/*  410 */     if (this.method_RIPA_bands.hasContent()) {
/*  411 */       methodAttrCalls.add(this.method_RIPA_bands.numBackwardsCalls());
/*      */     }
/*  413 */     if (this.method_AD_bands.hasContent()) {
/*  414 */       methodAttrCalls.add(this.method_AD_bands.numBackwardsCalls());
/*      */     }
/*      */ 
/*      */     
/*  418 */     Comparator<?> comparator = (arg0, arg1) -> {
/*      */         NewAttributeBands bands0 = (NewAttributeBands)arg0;
/*      */         NewAttributeBands bands1 = (NewAttributeBands)arg1;
/*      */         return bands0.getFlagIndex() - bands1.getFlagIndex();
/*      */       };
/*  423 */     Collections.sort(this.classAttributeBands, comparator);
/*  424 */     Collections.sort(this.methodAttributeBands, comparator);
/*  425 */     Collections.sort(this.fieldAttributeBands, comparator);
/*  426 */     Collections.sort(this.codeAttributeBands, comparator);
/*      */     
/*  428 */     for (Iterator<NewAttributeBands> iterator3 = this.classAttributeBands.iterator(); iterator3.hasNext(); ) {
/*  429 */       NewAttributeBands bands = iterator3.next();
/*  430 */       if (bands.isUsedAtLeastOnce()) {
/*  431 */         int[] backwardsCallCounts = bands.numBackwardsCalls();
/*  432 */         for (int m = 0; m < backwardsCallCounts.length; m++) {
/*  433 */           classAttrCalls.add(backwardsCallCounts[m]);
/*      */         }
/*      */       } 
/*      */     } 
/*  437 */     for (Iterator<NewAttributeBands> iterator2 = this.methodAttributeBands.iterator(); iterator2.hasNext(); ) {
/*  438 */       NewAttributeBands bands = iterator2.next();
/*  439 */       if (bands.isUsedAtLeastOnce()) {
/*  440 */         int[] backwardsCallCounts = bands.numBackwardsCalls();
/*  441 */         for (int m = 0; m < backwardsCallCounts.length; m++) {
/*  442 */           methodAttrCalls.add(backwardsCallCounts[m]);
/*      */         }
/*      */       } 
/*      */     } 
/*  446 */     for (Iterator<NewAttributeBands> iterator1 = this.fieldAttributeBands.iterator(); iterator1.hasNext(); ) {
/*  447 */       NewAttributeBands bands = iterator1.next();
/*  448 */       if (bands.isUsedAtLeastOnce()) {
/*  449 */         int[] backwardsCallCounts = bands.numBackwardsCalls();
/*  450 */         for (int m = 0; m < backwardsCallCounts.length; m++) {
/*  451 */           fieldAttrCalls.add(backwardsCallCounts[m]);
/*      */         }
/*      */       } 
/*      */     } 
/*  455 */     for (Iterator<NewAttributeBands> iterator = this.codeAttributeBands.iterator(); iterator.hasNext(); ) {
/*  456 */       NewAttributeBands bands = iterator.next();
/*  457 */       if (bands.isUsedAtLeastOnce()) {
/*  458 */         int[] backwardsCallCounts = bands.numBackwardsCalls();
/*  459 */         for (int m = 0; m < backwardsCallCounts.length; m++) {
/*  460 */           codeAttrCalls.add(backwardsCallCounts[m]);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  465 */     this.class_attr_calls = classAttrCalls.toArray();
/*  466 */     this.field_attr_calls = fieldAttrCalls.toArray();
/*  467 */     this.method_attr_calls = methodAttrCalls.toArray();
/*  468 */     this.code_attr_calls = codeAttrCalls.toArray();
/*      */   }
/*      */ 
/*      */   
/*      */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/*  473 */     PackingUtils.log("Writing class bands...");
/*      */     
/*  475 */     byte[] encodedBand = encodeBandInt("class_this", getInts(this.class_this), Codec.DELTA5);
/*  476 */     out.write(encodedBand);
/*  477 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_this[" + this.class_this.length + "]");
/*      */     
/*  479 */     encodedBand = encodeBandInt("class_super", getInts(this.class_super), Codec.DELTA5);
/*  480 */     out.write(encodedBand);
/*  481 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_super[" + this.class_super.length + "]");
/*      */     
/*  483 */     encodedBand = encodeBandInt("class_interface_count", this.class_interface_count, Codec.DELTA5);
/*  484 */     out.write(encodedBand);
/*  485 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface_count[" + this.class_interface_count.length + "]");
/*      */ 
/*      */     
/*  488 */     int totalInterfaces = sum(this.class_interface_count);
/*  489 */     int[] classInterface = new int[totalInterfaces];
/*  490 */     int k = 0;
/*  491 */     for (int i = 0; i < this.class_interface.length; i++) {
/*  492 */       if (this.class_interface[i] != null) {
/*  493 */         for (int n = 0; n < (this.class_interface[i]).length; n++) {
/*  494 */           CPClass cpClass = this.class_interface[i][n];
/*  495 */           classInterface[k] = cpClass.getIndex();
/*  496 */           k++;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/*  501 */     encodedBand = encodeBandInt("class_interface", classInterface, Codec.DELTA5);
/*  502 */     out.write(encodedBand);
/*  503 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface[" + classInterface.length + "]");
/*      */     
/*  505 */     encodedBand = encodeBandInt("class_field_count", this.class_field_count, Codec.DELTA5);
/*  506 */     out.write(encodedBand);
/*      */     
/*  508 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_field_count[" + this.class_field_count.length + "]");
/*      */     
/*  510 */     encodedBand = encodeBandInt("class_method_count", this.class_method_count, Codec.DELTA5);
/*  511 */     out.write(encodedBand);
/*      */     
/*  513 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_method_count[" + this.class_method_count.length + "]");
/*      */     
/*  515 */     int totalFields = sum(this.class_field_count);
/*  516 */     int[] fieldDescr = new int[totalFields];
/*  517 */     k = 0;
/*  518 */     for (int j = 0; j < this.index; j++) {
/*  519 */       for (int n = 0; n < (this.field_descr[j]).length; n++) {
/*  520 */         CPNameAndType descr = this.field_descr[j][n];
/*  521 */         fieldDescr[k] = descr.getIndex();
/*  522 */         k++;
/*      */       } 
/*      */     } 
/*      */     
/*  526 */     encodedBand = encodeBandInt("field_descr", fieldDescr, Codec.DELTA5);
/*  527 */     out.write(encodedBand);
/*  528 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_descr[" + fieldDescr.length + "]");
/*      */     
/*  530 */     writeFieldAttributeBands(out);
/*      */     
/*  532 */     int totalMethods = sum(this.class_method_count);
/*  533 */     int[] methodDescr = new int[totalMethods];
/*  534 */     k = 0;
/*  535 */     for (int m = 0; m < this.index; m++) {
/*  536 */       for (int n = 0; n < (this.method_descr[m]).length; n++) {
/*  537 */         CPNameAndType descr = this.method_descr[m][n];
/*  538 */         methodDescr[k] = descr.getIndex();
/*  539 */         k++;
/*      */       } 
/*      */     } 
/*      */     
/*  543 */     encodedBand = encodeBandInt("method_descr", methodDescr, Codec.MDELTA5);
/*  544 */     out.write(encodedBand);
/*  545 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_descr[" + methodDescr.length + "]");
/*      */     
/*  547 */     writeMethodAttributeBands(out);
/*  548 */     writeClassAttributeBands(out);
/*  549 */     writeCodeBands(out);
/*      */   }
/*      */   
/*      */   private int sum(int[] ints) {
/*  553 */     int sum = 0;
/*  554 */     for (int i = 0; i < ints.length; i++) {
/*  555 */       sum += ints[i];
/*      */     }
/*  557 */     return sum;
/*      */   }
/*      */   
/*      */   private void writeFieldAttributeBands(OutputStream out) throws IOException, Pack200Exception {
/*  561 */     byte[] encodedBand = encodeFlags("field_flags", this.field_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader
/*  562 */         .have_field_flags_hi());
/*  563 */     out.write(encodedBand);
/*  564 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_flags[" + this.field_flags.length + "]");
/*      */ 
/*      */ 
/*      */     
/*  568 */     encodedBand = encodeBandInt("field_attr_calls", this.field_attr_calls, Codec.UNSIGNED5);
/*  569 */     out.write(encodedBand);
/*      */     
/*  571 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_attr_calls[" + this.field_attr_calls.length + "]");
/*      */     
/*  573 */     encodedBand = encodeBandInt("fieldConstantValueKQ", cpEntryListToArray(this.fieldConstantValueKQ), Codec.UNSIGNED5);
/*  574 */     out.write(encodedBand);
/*  575 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldConstantValueKQ[" + this.fieldConstantValueKQ
/*  576 */         .size() + "]");
/*      */     
/*  578 */     encodedBand = encodeBandInt("fieldSignature", cpEntryListToArray(this.fieldSignature), Codec.UNSIGNED5);
/*  579 */     out.write(encodedBand);
/*  580 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldSignature[" + this.fieldSignature.size() + "]");
/*      */     
/*  582 */     this.field_RVA_bands.pack(out);
/*  583 */     this.field_RIA_bands.pack(out);
/*  584 */     for (Iterator<NewAttributeBands> iterator = this.fieldAttributeBands.iterator(); iterator.hasNext(); ) {
/*  585 */       NewAttributeBands bands = iterator.next();
/*  586 */       bands.pack(out);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void writeMethodAttributeBands(OutputStream out) throws IOException, Pack200Exception {
/*  591 */     byte[] encodedBand = encodeFlags("method_flags", this.method_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader
/*  592 */         .have_method_flags_hi());
/*  593 */     out.write(encodedBand);
/*  594 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_flags[" + this.method_flags.length + "]");
/*      */ 
/*      */ 
/*      */     
/*  598 */     encodedBand = encodeBandInt("method_attr_calls", this.method_attr_calls, Codec.UNSIGNED5);
/*  599 */     out.write(encodedBand);
/*      */     
/*  601 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_attr_calls[" + this.method_attr_calls.length + "]");
/*      */     
/*  603 */     encodedBand = encodeBandInt("methodExceptionNumber", this.methodExceptionNumber.toArray(), Codec.UNSIGNED5);
/*  604 */     out.write(encodedBand);
/*  605 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionNumber[" + this.methodExceptionNumber
/*  606 */         .size() + "]");
/*      */     
/*  608 */     encodedBand = encodeBandInt("methodExceptionClasses", cpEntryListToArray(this.methodExceptionClasses), Codec.UNSIGNED5);
/*      */     
/*  610 */     out.write(encodedBand);
/*  611 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionClasses[" + this.methodExceptionClasses
/*  612 */         .size() + "]");
/*      */     
/*  614 */     encodedBand = encodeBandInt("methodSignature", cpEntryListToArray(this.methodSignature), Codec.UNSIGNED5);
/*  615 */     out.write(encodedBand);
/*  616 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodSignature[" + this.methodSignature.size() + "]");
/*      */     
/*  618 */     this.method_RVA_bands.pack(out);
/*  619 */     this.method_RIA_bands.pack(out);
/*  620 */     this.method_RVPA_bands.pack(out);
/*  621 */     this.method_RIPA_bands.pack(out);
/*  622 */     this.method_AD_bands.pack(out);
/*  623 */     for (Iterator<NewAttributeBands> iterator = this.methodAttributeBands.iterator(); iterator.hasNext(); ) {
/*  624 */       NewAttributeBands bands = iterator.next();
/*  625 */       bands.pack(out);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void writeClassAttributeBands(OutputStream out) throws IOException, Pack200Exception {
/*  630 */     byte[] encodedBand = encodeFlags("class_flags", this.class_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader
/*  631 */         .have_class_flags_hi());
/*  632 */     out.write(encodedBand);
/*  633 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_flags[" + this.class_flags.length + "]");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  643 */     encodedBand = encodeBandInt("class_attr_calls", this.class_attr_calls, Codec.UNSIGNED5);
/*  644 */     out.write(encodedBand);
/*      */     
/*  646 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_attr_calls[" + this.class_attr_calls.length + "]");
/*      */     
/*  648 */     encodedBand = encodeBandInt("classSourceFile", cpEntryOrNullListToArray(this.classSourceFile), Codec.UNSIGNED5);
/*  649 */     out.write(encodedBand);
/*  650 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from classSourceFile[" + this.classSourceFile.size() + "]");
/*      */     
/*  652 */     encodedBand = encodeBandInt("class_enclosing_method_RC", cpEntryListToArray(this.classEnclosingMethodClass), Codec.UNSIGNED5);
/*      */     
/*  654 */     out.write(encodedBand);
/*  655 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_enclosing_method_RC[" + this.classEnclosingMethodClass
/*  656 */         .size() + "]");
/*      */     
/*  658 */     encodedBand = encodeBandInt("class_EnclosingMethod_RDN", cpEntryOrNullListToArray(this.classEnclosingMethodDesc), Codec.UNSIGNED5);
/*      */     
/*  660 */     out.write(encodedBand);
/*  661 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_EnclosingMethod_RDN[" + this.classEnclosingMethodDesc
/*  662 */         .size() + "]");
/*      */     
/*  664 */     encodedBand = encodeBandInt("class_Signature_RS", cpEntryListToArray(this.classSignature), Codec.UNSIGNED5);
/*  665 */     out.write(encodedBand);
/*      */     
/*  667 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_Signature_RS[" + this.classSignature.size() + "]");
/*      */     
/*  669 */     this.class_RVA_bands.pack(out);
/*  670 */     this.class_RIA_bands.pack(out);
/*      */     
/*  672 */     encodedBand = encodeBandInt("class_InnerClasses_N", this.class_InnerClasses_N, Codec.UNSIGNED5);
/*  673 */     out.write(encodedBand);
/*  674 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_N[" + this.class_InnerClasses_N.length + "]");
/*      */ 
/*      */     
/*  677 */     encodedBand = encodeBandInt("class_InnerClasses_RC", getInts(this.class_InnerClasses_RC), Codec.UNSIGNED5);
/*  678 */     out.write(encodedBand);
/*  679 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_RC[" + this.class_InnerClasses_RC.length + "]");
/*      */ 
/*      */     
/*  682 */     encodedBand = encodeBandInt("class_InnerClasses_F", this.class_InnerClasses_F, Codec.UNSIGNED5);
/*  683 */     out.write(encodedBand);
/*  684 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_F[" + this.class_InnerClasses_F.length + "]");
/*      */ 
/*      */     
/*  687 */     encodedBand = encodeBandInt("class_InnerClasses_outer_RCN", cpEntryOrNullListToArray(this.classInnerClassesOuterRCN), Codec.UNSIGNED5);
/*      */     
/*  689 */     out.write(encodedBand);
/*  690 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_outer_RCN[" + this.classInnerClassesOuterRCN
/*  691 */         .size() + "]");
/*      */     
/*  693 */     encodedBand = encodeBandInt("class_InnerClasses_name_RUN", cpEntryOrNullListToArray(this.classInnerClassesNameRUN), Codec.UNSIGNED5);
/*      */     
/*  695 */     out.write(encodedBand);
/*  696 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_name_RUN[" + this.classInnerClassesNameRUN
/*  697 */         .size() + "]");
/*      */     
/*  699 */     encodedBand = encodeBandInt("classFileVersionMinor", this.classFileVersionMinor.toArray(), Codec.UNSIGNED5);
/*  700 */     out.write(encodedBand);
/*  701 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMinor[" + this.classFileVersionMinor
/*  702 */         .size() + "]");
/*      */     
/*  704 */     encodedBand = encodeBandInt("classFileVersionMajor", this.classFileVersionMajor.toArray(), Codec.UNSIGNED5);
/*  705 */     out.write(encodedBand);
/*  706 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMajor[" + this.classFileVersionMajor
/*  707 */         .size() + "]");
/*      */     
/*  709 */     for (Iterator<NewAttributeBands> iterator = this.classAttributeBands.iterator(); iterator.hasNext(); ) {
/*  710 */       NewAttributeBands bands = iterator.next();
/*  711 */       bands.pack(out);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int[] getInts(CPClass[] cpClasses) {
/*  716 */     int[] ints = new int[cpClasses.length];
/*  717 */     for (int i = 0; i < ints.length; i++) {
/*  718 */       if (cpClasses[i] != null) {
/*  719 */         ints[i] = cpClasses[i].getIndex();
/*      */       }
/*      */     } 
/*  722 */     return ints;
/*      */   }
/*      */   
/*      */   private void writeCodeBands(OutputStream out) throws IOException, Pack200Exception {
/*  726 */     byte[] encodedBand = encodeBandInt("codeHeaders", this.codeHeaders, Codec.BYTE1);
/*  727 */     out.write(encodedBand);
/*  728 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHeaders[" + this.codeHeaders.length + "]");
/*      */     
/*  730 */     encodedBand = encodeBandInt("codeMaxStack", this.codeMaxStack.toArray(), Codec.UNSIGNED5);
/*  731 */     out.write(encodedBand);
/*  732 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxStack[" + this.codeMaxStack.size() + "]");
/*      */     
/*  734 */     encodedBand = encodeBandInt("codeMaxLocals", this.codeMaxLocals.toArray(), Codec.UNSIGNED5);
/*  735 */     out.write(encodedBand);
/*  736 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxLocals[" + this.codeMaxLocals.size() + "]");
/*      */     
/*  738 */     encodedBand = encodeBandInt("codeHandlerCount", this.codeHandlerCount.toArray(), Codec.UNSIGNED5);
/*  739 */     out.write(encodedBand);
/*      */     
/*  741 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCount[" + this.codeHandlerCount.size() + "]");
/*      */     
/*  743 */     encodedBand = encodeBandInt("codeHandlerStartP", integerListToArray(this.codeHandlerStartP), Codec.BCI5);
/*  744 */     out.write(encodedBand);
/*      */     
/*  746 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerStartP[" + this.codeHandlerStartP.size() + "]");
/*      */     
/*  748 */     encodedBand = encodeBandInt("codeHandlerEndPO", integerListToArray(this.codeHandlerEndPO), Codec.BRANCH5);
/*  749 */     out.write(encodedBand);
/*      */     
/*  751 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerEndPO[" + this.codeHandlerEndPO.size() + "]");
/*      */     
/*  753 */     encodedBand = encodeBandInt("codeHandlerCatchPO", integerListToArray(this.codeHandlerCatchPO), Codec.BRANCH5);
/*  754 */     out.write(encodedBand);
/*      */     
/*  756 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCatchPO[" + this.codeHandlerCatchPO.size() + "]");
/*      */     
/*  758 */     encodedBand = encodeBandInt("codeHandlerClass", cpEntryOrNullListToArray(this.codeHandlerClass), Codec.UNSIGNED5);
/*  759 */     out.write(encodedBand);
/*      */     
/*  761 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerClass[" + this.codeHandlerClass.size() + "]");
/*      */     
/*  763 */     writeCodeAttributeBands(out);
/*      */   }
/*      */   
/*      */   private void writeCodeAttributeBands(OutputStream out) throws IOException, Pack200Exception {
/*  767 */     byte[] encodedBand = encodeFlags("codeFlags", longListToArray(this.codeFlags), Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader
/*  768 */         .have_code_flags_hi());
/*  769 */     out.write(encodedBand);
/*  770 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeFlags[" + this.codeFlags.size() + "]");
/*      */ 
/*      */ 
/*      */     
/*  774 */     encodedBand = encodeBandInt("code_attr_calls", this.code_attr_calls, Codec.UNSIGNED5);
/*  775 */     out.write(encodedBand);
/*  776 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_attr_calls[" + this.code_attr_calls.length + "]");
/*      */     
/*  778 */     encodedBand = encodeBandInt("code_LineNumberTable_N", this.codeLineNumberTableN.toArray(), Codec.UNSIGNED5);
/*  779 */     out.write(encodedBand);
/*  780 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_N[" + this.codeLineNumberTableN
/*  781 */         .size() + "]");
/*      */     
/*  783 */     encodedBand = encodeBandInt("code_LineNumberTable_bci_P", integerListToArray(this.codeLineNumberTableBciP), Codec.BCI5);
/*      */     
/*  785 */     out.write(encodedBand);
/*  786 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_bci_P[" + this.codeLineNumberTableBciP
/*  787 */         .size() + "]");
/*      */     
/*  789 */     encodedBand = encodeBandInt("code_LineNumberTable_line", this.codeLineNumberTableLine.toArray(), Codec.UNSIGNED5);
/*  790 */     out.write(encodedBand);
/*  791 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_line[" + this.codeLineNumberTableLine
/*  792 */         .size() + "]");
/*      */     
/*  794 */     encodedBand = encodeBandInt("code_LocalVariableTable_N", this.codeLocalVariableTableN.toArray(), Codec.UNSIGNED5);
/*  795 */     out.write(encodedBand);
/*  796 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_N[" + this.codeLocalVariableTableN
/*  797 */         .size() + "]");
/*      */     
/*  799 */     encodedBand = encodeBandInt("code_LocalVariableTable_bci_P", integerListToArray(this.codeLocalVariableTableBciP), Codec.BCI5);
/*      */     
/*  801 */     out.write(encodedBand);
/*  802 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_bci_P[" + this.codeLocalVariableTableBciP
/*  803 */         .size() + "]");
/*      */     
/*  805 */     encodedBand = encodeBandInt("code_LocalVariableTable_span_O", integerListToArray(this.codeLocalVariableTableSpanO), Codec.BRANCH5);
/*      */     
/*  807 */     out.write(encodedBand);
/*  808 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_span_O[" + this.codeLocalVariableTableSpanO
/*  809 */         .size() + "]");
/*      */     
/*  811 */     encodedBand = encodeBandInt("code_LocalVariableTable_name_RU", cpEntryListToArray(this.codeLocalVariableTableNameRU), Codec.UNSIGNED5);
/*      */     
/*  813 */     out.write(encodedBand);
/*  814 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_name_RU[" + this.codeLocalVariableTableNameRU
/*  815 */         .size() + "]");
/*      */     
/*  817 */     encodedBand = encodeBandInt("code_LocalVariableTable_type_RS", cpEntryListToArray(this.codeLocalVariableTableTypeRS), Codec.UNSIGNED5);
/*      */     
/*  819 */     out.write(encodedBand);
/*  820 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_type_RS[" + this.codeLocalVariableTableTypeRS
/*  821 */         .size() + "]");
/*      */     
/*  823 */     encodedBand = encodeBandInt("code_LocalVariableTable_slot", this.codeLocalVariableTableSlot.toArray(), Codec.UNSIGNED5);
/*      */     
/*  825 */     out.write(encodedBand);
/*  826 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_slot[" + this.codeLocalVariableTableSlot
/*  827 */         .size() + "]");
/*      */     
/*  829 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_N", this.codeLocalVariableTypeTableN.toArray(), Codec.UNSIGNED5);
/*      */     
/*  831 */     out.write(encodedBand);
/*  832 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_N[" + this.codeLocalVariableTypeTableN
/*  833 */         .size() + "]");
/*      */     
/*  835 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_bci_P", 
/*  836 */         integerListToArray(this.codeLocalVariableTypeTableBciP), Codec.BCI5);
/*  837 */     out.write(encodedBand);
/*  838 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_bci_P[" + this.codeLocalVariableTypeTableBciP
/*  839 */         .size() + "]");
/*      */     
/*  841 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_span_O", 
/*  842 */         integerListToArray(this.codeLocalVariableTypeTableSpanO), Codec.BRANCH5);
/*  843 */     out.write(encodedBand);
/*  844 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_span_O[" + this.codeLocalVariableTypeTableSpanO
/*  845 */         .size() + "]");
/*      */     
/*  847 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_name_RU", 
/*  848 */         cpEntryListToArray(this.codeLocalVariableTypeTableNameRU), Codec.UNSIGNED5);
/*  849 */     out.write(encodedBand);
/*  850 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_name_RU[" + this.codeLocalVariableTypeTableNameRU
/*  851 */         .size() + "]");
/*      */     
/*  853 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_type_RS", 
/*  854 */         cpEntryListToArray(this.codeLocalVariableTypeTableTypeRS), Codec.UNSIGNED5);
/*  855 */     out.write(encodedBand);
/*  856 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_type_RS[" + this.codeLocalVariableTypeTableTypeRS
/*  857 */         .size() + "]");
/*      */     
/*  859 */     encodedBand = encodeBandInt("code_LocalVariableTypeTable_slot", this.codeLocalVariableTypeTableSlot.toArray(), Codec.UNSIGNED5);
/*      */     
/*  861 */     out.write(encodedBand);
/*  862 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_slot[" + this.codeLocalVariableTypeTableSlot
/*  863 */         .size() + "]");
/*      */     
/*  865 */     for (Iterator<NewAttributeBands> iterator = this.codeAttributeBands.iterator(); iterator.hasNext(); ) {
/*  866 */       NewAttributeBands bands = iterator.next();
/*  867 */       bands.pack(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void addMethod(int flags, String name, String desc, String signature, String[] exceptions) {
/*  873 */     CPNameAndType nt = this.cpBands.getCPNameAndType(name, desc);
/*  874 */     this.tempMethodDesc.add(nt);
/*  875 */     if (signature != null) {
/*  876 */       this.methodSignature.add(this.cpBands.getCPSignature(signature));
/*  877 */       flags |= 0x80000;
/*      */     } 
/*  879 */     if (exceptions != null) {
/*  880 */       this.methodExceptionNumber.add(exceptions.length);
/*  881 */       for (int i = 0; i < exceptions.length; i++) {
/*  882 */         this.methodExceptionClasses.add(this.cpBands.getCPClass(exceptions[i]));
/*      */       }
/*  884 */       flags |= 0x40000;
/*      */     } 
/*  886 */     if ((flags & 0x20000) != 0) {
/*  887 */       flags &= 0xFFFDFFFF;
/*  888 */       flags |= 0x100000;
/*      */     } 
/*  890 */     this.tempMethodFlags.add(Long.valueOf(flags));
/*  891 */     this.numMethodArgs = countArgs(desc);
/*  892 */     if (!this.anySyntheticMethods && (flags & 0x1000) != 0 && this.segment
/*  893 */       .getCurrentClassReader().hasSyntheticAttributes()) {
/*  894 */       this.cpBands.addCPUtf8("Synthetic");
/*  895 */       this.anySyntheticMethods = true;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void endOfMethod() {
/*  900 */     if (this.tempMethodRVPA != null) {
/*  901 */       this.method_RVPA_bands.addParameterAnnotation(this.tempMethodRVPA.numParams, this.tempMethodRVPA.annoN, this.tempMethodRVPA.pairN, this.tempMethodRVPA.typeRS, this.tempMethodRVPA.nameRU, this.tempMethodRVPA.t, this.tempMethodRVPA.values, this.tempMethodRVPA.caseArrayN, this.tempMethodRVPA.nestTypeRS, this.tempMethodRVPA.nestNameRU, this.tempMethodRVPA.nestPairN);
/*      */ 
/*      */ 
/*      */       
/*  905 */       this.tempMethodRVPA = null;
/*      */     } 
/*  907 */     if (this.tempMethodRIPA != null) {
/*  908 */       this.method_RIPA_bands.addParameterAnnotation(this.tempMethodRIPA.numParams, this.tempMethodRIPA.annoN, this.tempMethodRIPA.pairN, this.tempMethodRIPA.typeRS, this.tempMethodRIPA.nameRU, this.tempMethodRIPA.t, this.tempMethodRIPA.values, this.tempMethodRIPA.caseArrayN, this.tempMethodRIPA.nestTypeRS, this.tempMethodRIPA.nestNameRU, this.tempMethodRIPA.nestPairN);
/*      */ 
/*      */ 
/*      */       
/*  912 */       this.tempMethodRIPA = null;
/*      */     } 
/*  914 */     if (this.codeFlags.size() > 0) {
/*  915 */       long latestCodeFlag = ((Long)this.codeFlags.get(this.codeFlags.size() - 1)).longValue();
/*  916 */       int latestLocalVariableTableN = this.codeLocalVariableTableN.get(this.codeLocalVariableTableN.size() - 1);
/*  917 */       if (latestCodeFlag == 4L && latestLocalVariableTableN == 0) {
/*  918 */         this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1);
/*  919 */         this.codeFlags.remove(this.codeFlags.size() - 1);
/*  920 */         this.codeFlags.add(Integer.valueOf(0));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected static int countArgs(String descriptor) {
/*  926 */     int bra = descriptor.indexOf('(');
/*  927 */     int ket = descriptor.indexOf(')');
/*  928 */     if (bra == -1 || ket == -1 || ket < bra) {
/*  929 */       throw new IllegalArgumentException("No arguments");
/*      */     }
/*      */     
/*  932 */     boolean inType = false;
/*  933 */     boolean consumingNextType = false;
/*  934 */     int count = 0;
/*  935 */     for (int i = bra + 1; i < ket; i++) {
/*  936 */       char charAt = descriptor.charAt(i);
/*  937 */       if (inType && charAt == ';') {
/*  938 */         inType = false;
/*  939 */         consumingNextType = false;
/*  940 */       } else if (!inType && charAt == 'L') {
/*  941 */         inType = true;
/*  942 */         count++;
/*  943 */       } else if (charAt == '[') {
/*  944 */         consumingNextType = true;
/*  945 */       } else if (!inType) {
/*      */         
/*  947 */         if (consumingNextType) {
/*  948 */           count++;
/*  949 */           consumingNextType = false;
/*  950 */         } else if (charAt == 'D' || charAt == 'J') {
/*  951 */           count += 2;
/*      */         } else {
/*  953 */           count++;
/*      */         } 
/*      */       } 
/*  956 */     }  return count;
/*      */   }
/*      */ 
/*      */   
/*      */   public void endOfClass() {
/*  961 */     int numFields = this.tempFieldDesc.size();
/*  962 */     this.class_field_count[this.index] = numFields;
/*  963 */     this.field_descr[this.index] = new CPNameAndType[numFields];
/*  964 */     this.field_flags[this.index] = new long[numFields];
/*  965 */     for (int i = 0; i < numFields; i++) {
/*  966 */       this.field_descr[this.index][i] = this.tempFieldDesc.get(i);
/*  967 */       this.field_flags[this.index][i] = ((Long)this.tempFieldFlags.get(i)).longValue();
/*      */     } 
/*  969 */     int numMethods = this.tempMethodDesc.size();
/*  970 */     this.class_method_count[this.index] = numMethods;
/*  971 */     this.method_descr[this.index] = new CPNameAndType[numMethods];
/*  972 */     this.method_flags[this.index] = new long[numMethods];
/*  973 */     for (int j = 0; j < numMethods; j++) {
/*  974 */       this.method_descr[this.index][j] = this.tempMethodDesc.get(j);
/*  975 */       this.method_flags[this.index][j] = ((Long)this.tempMethodFlags.get(j)).longValue();
/*      */     } 
/*  977 */     this.tempFieldDesc.clear();
/*  978 */     this.tempFieldFlags.clear();
/*  979 */     this.tempMethodDesc.clear();
/*  980 */     this.tempMethodFlags.clear();
/*  981 */     this.index++;
/*      */   }
/*      */   
/*      */   public void addSourceFile(String source) {
/*  985 */     String implicitSourceFileName = this.class_this[this.index].toString();
/*  986 */     if (implicitSourceFileName.indexOf('$') != -1) {
/*  987 */       implicitSourceFileName = implicitSourceFileName.substring(0, implicitSourceFileName.indexOf('$'));
/*      */     }
/*  989 */     implicitSourceFileName = implicitSourceFileName.substring(implicitSourceFileName.lastIndexOf('/') + 1) + ".java";
/*      */     
/*  991 */     if (source.equals(implicitSourceFileName)) {
/*  992 */       this.classSourceFile.add(null);
/*      */     } else {
/*  994 */       this.classSourceFile.add(this.cpBands.getCPUtf8(source));
/*      */     } 
/*  996 */     this.class_flags[this.index] = this.class_flags[this.index] | 0x20000L;
/*      */   }
/*      */   
/*      */   public void addEnclosingMethod(String owner, String name, String desc) {
/* 1000 */     this.class_flags[this.index] = this.class_flags[this.index] | 0x40000L;
/* 1001 */     this.classEnclosingMethodClass.add(this.cpBands.getCPClass(owner));
/* 1002 */     this.classEnclosingMethodDesc.add((name == null) ? null : this.cpBands.getCPNameAndType(name, desc));
/*      */   }
/*      */ 
/*      */   
/*      */   public void addClassAttribute(NewAttribute attribute) {
/* 1007 */     String attributeName = attribute.type;
/* 1008 */     for (Iterator<NewAttributeBands> iterator = this.classAttributeBands.iterator(); iterator.hasNext(); ) {
/* 1009 */       NewAttributeBands bands = iterator.next();
/* 1010 */       if (bands.getAttributeName().equals(attributeName)) {
/* 1011 */         bands.addAttribute(attribute);
/* 1012 */         int flagIndex = bands.getFlagIndex();
/* 1013 */         this.class_flags[this.index] = this.class_flags[this.index] | (1 << flagIndex);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1017 */     throw new RuntimeException("No suitable definition for " + attributeName);
/*      */   }
/*      */   
/*      */   public void addFieldAttribute(NewAttribute attribute) {
/* 1021 */     String attributeName = attribute.type;
/* 1022 */     for (Iterator<NewAttributeBands> iterator = this.fieldAttributeBands.iterator(); iterator.hasNext(); ) {
/* 1023 */       NewAttributeBands bands = iterator.next();
/* 1024 */       if (bands.getAttributeName().equals(attributeName)) {
/* 1025 */         bands.addAttribute(attribute);
/* 1026 */         int flagIndex = bands.getFlagIndex();
/* 1027 */         Long flags = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
/* 1028 */         this.tempFieldFlags.add(Long.valueOf(flags.longValue() | (1 << flagIndex)));
/*      */         return;
/*      */       } 
/*      */     } 
/* 1032 */     throw new RuntimeException("No suitable definition for " + attributeName);
/*      */   }
/*      */   
/*      */   public void addMethodAttribute(NewAttribute attribute) {
/* 1036 */     String attributeName = attribute.type;
/* 1037 */     for (Iterator<NewAttributeBands> iterator = this.methodAttributeBands.iterator(); iterator.hasNext(); ) {
/* 1038 */       NewAttributeBands bands = iterator.next();
/* 1039 */       if (bands.getAttributeName().equals(attributeName)) {
/* 1040 */         bands.addAttribute(attribute);
/* 1041 */         int flagIndex = bands.getFlagIndex();
/* 1042 */         Long flags = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1043 */         this.tempMethodFlags.add(Long.valueOf(flags.longValue() | (1 << flagIndex)));
/*      */         return;
/*      */       } 
/*      */     } 
/* 1047 */     throw new RuntimeException("No suitable definition for " + attributeName);
/*      */   }
/*      */   
/*      */   public void addCodeAttribute(NewAttribute attribute) {
/* 1051 */     String attributeName = attribute.type;
/* 1052 */     for (Iterator<NewAttributeBands> iterator = this.codeAttributeBands.iterator(); iterator.hasNext(); ) {
/* 1053 */       NewAttributeBands bands = iterator.next();
/* 1054 */       if (bands.getAttributeName().equals(attributeName)) {
/* 1055 */         bands.addAttribute(attribute);
/* 1056 */         int flagIndex = bands.getFlagIndex();
/* 1057 */         Long flags = this.codeFlags.remove(this.codeFlags.size() - 1);
/* 1058 */         this.codeFlags.add(Long.valueOf(flags.longValue() | (1 << flagIndex)));
/*      */         return;
/*      */       } 
/*      */     } 
/* 1062 */     throw new RuntimeException("No suitable definition for " + attributeName);
/*      */   }
/*      */   
/*      */   public void addMaxStack(int maxStack, int maxLocals) {
/* 1066 */     Long latestFlag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1067 */     Long newFlag = Long.valueOf((latestFlag.intValue() | 0x20000));
/* 1068 */     this.tempMethodFlags.add(newFlag);
/* 1069 */     this.codeMaxStack.add(maxStack);
/* 1070 */     if ((newFlag.longValue() & 0x8L) == 0L) {
/* 1071 */       maxLocals--;
/*      */     }
/* 1073 */     maxLocals -= this.numMethodArgs;
/* 1074 */     this.codeMaxLocals.add(maxLocals);
/*      */   }
/*      */   
/*      */   public void addCode() {
/* 1078 */     this.codeHandlerCount.add(0);
/* 1079 */     if (!this.stripDebug) {
/* 1080 */       this.codeFlags.add(Long.valueOf(4L));
/* 1081 */       this.codeLocalVariableTableN.add(0);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void addHandler(Label start, Label end, Label handler, String type) {
/* 1086 */     int handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1);
/* 1087 */     this.codeHandlerCount.add(handlers + 1);
/* 1088 */     this.codeHandlerStartP.add(start);
/* 1089 */     this.codeHandlerEndPO.add(end);
/* 1090 */     this.codeHandlerCatchPO.add(handler);
/* 1091 */     this.codeHandlerClass.add((type == null) ? null : this.cpBands.getCPClass(type));
/*      */   }
/*      */   
/*      */   public void addLineNumber(int line, Label start) {
/* 1095 */     Long latestCodeFlag = this.codeFlags.get(this.codeFlags.size() - 1);
/* 1096 */     if ((latestCodeFlag.intValue() & 0x2) == 0) {
/* 1097 */       this.codeFlags.remove(this.codeFlags.size() - 1);
/* 1098 */       this.codeFlags.add(Long.valueOf((latestCodeFlag.intValue() | 0x2)));
/* 1099 */       this.codeLineNumberTableN.add(1);
/*      */     } else {
/* 1101 */       this.codeLineNumberTableN.increment(this.codeLineNumberTableN.size() - 1);
/*      */     } 
/* 1103 */     this.codeLineNumberTableLine.add(line);
/* 1104 */     this.codeLineNumberTableBciP.add(start);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addLocalVariable(String name, String desc, String signature, Label start, Label end, int indx) {
/* 1109 */     if (signature != null) {
/* 1110 */       Long latestCodeFlag = this.codeFlags.get(this.codeFlags.size() - 1);
/* 1111 */       if ((latestCodeFlag.intValue() & 0x8) == 0) {
/* 1112 */         this.codeFlags.remove(this.codeFlags.size() - 1);
/* 1113 */         this.codeFlags.add(Long.valueOf((latestCodeFlag.intValue() | 0x8)));
/* 1114 */         this.codeLocalVariableTypeTableN.add(1);
/*      */       } else {
/* 1116 */         this.codeLocalVariableTypeTableN.increment(this.codeLocalVariableTypeTableN.size() - 1);
/*      */       } 
/* 1118 */       this.codeLocalVariableTypeTableBciP.add(start);
/* 1119 */       this.codeLocalVariableTypeTableSpanO.add(end);
/* 1120 */       this.codeLocalVariableTypeTableNameRU.add(this.cpBands.getCPUtf8(name));
/* 1121 */       this.codeLocalVariableTypeTableTypeRS.add(this.cpBands.getCPSignature(signature));
/* 1122 */       this.codeLocalVariableTypeTableSlot.add(indx);
/*      */     } 
/*      */     
/* 1125 */     this.codeLocalVariableTableN.increment(this.codeLocalVariableTableN.size() - 1);
/* 1126 */     this.codeLocalVariableTableBciP.add(start);
/* 1127 */     this.codeLocalVariableTableSpanO.add(end);
/* 1128 */     this.codeLocalVariableTableNameRU.add(this.cpBands.getCPUtf8(name));
/* 1129 */     this.codeLocalVariableTableTypeRS.add(this.cpBands.getCPSignature(desc));
/* 1130 */     this.codeLocalVariableTableSlot.add(indx);
/*      */   }
/*      */   
/*      */   public void doBciRenumbering(IntList bciRenumbering, Map labelsToOffsets) {
/* 1134 */     renumberBci(this.codeLineNumberTableBciP, bciRenumbering, labelsToOffsets);
/* 1135 */     renumberBci(this.codeLocalVariableTableBciP, bciRenumbering, labelsToOffsets);
/* 1136 */     renumberOffsetBci(this.codeLocalVariableTableBciP, this.codeLocalVariableTableSpanO, bciRenumbering, labelsToOffsets);
/* 1137 */     renumberBci(this.codeLocalVariableTypeTableBciP, bciRenumbering, labelsToOffsets);
/* 1138 */     renumberOffsetBci(this.codeLocalVariableTypeTableBciP, this.codeLocalVariableTypeTableSpanO, bciRenumbering, labelsToOffsets);
/*      */     
/* 1140 */     renumberBci(this.codeHandlerStartP, bciRenumbering, labelsToOffsets);
/* 1141 */     renumberOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, bciRenumbering, labelsToOffsets);
/* 1142 */     renumberDoubleOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, this.codeHandlerCatchPO, bciRenumbering, labelsToOffsets);
/*      */ 
/*      */     
/* 1145 */     for (Iterator<NewAttributeBands> iterator3 = this.classAttributeBands.iterator(); iterator3.hasNext(); ) {
/* 1146 */       NewAttributeBands newAttributeBandSet = iterator3.next();
/* 1147 */       newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
/*      */     } 
/* 1149 */     for (Iterator<NewAttributeBands> iterator2 = this.methodAttributeBands.iterator(); iterator2.hasNext(); ) {
/* 1150 */       NewAttributeBands newAttributeBandSet = iterator2.next();
/* 1151 */       newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
/*      */     } 
/* 1153 */     for (Iterator<NewAttributeBands> iterator1 = this.fieldAttributeBands.iterator(); iterator1.hasNext(); ) {
/* 1154 */       NewAttributeBands newAttributeBandSet = iterator1.next();
/* 1155 */       newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
/*      */     } 
/* 1157 */     for (Iterator<NewAttributeBands> iterator = this.codeAttributeBands.iterator(); iterator.hasNext(); ) {
/* 1158 */       NewAttributeBands newAttributeBandSet = iterator.next();
/* 1159 */       newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void renumberBci(List<Integer> list, IntList bciRenumbering, Map labelsToOffsets) {
/* 1164 */     for (int i = list.size() - 1; i >= 0; i--) {
/* 1165 */       Object label = list.get(i);
/* 1166 */       if (label instanceof Integer) {
/*      */         break;
/*      */       }
/* 1169 */       if (label instanceof Label) {
/* 1170 */         list.remove(i);
/* 1171 */         Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
/* 1172 */         list.add(i, Integer.valueOf(bciRenumbering.get(bytecodeIndex.intValue())));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void renumberOffsetBci(List<Integer> relative, List<Integer> list, IntList bciRenumbering, Map labelsToOffsets) {
/* 1179 */     for (int i = list.size() - 1; i >= 0; i--) {
/* 1180 */       Object label = list.get(i);
/* 1181 */       if (label instanceof Integer) {
/*      */         break;
/*      */       }
/* 1184 */       if (label instanceof Label) {
/* 1185 */         list.remove(i);
/* 1186 */         Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
/*      */         
/* 1188 */         Integer renumberedOffset = Integer.valueOf(bciRenumbering.get(bytecodeIndex.intValue()) - ((Integer)relative.get(i)).intValue());
/* 1189 */         list.add(i, renumberedOffset);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void renumberDoubleOffsetBci(List<Integer> relative, List<Integer> firstOffset, List<Integer> list, IntList bciRenumbering, Map labelsToOffsets) {
/* 1197 */     for (int i = list.size() - 1; i >= 0; i--) {
/* 1198 */       Object label = list.get(i);
/* 1199 */       if (label instanceof Integer) {
/*      */         break;
/*      */       }
/* 1202 */       if (label instanceof Label) {
/* 1203 */         list.remove(i);
/* 1204 */         Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
/* 1205 */         Integer renumberedOffset = Integer.valueOf(bciRenumbering.get(bytecodeIndex.intValue()) - ((Integer)relative
/* 1206 */             .get(i)).intValue() - ((Integer)firstOffset.get(i)).intValue());
/* 1207 */         list.add(i, renumberedOffset);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isAnySyntheticClasses() {
/* 1213 */     return this.anySyntheticClasses;
/*      */   }
/*      */   
/*      */   public boolean isAnySyntheticFields() {
/* 1217 */     return this.anySyntheticFields;
/*      */   }
/*      */   
/*      */   public boolean isAnySyntheticMethods() {
/* 1221 */     return this.anySyntheticMethods;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void addParameterAnnotation(int parameter, String desc, boolean visible, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/* 1227 */     if (visible) {
/* 1228 */       if (this.tempMethodRVPA == null) {
/* 1229 */         this.tempMethodRVPA = new TempParamAnnotation(this.numMethodArgs);
/* 1230 */         this.tempMethodRVPA.addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/*      */       } 
/*      */       
/* 1233 */       Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1234 */       this.tempMethodFlags.add(Long.valueOf(flag.longValue() | 0x800000L));
/*      */     } else {
/* 1236 */       if (this.tempMethodRIPA == null) {
/* 1237 */         this.tempMethodRIPA = new TempParamAnnotation(this.numMethodArgs);
/* 1238 */         this.tempMethodRIPA.addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/*      */       } 
/*      */       
/* 1241 */       Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1242 */       this.tempMethodFlags.add(Long.valueOf(flag.longValue() | 0x1000000L));
/*      */     } 
/*      */   }
/*      */   
/*      */   private static class TempParamAnnotation
/*      */   {
/*      */     int numParams;
/*      */     int[] annoN;
/* 1250 */     IntList pairN = new IntList();
/* 1251 */     List typeRS = new ArrayList();
/* 1252 */     List nameRU = new ArrayList();
/* 1253 */     List t = new ArrayList();
/* 1254 */     List values = new ArrayList();
/* 1255 */     List caseArrayN = new ArrayList();
/* 1256 */     List nestTypeRS = new ArrayList();
/* 1257 */     List nestNameRU = new ArrayList();
/* 1258 */     List nestPairN = new ArrayList();
/*      */     
/*      */     public TempParamAnnotation(int numParams) {
/* 1261 */       this.numParams = numParams;
/* 1262 */       this.annoN = new int[numParams];
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void addParameterAnnotation(int parameter, String desc, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/* 1268 */       this.annoN[parameter] = this.annoN[parameter] + 1;
/* 1269 */       this.typeRS.add(desc);
/* 1270 */       this.pairN.add(nameRU.size());
/* 1271 */       this.nameRU.addAll(nameRU);
/* 1272 */       this.t.addAll(t);
/* 1273 */       this.values.addAll(values);
/* 1274 */       this.caseArrayN.addAll(caseArrayN);
/* 1275 */       this.nestTypeRS.addAll(nestTypeRS);
/* 1276 */       this.nestNameRU.addAll(nestNameRU);
/* 1277 */       this.nestPairN.addAll(nestPairN);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void addAnnotation(int context, String desc, boolean visible, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/*      */     Long flag;
/* 1284 */     switch (context) {
/*      */       case 0:
/* 1286 */         if (visible) {
/* 1287 */           this.class_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1288 */           if ((this.class_flags[this.index] & 0x200000L) != 0L) {
/* 1289 */             this.class_RVA_bands.incrementAnnoN(); break;
/*      */           } 
/* 1291 */           this.class_RVA_bands.newEntryInAnnoN();
/* 1292 */           this.class_flags[this.index] = this.class_flags[this.index] | 0x200000L;
/*      */           break;
/*      */         } 
/* 1295 */         this.class_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1296 */         if ((this.class_flags[this.index] & 0x400000L) != 0L) {
/* 1297 */           this.class_RIA_bands.incrementAnnoN(); break;
/*      */         } 
/* 1299 */         this.class_RIA_bands.newEntryInAnnoN();
/* 1300 */         this.class_flags[this.index] = this.class_flags[this.index] | 0x400000L;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/* 1305 */         if (visible) {
/* 1306 */           this.field_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1307 */           Long long_ = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
/* 1308 */           if ((long_.intValue() & 0x200000) != 0) {
/* 1309 */             this.field_RVA_bands.incrementAnnoN();
/*      */           } else {
/* 1311 */             this.field_RVA_bands.newEntryInAnnoN();
/*      */           } 
/* 1313 */           this.tempFieldFlags.add(Long.valueOf((long_.intValue() | 0x200000))); break;
/*      */         } 
/* 1315 */         this.field_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1316 */         flag = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
/* 1317 */         if ((flag.intValue() & 0x400000) != 0) {
/* 1318 */           this.field_RIA_bands.incrementAnnoN();
/*      */         } else {
/* 1320 */           this.field_RIA_bands.newEntryInAnnoN();
/*      */         } 
/* 1322 */         this.tempFieldFlags.add(Long.valueOf((flag.intValue() | 0x400000)));
/*      */         break;
/*      */       
/*      */       case 2:
/* 1326 */         if (visible) {
/* 1327 */           this.method_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1328 */           flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1329 */           if ((flag.intValue() & 0x200000) != 0) {
/* 1330 */             this.method_RVA_bands.incrementAnnoN();
/*      */           } else {
/* 1332 */             this.method_RVA_bands.newEntryInAnnoN();
/*      */           } 
/* 1334 */           this.tempMethodFlags.add(Long.valueOf((flag.intValue() | 0x200000))); break;
/*      */         } 
/* 1336 */         this.method_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1337 */         flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1338 */         if ((flag.intValue() & 0x400000) != 0) {
/* 1339 */           this.method_RIA_bands.incrementAnnoN();
/*      */         } else {
/* 1341 */           this.method_RIA_bands.newEntryInAnnoN();
/*      */         } 
/* 1343 */         this.tempMethodFlags.add(Long.valueOf((flag.intValue() | 0x400000)));
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void addAnnotationDefault(List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
/* 1351 */     this.method_AD_bands.addAnnotation(null, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
/* 1352 */     Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
/* 1353 */     this.tempMethodFlags.add(Long.valueOf(flag.longValue() | 0x2000000L));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeCurrentClass() {
/* 1362 */     if ((this.class_flags[this.index] & 0x20000L) != 0L) {
/* 1363 */       this.classSourceFile.remove(this.classSourceFile.size() - 1);
/*      */     }
/* 1365 */     if ((this.class_flags[this.index] & 0x40000L) != 0L) {
/* 1366 */       this.classEnclosingMethodClass.remove(this.classEnclosingMethodClass.size() - 1);
/* 1367 */       this.classEnclosingMethodDesc.remove(this.classEnclosingMethodDesc.size() - 1);
/*      */     } 
/* 1369 */     if ((this.class_flags[this.index] & 0x80000L) != 0L) {
/* 1370 */       this.classSignature.remove(this.classSignature.size() - 1);
/*      */     }
/* 1372 */     if ((this.class_flags[this.index] & 0x200000L) != 0L) {
/* 1373 */       this.class_RVA_bands.removeLatest();
/*      */     }
/* 1375 */     if ((this.class_flags[this.index] & 0x400000L) != 0L) {
/* 1376 */       this.class_RIA_bands.removeLatest();
/*      */     }
/* 1378 */     for (Iterator<Long> iterator1 = this.tempFieldFlags.iterator(); iterator1.hasNext(); ) {
/* 1379 */       Long flagsL = iterator1.next();
/* 1380 */       long flags = flagsL.longValue();
/* 1381 */       if ((flags & 0x80000L) != 0L) {
/* 1382 */         this.fieldSignature.remove(this.fieldSignature.size() - 1);
/*      */       }
/* 1384 */       if ((flags & 0x20000L) != 0L) {
/* 1385 */         this.fieldConstantValueKQ.remove(this.fieldConstantValueKQ.size() - 1);
/*      */       }
/* 1387 */       if ((flags & 0x200000L) != 0L) {
/* 1388 */         this.field_RVA_bands.removeLatest();
/*      */       }
/* 1390 */       if ((flags & 0x400000L) != 0L) {
/* 1391 */         this.field_RIA_bands.removeLatest();
/*      */       }
/*      */     } 
/* 1394 */     for (Iterator<Long> iterator = this.tempMethodFlags.iterator(); iterator.hasNext(); ) {
/* 1395 */       Long flagsL = iterator.next();
/* 1396 */       long flags = flagsL.longValue();
/* 1397 */       if ((flags & 0x80000L) != 0L) {
/* 1398 */         this.methodSignature.remove(this.methodSignature.size() - 1);
/*      */       }
/* 1400 */       if ((flags & 0x40000L) != 0L) {
/* 1401 */         int exceptions = this.methodExceptionNumber.remove(this.methodExceptionNumber.size() - 1);
/* 1402 */         for (int i = 0; i < exceptions; i++) {
/* 1403 */           this.methodExceptionClasses.remove(this.methodExceptionClasses.size() - 1);
/*      */         }
/*      */       } 
/* 1406 */       if ((flags & 0x20000L) != 0L) {
/* 1407 */         this.codeMaxLocals.remove(this.codeMaxLocals.size() - 1);
/* 1408 */         this.codeMaxStack.remove(this.codeMaxStack.size() - 1);
/* 1409 */         int handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1);
/* 1410 */         for (int i = 0; i < handlers; i++) {
/* 1411 */           int index = this.codeHandlerStartP.size() - 1;
/* 1412 */           this.codeHandlerStartP.remove(index);
/* 1413 */           this.codeHandlerEndPO.remove(index);
/* 1414 */           this.codeHandlerCatchPO.remove(index);
/* 1415 */           this.codeHandlerClass.remove(index);
/*      */         } 
/* 1417 */         if (!this.stripDebug) {
/* 1418 */           long cdeFlags = ((Long)this.codeFlags.remove(this.codeFlags.size() - 1)).longValue();
/* 1419 */           int numLocalVariables = this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1);
/* 1420 */           for (int j = 0; j < numLocalVariables; j++) {
/* 1421 */             int location = this.codeLocalVariableTableBciP.size() - 1;
/* 1422 */             this.codeLocalVariableTableBciP.remove(location);
/* 1423 */             this.codeLocalVariableTableSpanO.remove(location);
/* 1424 */             this.codeLocalVariableTableNameRU.remove(location);
/* 1425 */             this.codeLocalVariableTableTypeRS.remove(location);
/* 1426 */             this.codeLocalVariableTableSlot.remove(location);
/*      */           } 
/* 1428 */           if ((cdeFlags & 0x8L) != 0L) {
/*      */             
/* 1430 */             int numLocalVariablesInTypeTable = this.codeLocalVariableTypeTableN.remove(this.codeLocalVariableTypeTableN.size() - 1);
/* 1431 */             for (int k = 0; k < numLocalVariablesInTypeTable; k++) {
/* 1432 */               int location = this.codeLocalVariableTypeTableBciP.size() - 1;
/* 1433 */               this.codeLocalVariableTypeTableBciP.remove(location);
/* 1434 */               this.codeLocalVariableTypeTableSpanO.remove(location);
/* 1435 */               this.codeLocalVariableTypeTableNameRU.remove(location);
/* 1436 */               this.codeLocalVariableTypeTableTypeRS.remove(location);
/* 1437 */               this.codeLocalVariableTypeTableSlot.remove(location);
/*      */             } 
/*      */           } 
/* 1440 */           if ((cdeFlags & 0x2L) != 0L) {
/* 1441 */             int numLineNumbers = this.codeLineNumberTableN.remove(this.codeLineNumberTableN.size() - 1);
/* 1442 */             for (int k = 0; k < numLineNumbers; k++) {
/* 1443 */               int location = this.codeLineNumberTableBciP.size() - 1;
/* 1444 */               this.codeLineNumberTableBciP.remove(location);
/* 1445 */               this.codeLineNumberTableLine.remove(location);
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/* 1450 */       if ((flags & 0x200000L) != 0L) {
/* 1451 */         this.method_RVA_bands.removeLatest();
/*      */       }
/* 1453 */       if ((flags & 0x400000L) != 0L) {
/* 1454 */         this.method_RIA_bands.removeLatest();
/*      */       }
/* 1456 */       if ((flags & 0x800000L) != 0L) {
/* 1457 */         this.method_RVPA_bands.removeLatest();
/*      */       }
/* 1459 */       if ((flags & 0x1000000L) != 0L) {
/* 1460 */         this.method_RIPA_bands.removeLatest();
/*      */       }
/* 1462 */       if ((flags & 0x2000000L) != 0L) {
/* 1463 */         this.method_AD_bands.removeLatest();
/*      */       }
/*      */     } 
/* 1466 */     this.class_this[this.index] = null;
/* 1467 */     this.class_super[this.index] = null;
/* 1468 */     this.class_interface_count[this.index] = 0;
/* 1469 */     this.class_interface[this.index] = null;
/* 1470 */     this.major_versions[this.index] = 0;
/* 1471 */     this.class_flags[this.index] = 0L;
/* 1472 */     this.tempFieldDesc.clear();
/* 1473 */     this.tempFieldFlags.clear();
/* 1474 */     this.tempMethodDesc.clear();
/* 1475 */     this.tempMethodFlags.clear();
/* 1476 */     if (this.index > 0) {
/* 1477 */       this.index--;
/*      */     }
/*      */   }
/*      */   
/*      */   public int numClassesProcessed() {
/* 1482 */     return this.index;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\ClassBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */