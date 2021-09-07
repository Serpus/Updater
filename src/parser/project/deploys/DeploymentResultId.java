package parser.project.deploys;

import parser.project.Link;

public class DeploymentResultId {
    public String deploymentResultId;
    public Link link;

    @Override
    public String toString() {
        return String.format("{ \n" +
                "deploymentResultId: " + deploymentResultId + "\n" +
                "href" + link + "\n" +
                " }");
    }
}
