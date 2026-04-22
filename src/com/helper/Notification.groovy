package com.helper

class Notification implements Serializable {
    def script

    Notification(script) {
        this.script = script
    }

    def send(String status) {
        def recipient = "admin251807@gmail.com"
        def subject = "${status.toUpperCase()}: Job '${script.env.JOB_NAME}' [Build #${script.env.BUILD_NUMBER}]"

        // Custom HTML Template for the email body
        def details = """
            <h3>Build Status: ${status}</h3>
            <p><b>Job:</b> ${script.env.JOB_NAME}</p>
            <p><b>Build Number:</b> ${script.env.BUILD_NUMBER}</p>
            <p><b>Duration:</b> ${script.currentBuild.durationString}</p>
            <p><b>Console Logs:</b> <a href="${script.env.BUILD_URL}console">View Logs</a></p>
            <hr>
            <p><i>Sent automatically via Jenkins Shared Library</i></p>
        """

        script.echo "Attempting to send ${status} email to ${recipient}..."

        // Actual Jenkins Email Extension Plugin command
        script.emailext (
            to: recipient,
            subject: subject,
            body: details,
            mimeType: 'text/html'
        )
    }
}
