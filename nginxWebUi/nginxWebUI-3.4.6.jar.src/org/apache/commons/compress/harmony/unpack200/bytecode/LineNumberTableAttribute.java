/*    */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LineNumberTableAttribute
/*    */   extends BCIRenumberedAttribute
/*    */ {
/*    */   private final int line_number_table_length;
/*    */   private final int[] start_pcs;
/*    */   private final int[] line_numbers;
/*    */   private static CPUTF8 attributeName;
/*    */   
/*    */   public static void setAttributeName(CPUTF8 cpUTF8Value) {
/* 33 */     attributeName = cpUTF8Value;
/*    */   }
/*    */ 
/*    */   
/*    */   public LineNumberTableAttribute(int line_number_table_length, int[] start_pcs, int[] line_numbers) {
/* 38 */     super(attributeName);
/* 39 */     this.line_number_table_length = line_number_table_length;
/* 40 */     this.start_pcs = start_pcs;
/* 41 */     this.line_numbers = line_numbers;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int getLength() {
/* 46 */     return 2 + 4 * this.line_number_table_length;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBody(DataOutputStream dos) throws IOException {
/* 51 */     dos.writeShort(this.line_number_table_length);
/* 52 */     for (int i = 0; i < this.line_number_table_length; i++) {
/* 53 */       dos.writeShort(this.start_pcs[i]);
/* 54 */       dos.writeShort(this.line_numbers[i]);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "LineNumberTable: " + this.line_number_table_length + " lines";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ClassFileEntry[] getNestedClassFileEntries() {
/* 75 */     return new ClassFileEntry[] { getAttributeName() };
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 80 */     return (this == obj);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void resolve(ClassConstantPool pool) {
/* 92 */     super.resolve(pool);
/*    */   }
/*    */ 
/*    */   
/*    */   protected int[] getStartPCs() {
/* 97 */     return this.start_pcs;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\LineNumberTableAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */