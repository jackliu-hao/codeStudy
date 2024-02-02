package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

final class Assignment extends TemplateElement {
   private static final int OPERATOR_TYPE_EQUALS = 65536;
   private static final int OPERATOR_TYPE_PLUS_EQUALS = 65537;
   private static final int OPERATOR_TYPE_PLUS_PLUS = 65538;
   private static final int OPERATOR_TYPE_MINUS_MINUS = 65539;
   private final int scope;
   private final String variableName;
   private final int operatorType;
   private final Expression valueExp;
   private Expression namespaceExp;
   static final int NAMESPACE = 1;
   static final int LOCAL = 2;
   static final int GLOBAL = 3;
   private static final Number ONE = 1;

   Assignment(String variableName, int operator, Expression valueExp, int scope) {
      this.scope = scope;
      this.variableName = variableName;
      if (operator == 105) {
         this.operatorType = 65536;
      } else {
         switch (operator) {
            case 108:
               this.operatorType = 65537;
               break;
            case 109:
               this.operatorType = 0;
               break;
            case 110:
               this.operatorType = 1;
               break;
            case 111:
               this.operatorType = 2;
               break;
            case 112:
               this.operatorType = 3;
               break;
            case 113:
               this.operatorType = 65538;
               break;
            case 114:
               this.operatorType = 65539;
               break;
            default:
               throw new BugException();
         }
      }

      this.valueExp = valueExp;
   }

   void setNamespaceExp(Expression namespaceExp) {
      if (this.scope != 1 && namespaceExp != null) {
         throw new BugException();
      } else {
         this.namespaceExp = namespaceExp;
      }
   }

   TemplateElement[] accept(Environment env) throws TemplateException {
      Environment.Namespace namespace;
      TemplateModel value;
      if (this.namespaceExp == null) {
         switch (this.scope) {
            case 1:
               namespace = env.getCurrentNamespace();
               break;
            case 2:
               namespace = null;
               break;
            case 3:
               namespace = env.getGlobalNamespace();
               break;
            default:
               throw new BugException("Unexpected scope type: " + this.scope);
         }
      } else {
         value = this.namespaceExp.eval(env);

         try {
            namespace = (Environment.Namespace)value;
         } catch (ClassCastException var7) {
            throw new NonNamespaceException(this.namespaceExp, value, env);
         }

         if (namespace == null) {
            throw InvalidReferenceException.getInstance(this.namespaceExp, env);
         }
      }

      if (this.operatorType == 65536) {
         value = this.valueExp.eval(env);
         if (value == null) {
            if (!env.isClassicCompatible()) {
               throw InvalidReferenceException.getInstance(this.valueExp, env);
            }

            value = TemplateScalarModel.EMPTY_STRING;
         }
      } else {
         TemplateModel lhoValue;
         if (namespace == null) {
            lhoValue = env.getLocalVariable(this.variableName);
         } else {
            lhoValue = namespace.get(this.variableName);
         }

         if (this.operatorType == 65537) {
            if (lhoValue == null) {
               if (!env.isClassicCompatible()) {
                  throw InvalidReferenceException.getInstance(this.scope, this.variableName, this.getOperatorTypeAsString(), env);
               }

               lhoValue = TemplateScalarModel.EMPTY_STRING;
            }

            value = this.valueExp.eval(env);
            if (value == null) {
               if (!env.isClassicCompatible()) {
                  throw InvalidReferenceException.getInstance(this.valueExp, env);
               }

               value = TemplateScalarModel.EMPTY_STRING;
            }

            value = AddConcatExpression._eval(env, this.namespaceExp, (Expression)null, lhoValue, this.valueExp, value);
         } else {
            if (!(lhoValue instanceof TemplateNumberModel)) {
               if (lhoValue == null) {
                  throw InvalidReferenceException.getInstance(this.scope, this.variableName, this.getOperatorTypeAsString(), env);
               }

               throw new NonNumericalException(this.variableName, lhoValue, (String[])null, env);
            }

            Number lhoNumber = EvalUtil.modelToNumber((TemplateNumberModel)lhoValue, (Expression)null);
            if (this.operatorType == 65538) {
               value = AddConcatExpression._evalOnNumbers(env, this.getParentElement(), lhoNumber, ONE);
            } else if (this.operatorType == 65539) {
               value = ArithmeticExpression._eval(env, this.getParentElement(), lhoNumber, 0, ONE);
            } else {
               Number rhoNumber = this.valueExp.evalToNumber(env);
               value = ArithmeticExpression._eval(env, this, lhoNumber, this.operatorType, rhoNumber);
            }
         }
      }

      if (namespace == null) {
         env.setLocalVariable(this.variableName, value);
      } else {
         namespace.put(this.variableName, value);
      }

      return null;
   }

   protected String dump(boolean canonical) {
      StringBuilder buf = new StringBuilder();
      String dn = this.getParentElement() instanceof AssignmentInstruction ? null : this.getNodeTypeSymbol();
      if (dn != null) {
         if (canonical) {
            buf.append("<");
         }

         buf.append(dn);
         buf.append(' ');
      }

      buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.variableName));
      if (this.valueExp != null) {
         buf.append(' ');
      }

      buf.append(this.getOperatorTypeAsString());
      if (this.valueExp != null) {
         buf.append(' ');
         buf.append(this.valueExp.getCanonicalForm());
      }

      if (dn != null) {
         if (this.namespaceExp != null) {
            buf.append(" in ");
            buf.append(this.namespaceExp.getCanonicalForm());
         }

         if (canonical) {
            buf.append(">");
         }
      }

      String result = buf.toString();
      return result;
   }

   String getNodeTypeSymbol() {
      return getDirectiveName(this.scope);
   }

   static String getDirectiveName(int scope) {
      if (scope == 2) {
         return "#local";
      } else if (scope == 3) {
         return "#global";
      } else {
         return scope == 1 ? "#assign" : "#{unknown_assignment_type}";
      }
   }

   int getParameterCount() {
      return 5;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.variableName;
         case 1:
            return this.getOperatorTypeAsString();
         case 2:
            return this.valueExp;
         case 3:
            return this.scope;
         case 4:
            return this.namespaceExp;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.ASSIGNMENT_TARGET;
         case 1:
            return ParameterRole.ASSIGNMENT_OPERATOR;
         case 2:
            return ParameterRole.ASSIGNMENT_SOURCE;
         case 3:
            return ParameterRole.VARIABLE_SCOPE;
         case 4:
            return ParameterRole.NAMESPACE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   private String getOperatorTypeAsString() {
      if (this.operatorType == 65536) {
         return "=";
      } else if (this.operatorType == 65537) {
         return "+=";
      } else if (this.operatorType == 65538) {
         return "++";
      } else {
         return this.operatorType == 65539 ? "--" : ArithmeticExpression.getOperatorSymbol(this.operatorType) + "=";
      }
   }

   static String scopeAsString(int scope) {
      switch (scope) {
         case 1:
            return "template namespace";
         case 2:
            return "local scope";
         case 3:
            return "global scope";
         default:
            throw new AssertionError("Unsupported scope: " + scope);
      }
   }
}
