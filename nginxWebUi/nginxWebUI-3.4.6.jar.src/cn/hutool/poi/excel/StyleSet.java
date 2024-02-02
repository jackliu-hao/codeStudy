/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.poi.excel.style.StyleUtil;
/*     */ import java.io.Serializable;
/*     */ import org.apache.poi.hssf.util.HSSFColor;
/*     */ import org.apache.poi.ss.usermodel.BorderStyle;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.ss.usermodel.FillPatternType;
/*     */ import org.apache.poi.ss.usermodel.Font;
/*     */ import org.apache.poi.ss.usermodel.HorizontalAlignment;
/*     */ import org.apache.poi.ss.usermodel.IndexedColors;
/*     */ import org.apache.poi.ss.usermodel.VerticalAlignment;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
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
/*     */ public class StyleSet
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Workbook workbook;
/*     */   protected final CellStyle headCellStyle;
/*     */   protected final CellStyle cellStyle;
/*     */   protected final CellStyle cellStyleForNumber;
/*     */   protected final CellStyle cellStyleForDate;
/*     */   protected final CellStyle cellStyleForHyperlink;
/*     */   
/*     */   public StyleSet(Workbook workbook) {
/*  60 */     this.workbook = workbook;
/*  61 */     this.headCellStyle = StyleUtil.createHeadCellStyle(workbook);
/*  62 */     this.cellStyle = StyleUtil.createDefaultCellStyle(workbook);
/*     */ 
/*     */     
/*  65 */     this.cellStyleForNumber = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
/*     */     
/*  67 */     this.cellStyleForNumber.setDataFormat((short)2);
/*     */ 
/*     */     
/*  70 */     this.cellStyleForDate = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
/*     */     
/*  72 */     this.cellStyleForDate.setDataFormat((short)22);
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.cellStyleForHyperlink = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
/*  77 */     Font font = this.workbook.createFont();
/*  78 */     font.setUnderline((byte)1);
/*  79 */     font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
/*  80 */     this.cellStyleForHyperlink.setFont(font);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getHeadCellStyle() {
/*  89 */     return this.headCellStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getCellStyle() {
/*  98 */     return this.cellStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getCellStyleForNumber() {
/* 107 */     return this.cellStyleForNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getCellStyleForDate() {
/* 116 */     return this.cellStyleForDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getCellStyleForHyperlink() {
/* 126 */     return this.cellStyleForHyperlink;
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
/*     */   public StyleSet setBorder(BorderStyle borderSize, IndexedColors colorIndex) {
/* 138 */     StyleUtil.setBorder(this.headCellStyle, borderSize, colorIndex);
/* 139 */     StyleUtil.setBorder(this.cellStyle, borderSize, colorIndex);
/* 140 */     StyleUtil.setBorder(this.cellStyleForNumber, borderSize, colorIndex);
/* 141 */     StyleUtil.setBorder(this.cellStyleForDate, borderSize, colorIndex);
/* 142 */     StyleUtil.setBorder(this.cellStyleForHyperlink, borderSize, colorIndex);
/* 143 */     return this;
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
/*     */   public StyleSet setAlign(HorizontalAlignment halign, VerticalAlignment valign) {
/* 155 */     StyleUtil.setAlign(this.headCellStyle, halign, valign);
/* 156 */     StyleUtil.setAlign(this.cellStyle, halign, valign);
/* 157 */     StyleUtil.setAlign(this.cellStyleForNumber, halign, valign);
/* 158 */     StyleUtil.setAlign(this.cellStyleForDate, halign, valign);
/* 159 */     StyleUtil.setAlign(this.cellStyleForHyperlink, halign, valign);
/* 160 */     return this;
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
/*     */   public StyleSet setBackgroundColor(IndexedColors backgroundColor, boolean withHeadCell) {
/* 172 */     if (withHeadCell) {
/* 173 */       StyleUtil.setColor(this.headCellStyle, backgroundColor, FillPatternType.SOLID_FOREGROUND);
/*     */     }
/* 175 */     StyleUtil.setColor(this.cellStyle, backgroundColor, FillPatternType.SOLID_FOREGROUND);
/* 176 */     StyleUtil.setColor(this.cellStyleForNumber, backgroundColor, FillPatternType.SOLID_FOREGROUND);
/* 177 */     StyleUtil.setColor(this.cellStyleForDate, backgroundColor, FillPatternType.SOLID_FOREGROUND);
/* 178 */     StyleUtil.setColor(this.cellStyleForHyperlink, backgroundColor, FillPatternType.SOLID_FOREGROUND);
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
/*     */   public StyleSet setFont(short color, short fontSize, String fontName, boolean ignoreHead) {
/* 192 */     Font font = StyleUtil.createFont(this.workbook, color, fontSize, fontName);
/* 193 */     return setFont(font, ignoreHead);
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
/*     */   public StyleSet setFont(Font font, boolean ignoreHead) {
/* 205 */     if (false == ignoreHead) {
/* 206 */       this.headCellStyle.setFont(font);
/*     */     }
/* 208 */     this.cellStyle.setFont(font);
/* 209 */     this.cellStyleForNumber.setFont(font);
/* 210 */     this.cellStyleForDate.setFont(font);
/* 211 */     this.cellStyleForHyperlink.setFont(font);
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StyleSet setWrapText() {
/* 222 */     this.cellStyle.setWrapText(true);
/* 223 */     this.cellStyleForNumber.setWrapText(true);
/* 224 */     this.cellStyleForDate.setWrapText(true);
/* 225 */     this.cellStyleForHyperlink.setWrapText(true);
/* 226 */     return this;
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
/*     */   public CellStyle getStyleByValueType(Object value, boolean isHeader) {
/* 238 */     CellStyle style = null;
/*     */     
/* 240 */     if (isHeader && null != this.headCellStyle) {
/* 241 */       style = this.headCellStyle;
/* 242 */     } else if (null != this.cellStyle) {
/* 243 */       style = this.cellStyle;
/*     */     } 
/*     */     
/* 246 */     if (value instanceof java.util.Date || value instanceof java.time.temporal.TemporalAccessor || value instanceof java.util.Calendar) {
/*     */ 
/*     */ 
/*     */       
/* 250 */       if (null != this.cellStyleForDate) {
/* 251 */         style = this.cellStyleForDate;
/*     */       }
/* 253 */     } else if (value instanceof Number) {
/*     */       
/* 255 */       if ((value instanceof Double || value instanceof Float || value instanceof java.math.BigDecimal) && null != this.cellStyleForNumber)
/*     */       {
/* 257 */         style = this.cellStyleForNumber;
/*     */       }
/* 259 */     } else if (value instanceof org.apache.poi.ss.usermodel.Hyperlink) {
/*     */       
/* 261 */       if (null != this.cellStyleForHyperlink) {
/* 262 */         style = this.cellStyleForHyperlink;
/*     */       }
/*     */     } 
/*     */     
/* 266 */     return style;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\StyleSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */