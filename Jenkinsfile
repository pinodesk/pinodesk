pipeline {
    agent any
    stages {
        stage('Build') {
            when {
                branch 'master'
            }
            steps {
                echo 'Building executable'
                bat '.\\mvnw.cmd --show-version --batch-mode --no-transfer-progress clean package -Pdist -DskipTests'
            }
            post {
                success {
                    echo 'Build success'
                    archiveArtifacts 'target/*.exe'
                    mail to: '6z9i93lm@duck.com',
                         subject: "Success Pipeline: ${currentBuild.fullDisplayName}",
                         body: """
Pipeline ${currentBuild.fullDisplayName} has been successfully executed.

View the details here:

${env.BUILD_URL}.
"""
                    echo 'Clean up the workspace'
                    deleteDir()
                }
                failure {
                    echo 'Build failed'
                    mail to: 'a54xfbp1@duck.com',
                         subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                         body: """
There is a failure in pipeline ${currentBuild.fullDisplayName}.

View the details here:

${env.BUILD_URL}.
"""
                    echo 'Clean up the workspace'
                    deleteDir()
                }
            }
        }
    }
}
