package org.zirbes.grails.deploy

import org.apache.log4j.Logger

/** This class is used to ease the loading of configuration
 * settings coming from a heirarchy of locations.  Alternatively
 * use can use the  getDefaultConfiguration() method to get a
 * default configuration map that you can modify for use without
 * loading any configuration settings from the config files or
 * command line parameters.
 */
class ConfigBuilder {

	// Heirarchy:
	// 1. Command Line Param
	// 2. Destination setting
	// 3. Defaults setting
	// 4. Plugin default

	static log = Logger.getLogger(ConfigBuilder.class)

	/** This returns a Map of settings used by the DeployerService to 
	 *  configure containers and deployers for use in deploying war files
	 *  and controlling applications already deployed to a container.
	 */
	static Map loadConfiguration(String destination, Map argsMap, destConfigs) {

		def deployConfig = [:]
		deployConfig.propertySet = [:]
		def buildConfig = destConfigs.destinations

		log.debug "user configured defaults containerId: ${destConfigs.defaults.containerId}"
		log.debug "user configured defaults containerType: ${destConfigs.defaults.containerType}"
		log.debug "user configured defaults deployerType: ${destConfigs.defaults.deployerType}"
		log.debug "user configured defaults deployerType: ${destConfigs.defaults.configurationType}"

		// Start with build config defaults / plugin defaults
		deployConfig.containerId = destConfigs.defaults.containerId ?: 'tomcat7x'
		deployConfig.containerType = destConfigs.defaults.containerType ?: 'remote'
		deployConfig.deployerType = destConfigs.defaults.deployerType ?: 'remote'
		deployConfig.configurationType = destConfigs.defaults.configurationType ?: 'runtime'
		destConfigs.defaults.propertySet.each{ key, value ->
			def propertyName = key.replaceFirst(/set-/,'').replaceAll(/-/,'.')
			deployConfig.propertySet[propertyName] = value
		}

		log.debug "final default containerId: ${deployConfig.containerId}"
		log.debug "final default containerType: ${deployConfig.containerType}"
		log.debug "final default deployerType: ${deployConfig.deployerType}"
		log.debug "final default configurationType: ${deployConfig.configurationType}"
		// Set Configuration Properties
		deployConfig.properttSet.each{ key, value ->
			log.debug "final default propertySet:${key}=${value}"
		}

		// Load non-defaults (if destination is provided
		log.debug "destination: '${destination}'"
		if (destination != 'defaults') {
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

	/** Returns a default configuration map that can be used
	 *  without needing to load settings from the BuildConfig,
	 *  settings.groovy, or command line args. This is mostly
	 *  useful if you're using the service directly within your
	 *  application rather than through the grails 'deploy'
	 *  command.
	 *
	 *  It will return the Map:
	    <code>
		[	containerId: 'tomcat7x',
			containerType: 'remote',
			deployerType: 'remote',
			configurationType: 'runtime',
			propertSet: [ 
				protocol: 'http',
				hostname: 'localhost',
				'servlet.port': 8080 ] ]
		</code>
	 */
	static Map getDefaultConfiguration() {
		return [
			containerId: 'tomcat7x',
			containerType: 'remote',
			deployerType: 'remote',
			configurationType: 'runtime',
			propertSet: [ 
				protocol: 'http',
				hostname: 'localhost',
				'servlet.port': 8080 ] ]
	}
}
