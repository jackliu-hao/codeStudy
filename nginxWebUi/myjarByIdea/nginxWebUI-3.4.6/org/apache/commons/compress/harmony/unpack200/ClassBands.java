package org.apache.commons.compress.harmony.unpack200;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantValueAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.DeprecatedAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.EnclosingMethodAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionsAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LineNumberTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTypeTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.SignatureAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;

public class ClassBands extends BandSet {
   private int[] classFieldCount;
   private long[] classFlags;
   private long[] classAccessFlags;
   private int[][] classInterfacesInts;
   private int[] classMethodCount;
   private int[] classSuperInts;
   private String[] classThis;
   private int[] classThisInts;
   private ArrayList[] classAttributes;
   private int[] classVersionMajor;
   private int[] classVersionMinor;
   private IcTuple[][] icLocal;
   private List[] codeAttributes;
   private int[] codeHandlerCount;
   private int[] codeMaxNALocals;
   private int[] codeMaxStack;
   private ArrayList[][] fieldAttributes;
   private String[][] fieldDescr;
   private int[][] fieldDescrInts;
   private long[][] fieldFlags;
   private long[][] fieldAccessFlags;
   private ArrayList[][] methodAttributes;
   private String[][] methodDescr;
   private int[][] methodDescrInts;
   private long[][] methodFlags;
   private long[][] methodAccessFlags;
   private final AttributeLayoutMap attrMap;
   private final CpBands cpBands;
   private final SegmentOptions options;
   private final int classCount;
   private int[] methodAttrCalls;
   private int[][] codeHandlerStartP;
   private int[][] codeHandlerEndPO;
   private int[][] codeHandlerCatchPO;
   private int[][] codeHandlerClassRCN;
   private boolean[] codeHasAttributes;

   public ClassBands(Segment segment) {
      super(segment);
      this.attrMap = segment.getAttrDefinitionBands().getAttributeDefinitionMap();
      this.cpBands = segment.getCpBands();
      this.classCount = this.header.getClassCount();
      this.options = this.header.getOptions();
   }

   public void read(InputStream in) throws IOException, Pack200Exception {
      int classCount = this.header.getClassCount();
      this.classThisInts = this.decodeBandInt("class_this", in, Codec.DELTA5, classCount);
      this.classThis = this.getReferences(this.classThisInts, this.cpBands.getCpClass());
      this.classSuperInts = this.decodeBandInt("class_super", in, Codec.DELTA5, classCount);
      int[] classInterfaceLengths = this.decodeBandInt("class_interface_count", in, Codec.DELTA5, classCount);
      this.classInterfacesInts = this.decodeBandInt("class_interface", in, Codec.DELTA5, classInterfaceLengths);
      this.classFieldCount = this.decodeBandInt("class_field_count", in, Codec.DELTA5, classCount);
      this.classMethodCount = this.decodeBandInt("class_method_count", in, Codec.DELTA5, classCount);
      this.parseFieldBands(in);
      this.parseMethodBands(in);
      this.parseClassAttrBands(in);
      this.parseCodeBands(in);
   }

   public void unpack() {
   }

   private void parseFieldBands(InputStream in) throws IOException, Pack200Exception {
      this.fieldDescrInts = this.decodeBandInt("field_descr", in, Codec.DELTA5, this.classFieldCount);
      this.fieldDescr = this.getReferences(this.fieldDescrInts, this.cpBands.getCpDescriptor());
      this.parseFieldAttrBands(in);
   }

   private void parseFieldAttrBands(InputStream in) throws IOException, Pack200Exception {
      this.fieldFlags = this.parseFlags("field_flags", in, this.classFieldCount, Codec.UNSIGNED5, this.options.hasFieldFlagsHi());
      int fieldAttrCount = SegmentUtils.countBit16(this.fieldFlags);
      int[] fieldAttrCounts = this.decodeBandInt("field_attr_count", in, Codec.UNSIGNED5, fieldAttrCount);
      int[][] fieldAttrIndexes = this.decodeBandInt("field_attr_indexes", in, Codec.UNSIGNED5, fieldAttrCounts);
      int callCount = this.getCallCount(fieldAttrIndexes, this.fieldFlags, 1);
      int[] fieldAttrCalls = this.decodeBandInt("field_attr_calls", in, Codec.UNSIGNED5, callCount);
      this.fieldAttributes = new ArrayList[this.classCount][];

      int j;
      for(int i = 0; i < this.classCount; ++i) {
         this.fieldAttributes[i] = new ArrayList[this.fieldFlags[i].length];

         for(j = 0; j < this.fieldFlags[i].length; ++j) {
            this.fieldAttributes[i][j] = new ArrayList();
         }
      }

      AttributeLayout constantValueLayout = this.attrMap.getAttributeLayout("ConstantValue", 1);
      j = SegmentUtils.countMatches((long[][])this.fieldFlags, constantValueLayout);
      int[] field_constantValue_KQ = this.decodeBandInt("field_ConstantValue_KQ", in, Codec.UNSIGNED5, j);
      int constantValueIndex = 0;
      AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 1);
      int signatureCount = SegmentUtils.countMatches((long[][])this.fieldFlags, signatureLayout);
      int[] fieldSignatureRS = this.decodeBandInt("field_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
      int signatureIndex = 0;
      AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 1);

      int i;
      int backwardsCallIndex;
      int j;
      for(i = 0; i < this.classCount; ++i) {
         for(backwardsCallIndex = 0; backwardsCallIndex < this.fieldFlags[i].length; ++backwardsCallIndex) {
            long flag = this.fieldFlags[i][backwardsCallIndex];
            if (deprecatedLayout.matches(flag)) {
               this.fieldAttributes[i][backwardsCallIndex].add(new DeprecatedAttribute());
            }

            long result;
            String desc;
            String type;
            if (constantValueLayout.matches(flag)) {
               result = (long)field_constantValue_KQ[constantValueIndex];
               desc = this.fieldDescr[i][backwardsCallIndex];
               j = desc.indexOf(58);
               type = desc.substring(j + 1);
               if (type.equals("B") || type.equals("S") || type.equals("C") || type.equals("Z")) {
                  type = "I";
               }

               ClassFileEntry value = constantValueLayout.getValue(result, type, this.cpBands.getConstantPool());
               this.fieldAttributes[i][backwardsCallIndex].add(new ConstantValueAttribute(value));
               ++constantValueIndex;
            }

            if (signatureLayout.matches(flag)) {
               result = (long)fieldSignatureRS[signatureIndex];
               desc = this.fieldDescr[i][backwardsCallIndex];
               j = desc.indexOf(58);
               type = desc.substring(j + 1);
               CPUTF8 value = (CPUTF8)signatureLayout.getValue(result, type, this.cpBands.getConstantPool());
               this.fieldAttributes[i][backwardsCallIndex].add(new SignatureAttribute(value));
               ++signatureIndex;
            }
         }
      }

      i = this.parseFieldMetadataBands(in, fieldAttrCalls);
      backwardsCallIndex = i;
      int limit = this.options.hasFieldFlagsHi() ? 62 : 31;
      AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
      int[] counts = new int[limit + 1];
      List[] otherAttributes = new List[limit + 1];

      int i;
      for(i = 0; i < limit; ++i) {
         AttributeLayout layout = this.attrMap.getAttributeLayout(i, 1);
         if (layout != null && !layout.isDefaultLayout()) {
            otherLayouts[i] = layout;
            counts[i] = SegmentUtils.countMatches((long[][])this.fieldFlags, layout);
         }
      }

      for(i = 0; i < counts.length; ++i) {
         if (counts[i] > 0) {
            NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[i]);
            otherAttributes[i] = bands.parseAttributes(in, counts[i]);
            int numBackwardsCallables = otherLayouts[i].numBackwardsCallables();
            if (numBackwardsCallables > 0) {
               int[] backwardsCalls = new int[numBackwardsCallables];
               System.arraycopy(fieldAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
               bands.setBackwardsCalls(backwardsCalls);
               backwardsCallIndex += numBackwardsCallables;
            }
         }
      }

      for(i = 0; i < this.classCount; ++i) {
         for(j = 0; j < this.fieldFlags[i].length; ++j) {
            long flag = this.fieldFlags[i][j];
            int othersAddedAtStart = 0;

            for(int k = 0; k < otherLayouts.length; ++k) {
               if (otherLayouts[k] != null && otherLayouts[k].matches(flag)) {
                  if (otherLayouts[k].getIndex() < 15) {
                     this.fieldAttributes[i][j].add(othersAddedAtStart++, otherAttributes[k].get(0));
                  } else {
                     this.fieldAttributes[i][j].add(otherAttributes[k].get(0));
                  }

                  otherAttributes[k].remove(0);
               }
            }
         }
      }

   }

   private void parseMethodBands(InputStream in) throws IOException, Pack200Exception {
      this.methodDescrInts = this.decodeBandInt("method_descr", in, Codec.MDELTA5, this.classMethodCount);
      this.methodDescr = this.getReferences(this.methodDescrInts, this.cpBands.getCpDescriptor());
      this.parseMethodAttrBands(in);
   }

   private void parseMethodAttrBands(InputStream in) throws IOException, Pack200Exception {
      this.methodFlags = this.parseFlags("method_flags", in, this.classMethodCount, Codec.UNSIGNED5, this.options.hasMethodFlagsHi());
      int methodAttrCount = SegmentUtils.countBit16(this.methodFlags);
      int[] methodAttrCounts = this.decodeBandInt("method_attr_count", in, Codec.UNSIGNED5, methodAttrCount);
      int[][] methodAttrIndexes = this.decodeBandInt("method_attr_indexes", in, Codec.UNSIGNED5, methodAttrCounts);
      int callCount = this.getCallCount(methodAttrIndexes, this.methodFlags, 2);
      this.methodAttrCalls = this.decodeBandInt("method_attr_calls", in, Codec.UNSIGNED5, callCount);
      this.methodAttributes = new ArrayList[this.classCount][];

      int j;
      for(int i = 0; i < this.classCount; ++i) {
         this.methodAttributes[i] = new ArrayList[this.methodFlags[i].length];

         for(j = 0; j < this.methodFlags[i].length; ++j) {
            this.methodAttributes[i][j] = new ArrayList();
         }
      }

      AttributeLayout methodExceptionsLayout = this.attrMap.getAttributeLayout("Exceptions", 2);
      j = SegmentUtils.countMatches((long[][])this.methodFlags, methodExceptionsLayout);
      int[] numExceptions = this.decodeBandInt("method_Exceptions_n", in, Codec.UNSIGNED5, j);
      int[][] methodExceptionsRS = this.decodeBandInt("method_Exceptions_RC", in, Codec.UNSIGNED5, numExceptions);
      AttributeLayout methodSignatureLayout = this.attrMap.getAttributeLayout("Signature", 2);
      int count1 = SegmentUtils.countMatches((long[][])this.methodFlags, methodSignatureLayout);
      int[] methodSignatureRS = this.decodeBandInt("method_signature_RS", in, Codec.UNSIGNED5, count1);
      AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 2);
      int methodExceptionsIndex = 0;
      int methodSignatureIndex = 0;

      int i;
      int backwardsCallIndex;
      int j;
      for(i = 0; i < this.methodAttributes.length; ++i) {
         for(backwardsCallIndex = 0; backwardsCallIndex < this.methodAttributes[i].length; ++backwardsCallIndex) {
            long flag = this.methodFlags[i][backwardsCallIndex];
            if (methodExceptionsLayout.matches(flag)) {
               int n = numExceptions[methodExceptionsIndex];
               int[] exceptions = methodExceptionsRS[methodExceptionsIndex];
               CPClass[] exceptionClasses = new CPClass[n];

               for(j = 0; j < n; ++j) {
                  exceptionClasses[j] = this.cpBands.cpClassValue(exceptions[j]);
               }

               this.methodAttributes[i][backwardsCallIndex].add(new ExceptionsAttribute(exceptionClasses));
               ++methodExceptionsIndex;
            }

            if (methodSignatureLayout.matches(flag)) {
               long result = (long)methodSignatureRS[methodSignatureIndex];
               String desc = this.methodDescr[i][backwardsCallIndex];
               j = desc.indexOf(58);
               String type = desc.substring(j + 1);
               if (type.equals("B") || type.equals("H")) {
                  type = "I";
               }

               CPUTF8 value = (CPUTF8)methodSignatureLayout.getValue(result, type, this.cpBands.getConstantPool());
               this.methodAttributes[i][backwardsCallIndex].add(new SignatureAttribute(value));
               ++methodSignatureIndex;
            }

            if (deprecatedLayout.matches(flag)) {
               this.methodAttributes[i][backwardsCallIndex].add(new DeprecatedAttribute());
            }
         }
      }

      i = this.parseMethodMetadataBands(in, this.methodAttrCalls);
      backwardsCallIndex = i;
      int limit = this.options.hasMethodFlagsHi() ? 62 : 31;
      AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
      int[] counts = new int[limit + 1];
      List[] otherAttributes = new List[limit + 1];

      int i;
      for(i = 0; i < limit; ++i) {
         AttributeLayout layout = this.attrMap.getAttributeLayout(i, 2);
         if (layout != null && !layout.isDefaultLayout()) {
            otherLayouts[i] = layout;
            counts[i] = SegmentUtils.countMatches((long[][])this.methodFlags, layout);
         }
      }

      for(i = 0; i < counts.length; ++i) {
         if (counts[i] > 0) {
            NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[i]);
            otherAttributes[i] = bands.parseAttributes(in, counts[i]);
            int numBackwardsCallables = otherLayouts[i].numBackwardsCallables();
            if (numBackwardsCallables > 0) {
               int[] backwardsCalls = new int[numBackwardsCallables];
               System.arraycopy(this.methodAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
               bands.setBackwardsCalls(backwardsCalls);
               backwardsCallIndex += numBackwardsCallables;
            }
         }
      }

      for(i = 0; i < this.methodAttributes.length; ++i) {
         for(j = 0; j < this.methodAttributes[i].length; ++j) {
            long flag = this.methodFlags[i][j];
            int othersAddedAtStart = 0;

            for(int k = 0; k < otherLayouts.length; ++k) {
               if (otherLayouts[k] != null && otherLayouts[k].matches(flag)) {
                  if (otherLayouts[k].getIndex() < 15) {
                     this.methodAttributes[i][j].add(othersAddedAtStart++, otherAttributes[k].get(0));
                  } else {
                     this.methodAttributes[i][j].add(otherAttributes[k].get(0));
                  }

                  otherAttributes[k].remove(0);
               }
            }
         }
      }

   }

   private int getCallCount(int[][] methodAttrIndexes, long[][] flags, int context) throws Pack200Exception {
      int callCount = 0;

      int layoutsUsed;
      int i;
      int j;
      for(layoutsUsed = 0; layoutsUsed < methodAttrIndexes.length; ++layoutsUsed) {
         for(i = 0; i < methodAttrIndexes[layoutsUsed].length; ++i) {
            j = methodAttrIndexes[layoutsUsed][i];
            AttributeLayout layout = this.attrMap.getAttributeLayout(j, context);
            callCount += layout.numBackwardsCallables();
         }
      }

      layoutsUsed = 0;

      for(i = 0; i < flags.length; ++i) {
         for(j = 0; j < flags[i].length; ++j) {
            layoutsUsed = (int)((long)layoutsUsed | flags[i][j]);
         }
      }

      for(i = 0; i < 26; ++i) {
         if ((layoutsUsed & 1 << i) != 0) {
            AttributeLayout layout = this.attrMap.getAttributeLayout(i, context);
            callCount += layout.numBackwardsCallables();
         }
      }

      return callCount;
   }

   private void parseClassAttrBands(InputStream in) throws IOException, Pack200Exception {
      String[] cpUTF8 = this.cpBands.getCpUTF8();
      String[] cpClass = this.cpBands.getCpClass();
      this.classAttributes = new ArrayList[this.classCount];

      int classAttrCount;
      for(classAttrCount = 0; classAttrCount < this.classCount; ++classAttrCount) {
         this.classAttributes[classAttrCount] = new ArrayList();
      }

      this.classFlags = this.parseFlags("class_flags", in, this.classCount, Codec.UNSIGNED5, this.options.hasClassFlagsHi());
      classAttrCount = SegmentUtils.countBit16(this.classFlags);
      int[] classAttrCounts = this.decodeBandInt("class_attr_count", in, Codec.UNSIGNED5, classAttrCount);
      int[][] classAttrIndexes = this.decodeBandInt("class_attr_indexes", in, Codec.UNSIGNED5, classAttrCounts);
      int callCount = this.getCallCount(classAttrIndexes, new long[][]{this.classFlags}, 0);
      int[] classAttrCalls = this.decodeBandInt("class_attr_calls", in, Codec.UNSIGNED5, callCount);
      AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 0);
      AttributeLayout sourceFileLayout = this.attrMap.getAttributeLayout("SourceFile", 0);
      int sourceFileCount = SegmentUtils.countMatches((long[])this.classFlags, sourceFileLayout);
      int[] classSourceFile = this.decodeBandInt("class_SourceFile_RUN", in, Codec.UNSIGNED5, sourceFileCount);
      AttributeLayout enclosingMethodLayout = this.attrMap.getAttributeLayout("EnclosingMethod", 0);
      int enclosingMethodCount = SegmentUtils.countMatches((long[])this.classFlags, enclosingMethodLayout);
      int[] enclosingMethodRC = this.decodeBandInt("class_EnclosingMethod_RC", in, Codec.UNSIGNED5, enclosingMethodCount);
      int[] enclosingMethodRDN = this.decodeBandInt("class_EnclosingMethod_RDN", in, Codec.UNSIGNED5, enclosingMethodCount);
      AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 0);
      int signatureCount = SegmentUtils.countMatches((long[])this.classFlags, signatureLayout);
      int[] classSignature = this.decodeBandInt("class_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
      int backwardsCallsUsed = this.parseClassMetadataBands(in, classAttrCalls);
      AttributeLayout innerClassLayout = this.attrMap.getAttributeLayout("InnerClasses", 0);
      int innerClassCount = SegmentUtils.countMatches((long[])this.classFlags, innerClassLayout);
      int[] classInnerClassesN = this.decodeBandInt("class_InnerClasses_N", in, Codec.UNSIGNED5, innerClassCount);
      int[][] classInnerClassesRC = this.decodeBandInt("class_InnerClasses_RC", in, Codec.UNSIGNED5, classInnerClassesN);
      int[][] classInnerClassesF = this.decodeBandInt("class_InnerClasses_F", in, Codec.UNSIGNED5, classInnerClassesN);
      int flagsCount = 0;

      for(int i = 0; i < classInnerClassesF.length; ++i) {
         for(int j = 0; j < classInnerClassesF[i].length; ++j) {
            if (classInnerClassesF[i][j] != 0) {
               ++flagsCount;
            }
         }
      }

      int[] classInnerClassesOuterRCN = this.decodeBandInt("class_InnerClasses_outer_RCN", in, Codec.UNSIGNED5, flagsCount);
      int[] classInnerClassesNameRUN = this.decodeBandInt("class_InnerClasses_name_RUN", in, Codec.UNSIGNED5, flagsCount);
      AttributeLayout versionLayout = this.attrMap.getAttributeLayout("class-file version", 0);
      int versionCount = SegmentUtils.countMatches((long[])this.classFlags, versionLayout);
      int[] classFileVersionMinorH = this.decodeBandInt("class_file_version_minor_H", in, Codec.UNSIGNED5, versionCount);
      int[] classFileVersionMajorH = this.decodeBandInt("class_file_version_major_H", in, Codec.UNSIGNED5, versionCount);
      if (versionCount > 0) {
         this.classVersionMajor = new int[this.classCount];
         this.classVersionMinor = new int[this.classCount];
      }

      int defaultVersionMajor = this.header.getDefaultClassMajorVersion();
      int defaultVersionMinor = this.header.getDefaultClassMinorVersion();
      int backwardsCallIndex = backwardsCallsUsed;
      int limit = this.options.hasClassFlagsHi() ? 62 : 31;
      AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
      int[] counts = new int[limit + 1];
      List[] otherAttributes = new List[limit + 1];

      int sourceFileIndex;
      for(sourceFileIndex = 0; sourceFileIndex < limit; ++sourceFileIndex) {
         AttributeLayout layout = this.attrMap.getAttributeLayout(sourceFileIndex, 0);
         if (layout != null && !layout.isDefaultLayout()) {
            otherLayouts[sourceFileIndex] = layout;
            counts[sourceFileIndex] = SegmentUtils.countMatches((long[])this.classFlags, layout);
         }
      }

      int signatureIndex;
      for(sourceFileIndex = 0; sourceFileIndex < counts.length; ++sourceFileIndex) {
         if (counts[sourceFileIndex] > 0) {
            NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[sourceFileIndex]);
            otherAttributes[sourceFileIndex] = bands.parseAttributes(in, counts[sourceFileIndex]);
            signatureIndex = otherLayouts[sourceFileIndex].numBackwardsCallables();
            if (signatureIndex > 0) {
               int[] backwardsCalls = new int[signatureIndex];
               System.arraycopy(classAttrCalls, backwardsCallIndex, backwardsCalls, 0, signatureIndex);
               bands.setBackwardsCalls(backwardsCalls);
               backwardsCallIndex += signatureIndex;
            }
         }
      }

      sourceFileIndex = 0;
      int enclosingMethodIndex = 0;
      signatureIndex = 0;
      int innerClassIndex = 0;
      int innerClassC2NIndex = 0;
      int versionIndex = 0;
      this.icLocal = new IcTuple[this.classCount][];

      for(int i = 0; i < this.classCount; ++i) {
         long flag = this.classFlags[i];
         if (deprecatedLayout.matches(this.classFlags[i])) {
            this.classAttributes[i].add(new DeprecatedAttribute());
         }

         long result;
         int icTupleF;
         if (sourceFileLayout.matches(flag)) {
            result = (long)classSourceFile[sourceFileIndex];
            ClassFileEntry value = sourceFileLayout.getValue(result, this.cpBands.getConstantPool());
            if (value == null) {
               String className = this.classThis[i].substring(this.classThis[i].lastIndexOf(47) + 1);
               className = className.substring(className.lastIndexOf(46) + 1);
               char[] chars = className.toCharArray();
               icTupleF = -1;

               for(int j = 0; j < chars.length; ++j) {
                  if (chars[j] <= '-') {
                     icTupleF = j;
                     break;
                  }
               }

               if (icTupleF > -1) {
                  className = className.substring(0, icTupleF);
               }

               value = this.cpBands.cpUTF8Value(className + ".java", true);
            }

            this.classAttributes[i].add(new SourceFileAttribute((CPUTF8)value));
            ++sourceFileIndex;
         }

         if (enclosingMethodLayout.matches(flag)) {
            CPClass theClass = this.cpBands.cpClassValue(enclosingMethodRC[enclosingMethodIndex]);
            CPNameAndType theMethod = null;
            if (enclosingMethodRDN[enclosingMethodIndex] != 0) {
               theMethod = this.cpBands.cpNameAndTypeValue(enclosingMethodRDN[enclosingMethodIndex] - 1);
            }

            this.classAttributes[i].add(new EnclosingMethodAttribute(theClass, theMethod));
            ++enclosingMethodIndex;
         }

         if (signatureLayout.matches(flag)) {
            result = (long)classSignature[signatureIndex];
            CPUTF8 value = (CPUTF8)signatureLayout.getValue(result, this.cpBands.getConstantPool());
            this.classAttributes[i].add(new SignatureAttribute(value));
            ++signatureIndex;
         }

         int j;
         if (innerClassLayout.matches(flag)) {
            this.icLocal[i] = new IcTuple[classInnerClassesN[innerClassIndex]];
            j = 0;

            while(true) {
               if (j >= this.icLocal[i].length) {
                  ++innerClassIndex;
                  break;
               }

               int icTupleCIndex = classInnerClassesRC[innerClassIndex][j];
               int icTupleC2Index = -1;
               int icTupleNIndex = -1;
               String icTupleC = cpClass[icTupleCIndex];
               icTupleF = classInnerClassesF[innerClassIndex][j];
               String icTupleC2 = null;
               String icTupleN = null;
               if (icTupleF != 0) {
                  icTupleC2Index = classInnerClassesOuterRCN[innerClassC2NIndex];
                  icTupleNIndex = classInnerClassesNameRUN[innerClassC2NIndex];
                  icTupleC2 = cpClass[icTupleC2Index];
                  icTupleN = cpUTF8[icTupleNIndex];
                  ++innerClassC2NIndex;
               } else {
                  IcBands icBands = this.segment.getIcBands();
                  IcTuple[] icAll = icBands.getIcTuples();

                  for(int k = 0; k < icAll.length; ++k) {
                     if (icAll[k].getC().equals(icTupleC)) {
                        icTupleF = icAll[k].getF();
                        icTupleC2 = icAll[k].getC2();
                        icTupleN = icAll[k].getN();
                        break;
                     }
                  }
               }

               IcTuple icTuple = new IcTuple(icTupleC, icTupleF, icTupleC2, icTupleN, icTupleCIndex, icTupleC2Index, icTupleNIndex, j);
               this.icLocal[i][j] = icTuple;
               ++j;
            }
         }

         if (versionLayout.matches(flag)) {
            this.classVersionMajor[i] = classFileVersionMajorH[versionIndex];
            this.classVersionMinor[i] = classFileVersionMinorH[versionIndex];
            ++versionIndex;
         } else if (this.classVersionMajor != null) {
            this.classVersionMajor[i] = defaultVersionMajor;
            this.classVersionMinor[i] = defaultVersionMinor;
         }

         for(j = 0; j < otherLayouts.length; ++j) {
            if (otherLayouts[j] != null && otherLayouts[j].matches(flag)) {
               this.classAttributes[i].add(otherAttributes[j].get(0));
               otherAttributes[j].remove(0);
            }
         }
      }

   }

   private void parseCodeBands(InputStream in) throws Pack200Exception, IOException {
      AttributeLayout layout = this.attrMap.getAttributeLayout("Code", 2);
      int codeCount = SegmentUtils.countMatches((long[][])this.methodFlags, layout);
      int[] codeHeaders = this.decodeBandInt("code_headers", in, Codec.BYTE1, codeCount);
      boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
      if (!allCodeHasFlags) {
         this.codeHasAttributes = new boolean[codeCount];
      }

      int codeSpecialHeader = 0;

      for(int i = 0; i < codeCount; ++i) {
         if (codeHeaders[i] == 0) {
            ++codeSpecialHeader;
            if (!allCodeHasFlags) {
               this.codeHasAttributes[i] = true;
            }
         }
      }

      int[] codeMaxStackSpecials = this.decodeBandInt("code_max_stack", in, Codec.UNSIGNED5, codeSpecialHeader);
      int[] codeMaxNALocalsSpecials = this.decodeBandInt("code_max_na_locals", in, Codec.UNSIGNED5, codeSpecialHeader);
      int[] codeHandlerCountSpecials = this.decodeBandInt("code_handler_count", in, Codec.UNSIGNED5, codeSpecialHeader);
      this.codeMaxStack = new int[codeCount];
      this.codeMaxNALocals = new int[codeCount];
      this.codeHandlerCount = new int[codeCount];
      int special = 0;

      int i;
      int header;
      for(i = 0; i < codeCount; ++i) {
         header = 255 & codeHeaders[i];
         if (header < 0) {
            throw new IllegalStateException("Shouldn't get here");
         }

         if (header == 0) {
            this.codeMaxStack[i] = codeMaxStackSpecials[special];
            this.codeMaxNALocals[i] = codeMaxNALocalsSpecials[special];
            this.codeHandlerCount[i] = codeHandlerCountSpecials[special];
            ++special;
         } else if (header <= 144) {
            this.codeMaxStack[i] = (header - 1) % 12;
            this.codeMaxNALocals[i] = (header - 1) / 12;
            this.codeHandlerCount[i] = 0;
         } else if (header <= 208) {
            this.codeMaxStack[i] = (header - 145) % 8;
            this.codeMaxNALocals[i] = (header - 145) / 8;
            this.codeHandlerCount[i] = 1;
         } else {
            if (header > 255) {
               throw new IllegalStateException("Shouldn't get here either");
            }

            this.codeMaxStack[i] = (header - 209) % 7;
            this.codeMaxNALocals[i] = (header - 209) / 7;
            this.codeHandlerCount[i] = 2;
         }
      }

      this.codeHandlerStartP = this.decodeBandInt("code_handler_start_P", in, Codec.BCI5, this.codeHandlerCount);
      this.codeHandlerEndPO = this.decodeBandInt("code_handler_end_PO", in, Codec.BRANCH5, this.codeHandlerCount);
      this.codeHandlerCatchPO = this.decodeBandInt("code_handler_catch_PO", in, Codec.BRANCH5, this.codeHandlerCount);
      this.codeHandlerClassRCN = this.decodeBandInt("code_handler_class_RCN", in, Codec.UNSIGNED5, this.codeHandlerCount);
      i = allCodeHasFlags ? codeCount : codeSpecialHeader;
      this.codeAttributes = new List[i];

      for(header = 0; header < this.codeAttributes.length; ++header) {
         this.codeAttributes[header] = new ArrayList();
      }

      this.parseCodeAttrBands(in, i);
   }

   private void parseCodeAttrBands(InputStream in, int codeFlagsCount) throws IOException, Pack200Exception {
      long[] codeFlags = this.parseFlags("code_flags", in, codeFlagsCount, Codec.UNSIGNED5, this.segment.getSegmentHeader().getOptions().hasCodeFlagsHi());
      int codeAttrCount = SegmentUtils.countBit16(codeFlags);
      int[] codeAttrCounts = this.decodeBandInt("code_attr_count", in, Codec.UNSIGNED5, codeAttrCount);
      int[][] codeAttrIndexes = this.decodeBandInt("code_attr_indexes", in, Codec.UNSIGNED5, codeAttrCounts);
      int callCount = 0;

      int index;
      for(int i = 0; i < codeAttrIndexes.length; ++i) {
         for(int j = 0; j < codeAttrIndexes[i].length; ++j) {
            index = codeAttrIndexes[i][j];
            AttributeLayout layout = this.attrMap.getAttributeLayout(index, 3);
            callCount += layout.numBackwardsCallables();
         }
      }

      int[] codeAttrCalls = this.decodeBandInt("code_attr_calls", in, Codec.UNSIGNED5, callCount);
      AttributeLayout lineNumberTableLayout = this.attrMap.getAttributeLayout("LineNumberTable", 3);
      index = SegmentUtils.countMatches((long[])codeFlags, lineNumberTableLayout);
      int[] lineNumberTableN = this.decodeBandInt("code_LineNumberTable_N", in, Codec.UNSIGNED5, index);
      int[][] lineNumberTableBciP = this.decodeBandInt("code_LineNumberTable_bci_P", in, Codec.BCI5, lineNumberTableN);
      int[][] lineNumberTableLine = this.decodeBandInt("code_LineNumberTable_line", in, Codec.UNSIGNED5, lineNumberTableN);
      AttributeLayout localVariableTableLayout = this.attrMap.getAttributeLayout("LocalVariableTable", 3);
      AttributeLayout localVariableTypeTableLayout = this.attrMap.getAttributeLayout("LocalVariableTypeTable", 3);
      int lengthLocalVariableNBand = SegmentUtils.countMatches((long[])codeFlags, localVariableTableLayout);
      int[] localVariableTableN = this.decodeBandInt("code_LocalVariableTable_N", in, Codec.UNSIGNED5, lengthLocalVariableNBand);
      int[][] localVariableTableBciP = this.decodeBandInt("code_LocalVariableTable_bci_P", in, Codec.BCI5, localVariableTableN);
      int[][] localVariableTableSpanO = this.decodeBandInt("code_LocalVariableTable_span_O", in, Codec.BRANCH5, localVariableTableN);
      CPUTF8[][] localVariableTableNameRU = this.parseCPUTF8References("code_LocalVariableTable_name_RU", in, Codec.UNSIGNED5, localVariableTableN);
      CPUTF8[][] localVariableTableTypeRS = this.parseCPSignatureReferences("code_LocalVariableTable_type_RS", in, Codec.UNSIGNED5, localVariableTableN);
      int[][] localVariableTableSlot = this.decodeBandInt("code_LocalVariableTable_slot", in, Codec.UNSIGNED5, localVariableTableN);
      int lengthLocalVariableTypeTableNBand = SegmentUtils.countMatches((long[])codeFlags, localVariableTypeTableLayout);
      int[] localVariableTypeTableN = this.decodeBandInt("code_LocalVariableTypeTable_N", in, Codec.UNSIGNED5, lengthLocalVariableTypeTableNBand);
      int[][] localVariableTypeTableBciP = this.decodeBandInt("code_LocalVariableTypeTable_bci_P", in, Codec.BCI5, localVariableTypeTableN);
      int[][] localVariableTypeTableSpanO = this.decodeBandInt("code_LocalVariableTypeTable_span_O", in, Codec.BRANCH5, localVariableTypeTableN);
      CPUTF8[][] localVariableTypeTableNameRU = this.parseCPUTF8References("code_LocalVariableTypeTable_name_RU", in, Codec.UNSIGNED5, localVariableTypeTableN);
      CPUTF8[][] localVariableTypeTableTypeRS = this.parseCPSignatureReferences("code_LocalVariableTypeTable_type_RS", in, Codec.UNSIGNED5, localVariableTypeTableN);
      int[][] localVariableTypeTableSlot = this.decodeBandInt("code_LocalVariableTypeTable_slot", in, Codec.UNSIGNED5, localVariableTypeTableN);
      int backwardsCallIndex = 0;
      int limit = this.options.hasCodeFlagsHi() ? 62 : 31;
      AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
      int[] counts = new int[limit + 1];
      List[] otherAttributes = new List[limit + 1];

      int lineNumberIndex;
      for(lineNumberIndex = 0; lineNumberIndex < limit; ++lineNumberIndex) {
         AttributeLayout layout = this.attrMap.getAttributeLayout(lineNumberIndex, 3);
         if (layout != null && !layout.isDefaultLayout()) {
            otherLayouts[lineNumberIndex] = layout;
            counts[lineNumberIndex] = SegmentUtils.countMatches((long[])codeFlags, layout);
         }
      }

      int lvttIndex;
      for(lineNumberIndex = 0; lineNumberIndex < counts.length; ++lineNumberIndex) {
         if (counts[lineNumberIndex] > 0) {
            NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[lineNumberIndex]);
            otherAttributes[lineNumberIndex] = bands.parseAttributes(in, counts[lineNumberIndex]);
            lvttIndex = otherLayouts[lineNumberIndex].numBackwardsCallables();
            if (lvttIndex > 0) {
               int[] backwardsCalls = new int[lvttIndex];
               System.arraycopy(codeAttrCalls, backwardsCallIndex, backwardsCalls, 0, lvttIndex);
               bands.setBackwardsCalls(backwardsCalls);
               backwardsCallIndex += lvttIndex;
            }
         }
      }

      lineNumberIndex = 0;
      int lvtIndex = 0;
      lvttIndex = 0;

      for(int i = 0; i < codeFlagsCount; ++i) {
         if (lineNumberTableLayout.matches(codeFlags[i])) {
            LineNumberTableAttribute lnta = new LineNumberTableAttribute(lineNumberTableN[lineNumberIndex], lineNumberTableBciP[lineNumberIndex], lineNumberTableLine[lineNumberIndex]);
            ++lineNumberIndex;
            this.codeAttributes[i].add(lnta);
         }

         if (localVariableTableLayout.matches(codeFlags[i])) {
            LocalVariableTableAttribute lvta = new LocalVariableTableAttribute(localVariableTableN[lvtIndex], localVariableTableBciP[lvtIndex], localVariableTableSpanO[lvtIndex], localVariableTableNameRU[lvtIndex], localVariableTableTypeRS[lvtIndex], localVariableTableSlot[lvtIndex]);
            ++lvtIndex;
            this.codeAttributes[i].add(lvta);
         }

         if (localVariableTypeTableLayout.matches(codeFlags[i])) {
            LocalVariableTypeTableAttribute lvtta = new LocalVariableTypeTableAttribute(localVariableTypeTableN[lvttIndex], localVariableTypeTableBciP[lvttIndex], localVariableTypeTableSpanO[lvttIndex], localVariableTypeTableNameRU[lvttIndex], localVariableTypeTableTypeRS[lvttIndex], localVariableTypeTableSlot[lvttIndex]);
            ++lvttIndex;
            this.codeAttributes[i].add(lvtta);
         }

         for(int j = 0; j < otherLayouts.length; ++j) {
            if (otherLayouts[j] != null && otherLayouts[j].matches(codeFlags[i])) {
               this.codeAttributes[i].add(otherAttributes[j].get(0));
               otherAttributes[j].remove(0);
            }
         }
      }

   }

   private int parseFieldMetadataBands(InputStream in, int[] fieldAttrCalls) throws Pack200Exception, IOException {
      int backwardsCallsUsed = 0;
      String[] RxA = new String[]{"RVA", "RIA"};
      AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 1);
      AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 1);
      int rvaCount = SegmentUtils.countMatches((long[][])this.fieldFlags, rvaLayout);
      int riaCount = SegmentUtils.countMatches((long[][])this.fieldFlags, riaLayout);
      int[] RxACount = new int[]{rvaCount, riaCount};
      int[] backwardsCalls = new int[]{0, 0};
      if (rvaCount > 0) {
         backwardsCalls[0] = fieldAttrCalls[0];
         ++backwardsCallsUsed;
         if (riaCount > 0) {
            backwardsCalls[1] = fieldAttrCalls[1];
            ++backwardsCallsUsed;
         }
      } else if (riaCount > 0) {
         backwardsCalls[1] = fieldAttrCalls[0];
         ++backwardsCallsUsed;
      }

      MetadataBandGroup[] mb = this.parseMetadata(in, RxA, RxACount, backwardsCalls, "field");
      List rvaAttributes = mb[0].getAttributes();
      List riaAttributes = mb[1].getAttributes();
      int rvaAttributesIndex = 0;
      int riaAttributesIndex = 0;

      for(int i = 0; i < this.fieldFlags.length; ++i) {
         for(int j = 0; j < this.fieldFlags[i].length; ++j) {
            if (rvaLayout.matches(this.fieldFlags[i][j])) {
               this.fieldAttributes[i][j].add(rvaAttributes.get(rvaAttributesIndex++));
            }

            if (riaLayout.matches(this.fieldFlags[i][j])) {
               this.fieldAttributes[i][j].add(riaAttributes.get(riaAttributesIndex++));
            }
         }
      }

      return backwardsCallsUsed;
   }

   private MetadataBandGroup[] parseMetadata(InputStream in, String[] RxA, int[] RxACount, int[] backwardsCallCounts, String contextName) throws IOException, Pack200Exception {
      MetadataBandGroup[] mbg = new MetadataBandGroup[RxA.length];

      for(int i = 0; i < RxA.length; ++i) {
         mbg[i] = new MetadataBandGroup(RxA[i], this.cpBands);
         String rxa = RxA[i];
         if (rxa.indexOf(80) >= 0) {
            mbg[i].param_NB = this.decodeBandInt(contextName + "_" + rxa + "_param_NB", in, Codec.BYTE1, RxACount[i]);
         }

         int pairCount = 0;
         int j;
         int k;
         if (rxa.equals("AD")) {
            pairCount = RxACount[i];
         } else {
            mbg[i].anno_N = this.decodeBandInt(contextName + "_" + rxa + "_anno_N", in, Codec.UNSIGNED5, RxACount[i]);
            mbg[i].type_RS = this.parseCPSignatureReferences(contextName + "_" + rxa + "_type_RS", in, Codec.UNSIGNED5, mbg[i].anno_N);
            mbg[i].pair_N = this.decodeBandInt(contextName + "_" + rxa + "_pair_N", in, Codec.UNSIGNED5, mbg[i].anno_N);
            j = 0;

            while(true) {
               if (j >= mbg[i].pair_N.length) {
                  mbg[i].name_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_name_RU", in, Codec.UNSIGNED5, pairCount);
                  break;
               }

               for(k = 0; k < mbg[i].pair_N[j].length; ++k) {
                  pairCount += mbg[i].pair_N[j][k];
               }

               ++j;
            }
         }

         mbg[i].T = this.decodeBandInt(contextName + "_" + rxa + "_T", in, Codec.BYTE1, pairCount + backwardsCallCounts[i]);
         j = 0;
         k = 0;
         int FCount = 0;
         int JCount = 0;
         int cCount = 0;
         int eCount = 0;
         int sCount = 0;
         int arrayCount = 0;
         int atCount = 0;

         int nestPairCount;
         int j;
         for(nestPairCount = 0; nestPairCount < mbg[i].T.length; ++nestPairCount) {
            j = (char)mbg[i].T[nestPairCount];
            switch (j) {
               case 64:
                  ++atCount;
               case 65:
               case 69:
               case 71:
               case 72:
               case 75:
               case 76:
               case 77:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 84:
               case 85:
               case 86:
               case 87:
               case 88:
               case 89:
               case 92:
               case 93:
               case 94:
               case 95:
               case 96:
               case 97:
               case 98:
               case 100:
               case 102:
               case 103:
               case 104:
               case 105:
               case 106:
               case 107:
               case 108:
               case 109:
               case 110:
               case 111:
               case 112:
               case 113:
               case 114:
               default:
                  break;
               case 66:
               case 67:
               case 73:
               case 83:
               case 90:
                  ++j;
                  break;
               case 68:
                  ++k;
                  break;
               case 70:
                  ++FCount;
                  break;
               case 74:
                  ++JCount;
                  break;
               case 91:
                  ++arrayCount;
                  break;
               case 99:
                  ++cCount;
                  break;
               case 101:
                  ++eCount;
                  break;
               case 115:
                  ++sCount;
            }
         }

         mbg[i].caseI_KI = this.parseCPIntReferences(contextName + "_" + rxa + "_caseI_KI", in, Codec.UNSIGNED5, j);
         mbg[i].caseD_KD = this.parseCPDoubleReferences(contextName + "_" + rxa + "_caseD_KD", in, Codec.UNSIGNED5, k);
         mbg[i].caseF_KF = this.parseCPFloatReferences(contextName + "_" + rxa + "_caseF_KF", in, Codec.UNSIGNED5, FCount);
         mbg[i].caseJ_KJ = this.parseCPLongReferences(contextName + "_" + rxa + "_caseJ_KJ", in, Codec.UNSIGNED5, JCount);
         mbg[i].casec_RS = this.parseCPSignatureReferences(contextName + "_" + rxa + "_casec_RS", in, Codec.UNSIGNED5, cCount);
         mbg[i].caseet_RS = this.parseReferences(contextName + "_" + rxa + "_caseet_RS", in, Codec.UNSIGNED5, eCount, this.cpBands.getCpSignature());
         mbg[i].caseec_RU = this.parseReferences(contextName + "_" + rxa + "_caseec_RU", in, Codec.UNSIGNED5, eCount, this.cpBands.getCpUTF8());
         mbg[i].cases_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_cases_RU", in, Codec.UNSIGNED5, sCount);
         mbg[i].casearray_N = this.decodeBandInt(contextName + "_" + rxa + "_casearray_N", in, Codec.UNSIGNED5, arrayCount);
         mbg[i].nesttype_RS = this.parseCPUTF8References(contextName + "_" + rxa + "_nesttype_RS", in, Codec.UNSIGNED5, atCount);
         mbg[i].nestpair_N = this.decodeBandInt(contextName + "_" + rxa + "_nestpair_N", in, Codec.UNSIGNED5, atCount);
         nestPairCount = 0;

         for(j = 0; j < mbg[i].nestpair_N.length; ++j) {
            nestPairCount += mbg[i].nestpair_N[j];
         }

         mbg[i].nestname_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_nestname_RU", in, Codec.UNSIGNED5, nestPairCount);
      }

      return mbg;
   }

   private int parseMethodMetadataBands(InputStream in, int[] methodAttrCalls) throws Pack200Exception, IOException {
      int backwardsCallsUsed = 0;
      String[] RxA = new String[]{"RVA", "RIA", "RVPA", "RIPA", "AD"};
      int[] rxaCounts = new int[]{0, 0, 0, 0, 0};
      AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 2);
      AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 2);
      AttributeLayout rvpaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleParameterAnnotations", 2);
      AttributeLayout ripaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleParameterAnnotations", 2);
      AttributeLayout adLayout = this.attrMap.getAttributeLayout("AnnotationDefault", 2);
      AttributeLayout[] rxaLayouts = new AttributeLayout[]{rvaLayout, riaLayout, rvpaLayout, ripaLayout, adLayout};

      for(int i = 0; i < rxaLayouts.length; ++i) {
         rxaCounts[i] = SegmentUtils.countMatches((long[][])this.methodFlags, rxaLayouts[i]);
      }

      int[] backwardsCalls = new int[5];
      int methodAttrIndex = 0;

      for(int i = 0; i < backwardsCalls.length; ++i) {
         if (rxaCounts[i] > 0) {
            ++backwardsCallsUsed;
            backwardsCalls[i] = methodAttrCalls[methodAttrIndex];
            ++methodAttrIndex;
         } else {
            backwardsCalls[i] = 0;
         }
      }

      MetadataBandGroup[] mbgs = this.parseMetadata(in, RxA, rxaCounts, backwardsCalls, "method");
      List[] attributeLists = new List[RxA.length];
      int[] attributeListIndexes = new int[RxA.length];

      int i;
      for(i = 0; i < mbgs.length; ++i) {
         attributeLists[i] = mbgs[i].getAttributes();
         attributeListIndexes[i] = 0;
      }

      for(i = 0; i < this.methodFlags.length; ++i) {
         for(int j = 0; j < this.methodFlags[i].length; ++j) {
            for(int k = 0; k < rxaLayouts.length; ++k) {
               if (rxaLayouts[k].matches(this.methodFlags[i][j])) {
                  this.methodAttributes[i][j].add(attributeLists[k].get(attributeListIndexes[k]++));
               }
            }
         }
      }

      return backwardsCallsUsed;
   }

   private int parseClassMetadataBands(InputStream in, int[] classAttrCalls) throws Pack200Exception, IOException {
      int numBackwardsCalls = 0;
      String[] RxA = new String[]{"RVA", "RIA"};
      AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 0);
      AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 0);
      int rvaCount = SegmentUtils.countMatches((long[])this.classFlags, rvaLayout);
      int riaCount = SegmentUtils.countMatches((long[])this.classFlags, riaLayout);
      int[] RxACount = new int[]{rvaCount, riaCount};
      int[] backwardsCalls = new int[]{0, 0};
      if (rvaCount > 0) {
         ++numBackwardsCalls;
         backwardsCalls[0] = classAttrCalls[0];
         if (riaCount > 0) {
            ++numBackwardsCalls;
            backwardsCalls[1] = classAttrCalls[1];
         }
      } else if (riaCount > 0) {
         ++numBackwardsCalls;
         backwardsCalls[1] = classAttrCalls[0];
      }

      MetadataBandGroup[] mbgs = this.parseMetadata(in, RxA, RxACount, backwardsCalls, "class");
      List rvaAttributes = mbgs[0].getAttributes();
      List riaAttributes = mbgs[1].getAttributes();
      int rvaAttributesIndex = 0;
      int riaAttributesIndex = 0;

      for(int i = 0; i < this.classFlags.length; ++i) {
         if (rvaLayout.matches(this.classFlags[i])) {
            this.classAttributes[i].add(rvaAttributes.get(rvaAttributesIndex++));
         }

         if (riaLayout.matches(this.classFlags[i])) {
            this.classAttributes[i].add(riaAttributes.get(riaAttributesIndex++));
         }
      }

      return numBackwardsCalls;
   }

   public ArrayList[] getClassAttributes() {
      return this.classAttributes;
   }

   public int[] getClassFieldCount() {
      return this.classFieldCount;
   }

   public long[] getRawClassFlags() {
      return this.classFlags;
   }

   public long[] getClassFlags() throws Pack200Exception {
      if (this.classAccessFlags == null) {
         long mask = 32767L;

         int i;
         for(i = 0; i < 16; ++i) {
            AttributeLayout layout = this.attrMap.getAttributeLayout(i, 0);
            if (layout != null && !layout.isDefaultLayout()) {
               mask &= (long)(~(1 << i));
            }
         }

         this.classAccessFlags = new long[this.classFlags.length];

         for(i = 0; i < this.classFlags.length; ++i) {
            this.classAccessFlags[i] = this.classFlags[i] & mask;
         }
      }

      return this.classAccessFlags;
   }

   public int[][] getClassInterfacesInts() {
      return this.classInterfacesInts;
   }

   public int[] getClassMethodCount() {
      return this.classMethodCount;
   }

   public int[] getClassSuperInts() {
      return this.classSuperInts;
   }

   public int[] getClassThisInts() {
      return this.classThisInts;
   }

   public int[] getCodeMaxNALocals() {
      return this.codeMaxNALocals;
   }

   public int[] getCodeMaxStack() {
      return this.codeMaxStack;
   }

   public ArrayList[][] getFieldAttributes() {
      return this.fieldAttributes;
   }

   public int[][] getFieldDescrInts() {
      return this.fieldDescrInts;
   }

   public int[][] getMethodDescrInts() {
      return this.methodDescrInts;
   }

   public long[][] getFieldFlags() throws Pack200Exception {
      if (this.fieldAccessFlags == null) {
         long mask = 32767L;

         int i;
         for(i = 0; i < 16; ++i) {
            AttributeLayout layout = this.attrMap.getAttributeLayout(i, 1);
            if (layout != null && !layout.isDefaultLayout()) {
               mask &= (long)(~(1 << i));
            }
         }

         this.fieldAccessFlags = new long[this.fieldFlags.length][];

         for(i = 0; i < this.fieldFlags.length; ++i) {
            this.fieldAccessFlags[i] = new long[this.fieldFlags[i].length];

            for(int j = 0; j < this.fieldFlags[i].length; ++j) {
               this.fieldAccessFlags[i][j] = this.fieldFlags[i][j] & mask;
            }
         }
      }

      return this.fieldAccessFlags;
   }

   public ArrayList getOrderedCodeAttributes() {
      ArrayList orderedAttributeList = new ArrayList(this.codeAttributes.length);

      for(int classIndex = 0; classIndex < this.codeAttributes.length; ++classIndex) {
         ArrayList currentAttributes = new ArrayList(this.codeAttributes[classIndex].size());

         for(int attributeIndex = 0; attributeIndex < this.codeAttributes[classIndex].size(); ++attributeIndex) {
            Attribute attribute = (Attribute)this.codeAttributes[classIndex].get(attributeIndex);
            currentAttributes.add(attribute);
         }

         orderedAttributeList.add(currentAttributes);
      }

      return orderedAttributeList;
   }

   public ArrayList[][] getMethodAttributes() {
      return this.methodAttributes;
   }

   public String[][] getMethodDescr() {
      return this.methodDescr;
   }

   public long[][] getMethodFlags() throws Pack200Exception {
      if (this.methodAccessFlags == null) {
         long mask = 32767L;

         int i;
         for(i = 0; i < 16; ++i) {
            AttributeLayout layout = this.attrMap.getAttributeLayout(i, 2);
            if (layout != null && !layout.isDefaultLayout()) {
               mask &= (long)(~(1 << i));
            }
         }

         this.methodAccessFlags = new long[this.methodFlags.length][];

         for(i = 0; i < this.methodFlags.length; ++i) {
            this.methodAccessFlags[i] = new long[this.methodFlags[i].length];

            for(int j = 0; j < this.methodFlags[i].length; ++j) {
               this.methodAccessFlags[i][j] = this.methodFlags[i][j] & mask;
            }
         }
      }

      return this.methodAccessFlags;
   }

   public int[] getClassVersionMajor() {
      return this.classVersionMajor;
   }

   public int[] getClassVersionMinor() {
      return this.classVersionMinor;
   }

   public int[] getCodeHandlerCount() {
      return this.codeHandlerCount;
   }

   public int[][] getCodeHandlerCatchPO() {
      return this.codeHandlerCatchPO;
   }

   public int[][] getCodeHandlerClassRCN() {
      return this.codeHandlerClassRCN;
   }

   public int[][] getCodeHandlerEndPO() {
      return this.codeHandlerEndPO;
   }

   public int[][] getCodeHandlerStartP() {
      return this.codeHandlerStartP;
   }

   public IcTuple[][] getIcLocal() {
      return this.icLocal;
   }

   public boolean[] getCodeHasAttributes() {
      return this.codeHasAttributes;
   }
}
