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
/*     */ public class LocalVariableTableAttribute
/*     */   extends BCIRenumberedAttribute
/*     */ {
/*     */   private final int local_variable_table_length;
/*     */   private final int[] start_pcs;
/*     */   private final int[] lengths;
/*     */   private int[] name_indexes;
/*     */   private int[] descriptor_indexes;
/*     */   private final int[] indexes;
/*     */   private final CPUTF8[] names;
/*     */   private final CPUTF8[] descriptors;
/*     */   private int codeLength;
/*     */   private static CPUTF8 attributeName;
/*     */   
/*     */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/*  43 */     attributeName = cpUTF8Value;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalVariableTableAttribute(int local_variable_table_length, int[] start_pcs, int[] lengths, CPUTF8[] names, CPUTF8[] descriptors, int[] indexes) {
/*  48 */     super(attributeName);
/*  49 */     this.local_variable_table_length = local_variable_table_length;
/*  50 */     this.start_pcs = start_pcs;
/*  51 */     this.lengths = lengths;
/*  52 */     this.names = names;
/*  53 */     this.descriptors = descriptors;
/*  54 */     this.indexes = indexes;
/*     */   }
/*     */   
/*     */   public void setCodeLength(int length) {
/*  58 */     this.codeLength = length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getLength() {
/*  63 */     return 2 + 10 * this.local_variable_table_length;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBody(DataOutputStream dos) throws IOException {
/*  68 */     dos.writeShort(this.local_variable_table_length);
/*  69 */     for (int i = 0; i < this.local_variable_table_length; i++) {
/*  70 */       dos.writeShort(this.start_pcs[i]);
/*  71 */       dos.writeShort(this.lengths[i]);
/*  72 */       dos.writeShort(this.name_indexes[i]);
/*  73 */       dos.writeShort(this.descriptor_indexes[i]);
/*  74 */       dos.writeShort(this.indexes[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClassFileEntry[] getNestedClassFileEntries() {
/*  80 */     ArrayList<CPUTF8> nestedEntries = new ArrayList();
/*  81 */     nestedEntries.add(getAttributeName());
/*  82 */     for (int i = 0; i < this.local_variable_table_length; i++) {
/*  83 */       nestedEntries.add(this.names[i]);
/*  84 */       nestedEntries.add(this.descriptors[i]);
/*     */     } 
/*  86 */     ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
/*  87 */     nestedEntries.toArray(nestedEntryArray);
/*  88 */     return nestedEntryArray;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resolve(ClassConstantPool pool) {
/*  93 */     super.resolve(pool);
/*  94 */     this.name_indexes = new int[this.local_variable_table_length];
/*  95 */     this.descriptor_indexes = new int[this.local_variable_table_length];
/*  96 */     for (int i = 0; i < this.local_variable_table_length; i++) {
/*  97 */       this.names[i].resolve(pool);
/*  98 */       this.descriptors[i].resolve(pool);
/*  99 */       this.name_indexes[i] = pool.indexOf(this.names[i]);
/* 100 */       this.descriptor_indexes[i] = pool.indexOf(this.descriptors[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     return "LocalVariableTable: " + this.local_variable_table_length + " variables";
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] getStartPCs() {
/* 111 */     return this.start_pcs;
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
/* 123 */     int[] unrenumbered_start_pcs = new int[this.start_pcs.length];
/* 124 */     System.arraycopy(this.start_pcs, 0, unrenumbered_start_pcs, 0, this.start_pcs.length);
/*     */ 
/*     */     
/* 127 */     super.renumber(byteCodeOffsets);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     int maxSize = this.codeLength;
/*     */ 
/*     */ 
/*     */     
/* 141 */     for (int index = 0; index < this.lengths.length; index++) {
/* 142 */       int start_pc = this.start_pcs[index];
/* 143 */       int revisedLength = -1;
/* 144 */       int encodedLength = this.lengths[index];
/*     */ 
/*     */       
/* 147 */       int indexOfStartPC = unrenumbered_start_pcs[index];
/*     */ 
/*     */       
/* 150 */       int stopIndex = indexOfStartPC + encodedLength;
/* 151 */       if (stopIndex < 0) {
/* 152 */         throw new Pack200Exception("Error renumbering bytecode indexes");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 157 */       if (stopIndex == byteCodeOffsets.size()) {
/*     */         
/* 159 */         revisedLength = maxSize - start_pc;
/*     */       } else {
/*     */         
/* 162 */         int stopValue = ((Integer)byteCodeOffsets.get(stopIndex)).intValue();
/* 163 */         revisedLength = stopValue - start_pc;
/*     */       } 
/* 165 */       this.lengths[index] = revisedLength;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\LocalVariableTableAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */