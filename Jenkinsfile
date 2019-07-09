pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk8'
    }
    environment {
    	VERSION = readMavenPom().getVersion()
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    echo "VERSION = ${VERSION}"
                '''
            }
        }

        stage ('Build') {
		 		steps {
		 			withMaven(mavenSettingsConfig: '64b2f66f-fa43-4c22-86bc-47645fa2ff4e') {
            			sh '''
            				git checkout -b release-"${VERSION}"
            				mvn clean verify
            				
            			'''
		    		}
		    	}
            post {
                success {
                    junit '*/target/surefire-reports/*.xml'
                }
            }
        }
        
    }
    def getReleaseVersion() {
	    def pom = readMavenPom file: 'pom.xml'
	    def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
	    def versionNumber;
	    if (gitCommit == null) {
	        versionNumber = env.BUILD_NUMBER;
	    } else {
	        versionNumber = gitCommit.take(8);
	    }
	    return pom.version.replace("-SNAPSHOT", ".${versionNumber}")
	}
}