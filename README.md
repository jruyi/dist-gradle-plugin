## Gradle JRuyi Dist Plugin

Gradle JRuyi Dist Plugin helps to create distribution archives of JRuyi-based applications.

### Usage
To use this plugin, add the following to your build script.

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.org.jruyi.gradle:dist-gradle-plugin:2.5.5"
    }
}

apply plugin: "org.jruyi.dist"
```

Or for gradle 2.1+:

```groovy
plugins {
    id "org.jruyi.dist" version "2.5.5"
}
```

### Implicitly Applied Plugins

Applies the [distribution plugin] (https://docs.gradle.org/current/userguide/distribution_plugin.html).

### Tasks

This plugin adds a task called `prepareDist` which prepares jruyi for distribution.

### Configurations

To customize the distribution, please use the `jruyi` configuration as shown in the following example.

```gradle
jruyi {
    bootstrapPath = null
    log4j2ConfPath = null
    systemScriptPath = null

    packDefaultBins = true
}
```

### Packing Default JARs with Specific Versions

To pack the default JARs with specific versions, please define the version properties in `gradle.properties`.

```INI
commons_cli_version=1.3.1
jline_version=2.14.5
log4j_version=2.8.2
org_apache_felix_configadmin_version=1.8.14
org_apache_felix_framework_version=5.6.4
org_apache_felix_gogo_runtime_version=1.0.6
org_apache_felix_metatype_version=1.1.2
org_apache_felix_scr_version=2.0.12
slf4j_version=1.7.25
jruyi_system_version=2.5.5
jruyi_cli_version=2.0.2
jruyi_launcher_version=2.0.2
org_jruyi_clid_version=2.5.2
org_jruyi_cmd_version=2.0.6
org_jruyi_common_version=2.4.3
org_jruyi_io_version=2.5.5
org_jruyi_osgi_log_version=2.0.3
org_jruyi_tpe_version=2.0.4
```

Or define them as extra properties.

```gradle
ext {
    commons_cli_version = '1.3.1'
    jline_version = '2.14.5'
    log4j_version = '2.8.2'
    org_apache_felix_configadmin_version = '1.8.14'
    org_apache_felix_framework_version = '5.6.4'
    org_apache_felix_gogo_runtime_version = '1.0.6'
    org_apache_felix_metatype_version = '1.1.2'
    org_apache_felix_scr_version = '2.0.12'
    slf4j_version = '1.7.25'
    jruyi_system_version = '2.5.5'
    jruyi_cli_version = '2.0.2'
    jruyi_launcher_version = '2.0.2'
    org_jruyi_clid_version = '2.5.2'
    org_jruyi_cmd_version = '2.0.6'
    org_jruyi_common_version = '2.4.3'
    org_jruyi_io_version = '2.5.5'
    org_jruyi_osgi_log_version = '2.0.3'
    org_jruyi_tpe_version = '2.0.4'
}
```

Thus, the default values of those extra version properties would be overrided.

### Packing Additional JARs

To pack additional JARs, please add them to `dependencies.main`, `dependencies.lib` or `dependencies.bundle` accordingly.

### Packing Additional Files

To pack additional files, please configure the main distribution of the [distribution plugin] (https://docs.gradle.org/current/userguide/distribution_plugin.html).

### Change JRuyi Instance Home Dir for Output

To change JRuyi instance home dir for output, please define `jruyiInstHomeDir` as gradle extra property.

```groovy
ext {
    jruyiInstHomeDir = "inst/default"
}
```

Or define it in gradle.properties.

```INI
jruyiInstHomeDir=inst/default
```

## License

Gradle JRuyi Dist Plugin is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

