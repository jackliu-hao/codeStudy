/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.io.File;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.nio.charset.Charset;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.bind.Marshaller;
/*     */ import javax.xml.bind.Unmarshaller;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JAXBUtil
/*     */ {
/*     */   public static String beanToXml(Object bean) {
/*  47 */     return beanToXml(bean, CharsetUtil.CHARSET_UTF_8, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String beanToXml(Object bean, Charset charset, boolean format) {
/*     */     StringWriter writer;
/*     */     try {
/*  61 */       JAXBContext context = JAXBContext.newInstance(new Class[] { bean.getClass() });
/*  62 */       Marshaller marshaller = context.createMarshaller();
/*  63 */       marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(format));
/*  64 */       marshaller.setProperty("jaxb.encoding", charset.name());
/*  65 */       writer = new StringWriter();
/*  66 */       marshaller.marshal(bean, writer);
/*  67 */     } catch (Exception e) {
/*  68 */       throw new UtilException("convertToXml 错误：" + e.getMessage(), e);
/*     */     } 
/*  70 */     return writer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T xmlToBean(String xml, Class<T> c) {
/*  82 */     return xmlToBean(StrUtil.getReader(xml), c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T xmlToBean(File file, Charset charset, Class<T> c) {
/*  95 */     return xmlToBean(FileUtil.getReader(file, charset), c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T xmlToBean(Reader reader, Class<T> c) {
/*     */     try {
/* 109 */       JAXBContext context = JAXBContext.newInstance(new Class[] { c });
/* 110 */       Unmarshaller unmarshaller = context.createUnmarshaller();
/* 111 */       return (T)unmarshaller.unmarshal(reader);
/* 112 */     } catch (Exception e) {
/* 113 */       throw new RuntimeException("convertToJava2 错误：" + e.getMessage(), e);
/*     */     } finally {
/* 115 */       IoUtil.close(reader);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\JAXBUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */