/*   */ package com.beust.jcommander.converters;
/*   */ 
/*   */ import java.util.Arrays;
/*   */ import java.util.List;
/*   */ 
/*   */ public class CommaParameterSplitter
/*   */   implements IParameterSplitter {
/*   */   public List<String> split(String value) {
/* 9 */     return Arrays.asList(value.split(","));
/*   */   }
/*   */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\CommaParameterSplitter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */