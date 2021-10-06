package stands;

import parser.project.Project;

public class EIS4 extends Stand {
    private boolean isActive;
    private Project project;

    public EIS4() {
        isActive = true;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public String getName() {
        return "ЕИС-4";
    }
}
