package com.cym.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TelnetUtils {
   public static boolean isRunning(String host, int port) {
      Socket sClient = null;

      boolean var4;
      try {
         SocketAddress saAdd = new InetSocketAddress(host.trim(), port);
         sClient = new Socket();
         sClient.connect(saAdd, 1000);
         return true;
      } catch (UnknownHostException var20) {
         var4 = false;
         return var4;
      } catch (SocketTimeoutException var21) {
         var4 = false;
      } catch (IOException var22) {
         var4 = false;
         return var4;
      } catch (Exception var23) {
         var4 = false;
         return var4;
      } finally {
         try {
            if (sClient != null) {
               sClient.close();
            }
         } catch (Exception var19) {
         }

      }

      return var4;
   }

   public static void main(String[] args) {
      System.out.println(isRunning("127.0.0.1", 8080));
   }
}
