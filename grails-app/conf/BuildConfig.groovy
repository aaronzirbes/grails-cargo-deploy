grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// Deploy plugin configurations per server
// These can also go in your ~/.grails/setings.groovy file

// default deploy settings
grails.plugins.deploy.defaults = {
	containerId = 'tomcat7x'
	properties = {
		remote.username = 'autodeployer'
		remote.password = 'UGheoqu0Washu2aa0Li1'
		protocol = 'http'
		hostname = 'localhost'
		servlet.port = 8080
}

// These are groups of servers to deploy to
grails.plugins.deploy.groups = {
	all = [ 'staging', 'production' ]
}

// these are alternate destinations to deploy to
grails.plugins.deploy.destinations = {
	// staging deploy destination
	staging = {
		properties = {
			protocol = 'https'
			hostname = 'testing.example.org'
			servlet.port = 8443
		}
	}
	// production deploy destination
	production = {
		properties = {
			protocol = 'https'
			hostname = 'www.example.org'
			servlet.port = 443
		}
	}
}

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

		//runtime "org.codehaus.cargo:cargo-core:1.2.2"
		compile "org.codehaus.cargo:cargo-core:1.2.2"
		compile "org.codehaus.cargo:cargo-core-uberjar:1.2.2"
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.0.2") {
            export = false
        }
    }
}
