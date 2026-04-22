package com.helper

class Notification implements Serializable {
    def script

    Notification(script) {
        this.script = script
    }

    def send(String status) {
        // 1. Gather dynamic build data
        def jobName  = script.env.JOB_NAME
        def buildNum = script.env.BUILD_NUMBER
        def buildUrl = script.env.BUILD_URL
        def branch   = script.env.BRANCH_NAME ?: "main"
        def duration = script.currentBuild.durationString.replace(' and counting', '')

        // 2. Branch-based Condition
        // Example: We skip 'Started' alerts for feature branches to reduce noise
        if (branch != "main" && status == "Started") {
            script.echo "Skipping Start notification for non-main branch: ${branch}"
            return 
        }

        // 3. Prepare the message content
        def subject = "${status.toUpperCase()}: ${jobName} [${buildNum}]"
        def details = """
            <b>Branch:</b> ${branch}<br>
            <b>Status:</b> ${status}<br>
            <b>Duration:</b> ${duration}<br>
            <b>Console Output:</b> <a href='${buildUrl}'>${buildUrl}</a>
        """.stripIndent()

        // 4. Send Email (Graceful Failure Handling)
        try {
            script.emailext (
                to: "admin251807@gmail.com",
                subject: subject,
                body: "<h3>${subject}</h3>${details}",
                mimeType: 'text/html'
            )
        } catch (Exception e) {
            script.echo "Email Notification Failed: ${e.message}"
        }

        // 5. Send Slack (Graceful Failure Handling)
        def base = "https://hooks.slack.com/services/"
        def token = "T0B024E98QG/B0AV1G8CJQ1/pZbUeTa4ONk1I1p4xNNwD7EC"
        def icon = (status == 'Success') ? '✅' : (status == 'Started' ? '🚀' : '❌')

        def slackPayload = "{\"text\": \"${icon} *${subject}*\\n*Branch:* ${branch}\\n*Duration:* ${duration}\\n${buildUrl}\"}"

        try {
            script.sh "curl -s -X POST -H 'Content-type: application/json' --data '${slackPayload}' ${base}${token}"
        } catch (Exception e) {
            script.echo "Slack Notification Failed: ${e.message}"
        }
    }
}
