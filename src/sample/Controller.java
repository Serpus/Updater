package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import parser.project.Project;
import updater.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Controller extends DeployController {
    @FXML
    private Button confirmCustomBranch;
    @FXML
    private Button buttonCustomBranch;
    @FXML
    private Button refreshBranchesStatus;
    @FXML
    private Button newBuilding;
    @FXML
    private Button openAllBuilds;
    @FXML
    private Button connectToBamboo;
    @FXML
    private Pane modal;
    @FXML
    private Pane statusBuildsPane;
    @FXML
    private Pane auth;
    @FXML
    private DialogPane newBuildingModal;
    @FXML
    private DialogPane errorBuildPlans;
    @FXML
    private GridPane checkboxTable;
    @FXML
    private TextField branch;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextArea branchesStatus;
    @FXML
    private Label helpBranch;
    @FXML
    private Label nameBranch;
    @FXML
    private Label status;
    @FXML
    private Label branchError;
    @FXML
    private Label branchFormatError;
    @FXML
    private Label enterHint;
    @FXML
    private Label logopasError;
    @FXML
    private TabPane tabs;
    @FXML
    private Tab deployTab;
    @FXML
    private MenuButton branchMenu;
    @FXML
    private ListView<Label> statusListView;
    @FXML
    private ListView<Label> errorBuildPlansList;


    private Builder builder;
    private Deployer deployer;
    private DeployerOP deployerOP;
    private ArrayList<CheckBox> checkBoxListEIS;
    private ArrayList<CheckBox> checkBoxListLKP;
    private ArrayList<CheckBox> checkBoxListOP;
    private ArrayList<CheckBox> checkBoxListOther;
    private boolean isDeployTabDisable = true;
    private boolean isDeploysStart = false;

    public void someSettings() {
        // Вкладка деплоев после логина почему-то выглядит активной, если её заново не отключить
        if (isDeployTabDisable) {
            deployTab.setDisable(false);
            deployTab.setDisable(true);
            isDeployTabDisable = false;
        }

        // Проверка на заполнение имени ветки
        if (!branch.getText().isEmpty()) {
            branchError.setVisible(false);
        }

        // Проверка формата стенда
        if (Helper.checkBranchFormat(branch.getText())) {
            branchFormatError.setVisible(false);
        }

        // Проверка выбора стенда
        if ((!standName.getText().equalsIgnoreCase("Выберите стенд") |
                standName.getText().isEmpty()) & !isDeploysStart) {
            prepareDeployModal.setDisable(false);
        }
    }
    public void someAuthSettings() {
        if (!username.getText().isEmpty()) {
            connectToBamboo.setDisable(false);
            logopasError.setVisible(false);
        }

        username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                connectToBamboo();
            }
        });
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                connectToBamboo();
            }
        });
        branch.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                modalCustomBranch();
            }
        });
    }


    public void newBuilding() {
        newBuildingModal.setVisible(true);
        statusBuildsPane.setDisable(true);
        tabs.setDisable(true);
        Main.logger.info("newBuildingModal.isVisible(): " + newBuildingModal.isVisible());
        Main.logger.info("statusPane.isVisible(): " + statusBuildsPane.isVisible());
        Main.logger.info("tabs.isVisible(): " + tabs.isVisible());
    }

    public void newBuildingConfirm() {
        branch.clear();
        branchesStatus.clear();
        builder.cleanAllBuildsData();
        statusListView.getItems().clear();
        branch.setDisable(false);
        buttonCustomBranch.setDisable(false);
        newBuilding.setDisable(true);
        statusBuildsPane.setDisable(true);
        newBuildingModal.setVisible(false);
        tabs.setDisable(false);
        enterHint.setVisible(false);
        branchMenu.setDisable(false);
        deployTab.setDisable(true);
        deployStatusStandMenu.setDisable(true);
        prepareDeployModal.setDisable(false);
        isDeploysStart = false;
        disableRefreshButton();
        enableStands();
        clearDeployStatusStand();
        disableDeployStatusPanes();
        defaultVisibleDeployStatusPane();
        Main.logger.info("Cancel. Success cleaning data");
    }

    public void newBuildingCancel() {
        newBuildingModal.setVisible(false);
        statusBuildsPane.setDisable(false);
        tabs.setDisable(false);
    }

    public void connectToBamboo() {
        if (username.getText().isEmpty()) {
            connectToBamboo.setDisable(true);
            logopasError.setVisible(true);
            return;
        }
        final String user = username.getText();
        final String pass = password.getText();
        Main.logger.info("Start connecting to username: " + username.getText());
        try {
            builder = new Builder(user, pass);
            deployer = new Deployer(user, pass);
            deployerOP = new DeployerOP(user, pass);
            auth.setVisible(false);
            tabs.setDisable(false);
            deployerOP.getOpProjects().addAll(builder.getOpProjectsList());
            Main.logger.info("Success connection");
        } catch (NullPointerException e) {
            status.setVisible(true);
            if (Base.getResponseCode() == 500) {
                status.setText("Ошибка сервисов Bamboo. Попробуйте позже.");
            } else {
                status.setText("Не получилось( Попробуйте ещё раз.");
            }
            Main.logger.info("Failed connection with response code: " + Base.getResponseCode());
        }
    }


    //
    // Вкладка Билды
    //


    public void setHelpBranchTooltip() {
        helpBranch.setTooltip(new Tooltip("Название ветки в формате bamboo:\nrelease-x.x.x\nhotfix-x.x.x"));
    }

    public void modalCustomBranch() {
        if (branch.getText().isEmpty()) {
            branchError.setVisible(true);
            return;
        } else {
            branchError.setVisible(false);
        }
        if (!Helper.checkBranchFormat(branch.getText())) {
            branchFormatError.setVisible(true);
            return;
        } else {
            branchFormatError.setVisible(false);
        }
        int iterator = 0;
        int iteratorList = 0;
        statusBuildsPane.setVisible(false);
        checkBoxListEIS = new ArrayList<>();
        checkBoxListLKP = new ArrayList<>();
        checkBoxListOP = new ArrayList<>();
        checkBoxListOther = new ArrayList<>();
        Main.logger.info("branch: " + branch.getText());
        nameBranch.setText(branch.getText());
        builder.setBranchesInMap(branch.getText());
        // Выделяем билды по проектам.
        // ЕИС
        checkBoxListEIS.add(new CheckBox("Билд-планы ЗЧ:"));
        checkBoxListEIS.get(0).setOnAction(event -> {
            if (checkBoxListEIS.get(0).isSelected())
                for (CheckBox x : checkBoxListEIS)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListEIS)
                    x.setSelected(false);
        });
        checkBoxListEIS.get(0).setSelected(true);
        checkBoxListEIS.get(0).setStyle("-fx-font-weight: bold");
        checkboxTable.add(checkBoxListEIS.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project x : builder.getProjectsWithBranchesMap().get("EIS")) {
            checkBoxListEIS.add(new CheckBox(x.branch.name));
            checkBoxListEIS.get(iteratorList).setSelected(true);
            checkBoxListEIS.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListEIS));
            checkboxTable.add(checkBoxListEIS.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
        iteratorList = 0;
        if (checkBoxListEIS.size() == 1) {
            checkBoxListEIS.get(0).setSelected(false);
            checkBoxListEIS.get(0).setDisable(true);
        }
        checkboxTable.add(new Label("    "), 0, iterator++);
        // ЛКП
        checkBoxListLKP.add(new CheckBox("Билд-планы ЛКП:"));
        checkBoxListLKP.get(0).setOnAction(event -> {
            if (checkBoxListLKP.get(0).isSelected())
                for (CheckBox x : checkBoxListLKP)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListLKP)
                    x.setSelected(false);
        });
        checkBoxListLKP.get(0).setSelected(true);
        checkBoxListLKP.get(0).setStyle("-fx-font-weight: bold");
        checkboxTable.add(checkBoxListLKP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project x : builder.getProjectsWithBranchesMap().get("LKP")) {
            checkBoxListLKP.add(new CheckBox(x.branch.name));
            checkBoxListLKP.get(iteratorList).setSelected(true);
            checkBoxListLKP.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListLKP));
            checkboxTable.add(checkBoxListLKP.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
        iteratorList = 0;
        if (checkBoxListLKP.size() == 1) {
            checkBoxListLKP.get(0).setSelected(false);
            checkBoxListLKP.get(0).setDisable(true);
        }
        checkboxTable.add(new Label("    "), 0, iterator++);
        // ОЧ
        checkBoxListOP.add(new CheckBox("Билд-планы ОЧ:"));
        checkBoxListOP.get(0).setOnAction(event -> {
            if (checkBoxListOP.get(0).isSelected())
                for (CheckBox x : checkBoxListOP)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListOP)
                    x.setSelected(false);
        });
        checkBoxListOP.get(0).setSelected(true);
        checkBoxListOP.get(0).setStyle("-fx-font-weight: bold");
        checkboxTable.add(checkBoxListOP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project x : builder.getProjectsWithBranchesMap().get("EPZ")) {
            checkBoxListOP.add(new CheckBox(x.branch.name));
            checkBoxListOP.get(iteratorList).setSelected(true);
            checkBoxListOP.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListOP));
            checkboxTable.add(checkBoxListOP.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
        iteratorList = 0;
        if (checkBoxListOP.size() == 1) {
            checkBoxListOP.get(0).setSelected(false);
            checkBoxListOP.get(0).setDisable(true);
        }
        checkboxTable.add(new Label("    "), 0, iterator++);
        // Другие
        checkBoxListOther.add(new CheckBox("Остальные билд-планы:"));
        checkBoxListOther.get(0).setOnAction(event -> {
            if (checkBoxListOther.get(0).isSelected())
                for (CheckBox x : checkBoxListOther)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListOther)
                    x.setSelected(false);
        });
        checkBoxListOther.get(0).setSelected(true);
        checkBoxListOther.get(0).setStyle("-fx-font-weight: bold");
        checkboxTable.add(checkBoxListOther.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project x : builder.getProjectsWithBranchesMap().get("OTHER")) {
            checkBoxListOther.add(new CheckBox(x.branch.name));
            checkBoxListOther.get(iteratorList).setSelected(true);
            checkBoxListOther.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListOther));
            checkboxTable.add(checkBoxListOther.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
        if (checkBoxListOther.size() == 1) {
            checkBoxListOther.get(0).setSelected(false);
            checkBoxListOther.get(0).setDisable(true);
        }

        confirmCustomBranch.setVisible(true);
        modal.setVisible(true);
        branchMenu.setDisable(true);
    }

    public void cancelModal() {
        nameBranch.setText("");
        statusBuildsPane.setVisible(true);
        checkboxTable.getChildren().clear();
        modal.setVisible(false);
        branchMenu.setDisable(false);
        checkBoxListEIS.clear();
        checkBoxListLKP.clear();
        checkBoxListOP.clear();
        checkBoxListOther.clear();
        Main.logger.info("Cancel modal");
    }

    public void startBuildsCustomBranch() {
        statusBuildsPane.setVisible(true);
        branch.setDisable(true);
        for (CheckBox x : checkBoxListEIS) {
            if (x.isSelected()) {
                for (Project p : builder.getProjectsWithBranchesMap().get("EIS")) {
                    if (x.getText().equalsIgnoreCase(p.branch.name)) {
                        builder.getCheckedProjectsList().add(p);
                    }
                }
            }
        }

        for (CheckBox x : checkBoxListLKP) {
            if (x.isSelected()) {
                for (Project p : builder.getProjectsWithBranchesMap().get("LKP")) {
                    if (x.getText().equalsIgnoreCase(p.branch.name)) {
                        builder.getCheckedProjectsList().add(p);
                    }
                }
            }
        }

        for (CheckBox x : checkBoxListOP) {
            if (x.isSelected()) {
                for (Project p : builder.getProjectsWithBranchesMap().get("EPZ")) {
                    if (x.getText().equalsIgnoreCase(p.branch.name)) {
                        builder.getCheckedProjectsList().add(p);
                    }
                }
            }
        }

        for (CheckBox x : checkBoxListOther) {
            if (x.isSelected()) {
                for (Project p : builder.getProjectsWithBranchesMap().get("OTHER")) {
                    if (x.getText().equalsIgnoreCase(p.branch.name)) {
                        builder.getCheckedProjectsList().add(p);
                    }
                }
            }
        }

        Main.logger.info("builder.getCheckedProjectsList(): " + builder.getCheckedProjectsList());

        builder.startBuild(builder.getCheckedProjectsList());
        refreshBranchesStatus.setDisable(false);
        statusBuildsPane.setDisable(false);
        checkboxTable.getChildren().clear();
        modal.setVisible(false);
        buttonCustomBranch.setDisable(true);
        newBuilding.setDisable(false);

        setErrorBuildPlansList();
        if (builder.unStartedBuilds.size() > 0) {
            Main.logger.info("unStartedBuilds.size(): " + builder.unStartedBuilds.size());
            int i = 0;
            for (String x : builder.unStartedBuilds) {
                errorBuildPlansList.getItems().add(new Label());
                errorBuildPlansList.getItems().get(i).setText(x);
                i++;
            }
            errorBuildPlans.setVisible(true);
        }
        deployTab.setDisable(false);
        refreshBranchesStatus();
    }

    public void cancelBuildsError() {
        errorBuildPlans.setVisible(false);
        errorBuildPlansList.getItems().clear();
        builder.unStartedBuilds.clear();
    }

    private void setErrorBuildPlansList() {
        errorBuildPlansList.getItems().clear();
        errorBuildPlansList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String url = errorBuildPlansList.getSelectionModel().getSelectedItem().getText();
                Main.logger.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    private void setStatusListView() {
        statusListView.getItems().clear();
        statusListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String labelText = statusListView.getSelectionModel().getSelectedItem().getText();
                String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/browse/"));
                Main.logger.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    public void refreshBranchesStatus() {
        enterHint.setVisible(true);
        setStatusListView();
        if (branch.getText().contains("develop"))
            builder.getBuildResultDevelop();
        else
            builder.getBuildsResults();
        branchesStatus.setText("");
        int i = 0;
        for (Project x : builder.getStartedProjectsList()) {
            if (x.buildResultStatus.buildState.equalsIgnoreCase("Unknown")) {
                statusListView.getItems().add(new Label());
                statusListView.getItems().get(i).setText(x.branch.name + " = В процессе или очереди\n" +
                        createUrlForBuild(x.buildResultStatus.key));
                i++;
            }
            if (x.buildResultStatus.buildState.equalsIgnoreCase("Successful")) {
                statusListView.getItems().add(new Label());
                statusListView.getItems().get(i).setText(x.branch.name + " = " + x.buildResultStatus.buildState + "\n" +
                        createUrlForBuild(x.buildResultStatus.key));
                statusListView.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (x.buildResultStatus.buildState.equalsIgnoreCase("Failed")) {
                statusListView.getItems().add(new Label());
                statusListView.getItems().get(i).setText(x.branch.name + " = " + x.buildResultStatus.buildState + "\n" +
                        createUrlForBuild(x.buildResultStatus.key));
                statusListView.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        openAllBuilds.setDisable(statusListView.getItems().size() <= 0);
    }

    public void openAllBuilds() {
        for (Label i : statusListView.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/browse/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }

    private String createUrlForBuild(String branchKey) {
        return "https://ci-sel.dks.lanit.ru/browse/" + branchKey;
    }

    private void goToUrl(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setHotfix() {
        branch.clear();
        branch.setText("hotfix-");
        Main.logger.info("Set branch is hotfix");
    }

    public void setRelease() {
        branch.clear();
        branch.setText("release-");
        Main.logger.info("Set branch is release");
    }

    //
    // Деплои
    //

    private void checkUncheckedBox(final ArrayList<CheckBox> boxes) {
        int size = boxes.size();
        if (size == 1) {
            boxes.get(0).setSelected(false);
            boxes.get(0).setDisable(true);
            return;
        }
        int i = 0;
        for (CheckBox x : boxes) {
            if (x.isSelected())
                i++;
        }
        if (size > 1 & boxes.get(0).isSelected() & i <= 1) {
            boxes.get(0).setSelected(false);
        }
        if ((size - 1) == i & !boxes.get(0).isSelected()) {
            boxes.get(0).setSelected(true);
        }
    }


    /**
     * Показываем окно подготовки деплоев с актуальными успешными билдами
     */
    public void showDeployModal() {
        checkboxTableDeploys.getChildren().clear();
        checkBoxListSuccessBuilds = new ArrayList<>();
        checkBoxListUnknownBuilds = new ArrayList<>();
        deployModal.setVisible(true);
        statusDeploysPane1.setVisible(false);
        deployStatusStandMenu.setVisible(false);
        builder.getBuildsResults();

        int iteratorList = 0;
        int iterator = 0;

        checkBoxListSuccessBuilds.add(new CheckBox("Успешные билды:"));
        checkBoxListSuccessBuilds.get(0).setOnAction(event -> {
            if (checkBoxListSuccessBuilds.get(0).isSelected())
                for (CheckBox x : checkBoxListSuccessBuilds)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListSuccessBuilds)
                    x.setSelected(false);
        });
        checkBoxListSuccessBuilds.get(0).setSelected(true);
        checkBoxListSuccessBuilds.get(0).setStyle("-fx-font-weight: bold");
        checkboxTableDeploys.add(checkBoxListSuccessBuilds.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project p : builder.getStartedProjectsList()) {
            if (p.buildResultStatus.buildState.equalsIgnoreCase("Successful")
                    & !p.planKey.key.contains("EPZ")
                    & !p.planKey.key.contains("SPHINX")) {
                checkBoxListSuccessBuilds.add(new CheckBox(p.branch.name));
                checkBoxListSuccessBuilds.get(iteratorList).setSelected(true);
                checkBoxListSuccessBuilds.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListSuccessBuilds));
                checkboxTableDeploys.add(checkBoxListSuccessBuilds.get(iteratorList), 0, iterator);
                iterator++;
                iteratorList++;
            }
        }
        iteratorList = 0;
        checkboxTableDeploys.add(new Label("    "), 0, iterator++);

        // Не законченные
        checkBoxListUnknownBuilds = new ArrayList<>();
        checkBoxListUnknownBuilds.add(new CheckBox("Не завершённые билды:"));
        checkBoxListUnknownBuilds.get(0).setSelected(false);
        checkBoxListUnknownBuilds.get(0).setDisable(true);
        checkBoxListUnknownBuilds.get(0).setStyle("-fx-font-weight: bold");
        checkboxTableDeploys.add(checkBoxListUnknownBuilds.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project p : builder.getStartedProjectsList()) {
            if (p.buildResultStatus.buildState.equalsIgnoreCase("Unknown")
                    & !p.planKey.key.contains("EPZ")
                    & !p.planKey.key.contains("SPHINX")) {
                checkBoxListUnknownBuilds.add(new CheckBox(p.branch.name));
                checkBoxListUnknownBuilds.get(iteratorList).setSelected(false);
                checkBoxListUnknownBuilds.get(iteratorList).setDisable(true);
                checkboxTableDeploys.add(checkBoxListUnknownBuilds.get(iteratorList), 0, iterator);
                iterator++;
                iteratorList++;
            }
        }
    }

    /**
     * Стартуем деплои
     */
    public void startDeploying() {
        setDeployStatusStandMenu();
        for (CheckBox x : checkBoxListSuccessBuilds) {
            if (x.isSelected()) {
                for (Project p : builder.getStartedProjectsList()) {
                    if (x.getText().equalsIgnoreCase(p.branch.name)) {
                        deployer.getBuildsToDeploy().add(p);
                        break;
                    }
                }
            }
        }
        int i = 0;
        int standMenuSize =  deployStatusStandMenu.getItems().size();
        Main.logger.info("standMenuSize: " + standMenuSize);
        deployer.setDeployOnStands();
        for (MenuItem item : deployStatusStandMenu.getItems()) {
            Main.logger.info("Current ITEM: " + item.getText());
            i++;
            deployer.createRelease(i, item.getText());
            deployer.deploy(i);
        }
        deployModal.setVisible(false);
        deployStatusStandMenu.setVisible(true);
        prepareDeployModal.setDisable(true);
        isDeploysStart = true;
        statusDeploysPane1.setVisible(true);
        disableStands();
        enableDeployStatusPanes();
    }

    private void setStatusDeploysListView1() {
        statusDeploysListView1.getItems().clear();
        statusDeploysListView1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String labelText = statusDeploysListView1.getSelectionModel().getSelectedItem().getText();
                String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                Main.logger.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    private void setStatusDeploysListView2() {
        statusDeploysListView2.getItems().clear();
        statusDeploysListView2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String labelText = statusDeploysListView2.getSelectionModel().getSelectedItem().getText();
                String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                Main.logger.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    private void setStatusDeploysListView3() {
        statusDeploysListView3.getItems().clear();
        statusDeploysListView3.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String labelText = statusDeploysListView3.getSelectionModel().getSelectedItem().getText();
                String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                Main.logger.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    /**
     * Обновляем статусы деплоев
     */
    public void refreshDeploysStatus(final int number) {
        deployer.getDeployResult(number);
        switch (number) {
            case 1:
                refreshDeploysStatus1();
                break;
            case 2:
                refreshDeploysStatus2();
                break;
            case 3:
                refreshDeploysStatus3();
                break;
        }
    }

    public void refreshDeploysStatus1() {
        setStatusDeploysListView1();
        deployer.getDeployResult(1);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(1)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                statusDeploysListView1.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                statusDeploysListView1.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView1: " + statusDeploysListView1.getItems());
        openAllDeploys1.setDisable(statusDeploysListView1.getItems().size() <= 0);
    }
    public void refreshDeploysStatus2() {
        setStatusDeploysListView2();
        deployer.getDeployResult(2);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(2)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                statusDeploysListView2.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                statusDeploysListView2.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView2: " + statusDeploysListView2.getItems());
        openAllDeploys2.setDisable(statusDeploysListView2.getItems().size() <= 0);

    }
    public void refreshDeploysStatus3() {
        setStatusDeploysListView3();
        deployer.getDeployResult(3);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(3)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                statusDeploysListView3.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                statusDeploysListView3.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView3: " + statusDeploysListView3.getItems());
        openAllDeploys3.setDisable(statusDeploysListView3.getItems().size() <= 0);
    }

    /**
     * Делаем ссылки для деплоя
     * @param deploymentResultId deploymentResultId
     * @return ссылка на деплой
     */
    private String createUrlForDeploy(final String deploymentResultId) {
        return "https://ci-sel.dks.lanit.ru/deploy/viewDeploymentResult.action?deploymentResultId=" + deploymentResultId;
    }

    /**
     * Открываем все билды
     */
    public void openAllDeploys1() {
        for (Label i : statusDeploysListView1.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys2() {
        for (Label i : statusDeploysListView2.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys3() {
        for (Label i : statusDeploysListView3.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }







    // Деплой ОЧ






    public void showDeployModalEpzBd() {
        deployOpModal.setVisible(true);
        opProjectName.setText("Собранные билды ЕПЗ БД");
        checkBoxListBuildsOP = new ArrayList<>();

        int iteratorList = 0;
        int iterator = 0;

        checkBoxListBuildsOP.add(new CheckBox("Успешные билды:"));
        checkBoxListBuildsOP.get(0).setOnAction(event -> {
            if (checkBoxListBuildsOP.get(0).isSelected())
                for (CheckBox x : checkBoxListBuildsOP)
                    x.setSelected(true);
            else
                for (CheckBox x : checkBoxListBuildsOP)
                    x.setSelected(false);
        });
        checkBoxListBuildsOP.get(0).setSelected(true);
        checkBoxListBuildsOP.get(0).setStyle("-fx-font-weight: bold");
        checkboxTableDeploysOP.add(checkBoxListBuildsOP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;
        for (Project p : builder.getProjectsWithBranchesMap().get("EPZ")) {
            if (p.buildResultStatus.buildState.equalsIgnoreCase("Successful")
                    & !p.planKey.key.contains("EPZ")
                    & !p.planKey.key.contains("SPHINX")) {
                checkBoxListBuildsOP.add(new CheckBox(p.branch.name));
                checkBoxListBuildsOP.get(iteratorList).setSelected(true);
                checkBoxListBuildsOP.get(iteratorList).setOnAction(event -> checkUncheckedBox(checkBoxListBuildsOP));
                checkboxTableDeploysOP.add(checkBoxListBuildsOP.get(iteratorList), 0, iterator);
                iterator++;
                iteratorList++;
            }
        }
    }

    public void showDeployModalEpz() {
        deployOpModal.setVisible(true);
        opProjectName.setText("Собранные билды ЕПЗ");
    }

    public void showDeployModalSphinx() {
        deployOpModal.setVisible(true);
        opProjectName.setText("Собранные билды Sphinx");
    }

    public void cancelModalDeployOP() {
        deployOpModal.setVisible(false);
        opProjectName.setText("");
    }

    public void startDeployingOP() {

    }

    public void refreshDeploysStatus1op() {
        // Переделать под ОЧ
        /*setStatusDeploysListView1();
        deployer.getDeployResult(1);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(1)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                statusDeploysListView1.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView1.getItems().add(new Label());
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[1].deploymentResultId));
                statusDeploysListView1.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView1: " + statusDeploysListView1.getItems());
        openAllDeploys1.setDisable(statusDeploysListView1.getItems().size() <= 0);*/
    }
    public void refreshDeploysStatus2op() {
        // Переделать под ОЧ
        /*setStatusDeploysListView2();
        deployer.getDeployResult(2);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(2)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                statusDeploysListView2.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView2.getItems().add(new Label());
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[2].deploymentResultId));
                statusDeploysListView2.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView2: " + statusDeploysListView2.getItems());
        openAllDeploys2.setDisable(statusDeploysListView2.getItems().size() <= 0);*/

    }
    public void refreshDeploysStatus3op() {
        // Переделать под ОЧ
        /*setStatusDeploysListView3();
        deployer.getDeployResult(3);
        int i = 0;
        for (Project p : deployer.getDeployOnStands().get(3)) {
            String deploymentState = p.deploymentResult.deploymentState;
            String lifeCycleState = p.deploymentResult.lifeCycleState;
            if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = В процессе или очереди\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("SUCCESS")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                statusDeploysListView3.getItems().get(i).setTextFill(Paint.valueOf("Green"));
                i++;
            }
            if (deploymentState.equalsIgnoreCase("FAILED")) {
                statusDeploysListView3.getItems().add(new Label());
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = " + deploymentState + "\n" +
                        createUrlForDeploy(p.deploymentResultId[3].deploymentResultId));
                statusDeploysListView3.getItems().get(i).setTextFill(Paint.valueOf("Red"));
                i++;
            }
        }
        Main.logger.info("statusDeploysListView3: " + statusDeploysListView3.getItems());
        openAllDeploys3.setDisable(statusDeploysListView3.getItems().size() <= 0);*/
    }

    /**
     * Открываем все билды
     */
    public void openAllDeploys1op() {
        for (Label i : statusDeploysListView1op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys2op() {
        for (Label i : statusDeploysListView2op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys3op() {
        for (Label i : statusDeploysListView3op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            Main.logger.info("URL: " + url);
            goToUrl(url);
        }
    }
}