package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.Label;

public class ClassBands extends BandSet {
   private final CpBands cpBands;
   private final AttributeDefinitionBands attrBands;
   private final CPClass[] class_this;
   private final CPClass[] class_super;
   private final CPClass[][] class_interface;
   private final int[] class_interface_count;
   private final int[] major_versions;
   private final long[] class_flags;
   private int[] class_attr_calls;
   private final List classSourceFile = new ArrayList();
   private final List classEnclosingMethodClass = new ArrayList();
   private final List classEnclosingMethodDesc = new ArrayList();
   private final List classSignature = new ArrayList();
   private final IntList classFileVersionMinor = new IntList();
   private final IntList classFileVersionMajor = new IntList();
   private final int[] class_field_count;
   private final CPNameAndType[][] field_descr;
   private final long[][] field_flags;
   private int[] field_attr_calls;
   private final List fieldConstantValueKQ = new ArrayList();
   private final List fieldSignature = new ArrayList();
   private final int[] class_method_count;
   private final CPNameAndType[][] method_descr;
   private final long[][] method_flags;
   private int[] method_attr_calls;
   private final List methodSignature = new ArrayList();
   private final IntList methodExceptionNumber = new IntList();
   private final List methodExceptionClasses = new ArrayList();
   private int[] codeHeaders;
   private final IntList codeMaxStack = new IntList();
   private final IntList codeMaxLocals = new IntList();
   private final IntList codeHandlerCount = new IntList();
   private final List codeHandlerStartP = new ArrayList();
   private final List codeHandlerEndPO = new ArrayList();
   private final List codeHandlerCatchPO = new ArrayList();
   private final List codeHandlerClass = new ArrayList();
   private final List codeFlags = new ArrayList();
   private int[] code_attr_calls;
   private final IntList codeLineNumberTableN = new IntList();
   private final List codeLineNumberTableBciP = new ArrayList();
   private final IntList codeLineNumberTableLine = new IntList();
   private final IntList codeLocalVariableTableN = new IntList();
   private final List codeLocalVariableTableBciP = new ArrayList();
   private final List codeLocalVariableTableSpanO = new ArrayList();
   private final List codeLocalVariableTableNameRU = new ArrayList();
   private final List codeLocalVariableTableTypeRS = new ArrayList();
   private final IntList codeLocalVariableTableSlot = new IntList();
   private final IntList codeLocalVariableTypeTableN = new IntList();
   private final List codeLocalVariableTypeTableBciP = new ArrayList();
   private final List codeLocalVariableTypeTableSpanO = new ArrayList();
   private final List codeLocalVariableTypeTableNameRU = new ArrayList();
   private final List codeLocalVariableTypeTableTypeRS = new ArrayList();
   private final IntList codeLocalVariableTypeTableSlot = new IntList();
   private final MetadataBandGroup class_RVA_bands;
   private final MetadataBandGroup class_RIA_bands;
   private final MetadataBandGroup field_RVA_bands;
   private final MetadataBandGroup field_RIA_bands;
   private final MetadataBandGroup method_RVA_bands;
   private final MetadataBandGroup method_RIA_bands;
   private final MetadataBandGroup method_RVPA_bands;
   private final MetadataBandGroup method_RIPA_bands;
   private final MetadataBandGroup method_AD_bands;
   private final List classAttributeBands = new ArrayList();
   private final List methodAttributeBands = new ArrayList();
   private final List fieldAttributeBands = new ArrayList();
   private final List codeAttributeBands = new ArrayList();
   private final List tempFieldFlags = new ArrayList();
   private final List tempFieldDesc = new ArrayList();
   private final List tempMethodFlags = new ArrayList();
   private final List tempMethodDesc = new ArrayList();
   private TempParamAnnotation tempMethodRVPA;
   private TempParamAnnotation tempMethodRIPA;
   private boolean anySyntheticClasses = false;
   private boolean anySyntheticFields = false;
   private boolean anySyntheticMethods = false;
   private final Segment segment;
   private final Map classReferencesInnerClass = new HashMap();
   private final boolean stripDebug;
   private int index = 0;
   private int numMethodArgs = 0;
   private int[] class_InnerClasses_N;
   private CPClass[] class_InnerClasses_RC;
   private int[] class_InnerClasses_F;
   private List classInnerClassesOuterRCN;
   private List classInnerClassesNameRUN;

   public ClassBands(Segment segment, int numClasses, int effort, boolean stripDebug) throws IOException {
      super(effort, segment.getSegmentHeader());
      this.stripDebug = stripDebug;
      this.segment = segment;
      this.cpBands = segment.getCpBands();
      this.attrBands = segment.getAttrBands();
      this.class_this = new CPClass[numClasses];
      this.class_super = new CPClass[numClasses];
      this.class_interface_count = new int[numClasses];
      this.class_interface = new CPClass[numClasses][];
      this.class_field_count = new int[numClasses];
      this.class_method_count = new int[numClasses];
      this.field_descr = new CPNameAndType[numClasses][];
      this.field_flags = new long[numClasses][];
      this.method_descr = new CPNameAndType[numClasses][];
      this.method_flags = new long[numClasses][];

      for(int i = 0; i < numClasses; ++i) {
         this.field_flags[i] = new long[0];
         this.method_flags[i] = new long[0];
      }

      this.major_versions = new int[numClasses];
      this.class_flags = new long[numClasses];
      this.class_RVA_bands = new MetadataBandGroup("RVA", 0, this.cpBands, this.segmentHeader, effort);
      this.class_RIA_bands = new MetadataBandGroup("RIA", 0, this.cpBands, this.segmentHeader, effort);
      this.field_RVA_bands = new MetadataBandGroup("RVA", 1, this.cpBands, this.segmentHeader, effort);
      this.field_RIA_bands = new MetadataBandGroup("RIA", 1, this.cpBands, this.segmentHeader, effort);
      this.method_RVA_bands = new MetadataBandGroup("RVA", 2, this.cpBands, this.segmentHeader, effort);
      this.method_RIA_bands = new MetadataBandGroup("RIA", 2, this.cpBands, this.segmentHeader, effort);
      this.method_RVPA_bands = new MetadataBandGroup("RVPA", 2, this.cpBands, this.segmentHeader, effort);
      this.method_RIPA_bands = new MetadataBandGroup("RIPA", 2, this.cpBands, this.segmentHeader, effort);
      this.method_AD_bands = new MetadataBandGroup("AD", 2, this.cpBands, this.segmentHeader, effort);
      this.createNewAttributeBands();
   }

   private void createNewAttributeBands() throws IOException {
      List classAttributeLayouts = this.attrBands.getClassAttributeLayouts();
      Iterator iterator = classAttributeLayouts.iterator();

      while(iterator.hasNext()) {
         AttributeDefinitionBands.AttributeDefinition def = (AttributeDefinitionBands.AttributeDefinition)iterator.next();
         this.classAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
      }

      List methodAttributeLayouts = this.attrBands.getMethodAttributeLayouts();
      Iterator iterator = methodAttributeLayouts.iterator();

      while(iterator.hasNext()) {
         AttributeDefinitionBands.AttributeDefinition def = (AttributeDefinitionBands.AttributeDefinition)iterator.next();
         this.methodAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
      }

      List fieldAttributeLayouts = this.attrBands.getFieldAttributeLayouts();
      Iterator iterator = fieldAttributeLayouts.iterator();

      while(iterator.hasNext()) {
         AttributeDefinitionBands.AttributeDefinition def = (AttributeDefinitionBands.AttributeDefinition)iterator.next();
         this.fieldAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
      }

      List codeAttributeLayouts = this.attrBands.getCodeAttributeLayouts();
      Iterator iterator = codeAttributeLayouts.iterator();

      while(iterator.hasNext()) {
         AttributeDefinitionBands.AttributeDefinition def = (AttributeDefinitionBands.AttributeDefinition)iterator.next();
         this.codeAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
      }

   }

   public void addClass(int major, int flags, String className, String signature, String superName, String[] interfaces) {
      this.class_this[this.index] = this.cpBands.getCPClass(className);
      this.class_super[this.index] = this.cpBands.getCPClass(superName);
      this.class_interface_count[this.index] = interfaces.length;
      this.class_interface[this.index] = new CPClass[interfaces.length];

      for(int i = 0; i < interfaces.length; ++i) {
         this.class_interface[this.index][i] = this.cpBands.getCPClass(interfaces[i]);
      }

      this.major_versions[this.index] = major;
      this.class_flags[this.index] = (long)flags;
      if (!this.anySyntheticClasses && (flags & 4096) != 0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
         this.cpBands.addCPUtf8("Synthetic");
         this.anySyntheticClasses = true;
      }

      if ((flags & 131072) != 0) {
         flags &= -131073;
         flags |= 1048576;
      }

      if (signature != null) {
         long[] var10000 = this.class_flags;
         int var10001 = this.index;
         var10000[var10001] |= 524288L;
         this.classSignature.add(this.cpBands.getCPSignature(signature));
      }

   }

   public void currentClassReferencesInnerClass(CPClass inner) {
      if (this.index < this.class_this.length) {
         CPClass currentClass = this.class_this[this.index];
         if (currentClass != null && !currentClass.equals(inner) && !this.isInnerClassOf(currentClass.toString(), inner)) {
            Set referencedInnerClasses = (Set)this.classReferencesInnerClass.get(currentClass);
            if (referencedInnerClasses == null) {
               referencedInnerClasses = new HashSet();
               this.classReferencesInnerClass.put(currentClass, referencedInnerClasses);
            }

            ((Set)referencedInnerClasses).add(inner);
         }
      }

   }

   private boolean isInnerClassOf(String possibleInner, CPClass possibleOuter) {
      if (this.isInnerClass(possibleInner)) {
         String superClassName = possibleInner.substring(0, possibleInner.lastIndexOf(36));
         return superClassName.equals(possibleOuter.toString()) ? true : this.isInnerClassOf(superClassName, possibleOuter);
      } else {
         return false;
      }
   }

   private boolean isInnerClass(String possibleInner) {
      return possibleInner.indexOf(36) != -1;
   }

   public void addField(int flags, String name, String desc, String signature, Object value) {
      flags &= 65535;
      this.tempFieldDesc.add(this.cpBands.getCPNameAndType(name, desc));
      if (signature != null) {
         this.fieldSignature.add(this.cpBands.getCPSignature(signature));
         flags |= 524288;
      }

      if ((flags & 131072) != 0) {
         flags &= -131073;
         flags |= 1048576;
      }

      if (value != null) {
         this.fieldConstantValueKQ.add(this.cpBands.getConstant(value));
         flags |= 131072;
      }

      if (!this.anySyntheticFields && (flags & 4096) != 0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
         this.cpBands.addCPUtf8("Synthetic");
         this.anySyntheticFields = true;
      }

      this.tempFieldFlags.add((long)flags);
   }

   public void finaliseBands() {
      int defaultMajorVersion = this.segmentHeader.getDefaultMajorVersion();

      int removed;
      int i;
      long[] var10000;
      for(removed = 0; removed < this.class_flags.length; ++removed) {
         i = this.major_versions[removed];
         if (i != defaultMajorVersion) {
            var10000 = this.class_flags;
            var10000[removed] |= 16777216L;
            this.classFileVersionMajor.add(i);
            this.classFileVersionMinor.add(0);
         }
      }

      this.codeHeaders = new int[this.codeHandlerCount.size()];
      removed = 0;

      int i;
      for(i = 0; i < this.codeHeaders.length; ++i) {
         int numHandlers = this.codeHandlerCount.get(i - removed);
         i = this.codeMaxLocals.get(i - removed);
         int maxStack = this.codeMaxStack.get(i - removed);
         int header;
         if (numHandlers == 0) {
            header = i * 12 + maxStack + 1;
            if (header < 145 && maxStack < 12) {
               this.codeHeaders[i] = header;
            }
         } else if (numHandlers == 1) {
            header = i * 8 + maxStack + 145;
            if (header < 209 && maxStack < 8) {
               this.codeHeaders[i] = header;
            }
         } else if (numHandlers == 2) {
            header = i * 7 + maxStack + 209;
            if (header < 256 && maxStack < 7) {
               this.codeHeaders[i] = header;
            }
         }

         if (this.codeHeaders[i] != 0) {
            this.codeHandlerCount.remove(i - removed);
            this.codeMaxLocals.remove(i - removed);
            this.codeMaxStack.remove(i - removed);
            ++removed;
         } else if (!this.segment.getSegmentHeader().have_all_code_flags()) {
            this.codeFlags.add(0L);
         }
      }

      IntList innerClassesN = new IntList();
      List icLocal = new ArrayList();

      Iterator iterator;
      for(i = 0; i < this.class_this.length; ++i) {
         CPClass cpClass = this.class_this[i];
         Set referencedInnerClasses = (Set)this.classReferencesInnerClass.get(cpClass);
         if (referencedInnerClasses != null) {
            int innerN = 0;
            List innerClasses = this.segment.getIcBands().getInnerClassesForOuter(cpClass.toString());
            if (innerClasses != null) {
               iterator = innerClasses.iterator();

               while(iterator.hasNext()) {
                  referencedInnerClasses.remove(((IcBands.IcTuple)iterator.next()).C);
               }
            }

            iterator = referencedInnerClasses.iterator();

            while(iterator.hasNext()) {
               CPClass inner = (CPClass)iterator.next();
               IcBands.IcTuple icTuple = this.segment.getIcBands().getIcTuple(inner);
               if (icTuple != null && !icTuple.isAnonymous()) {
                  icLocal.add(icTuple);
                  ++innerN;
               }
            }

            if (innerN != 0) {
               innerClassesN.add(innerN);
               var10000 = this.class_flags;
               var10000[i] |= 8388608L;
            }
         }
      }

      this.class_InnerClasses_N = innerClassesN.toArray();
      this.class_InnerClasses_RC = new CPClass[icLocal.size()];
      this.class_InnerClasses_F = new int[icLocal.size()];
      this.classInnerClassesOuterRCN = new ArrayList();
      this.classInnerClassesNameRUN = new ArrayList();

      for(i = 0; i < this.class_InnerClasses_RC.length; ++i) {
         IcBands.IcTuple icTuple = (IcBands.IcTuple)icLocal.get(i);
         this.class_InnerClasses_RC[i] = icTuple.C;
         if (icTuple.C2 == null && icTuple.N == null) {
            this.class_InnerClasses_F[i] = 0;
         } else {
            if (icTuple.F == 0) {
               this.class_InnerClasses_F[i] = 65536;
            } else {
               this.class_InnerClasses_F[i] = icTuple.F;
            }

            this.classInnerClassesOuterRCN.add(icTuple.C2);
            this.classInnerClassesNameRUN.add(icTuple.N);
         }
      }

      IntList classAttrCalls = new IntList();
      IntList fieldAttrCalls = new IntList();
      IntList methodAttrCalls = new IntList();
      IntList codeAttrCalls = new IntList();
      if (this.class_RVA_bands.hasContent()) {
         classAttrCalls.add(this.class_RVA_bands.numBackwardsCalls());
      }

      if (this.class_RIA_bands.hasContent()) {
         classAttrCalls.add(this.class_RIA_bands.numBackwardsCalls());
      }

      if (this.field_RVA_bands.hasContent()) {
         fieldAttrCalls.add(this.field_RVA_bands.numBackwardsCalls());
      }

      if (this.field_RIA_bands.hasContent()) {
         fieldAttrCalls.add(this.field_RIA_bands.numBackwardsCalls());
      }

      if (this.method_RVA_bands.hasContent()) {
         methodAttrCalls.add(this.method_RVA_bands.numBackwardsCalls());
      }

      if (this.method_RIA_bands.hasContent()) {
         methodAttrCalls.add(this.method_RIA_bands.numBackwardsCalls());
      }

      if (this.method_RVPA_bands.hasContent()) {
         methodAttrCalls.add(this.method_RVPA_bands.numBackwardsCalls());
      }

      if (this.method_RIPA_bands.hasContent()) {
         methodAttrCalls.add(this.method_RIPA_bands.numBackwardsCalls());
      }

      if (this.method_AD_bands.hasContent()) {
         methodAttrCalls.add(this.method_AD_bands.numBackwardsCalls());
      }

      Comparator comparator = (arg0, arg1) -> {
         NewAttributeBands bands0 = (NewAttributeBands)arg0;
         NewAttributeBands bands1 = (NewAttributeBands)arg1;
         return bands0.getFlagIndex() - bands1.getFlagIndex();
      };
      Collections.sort(this.classAttributeBands, comparator);
      Collections.sort(this.methodAttributeBands, comparator);
      Collections.sort(this.fieldAttributeBands, comparator);
      Collections.sort(this.codeAttributeBands, comparator);
      iterator = this.classAttributeBands.iterator();

      while(true) {
         int i;
         NewAttributeBands bands;
         int[] backwardsCallCounts;
         do {
            if (!iterator.hasNext()) {
               iterator = this.methodAttributeBands.iterator();

               while(true) {
                  do {
                     if (!iterator.hasNext()) {
                        iterator = this.fieldAttributeBands.iterator();

                        while(true) {
                           do {
                              if (!iterator.hasNext()) {
                                 iterator = this.codeAttributeBands.iterator();

                                 while(true) {
                                    do {
                                       if (!iterator.hasNext()) {
                                          this.class_attr_calls = classAttrCalls.toArray();
                                          this.field_attr_calls = fieldAttrCalls.toArray();
                                          this.method_attr_calls = methodAttrCalls.toArray();
                                          this.code_attr_calls = codeAttrCalls.toArray();
                                          return;
                                       }

                                       bands = (NewAttributeBands)iterator.next();
                                    } while(!bands.isUsedAtLeastOnce());

                                    backwardsCallCounts = bands.numBackwardsCalls();

                                    for(i = 0; i < backwardsCallCounts.length; ++i) {
                                       codeAttrCalls.add(backwardsCallCounts[i]);
                                    }
                                 }
                              }

                              bands = (NewAttributeBands)iterator.next();
                           } while(!bands.isUsedAtLeastOnce());

                           backwardsCallCounts = bands.numBackwardsCalls();

                           for(i = 0; i < backwardsCallCounts.length; ++i) {
                              fieldAttrCalls.add(backwardsCallCounts[i]);
                           }
                        }
                     }

                     bands = (NewAttributeBands)iterator.next();
                  } while(!bands.isUsedAtLeastOnce());

                  backwardsCallCounts = bands.numBackwardsCalls();

                  for(i = 0; i < backwardsCallCounts.length; ++i) {
                     methodAttrCalls.add(backwardsCallCounts[i]);
                  }
               }
            }

            bands = (NewAttributeBands)iterator.next();
         } while(!bands.isUsedAtLeastOnce());

         backwardsCallCounts = bands.numBackwardsCalls();

         for(i = 0; i < backwardsCallCounts.length; ++i) {
            classAttrCalls.add(backwardsCallCounts[i]);
         }
      }
   }

   public void pack(OutputStream out) throws IOException, Pack200Exception {
      PackingUtils.log("Writing class bands...");
      byte[] encodedBand = this.encodeBandInt("class_this", this.getInts(this.class_this), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_this[" + this.class_this.length + "]");
      encodedBand = this.encodeBandInt("class_super", this.getInts(this.class_super), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_super[" + this.class_super.length + "]");
      encodedBand = this.encodeBandInt("class_interface_count", this.class_interface_count, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface_count[" + this.class_interface_count.length + "]");
      int totalInterfaces = this.sum(this.class_interface_count);
      int[] classInterface = new int[totalInterfaces];
      int k = 0;

      int i;
      for(i = 0; i < this.class_interface.length; ++i) {
         if (this.class_interface[i] != null) {
            for(int j = 0; j < this.class_interface[i].length; ++j) {
               CPClass cpClass = this.class_interface[i][j];
               classInterface[k] = cpClass.getIndex();
               ++k;
            }
         }
      }

      encodedBand = this.encodeBandInt("class_interface", classInterface, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface[" + classInterface.length + "]");
      encodedBand = this.encodeBandInt("class_field_count", this.class_field_count, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_field_count[" + this.class_field_count.length + "]");
      encodedBand = this.encodeBandInt("class_method_count", this.class_method_count, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_method_count[" + this.class_method_count.length + "]");
      i = this.sum(this.class_field_count);
      int[] fieldDescr = new int[i];
      k = 0;

      int i;
      for(i = 0; i < this.index; ++i) {
         for(int j = 0; j < this.field_descr[i].length; ++j) {
            CPNameAndType descr = this.field_descr[i][j];
            fieldDescr[k] = descr.getIndex();
            ++k;
         }
      }

      encodedBand = this.encodeBandInt("field_descr", fieldDescr, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_descr[" + fieldDescr.length + "]");
      this.writeFieldAttributeBands(out);
      i = this.sum(this.class_method_count);
      int[] methodDescr = new int[i];
      k = 0;

      for(int i = 0; i < this.index; ++i) {
         for(int j = 0; j < this.method_descr[i].length; ++j) {
            CPNameAndType descr = this.method_descr[i][j];
            methodDescr[k] = descr.getIndex();
            ++k;
         }
      }

      encodedBand = this.encodeBandInt("method_descr", methodDescr, Codec.MDELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_descr[" + methodDescr.length + "]");
      this.writeMethodAttributeBands(out);
      this.writeClassAttributeBands(out);
      this.writeCodeBands(out);
   }

   private int sum(int[] ints) {
      int sum = 0;

      for(int i = 0; i < ints.length; ++i) {
         sum += ints[i];
      }

      return sum;
   }

   private void writeFieldAttributeBands(OutputStream out) throws IOException, Pack200Exception {
      byte[] encodedBand = this.encodeFlags("field_flags", this.field_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_field_flags_hi());
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_flags[" + this.field_flags.length + "]");
      encodedBand = this.encodeBandInt("field_attr_calls", this.field_attr_calls, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_attr_calls[" + this.field_attr_calls.length + "]");
      encodedBand = this.encodeBandInt("fieldConstantValueKQ", this.cpEntryListToArray(this.fieldConstantValueKQ), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldConstantValueKQ[" + this.fieldConstantValueKQ.size() + "]");
      encodedBand = this.encodeBandInt("fieldSignature", this.cpEntryListToArray(this.fieldSignature), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldSignature[" + this.fieldSignature.size() + "]");
      this.field_RVA_bands.pack(out);
      this.field_RIA_bands.pack(out);
      Iterator iterator = this.fieldAttributeBands.iterator();

      while(iterator.hasNext()) {
         NewAttributeBands bands = (NewAttributeBands)iterator.next();
         bands.pack(out);
      }

   }

   private void writeMethodAttributeBands(OutputStream out) throws IOException, Pack200Exception {
      byte[] encodedBand = this.encodeFlags("method_flags", this.method_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_method_flags_hi());
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_flags[" + this.method_flags.length + "]");
      encodedBand = this.encodeBandInt("method_attr_calls", this.method_attr_calls, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_attr_calls[" + this.method_attr_calls.length + "]");
      encodedBand = this.encodeBandInt("methodExceptionNumber", this.methodExceptionNumber.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionNumber[" + this.methodExceptionNumber.size() + "]");
      encodedBand = this.encodeBandInt("methodExceptionClasses", this.cpEntryListToArray(this.methodExceptionClasses), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionClasses[" + this.methodExceptionClasses.size() + "]");
      encodedBand = this.encodeBandInt("methodSignature", this.cpEntryListToArray(this.methodSignature), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodSignature[" + this.methodSignature.size() + "]");
      this.method_RVA_bands.pack(out);
      this.method_RIA_bands.pack(out);
      this.method_RVPA_bands.pack(out);
      this.method_RIPA_bands.pack(out);
      this.method_AD_bands.pack(out);
      Iterator iterator = this.methodAttributeBands.iterator();

      while(iterator.hasNext()) {
         NewAttributeBands bands = (NewAttributeBands)iterator.next();
         bands.pack(out);
      }

   }

   private void writeClassAttributeBands(OutputStream out) throws IOException, Pack200Exception {
      byte[] encodedBand = this.encodeFlags("class_flags", this.class_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_class_flags_hi());
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_flags[" + this.class_flags.length + "]");
      encodedBand = this.encodeBandInt("class_attr_calls", this.class_attr_calls, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_attr_calls[" + this.class_attr_calls.length + "]");
      encodedBand = this.encodeBandInt("classSourceFile", this.cpEntryOrNullListToArray(this.classSourceFile), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from classSourceFile[" + this.classSourceFile.size() + "]");
      encodedBand = this.encodeBandInt("class_enclosing_method_RC", this.cpEntryListToArray(this.classEnclosingMethodClass), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_enclosing_method_RC[" + this.classEnclosingMethodClass.size() + "]");
      encodedBand = this.encodeBandInt("class_EnclosingMethod_RDN", this.cpEntryOrNullListToArray(this.classEnclosingMethodDesc), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_EnclosingMethod_RDN[" + this.classEnclosingMethodDesc.size() + "]");
      encodedBand = this.encodeBandInt("class_Signature_RS", this.cpEntryListToArray(this.classSignature), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_Signature_RS[" + this.classSignature.size() + "]");
      this.class_RVA_bands.pack(out);
      this.class_RIA_bands.pack(out);
      encodedBand = this.encodeBandInt("class_InnerClasses_N", this.class_InnerClasses_N, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_N[" + this.class_InnerClasses_N.length + "]");
      encodedBand = this.encodeBandInt("class_InnerClasses_RC", this.getInts(this.class_InnerClasses_RC), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_RC[" + this.class_InnerClasses_RC.length + "]");
      encodedBand = this.encodeBandInt("class_InnerClasses_F", this.class_InnerClasses_F, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_F[" + this.class_InnerClasses_F.length + "]");
      encodedBand = this.encodeBandInt("class_InnerClasses_outer_RCN", this.cpEntryOrNullListToArray(this.classInnerClassesOuterRCN), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_outer_RCN[" + this.classInnerClassesOuterRCN.size() + "]");
      encodedBand = this.encodeBandInt("class_InnerClasses_name_RUN", this.cpEntryOrNullListToArray(this.classInnerClassesNameRUN), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_name_RUN[" + this.classInnerClassesNameRUN.size() + "]");
      encodedBand = this.encodeBandInt("classFileVersionMinor", this.classFileVersionMinor.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMinor[" + this.classFileVersionMinor.size() + "]");
      encodedBand = this.encodeBandInt("classFileVersionMajor", this.classFileVersionMajor.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMajor[" + this.classFileVersionMajor.size() + "]");
      Iterator iterator = this.classAttributeBands.iterator();

      while(iterator.hasNext()) {
         NewAttributeBands bands = (NewAttributeBands)iterator.next();
         bands.pack(out);
      }

   }

   private int[] getInts(CPClass[] cpClasses) {
      int[] ints = new int[cpClasses.length];

      for(int i = 0; i < ints.length; ++i) {
         if (cpClasses[i] != null) {
            ints[i] = cpClasses[i].getIndex();
         }
      }

      return ints;
   }

   private void writeCodeBands(OutputStream out) throws IOException, Pack200Exception {
      byte[] encodedBand = this.encodeBandInt("codeHeaders", this.codeHeaders, Codec.BYTE1);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHeaders[" + this.codeHeaders.length + "]");
      encodedBand = this.encodeBandInt("codeMaxStack", this.codeMaxStack.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxStack[" + this.codeMaxStack.size() + "]");
      encodedBand = this.encodeBandInt("codeMaxLocals", this.codeMaxLocals.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxLocals[" + this.codeMaxLocals.size() + "]");
      encodedBand = this.encodeBandInt("codeHandlerCount", this.codeHandlerCount.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCount[" + this.codeHandlerCount.size() + "]");
      encodedBand = this.encodeBandInt("codeHandlerStartP", this.integerListToArray(this.codeHandlerStartP), Codec.BCI5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerStartP[" + this.codeHandlerStartP.size() + "]");
      encodedBand = this.encodeBandInt("codeHandlerEndPO", this.integerListToArray(this.codeHandlerEndPO), Codec.BRANCH5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerEndPO[" + this.codeHandlerEndPO.size() + "]");
      encodedBand = this.encodeBandInt("codeHandlerCatchPO", this.integerListToArray(this.codeHandlerCatchPO), Codec.BRANCH5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCatchPO[" + this.codeHandlerCatchPO.size() + "]");
      encodedBand = this.encodeBandInt("codeHandlerClass", this.cpEntryOrNullListToArray(this.codeHandlerClass), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerClass[" + this.codeHandlerClass.size() + "]");
      this.writeCodeAttributeBands(out);
   }

   private void writeCodeAttributeBands(OutputStream out) throws IOException, Pack200Exception {
      byte[] encodedBand = this.encodeFlags("codeFlags", this.longListToArray(this.codeFlags), Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_code_flags_hi());
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeFlags[" + this.codeFlags.size() + "]");
      encodedBand = this.encodeBandInt("code_attr_calls", this.code_attr_calls, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_attr_calls[" + this.code_attr_calls.length + "]");
      encodedBand = this.encodeBandInt("code_LineNumberTable_N", this.codeLineNumberTableN.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_N[" + this.codeLineNumberTableN.size() + "]");
      encodedBand = this.encodeBandInt("code_LineNumberTable_bci_P", this.integerListToArray(this.codeLineNumberTableBciP), Codec.BCI5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_bci_P[" + this.codeLineNumberTableBciP.size() + "]");
      encodedBand = this.encodeBandInt("code_LineNumberTable_line", this.codeLineNumberTableLine.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_line[" + this.codeLineNumberTableLine.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_N", this.codeLocalVariableTableN.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_N[" + this.codeLocalVariableTableN.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_bci_P", this.integerListToArray(this.codeLocalVariableTableBciP), Codec.BCI5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_bci_P[" + this.codeLocalVariableTableBciP.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_span_O", this.integerListToArray(this.codeLocalVariableTableSpanO), Codec.BRANCH5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_span_O[" + this.codeLocalVariableTableSpanO.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_name_RU", this.cpEntryListToArray(this.codeLocalVariableTableNameRU), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_name_RU[" + this.codeLocalVariableTableNameRU.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_type_RS", this.cpEntryListToArray(this.codeLocalVariableTableTypeRS), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_type_RS[" + this.codeLocalVariableTableTypeRS.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTable_slot", this.codeLocalVariableTableSlot.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_slot[" + this.codeLocalVariableTableSlot.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_N", this.codeLocalVariableTypeTableN.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_N[" + this.codeLocalVariableTypeTableN.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_bci_P", this.integerListToArray(this.codeLocalVariableTypeTableBciP), Codec.BCI5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_bci_P[" + this.codeLocalVariableTypeTableBciP.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_span_O", this.integerListToArray(this.codeLocalVariableTypeTableSpanO), Codec.BRANCH5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_span_O[" + this.codeLocalVariableTypeTableSpanO.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_name_RU", this.cpEntryListToArray(this.codeLocalVariableTypeTableNameRU), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_name_RU[" + this.codeLocalVariableTypeTableNameRU.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_type_RS", this.cpEntryListToArray(this.codeLocalVariableTypeTableTypeRS), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_type_RS[" + this.codeLocalVariableTypeTableTypeRS.size() + "]");
      encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_slot", this.codeLocalVariableTypeTableSlot.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_slot[" + this.codeLocalVariableTypeTableSlot.size() + "]");
      Iterator iterator = this.codeAttributeBands.iterator();

      while(iterator.hasNext()) {
         NewAttributeBands bands = (NewAttributeBands)iterator.next();
         bands.pack(out);
      }

   }

   public void addMethod(int flags, String name, String desc, String signature, String[] exceptions) {
      CPNameAndType nt = this.cpBands.getCPNameAndType(name, desc);
      this.tempMethodDesc.add(nt);
      if (signature != null) {
         this.methodSignature.add(this.cpBands.getCPSignature(signature));
         flags |= 524288;
      }

      if (exceptions != null) {
         this.methodExceptionNumber.add(exceptions.length);

         for(int i = 0; i < exceptions.length; ++i) {
            this.methodExceptionClasses.add(this.cpBands.getCPClass(exceptions[i]));
         }

         flags |= 262144;
      }

      if ((flags & 131072) != 0) {
         flags &= -131073;
         flags |= 1048576;
      }

      this.tempMethodFlags.add((long)flags);
      this.numMethodArgs = countArgs(desc);
      if (!this.anySyntheticMethods && (flags & 4096) != 0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
         this.cpBands.addCPUtf8("Synthetic");
         this.anySyntheticMethods = true;
      }

   }

   public void endOfMethod() {
      if (this.tempMethodRVPA != null) {
         this.method_RVPA_bands.addParameterAnnotation(this.tempMethodRVPA.numParams, this.tempMethodRVPA.annoN, this.tempMethodRVPA.pairN, this.tempMethodRVPA.typeRS, this.tempMethodRVPA.nameRU, this.tempMethodRVPA.t, this.tempMethodRVPA.values, this.tempMethodRVPA.caseArrayN, this.tempMethodRVPA.nestTypeRS, this.tempMethodRVPA.nestNameRU, this.tempMethodRVPA.nestPairN);
         this.tempMethodRVPA = null;
      }

      if (this.tempMethodRIPA != null) {
         this.method_RIPA_bands.addParameterAnnotation(this.tempMethodRIPA.numParams, this.tempMethodRIPA.annoN, this.tempMethodRIPA.pairN, this.tempMethodRIPA.typeRS, this.tempMethodRIPA.nameRU, this.tempMethodRIPA.t, this.tempMethodRIPA.values, this.tempMethodRIPA.caseArrayN, this.tempMethodRIPA.nestTypeRS, this.tempMethodRIPA.nestNameRU, this.tempMethodRIPA.nestPairN);
         this.tempMethodRIPA = null;
      }

      if (this.codeFlags.size() > 0) {
         long latestCodeFlag = (Long)this.codeFlags.get(this.codeFlags.size() - 1);
         int latestLocalVariableTableN = this.codeLocalVariableTableN.get(this.codeLocalVariableTableN.size() - 1);
         if (latestCodeFlag == 4L && latestLocalVariableTableN == 0) {
            this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1);
            this.codeFlags.remove(this.codeFlags.size() - 1);
            this.codeFlags.add(0);
         }
      }

   }

   protected static int countArgs(String descriptor) {
      int bra = descriptor.indexOf(40);
      int ket = descriptor.indexOf(41);
      if (bra != -1 && ket != -1 && ket >= bra) {
         boolean inType = false;
         boolean consumingNextType = false;
         int count = 0;

         for(int i = bra + 1; i < ket; ++i) {
            char charAt = descriptor.charAt(i);
            if (inType && charAt == ';') {
               inType = false;
               consumingNextType = false;
            } else if (!inType && charAt == 'L') {
               inType = true;
               ++count;
            } else if (charAt == '[') {
               consumingNextType = true;
            } else if (!inType) {
               if (consumingNextType) {
                  ++count;
                  consumingNextType = false;
               } else if (charAt != 'D' && charAt != 'J') {
                  ++count;
               } else {
                  count += 2;
               }
            }
         }

         return count;
      } else {
         throw new IllegalArgumentException("No arguments");
      }
   }

   public void endOfClass() {
      int numFields = this.tempFieldDesc.size();
      this.class_field_count[this.index] = numFields;
      this.field_descr[this.index] = new CPNameAndType[numFields];
      this.field_flags[this.index] = new long[numFields];

      int numMethods;
      for(numMethods = 0; numMethods < numFields; ++numMethods) {
         this.field_descr[this.index][numMethods] = (CPNameAndType)this.tempFieldDesc.get(numMethods);
         this.field_flags[this.index][numMethods] = (Long)this.tempFieldFlags.get(numMethods);
      }

      numMethods = this.tempMethodDesc.size();
      this.class_method_count[this.index] = numMethods;
      this.method_descr[this.index] = new CPNameAndType[numMethods];
      this.method_flags[this.index] = new long[numMethods];

      for(int i = 0; i < numMethods; ++i) {
         this.method_descr[this.index][i] = (CPNameAndType)this.tempMethodDesc.get(i);
         this.method_flags[this.index][i] = (Long)this.tempMethodFlags.get(i);
      }

      this.tempFieldDesc.clear();
      this.tempFieldFlags.clear();
      this.tempMethodDesc.clear();
      this.tempMethodFlags.clear();
      ++this.index;
   }

   public void addSourceFile(String source) {
      String implicitSourceFileName = this.class_this[this.index].toString();
      if (implicitSourceFileName.indexOf(36) != -1) {
         implicitSourceFileName = implicitSourceFileName.substring(0, implicitSourceFileName.indexOf(36));
      }

      implicitSourceFileName = implicitSourceFileName.substring(implicitSourceFileName.lastIndexOf(47) + 1) + ".java";
      if (source.equals(implicitSourceFileName)) {
         this.classSourceFile.add((Object)null);
      } else {
         this.classSourceFile.add(this.cpBands.getCPUtf8(source));
      }

      long[] var10000 = this.class_flags;
      int var10001 = this.index;
      var10000[var10001] |= 131072L;
   }

   public void addEnclosingMethod(String owner, String name, String desc) {
      long[] var10000 = this.class_flags;
      int var10001 = this.index;
      var10000[var10001] |= 262144L;
      this.classEnclosingMethodClass.add(this.cpBands.getCPClass(owner));
      this.classEnclosingMethodDesc.add(name == null ? null : this.cpBands.getCPNameAndType(name, desc));
   }

   public void addClassAttribute(NewAttribute attribute) {
      String attributeName = attribute.type;
      Iterator iterator = this.classAttributeBands.iterator();

      NewAttributeBands bands;
      do {
         if (!iterator.hasNext()) {
            throw new RuntimeException("No suitable definition for " + attributeName);
         }

         bands = (NewAttributeBands)iterator.next();
      } while(!bands.getAttributeName().equals(attributeName));

      bands.addAttribute(attribute);
      int flagIndex = bands.getFlagIndex();
      long[] var10000 = this.class_flags;
      int var10001 = this.index;
      var10000[var10001] |= (long)(1 << flagIndex);
   }

   public void addFieldAttribute(NewAttribute attribute) {
      String attributeName = attribute.type;
      Iterator iterator = this.fieldAttributeBands.iterator();

      NewAttributeBands bands;
      do {
         if (!iterator.hasNext()) {
            throw new RuntimeException("No suitable definition for " + attributeName);
         }

         bands = (NewAttributeBands)iterator.next();
      } while(!bands.getAttributeName().equals(attributeName));

      bands.addAttribute(attribute);
      int flagIndex = bands.getFlagIndex();
      Long flags = (Long)this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
      this.tempFieldFlags.add(flags | (long)(1 << flagIndex));
   }

   public void addMethodAttribute(NewAttribute attribute) {
      String attributeName = attribute.type;
      Iterator iterator = this.methodAttributeBands.iterator();

      NewAttributeBands bands;
      do {
         if (!iterator.hasNext()) {
            throw new RuntimeException("No suitable definition for " + attributeName);
         }

         bands = (NewAttributeBands)iterator.next();
      } while(!bands.getAttributeName().equals(attributeName));

      bands.addAttribute(attribute);
      int flagIndex = bands.getFlagIndex();
      Long flags = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
      this.tempMethodFlags.add(flags | (long)(1 << flagIndex));
   }

   public void addCodeAttribute(NewAttribute attribute) {
      String attributeName = attribute.type;
      Iterator iterator = this.codeAttributeBands.iterator();

      NewAttributeBands bands;
      do {
         if (!iterator.hasNext()) {
            throw new RuntimeException("No suitable definition for " + attributeName);
         }

         bands = (NewAttributeBands)iterator.next();
      } while(!bands.getAttributeName().equals(attributeName));

      bands.addAttribute(attribute);
      int flagIndex = bands.getFlagIndex();
      Long flags = (Long)this.codeFlags.remove(this.codeFlags.size() - 1);
      this.codeFlags.add(flags | (long)(1 << flagIndex));
   }

   public void addMaxStack(int maxStack, int maxLocals) {
      Long latestFlag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
      Long newFlag = (long)(latestFlag.intValue() | 131072);
      this.tempMethodFlags.add(newFlag);
      this.codeMaxStack.add(maxStack);
      if ((newFlag & 8L) == 0L) {
         --maxLocals;
      }

      maxLocals -= this.numMethodArgs;
      this.codeMaxLocals.add(maxLocals);
   }

   public void addCode() {
      this.codeHandlerCount.add(0);
      if (!this.stripDebug) {
         this.codeFlags.add(4L);
         this.codeLocalVariableTableN.add(0);
      }

   }

   public void addHandler(Label start, Label end, Label handler, String type) {
      int handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1);
      this.codeHandlerCount.add(handlers + 1);
      this.codeHandlerStartP.add(start);
      this.codeHandlerEndPO.add(end);
      this.codeHandlerCatchPO.add(handler);
      this.codeHandlerClass.add(type == null ? null : this.cpBands.getCPClass(type));
   }

   public void addLineNumber(int line, Label start) {
      Long latestCodeFlag = (Long)this.codeFlags.get(this.codeFlags.size() - 1);
      if ((latestCodeFlag.intValue() & 2) == 0) {
         this.codeFlags.remove(this.codeFlags.size() - 1);
         this.codeFlags.add((long)(latestCodeFlag.intValue() | 2));
         this.codeLineNumberTableN.add(1);
      } else {
         this.codeLineNumberTableN.increment(this.codeLineNumberTableN.size() - 1);
      }

      this.codeLineNumberTableLine.add(line);
      this.codeLineNumberTableBciP.add(start);
   }

   public void addLocalVariable(String name, String desc, String signature, Label start, Label end, int indx) {
      if (signature != null) {
         Long latestCodeFlag = (Long)this.codeFlags.get(this.codeFlags.size() - 1);
         if ((latestCodeFlag.intValue() & 8) == 0) {
            this.codeFlags.remove(this.codeFlags.size() - 1);
            this.codeFlags.add((long)(latestCodeFlag.intValue() | 8));
            this.codeLocalVariableTypeTableN.add(1);
         } else {
            this.codeLocalVariableTypeTableN.increment(this.codeLocalVariableTypeTableN.size() - 1);
         }

         this.codeLocalVariableTypeTableBciP.add(start);
         this.codeLocalVariableTypeTableSpanO.add(end);
         this.codeLocalVariableTypeTableNameRU.add(this.cpBands.getCPUtf8(name));
         this.codeLocalVariableTypeTableTypeRS.add(this.cpBands.getCPSignature(signature));
         this.codeLocalVariableTypeTableSlot.add(indx);
      }

      this.codeLocalVariableTableN.increment(this.codeLocalVariableTableN.size() - 1);
      this.codeLocalVariableTableBciP.add(start);
      this.codeLocalVariableTableSpanO.add(end);
      this.codeLocalVariableTableNameRU.add(this.cpBands.getCPUtf8(name));
      this.codeLocalVariableTableTypeRS.add(this.cpBands.getCPSignature(desc));
      this.codeLocalVariableTableSlot.add(indx);
   }

   public void doBciRenumbering(IntList bciRenumbering, Map labelsToOffsets) {
      this.renumberBci(this.codeLineNumberTableBciP, bciRenumbering, labelsToOffsets);
      this.renumberBci(this.codeLocalVariableTableBciP, bciRenumbering, labelsToOffsets);
      this.renumberOffsetBci(this.codeLocalVariableTableBciP, this.codeLocalVariableTableSpanO, bciRenumbering, labelsToOffsets);
      this.renumberBci(this.codeLocalVariableTypeTableBciP, bciRenumbering, labelsToOffsets);
      this.renumberOffsetBci(this.codeLocalVariableTypeTableBciP, this.codeLocalVariableTypeTableSpanO, bciRenumbering, labelsToOffsets);
      this.renumberBci(this.codeHandlerStartP, bciRenumbering, labelsToOffsets);
      this.renumberOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, bciRenumbering, labelsToOffsets);
      this.renumberDoubleOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, this.codeHandlerCatchPO, bciRenumbering, labelsToOffsets);
      Iterator iterator = this.classAttributeBands.iterator();

      NewAttributeBands newAttributeBandSet;
      while(iterator.hasNext()) {
         newAttributeBandSet = (NewAttributeBands)iterator.next();
         newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
      }

      iterator = this.methodAttributeBands.iterator();

      while(iterator.hasNext()) {
         newAttributeBandSet = (NewAttributeBands)iterator.next();
         newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
      }

      iterator = this.fieldAttributeBands.iterator();

      while(iterator.hasNext()) {
         newAttributeBandSet = (NewAttributeBands)iterator.next();
         newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
      }

      iterator = this.codeAttributeBands.iterator();

      while(iterator.hasNext()) {
         newAttributeBandSet = (NewAttributeBands)iterator.next();
         newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
      }

   }

   private void renumberBci(List list, IntList bciRenumbering, Map labelsToOffsets) {
      for(int i = list.size() - 1; i >= 0; --i) {
         Object label = list.get(i);
         if (label instanceof Integer) {
            break;
         }

         if (label instanceof Label) {
            list.remove(i);
            Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
            list.add(i, bciRenumbering.get(bytecodeIndex));
         }
      }

   }

   private void renumberOffsetBci(List relative, List list, IntList bciRenumbering, Map labelsToOffsets) {
      for(int i = list.size() - 1; i >= 0; --i) {
         Object label = list.get(i);
         if (label instanceof Integer) {
            break;
         }

         if (label instanceof Label) {
            list.remove(i);
            Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
            Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - (Integer)relative.get(i);
            list.add(i, renumberedOffset);
         }
      }

   }

   private void renumberDoubleOffsetBci(List relative, List firstOffset, List list, IntList bciRenumbering, Map labelsToOffsets) {
      for(int i = list.size() - 1; i >= 0; --i) {
         Object label = list.get(i);
         if (label instanceof Integer) {
            break;
         }

         if (label instanceof Label) {
            list.remove(i);
            Integer bytecodeIndex = (Integer)labelsToOffsets.get(label);
            Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - (Integer)relative.get(i) - (Integer)firstOffset.get(i);
            list.add(i, renumberedOffset);
         }
      }

   }

   public boolean isAnySyntheticClasses() {
      return this.anySyntheticClasses;
   }

   public boolean isAnySyntheticFields() {
      return this.anySyntheticFields;
   }

   public boolean isAnySyntheticMethods() {
      return this.anySyntheticMethods;
   }

   public void addParameterAnnotation(int parameter, String desc, boolean visible, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
      Long flag;
      if (visible) {
         if (this.tempMethodRVPA == null) {
            this.tempMethodRVPA = new TempParamAnnotation(this.numMethodArgs);
            this.tempMethodRVPA.addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
         }

         flag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
         this.tempMethodFlags.add(flag | 8388608L);
      } else {
         if (this.tempMethodRIPA == null) {
            this.tempMethodRIPA = new TempParamAnnotation(this.numMethodArgs);
            this.tempMethodRIPA.addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
         }

         flag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
         this.tempMethodFlags.add(flag | 16777216L);
      }

   }

   public void addAnnotation(int context, String desc, boolean visible, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
      Long flag;
      switch (context) {
         case 0:
            if (visible) {
               this.class_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               if ((this.class_flags[this.index] & 2097152L) != 0L) {
                  this.class_RVA_bands.incrementAnnoN();
               } else {
                  this.class_RVA_bands.newEntryInAnnoN();
                  this.class_flags[this.index] |= 2097152L;
               }
            } else {
               this.class_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               if ((this.class_flags[this.index] & 4194304L) != 0L) {
                  this.class_RIA_bands.incrementAnnoN();
               } else {
                  this.class_RIA_bands.newEntryInAnnoN();
                  this.class_flags[this.index] |= 4194304L;
               }
            }
            break;
         case 1:
            if (visible) {
               this.field_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               flag = (Long)this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
               if ((flag.intValue() & 2097152) != 0) {
                  this.field_RVA_bands.incrementAnnoN();
               } else {
                  this.field_RVA_bands.newEntryInAnnoN();
               }

               this.tempFieldFlags.add((long)(flag.intValue() | 2097152));
            } else {
               this.field_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               flag = (Long)this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
               if ((flag.intValue() & 4194304) != 0) {
                  this.field_RIA_bands.incrementAnnoN();
               } else {
                  this.field_RIA_bands.newEntryInAnnoN();
               }

               this.tempFieldFlags.add((long)(flag.intValue() | 4194304));
            }
            break;
         case 2:
            if (visible) {
               this.method_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               flag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
               if ((flag.intValue() & 2097152) != 0) {
                  this.method_RVA_bands.incrementAnnoN();
               } else {
                  this.method_RVA_bands.newEntryInAnnoN();
               }

               this.tempMethodFlags.add((long)(flag.intValue() | 2097152));
            } else {
               this.method_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
               flag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
               if ((flag.intValue() & 4194304) != 0) {
                  this.method_RIA_bands.incrementAnnoN();
               } else {
                  this.method_RIA_bands.newEntryInAnnoN();
               }

               this.tempMethodFlags.add((long)(flag.intValue() | 4194304));
            }
      }

   }

   public void addAnnotationDefault(List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
      this.method_AD_bands.addAnnotation((String)null, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
      Long flag = (Long)this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
      this.tempMethodFlags.add(flag | 33554432L);
   }

   public void removeCurrentClass() {
      if ((this.class_flags[this.index] & 131072L) != 0L) {
         this.classSourceFile.remove(this.classSourceFile.size() - 1);
      }

      if ((this.class_flags[this.index] & 262144L) != 0L) {
         this.classEnclosingMethodClass.remove(this.classEnclosingMethodClass.size() - 1);
         this.classEnclosingMethodDesc.remove(this.classEnclosingMethodDesc.size() - 1);
      }

      if ((this.class_flags[this.index] & 524288L) != 0L) {
         this.classSignature.remove(this.classSignature.size() - 1);
      }

      if ((this.class_flags[this.index] & 2097152L) != 0L) {
         this.class_RVA_bands.removeLatest();
      }

      if ((this.class_flags[this.index] & 4194304L) != 0L) {
         this.class_RIA_bands.removeLatest();
      }

      Iterator iterator = this.tempFieldFlags.iterator();

      Long flagsL;
      long flags;
      while(iterator.hasNext()) {
         flagsL = (Long)iterator.next();
         flags = flagsL;
         if ((flags & 524288L) != 0L) {
            this.fieldSignature.remove(this.fieldSignature.size() - 1);
         }

         if ((flags & 131072L) != 0L) {
            this.fieldConstantValueKQ.remove(this.fieldConstantValueKQ.size() - 1);
         }

         if ((flags & 2097152L) != 0L) {
            this.field_RVA_bands.removeLatest();
         }

         if ((flags & 4194304L) != 0L) {
            this.field_RIA_bands.removeLatest();
         }
      }

      iterator = this.tempMethodFlags.iterator();

      while(iterator.hasNext()) {
         flagsL = (Long)iterator.next();
         flags = flagsL;
         if ((flags & 524288L) != 0L) {
            this.methodSignature.remove(this.methodSignature.size() - 1);
         }

         int handlers;
         int i;
         if ((flags & 262144L) != 0L) {
            handlers = this.methodExceptionNumber.remove(this.methodExceptionNumber.size() - 1);

            for(i = 0; i < handlers; ++i) {
               this.methodExceptionClasses.remove(this.methodExceptionClasses.size() - 1);
            }
         }

         if ((flags & 131072L) != 0L) {
            this.codeMaxLocals.remove(this.codeMaxLocals.size() - 1);
            this.codeMaxStack.remove(this.codeMaxStack.size() - 1);
            handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1);

            for(i = 0; i < handlers; ++i) {
               int index = this.codeHandlerStartP.size() - 1;
               this.codeHandlerStartP.remove(index);
               this.codeHandlerEndPO.remove(index);
               this.codeHandlerCatchPO.remove(index);
               this.codeHandlerClass.remove(index);
            }

            if (!this.stripDebug) {
               long cdeFlags = (Long)this.codeFlags.remove(this.codeFlags.size() - 1);
               int numLocalVariables = this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1);

               int numLineNumbers;
               int i;
               for(numLineNumbers = 0; numLineNumbers < numLocalVariables; ++numLineNumbers) {
                  i = this.codeLocalVariableTableBciP.size() - 1;
                  this.codeLocalVariableTableBciP.remove(i);
                  this.codeLocalVariableTableSpanO.remove(i);
                  this.codeLocalVariableTableNameRU.remove(i);
                  this.codeLocalVariableTableTypeRS.remove(i);
                  this.codeLocalVariableTableSlot.remove(i);
               }

               int location;
               if ((cdeFlags & 8L) != 0L) {
                  numLineNumbers = this.codeLocalVariableTypeTableN.remove(this.codeLocalVariableTypeTableN.size() - 1);

                  for(i = 0; i < numLineNumbers; ++i) {
                     location = this.codeLocalVariableTypeTableBciP.size() - 1;
                     this.codeLocalVariableTypeTableBciP.remove(location);
                     this.codeLocalVariableTypeTableSpanO.remove(location);
                     this.codeLocalVariableTypeTableNameRU.remove(location);
                     this.codeLocalVariableTypeTableTypeRS.remove(location);
                     this.codeLocalVariableTypeTableSlot.remove(location);
                  }
               }

               if ((cdeFlags & 2L) != 0L) {
                  numLineNumbers = this.codeLineNumberTableN.remove(this.codeLineNumberTableN.size() - 1);

                  for(i = 0; i < numLineNumbers; ++i) {
                     location = this.codeLineNumberTableBciP.size() - 1;
                     this.codeLineNumberTableBciP.remove(location);
                     this.codeLineNumberTableLine.remove(location);
                  }
               }
            }
         }

         if ((flags & 2097152L) != 0L) {
            this.method_RVA_bands.removeLatest();
         }

         if ((flags & 4194304L) != 0L) {
            this.method_RIA_bands.removeLatest();
         }

         if ((flags & 8388608L) != 0L) {
            this.method_RVPA_bands.removeLatest();
         }

         if ((flags & 16777216L) != 0L) {
            this.method_RIPA_bands.removeLatest();
         }

         if ((flags & 33554432L) != 0L) {
            this.method_AD_bands.removeLatest();
         }
      }

      this.class_this[this.index] = null;
      this.class_super[this.index] = null;
      this.class_interface_count[this.index] = 0;
      this.class_interface[this.index] = null;
      this.major_versions[this.index] = 0;
      this.class_flags[this.index] = 0L;
      this.tempFieldDesc.clear();
      this.tempFieldFlags.clear();
      this.tempMethodDesc.clear();
      this.tempMethodFlags.clear();
      if (this.index > 0) {
         --this.index;
      }

   }

   public int numClassesProcessed() {
      return this.index;
   }

   private static class TempParamAnnotation {
      int numParams;
      int[] annoN;
      IntList pairN = new IntList();
      List typeRS = new ArrayList();
      List nameRU = new ArrayList();
      List t = new ArrayList();
      List values = new ArrayList();
      List caseArrayN = new ArrayList();
      List nestTypeRS = new ArrayList();
      List nestNameRU = new ArrayList();
      List nestPairN = new ArrayList();

      public TempParamAnnotation(int numParams) {
         this.numParams = numParams;
         this.annoN = new int[numParams];
      }

      public void addParameterAnnotation(int parameter, String desc, List nameRU, List t, List values, List caseArrayN, List nestTypeRS, List nestNameRU, List nestPairN) {
         int var10002 = this.annoN[parameter]++;
         this.typeRS.add(desc);
         this.pairN.add(nameRU.size());
         this.nameRU.addAll(nameRU);
         this.t.addAll(t);
         this.values.addAll(values);
         this.caseArrayN.addAll(caseArrayN);
         this.nestTypeRS.addAll(nestTypeRS);
         this.nestNameRU.addAll(nestNameRU);
         this.nestPairN.addAll(nestPairN);
      }
   }
}
