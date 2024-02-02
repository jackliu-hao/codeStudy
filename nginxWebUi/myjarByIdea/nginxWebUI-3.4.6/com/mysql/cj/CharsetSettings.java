package com.mysql.cj;

public interface CharsetSettings {
   String CHARACTER_SET_CLIENT = "character_set_client";
   String CHARACTER_SET_CONNECTION = "character_set_connection";
   String CHARACTER_SET_RESULTS = "character_set_results";
   String COLLATION_CONNECTION = "collation_connection";

   int configurePreHandshake(boolean var1);

   void configurePostHandshake(boolean var1);

   boolean doesPlatformDbCharsetMatches();

   String getPasswordCharacterEncoding();

   String getErrorMessageEncoding();

   String getMetadataEncoding();

   int getMetadataCollationIndex();

   boolean getRequiresEscapingEncoder();

   String getJavaEncodingForCollationIndex(int var1);

   int getMaxBytesPerChar(String var1);

   int getMaxBytesPerChar(Integer var1, String var2);

   Integer getCollationIndexForCollationName(String var1);

   String getCollationNameForCollationIndex(Integer var1);

   String getMysqlCharsetNameForCollationIndex(Integer var1);

   int getCollationIndexForJavaEncoding(String var1, ServerVersion var2);

   int getCollationIndexForMysqlCharsetName(String var1);

   String getJavaEncodingForMysqlCharset(String var1);

   String getMysqlCharsetForJavaEncoding(String var1, ServerVersion var2);

   boolean isMultibyteCharset(String var1);
}
