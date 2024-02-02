package cn.hutool.poi.excel.cell;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

public class NullCell implements Cell {
   private final Row row;
   private final int columnIndex;

   public NullCell(Row row, int columnIndex) {
      this.row = row;
      this.columnIndex = columnIndex;
   }

   public int getColumnIndex() {
      return this.columnIndex;
   }

   public int getRowIndex() {
      return this.getRow().getRowNum();
   }

   public Sheet getSheet() {
      return this.getRow().getSheet();
   }

   public Row getRow() {
      return this.row;
   }

   public void setCellType(CellType cellType) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setBlank() {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public CellType getCellType() {
      return null;
   }

   public CellType getCellTypeEnum() {
      return null;
   }

   public CellType getCachedFormulaResultType() {
      return null;
   }

   public CellType getCachedFormulaResultTypeEnum() {
      return null;
   }

   public void setCellValue(double value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellValue(Date value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellValue(LocalDateTime value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellValue(Calendar value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellValue(RichTextString value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellValue(String value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellFormula(String formula) throws FormulaParseException, IllegalStateException {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void removeFormula() throws IllegalStateException {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public String getCellFormula() {
      return null;
   }

   public double getNumericCellValue() {
      throw new UnsupportedOperationException("Cell value is null!");
   }

   public Date getDateCellValue() {
      return null;
   }

   public LocalDateTime getLocalDateTimeCellValue() {
      return null;
   }

   public RichTextString getRichStringCellValue() {
      return null;
   }

   public String getStringCellValue() {
      return null;
   }

   public void setCellValue(boolean value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void setCellErrorValue(byte value) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public boolean getBooleanCellValue() {
      throw new UnsupportedOperationException("Cell value is null!");
   }

   public byte getErrorCellValue() {
      throw new UnsupportedOperationException("Cell value is null!");
   }

   public void setCellStyle(CellStyle style) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public CellStyle getCellStyle() {
      return null;
   }

   public void setAsActiveCell() {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public CellAddress getAddress() {
      return null;
   }

   public void setCellComment(Comment comment) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public Comment getCellComment() {
      return null;
   }

   public void removeCellComment() {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public Hyperlink getHyperlink() {
      return null;
   }

   public void setHyperlink(Hyperlink link) {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public void removeHyperlink() {
      throw new UnsupportedOperationException("Can not set any thing to null cell!");
   }

   public CellRangeAddress getArrayFormulaRange() {
      return null;
   }

   public boolean isPartOfArrayFormulaGroup() {
      throw new UnsupportedOperationException("Cell value is null!");
   }
}
