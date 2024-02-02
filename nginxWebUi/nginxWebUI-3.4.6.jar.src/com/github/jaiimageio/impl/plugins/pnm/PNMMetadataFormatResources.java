/*    */ package com.github.jaiimageio.impl.plugins.pnm;
/*    */ 
/*    */ import java.util.ListResourceBundle;
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
/*    */ public class PNMMetadataFormatResources
/*    */   extends ListResourceBundle
/*    */ {
/* 50 */   static final Object[][] contents = new Object[][] { { "FormatName", "The format name. One of PBM, PGM or PPM" }, { "Variant", "The variant: RAWBITS or ASCII" }, { "Width", "The image width" }, { "Height", "The image height" }, { "MaximumSample", "The maximum bit depth of one sample." }, { "Comment", "A comment." } };
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
/*    */   protected Object[][] getContents() {
/* 63 */     return contents;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\pnm\PNMMetadataFormatResources.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */