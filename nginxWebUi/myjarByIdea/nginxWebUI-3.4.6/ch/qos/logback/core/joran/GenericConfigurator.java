package ch.qos.logback.core.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.joran.spi.SimpleRuleStore;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.xml.sax.InputSource;

public abstract class GenericConfigurator extends ContextAwareBase {
   private BeanDescriptionCache beanDescriptionCache;
   protected Interpreter interpreter;

   public final void doConfigure(URL url) throws JoranException {
      InputStream in = null;
      boolean var12 = false;

      String errMsg;
      try {
         var12 = true;
         informContextOfURLUsedForConfiguration(this.getContext(), url);
         URLConnection urlConnection = url.openConnection();
         urlConnection.setUseCaches(false);
         in = urlConnection.getInputStream();
         this.doConfigure(in, url.toExternalForm());
         var12 = false;
      } catch (IOException var15) {
         errMsg = "Could not open URL [" + url + "].";
         this.addError(errMsg, var15);
         throw new JoranException(errMsg, var15);
      } finally {
         if (var12) {
            if (in != null) {
               try {
                  in.close();
               } catch (IOException var13) {
                  String errMsg = "Could not close input stream";
                  this.addError(errMsg, var13);
                  throw new JoranException(errMsg, var13);
               }
            }

         }
      }

      if (in != null) {
         try {
            in.close();
         } catch (IOException var14) {
            errMsg = "Could not close input stream";
            this.addError(errMsg, var14);
            throw new JoranException(errMsg, var14);
         }
      }

   }

   public final void doConfigure(String filename) throws JoranException {
      this.doConfigure(new File(filename));
   }

   public final void doConfigure(File file) throws JoranException {
      FileInputStream fis = null;
      boolean var12 = false;

      String errMsg;
      try {
         var12 = true;
         URL url = file.toURI().toURL();
         informContextOfURLUsedForConfiguration(this.getContext(), url);
         fis = new FileInputStream(file);
         this.doConfigure(fis, url.toExternalForm());
         var12 = false;
      } catch (IOException var15) {
         errMsg = "Could not open [" + file.getPath() + "].";
         this.addError(errMsg, var15);
         throw new JoranException(errMsg, var15);
      } finally {
         if (var12) {
            if (fis != null) {
               try {
                  fis.close();
               } catch (IOException var13) {
                  String errMsg = "Could not close [" + file.getName() + "].";
                  this.addError(errMsg, var13);
                  throw new JoranException(errMsg, var13);
               }
            }

         }
      }

      if (fis != null) {
         try {
            fis.close();
         } catch (IOException var14) {
            errMsg = "Could not close [" + file.getName() + "].";
            this.addError(errMsg, var14);
            throw new JoranException(errMsg, var14);
         }
      }

   }

   public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
      ConfigurationWatchListUtil.setMainWatchURL(context, url);
   }

   public final void doConfigure(InputStream inputStream) throws JoranException {
      this.doConfigure(new InputSource(inputStream));
   }

   public final void doConfigure(InputStream inputStream, String systemId) throws JoranException {
      InputSource inputSource = new InputSource(inputStream);
      inputSource.setSystemId(systemId);
      this.doConfigure(inputSource);
   }

   protected BeanDescriptionCache getBeanDescriptionCache() {
      if (this.beanDescriptionCache == null) {
         this.beanDescriptionCache = new BeanDescriptionCache(this.getContext());
      }

      return this.beanDescriptionCache;
   }

   protected abstract void addInstanceRules(RuleStore var1);

   protected abstract void addImplicitRules(Interpreter var1);

   protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
   }

   protected ElementPath initialElementPath() {
      return new ElementPath();
   }

   protected void buildInterpreter() {
      RuleStore rs = new SimpleRuleStore(this.context);
      this.addInstanceRules(rs);
      this.interpreter = new Interpreter(this.context, rs, this.initialElementPath());
      InterpretationContext interpretationContext = this.interpreter.getInterpretationContext();
      interpretationContext.setContext(this.context);
      this.addImplicitRules(this.interpreter);
      this.addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
   }

   public final void doConfigure(InputSource inputSource) throws JoranException {
      long threshold = System.currentTimeMillis();
      SaxEventRecorder recorder = new SaxEventRecorder(this.context);
      recorder.recordEvents(inputSource);
      this.doConfigure(recorder.saxEventList);
      StatusUtil statusUtil = new StatusUtil(this.context);
      if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
         this.addInfo("Registering current configuration as safe fallback point");
         this.registerSafeConfiguration(recorder.saxEventList);
      }

   }

   public void doConfigure(List<SaxEvent> eventList) throws JoranException {
      this.buildInterpreter();
      synchronized(this.context.getConfigurationLock()) {
         this.interpreter.getEventPlayer().play(eventList);
      }
   }

   public void registerSafeConfiguration(List<SaxEvent> eventList) {
      this.context.putObject("SAFE_JORAN_CONFIGURATION", eventList);
   }

   public List<SaxEvent> recallSafeConfiguration() {
      return (List)this.context.getObject("SAFE_JORAN_CONFIGURATION");
   }
}
