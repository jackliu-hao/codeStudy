/*    */ package com.beust.jcommander.converters;
/*    */ 
/*    */ import com.beust.jcommander.ParameterException;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
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
/*    */ public class URIConverter
/*    */   extends BaseConverter<URI>
/*    */ {
/*    */   public URIConverter(String optionName) {
/* 34 */     super(optionName);
/*    */   }
/*    */   
/*    */   public URI convert(String value) {
/*    */     try {
/* 39 */       return new URI(value);
/* 40 */     } catch (URISyntaxException e) {
/* 41 */       throw new ParameterException(getErrorString(value, "a RFC 2396 and RFC 2732 compliant URI"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\converters\URIConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */