/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
/*     */ import org.apache.poi.hssf.usermodel.HSSFPicture;
/*     */ import org.apache.poi.hssf.usermodel.HSSFPictureData;
/*     */ import org.apache.poi.hssf.usermodel.HSSFShape;
/*     */ import org.apache.poi.hssf.usermodel.HSSFSheet;
/*     */ import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/*     */ import org.apache.poi.ooxml.POIXMLDocumentPart;
/*     */ import org.apache.poi.ss.usermodel.PictureData;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
/*     */ import org.apache.poi.xssf.usermodel.XSSFDrawing;
/*     */ import org.apache.poi.xssf.usermodel.XSSFPicture;
/*     */ import org.apache.poi.xssf.usermodel.XSSFShape;
/*     */ import org.apache.poi.xssf.usermodel.XSSFSheet;
/*     */ import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/*     */ import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExcelPicUtil
/*     */ {
/*     */   public static Map<String, PictureData> getPicMap(Workbook workbook, int sheetIndex) {
/*  41 */     Assert.notNull(workbook, "Workbook must be not null !", new Object[0]);
/*  42 */     if (sheetIndex < 0) {
/*  43 */       sheetIndex = 0;
/*     */     }
/*     */     
/*  46 */     if (workbook instanceof HSSFWorkbook)
/*  47 */       return getPicMapXls((HSSFWorkbook)workbook, sheetIndex); 
/*  48 */     if (workbook instanceof XSSFWorkbook) {
/*  49 */       return getPicMapXlsx((XSSFWorkbook)workbook, sheetIndex);
/*     */     }
/*  51 */     throw new IllegalArgumentException(StrUtil.format("Workbook type [{}] is not supported!", new Object[] { workbook.getClass() }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<String, PictureData> getPicMapXls(HSSFWorkbook workbook, int sheetIndex) {
/*  64 */     Map<String, PictureData> picMap = new HashMap<>();
/*  65 */     List<HSSFPictureData> pictures = workbook.getAllPictures();
/*  66 */     if (CollectionUtil.isNotEmpty(pictures)) {
/*  67 */       HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
/*     */ 
/*     */       
/*  70 */       for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
/*  71 */         if (shape instanceof HSSFPicture) {
/*  72 */           int pictureIndex = ((HSSFPicture)shape).getPictureIndex() - 1;
/*  73 */           HSSFClientAnchor anchor = (HSSFClientAnchor)shape.getAnchor();
/*  74 */           picMap.put(StrUtil.format("{}_{}", new Object[] { Integer.valueOf(anchor.getRow1()), Short.valueOf(anchor.getCol1()) }), (PictureData)pictures.get(pictureIndex));
/*     */         } 
/*     */       } 
/*     */     } 
/*  78 */     return picMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<String, PictureData> getPicMapXlsx(XSSFWorkbook workbook, int sheetIndex) {
/*  89 */     Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
/*  90 */     XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
/*     */     
/*  92 */     for (POIXMLDocumentPart dr : sheet.getRelations()) {
/*  93 */       if (dr instanceof XSSFDrawing) {
/*  94 */         XSSFDrawing drawing = (XSSFDrawing)dr;
/*  95 */         List<XSSFShape> shapes = drawing.getShapes();
/*     */ 
/*     */         
/*  98 */         for (XSSFShape shape : shapes) {
/*  99 */           if (shape instanceof XSSFPicture) {
/* 100 */             XSSFPicture pic = (XSSFPicture)shape;
/* 101 */             CTMarker ctMarker = pic.getPreferredSize().getFrom();
/* 102 */             sheetIndexPicMap.put(StrUtil.format("{}_{}", new Object[] { Integer.valueOf(ctMarker.getRow()), Integer.valueOf(ctMarker.getCol()) }), pic.getPictureData());
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 108 */     return sheetIndexPicMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelPicUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */