/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.IStringConverter;
/*    */ import java.io.File;
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
/*    */ public class FileConverter
/*    */   implements IStringConverter<File>
/*    */ {
/*    */   public File convert(String value) {
/* 33 */     return new File(value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\FileConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */