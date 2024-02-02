package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;

public class BcBands extends BandSet {
   private final CpBands cpBands;
   private final Segment segment;
   private final IntList bcCodes = new IntList();
   private final IntList bcCaseCount = new IntList();
   private final IntList bcCaseValue = new IntList();
   private final IntList bcByte = new IntList();
   private final IntList bcShort = new IntList();
   private final IntList bcLocal = new IntList();
   private final List bcLabel = new ArrayList();
   private final List bcIntref = new ArrayList();
   private final List bcFloatRef = new ArrayList();
   private final List bcLongRef = new ArrayList();
   private final List bcDoubleRef = new ArrayList();
   private final List bcStringRef = new ArrayList();
   private final List bcClassRef = new ArrayList();
   private final List bcFieldRef = new ArrayList();
   private final List bcMethodRef = new ArrayList();
   private final List bcIMethodRef = new ArrayList();
   private List bcThisField = new ArrayList();
   private final List bcSuperField = new ArrayList();
   private List bcThisMethod = new ArrayList();
   private List bcSuperMethod = new ArrayList();
   private List bcInitRef = new ArrayList();
   private String currentClass;
   private String superClass;
   private String currentNewClass;
   private static final int MULTIANEWARRAY = 197;
   private static final int ALOAD_0 = 42;
   private static final int WIDE = 196;
   private static final int INVOKEINTERFACE = 185;
   private static final int TABLESWITCH = 170;
   private static final int IINC = 132;
   private static final int LOOKUPSWITCH = 171;
   private static final int endMarker = 255;
   private final IntList bciRenumbering = new IntList();
   private final Map labelsToOffsets = new HashMap();
   private int byteCodeOffset;
   private int renumberedOffset;
   private final IntList bcLabelRelativeOffsets = new IntList();

   public BcBands(CpBands cpBands, Segment segment, int effort) {
      super(effort, segment.getSegmentHeader());
      this.cpBands = cpBands;
      this.segment = segment;
   }

   public void setCurrentClass(String name, String superName) {
      this.currentClass = name;
      this.superClass = superName;
   }

   public void finaliseBands() {
      this.bcThisField = this.getIndexInClass(this.bcThisField);
      this.bcThisMethod = this.getIndexInClass(this.bcThisMethod);
      this.bcSuperMethod = this.getIndexInClass(this.bcSuperMethod);
      this.bcInitRef = this.getIndexInClassForConstructor(this.bcInitRef);
   }

   public void pack(OutputStream out) throws IOException, Pack200Exception {
      PackingUtils.log("Writing byte code bands...");
      byte[] encodedBand = this.encodeBandInt("bcCodes", this.bcCodes.toArray(), Codec.BYTE1);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCodes[" + this.bcCodes.size() + "]");
      encodedBand = this.encodeBandInt("bcCaseCount", this.bcCaseCount.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseCount[" + this.bcCaseCount.size() + "]");
      encodedBand = this.encodeBandInt("bcCaseValue", this.bcCaseValue.toArray(), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseValue[" + this.bcCaseValue.size() + "]");
      encodedBand = this.encodeBandInt("bcByte", this.bcByte.toArray(), Codec.BYTE1);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcByte[" + this.bcByte.size() + "]");
      encodedBand = this.encodeBandInt("bcShort", this.bcShort.toArray(), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcShort[" + this.bcShort.size() + "]");
      encodedBand = this.encodeBandInt("bcLocal", this.bcLocal.toArray(), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLocal[" + this.bcLocal.size() + "]");
      encodedBand = this.encodeBandInt("bcLabel", this.integerListToArray(this.bcLabel), Codec.BRANCH5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLabel[" + this.bcLabel.size() + "]");
      encodedBand = this.encodeBandInt("bcIntref", this.cpEntryListToArray(this.bcIntref), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIntref[" + this.bcIntref.size() + "]");
      encodedBand = this.encodeBandInt("bcFloatRef", this.cpEntryListToArray(this.bcFloatRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFloatRef[" + this.bcFloatRef.size() + "]");
      encodedBand = this.encodeBandInt("bcLongRef", this.cpEntryListToArray(this.bcLongRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLongRef[" + this.bcLongRef.size() + "]");
      encodedBand = this.encodeBandInt("bcDoubleRef", this.cpEntryListToArray(this.bcDoubleRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcDoubleRef[" + this.bcDoubleRef.size() + "]");
      encodedBand = this.encodeBandInt("bcStringRef", this.cpEntryListToArray(this.bcStringRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcStringRef[" + this.bcStringRef.size() + "]");
      encodedBand = this.encodeBandInt("bcClassRef", this.cpEntryOrNullListToArray(this.bcClassRef), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcClassRef[" + this.bcClassRef.size() + "]");
      encodedBand = this.encodeBandInt("bcFieldRef", this.cpEntryListToArray(this.bcFieldRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFieldRef[" + this.bcFieldRef.size() + "]");
      encodedBand = this.encodeBandInt("bcMethodRef", this.cpEntryListToArray(this.bcMethodRef), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcMethodRef[" + this.bcMethodRef.size() + "]");
      encodedBand = this.encodeBandInt("bcIMethodRef", this.cpEntryListToArray(this.bcIMethodRef), Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIMethodRef[" + this.bcIMethodRef.size() + "]");
      encodedBand = this.encodeBandInt("bcThisField", this.integerListToArray(this.bcThisField), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisField[" + this.bcThisField.size() + "]");
      encodedBand = this.encodeBandInt("bcSuperField", this.integerListToArray(this.bcSuperField), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperField[" + this.bcSuperField.size() + "]");
      encodedBand = this.encodeBandInt("bcThisMethod", this.integerListToArray(this.bcThisMethod), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisMethod[" + this.bcThisMethod.size() + "]");
      encodedBand = this.encodeBandInt("bcSuperMethod", this.integerListToArray(this.bcSuperMethod), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperMethod[" + this.bcSuperMethod.size() + "]");
      encodedBand = this.encodeBandInt("bcInitRef", this.integerListToArray(this.bcInitRef), Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcInitRef[" + this.bcInitRef.size() + "]");
   }

   private List getIndexInClass(List cPMethodOrFieldList) {
      List indices = new ArrayList(cPMethodOrFieldList.size());

      for(int i = 0; i < cPMethodOrFieldList.size(); ++i) {
         CPMethodOrField cpMF = (CPMethodOrField)cPMethodOrFieldList.get(i);
         indices.add(cpMF.getIndexInClass());
      }

      return indices;
   }

   private List getIndexInClassForConstructor(List cPMethodList) {
      List indices = new ArrayList(cPMethodList.size());

      for(int i = 0; i < cPMethodList.size(); ++i) {
         CPMethodOrField cpMF = (CPMethodOrField)cPMethodList.get(i);
         indices.add(cpMF.getIndexInClassForConstructor());
      }

      return indices;
   }

   public void visitEnd() {
      int i;
      for(i = 0; i < this.bciRenumbering.size(); ++i) {
         if (this.bciRenumbering.get(i) == -1) {
            this.bciRenumbering.remove(i);
            this.bciRenumbering.add(i, ++this.renumberedOffset);
         }
      }

      if (this.renumberedOffset != 0) {
         if (this.renumberedOffset + 1 != this.bciRenumbering.size()) {
            throw new RuntimeException("Mistake made with renumbering");
         }

         for(i = this.bcLabel.size() - 1; i >= 0; --i) {
            Object label = this.bcLabel.get(i);
            if (label instanceof Integer) {
               break;
            }

            if (label instanceof Label) {
               this.bcLabel.remove(i);
               Integer offset = (Integer)this.labelsToOffsets.get(label);
               int relativeOffset = this.bcLabelRelativeOffsets.get(i);
               this.bcLabel.add(i, this.bciRenumbering.get(offset) - this.bciRenumbering.get(relativeOffset));
            }
         }

         this.bcCodes.add(255);
         this.segment.getClassBands().doBciRenumbering(this.bciRenumbering, this.labelsToOffsets);
         this.bciRenumbering.clear();
         this.labelsToOffsets.clear();
         this.byteCodeOffset = 0;
         this.renumberedOffset = 0;
      }

   }

   public void visitLabel(Label label) {
      this.labelsToOffsets.put(label, this.byteCodeOffset);
   }

   public void visitFieldInsn(int opcode, String owner, String name, String desc) {
      this.byteCodeOffset += 3;
      this.updateRenumbering();
      boolean aload_0 = false;
      if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
         this.bcCodes.remove(this.bcCodes.size() - 1);
         aload_0 = true;
      }

      CPMethodOrField cpField = this.cpBands.getCPField(owner, name, desc);
      if (aload_0) {
         opcode += 7;
      }

      if (owner.equals(this.currentClass)) {
         opcode += 24;
         this.bcThisField.add(cpField);
      } else {
         if (aload_0) {
            opcode -= 7;
            this.bcCodes.add(42);
         }

         this.bcFieldRef.add(cpField);
      }

      aload_0 = false;
      this.bcCodes.add(opcode);
   }

   private void updateRenumbering() {
      if (this.bciRenumbering.isEmpty()) {
         this.bciRenumbering.add(0);
      }

      ++this.renumberedOffset;

      for(int i = this.bciRenumbering.size(); i < this.byteCodeOffset; ++i) {
         this.bciRenumbering.add(-1);
      }

      this.bciRenumbering.add(this.renumberedOffset);
   }

   public void visitIincInsn(int var, int increment) {
      if (var <= 255 && increment <= 255) {
         this.byteCodeOffset += 3;
         this.bcCodes.add(132);
         this.bcLocal.add(var);
         this.bcByte.add(increment & 255);
      } else {
         this.byteCodeOffset += 6;
         this.bcCodes.add(196);
         this.bcCodes.add(132);
         this.bcLocal.add(var);
         this.bcShort.add(increment);
      }

      this.updateRenumbering();
   }

   public void visitInsn(int opcode) {
      if (opcode >= 202) {
         throw new RuntimeException("Non-standard bytecode instructions not supported");
      } else {
         this.bcCodes.add(opcode);
         ++this.byteCodeOffset;
         this.updateRenumbering();
      }
   }

   public void visitIntInsn(int opcode, int operand) {
      switch (opcode) {
         case 16:
         case 188:
            this.bcCodes.add(opcode);
            this.bcByte.add(operand & 255);
            this.byteCodeOffset += 2;
            break;
         case 17:
            this.bcCodes.add(opcode);
            this.bcShort.add(operand);
            this.byteCodeOffset += 3;
      }

      this.updateRenumbering();
   }

   public void visitJumpInsn(int opcode, Label label) {
      this.bcCodes.add(opcode);
      this.bcLabel.add(label);
      this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
      this.byteCodeOffset += 3;
      this.updateRenumbering();
   }

   public void visitLdcInsn(Object cst) {
      CPConstant constant = this.cpBands.getConstant(cst);
      if (!this.segment.lastConstantHadWideIndex() && !(constant instanceof CPLong) && !(constant instanceof CPDouble)) {
         this.byteCodeOffset += 2;
         if (constant instanceof CPInt) {
            this.bcCodes.add(234);
            this.bcIntref.add(constant);
         } else if (constant instanceof CPFloat) {
            this.bcCodes.add(235);
            this.bcFloatRef.add(constant);
         } else if (constant instanceof CPString) {
            this.bcCodes.add(18);
            this.bcStringRef.add(constant);
         } else if (constant instanceof CPClass) {
            this.bcCodes.add(233);
            this.bcClassRef.add(constant);
         }
      } else {
         this.byteCodeOffset += 3;
         if (constant instanceof CPInt) {
            this.bcCodes.add(237);
            this.bcIntref.add(constant);
         } else if (constant instanceof CPFloat) {
            this.bcCodes.add(238);
            this.bcFloatRef.add(constant);
         } else if (constant instanceof CPLong) {
            this.bcCodes.add(20);
            this.bcLongRef.add(constant);
         } else if (constant instanceof CPDouble) {
            this.bcCodes.add(239);
            this.bcDoubleRef.add(constant);
         } else if (constant instanceof CPString) {
            this.bcCodes.add(19);
            this.bcStringRef.add(constant);
         } else {
            if (!(constant instanceof CPClass)) {
               throw new RuntimeException("Constant should not be null");
            }

            this.bcCodes.add(236);
            this.bcClassRef.add(constant);
         }
      }

      this.updateRenumbering();
   }

   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
      this.bcCodes.add(171);
      this.bcLabel.add(dflt);
      this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
      this.bcCaseCount.add(keys.length);

      int padding;
      for(padding = 0; padding < labels.length; ++padding) {
         this.bcCaseValue.add(keys[padding]);
         this.bcLabel.add(labels[padding]);
         this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
      }

      padding = (this.byteCodeOffset + 1) % 4 == 0 ? 0 : 4 - (this.byteCodeOffset + 1) % 4;
      this.byteCodeOffset += 1 + padding + 8 + 8 * keys.length;
      this.updateRenumbering();
   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      this.byteCodeOffset += 3;
      switch (opcode) {
         case 182:
         case 183:
         case 184:
            boolean aload_0 = false;
            if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
               this.bcCodes.remove(this.bcCodes.size() - 1);
               aload_0 = true;
               opcode += 7;
            }

            if (owner.equals(this.currentClass)) {
               opcode += 24;
               if (name.equals("<init>") && opcode == 207) {
                  opcode = 230;
                  this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
               } else {
                  this.bcThisMethod.add(this.cpBands.getCPMethod(owner, name, desc));
               }
            } else if (owner.equals(this.superClass)) {
               opcode += 38;
               if (name.equals("<init>") && opcode == 221) {
                  opcode = 231;
                  this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
               } else {
                  this.bcSuperMethod.add(this.cpBands.getCPMethod(owner, name, desc));
               }
            } else {
               if (aload_0) {
                  opcode -= 7;
                  this.bcCodes.add(42);
               }

               if (name.equals("<init>") && opcode == 183 && owner.equals(this.currentNewClass)) {
                  opcode = 232;
                  this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
               } else {
                  this.bcMethodRef.add(this.cpBands.getCPMethod(owner, name, desc));
               }
            }

            this.bcCodes.add(opcode);
            break;
         case 185:
            this.byteCodeOffset += 2;
            CPMethodOrField cpIMethod = this.cpBands.getCPIMethod(owner, name, desc);
            this.bcIMethodRef.add(cpIMethod);
            this.bcCodes.add(185);
      }

      this.updateRenumbering();
   }

   public void visitMultiANewArrayInsn(String desc, int dimensions) {
      this.byteCodeOffset += 4;
      this.updateRenumbering();
      this.bcCodes.add(197);
      this.bcClassRef.add(this.cpBands.getCPClass(desc));
      this.bcByte.add(dimensions & 255);
   }

   public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
      this.bcCodes.add(170);
      this.bcLabel.add(dflt);
      this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
      this.bcCaseValue.add(min);
      int count = labels.length;
      this.bcCaseCount.add(count);

      int padding;
      for(padding = 0; padding < count; ++padding) {
         this.bcLabel.add(labels[padding]);
         this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
      }

      padding = this.byteCodeOffset % 4 == 0 ? 0 : 4 - this.byteCodeOffset % 4;
      this.byteCodeOffset += padding + 12 + 4 * labels.length;
      this.updateRenumbering();
   }

   public void visitTypeInsn(int opcode, String type) {
      this.byteCodeOffset += 3;
      this.updateRenumbering();
      this.bcCodes.add(opcode);
      this.bcClassRef.add(this.cpBands.getCPClass(type));
      if (opcode == 187) {
         this.currentNewClass = type;
      }

   }

   public void visitVarInsn(int opcode, int var) {
      if (var > 255) {
         this.byteCodeOffset += 4;
         this.bcCodes.add(196);
         this.bcCodes.add(opcode);
         this.bcLocal.add(var);
      } else if (var <= 3 && opcode != 169) {
         ++this.byteCodeOffset;
         switch (opcode) {
            case 21:
            case 54:
               this.bcCodes.add(opcode + 5 + var);
               break;
            case 22:
            case 55:
               this.bcCodes.add(opcode + 8 + var);
               break;
            case 23:
            case 56:
               this.bcCodes.add(opcode + 11 + var);
               break;
            case 24:
            case 57:
               this.bcCodes.add(opcode + 14 + var);
               break;
            case 25:
            case 58:
               this.bcCodes.add(opcode + 17 + var);
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
         }
      } else {
         this.byteCodeOffset += 2;
         this.bcCodes.add(opcode);
         this.bcLocal.add(var);
      }

      this.updateRenumbering();
   }
}
