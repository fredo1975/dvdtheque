pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk8'
    }
    environment {
    	VERSION = readMavenPom().getVersion()
    	def pom = readMavenPom file: 'pom.xml'
    	def NVERSION = pom.version.replace("-SNAPSHOT", "")
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
                '''
            }
        }

        stage ('Build') {
		 		steps {
		 			withMaven(mavenSettingsConfig: '64b2f66f-fa43-4c22-86bc-47645fa2ff4e') {
            			sh '''
            				git remote set-url origin https://fredo1975:github1975@github.com/fredo1975/dvdtheque.git
            				git checkout -b release-"${NVERSION}" test_jenkins_pipeline
            				mvn clean verify
            				mvn build-helper:parse-version versions:set -DnewVersion="${NVERSION}" versions:commit
            				git add *
            				git commit -m "replace SNAPSHOT"
            				git push --force origin release-"${NVERSION}"
            				git checkout test_jenkins_pipeline
            				git merge --no-ff release-"${NVERSION}"
            				mvn build-helper:parse-version versions:set -DnewVersion=2.26.0-SNAPSHOT versions:commit
            				git add *
            				git commit -m "new dev version"
            				git push origin test_jenkins_pipeline
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