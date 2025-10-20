pipeline {
    agent { label 'android' }

    environment {
        ANDROID_HOME = "/opt/android-sdk"
        ANDROID_SDK_ROOT = "/opt/android-sdk"
        PATH = "$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/34.0.0"
        JAVA_HOME = "/usr/lib/jvm/java-11-openjdk-amd64"
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
