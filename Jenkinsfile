pipeline {
    agent any

    options {
        timestamps()
    }

    triggers {
        cron('30 16 * * *') // Run daily at 16:30
    }

    tools {
        maven 'Maven'
        jdk 'jdk21'
    }

    environment {
        REPORT_URL = "${env.BUILD_URL}AllureReport/index.html"
    }

    stages {
        stage('📦 Checkout Code') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    echo "\033[1;44m=== 📦 CHECKOUT CODE ===\033[0m"
                    checkout scm
                }
            }
        }

        stage('🧱 Build Project') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    echo "\033[1;46m=== 🧱 BUILD PROJECT ===\033[0m"
                    dir('WebCatalogAuto') {
                        sh './mvnw clean compile -B -q'
                    }
                }
            }
        }

        stage('🧪 Run Tests') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    echo "\033[1;45m=== 🧪 RUN TESTS ===\033[0m"
                    dir('WebCatalogAuto') {
                        sh './mvnw test -B || true'
                    }
                }
            }
        }

        stage('📊 Generate Allure Report') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    echo "\033[1;42m=== 📊 GENERATE ALLURE REPORT ===\033[0m"
                    dir('WebCatalogAuto') {
                        sh './mvnw allure:report -B || true'
                    }
                }
            }
        }

        stage('🌐 Publish Allure Report') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    echo "\033[1;44m=== 🌐 PUBLISH ALLURE REPORT ===\033[0m"
                    dir('WebCatalogAuto') {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target/site/allure-maven-plugin',
                            reportFiles: 'index.html',
                            reportName: 'AllureReport'
                        ])
                        echo "🔗 Allure Report URL: ${REPORT_URL}"
                    }
                }
            }
        }
    }

    post {
        always {
            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                echo "\033[1;43m📦 Archiving test reports...\033[0m"
            }
            dir('WebCatalogAuto') {
                junit '**/target/surefire-reports/*.xml'
            }

            script {
                def recipients = 'test2711@yopmail.com, trikhang308@gmail.com, devtri232003@gmail.com'
                def emoji = currentBuild.currentResult == 'SUCCESS' ? '✅' :
                            currentBuild.currentResult == 'UNSTABLE' ? '⚠️' : '❌'
                def subject = "${emoji} Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                def cause = currentBuild.getBuildCauses().find { it.userName }?.userName ?: 'Automated Trigger'

                emailext(
                    subject: subject,
                    body: """
                        <h3>${emoji} Jenkins Build ${currentBuild.currentResult}</h3>
                        <p><b>Project:</b> ${env.JOB_NAME}</p>
                        <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                        <p><b>Status:</b> ${currentBuild.currentResult}</p>
                        <p><b>Triggered by:</b> ${cause}</p>
                        <hr>
                        <p><b>🔗 Allure Report:</b> <a href="${REPORT_URL}">Click to open Allure Report</a></p>
                        <p><b>🧾 Console Log:</b> <a href="${env.BUILD_URL}console">View Console Output</a></p>
                        <br><p>-- Jenkins CI/CD</p>
                    """,
                    to: recipients,
                    mimeType: 'text/html'
                )
            }
        }

        success {
            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                echo "\033[1;42m✅ Build finished successfully!\033[0m"
            }
        }

        unstable {
            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                echo "\033[1;43m⚠️ Build finished with warnings (UNSTABLE)!\033[0m"
            }
        }

        failure {
            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                echo "\033[1;41m❌ Build failed!\033[0m"
            }
        }
    }
}

