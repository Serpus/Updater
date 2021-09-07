package parser.project.deploys;

public class Version {
    public String id;
    public String name;
    public String creatorDisplayName;
    public String planBranchName;

    @Override
    public String toString() {
        return String.format("" +
                "{ \n" +
                "id:" + id + "\n" +
                "name:" + name + "\n" +
                "creatorDisplayName:" + creatorDisplayName + "\n" +
                "planBranchName:" + planBranchName + "\n" +
                " }");
    }
}
