package net.bytle.type;

import java.util.HashSet;
import java.util.Set;

/**
 * Java Manifest Attributes
 */
public class ManifestAttributes {

  static public Set<Attribute<?>> ALL = new HashSet<>();

  static public Attribute<String> MANIFEST_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ManifestVersion";
    }

    @Override
    public String getDescription() {
      return "The version of the manifest";
    }


  };

  static public Attribute<String> CONTENT_TYPE = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ContentType";
    }

    @Override
    public String getDescription() {
      return "Bundled extensions can use this attribute to find other JAR files containing needed classes";
    }

  };
  static public Attribute<String> CLASS_PATH = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ClassPath";
    }

    @Override
    public String getDescription() {
      return "??";
    }

  };
  static public Attribute<String> SIGNATURE_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "SignatureVersion";
    }

    @Override
    public String getDescription() {
      return "??";
    }

  };
  static public Attribute<String> MAIN_CLASS = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "MainClass";
    }

    @Override
    public String getDescription() {
      return "used for launching applications packaged in JAR files with the --jar option";
    }

  };
  static public Attribute<String> SEALED = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "Sealed";
    }

    @Override
    public String getDescription() {
      return "for sealing";
    }

  };

  // technotes/guides/extensions/spec.html#dependency
  static public Attribute<String> EXTENSION_LIST = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ExtensionList";
    }

    @Override
    public String getDescription() {
      return "declaring dependencies on installed extensions";
    }

  };

  // technotes/guides/extensions/spec.html#dependency
  static public Attribute<String> EXTENSION_NAME = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ExtensionName";
    }

    @Override
    public String getDescription() {
      return "used for declaring dependencies on installed extensions";
    }

  };
  // See Java Product Versioning Specification, technotes/guides/versioning/spec/versioning2.html#wp90779
  static public Attribute<String> IMPLEMENTATION_TITLE= new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "content-type";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };

  // manifest attribute used for package versioning.
  static public Attribute<String> IMPLEMENTATION_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ImplementationVersion";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };

  // manifest attribute used for package versioning.
  static public Attribute<String> IMPLEMENTATION_VENDOR = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "ImplementationVendor";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };
  // manifest attribute used for package versioning.
  static public Attribute<String> SPECIFICATION_TITLE = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "SpecificationTitle";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };

  // manifest attribute used for package versioning.
  static public Attribute<String> SPECIFICATION_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "SpecificationVersion";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };
  // manifest attribute used for package versioning.
  static public Attribute<String> SPECIFICATION_VENDOR = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "SpecificationVendor";
    }

    @Override
    public String getDescription() {
      return "manifest attribute used for package versioning";
    }

  };

  // The below parameters are not standard
  static public Attribute<String> DESCRIPTION= new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "Description";
    }

    @Override
    public String getDescription() {
      return "The package description";
    }

  };

  static public Attribute<String> PACKAGE_TITLE = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "PackageTitle";
    }

    @Override
    public String getDescription() {
      return "The name of the package";
    }

  };

  static public Attribute<String> PACKAGE_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "PackageVersion";
    }

    @Override
    public String getDescription() {
      return "The version";
    }

  };

  static public Attribute<String> PACKAGE_VENDOR = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "PackageVendor";
    }

    @Override
    public String getDescription() {
      return "The package vendor";
    }

  };

  static public Attribute<String> BUILD_COMMIT = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "BuildCommit";
    }

    @Override
    public String getDescription() {
      return "The build commit";
    }

  };

  static public Attribute<String> BUILD_TIME = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "BuildTime";
    }

    @Override
    public String getDescription() {
      return "The build time";
    }

  };
  static public Attribute<String> BUILD_JAVA_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "BuildJavaVersion";
    }

    @Override
    public String getDescription() {
      return "The build java version";
    }

  };

  static public Attribute<String> BUILD_GRADLE_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "buildGradleVersion";
    }

    @Override
    public String getDescription() {
      return "The build gradle version";
    }

  };

  static public Attribute<String> BUILD_OS_VERSION = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "BuildOsVersion";
    }

    @Override
    public String getDescription() {
      return "The build os version";
    }

  };
  static public Attribute<String> BUILD_OS_NAME = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "buildOsName";
    }

    @Override
    public String getDescription() {
      return "The os name";
    }

  };
  static public Attribute<String> BUILD_OS_ARCH = new ManifestAttribute<>() {

    @Override
    public String getName() {
      return "buildOsArch";
    }

    @Override
    public String getDescription() {
      return "The os architecture";
    }

  };


  static {
    ALL.add(MANIFEST_VERSION);
    ALL.add(CONTENT_TYPE);
    ALL.add(BUILD_TIME);
    ALL.add(BUILD_OS_NAME);
    ALL.add(BUILD_OS_ARCH);
    ALL.add(BUILD_OS_VERSION);
    ALL.add(BUILD_COMMIT);
    ALL.add(BUILD_GRADLE_VERSION);
    ALL.add(BUILD_JAVA_VERSION);
    ALL.add(CLASS_PATH);
    ALL.add(SIGNATURE_VERSION);
    ALL.add(SEALED);
    ALL.add(MAIN_CLASS);
    ALL.add(EXTENSION_NAME);
    ALL.add(EXTENSION_LIST);
    ALL.add(PACKAGE_VENDOR);
    ALL.add(PACKAGE_TITLE);
    ALL.add(PACKAGE_VERSION);
    ALL.add(IMPLEMENTATION_TITLE);
    ALL.add(IMPLEMENTATION_VENDOR);
    ALL.add(IMPLEMENTATION_VERSION);
  }

}
