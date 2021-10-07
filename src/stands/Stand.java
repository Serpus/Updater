package stands;

import parser.project.Project;

abstract public class Stand {
    public abstract boolean isActive();
    public abstract void setProjectOp(final Project projectOp);
    public abstract Project getProjectOp();
    public abstract String getName();
}
