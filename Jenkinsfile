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
            				mvn -e -X jgitflow:release-start jgitflow:release-finish -Djava.io.tmpdir=/var/tmp/exportDir -Dmaven.javadoc.skip=true
            				git rebase -i HEAD~4
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
}