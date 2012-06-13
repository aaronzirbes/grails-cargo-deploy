// import cargo stuff
import org.codehaus.cargo.util.AntTaskFactory

// command line usage
USAGE = """
usage: grails deployer <options> < start | stop | deploy | undeploy | redeploy >

Actions
-------

    start - Starts the application within the servlet container
    stop - Stops a running application within the servlet container
    deploy - DEFAULT - Deploys a war file to a servlet container
    undelploy - Removes a running application from a servlet container
    redeploy - Undeploys an existing running application from a servlet
        container and deploys a replacement WAR file

Options
-------

	Options are passed in the form --option=value when a value
	is required, otherwise you can pass a simple --option

	--container <the type of container to deploy to>

	    This can be any type of container that is supported by cargo.
		Some common containers are: tomcat6x, tomcat7x, orion2x, etc...
		You can find a list of supported containers at 
		http://cargo.codehaus.org/

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.container

	--password <password to authenticate with>

	    The password you will connect to the servlet container using

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.password

	--port <tcp port>

	    This is the TCP port that the app server is listening on

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.port

	--https

	    This flags whether or not to connect to the servlet container 
		is using SSL

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.https = true

	--server <server FQDN or IP address>

	    This is the hostname or IP address of the servlet container

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.server

	--server-url <URL to app server>

	    This is the full URL of the servlet container you wish to
		deploy to.  Do NOT use the --port, --https or --server 
		options if you use this option as they will be overwritten.

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.serverUrl
		
	--username <username to authenticate as>

	    The password you will connect to the servlet container using

		The equivalent setting in BuildConfig is:
		    grails.project.deploy.username

    --war <filename.war>

		This is the name of the WAR file to deploy

		The equivalent setting in BuildConfig.groovy is:
		    grails.project.war.file
"""

// This is a list of all accepted options
// and whether or not they take a string as
// a parameter
def optionsMap = [
	'container': true,
	'password': true,
	'port': true,
	'https': false,
	'server': true,
	'server-url': true,
	'username': true,
	'war': true ]

def allowedActions = [ 'start', 'stop', 'deploy', 'undeploy', 'redeploy']

// TODO: make sure ant can use cargo

// Load grails targets
includeTargets << grailsScript("_GrailsClasspath")
includeTargets << grailsScript("_GrailsPackage")

def cargoConfig = [:]
def cargoAction = ''

target(deploy: "Deploy a WAR file or manage a running servlet application") {
	// check for --non-interactive
	if (isInteractive) {
		finalMessage "Running in Interactive mode"
	}

	//// SETUP ANT for CARGO ////
	def cargo = AntTaskFactory.createTask('cargo.tasks')

	// pull config settings servlet container (params vs. config)
	configureDeploySettings()

	// TODO: run desired action
	ant.fail("This is not finished.")
}

target(configureDeploySettings: "Configuriing deployment settings") {

	// Pull in configuration settings from BuildConfig.groovy / settings.groovy
	// Defaults are after the elvis
	cargoConfig.war = grailsSettings.projectWarFile.absolutePath
	cargoConfig.container = grailsSettings.config.grails.project.deploy.container ?: 'tomcat6'
	cargoConfig.password = grailsSettings.config.grails.project.deploy.password ?: ''
	cargoConfig.username = grailsSettings.config.grails.project.deploy.username ?: ''
	cargoConfig.port = grailsSettings.config.grails.project.deploy.port ?: 8443
	cargoConfig.https = grailsSettings.config.grails.project.deploy.https ?: true
	cargoConfig.server = grailsSettings.config.grails.project.deploy.server ?: 'localhost'
	cargoConfig.'server-url' = grailsSettings.config.grails.project.deploy.serverUrl ?: null

	// First we configure from the grails settings.
	optionsMap.each{ option, useParam ->
		// The option was passed...
		if (argsMap[option] ) {
			// save it to the cargoConfig map
			cargoConfig[option] = argsMap[option]
		}
	}

	// Figure out the action
	def action = argsMap.params[0].toLowerCase()
	if (allowedActions.contains(action)) {
		cargoAction = action
	}

	// TODO: remove DEBUGGING
	finalMessage "Loaded the following options for WAR deployment:"
	cargoConfig.each{ option, value ->
		finalMessage "${option} = ${value}"
	}
	finalMessage "Action: ${cargoAction.toUpperCase()}"

}

// Helper Functions
/** Helper for printing informational messages */
finalMessage = { String message -> event('StatusFinal', [message]) }
/** Helper for printing informational messages */
printMessage = { String message -> event('StatusUpdate', [message]) }
/** Helper for printing error messages */
errorMessage = { String message -> event('StatusError', [message]) }

setDefaultTarget 'deploy'

