package org.noear.solon;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.PluginUtil;

public final class SolonProps extends Props {
   private NvMap args;
   private Class<?> source;
   private URL sourceLocation;
   private final List<PluginEntity> plugs = new ArrayList();
   private boolean isDebugMode;
   private boolean isDriftMode;
   private boolean isFilesMode;
   private boolean isWhiteMode;
   private boolean isSetupMode;
   private boolean isAloneMode;
   private String env;
   private Locale locale;
   private String extend;
   private String extendFilter;
   private String appName;
   private String appGroup;
   private String appNamespace;
   private String appTitle;
   private int serverPort;
   private String serverHost;

   public SolonProps() {
      super(System.getProperties());
   }

   public SolonProps load(Class<?> source, NvMap args) throws Exception {
      this.args = args;
      this.source = source;
      this.sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();
      this.args.forEach((k, v) -> {
         if (k.contains(".")) {
            System.setProperty(k, v);
         }

      });
      Properties sysPropOrg = new Properties();
      System.getProperties().forEach((k, v) -> {
         sysPropOrg.put(k, v);
      });
      this.loadInit(Utils.getResource("application.properties"), sysPropOrg);
      this.loadInit(Utils.getResource("application.yml"), sysPropOrg);
      this.loadInit(Utils.getResource("app.properties"), sysPropOrg);
      this.loadInit(Utils.getResource("app.yml"), sysPropOrg);
      this.loadEnv("solon.");
      this.env = this.getArg("env");
      if (Utils.isEmpty(this.env)) {
         this.env = this.getArg("profiles.active");
      }

      if (Utils.isNotEmpty(this.env)) {
         this.loadInit(Utils.getResource("application-" + this.env + ".properties"), sysPropOrg);
         this.loadInit(Utils.getResource("application-" + this.env + ".yml"), sysPropOrg);
         this.loadInit(Utils.getResource("app-" + this.env + ".properties"), sysPropOrg);
         this.loadInit(Utils.getResource("app-" + this.env + ".yml"), sysPropOrg);
      }

      String extConfig = this.getArg("extend.config");
      if (Utils.isNotEmpty(extConfig)) {
         this.loadInit((new File(extConfig)).toURI().toURL(), sysPropOrg);
      }

      this.isFilesMode = !this.sourceLocation.getPath().endsWith(".jar") && !this.sourceLocation.getPath().contains(".jar!/") && !this.sourceLocation.getPath().endsWith(".zip") && !this.sourceLocation.getPath().contains(".zip!/");
      this.isDebugMode = "1".equals(this.getArg("debug"));
      this.isSetupMode = "1".equals(this.getArg("setup"));
      this.isWhiteMode = "1".equals(this.getArg("white"));
      this.isDriftMode = "1".equals(this.getArg("drift"));
      this.isAloneMode = "1".equals(this.getArg("alone"));
      if (this.isDebugMode()) {
         System.setProperty("debug", "1");
      }

      this.extend = this.getArg("extend");
      this.extendFilter = this.getArg("extend.filter");
      String localeStr = this.getArg("locale");
      if (Utils.isNotEmpty(localeStr)) {
         this.locale = Utils.toLocale(localeStr);
         Locale.setDefault(this.locale);
      } else {
         this.locale = Locale.getDefault();
      }

      this.appName = this.getArg("app.name");
      this.appGroup = this.getArg("app.group");
      this.appNamespace = this.getArg("app.namespace");
      this.appTitle = this.getArg("app.title");
      return this;
   }

   private String getArg(String name) {
      return this.getArg(name, (String)null);
   }

   private String getArg(String name, String def) {
      String tmp = (String)this.args.get(name);
      if (Utils.isEmpty(tmp)) {
         tmp = this.get("solon." + name);
      }

      return Utils.isEmpty(tmp) ? def : tmp;
   }

   public SolonProps loadEnv(String keyStarts) {
      System.getenv().forEach((k, v) -> {
         if (k.startsWith(keyStarts)) {
            this.setProperty(k, v);
            System.setProperty(k, v);
         }

      });
      return this;
   }

   public void loadAdd(String url) {
      this.loadAdd((URL)Utils.getResource(url));
   }

   public void loadAdd(Properties props) {
      this.loadAddDo(props, true);
   }

   protected void loadInit(URL url, Properties sysPropOrg) {
      if (url != null) {
         Properties props = Utils.loadProperties(url);
         if (props == null) {
            return;
         }

         Iterator var4 = sysPropOrg.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry kv = (Map.Entry)var4.next();
            if (kv.getKey() instanceof String) {
               String key = (String)kv.getKey();
               if (!Utils.isEmpty(key) && props.containsKey(key)) {
                  props.put(key, kv.getValue());
               }
            }
         }

         this.loadAdd(props);
      }

   }

   protected void plugsScan(List<ClassLoader> classLoaders) {
      Iterator var2 = classLoaders.iterator();

      List var10002;
      while(var2.hasNext()) {
         ClassLoader classLoader = (ClassLoader)var2.next();
         var10002 = this.plugs;
         PluginUtil.scanPlugins(classLoader, (String)null, var10002::add);
      }

      JarClassLoader var10000 = JarClassLoader.global();
      var10002 = this.plugs;
      PluginUtil.findPlugins(var10000, this, var10002::add);
      this.plugsSort();
   }

   public Class<?> source() {
      return this.source;
   }

   public URL sourceLocation() {
      return this.sourceLocation;
   }

   public NvMap argx() {
      return this.args;
   }

   public List<PluginEntity> plugs() {
      return this.plugs;
   }

   public void plugsSort() {
      if (this.plugs.size() > 0) {
         this.plugs.sort(Comparator.comparingInt(PluginEntity::getPriority).reversed());
      }

   }

   public int serverPort() {
      if (this.serverPort == 0) {
         this.serverPort = this.getInt("server.port", 8080);
      }

      return this.serverPort;
   }

   public String serverHost() {
      if (this.serverHost == null) {
         this.serverHost = this.get("server.host", "");
      }

      return this.serverHost;
   }

   public String env() {
      return this.env;
   }

   public Locale locale() {
      return this.locale;
   }

   public String extend() {
      return this.extend;
   }

   public String extendFilter() {
      return this.extendFilter;
   }

   public String appName() {
      return this.appName;
   }

   public String appGroup() {
      return this.appGroup;
   }

   public String appNamespace() {
      return this.appNamespace;
   }

   public String appTitle() {
      return this.appTitle;
   }

   public String version() {
      return "1.9.4";
   }

   public boolean isDebugMode() {
      return this.isDebugMode;
   }

   public boolean isSetupMode() {
      return this.isSetupMode;
   }

   public boolean isFilesMode() {
      return this.isFilesMode;
   }

   public void isFilesMode(boolean isFilesMode) {
      this.isFilesMode = isFilesMode;
   }

   public boolean isDriftMode() {
      return this.isDriftMode;
   }

   public void isDriftMode(boolean isDriftMode) {
      this.isDriftMode = isDriftMode;
   }

   public boolean isAloneMode() {
      return this.isAloneMode;
   }

   public void isAloneMode(boolean isAloneMode) {
      this.isAloneMode = isAloneMode;
   }

   public boolean isWhiteMode() {
      return this.isWhiteMode;
   }

   public void isWhiteMode(boolean isWhiteMode) {
      this.isWhiteMode = isWhiteMode;
   }
}
