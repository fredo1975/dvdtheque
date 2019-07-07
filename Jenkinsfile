pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk8'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
        	withMaven(mavenSettingsConfig: '64b2f66f-fa43-4c22-86bc-47645fa2ff4e') {
		 		steps {
            		sh "mvn -e -X -U --batch-mode clean"
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