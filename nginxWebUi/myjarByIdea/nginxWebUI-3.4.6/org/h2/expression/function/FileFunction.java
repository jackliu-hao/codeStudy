package org.h2.expression.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;

public final class FileFunction extends Function1_2 {
   public static final int FILE_READ = 0;
   public static final int FILE_WRITE = 1;
   private static final String[] NAMES = new String[]{"FILE_READ", "FILE_WRITE"};
   private final int function;

   public FileFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1) {
      var1.getUser().checkAdmin();
      Object var2 = this.left.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         switch (this.function) {
            case 0:
               String var68 = ((Value)var2).getString();
               Database var69 = var1.getDatabase();

               try {
                  long var70 = FileUtils.size(var68);
                  InputStream var72 = FileUtils.newInputStream(var68);
                  Throwable var9 = null;

                  Object var71;
                  try {
                     if (this.right == null) {
                        var71 = var69.getLobStorage().createBlob(var72, var70);
                     } else {
                        Value var10 = this.right.getValue(var1);
                        InputStreamReader var11 = var10 == ValueNull.INSTANCE ? new InputStreamReader(var72) : new InputStreamReader(var72, var10.getString());
                        var71 = var69.getLobStorage().createClob(var11, var70);
                     }
                  } catch (Throwable var60) {
                     var9 = var60;
                     throw var60;
                  } finally {
                     if (var72 != null) {
                        if (var9 != null) {
                           try {
                              var72.close();
                           } catch (Throwable var57) {
                              var9.addSuppressed(var57);
                           }
                        } else {
                           var72.close();
                        }
                     }

                  }

                  var2 = var1.addTemporaryLob((ValueLob)var71);
                  break;
               } catch (IOException var63) {
                  throw DbException.convertIOException(var63, var68);
               }
            case 1:
               Value var3 = this.right.getValue(var1);
               if (var3 == ValueNull.INSTANCE) {
                  var2 = ValueNull.INSTANCE;
               } else {
                  String var4 = var3.getString();

                  try {
                     OutputStream var5 = Files.newOutputStream(Paths.get(var4));
                     Throwable var6 = null;

                     try {
                        InputStream var7 = ((Value)var2).getInputStream();
                        Throwable var8 = null;

                        try {
                           var2 = ValueBigint.get(IOUtils.copy(var7, var5));
                        } catch (Throwable var61) {
                           var8 = var61;
                           throw var61;
                        } finally {
                           if (var7 != null) {
                              if (var8 != null) {
                                 try {
                                    var7.close();
                                 } catch (Throwable var59) {
                                    var8.addSuppressed(var59);
                                 }
                              } else {
                                 var7.close();
                              }
                           }

                        }
                     } catch (Throwable var65) {
                        var6 = var65;
                        throw var65;
                     } finally {
                        if (var5 != null) {
                           if (var6 != null) {
                              try {
                                 var5.close();
                              } catch (Throwable var58) {
                                 var6.addSuppressed(var58);
                              }
                           } else {
                              var5.close();
                           }
                        }

                     }
                  } catch (IOException var67) {
                     throw DbException.convertIOException(var67, var4);
                  }
               }
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return (Value)var2;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      switch (this.function) {
         case 0:
            this.type = this.right == null ? TypeInfo.getTypeInfo(7, 2147483647L, 0, (ExtTypeInfo)null) : TypeInfo.getTypeInfo(3, 2147483647L, 0, (ExtTypeInfo)null);
            break;
         case 1:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
         case 8:
            return false;
         case 5:
            if (this.function == 1) {
               return false;
            }
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
