/*    */ package cn.hutool.poi.word;
/*    */ 
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
/*    */ public class WordUtil
/*    */ {
/*    */   public static Word07Writer getWriter() {
/* 18 */     return new Word07Writer();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Word07Writer getWriter(File destFile) {
/* 28 */     return new Word07Writer(destFile);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\word\WordUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */