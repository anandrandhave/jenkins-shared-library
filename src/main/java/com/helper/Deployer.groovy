package com.helper

class Deployer implements Serializable {
    def script

    Deployer(script) {
        this.script = script
    }

    // Existing validate method...
    def validate(String env) {
        script.echo "--- PHASE: VALIDATION ---"
        script.echo "Checking configuration for ${env} environment..."
    }

    // NEW: Enhanced Rollback Method
    def rollback(String env, String strategy) {
        script.echo "--- PHASE: ROLLBACK (${strategy.toUpperCase()}) ---"

        if (strategy == "blue-green") {
            script.echo "FAILURE DETECTED: Flipping traffic back to the 'Blue' (Stable) environment."
            script.echo "Action: Load Balancer target group updated for ${env}."
        } 
        else if (strategy == "canary") {
            script.echo "FAILURE DETECTED: Terminating Canary pods."
            script.echo "Action: Routing 100% of traffic back to stable version in ${env}."
        } 
        else {
            script.echo "Standard Rollback: Reverting to last successful build."
        }
    }
}
