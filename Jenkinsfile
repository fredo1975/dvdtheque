pipeline {
    agent any
    tools {
        maven 'Maven 3.6.0'
        jdk 'jdk8'
    }
    environment {
    	VERSION = readMavenPom().getVersion()
    	def pom = readMavenPom file: 'pom.xml'
    	def NVERSION = pom.version.replace("-SNAPSHOT", "")
    	def PROD_SERVER1_IP = '192.168.1.105'
    	def PROD_SERVER2_IP = '192.168.1.106'
    	def DEV_SERVER1_IP = '192.168.1.103'
    	def DEV_SERVER2_IP = '192.168.1.101'
    	ACTION_TYPE = "${env.ACTION_TYPE}"
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    echo "VERSION = ${VERSION}"
                    echo "pom = ${pom}"
                    echo "NVERSION = ${NVERSION}"
                    echo "DEV_VERSION = ${DEV_VERSION}"
                    echo "PROD_SERVER1_IP = ${PROD_SERVER1_IP}"
                    echo "DEV_SERVER1_IP = ${DEV_SERVER1_IP}"
                    echo "DEV_SERVER2_IP = ${DEV_SERVER2_IP}"
                '''
            }
        }
        stage('Deliver for development') {
            when {
                branch 'develop'
            }
            steps {
		 			withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 				script {
			 				sh '''mvn clean install -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" '''
			 			}
		    		}
		    	}
        }
    }
}