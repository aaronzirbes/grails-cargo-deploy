class DeployGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
		'docs/**',
		'src/docs/**' ]

    def title = "Deploy Plugin" // Headline display name of the plugin
    def author = "Aaron J. Zirbes"
    def authorEmail = "aaron.zirbes@gmail.com"
    def description = '''\
	This allows deployment of your grails applicaiton WAR file directly to an application server.
This plugin uses the cargo libraries to deploy WAR files and manage servlet containers.  If you wish to check to see if your servlet container is supported, see: http://cargo.codehaus.org/
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/deploy"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/aaronzirbes/grails-deploy/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/aaronzirbes/grails-deploy" ]

}
