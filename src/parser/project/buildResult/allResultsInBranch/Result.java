package parser.project.buildResult.allResultsInBranch;

import parser.project.Link;

public class Result {
    public Link link;
    public String buildResultKey;
    public String buildNumber;
    public String buildState;
    public PlanName plan;

    @Override
    public String toString() {
        return String.format("{ \n" +
                "link:" + link + "\n" +
                "buildResultKey:" + buildResultKey + "\n" +
                "buildNumber:" + buildNumber + "\n" +
                "buildState:" + buildState + "\n" +
                "plan:" + plan + "\n" +
                "}");
    }
}
