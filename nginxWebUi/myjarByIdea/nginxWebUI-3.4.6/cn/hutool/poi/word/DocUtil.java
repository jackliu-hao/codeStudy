package cn.hutool.poi.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.poi.exceptions.POIException;
import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocUtil {
   public static XWPFDocument create(File file) {
      try {
         return FileUtil.exist(file) ? new XWPFDocument(OPCPackage.open(file)) : new XWPFDocument();
      } catch (InvalidFormatException var2) {
         throw new POIException(var2);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }
}
