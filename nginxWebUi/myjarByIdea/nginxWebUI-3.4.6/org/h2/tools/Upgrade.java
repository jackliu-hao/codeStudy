package org.h2.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.h2.engine.ConnectionInfo;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

public final class Upgrade {
   private static final String[] CHECKSUMS = new String[]{"6fca37906aa3916ba609f47258c4abb4c749cd51aa28718a2339d9aa234a480c", "3233d38ee11e15243f66c98ad388da9f12cf038a203cf507415081e3329ac4f4", "7451e9f234f32fd9f07e4e5e682c0595806a803de656228a43887a525019ea74", "5a4dfaf211d32860623fdc5627f12a9cf8446b9cfabc742e7c0bad26835a8bb1", "f75efcaf9ccb91d94de920322c32328435e9705c19cc06b510c5f09c0a6245bf", "0ca368055dd72d539084c916642147780c944b90d98d2306da86814b174d1145", "4d9143f5b80f8878ca56edc383ae6d0a183a3b5879e83228dbacbe288007455c", "3df7aedd564cf61a464f4e95ec364eb7bb2b51d36863ed54edeb6ff2fed7b376", "7e8af7b5eca6334013fc024dab02e173a017b2d1c22c8481ed64a6af873d0819", "9a705009830ae80a368b1b66c8ba63071845fe25d8f6b0964aa14a3f31b46bdd", "8810d72867508b033a68830024e7fe7dd5a99e6f5bbb38c5a933aeb23badff00", "c8debc05829db1db2e6b6507a3f0561e1f72bd966d36f322bdf294baca29ed22", "75819d4adbf76d66af904e76b52b57afe26e9bc0e15aceed4e3c72cd7586b0d3", "c9ea3e95e77ae560322bca37d51601ae4b1d07ae90988af1e9fe1ceda80cd9ce", "1f4753d8d862d7d22d234625f617d3d7e91b73799c89b8a6036895f944a863eb", "eed53fcd3cf6e1159c90e57ef2b4cbd1fa3aff7a936988bb018af6fc17a2b6d9", "d3101d540ed004493952732d28bdf90a7968990bab7a2e04d16805469aa4eedd", "035dd78af874ada48339b90e8e4f1ffba0f32bb0fa37dec37ed523afa96a9c32", "1d03156b22b40812e39cca4d250eededfed4db8476bfbae78d60a24975cbe6d8", "8102cc96257d71caeff04f02c97020ae39268a32c1f0aa8fcdfda4e948ce48c8", "134ceafcae6ca661d8acd64c8e67d30f6ead609065dba9f6d3a0cde0d7bef6e3", "e453faccaaf7d8fe4eb8be744549c4a2395c7b3dcfcbc19173588c3756baff1e", "5973b4b467f1e0a69cf8c7b02d03d9dcadb4171d8a9635c85442a5829200e76f", "711cc225d8fe5325458c3947dda2093ef3a1cd4923e916082b27e87e41ca6735", "682f6997495a8389f4881b93cb8224685b9c6cbed487bcb445712402e52a4b80", "1407913cc6ba2f8c2928e8ad544c232273365d6eb66fdf84ec4213abf71449d5", "7756a89f10d5d5df23936bbb613de8b481e32d1099e5228968046fee62fee882", "2649d19db9eebbddc826029d236886dfece9404cd108ca590e82d3fd7d888278", "66f9389748f176c11c66c201a3737ebad0b1f4ace37cc2cd3da8962c92c72128", "7c3e3b93ffaf617393126870be7f8e1708bbe8e05b931c51c638a8cb03f79a36", "1d6dc1095d3d4b105a99034ab61ab5943c4dbb31551e7b244b403cb3c324964f", "8eabfde7cf64cedb7c25dc25ee7fe75a633c5cbeb18a1060da2045293fd53b14", "a9840c6024f8570ad3aa4d54388b4dd605640cb5ab163c444a123f7d4739aa09", "33d80491417eb117a0d64442dc3e60b78cf014ad099bb36a55d3835bb69e6248", "f153d03466acc00b66e699213fe092277e457502b5caf48c417ed3745f50eaac", "244b29d22939b43ecdcd3b0bfd279899df18e3af20a50241278b5b27bcf1a902", "070f9e4898044880e01232b269fea5285dbf7b814b7092701e755aa7d6941832", "4666d8f01c661054b973bc0f01f8b20f298d8e134e6fd26d78c74d43eeffd54e", "b0d95f18474beea619fcfba83f033e5702483457e0f0a1d1ffb4b757c5182582", "17aa5ced25f13f9adc2820e0ccc3010e3ce55944d10c9e2c0c631b77674d039b", "7fe66e211202733c52f02a328b55b30975287d9c509751bf87507e6227c6a2a7", "42e2ebbb7bdf29dd2de4ab16fc8fb511af6337d223afd66a5ee5fe183de05d57", "89e362f9525adf36d58487ff756ee93254bf92595a7098258a4c030e08e0742e", "1d1be843af365e8881e22732c8640e2b04c2821a0d7aa61d4152ac3f991bb735", "dbc88bb8cd8177b5f13b655d6afb525637129369422f0b7be0fe187950ea5132", "03f60ca37c0124fd2b9b177726396a51853ed0cade444e1674a090b73d341b08", "35103656071f1ffd1078b1a8c8028c9577297f31c5f8c7dcc845c7b4b6392619", "fa97521a2e72174485a96276bcf6f573d5e44ca6aba2f62de87b33b5bb0d4b91", "46d7ff55ccd910def16f9afd21d983f2eb2f9a6850fb501916f6673caebc2694", "0d99d51b8d7b8e94732d048438b9f555e031ecd52225613d7bea45290571886d", "0aca5eea86e8619e91ad61b82b77fb9d0e51e939c5603ab8da41be32c6f25664", "144d4ddb5d9f610b8b26809f1c65f442864cc55136325d3f02d7a93fb878a1db", "6ca30e38ccaa0c6f4264ef013327ef9ba5303f4be3d8fdbce0c3ae6451178c1e", "43908ee9db698cb335e2b85375d68a9d03d818869a0542b85d8d4e416619795b", "990b94cdfc89987281af4168fc2f6c9067be96a8533f5a6eb0f33da4d30d3e4b", "cc329a8742fb6e7168b00ebd0015816ff0d2462409add7c9d223826486de4691", "6ae3cc11a8bbaa5bd1d8494e62bccea4d354eaf042da468eac3bc5009fd33b67", "f281673f3248a4b5cb03fdc0cc39b944fe978366be959d0e8106fcc3197f4705", "da08fef0b2bc0ff8876f895e17605daf514405a064e3c2c11d2275a19d301be6", "2b76304ce4256ee9fd61156f9b6ef82c049ffdc8dc89af07fcf59e9532c7e7cd", "16428fd1e6a3e5baa8067c1c2e777e1e99af68c6ef3ff7fbbf1938937a048a82", "44673ff2834428fdb7f11dac3b9d679fb3039ea32194a69452971fdd7150a08b", "1025d0d70a4e899c41bc8fd7370cd3768826e78da91b66fd9357e44d03d79d30", "b3ff2ebe161976124965a9a841877ec4f6e913dbadcc31af27f1b99f6abd57e9", "9e47e14d5b4b9ead127b15a33b107ff06f0a7dd3f98b5d6c149e6ccae05dc0a2", "c4ac74be5971445e270bbd4344be58d9a06dc927223614217e5a87257a7edc03", "e3b7a39a2b45b61fa1521ef33b3ba676a5a9e1a397bc3ef4fb678d861a1b0ae4", "6204d0c206443681911fb9e04a3af5198b253b5627d16d8d8d79180d13319212", "11d6bff477f7ca392288f5f6d42ee61d0ccb63a34c99ba2d91710b2409673897", "c8dac03b66c8011cca4e44dcc7a8b1c8f8df769927c7672be1704e76f9ee7926", "23ba495a07bbbb3bd6c3084d10a96dad7a23741b8b6d64b213459a784195a98c", "e21ea665b74ec0115344b5afda5ec70ea27b528c3f103524e74c9854b1c4a284", "225b22e9857235c46c93861410b60b8c81c10dc8985f4faf188985ba5445126c", "b1cf34c64871014aa73580281cc464dfa72450d8860cc0752fc175e87edd6544", "b5b0c1836cead6831a50bd3e1b6c16fe6e583d4d2b7c4f41b4f838745c27cd01", "b99ea1f785c62b2a021664e72de696f8ea896f0da392a1c7baa3d4d47020b126", "0a05f4a0d5b85840148aadce63a423b5d3c36ef44756389b4faad08d2733faf5", "37f5216e14af2772930dff9b8734353f0a80e89ba3f33e065441de6537c5e842", "32dd6b149cb722aa4c2dd4d40a74a9cd41e32ac59a4e755a66e5753660d61d46", "3125a16743bc6b4cfbb61abba783203f1fb68230aa0fdc97898f796f99a5d42e", "3ad9ac4b6aae9cd9d3ac1c447465e1ed06019b851b893dd6a8d76ddb6d85bca6"};
   private static final String REPOSITORY = "https://repo1.maven.org/maven2";

