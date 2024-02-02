package cn.hutool.db.sql;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NamedSql {
   private static final char[] NAME_START_CHARS = new char[]{':', '@', '?'};
   private String sql;
   private final List<Object> params = new LinkedList();

   public NamedSql(String namedSql, Map<String, Object> paramMap) {
      this.parse(namedSql, paramMap);
   }

   public String getSql() {
      return this.sql;
   }

   public Object[] getParams() {
      return this.params.toArray(new Object[0]);
   }

   public List<Object> getParamList() {
      return this.params;
   }

   private void parse(String namedSql, Map<String, Object> paramMap) {
      if (MapUtil.isEmpty(paramMap)) {
         this.sql = namedSql;
      } else {
         int len = namedSql.length();
         StrBuilder name = StrUtil.strBuilder();
         StrBuilder sqlBuilder = StrUtil.strBuilder();
         Character nameStartChar = null;

         for(int i = 0; i < len; ++i) {
            char c = namedSql.charAt(i);
            if (ArrayUtil.contains(NAME_START_CHARS, c)) {
               this.replaceVar(nameStartChar, name, sqlBuilder, paramMap);
               nameStartChar = c;
            } else if (null != nameStartChar) {
               if (isGenerateChar(c)) {
                  name.append(c);
               } else {
                  this.replaceVar(nameStartChar, name, sqlBuilder, paramMap);
                  nameStartChar = null;
                  sqlBuilder.append(c);
               }
            } else {
               sqlBuilder.append(c);
            }
         }

         if (!name.isEmpty()) {
            this.replaceVar(nameStartChar, name, sqlBuilder, paramMap);
         }

         this.sql = sqlBuilder.toString();
      }
   }

   private void replaceVar(Character nameStartChar, StrBuilder name, StrBuilder sqlBuilder, Map<String, Object> paramMap) {
      if (name.isEmpty()) {
         if (null != nameStartChar) {
            sqlBuilder.append((Object)nameStartChar);
         }

      } else {
         String nameStr = name.toString();
         if (paramMap.containsKey(nameStr)) {
            Object paramValue = paramMap.get(nameStr);
            if (ArrayUtil.isArray(paramValue) && StrUtil.contains(sqlBuilder, "in")) {
               int length = ArrayUtil.length(paramValue);

               for(int i = 0; i < length; ++i) {
                  if (0 != i) {
                     sqlBuilder.append(',');
                  }

                  sqlBuilder.append('?');
                  this.params.add(ArrayUtil.get(paramValue, i));
               }
            } else {
               sqlBuilder.append('?');
               this.params.add(paramValue);
            }
         } else {
            sqlBuilder.append((Object)nameStartChar).append((CharSequence)name);
         }

         name.clear();
      }
   }

   private static boolean isGenerateChar(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= '0' && c <= '9';
   }
}
