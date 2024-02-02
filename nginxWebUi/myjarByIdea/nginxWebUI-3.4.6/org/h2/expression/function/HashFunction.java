package org.h2.expression.function;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.security.SHA3;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;

public final class HashFunction extends FunctionN {
   public static final int HASH = 0;
   public static final int ORA_HASH = 1;
   private static final String[] NAMES = new String[]{"HASH", "ORA_HASH"};
   private final int function;

   public HashFunction(Expression var1, int var2) {
      super(new Expression[]{var1});
      this.function = var2;
   }

   public HashFunction(Expression var1, Expression var2, Expression var3, int var4) {
      super(var3 == null ? new Expression[]{var1, var2} : new Expression[]{var1, var2, var3});
      this.function = var4;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      switch (this.function) {
         case 0:
            var2 = getHash(var2.getString(), var3, var4 == null ? 1 : var4.getInt());
            break;
         case 1:
            var2 = oraHash(var2, var3 == null ? 4294967295L : var3.getLong(), var4 == null ? 0L : var4.getLong());
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return var2;
   }

   private static Value getHash(String var0, Value var1, int var2) {
      if (var2 <= 0) {
         throw DbException.getInvalidValueException("iterations", var2);
      } else {
         MessageDigest var3;
         switch (StringUtils.toUpperEnglish(var0)) {
            case "MD5":
            case "SHA-1":
            case "SHA-224":
            case "SHA-256":
            case "SHA-384":
            case "SHA-512":
               var3 = hashImpl(var1, var0);
               break;
            case "SHA256":
               var3 = hashImpl(var1, "SHA-256");
               break;
            case "SHA3-224":
               var3 = hashImpl(var1, (MessageDigest)SHA3.getSha3_224());
               break;
            case "SHA3-256":
               var3 = hashImpl(var1, (MessageDigest)SHA3.getSha3_256());
               break;
            case "SHA3-384":
               var3 = hashImpl(var1, (MessageDigest)SHA3.getSha3_384());
               break;
            case "SHA3-512":
               var3 = hashImpl(var1, (MessageDigest)SHA3.getSha3_512());
               break;
            default:
               throw DbException.getInvalidValueException("algorithm", var0);
         }

         byte[] var6 = var3.digest();

         for(var5 = 1; var5 < var2; ++var5) {
            var6 = var3.digest(var6);
         }

         return ValueVarbinary.getNoCopy(var6);
      }
   }

   private static Value oraHash(Value var0, long var1, long var3) {
      if ((var1 & -4294967296L) != 0L) {
         throw DbException.getInvalidValueException("bucket", var1);
      } else if ((var3 & -4294967296L) != 0L) {
         throw DbException.getInvalidValueException("seed", var3);
      } else {
         MessageDigest var5 = hashImpl(var0, "SHA-1");
         if (var5 == null) {
            return ValueNull.INSTANCE;
         } else {
            if (var3 != 0L) {
               byte[] var6 = new byte[4];
               Bits.writeInt(var6, 0, (int)var3);
               var5.update(var6);
            }

            long var8 = Bits.readLong(var5.digest(), 0);
            return ValueBigint.get((var8 & Long.MAX_VALUE) % (var1 + 1L));
         }
      }
   }

   private static MessageDigest hashImpl(Value var0, String var1) {
      MessageDigest var2;
      try {
         var2 = MessageDigest.getInstance(var1);
      } catch (Exception var4) {
         throw DbException.convert(var4);
      }

      return hashImpl(var0, var2);
   }

   private static MessageDigest hashImpl(Value var0, MessageDigest var1) {
      try {
         switch (var0.getValueType()) {
            case 1:
            case 2:
            case 4:
               var1.update(var0.getString().getBytes(StandardCharsets.UTF_8));
               break;
            case 3:
            case 7:
               byte[] var2 = new byte[4096];
               InputStream var3 = var0.getInputStream();
               Throwable var4 = null;

               try {
                  int var5;
                  while((var5 = var3.read(var2)) > 0) {
                     var1.update(var2, 0, var5);
                  }

                  return var1;
               } catch (Throwable var14) {
                  var4 = var14;
                  throw var14;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var13) {
                           var4.addSuppressed(var13);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            case 5:
            case 6:
            default:
               var1.update(var0.getBytesNoCopy());
         }

         return var1;
      } catch (Exception var16) {
         throw DbException.convert(var16);
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      switch (this.function) {
         case 0:
            this.type = TypeInfo.TYPE_VARBINARY;
            break;
         case 1:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
