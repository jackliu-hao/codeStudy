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
/*    */ public abstract class ConstantPoolEntry
/*    */   extends ClassFileEntry
/*    */ {
/*    */   public static final byte CP_Class = 7;
/*    */   public static final byte CP_Double = 6;
/*    */   public static final byte CP_Fieldref = 9;
/*    */   public static final byte CP_Float = 4;
/*    */   public static final byte CP_Integer = 3;
/*    */   public static final byte CP_InterfaceMethodref = 11;
/*    */   public static final byte CP_Long = 5;
/*    */   public static final byte CP_Methodref = 10;
/*    */   public static final byte CP_NameAndType = 12;
/*    */   public static final byte CP_String = 8;
/*    */   public static final byte CP_UTF8 = 1;
/*    */   byte tag;
/*    */   protected int globalIndex;
/*    */   
/*    */   ConstantPoolEntry(byte tag, int globalIndex) {
/* 59 */     this.tag = tag;
/* 60 */     this.globalIndex = globalIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   public byte getTag() {
/* 67 */     return this.tag;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract int hashCode();
/*    */ 
/*    */   
/*    */   public void doWrite(DataOutputStream dos) throws IOException {
/* 75 */     dos.writeByte(this.tag);
/* 76 */     writeBody(dos);
/*    */   }
/*    */   
/*    */   protected abstract void writeBody(DataOutputStream paramDataOutputStream) throws IOException;
/*    */   
/*    */   public int getGlobalIndex() {
/* 82 */     return this.globalIndex;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ConstantPoolEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */