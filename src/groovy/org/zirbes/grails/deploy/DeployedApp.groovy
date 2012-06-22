package org.zirbes.grails.deploy

/** Holds information about an app deployed to a servlet container.
 * It is used when the 'list' action is called within the DeployerService
 * on a servlet container */
class DeployedApp {

	/** The context that the application is deployed to */
	String context
	/** The status of the deployed application */
	String status
	/** The name of the application as provided by the WAR file */
	String appName

	/** The default string converter for this class.
	 * This returns the 'appName' property.
	 */
	String toString() { appName }
}
