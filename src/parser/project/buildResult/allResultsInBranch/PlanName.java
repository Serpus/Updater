package parser.project.buildResult.allResultsInBranch;

public class PlanName {
    public String name;
    public Master master;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "name:" + name + "\n" +
                "master:" + master + "\n" +
                "}");
    }
}
