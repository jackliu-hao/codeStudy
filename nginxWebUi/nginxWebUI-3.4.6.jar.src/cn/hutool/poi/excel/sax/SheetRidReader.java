/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
/*     */ import org.apache.poi.xssf.eventusermodel.XSSFReader;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.helpers.DefaultHandler;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SheetRidReader
/*     */   extends DefaultHandler
/*     */ {
/*     */   private static final String TAG_NAME = "sheet";
/*     */   private static final String RID_ATTR = "r:id";
/*     */   private static final String SHEET_ID_ATTR = "sheetId";
/*     */   private static final String NAME_ATTR = "name";
/*     */   
/*     */   public static SheetRidReader parse(XSSFReader reader) {
/*  45 */     return (new SheetRidReader()).read(reader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private final Map<Integer, Integer> ID_RID_MAP = new LinkedHashMap<>();
/*  54 */   private final Map<String, Integer> NAME_RID_MAP = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SheetRidReader read(XSSFReader xssfReader) {
/*  63 */     InputStream workbookData = null;
/*     */     try {
/*  65 */       workbookData = xssfReader.getWorkbookData();
/*  66 */       ExcelSaxUtil.readFrom(workbookData, this);
/*  67 */     } catch (InvalidFormatException e) {
/*  68 */       throw new POIException(e);
/*  69 */     } catch (IOException e) {
/*  70 */       throw new IORuntimeException(e);
/*     */     } finally {
/*  72 */       IoUtil.close(workbookData);
/*     */     } 
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidBySheetId(int sheetId) {
/*  84 */     return this.ID_RID_MAP.get(Integer.valueOf(sheetId));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidBySheetIdBase0(int sheetId) {
/*  95 */     Integer rid = getRidBySheetId(sheetId + 1);
/*  96 */     if (null != rid) {
/*  97 */       return Integer.valueOf(rid.intValue() - 1);
/*     */     }
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidByName(String sheetName) {
/* 109 */     return this.NAME_RID_MAP.get(sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidByNameBase0(String sheetName) {
/* 120 */     Integer rid = getRidByName(sheetName);
/* 121 */     if (null != rid) {
/* 122 */       return Integer.valueOf(rid.intValue() - 1);
/*     */     }
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidByIndex(int index) {
/* 135 */     return (Integer)CollUtil.get(this.NAME_RID_MAP.values(), index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getRidByIndexBase0(int index) {
/* 146 */     Integer rid = (Integer)CollUtil.get(this.NAME_RID_MAP.values(), index);
/* 147 */     if (null != rid) {
/* 148 */       return Integer.valueOf(rid.intValue() - 1);
/*     */     }
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getSheetNames() {
/* 160 */     return ListUtil.toList(this.NAME_RID_MAP.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes) {
/* 165 */     if ("sheet".equalsIgnoreCase(localName)) {
/* 166 */       String ridStr = attributes.getValue("r:id");
/* 167 */       if (StrUtil.isEmpty(ridStr)) {
/*     */         return;
/*     */       }
/* 170 */       int rid = Integer.parseInt(StrUtil.removePrefixIgnoreCase(ridStr, "rId"));
/*     */ 
/*     */       
/* 173 */       String name = attributes.getValue("name");
/* 174 */       if (StrUtil.isNotEmpty(name)) {
/* 175 */         this.NAME_RID_MAP.put(name, Integer.valueOf(rid));
/*     */       }
/*     */ 
/*     */       
/* 179 */       String sheetIdStr = attributes.getValue("sheetId");
/* 180 */       if (StrUtil.isNotEmpty(sheetIdStr))
/* 181 */         this.ID_RID_MAP.put(Integer.valueOf(Integer.parseInt(sheetIdStr)), Integer.valueOf(rid)); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\SheetRidReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */