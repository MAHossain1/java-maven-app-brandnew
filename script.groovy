def testApp() {
    echo "testing the application..."
}


/*
this function come from shared library
*/
// def buildJar() {
//     echo "building the application"
//     sh 'mvn clean package'
// } 

// def buildImage() {
//     echo "building the docker image and push to dockerhub" 
//     withCredentials(
//         [usernamePassword(credentialsId:'docker-hub-credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]
//     ) {
//         sh 'docker build -t arman04/jma:1.0.0 .'
//         sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
//         sh 'docker push arman04/jma:1.0.0'
//     }
// }      

  
return this