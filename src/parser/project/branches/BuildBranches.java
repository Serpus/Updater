package parser.project.branches;

public class BuildBranches {
    public Branches branches;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "branches:" + branches + "\n" +
                        " }");
    }
}
