/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.IStringConverter;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
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
/*    */ public class PathConverter
/*    */   implements IStringConverter<Path>
/*    */ {
/*    */   public Path convert(String value) {
/* 34 */     return Paths.get(value, new String[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\PathConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */