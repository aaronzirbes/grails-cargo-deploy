package org.zirbes.grails.deploy

/** This class is used to ease the loading of configuration
 * settings coming from a heirarchy of locations
 */
class ConfigBuilder {

	// Heirarchy:
	// 1. Command Line Param
	// 2. Destination setting
	// 3. Defaults setting
	// 4. Plugin default

	static Map loadConfiguration(destination, argsMap, destConfigs) {

		def deployConfig = [:]

		// Start with build config defaults / plugin defaults
		deployConfig.containerId = buildConfig.defaults.containerId ?: 'tomcat7x'
		deployConfig.containerType = buildConfig.defaults.containerType ?: 'remote'
		deployConfig.deployerType = buildConfig.defaults.deployerType ?: 'remote'
		deployConfig.configurationType = buildConfig.defaults.configurationType ?: 'runtime'
		deployConfig.properties = buildConfig.defaults.properties

		// override with destination settings
		deployConfig.containerId = buildConfig.defaults.containerId
		deployConfig.containerType = buildConfig.defaults.containerType
		deployConfig.deployerType = buildConfig.defaults.deployerType
		deployConfig.configurationType = buildConfig.defaults.configurationType
		// override destination settings
		buildConfig.defaults.properties.each{ key, value ->
			deployConfig.properties[key] = value
		}

		// override command line args
		argsMap.each{ key, value ->
			println "reading key/value: (${key}/${value})"
			if (key.startsWith('--set-')) {
				// We found a property setting	
				def propertyName = key.replaceFirst(/--set-/,'').replaceAll(/-/,'.')
				println "converting key to propertyName: (${propertyName})"
				deployConfig.properties[propertyName] = value
			}
		}

		return buildConfig
	}
}
