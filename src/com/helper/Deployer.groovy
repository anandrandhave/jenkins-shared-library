package com.helper

class Deployer implements Serializable {
    def script

    // Constructor to allow access to Jenkins pipeline steps
    Deployer(script) {
        this.script = script
    }

    def validate(String env) {
        script.echo "--- PHASE: VALIDATION ---"
        script.echo "Checking configuration for ${env} environment..."
    }

    def deploy(String env) {
        script.echo "--- PHASE: DEPLOYMENT ---"
        script.echo "Deploying application to ${env}..."
        if (env == "prod") {
            script.echo "Enabling production-grade monitoring..."
        }
    }

    def rollback(String env) {
        script.echo "--- PHASE: ROLLBACK ---"
        script.echo "Deployment failed! Reverting ${env} to the last stable version."
    }
}
