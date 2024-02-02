/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
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
/*     */ final class Assignment
/*     */   extends TemplateElement
/*     */ {
/*     */   private static final int OPERATOR_TYPE_EQUALS = 65536;
/*     */   private static final int OPERATOR_TYPE_PLUS_EQUALS = 65537;
/*     */   private static final int OPERATOR_TYPE_PLUS_PLUS = 65538;
/*     */   private static final int OPERATOR_TYPE_MINUS_MINUS = 65539;
/*     */   private final int scope;
/*     */   private final String variableName;
/*     */   private final int operatorType;
/*     */   private final Expression valueExp;
/*     */   private Expression namespaceExp;
/*     */   static final int NAMESPACE = 1;
/*     */   static final int LOCAL = 2;
/*     */   static final int GLOBAL = 3;
/*  50 */   private static final Number ONE = Integer.valueOf(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Assignment(String variableName, int operator, Expression valueExp, int scope) {
/*  61 */     this.scope = scope;
/*     */     
/*  63 */     this.variableName = variableName;
/*     */     
/*  65 */     if (operator == 105) {
/*  66 */       this.operatorType = 65536;
/*     */     } else {
/*  68 */       switch (operator) {
/*     */         case 113:
/*  70 */           this.operatorType = 65538;
/*     */           break;
/*     */         case 114:
/*  73 */           this.operatorType = 65539;
/*     */           break;
/*     */         case 108:
/*  76 */           this.operatorType = 65537;
/*     */           break;
/*     */         case 109:
/*  79 */           this.operatorType = 0;
/*     */           break;
/*     */         case 110:
/*  82 */           this.operatorType = 1;
/*     */           break;
/*     */         case 111:
/*  85 */           this.operatorType = 2;
/*     */           break;
/*     */         case 112:
/*  88 */           this.operatorType = 3;
/*     */           break;
/*     */         default:
/*  91 */           throw new BugException();
/*     */       } 
/*     */     
/*     */     } 
/*  95 */     this.valueExp = valueExp;
/*     */   }
/*     */   
/*     */   void setNamespaceExp(Expression namespaceExp) {
/*  99 */     if (this.scope != 1 && namespaceExp != null) throw new BugException(); 
/* 100 */     this.namespaceExp = namespaceExp;
/*     */   }
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException {
/*     */     Environment.Namespace namespace;
/*     */     TemplateModel value;
/* 106 */     if (this.namespaceExp == null) {
/* 107 */       switch (this.scope) {
/*     */         case 2:
/* 109 */           namespace = null;
/*     */           break;
/*     */         case 3:
/* 112 */           namespace = env.getGlobalNamespace();
/*     */           break;
/*     */         case 1:
/* 115 */           namespace = env.getCurrentNamespace();
/*     */           break;
/*     */         default:
/* 118 */           throw new BugException("Unexpected scope type: " + this.scope);
/*     */       } 
/*     */     } else {
/* 121 */       TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);
/*     */       try {
/* 123 */         namespace = (Environment.Namespace)uncheckedNamespace;
/* 124 */       } catch (ClassCastException e) {
/* 125 */         throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
/*     */       } 
/* 127 */       if (namespace == null) {
/* 128 */         throw InvalidReferenceException.getInstance(this.namespaceExp, env);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 133 */     if (this.operatorType == 65536) {
/* 134 */       value = this.valueExp.eval(env);
/* 135 */       if (value == null) {
/* 136 */         if (env.isClassicCompatible()) {
/* 137 */           value = TemplateScalarModel.EMPTY_STRING;
/*     */         } else {
/* 139 */           throw InvalidReferenceException.getInstance(this.valueExp, env);
/*     */         } 
/*     */       }
/*     */     } else {
/*     */       TemplateModel lhoValue;
/* 144 */       if (namespace == null) {
/* 145 */         lhoValue = env.getLocalVariable(this.variableName);
/*     */       } else {
/* 147 */         lhoValue = namespace.get(this.variableName);
/*     */       } 
/*     */       
/* 150 */       if (this.operatorType == 65537) {
/* 151 */         if (lhoValue == null) {
/* 152 */           if (env.isClassicCompatible()) {
/* 153 */             lhoValue = TemplateScalarModel.EMPTY_STRING;
/*     */           } else {
/* 155 */             throw InvalidReferenceException.getInstance(this.scope, this.variableName, 
/* 156 */                 getOperatorTypeAsString(), env);
/*     */           } 
/*     */         }
/*     */         
/* 160 */         value = this.valueExp.eval(env);
/* 161 */         if (value == null) {
/* 162 */           if (env.isClassicCompatible()) {
/* 163 */             value = TemplateScalarModel.EMPTY_STRING;
/*     */           } else {
/* 165 */             throw InvalidReferenceException.getInstance(this.valueExp, env);
/*     */           } 
/*     */         }
/* 168 */         value = AddConcatExpression._eval(env, this.namespaceExp, null, lhoValue, this.valueExp, value);
/*     */       } else {
/*     */         Number lhoNumber;
/* 171 */         if (lhoValue instanceof TemplateNumberModel)
/* 172 */         { lhoNumber = EvalUtil.modelToNumber((TemplateNumberModel)lhoValue, null); }
/* 173 */         else { if (lhoValue == null) {
/* 174 */             throw InvalidReferenceException.getInstance(this.scope, this.variableName, getOperatorTypeAsString(), env);
/*     */           }
/* 176 */           throw new NonNumericalException(this.variableName, lhoValue, null, env); }
/*     */ 
/*     */         
/* 179 */         if (this.operatorType == 65538) {
/* 180 */           value = AddConcatExpression._evalOnNumbers(env, getParentElement(), lhoNumber, ONE);
/* 181 */         } else if (this.operatorType == 65539) {
/* 182 */           value = ArithmeticExpression._eval(env, 
/* 183 */               getParentElement(), lhoNumber, 0, ONE);
/*     */         } else {
/* 185 */           Number rhoNumber = this.valueExp.evalToNumber(env);
/* 186 */           value = ArithmeticExpression._eval(env, this, lhoNumber, this.operatorType, rhoNumber);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 191 */     if (namespace == null) {
/* 192 */       env.setLocalVariable(this.variableName, value);
/*     */     } else {
/* 194 */       namespace.put(this.variableName, value);
/*     */     } 
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 201 */     StringBuilder buf = new StringBuilder();
/* 202 */     String dn = (getParentElement() instanceof AssignmentInstruction) ? null : getNodeTypeSymbol();
/* 203 */     if (dn != null) {
/* 204 */       if (canonical) buf.append("<"); 
/* 205 */       buf.append(dn);
/* 206 */       buf.append(' ');
/*     */     } 
/*     */     
/* 209 */     buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.variableName));
/*     */     
/* 211 */     if (this.valueExp != null) {
/* 212 */       buf.append(' ');
/*     */     }
/* 214 */     buf.append(getOperatorTypeAsString());
/* 215 */     if (this.valueExp != null) {
/* 216 */       buf.append(' ');
/* 217 */       buf.append(this.valueExp.getCanonicalForm());
/*     */     } 
/* 219 */     if (dn != null) {
/* 220 */       if (this.namespaceExp != null) {
/* 221 */         buf.append(" in ");
/* 222 */         buf.append(this.namespaceExp.getCanonicalForm());
/*     */       } 
/* 224 */       if (canonical) buf.append(">"); 
/*     */     } 
/* 226 */     String result = buf.toString();
/* 227 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 232 */     return getDirectiveName(this.scope);
/*     */   }
/*     */   
/*     */   static String getDirectiveName(int scope) {
/* 236 */     if (scope == 2)
/* 237 */       return "#local"; 
/* 238 */     if (scope == 3)
/* 239 */       return "#global"; 
/* 240 */     if (scope == 1) {
/* 241 */       return "#assign";
/*     */     }
/* 243 */     return "#{unknown_assignment_type}";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 249 */     return 5;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 254 */     switch (idx) { case 0:
/* 255 */         return this.variableName;
/* 256 */       case 1: return getOperatorTypeAsString();
/* 257 */       case 2: return this.valueExp;
/* 258 */       case 3: return Integer.valueOf(this.scope);
/* 259 */       case 4: return this.namespaceExp; }
/* 260 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 266 */     switch (idx) { case 0:
/* 267 */         return ParameterRole.ASSIGNMENT_TARGET;
/* 268 */       case 1: return ParameterRole.ASSIGNMENT_OPERATOR;
/* 269 */       case 2: return ParameterRole.ASSIGNMENT_SOURCE;
/* 270 */       case 3: return ParameterRole.VARIABLE_SCOPE;
/* 271 */       case 4: return ParameterRole.NAMESPACE; }
/* 272 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 278 */     return false;
/*     */   }
/*     */   
/*     */   private String getOperatorTypeAsString() {
/* 282 */     if (this.operatorType == 65536)
/* 283 */       return "="; 
/* 284 */     if (this.operatorType == 65537)
/* 285 */       return "+="; 
/* 286 */     if (this.operatorType == 65538)
/* 287 */       return "++"; 
/* 288 */     if (this.operatorType == 65539) {
/* 289 */       return "--";
/*     */     }
/* 291 */     return ArithmeticExpression.getOperatorSymbol(this.operatorType) + "=";
/*     */   }
/*     */ 
/*     */   
/*     */   static String scopeAsString(int scope) {
/* 296 */     switch (scope) { case 1:
/* 297 */         return "template namespace";
/* 298 */       case 2: return "local scope";
/* 299 */       case 3: return "global scope"; }
/* 300 */      throw new AssertionError("Unsupported scope: " + scope);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Assignment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */