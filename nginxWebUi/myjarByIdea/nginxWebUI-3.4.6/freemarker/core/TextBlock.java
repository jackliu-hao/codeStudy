package freemarker.core;

import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

public final class TextBlock extends TemplateElement {
   private char[] text;
   private final boolean unparsed;

   public TextBlock(String text) {
      this(text, false);
   }

   public TextBlock(String text, boolean unparsed) {
      this(text.toCharArray(), unparsed);
   }

   TextBlock(char[] text, boolean unparsed) {
      this.text = text;
      this.unparsed = unparsed;
   }

   void replaceText(String text) {
      this.text = text.toCharArray();
   }

   public TemplateElement[] accept(Environment env) throws IOException {
      env.getOut().write(this.text);
      return null;
   }

   protected String dump(boolean canonical) {
      if (canonical) {
         String text = new String(this.text);
         return this.unparsed ? "<#noparse>" + text + "</#noparse>" : text;
      } else {
         return "text " + StringUtil.jQuote(new String(this.text));
      }
   }

   String getNodeTypeSymbol() {
      return "#text";
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return new String(this.text);
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.CONTENT;
      }
   }

   TemplateElement postParseCleanup(boolean stripWhitespace) {
      if (this.text.length == 0) {
         return this;
      } else {
         int openingCharsToStrip = 0;
         int trailingCharsToStrip = 0;
         boolean deliberateLeftTrim = this.deliberateLeftTrim();
         boolean deliberateRightTrim = this.deliberateRightTrim();
         if (stripWhitespace && this.text.length != 0) {
            TemplateElement parentElement = this.getParentElement();
            if (this.isTopLevelTextIfParentIs(parentElement) && this.previousSibling() == null) {
               return this;
            } else {
               if (!deliberateLeftTrim) {
                  trailingCharsToStrip = this.trailingCharsToStrip();
               }

               if (!deliberateRightTrim) {
                  openingCharsToStrip = this.openingCharsToStrip();
               }

               if (openingCharsToStrip == 0 && trailingCharsToStrip == 0) {
                  return this;
               } else {
                  this.text = substring(this.text, openingCharsToStrip, this.text.length - trailingCharsToStrip);
                  if (openingCharsToStrip > 0) {
                     ++this.beginLine;
                     this.beginColumn = 1;
                  }

                  if (trailingCharsToStrip > 0) {
                     this.endColumn = 0;
                  }

                  return this;
               }
            }
         } else {
            return this;
         }
      }
   }

   private boolean deliberateLeftTrim() {
      boolean result = false;

      for(TemplateElement elem = this.nextTerminalNode(); elem != null && elem.beginLine == this.endLine; elem = elem.nextTerminalNode()) {
         if (elem instanceof TrimInstruction) {
            TrimInstruction ti = (TrimInstruction)elem;
            if (!ti.left && !ti.right) {
               result = true;
            }

            if (ti.left) {
               result = true;
               int lastNewLineIndex = this.lastNewLineIndex();
               if (lastNewLineIndex >= 0 || this.beginColumn == 1) {
                  char[] firstPart = substring(this.text, 0, lastNewLineIndex + 1);
                  char[] lastLine = substring(this.text, 1 + lastNewLineIndex);
                  if (StringUtil.isTrimmableToEmpty(lastLine)) {
                     this.text = firstPart;
                     this.endColumn = 0;
                  } else {
                     int i;
                     for(i = 0; Character.isWhitespace(lastLine[i]); ++i) {
                     }

                     char[] printablePart = substring(lastLine, i);
                     this.text = concat(firstPart, printablePart);
                  }
               }
            }
         }
      }

      return result;
   }

   private boolean deliberateRightTrim() {
      boolean result = false;

      for(TemplateElement elem = this.prevTerminalNode(); elem != null && elem.endLine == this.beginLine; elem = elem.prevTerminalNode()) {
         if (elem instanceof TrimInstruction) {
            TrimInstruction ti = (TrimInstruction)elem;
            if (!ti.left && !ti.right) {
               result = true;
            }

            if (ti.right) {
               result = true;
               int firstLineIndex = this.firstNewLineIndex() + 1;
               if (firstLineIndex == 0) {
                  return false;
               }

               if (this.text.length > firstLineIndex && this.text[firstLineIndex - 1] == '\r' && this.text[firstLineIndex] == '\n') {
                  ++firstLineIndex;
               }

               char[] trailingPart = substring(this.text, firstLineIndex);
               char[] openingPart = substring(this.text, 0, firstLineIndex);
               if (StringUtil.isTrimmableToEmpty(openingPart)) {
                  this.text = trailingPart;
                  ++this.beginLine;
                  this.beginColumn = 1;
               } else {
                  int lastNonWS;
                  for(lastNonWS = openingPart.length - 1; Character.isWhitespace(this.text[lastNonWS]); --lastNonWS) {
                  }

                  char[] printablePart = substring(this.text, 0, lastNonWS + 1);
                  if (StringUtil.isTrimmableToEmpty(trailingPart)) {
                     boolean trimTrailingPart = true;

                     for(TemplateElement te = this.nextTerminalNode(); te != null && te.beginLine == this.endLine; te = te.nextTerminalNode()) {
                        if (te.heedsOpeningWhitespace()) {
                           trimTrailingPart = false;
                        }

                        if (te instanceof TrimInstruction && ((TrimInstruction)te).left) {
                           trimTrailingPart = true;
                           break;
                        }
                     }

                     if (trimTrailingPart) {
                        trailingPart = CollectionUtils.EMPTY_CHAR_ARRAY;
                     }
                  }

                  this.text = concat(printablePart, trailingPart);
               }
            }
         }
      }

      return result;
   }

   private int firstNewLineIndex() {
      char[] text = this.text;

      for(int i = 0; i < text.length; ++i) {
         char c = text[i];
         if (c == '\r' || c == '\n') {
            return i;
         }
      }

      return -1;
   }

   private int lastNewLineIndex() {
      char[] text = this.text;

      for(int i = text.length - 1; i >= 0; --i) {
         char c = text[i];
         if (c == '\r' || c == '\n') {
            return i;
         }
      }

      return -1;
   }

   private int openingCharsToStrip() {
      int newlineIndex = this.firstNewLineIndex();
      if (newlineIndex == -1 && this.beginColumn != 1) {
         return 0;
      } else {
         ++newlineIndex;
         if (this.text.length > newlineIndex && newlineIndex > 0 && this.text[newlineIndex - 1] == '\r' && this.text[newlineIndex] == '\n') {
            ++newlineIndex;
         }

         if (!StringUtil.isTrimmableToEmpty(this.text, 0, newlineIndex)) {
            return 0;
         } else {
            for(TemplateElement elem = this.prevTerminalNode(); elem != null && elem.endLine == this.beginLine; elem = elem.prevTerminalNode()) {
               if (elem.heedsOpeningWhitespace()) {
                  return 0;
               }
            }

            return newlineIndex;
         }
      }
   }

   private int trailingCharsToStrip() {
      int lastNewlineIndex = this.lastNewLineIndex();
      if (lastNewlineIndex == -1 && this.beginColumn != 1) {
         return 0;
      } else if (!StringUtil.isTrimmableToEmpty(this.text, lastNewlineIndex + 1)) {
         return 0;
      } else {
         for(TemplateElement elem = this.nextTerminalNode(); elem != null && elem.beginLine == this.endLine; elem = elem.nextTerminalNode()) {
            if (elem.heedsTrailingWhitespace()) {
               return 0;
            }
         }

         return this.text.length - (lastNewlineIndex + 1);
      }
   }

   boolean heedsTrailingWhitespace() {
      if (this.isIgnorable(true)) {
         return false;
      } else {
         for(int i = 0; i < this.text.length; ++i) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
               return false;
            }

            if (!Character.isWhitespace(c)) {
               return true;
            }
         }

         return true;
      }
   }

   boolean heedsOpeningWhitespace() {
      if (this.isIgnorable(true)) {
         return false;
      } else {
         for(int i = this.text.length - 1; i >= 0; --i) {
            char c = this.text[i];
            if (c == '\n' || c == '\r') {
               return false;
            }

            if (!Character.isWhitespace(c)) {
               return true;
            }
         }

         return true;
      }
   }

   boolean isIgnorable(boolean stripWhitespace) {
      if (this.text != null && this.text.length != 0) {
         if (stripWhitespace) {
            if (!StringUtil.isTrimmableToEmpty(this.text)) {
               return false;
            } else {
               TemplateElement parentElement = this.getParentElement();
               boolean atTopLevel = this.isTopLevelTextIfParentIs(parentElement);
               TemplateElement prevSibling = this.previousSibling();
               TemplateElement nextSibling = this.nextSibling();
               return (prevSibling == null && atTopLevel || this.nonOutputtingType(prevSibling)) && (nextSibling == null && atTopLevel || this.nonOutputtingType(nextSibling));
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean isTopLevelTextIfParentIs(TemplateElement parentElement) {
      return parentElement == null || parentElement.getParentElement() == null && parentElement instanceof MixedContent;
   }

   private boolean nonOutputtingType(TemplateElement element) {
      return element instanceof Macro || element instanceof Assignment || element instanceof AssignmentInstruction || element instanceof PropertySetting || element instanceof LibraryLoad || element instanceof Comment;
   }

   private static char[] substring(char[] c, int from, int to) {
      char[] c2 = new char[to - from];
      System.arraycopy(c, from, c2, 0, c2.length);
      return c2;
   }

   private static char[] substring(char[] c, int from) {
      return substring(c, from, c.length);
   }

   private static char[] concat(char[] c1, char[] c2) {
      char[] c = new char[c1.length + c2.length];
      System.arraycopy(c1, 0, c, 0, c1.length);
      System.arraycopy(c2, 0, c, c1.length, c2.length);
      return c;
   }

   boolean isOutputCacheable() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
