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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class PrepareDistTask extends DefaultTask {

	private static final String[] BINS = ["ruyi", "ruyi.bat", "ruyi-cli", "ruyi-cli.bat"]
	private static final String SYSTEM_RY = "00-system.ry"
	private static final String BOOTSTRAP = "bootstrap.xml"
	private static final String LOG4J2_CONF = "log4j2.xml"

	private String instHomeDir

	@TaskAction
	def prepareDist() {
		normalizeInstHomeDir()
		configureBins()
		configureBootstrap()
		configureLog4j2Conf()
		configureSystemScript()
		configureJarsToPack()
	}

	private def normalizeInstHomeDir() {
		String jruyiInstHomeDir
		def v = project.ext['jruyiInstHomeDir']
		if (v == null || (jruyiInstHomeDir = v.toString().trim()).empty) {
			v = project['jruyiInstHomeDir']
			if (v == null || (jruyiInstHomeDir = v.toString().trim()).empty)
				throw new GradleException("jruyiInstHomeDir cannot be null or empty")
		}
		instHomeDir = jruyiInstHomeDir
	}

	private def configureBins() {
		if (project.jruyi.packDefaultBins) {
			def binDir = "${project.buildDir}/jruyi/bin"
			project.file("$binDir").mkdirs()
			BINS.each { bin ->
				byte[] content = PrepareDistTask.class.getResourceAsStream("/RUYI-INF/bin/$bin").getBytes()
				project.file("$binDir/$bin").setBytes(content)
			}

			project.configure(project) {
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
						}
					}
				}
			}
		}
	}

	private def configureSystemScript() {
		String systemScriptPath = project.jruyi.systemScriptPath
		if (systemScriptPath == null) {
			def provDir = "${project.buildDir}/jruyi/conf/prov"
			project.file("$provDir").mkdirs()

			systemScriptPath = "$provDir/$SYSTEM_RY"
			byte[] content = PrepareDistTask.class.getResourceAsStream("/RUYI-INF/conf/prov/$SYSTEM_RY").getBytes()
			project.file("$systemScriptPath").setBytes(content)
		} else if ((systemScriptPath = systemScriptPath.trim()).empty)
			return

		def intoProvDir = "$instHomeDir/conf/prov"
		project.configure(project) {
			distributions {
				main {
					contents {
						from("$systemScriptPath") {
							into "$intoProvDir"
						}
					}
				}
			}
		}
	}

	private def configureBootstrap() {
		String bootstrapPath = project.jruyi.bootstrapPath
		if (bootstrapPath == null) {
			def confDir = "${project.buildDir}/jruyi/conf"
			project.file("$confDir").mkdirs()

			bootstrapPath = "$confDir/$BOOTSTRAP"
			byte[] content = PrepareDistTask.class.getResourceAsStream("/RUYI-INF/conf/$BOOTSTRAP").getBytes()
			project.file("$bootstrapPath").setBytes(content)
		} else if ((bootstrapPath = bootstrapPath.trim()).empty)
			return

		def intoConfDir = "$instHomeDir/conf"
		project.configure(project) {
			distributions {
				main {
					contents {
						from("$bootstrapPath") {
							into "$intoConfDir"
						}
					}
				}
			}
		}
	}

	private def configureLog4j2Conf() {
		String log4j2ConfPath = project.jruyi.log4j2ConfPath
		if (log4j2ConfPath == null) {
			def confDir = "${project.buildDir}/jruyi/conf"
			project.file("$confDir").mkdirs()

			log4j2ConfPath = "${confDir}/$LOG4J2_CONF"
			byte[] content = PrepareDistTask.class.getResourceAsStream("/RUYI-INF/conf/$LOG4J2_CONF").getBytes()
			project.file("$log4j2ConfPath").setBytes(content)
		} else if ((log4j2ConfPath = log4j2ConfPath.trim()).empty)
			return

		def intoConfDir = "$instHomeDir/conf"
		project.configure(project) {
			distributions {
				main {
					contents {
						from("$log4j2ConfPath") {
							into "$intoConfDir"
						}
					}
				}
			}
		}
	}

	private def configureJarsToPack() {
		project.configure(project) {
			distributions {
				main {
					contents {
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
		}
	}
}
