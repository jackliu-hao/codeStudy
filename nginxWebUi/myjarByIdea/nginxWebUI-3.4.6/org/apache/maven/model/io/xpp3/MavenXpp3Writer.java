package org.apache.maven.model.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class MavenXpp3Writer {
   private static final String NAMESPACE = null;

   public void write(Writer writer, Model model) throws IOException {
      XmlSerializer serializer = new MXSerializer();
      serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      serializer.setOutput(writer);
      serializer.startDocument(model.getModelEncoding(), (Boolean)null);
      this.writeModel(model, "project", serializer);
      serializer.endDocument();
   }

   public void write(OutputStream stream, Model model) throws IOException {
      XmlSerializer serializer = new MXSerializer();
      serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      serializer.setOutput(stream, model.getModelEncoding());
      serializer.startDocument(model.getModelEncoding(), (Boolean)null);
      this.writeModel(model, "project", serializer);
      serializer.endDocument();
   }

   private void writeActivation(Activation activation, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (activation.isActiveByDefault()) {
         serializer.startTag(NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(NAMESPACE, "activeByDefault");
      }

      if (activation.getJdk() != null) {
         serializer.startTag(NAMESPACE, "jdk").text(activation.getJdk()).endTag(NAMESPACE, "jdk");
      }

      if (activation.getOs() != null) {
         this.writeActivationOS(activation.getOs(), "os", serializer);
      }

      if (activation.getProperty() != null) {
         this.writeActivationProperty(activation.getProperty(), "property", serializer);
      }

      if (activation.getFile() != null) {
         this.writeActivationFile(activation.getFile(), "file", serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeActivationFile(ActivationFile activationFile, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (activationFile.getMissing() != null) {
         serializer.startTag(NAMESPACE, "missing").text(activationFile.getMissing()).endTag(NAMESPACE, "missing");
      }

      if (activationFile.getExists() != null) {
         serializer.startTag(NAMESPACE, "exists").text(activationFile.getExists()).endTag(NAMESPACE, "exists");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeActivationOS(ActivationOS activationOS, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (activationOS.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(activationOS.getName()).endTag(NAMESPACE, "name");
      }

      if (activationOS.getFamily() != null) {
         serializer.startTag(NAMESPACE, "family").text(activationOS.getFamily()).endTag(NAMESPACE, "family");
      }

      if (activationOS.getArch() != null) {
         serializer.startTag(NAMESPACE, "arch").text(activationOS.getArch()).endTag(NAMESPACE, "arch");
      }

      if (activationOS.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(activationOS.getVersion()).endTag(NAMESPACE, "version");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeActivationProperty(ActivationProperty activationProperty, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (activationProperty.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(activationProperty.getName()).endTag(NAMESPACE, "name");
      }

      if (activationProperty.getValue() != null) {
         serializer.startTag(NAMESPACE, "value").text(activationProperty.getValue()).endTag(NAMESPACE, "value");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeBuild(Build build, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (build.getSourceDirectory() != null) {
         serializer.startTag(NAMESPACE, "sourceDirectory").text(build.getSourceDirectory()).endTag(NAMESPACE, "sourceDirectory");
      }

      if (build.getScriptSourceDirectory() != null) {
         serializer.startTag(NAMESPACE, "scriptSourceDirectory").text(build.getScriptSourceDirectory()).endTag(NAMESPACE, "scriptSourceDirectory");
      }

      if (build.getTestSourceDirectory() != null) {
         serializer.startTag(NAMESPACE, "testSourceDirectory").text(build.getTestSourceDirectory()).endTag(NAMESPACE, "testSourceDirectory");
      }

      if (build.getOutputDirectory() != null) {
         serializer.startTag(NAMESPACE, "outputDirectory").text(build.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      }

      if (build.getTestOutputDirectory() != null) {
         serializer.startTag(NAMESPACE, "testOutputDirectory").text(build.getTestOutputDirectory()).endTag(NAMESPACE, "testOutputDirectory");
      }

      Iterator iter;
      if (build.getExtensions() != null && build.getExtensions().size() > 0) {
         serializer.startTag(NAMESPACE, "extensions");
         iter = build.getExtensions().iterator();

         while(iter.hasNext()) {
            Extension o = (Extension)iter.next();
            this.writeExtension(o, "extension", serializer);
         }

         serializer.endTag(NAMESPACE, "extensions");
      }

      if (build.getDefaultGoal() != null) {
         serializer.startTag(NAMESPACE, "defaultGoal").text(build.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      }

      Resource o;
      if (build.getResources() != null && build.getResources().size() > 0) {
         serializer.startTag(NAMESPACE, "resources");
         iter = build.getResources().iterator();

         while(iter.hasNext()) {
            o = (Resource)iter.next();
            this.writeResource(o, "resource", serializer);
         }

         serializer.endTag(NAMESPACE, "resources");
      }

      if (build.getTestResources() != null && build.getTestResources().size() > 0) {
         serializer.startTag(NAMESPACE, "testResources");
         iter = build.getTestResources().iterator();

         while(iter.hasNext()) {
            o = (Resource)iter.next();
            this.writeResource(o, "testResource", serializer);
         }

         serializer.endTag(NAMESPACE, "testResources");
      }

      if (build.getDirectory() != null) {
         serializer.startTag(NAMESPACE, "directory").text(build.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (build.getFinalName() != null) {
         serializer.startTag(NAMESPACE, "finalName").text(build.getFinalName()).endTag(NAMESPACE, "finalName");
      }

      if (build.getFilters() != null && build.getFilters().size() > 0) {
         serializer.startTag(NAMESPACE, "filters");
         iter = build.getFilters().iterator();

         while(iter.hasNext()) {
            String filter = (String)iter.next();
            serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
         }

         serializer.endTag(NAMESPACE, "filters");
      }

      if (build.getPluginManagement() != null) {
         this.writePluginManagement(build.getPluginManagement(), "pluginManagement", serializer);
      }

      if (build.getPlugins() != null && build.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         iter = build.getPlugins().iterator();

         while(iter.hasNext()) {
            Plugin o = (Plugin)iter.next();
            this.writePlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeBuildBase(BuildBase buildBase, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (buildBase.getDefaultGoal() != null) {
         serializer.startTag(NAMESPACE, "defaultGoal").text(buildBase.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      }

      Iterator iter;
      Resource o;
      if (buildBase.getResources() != null && buildBase.getResources().size() > 0) {
         serializer.startTag(NAMESPACE, "resources");
         iter = buildBase.getResources().iterator();

         while(iter.hasNext()) {
            o = (Resource)iter.next();
            this.writeResource(o, "resource", serializer);
         }

         serializer.endTag(NAMESPACE, "resources");
      }

      if (buildBase.getTestResources() != null && buildBase.getTestResources().size() > 0) {
         serializer.startTag(NAMESPACE, "testResources");
         iter = buildBase.getTestResources().iterator();

         while(iter.hasNext()) {
            o = (Resource)iter.next();
            this.writeResource(o, "testResource", serializer);
         }

         serializer.endTag(NAMESPACE, "testResources");
      }

      if (buildBase.getDirectory() != null) {
         serializer.startTag(NAMESPACE, "directory").text(buildBase.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (buildBase.getFinalName() != null) {
         serializer.startTag(NAMESPACE, "finalName").text(buildBase.getFinalName()).endTag(NAMESPACE, "finalName");
      }

      if (buildBase.getFilters() != null && buildBase.getFilters().size() > 0) {
         serializer.startTag(NAMESPACE, "filters");
         iter = buildBase.getFilters().iterator();

         while(iter.hasNext()) {
            String filter = (String)iter.next();
            serializer.startTag(NAMESPACE, "filter").text(filter).endTag(NAMESPACE, "filter");
         }

         serializer.endTag(NAMESPACE, "filters");
      }

      if (buildBase.getPluginManagement() != null) {
         this.writePluginManagement(buildBase.getPluginManagement(), "pluginManagement", serializer);
      }

      if (buildBase.getPlugins() != null && buildBase.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         iter = buildBase.getPlugins().iterator();

         while(iter.hasNext()) {
            Plugin o = (Plugin)iter.next();
            this.writePlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeCiManagement(CiManagement ciManagement, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (ciManagement.getSystem() != null) {
         serializer.startTag(NAMESPACE, "system").text(ciManagement.getSystem()).endTag(NAMESPACE, "system");
      }

      if (ciManagement.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(ciManagement.getUrl()).endTag(NAMESPACE, "url");
      }

      if (ciManagement.getNotifiers() != null && ciManagement.getNotifiers().size() > 0) {
         serializer.startTag(NAMESPACE, "notifiers");
         Iterator iter = ciManagement.getNotifiers().iterator();

         while(iter.hasNext()) {
            Notifier o = (Notifier)iter.next();
            this.writeNotifier(o, "notifier", serializer);
         }

         serializer.endTag(NAMESPACE, "notifiers");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeConfigurationContainer(ConfigurationContainer configurationContainer, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (configurationContainer.getInherited() != null) {
         serializer.startTag(NAMESPACE, "inherited").text(configurationContainer.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (configurationContainer.getConfiguration() != null) {
         ((Xpp3Dom)configurationContainer.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeContributor(Contributor contributor, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (contributor.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(contributor.getName()).endTag(NAMESPACE, "name");
      }

      if (contributor.getEmail() != null) {
         serializer.startTag(NAMESPACE, "email").text(contributor.getEmail()).endTag(NAMESPACE, "email");
      }

      if (contributor.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(contributor.getUrl()).endTag(NAMESPACE, "url");
      }

      if (contributor.getOrganization() != null) {
         serializer.startTag(NAMESPACE, "organization").text(contributor.getOrganization()).endTag(NAMESPACE, "organization");
      }

      if (contributor.getOrganizationUrl() != null) {
         serializer.startTag(NAMESPACE, "organizationUrl").text(contributor.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      }

      Iterator iter;
      String key;
      if (contributor.getRoles() != null && contributor.getRoles().size() > 0) {
         serializer.startTag(NAMESPACE, "roles");
         iter = contributor.getRoles().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            serializer.startTag(NAMESPACE, "role").text(key).endTag(NAMESPACE, "role");
         }

         serializer.endTag(NAMESPACE, "roles");
      }

      if (contributor.getTimezone() != null) {
         serializer.startTag(NAMESPACE, "timezone").text(contributor.getTimezone()).endTag(NAMESPACE, "timezone");
      }

      if (contributor.getProperties() != null && contributor.getProperties().size() > 0) {
         serializer.startTag(NAMESPACE, "properties");
         iter = contributor.getProperties().keySet().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            String value = (String)contributor.getProperties().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "properties");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeDependency(Dependency dependency, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (dependency.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(dependency.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (dependency.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(dependency.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (dependency.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(dependency.getVersion()).endTag(NAMESPACE, "version");
      }

      if (dependency.getType() != null && !dependency.getType().equals("jar")) {
         serializer.startTag(NAMESPACE, "type").text(dependency.getType()).endTag(NAMESPACE, "type");
      }

      if (dependency.getClassifier() != null) {
         serializer.startTag(NAMESPACE, "classifier").text(dependency.getClassifier()).endTag(NAMESPACE, "classifier");
      }

      if (dependency.getScope() != null) {
         serializer.startTag(NAMESPACE, "scope").text(dependency.getScope()).endTag(NAMESPACE, "scope");
      }

      if (dependency.getSystemPath() != null) {
         serializer.startTag(NAMESPACE, "systemPath").text(dependency.getSystemPath()).endTag(NAMESPACE, "systemPath");
      }

      if (dependency.getExclusions() != null && dependency.getExclusions().size() > 0) {
         serializer.startTag(NAMESPACE, "exclusions");
         Iterator iter = dependency.getExclusions().iterator();

         while(iter.hasNext()) {
            Exclusion o = (Exclusion)iter.next();
            this.writeExclusion(o, "exclusion", serializer);
         }

         serializer.endTag(NAMESPACE, "exclusions");
      }

      if (dependency.getOptional() != null) {
         serializer.startTag(NAMESPACE, "optional").text(dependency.getOptional()).endTag(NAMESPACE, "optional");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeDependencyManagement(DependencyManagement dependencyManagement, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (dependencyManagement.getDependencies() != null && dependencyManagement.getDependencies().size() > 0) {
         serializer.startTag(NAMESPACE, "dependencies");
         Iterator iter = dependencyManagement.getDependencies().iterator();

         while(iter.hasNext()) {
            Dependency o = (Dependency)iter.next();
            this.writeDependency(o, "dependency", serializer);
         }

         serializer.endTag(NAMESPACE, "dependencies");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeDeploymentRepository(DeploymentRepository deploymentRepository, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (!deploymentRepository.isUniqueVersion()) {
         serializer.startTag(NAMESPACE, "uniqueVersion").text(String.valueOf(deploymentRepository.isUniqueVersion())).endTag(NAMESPACE, "uniqueVersion");
      }

      if (deploymentRepository.getReleases() != null) {
         this.writeRepositoryPolicy(deploymentRepository.getReleases(), "releases", serializer);
      }

      if (deploymentRepository.getSnapshots() != null) {
         this.writeRepositoryPolicy(deploymentRepository.getSnapshots(), "snapshots", serializer);
      }

      if (deploymentRepository.getId() != null) {
         serializer.startTag(NAMESPACE, "id").text(deploymentRepository.getId()).endTag(NAMESPACE, "id");
      }

      if (deploymentRepository.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(deploymentRepository.getName()).endTag(NAMESPACE, "name");
      }

      if (deploymentRepository.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(deploymentRepository.getUrl()).endTag(NAMESPACE, "url");
      }

      if (deploymentRepository.getLayout() != null && !deploymentRepository.getLayout().equals("default")) {
         serializer.startTag(NAMESPACE, "layout").text(deploymentRepository.getLayout()).endTag(NAMESPACE, "layout");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeDeveloper(Developer developer, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (developer.getId() != null) {
         serializer.startTag(NAMESPACE, "id").text(developer.getId()).endTag(NAMESPACE, "id");
      }

      if (developer.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(developer.getName()).endTag(NAMESPACE, "name");
      }

      if (developer.getEmail() != null) {
         serializer.startTag(NAMESPACE, "email").text(developer.getEmail()).endTag(NAMESPACE, "email");
      }

      if (developer.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(developer.getUrl()).endTag(NAMESPACE, "url");
      }

      if (developer.getOrganization() != null) {
         serializer.startTag(NAMESPACE, "organization").text(developer.getOrganization()).endTag(NAMESPACE, "organization");
      }

      if (developer.getOrganizationUrl() != null) {
         serializer.startTag(NAMESPACE, "organizationUrl").text(developer.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      }

      Iterator iter;
      String key;
      if (developer.getRoles() != null && developer.getRoles().size() > 0) {
         serializer.startTag(NAMESPACE, "roles");
         iter = developer.getRoles().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            serializer.startTag(NAMESPACE, "role").text(key).endTag(NAMESPACE, "role");
         }

         serializer.endTag(NAMESPACE, "roles");
      }

      if (developer.getTimezone() != null) {
         serializer.startTag(NAMESPACE, "timezone").text(developer.getTimezone()).endTag(NAMESPACE, "timezone");
      }

      if (developer.getProperties() != null && developer.getProperties().size() > 0) {
         serializer.startTag(NAMESPACE, "properties");
         iter = developer.getProperties().keySet().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            String value = (String)developer.getProperties().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "properties");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeDistributionManagement(DistributionManagement distributionManagement, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (distributionManagement.getRepository() != null) {
         this.writeDeploymentRepository(distributionManagement.getRepository(), "repository", serializer);
      }

      if (distributionManagement.getSnapshotRepository() != null) {
         this.writeDeploymentRepository(distributionManagement.getSnapshotRepository(), "snapshotRepository", serializer);
      }

      if (distributionManagement.getSite() != null) {
         this.writeSite(distributionManagement.getSite(), "site", serializer);
      }

      if (distributionManagement.getDownloadUrl() != null) {
         serializer.startTag(NAMESPACE, "downloadUrl").text(distributionManagement.getDownloadUrl()).endTag(NAMESPACE, "downloadUrl");
      }

      if (distributionManagement.getRelocation() != null) {
         this.writeRelocation(distributionManagement.getRelocation(), "relocation", serializer);
      }

      if (distributionManagement.getStatus() != null) {
         serializer.startTag(NAMESPACE, "status").text(distributionManagement.getStatus()).endTag(NAMESPACE, "status");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeExclusion(Exclusion exclusion, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (exclusion.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(exclusion.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (exclusion.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(exclusion.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeExtension(Extension extension, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (extension.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(extension.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (extension.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(extension.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (extension.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(extension.getVersion()).endTag(NAMESPACE, "version");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeFileSet(FileSet fileSet, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (fileSet.getDirectory() != null) {
         serializer.startTag(NAMESPACE, "directory").text(fileSet.getDirectory()).endTag(NAMESPACE, "directory");
      }

      Iterator iter;
      String exclude;
      if (fileSet.getIncludes() != null && fileSet.getIncludes().size() > 0) {
         serializer.startTag(NAMESPACE, "includes");
         iter = fileSet.getIncludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "include").text(exclude).endTag(NAMESPACE, "include");
         }

         serializer.endTag(NAMESPACE, "includes");
      }

      if (fileSet.getExcludes() != null && fileSet.getExcludes().size() > 0) {
         serializer.startTag(NAMESPACE, "excludes");
         iter = fileSet.getExcludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
         }

         serializer.endTag(NAMESPACE, "excludes");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeIssueManagement(IssueManagement issueManagement, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (issueManagement.getSystem() != null) {
         serializer.startTag(NAMESPACE, "system").text(issueManagement.getSystem()).endTag(NAMESPACE, "system");
      }

      if (issueManagement.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(issueManagement.getUrl()).endTag(NAMESPACE, "url");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeLicense(License license, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (license.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(license.getName()).endTag(NAMESPACE, "name");
      }

      if (license.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(license.getUrl()).endTag(NAMESPACE, "url");
      }

      if (license.getDistribution() != null) {
         serializer.startTag(NAMESPACE, "distribution").text(license.getDistribution()).endTag(NAMESPACE, "distribution");
      }

      if (license.getComments() != null) {
         serializer.startTag(NAMESPACE, "comments").text(license.getComments()).endTag(NAMESPACE, "comments");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeMailingList(MailingList mailingList, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (mailingList.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(mailingList.getName()).endTag(NAMESPACE, "name");
      }

      if (mailingList.getSubscribe() != null) {
         serializer.startTag(NAMESPACE, "subscribe").text(mailingList.getSubscribe()).endTag(NAMESPACE, "subscribe");
      }

      if (mailingList.getUnsubscribe() != null) {
         serializer.startTag(NAMESPACE, "unsubscribe").text(mailingList.getUnsubscribe()).endTag(NAMESPACE, "unsubscribe");
      }

      if (mailingList.getPost() != null) {
         serializer.startTag(NAMESPACE, "post").text(mailingList.getPost()).endTag(NAMESPACE, "post");
      }

      if (mailingList.getArchive() != null) {
         serializer.startTag(NAMESPACE, "archive").text(mailingList.getArchive()).endTag(NAMESPACE, "archive");
      }

      if (mailingList.getOtherArchives() != null && mailingList.getOtherArchives().size() > 0) {
         serializer.startTag(NAMESPACE, "otherArchives");
         Iterator iter = mailingList.getOtherArchives().iterator();

         while(iter.hasNext()) {
            String otherArchive = (String)iter.next();
            serializer.startTag(NAMESPACE, "otherArchive").text(otherArchive).endTag(NAMESPACE, "otherArchive");
         }

         serializer.endTag(NAMESPACE, "otherArchives");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeModel(Model model, String tagName, XmlSerializer serializer) throws IOException {
      serializer.setPrefix("", "http://maven.apache.org/POM/4.0.0");
      serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      serializer.startTag(NAMESPACE, tagName);
      serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
      if (model.getModelVersion() != null) {
         serializer.startTag(NAMESPACE, "modelVersion").text(model.getModelVersion()).endTag(NAMESPACE, "modelVersion");
      }

      if (model.getParent() != null) {
         this.writeParent(model.getParent(), "parent", serializer);
      }

      if (model.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(model.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (model.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(model.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (model.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(model.getVersion()).endTag(NAMESPACE, "version");
      }

      if (model.getPackaging() != null && !model.getPackaging().equals("jar")) {
         serializer.startTag(NAMESPACE, "packaging").text(model.getPackaging()).endTag(NAMESPACE, "packaging");
      }

      if (model.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(model.getName()).endTag(NAMESPACE, "name");
      }

      if (model.getDescription() != null) {
         serializer.startTag(NAMESPACE, "description").text(model.getDescription()).endTag(NAMESPACE, "description");
      }

      if (model.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(model.getUrl()).endTag(NAMESPACE, "url");
      }

      if (model.getInceptionYear() != null) {
         serializer.startTag(NAMESPACE, "inceptionYear").text(model.getInceptionYear()).endTag(NAMESPACE, "inceptionYear");
      }

      if (model.getOrganization() != null) {
         this.writeOrganization(model.getOrganization(), "organization", serializer);
      }

      Iterator iter;
      if (model.getLicenses() != null && model.getLicenses().size() > 0) {
         serializer.startTag(NAMESPACE, "licenses");
         iter = model.getLicenses().iterator();

         while(iter.hasNext()) {
            License o = (License)iter.next();
            this.writeLicense(o, "license", serializer);
         }

         serializer.endTag(NAMESPACE, "licenses");
      }

      if (model.getDevelopers() != null && model.getDevelopers().size() > 0) {
         serializer.startTag(NAMESPACE, "developers");
         iter = model.getDevelopers().iterator();

         while(iter.hasNext()) {
            Developer o = (Developer)iter.next();
            this.writeDeveloper(o, "developer", serializer);
         }

         serializer.endTag(NAMESPACE, "developers");
      }

      if (model.getContributors() != null && model.getContributors().size() > 0) {
         serializer.startTag(NAMESPACE, "contributors");
         iter = model.getContributors().iterator();

         while(iter.hasNext()) {
            Contributor o = (Contributor)iter.next();
            this.writeContributor(o, "contributor", serializer);
         }

         serializer.endTag(NAMESPACE, "contributors");
      }

      if (model.getMailingLists() != null && model.getMailingLists().size() > 0) {
         serializer.startTag(NAMESPACE, "mailingLists");
         iter = model.getMailingLists().iterator();

         while(iter.hasNext()) {
            MailingList o = (MailingList)iter.next();
            this.writeMailingList(o, "mailingList", serializer);
         }

         serializer.endTag(NAMESPACE, "mailingLists");
      }

      if (model.getPrerequisites() != null) {
         this.writePrerequisites(model.getPrerequisites(), "prerequisites", serializer);
      }

      String key;
      if (model.getModules() != null && model.getModules().size() > 0) {
         serializer.startTag(NAMESPACE, "modules");
         iter = model.getModules().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            serializer.startTag(NAMESPACE, "module").text(key).endTag(NAMESPACE, "module");
         }

         serializer.endTag(NAMESPACE, "modules");
      }

      if (model.getScm() != null) {
         this.writeScm(model.getScm(), "scm", serializer);
      }

      if (model.getIssueManagement() != null) {
         this.writeIssueManagement(model.getIssueManagement(), "issueManagement", serializer);
      }

      if (model.getCiManagement() != null) {
         this.writeCiManagement(model.getCiManagement(), "ciManagement", serializer);
      }

      if (model.getDistributionManagement() != null) {
         this.writeDistributionManagement(model.getDistributionManagement(), "distributionManagement", serializer);
      }

      if (model.getProperties() != null && model.getProperties().size() > 0) {
         serializer.startTag(NAMESPACE, "properties");
         iter = model.getProperties().keySet().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            String value = (String)model.getProperties().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "properties");
      }

      if (model.getDependencyManagement() != null) {
         this.writeDependencyManagement(model.getDependencyManagement(), "dependencyManagement", serializer);
      }

      if (model.getDependencies() != null && model.getDependencies().size() > 0) {
         serializer.startTag(NAMESPACE, "dependencies");
         iter = model.getDependencies().iterator();

         while(iter.hasNext()) {
            Dependency o = (Dependency)iter.next();
            this.writeDependency(o, "dependency", serializer);
         }

         serializer.endTag(NAMESPACE, "dependencies");
      }

      Repository o;
      if (model.getRepositories() != null && model.getRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "repositories");
         iter = model.getRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "repository", serializer);
         }

         serializer.endTag(NAMESPACE, "repositories");
      }

      if (model.getPluginRepositories() != null && model.getPluginRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "pluginRepositories");
         iter = model.getPluginRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "pluginRepository", serializer);
         }

         serializer.endTag(NAMESPACE, "pluginRepositories");
      }

      if (model.getBuild() != null) {
         this.writeBuild(model.getBuild(), "build", serializer);
      }

      if (model.getReports() != null) {
         ((Xpp3Dom)model.getReports()).writeToSerializer(NAMESPACE, serializer);
      }

      if (model.getReporting() != null) {
         this.writeReporting(model.getReporting(), "reporting", serializer);
      }

      if (model.getProfiles() != null && model.getProfiles().size() > 0) {
         serializer.startTag(NAMESPACE, "profiles");
         iter = model.getProfiles().iterator();

         while(iter.hasNext()) {
            Profile o = (Profile)iter.next();
            this.writeProfile(o, "profile", serializer);
         }

         serializer.endTag(NAMESPACE, "profiles");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeModelBase(ModelBase modelBase, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      Iterator iter;
      String key;
      if (modelBase.getModules() != null && modelBase.getModules().size() > 0) {
         serializer.startTag(NAMESPACE, "modules");
         iter = modelBase.getModules().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            serializer.startTag(NAMESPACE, "module").text(key).endTag(NAMESPACE, "module");
         }

         serializer.endTag(NAMESPACE, "modules");
      }

      if (modelBase.getDistributionManagement() != null) {
         this.writeDistributionManagement(modelBase.getDistributionManagement(), "distributionManagement", serializer);
      }

      if (modelBase.getProperties() != null && modelBase.getProperties().size() > 0) {
         serializer.startTag(NAMESPACE, "properties");
         iter = modelBase.getProperties().keySet().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            String value = (String)modelBase.getProperties().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "properties");
      }

      if (modelBase.getDependencyManagement() != null) {
         this.writeDependencyManagement(modelBase.getDependencyManagement(), "dependencyManagement", serializer);
      }

      if (modelBase.getDependencies() != null && modelBase.getDependencies().size() > 0) {
         serializer.startTag(NAMESPACE, "dependencies");
         iter = modelBase.getDependencies().iterator();

         while(iter.hasNext()) {
            Dependency o = (Dependency)iter.next();
            this.writeDependency(o, "dependency", serializer);
         }

         serializer.endTag(NAMESPACE, "dependencies");
      }

      Repository o;
      if (modelBase.getRepositories() != null && modelBase.getRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "repositories");
         iter = modelBase.getRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "repository", serializer);
         }

         serializer.endTag(NAMESPACE, "repositories");
      }

      if (modelBase.getPluginRepositories() != null && modelBase.getPluginRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "pluginRepositories");
         iter = modelBase.getPluginRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "pluginRepository", serializer);
         }

         serializer.endTag(NAMESPACE, "pluginRepositories");
      }

      if (modelBase.getReports() != null) {
         ((Xpp3Dom)modelBase.getReports()).writeToSerializer(NAMESPACE, serializer);
      }

      if (modelBase.getReporting() != null) {
         this.writeReporting(modelBase.getReporting(), "reporting", serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeNotifier(Notifier notifier, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (notifier.getType() != null && !notifier.getType().equals("mail")) {
         serializer.startTag(NAMESPACE, "type").text(notifier.getType()).endTag(NAMESPACE, "type");
      }

      if (!notifier.isSendOnError()) {
         serializer.startTag(NAMESPACE, "sendOnError").text(String.valueOf(notifier.isSendOnError())).endTag(NAMESPACE, "sendOnError");
      }

      if (!notifier.isSendOnFailure()) {
         serializer.startTag(NAMESPACE, "sendOnFailure").text(String.valueOf(notifier.isSendOnFailure())).endTag(NAMESPACE, "sendOnFailure");
      }

      if (!notifier.isSendOnSuccess()) {
         serializer.startTag(NAMESPACE, "sendOnSuccess").text(String.valueOf(notifier.isSendOnSuccess())).endTag(NAMESPACE, "sendOnSuccess");
      }

      if (!notifier.isSendOnWarning()) {
         serializer.startTag(NAMESPACE, "sendOnWarning").text(String.valueOf(notifier.isSendOnWarning())).endTag(NAMESPACE, "sendOnWarning");
      }

      if (notifier.getAddress() != null) {
         serializer.startTag(NAMESPACE, "address").text(notifier.getAddress()).endTag(NAMESPACE, "address");
      }

      if (notifier.getConfiguration() != null && notifier.getConfiguration().size() > 0) {
         serializer.startTag(NAMESPACE, "configuration");
         Iterator iter = notifier.getConfiguration().keySet().iterator();

         while(iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)notifier.getConfiguration().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "configuration");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeOrganization(Organization organization, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (organization.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(organization.getName()).endTag(NAMESPACE, "name");
      }

      if (organization.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(organization.getUrl()).endTag(NAMESPACE, "url");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeParent(Parent parent, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (parent.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(parent.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (parent.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(parent.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (parent.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(parent.getVersion()).endTag(NAMESPACE, "version");
      }

      if (parent.getRelativePath() != null && !parent.getRelativePath().equals("../pom.xml")) {
         serializer.startTag(NAMESPACE, "relativePath").text(parent.getRelativePath()).endTag(NAMESPACE, "relativePath");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePatternSet(PatternSet patternSet, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      Iterator iter;
      String exclude;
      if (patternSet.getIncludes() != null && patternSet.getIncludes().size() > 0) {
         serializer.startTag(NAMESPACE, "includes");
         iter = patternSet.getIncludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "include").text(exclude).endTag(NAMESPACE, "include");
         }

         serializer.endTag(NAMESPACE, "includes");
      }

      if (patternSet.getExcludes() != null && patternSet.getExcludes().size() > 0) {
         serializer.startTag(NAMESPACE, "excludes");
         iter = patternSet.getExcludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
         }

         serializer.endTag(NAMESPACE, "excludes");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePlugin(Plugin plugin, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (plugin.getGroupId() != null && !plugin.getGroupId().equals("org.apache.maven.plugins")) {
         serializer.startTag(NAMESPACE, "groupId").text(plugin.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (plugin.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (plugin.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(plugin.getVersion()).endTag(NAMESPACE, "version");
      }

      if (plugin.getExtensions() != null) {
         serializer.startTag(NAMESPACE, "extensions").text(plugin.getExtensions()).endTag(NAMESPACE, "extensions");
      }

      Iterator iter;
      if (plugin.getExecutions() != null && plugin.getExecutions().size() > 0) {
         serializer.startTag(NAMESPACE, "executions");
         iter = plugin.getExecutions().iterator();

         while(iter.hasNext()) {
            PluginExecution o = (PluginExecution)iter.next();
            this.writePluginExecution(o, "execution", serializer);
         }

         serializer.endTag(NAMESPACE, "executions");
      }

      if (plugin.getDependencies() != null && plugin.getDependencies().size() > 0) {
         serializer.startTag(NAMESPACE, "dependencies");
         iter = plugin.getDependencies().iterator();

         while(iter.hasNext()) {
            Dependency o = (Dependency)iter.next();
            this.writeDependency(o, "dependency", serializer);
         }

         serializer.endTag(NAMESPACE, "dependencies");
      }

      if (plugin.getGoals() != null) {
         ((Xpp3Dom)plugin.getGoals()).writeToSerializer(NAMESPACE, serializer);
      }

      if (plugin.getInherited() != null) {
         serializer.startTag(NAMESPACE, "inherited").text(plugin.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (plugin.getConfiguration() != null) {
         ((Xpp3Dom)plugin.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePluginConfiguration(PluginConfiguration pluginConfiguration, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (pluginConfiguration.getPluginManagement() != null) {
         this.writePluginManagement(pluginConfiguration.getPluginManagement(), "pluginManagement", serializer);
      }

      if (pluginConfiguration.getPlugins() != null && pluginConfiguration.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         Iterator iter = pluginConfiguration.getPlugins().iterator();

         while(iter.hasNext()) {
            Plugin o = (Plugin)iter.next();
            this.writePlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePluginContainer(PluginContainer pluginContainer, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (pluginContainer.getPlugins() != null && pluginContainer.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         Iterator iter = pluginContainer.getPlugins().iterator();

         while(iter.hasNext()) {
            Plugin o = (Plugin)iter.next();
            this.writePlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePluginExecution(PluginExecution pluginExecution, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (pluginExecution.getId() != null && !pluginExecution.getId().equals("default")) {
         serializer.startTag(NAMESPACE, "id").text(pluginExecution.getId()).endTag(NAMESPACE, "id");
      }

      if (pluginExecution.getPhase() != null) {
         serializer.startTag(NAMESPACE, "phase").text(pluginExecution.getPhase()).endTag(NAMESPACE, "phase");
      }

      if (pluginExecution.getGoals() != null && pluginExecution.getGoals().size() > 0) {
         serializer.startTag(NAMESPACE, "goals");
         Iterator iter = pluginExecution.getGoals().iterator();

         while(iter.hasNext()) {
            String goal = (String)iter.next();
            serializer.startTag(NAMESPACE, "goal").text(goal).endTag(NAMESPACE, "goal");
         }

         serializer.endTag(NAMESPACE, "goals");
      }

      if (pluginExecution.getInherited() != null) {
         serializer.startTag(NAMESPACE, "inherited").text(pluginExecution.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (pluginExecution.getConfiguration() != null) {
         ((Xpp3Dom)pluginExecution.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePluginManagement(PluginManagement pluginManagement, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (pluginManagement.getPlugins() != null && pluginManagement.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         Iterator iter = pluginManagement.getPlugins().iterator();

         while(iter.hasNext()) {
            Plugin o = (Plugin)iter.next();
            this.writePlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writePrerequisites(Prerequisites prerequisites, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (prerequisites.getMaven() != null && !prerequisites.getMaven().equals("2.0")) {
         serializer.startTag(NAMESPACE, "maven").text(prerequisites.getMaven()).endTag(NAMESPACE, "maven");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeProfile(Profile profile, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (profile.getId() != null && !profile.getId().equals("default")) {
         serializer.startTag(NAMESPACE, "id").text(profile.getId()).endTag(NAMESPACE, "id");
      }

      if (profile.getActivation() != null) {
         this.writeActivation(profile.getActivation(), "activation", serializer);
      }

      if (profile.getBuild() != null) {
         this.writeBuildBase(profile.getBuild(), "build", serializer);
      }

      Iterator iter;
      String key;
      if (profile.getModules() != null && profile.getModules().size() > 0) {
         serializer.startTag(NAMESPACE, "modules");
         iter = profile.getModules().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            serializer.startTag(NAMESPACE, "module").text(key).endTag(NAMESPACE, "module");
         }

         serializer.endTag(NAMESPACE, "modules");
      }

      if (profile.getDistributionManagement() != null) {
         this.writeDistributionManagement(profile.getDistributionManagement(), "distributionManagement", serializer);
      }

      if (profile.getProperties() != null && profile.getProperties().size() > 0) {
         serializer.startTag(NAMESPACE, "properties");
         iter = profile.getProperties().keySet().iterator();

         while(iter.hasNext()) {
            key = (String)iter.next();
            String value = (String)profile.getProperties().get(key);
            serializer.startTag(NAMESPACE, "" + key + "").text(value).endTag(NAMESPACE, "" + key + "");
         }

         serializer.endTag(NAMESPACE, "properties");
      }

      if (profile.getDependencyManagement() != null) {
         this.writeDependencyManagement(profile.getDependencyManagement(), "dependencyManagement", serializer);
      }

      if (profile.getDependencies() != null && profile.getDependencies().size() > 0) {
         serializer.startTag(NAMESPACE, "dependencies");
         iter = profile.getDependencies().iterator();

         while(iter.hasNext()) {
            Dependency o = (Dependency)iter.next();
            this.writeDependency(o, "dependency", serializer);
         }

         serializer.endTag(NAMESPACE, "dependencies");
      }

      Repository o;
      if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "repositories");
         iter = profile.getRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "repository", serializer);
         }

         serializer.endTag(NAMESPACE, "repositories");
      }

      if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
         serializer.startTag(NAMESPACE, "pluginRepositories");
         iter = profile.getPluginRepositories().iterator();

         while(iter.hasNext()) {
            o = (Repository)iter.next();
            this.writeRepository(o, "pluginRepository", serializer);
         }

         serializer.endTag(NAMESPACE, "pluginRepositories");
      }

      if (profile.getReports() != null) {
         ((Xpp3Dom)profile.getReports()).writeToSerializer(NAMESPACE, serializer);
      }

      if (profile.getReporting() != null) {
         this.writeReporting(profile.getReporting(), "reporting", serializer);
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeRelocation(Relocation relocation, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (relocation.getGroupId() != null) {
         serializer.startTag(NAMESPACE, "groupId").text(relocation.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (relocation.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(relocation.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (relocation.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(relocation.getVersion()).endTag(NAMESPACE, "version");
      }

      if (relocation.getMessage() != null) {
         serializer.startTag(NAMESPACE, "message").text(relocation.getMessage()).endTag(NAMESPACE, "message");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeReportPlugin(ReportPlugin reportPlugin, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (reportPlugin.getGroupId() != null && !reportPlugin.getGroupId().equals("org.apache.maven.plugins")) {
         serializer.startTag(NAMESPACE, "groupId").text(reportPlugin.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (reportPlugin.getArtifactId() != null) {
         serializer.startTag(NAMESPACE, "artifactId").text(reportPlugin.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (reportPlugin.getVersion() != null) {
         serializer.startTag(NAMESPACE, "version").text(reportPlugin.getVersion()).endTag(NAMESPACE, "version");
      }

      if (reportPlugin.getInherited() != null) {
         serializer.startTag(NAMESPACE, "inherited").text(reportPlugin.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (reportPlugin.getConfiguration() != null) {
         ((Xpp3Dom)reportPlugin.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
      }

      if (reportPlugin.getReportSets() != null && reportPlugin.getReportSets().size() > 0) {
         serializer.startTag(NAMESPACE, "reportSets");
         Iterator iter = reportPlugin.getReportSets().iterator();

         while(iter.hasNext()) {
            ReportSet o = (ReportSet)iter.next();
            this.writeReportSet(o, "reportSet", serializer);
         }

         serializer.endTag(NAMESPACE, "reportSets");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeReportSet(ReportSet reportSet, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (reportSet.getId() != null && !reportSet.getId().equals("default")) {
         serializer.startTag(NAMESPACE, "id").text(reportSet.getId()).endTag(NAMESPACE, "id");
      }

      if (reportSet.getConfiguration() != null) {
         ((Xpp3Dom)reportSet.getConfiguration()).writeToSerializer(NAMESPACE, serializer);
      }

      if (reportSet.getInherited() != null) {
         serializer.startTag(NAMESPACE, "inherited").text(reportSet.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (reportSet.getReports() != null && reportSet.getReports().size() > 0) {
         serializer.startTag(NAMESPACE, "reports");
         Iterator iter = reportSet.getReports().iterator();

         while(iter.hasNext()) {
            String report = (String)iter.next();
            serializer.startTag(NAMESPACE, "report").text(report).endTag(NAMESPACE, "report");
         }

         serializer.endTag(NAMESPACE, "reports");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeReporting(Reporting reporting, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (reporting.getExcludeDefaults() != null) {
         serializer.startTag(NAMESPACE, "excludeDefaults").text(reporting.getExcludeDefaults()).endTag(NAMESPACE, "excludeDefaults");
      }

      if (reporting.getOutputDirectory() != null) {
         serializer.startTag(NAMESPACE, "outputDirectory").text(reporting.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      }

      if (reporting.getPlugins() != null && reporting.getPlugins().size() > 0) {
         serializer.startTag(NAMESPACE, "plugins");
         Iterator iter = reporting.getPlugins().iterator();

         while(iter.hasNext()) {
            ReportPlugin o = (ReportPlugin)iter.next();
            this.writeReportPlugin(o, "plugin", serializer);
         }

         serializer.endTag(NAMESPACE, "plugins");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeRepository(Repository repository, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (repository.getReleases() != null) {
         this.writeRepositoryPolicy(repository.getReleases(), "releases", serializer);
      }

      if (repository.getSnapshots() != null) {
         this.writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer);
      }

      if (repository.getId() != null) {
         serializer.startTag(NAMESPACE, "id").text(repository.getId()).endTag(NAMESPACE, "id");
      }

      if (repository.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(repository.getName()).endTag(NAMESPACE, "name");
      }

      if (repository.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(repository.getUrl()).endTag(NAMESPACE, "url");
      }

      if (repository.getLayout() != null && !repository.getLayout().equals("default")) {
         serializer.startTag(NAMESPACE, "layout").text(repository.getLayout()).endTag(NAMESPACE, "layout");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeRepositoryBase(RepositoryBase repositoryBase, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (repositoryBase.getId() != null) {
         serializer.startTag(NAMESPACE, "id").text(repositoryBase.getId()).endTag(NAMESPACE, "id");
      }

      if (repositoryBase.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(repositoryBase.getName()).endTag(NAMESPACE, "name");
      }

      if (repositoryBase.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(NAMESPACE, "url");
      }

      if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default")) {
         serializer.startTag(NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(NAMESPACE, "layout");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeRepositoryPolicy(RepositoryPolicy repositoryPolicy, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (repositoryPolicy.getEnabled() != null) {
         serializer.startTag(NAMESPACE, "enabled").text(repositoryPolicy.getEnabled()).endTag(NAMESPACE, "enabled");
      }

      if (repositoryPolicy.getUpdatePolicy() != null) {
         serializer.startTag(NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(NAMESPACE, "updatePolicy");
      }

      if (repositoryPolicy.getChecksumPolicy() != null) {
         serializer.startTag(NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(NAMESPACE, "checksumPolicy");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeResource(Resource resource, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (resource.getTargetPath() != null) {
         serializer.startTag(NAMESPACE, "targetPath").text(resource.getTargetPath()).endTag(NAMESPACE, "targetPath");
      }

      if (resource.getFiltering() != null) {
         serializer.startTag(NAMESPACE, "filtering").text(resource.getFiltering()).endTag(NAMESPACE, "filtering");
      }

      if (resource.getMergeId() != null) {
         serializer.startTag(NAMESPACE, "mergeId").text(resource.getMergeId()).endTag(NAMESPACE, "mergeId");
      }

      if (resource.getDirectory() != null) {
         serializer.startTag(NAMESPACE, "directory").text(resource.getDirectory()).endTag(NAMESPACE, "directory");
      }

      Iterator iter;
      String exclude;
      if (resource.getIncludes() != null && resource.getIncludes().size() > 0) {
         serializer.startTag(NAMESPACE, "includes");
         iter = resource.getIncludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "include").text(exclude).endTag(NAMESPACE, "include");
         }

         serializer.endTag(NAMESPACE, "includes");
      }

      if (resource.getExcludes() != null && resource.getExcludes().size() > 0) {
         serializer.startTag(NAMESPACE, "excludes");
         iter = resource.getExcludes().iterator();

         while(iter.hasNext()) {
            exclude = (String)iter.next();
            serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
         }

         serializer.endTag(NAMESPACE, "excludes");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeScm(Scm scm, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (scm.getConnection() != null) {
         serializer.startTag(NAMESPACE, "connection").text(scm.getConnection()).endTag(NAMESPACE, "connection");
      }

      if (scm.getDeveloperConnection() != null) {
         serializer.startTag(NAMESPACE, "developerConnection").text(scm.getDeveloperConnection()).endTag(NAMESPACE, "developerConnection");
      }

      if (scm.getTag() != null && !scm.getTag().equals("HEAD")) {
         serializer.startTag(NAMESPACE, "tag").text(scm.getTag()).endTag(NAMESPACE, "tag");
      }

      if (scm.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(scm.getUrl()).endTag(NAMESPACE, "url");
      }

      serializer.endTag(NAMESPACE, tagName);
   }

   private void writeSite(Site site, String tagName, XmlSerializer serializer) throws IOException {
      serializer.startTag(NAMESPACE, tagName);
      if (site.getId() != null) {
         serializer.startTag(NAMESPACE, "id").text(site.getId()).endTag(NAMESPACE, "id");
      }

      if (site.getName() != null) {
         serializer.startTag(NAMESPACE, "name").text(site.getName()).endTag(NAMESPACE, "name");
      }

      if (site.getUrl() != null) {
         serializer.startTag(NAMESPACE, "url").text(site.getUrl()).endTag(NAMESPACE, "url");
      }

      serializer.endTag(NAMESPACE, tagName);
   }
}
