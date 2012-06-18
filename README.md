grails-deploy
=============

The servlet container deployment plugin for Grails

Configuration
-------------

The following configuration options can be set in your grails-app/conf/BuildConfig.groovy file, or in your ~/.grails/settings.groovy file

    grailsSettings.projectWarFile.absolutePath : Path to your WAR file
    grailsSettings.config.grails.project.deploy.container : type of servlet container, note as of this writing, only tomcat6 has been tested
    grailsSettings.config.grails.project.deploy.context : The application context used to start, stop or undeploy the application
    grailsSettings.config.grails.project.deploy.password : The password used to connect to the remote servlet container
    grailsSettings.config.grails.project.deploy.username : The username used to connect to the remote servlet container

    grailsSettings.config.grails.project.deploy.port : The TCP port used to connect to the remote servlet container
    grailsSettings.config.grails.project.deploy.https : Set to true of the servlet container is using HTTPS
    grailsSettings.config.grails.project.deploy.hostname : The hostname of the remote servlet container

      ...or...

    grailsSettings.config.grails.project.deploy.serverUrl : The URL of the remote servlet container

All of these settings can also be specified via command line parameters

