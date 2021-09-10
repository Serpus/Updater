package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class DeployControllerOP {
    @FXML
    protected MenuButton standNameOP;
    @FXML
    protected MenuButton standName2op;
    @FXML
    protected MenuButton standName3op;
    @FXML
    protected Button addStand2op;
    @FXML
    protected Button addStand3op;
    @FXML
    protected Button removeStand2op;
    @FXML
    protected Button removeStand3op;
    @FXML
    protected Button openAllDeploys1op;
    @FXML
    protected Button openAllDeploys2op;
    @FXML
    protected Button openAllDeploys3op;
    @FXML
    protected Button refreshDeploysButton1op;
    @FXML
    protected Button refreshDeploysButton2op;
    @FXML
    protected Button refreshDeploysButton3op;
    @FXML
    protected Button prepareEpzBd;
    @FXML
    protected Button prepareEpz;
    @FXML
    protected Button prepareSphinx;
    @FXML
    protected Button cancelModalDeployOP;
    @FXML
    protected Button confirmDeployOP;
    @FXML
    protected DialogPane deployOpModal;
    @FXML
    protected GridPane checkboxTableDeploysOP;
    @FXML
    protected Pane statusDeploysPane1op;
    @FXML
    protected Pane statusDeploysPane2op;
    @FXML
    protected Pane statusDeploysPane3op;
    @FXML
    protected MenuButton deployStatusStandMenuOP;
    @FXML
    protected Label deployStatusStandLabel1op;
    @FXML
    protected Label deployStatusStandLabel2op;
    @FXML
    protected Label deployStatusStandLabel3op;
    @FXML
    protected Label opProjectName;
    @FXML
    protected ListView<Label> statusDeploysListView1op;
    @FXML
    protected ListView<Label> statusDeploysListView2op;
    @FXML
    protected ListView<Label> statusDeploysListView3op;




    public void setDeployStatusStandMenuOP() {
        deployStatusStandMenuOP.setDisable(false);
        if (standNameOP.getText().contains("ЕИС")) {
            deployStatusStandMenuOP.getItems().add(new MenuItem(standNameOP.getText()));
            deployStatusStandMenuOP.getItems().get(0).setOnAction(event -> setStandNameToDeployStatus1op(standNameOP.getText()));
        }

        if (standName2op.isVisible() & standName2op.getText().contains("ЕИС")) {
            deployStatusStandMenuOP.getItems().add(new MenuItem(standName2op.getText()));
            deployStatusStandMenuOP.getItems().get(1).setOnAction(event -> setStandNameToDeployStatus2op(standName2op.getText()));
        }

        if (standName3op.isVisible() & standName3op.getText().contains("ЕИС")) {
            deployStatusStandMenuOP.getItems().add(new MenuItem(standName3op.getText()));
            deployStatusStandMenuOP.getItems().get(2).setOnAction(event -> setStandNameToDeployStatus3op(standName3op.getText()));
        }
    }

    protected void clearDeployStatusStandOP() {
        deployStatusStandMenuOP.getItems().clear();
        deployStatusStandLabel1op.setText("");
        deployStatusStandLabel2op.setText("");
        deployStatusStandLabel3op.setText("");

    }

    private void setStandNameToDeployStatus1op(final String standName) {
        deployStatusStandMenuOP.setText(standName);
        deployStatusStandLabel1op.setText("Стенд " + standName);
        statusDeploysPane1op.setVisible(true);
        statusDeploysPane2op.setVisible(false);
        statusDeploysPane3op.setVisible(false);
        refreshDeploysButton1op.setDisable(false);
    }

    private void setStandNameToDeployStatus2op(final String standName) {
        deployStatusStandMenuOP.setText(standName);
        deployStatusStandLabel2op.setText("Стенд " + standName);
        statusDeploysPane1op.setVisible(false);
        statusDeploysPane2op.setVisible(true);
        statusDeploysPane3op.setVisible(false);
        refreshDeploysButton2op.setDisable(false);
    }

    private void setStandNameToDeployStatus3op(final String standName) {
        deployStatusStandMenuOP.setText(standName);
        deployStatusStandLabel3op.setText("Стенд " + standName);
        statusDeploysPane1op.setVisible(false);
        statusDeploysPane2op.setVisible(false);
        statusDeploysPane3op.setVisible(true);
        refreshDeploysButton3op.setDisable(false);
    }

    protected void enableDeployStatusPanesOP() {
        statusDeploysPane1op.setDisable(false);
        statusDeploysPane2op.setDisable(false);
        statusDeploysPane3op.setDisable(false);
    }

    protected void disableDeployStatusPanesOP() {
        statusDeploysPane1op.setDisable(true);
        statusDeploysPane2op.setDisable(true);
        statusDeploysPane3op.setDisable(true);
    }

    protected void disableRefreshButtonOP() {
        refreshDeploysButton1op.setDisable(true);
        refreshDeploysButton2op.setDisable(true);
        refreshDeploysButton3op.setDisable(true);
    }

    protected void defaultVisibleDeployStatusPaneOP() {
        statusDeploysPane1op.setVisible(true);
        statusDeploysPane2op.setVisible(false);
        statusDeploysPane3op.setVisible(false);
    }

    public void disableStandsOP() {
        standNameOP.setDisable(true);
        standName2op.setDisable(true);
        standName3op.setDisable(true);
        addStand2op.setDisable(true);
        addStand3op.setDisable(true);
        removeStand2op.setDisable(true);
        removeStand3op.setDisable(true);
    }

    public void enableStandsOP() {
        standNameOP.setDisable(false);
        standName2op.setDisable(false);
        standName3op.setDisable(false);
        addStand2op.setDisable(false);
        addStand3op.setDisable(false);
        removeStand2op.setDisable(false);
        removeStand3op.setDisable(false);
    }

    public void setEIS3op() {
        standNameOP.setText("ЕИС-3");
    }
    public void setEIS4op() {
        standNameOP.setText("ЕИС-4");
    }
    public void setEIS5op() {
        standNameOP.setText("ЕИС-5");
    }
    public void setEIS6op() {
        standNameOP.setText("ЕИС-6");
    }
    public void setEIS7op() {
        standNameOP.setText("ЕИС-7");
    }

    public void setEIS32op() {
        standName2op.setText("ЕИС-3");
    }
    public void setEIS42op() {
        standName2op.setText("ЕИС-4");
    }
    public void setEIS52op() {
        standName2op.setText("ЕИС-5");
    }
    public void setEIS62op() {
        standName2op.setText("ЕИС-6");
    }
    public void setEIS72op() {
        standName2op.setText("ЕИС-7");
    }

    public void setEIS33op() {
        standName3op.setText("ЕИС-3");
    }
    public void setEIS43op() {
        standName3op.setText("ЕИС-4");
    }
    public void setEIS53op() {
        standName3op.setText("ЕИС-5");
    }
    public void setEIS63op() {
        standName3op.setText("ЕИС-6");
    }
    public void setEIS73op() {
        standName3op.setText("ЕИС-7");
    }

    public void addStand2op() {
        standName2op.setVisible(true);
        addStand2op.setVisible(false);
        addStand3op.setVisible(true);
        removeStand2op.setVisible(true);
    }

    public void addStand3op() {
        standName3op.setVisible(true);
        addStand3op.setVisible(false);
        removeStand2op.setVisible(false);
        removeStand3op.setVisible(true);
    }

    public void removeStand2op() {
        standName2op.setVisible(false);
        removeStand2op.setVisible(false);
        addStand2op.setVisible(true);
        addStand3op.setVisible(false);
    }

    public void removeStand3op() {
        standName3op.setVisible(false);
        addStand3op.setVisible(true);
        removeStand3op.setVisible(false);
        removeStand2op.setVisible(true);
    }

    public void showDeployModalEpzBd() {
        deployOpModal.setVisible(true);
        opProjectName.setText("Собранные билды ЕПЗ БД");
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

}
