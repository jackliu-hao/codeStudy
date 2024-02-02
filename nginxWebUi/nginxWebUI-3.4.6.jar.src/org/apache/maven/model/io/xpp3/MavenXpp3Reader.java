/*      */ package org.apache.maven.model.io.xpp3;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import org.apache.maven.model.Activation;
/*      */ import org.apache.maven.model.ActivationFile;
/*      */ import org.apache.maven.model.ActivationOS;
/*      */ import org.apache.maven.model.ActivationProperty;
/*      */ import org.apache.maven.model.Build;
/*      */ import org.apache.maven.model.BuildBase;
/*      */ import org.apache.maven.model.CiManagement;
/*      */ import org.apache.maven.model.ConfigurationContainer;
/*      */ import org.apache.maven.model.Contributor;
/*      */ import org.apache.maven.model.Dependency;
/*      */ import org.apache.maven.model.DependencyManagement;
/*      */ import org.apache.maven.model.DeploymentRepository;
/*      */ import org.apache.maven.model.Developer;
/*      */ import org.apache.maven.model.DistributionManagement;
/*      */ import org.apache.maven.model.Exclusion;
/*      */ import org.apache.maven.model.Extension;
/*      */ import org.apache.maven.model.FileSet;
/*      */ import org.apache.maven.model.IssueManagement;
/*      */ import org.apache.maven.model.License;
/*      */ import org.apache.maven.model.MailingList;
/*      */ import org.apache.maven.model.Model;
/*      */ import org.apache.maven.model.ModelBase;
/*      */ import org.apache.maven.model.Notifier;
/*      */ import org.apache.maven.model.Organization;
/*      */ import org.apache.maven.model.Parent;
/*      */ import org.apache.maven.model.PatternSet;
/*      */ import org.apache.maven.model.Plugin;
/*      */ import org.apache.maven.model.PluginConfiguration;
/*      */ import org.apache.maven.model.PluginContainer;
/*      */ import org.apache.maven.model.PluginExecution;
/*      */ import org.apache.maven.model.PluginManagement;
/*      */ import org.apache.maven.model.Prerequisites;
/*      */ import org.apache.maven.model.Profile;
/*      */ import org.apache.maven.model.Relocation;
/*      */ import org.apache.maven.model.ReportPlugin;
/*      */ import org.apache.maven.model.ReportSet;
/*      */ import org.apache.maven.model.Reporting;
/*      */ import org.apache.maven.model.Repository;
/*      */ import org.apache.maven.model.RepositoryBase;
/*      */ import org.apache.maven.model.RepositoryPolicy;
/*      */ import org.apache.maven.model.Resource;
/*      */ import org.apache.maven.model.Scm;
/*      */ import org.apache.maven.model.Site;
/*      */ import org.codehaus.plexus.util.ReaderFactory;
/*      */ import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
/*      */ import org.codehaus.plexus.util.xml.pull.MXParser;
/*      */ import org.codehaus.plexus.util.xml.pull.XmlPullParser;
/*      */ import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MavenXpp3Reader
/*      */ {
/*      */   private boolean addDefaultEntities = true;
/*      */   
/*      */   private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set<String> parsed) throws XmlPullParserException {
/*  111 */     if (!parser.getName().equals(tagName) && !parser.getName().equals(alias))
/*      */     {
/*  113 */       return false;
/*      */     }
/*  115 */     if (!parsed.add(tagName))
/*      */     {
/*  117 */       throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null);
/*      */     }
/*  119 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict) throws XmlPullParserException, IOException {
/*  136 */     if (strict)
/*      */     {
/*  138 */       throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkUnknownElement(XmlPullParser parser, boolean strict) throws XmlPullParserException, IOException {
/*  153 */     if (strict)
/*      */     {
/*  155 */       throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
/*      */     }
/*      */     
/*  158 */     for (int unrecognizedTagCount = 1; unrecognizedTagCount > 0; ) {
/*      */       
/*  160 */       int eventType = parser.next();
/*  161 */       if (eventType == 2) {
/*      */         
/*  163 */         unrecognizedTagCount++; continue;
/*      */       } 
/*  165 */       if (eventType == 3)
/*      */       {
/*  167 */         unrecognizedTagCount--;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAddDefaultEntities() {
/*  179 */     return this.addDefaultEntities;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  194 */     return getBooleanValue(s, attribute, parser, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean getBooleanValue(String s, String attribute, XmlPullParser parser, String defaultValue) throws XmlPullParserException {
/*  210 */     if (s != null && s.length() != 0)
/*      */     {
/*  212 */       return Boolean.valueOf(s).booleanValue();
/*      */     }
/*  214 */     if (defaultValue != null)
/*      */     {
/*  216 */       return Boolean.valueOf(defaultValue).booleanValue();
/*      */     }
/*  218 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private byte getByteValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  234 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  238 */         return Byte.valueOf(s).byteValue();
/*      */       }
/*  240 */       catch (NumberFormatException nfe) {
/*      */         
/*  242 */         if (strict)
/*      */         {
/*  244 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  248 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private char getCharacterValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  263 */     if (s != null)
/*      */     {
/*  265 */       return s.charAt(0);
/*      */     }
/*  267 */     return Character.MIN_VALUE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Date getDateValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
/*  282 */     return getDateValue(s, attribute, null, parser);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Date getDateValue(String s, String attribute, String dateFormat, XmlPullParser parser) throws XmlPullParserException {
/*  298 */     if (s != null) {
/*      */       
/*  300 */       String effectiveDateFormat = dateFormat;
/*  301 */       if (dateFormat == null)
/*      */       {
/*  303 */         effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
/*      */       }
/*  305 */       if ("long".equals(effectiveDateFormat)) {
/*      */         
/*      */         try {
/*      */           
/*  309 */           return new Date(Long.parseLong(s));
/*      */         }
/*  311 */         catch (NumberFormatException e) {
/*      */           
/*  313 */           throw new XmlPullParserException(e.getMessage(), parser, e);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  320 */         DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
/*  321 */         return dateParser.parse(s);
/*      */       }
/*  323 */       catch (ParseException e) {
/*      */         
/*  325 */         throw new XmlPullParserException(e.getMessage(), parser, e);
/*      */       } 
/*      */     } 
/*      */     
/*  329 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double getDoubleValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  345 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  349 */         return Double.valueOf(s).doubleValue();
/*      */       }
/*  351 */       catch (NumberFormatException nfe) {
/*      */         
/*  353 */         if (strict)
/*      */         {
/*  355 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  359 */     return 0.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private float getFloatValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  375 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  379 */         return Float.valueOf(s).floatValue();
/*      */       }
/*  381 */       catch (NumberFormatException nfe) {
/*      */         
/*  383 */         if (strict)
/*      */         {
/*  385 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  389 */     return 0.0F;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getIntegerValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  405 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  409 */         return Integer.valueOf(s).intValue();
/*      */       }
/*  411 */       catch (NumberFormatException nfe) {
/*      */         
/*  413 */         if (strict)
/*      */         {
/*  415 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  419 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long getLongValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  435 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  439 */         return Long.valueOf(s).longValue();
/*      */       }
/*  441 */       catch (NumberFormatException nfe) {
/*      */         
/*  443 */         if (strict)
/*      */         {
/*  445 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  449 */     return 0L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getRequiredAttributeValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  465 */     if (s == null)
/*      */     {
/*  467 */       if (strict)
/*      */       {
/*  469 */         throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, null);
/*      */       }
/*      */     }
/*  472 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private short getShortValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
/*  488 */     if (s != null) {
/*      */       
/*      */       try {
/*      */         
/*  492 */         return Short.valueOf(s).shortValue();
/*      */       }
/*  494 */       catch (NumberFormatException nfe) {
/*      */         
/*  496 */         if (strict)
/*      */         {
/*  498 */           throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, nfe);
/*      */         }
/*      */       } 
/*      */     }
/*  502 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getTrimmedValue(String s) {
/*  513 */     if (s != null)
/*      */     {
/*  515 */       s = s.trim();
/*      */     }
/*  517 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initParser(XmlPullParser parser) throws XmlPullParserException {
/*  529 */     if (this.addDefaultEntities) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  535 */       parser.defineEntityReplacementText("nbsp", " ");
/*  536 */       parser.defineEntityReplacementText("iexcl", "¡");
/*  537 */       parser.defineEntityReplacementText("cent", "¢");
/*  538 */       parser.defineEntityReplacementText("pound", "£");
/*  539 */       parser.defineEntityReplacementText("curren", "¤");
/*  540 */       parser.defineEntityReplacementText("yen", "¥");
/*  541 */       parser.defineEntityReplacementText("brvbar", "¦");
/*  542 */       parser.defineEntityReplacementText("sect", "§");
/*  543 */       parser.defineEntityReplacementText("uml", "¨");
/*  544 */       parser.defineEntityReplacementText("copy", "©");
/*  545 */       parser.defineEntityReplacementText("ordf", "ª");
/*  546 */       parser.defineEntityReplacementText("laquo", "«");
/*  547 */       parser.defineEntityReplacementText("not", "¬");
/*  548 */       parser.defineEntityReplacementText("shy", "­");
/*  549 */       parser.defineEntityReplacementText("reg", "®");
/*  550 */       parser.defineEntityReplacementText("macr", "¯");
/*  551 */       parser.defineEntityReplacementText("deg", "°");
/*  552 */       parser.defineEntityReplacementText("plusmn", "±");
/*  553 */       parser.defineEntityReplacementText("sup2", "²");
/*  554 */       parser.defineEntityReplacementText("sup3", "³");
/*  555 */       parser.defineEntityReplacementText("acute", "´");
/*  556 */       parser.defineEntityReplacementText("micro", "µ");
/*  557 */       parser.defineEntityReplacementText("para", "¶");
/*  558 */       parser.defineEntityReplacementText("middot", "·");
/*  559 */       parser.defineEntityReplacementText("cedil", "¸");
/*  560 */       parser.defineEntityReplacementText("sup1", "¹");
/*  561 */       parser.defineEntityReplacementText("ordm", "º");
/*  562 */       parser.defineEntityReplacementText("raquo", "»");
/*  563 */       parser.defineEntityReplacementText("frac14", "¼");
/*  564 */       parser.defineEntityReplacementText("frac12", "½");
/*  565 */       parser.defineEntityReplacementText("frac34", "¾");
/*  566 */       parser.defineEntityReplacementText("iquest", "¿");
/*  567 */       parser.defineEntityReplacementText("Agrave", "À");
/*  568 */       parser.defineEntityReplacementText("Aacute", "Á");
/*  569 */       parser.defineEntityReplacementText("Acirc", "Â");
/*  570 */       parser.defineEntityReplacementText("Atilde", "Ã");
/*  571 */       parser.defineEntityReplacementText("Auml", "Ä");
/*  572 */       parser.defineEntityReplacementText("Aring", "Å");
/*  573 */       parser.defineEntityReplacementText("AElig", "Æ");
/*  574 */       parser.defineEntityReplacementText("Ccedil", "Ç");
/*  575 */       parser.defineEntityReplacementText("Egrave", "È");
/*  576 */       parser.defineEntityReplacementText("Eacute", "É");
/*  577 */       parser.defineEntityReplacementText("Ecirc", "Ê");
/*  578 */       parser.defineEntityReplacementText("Euml", "Ë");
/*  579 */       parser.defineEntityReplacementText("Igrave", "Ì");
/*  580 */       parser.defineEntityReplacementText("Iacute", "Í");
/*  581 */       parser.defineEntityReplacementText("Icirc", "Î");
/*  582 */       parser.defineEntityReplacementText("Iuml", "Ï");
/*  583 */       parser.defineEntityReplacementText("ETH", "Ð");
/*  584 */       parser.defineEntityReplacementText("Ntilde", "Ñ");
/*  585 */       parser.defineEntityReplacementText("Ograve", "Ò");
/*  586 */       parser.defineEntityReplacementText("Oacute", "Ó");
/*  587 */       parser.defineEntityReplacementText("Ocirc", "Ô");
/*  588 */       parser.defineEntityReplacementText("Otilde", "Õ");
/*  589 */       parser.defineEntityReplacementText("Ouml", "Ö");
/*  590 */       parser.defineEntityReplacementText("times", "×");
/*  591 */       parser.defineEntityReplacementText("Oslash", "Ø");
/*  592 */       parser.defineEntityReplacementText("Ugrave", "Ù");
/*  593 */       parser.defineEntityReplacementText("Uacute", "Ú");
/*  594 */       parser.defineEntityReplacementText("Ucirc", "Û");
/*  595 */       parser.defineEntityReplacementText("Uuml", "Ü");
/*  596 */       parser.defineEntityReplacementText("Yacute", "Ý");
/*  597 */       parser.defineEntityReplacementText("THORN", "Þ");
/*  598 */       parser.defineEntityReplacementText("szlig", "ß");
/*  599 */       parser.defineEntityReplacementText("agrave", "à");
/*  600 */       parser.defineEntityReplacementText("aacute", "á");
/*  601 */       parser.defineEntityReplacementText("acirc", "â");
/*  602 */       parser.defineEntityReplacementText("atilde", "ã");
/*  603 */       parser.defineEntityReplacementText("auml", "ä");
/*  604 */       parser.defineEntityReplacementText("aring", "å");
/*  605 */       parser.defineEntityReplacementText("aelig", "æ");
/*  606 */       parser.defineEntityReplacementText("ccedil", "ç");
/*  607 */       parser.defineEntityReplacementText("egrave", "è");
/*  608 */       parser.defineEntityReplacementText("eacute", "é");
/*  609 */       parser.defineEntityReplacementText("ecirc", "ê");
/*  610 */       parser.defineEntityReplacementText("euml", "ë");
/*  611 */       parser.defineEntityReplacementText("igrave", "ì");
/*  612 */       parser.defineEntityReplacementText("iacute", "í");
/*  613 */       parser.defineEntityReplacementText("icirc", "î");
/*  614 */       parser.defineEntityReplacementText("iuml", "ï");
/*  615 */       parser.defineEntityReplacementText("eth", "ð");
/*  616 */       parser.defineEntityReplacementText("ntilde", "ñ");
/*  617 */       parser.defineEntityReplacementText("ograve", "ò");
/*  618 */       parser.defineEntityReplacementText("oacute", "ó");
/*  619 */       parser.defineEntityReplacementText("ocirc", "ô");
/*  620 */       parser.defineEntityReplacementText("otilde", "õ");
/*  621 */       parser.defineEntityReplacementText("ouml", "ö");
/*  622 */       parser.defineEntityReplacementText("divide", "÷");
/*  623 */       parser.defineEntityReplacementText("oslash", "ø");
/*  624 */       parser.defineEntityReplacementText("ugrave", "ù");
/*  625 */       parser.defineEntityReplacementText("uacute", "ú");
/*  626 */       parser.defineEntityReplacementText("ucirc", "û");
/*  627 */       parser.defineEntityReplacementText("uuml", "ü");
/*  628 */       parser.defineEntityReplacementText("yacute", "ý");
/*  629 */       parser.defineEntityReplacementText("thorn", "þ");
/*  630 */       parser.defineEntityReplacementText("yuml", "ÿ");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  636 */       parser.defineEntityReplacementText("OElig", "Œ");
/*  637 */       parser.defineEntityReplacementText("oelig", "œ");
/*  638 */       parser.defineEntityReplacementText("Scaron", "Š");
/*  639 */       parser.defineEntityReplacementText("scaron", "š");
/*  640 */       parser.defineEntityReplacementText("Yuml", "Ÿ");
/*  641 */       parser.defineEntityReplacementText("circ", "ˆ");
/*  642 */       parser.defineEntityReplacementText("tilde", "˜");
/*  643 */       parser.defineEntityReplacementText("ensp", " ");
/*  644 */       parser.defineEntityReplacementText("emsp", " ");
/*  645 */       parser.defineEntityReplacementText("thinsp", " ");
/*  646 */       parser.defineEntityReplacementText("zwnj", "‌");
/*  647 */       parser.defineEntityReplacementText("zwj", "‍");
/*  648 */       parser.defineEntityReplacementText("lrm", "‎");
/*  649 */       parser.defineEntityReplacementText("rlm", "‏");
/*  650 */       parser.defineEntityReplacementText("ndash", "–");
/*  651 */       parser.defineEntityReplacementText("mdash", "—");
/*  652 */       parser.defineEntityReplacementText("lsquo", "‘");
/*  653 */       parser.defineEntityReplacementText("rsquo", "’");
/*  654 */       parser.defineEntityReplacementText("sbquo", "‚");
/*  655 */       parser.defineEntityReplacementText("ldquo", "“");
/*  656 */       parser.defineEntityReplacementText("rdquo", "”");
/*  657 */       parser.defineEntityReplacementText("bdquo", "„");
/*  658 */       parser.defineEntityReplacementText("dagger", "†");
/*  659 */       parser.defineEntityReplacementText("Dagger", "‡");
/*  660 */       parser.defineEntityReplacementText("permil", "‰");
/*  661 */       parser.defineEntityReplacementText("lsaquo", "‹");
/*  662 */       parser.defineEntityReplacementText("rsaquo", "›");
/*  663 */       parser.defineEntityReplacementText("euro", "€");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  669 */       parser.defineEntityReplacementText("fnof", "ƒ");
/*  670 */       parser.defineEntityReplacementText("Alpha", "Α");
/*  671 */       parser.defineEntityReplacementText("Beta", "Β");
/*  672 */       parser.defineEntityReplacementText("Gamma", "Γ");
/*  673 */       parser.defineEntityReplacementText("Delta", "Δ");
/*  674 */       parser.defineEntityReplacementText("Epsilon", "Ε");
/*  675 */       parser.defineEntityReplacementText("Zeta", "Ζ");
/*  676 */       parser.defineEntityReplacementText("Eta", "Η");
/*  677 */       parser.defineEntityReplacementText("Theta", "Θ");
/*  678 */       parser.defineEntityReplacementText("Iota", "Ι");
/*  679 */       parser.defineEntityReplacementText("Kappa", "Κ");
/*  680 */       parser.defineEntityReplacementText("Lambda", "Λ");
/*  681 */       parser.defineEntityReplacementText("Mu", "Μ");
/*  682 */       parser.defineEntityReplacementText("Nu", "Ν");
/*  683 */       parser.defineEntityReplacementText("Xi", "Ξ");
/*  684 */       parser.defineEntityReplacementText("Omicron", "Ο");
/*  685 */       parser.defineEntityReplacementText("Pi", "Π");
/*  686 */       parser.defineEntityReplacementText("Rho", "Ρ");
/*  687 */       parser.defineEntityReplacementText("Sigma", "Σ");
/*  688 */       parser.defineEntityReplacementText("Tau", "Τ");
/*  689 */       parser.defineEntityReplacementText("Upsilon", "Υ");
/*  690 */       parser.defineEntityReplacementText("Phi", "Φ");
/*  691 */       parser.defineEntityReplacementText("Chi", "Χ");
/*  692 */       parser.defineEntityReplacementText("Psi", "Ψ");
/*  693 */       parser.defineEntityReplacementText("Omega", "Ω");
/*  694 */       parser.defineEntityReplacementText("alpha", "α");
/*  695 */       parser.defineEntityReplacementText("beta", "β");
/*  696 */       parser.defineEntityReplacementText("gamma", "γ");
/*  697 */       parser.defineEntityReplacementText("delta", "δ");
/*  698 */       parser.defineEntityReplacementText("epsilon", "ε");
/*  699 */       parser.defineEntityReplacementText("zeta", "ζ");
/*  700 */       parser.defineEntityReplacementText("eta", "η");
/*  701 */       parser.defineEntityReplacementText("theta", "θ");
/*  702 */       parser.defineEntityReplacementText("iota", "ι");
/*  703 */       parser.defineEntityReplacementText("kappa", "κ");
/*  704 */       parser.defineEntityReplacementText("lambda", "λ");
/*  705 */       parser.defineEntityReplacementText("mu", "μ");
/*  706 */       parser.defineEntityReplacementText("nu", "ν");
/*  707 */       parser.defineEntityReplacementText("xi", "ξ");
/*  708 */       parser.defineEntityReplacementText("omicron", "ο");
/*  709 */       parser.defineEntityReplacementText("pi", "π");
/*  710 */       parser.defineEntityReplacementText("rho", "ρ");
/*  711 */       parser.defineEntityReplacementText("sigmaf", "ς");
/*  712 */       parser.defineEntityReplacementText("sigma", "σ");
/*  713 */       parser.defineEntityReplacementText("tau", "τ");
/*  714 */       parser.defineEntityReplacementText("upsilon", "υ");
/*  715 */       parser.defineEntityReplacementText("phi", "φ");
/*  716 */       parser.defineEntityReplacementText("chi", "χ");
/*  717 */       parser.defineEntityReplacementText("psi", "ψ");
/*  718 */       parser.defineEntityReplacementText("omega", "ω");
/*  719 */       parser.defineEntityReplacementText("thetasym", "ϑ");
/*  720 */       parser.defineEntityReplacementText("upsih", "ϒ");
/*  721 */       parser.defineEntityReplacementText("piv", "ϖ");
/*  722 */       parser.defineEntityReplacementText("bull", "•");
/*  723 */       parser.defineEntityReplacementText("hellip", "…");
/*  724 */       parser.defineEntityReplacementText("prime", "′");
/*  725 */       parser.defineEntityReplacementText("Prime", "″");
/*  726 */       parser.defineEntityReplacementText("oline", "‾");
/*  727 */       parser.defineEntityReplacementText("frasl", "⁄");
/*  728 */       parser.defineEntityReplacementText("weierp", "℘");
/*  729 */       parser.defineEntityReplacementText("image", "ℑ");
/*  730 */       parser.defineEntityReplacementText("real", "ℜ");
/*  731 */       parser.defineEntityReplacementText("trade", "™");
/*  732 */       parser.defineEntityReplacementText("alefsym", "ℵ");
/*  733 */       parser.defineEntityReplacementText("larr", "←");
/*  734 */       parser.defineEntityReplacementText("uarr", "↑");
/*  735 */       parser.defineEntityReplacementText("rarr", "→");
/*  736 */       parser.defineEntityReplacementText("darr", "↓");
/*  737 */       parser.defineEntityReplacementText("harr", "↔");
/*  738 */       parser.defineEntityReplacementText("crarr", "↵");
/*  739 */       parser.defineEntityReplacementText("lArr", "⇐");
/*  740 */       parser.defineEntityReplacementText("uArr", "⇑");
/*  741 */       parser.defineEntityReplacementText("rArr", "⇒");
/*  742 */       parser.defineEntityReplacementText("dArr", "⇓");
/*  743 */       parser.defineEntityReplacementText("hArr", "⇔");
/*  744 */       parser.defineEntityReplacementText("forall", "∀");
/*  745 */       parser.defineEntityReplacementText("part", "∂");
/*  746 */       parser.defineEntityReplacementText("exist", "∃");
/*  747 */       parser.defineEntityReplacementText("empty", "∅");
/*  748 */       parser.defineEntityReplacementText("nabla", "∇");
/*  749 */       parser.defineEntityReplacementText("isin", "∈");
/*  750 */       parser.defineEntityReplacementText("notin", "∉");
/*  751 */       parser.defineEntityReplacementText("ni", "∋");
/*  752 */       parser.defineEntityReplacementText("prod", "∏");
/*  753 */       parser.defineEntityReplacementText("sum", "∑");
/*  754 */       parser.defineEntityReplacementText("minus", "−");
/*  755 */       parser.defineEntityReplacementText("lowast", "∗");
/*  756 */       parser.defineEntityReplacementText("radic", "√");
/*  757 */       parser.defineEntityReplacementText("prop", "∝");
/*  758 */       parser.defineEntityReplacementText("infin", "∞");
/*  759 */       parser.defineEntityReplacementText("ang", "∠");
/*  760 */       parser.defineEntityReplacementText("and", "∧");
/*  761 */       parser.defineEntityReplacementText("or", "∨");
/*  762 */       parser.defineEntityReplacementText("cap", "∩");
/*  763 */       parser.defineEntityReplacementText("cup", "∪");
/*  764 */       parser.defineEntityReplacementText("int", "∫");
/*  765 */       parser.defineEntityReplacementText("there4", "∴");
/*  766 */       parser.defineEntityReplacementText("sim", "∼");
/*  767 */       parser.defineEntityReplacementText("cong", "≅");
/*  768 */       parser.defineEntityReplacementText("asymp", "≈");
/*  769 */       parser.defineEntityReplacementText("ne", "≠");
/*  770 */       parser.defineEntityReplacementText("equiv", "≡");
/*  771 */       parser.defineEntityReplacementText("le", "≤");
/*  772 */       parser.defineEntityReplacementText("ge", "≥");
/*  773 */       parser.defineEntityReplacementText("sub", "⊂");
/*  774 */       parser.defineEntityReplacementText("sup", "⊃");
/*  775 */       parser.defineEntityReplacementText("nsub", "⊄");
/*  776 */       parser.defineEntityReplacementText("sube", "⊆");
/*  777 */       parser.defineEntityReplacementText("supe", "⊇");
/*  778 */       parser.defineEntityReplacementText("oplus", "⊕");
/*  779 */       parser.defineEntityReplacementText("otimes", "⊗");
/*  780 */       parser.defineEntityReplacementText("perp", "⊥");
/*  781 */       parser.defineEntityReplacementText("sdot", "⋅");
/*  782 */       parser.defineEntityReplacementText("lceil", "⌈");
/*  783 */       parser.defineEntityReplacementText("rceil", "⌉");
/*  784 */       parser.defineEntityReplacementText("lfloor", "⌊");
/*  785 */       parser.defineEntityReplacementText("rfloor", "⌋");
/*  786 */       parser.defineEntityReplacementText("lang", "〈");
/*  787 */       parser.defineEntityReplacementText("rang", "〉");
/*  788 */       parser.defineEntityReplacementText("loz", "◊");
/*  789 */       parser.defineEntityReplacementText("spades", "♠");
/*  790 */       parser.defineEntityReplacementText("clubs", "♣");
/*  791 */       parser.defineEntityReplacementText("hearts", "♥");
/*  792 */       parser.defineEntityReplacementText("diams", "♦");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int nextTag(XmlPullParser parser) throws IOException, XmlPullParserException {
/*  808 */     int eventType = parser.next();
/*  809 */     if (eventType == 4)
/*      */     {
/*  811 */       eventType = parser.next();
/*      */     }
/*  813 */     if (eventType != 2 && eventType != 3)
/*      */     {
/*  815 */       throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null);
/*      */     }
/*  817 */     return eventType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Activation parseActivation(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/*  832 */     String tagName = parser.getName();
/*  833 */     Activation activation = new Activation();
/*  834 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  836 */       String name = parser.getAttributeName(i);
/*  837 */       String value = parser.getAttributeValue(i);
/*      */       
/*  839 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  845 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  848 */     Set parsed = new HashSet();
/*  849 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  851 */       if (checkFieldWithDuplicate(parser, "activeByDefault", null, parsed)) {
/*      */         
/*  853 */         activation.setActiveByDefault(getBooleanValue(getTrimmedValue(parser.nextText()), "activeByDefault", parser, "false")); continue;
/*      */       } 
/*  855 */       if (checkFieldWithDuplicate(parser, "jdk", null, parsed)) {
/*      */         
/*  857 */         activation.setJdk(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  859 */       if (checkFieldWithDuplicate(parser, "os", null, parsed)) {
/*      */         
/*  861 */         activation.setOs(parseActivationOS(parser, strict)); continue;
/*      */       } 
/*  863 */       if (checkFieldWithDuplicate(parser, "property", null, parsed)) {
/*      */         
/*  865 */         activation.setProperty(parseActivationProperty(parser, strict)); continue;
/*      */       } 
/*  867 */       if (checkFieldWithDuplicate(parser, "file", null, parsed)) {
/*      */         
/*  869 */         activation.setFile(parseActivationFile(parser, strict));
/*      */         
/*      */         continue;
/*      */       } 
/*  873 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/*  876 */     return activation;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ActivationFile parseActivationFile(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/*  891 */     String tagName = parser.getName();
/*  892 */     ActivationFile activationFile = new ActivationFile();
/*  893 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  895 */       String name = parser.getAttributeName(i);
/*  896 */       String value = parser.getAttributeValue(i);
/*      */       
/*  898 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  904 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  907 */     Set parsed = new HashSet();
/*  908 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  910 */       if (checkFieldWithDuplicate(parser, "missing", null, parsed)) {
/*      */         
/*  912 */         activationFile.setMissing(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  914 */       if (checkFieldWithDuplicate(parser, "exists", null, parsed)) {
/*      */         
/*  916 */         activationFile.setExists(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/*  920 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/*  923 */     return activationFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ActivationOS parseActivationOS(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/*  938 */     String tagName = parser.getName();
/*  939 */     ActivationOS activationOS = new ActivationOS();
/*  940 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  942 */       String name = parser.getAttributeName(i);
/*  943 */       String value = parser.getAttributeValue(i);
/*      */       
/*  945 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  951 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/*  954 */     Set parsed = new HashSet();
/*  955 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/*  957 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/*  959 */         activationOS.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  961 */       if (checkFieldWithDuplicate(parser, "family", null, parsed)) {
/*      */         
/*  963 */         activationOS.setFamily(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  965 */       if (checkFieldWithDuplicate(parser, "arch", null, parsed)) {
/*      */         
/*  967 */         activationOS.setArch(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/*  969 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/*  971 */         activationOS.setVersion(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/*  975 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/*  978 */     return activationOS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ActivationProperty parseActivationProperty(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/*  993 */     String tagName = parser.getName();
/*  994 */     ActivationProperty activationProperty = new ActivationProperty();
/*  995 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/*  997 */       String name = parser.getAttributeName(i);
/*  998 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1000 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1006 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1009 */     Set parsed = new HashSet();
/* 1010 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1012 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1014 */         activationProperty.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1016 */       if (checkFieldWithDuplicate(parser, "value", null, parsed)) {
/*      */         
/* 1018 */         activationProperty.setValue(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1022 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1025 */     return activationProperty;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Build parseBuild(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1040 */     String tagName = parser.getName();
/* 1041 */     Build build = new Build();
/* 1042 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1044 */       String name = parser.getAttributeName(i);
/* 1045 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1047 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1053 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1056 */     Set parsed = new HashSet();
/* 1057 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1059 */       if (checkFieldWithDuplicate(parser, "sourceDirectory", null, parsed)) {
/*      */         
/* 1061 */         build.setSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1063 */       if (checkFieldWithDuplicate(parser, "scriptSourceDirectory", null, parsed)) {
/*      */         
/* 1065 */         build.setScriptSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1067 */       if (checkFieldWithDuplicate(parser, "testSourceDirectory", null, parsed)) {
/*      */         
/* 1069 */         build.setTestSourceDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1071 */       if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
/*      */         
/* 1073 */         build.setOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1075 */       if (checkFieldWithDuplicate(parser, "testOutputDirectory", null, parsed)) {
/*      */         
/* 1077 */         build.setTestOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1079 */       if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
/*      */         
/* 1081 */         List<Extension> extensions = new ArrayList();
/* 1082 */         build.setExtensions(extensions);
/* 1083 */         while (parser.nextTag() == 2) {
/*      */           
/* 1085 */           if ("extension".equals(parser.getName())) {
/*      */             
/* 1087 */             extensions.add(parseExtension(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1091 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1095 */       if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
/*      */         
/* 1097 */         build.setDefaultGoal(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1099 */       if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
/*      */         
/* 1101 */         List<Resource> resources = new ArrayList();
/* 1102 */         build.setResources(resources);
/* 1103 */         while (parser.nextTag() == 2) {
/*      */           
/* 1105 */           if ("resource".equals(parser.getName())) {
/*      */             
/* 1107 */             resources.add(parseResource(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1111 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1115 */       if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
/*      */         
/* 1117 */         List<Resource> testResources = new ArrayList();
/* 1118 */         build.setTestResources(testResources);
/* 1119 */         while (parser.nextTag() == 2) {
/*      */           
/* 1121 */           if ("testResource".equals(parser.getName())) {
/*      */             
/* 1123 */             testResources.add(parseResource(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1127 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1131 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 1133 */         build.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1135 */       if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
/*      */         
/* 1137 */         build.setFinalName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1139 */       if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
/*      */         
/* 1141 */         List<String> filters = new ArrayList();
/* 1142 */         build.setFilters(filters);
/* 1143 */         while (parser.nextTag() == 2) {
/*      */           
/* 1145 */           if ("filter".equals(parser.getName())) {
/*      */             
/* 1147 */             filters.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1151 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1155 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 1157 */         build.setPluginManagement(parsePluginManagement(parser, strict)); continue;
/*      */       } 
/* 1159 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 1161 */         List<Plugin> plugins = new ArrayList();
/* 1162 */         build.setPlugins(plugins);
/* 1163 */         while (parser.nextTag() == 2) {
/*      */           
/* 1165 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 1167 */             plugins.add(parsePlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1171 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1177 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1180 */     return build;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BuildBase parseBuildBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1195 */     String tagName = parser.getName();
/* 1196 */     BuildBase buildBase = new BuildBase();
/* 1197 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1199 */       String name = parser.getAttributeName(i);
/* 1200 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1202 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1208 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1211 */     Set parsed = new HashSet();
/* 1212 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1214 */       if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
/*      */         
/* 1216 */         buildBase.setDefaultGoal(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1218 */       if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
/*      */         
/* 1220 */         List<Resource> resources = new ArrayList();
/* 1221 */         buildBase.setResources(resources);
/* 1222 */         while (parser.nextTag() == 2) {
/*      */           
/* 1224 */           if ("resource".equals(parser.getName())) {
/*      */             
/* 1226 */             resources.add(parseResource(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1230 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1234 */       if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
/*      */         
/* 1236 */         List<Resource> testResources = new ArrayList();
/* 1237 */         buildBase.setTestResources(testResources);
/* 1238 */         while (parser.nextTag() == 2) {
/*      */           
/* 1240 */           if ("testResource".equals(parser.getName())) {
/*      */             
/* 1242 */             testResources.add(parseResource(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1246 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1250 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 1252 */         buildBase.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1254 */       if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
/*      */         
/* 1256 */         buildBase.setFinalName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1258 */       if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
/*      */         
/* 1260 */         List<String> filters = new ArrayList();
/* 1261 */         buildBase.setFilters(filters);
/* 1262 */         while (parser.nextTag() == 2) {
/*      */           
/* 1264 */           if ("filter".equals(parser.getName())) {
/*      */             
/* 1266 */             filters.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1270 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1274 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 1276 */         buildBase.setPluginManagement(parsePluginManagement(parser, strict)); continue;
/*      */       } 
/* 1278 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 1280 */         List<Plugin> plugins = new ArrayList();
/* 1281 */         buildBase.setPlugins(plugins);
/* 1282 */         while (parser.nextTag() == 2) {
/*      */           
/* 1284 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 1286 */             plugins.add(parsePlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1290 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1296 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1299 */     return buildBase;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CiManagement parseCiManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1314 */     String tagName = parser.getName();
/* 1315 */     CiManagement ciManagement = new CiManagement();
/* 1316 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1318 */       String name = parser.getAttributeName(i);
/* 1319 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1321 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1327 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1330 */     Set parsed = new HashSet();
/* 1331 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1333 */       if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
/*      */         
/* 1335 */         ciManagement.setSystem(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1337 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1339 */         ciManagement.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1341 */       if (checkFieldWithDuplicate(parser, "notifiers", null, parsed)) {
/*      */         
/* 1343 */         List<Notifier> notifiers = new ArrayList();
/* 1344 */         ciManagement.setNotifiers(notifiers);
/* 1345 */         while (parser.nextTag() == 2) {
/*      */           
/* 1347 */           if ("notifier".equals(parser.getName())) {
/*      */             
/* 1349 */             notifiers.add(parseNotifier(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1353 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1359 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1362 */     return ciManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ConfigurationContainer parseConfigurationContainer(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1377 */     String tagName = parser.getName();
/* 1378 */     ConfigurationContainer configurationContainer = new ConfigurationContainer();
/* 1379 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1381 */       String name = parser.getAttributeName(i);
/* 1382 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1384 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1390 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1393 */     Set parsed = new HashSet();
/* 1394 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1396 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 1398 */         configurationContainer.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1400 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 1402 */         configurationContainer.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 1406 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1409 */     return configurationContainer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Contributor parseContributor(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1424 */     String tagName = parser.getName();
/* 1425 */     Contributor contributor = new Contributor();
/* 1426 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1428 */       String name = parser.getAttributeName(i);
/* 1429 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1431 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1437 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1440 */     Set parsed = new HashSet();
/* 1441 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1443 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1445 */         contributor.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1447 */       if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
/*      */         
/* 1449 */         contributor.setEmail(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1451 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1453 */         contributor.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1455 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 1457 */         contributor.setOrganization(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1459 */       if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
/*      */         
/* 1461 */         contributor.setOrganizationUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1463 */       if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
/*      */         
/* 1465 */         List<String> roles = new ArrayList();
/* 1466 */         contributor.setRoles(roles);
/* 1467 */         while (parser.nextTag() == 2) {
/*      */           
/* 1469 */           if ("role".equals(parser.getName())) {
/*      */             
/* 1471 */             roles.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1475 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1479 */       if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
/*      */         
/* 1481 */         contributor.setTimezone(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1483 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */         
/* 1485 */         while (parser.nextTag() == 2) {
/*      */           
/* 1487 */           String key = parser.getName();
/* 1488 */           String value = parser.nextText().trim();
/* 1489 */           contributor.addProperty(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 1494 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1497 */     return contributor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Dependency parseDependency(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1512 */     String tagName = parser.getName();
/* 1513 */     Dependency dependency = new Dependency();
/* 1514 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1516 */       String name = parser.getAttributeName(i);
/* 1517 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1519 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1525 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1528 */     Set parsed = new HashSet();
/* 1529 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1531 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 1533 */         dependency.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1535 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 1537 */         dependency.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1539 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 1541 */         dependency.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1543 */       if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
/*      */         
/* 1545 */         dependency.setType(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1547 */       if (checkFieldWithDuplicate(parser, "classifier", null, parsed)) {
/*      */         
/* 1549 */         dependency.setClassifier(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1551 */       if (checkFieldWithDuplicate(parser, "scope", null, parsed)) {
/*      */         
/* 1553 */         dependency.setScope(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1555 */       if (checkFieldWithDuplicate(parser, "systemPath", null, parsed)) {
/*      */         
/* 1557 */         dependency.setSystemPath(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1559 */       if (checkFieldWithDuplicate(parser, "exclusions", null, parsed)) {
/*      */         
/* 1561 */         List<Exclusion> exclusions = new ArrayList();
/* 1562 */         dependency.setExclusions(exclusions);
/* 1563 */         while (parser.nextTag() == 2) {
/*      */           
/* 1565 */           if ("exclusion".equals(parser.getName())) {
/*      */             
/* 1567 */             exclusions.add(parseExclusion(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1571 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1575 */       if (checkFieldWithDuplicate(parser, "optional", null, parsed)) {
/*      */         
/* 1577 */         dependency.setOptional(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1581 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1584 */     return dependency;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DependencyManagement parseDependencyManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1599 */     String tagName = parser.getName();
/* 1600 */     DependencyManagement dependencyManagement = new DependencyManagement();
/* 1601 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1603 */       String name = parser.getAttributeName(i);
/* 1604 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1606 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1612 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1615 */     Set parsed = new HashSet();
/* 1616 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1618 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 1620 */         List<Dependency> dependencies = new ArrayList();
/* 1621 */         dependencyManagement.setDependencies(dependencies);
/* 1622 */         while (parser.nextTag() == 2) {
/*      */           
/* 1624 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 1626 */             dependencies.add(parseDependency(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 1630 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1636 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1639 */     return dependencyManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DeploymentRepository parseDeploymentRepository(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1654 */     String tagName = parser.getName();
/* 1655 */     DeploymentRepository deploymentRepository = new DeploymentRepository();
/* 1656 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1658 */       String name = parser.getAttributeName(i);
/* 1659 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1661 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1667 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1670 */     Set parsed = new HashSet();
/* 1671 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1673 */       if (checkFieldWithDuplicate(parser, "uniqueVersion", null, parsed)) {
/*      */         
/* 1675 */         deploymentRepository.setUniqueVersion(getBooleanValue(getTrimmedValue(parser.nextText()), "uniqueVersion", parser, "true")); continue;
/*      */       } 
/* 1677 */       if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
/*      */         
/* 1679 */         deploymentRepository.setReleases(parseRepositoryPolicy(parser, strict)); continue;
/*      */       } 
/* 1681 */       if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
/*      */         
/* 1683 */         deploymentRepository.setSnapshots(parseRepositoryPolicy(parser, strict)); continue;
/*      */       } 
/* 1685 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 1687 */         deploymentRepository.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1689 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1691 */         deploymentRepository.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1693 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1695 */         deploymentRepository.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1697 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 1699 */         deploymentRepository.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1703 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1706 */     return deploymentRepository;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Developer parseDeveloper(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1721 */     String tagName = parser.getName();
/* 1722 */     Developer developer = new Developer();
/* 1723 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1725 */       String name = parser.getAttributeName(i);
/* 1726 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1728 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1734 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1737 */     Set parsed = new HashSet();
/* 1738 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1740 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 1742 */         developer.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1744 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 1746 */         developer.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1748 */       if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
/*      */         
/* 1750 */         developer.setEmail(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1752 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 1754 */         developer.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1756 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 1758 */         developer.setOrganization(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1760 */       if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
/*      */         
/* 1762 */         developer.setOrganizationUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1764 */       if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
/*      */         
/* 1766 */         List<String> roles = new ArrayList();
/* 1767 */         developer.setRoles(roles);
/* 1768 */         while (parser.nextTag() == 2) {
/*      */           
/* 1770 */           if ("role".equals(parser.getName())) {
/*      */             
/* 1772 */             roles.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 1776 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1780 */       if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
/*      */         
/* 1782 */         developer.setTimezone(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1784 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */         
/* 1786 */         while (parser.nextTag() == 2) {
/*      */           
/* 1788 */           String key = parser.getName();
/* 1789 */           String value = parser.nextText().trim();
/* 1790 */           developer.addProperty(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 1795 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1798 */     return developer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DistributionManagement parseDistributionManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1813 */     String tagName = parser.getName();
/* 1814 */     DistributionManagement distributionManagement = new DistributionManagement();
/* 1815 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1817 */       String name = parser.getAttributeName(i);
/* 1818 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1820 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1826 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1829 */     Set parsed = new HashSet();
/* 1830 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1832 */       if (checkFieldWithDuplicate(parser, "repository", null, parsed)) {
/*      */         
/* 1834 */         distributionManagement.setRepository(parseDeploymentRepository(parser, strict)); continue;
/*      */       } 
/* 1836 */       if (checkFieldWithDuplicate(parser, "snapshotRepository", null, parsed)) {
/*      */         
/* 1838 */         distributionManagement.setSnapshotRepository(parseDeploymentRepository(parser, strict)); continue;
/*      */       } 
/* 1840 */       if (checkFieldWithDuplicate(parser, "site", null, parsed)) {
/*      */         
/* 1842 */         distributionManagement.setSite(parseSite(parser, strict)); continue;
/*      */       } 
/* 1844 */       if (checkFieldWithDuplicate(parser, "downloadUrl", null, parsed)) {
/*      */         
/* 1846 */         distributionManagement.setDownloadUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1848 */       if (checkFieldWithDuplicate(parser, "relocation", null, parsed)) {
/*      */         
/* 1850 */         distributionManagement.setRelocation(parseRelocation(parser, strict)); continue;
/*      */       } 
/* 1852 */       if (checkFieldWithDuplicate(parser, "status", null, parsed)) {
/*      */         
/* 1854 */         distributionManagement.setStatus(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1858 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1861 */     return distributionManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Exclusion parseExclusion(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1876 */     String tagName = parser.getName();
/* 1877 */     Exclusion exclusion = new Exclusion();
/* 1878 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1880 */       String name = parser.getAttributeName(i);
/* 1881 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1883 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1889 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1892 */     Set parsed = new HashSet();
/* 1893 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1895 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 1897 */         exclusion.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1899 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 1901 */         exclusion.setGroupId(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1905 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1908 */     return exclusion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Extension parseExtension(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1923 */     String tagName = parser.getName();
/* 1924 */     Extension extension = new Extension();
/* 1925 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1927 */       String name = parser.getAttributeName(i);
/* 1928 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1930 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1936 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1939 */     Set parsed = new HashSet();
/* 1940 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1942 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 1944 */         extension.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1946 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 1948 */         extension.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1950 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 1952 */         extension.setVersion(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 1956 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 1959 */     return extension;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private FileSet parseFileSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 1974 */     String tagName = parser.getName();
/* 1975 */     FileSet fileSet = new FileSet();
/* 1976 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 1978 */       String name = parser.getAttributeName(i);
/* 1979 */       String value = parser.getAttributeValue(i);
/*      */       
/* 1981 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1987 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 1990 */     Set parsed = new HashSet();
/* 1991 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 1993 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 1995 */         fileSet.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 1997 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 1999 */         List<String> includes = new ArrayList();
/* 2000 */         fileSet.setIncludes(includes);
/* 2001 */         while (parser.nextTag() == 2) {
/*      */           
/* 2003 */           if ("include".equals(parser.getName())) {
/*      */             
/* 2005 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2009 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2013 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 2015 */         List<String> excludes = new ArrayList();
/* 2016 */         fileSet.setExcludes(excludes);
/* 2017 */         while (parser.nextTag() == 2) {
/*      */           
/* 2019 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 2021 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2025 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2031 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2034 */     return fileSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IssueManagement parseIssueManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2049 */     String tagName = parser.getName();
/* 2050 */     IssueManagement issueManagement = new IssueManagement();
/* 2051 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2053 */       String name = parser.getAttributeName(i);
/* 2054 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2056 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2062 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2065 */     Set parsed = new HashSet();
/* 2066 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2068 */       if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
/*      */         
/* 2070 */         issueManagement.setSystem(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2072 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2074 */         issueManagement.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2078 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2081 */     return issueManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private License parseLicense(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2096 */     String tagName = parser.getName();
/* 2097 */     License license = new License();
/* 2098 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2100 */       String name = parser.getAttributeName(i);
/* 2101 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2103 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2109 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2112 */     Set parsed = new HashSet();
/* 2113 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2115 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2117 */         license.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2119 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2121 */         license.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2123 */       if (checkFieldWithDuplicate(parser, "distribution", null, parsed)) {
/*      */         
/* 2125 */         license.setDistribution(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2127 */       if (checkFieldWithDuplicate(parser, "comments", null, parsed)) {
/*      */         
/* 2129 */         license.setComments(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2133 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2136 */     return license;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private MailingList parseMailingList(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2151 */     String tagName = parser.getName();
/* 2152 */     MailingList mailingList = new MailingList();
/* 2153 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2155 */       String name = parser.getAttributeName(i);
/* 2156 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2158 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2164 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2167 */     Set parsed = new HashSet();
/* 2168 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2170 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2172 */         mailingList.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2174 */       if (checkFieldWithDuplicate(parser, "subscribe", null, parsed)) {
/*      */         
/* 2176 */         mailingList.setSubscribe(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2178 */       if (checkFieldWithDuplicate(parser, "unsubscribe", null, parsed)) {
/*      */         
/* 2180 */         mailingList.setUnsubscribe(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2182 */       if (checkFieldWithDuplicate(parser, "post", null, parsed)) {
/*      */         
/* 2184 */         mailingList.setPost(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2186 */       if (checkFieldWithDuplicate(parser, "archive", null, parsed)) {
/*      */         
/* 2188 */         mailingList.setArchive(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2190 */       if (checkFieldWithDuplicate(parser, "otherArchives", null, parsed)) {
/*      */         
/* 2192 */         List<String> otherArchives = new ArrayList();
/* 2193 */         mailingList.setOtherArchives(otherArchives);
/* 2194 */         while (parser.nextTag() == 2) {
/*      */           
/* 2196 */           if ("otherArchive".equals(parser.getName())) {
/*      */             
/* 2198 */             otherArchives.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2202 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2208 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2211 */     return mailingList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Model parseModel(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2226 */     String tagName = parser.getName();
/* 2227 */     Model model = new Model();
/* 2228 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2230 */       String name = parser.getAttributeName(i);
/* 2231 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2233 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */         
/* 2237 */         if (!"xmlns".equals(name))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2243 */           checkUnknownAttribute(parser, name, tagName, strict); } 
/*      */       }
/*      */     } 
/* 2246 */     Set parsed = new HashSet();
/* 2247 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2249 */       if (checkFieldWithDuplicate(parser, "modelVersion", null, parsed)) {
/*      */         
/* 2251 */         model.setModelVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2253 */       if (checkFieldWithDuplicate(parser, "parent", null, parsed)) {
/*      */         
/* 2255 */         model.setParent(parseParent(parser, strict)); continue;
/*      */       } 
/* 2257 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2259 */         model.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2261 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2263 */         model.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2265 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 2267 */         model.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2269 */       if (checkFieldWithDuplicate(parser, "packaging", null, parsed)) {
/*      */         
/* 2271 */         model.setPackaging(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2273 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2275 */         model.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2277 */       if (checkFieldWithDuplicate(parser, "description", null, parsed)) {
/*      */         
/* 2279 */         model.setDescription(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2281 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2283 */         model.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2285 */       if (checkFieldWithDuplicate(parser, "inceptionYear", null, parsed)) {
/*      */         
/* 2287 */         model.setInceptionYear(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2289 */       if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
/*      */         
/* 2291 */         model.setOrganization(parseOrganization(parser, strict)); continue;
/*      */       } 
/* 2293 */       if (checkFieldWithDuplicate(parser, "licenses", null, parsed)) {
/*      */         
/* 2295 */         List<License> licenses = new ArrayList();
/* 2296 */         model.setLicenses(licenses);
/* 2297 */         while (parser.nextTag() == 2) {
/*      */           
/* 2299 */           if ("license".equals(parser.getName())) {
/*      */             
/* 2301 */             licenses.add(parseLicense(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2305 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2309 */       if (checkFieldWithDuplicate(parser, "developers", null, parsed)) {
/*      */         
/* 2311 */         List<Developer> developers = new ArrayList();
/* 2312 */         model.setDevelopers(developers);
/* 2313 */         while (parser.nextTag() == 2) {
/*      */           
/* 2315 */           if ("developer".equals(parser.getName())) {
/*      */             
/* 2317 */             developers.add(parseDeveloper(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2321 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2325 */       if (checkFieldWithDuplicate(parser, "contributors", null, parsed)) {
/*      */         
/* 2327 */         List<Contributor> contributors = new ArrayList();
/* 2328 */         model.setContributors(contributors);
/* 2329 */         while (parser.nextTag() == 2) {
/*      */           
/* 2331 */           if ("contributor".equals(parser.getName())) {
/*      */             
/* 2333 */             contributors.add(parseContributor(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2337 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2341 */       if (checkFieldWithDuplicate(parser, "mailingLists", null, parsed)) {
/*      */         
/* 2343 */         List<MailingList> mailingLists = new ArrayList();
/* 2344 */         model.setMailingLists(mailingLists);
/* 2345 */         while (parser.nextTag() == 2) {
/*      */           
/* 2347 */           if ("mailingList".equals(parser.getName())) {
/*      */             
/* 2349 */             mailingLists.add(parseMailingList(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2353 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2357 */       if (checkFieldWithDuplicate(parser, "prerequisites", null, parsed)) {
/*      */         
/* 2359 */         model.setPrerequisites(parsePrerequisites(parser, strict)); continue;
/*      */       } 
/* 2361 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 2363 */         List<String> modules = new ArrayList();
/* 2364 */         model.setModules(modules);
/* 2365 */         while (parser.nextTag() == 2) {
/*      */           
/* 2367 */           if ("module".equals(parser.getName())) {
/*      */             
/* 2369 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2373 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2377 */       if (checkFieldWithDuplicate(parser, "scm", null, parsed)) {
/*      */         
/* 2379 */         model.setScm(parseScm(parser, strict)); continue;
/*      */       } 
/* 2381 */       if (checkFieldWithDuplicate(parser, "issueManagement", null, parsed)) {
/*      */         
/* 2383 */         model.setIssueManagement(parseIssueManagement(parser, strict)); continue;
/*      */       } 
/* 2385 */       if (checkFieldWithDuplicate(parser, "ciManagement", null, parsed)) {
/*      */         
/* 2387 */         model.setCiManagement(parseCiManagement(parser, strict)); continue;
/*      */       } 
/* 2389 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 2391 */         model.setDistributionManagement(parseDistributionManagement(parser, strict)); continue;
/*      */       } 
/* 2393 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */         
/* 2395 */         while (parser.nextTag() == 2) {
/*      */           
/* 2397 */           String key = parser.getName();
/* 2398 */           String value = parser.nextText().trim();
/* 2399 */           model.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 2402 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 2404 */         model.setDependencyManagement(parseDependencyManagement(parser, strict)); continue;
/*      */       } 
/* 2406 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 2408 */         List<Dependency> dependencies = new ArrayList();
/* 2409 */         model.setDependencies(dependencies);
/* 2410 */         while (parser.nextTag() == 2) {
/*      */           
/* 2412 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 2414 */             dependencies.add(parseDependency(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2418 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2422 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 2424 */         List<Repository> repositories = new ArrayList();
/* 2425 */         model.setRepositories(repositories);
/* 2426 */         while (parser.nextTag() == 2) {
/*      */           
/* 2428 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 2430 */             repositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2434 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2438 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 2440 */         List<Repository> pluginRepositories = new ArrayList();
/* 2441 */         model.setPluginRepositories(pluginRepositories);
/* 2442 */         while (parser.nextTag() == 2) {
/*      */           
/* 2444 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 2446 */             pluginRepositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2450 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2454 */       if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
/*      */         
/* 2456 */         model.setBuild(parseBuild(parser, strict)); continue;
/*      */       } 
/* 2458 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 2460 */         model.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 2462 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 2464 */         model.setReporting(parseReporting(parser, strict)); continue;
/*      */       } 
/* 2466 */       if (checkFieldWithDuplicate(parser, "profiles", null, parsed)) {
/*      */         
/* 2468 */         List<Profile> profiles = new ArrayList();
/* 2469 */         model.setProfiles(profiles);
/* 2470 */         while (parser.nextTag() == 2) {
/*      */           
/* 2472 */           if ("profile".equals(parser.getName())) {
/*      */             
/* 2474 */             profiles.add(parseProfile(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2478 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2484 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2487 */     return model;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ModelBase parseModelBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2502 */     String tagName = parser.getName();
/* 2503 */     ModelBase modelBase = new ModelBase();
/* 2504 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2506 */       String name = parser.getAttributeName(i);
/* 2507 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2509 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2515 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2518 */     Set parsed = new HashSet();
/* 2519 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2521 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 2523 */         List<String> modules = new ArrayList();
/* 2524 */         modelBase.setModules(modules);
/* 2525 */         while (parser.nextTag() == 2) {
/*      */           
/* 2527 */           if ("module".equals(parser.getName())) {
/*      */             
/* 2529 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2533 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2537 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 2539 */         modelBase.setDistributionManagement(parseDistributionManagement(parser, strict)); continue;
/*      */       } 
/* 2541 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */         
/* 2543 */         while (parser.nextTag() == 2) {
/*      */           
/* 2545 */           String key = parser.getName();
/* 2546 */           String value = parser.nextText().trim();
/* 2547 */           modelBase.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 2550 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 2552 */         modelBase.setDependencyManagement(parseDependencyManagement(parser, strict)); continue;
/*      */       } 
/* 2554 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 2556 */         List<Dependency> dependencies = new ArrayList();
/* 2557 */         modelBase.setDependencies(dependencies);
/* 2558 */         while (parser.nextTag() == 2) {
/*      */           
/* 2560 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 2562 */             dependencies.add(parseDependency(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2566 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2570 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 2572 */         List<Repository> repositories = new ArrayList();
/* 2573 */         modelBase.setRepositories(repositories);
/* 2574 */         while (parser.nextTag() == 2) {
/*      */           
/* 2576 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 2578 */             repositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2582 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2586 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 2588 */         List<Repository> pluginRepositories = new ArrayList();
/* 2589 */         modelBase.setPluginRepositories(pluginRepositories);
/* 2590 */         while (parser.nextTag() == 2) {
/*      */           
/* 2592 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 2594 */             pluginRepositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2598 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2602 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 2604 */         modelBase.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 2606 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 2608 */         modelBase.setReporting(parseReporting(parser, strict));
/*      */         
/*      */         continue;
/*      */       } 
/* 2612 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2615 */     return modelBase;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Notifier parseNotifier(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2630 */     String tagName = parser.getName();
/* 2631 */     Notifier notifier = new Notifier();
/* 2632 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2634 */       String name = parser.getAttributeName(i);
/* 2635 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2637 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2643 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2646 */     Set parsed = new HashSet();
/* 2647 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2649 */       if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
/*      */         
/* 2651 */         notifier.setType(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2653 */       if (checkFieldWithDuplicate(parser, "sendOnError", null, parsed)) {
/*      */         
/* 2655 */         notifier.setSendOnError(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnError", parser, "true")); continue;
/*      */       } 
/* 2657 */       if (checkFieldWithDuplicate(parser, "sendOnFailure", null, parsed)) {
/*      */         
/* 2659 */         notifier.setSendOnFailure(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnFailure", parser, "true")); continue;
/*      */       } 
/* 2661 */       if (checkFieldWithDuplicate(parser, "sendOnSuccess", null, parsed)) {
/*      */         
/* 2663 */         notifier.setSendOnSuccess(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnSuccess", parser, "true")); continue;
/*      */       } 
/* 2665 */       if (checkFieldWithDuplicate(parser, "sendOnWarning", null, parsed)) {
/*      */         
/* 2667 */         notifier.setSendOnWarning(getBooleanValue(getTrimmedValue(parser.nextText()), "sendOnWarning", parser, "true")); continue;
/*      */       } 
/* 2669 */       if (checkFieldWithDuplicate(parser, "address", null, parsed)) {
/*      */         
/* 2671 */         notifier.setAddress(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2673 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 2675 */         while (parser.nextTag() == 2) {
/*      */           
/* 2677 */           String key = parser.getName();
/* 2678 */           String value = parser.nextText().trim();
/* 2679 */           notifier.addConfiguration(key, value);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 2684 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2687 */     return notifier;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Organization parseOrganization(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2702 */     String tagName = parser.getName();
/* 2703 */     Organization organization = new Organization();
/* 2704 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2706 */       String name = parser.getAttributeName(i);
/* 2707 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2709 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2715 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2718 */     Set parsed = new HashSet();
/* 2719 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2721 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 2723 */         organization.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2725 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 2727 */         organization.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2731 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2734 */     return organization;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Parent parseParent(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2749 */     String tagName = parser.getName();
/* 2750 */     Parent parent = new Parent();
/* 2751 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2753 */       String name = parser.getAttributeName(i);
/* 2754 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2756 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2762 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2765 */     Set parsed = new HashSet();
/* 2766 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2768 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2770 */         parent.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2772 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2774 */         parent.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2776 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 2778 */         parent.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2780 */       if (checkFieldWithDuplicate(parser, "relativePath", null, parsed)) {
/*      */         
/* 2782 */         parent.setRelativePath(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 2786 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2789 */     return parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PatternSet parsePatternSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2804 */     String tagName = parser.getName();
/* 2805 */     PatternSet patternSet = new PatternSet();
/* 2806 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2808 */       String name = parser.getAttributeName(i);
/* 2809 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2811 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2817 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2820 */     Set parsed = new HashSet();
/* 2821 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2823 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 2825 */         List<String> includes = new ArrayList();
/* 2826 */         patternSet.setIncludes(includes);
/* 2827 */         while (parser.nextTag() == 2) {
/*      */           
/* 2829 */           if ("include".equals(parser.getName())) {
/*      */             
/* 2831 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2835 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2839 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 2841 */         List<String> excludes = new ArrayList();
/* 2842 */         patternSet.setExcludes(excludes);
/* 2843 */         while (parser.nextTag() == 2) {
/*      */           
/* 2845 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 2847 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 2851 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2857 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2860 */     return patternSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Plugin parsePlugin(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2875 */     String tagName = parser.getName();
/* 2876 */     Plugin plugin = new Plugin();
/* 2877 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2879 */       String name = parser.getAttributeName(i);
/* 2880 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2882 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2888 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2891 */     Set parsed = new HashSet();
/* 2892 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2894 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 2896 */         plugin.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2898 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 2900 */         plugin.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2902 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 2904 */         plugin.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2906 */       if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
/*      */         
/* 2908 */         plugin.setExtensions(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2910 */       if (checkFieldWithDuplicate(parser, "executions", null, parsed)) {
/*      */         
/* 2912 */         List<PluginExecution> executions = new ArrayList();
/* 2913 */         plugin.setExecutions(executions);
/* 2914 */         while (parser.nextTag() == 2) {
/*      */           
/* 2916 */           if ("execution".equals(parser.getName())) {
/*      */             
/* 2918 */             executions.add(parsePluginExecution(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2922 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2926 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 2928 */         List<Dependency> dependencies = new ArrayList();
/* 2929 */         plugin.setDependencies(dependencies);
/* 2930 */         while (parser.nextTag() == 2) {
/*      */           
/* 2932 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 2934 */             dependencies.add(parseDependency(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 2938 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 2942 */       if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
/*      */         
/* 2944 */         plugin.setGoals(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 2946 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 2948 */         plugin.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 2950 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 2952 */         plugin.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 2956 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 2959 */     return plugin;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PluginConfiguration parsePluginConfiguration(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 2974 */     String tagName = parser.getName();
/* 2975 */     PluginConfiguration pluginConfiguration = new PluginConfiguration();
/* 2976 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 2978 */       String name = parser.getAttributeName(i);
/* 2979 */       String value = parser.getAttributeValue(i);
/*      */       
/* 2981 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2987 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 2990 */     Set parsed = new HashSet();
/* 2991 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 2993 */       if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
/*      */         
/* 2995 */         pluginConfiguration.setPluginManagement(parsePluginManagement(parser, strict)); continue;
/*      */       } 
/* 2997 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 2999 */         List<Plugin> plugins = new ArrayList();
/* 3000 */         pluginConfiguration.setPlugins(plugins);
/* 3001 */         while (parser.nextTag() == 2) {
/*      */           
/* 3003 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3005 */             plugins.add(parsePlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3009 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3015 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3018 */     return pluginConfiguration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PluginContainer parsePluginContainer(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3033 */     String tagName = parser.getName();
/* 3034 */     PluginContainer pluginContainer = new PluginContainer();
/* 3035 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3037 */       String name = parser.getAttributeName(i);
/* 3038 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3040 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3046 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3049 */     Set parsed = new HashSet();
/* 3050 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3052 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3054 */         List<Plugin> plugins = new ArrayList();
/* 3055 */         pluginContainer.setPlugins(plugins);
/* 3056 */         while (parser.nextTag() == 2) {
/*      */           
/* 3058 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3060 */             plugins.add(parsePlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3064 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3070 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3073 */     return pluginContainer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PluginExecution parsePluginExecution(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3088 */     String tagName = parser.getName();
/* 3089 */     PluginExecution pluginExecution = new PluginExecution();
/* 3090 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3092 */       String name = parser.getAttributeName(i);
/* 3093 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3095 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3101 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3104 */     Set parsed = new HashSet();
/* 3105 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3107 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3109 */         pluginExecution.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3111 */       if (checkFieldWithDuplicate(parser, "phase", null, parsed)) {
/*      */         
/* 3113 */         pluginExecution.setPhase(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3115 */       if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
/*      */         
/* 3117 */         List<String> goals = new ArrayList();
/* 3118 */         pluginExecution.setGoals(goals);
/* 3119 */         while (parser.nextTag() == 2) {
/*      */           
/* 3121 */           if ("goal".equals(parser.getName())) {
/*      */             
/* 3123 */             goals.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3127 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3131 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3133 */         pluginExecution.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3135 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3137 */         pluginExecution.setConfiguration(Xpp3DomBuilder.build(parser));
/*      */         
/*      */         continue;
/*      */       } 
/* 3141 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3144 */     return pluginExecution;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private PluginManagement parsePluginManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3159 */     String tagName = parser.getName();
/* 3160 */     PluginManagement pluginManagement = new PluginManagement();
/* 3161 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3163 */       String name = parser.getAttributeName(i);
/* 3164 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3166 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3172 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3175 */     Set parsed = new HashSet();
/* 3176 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3178 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3180 */         List<Plugin> plugins = new ArrayList();
/* 3181 */         pluginManagement.setPlugins(plugins);
/* 3182 */         while (parser.nextTag() == 2) {
/*      */           
/* 3184 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3186 */             plugins.add(parsePlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3190 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3196 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3199 */     return pluginManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Prerequisites parsePrerequisites(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3214 */     String tagName = parser.getName();
/* 3215 */     Prerequisites prerequisites = new Prerequisites();
/* 3216 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3218 */       String name = parser.getAttributeName(i);
/* 3219 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3221 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3227 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3230 */     Set parsed = new HashSet();
/* 3231 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3233 */       if (checkFieldWithDuplicate(parser, "maven", null, parsed)) {
/*      */         
/* 3235 */         prerequisites.setMaven(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3239 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3242 */     return prerequisites;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Profile parseProfile(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3257 */     String tagName = parser.getName();
/* 3258 */     Profile profile = new Profile();
/* 3259 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3261 */       String name = parser.getAttributeName(i);
/* 3262 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3264 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3270 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3273 */     Set parsed = new HashSet();
/* 3274 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3276 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3278 */         profile.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3280 */       if (checkFieldWithDuplicate(parser, "activation", null, parsed)) {
/*      */         
/* 3282 */         profile.setActivation(parseActivation(parser, strict)); continue;
/*      */       } 
/* 3284 */       if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
/*      */         
/* 3286 */         profile.setBuild(parseBuildBase(parser, strict)); continue;
/*      */       } 
/* 3288 */       if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
/*      */         
/* 3290 */         List<String> modules = new ArrayList();
/* 3291 */         profile.setModules(modules);
/* 3292 */         while (parser.nextTag() == 2) {
/*      */           
/* 3294 */           if ("module".equals(parser.getName())) {
/*      */             
/* 3296 */             modules.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3300 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3304 */       if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
/*      */         
/* 3306 */         profile.setDistributionManagement(parseDistributionManagement(parser, strict)); continue;
/*      */       } 
/* 3308 */       if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
/*      */         
/* 3310 */         while (parser.nextTag() == 2) {
/*      */           
/* 3312 */           String key = parser.getName();
/* 3313 */           String value = parser.nextText().trim();
/* 3314 */           profile.addProperty(key, value);
/*      */         }  continue;
/*      */       } 
/* 3317 */       if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
/*      */         
/* 3319 */         profile.setDependencyManagement(parseDependencyManagement(parser, strict)); continue;
/*      */       } 
/* 3321 */       if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
/*      */         
/* 3323 */         List<Dependency> dependencies = new ArrayList();
/* 3324 */         profile.setDependencies(dependencies);
/* 3325 */         while (parser.nextTag() == 2) {
/*      */           
/* 3327 */           if ("dependency".equals(parser.getName())) {
/*      */             
/* 3329 */             dependencies.add(parseDependency(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3333 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3337 */       if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
/*      */         
/* 3339 */         List<Repository> repositories = new ArrayList();
/* 3340 */         profile.setRepositories(repositories);
/* 3341 */         while (parser.nextTag() == 2) {
/*      */           
/* 3343 */           if ("repository".equals(parser.getName())) {
/*      */             
/* 3345 */             repositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3349 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3353 */       if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
/*      */         
/* 3355 */         List<Repository> pluginRepositories = new ArrayList();
/* 3356 */         profile.setPluginRepositories(pluginRepositories);
/* 3357 */         while (parser.nextTag() == 2) {
/*      */           
/* 3359 */           if ("pluginRepository".equals(parser.getName())) {
/*      */             
/* 3361 */             pluginRepositories.add(parseRepository(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3365 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3369 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 3371 */         profile.setReports(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3373 */       if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
/*      */         
/* 3375 */         profile.setReporting(parseReporting(parser, strict));
/*      */         
/*      */         continue;
/*      */       } 
/* 3379 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3382 */     return profile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Relocation parseRelocation(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3397 */     String tagName = parser.getName();
/* 3398 */     Relocation relocation = new Relocation();
/* 3399 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3401 */       String name = parser.getAttributeName(i);
/* 3402 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3404 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3410 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3413 */     Set parsed = new HashSet();
/* 3414 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3416 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3418 */         relocation.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3420 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3422 */         relocation.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3424 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3426 */         relocation.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3428 */       if (checkFieldWithDuplicate(parser, "message", null, parsed)) {
/*      */         
/* 3430 */         relocation.setMessage(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3434 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3437 */     return relocation;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ReportPlugin parseReportPlugin(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3452 */     String tagName = parser.getName();
/* 3453 */     ReportPlugin reportPlugin = new ReportPlugin();
/* 3454 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3456 */       String name = parser.getAttributeName(i);
/* 3457 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3459 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3465 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3468 */     Set parsed = new HashSet();
/* 3469 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3471 */       if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
/*      */         
/* 3473 */         reportPlugin.setGroupId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3475 */       if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
/*      */         
/* 3477 */         reportPlugin.setArtifactId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3479 */       if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
/*      */         
/* 3481 */         reportPlugin.setVersion(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3483 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3485 */         reportPlugin.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3487 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3489 */         reportPlugin.setConfiguration(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3491 */       if (checkFieldWithDuplicate(parser, "reportSets", null, parsed)) {
/*      */         
/* 3493 */         List<ReportSet> reportSets = new ArrayList();
/* 3494 */         reportPlugin.setReportSets(reportSets);
/* 3495 */         while (parser.nextTag() == 2) {
/*      */           
/* 3497 */           if ("reportSet".equals(parser.getName())) {
/*      */             
/* 3499 */             reportSets.add(parseReportSet(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3503 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3509 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3512 */     return reportPlugin;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ReportSet parseReportSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3527 */     String tagName = parser.getName();
/* 3528 */     ReportSet reportSet = new ReportSet();
/* 3529 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3531 */       String name = parser.getAttributeName(i);
/* 3532 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3534 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3540 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3543 */     Set parsed = new HashSet();
/* 3544 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3546 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3548 */         reportSet.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3550 */       if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
/*      */         
/* 3552 */         reportSet.setConfiguration(Xpp3DomBuilder.build(parser)); continue;
/*      */       } 
/* 3554 */       if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
/*      */         
/* 3556 */         reportSet.setInherited(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3558 */       if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
/*      */         
/* 3560 */         List<String> reports = new ArrayList();
/* 3561 */         reportSet.setReports(reports);
/* 3562 */         while (parser.nextTag() == 2) {
/*      */           
/* 3564 */           if ("report".equals(parser.getName())) {
/*      */             
/* 3566 */             reports.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3570 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3576 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3579 */     return reportSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Reporting parseReporting(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3594 */     String tagName = parser.getName();
/* 3595 */     Reporting reporting = new Reporting();
/* 3596 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3598 */       String name = parser.getAttributeName(i);
/* 3599 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3601 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3607 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3610 */     Set parsed = new HashSet();
/* 3611 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3613 */       if (checkFieldWithDuplicate(parser, "excludeDefaults", null, parsed)) {
/*      */         
/* 3615 */         reporting.setExcludeDefaults(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3617 */       if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
/*      */         
/* 3619 */         reporting.setOutputDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3621 */       if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
/*      */         
/* 3623 */         List<ReportPlugin> plugins = new ArrayList();
/* 3624 */         reporting.setPlugins(plugins);
/* 3625 */         while (parser.nextTag() == 2) {
/*      */           
/* 3627 */           if ("plugin".equals(parser.getName())) {
/*      */             
/* 3629 */             plugins.add(parseReportPlugin(parser, strict));
/*      */             
/*      */             continue;
/*      */           } 
/* 3633 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3639 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3642 */     return reporting;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Repository parseRepository(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3657 */     String tagName = parser.getName();
/* 3658 */     Repository repository = new Repository();
/* 3659 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3661 */       String name = parser.getAttributeName(i);
/* 3662 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3664 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3670 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3673 */     Set parsed = new HashSet();
/* 3674 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3676 */       if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
/*      */         
/* 3678 */         repository.setReleases(parseRepositoryPolicy(parser, strict)); continue;
/*      */       } 
/* 3680 */       if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
/*      */         
/* 3682 */         repository.setSnapshots(parseRepositoryPolicy(parser, strict)); continue;
/*      */       } 
/* 3684 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3686 */         repository.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3688 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 3690 */         repository.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3692 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 3694 */         repository.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3696 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 3698 */         repository.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3702 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3705 */     return repository;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RepositoryBase parseRepositoryBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3720 */     String tagName = parser.getName();
/* 3721 */     RepositoryBase repositoryBase = new RepositoryBase();
/* 3722 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3724 */       String name = parser.getAttributeName(i);
/* 3725 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3727 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3733 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3736 */     Set parsed = new HashSet();
/* 3737 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3739 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3741 */         repositoryBase.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3743 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 3745 */         repositoryBase.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3747 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 3749 */         repositoryBase.setUrl(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3751 */       if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
/*      */         
/* 3753 */         repositoryBase.setLayout(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3757 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3760 */     return repositoryBase;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RepositoryPolicy parseRepositoryPolicy(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3775 */     String tagName = parser.getName();
/* 3776 */     RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
/* 3777 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3779 */       String name = parser.getAttributeName(i);
/* 3780 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3782 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3788 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3791 */     Set parsed = new HashSet();
/* 3792 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3794 */       if (checkFieldWithDuplicate(parser, "enabled", null, parsed)) {
/*      */         
/* 3796 */         repositoryPolicy.setEnabled(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3798 */       if (checkFieldWithDuplicate(parser, "updatePolicy", null, parsed)) {
/*      */         
/* 3800 */         repositoryPolicy.setUpdatePolicy(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3802 */       if (checkFieldWithDuplicate(parser, "checksumPolicy", null, parsed)) {
/*      */         
/* 3804 */         repositoryPolicy.setChecksumPolicy(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3808 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3811 */     return repositoryPolicy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Resource parseResource(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3826 */     String tagName = parser.getName();
/* 3827 */     Resource resource = new Resource();
/* 3828 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3830 */       String name = parser.getAttributeName(i);
/* 3831 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3833 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3839 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3842 */     Set parsed = new HashSet();
/* 3843 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3845 */       if (checkFieldWithDuplicate(parser, "targetPath", null, parsed)) {
/*      */         
/* 3847 */         resource.setTargetPath(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3849 */       if (checkFieldWithDuplicate(parser, "filtering", null, parsed)) {
/*      */         
/* 3851 */         resource.setFiltering(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3853 */       if (checkFieldWithDuplicate(parser, "mergeId", null, parsed)) {
/*      */         
/* 3855 */         resource.setMergeId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3857 */       if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
/*      */         
/* 3859 */         resource.setDirectory(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3861 */       if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
/*      */         
/* 3863 */         List<String> includes = new ArrayList();
/* 3864 */         resource.setIncludes(includes);
/* 3865 */         while (parser.nextTag() == 2) {
/*      */           
/* 3867 */           if ("include".equals(parser.getName())) {
/*      */             
/* 3869 */             includes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3873 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         continue;
/*      */       } 
/* 3877 */       if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
/*      */         
/* 3879 */         List<String> excludes = new ArrayList();
/* 3880 */         resource.setExcludes(excludes);
/* 3881 */         while (parser.nextTag() == 2) {
/*      */           
/* 3883 */           if ("exclude".equals(parser.getName())) {
/*      */             
/* 3885 */             excludes.add(getTrimmedValue(parser.nextText()));
/*      */             
/*      */             continue;
/*      */           } 
/* 3889 */           checkUnknownElement(parser, strict);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 3895 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3898 */     return resource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Scm parseScm(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3913 */     String tagName = parser.getName();
/* 3914 */     Scm scm = new Scm();
/* 3915 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3917 */       String name = parser.getAttributeName(i);
/* 3918 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3920 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3926 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3929 */     Set parsed = new HashSet();
/* 3930 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3932 */       if (checkFieldWithDuplicate(parser, "connection", null, parsed)) {
/*      */         
/* 3934 */         scm.setConnection(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3936 */       if (checkFieldWithDuplicate(parser, "developerConnection", null, parsed)) {
/*      */         
/* 3938 */         scm.setDeveloperConnection(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3940 */       if (checkFieldWithDuplicate(parser, "tag", null, parsed)) {
/*      */         
/* 3942 */         scm.setTag(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3944 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 3946 */         scm.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 3950 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 3953 */     return scm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Site parseSite(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 3968 */     String tagName = parser.getName();
/* 3969 */     Site site = new Site();
/* 3970 */     for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
/*      */       
/* 3972 */       String name = parser.getAttributeName(i);
/* 3973 */       String value = parser.getAttributeValue(i);
/*      */       
/* 3975 */       if (name.indexOf(':') < 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3981 */         checkUnknownAttribute(parser, name, tagName, strict);
/*      */       }
/*      */     } 
/* 3984 */     Set parsed = new HashSet();
/* 3985 */     while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
/*      */       
/* 3987 */       if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
/*      */         
/* 3989 */         site.setId(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3991 */       if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
/*      */         
/* 3993 */         site.setName(getTrimmedValue(parser.nextText())); continue;
/*      */       } 
/* 3995 */       if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
/*      */         
/* 3997 */         site.setUrl(getTrimmedValue(parser.nextText()));
/*      */         
/*      */         continue;
/*      */       } 
/* 4001 */       checkUnknownElement(parser, strict);
/*      */     } 
/*      */     
/* 4004 */     return site;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Model read(Reader reader, boolean strict) throws IOException, XmlPullParserException {
/* 4019 */     MXParser mXParser = new MXParser();
/*      */     
/* 4021 */     mXParser.setInput(reader);
/*      */     
/* 4023 */     initParser((XmlPullParser)mXParser);
/*      */     
/* 4025 */     return read((XmlPullParser)mXParser, strict);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Model read(Reader reader) throws IOException, XmlPullParserException {
/* 4039 */     return read(reader, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Model read(InputStream in, boolean strict) throws IOException, XmlPullParserException {
/* 4054 */     return read((Reader)ReaderFactory.newXmlReader(in), strict);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Model read(InputStream in) throws IOException, XmlPullParserException {
/* 4068 */     return read((Reader)ReaderFactory.newXmlReader(in));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Model read(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
/* 4083 */     int eventType = parser.getEventType();
/* 4084 */     while (eventType != 1) {
/*      */       
/* 4086 */       if (eventType == 2) {
/*      */         
/* 4088 */         if (strict && !"project".equals(parser.getName()))
/*      */         {
/* 4090 */           throw new XmlPullParserException("Expected root element 'project' but found '" + parser.getName() + "'", parser, null);
/*      */         }
/* 4092 */         Model model = parseModel(parser, strict);
/* 4093 */         model.setModelEncoding(parser.getInputEncoding());
/* 4094 */         return model;
/*      */       } 
/* 4096 */       eventType = parser.next();
/*      */     } 
/* 4098 */     throw new XmlPullParserException("Expected root element 'project' but found no element at all: invalid XML document", parser, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAddDefaultEntities(boolean addDefaultEntities) {
/* 4108 */     this.addDefaultEntities = addDefaultEntities;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\io\xpp3\MavenXpp3Reader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */