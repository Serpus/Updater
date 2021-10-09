package parser.project.buildResult.allResultsInBranch;

public class OneResult {
    public Result oneResult;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "ontResult:" + oneResult + "\n" +
                "}");
    }
}
