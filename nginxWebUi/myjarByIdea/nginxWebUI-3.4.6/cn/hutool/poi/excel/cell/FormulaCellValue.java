package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

public class FormulaCellValue implements CellValue<String>, CellSetter {
   private final String formula;
   private final Object result;

   public FormulaCellValue(String formula) {
      this(formula, (Object)null);
   }

   public FormulaCellValue(String formula, Object result) {
      this.formula = formula;
      this.result = result;
   }

   public String getValue() {
      return this.formula;
   }

   public void setValue(Cell cell) {
      cell.setCellFormula(this.formula);
   }

   public Object getResult() {
      return this.result;
   }

   public String toString() {
      return this.getResult().toString();
   }
}
