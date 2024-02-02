/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.exceptions.DependencyException;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.ExcelDateUtil;
/*     */ import cn.hutool.poi.excel.sax.handler.RowHandler;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
/*     */ import org.apache.poi.hssf.record.CellValueRecordInterface;
/*     */ import org.apache.poi.ss.usermodel.DataFormatter;
/*     */ import org.apache.poi.ss.usermodel.DateUtil;
/*     */ import org.apache.poi.util.XMLHelper;
/*     */ import org.apache.poi.xssf.model.SharedStrings;
/*     */ import org.apache.poi.xssf.usermodel.XSSFRichTextString;
/*     */ import org.xml.sax.ContentHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
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
/*     */ public class ExcelSaxUtil
/*     */ {
/*     */   public static final char CELL_FILL_CHAR = '@';
/*     */   public static final int MAX_CELL_BIT = 3;
/*     */   
/*     */   public static ExcelSaxReader<?> createSaxReader(boolean isXlsx, RowHandler rowHandler) {
/*  48 */     return isXlsx ? new Excel07SaxReader(rowHandler) : new Excel03SaxReader(rowHandler);
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
/*     */   public static Object getDataValue(CellDataType cellDataType, String value, SharedStrings sharedStrings, String numFmtString) {
/*  63 */     if (null == value) {
/*  64 */       return null;
/*     */     }
/*     */     
/*  67 */     if (null == cellDataType) {
/*  68 */       cellDataType = CellDataType.NULL;
/*     */     }
/*     */ 
/*     */     
/*  72 */     switch (cellDataType)
/*     */     { case BOOL:
/*  74 */         result = Boolean.valueOf((value.charAt(0) != '0'));
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
/* 111 */         return result;case ERROR: result = StrUtil.format("\\\"ERROR: {} ", new Object[] { value }); return result;case FORMULA: result = StrUtil.format("\"{}\"", new Object[] { value }); return result;case INLINESTR: result = (new XSSFRichTextString(value)).toString(); return result;case SSTINDEX: try { int index = Integer.parseInt(value); result = sharedStrings.getItemAt(index).getString(); } catch (NumberFormatException e) { result = value; }  return result;case NUMBER: try { result = getNumberValue(value, numFmtString); } catch (NumberFormatException e) { result = value; }  return result;case DATE: try { result = getDateValue(value); } catch (Exception e) { result = value; }  return result; }  Object result = value; return result;
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
/*     */   public static String formatCellContent(String value, int numFmtIndex, String numFmtString) {
/* 123 */     if (null != numFmtString) {
/*     */       try {
/* 125 */         value = (new DataFormatter()).formatRawCellContents(Double.parseDouble(value), numFmtIndex, numFmtString);
/* 126 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/* 130 */     return value;
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
/*     */   public static int countNullCell(String preRef, String ref) {
/* 143 */     String preXfd = StrUtil.nullToDefault(preRef, "@").replaceAll("\\d+", "");
/* 144 */     String xfd = StrUtil.nullToDefault(ref, "@").replaceAll("\\d+", "");
/*     */ 
/*     */ 
/*     */     
/* 148 */     preXfd = StrUtil.fillBefore(preXfd, '@', 3);
/* 149 */     xfd = StrUtil.fillBefore(xfd, '@', 3);
/*     */     
/* 151 */     char[] preLetter = preXfd.toCharArray();
/* 152 */     char[] letter = xfd.toCharArray();
/*     */     
/* 154 */     int res = (letter[0] - preLetter[0]) * 26 * 26 + (letter[1] - preLetter[1]) * 26 + letter[2] - preLetter[2];
/* 155 */     return res - 1;
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
/*     */   public static void readFrom(InputStream xmlDocStream, ContentHandler handler) throws DependencyException, POIException, IORuntimeException {
/*     */     XMLReader xmlReader;
/*     */     try {
/* 171 */       xmlReader = XMLHelper.newXMLReader();
/* 172 */     } catch (SAXException|javax.xml.parsers.ParserConfigurationException e) {
/* 173 */       if (e.getMessage().contains("org.apache.xerces.parsers.SAXParser")) {
/* 174 */         throw new DependencyException(e, "You need to add 'xerces:xercesImpl' to your project and version >= 2.11.0", new Object[0]);
/*     */       }
/* 176 */       throw new POIException(e);
/*     */     } 
/*     */     
/* 179 */     xmlReader.setContentHandler(handler);
/*     */     try {
/* 181 */       xmlReader.parse(new InputSource(xmlDocStream));
/* 182 */     } catch (IOException e) {
/* 183 */       throw new IORuntimeException(e);
/* 184 */     } catch (SAXException e) {
/* 185 */       throw new POIException(e);
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
/*     */   public static boolean isDateFormat(CellValueRecordInterface cell, FormatTrackingHSSFListener formatListener) {
/* 198 */     int formatIndex = formatListener.getFormatIndex(cell);
/* 199 */     String formatString = formatListener.getFormatString(cell);
/* 200 */     return isDateFormat(formatIndex, formatString);
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
/*     */   public static boolean isDateFormat(int formatIndex, String formatString) {
/* 213 */     return ExcelDateUtil.isDateFormat(formatIndex, formatString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DateTime getDateValue(String value) {
/* 224 */     return getDateValue(Double.parseDouble(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DateTime getDateValue(double value) {
/* 235 */     return DateUtil.date(DateUtil.getJavaDate(value, false));
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
/*     */   public static Object getNumberOrDateValue(CellValueRecordInterface cell, double value, FormatTrackingHSSFListener formatListener) {
/* 248 */     if (isDateFormat(cell, formatListener))
/*     */     {
/* 250 */       return getDateValue(value);
/*     */     }
/* 252 */     return getNumberValue(value, formatListener.getFormatString(cell));
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
/*     */   private static Number getNumberValue(String value, String numFmtString) {
/* 264 */     if (StrUtil.isBlank(value)) {
/* 265 */       return null;
/*     */     }
/* 267 */     return getNumberValue(Double.parseDouble(value), numFmtString);
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
/*     */   private static Number getNumberValue(double numValue, String numFmtString) {
/* 280 */     if (null != numFmtString && false == StrUtil.contains(numFmtString, '.')) {
/* 281 */       long longPart = (long)numValue;
/*     */       
/* 283 */       if (longPart == numValue)
/*     */       {
/* 285 */         return Long.valueOf(longPart);
/*     */       }
/*     */     } 
/* 288 */     return Double.valueOf(numValue);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\ExcelSaxUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */