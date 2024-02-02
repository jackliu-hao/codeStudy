package com.sun.jna.platform.win32.COM.tlb.imp;

import java.util.Hashtable;

public class TlbCmdlineArgs extends Hashtable<String, String> implements TlbConst {
   private static final long serialVersionUID = 1L;

   public TlbCmdlineArgs(String[] args) {
      this.readCmdArgs(args);
   }

   public int getIntParam(String key) {
      String param = this.getRequiredParam(key);
      return Integer.parseInt(param);
   }

   public String getParam(String key) {
      return (String)this.get(key);
   }

   public String getRequiredParam(String key) {
      String param = this.getParam(key);
      if (param == null) {
         throw new TlbParameterNotFoundException("Commandline parameter not found: " + key);
      } else {
         return param;
      }
   }

   private void readCmdArgs(String[] args) {
      if (args.length < 2) {
         this.showCmdHelp();
      }

      for(int i = 0; i < args.length; i += 2) {
         String cmdName = args[i];
         String cmdValue = args[i + 1];
         if (!cmdName.startsWith("-") || cmdValue.startsWith("-")) {
            this.showCmdHelp();
            break;
         }

         this.put(cmdName.substring(1), cmdValue);
      }

   }

   public boolean isTlbFile() {
      return this.containsKey("tlb.file");
   }

   public boolean isTlbId() {
      return this.containsKey("tlb.id");
   }

   public String getBindingMode() {
      return this.containsKey("bind.mode") ? this.getParam("bind.mode") : "vtable";
   }

   public void showCmdHelp() {
      String helpStr = "usage: TlbImp [-tlb.id -tlb.major.version -tlb.minor.version] [-tlb.file] [-bind.mode vTable, dispId] [-output.dir]\n\noptions:\n-tlb.id               The guid of the type library.\n-tlb.major.version    The major version of the type library.\n-tlb.minor.version    The minor version of the type library.\n-tlb.file             The file name containing the type library.\n-bind.mode            The binding mode used to create the Java code.\n-output.dir           The optional output directory, default is the user temp directory.\n\nsamples:\nMicrosoft Shell Controls And Automation:\n-tlb.file shell32.dll\n-tlb.id {50A7E9B0-70EF-11D1-B75A-00A0C90564FE} -tlb.major.version 1 -tlb.minor.version 0\n\nMicrosoft Word 12.0 Object Library:\n-tlb.id {00020905-0000-0000-C000-000000000046} -tlb.major.version 8 -tlb.minor.version 4\n\n";
      System.out.println(helpStr);
      System.exit(0);
   }
}
