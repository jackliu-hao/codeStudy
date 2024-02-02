package cn.hutool.poi.excel.sax;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SheetRidReader extends DefaultHandler {
   private static final String TAG_NAME = "sheet";
   private static final String RID_ATTR = "r:id";
   private static final String SHEET_ID_ATTR = "sheetId";
   private static final String NAME_ATTR = "name";
   private final Map<Integer, Integer> ID_RID_MAP = new LinkedHashMap();
   private final Map<String, Integer> NAME_RID_MAP = new LinkedHashMap();

   public static SheetRidReader parse(XSSFReader reader) {
      return (new SheetRidReader()).read(reader);
   }

   public SheetRidReader read(XSSFReader xssfReader) {
      InputStream workbookData = null;

      try {
         workbookData = xssfReader.getWorkbookData();
         ExcelSaxUtil.readFrom(workbookData, this);
      } catch (InvalidFormatException var8) {
         throw new POIException(var8);
      } catch (IOException var9) {
         throw new IORuntimeException(var9);
      } finally {
         IoUtil.close(workbookData);
      }

      return this;
   }

   public Integer getRidBySheetId(int sheetId) {
      return (Integer)this.ID_RID_MAP.get(sheetId);
   }

   public Integer getRidBySheetIdBase0(int sheetId) {
      Integer rid = this.getRidBySheetId(sheetId + 1);
      return null != rid ? rid - 1 : null;
   }

   public Integer getRidByName(String sheetName) {
      return (Integer)this.NAME_RID_MAP.get(sheetName);
   }

   public Integer getRidByNameBase0(String sheetName) {
      Integer rid = this.getRidByName(sheetName);
      return null != rid ? rid - 1 : null;
   }

   public Integer getRidByIndex(int index) {
      return (Integer)CollUtil.get(this.NAME_RID_MAP.values(), index);
   }

   public Integer getRidByIndexBase0(int index) {
      Integer rid = (Integer)CollUtil.get(this.NAME_RID_MAP.values(), index);
      return null != rid ? rid - 1 : null;
   }

   public List<String> getSheetNames() {
      return ListUtil.toList((Collection)this.NAME_RID_MAP.keySet());
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if ("sheet".equalsIgnoreCase(localName)) {
         String ridStr = attributes.getValue("r:id");
         if (StrUtil.isEmpty(ridStr)) {
            return;
         }

         int rid = Integer.parseInt(StrUtil.removePrefixIgnoreCase(ridStr, "rId"));
         String name = attributes.getValue("name");
         if (StrUtil.isNotEmpty(name)) {
            this.NAME_RID_MAP.put(name, rid);
         }

         String sheetIdStr = attributes.getValue("sheetId");
         if (StrUtil.isNotEmpty(sheetIdStr)) {
            this.ID_RID_MAP.put(Integer.parseInt(sheetIdStr), rid);
         }
      }

   }
}
