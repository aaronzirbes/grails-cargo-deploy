package org.zirbes.grails.deploy

import org.apache.log4j.Logger
import org.codehaus.cargo.container.Container
import org.codehaus.cargo.container.ContainerException
import org.codehaus.cargo.container.ContainerType
import org.codehaus.cargo.container.configuration.Configuration
import org.codehaus.cargo.container.configuration.ConfigurationType
import org.codehaus.cargo.container.deployable.DeployableType
import org.codehaus.cargo.container.deployer.DeployerType
import org.codehaus.cargo.container.property.GeneralPropertySet
import org.codehaus.cargo.container.property.RemotePropertySet
import org.codehaus.cargo.container.property.ServletPropertySet
import org.codehaus.cargo.generic.DefaultContainerCapabilityFactory
import org.codehaus.cargo.generic.DefaultContainerFactory
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory

/** This service is a groovy wrapper for a lot of the cargo factory classes
 * and for common deployment commands. It uses configuration Maps that are 
 * returned by the static ConfigurationBuilder.loadConfiguration() method, but 
 * will work with any kind of Map as long as the proper attributes are included.
 *
 * More information about cargo can be found at http://cargo.codehaus.org
 */
class DeployerService {

	def log = Logger.getLogger(DeployerService.class)

	/** Returns a URL listing all the available propertySet values you can use within a deployment destination */
	def getSettingConstantsLink() {
		 return new URL('http://cargo.codehaus.org/maven-site/cargo-core/apidocs/constant-values.html')
	}

	/** Checks to ensure a configuration supports deployment of a WAR file */
	def checkConfiguration(configuration) {
		def containerId = config.staging.containerId
		def containerCompatability = new DefaultContainerCapabilityFactory().createContainerCapability(containerId)
		
		if (! containerCompatability.supportsDeployableType(DeployableType.WAR) ){
			return false
		} else {
			return true
		}
	}

	/** Returns a Deployable object for the specific containerId you have configured.
	 * 
	 *  @param configuration a configuration object as returned by the ConfigurationBuilder.
	 *  @param warFile	a File object representing the WAR file you wish to deploy.
	 */
	def getWar(configuration, File warFile) {
		def containerId = configuration?.containerId ?: 'tomcat7x'
		new DefaultDeployableFactory().createDeployable(containerId, warFile.absolutePath, DeployableType.WAR)
	}

	/** Returns a Configuration object for the specific configuration destination you are using.
	 *
	 *  @param configuration a configuration object as returned by the ConfigurationBuilder.
	 */
	def getConfig(configuration) {

		// Pull in config settings
		def containerId = configuration?.containerId ?: 'tomcat7x'
		def containerType = configuration.containerType ?: 'remote'
		def configurationType = configuration?.configurationType ?: 'runtime'
		def configurationProperties = configuration.propertySet
		// If debugging is on, print out settings
		log.debug "containerId: '${containerId}'"
		log.debug "containerType: '${containerType}'"
		log.debug "configurationType: '${configurationType}'"

		// Instantiate container and config types
		def containerTypeInstance = new ContainerType(containerType)
		def configurationTypeInstance = new ConfigurationType(configurationType)
		// Instantiate the configuration
		def containerConfig = new DefaultConfigurationFactory()
			.createConfiguration(containerId, containerTypeInstance, configurationTypeInstance)
		// Set Configuration Properties
		configurationProperties.each{ key, value ->
			def prop = 'cargo.' + key.toString()
			log.debug "containerConfig.setProperty(${prop}, ${value})"
			containerConfig.setProperty(prop, value.toString())
		}

		return containerConfig
	}

	/** Returns a Container object for the specific configuration destination you are using.
	 *
	 *  @param configuration a configuration object as returned by the ConfigurationBuilder.
	 */
	def getContainer(configuration) {
		def containerId = configuration?.containerId ?: 'tomcat7x'
		def containerType = configuration.containerType ?: 'remote'

		def containerTypeInstance = new ContainerType(containerType)

		def containerConfig = getConfig(configuration)

		new DefaultContainerFactory().createContainer(containerId, containerTypeInstance, containerConfig)
	}

	/** Returns a Deployer object for the specific configuration destination you are using.
	 *
	 *  @param configuration a configuration object as returned by the ConfigurationBuilder.
	 */
	def getDeployer(configuration) {

		def deployerType = configuration?.deployerType ?: 'remote'

		log.debug "deployerType: '${deployerType}'"

		def deployerTypeInstance = new DeployerType(deployerType)

		def container = getContainer(configuration)

		new DefaultDeployerFactory().createDeployer(container, deployerTypeInstance)
	}

	/** Runs an option on a particular deployment destination for the specific configuraiton
	 * destination that you are using.
	 *
	 *  @param configuration	a configuration object as returned by the ConfigurationBuilder.
	 *  @param warFile			a File object representing the WAR file you wish to deploy.
	 *  @param action			a String containing one of the supported actions of your Deployer. The available options are 'deploy', 'redeploy', 'start', 'stop', 'undeploy', and 'list'.  List is only supported on a small set of containers, one of which is Tomcat.
	 */
	def runAction(configuration, File warFile, String action) {
		def deployer = getDeployer(configuration)
		def war = getWar(configuration, warFile)

		switch (action) {
			case "deploy":
				return deployer.deploy(war)
				break
			case "redeploy":
				return deployer.redeploy(war)
				break
			case "start":
				return deployer.start(war)
				break
			case "stop":
				return deployer.stop(war)
				break
			case "undeploy":
				return deployer.undeploy(war)
				break
			case "list":
				return getDeployedApps(deployer)
				break
			default:
				return false
		}

	}

	/** Returns a list of deployed applications from a tomcat application server.
	 * 
	 *  @param deployer	a Deployer object that supports the .list() method.
	 */
	def getDeployedApps(deployer) {
		if (deployer?.respondsTo('list')) {
			return deployer.list()
		} else {
			log.warn ".list() is not supported on this Deployer."
			throw new ContainerException(".list() is not supported on this Deployer.")
		}
	}
}

