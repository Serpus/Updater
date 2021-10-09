package parser.project.deploys;

public class DeploymentResult {
    public String name;
    public String creatorDisplayName;
    public String deploymentState;
    public String lifeCycleState;

    @Override
    public String toString() {
        return String.format("{ \n" +
                "name: " + name + "\n" +
                "creatorDisplayName: " + creatorDisplayName + "\n" +
                "deploymentState: " + deploymentState + "\n" +
                "lifeCycleState: " + lifeCycleState + "\n" +
                " }");
    }
}
