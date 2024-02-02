/*    */ package org.codehaus.plexus.util.cli.shell;
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
/*    */ public class CommandShell
/*    */   extends Shell
/*    */ {
/*    */   public CommandShell() {
/* 33 */     setShellCommand("command.com");
/* 34 */     setShellArgs(new String[] { "/C" });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\shell\CommandShell.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */