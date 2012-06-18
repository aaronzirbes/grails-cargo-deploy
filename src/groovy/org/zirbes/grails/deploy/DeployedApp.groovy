package org.zirbes.grails.deploy

class DeployedApp {
	String server
	String url
	String context
	String status
	String appName

	String toString() { appName }
}
