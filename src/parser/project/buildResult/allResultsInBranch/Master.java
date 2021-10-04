package parser.project.buildResult.allResultsInBranch;

public class Master {
    public String name;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "name:" + name + "\n" +
                "}");
    }
}
