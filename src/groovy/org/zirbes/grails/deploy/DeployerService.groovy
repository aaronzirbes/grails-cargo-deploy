package org.zirbes.grails.deploy

import org.codehaus.cargo.container.Container
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

class DeployerService {

	def getSettingConstantsLink() {
		 return new URL('http://cargo.codehaus.org/maven-site/cargo-core/apidocs/constant-values.html')
	}

	def checkConfiguration(configuration) {
		def containerId = config.staging.containerId
		def containerCompatability = new DefaultContainerCapabilityFactory().createContainerCapability(containerId)
		
		if (! containerCompatability.supportsDeployableType(DeployableType.WAR) ){
			return false
		} else {
			return true
		}
	}

	def getWar(configuration, File warFile) {
		def containerId = configuration?.containerId ?: 'tomcat7x'
		new DefaultDeployableFactory().createDeployable(containerId, warFile.absolutePath, DeployableType.WAR)
	}

	def getConfig(configuration) {
		def containerId = configuration?.containerId ?: 'tomcat7x'
		def containerType = configuration.containerType ?: 'remote'
		def configurationType = configuration?.configurationType ?: 'runtime'

		def configurationProperties = configuration.propertySet

		def containerTypeInstance = new ContainerType(containerType)
		def configurationTypeInstance = new ConfigurationType(configurationType)


		def containerConfig = new DefaultConfigurationFactory()
			.createConfiguration(containerId, containerTypeInstance, configurationTypeInstance)

		// Apply Configuration Settings
		configurationProperties.each{ key, value ->
			def prop = 'cargo.' + key.toString()
			containerConfig.setProperty(prop, value.toString())
		}

		return containerConfig
	}

	def getContainer(configuration) {
		def containerId = configuration?.containerId ?: 'tomcat7x'
		def containerType = configuration.containerType ?: 'remote'

		def containerTypeInstance = new ContainerType(containerType)

		def containerConfig = getConfig(configuration)

		new DefaultContainerFactory().createContainer(containerId, containerTypeInstance, containerConfig)
	}

	def getDeployer(configuration) {

		def deployerType = configuration?.deployerType ?: 'remote'

		def deployerTypeInstance = new DeployerType(deployerType)

		def container = getContainer(configuration)

		new DefaultDeployerFactory().createDeployer(container, deployerTypeInstance)
	}

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

	/**
	 * Get a list of deployed applications from a tomcat application server.
	 */
	def getDeployedApps(deployer) {

		Set<DeployedApp> appList = new HashSet<DeployedApp>()

		if (deployer?.respondsTo('list')) {

			def deployerList = deployer.list()
			if (deployerList) {
				def applications = deployerList.replace('\r','')?.split('\n')

				String result = applications[0]
				if (result.startsWith('OK -') ) {
					//Success
					applications.each{ line ->
						def parts = line.split(':')
						if (parts.length == 4) {
							def appName = parts[3]
							if (! appName.contains('/') ) {
								def deployedApp = new DeployedApp()
								deployedApp.context = parts[0]
								deployedApp.appName = appName
								deployedApp.status = parts[1]
								
								appList.add(deployedApp)
							}
						}
					}
				} else {
					log.error "Failed to get deployed application status, read: ${result}"
				}
			}
		}

		log.debug "Deployed Apps:"
		appList.each{ da ->
			log.debug "appName: ${da.appName}"
			log.debug "  server: ${da.server}"
			log.debug "  url: ${da.url}"
			log.debug "  context: ${da.context}"
			log.debug "  status: ${da.status}"
		}

		return appList
	}
}

