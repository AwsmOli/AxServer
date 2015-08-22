package eu.isawsm.accelerate.server.UI;/**
 * Created by ofade on 14.08.2015.
 */

import Shared.Car;
import Shared.Course;
import Shared.Lap;
import com.sun.javafx.binding.SelectBinding;
import eu.isawsm.accelerate.server.AxProperties;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;


public class MainForm {

    public Label lblStatus;
    public TableView<carTableEntry> tblResults;
    public TableColumn<carTableEntry, String> consistencyCol;
    public TableColumn<carTableEntry, Integer> lapsCol;
    public TableColumn<carTableEntry, Double> bestCol;
    public TableColumn<carTableEntry, Double> avgCol;
    public TableColumn<carTableEntry, String> driverCol;
    public TableView<detailTableEntry> tblDetail;
    public TableColumn<detailTableEntry, Integer> tcNumber;
    public TableColumn<detailTableEntry, Image> tcIcon;
    public TableColumn<detailTableEntry, Double> tcTime;
    private AxProperties properties;
    private ObservableList<Car> cars;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;


    }

    private Stage primaryStage;

    @FXML
    private void openSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings.fxml"));
        Parent root = loader.load();
        SettingsView settings = loader.getController();
        settings.setProperties(properties);
        Stage stage = new Stage();

        stage.setScene(new Scene(root));
        stage.setTitle("Axelerate Server - Settings");
        stage.getIcons().add(new Image("ic_launcher.png"));

        stage.showAndWait();

    }

    @FXML
    public void close() {
        primaryStage.close();
    }

    @FXML
    public void openAbout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/About.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setScene(new Scene(root));
        stage.setTitle("Axelerate Server - Settings");
        stage.getIcons().add(new Image("ic_launcher.png"));

        stage.show();
    }

    public void setStatusText(String s) {
        lblStatus.setText(s);
    }

    public void setProperties(AxProperties properties) {
        this.properties = properties;
    }

    public void setCars(ObservableList<Car> cars) {
        this.cars = cars;

        tblResults.setRowFactory(tv -> {
            TableRow<carTableEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) return;

                if (event.getClickCount() == 2) {
                    carTableEntry rowData = row.getItem();


                    Optional<String> response = Dialogs.create().styleClass(Dialog.STYLE_CLASS_NATIVE)
                            .owner(primaryStage)
                            .title("Name Driver")
                            .masthead("Set the name for " + rowData.getDriver())
                            .message("Name:")
                            .showTextInput(rowData.getDriver());

                    if (response.isPresent() && !response.get().trim().isEmpty())
                        rowData.setDriver(response.get().trim());
                } else {
                    ObservableList<detailTableEntry> detailTableEntries = FXCollections.observableArrayList();
                    detailTableEntries.addAll(row.getItem().car.getLaps().stream().map(l -> new detailTableEntry(row.getItem().car, l)).collect(Collectors.toList()));
                    Collections.reverse(detailTableEntries);
                    tblDetail.setItems(detailTableEntries);
                }
            });
            return row;
        });


        driverCol.setCellValueFactory(
                new PropertyValueFactory<>("driver")
        );
        avgCol.setCellValueFactory(
                new PropertyValueFactory<>("avg")
        );
        bestCol.setCellValueFactory(
                new PropertyValueFactory<>("best")
        );
        lapsCol.setCellValueFactory(
                new PropertyValueFactory<>("laps")
        );
        consistencyCol.setCellValueFactory(
                new PropertyValueFactory<>("consistency")
        );
        tblResults.setItems(carTableEntries);

        tblResults.getColumns().add(tmp);

        tcNumber.setCellValueFactory(new PropertyValueFactory<>("number"));

        tcIcon.setCellValueFactory(new PropertyValueFactory<>("image"));
        tcIcon.setCellFactory(param -> new TableCell<detailTableEntry, Image>(){
            @Override
            public void updateItem(Image item, boolean empty) {
                if(item!=null && !empty){
                    ImageView imageview = new ImageView();
                    imageview.setFitHeight(12);
                    imageview.setFitWidth(12);
                    imageview.setImage(item);

                    //SETTING ALL THE GRAPHICS COMPONENT FOR CELL
                    setGraphic(imageview);
                } else {
                    setGraphic(new ImageView());
                }
            }
        });

        tcTime.setCellValueFactory(new PropertyValueFactory<>("time"));


    }
    TableColumn<carTableEntry, String> tmp = new TableColumn<>();
    ObservableList<carTableEntry> carTableEntries = FXCollections.observableArrayList();
    public void updateCars(Car c) {
        for(carTableEntry cte : carTableEntries){
            if(cte.car.equals(c)) {
                cte.car = c;
                tmp.setVisible(false);
                tmp.setVisible(true);
                return;
            }
        }
        carTableEntries.add(new carTableEntry(c));
    }

    public void tableClicked(Event event) {

    }

    public class carTableEntry{
        public carTableEntry(Car car) {
            this.car = car;
        }

        public Car car;
        private String driverName = null;

        public String getDriver() {
            if(driverName != null) return driverName;
            return String.valueOf(car.getTransponderID());
        }

        public void setDriver(String driverName) {
            this.driverName = driverName;
        }

        public Double getAvg() {
            return car.getAvgTime(properties.club.getTracks().get(0).getCourse(), 50) /1000;
        }

        public Double getBest() {
            return car.getBestTime(properties.club.getTracks().get(0).getCourse()) /1000;
        }
        public Integer getLaps() {
            return car.getLapCount(properties.club.getTracks().get(0).getCourse());
        }

        public String getConsistency() {
            Double retVal = car.getConsistency(properties.club.getTracks().get(0).getCourse(), 50);
            if(retVal == -1) return "-";
            return retVal +"%";
        }
    }

    public class detailTableEntry{
        private Lap lap;
        private Car car;

        public detailTableEntry(Car car, Lap lap) {
           this.lap = lap;
            this.car = car;
        }

        public Integer getNumber() {
            return car.getLaps().indexOf(lap) +1;
        }

        public Image getImage() {

            Image retVal;

            Course currentCourse = lap.getCourse();

            if (lap.getTime() == car.getBestTime(currentCourse)) {
                retVal = (new Image("/star.png"));
                return retVal;
            }
            if (car.getAvgTime(currentCourse) + 5000 < lap.getTime()) {

                retVal = (new Image("/warn.png"));
                return retVal;
            }

            if (car.getAvgTime(currentCourse)+500 < lap.getTime()) {

                retVal = (new Image("/down.png"));
                return retVal;
            } else if(car.getAvgTime(currentCourse)-500 > lap.getTime() ) {

                retVal = (new Image("/up.png"));
                return retVal;
            } else {

                retVal = (new Image("/avg.png"));
                return retVal;
            }
        }

        public Double getTime() {
            return ((double) (lap.getTime())) / 1000d;
        }

    }
}
