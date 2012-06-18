grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// Deploy plugin configuration options and defaults
// grails.project.deploy.container = 'tomcat6'
// grails.project.deploy.context = null
// grails.project.deploy.password = ''
// grails.project.deploy.username = ''
// grails.project.deploy.port = 8080
// grails.project.deploy.https = false
// grails.project.deploy.hostname = 'localhost'
// grails.project.deploy.serverUrl = null

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
