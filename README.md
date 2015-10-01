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
		classpath "gradle.plugin.org.jruyi.gradle:dist-gradle-plugin:0.3.2"
	}
}

apply plugin: "org.jruyi.dist"
```

Or for gradle 2.1+

```groovy
plugins {
	id "org.jruyi.dist" version "0.3.2"
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
disruptor_version=3.3.2
jline_version=2.13
log4j_version=2.4
org_apache_felix_configadmin_version=1.8.8
org_apache_felix_framework_version=5.2.0
org_apache_felix_gogo_runtime_version=0.16.2
org_apache_felix_metatype_version=1.1.2
org_apache_felix_scr_version=2.0.2
slf4j_version=1.7.12
jruyi_system_version=2.3.2
jruyi_cli_version=2.0.1
jruyi_launcher_version=2.0.1
org_jruyi_clid_version=2.3.2
org_jruyi_cmd_version=2.0.4
org_jruyi_common_version=2.3.2
org_jruyi_io_version=2.3.2
org_jruyi_osgi_log_version=2.0.2
```

Or define them as extra properties.

```gradle
ext {
	commons_cli_version = '1.3.1'
    disruptor_version = '3.3.2'
    jline_version = '2.13'
    log4j_version = '2.4'
    org_apache_felix_configadmin_version = '1.8.8'
    org_apache_felix_framework_version = '5.2.0'
    org_apache_felix_gogo_runtime_version = '0.16.2'
    org_apache_felix_metatype_version = '1.1.2'
    org_apache_felix_scr_version = '2.0.2'
    slf4j_version = '1.7.12'
    jruyi_system_version = '2.3.2'
    jruyi_cli_version = '2.0.1'
    jruyi_launcher_version = '2.0.1'
    org_jruyi_clid_version = '2.3.2'
    org_jruyi_cmd_version = '2.0.4'
    org_jruyi_common_version = '2.3.2'
    org_jruyi_io_version = '2.3.2'
    org_jruyi_osgi_log_version = '2.0.2'
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