   public static boolean upgrade(String var0, Properties var1, int var2) throws Exception {
      Properties var3 = new Properties();
      var3.putAll(var1);
      Object var4 = var1.get("password");
      if (var4 instanceof char[]) {
         var3.put("password", ((char[])((char[])var4)).clone());
      }

      ConnectionInfo var5 = new ConnectionInfo(var0, var1, (String)null, (Object)null);
      if (var5.isPersistent() && !var5.isRemote()) {
         String var6 = var5.getName();
         String var7 = var6 + ".script.sql";
         StringBuilder var8 = (new StringBuilder("jdbc:h2:")).append(var6).append(";ACCESS_MODE_DATA=r");
         copyProperty(var5, var8, "FILE_LOCK");
         copyProperty(var5, var8, "MV_STORE");
         String var9 = copyProperty(var5, var8, "CIPHER");
         String var10 = var9 == null ? "" : " CIPHER AES PASSWORD '" + UUID.randomUUID() + "' --hide--";
         Driver var11 = loadH2(var2);

         Throwable var13;
         try {
            Connection var12 = var11.connect(var8.toString(), var3);
            var13 = null;

            try {
               var12.createStatement().execute(StringUtils.quoteStringSQL(new StringBuilder("SCRIPT TO "), var7).append(var10).toString());
            } catch (Throwable var65) {
               var13 = var65;
               throw var65;
            } finally {
               if (var12 != null) {
                  if (var13 != null) {
                     try {
                        var12.close();
                     } catch (Throwable var64) {
                        var13.addSuppressed(var64);
                     }
                  } else {
                     var12.close();
                  }
               }

            }
         } finally {
            unloadH2(var11);
         }

         rename(var6, false);

         try {
            JdbcConnection var72 = new JdbcConnection(var0, var1, (String)null, (Object)null, false);
            var13 = null;

            try {
               StringBuilder var14 = StringUtils.quoteStringSQL(new StringBuilder("RUNSCRIPT FROM "), var7).append(var10);
               if (var2 <= 200) {
                  var14.append(" FROM_1X");
               }

               var72.createStatement().execute(var14.toString());
            } catch (Throwable var68) {
               var13 = var68;
               throw var68;
            } finally {
               if (var72 != null) {
                  if (var13 != null) {
                     try {
                        var72.close();
                     } catch (Throwable var63) {
                        var13.addSuppressed(var63);
                     }
                  } else {
                     var72.close();
                  }
               }

            }
         } catch (Throwable var70) {
            rename(var6, true);
            throw var70;
         } finally {
            Files.deleteIfExists(Paths.get(var7));
         }

         return true;
      } else {
         return false;
      }
   }

   private static void rename(String var0, boolean var1) throws IOException {
      rename(var0, ".mv.db", var1);
      rename(var0, ".lobs.db", var1);
   }

   private static void rename(String var0, String var1, boolean var2) throws IOException {
      String var3 = var0 + var1;
      String var4 = var3 + ".bak";
      if (var2) {
         String var5 = var3;
         var3 = var4;
         var4 = var5;
      }

      Path var6 = Paths.get(var3);
      if (Files.exists(var6, new LinkOption[0])) {
         Files.move(var6, Paths.get(var4), StandardCopyOption.ATOMIC_MOVE);
      }

   }

   private static String copyProperty(ConnectionInfo var0, StringBuilder var1, String var2) {
      try {
         String var3 = var0.getProperty(var2, (String)null);
         if (var3 != null) {
            var1.append(';').append(var2).append('=').append(var3);
         }

         return var3;
      } catch (Exception var4) {
         return null;
      }
   }

   public static Driver loadH2(int var0) throws IOException, ReflectiveOperationException {
      String var1;
      if (var0 >= 201) {
         if ((var0 & 1) != 0 || var0 > 210) {
            throw new IllegalArgumentException("version=" + var0);
         }

         var1 = "2.0.";
      } else if (var0 >= 177) {
         var1 = "1.4.";
      } else if (var0 >= 146 && var0 != 147) {
         var1 = "1.3.";
      } else {
         if (var0 < 120) {
            throw new IllegalArgumentException("version=" + var0);
         }

         var1 = "1.2.";
      }

      String var2 = var1 + var0;
      byte[] var3 = downloadUsingMaven("com.h2database", "h2", var2, CHECKSUMS[var0 - 120]);
      ZipInputStream var4 = new ZipInputStream(new ByteArrayInputStream(var3));
      final HashMap var5 = new HashMap(var0 >= 198 ? 2048 : 1024);
      ByteArrayOutputStream var6 = new ByteArrayOutputStream();

      ZipEntry var7;
      while((var7 = var4.getNextEntry()) != null) {
         if (!var7.isDirectory()) {
            IOUtils.copy(var4, var6);
            var5.put(var7.getName(), var6.toByteArray());
            var6.reset();
         }
      }

      ClassLoader var8 = new ClassLoader((ClassLoader)null) {
         protected Class<?> findClass(String var1) throws ClassNotFoundException {
            String var2 = var1.replace('.', '/') + ".class";
            byte[] var3 = (byte[])var5.get(var2);
            return var3 == null ? ClassLoader.getSystemClassLoader().loadClass(var1) : this.defineClass(var1, var3, 0, var3.length);
         }

         public InputStream getResourceAsStream(String var1) {
            byte[] var2 = (byte[])var5.get(var1);
            return var2 != null ? new ByteArrayInputStream(var2) : null;
         }
      };
      return (Driver)var8.loadClass("org.h2.Driver").getDeclaredMethod("load").invoke((Object)null);
   }

   public static void unloadH2(Driver var0) throws ReflectiveOperationException {
      var0.getClass().getDeclaredMethod("unload").invoke((Object)null);
   }

   private static byte[] downloadUsingMaven(String var0, String var1, String var2, String var3) throws IOException {
      String var4 = var0.replace('.', '/') + '/' + var1 + '/' + var2 + '/' + var1 + '-' + var2 + ".jar";
      Path var5 = Paths.get(System.getProperty("user.home") + "/.m2/repository");
      if (Files.isDirectory(var5, new LinkOption[0])) {
         Path var6 = var5.resolve(var4);
         if (!Files.exists(var6, new LinkOption[0])) {
            try {
               ArrayList var7 = new ArrayList();
               if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                  var7.add("cmd");
                  var7.add("/C");
               }

               var7.add("mvn");
               var7.add("org.apache.maven.plugins:maven-dependency-plugin:2.1:get");
               var7.add("-DrepoUrl=https://repo1.maven.org/maven2");
               var7.add("-Dartifact=" + var0 + ':' + var1 + ':' + var2);
               exec(var7);
            } catch (RuntimeException var8) {
               System.out.println("Could not download using Maven: " + var8.toString());
            }
         }

         if (Files.exists(var6, new LinkOption[0])) {
            return check(Files.readAllBytes(var6), var3, var6.toAbsolutePath().toString());
         }
      }

      return download("https://repo1.maven.org/maven2/" + var4, var3);
   }

   private static int exec(ArrayList<String> var0) {
      try {
         ProcessBuilder var1 = new ProcessBuilder(new String[0]);
         var1.command((String[])var0.toArray(new String[0]));
         var1.inheritIO();
         Process var2 = var1.start();
         var2.waitFor();
         return var2.exitValue();
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static byte[] download(String var0, String var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try {
         System.out.println("Downloading " + var0);
         URL var3 = new URL(var0);
         BufferedInputStream var4 = new BufferedInputStream(var3.openStream());
         long var5 = System.nanoTime();
         int var7 = 0;

         while(true) {
            long var8 = System.nanoTime();
            if (var8 - var5 > 1000000000L) {
               System.out.println("Downloaded " + var7 + " bytes");
               var5 = var8;
            }

            int var10 = var4.read();
            ++var7;
            if (var10 < 0) {
               var4.close();
               return check(var2.toByteArray(), var1, (String)null);
            }

            var2.write(var10);
         }
      } catch (IOException var11) {
         throw new RuntimeException("Error downloading " + var0, var11);
      }
   }

   private static byte[] check(byte[] var0, String var1, String var2) {
      String var3 = getSHA256(var0);
      if (var1 == null) {
         System.out.println('"' + var3 + '"');
      } else if (!var3.equals(var1)) {
         StringBuilder var4 = (new StringBuilder()).append("SHA-256 checksum mismatch; got: ").append(var3).append(" expected: ").append(var1);
         if (var2 != null) {
            var4.append(" for file ").append(var2);
         }

         throw new RuntimeException(var4.toString());
      }

      return var0;
   }

   private static String getSHA256(byte[] var0) {
      try {
         return StringUtils.convertBytesToHex(MessageDigest.getInstance("SHA-256").digest(var0));
      } catch (NoSuchAlgorithmException var2) {
         throw new RuntimeException(var2);
      }
   }

   private Upgrade() {
   }
}
