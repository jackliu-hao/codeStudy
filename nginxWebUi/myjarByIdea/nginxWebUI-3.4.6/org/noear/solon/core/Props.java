package org.noear.solon.core;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.noear.solon.Utils;

public class Props extends Properties {
   private Set<BiConsumer<String, String>> _changeEvent = new HashSet();

   public Props() {
   }

   public Props(Properties defaults) {
      super(defaults);
   }

   public synchronized int size() {
      return this.defaults == null ? super.size() : super.size() + this.defaults.size();
   }

   public String get(String key) {
      return this.getProperty(key);
   }

   public String getByExpr(String expr) {
      String name = expr;
      if (expr.startsWith("${") && expr.endsWith("}")) {
         name = expr.substring(2, expr.length() - 1);
      }

      return this.get(name);
   }

   public String getByParse(String expr) {
      if (Utils.isEmpty(expr)) {
         return expr;
      } else {
         int start = expr.indexOf("${");
         if (start < 0) {
            return expr;
         } else {
            int end = expr.indexOf("}");
            String name = expr.substring(start + 2, end);
            String value = this.get(name);
            return expr.substring(0, start) + value + expr.substring(end + 1);
         }
      }
   }

   public String get(String key, String def) {
      return this.getProperty(key, def);
   }

   public boolean getBool(String key, boolean def) {
      return (Boolean)this.getOrDef(key, def, Boolean::parseBoolean);
   }

   public int getInt(String key, int def) {
      return (Integer)this.getOrDef(key, def, Integer::parseInt);
   }

   public long getLong(String key, long def) {
      return (Long)this.getOrDef(key, def, Long::parseLong);
   }

   public Double getDouble(String key, double def) {
      return (Double)this.getOrDef(key, def, Double::parseDouble);
   }

   private <T> T getOrDef(String key, T def, Function<String, T> convert) {
      String temp = this.get(key);
      return Utils.isEmpty(temp) ? def : convert.apply(temp);
   }

   public <T> T getBean(String keyStarts, Class<T> clz) {
      Properties props = this.getProp(keyStarts);
      return PropsConverter.global().convert(props, (Object)null, clz, (Type)null);
   }

   public Props getProp(String keyStarts) {
      Props prop = new Props();
      this.doFind(keyStarts + ".", prop::put);
      if (prop.size() == 0) {
         this.doFind(keyStarts + "[", (k, v) -> {
            prop.put("[" + k, v);
         });
      }

      return prop;
   }

   public Props getPropByExpr(String expr) {
      String name = expr;
      if (expr.startsWith("${") && expr.endsWith("}")) {
         name = expr.substring(2, expr.length() - 1);
      }

      return this.getProp(name);
   }

   public NvMap getXmap(String keyStarts) {
      NvMap map = new NvMap();
      this.doFind(keyStarts + ".", map::put);
      return map;
   }

   private void doFind(String keyStarts, BiConsumer<String, String> setFun) {
      int idx2 = keyStarts.length();
      this.forEach((k, v) -> {
         if (k instanceof String && v instanceof String) {
            String keyStr = (String)k;
            if (keyStr.startsWith(keyStarts)) {
               String key = keyStr.substring(idx2);
               setFun.accept(key, (String)v);
               if (key.contains("-")) {
                  String[] ss = key.split("-");
                  StringBuilder sb = new StringBuilder(key.length());
                  sb.append(ss[0]);

                  for(int i = 1; i < ss.length; ++i) {
                     if (ss[i].length() > 1) {
                        sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
                     } else {
                        sb.append(ss[i].toUpperCase());
                     }
                  }

                  setFun.accept(sb.toString(), (String)v);
               }
            }
         }

      });
   }

   public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
      if (this.defaults == null) {
         super.forEach(action);
      } else {
         this.defaults.forEach(action);
         super.forEach((k, v) -> {
            if (!this.defaults.containsKey(k)) {
               action.accept(k, v);
            }

         });
      }

   }

   public void onChange(BiConsumer<String, String> event) {
      this._changeEvent.add(event);
   }

   public synchronized Object put(Object key, Object value) {
      Object obj = super.put(key, value);
      if (key instanceof String && value instanceof String) {
         this._changeEvent.forEach((event) -> {
            event.accept((String)key, (String)value);
         });
      }

      return obj;
   }

   public void loadAdd(URL url) {
      if (url != null) {
         Properties props = Utils.loadProperties(url);
         this.loadAdd(props);
      }

   }

   public void loadAdd(Properties props) {
      this.loadAddDo(props, false);
   }

   protected void loadAddDo(Properties props, boolean toSystem) {
      if (props != null) {
         Iterator var3 = props.entrySet().iterator();

         while(true) {
            Object k1;
            Object v1;
            String key;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  Map.Entry<Object, Object> kv = (Map.Entry)var3.next();
                  k1 = kv.getKey();
                  v1 = kv.getValue();
               } while(!(k1 instanceof String));

               key = (String)k1;
            } while(Utils.isEmpty(key));

            if (v1 instanceof String) {
               String v1Str = (String)v1;
               int symStart = 0;

               while(true) {
                  symStart = v1Str.indexOf("${", symStart);
                  if (symStart < 0) {
                     break;
                  }

                  int symEnd = v1Str.indexOf("}", symStart + 1);
                  if (symEnd <= symStart) {
                     break;
                  }

                  String tmpK = v1Str.substring(symStart + 2, symEnd);
                  String tmpV2 = props.getProperty(tmpK);
                  if (tmpV2 == null) {
                     tmpV2 = this.getProperty(tmpK);
                  }

                  if (tmpV2 == null) {
                     symStart = symEnd;
                  } else {
                     if (symStart > 0) {
                        tmpV2 = v1Str.substring(0, symStart) + tmpV2;
                     }

                     symStart = tmpV2.length();
                     v1Str = tmpV2 + v1Str.substring(symEnd + 1);
                  }
               }

               v1 = v1Str;
            }

            if (v1 != null) {
               if (toSystem) {
                  System.getProperties().put(k1, v1);
               }

               this.put(k1, v1);
            }
         }
      }
   }
}
