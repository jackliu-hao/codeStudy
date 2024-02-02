package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.harmony.unpack200.Segment;

public class CodeAttribute extends BCIRenumberedAttribute {
   public List attributes = new ArrayList();
   public List byteCodeOffsets = new ArrayList();
   public List byteCodes = new ArrayList();
   public int codeLength;
   public List exceptionTable;
   public int maxLocals;
   public int maxStack;
   private static CPUTF8 attributeName;

   public CodeAttribute(int maxStack, int maxLocals, byte[] codePacked, Segment segment, OperandManager operandManager, List exceptionTable) {
      super(attributeName);
      this.maxLocals = maxLocals;
      this.maxStack = maxStack;
      this.codeLength = 0;
      this.exceptionTable = exceptionTable;
      this.byteCodeOffsets.add(0);
      int byteCodeIndex = 0;

      int i;
      ByteCode byteCode;
      for(i = 0; i < codePacked.length; ++i) {
         byteCode = ByteCode.getByteCode(codePacked[i] & 255);
         byteCode.setByteCodeIndex(byteCodeIndex);
         ++byteCodeIndex;
         byteCode.extractOperands(operandManager, segment, this.codeLength);
         this.byteCodes.add(byteCode);
         this.codeLength += byteCode.getLength();
         int lastBytecodePosition = (Integer)this.byteCodeOffsets.get(this.byteCodeOffsets.size() - 1);
         if (byteCode.hasMultipleByteCodes()) {
            this.byteCodeOffsets.add(lastBytecodePosition + 1);
            ++byteCodeIndex;
         }

         if (i < codePacked.length - 1) {
            this.byteCodeOffsets.add(lastBytecodePosition + byteCode.getLength());
         }

         if (byteCode.getOpcode() == 196) {
            ++i;
         }
      }

      for(i = 0; i < this.byteCodes.size(); ++i) {
         byteCode = (ByteCode)this.byteCodes.get(i);
         byteCode.applyByteCodeTargetFixup(this);
      }

   }

   protected int getLength() {
      int attributesSize = 0;

      for(int it = 0; it < this.attributes.size(); ++it) {
         Attribute attribute = (Attribute)this.attributes.get(it);
         attributesSize += attribute.getLengthIncludingHeader();
      }

      return 8 + this.codeLength + 2 + this.exceptionTable.size() * 8 + 2 + attributesSize;
   }

   protected ClassFileEntry[] getNestedClassFileEntries() {
      ArrayList nestedEntries = new ArrayList(this.attributes.size() + this.byteCodes.size() + 10);
      nestedEntries.add(this.getAttributeName());
      nestedEntries.addAll(this.byteCodes);
      nestedEntries.addAll(this.attributes);

      for(int iter = 0; iter < this.exceptionTable.size(); ++iter) {
         ExceptionTableEntry entry = (ExceptionTableEntry)this.exceptionTable.get(iter);
         CPClass catchType = entry.getCatchType();
         if (catchType != null) {
            nestedEntries.add(catchType);
         }
      }

      ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
      nestedEntries.toArray(nestedEntryArray);
      return nestedEntryArray;
   }

   protected void resolve(ClassConstantPool pool) {
      super.resolve(pool);

      int it;
      for(it = 0; it < this.attributes.size(); ++it) {
         Attribute attribute = (Attribute)this.attributes.get(it);
         attribute.resolve(pool);
      }

      for(it = 0; it < this.byteCodes.size(); ++it) {
         ByteCode byteCode = (ByteCode)this.byteCodes.get(it);
         byteCode.resolve(pool);
      }

      for(it = 0; it < this.exceptionTable.size(); ++it) {
         ExceptionTableEntry entry = (ExceptionTableEntry)this.exceptionTable.get(it);
         entry.resolve(pool);
      }

   }

   public String toString() {
      return "Code: " + this.getLength() + " bytes";
   }

   protected void writeBody(DataOutputStream dos) throws IOException {
      dos.writeShort(this.maxStack);
      dos.writeShort(this.maxLocals);
      dos.writeInt(this.codeLength);

      int it;
      for(it = 0; it < this.byteCodes.size(); ++it) {
         ByteCode byteCode = (ByteCode)this.byteCodes.get(it);
         byteCode.write(dos);
      }

      dos.writeShort(this.exceptionTable.size());

      for(it = 0; it < this.exceptionTable.size(); ++it) {
         ExceptionTableEntry entry = (ExceptionTableEntry)this.exceptionTable.get(it);
         entry.write(dos);
      }

      dos.writeShort(this.attributes.size());

      for(it = 0; it < this.attributes.size(); ++it) {
         Attribute attribute = (Attribute)this.attributes.get(it);
         attribute.write(dos);
      }

   }

   public void addAttribute(Attribute attribute) {
      this.attributes.add(attribute);
      if (attribute instanceof LocalVariableTableAttribute) {
         ((LocalVariableTableAttribute)attribute).setCodeLength(this.codeLength);
      }

      if (attribute instanceof LocalVariableTypeTableAttribute) {
         ((LocalVariableTypeTableAttribute)attribute).setCodeLength(this.codeLength);
      }

   }

   protected int[] getStartPCs() {
      return null;
   }

   public void renumber(List byteCodeOffsets) {
      for(int iter = 0; iter < this.exceptionTable.size(); ++iter) {
         ExceptionTableEntry entry = (ExceptionTableEntry)this.exceptionTable.get(iter);
         entry.renumber(byteCodeOffsets);
      }

   }

   public static void setAttributeName(CPUTF8 attributeName) {
      CodeAttribute.attributeName = attributeName;
   }
}
