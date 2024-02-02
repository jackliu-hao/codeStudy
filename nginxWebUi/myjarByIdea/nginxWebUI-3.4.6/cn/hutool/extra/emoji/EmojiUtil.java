package cn.hutool.extra.emoji;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiParser.FitzpatrickAction;
import java.util.List;
import java.util.Set;

public class EmojiUtil {
   public static boolean isEmoji(String str) {
      return EmojiManager.isEmoji(str);
   }

   public static boolean containsEmoji(String str) {
      return EmojiManager.containsEmoji(str);
   }

   public static Set<Emoji> getByTag(String tag) {
      return EmojiManager.getForTag(tag);
   }

   public static Emoji get(String alias) {
      return EmojiManager.getForAlias(alias);
   }

   public static String toUnicode(String str) {
      return EmojiParser.parseToUnicode(str);
   }

   public static String toAlias(String str) {
      return toAlias(str, FitzpatrickAction.PARSE);
   }

   public static String toAlias(String str, EmojiParser.FitzpatrickAction fitzpatrickAction) {
      return EmojiParser.parseToAliases(str, fitzpatrickAction);
   }

   public static String toHtmlHex(String str) {
      return toHtml(str, true);
   }

   public static String toHtml(String str) {
      return toHtml(str, false);
   }

   public static String toHtml(String str, boolean isHex) {
      return isHex ? EmojiParser.parseToHtmlHexadecimal(str) : EmojiParser.parseToHtmlDecimal(str);
   }

   public static String removeAllEmojis(String str) {
      return EmojiParser.removeAllEmojis(str);
   }

   public static List<String> extractEmojis(String str) {
      return EmojiParser.extractEmojis(str);
   }
}
