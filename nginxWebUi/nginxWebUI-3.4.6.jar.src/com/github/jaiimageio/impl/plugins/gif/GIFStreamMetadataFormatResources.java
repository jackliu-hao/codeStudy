/*    */ package com.github.jaiimageio.impl.plugins.gif;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GIFStreamMetadataFormatResources
/*    */   extends ListResourceBundle
/*    */ {
/*    */   protected Object[][] getContents() {
/* 56 */     return new Object[][] { { "Version", "The file version, either 87a or 89a" }, { "LogicalScreenDescriptor", "The logical screen descriptor, except for the global color table" }, { "GlobalColorTable", "The global color table" }, { "ColorTableEntry", "A global color table entry" }, { "Version/value", "The version string" }, { "LogicalScreenDescriptor/logicalScreenWidth", "The width in pixels of the whole picture" }, { "LogicalScreenDescriptor/logicalScreenHeight", "The height in pixels of the whole picture" }, { "LogicalScreenDescriptor/colorResolution", "The number of bits of color resolution, beteen 1 and 8" }, { "LogicalScreenDescriptor/pixelAspectRatio", "If 0, indicates square pixels, else W/H = (value + 15)/64" }, { "GlobalColorTable/sizeOfGlobalColorTable", "The number of entries in the global color table" }, { "GlobalColorTable/backgroundColorIndex", "The index of the color table entry to be used as a background" }, { "GlobalColorTable/sortFlag", "True if the global color table is sorted by frequency" }, { "ColorTableEntry/index", "The index of the color table entry" }, { "ColorTableEntry/red", "The red value for the color table entry" }, { "ColorTableEntry/green", "The green value for the color table entry" }, { "ColorTableEntry/blue", "The blue value for the color table entry" } };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFStreamMetadataFormatResources.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */