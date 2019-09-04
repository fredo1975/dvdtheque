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
    	def PROD_SERVER_IP = '192.168.1.100'
    	def DEV_SERVER_IP = '192.168.1.103'
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
                    echo "PROD_SERVER_IP = ${PROD_SERVER_IP}"
                    echo "DEV_SERVER_IP = ${DEV_SERVER_IP}"
                '''
            }
        }
        stage ('Build') {
		 		steps {
		 			withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 				script {
			 				if("${ACTION_TYPE}" == "release"){
			 					sh ''' mvn -X -U jgitflow:release-start -DdevelopmentVersion=${DEV_VERSION} jgitflow:release-finish -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" '''
			 				}else if ("${ACTION_TYPE}" == "release-noTest") {
			 					sh '''mvn clean install -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" -Dmaven.test.skip=true'''
			 				}
			 			}
		    		}
		    	}
            post {
                success {
                	script {
			 			if("${ACTION_TYPE}" == "release"){
                    		junit '*/target/surefire-reports/*.xml'
                    	}
                    }
                }
            }
        }
        stage('Deliver') {
            steps {
                sh 'echo stoping dvdtheque-jenkins-rest.service on remote dev server "${DEV_SERVER_IP}" ...'
                sh 'ssh jenkins@$DEV_SERVER_IP sudo systemctl stop dvdtheque-jenkins-rest.service'
                sh 'echo copying dvdtheque-rest-services-"${VERSION}".jar to remote dev server "${DEV_SERVER_IP}" in /opt/dvdtheque_rest_jenkins_service/dvdtheque-rest-services.jar ...'
               	sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER_IP:/opt/dvdtheque_rest_jenkins_service/dvdtheque-rest-services.jar'
                sh 'echo starting dvdtheque-jenkins-rest.service on remote dev server "${DEV_SERVER_IP}" ...'
                sh 'ssh jenkins@$DEV_SERVER_IP sudo systemctl start dvdtheque-jenkins-rest.service'
                script {
			 		if("${ACTION_TYPE}" == "release"){
			 			sh 'echo stoping dvdtheque-prod-rest.service on remote prod server 192.168.1.100 ...'
                		sh 'ssh jenkins@$PROD_SERVER_IP sudo systemctl stop dvdtheque-prod-rest.service'
			 			sh 'echo copying dvdtheque-rest-services-${NVERSION}.jar to remote 192.168.1.100 server to /opt/dvdtheque_rest_service/prod/dvdtheque-rest-services.jar ...'
                		sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$NVERSION.jar jenkins@$PROD_SERVER_IP:/opt/dvdtheque_rest_service/prod/dvdtheque-rest-services.jar'
                		sh 'echo starting dvdtheque-prod-rest.service on remote prod server 192.168.1.100 ...'
                		sh 'ssh jenkins@$PROD_SERVER_IP sudo systemctl start dvdtheque-prod-rest.service'
			 		}
			 	}
            }
        }
    }
}