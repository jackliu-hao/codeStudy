package com.cym.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cym.model.Version;
import com.cym.utils.SystemTool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class VersionConfig {
   Logger logger = LoggerFactory.getLogger(VersionConfig.class);
   public Version newVersion;
   public String currentVersion;

   @Init
   public void checkVersion() {
      try {
         this.currentVersion = this.getFromPom();
      } catch (Exception var3) {
         this.logger.info((String)var3.getMessage(), (Throwable)var3);
      }

      try {
         String json = HttpUtil.get("https://www.nginxwebui.cn/download/version.json", 1000);
         if (StrUtil.isNotEmpty(json)) {
            this.newVersion = (Version)JSONUtil.toBean(json, Version.class);
         }
      } catch (Exception var2) {
         this.logger.error("更新服务器不可访问");
      }

   }

   public String getFromPom() throws FileNotFoundException, IOException, XmlPullParserException {
      String jarPath = VersionConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
      jarPath = URLDecoder.decode(jarPath, "UTF-8");

      try {
         URL url = new URL("jar:file:" + jarPath + "!/META-INF/maven/com.cym/nginxWebUI/pom.properties");
         InputStream inputStream = url.openStream();
         Properties properties = new Properties();
         properties.load(inputStream);
         String version = properties.getProperty("version");
         return version;
      } catch (Exception var7) {
         MavenXpp3Reader reader = new MavenXpp3Reader();
         String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
         if (SystemTool.isWindows() && basePath.startsWith("/")) {
            basePath = basePath.substring(1);
         }

         if (basePath.indexOf("/target/") != -1) {
            basePath = basePath.substring(0, basePath.indexOf("/target/"));
         }

         Model model = reader.read((Reader)(new FileReader(new File(basePath, "pom.xml"))));
         String version = model.getVersion();
         return version;
      }
   }
}
