package cn.hutool.extra.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class GanymedUtil {
   public static Connection connect(String sshHost, int sshPort) {
      Connection conn = new Connection(sshHost, sshPort);

      try {
         conn.connect();
         return conn;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
      if (StrUtil.isEmpty(sshUser)) {
         sshUser = "root";
      }

      Connection connect = connect(sshHost, sshPort);

      try {
         connect.authenticateWithPassword(sshUser, sshPass);
         return connect.openSession();
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }
   }

   public static String exec(Session session, String cmd, Charset charset, OutputStream errStream) {
      String result;
      try {
         session.execCommand(cmd, charset.name());
         result = IoUtil.read(new StreamGobbler(session.getStdout()), (Charset)charset);
         IoUtil.copy((InputStream)(new StreamGobbler(session.getStderr())), (OutputStream)errStream);
      } catch (IOException var9) {
         throw new IORuntimeException(var9);
      } finally {
         close(session);
      }

      return result;
   }

   public static String execByShell(Session session, String cmd, Charset charset, OutputStream errStream) {
      String result;
      try {
         session.requestDumbPTY();
         IoUtil.write(session.getStdin(), charset, true, cmd);
         result = IoUtil.read(new StreamGobbler(session.getStdout()), (Charset)charset);
         if (null != errStream) {
            IoUtil.copy((InputStream)(new StreamGobbler(session.getStderr())), (OutputStream)errStream);
         }
      } catch (IOException var9) {
         throw new IORuntimeException(var9);
      } finally {
         close(session);
      }

      return result;
   }

   public static void close(Session session) {
      if (session != null) {
         session.close();
      }

   }
}
