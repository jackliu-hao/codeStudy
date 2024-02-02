/*     */ package org.objectweb.asm;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Attribute
/*     */ {
/*     */   public final String type;
/*     */   private byte[] content;
/*     */   Attribute nextAttribute;
/*     */   
/*     */   protected Attribute(String type) {
/*  65 */     this.type = type;
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
/*     */ 
/*     */   
/*     */   public boolean isUnknown() {
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCodeAttribute() {
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Label[] getLabels() {
/*  98 */     return new Label[0];
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
/*     */   protected Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
/* 128 */     Attribute attribute = new Attribute(this.type);
/* 129 */     attribute.content = new byte[length];
/* 130 */     System.arraycopy(classReader.classFileBuffer, offset, attribute.content, 0, length);
/* 131 */     return attribute;
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
/*     */   protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
/* 159 */     return new ByteVector(this.content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int getAttributeCount() {
/* 168 */     int count = 0;
/* 169 */     Attribute attribute = this;
/* 170 */     while (attribute != null) {
/* 171 */       count++;
/* 172 */       attribute = attribute.nextAttribute;
/*     */     } 
/* 174 */     return count;
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
/*     */   
/*     */   final int computeAttributesSize(SymbolTable symbolTable) {
/* 187 */     byte[] code = null;
/* 188 */     int codeLength = 0;
/* 189 */     int maxStack = -1;
/* 190 */     int maxLocals = -1;
/* 191 */     return computeAttributesSize(symbolTable, code, 0, -1, -1);
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
/*     */   final int computeAttributesSize(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals) {
/* 219 */     ClassWriter classWriter = symbolTable.classWriter;
/* 220 */     int size = 0;
/* 221 */     Attribute attribute = this;
/* 222 */     while (attribute != null) {
/* 223 */       symbolTable.addConstantUtf8(attribute.type);
/* 224 */       size += 6 + (attribute.write(classWriter, code, codeLength, maxStack, maxLocals)).length;
/* 225 */       attribute = attribute.nextAttribute;
/*     */     } 
/* 227 */     return size;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int computeAttributesSize(SymbolTable symbolTable, int accessFlags, int signatureIndex) {
/* 244 */     int size = 0;
/*     */     
/* 246 */     if ((accessFlags & 0x1000) != 0 && symbolTable
/* 247 */       .getMajorVersion() < 49) {
/*     */       
/* 249 */       symbolTable.addConstantUtf8("Synthetic");
/* 250 */       size += 6;
/*     */     } 
/* 252 */     if (signatureIndex != 0) {
/*     */       
/* 254 */       symbolTable.addConstantUtf8("Signature");
/* 255 */       size += 8;
/*     */     } 
/*     */     
/* 258 */     if ((accessFlags & 0x20000) != 0) {
/*     */       
/* 260 */       symbolTable.addConstantUtf8("Deprecated");
/* 261 */       size += 6;
/*     */     } 
/* 263 */     return size;
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
/*     */   final void putAttributes(SymbolTable symbolTable, ByteVector output) {
/* 275 */     byte[] code = null;
/* 276 */     int codeLength = 0;
/* 277 */     int maxStack = -1;
/* 278 */     int maxLocals = -1;
/* 279 */     putAttributes(symbolTable, code, 0, -1, -1, output);
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
/*     */   final void putAttributes(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals, ByteVector output) {
/* 307 */     ClassWriter classWriter = symbolTable.classWriter;
/* 308 */     Attribute attribute = this;
/* 309 */     while (attribute != null) {
/*     */       
/* 311 */       ByteVector attributeContent = attribute.write(classWriter, code, codeLength, maxStack, maxLocals);
/*     */       
/* 313 */       output.putShort(symbolTable.addConstantUtf8(attribute.type)).putInt(attributeContent.length);
/* 314 */       output.putByteArray(attributeContent.data, 0, attributeContent.length);
/* 315 */       attribute = attribute.nextAttribute;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void putAttributes(SymbolTable symbolTable, int accessFlags, int signatureIndex, ByteVector output) {
/* 335 */     if ((accessFlags & 0x1000) != 0 && symbolTable
/* 336 */       .getMajorVersion() < 49) {
/* 337 */       output.putShort(symbolTable.addConstantUtf8("Synthetic")).putInt(0);
/*     */     }
/* 339 */     if (signatureIndex != 0) {
/* 340 */       output
/* 341 */         .putShort(symbolTable.addConstantUtf8("Signature"))
/* 342 */         .putInt(2)
/* 343 */         .putShort(signatureIndex);
/*     */     }
/* 345 */     if ((accessFlags & 0x20000) != 0) {
/* 346 */       output.putShort(symbolTable.addConstantUtf8("Deprecated")).putInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Set
/*     */   {
/*     */     private static final int SIZE_INCREMENT = 6;
/*     */     
/*     */     private int size;
/* 356 */     private Attribute[] data = new Attribute[6];
/*     */     
/*     */     void addAttributes(Attribute attributeList) {
/* 359 */       Attribute attribute = attributeList;
/* 360 */       while (attribute != null) {
/* 361 */         if (!contains(attribute)) {
/* 362 */           add(attribute);
/*     */         }
/* 364 */         attribute = attribute.nextAttribute;
/*     */       } 
/*     */     }
/*     */     
/*     */     Attribute[] toArray() {
/* 369 */       Attribute[] result = new Attribute[this.size];
/* 370 */       System.arraycopy(this.data, 0, result, 0, this.size);
/* 371 */       return result;
/*     */     }
/*     */     
/*     */     private boolean contains(Attribute attribute) {
/* 375 */       for (int i = 0; i < this.size; i++) {
/* 376 */         if ((this.data[i]).type.equals(attribute.type)) {
/* 377 */           return true;
/*     */         }
/*     */       } 
/* 380 */       return false;
/*     */     }
/*     */     
/*     */     private void add(Attribute attribute) {
/* 384 */       if (this.size >= this.data.length) {
/* 385 */         Attribute[] newData = new Attribute[this.data.length + 6];
/* 386 */         System.arraycopy(this.data, 0, newData, 0, this.size);
/* 387 */         this.data = newData;
/*     */       } 
/* 389 */       this.data[this.size++] = attribute;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\Attribute.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */