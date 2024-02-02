package cn.hutool.db.sql;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.Entity;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqlUtil {
   public static String buildEqualsWhere(Entity entity, List<Object> paramValues) {
      if (null != entity && !entity.isEmpty()) {
         StringBuilder sb = new StringBuilder(" WHERE ");
         boolean isNotFirst = false;
         Iterator var4 = entity.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var4.next();
            if (isNotFirst) {
               sb.append(" and ");
            } else {
               isNotFirst = true;
            }

            sb.append("`").append((String)entry.getKey()).append("`").append(" = ?");
            paramValues.add(entry.getValue());
         }

         return sb.toString();
      } else {
         return "";
      }
   }

   public static Condition[] buildConditions(Entity entity) {
      if (null != entity && !entity.isEmpty()) {
         Condition[] conditions = new Condition[entity.size()];
         int i = 0;
         Iterator var4 = entity.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var4.next();
            Object value = entry.getValue();
            if (value instanceof Condition) {
               conditions[i++] = (Condition)value;
            } else {
               conditions[i++] = new Condition((String)entry.getKey(), value);
            }
         }

         return conditions;
      } else {
         return null;
      }
   }

   public static String buildLikeValue(String value, Condition.LikeType likeType, boolean withLikeKeyword) {
      if (null == value) {
         return null;
      } else {
         StringBuilder likeValue = StrUtil.builder(new CharSequence[]{withLikeKeyword ? "LIKE " : ""});
         switch (likeType) {
            case StartWith:
               likeValue.append(value).append('%');
               break;
            case EndWith:
               likeValue.append('%').append(value);
               break;
            case Contains:
               likeValue.append('%').append(value).append('%');
         }

         return likeValue.toString();
      }
   }

   public static String formatSql(String sql) {
      return SqlFormatter.format(sql);
   }

   public static String rowIdToString(RowId rowId) {
      return StrUtil.str(rowId.getBytes(), CharsetUtil.CHARSET_ISO_8859_1);
   }

   public static String clobToStr(Clob clob) {
      Reader reader = null;

      String var2;
      try {
         reader = clob.getCharacterStream();
         var2 = IoUtil.read(reader);
      } catch (SQLException var6) {
         throw new DbRuntimeException(var6);
      } finally {
         IoUtil.close(reader);
      }

      return var2;
   }

   public static String blobToStr(Blob blob, Charset charset) {
      InputStream in = null;

      String var3;
      try {
         in = blob.getBinaryStream();
         var3 = IoUtil.read(in, charset);
      } catch (SQLException var7) {
         throw new DbRuntimeException(var7);
      } finally {
         IoUtil.close(in);
      }

      return var3;
   }

   public static Blob createBlob(Connection conn, InputStream dataStream, boolean closeAfterUse) {
      OutputStream out = null;

      Blob blob;
      try {
         blob = conn.createBlob();
         out = blob.setBinaryStream(1L);
         IoUtil.copy(dataStream, out);
      } catch (SQLException var9) {
         throw new DbRuntimeException(var9);
      } finally {
         IoUtil.close(out);
         if (closeAfterUse) {
            IoUtil.close(dataStream);
         }

      }

      return blob;
   }

   public static Blob createBlob(Connection conn, byte[] data) {
      try {
         Blob blob = conn.createBlob();
         blob.setBytes(0L, data);
         return blob;
      } catch (SQLException var4) {
         throw new DbRuntimeException(var4);
      }
   }

   public static Date toSqlDate(java.util.Date date) {
      return new Date(date.getTime());
   }

   public static Timestamp toSqlTimestamp(java.util.Date date) {
      return new Timestamp(date.getTime());
   }
}
