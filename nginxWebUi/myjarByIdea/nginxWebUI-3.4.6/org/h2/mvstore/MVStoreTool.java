package org.h2.mvstore;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.Compressor;
import org.h2.message.DbException;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.StringDataType;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.util.Utils;

public class MVStoreTool {
   public static void main(String... var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         String var2;
         if ("-dump".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            dump(var2, new PrintWriter(System.out), true);
         } else if ("-info".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            info(var2, new PrintWriter(System.out));
         } else if ("-compact".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            compact(var2, false);
         } else if ("-compress".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            compact(var2, true);
         } else if ("-rollback".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            ++var1;
            long var3 = Long.decode(var0[var1]);
            rollback(var2, var3, new PrintWriter(System.out));
         } else if ("-repair".equals(var0[var1])) {
            ++var1;
            var2 = var0[var1];
            repair(var2);
         }
      }

   }

   public static void dump(String var0, boolean var1) {
      dump(var0, new PrintWriter(System.out), var1);
   }

   public static void info(String var0) {
      info(var0, new PrintWriter(System.out));
   }

   public static void dump(String var0, Writer var1, boolean var2) {
      PrintWriter var3 = new PrintWriter(var1, true);
      if (!FilePath.get(var0).exists()) {
         var3.println("File not found: " + var0);
      } else {
         long var4 = FileUtils.size(var0);
         var3.printf("File %s, %d bytes, %d MB\n", var0, var4, var4 / 1024L / 1024L);
         short var6 = 4096;
         TreeMap var7 = new TreeMap();
         long var8 = 0L;

         try {
            FileChannel var10 = FilePath.get(var0).open("r");
            Throwable var11 = null;

            try {
               long var12 = var10.size();
               int var14 = Long.toHexString(var12).length();
               ByteBuffer var15 = ByteBuffer.allocate(4096);
               long var16 = 0L;
               long var18 = 0L;

               label493:
               while(true) {
                  while(true) {
                     int var20;
                     while(true) {
                        if (var18 >= var12) {
                           var3.printf("%n%0" + var14 + "x eof%n", var12);
                           var3.printf("\n");
                           var16 = Math.max(1L, var16);
                           var3.printf("page size total: %d bytes, page count: %d, average page size: %d bytes\n", var8, var16, var8 / var16);
                           var8 = Math.max(1L, var8);
                           Iterator var66 = var7.keySet().iterator();

                           while(var66.hasNext()) {
                              Integer var19 = (Integer)var66.next();
                              var20 = (int)(100L * (Long)var7.get(var19) / var8);
                              var3.printf("map %x: %d bytes, %d%%%n", var19, var7.get(var19), var20);
                           }
                           break label493;
                        }

                        var15.rewind();

                        try {
                           DataUtils.readFully(var10, var18, var15);
                           break;
                        } catch (MVStoreException var60) {
                           var18 += (long)var6;
                           var3.printf("ERROR illegal position %d%n", var18);
                        }
                     }

                     var15.rewind();
                     var20 = var15.get();
                     if (var20 == 72) {
                        String var67 = (new String(var15.array(), StandardCharsets.ISO_8859_1)).trim();
                        var3.printf("%0" + var14 + "x fileHeader %s%n", var18, var67);
                        var18 += (long)var6;
                     } else if (var20 != 99) {
                        var18 += (long)var6;
                     } else {
                        var15.position(0);

                        Chunk var21;
                        try {
                           var21 = Chunk.readChunkHeader(var15, var18);
                        } catch (MVStoreException var61) {
                           var18 += (long)var6;
                           continue;
                        }

                        if (var21.len <= 0) {
                           var18 += (long)var6;
                        } else {
                           int var22 = var21.len * 4096;
                           var3.printf("%n%0" + var14 + "x chunkHeader %s%n", var18, var21.toString());
                           ByteBuffer var23 = ByteBuffer.allocate(var22);
                           DataUtils.readFully(var10, var18, var23);
                           int var24 = var15.position();
                           var18 += (long)var22;
                           int var25 = var21.pageCount;
                           var16 += (long)var21.pageCount;
                           TreeMap var26 = new TreeMap();
                           int var27 = 0;

                           int var28;
                           int var30;
                           while(var25 > 0) {
                              var28 = var24;

                              try {
                                 var23.position(var24);
                              } catch (IllegalArgumentException var62) {
                                 var3.printf("ERROR illegal position %d%n", var24);
                                 break;
                              }

                              int var29 = var23.getInt();
                              var23.getShort();
                              DataUtils.readVarInt(var23);
                              var30 = DataUtils.readVarInt(var23);
                              int var31 = DataUtils.readVarInt(var23);
                              byte var32 = var23.get();
                              boolean var33 = (var32 & 2) != 0;
                              boolean var34 = (var32 & 1) != 0;
                              if (var2) {
                                 var3.printf("+%0" + var14 + "x %s, map %x, %d entries, %d bytes, maxLen %x%n", var24, (var34 ? "node" : "leaf") + (var33 ? " compressed" : ""), var30, var34 ? var31 + 1 : var31, var29, DataUtils.getPageMaxLength(DataUtils.getPagePos(0, 0, var29, 0)));
                              }

                              var24 += var29;
                              Integer var35 = (Integer)var26.get(var30);
                              if (var35 == null) {
                                 var35 = 0;
                              }

                              var26.put(var30, var35 + var29);
                              Long var36 = (Long)var7.get(var30);
                              if (var36 == null) {
                                 var36 = 0L;
                              }

                              var7.put(var30, var36 + (long)var29);
                              var27 += var29;
                              var8 += (long)var29;
                              --var25;
                              long[] var37 = null;
                              long[] var38 = null;
                              if (var34) {
                                 var37 = new long[var31 + 1];

                                 int var39;
                                 for(var39 = 0; var39 <= var31; ++var39) {
                                    var37[var39] = var23.getLong();
                                 }

                                 var38 = new long[var31 + 1];

                                 for(var39 = 0; var39 <= var31; ++var39) {
                                    long var40 = DataUtils.readVarLong(var23);
                                    var38[var39] = var40;
                                 }
                              }

                              String[] var70 = new String[var31];
                              long var41;
                              if (var30 == 0 && var2) {
                                 ByteBuffer var72;
                                 if (var33) {
                                    boolean var73 = (var32 & 6) != 6;
                                    Compressor var42 = getCompressor(var73);
                                    int var43 = DataUtils.readVarInt(var23);
                                    int var44 = var29 + var28 - var23.position();
                                    byte[] var45 = Utils.newBytes(var44);
                                    var23.get(var45);
                                    int var46 = var44 + var43;
                                    var72 = ByteBuffer.allocate(var46);
                                    var42.expand(var45, 0, var44, var72.array(), 0, var46);
                                 } else {
                                    var72 = var23;
                                 }

                                 int var74;
                                 for(var74 = 0; var74 < var31; ++var74) {
                                    String var75 = StringDataType.INSTANCE.read(var72);
                                    var70[var74] = var75;
                                 }

                                 if (var34) {
                                    for(var74 = 0; var74 < var31; ++var74) {
                                       long var79 = var37[var74];
                                       var3.printf("    %d children < %s @ chunk %x +%0" + var14 + "x%n", var38[var74], var70[var74], DataUtils.getPageChunkId(var79), DataUtils.getPageOffset(var79));
                                    }

                                    var41 = var37[var31];
                                    var3.printf("    %d children >= %s @ chunk %x +%0" + var14 + "x%n", var38[var31], var70.length >= var31 ? null : var70[var31], DataUtils.getPageChunkId(var41), DataUtils.getPageOffset(var41));
                                 } else {
                                    String[] var76 = new String[var31];

                                    int var77;
                                    for(var77 = 0; var77 < var31; ++var77) {
                                       String var78 = StringDataType.INSTANCE.read(var72);
                                       var76[var77] = var78;
                                    }

                                    for(var77 = 0; var77 < var31; ++var77) {
                                       var3.println("    " + var70[var77] + " = " + var76[var77]);
                                    }
                                 }
                              } else if (var34 && var2) {
                                 for(int var71 = 0; var71 <= var31; ++var71) {
                                    var41 = var37[var71];
                                    var3.printf("    %d children @ chunk %x +%0" + var14 + "x%n", var38[var71], DataUtils.getPageChunkId(var41), DataUtils.getPageOffset(var41));
                                 }
                              }
                           }

                           var27 = Math.max(1, var27);
                           Iterator var68 = var26.keySet().iterator();

                           while(var68.hasNext()) {
                              Integer var69 = (Integer)var68.next();
                              var30 = 100 * (Integer)var26.get(var69) / var27;
                              var3.printf("map %x: %d bytes, %d%%%n", var69, var26.get(var69), var30);
                           }

                           var28 = var23.limit() - 128;

                           try {
                              var23.position(var28);
                              var3.printf("+%0" + var14 + "x chunkFooter %s%n", var28, (new String(var23.array(), var23.position(), 128, StandardCharsets.ISO_8859_1)).trim());
                           } catch (IllegalArgumentException var59) {
                              var3.printf("ERROR illegal footer position %d%n", var28);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var63) {
               var11 = var63;
               throw var63;
            } finally {
               if (var10 != null) {
                  if (var11 != null) {
                     try {
                        var10.close();
                     } catch (Throwable var58) {
                        var11.addSuppressed(var58);
                     }
                  } else {
                     var10.close();
                  }
               }

            }
         } catch (IOException var65) {
            var3.println("ERROR: " + var65);
            var65.printStackTrace(var3);
         }

         var3.flush();
      }
   }

   private static Compressor getCompressor(boolean var0) {
      return (Compressor)(var0 ? new CompressLZF() : new CompressDeflate());
   }

   public static String info(String var0, Writer var1) {
      PrintWriter var2 = new PrintWriter(var1, true);
      if (!FilePath.get(var0).exists()) {
         var2.println("File not found: " + var0);
         return "File not found: " + var0;
      } else {
         long var3 = FileUtils.size(var0);

         try {
            MVStore var5 = (new MVStore.Builder()).fileName(var0).recoveryMode().readOnly().open();
            Throwable var6 = null;

            try {
               MVMap var7 = var5.getLayoutMap();
               Map var8 = var5.getStoreHeader();
               long var9 = DataUtils.readHexLong(var8, "created", 0L);
               TreeMap var11 = new TreeMap();
               long var12 = 0L;
               long var14 = 0L;
               long var16 = 0L;
               long var18 = 0L;
               Iterator var20 = var7.entrySet().iterator();

               Map.Entry var21;
               while(var20.hasNext()) {
                  var21 = (Map.Entry)var20.next();
                  String var22 = (String)var21.getKey();
                  if (var22.startsWith("chunk.")) {
                     Chunk var23 = Chunk.fromString((String)var21.getValue());
                     var11.put(var23.id, var23);
                     var12 += (long)(var23.len * 4096);
                     var14 += var23.maxLen;
                     var16 += var23.maxLenLive;
                     if (var23.maxLenLive > 0L) {
                        var18 += var23.maxLen;
                     }
                  }
               }

               var2.printf("Created: %s\n", formatTimestamp(var9, var9));
               var2.printf("Last modified: %s\n", formatTimestamp(FileUtils.lastModified(var0), var9));
               var2.printf("File length: %d\n", var3);
               var2.printf("The last chunk is not listed\n");
               var2.printf("Chunk length: %d\n", var12);
               var2.printf("Chunk count: %d\n", var11.size());
               var2.printf("Used space: %d%%\n", getPercent(var12, var3));
               var2.printf("Chunk fill rate: %d%%\n", var14 == 0L ? 100 : getPercent(var16, var14));
               var2.printf("Chunk fill rate excluding empty chunks: %d%%\n", var18 == 0L ? 100 : getPercent(var16, var18));
               var20 = var11.entrySet().iterator();

               while(true) {
                  if (!var20.hasNext()) {
                     var2.printf("\n");
                     break;
                  }

                  var21 = (Map.Entry)var20.next();
                  Chunk var36 = (Chunk)var21.getValue();
                  long var37 = var9 + var36.time;
                  var2.printf("  Chunk %d: %s, %d%% used, %d blocks", var36.id, formatTimestamp(var37, var9), getPercent(var36.maxLenLive, var36.maxLen), var36.len);
                  if (var36.maxLenLive == 0L) {
                     var2.printf(", unused: %s", formatTimestamp(var9 + var36.unused, var9));
                  }

                  var2.printf("\n");
               }
            } catch (Throwable var33) {
               var6 = var33;
               throw var33;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var32) {
                        var6.addSuppressed(var32);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Exception var35) {
            var2.println("ERROR: " + var35);
            var35.printStackTrace(var2);
            return var35.getMessage();
         }

         var2.flush();
         return null;
      }
   }

   private static String formatTimestamp(long var0, long var2) {
      String var4 = (new Timestamp(var0)).toString();
      String var5 = var4.substring(0, 19);
      var5 = var5 + " (+" + (var0 - var2) / 1000L + " s)";
      return var5;
   }

   private static int getPercent(long var0, long var2) {
      if (var0 == 0L) {
         return 0;
      } else {
         return var0 == var2 ? 100 : (int)(1L + 98L * var0 / Math.max(1L, var2));
      }
   }

   public static void compact(String var0, boolean var1) {
      String var2 = var0 + ".tempFile";
      FileUtils.delete(var2);
      compact(var0, var2, var1);

      try {
         FileUtils.moveAtomicReplace(var2, var0);
      } catch (DbException var5) {
         String var4 = var0 + ".newFile";
         FileUtils.delete(var4);
         FileUtils.move(var2, var4);
         FileUtils.delete(var0);
         FileUtils.move(var4, var0);
      }

   }

   public static void compactCleanUp(String var0) {
      String var1 = var0 + ".tempFile";
      if (FileUtils.exists(var1)) {
         FileUtils.delete(var1);
      }

      String var2 = var0 + ".newFile";
      if (FileUtils.exists(var2)) {
         if (FileUtils.exists(var0)) {
            FileUtils.delete(var2);
         } else {
            FileUtils.move(var2, var0);
         }
      }

   }

   public static void compact(String var0, String var1, boolean var2) {
      MVStore var3 = (new MVStore.Builder()).fileName(var0).readOnly().open();
      Throwable var4 = null;

      try {
         FileUtils.delete(var1);
         MVStore.Builder var5 = (new MVStore.Builder()).fileName(var1);
         if (var2) {
            var5.compress();
         }

         MVStore var6 = var5.open();
         Throwable var7 = null;

         try {
            compact(var3, var6);
         } catch (Throwable var30) {
            var7 = var30;
            throw var30;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var29) {
                     var7.addSuppressed(var29);
                  }
               } else {
                  var6.close();
               }
            }

         }
      } catch (Throwable var32) {
         var4 = var32;
         throw var32;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var28) {
                  var4.addSuppressed(var28);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   public static void compact(MVStore var0, MVStore var1) {
      int var2 = var1.getAutoCommitDelay();
      boolean var3 = var1.getReuseSpace();

      try {
         var1.setReuseSpace(false);
         var1.setAutoCommitDelay(0);
         MVMap var4 = var0.getMetaMap();
         MVMap var5 = var1.getMetaMap();
         Iterator var6 = var4.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            String var8 = (String)var7.getKey();
            if (!var8.startsWith("map.") && !var8.startsWith("name.")) {
               var5.put(var8, var7.getValue());
            }
         }

         var6 = var0.getMapNames().iterator();

         while(var6.hasNext()) {
            String var14 = (String)var6.next();
            MVMap.Builder var15 = getGenericMapBuilder();
            if (var14.startsWith("undoLog")) {
               var15.singleWriter();
            }

            MVMap var9 = var0.openMap(var14, var15);
            MVMap var10 = var1.openMap(var14, var15);
            var10.copyFrom(var9);
            var5.put(MVMap.getMapKey(var10.getId()), var4.get(MVMap.getMapKey(var9.getId())));
         }

         var1.commit();
      } finally {
         var1.setAutoCommitDelay(var2);
         var1.setReuseSpace(var3);
      }

   }

   public static void repair(String var0) {
      PrintWriter var1 = new PrintWriter(System.out);
      long var2 = Long.MAX_VALUE;

      for(OutputStream var4 = new OutputStream() {
         public void write(int var1) {
         }
      }; var2 >= 0L; --var2) {
         var1.println(var2 == Long.MAX_VALUE ? "Trying latest version" : "Trying version " + var2);
         var1.flush();
         var2 = rollback(var0, var2, new PrintWriter(var4));

         try {
            String var5 = info(var0 + ".temp", new PrintWriter(var4));
            if (var5 == null) {
               FilePath.get(var0).moveTo(FilePath.get(var0 + ".back"), true);
               FilePath.get(var0 + ".temp").moveTo(FilePath.get(var0), true);
               var1.println("Success");
               break;
            }

            var1.println("    ... failed: " + var5);
         } catch (Exception var6) {
            var1.println("Fail: " + var6.getMessage());
            var1.flush();
         }
      }

      var1.flush();
   }

   public static long rollback(String var0, long var1, Writer var3) {
      long var4 = -1L;
      PrintWriter var6 = new PrintWriter(var3, true);
      if (!FilePath.get(var0).exists()) {
         var6.println("File not found: " + var0);
         return var4;
      } else {
         FileChannel var7 = null;
         FileChannel var8 = null;
         short var9 = 4096;

         try {
            var7 = FilePath.get(var0).open("r");
            FilePath.get(var0 + ".temp").delete();
            var8 = FilePath.get(var0 + ".temp").open("rw");
            long var10 = var7.size();
            ByteBuffer var12 = ByteBuffer.allocate(4096);
            Chunk var13 = null;
            long var14 = 0L;

            while(true) {
               while(var14 < var10) {
                  var12.rewind();
                  DataUtils.readFully(var7, var14, var12);
                  var12.rewind();
                  byte var16 = var12.get();
                  if (var16 == 72) {
                     var12.rewind();
                     var8.write(var12, var14);
                     var14 += (long)var9;
                  } else if (var16 != 99) {
                     var14 += (long)var9;
                  } else {
                     Chunk var17;
                     try {
                        var17 = Chunk.readChunkHeader(var12, var14);
                     } catch (MVStoreException var33) {
                        var14 += (long)var9;
                        continue;
                     }

                     if (var17.len <= 0) {
                        var14 += (long)var9;
                     } else {
                        int var18 = var17.len * 4096;
                        ByteBuffer var19 = ByteBuffer.allocate(var18);
                        DataUtils.readFully(var7, var14, var19);
                        if (var17.version > var1) {
                           var14 += (long)var18;
                        } else {
                           var19.rewind();
                           var8.write(var19, var14);
                           if (var13 == null || var17.version > var13.version) {
                              var13 = var17;
                              var4 = var17.version;
                           }

                           var14 += (long)var18;
                        }
                     }
                  }
               }

               int var36 = var13.len * 4096;
               ByteBuffer var15 = ByteBuffer.allocate(var36);
               DataUtils.readFully(var7, var13.block * 4096L, var15);
               var15.rewind();
               var8.write(var15, var10);
               break;
            }
         } catch (IOException var34) {
            var6.println("ERROR: " + var34);
            var34.printStackTrace(var6);
         } finally {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (IOException var32) {
               }
            }

            if (var8 != null) {
               try {
                  var8.close();
               } catch (IOException var31) {
               }
            }

         }

         var6.flush();
         return var4;
      }
   }

   static MVMap.Builder<Object, Object> getGenericMapBuilder() {
      return (new MVMap.Builder()).keyType(MVStoreTool.GenericDataType.INSTANCE).valueType(MVStoreTool.GenericDataType.INSTANCE);
   }

   private static class GenericDataType extends BasicDataType<byte[]> {
      static GenericDataType INSTANCE = new GenericDataType();

      public boolean isMemoryEstimationAllowed() {
         return false;
      }

      public int getMemory(byte[] var1) {
         return var1 == null ? 0 : var1.length * 8;
      }

      public byte[][] createStorage(int var1) {
         return new byte[var1][];
      }

      public void write(WriteBuffer var1, byte[] var2) {
         if (var2 != null) {
            var1.put(var2);
         }

      }

      public byte[] read(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (var2 == 0) {
            return null;
         } else {
            byte[] var3 = new byte[var2];
            var1.get(var3);
            return var3;
         }
      }
   }
}
