/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.compress.harmony.unpack200.Segment;
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
/*     */ public class CodeAttribute
/*     */   extends BCIRenumberedAttribute
/*     */ {
/*  28 */   public List attributes = new ArrayList();
/*     */   
/*  30 */   public List byteCodeOffsets = new ArrayList();
/*  31 */   public List byteCodes = new ArrayList();
/*     */   
/*     */   public int codeLength;
/*     */   public List exceptionTable;
/*     */   public int maxLocals;
/*     */   public int maxStack;
/*     */   private static CPUTF8 attributeName;
/*     */   
/*     */   public CodeAttribute(int maxStack, int maxLocals, byte[] codePacked, Segment segment, OperandManager operandManager, List exceptionTable) {
/*  40 */     super(attributeName);
/*  41 */     this.maxLocals = maxLocals;
/*  42 */     this.maxStack = maxStack;
/*  43 */     this.codeLength = 0;
/*  44 */     this.exceptionTable = exceptionTable;
/*  45 */     this.byteCodeOffsets.add(Integer.valueOf(0));
/*  46 */     int byteCodeIndex = 0; int i;
/*  47 */     for (i = 0; i < codePacked.length; i++) {
/*  48 */       ByteCode byteCode = ByteCode.getByteCode(codePacked[i] & 0xFF);
/*     */ 
/*     */       
/*  51 */       byteCode.setByteCodeIndex(byteCodeIndex);
/*  52 */       byteCodeIndex++;
/*  53 */       byteCode.extractOperands(operandManager, segment, this.codeLength);
/*  54 */       this.byteCodes.add(byteCode);
/*  55 */       this.codeLength += byteCode.getLength();
/*  56 */       int lastBytecodePosition = ((Integer)this.byteCodeOffsets.get(this.byteCodeOffsets.size() - 1)).intValue();
/*     */ 
/*     */ 
/*     */       
/*  60 */       if (byteCode.hasMultipleByteCodes()) {
/*  61 */         this.byteCodeOffsets.add(Integer.valueOf(lastBytecodePosition + 1));
/*  62 */         byteCodeIndex++;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  67 */       if (i < codePacked.length - 1) {
/*  68 */         this.byteCodeOffsets.add(Integer.valueOf(lastBytecodePosition + byteCode.getLength()));
/*     */       }
/*  70 */       if (byteCode.getOpcode() == 196)
/*     */       {
/*     */ 
/*     */         
/*  74 */         i++;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     for (i = 0; i < this.byteCodes.size(); i++) {
/*  82 */       ByteCode byteCode = this.byteCodes.get(i);
/*  83 */       byteCode.applyByteCodeTargetFixup(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  89 */     int attributesSize = 0;
/*  90 */     for (int it = 0; it < this.attributes.size(); it++) {
/*  91 */       Attribute attribute = this.attributes.get(it);
/*  92 */       attributesSize += attribute.getLengthIncludingHeader();
/*     */     } 
/*  94 */     return 8 + this.codeLength + 2 + this.exceptionTable.size() * 8 + 2 + attributesSize;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  99 */     ArrayList<CPUTF8> nestedEntries = new ArrayList(this.attributes.size() + this.byteCodes.size() + 10);
/* 100 */     nestedEntries.add(getAttributeName());
/* 101 */     nestedEntries.addAll(this.byteCodes);
/* 102 */     nestedEntries.addAll(this.attributes);
/*     */     
/* 104 */     for (int iter = 0; iter < this.exceptionTable.size(); iter++) {
/* 105 */       ExceptionTableEntry entry = this.exceptionTable.get(iter);
/* 106 */       CPClass catchType = entry.getCatchType();
/*     */ 
/*     */ 
/*     */       
/* 110 */       if (catchType != null) {
/* 111 */         nestedEntries.add(catchType);
/*     */       }
/*     */     } 
/* 114 */     ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
/* 115 */     nestedEntries.toArray(nestedEntryArray);
/* 116 */     return nestedEntryArray;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/* 121 */     super.resolve(pool); int it;
/* 122 */     for (it = 0; it < this.attributes.size(); it++) {
/* 123 */       Attribute attribute = this.attributes.get(it);
/* 124 */       attribute.resolve(pool);
/*     */     } 
/*     */     
/* 127 */     for (it = 0; it < this.byteCodes.size(); it++) {
/* 128 */       ByteCode byteCode = this.byteCodes.get(it);
/* 129 */       byteCode.resolve(pool);
/*     */     } 
/*     */     
/* 132 */     for (it = 0; it < this.exceptionTable.size(); it++) {
/* 133 */       ExceptionTableEntry entry = this.exceptionTable.get(it);
/* 134 */       entry.resolve(pool);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return "Code: " + getLength() + " bytes";
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 145 */     dos.writeShort(this.maxStack);
/* 146 */     dos.writeShort(this.maxLocals);
/*     */     
/* 148 */     dos.writeInt(this.codeLength); int it;
/* 149 */     for (it = 0; it < this.byteCodes.size(); it++) {
/* 150 */       ByteCode byteCode = this.byteCodes.get(it);
/* 151 */       byteCode.write(dos);
/*     */     } 
/*     */     
/* 154 */     dos.writeShort(this.exceptionTable.size());
/* 155 */     for (it = 0; it < this.exceptionTable.size(); it++) {
/* 156 */       ExceptionTableEntry entry = this.exceptionTable.get(it);
/* 157 */       entry.write(dos);
/*     */     } 
/*     */     
/* 160 */     dos.writeShort(this.attributes.size());
/* 161 */     for (it = 0; it < this.attributes.size(); it++) {
/* 162 */       Attribute attribute = this.attributes.get(it);
/* 163 */       attribute.write(dos);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addAttribute(Attribute attribute) {
/* 168 */     this.attributes.add(attribute);
/* 169 */     if (attribute instanceof LocalVariableTableAttribute) {
/* 170 */       ((LocalVariableTableAttribute)attribute).setCodeLength(this.codeLength);
/*     */     }
/* 172 */     if (attribute instanceof LocalVariableTypeTableAttribute) {
/* 173 */       ((LocalVariableTypeTableAttribute)attribute).setCodeLength(this.codeLength);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] getStartPCs() {
/* 180 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void renumber(List byteCodeOffsets) {
/* 185 */     for (int iter = 0; iter < this.exceptionTable.size(); iter++) {
/* 186 */       ExceptionTableEntry entry = this.exceptionTable.get(iter);
/* 187 */       entry.renumber(byteCodeOffsets);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setAttributeName(CPUTF8 attributeName) {
/* 192 */     CodeAttribute.attributeName = attributeName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\CodeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */