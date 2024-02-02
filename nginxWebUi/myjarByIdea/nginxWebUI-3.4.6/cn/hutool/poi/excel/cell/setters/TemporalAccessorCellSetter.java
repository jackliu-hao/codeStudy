package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

public class TemporalAccessorCellSetter implements CellSetter {
   private final TemporalAccessor value;

   TemporalAccessorCellSetter(TemporalAccessor value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      if (this.value instanceof Instant) {
         cell.setCellValue(Date.from((Instant)this.value));
      } else if (this.value instanceof LocalDateTime) {
         cell.setCellValue((LocalDateTime)this.value);
      } else if (this.value instanceof LocalDate) {
         cell.setCellValue((LocalDate)this.value);
      }

   }
}
