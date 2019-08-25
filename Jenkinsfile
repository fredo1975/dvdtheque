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
    	def SERVER_IP = '192.168.1.100'
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
                    echo "SERVER_IP = ${SERVER_IP}"
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
                sh 'echo \'stoping dvdtheque-jenkins-rest.service ...\''
                sh 'sudo systemctl stop dvdtheque-jenkins-rest.service'
                script {
			 		if("${ACTION_TYPE}" == "release"){
			 			sh 'echo \'copying dvdtheque-web-${NVERSION}.jar to  /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar ...\''
                		sh 'mv dvdtheque-web/target/dvdtheque-web-$NVERSION.jar /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar'
			 		}else if ("${ACTION_TYPE}" == "release-noTest") {
			 			sh 'echo \'copying dvdtheque-web-${VERSION}.jar to  /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar ...\''
                		sh 'mv dvdtheque-web/target/dvdtheque-web-$VERSION.jar /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar'
			 		}
			 	}
                sh 'echo \'starting dvdtheque-jenkins-rest.service ...\''
                sh 'sudo systemctl start dvdtheque-jenkins-rest.service'
                script {
			 		if("${ACTION_TYPE}" == "release"){
			 			sh 'echo \'stoping dvdtheque-prod-rest.service on remote prod server 192.168.1.100 ...\''
                		sh 'ssh fredo@192.168.1.100 sudo systemctl stop dvdtheque-prod-rest.service'
			 			sh 'echo \'copying dvdtheque-web-${NVERSION}.jar to remote 192.168.1.100 server to /opt/dvdtheque_rest_service/prod/dvdtheque-web.jar ...\''
                		sh 'scp -r dvdtheque-web/target/dvdtheque-web-$NVERSION.jar fredo@192.168.1.100:/opt/dvdtheque_rest_service/prod/dvdtheque-web.jar'
                		sh 'echo \'starting dvdtheque-prod-rest.service on remote prod server 192.168.1.100 ...\''
                		sh 'ssh fredo@192.168.1.100 sudo systemctl start dvdtheque-prod-rest.service'
			 		}
			 	}
            }
        }
    }
}