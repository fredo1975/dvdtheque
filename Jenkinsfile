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
    	def PROD_SERVER1_IP = '192.168.1.105'
    	def PROD_SERVER2_IP = '192.168.1.106'
    	def DEV_SERVER1_IP = '192.168.1.103'
    	def DEV_SERVER2_IP = '192.168.1.101'
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
                    echo "PROD_SERVER1_IP = ${PROD_SERVER1_IP}"
                    echo "DEV_SERVER1_IP = ${DEV_SERVER1_IP}"
                    echo "DEV_SERVER2_IP = ${DEV_SERVER2_IP}"
                '''
            }
        }
        stage ('Build') {
		 		steps {
		 			withMaven(mavenSettingsConfig: 'MyMavenSettings') {
		 				script {
			 				if("${ACTION_TYPE}" == "prod"){
			 					sh ''' mvn -U jgitflow:release-start -DdevelopmentVersion=${DEV_VERSION} jgitflow:release-finish -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" '''
			 				}else if ("${ACTION_TYPE}" == "dev-noTest") {
			 					sh '''mvn clean install -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" -DskipTests'''
			 				}else if ("${ACTION_TYPE}" == "dev") {
			 					sh '''mvn clean install -Darguments="-Djava.io.tmpdir=/var/tmp/exportDir" '''
			 				}
			 			}
		    		}
		    	}
            post {
                success {
                	script {
			 			if("${ACTION_TYPE}" == "prod" || "${ACTION_TYPE}" == "dev"){
                    		junit '*/target/surefire-reports/*.xml'
                    	}
                    }
                }
            }
        }
        stage('Stopping Dev1 Rest service') {
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Stopping Dev2 Rest service') {
        	steps {
	       		sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl stop dvdtheque-rest.service'
	       	}
	    }
	    stage('Stopping Prod1 Rest service') {
	    	steps {
	    		script {
			    	if("${ACTION_TYPE}" == "prod"){
			    		sh 'ssh jenkins@$PROD_SERVER1_IP sudo systemctl stop dvdtheque-rest.service'
			    	}else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
			    		sh 'echo nothing to do'
			    	}
		    	}
	    	}
	    }
	    stage('Stopping Prod2 Rest service') {
	    	steps {
	    		script {
			    	if("${ACTION_TYPE}" == "prod"){
			    		sh 'ssh jenkins@$PROD_SERVER2_IP sudo systemctl stop dvdtheque-rest.service'
			    	}else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
			    		sh 'echo nothing to do'
			    	}
		    	}
	    	}
	    }
        stage('Copying dvdtheque-rest-services') {
            steps {
                script {
			 		if("${ACTION_TYPE}" == "prod"){
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$NVERSION.jar jenkins@$DEV_SERVER1_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$NVERSION.jar jenkins@$DEV_SERVER2_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$NVERSION.jar jenkins@$PROD_SERVER1_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$NVERSION.jar jenkins@$PROD_SERVER2_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 		}else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER1_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 			sh 'scp dvdtheque-rest-services/target/dvdtheque-rest-services-$VERSION.jar jenkins@$DEV_SERVER2_IP:/opt/dvdtheque_rest_service/dvdtheque-rest-services.jar'
			 		}
			 	}
            }
        }
        stage('Sarting Dev1 Rest service') {
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER1_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Sarting Dev2 Rest service') {
        	steps {
	        	sh 'ssh jenkins@$DEV_SERVER2_IP sudo systemctl start dvdtheque-rest.service'
	        }
   		}
   		stage('Sarting Prod1 Rest service') {
   			steps {
	   			script {
		   			if("${ACTION_TYPE}" == "prod"){
		   				sh 'ssh jenkins@$PROD_SERVER1_IP sudo systemctl start dvdtheque-rest.service'
		   			}else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
		   				sh 'echo nothing to do'
		   			}
	   			}
	   		}
   		}
   		stage('Sarting Prod2 Rest service') {
   			steps {
	   			script {
		   			if("${ACTION_TYPE}" == "prod"){
		   				sh 'ssh jenkins@$PROD_SERVER2_IP sudo systemctl start dvdtheque-rest.service'
		   			}else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
		   				sh 'echo nothing to do'
		   			}
	   			}
	   		}
   		}
   		stage('Check status Dev1 Rest service') {
   			steps {
	        	sh "ssh jenkins@$DEV_SERVER1_IP sudo systemctl status dvdtheque-rest.service"
	        }
	   	}
	   	stage('Check status Dev2 Rest service') {
   			steps {
	        	sh "ssh jenkins@$DEV_SERVER2_IP sudo systemctl status dvdtheque-rest.service"
	        }
	   	}
		stage('Check status Prod1 Rest service') {
			steps {
				script {
				    if("${ACTION_TYPE}" == "prod"){
				         sh "ssh jenkins@$PROD_SERVER1_IP sudo systemctl status dvdtheque-rest.service"
				    }else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
				         sh 'echo nothing to do'
				    }
			    }
			}
		}
		stage('Check status Prod2 Rest service') {
			steps {
				script {
				    if("${ACTION_TYPE}" == "prod"){
				         sh "ssh jenkins@$PROD_SERVER2_IP sudo systemctl status dvdtheque-rest.service"
				    }else if ("${ACTION_TYPE}" == "dev-noTest" || "${ACTION_TYPE}" == "dev") {
				         sh 'echo nothing to do'
				    }
			    }
			}
		}
    }
}