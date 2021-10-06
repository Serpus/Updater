package stands;

import parser.project.Project;

abstract public class Stand {
    public abstract boolean isActive();
    public abstract void setProject(final Project project);
    public abstract Project getProject();
    public abstract String getName();
}
