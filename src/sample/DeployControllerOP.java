package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class DeployControllerOP {
    @FXML
    protected MenuButton standNameOP;
    @FXML
    protected MenuButton standName2op;
    @FXML
    protected MenuButton standName3op;

    @FXML
    protected MenuItem EIS3op;
    @FXML
    protected MenuItem EIS4op;
    @FXML
    protected MenuItem EIS5op;
    @FXML
    protected MenuItem EIS6op;
    @FXML
    protected MenuItem EIS7op;

    @FXML
    protected MenuItem EIS32op;
    @FXML
    protected MenuItem EIS42op;
    @FXML
    protected MenuItem EIS52op;
    @FXML
    protected MenuItem EIS62op;
    @FXML
    protected MenuItem EIS72op;

    @FXML
    protected MenuItem EIS33op;
    @FXML
    protected MenuItem EIS43op;
    @FXML
    protected MenuItem EIS53op;
    @FXML
    protected MenuItem EIS63op;
    @FXML
    protected MenuItem EIS73op;

    @FXML
    protected Button addStand2op;
    @FXML
    protected Button addStand3op;
    @FXML
    protected Button removeStand2op;
    @FXML
    protected Button removeStand3op;

    @FXML
    protected Button openAllDeploysEis3Op;
    @FXML
    protected Button openAllDeploysEis4Op;
    @FXML
    protected Button openAllDeploysEis5Op;
    @FXML
    protected Button openAllDeploysEis6Op;
    @FXML
    protected Button openAllDeploysEis7Op;

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
    protected Tab Eis3OpTab;
    @FXML
    protected Tab Eis4OpTab;
    @FXML
    protected Tab Eis5OpTab;
    @FXML
    protected Tab Eis6OpTab;
    @FXML
    protected Tab Eis7OpTab;

    @FXML
    protected Pane statusDeploysPaneEis3Op;
    @FXML
    protected Pane statusDeploysPaneEis4Op;
    @FXML
    protected Pane statusDeploysPaneEis5Op;
    @FXML
    protected Pane statusDeploysPaneEis6Op;
    @FXML
    protected Pane statusDeploysPaneEis7Op;

    @FXML
    protected MenuButton deployStatusStandMenuOP;

    @FXML
    protected Label deployStatusStandLabelEis3Op;
    @FXML
    protected Label deployStatusStandLabelEis4Op;
    @FXML
    protected Label deployStatusStandLabelEis5Op;
    @FXML
    protected Label deployStatusStandLabelEis6Op;
    @FXML
    protected Label deployStatusStandLabelEis7Op;

    @FXML
    protected Label opProjectName;
    @FXML
    protected ListView<Label> statusDeploysListView1op;
    @FXML
    protected ListView<Label> statusDeploysListView2op;
    @FXML
    protected ListView<Label> statusDeploysListView3op;

    protected ArrayList<RadioButton> radioGroupListBuildsOP;



    protected void clearDeployStatusStandOP() {
        deployStatusStandMenuOP.getItems().clear();
        deployStatusStandLabelEis3Op.setText("");
        deployStatusStandLabelEis3Op.setText("");
        deployStatusStandLabelEis3Op.setText("");
    }

    /**
     * Активируем вкладку стенда
     *
     * @param standName имя стенда
     */
    private void enableStandTab(final String standName) {
        String shortStand = standName.replace("ЕИС-", "");
        switch (shortStand) {
            case("3"):
                Eis3OpTab.setDisable(false);
                break;
            case("4"):
                Eis4OpTab.setDisable(false);
                break;
            case("5"):
                Eis5OpTab.setDisable(false);
                break;
            case("6"):
                Eis6OpTab.setDisable(false);
                break;
            case("7"):
                Eis7OpTab.setDisable(false);
                break;
        }
    }

    /**
     * Отключаем вкладку стенда
     *
     * @param standName имя стенда
     */
    private void disableStandTab(final String standName) {
        if (standName == null)
            return;
        String shortStand = standName.replace("ЕИС-", "");
        switch (shortStand) {
            case("3"):
                Eis3OpTab.setDisable(true);
                break;
            case("4"):
                Eis4OpTab.setDisable(true);
                break;
            case("5"):
                Eis5OpTab.setDisable(true);
                break;
            case("6"):
                Eis6OpTab.setDisable(true);
                break;
            case("7"):
                Eis7OpTab.setDisable(true);
                break;
        }
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
        disableStandTab(standNameOP.getText());
        standNameOP.setText("ЕИС-3");
        enableStandTab(standNameOP.getText());
    }
    public void setEIS4op() {
        disableStandTab(standNameOP.getText());
        standNameOP.setText("ЕИС-4");
        enableStandTab(standNameOP.getText());
    }
    public void setEIS5op() {
        disableStandTab(standNameOP.getText());
        standNameOP.setText("ЕИС-5");
        enableStandTab(standNameOP.getText());
    }
    public void setEIS6op() {
        disableStandTab(standNameOP.getText());
        standNameOP.setText("ЕИС-6");
        enableStandTab(standNameOP.getText());
    }
    public void setEIS7op() {
        disableStandTab(standNameOP.getText());
        standNameOP.setText("ЕИС-7");
        enableStandTab(standNameOP.getText());
    }

    public void setEIS32op() {
        disableStandTab(standName2op.getText());
        standName2op.setText("ЕИС-3");
        enableStandTab(standName2op.getText());
    }
    public void setEIS42op() {
        disableStandTab(standName2op.getText());
        standName2op.setText("ЕИС-4");
        enableStandTab(standName2op.getText());
    }
    public void setEIS52op() {
        disableStandTab(standName2op.getText());
        standName2op.setText("ЕИС-5");
        enableStandTab(standName2op.getText());
    }
    public void setEIS62op() {
        disableStandTab(standName2op.getText());
        standName2op.setText("ЕИС-6");
        enableStandTab(standName2op.getText());
    }
    public void setEIS72op() {
        disableStandTab(standName2op.getText());
        standName2op.setText("ЕИС-7");
        enableStandTab(standName2op.getText());
    }

    public void setEIS33op() {
        disableStandTab(standName3op.getText());
        standName3op.setText("ЕИС-3");
        enableStandTab(standName3op.getText());
    }
    public void setEIS43op() {
        disableStandTab(standName3op.getText());
        standName3op.setText("ЕИС-4");
        enableStandTab(standName3op.getText());
    }
    public void setEIS53op() {
        disableStandTab(standName3op.getText());
        standName3op.setText("ЕИС-5");
        enableStandTab(standName3op.getText());
    }
    public void setEIS63op() {
        disableStandTab(standName3op.getText());
        standName3op.setText("ЕИС-6");
        enableStandTab(standName3op.getText());
    }
    public void setEIS73op() {
        disableStandTab(standName3op.getText());
        standName3op.setText("ЕИС-7");
        enableStandTab(standName3op.getText());
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
        disableStandTab(standName2op.getText());
        standName2op.setText("Выберите стенд");
    }

    public void removeStand3op() {
        standName3op.setVisible(false);
        addStand3op.setVisible(true);
        removeStand3op.setVisible(false);
        removeStand2op.setVisible(true);
        disableStandTab(standName3op.getText());
        standName3op.setText("Выберите стенд");
    }
}
