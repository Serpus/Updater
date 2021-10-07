package stands;

import parser.project.Project;

public class EIS7 extends Stand {
    private boolean isActive;
    private Project projectOp;

    public EIS7() {
        isActive = true;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setProjectOp(Project projectOp) {
        this.projectOp = projectOp;
    }

    @Override
    public Project getProjectOp() {
        return projectOp;
    }

    @Override
    public String getName() {
        return "ЕИС-7";
    }
}
