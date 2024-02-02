package cn.hutool.core.bean;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanPath implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final char[] EXP_CHARS = new char[]{'.', '[', ']'};
   private boolean isStartWith = false;
   protected List<String> patternParts;

   public static BeanPath create(String expression) {
      return new BeanPath(expression);
   }

   public BeanPath(String expression) {
      this.init(expression);
   }

   public List<String> getPatternParts() {
      return this.patternParts;
   }

   public Object get(Object bean) {
      return this.get(this.patternParts, bean, false);
   }

   public void set(Object bean, Object value) {
      this.set(bean, this.patternParts, value);
   }

   public String toString() {
      return this.patternParts.toString();
   }

   private void set(Object bean, List<String> patternParts, Object value) {
      Object subBean = this.get(patternParts, bean, true);
      if (null == subBean) {
         this.set(bean, patternParts.subList(0, patternParts.size() - 1), new HashMap());
         subBean = this.get(patternParts, bean, true);
      }

      BeanUtil.setFieldValue(subBean, (String)patternParts.get(patternParts.size() - 1), value);
   }

   private Object get(List<String> patternParts, Object bean, boolean ignoreLast) {
      int length = patternParts.size();
      if (ignoreLast) {
         --length;
      }

      Object subBean = bean;
      boolean isFirst = true;

      for(int i = 0; i < length; ++i) {
         String patternPart = (String)patternParts.get(i);
         subBean = getFieldValue(subBean, patternPart);
         if (null == subBean) {
            if (!isFirst || this.isStartWith || !BeanUtil.isMatchName(bean, patternPart, true)) {
               return null;
            }

            subBean = bean;
            isFirst = false;
         }
      }

      return subBean;
   }

   private static Object getFieldValue(Object bean, String expression) {
      if (StrUtil.isBlank(expression)) {
         return null;
      } else {
         List keys;
         int i;
         if (StrUtil.contains(expression, ':')) {
            keys = StrUtil.splitTrim(expression, ':');
            int start = Integer.parseInt((String)keys.get(0));
            i = Integer.parseInt((String)keys.get(1));
            int step = 1;
            if (3 == keys.size()) {
               step = Integer.parseInt((String)keys.get(2));
            }

            if (bean instanceof Collection) {
               return CollUtil.sub((Collection)bean, start, i, step);
            } else {
               return ArrayUtil.isArray(bean) ? ArrayUtil.sub(bean, start, i, step) : null;
            }
         } else if (!StrUtil.contains(expression, ',')) {
            return BeanUtil.getFieldValue(bean, expression);
         } else {
            keys = StrUtil.splitTrim(expression, ',');
            if (bean instanceof Collection) {
               return CollUtil.getAny((Collection)bean, (int[])Convert.convert((Class)int[].class, keys));
            } else if (ArrayUtil.isArray(bean)) {
               return ArrayUtil.getAny(bean, (int[])Convert.convert((Class)int[].class, keys));
            } else {
               String[] unWrappedKeys = new String[keys.size()];

               for(i = 0; i < unWrappedKeys.length; ++i) {
                  unWrappedKeys[i] = StrUtil.unWrap((CharSequence)keys.get(i), '\'');
               }

               if (bean instanceof Map) {
                  return MapUtil.getAny((Map)bean, unWrappedKeys);
               } else {
                  Map<String, Object> map = BeanUtil.beanToMap(bean);
                  return MapUtil.getAny(map, unWrappedKeys);
               }
            }
         }
      }
   }

   private void init(String expression) {
      List<String> localPatternParts = new ArrayList();
      int length = expression.length();
      StringBuilder builder = new StringBuilder();
      boolean isNumStart = false;
      boolean isInWrap = false;

      for(int i = 0; i < length; ++i) {
         char c = expression.charAt(i);
         if (0 == i && '$' == c) {
            this.isStartWith = true;
         } else if ('\'' == c) {
            isInWrap = !isInWrap;
         } else if (!isInWrap && ArrayUtil.contains(EXP_CHARS, c)) {
            if (']' == c) {
               if (!isNumStart) {
                  throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find ']' but no '[' !", new Object[]{expression, i}));
               }

               isNumStart = false;
            } else {
               if (isNumStart) {
                  throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find '[' but no ']' !", new Object[]{expression, i}));
               }

               if ('[' == c) {
                  isNumStart = true;
               }
            }

            if (builder.length() > 0) {
               localPatternParts.add(builder.toString());
            }

            builder.setLength(0);
         } else {
            builder.append(c);
         }
      }

      if (isNumStart) {
         throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find '[' but no ']' !", new Object[]{expression, length - 1}));
      } else {
         if (builder.length() > 0) {
            localPatternParts.add(builder.toString());
         }

         this.patternParts = ListUtil.unmodifiable(localPatternParts);
      }
   }
}
