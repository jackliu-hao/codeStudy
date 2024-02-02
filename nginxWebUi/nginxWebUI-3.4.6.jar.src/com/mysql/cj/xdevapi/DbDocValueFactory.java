/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.AssertionFailedException;
/*    */ import com.mysql.cj.result.DefaultValueFactory;
/*    */ import com.mysql.cj.result.Field;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.io.IOException;
/*    */ import java.io.StringReader;
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
/*    */ public class DbDocValueFactory
/*    */   extends DefaultValueFactory<DbDoc>
/*    */ {
/*    */   public DbDocValueFactory(PropertySet pset) {
/* 53 */     super(pset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DbDoc createFromBytes(byte[] bytes, int offset, int length, Field f) {
/*    */     try {
/* 63 */       return JsonParser.parseDoc(new StringReader(StringUtils.toString(bytes, offset, length, f.getEncoding())));
/* 64 */     } catch (IOException ex) {
/* 65 */       throw AssertionFailedException.shouldNotHappen(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public DbDoc createFromNull() {
/* 71 */     return null;
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 75 */     return DbDoc.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DbDocValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */