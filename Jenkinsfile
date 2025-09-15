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


        stage('Increment Version') {
            steps {
                script {
                    sh '''
                    mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit
                    '''

                    def version = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    env.IMAGE_NAME = "jma:${version}-${BUILD_NUMBER}"
                    echo "Using IMAGE_NAME=${env.IMAGE_NAME}"
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
                        sh "docker build -t arman04/$IMAGE_NAME ."
                        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
                        sh "docker push arman04/$IMAGE_NAME"
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


        stage('Commit version update') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'jenkins-credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "Jenkins"'
                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'
                        sh "git remote set-url origin https://${USERNAME}:${PASSWORD}@github.com/MAHossain1/java-maven-app-brandnew.git"
                        sh 'git add .'
                        sh '''
                            if git status --porcelain | grep .; then
                                git commit -m "Incrementing the version of the application"
                            else
                                echo "No changes to commit"
                            fi
                        '''
                        sh 'git push origin HEAD:main || echo "Push failed—check branch or remote"'
                    }
                }
            }
        }

        // stage('Commit version update') {
        //     steps {
        //         script {
        //             sshagent(['github-ssh-key']) {
        //                 sh 'git config --global user.email "jenkins@example.com"'
        //                 sh 'git config --global user.name "Jenkins"'
        //                 sh 'git remote set-url origin git@github.com:MAHossain1/java-maven-app-again.git'
        //                 sh 'git status'
        //                 sh 'git add .'
        //                 sh 'ssh-keyscan github.com >> ~/.ssh/known_hosts'
        //                 sh '''
        //                     if git status --porcelain | grep .; then
        //                         git commit -m "Incrementing the version of the application"
        //                     else
        //                         echo "No changes to commit"
        //                     fi
        //                 '''

        //                 sh 'git push origin HEAD:jenkins-jobs'
        //             }
        //         }
        //     }
        // }


    }

}