/*    */ package ch.qos.logback.classic.net.server;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.spi.ClassPackagingData;
/*    */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*    */ import ch.qos.logback.classic.spi.LoggerContextVO;
/*    */ import ch.qos.logback.classic.spi.LoggerRemoteView;
/*    */ import ch.qos.logback.classic.spi.LoggingEventVO;
/*    */ import ch.qos.logback.classic.spi.StackTraceElementProxy;
/*    */ import ch.qos.logback.classic.spi.ThrowableProxy;
/*    */ import ch.qos.logback.classic.spi.ThrowableProxyVO;
/*    */ import ch.qos.logback.core.net.HardenedObjectInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.helpers.BasicMarker;
/*    */ 
/*    */ 
/*    */ public class HardenedLoggingEventInputStream
/*    */   extends HardenedObjectInputStream
/*    */ {
/*    */   static final String ARRAY_PREFIX = "[L";
/*    */   
/*    */   public static List<String> getWhilelist() {
/* 27 */     List<String> whitelist = new ArrayList<String>();
/* 28 */     whitelist.add(LoggingEventVO.class.getName());
/* 29 */     whitelist.add(LoggerContextVO.class.getName());
/* 30 */     whitelist.add(LoggerRemoteView.class.getName());
/* 31 */     whitelist.add(ThrowableProxyVO.class.getName());
/* 32 */     whitelist.add(BasicMarker.class.getName());
/* 33 */     whitelist.add(Level.class.getName());
/* 34 */     whitelist.add(Logger.class.getName());
/* 35 */     whitelist.add(StackTraceElement.class.getName());
/* 36 */     whitelist.add(StackTraceElement[].class.getName());
/* 37 */     whitelist.add(ThrowableProxy.class.getName());
/* 38 */     whitelist.add(ThrowableProxy[].class.getName());
/* 39 */     whitelist.add(IThrowableProxy.class.getName());
/* 40 */     whitelist.add(IThrowableProxy[].class.getName());
/* 41 */     whitelist.add(StackTraceElementProxy.class.getName());
/* 42 */     whitelist.add(StackTraceElementProxy[].class.getName());
/* 43 */     whitelist.add(ClassPackagingData.class.getName());
/*    */     
/* 45 */     return whitelist;
/*    */   }
/*    */   
/*    */   public HardenedLoggingEventInputStream(InputStream is) throws IOException {
/* 49 */     super(is, getWhilelist());
/*    */   }
/*    */   
/*    */   public HardenedLoggingEventInputStream(InputStream is, List<String> additionalAuthorizedClasses) throws IOException {
/* 53 */     this(is);
/* 54 */     addToWhitelist(additionalAuthorizedClasses);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\server\HardenedLoggingEventInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */