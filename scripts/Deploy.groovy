// command line usage
USAGE = """
usage: grails deploy [options] [action]

Actions
-------

    start - Starts the application within the servlet container
    stop - Stops a running application within the servlet container
    deploy - DEFAULT - Deploys a war file to a servlet container
    undelploy - Removes a running application from a servlet container
    redeploy - Undeploys an existing running application from a servlet
        container and deploys a replacement WAR file
    list - List running applications (on supported servlet containers)

Options
-------

	Options are passed in the form --option=value when a value
	is required, otherwise you can pass a simple --option

	--destination <deployment destination>

	    The destination to deploy to.  This defaults to 'defaults'.  The
		default and other configurations can be configured in your 
		BuildConfig.groovy or settings.groovy file.  See the documentation
		for details.

	--destination-group <deployment destination group>

	    A group of destination servers to deploy the application to.  These
		must be configured in your BuildConfig.groovy or settings.groovy
		file.  See the documentation for details.

    --help

		display this help screen

    --war <filename.war>

		This is the name of the WAR file to deploy or redeploy

		This will override the following setting in BuildConfig.groovy:
		    grails.project.war.file

	--set-CUSTOM-SETTING

	    This allows you to override any configuration setting
		used in the deployment configuration file. Any dots
	    are replaced by dashes.  For example
		'--set-remote-password=SuperDuperSecret' would override
		the 'remote.password' setting in your configuration file.
"""

def allowedActions = [ 'start', 'stop', 'deploy', 'undeploy', 'redeploy', 'list' ]

// Load grails targets
includeTargets << grailsScript("_GrailsBootstrap")

def deployDestination
def deployDestinationGroup
def deployWar
def deployAction = ''
def deployConfigNames = [] as Set
def deployConfig

target(deploy: "Deploy a WAR file or manage a running servlet application") {
	// we need to load the app to get the cargo dependencies in memory
	depends(loadApp)
	// check for --non-interactive
	if (!isInteractive) {
		printMessage "Running in Non-Interactive mode."
	}

	configureDeploySettings()

	// instantiate the war file
	def warFile = new File(deployWar)
	if (! warFile ) {
		errorMessage "Unable to find WAR file: ${deployWar}"
		return 1
	} else {
		printMessage "Application context loaded from ${warFile}."
	}

	// pull config settings servlet container
	def DeployerService = classLoader.loadClass("org.zirbes.grails.deploy.DeployerService")
	def deployerService = DeployerService.newInstance()

	// dpeloy the app to the servlet container(s)
	def j = deployDestinationGroup.size()
	deployDestinationGroup.eachWithIndex{ dest, i ->
		def startTime = System.currentTimeMillis()
		// run the action
		printMessage "Running '${deployAction}' on '${warFile}' to '${dest}' (${i + 1}/$j)..."

		// Load the cargo ant factory builder
		def ConfigBuilder = classLoader.loadClass("org.zirbes.grails.deploy.ConfigBuilder")

		// Instantiate the Config Builder
		deployConfig = ConfigBuilder.loadConfiguration(dest, argsMap, grailsSettings.config.grails.plugins.deploy)

		// get the container deployable WAR
		def deployableWar = deployerService.getWar(deployConfig, warFile)
		// Run the action
		deployerService.runAction(deployConfig, warFile, deployAction)

		def endTime = System.currentTimeMillis()
		def elapsedTime = endTime - startTime
		finalMessage "Finished '${deployAction}' on '${warFile}' to '${dest}' (${elapsedTime / 1000} s)."
	}
}

target(configureDeploySettings: "Configuring deployment settings") {

	deployDestination = argsMap.destination ?: 'defaults'
	deployDestinationGroup = argsMap.'destination-group' ?: null
	deployWar = argsMap.war ?: grailsSettings.projectWarFile.absolutePath
	deployAction = argsMap.params[0]?.toLowerCase() ?: 'deploy'

	// If no set was loaded, load a set of a single config
	if (!deployDestinationGroup) {
		deployDestinationGroup = [ deployDestination ]
	}

	// Figure out the action
	def action = argsMap.params[0]?.toLowerCase()
	if (action && allowedActions.contains(action)) {
		deployAction = action
	} else {
		// default action
		action = 'deploy'
	}

}

// Helper Functions
/** Helper for printing informational messages */
finalMessage = { String message -> event('StatusFinal', [message]) }
/** Helper for printing informational messages */
printMessage = { String message -> event('StatusUpdate', [message]) }
/** Helper for printing error messages */
errorMessage = { String message -> event('StatusError', [message]) }

setDefaultTarget 'deploy'

