import com.helper.Deployer

def call(String environment) {
    def deployer = new Deployer(this)

    deployer.validate(environment)
    deployer.deploy(environment)
}
