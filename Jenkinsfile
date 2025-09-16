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
                    echo "Incrementing the application version"
                    sh '''
                        echo "Workspace: $PWD"
                        ls -l pom.xml
                        chmod 664 pom.xml || true
                        mvn --batch-mode -f pom.xml build-helper:parse-version versions:set \
                            -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
                            versions:commit -DgenerateBackupPoms=false
                        echo "pom.xml after update:"
                        cat pom.xml | grep '<version>'
                        cp pom.xml /tmp/pom.xml.after || true
                    '''
                    def version = sh(script: 'mvn --batch-mode -f pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()
                    env.IMAGE_NAME = "jma:${version}-${BUILD_NUMBER}"
                    echo "New version: ${version}, Image name: ${env.IMAGE_NAME}"
                    stash name: 'pom', includes: 'pom.xml'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo "testing the application"
                    unstash 'pom'
                    sh 'cat pom.xml | grep "<version>"'
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
                    unstash 'pom'
                    sh 'cat pom.xml | grep "<version>"'
                    gv.buildJar()
                }
            }
        }

        stage("Build Image") {
            steps {
                script {
                    echo "building the docker image and push to dockerhub"
                    unstash 'pom'
                    sh 'cat pom.xml | grep "<version>"'
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
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
                    unstash 'pom'
                    sh 'cat pom.xml | grep "<version>"'
                }
            }
        }

        stage('Commit version update') {
            steps {
                script {
                    sshagent(['jenkins-ssh-github']) {
                        sh """
                            # Configure Git
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "Jenkins"

                            # Add GitHub to known_hosts
                            mkdir -p ~/.ssh
                            ssh-keyscan github.com >> ~/.ssh/known_hosts

                            # Ensure remote is correct
                            git remote set-url origin git@github.com:MAHossain1/java-maven-app-brandnew.git

                            # Stage only pom.xml
                            git add pom.xml

                            # Commit only if pom.xml has changes
                            if git status --porcelain pom.xml | grep .; then
                                git commit -m "Increment version to ${env.IMAGE_NAME}"
                            else
                                echo "No changes to pom.xml to commit"
                            fi

                            # Push changes to main
                            git push origin main
                        """
                    }
                }
            }
        }
    }
}
 // stage('Commit version update') {
        //     steps {
        //         script {
        //             sshagent(['jenkins-ssh-github']) {
        //                 sh '''
        //                     # Configure Git
        //                     git config --global user.email "jenkins@example.com"
        //                     git config --global user.name "Jenkins"

        //                     # Add GitHub to known_hosts
        //                     mkdir -p ~/.ssh
        //                     ssh-keyscan github.com >> ~/.ssh/known_hosts

        //                     # Ensure remote is correct
        //                     git remote set-url origin git@github.com:MAHossain1/java-maven-app-again.git

        //                     # Fetch and reset to remote main to avoid push conflicts
        //                     git fetch origin main
        //                     git reset --hard origin/main

        //                     # Add only version-related files (e.g., pom.xml)
        //                     git add pom.xml

        //                     # Commit only if there are changes
        //                     if git status --porcelain | grep .; then
        //                         git commit -m "Incrementing the version of the application"
        //                     else
        //                         echo "No changes to commit"
        //                     fi

        //                     # Push changes to main
        //                     git push origin HEAD:main
        //                 '''
        //             }
        //         }
        //     }
        // }