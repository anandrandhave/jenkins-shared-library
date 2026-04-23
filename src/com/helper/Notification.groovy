package com.helper

class Notification implements Serializable {
    def script

    Notification(script) {
        this.script = script
    }

    def send(String status) {
        def jobName  = script.env.JOB_NAME
        def buildNum = script.env.BUILD_NUMBER
        def branch   = script.env.BRANCH_NAME ?: "main"
        def duration = script.currentBuild.durationString.replace(' and counting', '')

        // 1. Condition: Skip Started for non-main branches
        if (branch != "main" && status == "Started") {
            return 
        }

        def subject = "${status.toUpperCase()}: ${jobName} [${buildNum}]"
        
        // 2. Logic for Icons (Added Unstable ⚠️)
        def icon = '❓'
        if (status == 'Success') icon = '✅'
        else if (status == 'Started') icon = '🚀'
        else if (status == 'Failure') icon = '❌'
        else if (status == 'Unstable') icon = '⚠️'

        // 3. Send Email
        try {
            script.emailext (
                to: "admin251807@gmail.com",
                subject: subject,
                body: "<h3>${subject}</h3><b>Status:</b> ${status}<br><b>Duration:</b> ${duration}",
                mimeType: 'text/html'
            )
        } catch (Exception e) { script.echo "Email Error: ${e.message}" }

        // 4. Send Slack
        def base = "https://hooks.slack.com/services/"
        def token = "T0B024E98QG/B0AV1G8CJQ1/pZbUeTa4ONk1I1p4xNNwD7EC"
        def payload = "{\"text\": \"${icon} *${subject}*\\n*Duration:* ${duration}\"}"
        
        try {
            script.sh "curl -s -X POST -H 'Content-type: application/json' --data '${payload}' ${base}${token}"
        } catch (Exception e) { script.echo "Slack Error: ${e.message}" }
    }
}
