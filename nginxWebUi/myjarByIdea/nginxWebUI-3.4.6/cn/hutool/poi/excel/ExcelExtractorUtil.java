package cn.hutool.poi.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExtractorUtil {
   public static ExcelExtractor getExtractor(Workbook wb) {
      Object extractor;
      if (wb instanceof HSSFWorkbook) {
         extractor = new org.apache.poi.hssf.extractor.ExcelExtractor((HSSFWorkbook)wb);
      } else {
         extractor = new XSSFExcelExtractor((XSSFWorkbook)wb);
      }

      return (ExcelExtractor)extractor;
   }

   public static String readAsText(Workbook wb, boolean withSheetName) {
      ExcelExtractor extractor = getExtractor(wb);
      extractor.setIncludeSheetNames(withSheetName);
      return extractor.getText();
   }
}
