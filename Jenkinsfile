pipeline {
    agent any
    tools {
        maven 'Maven 3.6.0'
        jdk 'jdk8'
    }
    environment {
    	/*VERSION = readMavenPom().getVersion()
    	def pom = readMavenPom file: 'pom.xml'
    	def NVERSION = pom.version.replace("-SNAPSHOT", "")
    	def PROD_SERVER1_IP = '192.168.1.105'
    	def PROD_SERVER2_IP = '192.168.1.106'*/
    	def DEV_SERVER1_IP = '192.168.1.103'
    	def DEV_SERVER2_IP = '192.168.1.101'
    	def JAVA_OPTS='-Djava.io.tmpdir=/var/tmp/exportDir'
    	GIT_COMMIT_SHORT = sh(
                script: "printf \$(git rev-parse --short HEAD)",
                returnStdout: true
        )
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "VERSION = ${VERSION}"
                    echo "pom = ${pom}"
                    echo "NVERSION = ${NVERSION}"
                    echo "DEV_VERSION = ${DEV_VERSION}"
                    echo "PROD_SERVER1_IP = ${PROD_SERVER1_IP}"
                    echo "DEV_SERVER1_IP = ${DEV_SERVER1_IP}"
                    echo "DEV_SERVER2_IP = ${DEV_SERVER2_IP}"
                    echo "GIT_COMMIT_SHORT = ${GIT_COMMIT_SHORT}"
                '''
                sh 'env'
            }
        }
        stage('Build for development') {
        	when {
                branch 'develop'
            }
            steps {
		 		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 			script {
			 			def pom = readMavenPom file: 'pom.xml'
						VERSION = sh(
							script: "pom.version.replaceAll('SNAPSHOT', BUILD_TIMESTAMP + '.' + GIT_COMMIT_SHORT)",
							returnStdout: true
						)
			 		}
			 		echo VERSION
			 		sh """
				    	mvn -B org.codehaus.mojo:versions-maven-plugin:2.5:set -DprocessAllModules -DnewVersion=${VERSION}
				    """
			      	sh """
			        	mvn -B clean compile
			      	"""
		    	}
		    }
        }
        stage('Unit Tests') {
        	// We have seperate stage for tests so 
			// they stand out in grouping and visualizations
			steps {
				withMaven(mavenSettingsConfig: 'MyMavenSettings') {
			 		script {
			 			sh ''' 
			 				mvn -B test -Darguments="${JAVA_OPTS}"
			 			'''
			 		}
	            }
			}
			// Note that, this requires having test results. 
			// But you should anyway never skip tests in branch builds
			post {
				always {
			    	junit '**/target/surefire-reports/*.xml'
			    }
			}   
        }
        stage('Deploy for development') {
            when {
                branch 'develop'
            }
            steps {
		 		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 			script {
			 			sh """
					    	 mvn -B deploy -DskipTests
					    """
			 		}
		    	}
		    }
        }
        stage('Stopping Dev1 Rest service') {
        	when {
                branch 'develop'
            }
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Stopping Dev2 Rest service') {
	    	when {
                branch 'develop'
            }
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Copying dvdtheque-rest-services') {
	    	when {
                branch 'develop'
            }
            steps {
                script {
			 		sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER1_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 		sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER2_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 	}
            }
        }
        stage('Sarting Dev1 Rest service') {
        	when {
                branch 'develop'
            }
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Sarting Dev2 Rest service') {
   			when {
                branch 'develop'
            }
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
    }
}