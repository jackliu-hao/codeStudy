package freemarker.core;

import freemarker.ext.beans._MethodUtil;
import freemarker.log.Logger;
import freemarker.template.Template;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class _ErrorDescriptionBuilder {
   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
   private final String description;
   private final Object[] descriptionParts;
   private Expression blamed;
   private boolean showBlamer;
   private Object tip;
   private Object[] tips;
   private Template template;

   public _ErrorDescriptionBuilder(String description) {
      this.description = description;
      this.descriptionParts = null;
   }

   public _ErrorDescriptionBuilder(Object... descriptionParts) {
      this.descriptionParts = descriptionParts;
      this.description = null;
   }

   public String toString() {
      return this.toString((TemplateElement)null, true);
   }

   public String toString(TemplateElement parentElement, boolean showTips) {
      if (this.blamed == null && this.tips == null && this.tip == null && this.descriptionParts == null) {
         return this.description;
      } else {
         StringBuilder sb = new StringBuilder(200);
         if (parentElement != null && this.blamed != null && this.showBlamer) {
            try {
               Blaming blaming = this.findBlaming(parentElement, this.blamed, 0);
               if (blaming != null) {
                  sb.append("For ");
                  String nss = blaming.blamer.getNodeTypeSymbol();
                  char q = nss.indexOf(34) == -1 ? 34 : 96;
                  sb.append((char)q).append(nss).append((char)q);
                  sb.append(" ").append(blaming.roleOfblamed).append(": ");
               }
            } catch (Throwable var9) {
               LOG.error("Error when searching blamer for better error message.", var9);
            }
         }

         if (this.description != null) {
            sb.append(this.description);
         } else {
            this.appendParts(sb, this.descriptionParts);
         }

         String extraTip = null;
         int i;
         int allTipsLen;
         if (this.blamed != null) {
            for(allTipsLen = sb.length() - 1; allTipsLen >= 0 && Character.isWhitespace(sb.charAt(allTipsLen)); --allTipsLen) {
               sb.deleteCharAt(allTipsLen);
            }

            char lastChar = sb.length() > 0 ? sb.charAt(sb.length() - 1) : 0;
            if (lastChar != 0) {
               sb.append('\n');
            }

            if (lastChar != ':') {
               sb.append("The blamed expression:\n");
            }

            String[] lines = this.splitToLines(this.blamed.toString());

            for(i = 0; i < lines.length; ++i) {
               sb.append(i == 0 ? "==> " : "\n    ");
               sb.append(lines[i]);
            }

            sb.append("  [");
            sb.append(this.blamed.getStartLocation());
            sb.append(']');
            if (this.containsSingleInterpolatoinLiteral(this.blamed, 0)) {
               extraTip = "It has been noticed that you are using ${...} as the sole content of a quoted string. That does nothing but forcably converts the value inside ${...} to string (as it inserts it into the enclosing string). If that's not what you meant, just remove the quotation marks, ${ and }; you don't need them. If you indeed wanted to convert to string, use myExpression?string instead.";
            }
         }

         if (showTips) {
            allTipsLen = (this.tips != null ? this.tips.length : 0) + (this.tip != null ? 1 : 0) + (extraTip != null ? 1 : 0);
            Object[] allTips;
            if (this.tips != null && allTipsLen == this.tips.length) {
               allTips = this.tips;
            } else {
               allTips = new Object[allTipsLen];
               i = 0;
               if (this.tip != null) {
                  allTips[i++] = this.tip;
               }

               if (this.tips != null) {
                  for(int i = 0; i < this.tips.length; ++i) {
                     allTips[i++] = this.tips[i];
                  }
               }

               if (extraTip != null) {
                  allTips[i++] = extraTip;
               }
            }

            if (allTips != null && allTips.length > 0) {
               sb.append("\n\n");

               for(i = 0; i < allTips.length; ++i) {
                  if (i != 0) {
                     sb.append('\n');
                  }

                  sb.append("----").append('\n');
                  sb.append("Tip: ");
                  Object tip = allTips[i];
                  if (!(tip instanceof Object[])) {
                     sb.append(allTips[i]);
                  } else {
                     this.appendParts(sb, (Object[])((Object[])tip));
                  }
               }

               sb.append('\n').append("----");
            }
         }

         return sb.toString();
      }
   }

   private boolean containsSingleInterpolatoinLiteral(Expression exp, int recursionDepth) {
      if (exp == null) {
         return false;
      } else if (recursionDepth > 20) {
         return false;
      } else if (exp instanceof StringLiteral && ((StringLiteral)exp).isSingleInterpolationLiteral()) {
         return true;
      } else {
         int paramCnt = exp.getParameterCount();

         for(int i = 0; i < paramCnt; ++i) {
            Object paramValue = exp.getParameterValue(i);
            if (paramValue instanceof Expression) {
               boolean result = this.containsSingleInterpolatoinLiteral((Expression)paramValue, recursionDepth + 1);
               if (result) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private Blaming findBlaming(TemplateObject parent, Expression blamed, int recursionDepth) {
      if (recursionDepth > 50) {
         return null;
      } else {
         int paramCnt = parent.getParameterCount();

         for(int i = 0; i < paramCnt; ++i) {
            Object paramValue = parent.getParameterValue(i);
            Blaming blaming;
            if (paramValue == blamed) {
               blaming = new Blaming();
               blaming.blamer = parent;
               blaming.roleOfblamed = parent.getParameterRole(i);
               return blaming;
            }

            if (paramValue instanceof TemplateObject) {
               blaming = this.findBlaming((TemplateObject)paramValue, blamed, recursionDepth + 1);
               if (blaming != null) {
                  return blaming;
               }
            }
         }

         return null;
      }
   }

   private void appendParts(StringBuilder sb, Object[] parts) {
      Template template = this.template != null ? this.template : (this.blamed != null ? this.blamed.getTemplate() : null);

      for(int i = 0; i < parts.length; ++i) {
         Object partObj = parts[i];
         if (partObj instanceof Object[]) {
            this.appendParts(sb, (Object[])((Object[])partObj));
         } else {
            String partStr = tryToString(partObj);
            if (partStr == null) {
               partStr = "null";
            }

            if (template == null) {
               sb.append(partStr);
            } else if (partStr.length() > 4 && partStr.charAt(0) == '<' && (partStr.charAt(1) == '#' || partStr.charAt(1) == '@' || partStr.charAt(1) == '/' && (partStr.charAt(2) == '#' || partStr.charAt(2) == '@')) && partStr.charAt(partStr.length() - 1) == '>') {
               if (template.getActualTagSyntax() == 2) {
                  sb.append('[');
                  sb.append(partStr.substring(1, partStr.length() - 1));
                  sb.append(']');
               } else {
                  sb.append(partStr);
               }
            } else {
               sb.append(partStr);
            }
         }
      }

   }

   public static String toString(Object partObj) {
      return toString(partObj, false);
   }

   public static String tryToString(Object partObj) {
      return toString(partObj, true);
   }

   private static String toString(Object partObj, boolean suppressToStringException) {
      if (partObj == null) {
         return null;
      } else {
         String partStr;
         if (partObj instanceof Class) {
            partStr = ClassUtil.getShortClassName((Class)partObj);
         } else if (!(partObj instanceof Method) && !(partObj instanceof Constructor)) {
            partStr = suppressToStringException ? StringUtil.tryToString(partObj) : partObj.toString();
         } else {
            partStr = _MethodUtil.toString((Member)partObj);
         }

         return partStr;
      }
   }

   private String[] splitToLines(String s) {
      s = StringUtil.replace(s, "\r\n", "\n");
      s = StringUtil.replace(s, "\r", "\n");
      String[] lines = StringUtil.split(s, '\n');
      return lines;
   }

   public _ErrorDescriptionBuilder template(Template template) {
      this.template = template;
      return this;
   }

   public _ErrorDescriptionBuilder blame(Expression blamedExpr) {
      this.blamed = blamedExpr;
      return this;
   }

   public _ErrorDescriptionBuilder showBlamer(boolean showBlamer) {
      this.showBlamer = showBlamer;
      return this;
   }

   public _ErrorDescriptionBuilder tip(String tip) {
      this.tip((Object)tip);
      return this;
   }

   public _ErrorDescriptionBuilder tip(Object... tip) {
      this.tip((Object)tip);
      return this;
   }

   private _ErrorDescriptionBuilder tip(Object tip) {
      if (tip == null) {
         return this;
      } else {
         if (this.tip == null) {
            this.tip = tip;
         } else if (this.tips == null) {
            this.tips = new Object[]{tip};
         } else {
            int origTipsLen = this.tips.length;
            Object[] newTips = new Object[origTipsLen + 1];

            for(int i = 0; i < origTipsLen; ++i) {
               newTips[i] = this.tips[i];
            }

            newTips[origTipsLen] = tip;
            this.tips = newTips;
         }

         return this;
      }
   }

   public _ErrorDescriptionBuilder tips(Object... tips) {
      if (tips != null && tips.length != 0) {
         if (this.tips == null) {
            this.tips = tips;
         } else {
            int origTipsLen = this.tips.length;
            int additionalTipsLen = tips.length;
            Object[] newTips = new Object[origTipsLen + additionalTipsLen];

            int i;
            for(i = 0; i < origTipsLen; ++i) {
               newTips[i] = this.tips[i];
            }

            for(i = 0; i < additionalTipsLen; ++i) {
               newTips[origTipsLen + i] = tips[i];
            }

            this.tips = newTips;
         }

         return this;
      } else {
         return this;
      }
   }

   private static class Blaming {
      TemplateObject blamer;
      ParameterRole roleOfblamed;

      private Blaming() {
      }

      // $FF: synthetic method
      Blaming(Object x0) {
         this();
      }
   }
}
