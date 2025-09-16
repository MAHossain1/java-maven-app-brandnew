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
                    '''
                    def pomContent = readFile('pom.xml')
                    def matcher = pomContent =~ '<version>(.+)</version>'
                    if (matcher) {
                        def version = matcher[0][1]
                        env.IMAGE_NAME = "jma:${version}-${BUILD_NUMBER}"
                        echo "New version: ${version}, Image name: ${env.IMAGE_NAME}"
                    } else {
                        error "Failed to parse version from pom.xml:\n${pomContent}"
                    }
                    // Stash the updated pom.xml
                    stash name: 'pom', includes: 'pom.xml'
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
                    // Unstash pom.xml to ensure the updated version is used
                    unstash 'pom'
                    gv.buildJar()
                }
            }
        }

        stage("Build Image") {
            steps {
                script {
                    echo "building the docker image and push to dockerhub"
                    // Unstash pom.xml for consistency
                    unstash 'pom'
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
                    // Unstash pom.xml if needed for deployment
                    unstash 'pom'
                }
            }
        }

        // Temporarily disable or modify Commit version update to avoid reset
        stage('Commit version update') {
            when {
                expression { false } // Disable until you're ready to implement
            }
            steps {
                script {
                    echo "Skipping commit for now"
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