pipeline {
    agent any
    tools {
        maven 'Maven 3.6.0'
        jdk 'jdk8'
    }
    environment {
    	def DEV_SERVER1_IP = '192.168.1.103'
    	def DEV_SERVER2_IP = '192.168.1.105'
    	def PROD_SERVER1_IP = '192.168.1.105'
    	def PROD_SERVER2_IP = '192.168.1.106'
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
                    echo "DEV_VERSION = ${DEV_VERSION}"
                    echo "PROD_SERVER1_IP = ${PROD_SERVER1_IP}"
                    echo "PROD_SERVER2_IP = ${PROD_SERVER2_IP}"
                    echo "DEV_SERVER1_IP = ${DEV_SERVER1_IP}"
                    echo "DEV_SERVER2_IP = ${DEV_SERVER2_IP}"
                    echo "GIT_COMMIT_SHORT = ${GIT_COMMIT_SHORT}"
                '''
                sh 'env'
            }
        }
        stage('Clone repository') {
			steps {
				script {
					checkout scm
				}
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
				    	VERSION = pom.version
			 		}
			 		echo VERSION
			 		sh """
			        	mvn -B clean compile
			      	"""
		    	}
		    }
        }
        stage('Build for production') {
        	when {
                branch 'master'
            }
            steps {
		 		withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 			script {
			 			def pom = readMavenPom file: 'pom.xml'
				    	VERSION = pom.version.replaceAll('SNAPSHOT', BUILD_TIMESTAMP + "." + GIT_COMMIT_SHORT)
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
        	when {
                branch 'develop'
            }
        	steps {
				withMaven(mavenSettingsConfig: 'MyMavenSettings') {
			 		script {
			 			sh ''' 
			 				mvn -B test -Darguments="${JAVA_OPTS}"
			 			'''
			 		}
	            }
			}
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
        stage('Deploy for production') {
            when {
                branch 'master'
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
	    stage('Stopping Prod1 Rest service') {
        	when {
                branch 'master'
            }
        	steps {
        		sh 'ssh jenkins@$PROD_SERVER1_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Stopping Prod2 Rest service') {
        	when {
                branch 'master'
            }
        	steps {
        		sh 'ssh jenkins@$PROD_SERVER2_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Copying develop dvdtheque-rest-services') {
	    	when {
                branch 'develop'
            }
            steps {
                script {
			 		sh """
			 			scp dvdtheque-rest-services/target/dvdtheque-rest-services-${VERSION}.jar jenkins@${DEV_SERVER1_IP}:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar
			 		"""
			 		sh """
			 			scp dvdtheque-rest-services/target/dvdtheque-rest-services-${VERSION}.jar jenkins@${DEV_SERVER2_IP}:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar
			 		"""
			 	}
            }
        }
        stage('Copying develop dvdtheque-batch-app') {
	    	when {
                branch 'develop'
            }
            steps {
                script {
			 		sh """
			 			scp dvdtheque-batch-app/target/dvdtheque-batch-app-${VERSION}.jar jenkins@${DEV_SERVER1_IP}:/opt/dvdtheque_batch_service/dvdtheque-batch-app.jar
			 		"""
			 	}
            }
        }
        stage('Copying prouction dvdtheque-rest-services') {
	    	when {
                branch 'master'
            }
            steps {
                script {
                	sh """
			 			scp dvdtheque-rest-services/target/dvdtheque-rest-services-${VERSION}.jar jenkins@${PROD_SERVER1_IP}:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar
			 		"""
			 		sh """
			 			scp dvdtheque-rest-services/target/dvdtheque-rest-services-${VERSION}.jar jenkins@${PROD_SERVER2_IP}:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar
			 		"""
			 	}
            }
        }
        stage('Copying prouction dvdtheque-batch-app') {
	    	when {
                branch 'master'
            }
            steps {
                script {
			 		sh """
			 			scp dvdtheque-batch-app/target/dvdtheque-batch-app-${VERSION}.jar jenkins@${PROD_SERVER1_IP}:/opt/dvdtheque_batch_service/dvdtheque-batch-app.jar
			 		"""
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
   		stage('Sarting Prod1 Rest service') {
        	when {
                branch 'master'
            }
        	steps {
	        	sh 'ssh jenkins@$PROD_SERVER1_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Sarting Prod2 Rest service') {
   			when {
                branch 'master'
            }
        	steps {
	        	sh 'ssh jenkins@$PROD_SERVER2_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Check status Dev1 Rest service') {
   			when {
                branch 'develop'
            }
			steps {
				script {
				    sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl status dvdtheque-rest.service'
			    }
			}
		}
		stage('Check status Dev2 Rest service') {
			when {
                branch 'develop'
            }
			steps {
				script {
				    sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl status dvdtheque-rest.service'
			    }
			}
		}
		stage('Check status Prod1 Rest service') {
			when {
                branch 'master'
            }
			steps {
				script {
				    sh 'ssh jenkins@$PROD_SERVER1_IP sudo systemctl status dvdtheque-rest.service'
			    }
			}
		}
		stage('Check status Prod2 Rest service') {
			when {
                branch 'master'
            }
			steps {
				script {
				    sh 'ssh jenkins@$PROD_SERVER2_IP sudo systemctl status dvdtheque-rest.service'
			    }
			}
		}
    }
}