package parser.project.buildResult.allResultsInBranch;

public class AllBuildResultInBranchKey {
    public Results results;
    @Override
    public String toString() {
        return String.format("{ \n" +
                "results:" + results + "\n" +
                "}");
    }
}
