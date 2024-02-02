/*    */ package io.undertow.servlet;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Date;
/*    */ import java.util.Set;
/*    */ import javax.servlet.UnavailableException;
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.Logger;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.LogMessage;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageLogger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @MessageLogger(projectCode = "UT")
/*    */ public interface UndertowServletLogger
/*    */   extends BasicLogger
/*    */ {
/* 44 */   public static final UndertowServletLogger ROOT_LOGGER = (UndertowServletLogger)Logger.getMessageLogger(UndertowServletLogger.class, UndertowServletLogger.class.getPackage().getName());
/*    */   
/* 46 */   public static final UndertowServletLogger REQUEST_LOGGER = (UndertowServletLogger)Logger.getMessageLogger(UndertowServletLogger.class, UndertowServletLogger.class.getPackage().getName() + ".request");
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15002, value = "Stopping servlet %s due to permanent unavailability")
/*    */   void stoppingServletDueToPermanentUnavailability(String paramString, @Cause UnavailableException paramUnavailableException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15003, value = "Stopping servlet %s till %s due to temporary unavailability")
/*    */   void stoppingServletUntilDueToTemporaryUnavailability(String paramString, Date paramDate, @Cause UnavailableException paramUnavailableException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15005, value = "Error invoking method %s on listener %s")
/*    */   void errorInvokingListener(String paramString, Class<?> paramClass, @Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15006, value = "IOException dispatching async event")
/*    */   void ioExceptionDispatchingAsyncEvent(@Cause IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15007, value = "Stack trace on error enabled for deployment %s, please do not enable for production use")
/*    */   void servletStackTracesAll(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15008, value = "Failed to load development mode persistent sessions")
/*    */   void failedtoLoadPersistentSessions(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15009, value = "Failed to persist session attribute %s with value %s for session %s")
/*    */   void failedToPersistSessionAttribute(String paramString1, Object paramObject, String paramString2, @Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15010, value = "Failed to persist sessions")
/*    */   void failedToPersistSessions(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15012, value = "Failed to generate error page %s for original exception: %s. Generating error page resulted in a %s.")
/*    */   void errorGeneratingErrorPage(String paramString, Object paramObject, int paramInt, @Cause Throwable paramThrowable);
/*    */   
/*    */   @Message(id = 15014, value = "Error reading rewrite configuration")
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   void errorReadingRewriteConfiguration(@Cause IOException paramIOException);
/*    */   
/*    */   @Message(id = 15015, value = "Error reading rewrite configuration: %s")
/*    */   IllegalArgumentException invalidRewriteConfiguration(String paramString);
/*    */   
/*    */   @Message(id = 15016, value = "Invalid rewrite map class: %s")
/*    */   IllegalArgumentException invalidRewriteMap(String paramString);
/*    */   
/*    */   @Message(id = 15017, value = "Error reading rewrite flags in line %s as %s")
/*    */   IllegalArgumentException invalidRewriteFlags(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 15018, value = "Error reading rewrite flags in line %s")
/*    */   IllegalArgumentException invalidRewriteFlags(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15019, value = "Failed to destroy %s")
/*    */   void failedToDestroy(Object paramObject, @Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15020, value = "Path %s is secured for some HTTP methods, however it is not secured for %s")
/*    */   void unsecuredMethodsOnPath(String paramString, Set<String> paramSet);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 15021, value = "Failure dispatching async event")
/*    */   void failureDispatchingAsyncEvent(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 15022, value = "Requested resource at %s does not exist for include method")
/*    */   void requestedResourceDoesNotExistForIncludeMethod(String paramString);
/*    */   
/*    */   @Message(id = 15023, value = "This Context has been already destroyed")
/*    */   IllegalStateException contextDestroyed();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\UndertowServletLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */