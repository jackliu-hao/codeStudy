package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.XmlUtil;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.TimeZone;
import org.w3c.dom.Node;

public class StringConverter extends AbstractConverter<String> {
   private static final long serialVersionUID = 1L;

   protected String convertInternal(Object value) {
      if (value instanceof TimeZone) {
         return ((TimeZone)value).getID();
      } else if (value instanceof Node) {
         return XmlUtil.toStr((Node)value);
      } else if (value instanceof Clob) {
         return clobToStr((Clob)value);
      } else if (value instanceof Blob) {
         return blobToStr((Blob)value);
      } else {
         return value instanceof Type ? ((Type)value).getTypeName() : this.convertToStr(value);
      }
   }

   private static String clobToStr(Clob clob) {
      Reader reader = null;

      String var2;
      try {
         reader = clob.getCharacterStream();
         var2 = IoUtil.read(reader);
      } catch (SQLException var6) {
         throw new ConvertException(var6);
      } finally {
         IoUtil.close(reader);
      }

      return var2;
   }

   private static String blobToStr(Blob blob) {
      InputStream in = null;

      String var2;
      try {
         in = blob.getBinaryStream();
         var2 = IoUtil.read(in, CharsetUtil.CHARSET_UTF_8);
      } catch (SQLException var6) {
         throw new ConvertException(var6);
      } finally {
         IoUtil.close(in);
      }

      return var2;
   }
}
