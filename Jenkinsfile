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
                    junit '*/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Deliver') {
            steps {
                sh 'echo \'stoping dvdtheque-jenkins-rest.service ...\''
                sh 'sudo systemctl stop dvdtheque-jenkins-rest.service'
                sh 'echo \'copying dvdtheque-web-*.jar to  /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar ...\''
                sh 'cp dvdtheque-web/target/dvdtheque-web-*.jar /opt/dvdtheque_rest_jenkins_service/dvdtheque-web.jar'
                sh 'echo \'starting dvdtheque-jenkins-rest.service ...\''
                sh 'sudo systemctl start dvdtheque-jenkins-rest.service'
            }
        }
    }
}