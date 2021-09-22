package updater;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.log4j.Logger;
import parser.project.Environments;
import parser.project.Project;
import parser.project.deploys.DeploymentResult;
import parser.project.deploys.DeploymentResultId;
import parser.project.deploys.Version;

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

    public void getDeployResult(final int number) {
        log.info("Request number: " + number);
        for (Project p : deployOpOnStands.get(number)) {
            log.info("Project: " + p);
            String url = p.deploymentResultId[number].link.href;
            log.info("url: " + url);
            String response = getResponse2(url);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            DeploymentResult deploymentResult = g.fromJson(reader, DeploymentResult.class);
            p.setDeploymentResult(deploymentResult);
        }
    }
}