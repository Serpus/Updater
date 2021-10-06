package parser.project.buildResult.allResultsInBranch;

public class PlanName {
    public String name;
    public String shortName;
    public Master master;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "name:" + name + "\n" +
                "shortName:" + shortName + "\n" +
                "master:" + master + "\n" +
                "}");
    }
}
