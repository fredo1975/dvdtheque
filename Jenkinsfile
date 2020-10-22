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
		    post {
                success {
                	script {
			 			junit '*/target/surefire-reports/*.xml'
                    }
                }
            }
        }
        stage('Stopping Dev1 Rest service') {
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Stopping Dev2 Rest service') {
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Copying dvdtheque-rest-services') {
            steps {
                script {
			 		sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER1_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 		sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER2_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 	}
            }
        }
        stage('Sarting Dev1 Rest service') {
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Sarting Dev2 Rest service') {
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
    }
}