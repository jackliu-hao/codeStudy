package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.StringUtils;

public class XmlWriterUtil {
   public static final String LS = System.getProperty("line.separator");
   public static final int DEFAULT_INDENTATION_SIZE = 2;
   public static final int DEFAULT_COLUMN_LINE = 80;

   public static void writeLineBreak(XMLWriter writer) {
      writeLineBreak(writer, 1);
   }

   public static void writeLineBreak(XMLWriter writer, int repeat) {
      for(int i = 0; i < repeat; ++i) {
         writer.writeMarkup(LS);
      }

   }

   public static void writeLineBreak(XMLWriter writer, int repeat, int indent) {
      writeLineBreak(writer, repeat, indent, 2);
   }

   public static void writeLineBreak(XMLWriter writer, int repeat, int indent, int indentSize) {
      writeLineBreak(writer, repeat);
      if (indent < 0) {
         indent = 0;
      }

      if (indentSize < 0) {
         indentSize = 0;
      }

      writer.writeText(StringUtils.repeat(" ", indent * indentSize));
   }

   public static void writeCommentLineBreak(XMLWriter writer) {
      writeCommentLineBreak(writer, 80);
   }

   public static void writeCommentLineBreak(XMLWriter writer, int columnSize) {
      if (columnSize < 10) {
         columnSize = 80;
      }

      writer.writeMarkup("<!-- " + StringUtils.repeat("=", columnSize - 10) + " -->" + LS);
   }

   public static void writeComment(XMLWriter writer, String comment) {
      writeComment(writer, comment, 0, 2);
   }

   public static void writeComment(XMLWriter writer, String comment, int indent) {
      writeComment(writer, comment, indent, 2);
   }

   public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize) {
      writeComment(writer, comment, indent, indentSize, 80);
   }

   public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
      if (comment == null) {
         comment = "null";
      }

      if (indent < 0) {
         indent = 0;
      }

      if (indentSize < 0) {
         indentSize = 0;
      }

      if (columnSize < 0) {
         columnSize = 80;
      }

      String indentation = StringUtils.repeat(" ", indent * indentSize);
      int magicNumber = indentation.length() + columnSize - "-->".length() - 1;
      String[] sentences = StringUtils.split(comment, LS);
      StringBuffer line = new StringBuffer(indentation + "<!-- ");

      for(int i = 0; i < sentences.length; ++i) {
         String sentence = sentences[i];
         String[] words = StringUtils.split(sentence, " ");

         for(int j = 0; j < words.length; ++j) {
            StringBuffer sentenceTmp = new StringBuffer(line.toString());
            sentenceTmp.append(words[j]).append(' ');
            if (sentenceTmp.length() > magicNumber) {
               if (line.length() != indentation.length() + "<!-- ".length()) {
                  if (magicNumber - line.length() > 0) {
                     line.append(StringUtils.repeat(" ", magicNumber - line.length()));
                  }

                  line.append("-->").append(LS);
                  writer.writeMarkup(line.toString());
               }

               line = new StringBuffer(indentation + "<!-- ");
               line.append(words[j]).append(' ');
            } else {
               line.append(words[j]).append(' ');
            }
         }

         if (magicNumber - line.length() > 0) {
            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
         }
      }

      if (line.length() <= magicNumber) {
         line.append(StringUtils.repeat(" ", magicNumber - line.length()));
      }

      line.append("-->").append(LS);
      writer.writeMarkup(line.toString());
   }

   public static void writeCommentText(XMLWriter writer, String comment) {
      writeCommentText(writer, comment, 0, 2);
   }

   public static void writeCommentText(XMLWriter writer, String comment, int indent) {
      writeCommentText(writer, comment, indent, 2);
   }

   public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize) {
      writeCommentText(writer, comment, indent, indentSize, 80);
   }

   public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
      if (indent < 0) {
         indent = 0;
      }

      if (indentSize < 0) {
         indentSize = 0;
      }

      if (columnSize < 0) {
         columnSize = 80;
      }

      writeLineBreak(writer, 1);
      writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
      writeCommentLineBreak(writer, columnSize);
      writeComment(writer, comment, indent, indentSize, columnSize);
      writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
      writeCommentLineBreak(writer, columnSize);
      writeLineBreak(writer, 1, indent, indentSize);
   }
}
