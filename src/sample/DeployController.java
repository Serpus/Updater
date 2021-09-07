package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class DeployController {
    @FXML
    protected MenuButton standName;
    @FXML
    protected MenuButton standName2;
    @FXML
    protected MenuButton standName3;
    @FXML
    protected Button prepareDeployModal;
    @FXML
    protected Button addStand2;
    @FXML
    protected Button addStand3;
    @FXML
    protected Button removeStand2;
    @FXML
    protected Button removeStand3;
    @FXML
    protected Button openAllDeploys1;
    @FXML
    protected Button openAllDeploys2;
    @FXML
    protected Button openAllDeploys3;
    @FXML
    protected Button refreshDeploysButton1;
    @FXML
    protected Button refreshDeploysButton2;
    @FXML
    protected Button refreshDeploysButton3;
    @FXML
    protected DialogPane deployModal;
    @FXML
    protected Pane statusDeploysPane1;
    @FXML
    protected Pane statusDeploysPane2;
    @FXML
    protected Pane statusDeploysPane3;
    @FXML
    protected GridPane checkboxTableDeploys;
    @FXML
    protected MenuButton deployStatusStandMenu;
    @FXML
    protected Label deployStatusStandLabel1;
    @FXML
    protected Label deployStatusStandLabel2;
    @FXML
    protected Label deployStatusStandLabel3;
    @FXML
    protected ListView<Label> statusDeploysListView1;
    @FXML
    protected ListView<Label> statusDeploysListView2;
    @FXML
    protected ListView<Label> statusDeploysListView3;

    protected ArrayList<CheckBox> checkBoxListSuccessBuilds;
    protected ArrayList<CheckBox> checkBoxListUnknownBuilds;



    public void cancelModalDeploy() {
        deployModal.setVisible(false);
        statusDeploysPane1.setVisible(true);
        deployStatusStandMenu.setVisible(true);
    }

    public void setDeployStatusStandMenu() {
        deployStatusStandMenu.setDisable(false);
        if (standName.getText().contains("ЕИС")) {
            deployStatusStandMenu.getItems().add(new MenuItem(standName.getText()));
            deployStatusStandMenu.getItems().get(0).setOnAction(event -> setStandNameToDeployStatus1(standName.getText()));
        }

        if (standName2.isVisible() & standName2.getText().contains("ЕИС")) {
            deployStatusStandMenu.getItems().add(new MenuItem(standName2.getText()));
            deployStatusStandMenu.getItems().get(1).setOnAction(event -> setStandNameToDeployStatus2(standName2.getText()));
        }

        if (standName3.isVisible() & standName3.getText().contains("ЕИС")) {
            deployStatusStandMenu.getItems().add(new MenuItem(standName3.getText()));
            deployStatusStandMenu.getItems().get(2).setOnAction(event -> setStandNameToDeployStatus3(standName3.getText()));
        }
    }

    protected void clearDeployStatusStand() {
        deployStatusStandMenu.getItems().clear();
        deployStatusStandLabel1.setText("");
        deployStatusStandLabel2.setText("");
        deployStatusStandLabel3.setText("");

    }

    private void setStandNameToDeployStatus1(final String standName) {
        deployStatusStandMenu.setText(standName);
        deployStatusStandLabel1.setText("Стенд " + standName);
        statusDeploysPane1.setVisible(true);
        statusDeploysPane2.setVisible(false);
        statusDeploysPane3.setVisible(false);
        refreshDeploysButton1.setDisable(false);
    }

    private void setStandNameToDeployStatus2(final String standName) {
        deployStatusStandMenu.setText(standName);
        deployStatusStandLabel2.setText("Стенд " + standName);
        statusDeploysPane1.setVisible(false);
        statusDeploysPane2.setVisible(true);
        statusDeploysPane3.setVisible(false);
        refreshDeploysButton2.setDisable(false);
    }

    private void setStandNameToDeployStatus3(final String standName) {
        deployStatusStandMenu.setText(standName);
        deployStatusStandLabel3.setText("Стенд " + standName);
        statusDeploysPane1.setVisible(false);
        statusDeploysPane2.setVisible(false);
        statusDeploysPane3.setVisible(true);
        refreshDeploysButton3.setDisable(false);
    }

    protected void enableDeployStatusPanes() {
        statusDeploysPane1.setDisable(false);
        statusDeploysPane2.setDisable(false);
        statusDeploysPane3.setDisable(false);
    }

    protected void disableDeployStatusPanes() {
        statusDeploysPane1.setDisable(true);
        statusDeploysPane2.setDisable(true);
        statusDeploysPane3.setDisable(true);
    }

    protected void disableRefreshButton() {
        refreshDeploysButton1.setDisable(true);
        refreshDeploysButton2.setDisable(true);
        refreshDeploysButton3.setDisable(true);
    }

    protected void defaultVisibleDeployStatusPane() {
        statusDeploysPane1.setVisible(true);
        statusDeploysPane2.setVisible(false);
        statusDeploysPane3.setVisible(false);
    }

    public void disableStands() {
        standName.setDisable(true);
        standName2.setDisable(true);
        standName3.setDisable(true);
        addStand2.setDisable(true);
        addStand3.setDisable(true);
        removeStand2.setDisable(true);
        removeStand3.setDisable(true);
    }

    public void enableStands() {
        standName.setDisable(false);
        standName2.setDisable(false);
        standName3.setDisable(false);
        addStand2.setDisable(false);
        addStand3.setDisable(false);
        removeStand2.setDisable(false);
        removeStand3.setDisable(false);
    }

    public void setEIS3() {
        standName.setText("ЕИС-3");
    }
    public void setEIS4() {
        standName.setText("ЕИС-4");
    }
    public void setEIS5() {
        standName.setText("ЕИС-5");
    }
    public void setEIS6() {
        standName.setText("ЕИС-6");
    }
    public void setEIS7() {
        standName.setText("ЕИС-7");
    }

    public void setEIS32() {
        standName2.setText("ЕИС-3");
    }
    public void setEIS42() {
        standName2.setText("ЕИС-4");
    }
    public void setEIS52() {
        standName2.setText("ЕИС-5");
    }
    public void setEIS62() {
        standName2.setText("ЕИС-6");
    }
    public void setEIS72() {
        standName2.setText("ЕИС-7");
    }

    public void setEIS33() {
        standName3.setText("ЕИС-3");
    }
    public void setEIS43() {
        standName3.setText("ЕИС-4");
    }
    public void setEIS53() {
        standName3.setText("ЕИС-5");
    }
    public void setEIS63() {
        standName3.setText("ЕИС-6");
    }
    public void setEIS73() {
        standName3.setText("ЕИС-7");
    }

    public void addStand2() {
        standName2.setVisible(true);
        addStand2.setVisible(false);
        addStand3.setVisible(true);
        removeStand2.setVisible(true);
    }

    public void addStand3() {
        standName3.setVisible(true);
        addStand3.setVisible(false);
        removeStand2.setVisible(false);
        removeStand3.setVisible(true);
    }

    public void removeStand2() {
        standName2.setVisible(false);
        removeStand2.setVisible(false);
        addStand2.setVisible(true);
        addStand3.setVisible(false);
    }

    public void removeStand3() {
        standName3.setVisible(false);
        addStand3.setVisible(true);
        removeStand3.setVisible(false);
        removeStand2.setVisible(true);
    }

}
