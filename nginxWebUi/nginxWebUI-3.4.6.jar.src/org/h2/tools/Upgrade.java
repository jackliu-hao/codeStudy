/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Properties;
/*     */ import java.util.UUID;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.h2.engine.ConnectionInfo;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Upgrade
/*     */ {
/*  39 */   private static final String[] CHECKSUMS = new String[] { "6fca37906aa3916ba609f47258c4abb4c749cd51aa28718a2339d9aa234a480c", "3233d38ee11e15243f66c98ad388da9f12cf038a203cf507415081e3329ac4f4", "7451e9f234f32fd9f07e4e5e682c0595806a803de656228a43887a525019ea74", "5a4dfaf211d32860623fdc5627f12a9cf8446b9cfabc742e7c0bad26835a8bb1", "f75efcaf9ccb91d94de920322c32328435e9705c19cc06b510c5f09c0a6245bf", "0ca368055dd72d539084c916642147780c944b90d98d2306da86814b174d1145", "4d9143f5b80f8878ca56edc383ae6d0a183a3b5879e83228dbacbe288007455c", "3df7aedd564cf61a464f4e95ec364eb7bb2b51d36863ed54edeb6ff2fed7b376", "7e8af7b5eca6334013fc024dab02e173a017b2d1c22c8481ed64a6af873d0819", "9a705009830ae80a368b1b66c8ba63071845fe25d8f6b0964aa14a3f31b46bdd", "8810d72867508b033a68830024e7fe7dd5a99e6f5bbb38c5a933aeb23badff00", "c8debc05829db1db2e6b6507a3f0561e1f72bd966d36f322bdf294baca29ed22", "75819d4adbf76d66af904e76b52b57afe26e9bc0e15aceed4e3c72cd7586b0d3", "c9ea3e95e77ae560322bca37d51601ae4b1d07ae90988af1e9fe1ceda80cd9ce", "1f4753d8d862d7d22d234625f617d3d7e91b73799c89b8a6036895f944a863eb", "eed53fcd3cf6e1159c90e57ef2b4cbd1fa3aff7a936988bb018af6fc17a2b6d9", "d3101d540ed004493952732d28bdf90a7968990bab7a2e04d16805469aa4eedd", "035dd78af874ada48339b90e8e4f1ffba0f32bb0fa37dec37ed523afa96a9c32", "1d03156b22b40812e39cca4d250eededfed4db8476bfbae78d60a24975cbe6d8", "8102cc96257d71caeff04f02c97020ae39268a32c1f0aa8fcdfda4e948ce48c8", "134ceafcae6ca661d8acd64c8e67d30f6ead609065dba9f6d3a0cde0d7bef6e3", "e453faccaaf7d8fe4eb8be744549c4a2395c7b3dcfcbc19173588c3756baff1e", "5973b4b467f1e0a69cf8c7b02d03d9dcadb4171d8a9635c85442a5829200e76f", "711cc225d8fe5325458c3947dda2093ef3a1cd4923e916082b27e87e41ca6735", "682f6997495a8389f4881b93cb8224685b9c6cbed487bcb445712402e52a4b80", "1407913cc6ba2f8c2928e8ad544c232273365d6eb66fdf84ec4213abf71449d5", "7756a89f10d5d5df23936bbb613de8b481e32d1099e5228968046fee62fee882", "2649d19db9eebbddc826029d236886dfece9404cd108ca590e82d3fd7d888278", "66f9389748f176c11c66c201a3737ebad0b1f4ace37cc2cd3da8962c92c72128", "7c3e3b93ffaf617393126870be7f8e1708bbe8e05b931c51c638a8cb03f79a36", "1d6dc1095d3d4b105a99034ab61ab5943c4dbb31551e7b244b403cb3c324964f", "8eabfde7cf64cedb7c25dc25ee7fe75a633c5cbeb18a1060da2045293fd53b14", "a9840c6024f8570ad3aa4d54388b4dd605640cb5ab163c444a123f7d4739aa09", "33d80491417eb117a0d64442dc3e60b78cf014ad099bb36a55d3835bb69e6248", "f153d03466acc00b66e699213fe092277e457502b5caf48c417ed3745f50eaac", "244b29d22939b43ecdcd3b0bfd279899df18e3af20a50241278b5b27bcf1a902", "070f9e4898044880e01232b269fea5285dbf7b814b7092701e755aa7d6941832", "4666d8f01c661054b973bc0f01f8b20f298d8e134e6fd26d78c74d43eeffd54e", "b0d95f18474beea619fcfba83f033e5702483457e0f0a1d1ffb4b757c5182582", "17aa5ced25f13f9adc2820e0ccc3010e3ce55944d10c9e2c0c631b77674d039b", "7fe66e211202733c52f02a328b55b30975287d9c509751bf87507e6227c6a2a7", "42e2ebbb7bdf29dd2de4ab16fc8fb511af6337d223afd66a5ee5fe183de05d57", "89e362f9525adf36d58487ff756ee93254bf92595a7098258a4c030e08e0742e", "1d1be843af365e8881e22732c8640e2b04c2821a0d7aa61d4152ac3f991bb735", "dbc88bb8cd8177b5f13b655d6afb525637129369422f0b7be0fe187950ea5132", "03f60ca37c0124fd2b9b177726396a51853ed0cade444e1674a090b73d341b08", "35103656071f1ffd1078b1a8c8028c9577297f31c5f8c7dcc845c7b4b6392619", "fa97521a2e72174485a96276bcf6f573d5e44ca6aba2f62de87b33b5bb0d4b91", "46d7ff55ccd910def16f9afd21d983f2eb2f9a6850fb501916f6673caebc2694", "0d99d51b8d7b8e94732d048438b9f555e031ecd52225613d7bea45290571886d", "0aca5eea86e8619e91ad61b82b77fb9d0e51e939c5603ab8da41be32c6f25664", "144d4ddb5d9f610b8b26809f1c65f442864cc55136325d3f02d7a93fb878a1db", "6ca30e38ccaa0c6f4264ef013327ef9ba5303f4be3d8fdbce0c3ae6451178c1e", "43908ee9db698cb335e2b85375d68a9d03d818869a0542b85d8d4e416619795b", "990b94cdfc89987281af4168fc2f6c9067be96a8533f5a6eb0f33da4d30d3e4b", "cc329a8742fb6e7168b00ebd0015816ff0d2462409add7c9d223826486de4691", "6ae3cc11a8bbaa5bd1d8494e62bccea4d354eaf042da468eac3bc5009fd33b67", "f281673f3248a4b5cb03fdc0cc39b944fe978366be959d0e8106fcc3197f4705", "da08fef0b2bc0ff8876f895e17605daf514405a064e3c2c11d2275a19d301be6", "2b76304ce4256ee9fd61156f9b6ef82c049ffdc8dc89af07fcf59e9532c7e7cd", "16428fd1e6a3e5baa8067c1c2e777e1e99af68c6ef3ff7fbbf1938937a048a82", "44673ff2834428fdb7f11dac3b9d679fb3039ea32194a69452971fdd7150a08b", "1025d0d70a4e899c41bc8fd7370cd3768826e78da91b66fd9357e44d03d79d30", "b3ff2ebe161976124965a9a841877ec4f6e913dbadcc31af27f1b99f6abd57e9", "9e47e14d5b4b9ead127b15a33b107ff06f0a7dd3f98b5d6c149e6ccae05dc0a2", "c4ac74be5971445e270bbd4344be58d9a06dc927223614217e5a87257a7edc03", "e3b7a39a2b45b61fa1521ef33b3ba676a5a9e1a397bc3ef4fb678d861a1b0ae4", "6204d0c206443681911fb9e04a3af5198b253b5627d16d8d8d79180d13319212", "11d6bff477f7ca392288f5f6d42ee61d0ccb63a34c99ba2d91710b2409673897", "c8dac03b66c8011cca4e44dcc7a8b1c8f8df769927c7672be1704e76f9ee7926", "23ba495a07bbbb3bd6c3084d10a96dad7a23741b8b6d64b213459a784195a98c", "e21ea665b74ec0115344b5afda5ec70ea27b528c3f103524e74c9854b1c4a284", "225b22e9857235c46c93861410b60b8c81c10dc8985f4faf188985ba5445126c", "b1cf34c64871014aa73580281cc464dfa72450d8860cc0752fc175e87edd6544", "b5b0c1836cead6831a50bd3e1b6c16fe6e583d4d2b7c4f41b4f838745c27cd01", "b99ea1f785c62b2a021664e72de696f8ea896f0da392a1c7baa3d4d47020b126", "0a05f4a0d5b85840148aadce63a423b5d3c36ef44756389b4faad08d2733faf5", "37f5216e14af2772930dff9b8734353f0a80e89ba3f33e065441de6537c5e842", "32dd6b149cb722aa4c2dd4d40a74a9cd41e32ac59a4e755a66e5753660d61d46", "3125a16743bc6b4cfbb61abba783203f1fb68230aa0fdc97898f796f99a5d42e", "3ad9ac4b6aae9cd9d3ac1c447465e1ed06019b851b893dd6a8d76ddb6d85bca6" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String REPOSITORY = "https://repo1.maven.org/maven2";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean upgrade(String paramString, Properties paramProperties, int paramInt) throws Exception {
/* 141 */     Properties properties = new Properties();
/* 142 */     properties.putAll(paramProperties);
/* 143 */     Object object = paramProperties.get("password");
/* 144 */     if (object instanceof char[]) {
/* 145 */       properties.put("password", ((char[])object).clone());
/*     */     }
/* 147 */     ConnectionInfo connectionInfo = new ConnectionInfo(paramString, paramProperties, null, null);
/* 148 */     if (!connectionInfo.isPersistent() || connectionInfo.isRemote()) {
/* 149 */       return false;
/*     */     }
/* 151 */     String str1 = connectionInfo.getName();
/* 152 */     String str2 = str1 + ".script.sql";
/* 153 */     StringBuilder stringBuilder = (new StringBuilder("jdbc:h2:")).append(str1).append(";ACCESS_MODE_DATA=r");
/* 154 */     copyProperty(connectionInfo, stringBuilder, "FILE_LOCK");
/* 155 */     copyProperty(connectionInfo, stringBuilder, "MV_STORE");
/* 156 */     String str3 = copyProperty(connectionInfo, stringBuilder, "CIPHER");
/* 157 */     String str4 = (str3 == null) ? "" : (" CIPHER AES PASSWORD '" + UUID.randomUUID() + "' --hide--");
/* 158 */     Driver driver = loadH2(paramInt);
/* 159 */     try (Connection null = driver.connect(stringBuilder.toString(), properties)) {
/* 160 */       connection.createStatement().execute(StringUtils.quoteStringSQL(new StringBuilder("SCRIPT TO "), str2)
/* 161 */           .append(str4).toString());
/*     */     } finally {
/* 163 */       unloadH2(driver);
/*     */     } 
/* 165 */     rename(str1, false);
/* 166 */     try (JdbcConnection null = new JdbcConnection(paramString, paramProperties, null, null, false)) {
/*     */       
/* 168 */       StringBuilder stringBuilder1 = StringUtils.quoteStringSQL(new StringBuilder("RUNSCRIPT FROM "), str2).append(str4);
/* 169 */       if (paramInt <= 200) {
/* 170 */         stringBuilder1.append(" FROM_1X");
/*     */       }
/* 172 */       jdbcConnection.createStatement().execute(stringBuilder1.toString());
/* 173 */     } catch (Throwable throwable) {
/* 174 */       rename(str1, true);
/* 175 */       throw throwable;
/*     */     } finally {
/* 177 */       Files.deleteIfExists(Paths.get(str2, new String[0]));
/*     */     } 
/* 179 */     return true;
/*     */   }
/*     */   
/*     */   private static void rename(String paramString, boolean paramBoolean) throws IOException {
/* 183 */     rename(paramString, ".mv.db", paramBoolean);
/* 184 */     rename(paramString, ".lobs.db", paramBoolean);
/*     */   }
/*     */   
/*     */   private static void rename(String paramString1, String paramString2, boolean paramBoolean) throws IOException {
/* 188 */     String str1 = paramString1 + paramString2;
/* 189 */     String str2 = str1 + ".bak";
/* 190 */     if (paramBoolean) {
/* 191 */       String str = str1;
/* 192 */       str1 = str2;
/* 193 */       str2 = str;
/*     */     } 
/* 195 */     Path path = Paths.get(str1, new String[0]);
/* 196 */     if (Files.exists(path, new java.nio.file.LinkOption[0])) {
/* 197 */       Files.move(path, Paths.get(str2, new String[0]), new CopyOption[] { StandardCopyOption.ATOMIC_MOVE });
/*     */     }
/*     */   }
/*     */   
/*     */   private static String copyProperty(ConnectionInfo paramConnectionInfo, StringBuilder paramStringBuilder, String paramString) {
/*     */     try {
/* 203 */       String str = paramConnectionInfo.getProperty(paramString, null);
/* 204 */       if (str != null) {
/* 205 */         paramStringBuilder.append(';').append(paramString).append('=').append(str);
/*     */       }
/* 207 */       return str;
/* 208 */     } catch (Exception exception) {
/* 209 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Driver loadH2(int paramInt) throws IOException, ReflectiveOperationException {
/*     */     String str1;
/* 226 */     if (paramInt >= 201) {
/* 227 */       if ((paramInt & 0x1) != 0 || paramInt > 210) {
/* 228 */         throw new IllegalArgumentException("version=" + paramInt);
/*     */       }
/* 230 */       str1 = "2.0.";
/* 231 */     } else if (paramInt >= 177) {
/* 232 */       str1 = "1.4.";
/* 233 */     } else if (paramInt >= 146 && paramInt != 147) {
/* 234 */       str1 = "1.3.";
/* 235 */     } else if (paramInt >= 120) {
/* 236 */       str1 = "1.2.";
/*     */     } else {
/* 238 */       throw new IllegalArgumentException("version=" + paramInt);
/*     */     } 
/* 240 */     String str2 = str1 + paramInt;
/* 241 */     byte[] arrayOfByte = downloadUsingMaven("com.h2database", "h2", str2, CHECKSUMS[paramInt - 120]);
/* 242 */     ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(arrayOfByte));
/* 243 */     final HashMap<Object, Object> map = new HashMap<>((paramInt >= 198) ? 2048 : 1024);
/* 244 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ZipEntry zipEntry;
/* 245 */     while ((zipEntry = zipInputStream.getNextEntry()) != null) {
/* 246 */       if (zipEntry.isDirectory()) {
/*     */         continue;
/*     */       }
/* 249 */       IOUtils.copy(zipInputStream, byteArrayOutputStream);
/* 250 */       hashMap.put(zipEntry.getName(), byteArrayOutputStream.toByteArray());
/* 251 */       byteArrayOutputStream.reset();
/*     */     } 
/* 253 */     ClassLoader classLoader = new ClassLoader(null)
/*     */       {
/*     */         protected Class<?> findClass(String param1String) throws ClassNotFoundException {
/* 256 */           String str = param1String.replace('.', '/') + ".class";
/* 257 */           byte[] arrayOfByte = (byte[])map.get(str);
/* 258 */           if (arrayOfByte == null) {
/* 259 */             return ClassLoader.getSystemClassLoader().loadClass(param1String);
/*     */           }
/* 261 */           return defineClass(param1String, arrayOfByte, 0, arrayOfByte.length);
/*     */         }
/*     */ 
/*     */         
/*     */         public InputStream getResourceAsStream(String param1String) {
/* 266 */           byte[] arrayOfByte = (byte[])map.get(param1String);
/* 267 */           return (arrayOfByte != null) ? new ByteArrayInputStream(arrayOfByte) : null;
/*     */         }
/*     */       };
/* 270 */     return (Driver)classLoader.loadClass("org.h2.Driver").getDeclaredMethod("load", new Class[0]).invoke(null, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unloadH2(Driver paramDriver) throws ReflectiveOperationException {
/* 282 */     paramDriver.getClass().getDeclaredMethod("unload", new Class[0]).invoke(null, new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] downloadUsingMaven(String paramString1, String paramString2, String paramString3, String paramString4) throws IOException {
/* 287 */     String str = paramString1.replace('.', '/') + '/' + paramString2 + '/' + paramString3 + '/' + paramString2 + '-' + paramString3 + ".jar";
/*     */     
/* 289 */     Path path = Paths.get(System.getProperty("user.home") + "/.m2/repository", new String[0]);
/* 290 */     if (Files.isDirectory(path, new java.nio.file.LinkOption[0])) {
/* 291 */       Path path1 = path.resolve(str);
/* 292 */       if (!Files.exists(path1, new java.nio.file.LinkOption[0])) {
/*     */         try {
/* 294 */           ArrayList<String> arrayList = new ArrayList();
/* 295 */           if (System.getProperty("os.name").toLowerCase().contains("windows")) {
/* 296 */             arrayList.add("cmd");
/* 297 */             arrayList.add("/C");
/*     */           } 
/* 299 */           arrayList.add("mvn");
/* 300 */           arrayList.add("org.apache.maven.plugins:maven-dependency-plugin:2.1:get");
/* 301 */           arrayList.add("-DrepoUrl=https://repo1.maven.org/maven2");
/* 302 */           arrayList.add("-Dartifact=" + paramString1 + ':' + paramString2 + ':' + paramString3);
/* 303 */           exec(arrayList);
/* 304 */         } catch (RuntimeException runtimeException) {
/* 305 */           System.out.println("Could not download using Maven: " + runtimeException.toString());
/*     */         } 
/*     */       }
/* 308 */       if (Files.exists(path1, new java.nio.file.LinkOption[0])) {
/* 309 */         return check(Files.readAllBytes(path1), paramString4, path1.toAbsolutePath().toString());
/*     */       }
/*     */     } 
/* 312 */     return download("https://repo1.maven.org/maven2/" + str, paramString4);
/*     */   }
/*     */   
/*     */   private static int exec(ArrayList<String> paramArrayList) {
/*     */     try {
/* 317 */       ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
/* 318 */       processBuilder.command(paramArrayList.<String>toArray(new String[0]));
/* 319 */       processBuilder.inheritIO();
/* 320 */       Process process = processBuilder.start();
/* 321 */       process.waitFor();
/* 322 */       return process.exitValue();
/* 323 */     } catch (Exception exception) {
/* 324 */       throw new RuntimeException(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static byte[] download(String paramString1, String paramString2) {
/* 329 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*     */     try {
/* 331 */       System.out.println("Downloading " + paramString1);
/* 332 */       URL uRL = new URL(paramString1);
/* 333 */       BufferedInputStream bufferedInputStream = new BufferedInputStream(uRL.openStream());
/* 334 */       long l = System.nanoTime();
/* 335 */       byte b = 0;
/*     */       while (true) {
/* 337 */         long l1 = System.nanoTime();
/* 338 */         if (l1 - l > 1000000000L) {
/* 339 */           System.out.println("Downloaded " + b + " bytes");
/* 340 */           l = l1;
/*     */         } 
/* 342 */         int i = bufferedInputStream.read();
/* 343 */         b++;
/* 344 */         if (i < 0) {
/*     */           break;
/*     */         }
/* 347 */         byteArrayOutputStream.write(i);
/*     */       } 
/* 349 */       bufferedInputStream.close();
/* 350 */     } catch (IOException iOException) {
/* 351 */       throw new RuntimeException("Error downloading " + paramString1, iOException);
/*     */     } 
/* 353 */     return check(byteArrayOutputStream.toByteArray(), paramString2, null);
/*     */   }
/*     */   
/*     */   private static byte[] check(byte[] paramArrayOfbyte, String paramString1, String paramString2) {
/* 357 */     String str = getSHA256(paramArrayOfbyte);
/* 358 */     if (paramString1 == null) {
/* 359 */       System.out.println('"' + str + '"');
/*     */     }
/* 361 */     else if (!str.equals(paramString1)) {
/*     */       
/* 363 */       StringBuilder stringBuilder = (new StringBuilder()).append("SHA-256 checksum mismatch; got: ").append(str).append(" expected: ").append(paramString1);
/* 364 */       if (paramString2 != null) {
/* 365 */         stringBuilder.append(" for file ").append(paramString2);
/*     */       }
/* 367 */       throw new RuntimeException(stringBuilder.toString());
/*     */     } 
/*     */     
/* 370 */     return paramArrayOfbyte;
/*     */   }
/*     */   
/*     */   private static String getSHA256(byte[] paramArrayOfbyte) {
/*     */     try {
/* 375 */       return StringUtils.convertBytesToHex(MessageDigest.getInstance("SHA-256").digest(paramArrayOfbyte));
/* 376 */     } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
/* 377 */       throw new RuntimeException(noSuchAlgorithmException);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Upgrade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */