pipeline {
    agent any

    environment {
        AWS_REGION    = 'us-east-1'
        S3_BUCKET     = 'cicd-artifacts-3ecbf78f'
        BUILD_VERSION = "1.0.${BUILD_NUMBER}"
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Unit Tests') {
            steps {
                sh 'mvn -B clean verify'
            }
            post {
                always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn -B sonar:sonar -Dsonar.projectKey=demo-app -Dsonar.projectName=demo-app'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
            }
        }

        stage('Upload Artifact to S3') {
            steps {
                sh """
                  aws s3 cp target/demo-app-1.0.0.jar \
                    s3://${S3_BUCKET}/demo-app/${BUILD_VERSION}/demo-app-${BUILD_VERSION}.jar \
                    --region ${AWS_REGION}
                """
            }
        }
    }

    post {
        success { echo "Build #${BUILD_NUMBER} (v${BUILD_VERSION}) succeeded" }
        failure { echo "Build #${BUILD_NUMBER} failed" }
    }
}
