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

	static def loadConfiguration(destination, argsMap, destConfigs) {

		def deployConfig = [:]
		deployConfig.propertySet = [:]
		def buildConfig = destConfigs.destinations


		// Start with build config defaults / plugin defaults
		deployConfig.containerId = destConfigs.defaults.containerId ?: 'tomcat7x'
		deployConfig.containerType = destConfigs.defaults.containerType ?: 'remote'
		deployConfig.deployerType = destConfigs.defaults.deployerType ?: 'remote'
		deployConfig.configurationType = destConfigs.defaults.configurationType ?: 'runtime'
		destConfigs.defaults.propertySet.each{ key, value ->
			def propertyName = key.replaceFirst(/set-/,'').replaceAll(/-/,'.')
			deployConfig.propertySet[propertyName] = value
		}

		// override with destination settings
		deployConfig.containerId = buildConfig[destination].containerId
		deployConfig.containerType = buildConfig[destination].containerType
		deployConfig.deployerType = buildConfig[destination].deployerType
		deployConfig.configurationType = buildConfig[destination].configurationType

		// override destination settings
		buildConfig[destination].propertySet.each{ key, value ->
			def propertyName = key.replaceFirst(/set-/,'').replaceAll(/-/,'.')
			deployConfig.propertySet[propertyName] = value
		}

		// override command line args
		argsMap.each{ key, value ->
			if (key.startsWith('set-')) {
				// We found a property setting	
				def propertyName = key.replaceFirst(/set-/,'').replaceAll(/-/,'.')
				deployConfig.propertySet[propertyName] = value
			}
		}

		return deployConfig
	}
}
