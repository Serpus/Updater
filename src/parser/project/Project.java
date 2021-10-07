package parser.project;

import parser.project.branches.Branch;
import parser.project.buildResult.BuildResult;
import parser.project.buildResult.BuildResultStatus;
import parser.project.buildResult.allResultsInBranch.Results;
import parser.project.deploys.DeploymentResult;
import parser.project.deploys.DeploymentResultId;
import parser.project.deploys.Version;

import java.util.List;

public class Project {
    public int id;
    public String name;
    public PlanKey planKey;
    public String description;
    public List<Environments> environments;
    public Branch branch;
    public BuildResult buildResult;
    public BuildResultStatus buildResultStatus;
    public Results results;
    public Environments currentEnvironment;
    public Version[] version = new Version[3];
    public Version versionOp = new Version();
    public DeploymentResultId[] deploymentResultId = new DeploymentResultId[3];
    public DeploymentResultId deploymentResultIdOp = new DeploymentResultId();
    public DeploymentResult deploymentResult;

    @Override
    public String toString() {
        return String.format(
                "{ \n" +
                        "id:" + String.valueOf(id) + ",\n" +
                        "name:" + String.valueOf(name) + ",\n" +
                        "planKey:" + planKey + ",\n" +
                        "environments:" + environments + ",\n" +
                        "branch:" + branch + "\n" +
                        "buildResult:" + buildResult + "\n" +
                        "buildResultStatus:" + buildResultStatus + "\n" +
                        "results:" + results + "\n" +
                        "currentEnvironment:" + currentEnvironment + "\n" +
                        "version:" + version + "\n" +
                        "deploymentResultId:" + deploymentResultId + "\n" +
                        "deploymentResult:" + deploymentResult + "\n" +
                        " }");
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setBuildResult(BuildResult buildResult) {
        this.buildResult = buildResult;
    }

    public void setBuildResultStatus(BuildResultStatus buildResultStatus) {
        this.buildResultStatus = buildResultStatus;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public void setCurrentEnvironment(Environments environment) {
        this.currentEnvironment = environment;
    }

    public void setVersionOp(Version version) {
        this.versionOp = version;
    }

    public void setVersion(Version version, final int number) {
        this.version[number] = version;
    }

    public void setDeploymentResultId(DeploymentResultId deploymentResultId, final int number) {
        this.deploymentResultId[number] = deploymentResultId;
    }

    public void setDeploymentResultIdOp(DeploymentResultId deploymentResultId) {
        this.deploymentResultIdOp = deploymentResultId;
    }

    public void setDeploymentResult(DeploymentResult deploymentResult) {
        this.deploymentResult = deploymentResult;
    }
}
