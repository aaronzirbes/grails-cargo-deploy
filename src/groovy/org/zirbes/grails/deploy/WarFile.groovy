package org.zirbes.grails.deploy

class WarFile implements Serializable {

	File warFile
	DeployedApp deployedApp
	String context
	String warFileName
	String warFilePath
	Boolean deployable = false

	public WarFile(String filePath) {

		def war = new File(filePath)

		if (war.exists() && war.isFile() && war.canRead() ) {

			// We found a readable war file
			warFile = war
			warFilePath = warFile.absolutePath
			warFileName = warFile.name

			// Get the context name
			context = warFileName.reverse().replaceFirst(/raw\./, '').reverse()
			if (context == 'ROOT') { context = '/' }

			deployable = true

		} else {
			throw new java.io.IOException("Unable to read WAR file : ${filePath}")
		}
	}

	String toString() { warFileName }

}
