package com.helper

class Notification implements Serializable {
    def script

    Notification(script) {
        this.script = script
    }

    def send(String status) {
        def jobName  = script.env.JOB_NAME
        def buildNum = script.env.BUILD_NUMBER
        def buildUrl = script.env.BUILD_URL
        def branch   = script.env.BRANCH_NAME ?: "main"
        // Keep duration consistent
        def duration = script.currentBuild.durationString.replace(' and counting', '')

        if (branch != "main" && status == "Started") { return }

        def subject = "${status.toUpperCase()}: ${jobName} [${buildNum}]"
        
        // Logic for Icons
        def icon = (status == 'Success') ? '✅' : (status == 'Started' ? '🚀' : (status == 'Failure' ? '❌' : '⚠️'))

        // Constant Body Template
        def emailBody = """
            <h3>${subject}</h3>
            <b>Branch:</b> ${branch}<br>
            <b>Status:</b> ${status}<br>
            <b>Duration:</b> ${duration}<br>
            <b>Console Output:</b> <a href='${buildUrl}'>${buildUrl}</a>
        """.stripIndent()

        // Send Email
        try {
            script.emailext (
                to: "admin251807@gmail.com",
                subject: subject,
                body: emailBody,
                mimeType: 'text/html'
            )
        } catch (Exception e) { script.echo "Email Failed: ${e.message}" }

        // Send Slack
        try {
            def base = "https://hooks.slack.com/services/"
            def token = "T0B024E98QG/B0AV1G8CJQ1/pZbUeTa4ONk1I1p4xNNwD7EC"
            def payload = "{\"text\": \"${icon} *${subject}*\\n*Duration:* ${duration}\\n${buildUrl}\"}"
            script.sh "curl -s -X POST -H 'Content-type: application/json' --data '${payload}' ${base}${token}"
        } catch (Exception e) { script.echo "Slack Failed: ${e.message}" }
    }
}
