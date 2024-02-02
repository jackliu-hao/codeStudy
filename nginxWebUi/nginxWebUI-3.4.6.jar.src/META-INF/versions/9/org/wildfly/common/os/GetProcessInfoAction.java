/*    */ package META-INF.versions.9.org.wildfly.common.os;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.security.PrivilegedAction;
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
/*    */ final class GetProcessInfoAction
/*    */   implements PrivilegedAction<Object[]>
/*    */ {
/*    */   public Object[] run() {
/* 33 */     ProcessHandle processHandle = ProcessHandle.current();
/* 34 */     long pid = processHandle.pid();
/* 35 */     String processName = System.getProperty("jboss.process.name");
/* 36 */     if (processName == null) {
/* 37 */       String classPath = System.getProperty("java.class.path");
/* 38 */       String commandLine = System.getProperty("sun.java.command");
/* 39 */       if (commandLine != null) {
/* 40 */         if (classPath != null && commandLine.startsWith(classPath)) {
/*    */           
/* 42 */           int sepIdx = classPath.lastIndexOf(File.separatorChar);
/* 43 */           if (sepIdx > 0) {
/* 44 */             processName = classPath.substring(sepIdx + 1);
/*    */           } else {
/* 46 */             processName = classPath;
/*    */           } 
/*    */         } else {
/*    */           String className;
/*    */           
/* 51 */           int firstSpace = commandLine.indexOf(' ');
/*    */           
/* 53 */           if (firstSpace > 0) {
/* 54 */             className = commandLine.substring(0, firstSpace);
/*    */           } else {
/* 56 */             className = commandLine;
/*    */           } 
/*    */           
/* 59 */           int lastDot = className.lastIndexOf('.', firstSpace);
/* 60 */           if (lastDot > 0) {
/* 61 */             processName = className.substring(lastDot + 1);
/* 62 */             if (processName.equalsIgnoreCase("jar") || processName.equalsIgnoreCase("ȷar")) {
/*    */               
/* 64 */               int secondLastDot = className.lastIndexOf('.', lastDot - 1);
/* 65 */               int sepIdx = className.lastIndexOf(File.separatorChar);
/* 66 */               int lastSep = (secondLastDot == -1) ? sepIdx : ((sepIdx == -1) ? secondLastDot : Math.max(sepIdx, secondLastDot));
/* 67 */               if (lastSep > 0) {
/* 68 */                 processName = className.substring(lastSep + 1);
/*    */               } else {
/* 70 */                 processName = className;
/*    */               } 
/*    */             } 
/*    */           } else {
/* 74 */             processName = className;
/*    */           } 
/*    */         } 
/*    */       }
/*    */     } 
/* 79 */     if (processName == null) {
/* 80 */       processName = processHandle.info().command().orElse(null);
/*    */     }
/* 82 */     if (processName == null) {
/* 83 */       processName = "<unknown>";
/*    */     }
/* 85 */     return new Object[] { Long.valueOf(pid), processName };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\9\org\wildfly\common\os\GetProcessInfoAction.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */