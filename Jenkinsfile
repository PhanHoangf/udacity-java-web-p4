pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                git 'https://github.com/PhanHoangf/udacity-java-web-p4.git'
                sh './mvnw clean compile'
            }
        }
    }
}
