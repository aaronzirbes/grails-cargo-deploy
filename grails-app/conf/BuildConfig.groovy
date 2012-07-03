grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.docs.output.dir = 'docs/'

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
    }
    dependencies {
		compile "org.codehaus.cargo:cargo-core:1.2.2"
		compile "org.codehaus.cargo:cargo-core-uberjar:1.2.2"
    }
    plugins {
        build(":tomcat:$grailsVersion",
			":rest-client-builder:1.0.2",
            ":release:2.0.3") {
            export = false
        }
    }
}
