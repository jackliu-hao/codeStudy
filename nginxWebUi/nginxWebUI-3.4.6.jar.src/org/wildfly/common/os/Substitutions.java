/*    */ package org.wildfly.common.os;
/*    */ 
/*    */ import com.oracle.svm.core.annotate.Substitute;
/*    */ import com.oracle.svm.core.annotate.TargetClass;
/*    */ import java.io.File;
/*    */ import org.graalvm.nativeimage.ProcessProperties;
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
/*    */ final class Substitutions
/*    */ {
/*    */   @TargetClass(className = "org.wildfly.common.os.GetProcessInfoAction")
/*    */   static final class Target_org_wildfly_common_os_GetProcessInfoAction
/*    */   {
/*    */     @Substitute
/*    */     public Object[] run() {
/* 33 */       return new Object[] { Long.valueOf(ProcessProperties.getProcessID() & 0xFFFFFFFFL), Substitutions.ProcessUtils.getProcessName() };
/*    */     }
/*    */   }
/*    */   
/*    */   static final class ProcessUtils {
/*    */     static String getProcessName() {
/* 39 */       String name = System.getProperty("jboss.process.name");
/* 40 */       if (name == null) {
/* 41 */         String exeName = ProcessProperties.getExecutableName();
/* 42 */         if (!exeName.isEmpty()) {
/* 43 */           int idx = exeName.lastIndexOf(File.separatorChar);
/* 44 */           name = (idx == -1) ? exeName : ((idx == exeName.length() - 1) ? null : exeName.substring(idx + 1));
/*    */         } 
/*    */       } 
/* 47 */       if (name == null) {
/* 48 */         name = "<unknown>";
/*    */       }
/* 50 */       return name;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\os\Substitutions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */