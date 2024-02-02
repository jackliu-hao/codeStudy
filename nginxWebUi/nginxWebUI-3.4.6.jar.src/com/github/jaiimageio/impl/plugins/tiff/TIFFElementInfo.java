/*    */ package com.github.jaiimageio.impl.plugins.tiff;
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
/*    */ public class TIFFElementInfo
/*    */ {
/*    */   String[] childNames;
/*    */   String[] attributeNames;
/*    */   int childPolicy;
/* 54 */   int minChildren = 0;
/* 55 */   int maxChildren = Integer.MAX_VALUE;
/*    */   
/* 57 */   int objectValueType = 0;
/* 58 */   Class objectClass = null;
/* 59 */   Object objectDefaultValue = null;
/* 60 */   Object[] objectEnumerations = null;
/* 61 */   Comparable objectMinValue = null;
/* 62 */   Comparable objectMaxValue = null;
/* 63 */   int objectArrayMinLength = 0;
/* 64 */   int objectArrayMaxLength = 0;
/*    */ 
/*    */ 
/*    */   
/*    */   public TIFFElementInfo(String[] childNames, String[] attributeNames, int childPolicy) {
/* 69 */     this.childNames = childNames;
/* 70 */     this.attributeNames = attributeNames;
/* 71 */     this.childPolicy = childPolicy;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFElementInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */