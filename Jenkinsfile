// def gv // for global variable to load script.groovy
def gv

pipeline {
    agent any

    tools {
        maven 'Maven'
    }

   

    stages {

        stage('init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }

        stage ('Increment Version') {
            steps {
                script {
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "jma:${version}-${BUILD_NUMBER}"  
                }
            }
        }
        

        stage('Test') {
            steps {
               script {
                   echo "testing the application"
               }
            }
        }

        stage("Build") {
            when {
                expression {
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    gv.buildJar()
                }
            }
        }

        stage("Build Image") {
            steps {
                script {
                    echo "building the docker image and push to dockerhub" 
                    withCredentials([usernamePassword(credentialsId:'docker-hub-credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        sh "docker build -t arman04/jma:$IMAGE_NAME ."
                        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
                        sh "docker push arman04/jma:$IMAGE_NAME"
                    }
                }
            }
        }


        stage("Deploy") {
            when {
                expression {
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    echo "deploying the application"
                }
            }
        }

    }

}