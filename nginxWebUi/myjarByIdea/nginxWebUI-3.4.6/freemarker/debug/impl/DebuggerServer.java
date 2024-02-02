package freemarker.debug.impl;

import freemarker.log.Logger;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

class DebuggerServer {
   private static final Logger LOG = Logger.getLogger("freemarker.debug.server");
   private static final Random R = new SecureRandom();
   private final byte[] password;
   private final int port = SecurityUtilities.getSystemProperty("freemarker.debug.port", 7011);
   private final Serializable debuggerStub;
   private boolean stop = false;
   private ServerSocket serverSocket;

   public DebuggerServer(Serializable debuggerStub) {
      try {
         this.password = SecurityUtilities.getSystemProperty("freemarker.debug.password", "").getBytes("UTF-8");
      } catch (UnsupportedEncodingException var3) {
         throw new UndeclaredThrowableException(var3);
      }

      this.debuggerStub = debuggerStub;
   }

   public void start() {
      (new Thread(new Runnable() {
         public void run() {
            DebuggerServer.this.startInternal();
         }
      }, "FreeMarker Debugger Server Acceptor")).start();
   }

   private void startInternal() {
      try {
         this.serverSocket = new ServerSocket(this.port);

         while(!this.stop) {
            Socket s = this.serverSocket.accept();
            (new Thread(new DebuggerAuthProtocol(s))).start();
         }
      } catch (IOException var2) {
         LOG.error("Debugger server shut down.", var2);
      }

   }

   public void stop() {
      this.stop = true;
      if (this.serverSocket != null) {
         try {
            this.serverSocket.close();
         } catch (IOException var2) {
            LOG.error("Unable to close server socket.", var2);
         }
      }

   }

   private class DebuggerAuthProtocol implements Runnable {
      private final Socket s;

      DebuggerAuthProtocol(Socket s) {
         this.s = s;
      }

      public void run() {
         try {
            ObjectOutputStream out = new ObjectOutputStream(this.s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(this.s.getInputStream());
            byte[] challenge = new byte[512];
            DebuggerServer.R.nextBytes(challenge);
            out.writeInt(220);
            out.writeObject(challenge);
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(DebuggerServer.this.password);
            md.update(challenge);
            byte[] response = (byte[])((byte[])in.readObject());
            if (Arrays.equals(response, md.digest())) {
               out.writeObject(DebuggerServer.this.debuggerStub);
            } else {
               out.writeObject((Object)null);
            }
         } catch (Exception var6) {
            DebuggerServer.LOG.warn("Connection to " + this.s.getInetAddress().getHostAddress() + " abruply broke", var6);
         }

      }
   }
}
