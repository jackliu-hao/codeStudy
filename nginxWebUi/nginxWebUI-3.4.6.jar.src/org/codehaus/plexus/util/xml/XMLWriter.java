package org.codehaus.plexus.util.xml;

public interface XMLWriter {
  void startElement(String paramString);
  
  void addAttribute(String paramString1, String paramString2);
  
  void writeText(String paramString);
  
  void writeMarkup(String paramString);
  
  void endElement();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XMLWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */