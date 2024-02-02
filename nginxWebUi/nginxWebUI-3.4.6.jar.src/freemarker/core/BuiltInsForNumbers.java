/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleDate;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.NumberUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
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
/*     */ class BuiltInsForNumbers
/*     */ {
/*     */   private static abstract class abcBI
/*     */     extends BuiltInForNumber
/*     */   {
/*     */     private abcBI() {}
/*     */     
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
/*     */       int n;
/*     */       try {
/*  49 */         n = NumberUtil.toIntExact(num);
/*  50 */       } catch (ArithmeticException e) {
/*  51 */         throw new _TemplateModelException(this.target, new Object[] { "The left side operand value isn't compatible with ?", this.key, ": ", e
/*  52 */               .getMessage() });
/*     */       } 
/*     */       
/*  55 */       if (n <= 0) {
/*  56 */         throw new _TemplateModelException(this.target, new Object[] { "The left side operand of to ?", this.key, " must be at least 1, but was ", 
/*  57 */               Integer.valueOf(n), "." });
/*     */       }
/*  59 */       return (TemplateModel)new SimpleScalar(toABC(n));
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract String toABC(int param1Int);
/*     */   }
/*     */   
/*     */   static class lower_abcBI
/*     */     extends abcBI
/*     */   {
/*     */     protected String toABC(int n) {
/*  70 */       return StringUtil.toLowerABC(n);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class upper_abcBI
/*     */     extends abcBI
/*     */   {
/*     */     protected String toABC(int n) {
/*  79 */       return StringUtil.toUpperABC(n);
/*     */     }
/*     */   }
/*     */   
/*     */   static class absBI
/*     */     extends BuiltInForNumber
/*     */   {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
/*  87 */       if (num instanceof Integer) {
/*  88 */         int n = num.intValue();
/*  89 */         if (n < 0) {
/*  90 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/*  92 */         return model;
/*     */       } 
/*  94 */       if (num instanceof BigDecimal) {
/*  95 */         BigDecimal n = (BigDecimal)num;
/*  96 */         if (n.signum() < 0) {
/*  97 */           return (TemplateModel)new SimpleNumber(n.negate());
/*     */         }
/*  99 */         return model;
/*     */       } 
/* 101 */       if (num instanceof Double) {
/* 102 */         double n = num.doubleValue();
/* 103 */         if (n < 0.0D) {
/* 104 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/* 106 */         return model;
/*     */       } 
/* 108 */       if (num instanceof Float) {
/* 109 */         float n = num.floatValue();
/* 110 */         if (n < 0.0F) {
/* 111 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/* 113 */         return model;
/*     */       } 
/* 115 */       if (num instanceof Long) {
/* 116 */         long n = num.longValue();
/* 117 */         if (n < 0L) {
/* 118 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/* 120 */         return model;
/*     */       } 
/* 122 */       if (num instanceof Short) {
/* 123 */         short n = num.shortValue();
/* 124 */         if (n < 0) {
/* 125 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/* 127 */         return model;
/*     */       } 
/* 129 */       if (num instanceof Byte) {
/* 130 */         byte n = num.byteValue();
/* 131 */         if (n < 0) {
/* 132 */           return (TemplateModel)new SimpleNumber(-n);
/*     */         }
/* 134 */         return model;
/*     */       } 
/* 136 */       if (num instanceof BigInteger) {
/* 137 */         BigInteger n = (BigInteger)num;
/* 138 */         if (n.signum() < 0) {
/* 139 */           return (TemplateModel)new SimpleNumber(n.negate());
/*     */         }
/* 141 */         return model;
/*     */       } 
/*     */       
/* 144 */       throw new _TemplateModelException(new Object[] { "Unsupported number class: ", num.getClass() });
/*     */     }
/*     */   }
/*     */   
/*     */   static class byteBI
/*     */     extends BuiltInForNumber
/*     */   {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 152 */       if (num instanceof Byte) {
/* 153 */         return model;
/*     */       }
/* 155 */       return (TemplateModel)new SimpleNumber(Byte.valueOf(num.byteValue()));
/*     */     }
/*     */   }
/*     */   
/*     */   static class ceilingBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 162 */       return (TemplateModel)new SimpleNumber((new BigDecimal(num.doubleValue())).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 2));
/*     */     }
/*     */   }
/*     */   
/*     */   static class doubleBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 169 */       if (num instanceof Double) {
/* 170 */         return model;
/*     */       }
/* 172 */       return (TemplateModel)new SimpleNumber(num.doubleValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class floatBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 179 */       if (num instanceof Float) {
/* 180 */         return model;
/*     */       }
/* 182 */       return (TemplateModel)new SimpleNumber(num.floatValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class floorBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 189 */       return (TemplateModel)new SimpleNumber((new BigDecimal(num.doubleValue())).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
/*     */     }
/*     */   }
/*     */   
/*     */   static class intBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 196 */       if (num instanceof Integer) {
/* 197 */         return model;
/*     */       }
/* 199 */       return (TemplateModel)new SimpleNumber(num.intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_infiniteBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
/* 206 */       return NumberUtil.isInfinite(num) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_nanBI
/*     */     extends BuiltInForNumber
/*     */   {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
/* 214 */       return NumberUtil.isNaN(num) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class longBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 223 */       TemplateModel model = this.target.eval(env);
/* 224 */       if (!(model instanceof freemarker.template.TemplateNumberModel) && model instanceof TemplateDateModel) {
/*     */         
/* 226 */         Date date = EvalUtil.modelToDate((TemplateDateModel)model, this.target);
/* 227 */         return (TemplateModel)new SimpleNumber(date.getTime());
/*     */       } 
/* 229 */       Number num = this.target.modelToNumber(model, env);
/* 230 */       if (num instanceof Long) {
/* 231 */         return model;
/*     */       }
/* 233 */       return (TemplateModel)new SimpleNumber(num.longValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class number_to_dateBI
/*     */     extends BuiltInForNumber
/*     */   {
/*     */     private final int dateType;
/*     */     
/*     */     number_to_dateBI(int dateType) {
/* 243 */       this.dateType = dateType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
/* 249 */       return (TemplateModel)new SimpleDate(new Date(BuiltInsForNumbers.safeToLong(num)), this.dateType);
/*     */     }
/*     */   }
/*     */   
/*     */   static class roundBI extends BuiltInForNumber {
/* 254 */     private static final BigDecimal half = new BigDecimal("0.5");
/*     */     
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 257 */       return (TemplateModel)new SimpleNumber((new BigDecimal(num.doubleValue())).add(half).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
/*     */     }
/*     */   }
/*     */   
/*     */   static class shortBI
/*     */     extends BuiltInForNumber {
/*     */     TemplateModel calculateResult(Number num, TemplateModel model) {
/* 264 */       if (num instanceof Short) {
/* 265 */         return model;
/*     */       }
/* 267 */       return (TemplateModel)new SimpleNumber(Short.valueOf(num.shortValue()));
/*     */     }
/*     */   }
/*     */   
/*     */   private static final long safeToLong(Number num) throws TemplateModelException {
/* 272 */     if (num instanceof Double) {
/* 273 */       double d = Math.round(num.doubleValue());
/* 274 */       if (d > 9.223372036854776E18D || d < -9.223372036854776E18D) {
/* 275 */         throw new _TemplateModelException(new Object[] { "Number doesn't fit into a 64 bit signed integer (long): ", 
/* 276 */               Double.valueOf(d) });
/*     */       }
/* 278 */       return (long)d;
/*     */     } 
/* 280 */     if (num instanceof Float) {
/* 281 */       float f = Math.round(num.floatValue());
/* 282 */       if (f > 9.223372E18F || f < -9.223372E18F) {
/* 283 */         throw new _TemplateModelException(new Object[] { "Number doesn't fit into a 64 bit signed integer (long): ", 
/* 284 */               Float.valueOf(f) });
/*     */       }
/* 286 */       return (long)f;
/*     */     } 
/* 288 */     if (num instanceof BigDecimal) {
/* 289 */       BigDecimal bd = ((BigDecimal)num).setScale(0, 4);
/* 290 */       if (bd.compareTo(BIG_DECIMAL_LONG_MAX) > 0 || bd.compareTo(BIG_DECIMAL_LONG_MIN) < 0) {
/* 291 */         throw new _TemplateModelException(new Object[] { "Number doesn't fit into a 64 bit signed integer (long): ", bd });
/*     */       }
/* 293 */       return bd.longValue();
/*     */     } 
/* 295 */     if (num instanceof BigInteger) {
/* 296 */       BigInteger bi = (BigInteger)num;
/* 297 */       if (bi.compareTo(BIG_INTEGER_LONG_MAX) > 0 || bi.compareTo(BIG_INTEGER_LONG_MIN) < 0) {
/* 298 */         throw new _TemplateModelException(new Object[] { "Number doesn't fit into a 64 bit signed integer (long): ", bi });
/*     */       }
/* 300 */       return bi.longValue();
/*     */     } 
/* 302 */     if (num instanceof Long || num instanceof Integer || num instanceof Byte || num instanceof Short) {
/* 303 */       return num.longValue();
/*     */     }
/*     */     
/* 306 */     throw new _TemplateModelException(new Object[] { "Unsupported number type: ", num.getClass() });
/*     */   }
/*     */ 
/*     */   
/* 310 */   private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal("1");
/* 311 */   private static final BigDecimal BIG_DECIMAL_LONG_MIN = BigDecimal.valueOf(Long.MIN_VALUE);
/* 312 */   private static final BigDecimal BIG_DECIMAL_LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);
/* 313 */   private static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
/*     */   
/* 315 */   private static final BigInteger BIG_INTEGER_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForNumbers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */