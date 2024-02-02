package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.security.BlockCipher;
import org.h2.security.CipherFactory;
import org.h2.util.MathUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarbinary;

public final class CryptFunction extends FunctionN {
   public static final int ENCRYPT = 0;
   public static final int DECRYPT = 1;
   private static final String[] NAMES = new String[]{"ENCRYPT", "DECRYPT"};
   private final int function;

   public CryptFunction(Expression var1, Expression var2, Expression var3, int var4) {
      super(new Expression[]{var1, var2, var3});
      this.function = var4;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      BlockCipher var5 = CipherFactory.getBlockCipher(var2.getString());
      var5.setKey(getPaddedArrayCopy(var3.getBytesNoCopy(), var5.getKeyLength()));
      byte[] var6 = getPaddedArrayCopy(var4.getBytesNoCopy(), 16);
      switch (this.function) {
         case 0:
            var5.encrypt(var6, 0, var6.length);
            break;
         case 1:
            var5.decrypt(var6, 0, var6.length);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return ValueVarbinary.getNoCopy(var6);
   }

   private static byte[] getPaddedArrayCopy(byte[] var0, int var1) {
      return Utils.copyBytes(var0, MathUtils.roundUpInt(var0.length, var1));
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      TypeInfo var3 = this.args[2].getType();
      this.type = DataType.isBinaryStringType(var3.getValueType()) ? TypeInfo.getTypeInfo(6, var3.getPrecision(), 0, (ExtTypeInfo)null) : TypeInfo.TYPE_VARBINARY;
      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
