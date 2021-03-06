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
import org.apache.log4j.Logger;
import parser.project.Project;
import parser.project.buildResult.allResultsInBranch.Result;
import stands.*;
import updater.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class Controller extends DeployController {
    private static final Logger log = Logger.getLogger(Controller.class);
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
    private Tab deployTabOP;
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
    private EIS3 eis3;
    private EIS4 eis4;
    private EIS5 eis5;
    private EIS6 eis6;
    private EIS7 eis7;
    private List<Stand> activeClassList;

    public void someSettings() {
        // ?????????????? ?????????????? ?????????? ???????????? ????????????-???? ???????????????? ????????????????, ???????? ???? ???????????? ???? ??????????????????
        if (isDeployTabDisable) {
            deployTab.setDisable(false);
            deployTab.setDisable(true);
            deployTabOP.setDisable(false);
            deployTabOP.setDisable(true);
            isDeployTabDisable = false;
        }

        // ???????????????? ???? ???????????????????? ?????????? ??????????
        if (!branch.getText().isEmpty()) {
            branchError.setVisible(false);
            deployTabOP.setDisable(false);
        }
        if (branch.getText().isEmpty()) {
            deployTabOP.setDisable(true);
        }

        // ???????????????? ?????????????? ????????????
        if (Helper.checkBranchFormat(branch.getText())) {
            branchFormatError.setVisible(false);
        }

        // ???????????????? ???????????? ????????????
        if ((!standName.getText().equalsIgnoreCase("???????????????? ??????????") |
                standName.getText().isEmpty()) & !isDeploysStart) {
            prepareDeployModal.setDisable(false);
        }

        if (standNameOP.getText().equalsIgnoreCase("???????????????? ??????????") | standName.getText().isEmpty())
            confirmStandsButton.setVisible(false);
        else
            confirmStandsButton.setVisible(true);
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
        log.info("newBuildingModal.isVisible(): " + newBuildingModal.isVisible());
        log.info("statusPane.isVisible(): " + statusBuildsPane.isVisible());
        log.info("tabs.isVisible(): " + tabs.isVisible());
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
        deployTabOP.setDisable(true);
        deployStatusStandMenu.setDisable(true);
        prepareDeployModal.setDisable(false);
        isDeploysStart = false;
        isDeployTabDisable = true;
        disableRefreshButton();
        enableStands();
        clearDeployStatusStand();
        disableDeployStatusPanes();
        defaultVisibleDeployStatusPane();
        clearDeployList();
        deployer = new Deployer(username.getText(), password.getText());
        log.info("Cancel. Success cleaning data");
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
        log.info("Start connecting to username: " + username.getText());
        try {
            builder = new Builder(user, pass);
            deployer = new Deployer(user, pass);
            deployerOP = new DeployerOP(user, pass);
            auth.setVisible(false);
            tabs.setDisable(false);
            deployerOP.getOpProjects().addAll(builder.getOpProjectsList());
            log.info("Success connection");
        } catch (NullPointerException e) {
            status.setVisible(true);
            if (Base.getResponseCode() == 500) {
                status.setText("???????????? ???????????????? Bamboo. ???????????????????? ??????????.");
            } else {
                status.setText("???? ????????????????????( ???????????????????? ?????? ??????.");
            }
            log.info("Failed connection with response code: " + Base.getResponseCode());
        }
    }


    //
    // ?????????????? ??????????
    //


    public void setHelpBranchTooltip() {
        helpBranch.setTooltip(new Tooltip("???????????????? ?????????? ?? ?????????????? bamboo:\nrelease-x.x.x\nhotfix-x.x.x"));
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
        log.info("branch: " + branch.getText());
        nameBranch.setText(branch.getText());
        builder.setBranchesInMap(branch.getText());
        // ???????????????? ?????????? ???? ????????????????.
        // ??????
        checkBoxListEIS.add(new CheckBox("????????-?????????? ????:"));
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
        // ??????
        checkBoxListLKP.add(new CheckBox("????????-?????????? ??????:"));
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
        // ????
        checkBoxListOP.add(new CheckBox("????????-?????????? ????:"));
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
        // ????????????
        checkBoxListOther.add(new CheckBox("?????????????????? ????????-??????????:"));
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
        log.info("Cancel modal");
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

        log.info("builder.getCheckedProjectsList(): " + builder.getCheckedProjectsList());

        builder.startBuild(builder.getCheckedProjectsList());
        refreshBranchesStatus.setDisable(false);
        statusBuildsPane.setDisable(false);
        checkboxTable.getChildren().clear();
        modal.setVisible(false);
        buttonCustomBranch.setDisable(true);
        newBuilding.setDisable(false);

        setErrorBuildPlansList();
        if (builder.unStartedBuilds.size() > 0) {
            log.info("unStartedBuilds.size(): " + builder.unStartedBuilds.size());
            int i = 0;
            for (String x : builder.unStartedBuilds) {
                errorBuildPlansList.getItems().add(new Label());
                errorBuildPlansList.getItems().get(i).setText(x);
                i++;
            }
            errorBuildPlans.setVisible(true);
        }
        deployTab.setDisable(false);
        deployTabOP.setDisable(false);
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
                log.info("URL: " + url);
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
                log.info("URL: " + url);
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
                statusListView.getItems().get(i).setText(x.branch.name + " = ?? ???????????????? ?????? ??????????????\n" +
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
            log.info("URL: " + url);
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
        log.info("Set branch is hotfix");
    }

    public void setRelease() {
        branch.clear();
        branch.setText("release-");
        log.info("Set branch is release");
    }

    //
    // ????????????
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

    private void paintRadio(ArrayList<RadioButton> buttons) {
        for (RadioButton b : buttons) {
            if (!b.getText().contains("Successful")) {
                b.setDisable(true);
                b.setSelected(false);
            }
        }
    }

    /**
     * ???????????????????? ???????? ???????????????????? ?????????????? ?? ?????????????????????? ?????????????????? ??????????????
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

        checkBoxListSuccessBuilds.add(new CheckBox("???????????????? ??????????:"));
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

        // ???? ??????????????????????
        checkBoxListUnknownBuilds = new ArrayList<>();
        checkBoxListUnknownBuilds.add(new CheckBox("???? ?????????????????????? ??????????:"));
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
     * ???????????????? ????????????
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
        int standMenuSize = deployStatusStandMenu.getItems().size();
        log.info("standMenuSize: " + standMenuSize);
        deployer.setDeployOnStands();
        for (MenuItem item : deployStatusStandMenu.getItems()) {
            log.info("Current ITEM: " + item.getText());
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
                log.info("URL: " + url);
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
                log.info("URL: " + url);
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
                log.info("URL: " + url);
                goToUrl(url);
            }
        });
    }

    /**
     * ?????????????????? ?????????????? ??????????????
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
                statusDeploysListView1.getItems().get(i).setText(p.branch.name + " = ?? ???????????????? ?????? ??????????????\n" +
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
        log.info("statusDeploysListView1: " + statusDeploysListView1.getItems());
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
                statusDeploysListView2.getItems().get(i).setText(p.branch.name + " = ?? ???????????????? ?????? ??????????????\n" +
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
        log.info("statusDeploysListView2: " + statusDeploysListView2.getItems());
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
                statusDeploysListView3.getItems().get(i).setText(p.branch.name + " = ?? ???????????????? ?????? ??????????????\n" +
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
        log.info("statusDeploysListView3: " + statusDeploysListView3.getItems());
        openAllDeploys3.setDisable(statusDeploysListView3.getItems().size() <= 0);
    }

    /**
     * ???????????? ???????????? ?????? ????????????
     * @param deploymentResultId deploymentResultId
     * @return ???????????? ???? ????????????
     */
    private String createUrlForDeploy(final String deploymentResultId) {
        return "https://ci-sel.dks.lanit.ru/deploy/viewDeploymentResult.action?deploymentResultId=" + deploymentResultId;
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploys1() {
        for (Label i : statusDeploysListView1.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys2() {
        for (Label i : statusDeploysListView2.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    public void openAllDeploys3() {
        for (Label i : statusDeploysListView3.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }







    // ???????????? ????



    public void someDeployOpModalSettings() {
        int selected = 0;
        for (RadioButton b : radioGroupListBuildsOP)
        if (b.isSelected())
            selected++;
        confirmDeployOP.setDisable(selected == 0);
    }


    public void showDeployModalEpzBd() {
        deployOpModal.setVisible(true);
        opProjectName.setText("?????????????????? ?????????? ?????? ????");
        radioGroupListBuildsOP = new ArrayList<>();
        ToggleGroup group = new ToggleGroup();

        int iteratorList = 0;
        int iterator = 0;

        radioGroupListBuildsOP.add(new RadioButton("???????????????? ??????????:"));
        radioGroupListBuildsOP.get(0).setDisable(true);
        radioGroupListBuildsOP.get(0).setStyle("-fx-font-weight: bold");
        gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;

        deployerOP.setOpBuildsResult(branch.getText());
        Project epzBdProject = deployerOP.getProject(deployerOP.getOpProjects(), "EPZ-EPZDATABASE");
        if (epzBdProject.results == null || epzBdProject.results.result.length == 0) {
            log.error("?????????????????????? ???????????????? ?????????? ?? ???????? ?????????? " + epzBdProject.name + " ?? ?????????? " + branch.getText());
            return;
        }
        for (Result r : epzBdProject.results.result) {
            radioGroupListBuildsOP.add(new RadioButton(r.plan.master.name + " #" + r.buildNumber + " - " + r.buildState));
            radioGroupListBuildsOP.get(iteratorList).setToggleGroup(group);
            radioGroupListBuildsOP.get(iteratorList).setOnMouseMoved(event -> paintRadio(radioGroupListBuildsOP));
            gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
    }

    public void showDeployModalEpz() {
        deployOpModal.setVisible(true);
        opProjectName.setText("?????????????????? ?????????? ??????");
        radioGroupListBuildsOP = new ArrayList<>();
        ToggleGroup group = new ToggleGroup();

        int iteratorList = 0;
        int iterator = 0;

        radioGroupListBuildsOP.add(new RadioButton("???????????????? ??????????:"));
        radioGroupListBuildsOP.get(0).setDisable(true);
        radioGroupListBuildsOP.get(0).setStyle("-fx-font-weight: bold");
        gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;

        deployerOP.setOpBuildsResult(branch.getText());
        Project epzProject = deployerOP.getProject(deployerOP.getOpProjects(), "EPZ-EPZWF");
        if (epzProject.results == null || epzProject.results.result.length == 0) {
            log.error("?????????????????????? ???????????????? ?????????? ?? ???????? ?????????? " + epzProject.name + " ?? ?????????? " + branch.getText());
            return;
        }
        for (Result r : epzProject.results.result) {
            radioGroupListBuildsOP.add(new RadioButton(r.plan.master.name + " #" + r.buildNumber + " - " + r.buildState));
            radioGroupListBuildsOP.get(iteratorList).setToggleGroup(group);
            radioGroupListBuildsOP.get(iteratorList).setOnMouseMoved(event -> paintRadio(radioGroupListBuildsOP));
            gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
    }

    public void showDeployModalSphinx() {
        deployOpModal.setVisible(true);
        opProjectName.setText("?????????????????? ?????????? Sphinx");
        radioGroupListBuildsOP = new ArrayList<>();
        ToggleGroup group = new ToggleGroup();

        int iteratorList = 0;
        int iterator = 0;

        radioGroupListBuildsOP.add(new RadioButton("???????????????? ??????????:"));
        radioGroupListBuildsOP.get(0).setDisable(true);
        radioGroupListBuildsOP.get(0).setStyle("-fx-font-weight: bold");
        gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
        iterator++;
        iteratorList++;

        deployerOP.setOpBuildsResult(branch.getText());
        Project sphinxProject = deployerOP.getProject(deployerOP.getOpProjects(), "EPZ-SPHXEPZN");
        if (sphinxProject.results == null || sphinxProject.results.result.length == 0) {
            log.error("?????????????????????? ???????????????? ?????????? ?? ???????? ?????????? " + sphinxProject.name + " ?? ?????????? " + branch.getText());
            return;
        }
        for (Result r : sphinxProject.results.result) {
            radioGroupListBuildsOP.add(new RadioButton(r.plan.master.name + " #" + r.buildNumber + " - " + r.buildState));
            radioGroupListBuildsOP.get(iteratorList).setToggleGroup(group);
            radioGroupListBuildsOP.get(iteratorList).setOnMouseMoved(event -> paintRadio(radioGroupListBuildsOP));
            gridPaneTableDeploysOP.add(radioGroupListBuildsOP.get(iteratorList), 0, iterator);
            iterator++;
            iteratorList++;
        }
    }

    public void cancelModalDeployOP() {
        deployOpModal.setVisible(false);
        opProjectName.setText("");
        radioGroupListBuildsOP.clear();
        gridPaneTableDeploysOP.getChildren().removeAll();
        gridPaneTableDeploysOP.getChildren().clear();
        log.info("?????????????? ???????????? ?? ???? ?????????????? ???????????? ????");
    }

    public void startDeployingOP() {
        RadioButton selectedRadio = new RadioButton();
        for (RadioButton radio : radioGroupListBuildsOP) {
            if (radio.isSelected()) {
                selectedRadio = radio;
            }
        }
        String radioText = selectedRadio.getText();

        log.info("Selected radioButton is: " + radioText);

        int buildNumber = Integer.parseInt(radioText.substring(radioText.indexOf("#") + 1, radioText.lastIndexOf(" -")));
        log.info("Build number: " + buildNumber);

        Project selectedProject = new Project();
        for (Project p : deployerOP.getOpProjects()) {
            if (p.results == null)
                continue;
            for (Result r : p.results.result) {
                if (radioText.contains(r.plan.master.name) & r.plan.master.name.contains(p.name)) {
                    selectedProject = p;
                    break;
                }
            }
        }
        log.info("Selected Project is: " + selectedProject);

        clearOtherResults(selectedProject, buildNumber);

        setActivateStands();
        for (Stand stand : activeClassList) {
            log.info("?????????????? ??????????: " + stand.getName());
            stand.setProjectOp(selectedProject);
            deployerOP.createRelease(stand);
            deployerOP.deploy(stand);
            refreshStand(stand);
        }

        cancelModalDeployOP();
        enableStatusDeploysPanes();
    }

    private void setActivateStands() {
        activeClassList = new ArrayList<>();
        for (String stand: getStandNamesList()) {
            switch (stand) {
                case ("??????-3"):
                    eis3 = new EIS3();
                    activeClassList.add(eis3);
                    break;
                case ("??????-4"):
                    eis4 = new EIS4();
                    activeClassList.add(eis4);
                    break;
                case ("??????-5"):
                    eis5 = new EIS5();
                    activeClassList.add(eis5);
                    break;
                case ("??????-6"):
                    eis6 = new EIS6();
                    activeClassList.add(eis6);
                    break;
                case ("??????-7"):
                    eis7 = new EIS7();
                    activeClassList.add(eis7);
                    break;
            }
        }
    }

    private void clearOtherResults(final Project project, final int buildNumber) {
        Result temp;
        for (Result r : project.results.result) {
            if (r.buildNumber == buildNumber) {
                temp = r;
                project.results.result = null;
                project.oneResult = temp;
            }
        }
    }

    private void refreshStand(Stand stand) {
        switch (stand.getName()) {
            case ("??????-3"):
                refreshDeploysStatusEis3Op();
                break;
            case ("??????-4"):
                refreshDeploysStatusEis4Op();
                break;
            case ("??????-5"):
                refreshDeploysStatusEis5Op();
                break;
            case ("??????-6"):
                refreshDeploysStatusEis6Op();
                break;
            case ("??????-7"):
                refreshDeploysStatusEis7Op();
                break;
        }
    }

    public void setStatusDeploysListViewOp(Stand stand) {
        switch (stand.getName()) {
            case ("??????-3"):
                statusDeploysListViewEis3Op.getItems().clear();
                statusDeploysListViewEis3Op.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        String labelText = statusDeploysListViewEis3Op.getSelectionModel().getSelectedItem().getText();
                        String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                        log.info("URL: " + url);
                        goToUrl(url);
                    }
                });
                break;
            case ("??????-4"):
                statusDeploysListViewEis4Op.getItems().clear();
                statusDeploysListViewEis4Op.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        String labelText = statusDeploysListViewEis4Op.getSelectionModel().getSelectedItem().getText();
                        String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                        log.info("URL: " + url);
                        goToUrl(url);
                    }
                });
                break;
            case ("??????-5"):
                statusDeploysListViewEis5Op.getItems().clear();
                statusDeploysListViewEis5Op.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        String labelText = statusDeploysListViewEis5Op.getSelectionModel().getSelectedItem().getText();
                        String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                        log.info("URL: " + url);
                        goToUrl(url);
                    }
                });
                break;
            case ("??????-6"):
                statusDeploysListViewEis6Op.getItems().clear();
                statusDeploysListViewEis6Op.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        String labelText = statusDeploysListViewEis6Op.getSelectionModel().getSelectedItem().getText();
                        String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                        log.info("URL: " + url);
                        goToUrl(url);
                    }
                });
                break;
            case ("??????-7"):
                statusDeploysListViewEis7Op.getItems().clear();
                statusDeploysListViewEis7Op.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        String labelText = statusDeploysListViewEis7Op.getSelectionModel().getSelectedItem().getText();
                        String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
                        log.info("URL: " + url);
                        goToUrl(url);
                    }
                });
                break;
        }
    }

    public void refreshDeploysStatusEis3Op() {
        setStatusDeploysListViewOp(eis3);
        deployerOP.getDeployResult(eis3);
        Project p = eis3.getProjectOp();

        int i = 0;

        String deploymentState = p.deploymentResult.deploymentState;
        String lifeCycleState = p.deploymentResult.lifeCycleState;

        if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
            statusDeploysListViewEis3Op.getItems().add(new Label());
            statusDeploysListViewEis3Op.getItems().get(i).setText(p.oneResult.plan.name + " = ?? ???????????????? ?????? ??????????????\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("SUCCESS")) {
            statusDeploysListViewEis3Op.getItems().add(new Label());
            statusDeploysListViewEis3Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis3Op.getItems().get(i).setTextFill(Paint.valueOf("Green"));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("FAILED")) {
            statusDeploysListViewEis3Op.getItems().add(new Label());
            statusDeploysListViewEis3Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis3Op.getItems().get(i).setTextFill(Paint.valueOf("Red"));
        }
        log.info("statusDeploysListView1: " + statusDeploysListViewEis3Op.getItems());
        openAllDeploys1.setDisable(statusDeploysListViewEis3Op.getItems().size() <= 0);
    }
    public void refreshDeploysStatusEis4Op() {
        setStatusDeploysListViewOp(eis4);
        deployerOP.getDeployResult(eis4);
        Project p = eis4.getProjectOp();

        int i = 0;

        String deploymentState = p.deploymentResult.deploymentState;
        String lifeCycleState = p.deploymentResult.lifeCycleState;

        if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
            statusDeploysListViewEis4Op.getItems().add(new Label());
            statusDeploysListViewEis4Op.getItems().get(i).setText(p.oneResult.plan.name + " = ?? ???????????????? ?????? ??????????????\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("SUCCESS")) {
            statusDeploysListViewEis4Op.getItems().add(new Label());
            statusDeploysListViewEis4Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis4Op.getItems().get(i).setTextFill(Paint.valueOf("Green"));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("FAILED")) {
            statusDeploysListViewEis4Op.getItems().add(new Label());
            statusDeploysListViewEis4Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis4Op.getItems().get(i).setTextFill(Paint.valueOf("Red"));
        }
        log.info("statusDeploysListView1: " + statusDeploysListViewEis4Op.getItems());
        openAllDeploys1.setDisable(statusDeploysListViewEis4Op.getItems().size() <= 0);
    }
    public void refreshDeploysStatusEis5Op() {
        setStatusDeploysListViewOp(eis5);
        deployerOP.getDeployResult(eis5);
        Project p = eis5.getProjectOp();

        int i = 0;

        String deploymentState = p.deploymentResult.deploymentState;
        String lifeCycleState = p.deploymentResult.lifeCycleState;

        if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
            statusDeploysListViewEis5Op.getItems().add(new Label());
            statusDeploysListViewEis5Op.getItems().get(i).setText(p.oneResult.plan.name + " = ?? ???????????????? ?????? ??????????????\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("SUCCESS")) {
            statusDeploysListViewEis5Op.getItems().add(new Label());
            statusDeploysListViewEis5Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis5Op.getItems().get(i).setTextFill(Paint.valueOf("Green"));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("FAILED")) {
            statusDeploysListViewEis5Op.getItems().add(new Label());
            statusDeploysListViewEis5Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis5Op.getItems().get(i).setTextFill(Paint.valueOf("Red"));
        }
        log.info("statusDeploysListView1: " + statusDeploysListViewEis5Op.getItems());
        openAllDeploys1.setDisable(statusDeploysListViewEis5Op.getItems().size() <= 0);
    }
    public void refreshDeploysStatusEis6Op() {
        setStatusDeploysListViewOp(eis6);
        deployerOP.getDeployResult(eis6);
        Project p = eis6.getProjectOp();

        int i = 0;

        String deploymentState = p.deploymentResult.deploymentState;
        String lifeCycleState = p.deploymentResult.lifeCycleState;

        if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
            statusDeploysListViewEis6Op.getItems().add(new Label());
            statusDeploysListViewEis6Op.getItems().get(i).setText(p.oneResult.plan.name + " = ?? ???????????????? ?????? ??????????????\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("SUCCESS")) {
            statusDeploysListViewEis6Op.getItems().add(new Label());
            statusDeploysListViewEis6Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis6Op.getItems().get(i).setTextFill(Paint.valueOf("Green"));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("FAILED")) {
            statusDeploysListViewEis6Op.getItems().add(new Label());
            statusDeploysListViewEis6Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis6Op.getItems().get(i).setTextFill(Paint.valueOf("Red"));
        }
        log.info("statusDeploysListViewEis6Op: " + statusDeploysListViewEis6Op.getItems());
        openAllDeploys1.setDisable(statusDeploysListViewEis6Op.getItems().size() <= 0);
    }
    public void refreshDeploysStatusEis7Op() {
        setStatusDeploysListViewOp(eis7);
        deployerOP.getDeployResult(eis7);
        Project p = eis7.getProjectOp();

        int i = 0;

        String deploymentState = p.deploymentResult.deploymentState;
        String lifeCycleState = p.deploymentResult.lifeCycleState;

        if (deploymentState.equalsIgnoreCase("UNKNOWN")) {
            statusDeploysListViewEis7Op.getItems().add(new Label());
            statusDeploysListViewEis7Op.getItems().get(i).setText(p.oneResult.plan.name + " = ?? ???????????????? ?????? ??????????????\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("SUCCESS")) {
            statusDeploysListViewEis7Op.getItems().add(new Label());
            statusDeploysListViewEis7Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis7Op.getItems().get(i).setTextFill(Paint.valueOf("Green"));
            i++;
        }
        if (deploymentState.equalsIgnoreCase("FAILED")) {
            statusDeploysListViewEis7Op.getItems().add(new Label());
            statusDeploysListViewEis7Op.getItems().get(i).setText(p.oneResult.plan.name + " = " + deploymentState + "\n" +
                    createUrlForDeploy(p.deploymentResultIdOp.deploymentResultId));
            statusDeploysListViewEis7Op.getItems().get(i).setTextFill(Paint.valueOf("Red"));
        }
        log.info("statusDeploysListViewEis7Op: " + statusDeploysListViewEis7Op.getItems());
        openAllDeploys1.setDisable(statusDeploysListViewEis7Op.getItems().size() <= 0);
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploysEis3op() {
        for (Label i : statusDeploysListViewEis3Op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploysEis4op() {
        for (Label i : statusDeploysListViewEis4Op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploysEis5op() {
        for (Label i : statusDeploysListViewEis5Op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploysEis6op() {
        for (Label i : statusDeploysListViewEis6Op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }

    /**
     * ?????????????????? ?????? ??????????
     */
    public void openAllDeploysEis7op() {
        for (Label i : statusDeploysListViewEis7Op.getItems()) {
            String labelText = i.getText();
            String url = labelText.substring(labelText.indexOf("https://ci-sel.dks.lanit.ru/"));
            log.info("URL: " + url);
            goToUrl(url);
        }
    }
}