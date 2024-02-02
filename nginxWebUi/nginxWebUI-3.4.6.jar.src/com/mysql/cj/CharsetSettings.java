package com.mysql.cj;

public interface CharsetSettings {
  public static final String CHARACTER_SET_CLIENT = "character_set_client";
  
  public static final String CHARACTER_SET_CONNECTION = "character_set_connection";
  
  public static final String CHARACTER_SET_RESULTS = "character_set_results";
  
  public static final String COLLATION_CONNECTION = "collation_connection";
  
  int configurePreHandshake(boolean paramBoolean);
  
  void configurePostHandshake(boolean paramBoolean);
  
  boolean doesPlatformDbCharsetMatches();
  
  String getPasswordCharacterEncoding();
  
  String getErrorMessageEncoding();
  
  String getMetadataEncoding();
  
  int getMetadataCollationIndex();
  
  boolean getRequiresEscapingEncoder();
  
  String getJavaEncodingForCollationIndex(int paramInt);
  
  int getMaxBytesPerChar(String paramString);
  
  int getMaxBytesPerChar(Integer paramInteger, String paramString);
  
  Integer getCollationIndexForCollationName(String paramString);
  
  String getCollationNameForCollationIndex(Integer paramInteger);
  
  String getMysqlCharsetNameForCollationIndex(Integer paramInteger);
  
  int getCollationIndexForJavaEncoding(String paramString, ServerVersion paramServerVersion);
  
  int getCollationIndexForMysqlCharsetName(String paramString);
  
  String getJavaEncodingForMysqlCharset(String paramString);
  
  String getMysqlCharsetForJavaEncoding(String paramString, ServerVersion paramServerVersion);
  
  boolean isMultibyteCharset(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CharsetSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */