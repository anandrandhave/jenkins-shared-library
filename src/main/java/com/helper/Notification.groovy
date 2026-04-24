package com.helper

class Notification implements Serializable {
    def script
    Notification(script) { this.script = script }

    def send(String status) {
        def jobName  = script.env.JOB_NAME
        def buildNum = script.env.BUILD_NUMBER
        def buildUrl = script.env.BUILD_URL
        def branch   = script.env.BRANCH_NAME ?: "main"
        def duration = script.currentBuild.durationString.replace(' and counting', '')
        
        // Extract Environment from parameters
        def envName  = script.params.ENVIRONMENT ?: "N/A"

        // Capture Changes (Git Commits)
        def changeLog = ""
        def changeLogSets = script.currentBuild.changeSets
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                changeLog += "- ${entry.msg} [${entry.author}]\\n"
            }
        }
        if (changeLog == "") { changeLog = "No changes detected" }

        if (branch != "main" && status == "Started") { return }

        def subject = "${status.toUpperCase()}: ${jobName} [${buildNum}]"
        def icon = (status == 'Success') ? '✅' : (status == 'Started' ? '🚀' : (status == 'Failure' ? '❌' : '⚠️'))

        // Constant Email Body
        def emailBody = """
            <h3>${subject}</h3>
            <b>Branch:</b> ${branch}<br>
            <b>Environment:</b> ${envName}<br>
            <b>Status:</b> ${status}<br>
            <b>Duration:</b> ${duration}<br>
            <b>Console Output:</b> <a href='${buildUrl}'>${buildUrl}</a>
        """.stripIndent()

        // Send Email
        try {
            script.emailext(to: "admin251807@gmail.com", subject: subject, body: emailBody, mimeType: 'text/html')
        } catch (Exception e) { script.echo "Email Failed: ${e.message}" }

        // Send Detailed Slack Notification
        try {
            def base = "https://hooks.slack.com/services/"
            def token = "T0B024E98QG/B0AV1G8CJQ1/pZbUeTa4ONk1I1p4xNNwD7EC"
            
            // Constructing the Slack Payload with all details
            def payload = """
            {
                "text": "${icon} *${subject}*",
                "attachments": [
                    {
                        "color": "${status == 'Success' ? 'good' : (status == 'Failure' ? 'danger' : 'warning')}",
                        "fields": [
                            {"title": "Branch", "value": "${branch}", "short": true},
                            {"title": "Environment", "value": "${envName}", "short": true},
                            {"title": "Status", "value": "${status}", "short": true},
                            {"title": "Duration", "value": "${duration}", "short": true},
                            {"title": "Changes", "value": "${changeLog}", "short": false},
                            {"title": "Console Output", "value": "<${buildUrl}|Click here to view logs>", "short": false}
                        ]
                    }
                ]
            }
            """.stripIndent()

            script.sh "curl -s -X POST -H 'Content-type: application/json' --data '${payload}' ${base}${token}"
        } catch (Exception e) { script.echo "Slack Failed: ${e.message}" }
    }
}
