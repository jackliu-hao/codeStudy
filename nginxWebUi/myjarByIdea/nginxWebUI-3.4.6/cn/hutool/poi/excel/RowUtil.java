package cn.hutool.poi.excel;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.hutool.poi.excel.cell.CellUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeUtil;

public class RowUtil {
   public static Row getOrCreateRow(Sheet sheet, int rowIndex) {
      Row row = sheet.getRow(rowIndex);
      if (null == row) {
         row = sheet.createRow(rowIndex);
      }

      return row;
   }

   public static List<Object> readRow(Row row, CellEditor cellEditor) {
      return readRow(row, 0, 32767, cellEditor);
   }

   public static List<Object> readRow(Row row, int startCellNumInclude, int endCellNumInclude, CellEditor cellEditor) {
      if (null == row) {
         return new ArrayList(0);
      } else {
         short rowLength = row.getLastCellNum();
         if (rowLength < 0) {
            return ListUtil.empty();
         } else {
            int size = Math.min(endCellNumInclude + 1, rowLength);
            List<Object> cellValues = new ArrayList(size);
            boolean isAllNull = true;

            for(int i = startCellNumInclude; i < size; ++i) {
               Object cellValue = CellUtil.getCellValue(CellUtil.getCell(row, i), cellEditor);
               isAllNull &= StrUtil.isEmptyIfStr(cellValue);
               cellValues.add(cellValue);
            }

            return (List)(isAllNull ? ListUtil.empty() : cellValues);
         }
      }
   }

   public static void writeRow(Row row, Iterable<?> rowData) {
      writeRow(row, rowData, (StyleSet)null, false);
   }

   public static void writeRow(Row row, Iterable<?> rowData, StyleSet styleSet, boolean isHeader) {
      int i = 0;

      for(Iterator var6 = rowData.iterator(); var6.hasNext(); ++i) {
         Object value = var6.next();
         Cell cell = row.createCell(i);
         CellUtil.setCellValue(cell, value, styleSet, isHeader);
      }

   }

   public static void insertRow(Sheet sheet, int startRow, int insertNumber) {
      if (insertNumber > 0) {
         Row sourceRow = (Row)Optional.ofNullable(sheet.getRow(startRow)).orElseGet(() -> {
            return sheet.createRow(insertNumber);
         });
         sheet.shiftRows(startRow, sheet.getLastRowNum(), insertNumber, true, false);
         IntStream.range(startRow, startRow + insertNumber).forEachOrdered((i) -> {
            Row row = sheet.createRow(i);
            row.setHeightInPoints(sourceRow.getHeightInPoints());
            short lastCellNum = sourceRow.getLastCellNum();
            IntStream.range(0, lastCellNum).forEachOrdered((j) -> {
               Cell cell = row.createCell(j);
               cell.setCellStyle(sourceRow.getCell(j).getCellStyle());
            });
         });
      }
   }

   public static void removeRow(Row row) {
      if (row != null) {
         int rowIndex = row.getRowNum();
         Sheet sheet = row.getSheet();
         int lastRow = sheet.getLastRowNum();
         if (rowIndex >= 0 && rowIndex < lastRow) {
            List<CellRangeAddress> updateMergedRegions = new ArrayList();
            IntStream.range(0, sheet.getNumMergedRegions()).forEach((i) -> {
               CellRangeAddress mr = sheet.getMergedRegion(i);
               if (mr.containsRow(rowIndex)) {
                  if (mr.getFirstRow() != mr.getLastRow() - 1 || mr.getFirstColumn() != mr.getLastColumn()) {
                     updateMergedRegions.add(mr);
                  }
               }
            });
            sheet.shiftRows(rowIndex + 1, lastRow, -1);
            List<Integer> removeMergedRegions = (List)IntStream.range(0, sheet.getNumMergedRegions()).filter((i) -> {
               return updateMergedRegions.stream().anyMatch((umr) -> {
                  return CellRangeUtil.contains(umr, sheet.getMergedRegion(i));
               });
            }).boxed().collect(Collectors.toList());
            sheet.removeMergedRegions(removeMergedRegions);
            updateMergedRegions.forEach((mr) -> {
               mr.setLastRow(mr.getLastRow() - 1);
               sheet.addMergedRegion(mr);
            });
            sheet.validateMergedRegions();
         }

         if (rowIndex == lastRow) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
               sheet.removeRow(removingRow);
            }
         }

      }
   }
}
