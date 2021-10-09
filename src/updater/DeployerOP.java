package updater;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.log4j.Logger;
import parser.project.Environments;
import parser.project.Project;
import parser.project.branches.Branch;
import parser.project.branches.BuildBranches;
import parser.project.buildResult.allResultsInBranch.AllBuildResultInBranchKey;
import parser.project.buildResult.allResultsInBranch.Result;
import parser.project.deploys.DeploymentResult;
import parser.project.deploys.DeploymentResultId;
import parser.project.deploys.Version;
import stands.Stand;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeployerOP extends Builder {
    private static final Logger log = Logger.getLogger(DeployerOP.class);

    public DeployerOP(String username, String password) {
        super(username, password);
    }

    private List<Project> opProjects = new ArrayList<>();
    public List<Project> getOpProjects() {
        return opProjects;
    }

    private List<Project> buildsOpToDeploy = new ArrayList<>();
    public List<Project> getBuildsOpToDeploy() {
        return buildsOpToDeploy;
    }

    private List<Result> successBuildsInEpzBd;
    private List<Result> successBuildsInEpz;
    private List<Result> successBuildsInSphinx;

    private HashMap<Integer, List<Project>> deployOpOnStands = new HashMap<>();
    public HashMap<Integer, List<Project>> getDeployOpOnStands() {
        return deployOpOnStands;
    }

    private Base base = new Base(username, password);

    public void setDeployOnStands() {
        deployOpOnStands.put(1, new ArrayList<>());
        deployOpOnStands.put(2, new ArrayList<>());
        deployOpOnStands.put(3, new ArrayList<>());
    }

    /**
     * Делаем релиз
     */
    public void createRelease(final int number, final String standNameLocal) {
        String standNameShort = standNameLocal.replace("ЕИС-", "");
        for (Project p : buildsOpToDeploy) {
            for (Environments e : p.environments) {
                if (e.name.contains(standNameShort)) {
                    p.setCurrentEnvironment(e);
                    break;
                }
            }
           /* String json = "{'planResultKey':'EIS-EISRDIKWF40-14'," +
                    "'name':'release-11.0.0-14'," +
                    "'nextVersionName':'release-11.0.0-15'}";*/
            String json = "{\"planResultKey\":\"" + p.buildResult.buildResultKey + "\"," +
                    "\"name\":\"" + p.branch.shortName + "-" + p.buildResult.buildNumber + "-" + standNameShort + "\"," +
                    "\"nextVersionName\":\"" + p.branch.shortName + "-" + (p.buildResult.buildNumber + 1) + "-" + standNameShort + "\"}";
            log.info("JSON: " + json);
//            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/65404967/version";
            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/" + p.currentEnvironment.deploymentProjectId + "/version";
            log.info("request: " + request);
            String response = base.getResponsePost(request, json);
            log.info("response: " + response);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            Version version = g.fromJson(reader, Version.class);
            p.setVersion(version, number);
            deployOpOnStands.get(number).add(p);
        }
    }

    /**
     * Делаем релиз
     */
    public void createRelease(Stand stand) {
        Project project = stand.getProjectOp();
        String shortStand = stand.getName().replace("ЕИС-", "");
        for (Environments e : project.environments) {
            if (e.name.contains(shortStand)) {
                project.setCurrentEnvironment(e);
                break;
            }
        }
        /* String json = "{'planResultKey':'EIS-EISRDIKWF40-14'," +
                    "'name':'release-11.0.0-14'," +
                    "'nextVersionName':'release-11.0.0-15'}";*/
        String json = "{\"planResultKey\":\"" + project.oneResult.buildResultKey + "\"," +
                "\"name\":\"" + project.oneResult.plan.shortName + "-" + project.oneResult.buildNumber + "-" + shortStand + "\"," +
                "\"nextVersionName\":\"" + project.oneResult.plan.shortName + "-" + (project.oneResult.buildNumber + 1) + "-" + shortStand + "\"}";
        log.info("JSON: " + json);
//            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/65404967/version";
        String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/" + project.currentEnvironment.deploymentProjectId + "/version";
        log.info("request release: " + request);
        String response = base.getResponsePost(request, json);
        log.info("response: " + response);
        Gson g = new Gson();
        JsonReader reader = new JsonReader(new StringReader(response));
        Version version = g.fromJson(reader, Version.class);
        project.setVersionOp(version);
    }

    /**
     * Деплоим билд-план на стенд
     */
    public void deploy(final int number) {
        /*for (String x: projectsEnvironments.keySet()) {
            for (Environments y: projectsEnvironments.get(x)) {
                String url = "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?";
                if (y.name.toLowerCase(Locale.ROOT).contains(stand.toLowerCase(Locale.ROOT))) {
                    url += "environmentId=" + y.id;
                    for (String v: allVersions.keySet())
                        if (v.equals(x)) {
                            url += "&versionId=" + String.valueOf(allVersions.get(v));
                            System.out.println(url);
                        }
                }
            }
            }*/
        for (Project p : deployOpOnStands.get(number)) {
            // String url = "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?environmentId=&versionId=";
            String url =
                    "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?" +
                    "environmentId=" + p.currentEnvironment.id +
                    "&versionId=" + p.version[number].id;
            log.info("url: " + url);
            String response = base.getResponsePost(url, "");
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            DeploymentResultId deploymentResultId = g.fromJson(reader, DeploymentResultId.class);
            p.setDeploymentResultId(deploymentResultId, number);
            log.info("deploymentResultId: " + deploymentResultId);
        }
    }

    /**
     * Деплоим билд-план на стенд
     */
    public void deploy(Stand stand) {
        Project p = stand.getProjectOp();
        String url =
                "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?" +
                        "environmentId=" + p.currentEnvironment.id +
                        "&versionId=" + p.versionOp.id;
        log.info("request deploy: " + url);
        String response = base.getResponsePost(url, "");
        Gson g = new Gson();
        JsonReader reader = new JsonReader(new StringReader(response));
        DeploymentResultId deploymentResultId = g.fromJson(reader, DeploymentResultId.class);
        p.setDeploymentResultIdOp(deploymentResultId);
        log.info("deploymentResultId: " + deploymentResultId);
    }

    public void getDeployResult(Stand stand) {
        Project p = stand.getProjectOp();
        log.info("Project: " + p);
        String url = p.deploymentResultIdOp.link.href;
        log.info("response deploy result: " + url);
        String response = getResponse2(url);
        Gson g = new Gson();
        JsonReader reader = new JsonReader(new StringReader(response));
        DeploymentResult deploymentResult = g.fromJson(reader, DeploymentResult.class);
        log.info("deploymentResult: " + deploymentResult);
        p.setDeploymentResult(deploymentResult);
    }

    /**
     * Сетим последние успешные билды для всех билдов ОЧ
     *
     * @param branchName название ветки
     */
    public void setOpBuildsResult(final String branchName) {
        log.info("Branch name: " + branchName);
        for (Project p : opProjects) {
            String branchKey = getBranchKeyForBuildPlan(p.planKey.key, branchName);
            if (branchKey == null) {
                log.error("Ключ ветки не найден. Не выполяем поиск билдов");
                continue;
            }

            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/result/" + branchKey; // + "?max-results=5";
            log.info("request: " + request);
            String response = base.getResponse2(request);
            log.info("response: " + response);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            AllBuildResultInBranchKey allResults = g.fromJson(reader, AllBuildResultInBranchKey.class);
            p.setResults(allResults.results);
        }
    }

    /**
     * Получаем нужный билд план по ключу билда
     *
     * @param projectsList список проектов билдов, откуда будет делаться выборка
     * @param projectName id билда из ссылки. <br/>
     *                   К примеру: https://ci-sel.dks.lanit.ru/browse/<b>EPZ-EPZWF230</b>
     * @return Возвращает нужный Project, либо null
     */
    public Project getProject(List<Project> projectsList, final String projectName) {
        for (Project p : projectsList) {
            if (p.planKey.key.equalsIgnoreCase(projectName))
                return p;
        }
        log.error("Нужный проект не найден в списке билдов");
        return null;
    }

    /**
     * Получаем ключ для нужной ветки
     *
     * @return Возвращает ветку в формате {EPZ}-{buildPlanName}-{shortKey} при наличии
     * или NULL при отсутствии такой ветки в проекте
     */
    private String getBranchKeyForBuildPlan(final String projectName, final String branchName) {
        String request= "https://ci-sel.dks.lanit.ru/rest/api/latest/plan/" + projectName + "/branch";
        log.info("request: " + request);
        String response = base.getResponse2(request);
        log.info("response: " + response);
        Gson g = new Gson();
        JsonReader reader = new JsonReader(new StringReader(response));
        BuildBranches buildBranches = g.fromJson(reader, BuildBranches.class);
        for (Branch b : buildBranches.branches.branch) {
            if (b.shortName.equalsIgnoreCase(branchName)) {
                return b.key;
            }
        }
        log.error("No such branches is project");
        return null;
    }
}