package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MailcapCommandMap extends CommandMap {
   private static MailcapFile defDB = null;
   private MailcapFile[] DB;
   private static final int PROG = 0;

   public MailcapCommandMap() {
      List dbv = new ArrayList(5);
      MailcapFile mf = null;
      dbv.add((Object)null);
      LogSupport.log("MailcapCommandMap: load HOME");

      String system_mailcap;
      try {
         system_mailcap = System.getProperty("user.home");
         if (system_mailcap != null) {
            String path = system_mailcap + File.separator + ".mailcap";
            mf = this.loadFile(path);
            if (mf != null) {
               dbv.add(mf);
            }
         }
      } catch (SecurityException var7) {
      }

      LogSupport.log("MailcapCommandMap: load SYS");

      try {
         system_mailcap = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
         mf = this.loadFile(system_mailcap);
         if (mf != null) {
            dbv.add(mf);
         }
      } catch (SecurityException var6) {
      }

      LogSupport.log("MailcapCommandMap: load JAR");
      this.loadAllResources(dbv, "META-INF/mailcap");
      LogSupport.log("MailcapCommandMap: load DEF");
      synchronized(MailcapCommandMap.class) {
         if (defDB == null) {
            defDB = this.loadResource("/META-INF/mailcap.default");
         }
      }

      if (defDB != null) {
         dbv.add(defDB);
      }

      this.DB = new MailcapFile[dbv.size()];
      this.DB = (MailcapFile[])((MailcapFile[])dbv.toArray(this.DB));
   }

   private MailcapFile loadResource(String name) {
      InputStream clis = null;

      MailcapFile var4;
      try {
         clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
         if (clis == null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
            }

            return null;
         }

         MailcapFile mf = new MailcapFile(clis);
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
         }

         var4 = mf;
      } catch (IOException var17) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var17);
         }

         return null;
      } catch (SecurityException var18) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var18);
         }

         return null;
      } finally {
         try {
            if (clis != null) {
               clis.close();
            }
         } catch (IOException var16) {
         }

      }

      return var4;
   }

   private void loadAllResources(List v, String name) {
      boolean anyLoaded = false;

      try {
         ClassLoader cld = null;
         cld = SecuritySupport.getContextClassLoader();
         if (cld == null) {
            cld = this.getClass().getClassLoader();
         }

         URL[] urls;
         if (cld != null) {
            urls = SecuritySupport.getResources(cld, name);
         } else {
            urls = SecuritySupport.getSystemResources(name);
         }

         if (urls != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("MailcapCommandMap: getResources");
            }

            for(int i = 0; i < urls.length; ++i) {
               URL url = urls[i];
               InputStream clis = null;
               if (LogSupport.isLoggable()) {
                  LogSupport.log("MailcapCommandMap: URL " + url);
               }

               try {
                  clis = SecuritySupport.openStream(url);
                  if (clis != null) {
                     v.add(new MailcapFile(clis));
                     anyLoaded = true;
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                     }
                  } else if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                  }
               } catch (IOException var21) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: can't load " + url, var21);
                  }
               } catch (SecurityException var22) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: can't load " + url, var22);
                  }
               } finally {
                  try {
                     if (clis != null) {
                        clis.close();
                     }
                  } catch (IOException var20) {
                  }

               }
            }
         }
      } catch (Exception var24) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var24);
         }
      }

      if (!anyLoaded) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: !anyLoaded");
         }

         MailcapFile mf = this.loadResource("/" + name);
         if (mf != null) {
            v.add(mf);
         }
      }

   }

   private MailcapFile loadFile(String name) {
      MailcapFile mtf = null;

      try {
         mtf = new MailcapFile(name);
      } catch (IOException var4) {
      }

      return mtf;
   }

   public MailcapCommandMap(String fileName) throws IOException {
      this();
      if (LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
      }

      if (this.DB[0] == null) {
         this.DB[0] = new MailcapFile(fileName);
      }

   }

   public MailcapCommandMap(InputStream is) {
      this();
      LogSupport.log("MailcapCommandMap: load PROG");
      if (this.DB[0] == null) {
         try {
            this.DB[0] = new MailcapFile(is);
         } catch (IOException var3) {
         }
      }

   }

   public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
      List cmdList = new ArrayList();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase();
      }

      int i;
      Map cmdMap;
      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      cmdInfos = (CommandInfo[])((CommandInfo[])cmdList.toArray(cmdInfos));
      return cmdInfos;
   }

   private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
      Iterator verb_enum = cmdHash.keySet().iterator();

      while(verb_enum.hasNext()) {
         String verb = (String)verb_enum.next();
         if (!this.checkForVerb(cmdList, verb)) {
            List cmdList2 = (List)cmdHash.get(verb);
            String className = (String)cmdList2.get(0);
            cmdList.add(new CommandInfo(verb, className));
         }
      }

   }

   private boolean checkForVerb(List cmdList, String verb) {
      Iterator ee = cmdList.iterator();

      String enum_verb;
      do {
         if (!ee.hasNext()) {
            return false;
         }

         enum_verb = ((CommandInfo)ee.next()).getCommandName();
      } while(!enum_verb.equals(verb));

      return true;
   }

   public synchronized CommandInfo[] getAllCommands(String mimeType) {
      List cmdList = new ArrayList();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase();
      }

      int i;
      Map cmdMap;
      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      cmdInfos = (CommandInfo[])((CommandInfo[])cmdList.toArray(cmdInfos));
      return cmdInfos;
   }

   private void appendCmdsToList(Map typeHash, List cmdList) {
      Iterator verb_enum = typeHash.keySet().iterator();

      while(verb_enum.hasNext()) {
         String verb = (String)verb_enum.next();
         List cmdList2 = (List)typeHash.get(verb);
         Iterator cmd_enum = cmdList2.iterator();

         while(cmd_enum.hasNext()) {
            String cmd = (String)cmd_enum.next();
            cmdList.add(new CommandInfo(verb, cmd));
         }
      }

   }

   public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase();
      }

      int i;
      Map cmdMap;
      List v;
      String cmdClassName;
      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               v = (List)cmdMap.get(cmdName);
               if (v != null) {
                  cmdClassName = (String)v.get(0);
                  if (cmdClassName != null) {
                     return new CommandInfo(cmdName, cmdClassName);
                  }
               }
            }
         }
      }

      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               v = (List)cmdMap.get(cmdName);
               if (v != null) {
                  cmdClassName = (String)v.get(0);
                  if (cmdClassName != null) {
                     return new CommandInfo(cmdName, cmdClassName);
                  }
               }
            }
         }
      }

      return null;
   }

   public synchronized void addMailcap(String mail_cap) {
      LogSupport.log("MailcapCommandMap: add to PROG");
      if (this.DB[0] == null) {
         this.DB[0] = new MailcapFile();
      }

      this.DB[0].appendToMailcap(mail_cap);
   }

   public synchronized DataContentHandler createDataContentHandler(String mimeType) {
      if (LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: createDataContentHandler for " + mimeType);
      }

      if (mimeType != null) {
         mimeType = mimeType.toLowerCase();
      }

      int i;
      Map cmdMap;
      List v;
      String name;
      DataContentHandler dch;
      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("  search DB #" + i);
            }

            cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               v = (List)cmdMap.get("content-handler");
               if (v != null) {
                  name = (String)v.get(0);
                  dch = this.getDataContentHandler(name);
                  if (dch != null) {
                     return dch;
                  }
               }
            }
         }
      }

      for(i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("  search fallback DB #" + i);
            }

            cmdMap = this.DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               v = (List)cmdMap.get("content-handler");
               if (v != null) {
                  name = (String)v.get(0);
                  dch = this.getDataContentHandler(name);
                  if (dch != null) {
                     return dch;
                  }
               }
            }
         }
      }

      return null;
   }

   private DataContentHandler getDataContentHandler(String name) {
      if (LogSupport.isLoggable()) {
         LogSupport.log("    got content-handler");
      }

      if (LogSupport.isLoggable()) {
         LogSupport.log("      class " + name);
      }

      try {
         ClassLoader cld = null;
         cld = SecuritySupport.getContextClassLoader();
         if (cld == null) {
            cld = this.getClass().getClassLoader();
         }

         Class cl = null;

         try {
            cl = cld.loadClass(name);
         } catch (Exception var5) {
            cl = Class.forName(name);
         }

         if (cl != null) {
            return (DataContentHandler)cl.newInstance();
         }
      } catch (IllegalAccessException var6) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var6);
         }
      } catch (ClassNotFoundException var7) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var7);
         }
      } catch (InstantiationException var8) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var8);
         }
      }

      return null;
   }

   public synchronized String[] getMimeTypes() {
      List mtList = new ArrayList();

      for(int i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            String[] ts = this.DB[i].getMimeTypes();
            if (ts != null) {
               for(int j = 0; j < ts.length; ++j) {
                  if (!mtList.contains(ts[j])) {
                     mtList.add(ts[j]);
                  }
               }
            }
         }
      }

      String[] mts = new String[mtList.size()];
      mts = (String[])((String[])mtList.toArray(mts));
      return mts;
   }

   public synchronized String[] getNativeCommands(String mimeType) {
      List cmdList = new ArrayList();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase();
      }

      for(int i = 0; i < this.DB.length; ++i) {
         if (this.DB[i] != null) {
            String[] cmds = this.DB[i].getNativeCommands(mimeType);
            if (cmds != null) {
               for(int j = 0; j < cmds.length; ++j) {
                  if (!cmdList.contains(cmds[j])) {
                     cmdList.add(cmds[j]);
                  }
               }
            }
         }
      }

      String[] cmds = new String[cmdList.size()];
      cmds = (String[])((String[])cmdList.toArray(cmds));
      return cmds;
   }
}
