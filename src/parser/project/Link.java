package parser.project;

public class Link {
    public String href;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "href:" + href + " \n" +
                        " }");
    }
}
