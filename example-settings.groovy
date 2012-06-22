// Deploy plugin configurations per server
// These can also go in your ~/.grails/setings.groovy file

// default deploy settings

grails.plugins.deploy.defaults.containerId = 'tomcat7x'
grails.plugins.deploy.defaults.propertySet = [
			'remote.username': 'autodeployer',
			'remote.password': 'UGheoqu0Washu2aa0Li1',
			protocol: 'http',
			hostname: 'localhost',
			'servlet.port': 8080 ]

// These are groups of servers to deploy to
grails.plugins.deploy.groups = [ all: [ 'staging', 'production' ] ]

// these are alternate destinations to deploy to
grails.plugins.deploy.destinations = [
	// staging deploy destination
	staging: [
		propertySet: [
			protocol: 'https',
			hostname: 'testing.example.org',
			'servlet.port': 8443 ] ],
	// production deploy destination
	production: [
		propertySet: [
			protocol: 'https',
			hostname: 'www.example.org',
			'servlet.port': 443 ] ],

	testing: [
		containerId: 'tomcat6x',
		propertySet: [
			'remote.username': 'deployer',
			'hostname': '192.168.6.142',
			'servlet.port': 80 ] ] ]
