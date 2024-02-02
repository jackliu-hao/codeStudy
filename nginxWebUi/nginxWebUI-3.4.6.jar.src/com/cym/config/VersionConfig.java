/*    */ package com.cym.config;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.http.HttpUtil;
/*    */ import cn.hutool.json.JSONUtil;
/*    */ import com.cym.model.Version;
/*    */ import com.cym.utils.SystemTool;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.net.URLDecoder;
/*    */ import java.util.Properties;
/*    */ import org.apache.maven.model.Model;
/*    */ import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
/*    */ import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
/*    */ import org.noear.solon.annotation.Configuration;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ @Configuration
/*    */ public class VersionConfig
/*    */ {
/* 28 */   Logger logger = LoggerFactory.getLogger(VersionConfig.class);
/*    */   
/*    */   public Version newVersion;
/*    */   
/*    */   public String currentVersion;
/*    */ 
/*    */   
/*    */   @Init
/*    */   public void checkVersion() {
/*    */     try {
/* 38 */       this.currentVersion = getFromPom();
/* 39 */     } catch (Exception e) {
/* 40 */       this.logger.info(e.getMessage(), e);
/*    */     } 
/*    */ 
/*    */     
/*    */     try {
/* 45 */       String json = HttpUtil.get("https://www.nginxwebui.cn/download/version.json", 1000);
/* 46 */       if (StrUtil.isNotEmpty(json)) {
/* 47 */         this.newVersion = (Version)JSONUtil.toBean(json, Version.class);
/*    */       }
/* 49 */     } catch (Exception e) {
/* 50 */       this.logger.error("更新服务器不可访问");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getFromPom() throws FileNotFoundException, IOException, XmlPullParserException {
/* 59 */     String jarPath = VersionConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
/* 60 */     jarPath = URLDecoder.decode(jarPath, "UTF-8");
/*    */     try {
/* 62 */       URL url = new URL("jar:file:" + jarPath + "!/META-INF/maven/com.cym/nginxWebUI/pom.properties");
/* 63 */       InputStream inputStream = url.openStream();
/* 64 */       Properties properties = new Properties();
/* 65 */       properties.load(inputStream);
/* 66 */       String version = properties.getProperty("version");
/* 67 */       return version;
/* 68 */     } catch (Exception e) {
/*    */       
/* 70 */       MavenXpp3Reader reader = new MavenXpp3Reader();
/* 71 */       String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
/* 72 */       if (SystemTool.isWindows().booleanValue() && basePath.startsWith("/")) {
/* 73 */         basePath = basePath.substring(1);
/*    */       }
/* 75 */       if (basePath.indexOf("/target/") != -1) {
/* 76 */         basePath = basePath.substring(0, basePath.indexOf("/target/"));
/*    */       }
/* 78 */       Model model = reader.read(new FileReader(new File(basePath, "pom.xml")));
/* 79 */       String version = model.getVersion();
/* 80 */       return version;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\config\VersionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */