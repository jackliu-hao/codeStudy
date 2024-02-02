/*    */ package org.codehaus.plexus.util.cli.shell;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
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
/*    */ public class CmdShell
/*    */   extends Shell
/*    */ {
/*    */   public CmdShell() {
/* 36 */     setShellCommand("cmd.exe");
/* 37 */     setQuotedExecutableEnabled(true);
/* 38 */     setShellArgs(new String[] { "/X", "/C" });
/*    */   }
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
/*    */   public List getCommandLine(String executable, String[] arguments) {
/* 82 */     StringBuffer sb = new StringBuffer();
/* 83 */     sb.append("\"");
/* 84 */     sb.append(super.getCommandLine(executable, arguments).get(0));
/* 85 */     sb.append("\"");
/*    */     
/* 87 */     return Arrays.asList(new String[] { sb.toString() });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\shell\CmdShell.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */