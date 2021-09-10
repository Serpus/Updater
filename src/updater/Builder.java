package updater;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import okhttp3.*;
import parser.project.Project;
import parser.project.branches.Branch;
import parser.project.branches.BuildBranches;
import parser.project.buildResult.BuildResult;
import parser.project.buildResult.BuildResultStatus;
import sample.Main;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Builder extends Base {

    public Builder(String username, String password) {
        super(username, password);
    }

    private final String responseProject = getResponse2("https://ci-sel.dks.lanit.ru/rest/api/latest/deploy/project/all");
    private static int responseCode;
    private List<Project> allProjectList;
    /**
     * Все билды в бамбу
     */
    private Map<String, List<Project>> allProjectsMap = setAllProjectsMap();

    /**
     * Мапа с билдами с нужной веткой
     */
    private final Map<String, List<Project>> projectsWithBranchesMap = new HashMap<>();
    public Map<String, List<Project>> getProjectsWithBranchesMap() {
        return projectsWithBranchesMap;
    }

    /**
     * Мапа с отмеченными билдами
     */
    private final List<Project> checkedProjectsList = new ArrayList<>();
    public List<Project> getCheckedProjectsList() {
        return checkedProjectsList;
    }

    /**
     * Мапа с запущенными билдами
     */
    private final List<Project> startedProjectsList = new ArrayList<>();
    public List<Project> getStartedProjectsList() {
        return startedProjectsList;
    }

    private List<Project> opProjectsList;
    public List<Project> getOpProjectsList() {
        return opProjectsList;
    }

    /**
     * Очищаем данные для нового набора сборок
     */
    public void cleanAllBuildsData() {
        projectsWithBranchesMap.clear();
        checkedProjectsList.clear();
        startedProjectsList.clear();
    }

    /**
     * Объёдиняем проекты в одну мапу
     * @return Возвращает мапу с проектами, с ключами EIS, LSP, EPZ, OTHER
     */
    private Map<String, List<Project>> setAllProjectsMap() {
        setAllProjectsInList();
        Map<String, List<Project>> allProjects = new HashMap<>();
        allProjects.put("EIS", getProjectsEIS());
        allProjects.put("LKP", getProjectsLKP());
        allProjects.put("EPZ", getProjectsOP());
        allProjects.put("OTHER", getOtherProjects());
        return allProjects;
    }

    /**
     * Парсим все полученные проекты в список
     */
    private void setAllProjectsInList() {
        Gson g = new Gson();
        JsonReader reader = new JsonReader(new StringReader(responseProject));
        reader.setLenient(true);
        Project[] projects = g.fromJson(reader, Project[].class);
        allProjectList = new ArrayList<>();
        for (Project x: projects) {
            try {
                allProjectList.add(x);
            } catch (NullPointerException ignored) {}
        }
    }

    /**
     * Получаем билд-планы ЕИС
     */
    public List<Project> getProjectsEIS() {
        List<Project> eisProjectsList = new ArrayList<>();
        for (Project x: allProjectList) {
            if (x.planKey.key.contains("EIS-"))
                eisProjectsList.add(x);
        }
        return eisProjectsList;
    }
    /**
     * Получаем билд-планы ЛКП
     */
    public List<Project> getProjectsLKP() {
        List<Project> lkpProjectsList = new ArrayList<>();
        for (Project x: allProjectList) {
            if (x.planKey.key.contains("LKP-")
                    & !x.planKey.key.equalsIgnoreCase("LKP-LKPCOMMON"))
                lkpProjectsList.add(x);
        }
        return lkpProjectsList;
    }

    /**
     * Получаем билд-планы ОЧ
     */
    public List<Project> getProjectsOP() {
        List<Project> epzProjectsList = new ArrayList<>();
        for (Project x: allProjectList) {
            if (x.planKey.key.contains("EPZ-"))
                epzProjectsList.add(x);
        }

        for (Project x: allProjectList) {
            if (x.planKey.key.contains("SPHINX-"))
                epzProjectsList.add(x);
        }
        opProjectsList = new ArrayList<>(epzProjectsList);
        return epzProjectsList;
    }

    /**
     * Получаем остальные билд-планы
     */
    public List<Project> getOtherProjects() {
        List<Project> otherProjectsList = new ArrayList<>();
        for (Project x : allProjectList) {
            if (x.planKey.key.contains("EDO-EDO")) otherProjectsList.add(x);
            if (x.planKey.key.contains("TECH-TECHSUPPORT")) otherProjectsList.add(x);
            if (x.planKey.key.contains("TECH-FSTORETECHWF")) otherProjectsList.add(x);
            if (x.planKey.key.contains("DBF-DBFSCHED")) otherProjectsList.add(x);
            if (x.planKey.key.contains("DBF-DBFADM")) otherProjectsList.add(x);
            if (x.planKey.key.contains("DBF-DBF")) otherProjectsList.add(x);
        }

        return otherProjectsList;
    }

    /**
     * Получаем все ветки и убираем, которые не нужно обновлять (нет ветки) и сетим в общую мапу
     * @param branchName Имя ветки. Значение введено пользователем
     */
    public void setBranchesInMap(final String branchName) {
        Gson g = new Gson();
        List<Project> localList = new ArrayList<>();
        for (Project x: allProjectsMap.get("EIS")) {
            String responseBranches = getResponse("https://ci-sel.dks.lanit.ru/rest/api/latest/plan/" + x.planKey.key + "/branch");
            Main.logger.info("responseBranches: " + responseBranches);
            JsonReader reader = new JsonReader(new StringReader(responseBranches));
            BuildBranches buildBranches = g.fromJson(reader, BuildBranches.class);
            for (Branch i : buildBranches.branches.branch) {
                if (i.shortName.equalsIgnoreCase(branchName)) {
                    i.name = i.name.replace("PRIV - ", "");
                    x.setBranch(i);
                    localList.add(x);
                    Main.logger.info("Project " + x.planKey.key + " has branch: " + i);
                }
            }
        }
        projectsWithBranchesMap.put("EIS", new ArrayList<>(localList));
        localList.clear();

        for (Project x: allProjectsMap.get("LKP")) {
            String responseBranches = getResponse("https://ci-sel.dks.lanit.ru/rest/api/latest/plan/" + x.planKey.key + "/branch");
            Main.logger.info("responseBranches: " + responseBranches);
            JsonReader reader = new JsonReader(new StringReader(responseBranches));
            BuildBranches buildBranches = g.fromJson(reader, BuildBranches.class);
            for (Branch i : buildBranches.branches.branch) {
                if (i.shortName.equalsIgnoreCase(branchName)) {
                    i.name = i.name.replace("LKP - ", "");
                    x.setBranch(i);
                    localList.add(x);
                    Main.logger.info("Project " + x.planKey.key + " has branch: " + i);
                }
            }
        }
        projectsWithBranchesMap.put("LKP", new ArrayList<>(localList));
        localList.clear();

        for (Project x: allProjectsMap.get("EPZ")) {
            String responseBranches = getResponse2("https://ci-sel.dks.lanit.ru/rest/api/latest/plan/" + x.planKey.key + "/branch");
            Main.logger.info("responseBranches: " + responseBranches);
            JsonReader reader = new JsonReader(new StringReader(responseBranches));
            BuildBranches buildBranches = g.fromJson(reader, BuildBranches.class);
            for (Branch i : buildBranches.branches.branch) {
                if (i.shortName.equalsIgnoreCase(branchName)) {
                    i.name = i.name.replace("EPZ - ", "");
                    i.name = i.name.replace("Sphinx - ", "");
                    x.setBranch(i);
                    localList.add(x);
                    Main.logger.info("Project " + x.planKey.key + " has branch: " + i);
                }
            }
        }
        projectsWithBranchesMap.put("EPZ", new ArrayList<>(localList));
        localList.clear();

        for (Project x: allProjectsMap.get("OTHER")) {
            String responseBranches = getResponse("https://ci-sel.dks.lanit.ru/rest/api/latest/plan/" + x.planKey.key + "/branch");
            Main.logger.info("responseBranches: " + responseBranches);
            JsonReader reader = new JsonReader(new StringReader(responseBranches));
            BuildBranches buildBranches = g.fromJson(reader, BuildBranches.class);
            for (Branch i : buildBranches.branches.branch) {
                if (i.shortName.equalsIgnoreCase(branchName)) {
                    x.setBranch(i);
                    localList.add(x);
                    Main.logger.info("Project " + x.planKey.key + " has branch: " + i);
                }
            }
        }
        projectsWithBranchesMap.put("OTHER", new ArrayList<>(localList));
        localList.clear();
    }

    public final List<String> unStartedBuilds = new ArrayList<>();
    /**
     * Запускаем нужные билды
     */
    public void startBuild(final List<Project> checkedProjectsMap) {
        final OkHttpClient httpClient = new OkHttpClient();
        String response1 = "";
        String credential = Credentials.basic(username, password);
        for (Project p: checkedProjectsMap) {
            Main.logger.info("https://ci-sel.dks.lanit.ru/rest/api/latest/queue/" + p.branch.key);
//            response1 = "{\"planKey\":\"EIS-EISRDIKWF47\",\"buildNumber\":1,\"buildResultKey\":\"EIS-EISRDIKWF47-22\",\"triggerReason\":\"Manual build\",\"link\":{\"href\":\"https://ci-sel.dks.lanit.ru/rest/api/latest/result/EIS-EISRDIKWF47-1\",\"rel\":\"self\"}}";
            RequestBody body = RequestBody.create(null, new byte[0]);
            Request request = new Request.Builder()
                    .addHeader("Authorization", credential)
                    .addHeader("Accept", "application/json")
                    .addHeader("Coockie",
                            "JSESSIONID=D70C04C50FE50EA25CF5504B571C73FD")
                    .addHeader("User-Agent", "OkHttp Bot")
                    .url("https://ci-sel.dks.lanit.ru/rest/api/latest/queue/" + p.branch.key)
                    .post(body)
                    .build();
            try {
                Call call = httpClient.newCall(request);
                Response response = call.execute();
                responseCode = response.code();
                if (response.code() != 200) {
                    Main.logger.info("BuildPlan " + p.branch.key + " didn't start");
                    unStartedBuilds.add("https://ci-sel.dks.lanit.ru/browse/" + p.branch.key);
                    continue;
                }
                response1 = response.body().string();
                Main.logger.info("response: " + response1);
            } catch (IOException e) {
                Main.logger.info("Error");
                e.printStackTrace();
                httpClient.connectionPool().evictAll();
            }

            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(response1));
            BuildResult buildResult = g.fromJson(reader, BuildResult.class);
            p.setBuildResult(buildResult);
            startedProjectsList.add(p);
        }
        Main.logger.info("startedProjects: " + startedProjectsList);
    }

    /**
     * Получаем результаты билдов
     */
    public void getBuildsResults() {
        for (Project p : startedProjectsList) {
            String responseResult = getResponse2(p.buildResult.link.href);
            Main.logger.info("responseResult: " + responseResult);
            Gson g = new Gson();
            JsonReader reader = new JsonReader(new StringReader(responseResult));
            BuildResultStatus buildResultStatus = g.fromJson(reader, BuildResultStatus.class);
            p.setBuildResultStatus(buildResultStatus);
        }
    }

    /**
     * Получаем результаты билдов для ветки develop
     */
    public Map<String, String> getBuildResultDevelop() {
        /*Map<String, String> buildResults = new HashMap<>();
        for (String key : startedBuilds.keySet()) {
            String responseResult = getResponse2(startedBuilds.get(key));
            Main.logger.info("responseResult: " + responseResult);
            List<String> buildName = JsonPath
                    .parse(responseResult)
                    .read(".shortName");
            List<String> buildResult = JsonPath
                    .parse(responseResult)
                    .read(".buildState");
            buildResults.put(buildName.get(0),buildResult.get(0));
        } */
        return null;
    }

    public static int getResponseCode() {
        return responseCode;
    }
}
