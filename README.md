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

Example
-------

Assuming you have a war file named 'myapp' and you have a user
created in the tomcat-users.xml file named 'deployer' with a
randomly generated password, we're using 'UGheoqu0Washu2aa0Li1' for
example.  We're assuming tomcat is running on your localhost, and is listening on tcp port 8080

to deploy:

    grails deploy --war=testapp.war --username=deployer --password=UGheoqu0Washu2aa0Li1 --hostname=localhost --port=8080 deploy

alternatively:

    grails deploy --war=testapp.war --username=deployer --password=UGheoqu0Washu2aa0Li1 --server-url=http://localhost:8080 deploy

or to undeploy:

    grails deploy --war=testapp.war --username=deployer --password=UGheoqu0Washu2aa0Li1 --hostname=localhost --port=8080 undeploy


Tips
----

* Need a quick and easy password generator?  Try pwgen.  It's available in apt, and in homebrew.

