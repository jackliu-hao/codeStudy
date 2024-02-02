package cn.hutool.core.lang;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectId {
   private static final AtomicInteger NEXT_INC = new AtomicInteger(RandomUtil.randomInt());
   private static final int MACHINE = getMachinePiece() | getProcessPiece();

   public static boolean isValid(String s) {
      if (s == null) {
         return false;
      } else {
         s = StrUtil.removeAll(s, "-");
         int len = s.length();
         if (len != 24) {
            return false;
         } else {
            for(int i = 0; i < len; ++i) {
               char c = s.charAt(i);
               if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static byte[] nextBytes() {
      ByteBuffer bb = ByteBuffer.wrap(new byte[12]);
      bb.putInt((int)DateUtil.currentSeconds());
      bb.putInt(MACHINE);
      bb.putInt(NEXT_INC.getAndIncrement());
      return bb.array();
   }

   public static String next() {
      return next(false);
   }

   public static String next(boolean withHyphen) {
      byte[] array = nextBytes();
      StringBuilder buf = new StringBuilder(withHyphen ? 26 : 24);

      for(int i = 0; i < array.length; ++i) {
         if (withHyphen && i % 4 == 0 && i != 0) {
            buf.append("-");
         }

         int t = array[i] & 255;
         if (t < 16) {
            buf.append('0');
         }

         buf.append(Integer.toHexString(t));
      }

      return buf.toString();
   }

   private static int getMachinePiece() {
      int machinePiece;
      try {
         StringBuilder netSb = new StringBuilder();
         Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

         while(e.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface)e.nextElement();
            netSb.append(ni.toString());
         }

         machinePiece = netSb.toString().hashCode() << 16;
      } catch (Throwable var4) {
         machinePiece = RandomUtil.randomInt() << 16;
      }

      return machinePiece;
   }

   private static int getProcessPiece() {
      int processId;
      try {
         processId = RuntimeUtil.getPid();
      } catch (Throwable var5) {
         processId = RandomUtil.randomInt();
      }

      ClassLoader loader = ClassLoaderUtil.getClassLoader();
      int loaderId = loader != null ? System.identityHashCode(loader) : 0;
      String processSb = Integer.toHexString(processId) + Integer.toHexString(loaderId);
      int processPiece = processSb.hashCode() & '\uffff';
      return processPiece;
   }
}
