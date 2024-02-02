/*    */ package cn.hutool.poi.word;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.poi.exceptions.POIException;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
/*    */ import org.apache.poi.openxml4j.opc.OPCPackage;
/*    */ import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
/*    */ public class DocUtil
/*    */ {
/*    */   public static XWPFDocument create(File file) {
/*    */     try {
/* 29 */       return FileUtil.exist(file) ? new XWPFDocument(OPCPackage.open(file)) : new XWPFDocument();
/* 30 */     } catch (InvalidFormatException e) {
/* 31 */       throw new POIException(e);
/* 32 */     } catch (IOException e) {
/* 33 */       throw new IORuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\word\DocUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */