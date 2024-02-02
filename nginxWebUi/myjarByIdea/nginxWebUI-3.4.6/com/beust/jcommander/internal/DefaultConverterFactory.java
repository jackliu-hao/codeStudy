package com.beust.jcommander.internal;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.converters.BigDecimalConverter;
import com.beust.jcommander.converters.BooleanConverter;
import com.beust.jcommander.converters.DoubleConverter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.FloatConverter;
import com.beust.jcommander.converters.ISO8601DateConverter;
import com.beust.jcommander.converters.IntegerConverter;
import com.beust.jcommander.converters.LongConverter;
import com.beust.jcommander.converters.PathConverter;
import com.beust.jcommander.converters.StringConverter;
import com.beust.jcommander.converters.URIConverter;
import com.beust.jcommander.converters.URLConverter;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

public class DefaultConverterFactory implements IStringConverterFactory {
   private static Map<Class, Class<? extends IStringConverter<?>>> m_classConverters = Maps.newHashMap();

   public Class<? extends IStringConverter<?>> getConverter(Class forType) {
      return (Class)m_classConverters.get(forType);
   }

   static {
      m_classConverters.put(String.class, StringConverter.class);
      m_classConverters.put(Integer.class, IntegerConverter.class);
      m_classConverters.put(Integer.TYPE, IntegerConverter.class);
      m_classConverters.put(Long.class, LongConverter.class);
      m_classConverters.put(Long.TYPE, LongConverter.class);
      m_classConverters.put(Float.class, FloatConverter.class);
      m_classConverters.put(Float.TYPE, FloatConverter.class);
      m_classConverters.put(Double.class, DoubleConverter.class);
      m_classConverters.put(Double.TYPE, DoubleConverter.class);
      m_classConverters.put(Boolean.class, BooleanConverter.class);
      m_classConverters.put(Boolean.TYPE, BooleanConverter.class);
      m_classConverters.put(File.class, FileConverter.class);
      m_classConverters.put(BigDecimal.class, BigDecimalConverter.class);
      m_classConverters.put(Date.class, ISO8601DateConverter.class);
      m_classConverters.put(Path.class, PathConverter.class);
      m_classConverters.put(URI.class, URIConverter.class);
      m_classConverters.put(URL.class, URLConverter.class);
   }
}
