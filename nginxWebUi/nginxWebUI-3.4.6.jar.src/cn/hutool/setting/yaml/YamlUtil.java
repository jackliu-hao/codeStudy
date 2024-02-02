/*     */ package cn.hutool.setting.yaml;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Dict;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import org.yaml.snakeyaml.DumperOptions;
/*     */ import org.yaml.snakeyaml.Yaml;
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
/*     */ public class YamlUtil
/*     */ {
/*     */   public static Dict loadByPath(String path) {
/*  29 */     return loadByPath(path, Dict.class);
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
/*     */   public static <T> T loadByPath(String path, Class<T> type) {
/*  41 */     return load(ResourceUtil.getStream(path), type);
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
/*     */   public static <T> T load(InputStream in, Class<T> type) {
/*  53 */     return load((Reader)IoUtil.getBomReader(in), type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Dict load(Reader reader) {
/*  63 */     return load(reader, Dict.class);
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
/*     */   public static <T> T load(Reader reader, Class<T> type) {
/*  75 */     return load(reader, type, true);
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
/*     */   public static <T> T load(Reader reader, Class<T> type, boolean isCloseReader) {
/*     */     Class<Object> clazz;
/*  88 */     Assert.notNull(reader, "Reader must be not null !", new Object[0]);
/*  89 */     if (null == type)
/*     */     {
/*  91 */       clazz = Object.class;
/*     */     }
/*     */     
/*  94 */     Yaml yaml = new Yaml();
/*     */     try {
/*  96 */       return (T)yaml.loadAs(reader, clazz);
/*     */     } finally {
/*  98 */       if (isCloseReader) {
/*  99 */         IoUtil.close(reader);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dump(Object object, Writer writer) {
/* 111 */     DumperOptions options = new DumperOptions();
/* 112 */     options.setIndent(2);
/* 113 */     options.setPrettyFlow(true);
/* 114 */     options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
/*     */     
/* 116 */     dump(object, writer, options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dump(Object object, Writer writer, DumperOptions dumperOptions) {
/* 127 */     if (null == dumperOptions) {
/* 128 */       dumperOptions = new DumperOptions();
/*     */     }
/* 130 */     Yaml yaml = new Yaml(dumperOptions);
/* 131 */     yaml.dump(object, writer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\yaml\YamlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */