package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

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
    protected Button confirmStandsButton;
    @FXML
    protected Button newStandsButton;
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
    protected ListView<Label> statusDeploysListViewEis3Op;
    @FXML
    protected ListView<Label> statusDeploysListViewEis4Op;
    @FXML
    protected ListView<Label> statusDeploysListViewEis5Op;
    @FXML
    protected ListView<Label> statusDeploysListViewEis6Op;
    @FXML
    protected ListView<Label> statusDeploysListViewEis7Op;

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

    private void disableAllStandsTab() {
        Eis3OpTab.setDisable(true);
        Eis4OpTab.setDisable(true);
        Eis5OpTab.setDisable(true);
        Eis6OpTab.setDisable(true);
        Eis7OpTab.setDisable(true);
    }

    public void confirmStands() {
        standNameOP.setDisable(true);
        standName2op.setDisable(true);
        standName3op.setDisable(true);

        addStand2op.setDisable(true);
        addStand3op.setDisable(true);
        removeStand2op.setDisable(true);
        removeStand3op.setDisable(true);

        prepareEpz.setDisable(false);
        prepareEpzBd.setDisable(false);
        prepareSphinx.setDisable(false);

        newStandsButton.setVisible(true);
        confirmStandsButton.setVisible(false);

        enableStandTab(standNameOP.getText());
        enableStandTab(standName2op.getText());
        enableStandTab(standName3op.getText());
    }

    public void newStands() {
        resetTab();
    }

    public void resetTab() {
        standNameOP.setDisable(false);
        standName2op.setDisable(false);
        standName3op.setDisable(false);

        addStand2op.setDisable(false);
        addStand3op.setDisable(false);
        removeStand2op.setDisable(false);
        removeStand3op.setDisable(false);

        prepareEpz.setDisable(true);
        prepareEpzBd.setDisable(true);
        prepareSphinx.setDisable(true);

        newStandsButton.setVisible(false);
        confirmStandsButton.setVisible(false);
        standNameOP.setText("Выберите стенд");
        standName2op.setText("Выберите стенд");
        standName3op.setText("Выберите стенд");
        disableAllStandsTab();
    }

    public List<String> getStandNamesList() {
        List<String> local = new ArrayList<>();
        String standNameOPText = standNameOP.getText();
        String standName2OPText = standName2op.getText();
        String standName3OPText = standName3op.getText();

        if (!(standNameOPText == null || standNameOPText.isEmpty()))
            local.add(standNameOPText);
        if (!(standName2OPText == null || standName2OPText.isEmpty()))
            local.add(standName2OPText);
        if (!(standName3OPText == null || standName3OPText.isEmpty()))
            local.add(standName3OPText);

        return local;
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
        standName2op.setText("Выберите стенд");
    }

    public void removeStand3op() {
        standName3op.setVisible(false);
        addStand3op.setVisible(true);
        removeStand3op.setVisible(false);
        removeStand2op.setVisible(true);
        standName3op.setText("Выберите стенд");
    }
}
