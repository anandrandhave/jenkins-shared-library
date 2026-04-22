package com.helper

class Notification implements Serializable {
    def script

    Notification(script) {
        this.script = script
    }

    def send(String status) {
        // Define message details once to use for both Email and Slack
        def subject = "${status}: Job '${script.env.JOB_NAME}' [${script.env.BUILD_NUMBER}]"
        def details = "Check console output at: ${script.env.BUILD_URL}"

        // Define color for Slack (good = green, danger = red)
        def slackColor = (status == 'Success') ? 'good' : 'danger'

        // 1. Email Notification (to your Mailtrap Sandbox)
        script.emailext (
            to: "admin251807@gmail.com",
            subject: subject,
            body: details,
            mimeType: 'text/html'
        )

        // 2. Slack Notification (Using your specific IDs)
        script.slackSend (
            tokenCredentialId: 'slack-webhook', 
            channel: 'C0B024EQX32', 
            color: slackColor,
            message: "${subject}\n${details}"
        )
    }
}
