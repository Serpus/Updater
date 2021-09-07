package parser.project;

public class Environments {
    public Integer id;
    public Key key;
    public String name;
    public String description;
    public String deploymentProjectId;
    public Operations operations;
    public String position;
    public String configurationState;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "id:" + String.valueOf(id) + ",\n" +
                        "key:" + key + ",\n" +
                        "name:" + String.valueOf(name) + ",\n" +
                        "deploymentProjectId:" + String.valueOf(deploymentProjectId) + ",\n" +
                        " }");
    }
}
