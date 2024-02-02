package cn.hutool.poi.excel;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

public class ExcelPicUtil {
   public static Map<String, PictureData> getPicMap(Workbook workbook, int sheetIndex) {
      Assert.notNull(workbook, "Workbook must be not null !");
      if (sheetIndex < 0) {
         sheetIndex = 0;
      }

      if (workbook instanceof HSSFWorkbook) {
         return getPicMapXls((HSSFWorkbook)workbook, sheetIndex);
      } else if (workbook instanceof XSSFWorkbook) {
         return getPicMapXlsx((XSSFWorkbook)workbook, sheetIndex);
      } else {
         throw new IllegalArgumentException(StrUtil.format("Workbook type [{}] is not supported!", new Object[]{workbook.getClass()}));
      }
   }

   private static Map<String, PictureData> getPicMapXls(HSSFWorkbook workbook, int sheetIndex) {
      Map<String, PictureData> picMap = new HashMap();
      List<HSSFPictureData> pictures = workbook.getAllPictures();
      if (CollectionUtil.isNotEmpty(pictures)) {
         HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
         Iterator var7 = sheet.getDrawingPatriarch().getChildren().iterator();

         while(var7.hasNext()) {
            HSSFShape shape = (HSSFShape)var7.next();
            if (shape instanceof HSSFPicture) {
               int pictureIndex = ((HSSFPicture)shape).getPictureIndex() - 1;
               HSSFClientAnchor anchor = (HSSFClientAnchor)shape.getAnchor();
               picMap.put(StrUtil.format("{}_{}", new Object[]{anchor.getRow1(), anchor.getCol1()}), pictures.get(pictureIndex));
            }
         }
      }

      return picMap;
   }

   private static Map<String, PictureData> getPicMapXlsx(XSSFWorkbook workbook, int sheetIndex) {
      Map<String, PictureData> sheetIndexPicMap = new HashMap();
      XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
      Iterator var5 = sheet.getRelations().iterator();

      while(true) {
         POIXMLDocumentPart dr;
         do {
            if (!var5.hasNext()) {
               return sheetIndexPicMap;
            }

            dr = (POIXMLDocumentPart)var5.next();
         } while(!(dr instanceof XSSFDrawing));

         XSSFDrawing drawing = (XSSFDrawing)dr;
         List<XSSFShape> shapes = drawing.getShapes();
         Iterator var10 = shapes.iterator();

         while(var10.hasNext()) {
            XSSFShape shape = (XSSFShape)var10.next();
            if (shape instanceof XSSFPicture) {
               XSSFPicture pic = (XSSFPicture)shape;
               CTMarker ctMarker = pic.getPreferredSize().getFrom();
               sheetIndexPicMap.put(StrUtil.format("{}_{}", new Object[]{ctMarker.getRow(), ctMarker.getCol()}), pic.getPictureData());
            }
         }
      }
   }
}
