package parser.project.buildResult;

import parser.project.Link;

public class BuildResult {
    public String planKey;
    public int buildNumber;
    public String buildResultKey;
    public Link link;

    @Override
    public String toString() {
        return String.format("{ \n" +
                "planKey:" + planKey + "\n" +
                "buildNumber:" + buildNumber + "\n" +
                "buildResultKey:" + buildResultKey + "\n" +
                "link:" + link + "\n" +
                "}");
    }
}
