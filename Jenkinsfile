pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                git 'https://github.com/PhanHoangf/udacity-java-web-p4.git'
                sh 'cd starter_code'
                sh 'mvn clean compile'
            }
        }
    }
}
