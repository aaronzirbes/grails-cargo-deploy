package org.zirbes.grails.deploy

//import org.codehaus.cargo.container.glassfish.GlassFish3x6xRemoteContainer
//import org.codehaus.cargo.container.glassfish.GlassFish3x6xRemoteDeployer
//import org.codehaus.cargo.container.glassfish.GlassFish3xRuntimeConfiguration
import org.codehaus.cargo.container.deployable.WAR
import org.codehaus.cargo.container.property.GeneralPropertySet
import org.codehaus.cargo.container.property.RemotePropertySet
import org.codehaus.cargo.container.property.ServletPropertySet
import org.codehaus.cargo.container.tomcat.Tomcat6xRemoteContainer
import org.codehaus.cargo.container.tomcat.Tomcat6xRemoteDeployer
import org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration
import org.codehaus.cargo.container.tomcat.TomcatWAR
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder

class DeployerService {

	private Map configuration

	DeployerService(Map _configuration) {
		configuration = _configuration
	}

	// These are the default closures for Tomcat 6.x, but they
	// can be overwrittent to return constructors for any 
	// servlet container supported by Cargo
	def newContainerWar = { warPath -> new TomcatWAR(warPath) }
	def newRemoteContainer = { containerConfiguration -> new Tomcat6xRemoteContainer(containerConfiguration) }
	def newRemoteDepoloyer = { remoteContainer -> new Tomcat6xRemoteDeployer(remoteContainer) }
	def newContainerConfiguration = { new TomcatRuntimeConfiguration() }

	// Here is how you could do this for Glassfish v3
	/*
	def newContainerWar = { warPath -> new WAR(warPath) }
	def newRemoteContainer = { containerConfiguration -> new GlassFish3xRemoteContainer(containerConfiguration) }
	def newRemoteDepoloyer = { remoteContainer -> new GlassFish3xRemoteDeployer(remoteContainer) }
	def newContainerConfiguration = { new GlassFish3xRuntimeConfiguration() }
	*/

	private def serviceWebApp(String context, String action) {
		Boolean returnStatus = false

		def deployer = getDeployer()
		if (deployer) {
			// create fake war name
			def warName = context + '.war'

			// creat fake war object
			def deployable = new WAR(warName)

			def deployedApps = getDeployedApps(deployer)

			log.debug "Deployed Apps:"
			deployedApps.each{ da ->
				log.debug "appName: ${da.appName}"
				log.debug "  server: ${da.server}"
				log.debug "  url: ${da.url}"
				log.debug "  context: ${da.context}"
				log.debug "  status: ${da.status}"
			}

			def expectedStatus = 'UNKOWN'

			if (action == 'undeploy') {
				expectedStatus = [ 'running', 'stopped' ]
			} else if (action == 'start') {
				expectedStatus = [ 'stopped' ]
			} else if (action == 'stop') {
				expectedStatus = [ 'running' ]
			}

			if ( deployedApps.find{ it.appName == context && expectedStatus.contains( it.status ) } ) {
				if (action == 'undeploy') {
					log.info "Undeploying ${context}"
					returnStatus = deployer.undeploy(deployable)
				} else if (action == 'start') {
					log.info "Starting ${context}"
					returnStatus = deployer.start(deployable)
				} else if (action == 'stop') {
					log.info "Stopping ${context}"
					returnStatus = deployer.stop(deployable)
				}
			} else {
				log.info "application context '${context}' isn't ${expectedStatus}, can't ${action}!"
			}
		}
		return returnStatus
	}

	def startWebApp(String context) {
		serviceWebApp(context, 'start')
	}
	def stopWebApp(String context) {
		serviceWebApp(context, 'stop')
	}
	def unDeployWebApp(String context) {
		serviceWebApp(context, 'undeploy')
	}

	/** Redeploy a web app to a servlet containter.
	 *  Right now, this only works with tomcat.
	 */
	def reDeployWebApp(String warFileName) {

		Boolean returnStatus = false

		def warFile = new WarFile(warFileName)

		if (warFile && warFile.deployable) {
			log.info "Deploying ${warFile}..."

			def deployer = getDeployer()
			if (deployer) {
				def deployable = newContainerWar(warFile.warFilePath)

				def deployedApps = getDeployedApps(deployer)

				if (deployedApps.find{ it.appName == warFile.context}) {
					log.info "Redeploying ${warFile.context}"
					returnStatus = deployer.redeploy(deployable)
				} else {
					log.info "Deploying ${warFile.context}"
					returnStatus = deployer.deploy(deployable)
				}
			}
		}
		return returnStatus
	}

	/** Deploy a web app to a servlet containter */
	def deployWebApp(String warFileName) {

		Boolean returnStatus = false

		def warFile = new WarFile(warFileName)

		if (warFile && warFile.deployable) {
			log.info "Deploying ${warFile}..."

			def deployer = getDeployer()
			if (deployer) {
				def deployable = newContainerWar(warFile.warFilePath)

				log.info "Deploying ${warFile.context}"
				returnStatus = deployer.deploy(deployable)
			}
		}
		return returnStatus
	}

	/** Configure a remote servlet container. */
	def configureContainer() {

		def cargoConfig = configuration
		// Load an empty configuration
		def containerConfiguration = newContainerConfiguration()
		// define the server URL
		def serverURL = null
		if (cargoConfig['server-url']) {
			if (cargoConfig.container == 'tomcat6') {
				serverURL = new URL(server.url + '/manager').toExternalForm()
			} else {
				serverURL = new URL(server.url).toExternalForm()
			}
		}

		// Load username/password
		containerConfiguration.setProperty(RemotePropertySet.USERNAME, cargoConfig.username)
		containerConfiguration.setProperty(RemotePropertySet.PASSWORD, cargoConfig.password)

		// Load server info
		if (serverURL) {
			containerConfiguration.setProperty(RemotePropertySet.URI, serverURL)
		} else {
			// Load from host/protocol/port
			containerConfiguration.setProperty(GeneralPropertySet.PROTOCOL, (cargoConfig.https ? 'https' : 'http' ))
			containerConfiguration.setProperty(GeneralPropertySet.HOSTNAME, cargoConfig.hostname)
			containerConfiguration.setProperty(ServletPropertySet.PORT, cargoConfig.port.toString())
		}

		return newRemoteContainer(containerConfiguration)
	}

	/** Get a list of deployed applications from an application server.
	 * Right now this only works with a Tomcat Server
	 */
	def getDeployedApps(deployer) {

		Set<DeployedApp> appList = new HashSet<DeployedApp>()

		if (deployer) {

			def containerConfiguration = deployer.getConfiguration()
			def url = containerConfiguration.getPropertyValue(RemotePropertySet.URI)?.replace(/\/manager/, '')
			def serverName = configuration?.hostname ?: configuration?.serverUrl

			def deployerList = deployer?.list()
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
								deployedApp.server = serverName
								deployedApp.url = url
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
		return appList
	}

	/** Builds and returns a remote deployer from the configuration */
	def getDeployer() {
		return newRemoteDepoloyer( configureContainer() )
	}
}

