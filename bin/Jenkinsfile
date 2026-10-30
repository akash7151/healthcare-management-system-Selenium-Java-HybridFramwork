pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser for test execution')
        choice(name: 'ENVIRONMENT', choices: ['local', 'qa', 'staging'], description: 'Target environment')
        choice(name: 'TEST_SUITE', choices: ['all', 'login', 'receptionist', 'billing', 'admin', 'smoke'], description: 'Test suite to run')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
        string(name: 'THREAD_COUNT', defaultValue: '3', description: 'Parallel thread count')
    }

    environment {
        APP_URL = 'http://localhost:5173/'
        JAVA_HOME = tool name: 'jdk-21', type: 'jdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
        timeout(time: 60, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/15 * * * *')
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Verify Java Version') {
            steps {
                sh 'java -version'
            }
        }

        stage('Verify Maven Installation') {
            steps {
                sh 'mvn -version'
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'mvn -B dependency:resolve'
            }
        }

        stage('Verify Application Availability') {
            steps {
                script {
                    def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' ${APP_URL}", returnStdout: true).trim()
                    if (response != '200') {
                        error("Application not reachable at ${APP_URL}. Start frontend (port 5173) and backend (port 4000) before running tests.")
                    }
                }
            }
        }

        stage('Run Selenium Automation Tests') {
            steps {
                withCredentials([
                    string(credentialsId: 'healthcare-admin-email', variable: 'AUTH_ADMIN_EMAIL'),
                    string(credentialsId: 'healthcare-admin-password', variable: 'AUTH_ADMIN_PASSWORD'),
                    string(credentialsId: 'healthcare-receptionist-email', variable: 'AUTH_RECEPTIONIST_EMAIL'),
                    string(credentialsId: 'healthcare-receptionist-password', variable: 'AUTH_RECEPTIONIST_PASSWORD'),
                    string(credentialsId: 'healthcare-billing-email', variable: 'AUTH_BILLING_EMAIL'),
                    string(credentialsId: 'healthcare-billing-password', variable: 'AUTH_BILLING_PASSWORD')
                ]) {
                    script {
                        def testArg = resolveTestSuite(params.TEST_SUITE)
                        sh """
                            mvn -B clean test \
                              -Dbrowser=${params.BROWSER} \
                              -Dheadless=${params.HEADLESS} \
                              -Denvironment=${params.ENVIRONMENT} \
                              -Dthread.count=${params.THREAD_COUNT} \
                              ${testArg}
                        """
                    }
                }
            }
        }

        stage('Generate Extent Reports') {
            steps {
                echo 'Extent report generated at reports/ExtentReport.html'
            }
        }

        stage('Publish TestNG Results') {
            steps {
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            }
        }

        stage('Archive Screenshots') {
            steps {
                archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true, fingerprint: true
                archiveArtifacts artifacts: 'reports/ExtentReport.html', allowEmptyArchive: true, fingerprint: true
                archiveArtifacts artifacts: 'logs/automation.log', allowEmptyArchive: true, fingerprint: true
            }
        }

        stage('Send Email Notifications') {
            steps {
                script {
                    emailext(
                        subject: "[Healthcare Automation] Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                        body: """
                            <p>Healthcare Management automation finished.</p>
                            <ul>
                              <li>Status: ${currentBuild.currentResult}</li>
                              <li>Suite: ${params.TEST_SUITE}</li>
                              <li>Browser: ${params.BROWSER}</li>
                              <li>Environment: ${params.ENVIRONMENT}</li>
                              <li>Duration: ${currentBuild.durationString}</li>
                              <li>URL: ${env.BUILD_URL}</li>
                            </ul>
                            <p>See archived Extent report and screenshots.</p>
                        """,
                        to: '${DEFAULT_RECIPIENTS}',
                        attachLog: true
                    )
                }
            }
        }

        stage('Finalize Build') {
            steps {
                echo 'Pipeline completed.'
            }
        }
    }

    post {
        failure {
            echo 'Critical tests failed. Build marked unstable/failed.'
        }
        always {
            cleanWs(deleteDirs: true, patterns: [[pattern: 'target', type: 'INCLUDE']])
        }
    }
}

def resolveTestSuite(String suite) {
    switch (suite) {
        case 'login': return '-Dtest=LoginTests'
        case 'receptionist': return '-Dtest=ReceptionistTests'
        case 'billing': return '-Dtest=BillingStaffTests'
        case 'admin': return '-Dtest=ReceptionistAdminTests'
        case 'smoke': return '-Dtest=LoginTests#testValidAdminLogin,ReceptionistTests#testReceptionistDashboardAndMenu,BillingStaffTests#testBillingDashboardAndMenu,ReceptionistAdminTests#testAdminDashboardAndMenu'
        default: return ''
    }
}
