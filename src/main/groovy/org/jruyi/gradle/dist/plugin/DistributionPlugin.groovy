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
			'jruyiInstHomeDir'                     : 'inst/default',

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

	def makeJarAsDependency(Project project, Task prepareDist) {
		Task jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME);
		if (jar == null)
			return

		prepareDist.dependsOn jar
	}

	private configureProject(Project project) {

		project.allprojects.each { p ->
			PROPS.each { k, v ->
				if (!p.hasProperty(k) && !p.ext.has(k))
					p.ext[k] = v
			}
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

			distTar {
				compression = Compression.GZIP
				extension = 'tar.gz'
			}
		}
	}
}
