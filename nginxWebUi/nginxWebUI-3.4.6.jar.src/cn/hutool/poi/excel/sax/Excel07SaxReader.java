/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.sax.handler.RowHandler;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
/*     */ import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
/*     */ import org.apache.poi.openxml4j.opc.OPCPackage;
/*     */ import org.apache.poi.openxml4j.opc.PackageAccess;
/*     */ import org.apache.poi.xssf.eventusermodel.XSSFReader;
/*     */ import org.apache.poi.xssf.model.SharedStrings;
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
/*     */ public class Excel07SaxReader
/*     */   implements ExcelSaxReader<Excel07SaxReader>
/*     */ {
/*     */   private final SheetDataSaxHandler handler;
/*     */   
/*     */   public Excel07SaxReader(RowHandler rowHandler) {
/*  38 */     this.handler = new SheetDataSaxHandler(rowHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Excel07SaxReader setRowHandler(RowHandler rowHandler) {
/*  48 */     this.handler.setRowHandler(rowHandler);
/*  49 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Excel07SaxReader read(File file, int rid) throws POIException {
/*  55 */     return read(file, "rId" + rid);
/*     */   }
/*     */ 
/*     */   
/*     */   public Excel07SaxReader read(File file, String idOrRidOrSheetName) throws POIException {
/*  60 */     try (OPCPackage open = OPCPackage.open(file, PackageAccess.READ)) {
/*  61 */       return read(open, idOrRidOrSheetName);
/*  62 */     } catch (InvalidFormatException|IOException e) {
/*  63 */       throw new POIException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Excel07SaxReader read(InputStream in, int rid) throws POIException {
/*  69 */     return read(in, "rId" + rid);
/*     */   }
/*     */ 
/*     */   
/*     */   public Excel07SaxReader read(InputStream in, String idOrRidOrSheetName) throws POIException {
/*  74 */     try (OPCPackage opcPackage = OPCPackage.open(in)) {
/*  75 */       return read(opcPackage, idOrRidOrSheetName);
/*  76 */     } catch (IOException e) {
/*  77 */       throw new IORuntimeException(e);
/*  78 */     } catch (InvalidFormatException e) {
/*  79 */       throw new POIException(e);
/*     */     } 
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
/*     */   public Excel07SaxReader read(OPCPackage opcPackage, int rid) throws POIException {
/*  92 */     return read(opcPackage, "rId" + rid);
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
/*     */   public Excel07SaxReader read(OPCPackage opcPackage, String idOrRidOrSheetName) throws POIException {
/*     */     try {
/* 105 */       return read(new XSSFReader(opcPackage), idOrRidOrSheetName);
/* 106 */     } catch (OpenXML4JException e) {
/* 107 */       throw new POIException(e);
/* 108 */     } catch (IOException e) {
/* 109 */       throw new IORuntimeException(e);
/*     */     } 
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
/*     */   
/*     */   public Excel07SaxReader read(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
/*     */     try {
/* 125 */       this.handler.stylesTable = xssfReader.getStylesTable();
/* 126 */     } catch (IOException|InvalidFormatException iOException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     this.handler.sharedStrings = (SharedStrings)ReflectUtil.invoke(xssfReader, "getSharedStringsTable", new Object[0]);
/*     */     
/* 135 */     return readSheets(xssfReader, idOrRidOrSheetName);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private Excel07SaxReader readSheets(XSSFReader xssfReader, String idOrRidOrSheetName) throws POIException {
/* 151 */     this.handler.sheetIndex = getSheetIndex(xssfReader, idOrRidOrSheetName);
/* 152 */     InputStream sheetInputStream = null;
/*     */     try {
/* 154 */       if (this.handler.sheetIndex > -1) {
/*     */         
/* 156 */         sheetInputStream = xssfReader.getSheet("rId" + (this.handler.sheetIndex + 1));
/* 157 */         ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
/* 158 */         this.handler.rowHandler.doAfterAllAnalysed();
/*     */       } else {
/* 160 */         this.handler.sheetIndex = -1;
/*     */         
/* 162 */         Iterator<InputStream> sheetInputStreams = xssfReader.getSheetsData();
/* 163 */         while (sheetInputStreams.hasNext()) {
/*     */           
/* 165 */           this.handler.index = 0;
/* 166 */           this.handler.sheetIndex++;
/* 167 */           sheetInputStream = sheetInputStreams.next();
/* 168 */           ExcelSaxUtil.readFrom(sheetInputStream, this.handler);
/* 169 */           this.handler.rowHandler.doAfterAllAnalysed();
/*     */         } 
/*     */       } 
/* 172 */     } catch (RuntimeException e) {
/* 173 */       throw e;
/* 174 */     } catch (Exception e) {
/* 175 */       throw new POIException(e);
/*     */     } finally {
/* 177 */       IoUtil.close(sheetInputStream);
/*     */     } 
/* 179 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getSheetIndex(XSSFReader xssfReader, String idOrRidOrSheetName) {
/* 197 */     if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "rId")) {
/* 198 */       return Integer.parseInt(StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "rId"));
/*     */     }
/*     */ 
/*     */     
/* 202 */     SheetRidReader ridReader = SheetRidReader.parse(xssfReader);
/*     */     
/* 204 */     if (StrUtil.startWithIgnoreCase(idOrRidOrSheetName, "sheetName:")) {
/*     */       
/* 206 */       idOrRidOrSheetName = StrUtil.removePrefixIgnoreCase(idOrRidOrSheetName, "sheetName:");
/* 207 */       Integer rid = ridReader.getRidByNameBase0(idOrRidOrSheetName);
/* 208 */       if (null != rid) {
/* 209 */         return rid.intValue();
/*     */       }
/*     */     } else {
/*     */       
/* 213 */       Integer rid = ridReader.getRidByNameBase0(idOrRidOrSheetName);
/* 214 */       if (null != rid) {
/* 215 */         return rid.intValue();
/*     */       }
/*     */       
/*     */       try {
/* 219 */         int sheetIndex = Integer.parseInt(idOrRidOrSheetName);
/* 220 */         rid = ridReader.getRidBySheetIdBase0(sheetIndex);
/*     */         
/* 222 */         return ((Integer)ObjectUtil.defaultIfNull(rid, Integer.valueOf(sheetIndex))).intValue();
/* 223 */       } catch (NumberFormatException numberFormatException) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 228 */     throw new IllegalArgumentException("Invalid rId or id or sheetName: " + idOrRidOrSheetName);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\Excel07SaxReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */