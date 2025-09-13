
@Library('jenkins-shared-library')

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
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'jenkins-shared-library'
                }
            }
            steps {
                script {
                    buildJar()
                }
            }
        }

        stage("Build Image") {
            steps {
                script {
                    buildImage 'arman04/jma:1.1.0'
                }
            }
        }


        stage("Deploy") {
            when {
                expression {
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'jenkins-shared-library'
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