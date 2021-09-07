package parser.project.buildResult;

import parser.project.Link;

public class BuildResultStatus {
    public Link link;
    public String key;
    public String buildState;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "link:" + link + "\n" +
                        "key:" + key + "\n" +
                        "buildState:" + buildState + "\n" +
                        "}");
    }
}
