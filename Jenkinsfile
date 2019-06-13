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
                sh 'mvn clean install -Djava.io.tmpdir=/var/tmp/exportDir' 
            }
            post {
                success {
                    junit '*/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Deliver') {
            steps {
                sh 'echo \'stoping dvdtheque-dev-rest.service ...\''
                sh 'sudo systemctl stop dvdtheque-dev-rest.service'
                sh 'echo \'copying dvdtheque-web-*.jar to  /opt/dvdtheque_rest_service/dev/dvdtheque-web.jar ...\''
                sh 'sudo cp dvdtheque-web/target/dvdtheque-web-*.jar /opt/dvdtheque_rest_service/dev/dvdtheque-web.jar'
                sh 'echo \'starting dvdtheque-dev-rest.service ...\''
                sh 'sudo systemctl start dvdtheque-dev-rest.service'
            }
        }
    }
}