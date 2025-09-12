def gv

pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

   

    stages {

        stage('init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }

        // stage('Checkout') {
        //     steps {
        //         git branch: 'main',
        //             url: 'git@github.com:YourUser/YourRepo.git',
        //             credentialsId: 'github-ssh'
        //     }
        // }

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
                    echo "building the application"
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