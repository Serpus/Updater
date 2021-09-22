package updater;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import parser.project.Environments;
import parser.project.Project;
import parser.project.deploys.DeploymentResult;
import parser.project.deploys.DeploymentResultId;
import parser.project.deploys.Version;
import sample.Main;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Deployer extends Builder {

    public Deployer(String username, String password) {
        super(username, password);
    }

    private List<Project> buildsToDeploy = new ArrayList<>();
    public List<Project> getBuildsToDeploy() {
        return buildsToDeploy;
    }

    private HashMap<Integer, List<Project>> deployOnStands = new HashMap<>();
    public HashMap<Integer, List<Project>> getDeployOnStands() {
        return deployOnStands;
    }

    private Base base = new Base(username, password);

    public void setDeployOnStands() {
        deployOnStands.put(1, new ArrayList<>());
        deployOnStands.put(2, new ArrayList<>());
        deployOnStands.put(3, new ArrayList<>());
    }

    /**
     * Делаем релиз
     */
    public void createRelease(final int number, final String standNameLocal) {
        String standNameShort = standNameLocal.replace("ЕИС-", "");
        for (Project p : buildsToDeploy) {
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
            Main.logger.info("JSON: " + json);
//            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/65404967/version";
            String request = "https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/" + p.currentEnvironment.deploymentProjectId + "/version";
            Main.logger.info("request: " + request);
            String response = base.getResponsePost(request, json);
            Main.logger.info("response: " + response);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            Version version = g.fromJson(reader, Version.class);
            p.setVersion(version, number);
            deployOnStands.get(number).add(p);
            Main.logger.info("End of collection");
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
        for (Project p : deployOnStands.get(number)) {
            // String url = "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?environmentId=&versionId=";
            String url =
                    "https://ci-sel.dks.lanit.ru/rest/api/latest/queue/deployment?" +
                    "environmentId=" + p.currentEnvironment.id +
                    "&versionId=" + p.version[number].id;
            Main.logger.info("url: " + url);
            String response = base.getResponsePost(url, "");
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            DeploymentResultId deploymentResultId = g.fromJson(reader, DeploymentResultId.class);
            p.setDeploymentResultId(deploymentResultId, number);
            Main.logger.info("deploymentResultId: " + deploymentResultId);
        }
    }

    public void getDeployResult(final int number) {
        Main.logger.info("Request number: " + number);
        for (Project p : deployOnStands.get(number)) {
            Main.logger.info("Project: " + p);
            String url = p.deploymentResultId[number].link.href;
            Main.logger.info("url: " + url);
            String response = getResponse2(url);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response));
            DeploymentResult deploymentResult = g.fromJson(reader, DeploymentResult.class);
            p.setDeploymentResult(deploymentResult);
        }
    }
}