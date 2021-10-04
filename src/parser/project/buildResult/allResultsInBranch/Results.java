package parser.project.buildResult.allResultsInBranch;

public class Results {
    public Result[] result;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "result:" + result + "\n" +
                "}");
    }
}
