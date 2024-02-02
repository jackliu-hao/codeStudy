package com.cym.utils;

import cn.hutool.core.date.DateUtil;
import com.cym.ext.NetworkInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Formatter;
import java.util.Optional;
import java.util.Properties;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetWorkUtil {
   private static final int SLEEP_TIME = 2000;
   static Logger logger = LoggerFactory.getLogger(NetWorkUtil.class);

   public static NetworkInfo getNetworkDownUp() {
      Properties props = System.getProperties();
      String os = props.getProperty("os.name").toLowerCase();
      os = os.toLowerCase().startsWith("win") ? "windows" : "linux";
      Process pro = null;
      Runtime r = Runtime.getRuntime();
      BufferedReader input = null;
      NetworkInfo networkInfo = new NetworkInfo();

      try {
         String command = "windows".equals(os) ? "netstat -e" : "ifconfig";
         pro = r.exec(command);
         input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
         long[] result1 = readInLine(input, os);
         Thread.sleep(2000L);
         pro.destroy();
         input.close();
         pro = r.exec(command);
         input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
         long[] result2 = readInLine(input, os);
         networkInfo.setReceive(formatNumber((double)(result2[0] - result1[0]) / 2048.0));
         networkInfo.setSend(formatNumber((double)(result2[1] - result1[1]) / 2048.0));
         if (networkInfo.getReceive() < 0.0) {
            networkInfo.setReceive(0.0 - networkInfo.getReceive());
         }

         if (networkInfo.getSend() < 0.0) {
            networkInfo.setSend(0.0 - networkInfo.getSend());
         }
      } catch (Exception var17) {
         logger.error((String)var17.getMessage(), (Throwable)var17);
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException var16) {
               logger.error((String)var16.getMessage(), (Throwable)var16);
            }
         }

         Optional.ofNullable(pro).ifPresent((p) -> {
            p.destroy();
         });
      }

      networkInfo.setTime(DateUtil.format(new Date(), "HH:mm:ss"));
      return networkInfo;
   }

   private static long[] readInLine(BufferedReader input, String osType) {
      long[] arr = new long[2];
      StringTokenizer tokenStat = null;

      try {
         if (osType.equals("linux")) {
            long rx = 0L;
            long tx = 0L;
            String line = null;

            while((line = input.readLine()) != null) {
               if (line.indexOf("RX packets") >= 0) {
                  rx += formatLong(line);
               } else if (line.indexOf("TX packets") >= 0) {
                  tx += formatLong(line);
               }
            }

            arr[0] = rx;
            arr[1] = tx;
         } else {
            input.readLine();
            input.readLine();
            input.readLine();
            input.readLine();
            tokenStat = new StringTokenizer(input.readLine());
            tokenStat.nextToken();
            arr[0] = Long.parseLong(tokenStat.nextToken());
            arr[1] = Long.parseLong(tokenStat.nextToken());
         }
      } catch (Exception var9) {
         logger.error((String)var9.getMessage(), (Throwable)var9);
      }

      return arr;
   }

   private static long formatLong(String line) {
      line = line.replace("RX packets", "").replace("TX packets", "").replace(":", "").trim().split(" ")[0];
      return Long.parseLong(line) * 1024L;
   }

   private static Double formatNumber(double f) {
      return Double.parseDouble((new Formatter()).format("%.2f", f).toString());
   }

   public static void main(String[] args) {
      String line = "RX packets:8889 errors:0 dropped:0 overruns:0 frame:0";
      System.out.println(formatLong(line));
   }
}
