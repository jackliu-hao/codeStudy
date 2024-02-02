/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
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
/*     */ public class LocalVariableTypeTableAttribute
/*     */   extends BCIRenumberedAttribute
/*     */ {
/*     */   private final int local_variable_type_table_length;
/*     */   private final int[] start_pcs;
/*     */   private final int[] lengths;
/*     */   private int[] name_indexes;
/*     */   private int[] signature_indexes;
/*     */   private final int[] indexes;
/*     */   private final CPUTF8[] names;
/*     */   private final CPUTF8[] signatures;
/*     */   private int codeLength;
/*     */   private static CPUTF8 attributeName;
/*     */   
/*     */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/*  43 */     attributeName = cpUTF8Value;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalVariableTypeTableAttribute(int local_variable_type_table_length, int[] start_pcs, int[] lengths, CPUTF8[] names, CPUTF8[] signatures, int[] indexes) {
/*  48 */     super(attributeName);
/*  49 */     this.local_variable_type_table_length = local_variable_type_table_length;
/*  50 */     this.start_pcs = start_pcs;
/*  51 */     this.lengths = lengths;
/*  52 */     this.names = names;
/*  53 */     this.signatures = signatures;
/*  54 */     this.indexes = indexes;
/*     */   }
/*     */   
/*     */   public void setCodeLength(int length) {
/*  58 */     this.codeLength = length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  63 */     return 2 + 10 * this.local_variable_type_table_length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  68 */     dos.writeShort(this.local_variable_type_table_length);
/*  69 */     for (int i = 0; i < this.local_variable_type_table_length; i++) {
/*  70 */       dos.writeShort(this.start_pcs[i]);
/*  71 */       dos.writeShort(this.lengths[i]);
/*  72 */       dos.writeShort(this.name_indexes[i]);
/*  73 */       dos.writeShort(this.signature_indexes[i]);
/*  74 */       dos.writeShort(this.indexes[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  80 */     super.resolve(pool);
/*  81 */     this.name_indexes = new int[this.local_variable_type_table_length];
/*  82 */     this.signature_indexes = new int[this.local_variable_type_table_length];
/*  83 */     for (int i = 0; i < this.local_variable_type_table_length; i++) {
/*  84 */       this.names[i].resolve(pool);
/*  85 */       this.signatures[i].resolve(pool);
/*  86 */       this.name_indexes[i] = pool.indexOf(this.names[i]);
/*  87 */       this.signature_indexes[i] = pool.indexOf(this.signatures[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  93 */     ArrayList<CPUTF8> nestedEntries = new ArrayList();
/*  94 */     nestedEntries.add(getAttributeName());
/*  95 */     for (int i = 0; i < this.local_variable_type_table_length; i++) {
/*  96 */       nestedEntries.add(this.names[i]);
/*  97 */       nestedEntries.add(this.signatures[i]);
/*     */     } 
/*  99 */     ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
/* 100 */     nestedEntries.toArray(nestedEntryArray);
/* 101 */     return nestedEntryArray;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] getStartPCs() {
/* 106 */     return this.start_pcs;
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
/*     */   public void renumber(List<Integer> byteCodeOffsets) throws Pack200Exception {
/* 118 */     int[] unrenumbered_start_pcs = new int[this.start_pcs.length];
/* 119 */     System.arraycopy(this.start_pcs, 0, unrenumbered_start_pcs, 0, this.start_pcs.length);
/*     */ 
/*     */     
/* 122 */     super.renumber(byteCodeOffsets);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     int maxSize = this.codeLength;
/*     */ 
/*     */ 
/*     */     
/* 136 */     for (int index = 0; index < this.lengths.length; index++) {
/* 137 */       int start_pc = this.start_pcs[index];
/* 138 */       int revisedLength = -1;
/* 139 */       int encodedLength = this.lengths[index];
/*     */ 
/*     */       
/* 142 */       int indexOfStartPC = unrenumbered_start_pcs[index];
/*     */ 
/*     */       
/* 145 */       int stopIndex = indexOfStartPC + encodedLength;
/* 146 */       if (stopIndex < 0) {
/* 147 */         throw new Pack200Exception("Error renumbering bytecode indexes");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 152 */       if (stopIndex == byteCodeOffsets.size()) {
/*     */         
/* 154 */         revisedLength = maxSize - start_pc;
/*     */       } else {
/*     */         
/* 157 */         int stopValue = ((Integer)byteCodeOffsets.get(stopIndex)).intValue();
/* 158 */         revisedLength = stopValue - start_pc;
/*     */       } 
/* 160 */       this.lengths[index] = revisedLength;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 166 */     return "LocalVariableTypeTable: " + this.local_variable_type_table_length + " varaibles";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\LocalVariableTypeTableAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */