package org.apache.commons.compress.harmony.unpack200;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.BCIRenumberedAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionTableEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class BcBands extends BandSet {
   private byte[][][] methodByteCodePacked;
   private int[] bcCaseCount;
   private int[] bcCaseValue;
   private int[] bcByte;
   private int[] bcLocal;
   private int[] bcShort;
   private int[] bcLabel;
   private int[] bcIntRef;
   private int[] bcFloatRef;
   private int[] bcLongRef;
   private int[] bcDoubleRef;
   private int[] bcStringRef;
   private int[] bcClassRef;
   private int[] bcFieldRef;
   private int[] bcMethodRef;
   private int[] bcIMethodRef;
   private int[] bcThisField;
   private int[] bcSuperField;
   private int[] bcThisMethod;
   private int[] bcSuperMethod;
   private int[] bcInitRef;
   private int[] bcEscRef;
   private int[] bcEscRefSize;
   private int[] bcEscSize;
   private int[][] bcEscByte;
   private List wideByteCodes;

   public BcBands(Segment segment) {
      super(segment);
   }

   public void read(InputStream in) throws IOException, Pack200Exception {
      AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
      int classCount = this.header.getClassCount();
      long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
      int bcCaseCountCount = 0;
      int bcByteCount = 0;
      int bcShortCount = 0;
      int bcLocalCount = 0;
      int bcLabelCount = 0;
      int bcIntRefCount = 0;
      int bcFloatRefCount = 0;
      int bcLongRefCount = 0;
      int bcDoubleRefCount = 0;
      int bcStringRefCount = 0;
      int bcClassRefCount = 0;
      int bcFieldRefCount = 0;
      int bcMethodRefCount = 0;
      int bcIMethodRefCount = 0;
      int bcThisFieldCount = 0;
      int bcSuperFieldCount = 0;
      int bcThisMethodCount = 0;
      int bcSuperMethodCount = 0;
      int bcInitRefCount = 0;
      int bcEscCount = 0;
      int bcEscRefCount = 0;
      AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
      AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
      this.methodByteCodePacked = new byte[classCount][][];
      int bcParsed = 0;
      List switchIsTableSwitch = new ArrayList();
      this.wideByteCodes = new ArrayList();

      int c;
      int index;
      for(c = 0; c < classCount; ++c) {
         index = methodFlags[c].length;
         this.methodByteCodePacked[c] = new byte[index][];

         for(int m = 0; m < index; ++m) {
            long methodFlag = methodFlags[c][m];
            if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
               ByteArrayOutputStream codeBytes = new ByteArrayOutputStream();

               byte code;
               while((code = (byte)(255 & in.read())) != -1) {
                  codeBytes.write(code);
               }

               this.methodByteCodePacked[c][m] = codeBytes.toByteArray();
               bcParsed += this.methodByteCodePacked[c][m].length;
               int[] codes = new int[this.methodByteCodePacked[c][m].length];

               int i;
               for(i = 0; i < codes.length; ++i) {
                  codes[i] = this.methodByteCodePacked[c][m][i] & 255;
               }

               for(i = 0; i < this.methodByteCodePacked[c][m].length; ++i) {
                  int codePacked = 255 & this.methodByteCodePacked[c][m][i];
                  switch (codePacked) {
                     case 16:
                     case 188:
                        ++bcByteCount;
                        break;
                     case 17:
                        ++bcShortCount;
                        break;
                     case 18:
                     case 19:
                        ++bcStringRefCount;
                        break;
                     case 20:
                        ++bcLongRefCount;
                        break;
                     case 21:
                     case 22:
                     case 23:
                     case 24:
                     case 25:
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
                     case 54:
                     case 55:
                     case 56:
                     case 57:
                     case 58:
                     case 59:
                     case 60:
                     case 61:
                     case 62:
                     case 63:
                     case 64:
                     case 65:
                     case 66:
                     case 67:
                     case 68:
                     case 69:
                     case 70:
                     case 71:
                     case 72:
                     case 73:
                     case 74:
                     case 75:
                     case 76:
                     case 77:
                     case 78:
                     case 79:
                     case 80:
                     case 81:
                     case 82:
                     case 83:
                     case 84:
                     case 85:
                     case 86:
                     case 87:
                     case 88:
                     case 89:
                     case 90:
                     case 91:
                     case 92:
                     case 93:
                     case 94:
                     case 95:
                     case 96:
                     case 97:
                     case 98:
                     case 99:
                     case 100:
                     case 101:
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
                     case 115:
                     case 116:
                     case 117:
                     case 118:
                     case 119:
                     case 120:
                     case 121:
                     case 122:
                     case 123:
                     case 124:
                     case 125:
                     case 126:
                     case 127:
                     case 128:
                     case 129:
                     case 130:
                     case 131:
                     case 133:
                     case 134:
                     case 135:
                     case 136:
                     case 137:
                     case 138:
                     case 139:
                     case 140:
                     case 141:
                     case 142:
                     case 143:
                     case 144:
                     case 145:
                     case 146:
                     case 147:
                     case 148:
                     case 149:
                     case 150:
                     case 151:
                     case 152:
                     case 153:
                     case 154:
                     case 155:
                     case 156:
                     case 157:
                     case 158:
                     case 159:
                     case 160:
                     case 161:
                     case 162:
                     case 163:
                     case 164:
                     case 165:
                     case 166:
                     case 172:
                     case 173:
                     case 174:
                     case 175:
                     case 176:
                     case 177:
                     case 186:
                     case 190:
                     case 191:
                     case 194:
                     case 195:
                     case 198:
                     case 199:
                     case 240:
                     case 241:
                     case 242:
                     case 243:
                     case 244:
                     case 245:
                     case 246:
                     case 247:
                     case 248:
                     case 249:
                     case 250:
                     case 251:
                     case 252:
                     default:
                        if (!this.endsWithLoad(codePacked) && !this.endsWithStore(codePacked)) {
                           if (this.startsWithIf(codePacked)) {
                              ++bcLabelCount;
                           }
                           break;
                        }

                        ++bcLocalCount;
                        break;
                     case 132:
                        ++bcLocalCount;
                        ++bcByteCount;
                        break;
                     case 167:
                     case 168:
                     case 200:
                     case 201:
                        ++bcLabelCount;
                        break;
                     case 169:
                        ++bcLocalCount;
                        break;
                     case 170:
                        switchIsTableSwitch.add(true);
                        ++bcCaseCountCount;
                        ++bcLabelCount;
                        break;
                     case 171:
                        switchIsTableSwitch.add(false);
                        ++bcCaseCountCount;
                        ++bcLabelCount;
                        break;
                     case 178:
                     case 179:
                     case 180:
                     case 181:
                        ++bcFieldRefCount;
                        break;
                     case 182:
                     case 183:
                     case 184:
                        ++bcMethodRefCount;
                        break;
                     case 185:
                        ++bcIMethodRefCount;
                        break;
                     case 196:
                        int nextInstruction = 255 & this.methodByteCodePacked[c][m][i + 1];
                        this.wideByteCodes.add(nextInstruction);
                        if (nextInstruction == 132) {
                           ++bcLocalCount;
                           ++bcShortCount;
                        } else if (!this.endsWithLoad(nextInstruction) && !this.endsWithStore(nextInstruction) && nextInstruction != 169) {
                           this.segment.log(2, "Found unhandled " + ByteCode.getByteCode(nextInstruction));
                        } else {
                           ++bcLocalCount;
                        }

                        ++i;
                        break;
                     case 197:
                        ++bcByteCount;
                     case 187:
                     case 189:
                     case 192:
                     case 193:
                     case 233:
                     case 236:
                        ++bcClassRefCount;
                        break;
                     case 202:
                     case 203:
                     case 204:
                     case 205:
                     case 209:
                     case 210:
                     case 211:
                     case 212:
                        ++bcThisFieldCount;
                        break;
                     case 206:
                     case 207:
                     case 208:
                     case 213:
                     case 214:
                     case 215:
                        ++bcThisMethodCount;
                        break;
                     case 216:
                     case 217:
                     case 218:
                     case 219:
                     case 223:
                     case 224:
                     case 225:
                     case 226:
                        ++bcSuperFieldCount;
                        break;
                     case 220:
                     case 221:
                     case 222:
                     case 227:
                     case 228:
                     case 229:
                        ++bcSuperMethodCount;
                        break;
                     case 230:
                     case 231:
                     case 232:
                        ++bcInitRefCount;
                        break;
                     case 234:
                     case 237:
                        ++bcIntRefCount;
                        break;
                     case 235:
                     case 238:
                        ++bcFloatRefCount;
                        break;
                     case 239:
                        ++bcDoubleRefCount;
                        break;
                     case 253:
                        ++bcEscRefCount;
                        break;
                     case 254:
                        ++bcEscCount;
                  }
               }
            }
         }
      }

      this.bcCaseCount = this.decodeBandInt("bc_case_count", in, Codec.UNSIGNED5, bcCaseCountCount);
      c = 0;

      for(index = 0; index < this.bcCaseCount.length; ++index) {
         boolean isTableSwitch = (Boolean)switchIsTableSwitch.get(index);
         if (isTableSwitch) {
            ++c;
         } else {
            c += this.bcCaseCount[index];
         }
      }

      this.bcCaseValue = this.decodeBandInt("bc_case_value", in, Codec.DELTA5, c);

      for(index = 0; index < bcCaseCountCount; ++index) {
         bcLabelCount += this.bcCaseCount[index];
      }

      this.bcByte = this.decodeBandInt("bc_byte", in, Codec.BYTE1, bcByteCount);
      this.bcShort = this.decodeBandInt("bc_short", in, Codec.DELTA5, bcShortCount);
      this.bcLocal = this.decodeBandInt("bc_local", in, Codec.UNSIGNED5, bcLocalCount);
      this.bcLabel = this.decodeBandInt("bc_label", in, Codec.BRANCH5, bcLabelCount);
      this.bcIntRef = this.decodeBandInt("bc_intref", in, Codec.DELTA5, bcIntRefCount);
      this.bcFloatRef = this.decodeBandInt("bc_floatref", in, Codec.DELTA5, bcFloatRefCount);
      this.bcLongRef = this.decodeBandInt("bc_longref", in, Codec.DELTA5, bcLongRefCount);
      this.bcDoubleRef = this.decodeBandInt("bc_doubleref", in, Codec.DELTA5, bcDoubleRefCount);
      this.bcStringRef = this.decodeBandInt("bc_stringref", in, Codec.DELTA5, bcStringRefCount);
      this.bcClassRef = this.decodeBandInt("bc_classref", in, Codec.UNSIGNED5, bcClassRefCount);
      this.bcFieldRef = this.decodeBandInt("bc_fieldref", in, Codec.DELTA5, bcFieldRefCount);
      this.bcMethodRef = this.decodeBandInt("bc_methodref", in, Codec.UNSIGNED5, bcMethodRefCount);
      this.bcIMethodRef = this.decodeBandInt("bc_imethodref", in, Codec.DELTA5, bcIMethodRefCount);
      this.bcThisField = this.decodeBandInt("bc_thisfield", in, Codec.UNSIGNED5, bcThisFieldCount);
      this.bcSuperField = this.decodeBandInt("bc_superfield", in, Codec.UNSIGNED5, bcSuperFieldCount);
      this.bcThisMethod = this.decodeBandInt("bc_thismethod", in, Codec.UNSIGNED5, bcThisMethodCount);
      this.bcSuperMethod = this.decodeBandInt("bc_supermethod", in, Codec.UNSIGNED5, bcSuperMethodCount);
      this.bcInitRef = this.decodeBandInt("bc_initref", in, Codec.UNSIGNED5, bcInitRefCount);
      this.bcEscRef = this.decodeBandInt("bc_escref", in, Codec.UNSIGNED5, bcEscRefCount);
      this.bcEscRefSize = this.decodeBandInt("bc_escrefsize", in, Codec.UNSIGNED5, bcEscRefCount);
      this.bcEscSize = this.decodeBandInt("bc_escsize", in, Codec.UNSIGNED5, bcEscCount);
      this.bcEscByte = this.decodeBandInt("bc_escbyte", in, Codec.BYTE1, this.bcEscSize);
   }

   public void unpack() throws Pack200Exception {
      int classCount = this.header.getClassCount();
      long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
      int[] codeMaxNALocals = this.segment.getClassBands().getCodeMaxNALocals();
      int[] codeMaxStack = this.segment.getClassBands().getCodeMaxStack();
      ArrayList[][] methodAttributes = this.segment.getClassBands().getMethodAttributes();
      String[][] methodDescr = this.segment.getClassBands().getMethodDescr();
      AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
      AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
      AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
      AttributeLayout staticModifier = attributeDefinitionMap.getAttributeLayout("ACC_STATIC", 2);
      int[] wideByteCodeArray = new int[this.wideByteCodes.size()];

      for(int index = 0; index < wideByteCodeArray.length; ++index) {
         wideByteCodeArray[index] = (Integer)this.wideByteCodes.get(index);
      }

      OperandManager operandManager = new OperandManager(this.bcCaseCount, this.bcCaseValue, this.bcByte, this.bcShort, this.bcLocal, this.bcLabel, this.bcIntRef, this.bcFloatRef, this.bcLongRef, this.bcDoubleRef, this.bcStringRef, this.bcClassRef, this.bcFieldRef, this.bcMethodRef, this.bcIMethodRef, this.bcThisField, this.bcSuperField, this.bcThisMethod, this.bcSuperMethod, this.bcInitRef, wideByteCodeArray);
      operandManager.setSegment(this.segment);
      int i = 0;
      ArrayList orderedCodeAttributes = this.segment.getClassBands().getOrderedCodeAttributes();
      int codeAttributeIndex = 0;
      int[] handlerCount = this.segment.getClassBands().getCodeHandlerCount();
      int[][] handlerStartPCs = this.segment.getClassBands().getCodeHandlerStartP();
      int[][] handlerEndPCs = this.segment.getClassBands().getCodeHandlerEndPO();
      int[][] handlerCatchPCs = this.segment.getClassBands().getCodeHandlerCatchPO();
      int[][] handlerClassTypes = this.segment.getClassBands().getCodeHandlerClassRCN();
      boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
      boolean[] codeHasFlags = this.segment.getClassBands().getCodeHasAttributes();

      for(int c = 0; c < classCount; ++c) {
         int numberOfMethods = methodFlags[c].length;

         for(int m = 0; m < numberOfMethods; ++m) {
            long methodFlag = methodFlags[c][m];
            if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
               int maxStack = codeMaxStack[i];
               int maxLocal = codeMaxNALocals[i];
               if (!staticModifier.matches(methodFlag)) {
                  ++maxLocal;
               }

               maxLocal += SegmentUtils.countInvokeInterfaceArgs(methodDescr[c][m]);
               String[] cpClass = this.segment.getCpBands().getCpClass();
               operandManager.setCurrentClass(cpClass[this.segment.getClassBands().getClassThisInts()[c]]);
               operandManager.setSuperClass(cpClass[this.segment.getClassBands().getClassSuperInts()[c]]);
               List exceptionTable = new ArrayList();
               if (handlerCount != null) {
                  for(int j = 0; j < handlerCount[i]; ++j) {
                     int handlerClass = handlerClassTypes[i][j] - 1;
                     CPClass cpHandlerClass = null;
                     if (handlerClass != -1) {
                        cpHandlerClass = this.segment.getCpBands().cpClassValue(handlerClass);
                     }

                     ExceptionTableEntry entry = new ExceptionTableEntry(handlerStartPCs[i][j], handlerEndPCs[i][j], handlerCatchPCs[i][j], cpHandlerClass);
                     exceptionTable.add(entry);
                  }
               }

               CodeAttribute codeAttr = new CodeAttribute(maxStack, maxLocal, this.methodByteCodePacked[c][m], this.segment, operandManager, exceptionTable);
               ArrayList methodAttributesList = methodAttributes[c][m];
               int indexForCodeAttr = 0;

               for(int index = 0; index < methodAttributesList.size(); ++index) {
                  Attribute attribute = (Attribute)methodAttributesList.get(index);
                  if (!(attribute instanceof NewAttribute) || ((NewAttribute)attribute).getLayoutIndex() >= 15) {
                     break;
                  }

                  ++indexForCodeAttr;
               }

               methodAttributesList.add(indexForCodeAttr, codeAttr);
               codeAttr.renumber(codeAttr.byteCodeOffsets);
               List currentAttributes;
               if (allCodeHasFlags) {
                  currentAttributes = (List)orderedCodeAttributes.get(i);
               } else if (codeHasFlags[i]) {
                  currentAttributes = (List)orderedCodeAttributes.get(codeAttributeIndex);
                  ++codeAttributeIndex;
               } else {
                  currentAttributes = Collections.EMPTY_LIST;
               }

               for(int index = 0; index < currentAttributes.size(); ++index) {
                  Attribute currentAttribute = (Attribute)currentAttributes.get(index);
                  codeAttr.addAttribute(currentAttribute);
                  if (currentAttribute.hasBCIRenumbering()) {
                     ((BCIRenumberedAttribute)currentAttribute).renumber(codeAttr.byteCodeOffsets);
                  }
               }

               ++i;
            }
         }
      }

   }

   private boolean startsWithIf(int codePacked) {
      return codePacked >= 153 && codePacked <= 166 || codePacked == 198 || codePacked == 199;
   }

   private boolean endsWithLoad(int codePacked) {
      return codePacked >= 21 && codePacked <= 25;
   }

   private boolean endsWithStore(int codePacked) {
      return codePacked >= 54 && codePacked <= 58;
   }

   public byte[][][] getMethodByteCodePacked() {
      return this.methodByteCodePacked;
   }

   public int[] getBcCaseCount() {
      return this.bcCaseCount;
   }

   public int[] getBcCaseValue() {
      return this.bcCaseValue;
   }

   public int[] getBcByte() {
      return this.bcByte;
   }

   public int[] getBcClassRef() {
      return this.bcClassRef;
   }

   public int[] getBcDoubleRef() {
      return this.bcDoubleRef;
   }

   public int[] getBcFieldRef() {
      return this.bcFieldRef;
   }

   public int[] getBcFloatRef() {
      return this.bcFloatRef;
   }

   public int[] getBcIMethodRef() {
      return this.bcIMethodRef;
   }

   public int[] getBcInitRef() {
      return this.bcInitRef;
   }

   public int[] getBcIntRef() {
      return this.bcIntRef;
   }

   public int[] getBcLabel() {
      return this.bcLabel;
   }

   public int[] getBcLocal() {
      return this.bcLocal;
   }

   public int[] getBcLongRef() {
      return this.bcLongRef;
   }

   public int[] getBcMethodRef() {
      return this.bcMethodRef;
   }

   public int[] getBcShort() {
      return this.bcShort;
   }

   public int[] getBcStringRef() {
      return this.bcStringRef;
   }

   public int[] getBcSuperField() {
      return this.bcSuperField;
   }

   public int[] getBcSuperMethod() {
      return this.bcSuperMethod;
   }

   public int[] getBcThisField() {
      return this.bcThisField;
   }

   public int[] getBcThisMethod() {
      return this.bcThisMethod;
   }
}
