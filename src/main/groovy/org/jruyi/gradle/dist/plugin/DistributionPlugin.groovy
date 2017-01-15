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
import org.gradle.api.tasks.bundling.Compression

import java.util.regex.Pattern

class DistributionPlugin implements Plugin<Project> {

	public static final String PREPARE_DIST_TASK_NAME = 'prepareDist'

	private static final def PROPS = [
			'jruyiInstHomeDir'                     : 'inst/default',

			'commons_cli_version'                  : '1.3.1',
			'jline_version'                        : '2.14.2',
			'log4j_version'                        : '2.7',
			'org_apache_felix_configadmin_version' : '1.8.12',
			'org_apache_felix_framework_version'   : '5.6.1',
			'org_apache_felix_gogo_runtime_version': '1.0.2',
			'org_apache_felix_metatype_version'    : '1.1.2',
			'org_apache_felix_scr_version'         : '2.0.6',
			'slf4j_version'                        : '1.7.22',

			'jruyi_system_version'                 : '2.5.4',

			'jruyi_cli_version'                    : '2.0.2',
			'jruyi_launcher_version'               : '2.0.2',
			'org_jruyi_clid_version'               : '2.5.2',
			'org_jruyi_cmd_version'                : '2.0.6',
			'org_jruyi_common_version'             : '2.4.3',
			'org_jruyi_io_version'                 : '2.5.4',
			'org_jruyi_osgi_log_version'           : '2.0.3',
			'org_jruyi_tpe_version'                : '2.0.4',
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

			configurations {
				main
				lib
				bundle
			}

			configurations.main.transitive = false
			configurations.lib.transitive = false
			configurations.bundle.transitive = false

			distributions {
				main {
					contents {
						def pattern = Pattern.compile('(.*)-[0-9]+\\..*.jar')
						from(configurations.main) {
							into 'main'
							rename pattern, '$1.jar'
						}

						from(configurations.lib) {
							into 'lib'
							rename pattern, '$1.jar'
						}

						from(configurations.bundle) {
							into 'bundles'
							rename pattern, '$1.jar'
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
