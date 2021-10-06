package parser.project.buildResult.allResultsInBranch;

public class Results {
    public Result[] result;
    public Result oneResult;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "result:" + result + "\n" +
                "ontResult:" + oneResult + "\n" +
                "}");
    }
}
