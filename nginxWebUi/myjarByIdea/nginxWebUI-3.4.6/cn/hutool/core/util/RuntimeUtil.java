package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Pid;
import cn.hutool.core.text.StrBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class RuntimeUtil {
   public static String execForStr(String... cmds) throws IORuntimeException {
      return execForStr(CharsetUtil.systemCharset(), cmds);
   }

   public static String execForStr(Charset charset, String... cmds) throws IORuntimeException {
      return getResult(exec(cmds), charset);
   }

   public static List<String> execForLines(String... cmds) throws IORuntimeException {
      return execForLines(CharsetUtil.systemCharset(), cmds);
   }

   public static List<String> execForLines(Charset charset, String... cmds) throws IORuntimeException {
      return getResultLines(exec(cmds), charset);
   }

   public static Process exec(String... cmds) {
      try {
         Process process = (new ProcessBuilder(handleCmds(cmds))).redirectErrorStream(true).start();
         return process;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static Process exec(String[] envp, String... cmds) {
      return exec(envp, (File)null, cmds);
   }

   public static Process exec(String[] envp, File dir, String... cmds) {
      try {
         return Runtime.getRuntime().exec(handleCmds(cmds), envp, dir);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static List<String> getResultLines(Process process) {
      return getResultLines(process, CharsetUtil.systemCharset());
   }

   public static List<String> getResultLines(Process process, Charset charset) {
      InputStream in = null;

      List var3;
      try {
         in = process.getInputStream();
         var3 = (List)IoUtil.readLines(in, (Charset)charset, (Collection)(new ArrayList()));
      } finally {
         IoUtil.close(in);
         destroy(process);
      }

      return var3;
   }

   public static String getResult(Process process) {
      return getResult(process, CharsetUtil.systemCharset());
   }

   public static String getResult(Process process, Charset charset) {
      InputStream in = null;

      String var3;
      try {
         in = process.getInputStream();
         var3 = IoUtil.read(in, charset);
      } finally {
         IoUtil.close(in);
         destroy(process);
      }

      return var3;
   }

   public static String getErrorResult(Process process) {
      return getErrorResult(process, CharsetUtil.systemCharset());
   }

   public static String getErrorResult(Process process, Charset charset) {
      InputStream in = null;

      String var3;
      try {
         in = process.getErrorStream();
         var3 = IoUtil.read(in, charset);
      } finally {
         IoUtil.close(in);
         destroy(process);
      }

      return var3;
   }

   public static void destroy(Process process) {
      if (null != process) {
         process.destroy();
      }

   }

   public static void addShutdownHook(Runnable hook) {
      Runtime.getRuntime().addShutdownHook(hook instanceof Thread ? (Thread)hook : new Thread(hook));
   }

   public static int getProcessorCount() {
      return Runtime.getRuntime().availableProcessors();
   }

   public static long getFreeMemory() {
      return Runtime.getRuntime().freeMemory();
   }

   public static long getTotalMemory() {
      return Runtime.getRuntime().totalMemory();
   }

   public static long getMaxMemory() {
      return Runtime.getRuntime().maxMemory();
   }

   public static long getUsableMemory() {
      return getMaxMemory() - getTotalMemory() + getFreeMemory();
   }

   public static int getPid() throws UtilException {
      return Pid.INSTANCE.get();
   }

   private static String[] handleCmds(String... cmds) {
      if (ArrayUtil.isEmpty((Object[])cmds)) {
         throw new NullPointerException("Command is empty !");
      } else {
         if (1 == cmds.length) {
            String cmd = cmds[0];
            if (StrUtil.isBlank(cmd)) {
               throw new NullPointerException("Command is blank !");
            }

            cmds = cmdSplit(cmd);
         }

         return cmds;
      }
   }

   private static String[] cmdSplit(String cmd) {
      List<String> cmds = new ArrayList();
      int length = cmd.length();
      Stack<Character> stack = new Stack();
      boolean inWrap = false;
      StrBuilder cache = StrUtil.strBuilder();

      for(int i = 0; i < length; ++i) {
         char c = cmd.charAt(i);
         switch (c) {
            case ' ':
               if (inWrap) {
                  cache.append(c);
               } else {
                  cmds.add(cache.toString());
                  cache.reset();
               }
               break;
            case '"':
            case '\'':
               if (inWrap) {
                  if (c == (Character)stack.peek()) {
                     stack.pop();
                     inWrap = false;
                  }

                  cache.append(c);
               } else {
                  stack.push(c);
                  cache.append(c);
                  inWrap = true;
               }
               break;
            default:
               cache.append(c);
         }
      }

      if (cache.hasContent()) {
         cmds.add(cache.toString());
      }

      return (String[])cmds.toArray(new String[0]);
   }
}
