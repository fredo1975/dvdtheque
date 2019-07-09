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
		 			withMaven(mavenSettingsConfig: '64b2f66f-fa43-4c22-86bc-47645fa2ff4e') {
            			sh '''
            				mvn -e -X --batch-mode release:clean  \
            					release:prepare \
            					release:update-versions -DdevelopmentVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.nextMinorVersion}.0-SNAPSHOT \
            					release:perform -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir -Dmaven.javadoc.skip=true"
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