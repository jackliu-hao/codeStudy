/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.Properties;
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
/*    */ public final class Version
/*    */ {
/*    */   private static final String JAR_NAME;
/*    */   private static final String VERSION_STRING;
/*    */   
/*    */   static {
/* 38 */     Properties versionProps = new Properties();
/* 39 */     String jarName = "(unknown)";
/* 40 */     String versionString = "(unknown)"; 
/* 41 */     try { InputStream stream = Version.class.getResourceAsStream("Version.properties"); 
/* 42 */       try { if (stream != null) { InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8); 
/* 43 */           try { versionProps.load(reader);
/* 44 */             jarName = versionProps.getProperty("jarName", jarName);
/* 45 */             versionString = versionProps.getProperty("version", versionString);
/* 46 */             reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  }
/* 47 */          if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null) try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException iOException) {}
/*    */     
/* 49 */     JAR_NAME = jarName;
/* 50 */     VERSION_STRING = versionString;
/*    */     try {
/* 52 */       Messages.msg.version(versionString);
/* 53 */     } catch (Throwable throwable) {}
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getJarName() {
/* 62 */     return JAR_NAME;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getVersionString() {
/* 71 */     return VERSION_STRING;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/* 80 */     System.out.printf("JBoss Threads version %s\n", new Object[] { VERSION_STRING });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */