package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBUtil {
   public static String beanToXml(Object bean) {
      return beanToXml(bean, CharsetUtil.CHARSET_UTF_8, true);
   }

   public static String beanToXml(Object bean, Charset charset, boolean format) {
      StringWriter writer;
      try {
         JAXBContext context = JAXBContext.newInstance(new Class[]{bean.getClass()});
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty("jaxb.formatted.output", format);
         marshaller.setProperty("jaxb.encoding", charset.name());
         writer = new StringWriter();
         marshaller.marshal(bean, writer);
      } catch (Exception var6) {
         throw new UtilException("convertToXml 错误：" + var6.getMessage(), var6);
      }

      return writer.toString();
   }

   public static <T> T xmlToBean(String xml, Class<T> c) {
      return xmlToBean((Reader)StrUtil.getReader(xml), c);
   }

   public static <T> T xmlToBean(File file, Charset charset, Class<T> c) {
      return xmlToBean((Reader)FileUtil.getReader(file, charset), c);
   }

   public static <T> T xmlToBean(Reader reader, Class<T> c) {
      Object var4;
      try {
         JAXBContext context = JAXBContext.newInstance(new Class[]{c});
         Unmarshaller unmarshaller = context.createUnmarshaller();
         var4 = unmarshaller.unmarshal(reader);
      } catch (Exception var8) {
         throw new RuntimeException("convertToJava2 错误：" + var8.getMessage(), var8);
      } finally {
         IoUtil.close(reader);
      }

      return var4;
   }
}
