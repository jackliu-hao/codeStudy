/*     */ package ch.qos.logback.classic.util;
/*     */ 
/*     */ import ch.qos.logback.classic.BasicConfigurator;
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.joran.JoranConfigurator;
/*     */ import ch.qos.logback.classic.spi.Configurator;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.LogbackException;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.InfoStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.status.WarnStatus;
/*     */ import ch.qos.logback.core.util.Loader;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import ch.qos.logback.core.util.StatusListenerConfigHelper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Set;
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
/*     */ public class ContextInitializer
/*     */ {
/*     */   public static final String AUTOCONFIG_FILE = "logback.xml";
/*     */   public static final String TEST_AUTOCONFIG_FILE = "logback-test.xml";
/*     */   public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
/*     */   final LoggerContext loggerContext;
/*     */   
/*     */   public ContextInitializer(LoggerContext loggerContext) {
/*  53 */     this.loggerContext = loggerContext;
/*     */   }
/*     */   
/*     */   public void configureByResource(URL url) throws JoranException {
/*  57 */     if (url == null) {
/*  58 */       throw new IllegalArgumentException("URL argument cannot be null");
/*     */     }
/*  60 */     String urlString = url.toString();
/*  61 */     if (urlString.endsWith("xml")) {
/*  62 */       JoranConfigurator configurator = new JoranConfigurator();
/*  63 */       configurator.setContext((Context)this.loggerContext);
/*  64 */       configurator.doConfigure(url);
/*     */     } else {
/*  66 */       throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be .xml");
/*     */     } 
/*     */   }
/*     */   
/*     */   void joranConfigureByResource(URL url) throws JoranException {
/*  71 */     JoranConfigurator configurator = new JoranConfigurator();
/*  72 */     configurator.setContext((Context)this.loggerContext);
/*  73 */     configurator.doConfigure(url);
/*     */   }
/*     */   
/*     */   private URL findConfigFileURLFromSystemProperties(ClassLoader classLoader, boolean updateStatus) {
/*  77 */     String logbackConfigFile = OptionHelper.getSystemProperty("logback.configurationFile");
/*  78 */     if (logbackConfigFile != null) {
/*  79 */       URL result = null;
/*     */       try {
/*  81 */         result = new URL(logbackConfigFile);
/*  82 */         return result;
/*  83 */       } catch (MalformedURLException e) {
/*     */ 
/*     */         
/*  86 */         result = Loader.getResource(logbackConfigFile, classLoader);
/*  87 */         if (result != null) {
/*  88 */           return result;
/*     */         }
/*  90 */         File f = new File(logbackConfigFile);
/*  91 */         if (f.exists() && f.isFile()) {
/*     */           try {
/*  93 */             result = f.toURI().toURL();
/*  94 */             return result;
/*  95 */           } catch (MalformedURLException malformedURLException) {}
/*     */         }
/*     */       } finally {
/*     */         
/*  99 */         if (updateStatus) {
/* 100 */           statusOnResourceSearch(logbackConfigFile, classLoader, result);
/*     */         }
/*     */       } 
/*     */     } 
/* 104 */     return null;
/*     */   }
/*     */   
/*     */   public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
/* 108 */     ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
/* 109 */     URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
/* 110 */     if (url != null) {
/* 111 */       return url;
/*     */     }
/*     */     
/* 114 */     url = getResource("logback-test.xml", myClassLoader, updateStatus);
/* 115 */     if (url != null) {
/* 116 */       return url;
/*     */     }
/*     */     
/* 119 */     return getResource("logback.xml", myClassLoader, updateStatus);
/*     */   }
/*     */   
/*     */   private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
/* 123 */     URL url = Loader.getResource(filename, myClassLoader);
/* 124 */     if (updateStatus) {
/* 125 */       statusOnResourceSearch(filename, myClassLoader, url);
/*     */     }
/* 127 */     return url;
/*     */   }
/*     */   
/*     */   public void autoConfig() throws JoranException {
/* 131 */     StatusListenerConfigHelper.installIfAsked((Context)this.loggerContext);
/* 132 */     URL url = findURLOfDefaultConfigurationFile(true);
/* 133 */     if (url != null) {
/* 134 */       configureByResource(url);
/*     */     } else {
/* 136 */       Configurator c = EnvUtil.<Configurator>loadFromServiceLoader(Configurator.class);
/* 137 */       if (c != null) {
/*     */         try {
/* 139 */           c.setContext((Context)this.loggerContext);
/* 140 */           c.configure(this.loggerContext);
/* 141 */         } catch (Exception e) {
/* 142 */           throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", new Object[] { (c != null) ? c.getClass()
/* 143 */                   .getCanonicalName() : "null" }), e);
/*     */         } 
/*     */       } else {
/* 146 */         BasicConfigurator basicConfigurator = new BasicConfigurator();
/* 147 */         basicConfigurator.setContext((Context)this.loggerContext);
/* 148 */         basicConfigurator.configure(this.loggerContext);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url) {
/* 154 */     StatusManager sm = this.loggerContext.getStatusManager();
/* 155 */     if (url == null) {
/* 156 */       sm.add((Status)new InfoStatus("Could NOT find resource [" + resourceName + "]", this.loggerContext));
/*     */     } else {
/* 158 */       sm.add((Status)new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", this.loggerContext));
/* 159 */       multiplicityWarning(resourceName, classLoader);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
/* 164 */     Set<URL> urlSet = null;
/* 165 */     StatusManager sm = this.loggerContext.getStatusManager();
/*     */     try {
/* 167 */       urlSet = Loader.getResources(resourceName, classLoader);
/* 168 */     } catch (IOException e) {
/* 169 */       sm.add((Status)new ErrorStatus("Failed to get url list for resource [" + resourceName + "]", this.loggerContext, e));
/*     */     } 
/* 171 */     if (urlSet != null && urlSet.size() > 1) {
/* 172 */       sm.add((Status)new WarnStatus("Resource [" + resourceName + "] occurs multiple times on the classpath.", this.loggerContext));
/* 173 */       for (URL url : urlSet)
/* 174 */         sm.add((Status)new WarnStatus("Resource [" + resourceName + "] occurs at [" + url.toString() + "]", this.loggerContext)); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\ContextInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */