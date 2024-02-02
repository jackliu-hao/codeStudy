package cn.hutool.setting.dialect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.BasicTypeGetter;
import cn.hutool.core.getter.OptBasicTypeGetter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.io.resource.UrlResource;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public final class Props extends Properties implements BasicTypeGetter<String>, OptBasicTypeGetter<String> {
   private static final long serialVersionUID = 1935981579709590740L;
   public static final String EXT_NAME = "properties";
   private Resource resource;
   private WatchMonitor watchMonitor;
   private transient Charset charset;

   public static Props create() {
      return new Props();
   }

   public static Props getProp(String resource) {
      return new Props(resource);
   }

   public static Props getProp(String resource, String charsetName) {
      return new Props(resource, charsetName);
   }

   public static Props getProp(String resource, Charset charset) {
      return new Props(resource, charset);
   }

   public Props() {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
   }

   public Props(String path) {
      this(path, CharsetUtil.CHARSET_ISO_8859_1);
   }

   public Props(String path, String charsetName) {
      this(path, CharsetUtil.charset(charsetName));
   }

   public Props(String path, Charset charset) {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
      Assert.notBlank(path, "Blank properties file path !");
      if (null != charset) {
         this.charset = charset;
      }

      this.load(ResourceUtil.getResourceObj(path));
   }

   public Props(File propertiesFile) {
      this(propertiesFile, StandardCharsets.ISO_8859_1);
   }

   public Props(File propertiesFile, String charsetName) {
      this(propertiesFile, Charset.forName(charsetName));
   }

   public Props(File propertiesFile, Charset charset) {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
      Assert.notNull(propertiesFile, "Null properties file!");
      this.charset = charset;
      this.load((Resource)(new FileResource(propertiesFile)));
   }

   public Props(String path, Class<?> clazz) {
      this(path, clazz, "ISO-8859-1");
   }

   public Props(String path, Class<?> clazz, String charsetName) {
      this(path, clazz, CharsetUtil.charset(charsetName));
   }

   public Props(String path, Class<?> clazz, Charset charset) {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
      Assert.notBlank(path, "Blank properties file path !");
      if (null != charset) {
         this.charset = charset;
      }

      this.load((Resource)(new ClassPathResource(path, clazz)));
   }

   public Props(URL propertiesUrl) {
      this(propertiesUrl, StandardCharsets.ISO_8859_1);
   }

   public Props(URL propertiesUrl, String charsetName) {
      this(propertiesUrl, CharsetUtil.charset(charsetName));
   }

   public Props(URL propertiesUrl, Charset charset) {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
      Assert.notNull(propertiesUrl, "Null properties URL !");
      if (null != charset) {
         this.charset = charset;
      }

      this.load(propertiesUrl);
   }

   public Props(Properties properties) {
      this.charset = CharsetUtil.CHARSET_ISO_8859_1;
      if (MapUtil.isNotEmpty(properties)) {
         this.putAll(properties);
      }

   }

   public void load(URL url) {
      this.load((Resource)(new UrlResource(url)));
   }

   public void load(Resource resource) {
      Assert.notNull(resource, "Props resource must be not null!");
      this.resource = resource;

      try {
         BufferedReader reader = resource.getReader(this.charset);
         Throwable var3 = null;

         try {
            super.load(reader);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (reader != null) {
               if (var3 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  reader.close();
               }
            }

         }

      } catch (IOException var15) {
         throw new IORuntimeException(var15);
      }
   }

   public void load() {
      this.load(this.resource);
   }

   public void autoLoad(boolean autoReload) {
      if (autoReload) {
         Assert.notNull(this.resource, "Properties resource must be not null!");
         if (null != this.watchMonitor) {
            this.watchMonitor.close();
         }

         this.watchMonitor = WatchUtil.createModify((URL)this.resource.getUrl(), new SimpleWatcher() {
            public void onModify(WatchEvent<?> event, Path currentPath) {
               Props.this.load();
            }
         });
         this.watchMonitor.start();
      } else {
         IoUtil.close(this.watchMonitor);
         this.watchMonitor = null;
      }

   }

   public Object getObj(String key, Object defaultValue) {
      return this.getStr(key, null == defaultValue ? null : defaultValue.toString());
   }

   public Object getObj(String key) {
      return this.getObj((String)key, (Object)null);
   }

   public String getStr(String key, String defaultValue) {
      return super.getProperty(key, defaultValue);
   }

   public String getStr(String key) {
      return super.getProperty(key);
   }

   public Integer getInt(String key, Integer defaultValue) {
      return Convert.toInt(this.getStr(key), defaultValue);
   }

   public Integer getInt(String key) {
      return this.getInt((String)key, (Integer)null);
   }

   public Boolean getBool(String key, Boolean defaultValue) {
      return Convert.toBool(this.getStr(key), defaultValue);
   }

   public Boolean getBool(String key) {
      return this.getBool((String)key, (Boolean)null);
   }

   public Long getLong(String key, Long defaultValue) {
      return Convert.toLong(this.getStr(key), defaultValue);
   }

   public Long getLong(String key) {
      return this.getLong((String)key, (Long)null);
   }

   public Character getChar(String key, Character defaultValue) {
      String value = this.getStr(key);
      return StrUtil.isBlank(value) ? defaultValue : value.charAt(0);
   }

   public Character getChar(String key) {
      return this.getChar((String)key, (Character)null);
   }

   public Float getFloat(String key) {
      return this.getFloat((String)key, (Float)null);
   }

   public Float getFloat(String key, Float defaultValue) {
      return Convert.toFloat(this.getStr(key), defaultValue);
   }

   public Double getDouble(String key, Double defaultValue) throws NumberFormatException {
      return Convert.toDouble(this.getStr(key), defaultValue);
   }

   public Double getDouble(String key) throws NumberFormatException {
      return this.getDouble((String)key, (Double)null);
   }

   public Short getShort(String key, Short defaultValue) {
      return Convert.toShort(this.getStr(key), defaultValue);
   }

   public Short getShort(String key) {
      return this.getShort((String)key, (Short)null);
   }

   public Byte getByte(String key, Byte defaultValue) {
      return Convert.toByte(this.getStr(key), defaultValue);
   }

   public Byte getByte(String key) {
      return this.getByte((String)key, (Byte)null);
   }

   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
      String valueStr = this.getStr(key);
      if (StrUtil.isBlank(valueStr)) {
         return defaultValue;
      } else {
         try {
            return new BigDecimal(valueStr);
         } catch (Exception var5) {
            return defaultValue;
         }
      }
   }

   public BigDecimal getBigDecimal(String key) {
      return this.getBigDecimal((String)key, (BigDecimal)null);
   }

   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
      String valueStr = this.getStr(key);
      if (StrUtil.isBlank(valueStr)) {
         return defaultValue;
      } else {
         try {
            return new BigInteger(valueStr);
         } catch (Exception var5) {
            return defaultValue;
         }
      }
   }

   public BigInteger getBigInteger(String key) {
      return this.getBigInteger((String)key, (BigInteger)null);
   }

   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key, E defaultValue) {
      return Convert.toEnum(clazz, this.getStr(key), defaultValue);
   }

   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) {
      return this.getEnum(clazz, (String)key, (Enum)null);
   }

   public Date getDate(String key, Date defaultValue) {
      return Convert.toDate(this.getStr(key), defaultValue);
   }

   public Date getDate(String key) {
      return this.getDate((String)key, (Date)null);
   }

   public String getAndRemoveStr(String... keys) {
      Object value = null;
      String[] var3 = keys;
      int var4 = keys.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String key = var3[var5];
         value = this.remove(key);
         if (null != value) {
            break;
         }
      }

      return (String)value;
   }

   public Properties toProperties() {
      Properties properties = new Properties();
      properties.putAll(this);
      return properties;
   }

   public <T> T toBean(Class<T> beanClass) {
      return this.toBean(beanClass, (String)null);
   }

   public <T> T toBean(Class<T> beanClass, String prefix) {
      T bean = ReflectUtil.newInstanceIfPossible(beanClass);
      return this.fillBean(bean, prefix);
   }

   public <T> T fillBean(T bean, String prefix) {
      prefix = StrUtil.nullToEmpty(StrUtil.addSuffixIfNot(prefix, "."));
      Iterator var4 = this.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<Object, Object> entry = (Map.Entry)var4.next();
         String key = (String)entry.getKey();
         if (StrUtil.startWith(key, prefix)) {
            try {
               BeanUtil.setProperty(bean, StrUtil.subSuf(key, prefix.length()), entry.getValue());
            } catch (Exception var7) {
               StaticLog.debug("Ignore property: [{}]", key);
            }
         }
      }

      return bean;
   }

   public void setProperty(String key, Object value) {
      super.setProperty(key, value.toString());
   }

   public void store(String absolutePath) throws IORuntimeException {
      Writer writer = null;

      try {
         writer = FileUtil.getWriter(absolutePath, this.charset, false);
         super.store(writer, (String)null);
      } catch (IOException var7) {
         throw new IORuntimeException(var7, "Store properties to [{}] error!", new Object[]{absolutePath});
      } finally {
         IoUtil.close(writer);
      }

   }

   public void store(String path, Class<?> clazz) {
      this.store(FileUtil.getAbsolutePath(path, clazz));
   }
}
