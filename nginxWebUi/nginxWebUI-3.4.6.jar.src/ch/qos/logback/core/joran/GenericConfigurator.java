/*     */ package ch.qos.logback.core.joran;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.joran.event.SaxEvent;
/*     */ import ch.qos.logback.core.joran.event.SaxEventRecorder;
/*     */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
/*     */ import ch.qos.logback.core.joran.spi.ElementPath;
/*     */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*     */ import ch.qos.logback.core.joran.spi.Interpreter;
/*     */ import ch.qos.logback.core.joran.spi.JoranException;
/*     */ import ch.qos.logback.core.joran.spi.RuleStore;
/*     */ import ch.qos.logback.core.joran.spi.SimpleRuleStore;
/*     */ import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.status.StatusUtil;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.List;
/*     */ import org.xml.sax.InputSource;
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
/*     */ public abstract class GenericConfigurator
/*     */   extends ContextAwareBase
/*     */ {
/*     */   private BeanDescriptionCache beanDescriptionCache;
/*     */   protected Interpreter interpreter;
/*     */   
/*     */   public final void doConfigure(URL url) throws JoranException {
/*  44 */     InputStream in = null;
/*     */     try {
/*  46 */       informContextOfURLUsedForConfiguration(getContext(), url);
/*  47 */       URLConnection urlConnection = url.openConnection();
/*     */ 
/*     */       
/*  50 */       urlConnection.setUseCaches(false);
/*     */       
/*  52 */       in = urlConnection.getInputStream();
/*  53 */       doConfigure(in, url.toExternalForm());
/*  54 */     } catch (IOException ioe) {
/*  55 */       String errMsg = "Could not open URL [" + url + "].";
/*  56 */       addError(errMsg, ioe);
/*  57 */       throw new JoranException(errMsg, ioe);
/*     */     } finally {
/*  59 */       if (in != null) {
/*     */         try {
/*  61 */           in.close();
/*  62 */         } catch (IOException ioe) {
/*  63 */           String errMsg = "Could not close input stream";
/*  64 */           addError(errMsg, ioe);
/*  65 */           throw new JoranException(errMsg, ioe);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void doConfigure(String filename) throws JoranException {
/*  72 */     doConfigure(new File(filename));
/*     */   }
/*     */   
/*     */   public final void doConfigure(File file) throws JoranException {
/*  76 */     FileInputStream fis = null;
/*     */     try {
/*  78 */       URL url = file.toURI().toURL();
/*  79 */       informContextOfURLUsedForConfiguration(getContext(), url);
/*  80 */       fis = new FileInputStream(file);
/*  81 */       doConfigure(fis, url.toExternalForm());
/*  82 */     } catch (IOException ioe) {
/*  83 */       String errMsg = "Could not open [" + file.getPath() + "].";
/*  84 */       addError(errMsg, ioe);
/*  85 */       throw new JoranException(errMsg, ioe);
/*     */     } finally {
/*  87 */       if (fis != null) {
/*     */         try {
/*  89 */           fis.close();
/*  90 */         } catch (IOException ioe) {
/*  91 */           String errMsg = "Could not close [" + file.getName() + "].";
/*  92 */           addError(errMsg, ioe);
/*  93 */           throw new JoranException(errMsg, ioe);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
/* 100 */     ConfigurationWatchListUtil.setMainWatchURL(context, url);
/*     */   }
/*     */   
/*     */   public final void doConfigure(InputStream inputStream) throws JoranException {
/* 104 */     doConfigure(new InputSource(inputStream));
/*     */   }
/*     */   
/*     */   public final void doConfigure(InputStream inputStream, String systemId) throws JoranException {
/* 108 */     InputSource inputSource = new InputSource(inputStream);
/* 109 */     inputSource.setSystemId(systemId);
/* 110 */     doConfigure(inputSource);
/*     */   }
/*     */   
/*     */   protected BeanDescriptionCache getBeanDescriptionCache() {
/* 114 */     if (this.beanDescriptionCache == null) {
/* 115 */       this.beanDescriptionCache = new BeanDescriptionCache(getContext());
/*     */     }
/* 117 */     return this.beanDescriptionCache;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract void addInstanceRules(RuleStore paramRuleStore);
/*     */ 
/*     */   
/*     */   protected abstract void addImplicitRules(Interpreter paramInterpreter);
/*     */   
/*     */   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {}
/*     */   
/*     */   protected ElementPath initialElementPath() {
/* 129 */     return new ElementPath();
/*     */   }
/*     */   
/*     */   protected void buildInterpreter() {
/* 133 */     SimpleRuleStore simpleRuleStore = new SimpleRuleStore(this.context);
/* 134 */     addInstanceRules((RuleStore)simpleRuleStore);
/* 135 */     this.interpreter = new Interpreter(this.context, (RuleStore)simpleRuleStore, initialElementPath());
/* 136 */     InterpretationContext interpretationContext = this.interpreter.getInterpretationContext();
/* 137 */     interpretationContext.setContext(this.context);
/* 138 */     addImplicitRules(this.interpreter);
/* 139 */     addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void doConfigure(InputSource inputSource) throws JoranException {
/* 146 */     long threshold = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */     
/* 150 */     SaxEventRecorder recorder = new SaxEventRecorder(this.context);
/* 151 */     recorder.recordEvents(inputSource);
/* 152 */     doConfigure(recorder.saxEventList);
/*     */     
/* 154 */     StatusUtil statusUtil = new StatusUtil(this.context);
/* 155 */     if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
/* 156 */       addInfo("Registering current configuration as safe fallback point");
/* 157 */       registerSafeConfiguration(recorder.saxEventList);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void doConfigure(List<SaxEvent> eventList) throws JoranException {
/* 162 */     buildInterpreter();
/*     */     
/* 164 */     synchronized (this.context.getConfigurationLock()) {
/* 165 */       this.interpreter.getEventPlayer().play(eventList);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerSafeConfiguration(List<SaxEvent> eventList) {
/* 176 */     this.context.putObject("SAFE_JORAN_CONFIGURATION", eventList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<SaxEvent> recallSafeConfiguration() {
/* 184 */     return (List<SaxEvent>)this.context.getObject("SAFE_JORAN_CONFIGURATION");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\GenericConfigurator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */