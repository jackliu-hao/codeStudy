/*      */ package org.apache.maven.model.io.xpp3;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Writer;
/*      */ import java.util.Iterator;
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
/*      */ import org.codehaus.plexus.util.xml.Xpp3Dom;
/*      */ import org.codehaus.plexus.util.xml.pull.MXSerializer;
/*      */ import org.codehaus.plexus.util.xml.pull.XmlSerializer;
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
/*      */ public class MavenXpp3Writer
/*      */ {
/*   82 */   private static final String NAMESPACE = null;
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
/*      */   public void write(Writer writer, Model model) throws IOException {
/*   99 */     MXSerializer mXSerializer = new MXSerializer();
/*  100 */     mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
/*  101 */     mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
/*  102 */     mXSerializer.setOutput(writer);
/*  103 */     mXSerializer.startDocument(model.getModelEncoding(), null);
/*  104 */     writeModel(model, "project", (XmlSerializer)mXSerializer);
/*  105 */     mXSerializer.endDocument();
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
/*      */   public void write(OutputStream stream, Model model) throws IOException {
/*  118 */     MXSerializer mXSerializer = new MXSerializer();
/*  119 */     mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
/*  120 */     mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
/*  121 */     mXSerializer.setOutput(stream, model.getModelEncoding());
/*  122 */     mXSerializer.startDocument(model.getModelEncoding(), null);
/*  123 */     writeModel(model, "project", (XmlSerializer)mXSerializer);
/*  124 */     mXSerializer.endDocument();
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
/*      */   private void writeActivation(Activation activation, String tagName, XmlSerializer serializer) throws IOException {
/*  138 */     serializer.startTag(NAMESPACE, tagName);
/*  139 */     if (activation.isActiveByDefault())
/*      */     {
/*  141 */       serializer.startTag(NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(NAMESPACE, "activeByDefault");
/*      */     }
/*  143 */     if (activation.getJdk() != null)
/*      */     {
/*  145 */       serializer.startTag(NAMESPACE, "jdk").text(activation.getJdk()).endTag(NAMESPACE, "jdk");
/*      */     }
/*  147 */     if (activation.getOs() != null)
/*      */     {
/*  149 */       writeActivationOS(activation.getOs(), "os", serializer);
/*      */     }
/*  151 */     if (activation.getProperty() != null)
/*      */     {
/*  153 */       writeActivationProperty(activation.getProperty(), "property", serializer);
/*      */     }
/*  155 */     if (activation.getFile() != null)
/*      */     {
/*  157 */       writeActivationFile(activation.getFile(), "file", serializer);
/*      */     }
/*  159 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeActivationFile(ActivationFile activationFile, String tagName, XmlSerializer serializer) throws IOException {
/*  173 */     serializer.startTag(NAMESPACE, tagName);
/*  174 */     if (activationFile.getMissing() != null)
/*      */     {
/*  176 */       serializer.startTag(NAMESPACE, "missing").text(activationFile.getMissing()).endTag(NAMESPACE, "missing");
/*      */     }
/*  178 */     if (activationFile.getExists() != null)
/*      */     {
/*  180 */       serializer.startTag(NAMESPACE, "exists").text(activationFile.getExists()).endTag(NAMESPACE, "exists");
/*      */     }
/*  182 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeActivationOS(ActivationOS activationOS, String tagName, XmlSerializer serializer) throws IOException {
/*  196 */     serializer.startTag(NAMESPACE, tagName);
/*  197 */     if (activationOS.getName() != null)
/*      */     {
/*  199 */       serializer.startTag(NAMESPACE, "name").text(activationOS.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  201 */     if (activationOS.getFamily() != null)
/*      */     {
/*  203 */       serializer.startTag(NAMESPACE, "family").text(activationOS.getFamily()).endTag(NAMESPACE, "family");
/*      */     }
/*  205 */     if (activationOS.getArch() != null)
/*      */     {
/*  207 */       serializer.startTag(NAMESPACE, "arch").text(activationOS.getArch()).endTag(NAMESPACE, "arch");
/*      */     }
/*  209 */     if (activationOS.getVersion() != null)
/*      */     {
/*  211 */       serializer.startTag(NAMESPACE, "version").text(activationOS.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/*  213 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeActivationProperty(ActivationProperty activationProperty, String tagName, XmlSerializer serializer) throws IOException {
/*  227 */     serializer.startTag(NAMESPACE, tagName);
/*  228 */     if (activationProperty.getName() != null)
/*      */     {
/*  230 */       serializer.startTag(NAMESPACE, "name").text(activationProperty.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  232 */     if (activationProperty.getValue() != null)
/*      */     {
/*  234 */       serializer.startTag(NAMESPACE, "value").text(activationProperty.getValue()).endTag(NAMESPACE, "value");
/*      */     }
/*  236 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeBuild(Build build, String tagName, XmlSerializer serializer) throws IOException {
/*  250 */     serializer.startTag(NAMESPACE, tagName);
/*  251 */     if (build.getSourceDirectory() != null)
/*      */     {
/*  253 */       serializer.startTag(NAMESPACE, "sourceDirectory").text(build.getSourceDirectory()).endTag(NAMESPACE, "sourceDirectory");
/*      */     }
/*  255 */     if (build.getScriptSourceDirectory() != null)
/*      */     {
/*  257 */       serializer.startTag(NAMESPACE, "scriptSourceDirectory").text(build.getScriptSourceDirectory()).endTag(NAMESPACE, "scriptSourceDirectory");
/*      */     }
/*  259 */     if (build.getTestSourceDirectory() != null)
/*      */     {
/*  261 */       serializer.startTag(NAMESPACE, "testSourceDirectory").text(build.getTestSourceDirectory()).endTag(NAMESPACE, "testSourceDirectory");
/*      */     }
/*  263 */     if (build.getOutputDirectory() != null)
/*      */     {
/*  265 */       serializer.startTag(NAMESPACE, "outputDirectory").text(build.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
/*      */     }
/*  267 */     if (build.getTestOutputDirectory() != null)
/*      */     {
/*  269 */       serializer.startTag(NAMESPACE, "testOutputDirectory").text(build.getTestOutputDirectory()).endTag(NAMESPACE, "testOutputDirectory");
/*      */     }
/*  271 */     if (build.getExtensions() != null && build.getExtensions().size() > 0) {
/*      */       
/*  273 */       serializer.startTag(NAMESPACE, "extensions");
/*  274 */       for (Iterator<Extension> iter = build.getExtensions().iterator(); iter.hasNext(); ) {
/*      */         
/*  276 */         Extension o = iter.next();
/*  277 */         writeExtension(o, "extension", serializer);
/*      */       } 
/*  279 */       serializer.endTag(NAMESPACE, "extensions");
/*      */     } 
/*  281 */     if (build.getDefaultGoal() != null)
/*      */     {
/*  283 */       serializer.startTag(NAMESPACE, "defaultGoal").text(build.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
/*      */     }
/*  285 */     if (build.getResources() != null && build.getResources().size() > 0) {
/*      */       
/*  287 */       serializer.startTag(NAMESPACE, "resources");
/*  288 */       for (Iterator<Resource> iter = build.getResources().iterator(); iter.hasNext(); ) {
/*      */         
/*  290 */         Resource o = iter.next();
/*  291 */         writeResource(o, "resource", serializer);
/*      */       } 
/*  293 */       serializer.endTag(NAMESPACE, "resources");
/*      */     } 
/*  295 */     if (build.getTestResources() != null && build.getTestResources().size() > 0) {
/*      */       
/*  297 */       serializer.startTag(NAMESPACE, "testResources");
/*  298 */       for (Iterator<Resource> iter = build.getTestResources().iterator(); iter.hasNext(); ) {
/*      */         
/*  300 */         Resource o = iter.next();
/*  301 */         writeResource(o, "testResource", serializer);
/*      */       } 
/*  303 */       serializer.endTag(NAMESPACE, "testResources");
/*      */     } 
/*  305 */     if (build.getDirectory() != null)
/*      */     {
/*  307 */       serializer.startTag(NAMESPACE, "directory").text(build.getDirectory()).endTag(NAMESPACE, "directory");
/*      */     }
/*  309 */     if (build.getFinalName() != null)
/*      */     {
/*  311 */       serializer.startTag(NAMESPACE, "finalName").text(build.getFinalName()).endTag(NAMESPACE, "finalName");
/*      */     }
/*  313 */     if (build.getFilters() != null && build.getFilters().size() > 0) {
/*      */       
/*  315 */       serializer.startTag(NAMESPACE, "filters");
/*  316 */       for (Iterator<String> iter = build.getFilters().iterator(); iter.hasNext(); ) {
/*      */         
/*  318 */         String filter = iter.next();
/*  319 */         serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
/*      */       } 
/*  321 */       serializer.endTag(NAMESPACE, "filters");
/*      */     } 
/*  323 */     if (build.getPluginManagement() != null)
/*      */     {
/*  325 */       writePluginManagement(build.getPluginManagement(), "pluginManagement", serializer);
/*      */     }
/*  327 */     if (build.getPlugins() != null && build.getPlugins().size() > 0) {
/*      */       
/*  329 */       serializer.startTag(NAMESPACE, "plugins");
/*  330 */       for (Iterator<Plugin> iter = build.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/*  332 */         Plugin o = iter.next();
/*  333 */         writePlugin(o, "plugin", serializer);
/*      */       } 
/*  335 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/*  337 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeBuildBase(BuildBase buildBase, String tagName, XmlSerializer serializer) throws IOException {
/*  351 */     serializer.startTag(NAMESPACE, tagName);
/*  352 */     if (buildBase.getDefaultGoal() != null)
/*      */     {
/*  354 */       serializer.startTag(NAMESPACE, "defaultGoal").text(buildBase.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
/*      */     }
/*  356 */     if (buildBase.getResources() != null && buildBase.getResources().size() > 0) {
/*      */       
/*  358 */       serializer.startTag(NAMESPACE, "resources");
/*  359 */       for (Iterator<Resource> iter = buildBase.getResources().iterator(); iter.hasNext(); ) {
/*      */         
/*  361 */         Resource o = iter.next();
/*  362 */         writeResource(o, "resource", serializer);
/*      */       } 
/*  364 */       serializer.endTag(NAMESPACE, "resources");
/*      */     } 
/*  366 */     if (buildBase.getTestResources() != null && buildBase.getTestResources().size() > 0) {
/*      */       
/*  368 */       serializer.startTag(NAMESPACE, "testResources");
/*  369 */       for (Iterator<Resource> iter = buildBase.getTestResources().iterator(); iter.hasNext(); ) {
/*      */         
/*  371 */         Resource o = iter.next();
/*  372 */         writeResource(o, "testResource", serializer);
/*      */       } 
/*  374 */       serializer.endTag(NAMESPACE, "testResources");
/*      */     } 
/*  376 */     if (buildBase.getDirectory() != null)
/*      */     {
/*  378 */       serializer.startTag(NAMESPACE, "directory").text(buildBase.getDirectory()).endTag(NAMESPACE, "directory");
/*      */     }
/*  380 */     if (buildBase.getFinalName() != null)
/*      */     {
/*  382 */       serializer.startTag(NAMESPACE, "finalName").text(buildBase.getFinalName()).endTag(NAMESPACE, "finalName");
/*      */     }
/*  384 */     if (buildBase.getFilters() != null && buildBase.getFilters().size() > 0) {
/*      */       
/*  386 */       serializer.startTag(NAMESPACE, "filters");
/*  387 */       for (Iterator<String> iter = buildBase.getFilters().iterator(); iter.hasNext(); ) {
/*      */         
/*  389 */         String filter = iter.next();
/*  390 */         serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
/*      */       } 
/*  392 */       serializer.endTag(NAMESPACE, "filters");
/*      */     } 
/*  394 */     if (buildBase.getPluginManagement() != null)
/*      */     {
/*  396 */       writePluginManagement(buildBase.getPluginManagement(), "pluginManagement", serializer);
/*      */     }
/*  398 */     if (buildBase.getPlugins() != null && buildBase.getPlugins().size() > 0) {
/*      */       
/*  400 */       serializer.startTag(NAMESPACE, "plugins");
/*  401 */       for (Iterator<Plugin> iter = buildBase.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/*  403 */         Plugin o = iter.next();
/*  404 */         writePlugin(o, "plugin", serializer);
/*      */       } 
/*  406 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/*  408 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeCiManagement(CiManagement ciManagement, String tagName, XmlSerializer serializer) throws IOException {
/*  422 */     serializer.startTag(NAMESPACE, tagName);
/*  423 */     if (ciManagement.getSystem() != null)
/*      */     {
/*  425 */       serializer.startTag(NAMESPACE, "system").text(ciManagement.getSystem()).endTag(NAMESPACE, "system");
/*      */     }
/*  427 */     if (ciManagement.getUrl() != null)
/*      */     {
/*  429 */       serializer.startTag(NAMESPACE, "url").text(ciManagement.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  431 */     if (ciManagement.getNotifiers() != null && ciManagement.getNotifiers().size() > 0) {
/*      */       
/*  433 */       serializer.startTag(NAMESPACE, "notifiers");
/*  434 */       for (Iterator<Notifier> iter = ciManagement.getNotifiers().iterator(); iter.hasNext(); ) {
/*      */         
/*  436 */         Notifier o = iter.next();
/*  437 */         writeNotifier(o, "notifier", serializer);
/*      */       } 
/*  439 */       serializer.endTag(NAMESPACE, "notifiers");
/*      */     } 
/*  441 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeConfigurationContainer(ConfigurationContainer configurationContainer, String tagName, XmlSerializer serializer) throws IOException {
/*  455 */     serializer.startTag(NAMESPACE, tagName);
/*  456 */     if (configurationContainer.getInherited() != null)
/*      */     {
/*  458 */       serializer.startTag(NAMESPACE, "inherited").text(configurationContainer.getInherited()).endTag(NAMESPACE, "inherited");
/*      */     }
/*  460 */     if (configurationContainer.getConfiguration() != null)
/*      */     {
/*  462 */       ((Xpp3Dom)configurationContainer.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/*  464 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeContributor(Contributor contributor, String tagName, XmlSerializer serializer) throws IOException {
/*  478 */     serializer.startTag(NAMESPACE, tagName);
/*  479 */     if (contributor.getName() != null)
/*      */     {
/*  481 */       serializer.startTag(NAMESPACE, "name").text(contributor.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  483 */     if (contributor.getEmail() != null)
/*      */     {
/*  485 */       serializer.startTag(NAMESPACE, "email").text(contributor.getEmail()).endTag(NAMESPACE, "email");
/*      */     }
/*  487 */     if (contributor.getUrl() != null)
/*      */     {
/*  489 */       serializer.startTag(NAMESPACE, "url").text(contributor.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  491 */     if (contributor.getOrganization() != null)
/*      */     {
/*  493 */       serializer.startTag(NAMESPACE, "organization").text(contributor.getOrganization()).endTag(NAMESPACE, "organization");
/*      */     }
/*  495 */     if (contributor.getOrganizationUrl() != null)
/*      */     {
/*  497 */       serializer.startTag(NAMESPACE, "organizationUrl").text(contributor.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
/*      */     }
/*  499 */     if (contributor.getRoles() != null && contributor.getRoles().size() > 0) {
/*      */       
/*  501 */       serializer.startTag(NAMESPACE, "roles");
/*  502 */       for (Iterator<String> iter = contributor.getRoles().iterator(); iter.hasNext(); ) {
/*      */         
/*  504 */         String role = iter.next();
/*  505 */         serializer.startTag(NAMESPACE, "role").text(role).endTag(NAMESPACE, "role");
/*      */       } 
/*  507 */       serializer.endTag(NAMESPACE, "roles");
/*      */     } 
/*  509 */     if (contributor.getTimezone() != null)
/*      */     {
/*  511 */       serializer.startTag(NAMESPACE, "timezone").text(contributor.getTimezone()).endTag(NAMESPACE, "timezone");
/*      */     }
/*  513 */     if (contributor.getProperties() != null && contributor.getProperties().size() > 0) {
/*      */       
/*  515 */       serializer.startTag(NAMESPACE, "properties");
/*  516 */       for (Iterator<String> iter = contributor.getProperties().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/*  518 */         String key = iter.next();
/*  519 */         String value = (String)contributor.getProperties().get(key);
/*  520 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/*  522 */       serializer.endTag(NAMESPACE, "properties");
/*      */     } 
/*  524 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeDependency(Dependency dependency, String tagName, XmlSerializer serializer) throws IOException {
/*  538 */     serializer.startTag(NAMESPACE, tagName);
/*  539 */     if (dependency.getGroupId() != null)
/*      */     {
/*  541 */       serializer.startTag(NAMESPACE, "groupId").text(dependency.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/*  543 */     if (dependency.getArtifactId() != null)
/*      */     {
/*  545 */       serializer.startTag(NAMESPACE, "artifactId").text(dependency.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/*  547 */     if (dependency.getVersion() != null)
/*      */     {
/*  549 */       serializer.startTag(NAMESPACE, "version").text(dependency.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/*  551 */     if (dependency.getType() != null && !dependency.getType().equals("jar"))
/*      */     {
/*  553 */       serializer.startTag(NAMESPACE, "type").text(dependency.getType()).endTag(NAMESPACE, "type");
/*      */     }
/*  555 */     if (dependency.getClassifier() != null)
/*      */     {
/*  557 */       serializer.startTag(NAMESPACE, "classifier").text(dependency.getClassifier()).endTag(NAMESPACE, "classifier");
/*      */     }
/*  559 */     if (dependency.getScope() != null)
/*      */     {
/*  561 */       serializer.startTag(NAMESPACE, "scope").text(dependency.getScope()).endTag(NAMESPACE, "scope");
/*      */     }
/*  563 */     if (dependency.getSystemPath() != null)
/*      */     {
/*  565 */       serializer.startTag(NAMESPACE, "systemPath").text(dependency.getSystemPath()).endTag(NAMESPACE, "systemPath");
/*      */     }
/*  567 */     if (dependency.getExclusions() != null && dependency.getExclusions().size() > 0) {
/*      */       
/*  569 */       serializer.startTag(NAMESPACE, "exclusions");
/*  570 */       for (Iterator<Exclusion> iter = dependency.getExclusions().iterator(); iter.hasNext(); ) {
/*      */         
/*  572 */         Exclusion o = iter.next();
/*  573 */         writeExclusion(o, "exclusion", serializer);
/*      */       } 
/*  575 */       serializer.endTag(NAMESPACE, "exclusions");
/*      */     } 
/*  577 */     if (dependency.getOptional() != null)
/*      */     {
/*  579 */       serializer.startTag(NAMESPACE, "optional").text(dependency.getOptional()).endTag(NAMESPACE, "optional");
/*      */     }
/*  581 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeDependencyManagement(DependencyManagement dependencyManagement, String tagName, XmlSerializer serializer) throws IOException {
/*  595 */     serializer.startTag(NAMESPACE, tagName);
/*  596 */     if (dependencyManagement.getDependencies() != null && dependencyManagement.getDependencies().size() > 0) {
/*      */       
/*  598 */       serializer.startTag(NAMESPACE, "dependencies");
/*  599 */       for (Iterator<Dependency> iter = dependencyManagement.getDependencies().iterator(); iter.hasNext(); ) {
/*      */         
/*  601 */         Dependency o = iter.next();
/*  602 */         writeDependency(o, "dependency", serializer);
/*      */       } 
/*  604 */       serializer.endTag(NAMESPACE, "dependencies");
/*      */     } 
/*  606 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeDeploymentRepository(DeploymentRepository deploymentRepository, String tagName, XmlSerializer serializer) throws IOException {
/*  620 */     serializer.startTag(NAMESPACE, tagName);
/*  621 */     if (deploymentRepository.isUniqueVersion() != true)
/*      */     {
/*  623 */       serializer.startTag(NAMESPACE, "uniqueVersion").text(String.valueOf(deploymentRepository.isUniqueVersion())).endTag(NAMESPACE, "uniqueVersion");
/*      */     }
/*  625 */     if (deploymentRepository.getReleases() != null)
/*      */     {
/*  627 */       writeRepositoryPolicy(deploymentRepository.getReleases(), "releases", serializer);
/*      */     }
/*  629 */     if (deploymentRepository.getSnapshots() != null)
/*      */     {
/*  631 */       writeRepositoryPolicy(deploymentRepository.getSnapshots(), "snapshots", serializer);
/*      */     }
/*  633 */     if (deploymentRepository.getId() != null)
/*      */     {
/*  635 */       serializer.startTag(NAMESPACE, "id").text(deploymentRepository.getId()).endTag(NAMESPACE, "id");
/*      */     }
/*  637 */     if (deploymentRepository.getName() != null)
/*      */     {
/*  639 */       serializer.startTag(NAMESPACE, "name").text(deploymentRepository.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  641 */     if (deploymentRepository.getUrl() != null)
/*      */     {
/*  643 */       serializer.startTag(NAMESPACE, "url").text(deploymentRepository.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  645 */     if (deploymentRepository.getLayout() != null && !deploymentRepository.getLayout().equals("default"))
/*      */     {
/*  647 */       serializer.startTag(NAMESPACE, "layout").text(deploymentRepository.getLayout()).endTag(NAMESPACE, "layout");
/*      */     }
/*  649 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeDeveloper(Developer developer, String tagName, XmlSerializer serializer) throws IOException {
/*  663 */     serializer.startTag(NAMESPACE, tagName);
/*  664 */     if (developer.getId() != null)
/*      */     {
/*  666 */       serializer.startTag(NAMESPACE, "id").text(developer.getId()).endTag(NAMESPACE, "id");
/*      */     }
/*  668 */     if (developer.getName() != null)
/*      */     {
/*  670 */       serializer.startTag(NAMESPACE, "name").text(developer.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  672 */     if (developer.getEmail() != null)
/*      */     {
/*  674 */       serializer.startTag(NAMESPACE, "email").text(developer.getEmail()).endTag(NAMESPACE, "email");
/*      */     }
/*  676 */     if (developer.getUrl() != null)
/*      */     {
/*  678 */       serializer.startTag(NAMESPACE, "url").text(developer.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  680 */     if (developer.getOrganization() != null)
/*      */     {
/*  682 */       serializer.startTag(NAMESPACE, "organization").text(developer.getOrganization()).endTag(NAMESPACE, "organization");
/*      */     }
/*  684 */     if (developer.getOrganizationUrl() != null)
/*      */     {
/*  686 */       serializer.startTag(NAMESPACE, "organizationUrl").text(developer.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
/*      */     }
/*  688 */     if (developer.getRoles() != null && developer.getRoles().size() > 0) {
/*      */       
/*  690 */       serializer.startTag(NAMESPACE, "roles");
/*  691 */       for (Iterator<String> iter = developer.getRoles().iterator(); iter.hasNext(); ) {
/*      */         
/*  693 */         String role = iter.next();
/*  694 */         serializer.startTag(NAMESPACE, "role").text(role).endTag(NAMESPACE, "role");
/*      */       } 
/*  696 */       serializer.endTag(NAMESPACE, "roles");
/*      */     } 
/*  698 */     if (developer.getTimezone() != null)
/*      */     {
/*  700 */       serializer.startTag(NAMESPACE, "timezone").text(developer.getTimezone()).endTag(NAMESPACE, "timezone");
/*      */     }
/*  702 */     if (developer.getProperties() != null && developer.getProperties().size() > 0) {
/*      */       
/*  704 */       serializer.startTag(NAMESPACE, "properties");
/*  705 */       for (Iterator<String> iter = developer.getProperties().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/*  707 */         String key = iter.next();
/*  708 */         String value = (String)developer.getProperties().get(key);
/*  709 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/*  711 */       serializer.endTag(NAMESPACE, "properties");
/*      */     } 
/*  713 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeDistributionManagement(DistributionManagement distributionManagement, String tagName, XmlSerializer serializer) throws IOException {
/*  727 */     serializer.startTag(NAMESPACE, tagName);
/*  728 */     if (distributionManagement.getRepository() != null)
/*      */     {
/*  730 */       writeDeploymentRepository(distributionManagement.getRepository(), "repository", serializer);
/*      */     }
/*  732 */     if (distributionManagement.getSnapshotRepository() != null)
/*      */     {
/*  734 */       writeDeploymentRepository(distributionManagement.getSnapshotRepository(), "snapshotRepository", serializer);
/*      */     }
/*  736 */     if (distributionManagement.getSite() != null)
/*      */     {
/*  738 */       writeSite(distributionManagement.getSite(), "site", serializer);
/*      */     }
/*  740 */     if (distributionManagement.getDownloadUrl() != null)
/*      */     {
/*  742 */       serializer.startTag(NAMESPACE, "downloadUrl").text(distributionManagement.getDownloadUrl()).endTag(NAMESPACE, "downloadUrl");
/*      */     }
/*  744 */     if (distributionManagement.getRelocation() != null)
/*      */     {
/*  746 */       writeRelocation(distributionManagement.getRelocation(), "relocation", serializer);
/*      */     }
/*  748 */     if (distributionManagement.getStatus() != null)
/*      */     {
/*  750 */       serializer.startTag(NAMESPACE, "status").text(distributionManagement.getStatus()).endTag(NAMESPACE, "status");
/*      */     }
/*  752 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeExclusion(Exclusion exclusion, String tagName, XmlSerializer serializer) throws IOException {
/*  766 */     serializer.startTag(NAMESPACE, tagName);
/*  767 */     if (exclusion.getArtifactId() != null)
/*      */     {
/*  769 */       serializer.startTag(NAMESPACE, "artifactId").text(exclusion.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/*  771 */     if (exclusion.getGroupId() != null)
/*      */     {
/*  773 */       serializer.startTag(NAMESPACE, "groupId").text(exclusion.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/*  775 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeExtension(Extension extension, String tagName, XmlSerializer serializer) throws IOException {
/*  789 */     serializer.startTag(NAMESPACE, tagName);
/*  790 */     if (extension.getGroupId() != null)
/*      */     {
/*  792 */       serializer.startTag(NAMESPACE, "groupId").text(extension.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/*  794 */     if (extension.getArtifactId() != null)
/*      */     {
/*  796 */       serializer.startTag(NAMESPACE, "artifactId").text(extension.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/*  798 */     if (extension.getVersion() != null)
/*      */     {
/*  800 */       serializer.startTag(NAMESPACE, "version").text(extension.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/*  802 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeFileSet(FileSet fileSet, String tagName, XmlSerializer serializer) throws IOException {
/*  816 */     serializer.startTag(NAMESPACE, tagName);
/*  817 */     if (fileSet.getDirectory() != null)
/*      */     {
/*  819 */       serializer.startTag(NAMESPACE, "directory").text(fileSet.getDirectory()).endTag(NAMESPACE, "directory");
/*      */     }
/*  821 */     if (fileSet.getIncludes() != null && fileSet.getIncludes().size() > 0) {
/*      */       
/*  823 */       serializer.startTag(NAMESPACE, "includes");
/*  824 */       for (Iterator<String> iter = fileSet.getIncludes().iterator(); iter.hasNext(); ) {
/*      */         
/*  826 */         String include = iter.next();
/*  827 */         serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
/*      */       } 
/*  829 */       serializer.endTag(NAMESPACE, "includes");
/*      */     } 
/*  831 */     if (fileSet.getExcludes() != null && fileSet.getExcludes().size() > 0) {
/*      */       
/*  833 */       serializer.startTag(NAMESPACE, "excludes");
/*  834 */       for (Iterator<String> iter = fileSet.getExcludes().iterator(); iter.hasNext(); ) {
/*      */         
/*  836 */         String exclude = iter.next();
/*  837 */         serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
/*      */       } 
/*  839 */       serializer.endTag(NAMESPACE, "excludes");
/*      */     } 
/*  841 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeIssueManagement(IssueManagement issueManagement, String tagName, XmlSerializer serializer) throws IOException {
/*  855 */     serializer.startTag(NAMESPACE, tagName);
/*  856 */     if (issueManagement.getSystem() != null)
/*      */     {
/*  858 */       serializer.startTag(NAMESPACE, "system").text(issueManagement.getSystem()).endTag(NAMESPACE, "system");
/*      */     }
/*  860 */     if (issueManagement.getUrl() != null)
/*      */     {
/*  862 */       serializer.startTag(NAMESPACE, "url").text(issueManagement.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  864 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeLicense(License license, String tagName, XmlSerializer serializer) throws IOException {
/*  878 */     serializer.startTag(NAMESPACE, tagName);
/*  879 */     if (license.getName() != null)
/*      */     {
/*  881 */       serializer.startTag(NAMESPACE, "name").text(license.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  883 */     if (license.getUrl() != null)
/*      */     {
/*  885 */       serializer.startTag(NAMESPACE, "url").text(license.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  887 */     if (license.getDistribution() != null)
/*      */     {
/*  889 */       serializer.startTag(NAMESPACE, "distribution").text(license.getDistribution()).endTag(NAMESPACE, "distribution");
/*      */     }
/*  891 */     if (license.getComments() != null)
/*      */     {
/*  893 */       serializer.startTag(NAMESPACE, "comments").text(license.getComments()).endTag(NAMESPACE, "comments");
/*      */     }
/*  895 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeMailingList(MailingList mailingList, String tagName, XmlSerializer serializer) throws IOException {
/*  909 */     serializer.startTag(NAMESPACE, tagName);
/*  910 */     if (mailingList.getName() != null)
/*      */     {
/*  912 */       serializer.startTag(NAMESPACE, "name").text(mailingList.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  914 */     if (mailingList.getSubscribe() != null)
/*      */     {
/*  916 */       serializer.startTag(NAMESPACE, "subscribe").text(mailingList.getSubscribe()).endTag(NAMESPACE, "subscribe");
/*      */     }
/*  918 */     if (mailingList.getUnsubscribe() != null)
/*      */     {
/*  920 */       serializer.startTag(NAMESPACE, "unsubscribe").text(mailingList.getUnsubscribe()).endTag(NAMESPACE, "unsubscribe");
/*      */     }
/*  922 */     if (mailingList.getPost() != null)
/*      */     {
/*  924 */       serializer.startTag(NAMESPACE, "post").text(mailingList.getPost()).endTag(NAMESPACE, "post");
/*      */     }
/*  926 */     if (mailingList.getArchive() != null)
/*      */     {
/*  928 */       serializer.startTag(NAMESPACE, "archive").text(mailingList.getArchive()).endTag(NAMESPACE, "archive");
/*      */     }
/*  930 */     if (mailingList.getOtherArchives() != null && mailingList.getOtherArchives().size() > 0) {
/*      */       
/*  932 */       serializer.startTag(NAMESPACE, "otherArchives");
/*  933 */       for (Iterator<String> iter = mailingList.getOtherArchives().iterator(); iter.hasNext(); ) {
/*      */         
/*  935 */         String otherArchive = iter.next();
/*  936 */         serializer.startTag(NAMESPACE, "otherArchive").text(otherArchive).endTag(NAMESPACE, "otherArchive");
/*      */       } 
/*  938 */       serializer.endTag(NAMESPACE, "otherArchives");
/*      */     } 
/*  940 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeModel(Model model, String tagName, XmlSerializer serializer) throws IOException {
/*  954 */     serializer.setPrefix("", "http://maven.apache.org/POM/4.0.0");
/*  955 */     serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
/*  956 */     serializer.startTag(NAMESPACE, tagName);
/*  957 */     serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
/*  958 */     if (model.getModelVersion() != null)
/*      */     {
/*  960 */       serializer.startTag(NAMESPACE, "modelVersion").text(model.getModelVersion()).endTag(NAMESPACE, "modelVersion");
/*      */     }
/*  962 */     if (model.getParent() != null)
/*      */     {
/*  964 */       writeParent(model.getParent(), "parent", serializer);
/*      */     }
/*  966 */     if (model.getGroupId() != null)
/*      */     {
/*  968 */       serializer.startTag(NAMESPACE, "groupId").text(model.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/*  970 */     if (model.getArtifactId() != null)
/*      */     {
/*  972 */       serializer.startTag(NAMESPACE, "artifactId").text(model.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/*  974 */     if (model.getVersion() != null)
/*      */     {
/*  976 */       serializer.startTag(NAMESPACE, "version").text(model.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/*  978 */     if (model.getPackaging() != null && !model.getPackaging().equals("jar"))
/*      */     {
/*  980 */       serializer.startTag(NAMESPACE, "packaging").text(model.getPackaging()).endTag(NAMESPACE, "packaging");
/*      */     }
/*  982 */     if (model.getName() != null)
/*      */     {
/*  984 */       serializer.startTag(NAMESPACE, "name").text(model.getName()).endTag(NAMESPACE, "name");
/*      */     }
/*  986 */     if (model.getDescription() != null)
/*      */     {
/*  988 */       serializer.startTag(NAMESPACE, "description").text(model.getDescription()).endTag(NAMESPACE, "description");
/*      */     }
/*  990 */     if (model.getUrl() != null)
/*      */     {
/*  992 */       serializer.startTag(NAMESPACE, "url").text(model.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/*  994 */     if (model.getInceptionYear() != null)
/*      */     {
/*  996 */       serializer.startTag(NAMESPACE, "inceptionYear").text(model.getInceptionYear()).endTag(NAMESPACE, "inceptionYear");
/*      */     }
/*  998 */     if (model.getOrganization() != null)
/*      */     {
/* 1000 */       writeOrganization(model.getOrganization(), "organization", serializer);
/*      */     }
/* 1002 */     if (model.getLicenses() != null && model.getLicenses().size() > 0) {
/*      */       
/* 1004 */       serializer.startTag(NAMESPACE, "licenses");
/* 1005 */       for (Iterator<License> iter = model.getLicenses().iterator(); iter.hasNext(); ) {
/*      */         
/* 1007 */         License o = iter.next();
/* 1008 */         writeLicense(o, "license", serializer);
/*      */       } 
/* 1010 */       serializer.endTag(NAMESPACE, "licenses");
/*      */     } 
/* 1012 */     if (model.getDevelopers() != null && model.getDevelopers().size() > 0) {
/*      */       
/* 1014 */       serializer.startTag(NAMESPACE, "developers");
/* 1015 */       for (Iterator<Developer> iter = model.getDevelopers().iterator(); iter.hasNext(); ) {
/*      */         
/* 1017 */         Developer o = iter.next();
/* 1018 */         writeDeveloper(o, "developer", serializer);
/*      */       } 
/* 1020 */       serializer.endTag(NAMESPACE, "developers");
/*      */     } 
/* 1022 */     if (model.getContributors() != null && model.getContributors().size() > 0) {
/*      */       
/* 1024 */       serializer.startTag(NAMESPACE, "contributors");
/* 1025 */       for (Iterator<Contributor> iter = model.getContributors().iterator(); iter.hasNext(); ) {
/*      */         
/* 1027 */         Contributor o = iter.next();
/* 1028 */         writeContributor(o, "contributor", serializer);
/*      */       } 
/* 1030 */       serializer.endTag(NAMESPACE, "contributors");
/*      */     } 
/* 1032 */     if (model.getMailingLists() != null && model.getMailingLists().size() > 0) {
/*      */       
/* 1034 */       serializer.startTag(NAMESPACE, "mailingLists");
/* 1035 */       for (Iterator<MailingList> iter = model.getMailingLists().iterator(); iter.hasNext(); ) {
/*      */         
/* 1037 */         MailingList o = iter.next();
/* 1038 */         writeMailingList(o, "mailingList", serializer);
/*      */       } 
/* 1040 */       serializer.endTag(NAMESPACE, "mailingLists");
/*      */     } 
/* 1042 */     if (model.getPrerequisites() != null)
/*      */     {
/* 1044 */       writePrerequisites(model.getPrerequisites(), "prerequisites", serializer);
/*      */     }
/* 1046 */     if (model.getModules() != null && model.getModules().size() > 0) {
/*      */       
/* 1048 */       serializer.startTag(NAMESPACE, "modules");
/* 1049 */       for (Iterator<String> iter = model.getModules().iterator(); iter.hasNext(); ) {
/*      */         
/* 1051 */         String module = iter.next();
/* 1052 */         serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
/*      */       } 
/* 1054 */       serializer.endTag(NAMESPACE, "modules");
/*      */     } 
/* 1056 */     if (model.getScm() != null)
/*      */     {
/* 1058 */       writeScm(model.getScm(), "scm", serializer);
/*      */     }
/* 1060 */     if (model.getIssueManagement() != null)
/*      */     {
/* 1062 */       writeIssueManagement(model.getIssueManagement(), "issueManagement", serializer);
/*      */     }
/* 1064 */     if (model.getCiManagement() != null)
/*      */     {
/* 1066 */       writeCiManagement(model.getCiManagement(), "ciManagement", serializer);
/*      */     }
/* 1068 */     if (model.getDistributionManagement() != null)
/*      */     {
/* 1070 */       writeDistributionManagement(model.getDistributionManagement(), "distributionManagement", serializer);
/*      */     }
/* 1072 */     if (model.getProperties() != null && model.getProperties().size() > 0) {
/*      */       
/* 1074 */       serializer.startTag(NAMESPACE, "properties");
/* 1075 */       for (Iterator<String> iter = model.getProperties().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/* 1077 */         String key = iter.next();
/* 1078 */         String value = (String)model.getProperties().get(key);
/* 1079 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/* 1081 */       serializer.endTag(NAMESPACE, "properties");
/*      */     } 
/* 1083 */     if (model.getDependencyManagement() != null)
/*      */     {
/* 1085 */       writeDependencyManagement(model.getDependencyManagement(), "dependencyManagement", serializer);
/*      */     }
/* 1087 */     if (model.getDependencies() != null && model.getDependencies().size() > 0) {
/*      */       
/* 1089 */       serializer.startTag(NAMESPACE, "dependencies");
/* 1090 */       for (Iterator<Dependency> iter = model.getDependencies().iterator(); iter.hasNext(); ) {
/*      */         
/* 1092 */         Dependency o = iter.next();
/* 1093 */         writeDependency(o, "dependency", serializer);
/*      */       } 
/* 1095 */       serializer.endTag(NAMESPACE, "dependencies");
/*      */     } 
/* 1097 */     if (model.getRepositories() != null && model.getRepositories().size() > 0) {
/*      */       
/* 1099 */       serializer.startTag(NAMESPACE, "repositories");
/* 1100 */       for (Iterator<Repository> iter = model.getRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1102 */         Repository o = iter.next();
/* 1103 */         writeRepository(o, "repository", serializer);
/*      */       } 
/* 1105 */       serializer.endTag(NAMESPACE, "repositories");
/*      */     } 
/* 1107 */     if (model.getPluginRepositories() != null && model.getPluginRepositories().size() > 0) {
/*      */       
/* 1109 */       serializer.startTag(NAMESPACE, "pluginRepositories");
/* 1110 */       for (Iterator<Repository> iter = model.getPluginRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1112 */         Repository o = iter.next();
/* 1113 */         writeRepository(o, "pluginRepository", serializer);
/*      */       } 
/* 1115 */       serializer.endTag(NAMESPACE, "pluginRepositories");
/*      */     } 
/* 1117 */     if (model.getBuild() != null)
/*      */     {
/* 1119 */       writeBuild(model.getBuild(), "build", serializer);
/*      */     }
/* 1121 */     if (model.getReports() != null)
/*      */     {
/* 1123 */       ((Xpp3Dom)model.getReports()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1125 */     if (model.getReporting() != null)
/*      */     {
/* 1127 */       writeReporting(model.getReporting(), "reporting", serializer);
/*      */     }
/* 1129 */     if (model.getProfiles() != null && model.getProfiles().size() > 0) {
/*      */       
/* 1131 */       serializer.startTag(NAMESPACE, "profiles");
/* 1132 */       for (Iterator<Profile> iter = model.getProfiles().iterator(); iter.hasNext(); ) {
/*      */         
/* 1134 */         Profile o = iter.next();
/* 1135 */         writeProfile(o, "profile", serializer);
/*      */       } 
/* 1137 */       serializer.endTag(NAMESPACE, "profiles");
/*      */     } 
/* 1139 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeModelBase(ModelBase modelBase, String tagName, XmlSerializer serializer) throws IOException {
/* 1153 */     serializer.startTag(NAMESPACE, tagName);
/* 1154 */     if (modelBase.getModules() != null && modelBase.getModules().size() > 0) {
/*      */       
/* 1156 */       serializer.startTag(NAMESPACE, "modules");
/* 1157 */       for (Iterator<String> iter = modelBase.getModules().iterator(); iter.hasNext(); ) {
/*      */         
/* 1159 */         String module = iter.next();
/* 1160 */         serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
/*      */       } 
/* 1162 */       serializer.endTag(NAMESPACE, "modules");
/*      */     } 
/* 1164 */     if (modelBase.getDistributionManagement() != null)
/*      */     {
/* 1166 */       writeDistributionManagement(modelBase.getDistributionManagement(), "distributionManagement", serializer);
/*      */     }
/* 1168 */     if (modelBase.getProperties() != null && modelBase.getProperties().size() > 0) {
/*      */       
/* 1170 */       serializer.startTag(NAMESPACE, "properties");
/* 1171 */       for (Iterator<String> iter = modelBase.getProperties().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/* 1173 */         String key = iter.next();
/* 1174 */         String value = (String)modelBase.getProperties().get(key);
/* 1175 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/* 1177 */       serializer.endTag(NAMESPACE, "properties");
/*      */     } 
/* 1179 */     if (modelBase.getDependencyManagement() != null)
/*      */     {
/* 1181 */       writeDependencyManagement(modelBase.getDependencyManagement(), "dependencyManagement", serializer);
/*      */     }
/* 1183 */     if (modelBase.getDependencies() != null && modelBase.getDependencies().size() > 0) {
/*      */       
/* 1185 */       serializer.startTag(NAMESPACE, "dependencies");
/* 1186 */       for (Iterator<Dependency> iter = modelBase.getDependencies().iterator(); iter.hasNext(); ) {
/*      */         
/* 1188 */         Dependency o = iter.next();
/* 1189 */         writeDependency(o, "dependency", serializer);
/*      */       } 
/* 1191 */       serializer.endTag(NAMESPACE, "dependencies");
/*      */     } 
/* 1193 */     if (modelBase.getRepositories() != null && modelBase.getRepositories().size() > 0) {
/*      */       
/* 1195 */       serializer.startTag(NAMESPACE, "repositories");
/* 1196 */       for (Iterator<Repository> iter = modelBase.getRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1198 */         Repository o = iter.next();
/* 1199 */         writeRepository(o, "repository", serializer);
/*      */       } 
/* 1201 */       serializer.endTag(NAMESPACE, "repositories");
/*      */     } 
/* 1203 */     if (modelBase.getPluginRepositories() != null && modelBase.getPluginRepositories().size() > 0) {
/*      */       
/* 1205 */       serializer.startTag(NAMESPACE, "pluginRepositories");
/* 1206 */       for (Iterator<Repository> iter = modelBase.getPluginRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1208 */         Repository o = iter.next();
/* 1209 */         writeRepository(o, "pluginRepository", serializer);
/*      */       } 
/* 1211 */       serializer.endTag(NAMESPACE, "pluginRepositories");
/*      */     } 
/* 1213 */     if (modelBase.getReports() != null)
/*      */     {
/* 1215 */       ((Xpp3Dom)modelBase.getReports()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1217 */     if (modelBase.getReporting() != null)
/*      */     {
/* 1219 */       writeReporting(modelBase.getReporting(), "reporting", serializer);
/*      */     }
/* 1221 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeNotifier(Notifier notifier, String tagName, XmlSerializer serializer) throws IOException {
/* 1235 */     serializer.startTag(NAMESPACE, tagName);
/* 1236 */     if (notifier.getType() != null && !notifier.getType().equals("mail"))
/*      */     {
/* 1238 */       serializer.startTag(NAMESPACE, "type").text(notifier.getType()).endTag(NAMESPACE, "type");
/*      */     }
/* 1240 */     if (notifier.isSendOnError() != true)
/*      */     {
/* 1242 */       serializer.startTag(NAMESPACE, "sendOnError").text(String.valueOf(notifier.isSendOnError())).endTag(NAMESPACE, "sendOnError");
/*      */     }
/* 1244 */     if (notifier.isSendOnFailure() != true)
/*      */     {
/* 1246 */       serializer.startTag(NAMESPACE, "sendOnFailure").text(String.valueOf(notifier.isSendOnFailure())).endTag(NAMESPACE, "sendOnFailure");
/*      */     }
/* 1248 */     if (notifier.isSendOnSuccess() != true)
/*      */     {
/* 1250 */       serializer.startTag(NAMESPACE, "sendOnSuccess").text(String.valueOf(notifier.isSendOnSuccess())).endTag(NAMESPACE, "sendOnSuccess");
/*      */     }
/* 1252 */     if (notifier.isSendOnWarning() != true)
/*      */     {
/* 1254 */       serializer.startTag(NAMESPACE, "sendOnWarning").text(String.valueOf(notifier.isSendOnWarning())).endTag(NAMESPACE, "sendOnWarning");
/*      */     }
/* 1256 */     if (notifier.getAddress() != null)
/*      */     {
/* 1258 */       serializer.startTag(NAMESPACE, "address").text(notifier.getAddress()).endTag(NAMESPACE, "address");
/*      */     }
/* 1260 */     if (notifier.getConfiguration() != null && notifier.getConfiguration().size() > 0) {
/*      */       
/* 1262 */       serializer.startTag(NAMESPACE, "configuration");
/* 1263 */       for (Iterator<String> iter = notifier.getConfiguration().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/* 1265 */         String key = iter.next();
/* 1266 */         String value = (String)notifier.getConfiguration().get(key);
/* 1267 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/* 1269 */       serializer.endTag(NAMESPACE, "configuration");
/*      */     } 
/* 1271 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeOrganization(Organization organization, String tagName, XmlSerializer serializer) throws IOException {
/* 1285 */     serializer.startTag(NAMESPACE, tagName);
/* 1286 */     if (organization.getName() != null)
/*      */     {
/* 1288 */       serializer.startTag(NAMESPACE, "name").text(organization.getName()).endTag(NAMESPACE, "name");
/*      */     }
/* 1290 */     if (organization.getUrl() != null)
/*      */     {
/* 1292 */       serializer.startTag(NAMESPACE, "url").text(organization.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/* 1294 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeParent(Parent parent, String tagName, XmlSerializer serializer) throws IOException {
/* 1308 */     serializer.startTag(NAMESPACE, tagName);
/* 1309 */     if (parent.getArtifactId() != null)
/*      */     {
/* 1311 */       serializer.startTag(NAMESPACE, "artifactId").text(parent.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/* 1313 */     if (parent.getGroupId() != null)
/*      */     {
/* 1315 */       serializer.startTag(NAMESPACE, "groupId").text(parent.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/* 1317 */     if (parent.getVersion() != null)
/*      */     {
/* 1319 */       serializer.startTag(NAMESPACE, "version").text(parent.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/* 1321 */     if (parent.getRelativePath() != null && !parent.getRelativePath().equals("../pom.xml"))
/*      */     {
/* 1323 */       serializer.startTag(NAMESPACE, "relativePath").text(parent.getRelativePath()).endTag(NAMESPACE, "relativePath");
/*      */     }
/* 1325 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePatternSet(PatternSet patternSet, String tagName, XmlSerializer serializer) throws IOException {
/* 1339 */     serializer.startTag(NAMESPACE, tagName);
/* 1340 */     if (patternSet.getIncludes() != null && patternSet.getIncludes().size() > 0) {
/*      */       
/* 1342 */       serializer.startTag(NAMESPACE, "includes");
/* 1343 */       for (Iterator<String> iter = patternSet.getIncludes().iterator(); iter.hasNext(); ) {
/*      */         
/* 1345 */         String include = iter.next();
/* 1346 */         serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
/*      */       } 
/* 1348 */       serializer.endTag(NAMESPACE, "includes");
/*      */     } 
/* 1350 */     if (patternSet.getExcludes() != null && patternSet.getExcludes().size() > 0) {
/*      */       
/* 1352 */       serializer.startTag(NAMESPACE, "excludes");
/* 1353 */       for (Iterator<String> iter = patternSet.getExcludes().iterator(); iter.hasNext(); ) {
/*      */         
/* 1355 */         String exclude = iter.next();
/* 1356 */         serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
/*      */       } 
/* 1358 */       serializer.endTag(NAMESPACE, "excludes");
/*      */     } 
/* 1360 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePlugin(Plugin plugin, String tagName, XmlSerializer serializer) throws IOException {
/* 1374 */     serializer.startTag(NAMESPACE, tagName);
/* 1375 */     if (plugin.getGroupId() != null && !plugin.getGroupId().equals("org.apache.maven.plugins"))
/*      */     {
/* 1377 */       serializer.startTag(NAMESPACE, "groupId").text(plugin.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/* 1379 */     if (plugin.getArtifactId() != null)
/*      */     {
/* 1381 */       serializer.startTag(NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/* 1383 */     if (plugin.getVersion() != null)
/*      */     {
/* 1385 */       serializer.startTag(NAMESPACE, "version").text(plugin.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/* 1387 */     if (plugin.getExtensions() != null)
/*      */     {
/* 1389 */       serializer.startTag(NAMESPACE, "extensions").text(plugin.getExtensions()).endTag(NAMESPACE, "extensions");
/*      */     }
/* 1391 */     if (plugin.getExecutions() != null && plugin.getExecutions().size() > 0) {
/*      */       
/* 1393 */       serializer.startTag(NAMESPACE, "executions");
/* 1394 */       for (Iterator<PluginExecution> iter = plugin.getExecutions().iterator(); iter.hasNext(); ) {
/*      */         
/* 1396 */         PluginExecution o = iter.next();
/* 1397 */         writePluginExecution(o, "execution", serializer);
/*      */       } 
/* 1399 */       serializer.endTag(NAMESPACE, "executions");
/*      */     } 
/* 1401 */     if (plugin.getDependencies() != null && plugin.getDependencies().size() > 0) {
/*      */       
/* 1403 */       serializer.startTag(NAMESPACE, "dependencies");
/* 1404 */       for (Iterator<Dependency> iter = plugin.getDependencies().iterator(); iter.hasNext(); ) {
/*      */         
/* 1406 */         Dependency o = iter.next();
/* 1407 */         writeDependency(o, "dependency", serializer);
/*      */       } 
/* 1409 */       serializer.endTag(NAMESPACE, "dependencies");
/*      */     } 
/* 1411 */     if (plugin.getGoals() != null)
/*      */     {
/* 1413 */       ((Xpp3Dom)plugin.getGoals()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1415 */     if (plugin.getInherited() != null)
/*      */     {
/* 1417 */       serializer.startTag(NAMESPACE, "inherited").text(plugin.getInherited()).endTag(NAMESPACE, "inherited");
/*      */     }
/* 1419 */     if (plugin.getConfiguration() != null)
/*      */     {
/* 1421 */       ((Xpp3Dom)plugin.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1423 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePluginConfiguration(PluginConfiguration pluginConfiguration, String tagName, XmlSerializer serializer) throws IOException {
/* 1437 */     serializer.startTag(NAMESPACE, tagName);
/* 1438 */     if (pluginConfiguration.getPluginManagement() != null)
/*      */     {
/* 1440 */       writePluginManagement(pluginConfiguration.getPluginManagement(), "pluginManagement", serializer);
/*      */     }
/* 1442 */     if (pluginConfiguration.getPlugins() != null && pluginConfiguration.getPlugins().size() > 0) {
/*      */       
/* 1444 */       serializer.startTag(NAMESPACE, "plugins");
/* 1445 */       for (Iterator<Plugin> iter = pluginConfiguration.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/* 1447 */         Plugin o = iter.next();
/* 1448 */         writePlugin(o, "plugin", serializer);
/*      */       } 
/* 1450 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/* 1452 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePluginContainer(PluginContainer pluginContainer, String tagName, XmlSerializer serializer) throws IOException {
/* 1466 */     serializer.startTag(NAMESPACE, tagName);
/* 1467 */     if (pluginContainer.getPlugins() != null && pluginContainer.getPlugins().size() > 0) {
/*      */       
/* 1469 */       serializer.startTag(NAMESPACE, "plugins");
/* 1470 */       for (Iterator<Plugin> iter = pluginContainer.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/* 1472 */         Plugin o = iter.next();
/* 1473 */         writePlugin(o, "plugin", serializer);
/*      */       } 
/* 1475 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/* 1477 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePluginExecution(PluginExecution pluginExecution, String tagName, XmlSerializer serializer) throws IOException {
/* 1491 */     serializer.startTag(NAMESPACE, tagName);
/* 1492 */     if (pluginExecution.getId() != null && !pluginExecution.getId().equals("default"))
/*      */     {
/* 1494 */       serializer.startTag(NAMESPACE, "id").text(pluginExecution.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 1496 */     if (pluginExecution.getPhase() != null)
/*      */     {
/* 1498 */       serializer.startTag(NAMESPACE, "phase").text(pluginExecution.getPhase()).endTag(NAMESPACE, "phase");
/*      */     }
/* 1500 */     if (pluginExecution.getGoals() != null && pluginExecution.getGoals().size() > 0) {
/*      */       
/* 1502 */       serializer.startTag(NAMESPACE, "goals");
/* 1503 */       for (Iterator<String> iter = pluginExecution.getGoals().iterator(); iter.hasNext(); ) {
/*      */         
/* 1505 */         String goal = iter.next();
/* 1506 */         serializer.startTag(NAMESPACE, "goal").text(goal).endTag(NAMESPACE, "goal");
/*      */       } 
/* 1508 */       serializer.endTag(NAMESPACE, "goals");
/*      */     } 
/* 1510 */     if (pluginExecution.getInherited() != null)
/*      */     {
/* 1512 */       serializer.startTag(NAMESPACE, "inherited").text(pluginExecution.getInherited()).endTag(NAMESPACE, "inherited");
/*      */     }
/* 1514 */     if (pluginExecution.getConfiguration() != null)
/*      */     {
/* 1516 */       ((Xpp3Dom)pluginExecution.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1518 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePluginManagement(PluginManagement pluginManagement, String tagName, XmlSerializer serializer) throws IOException {
/* 1532 */     serializer.startTag(NAMESPACE, tagName);
/* 1533 */     if (pluginManagement.getPlugins() != null && pluginManagement.getPlugins().size() > 0) {
/*      */       
/* 1535 */       serializer.startTag(NAMESPACE, "plugins");
/* 1536 */       for (Iterator<Plugin> iter = pluginManagement.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/* 1538 */         Plugin o = iter.next();
/* 1539 */         writePlugin(o, "plugin", serializer);
/*      */       } 
/* 1541 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/* 1543 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writePrerequisites(Prerequisites prerequisites, String tagName, XmlSerializer serializer) throws IOException {
/* 1557 */     serializer.startTag(NAMESPACE, tagName);
/* 1558 */     if (prerequisites.getMaven() != null && !prerequisites.getMaven().equals("2.0"))
/*      */     {
/* 1560 */       serializer.startTag(NAMESPACE, "maven").text(prerequisites.getMaven()).endTag(NAMESPACE, "maven");
/*      */     }
/* 1562 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeProfile(Profile profile, String tagName, XmlSerializer serializer) throws IOException {
/* 1576 */     serializer.startTag(NAMESPACE, tagName);
/* 1577 */     if (profile.getId() != null && !profile.getId().equals("default"))
/*      */     {
/* 1579 */       serializer.startTag(NAMESPACE, "id").text(profile.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 1581 */     if (profile.getActivation() != null)
/*      */     {
/* 1583 */       writeActivation(profile.getActivation(), "activation", serializer);
/*      */     }
/* 1585 */     if (profile.getBuild() != null)
/*      */     {
/* 1587 */       writeBuildBase(profile.getBuild(), "build", serializer);
/*      */     }
/* 1589 */     if (profile.getModules() != null && profile.getModules().size() > 0) {
/*      */       
/* 1591 */       serializer.startTag(NAMESPACE, "modules");
/* 1592 */       for (Iterator<String> iter = profile.getModules().iterator(); iter.hasNext(); ) {
/*      */         
/* 1594 */         String module = iter.next();
/* 1595 */         serializer.startTag(NAMESPACE, "module").text(module).endTag(NAMESPACE, "module");
/*      */       } 
/* 1597 */       serializer.endTag(NAMESPACE, "modules");
/*      */     } 
/* 1599 */     if (profile.getDistributionManagement() != null)
/*      */     {
/* 1601 */       writeDistributionManagement(profile.getDistributionManagement(), "distributionManagement", serializer);
/*      */     }
/* 1603 */     if (profile.getProperties() != null && profile.getProperties().size() > 0) {
/*      */       
/* 1605 */       serializer.startTag(NAMESPACE, "properties");
/* 1606 */       for (Iterator<String> iter = profile.getProperties().keySet().iterator(); iter.hasNext(); ) {
/*      */         
/* 1608 */         String key = iter.next();
/* 1609 */         String value = (String)profile.getProperties().get(key);
/* 1610 */         serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
/*      */       } 
/* 1612 */       serializer.endTag(NAMESPACE, "properties");
/*      */     } 
/* 1614 */     if (profile.getDependencyManagement() != null)
/*      */     {
/* 1616 */       writeDependencyManagement(profile.getDependencyManagement(), "dependencyManagement", serializer);
/*      */     }
/* 1618 */     if (profile.getDependencies() != null && profile.getDependencies().size() > 0) {
/*      */       
/* 1620 */       serializer.startTag(NAMESPACE, "dependencies");
/* 1621 */       for (Iterator<Dependency> iter = profile.getDependencies().iterator(); iter.hasNext(); ) {
/*      */         
/* 1623 */         Dependency o = iter.next();
/* 1624 */         writeDependency(o, "dependency", serializer);
/*      */       } 
/* 1626 */       serializer.endTag(NAMESPACE, "dependencies");
/*      */     } 
/* 1628 */     if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
/*      */       
/* 1630 */       serializer.startTag(NAMESPACE, "repositories");
/* 1631 */       for (Iterator<Repository> iter = profile.getRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1633 */         Repository o = iter.next();
/* 1634 */         writeRepository(o, "repository", serializer);
/*      */       } 
/* 1636 */       serializer.endTag(NAMESPACE, "repositories");
/*      */     } 
/* 1638 */     if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
/*      */       
/* 1640 */       serializer.startTag(NAMESPACE, "pluginRepositories");
/* 1641 */       for (Iterator<Repository> iter = profile.getPluginRepositories().iterator(); iter.hasNext(); ) {
/*      */         
/* 1643 */         Repository o = iter.next();
/* 1644 */         writeRepository(o, "pluginRepository", serializer);
/*      */       } 
/* 1646 */       serializer.endTag(NAMESPACE, "pluginRepositories");
/*      */     } 
/* 1648 */     if (profile.getReports() != null)
/*      */     {
/* 1650 */       ((Xpp3Dom)profile.getReports()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1652 */     if (profile.getReporting() != null)
/*      */     {
/* 1654 */       writeReporting(profile.getReporting(), "reporting", serializer);
/*      */     }
/* 1656 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeRelocation(Relocation relocation, String tagName, XmlSerializer serializer) throws IOException {
/* 1670 */     serializer.startTag(NAMESPACE, tagName);
/* 1671 */     if (relocation.getGroupId() != null)
/*      */     {
/* 1673 */       serializer.startTag(NAMESPACE, "groupId").text(relocation.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/* 1675 */     if (relocation.getArtifactId() != null)
/*      */     {
/* 1677 */       serializer.startTag(NAMESPACE, "artifactId").text(relocation.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/* 1679 */     if (relocation.getVersion() != null)
/*      */     {
/* 1681 */       serializer.startTag(NAMESPACE, "version").text(relocation.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/* 1683 */     if (relocation.getMessage() != null)
/*      */     {
/* 1685 */       serializer.startTag(NAMESPACE, "message").text(relocation.getMessage()).endTag(NAMESPACE, "message");
/*      */     }
/* 1687 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeReportPlugin(ReportPlugin reportPlugin, String tagName, XmlSerializer serializer) throws IOException {
/* 1701 */     serializer.startTag(NAMESPACE, tagName);
/* 1702 */     if (reportPlugin.getGroupId() != null && !reportPlugin.getGroupId().equals("org.apache.maven.plugins"))
/*      */     {
/* 1704 */       serializer.startTag(NAMESPACE, "groupId").text(reportPlugin.getGroupId()).endTag(NAMESPACE, "groupId");
/*      */     }
/* 1706 */     if (reportPlugin.getArtifactId() != null)
/*      */     {
/* 1708 */       serializer.startTag(NAMESPACE, "artifactId").text(reportPlugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
/*      */     }
/* 1710 */     if (reportPlugin.getVersion() != null)
/*      */     {
/* 1712 */       serializer.startTag(NAMESPACE, "version").text(reportPlugin.getVersion()).endTag(NAMESPACE, "version");
/*      */     }
/* 1714 */     if (reportPlugin.getInherited() != null)
/*      */     {
/* 1716 */       serializer.startTag(NAMESPACE, "inherited").text(reportPlugin.getInherited()).endTag(NAMESPACE, "inherited");
/*      */     }
/* 1718 */     if (reportPlugin.getConfiguration() != null)
/*      */     {
/* 1720 */       ((Xpp3Dom)reportPlugin.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1722 */     if (reportPlugin.getReportSets() != null && reportPlugin.getReportSets().size() > 0) {
/*      */       
/* 1724 */       serializer.startTag(NAMESPACE, "reportSets");
/* 1725 */       for (Iterator<ReportSet> iter = reportPlugin.getReportSets().iterator(); iter.hasNext(); ) {
/*      */         
/* 1727 */         ReportSet o = iter.next();
/* 1728 */         writeReportSet(o, "reportSet", serializer);
/*      */       } 
/* 1730 */       serializer.endTag(NAMESPACE, "reportSets");
/*      */     } 
/* 1732 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeReportSet(ReportSet reportSet, String tagName, XmlSerializer serializer) throws IOException {
/* 1746 */     serializer.startTag(NAMESPACE, tagName);
/* 1747 */     if (reportSet.getId() != null && !reportSet.getId().equals("default"))
/*      */     {
/* 1749 */       serializer.startTag(NAMESPACE, "id").text(reportSet.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 1751 */     if (reportSet.getConfiguration() != null)
/*      */     {
/* 1753 */       ((Xpp3Dom)reportSet.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
/*      */     }
/* 1755 */     if (reportSet.getInherited() != null)
/*      */     {
/* 1757 */       serializer.startTag(NAMESPACE, "inherited").text(reportSet.getInherited()).endTag(NAMESPACE, "inherited");
/*      */     }
/* 1759 */     if (reportSet.getReports() != null && reportSet.getReports().size() > 0) {
/*      */       
/* 1761 */       serializer.startTag(NAMESPACE, "reports");
/* 1762 */       for (Iterator<String> iter = reportSet.getReports().iterator(); iter.hasNext(); ) {
/*      */         
/* 1764 */         String report = iter.next();
/* 1765 */         serializer.startTag(NAMESPACE, "report").text(report).endTag(NAMESPACE, "report");
/*      */       } 
/* 1767 */       serializer.endTag(NAMESPACE, "reports");
/*      */     } 
/* 1769 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeReporting(Reporting reporting, String tagName, XmlSerializer serializer) throws IOException {
/* 1783 */     serializer.startTag(NAMESPACE, tagName);
/* 1784 */     if (reporting.getExcludeDefaults() != null)
/*      */     {
/* 1786 */       serializer.startTag(NAMESPACE, "excludeDefaults").text(reporting.getExcludeDefaults()).endTag(NAMESPACE, "excludeDefaults");
/*      */     }
/* 1788 */     if (reporting.getOutputDirectory() != null)
/*      */     {
/* 1790 */       serializer.startTag(NAMESPACE, "outputDirectory").text(reporting.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
/*      */     }
/* 1792 */     if (reporting.getPlugins() != null && reporting.getPlugins().size() > 0) {
/*      */       
/* 1794 */       serializer.startTag(NAMESPACE, "plugins");
/* 1795 */       for (Iterator<ReportPlugin> iter = reporting.getPlugins().iterator(); iter.hasNext(); ) {
/*      */         
/* 1797 */         ReportPlugin o = iter.next();
/* 1798 */         writeReportPlugin(o, "plugin", serializer);
/*      */       } 
/* 1800 */       serializer.endTag(NAMESPACE, "plugins");
/*      */     } 
/* 1802 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeRepository(Repository repository, String tagName, XmlSerializer serializer) throws IOException {
/* 1816 */     serializer.startTag(NAMESPACE, tagName);
/* 1817 */     if (repository.getReleases() != null)
/*      */     {
/* 1819 */       writeRepositoryPolicy(repository.getReleases(), "releases", serializer);
/*      */     }
/* 1821 */     if (repository.getSnapshots() != null)
/*      */     {
/* 1823 */       writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer);
/*      */     }
/* 1825 */     if (repository.getId() != null)
/*      */     {
/* 1827 */       serializer.startTag(NAMESPACE, "id").text(repository.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 1829 */     if (repository.getName() != null)
/*      */     {
/* 1831 */       serializer.startTag(NAMESPACE, "name").text(repository.getName()).endTag(NAMESPACE, "name");
/*      */     }
/* 1833 */     if (repository.getUrl() != null)
/*      */     {
/* 1835 */       serializer.startTag(NAMESPACE, "url").text(repository.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/* 1837 */     if (repository.getLayout() != null && !repository.getLayout().equals("default"))
/*      */     {
/* 1839 */       serializer.startTag(NAMESPACE, "layout").text(repository.getLayout()).endTag(NAMESPACE, "layout");
/*      */     }
/* 1841 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeRepositoryBase(RepositoryBase repositoryBase, String tagName, XmlSerializer serializer) throws IOException {
/* 1855 */     serializer.startTag(NAMESPACE, tagName);
/* 1856 */     if (repositoryBase.getId() != null)
/*      */     {
/* 1858 */       serializer.startTag(NAMESPACE, "id").text(repositoryBase.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 1860 */     if (repositoryBase.getName() != null)
/*      */     {
/* 1862 */       serializer.startTag(NAMESPACE, "name").text(repositoryBase.getName()).endTag(NAMESPACE, "name");
/*      */     }
/* 1864 */     if (repositoryBase.getUrl() != null)
/*      */     {
/* 1866 */       serializer.startTag(NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/* 1868 */     if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default"))
/*      */     {
/* 1870 */       serializer.startTag(NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(NAMESPACE, "layout");
/*      */     }
/* 1872 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeRepositoryPolicy(RepositoryPolicy repositoryPolicy, String tagName, XmlSerializer serializer) throws IOException {
/* 1886 */     serializer.startTag(NAMESPACE, tagName);
/* 1887 */     if (repositoryPolicy.getEnabled() != null)
/*      */     {
/* 1889 */       serializer.startTag(NAMESPACE, "enabled").text(repositoryPolicy.getEnabled()).endTag(NAMESPACE, "enabled");
/*      */     }
/* 1891 */     if (repositoryPolicy.getUpdatePolicy() != null)
/*      */     {
/* 1893 */       serializer.startTag(NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(NAMESPACE, "updatePolicy");
/*      */     }
/* 1895 */     if (repositoryPolicy.getChecksumPolicy() != null)
/*      */     {
/* 1897 */       serializer.startTag(NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(NAMESPACE, "checksumPolicy");
/*      */     }
/* 1899 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeResource(Resource resource, String tagName, XmlSerializer serializer) throws IOException {
/* 1913 */     serializer.startTag(NAMESPACE, tagName);
/* 1914 */     if (resource.getTargetPath() != null)
/*      */     {
/* 1916 */       serializer.startTag(NAMESPACE, "targetPath").text(resource.getTargetPath()).endTag(NAMESPACE, "targetPath");
/*      */     }
/* 1918 */     if (resource.getFiltering() != null)
/*      */     {
/* 1920 */       serializer.startTag(NAMESPACE, "filtering").text(resource.getFiltering()).endTag(NAMESPACE, "filtering");
/*      */     }
/* 1922 */     if (resource.getMergeId() != null)
/*      */     {
/* 1924 */       serializer.startTag(NAMESPACE, "mergeId").text(resource.getMergeId()).endTag(NAMESPACE, "mergeId");
/*      */     }
/* 1926 */     if (resource.getDirectory() != null)
/*      */     {
/* 1928 */       serializer.startTag(NAMESPACE, "directory").text(resource.getDirectory()).endTag(NAMESPACE, "directory");
/*      */     }
/* 1930 */     if (resource.getIncludes() != null && resource.getIncludes().size() > 0) {
/*      */       
/* 1932 */       serializer.startTag(NAMESPACE, "includes");
/* 1933 */       for (Iterator<String> iter = resource.getIncludes().iterator(); iter.hasNext(); ) {
/*      */         
/* 1935 */         String include = iter.next();
/* 1936 */         serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
/*      */       } 
/* 1938 */       serializer.endTag(NAMESPACE, "includes");
/*      */     } 
/* 1940 */     if (resource.getExcludes() != null && resource.getExcludes().size() > 0) {
/*      */       
/* 1942 */       serializer.startTag(NAMESPACE, "excludes");
/* 1943 */       for (Iterator<String> iter = resource.getExcludes().iterator(); iter.hasNext(); ) {
/*      */         
/* 1945 */         String exclude = iter.next();
/* 1946 */         serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
/*      */       } 
/* 1948 */       serializer.endTag(NAMESPACE, "excludes");
/*      */     } 
/* 1950 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeScm(Scm scm, String tagName, XmlSerializer serializer) throws IOException {
/* 1964 */     serializer.startTag(NAMESPACE, tagName);
/* 1965 */     if (scm.getConnection() != null)
/*      */     {
/* 1967 */       serializer.startTag(NAMESPACE, "connection").text(scm.getConnection()).endTag(NAMESPACE, "connection");
/*      */     }
/* 1969 */     if (scm.getDeveloperConnection() != null)
/*      */     {
/* 1971 */       serializer.startTag(NAMESPACE, "developerConnection").text(scm.getDeveloperConnection()).endTag(NAMESPACE, "developerConnection");
/*      */     }
/* 1973 */     if (scm.getTag() != null && !scm.getTag().equals("HEAD"))
/*      */     {
/* 1975 */       serializer.startTag(NAMESPACE, "tag").text(scm.getTag()).endTag(NAMESPACE, "tag");
/*      */     }
/* 1977 */     if (scm.getUrl() != null)
/*      */     {
/* 1979 */       serializer.startTag(NAMESPACE, "url").text(scm.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/* 1981 */     serializer.endTag(NAMESPACE, tagName);
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
/*      */   private void writeSite(Site site, String tagName, XmlSerializer serializer) throws IOException {
/* 1995 */     serializer.startTag(NAMESPACE, tagName);
/* 1996 */     if (site.getId() != null)
/*      */     {
/* 1998 */       serializer.startTag(NAMESPACE, "id").text(site.getId()).endTag(NAMESPACE, "id");
/*      */     }
/* 2000 */     if (site.getName() != null)
/*      */     {
/* 2002 */       serializer.startTag(NAMESPACE, "name").text(site.getName()).endTag(NAMESPACE, "name");
/*      */     }
/* 2004 */     if (site.getUrl() != null)
/*      */     {
/* 2006 */       serializer.startTag(NAMESPACE, "url").text(site.getUrl()).endTag(NAMESPACE, "url");
/*      */     }
/* 2008 */     serializer.endTag(NAMESPACE, tagName);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\io\xpp3\MavenXpp3Writer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */