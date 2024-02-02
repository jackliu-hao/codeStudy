/*    */ package cn.hutool.poi.excel;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.poi.poifs.filesystem.FileMagic;
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
/*    */ public class ExcelFileUtil
/*    */ {
/*    */   public static boolean isXls(InputStream in) {
/* 28 */     return (FileMagic.OLE2 == getFileMagic(in));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isXlsx(InputStream in) {
/* 40 */     return (FileMagic.OOXML == getFileMagic(in));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isXlsx(File file) {
/*    */     try {
/* 53 */       return (FileMagic.valueOf(file) == FileMagic.OOXML);
/* 54 */     } catch (IOException e) {
/* 55 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
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
/*    */   private static FileMagic getFileMagic(InputStream in) {
/*    */     FileMagic magic;
/* 72 */     in = FileMagic.prepareToCheckMagic(in);
/*    */     try {
/* 74 */       magic = FileMagic.valueOf(in);
/* 75 */     } catch (IOException e) {
/* 76 */       throw new IORuntimeException(e);
/*    */     } 
/*    */     
/* 79 */     return magic;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelFileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */