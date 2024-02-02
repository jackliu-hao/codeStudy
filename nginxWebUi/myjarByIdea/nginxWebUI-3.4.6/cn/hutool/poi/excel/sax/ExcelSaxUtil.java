package cn.hutool.poi.excel.sax;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelDateUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.exceptions.POIException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ExcelSaxUtil {
   public static final char CELL_FILL_CHAR = '@';
   public static final int MAX_CELL_BIT = 3;

   public static ExcelSaxReader<?> createSaxReader(boolean isXlsx, RowHandler rowHandler) {
      return (ExcelSaxReader)(isXlsx ? new Excel07SaxReader(rowHandler) : new Excel03SaxReader(rowHandler));
   }

   public static Object getDataValue(CellDataType cellDataType, String value, SharedStrings sharedStrings, String numFmtString) {
      if (null == value) {
         return null;
      } else {
         if (null == cellDataType) {
            cellDataType = CellDataType.NULL;
         }

         Object result;
         switch (cellDataType) {
            case BOOL:
               result = value.charAt(0) != '0';
               break;
            case ERROR:
               result = StrUtil.format("\\\"ERROR: {} ", new Object[]{value});
               break;
            case FORMULA:
               result = StrUtil.format("\"{}\"", new Object[]{value});
               break;
            case INLINESTR:
               result = (new XSSFRichTextString(value)).toString();
               break;
            case SSTINDEX:
               try {
                  int index = Integer.parseInt(value);
                  result = sharedStrings.getItemAt(index).getString();
               } catch (NumberFormatException var8) {
                  result = value;
               }
               break;
            case NUMBER:
               try {
                  result = getNumberValue(value, numFmtString);
               } catch (NumberFormatException var7) {
                  result = value;
               }
               break;
            case DATE:
               try {
                  result = getDateValue(value);
               } catch (Exception var6) {
                  result = value;
               }
               break;
            default:
               result = value;
         }

         return result;
      }
   }

   public static String formatCellContent(String value, int numFmtIndex, String numFmtString) {
      if (null != numFmtString) {
         try {
            value = (new DataFormatter()).formatRawCellContents(Double.parseDouble(value), numFmtIndex, numFmtString);
         } catch (NumberFormatException var4) {
         }
      }

      return value;
   }

   public static int countNullCell(String preRef, String ref) {
      String preXfd = StrUtil.nullToDefault(preRef, "@").replaceAll("\\d+", "");
      String xfd = StrUtil.nullToDefault(ref, "@").replaceAll("\\d+", "");
      preXfd = StrUtil.fillBefore(preXfd, '@', 3);
      xfd = StrUtil.fillBefore(xfd, '@', 3);
      char[] preLetter = preXfd.toCharArray();
      char[] letter = xfd.toCharArray();
      int res = (letter[0] - preLetter[0]) * 26 * 26 + (letter[1] - preLetter[1]) * 26 + (letter[2] - preLetter[2]);
      return res - 1;
   }

   public static void readFrom(InputStream xmlDocStream, ContentHandler handler) throws DependencyException, POIException, IORuntimeException {
      XMLReader xmlReader;
      try {
         xmlReader = XMLHelper.newXMLReader();
      } catch (ParserConfigurationException | SAXException var6) {
         if (var6.getMessage().contains("org.apache.xerces.parsers.SAXParser")) {
            throw new DependencyException(var6, "You need to add 'xerces:xercesImpl' to your project and version >= 2.11.0", new Object[0]);
         }

         throw new POIException(var6);
      }

      xmlReader.setContentHandler(handler);

      try {
         xmlReader.parse(new InputSource(xmlDocStream));
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      } catch (SAXException var5) {
         throw new POIException(var5);
      }
   }

   public static boolean isDateFormat(CellValueRecordInterface cell, FormatTrackingHSSFListener formatListener) {
      int formatIndex = formatListener.getFormatIndex(cell);
      String formatString = formatListener.getFormatString(cell);
      return isDateFormat(formatIndex, formatString);
   }

   public static boolean isDateFormat(int formatIndex, String formatString) {
      return ExcelDateUtil.isDateFormat(formatIndex, formatString);
   }

   public static DateTime getDateValue(String value) {
      return getDateValue(Double.parseDouble(value));
   }

   public static DateTime getDateValue(double value) {
      return DateUtil.date(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value, false));
   }

   public static Object getNumberOrDateValue(CellValueRecordInterface cell, double value, FormatTrackingHSSFListener formatListener) {
      return isDateFormat(cell, formatListener) ? getDateValue(value) : getNumberValue(value, formatListener.getFormatString(cell));
   }

   private static Number getNumberValue(String value, String numFmtString) {
      return StrUtil.isBlank(value) ? null : getNumberValue(Double.parseDouble(value), numFmtString);
   }

   private static Number getNumberValue(double numValue, String numFmtString) {
      if (null != numFmtString && !StrUtil.contains(numFmtString, '.')) {
         long longPart = (long)numValue;
         if ((double)longPart == numValue) {
            return longPart;
         }
      }

      return numValue;
   }
}
