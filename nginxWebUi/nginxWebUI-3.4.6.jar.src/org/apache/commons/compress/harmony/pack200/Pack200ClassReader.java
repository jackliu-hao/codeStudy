/*    */ package org.apache.commons.compress.harmony.pack200;
/*    */ 
/*    */ import org.objectweb.asm.ClassReader;
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
/*    */ public class Pack200ClassReader
/*    */   extends ClassReader
/*    */ {
/*    */   private boolean lastConstantHadWideIndex;
/*    */   private int lastUnsignedShort;
/*    */   private boolean anySyntheticAttributes;
/*    */   private String fileName;
/*    */   
/*    */   public Pack200ClassReader(byte[] b) {
/* 35 */     super(b);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int readUnsignedShort(int index) {
/* 42 */     int unsignedShort = super.readUnsignedShort(index);
/* 43 */     if (this.b[index - 1] == 19) {
/* 44 */       this.lastUnsignedShort = unsignedShort;
/*    */     } else {
/* 46 */       this.lastUnsignedShort = -32768;
/*    */     } 
/* 48 */     return unsignedShort;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object readConst(int item, char[] buf) {
/* 53 */     this.lastConstantHadWideIndex = (item == this.lastUnsignedShort);
/* 54 */     return super.readConst(item, buf);
/*    */   }
/*    */ 
/*    */   
/*    */   public String readUTF8(int arg0, char[] arg1) {
/* 59 */     String utf8 = super.readUTF8(arg0, arg1);
/* 60 */     if (!this.anySyntheticAttributes && "Synthetic".equals(utf8)) {
/* 61 */       this.anySyntheticAttributes = true;
/*    */     }
/* 63 */     return utf8;
/*    */   }
/*    */   
/*    */   public boolean lastConstantHadWideIndex() {
/* 67 */     return this.lastConstantHadWideIndex;
/*    */   }
/*    */   
/*    */   public boolean hasSyntheticAttributes() {
/* 71 */     return this.anySyntheticAttributes;
/*    */   }
/*    */   
/*    */   public void setFileName(String name) {
/* 75 */     this.fileName = name;
/*    */   }
/*    */   
/*    */   public String getFileName() {
/* 79 */     return this.fileName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Pack200ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */