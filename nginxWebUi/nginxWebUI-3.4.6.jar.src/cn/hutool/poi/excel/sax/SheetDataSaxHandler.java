/*     */ package cn.hutool.poi.excel.sax;
/*     */ 
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.cell.FormulaCellValue;
/*     */ import cn.hutool.poi.excel.sax.handler.RowHandler;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.poi.ss.usermodel.BuiltinFormats;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.xssf.model.SharedStrings;
/*     */ import org.apache.poi.xssf.model.StylesTable;
/*     */ import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SheetDataSaxHandler
/*     */   extends DefaultHandler
/*     */ {
/*     */   protected StylesTable stylesTable;
/*     */   protected SharedStrings sharedStrings;
/*     */   protected int sheetIndex;
/*     */   protected int index;
/*     */   private int curCell;
/*     */   private CellDataType cellDataType;
/*     */   private long rowNumber;
/*     */   private String curCoordinate;
/*     */   private ElementName curElementName;
/*     */   private String preCoordinate;
/*     */   private String maxCellCoordinate;
/*     */   private XSSFCellStyle xssfCellStyle;
/*     */   private String numFmtString;
/*     */   private boolean isInSheetData;
/*  59 */   private final StrBuilder lastContent = StrUtil.strBuilder();
/*     */   
/*  61 */   private final StrBuilder lastFormula = StrUtil.strBuilder();
/*     */   
/*  63 */   private List<Object> rowCellList = new ArrayList();
/*     */   
/*     */   public SheetDataSaxHandler(RowHandler rowHandler) {
/*  66 */     this.rowHandler = rowHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RowHandler rowHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRowHandler(RowHandler rowHandler) {
/*  80 */     this.rowHandler = rowHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes) {
/*  88 */     if ("sheetData".equals(qName)) {
/*  89 */       this.isInSheetData = true;
/*     */       
/*     */       return;
/*     */     } 
/*  93 */     if (false == this.isInSheetData) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  98 */     ElementName name = ElementName.of(qName);
/*  99 */     this.curElementName = name;
/*     */     
/* 101 */     if (null != name) {
/* 102 */       switch (name) {
/*     */         
/*     */         case row:
/* 105 */           startRow(attributes);
/*     */           break;
/*     */         
/*     */         case c:
/* 109 */           startCell(attributes);
/*     */           break;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endElement(String uri, String localName, String qName) {
/* 120 */     if ("sheetData".equals(qName)) {
/*     */       
/* 122 */       this.isInSheetData = false;
/*     */       
/*     */       return;
/*     */     } 
/* 126 */     if (false == this.isInSheetData) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 131 */     this.curElementName = null;
/* 132 */     if (ElementName.c.match(qName)) {
/* 133 */       endCell();
/* 134 */     } else if (ElementName.row.match(qName)) {
/* 135 */       endRow();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void characters(char[] ch, int start, int length) {
/* 142 */     if (false == this.isInSheetData) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 147 */     ElementName elementName = this.curElementName;
/* 148 */     if (null != elementName) {
/* 149 */       switch (elementName) {
/*     */         
/*     */         case v:
/* 152 */           this.lastContent.append(ch, start, length);
/*     */           break;
/*     */         
/*     */         case f:
/* 156 */           this.lastFormula.append(ch, start, length);
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } else {
/* 162 */       this.lastContent.append(ch, start, length);
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
/*     */   private void startRow(Attributes attributes) {
/* 174 */     String rValue = AttributeName.r.getValue(attributes);
/* 175 */     if (null != rValue) {
/* 176 */       this.rowNumber = Long.parseLong(rValue) - 1L;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void startCell(Attributes attributes) {
/* 187 */     String tempCurCoordinate = AttributeName.r.getValue(attributes);
/*     */     
/* 189 */     if (this.preCoordinate == null) {
/* 190 */       this.preCoordinate = String.valueOf('@');
/*     */     } else {
/*     */       
/* 193 */       this.preCoordinate = this.curCoordinate;
/*     */     } 
/*     */     
/* 196 */     this.curCoordinate = tempCurCoordinate;
/*     */     
/* 198 */     setCellType(attributes);
/*     */ 
/*     */     
/* 201 */     this.lastContent.reset();
/* 202 */     this.lastFormula.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void endRow() {
/* 210 */     if (this.index == 0) {
/* 211 */       this.maxCellCoordinate = this.curCoordinate;
/*     */     }
/*     */ 
/*     */     
/* 215 */     if (this.maxCellCoordinate != null) {
/* 216 */       fillBlankCell(this.curCoordinate, this.maxCellCoordinate, true);
/*     */     }
/*     */     
/* 219 */     this.rowHandler.handle(this.sheetIndex, this.rowNumber, this.rowCellList);
/*     */ 
/*     */ 
/*     */     
/* 223 */     this.rowCellList = new ArrayList(this.curCell + 1);
/*     */     
/* 225 */     this.index++;
/*     */     
/* 227 */     this.curCell = 0;
/*     */     
/* 229 */     this.curCoordinate = null;
/* 230 */     this.preCoordinate = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void endCell() {
/* 238 */     fillBlankCell(this.preCoordinate, this.curCoordinate, false);
/*     */     
/* 240 */     String contentStr = StrUtil.trim((CharSequence)this.lastContent);
/* 241 */     Object value = ExcelSaxUtil.getDataValue(this.cellDataType, contentStr, this.sharedStrings, this.numFmtString);
/* 242 */     if (false == this.lastFormula.isEmpty()) {
/* 243 */       value = new FormulaCellValue(StrUtil.trim((CharSequence)this.lastFormula), value);
/*     */     }
/* 245 */     addCellValue(this.curCell++, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addCellValue(int index, Object value) {
/* 255 */     this.rowCellList.add(index, value);
/* 256 */     this.rowHandler.handleCell(this.sheetIndex, this.rowNumber, index, value, (CellStyle)this.xssfCellStyle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillBlankCell(String preCoordinate, String curCoordinate, boolean isEnd) {
/* 267 */     if (false == curCoordinate.equals(preCoordinate)) {
/* 268 */       int len = ExcelSaxUtil.countNullCell(preCoordinate, curCoordinate);
/* 269 */       if (isEnd) {
/* 270 */         len++;
/*     */       }
/* 272 */       while (len-- > 0) {
/* 273 */         addCellValue(this.curCell++, "");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setCellType(Attributes attributes) {
/* 285 */     this.numFmtString = "";
/* 286 */     this.cellDataType = CellDataType.of(AttributeName.t.getValue(attributes));
/*     */ 
/*     */     
/* 289 */     if (null != this.stylesTable) {
/* 290 */       String xfIndexStr = AttributeName.s.getValue(attributes);
/* 291 */       if (null != xfIndexStr) {
/* 292 */         this.xssfCellStyle = this.stylesTable.getStyleAt(Integer.parseInt(xfIndexStr));
/*     */         
/* 294 */         int numFmtIndex = this.xssfCellStyle.getDataFormat();
/* 295 */         this.numFmtString = (String)ObjectUtil.defaultIfNull(this.xssfCellStyle
/* 296 */             .getDataFormatString(), () -> BuiltinFormats.getBuiltinFormat(numFmtIndex));
/*     */         
/* 298 */         if (CellDataType.NUMBER == this.cellDataType && ExcelSaxUtil.isDateFormat(numFmtIndex, this.numFmtString))
/* 299 */           this.cellDataType = CellDataType.DATE; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\SheetDataSaxHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */