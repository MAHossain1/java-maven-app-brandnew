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


        // stage('Commit version update') {
        //     steps {
        //         script {
        //             withCredentials([usernamePassword(credentialsId: 'jenkins-credentials', passwordVariable: 'GIT_TOKEN', usernameVariable: 'GIT_USER')]) {
        //                 sh '''
        //                     git config --global user.email "jenkins@example.com"
        //                     git config --global user.name "Jenkins"

        //                     # Make sure we are on main and up-to-date
        //                     git fetch origin
        //                     git checkout -B main origin/main

        //                     # Add only pom.xml to avoid junk
        //                     git add pom.xml

        //                     if git diff --cached --quiet; then
        //                         echo "No changes to commit"
        //                     else
        //                         git commit -m "Incrementing the version of the application"
        //                         git push https://${GIT_USER}:${GIT_TOKEN}@github.com/MAHossain1/java-maven-app-brandnew.git main
        //                     fi
        //                 '''
        //             }
        //         }
        //     }
        // }



       stage('Commit version update') {
            steps {
                script {
                    sshagent(['jenkins-ssh-github']) {
                        sh '''
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "Jenkins"

                            # Make sure correct remote is set
                            git remote set-url origin git@github.com:MAHossain1/java-maven-app-brandnew.git

                            git add pom.xml
                            if git diff --cached --quiet; then
                                echo "No changes to commit"
                            else
                                git commit -m "Incrementing the version of the application"
                                git push origin main
                            fi
                        '''
                    }
                }
            }
        }



    }

}