package parser.project.branches;

import java.util.List;

public class Branches {
    public int size;
    public List<Branch> branch;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "size:" + size + "\n" +
                        "branch:" + branch + "\n" +
                        " }");
    }
}
