/*    */ package org.h2.value;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.text.CollationKey;
/*    */ import java.text.Collator;
/*    */ import java.util.Comparator;
/*    */ import java.util.Locale;
/*    */ import org.h2.util.Bits;
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
/*    */ public class CharsetCollator
/*    */   extends Collator
/*    */ {
/* 24 */   static final Comparator<byte[]> COMPARATOR = Bits::compareNotNullSigned;
/*    */   
/*    */   private final Charset charset;
/*    */   
/*    */   public CharsetCollator(Charset paramCharset) {
/* 29 */     this.charset = paramCharset;
/*    */   }
/*    */   
/*    */   public Charset getCharset() {
/* 33 */     return this.charset;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(String paramString1, String paramString2) {
/* 38 */     return COMPARATOR.compare(toBytes(paramString1), toBytes(paramString2));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   byte[] toBytes(String paramString) {
/* 48 */     if (getStrength() <= 1)
/*    */     {
/* 50 */       paramString = paramString.toUpperCase(Locale.ROOT);
/*    */     }
/* 52 */     return paramString.getBytes(this.charset);
/*    */   }
/*    */ 
/*    */   
/*    */   public CollationKey getCollationKey(String paramString) {
/* 57 */     return new CharsetCollationKey(paramString);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return 255;
/*    */   }
/*    */   
/*    */   private class CharsetCollationKey
/*    */     extends CollationKey {
/*    */     private final byte[] bytes;
/*    */     
/*    */     CharsetCollationKey(String param1String) {
/* 70 */       super(param1String);
/* 71 */       this.bytes = CharsetCollator.this.toBytes(param1String);
/*    */     }
/*    */ 
/*    */     
/*    */     public int compareTo(CollationKey param1CollationKey) {
/* 76 */       return CharsetCollator.COMPARATOR.compare(this.bytes, param1CollationKey.toByteArray());
/*    */     }
/*    */ 
/*    */     
/*    */     public byte[] toByteArray() {
/* 81 */       return this.bytes;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\CharsetCollator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */