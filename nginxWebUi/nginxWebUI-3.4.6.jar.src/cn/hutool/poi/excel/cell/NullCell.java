/*     */ package cn.hutool.poi.excel.cell;
/*     */ 
/*     */ import java.time.LocalDateTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import org.apache.poi.ss.formula.FormulaParseException;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.ss.usermodel.CellType;
/*     */ import org.apache.poi.ss.usermodel.Comment;
/*     */ import org.apache.poi.ss.usermodel.Hyperlink;
/*     */ import org.apache.poi.ss.usermodel.RichTextString;
/*     */ import org.apache.poi.ss.usermodel.Row;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.util.CellAddress;
/*     */ import org.apache.poi.ss.util.CellRangeAddress;
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
/*     */ public class NullCell
/*     */   implements Cell
/*     */ {
/*     */   private final Row row;
/*     */   private final int columnIndex;
/*     */   
/*     */   public NullCell(Row row, int columnIndex) {
/*  37 */     this.row = row;
/*  38 */     this.columnIndex = columnIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnIndex() {
/*  43 */     return this.columnIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRowIndex() {
/*  48 */     return getRow().getRowNum();
/*     */   }
/*     */ 
/*     */   
/*     */   public Sheet getSheet() {
/*  53 */     return getRow().getSheet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Row getRow() {
/*  58 */     return this.row;
/*     */   }
/*     */   
/*     */   public void setCellType(CellType cellType) {
/*  62 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlank() {
/*  67 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public CellType getCellType() {
/*  72 */     return null;
/*     */   }
/*     */   
/*     */   public CellType getCellTypeEnum() {
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public CellType getCachedFormulaResultType() {
/*  81 */     return null;
/*     */   }
/*     */   
/*     */   public CellType getCachedFormulaResultTypeEnum() {
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(double value) {
/*  90 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(Date value) {
/*  95 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(LocalDateTime value) {
/* 100 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(Calendar value) {
/* 105 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(RichTextString value) {
/* 110 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(String value) {
/* 115 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellFormula(String formula) throws FormulaParseException, IllegalStateException {
/* 120 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeFormula() throws IllegalStateException {
/* 125 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCellFormula() {
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getNumericCellValue() {
/* 135 */     throw new UnsupportedOperationException("Cell value is null!");
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDateCellValue() {
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalDateTime getLocalDateTimeCellValue() {
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public RichTextString getRichStringCellValue() {
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringCellValue() {
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellValue(boolean value) {
/* 160 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellErrorValue(byte value) {
/* 165 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBooleanCellValue() {
/* 170 */     throw new UnsupportedOperationException("Cell value is null!");
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getErrorCellValue() {
/* 175 */     throw new UnsupportedOperationException("Cell value is null!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellStyle(CellStyle style) {
/* 180 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public CellStyle getCellStyle() {
/* 185 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsActiveCell() {
/* 190 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public CellAddress getAddress() {
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCellComment(Comment comment) {
/* 200 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public Comment getCellComment() {
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeCellComment() {
/* 210 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public Hyperlink getHyperlink() {
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHyperlink(Hyperlink link) {
/* 220 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeHyperlink() {
/* 225 */     throw new UnsupportedOperationException("Can not set any thing to null cell!");
/*     */   }
/*     */ 
/*     */   
/*     */   public CellRangeAddress getArrayFormulaRange() {
/* 230 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPartOfArrayFormulaGroup() {
/* 235 */     throw new UnsupportedOperationException("Cell value is null!");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\NullCell.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */