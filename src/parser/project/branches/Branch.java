package parser.project.branches;

import parser.project.Link;

public class Branch {
    public String shortName;
    public String shortKey;
    public Link link;
    public String key;
    public String name;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "shortName:" + shortName + "\n" +
                        "shortKey:" + shortKey + "\n" +
                        "link:" + link + "\n" +
                        "key:" + key + "\n" +
                        "name:" + name + "\n" +
                        " }\n");
    }
}
