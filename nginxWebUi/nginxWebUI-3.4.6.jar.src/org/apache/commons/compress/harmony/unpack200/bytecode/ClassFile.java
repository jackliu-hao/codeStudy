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
/*    */ public class ClassFile
/*    */ {
/*    */   public int major;
/*    */   public int minor;
/* 29 */   private final int magic = -889275714;
/* 30 */   public ClassConstantPool pool = new ClassConstantPool();
/*    */   public int accessFlags;
/*    */   public int thisClass;
/*    */   public int superClass;
/*    */   public int[] interfaces;
/*    */   public ClassFileEntry[] fields;
/*    */   public ClassFileEntry[] methods;
/*    */   public Attribute[] attributes;
/*    */   
/*    */   public void write(DataOutputStream dos) throws IOException {
/* 40 */     dos.writeInt(-889275714);
/* 41 */     dos.writeShort(this.minor);
/* 42 */     dos.writeShort(this.major);
/* 43 */     dos.writeShort(this.pool.size() + 1); int i;
/* 44 */     for (i = 1; i <= this.pool.size(); i++) {
/*    */       ConstantPoolEntry entry;
/* 46 */       (entry = (ConstantPoolEntry)this.pool.get(i)).doWrite(dos);
/*    */ 
/*    */       
/* 49 */       if (entry.getTag() == 6 || entry.getTag() == 5) {
/* 50 */         i++;
/*    */       }
/*    */     } 
/* 53 */     dos.writeShort(this.accessFlags);
/* 54 */     dos.writeShort(this.thisClass);
/* 55 */     dos.writeShort(this.superClass);
/* 56 */     dos.writeShort(this.interfaces.length);
/* 57 */     for (i = 0; i < this.interfaces.length; i++) {
/* 58 */       dos.writeShort(this.interfaces[i]);
/*    */     }
/* 60 */     dos.writeShort(this.fields.length);
/* 61 */     for (i = 0; i < this.fields.length; i++) {
/* 62 */       this.fields[i].write(dos);
/*    */     }
/* 64 */     dos.writeShort(this.methods.length);
/* 65 */     for (i = 0; i < this.methods.length; i++) {
/* 66 */       this.methods[i].write(dos);
/*    */     }
/* 68 */     dos.writeShort(this.attributes.length);
/* 69 */     for (i = 0; i < this.attributes.length; i++)
/* 70 */       this.attributes[i].write(dos); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ClassFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */