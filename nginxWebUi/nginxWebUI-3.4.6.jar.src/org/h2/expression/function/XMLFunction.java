/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public final class XMLFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int XMLATTR = 0;
/*     */   public static final int XMLCDATA = 1;
/*     */   public static final int XMLCOMMENT = 2;
/*     */   public static final int XMLNODE = 3;
/*     */   public static final int XMLSTARTDOC = 4;
/*     */   public static final int XMLTEXT = 5;
/*  54 */   private static final String[] NAMES = new String[] { "XMLATTR", "XMLCDATA", "XMLCOMMENT", "XMLNODE", "XMLSTARTDOC", "XMLTEXT" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public XMLFunction(int paramInt) {
/*  61 */     super(new Expression[4]);
/*  62 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  67 */     switch (this.function) {
/*     */       case 3:
/*  69 */         return xmlNode(paramSessionLocal);
/*     */       case 4:
/*  71 */         return ValueVarchar.get(StringUtils.xmlStartDoc(), (CastDataProvider)paramSessionLocal);
/*     */     } 
/*  73 */     return super.getValue(paramSessionLocal);
/*     */   }
/*     */   
/*     */   private Value xmlNode(SessionLocal paramSessionLocal) {
/*     */     boolean bool;
/*  78 */     Value value = this.args[0].getValue(paramSessionLocal);
/*  79 */     if (value == ValueNull.INSTANCE) {
/*  80 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  82 */     int i = this.args.length;
/*  83 */     String str1 = (i >= 2) ? this.args[1].getValue(paramSessionLocal).getString() : null;
/*  84 */     String str2 = (i >= 3) ? this.args[2].getValue(paramSessionLocal).getString() : null;
/*     */     
/*  86 */     if (i >= 4) {
/*  87 */       Value value1 = this.args[3].getValue(paramSessionLocal);
/*  88 */       if (value1 == ValueNull.INSTANCE) {
/*  89 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  91 */       bool = value1.getBoolean();
/*     */     } else {
/*  93 */       bool = true;
/*     */     } 
/*  95 */     return ValueVarchar.get(StringUtils.xmlNode(value.getString(), str1, str2, bool), (CastDataProvider)paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 100 */     switch (this.function) {
/*     */       case 0:
/* 102 */         paramValue1 = ValueVarchar.get(StringUtils.xmlAttr(paramValue1.getString(), paramValue2.getString()), (CastDataProvider)paramSessionLocal);
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
/* 116 */         return paramValue1;case 1: paramValue1 = ValueVarchar.get(StringUtils.xmlCData(paramValue1.getString()), (CastDataProvider)paramSessionLocal); return paramValue1;case 2: paramValue1 = ValueVarchar.get(StringUtils.xmlComment(paramValue1.getString()), (CastDataProvider)paramSessionLocal); return paramValue1;case 5: paramValue1 = ValueVarchar.get(StringUtils.xmlText(paramValue1.getString(), (paramValue2 != null && paramValue2.getBoolean())), (CastDataProvider)paramSessionLocal); return paramValue1;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   } public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     byte b1, b2;
/* 121 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/*     */     
/* 123 */     switch (this.function) {
/*     */       case 0:
/* 125 */         b2 = b1 = 2;
/*     */         break;
/*     */       case 3:
/* 128 */         b1 = 1;
/* 129 */         b2 = 4;
/*     */         break;
/*     */       case 1:
/*     */       case 2:
/* 133 */         b2 = b1 = 1;
/*     */         break;
/*     */       case 4:
/* 136 */         b2 = b1 = 0;
/*     */         break;
/*     */       case 5:
/* 139 */         b1 = 1;
/* 140 */         b2 = 2;
/*     */         break;
/*     */       default:
/* 143 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 145 */     int i = this.args.length;
/* 146 */     if (i < b1 || i > b2) {
/* 147 */       throw DbException.get(7001, new String[] { getName(), b1 + ".." + b2 });
/*     */     }
/* 149 */     this.type = TypeInfo.TYPE_VARCHAR;
/* 150 */     if (bool) {
/* 151 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 153 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 158 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\XMLFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */