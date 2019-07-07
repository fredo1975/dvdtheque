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
            steps {
                sh 'mvn release:clean build-helper:parse-version versions:set release:prepare -DdevelopmentVersion='\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0-SNAPSHOT' release:perform -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir"'
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