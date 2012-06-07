// import cargo stuff

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

// TODO: make sure ant can use cargo

// Load grails targets
includeTargets << grailsScript("_GrailsPackage")

def cargoConfig = [:]

target(deploy: "Deploy a WAR file or manage a runnin servlet application") {
	// check for --non-interactive
	if (isInteractive) {
		finalMessage "Running in Interactive mode"
	}

	// pull config settings servlet container (params vs. config)
	configureDeploySettings()

	// TODO: pull action settings (start|stop|depoloy|undeploy|redeploy)
	// TODO: run desired action
	ant.fail("This is not finished.")
}

target(configureDeploySettings: "Configuriing deployment settings") {
	// Pull in configuration settings

	// Container Type
	optionsMap.container = grailsSettings.projectWarFile.absolutePath ?: 'tomcat7x'
	// WAR file location
	optionsMap.war = grailsSettings.projectWarFile.absolutePath

	// First we configure from the grails settings.
	
	
	optionsMap.eachWithKey{ option, useParam ->

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

