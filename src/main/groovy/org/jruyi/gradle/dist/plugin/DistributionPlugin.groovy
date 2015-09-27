/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jruyi.gradle.dist.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Compression

class DistributionPlugin implements Plugin<Project> {

	public static final String PREPARE_DIST_TASK_NAME = 'prepareDist'

	private static final def PROPS = [
			'commons_cli_version'                  : '1.3.1',
			'disruptor_version'                    : '3.3.2',
			'jline_version'                        : '2.13',
			'log4j_version'                        : '2.4',
			'org_apache_felix_configadmin_version' : '1.8.8',
			'org_apache_felix_framework_version'   : '5.2.0',
			'org_apache_felix_gogo_runtime_version': '0.16.2',
			'org_apache_felix_metatype_version'    : '1.1.2',
			'org_apache_felix_scr_version'         : '2.0.2',
			'slf4j_version'                        : '1.7.12',

			'jruyi_system_version'                 : '2.3.2',

			'jruyi_cli_version'                    : '2.0.1',
			'jruyi_launcher_version'               : '2.0.1',
			'org_jruyi_clid_version'               : '2.3.2',
			'org_jruyi_cmd_version'                : '2.0.4',
			'org_jruyi_common_version'             : '2.3.2',
			'org_jruyi_io_version'                 : '2.3.2',
			'org_jruyi_osgi_log_version'           : '2.0.2',
	]

	@Override
	void apply(Project project) {

		configureProject(project)

		project.extensions.create("jruyi", DistributionPluginExtension)
		PrepareDistTask prepareDist = project.tasks.create(PREPARE_DIST_TASK_NAME, PrepareDistTask)

		Task installDist = project.tasks.getByName("installDist")
		installDist.dependsOn prepareDist

		Task distTar = project.tasks.getByName("distTar")
		distTar.dependsOn prepareDist

		if (project.plugins.hasPlugin('java'))
			makeJarAsDependency(project, prepareDist)
		else {
			project.plugins.whenPluginAdded { plugin ->
				if (plugin instanceof JavaPlugin)
					makeJarAsDependency(project, prepareDist)
			}
		}
	}

	private def makeJarAsDependency(Project project, Task prepareDist) {
		Task jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME);
		if (jar == null)
			return

		prepareDist.dependsOn jar
	}

	private configureProject(Project project) {

		PROPS.each { k, v ->
			if (!project.hasProperty(k) && !project.ext.has(k))
				project.ext[k] = v
		}

		project.configure(project) {

			apply plugin: 'distribution'

			repositories {
				jcenter()
			}

			configurations.all {
				transitive = false
			}

			configurations {
				main
				lib
				bundle
			}

			dependencies {
				main "org.jruyi:jruyi-launcher:$jruyi_launcher_version"
				main "org.jruyi:jruyi-cli:$jruyi_cli_version"

				lib "commons-cli:commons-cli:$commons_cli_version"
				lib "com.lmax:disruptor:$disruptor_version"
				lib "jline:jline:$jline_version"
				lib "org.slf4j:slf4j-api:$slf4j_version"
				lib "org.apache.logging.log4j:log4j-api:$log4j_version"
				lib "org.apache.logging.log4j:log4j-core:$log4j_version"
				lib "org.apache.logging.log4j:log4j-slf4j-impl:$log4j_version"

				lib "org.jruyi:jruyi-system:$jruyi_system_version"

				bundle "org.apache.felix:org.apache.felix.configadmin:$org_apache_felix_configadmin_version"
				bundle "org.apache.felix:org.apache.felix.framework:$org_apache_felix_framework_version"
				bundle "org.apache.felix:org.apache.felix.gogo.runtime:$org_apache_felix_gogo_runtime_version"
				bundle "org.apache.felix:org.apache.felix.metatype:$org_apache_felix_metatype_version"
				bundle "org.apache.felix:org.apache.felix.scr:$org_apache_felix_scr_version"

				bundle "org.jruyi:org.jruyi.clid:$org_jruyi_clid_version"
				bundle "org.jruyi:org.jruyi.cmd:$org_jruyi_cmd_version"
				bundle "org.jruyi:org.jruyi.common:$org_jruyi_common_version"
				bundle "org.jruyi:org.jruyi.io:$org_jruyi_io_version"
				bundle "org.jruyi:org.jruyi.osgi.log:$org_jruyi_osgi_log_version"
			}

			distributions {
				main {
					contents {

						from("$buildDir/jruyi/bin") {
							include '**/*.bat'
							into 'bin'
						}
						from("$buildDir/jruyi/bin") {
							exclude '**/*.bat'
							fileMode = 0755
							into 'bin'
						}

						def jars = [:]
						configurations.main.resolvedConfiguration.resolvedArtifacts.each { artifact ->
							jars[artifact.file.name] = "${artifact.name}.${artifact.extension}"
						}
						from(configurations.main) {
							into 'main'
							rename { name -> jars.remove(name) }
						}

						configurations.lib.resolvedConfiguration.resolvedArtifacts.each { artifact ->
							jars[artifact.file.name] = "${artifact.name}.${artifact.extension}"
						}
						from(configurations.lib) {
							into 'lib'
							rename { name -> jars.remove(name) }
						}

						configurations.bundle.resolvedConfiguration.resolvedArtifacts.each { artifact ->
							jars[artifact.file.name] = "${artifact.name}.${artifact.extension}"
						}
						from(configurations.bundle) {
							into 'bundles'
							rename { name -> jars.remove(name) }
						}
					}
				}
			}

			distTar {
				compression = Compression.GZIP
				extension = 'tar.gz'
			}
		}
	}
}
