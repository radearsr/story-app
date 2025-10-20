pipeline {
    agent { label 'android' }

    environment {
        JAVA_HOME = "/opt/java/openjdk"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        ANDROID_HOME = "/opt/android-sdk"
        ANDROID_SDK_ROOT = "/opt/android-sdk"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Checking out project source...'
                checkout scm
            }
        }

        stage('Prepare Gradle') {
            steps {
                echo '⚙️ Setting gradle wrapper permissions...'
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Clean Build') {
            steps {
                echo '🧹 Cleaning previous builds...'
                sh './gradlew clean'
            }
        }

        stage('Assemble Release') {
            steps {
                echo '🏗️ Building release APK...'
                sh './gradlew assembleRelease'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo '📦 Archiving generated APK...'
                archiveArtifacts artifacts: 'app/build/outputs/apk/release/*.apk', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Build sukses! APK telah diarsipkan.'
        }
        failure {
            echo '❌ Build gagal! Periksa log di Jenkins console output.'
        }
    }
}
