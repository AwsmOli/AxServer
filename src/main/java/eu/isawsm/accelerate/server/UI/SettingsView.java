package eu.isawsm.accelerate.server.UI;

import eu.isawsm.accelerate.server.AxProperties;
import eu.isawsm.accelerate.server.Readers.SerialReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.naming.ConfigurationException;

/**
 * Created by ofade on 15.08.2015.
 */
public class SettingsView {
    public TextField tfClubName;
    public TextField tfMinLapTime;
    public TextField tfMaxLapTime;
    public TextField tfWifiPort;
    public ComboBox cbSerialPort;
    public ComboBox cdDecoder;
    private AxProperties properties;

    private ObservableList<String> decoderOptions =
            FXCollections.observableArrayList(
                    "MyLaps P89",
                    "MyLaps P3"
            );
    private ObservableList<String> comPortOptions =
            FXCollections.observableArrayList(
                    SerialReader.searchForPorts().keySet()
            );

    public SettingsView() {

    }

    public void cancel(ActionEvent actionEvent) {
        ((Stage)tfClubName.getScene().getWindow()).close();
    }

    public void save(ActionEvent actionEvent) {

        properties.writeClubName(tfClubName.getText());
        properties.writeMinLapTime(tfMinLapTime.getText());
        properties.writeMaxLapTime(tfMaxLapTime.getText());

        properties.writeWifiPort(tfWifiPort.getText());
        properties.writeSerialPort(cbSerialPort.getValue());
        properties.writeDecoder(cdDecoder.getValue());

        ((Stage)tfClubName.getScene().getWindow()).close();
    }

    public void setProperties(AxProperties properties) {
        this.properties = properties;

        tfClubName.setText(properties.club.getName());
        tfMinLapTime.setText(String.valueOf(properties.club.getTracks().get(0).getCourse().getMinTime()));
        tfMaxLapTime.setText(String.valueOf(properties.club.getTracks().get(0).getCourse().getMinTime()));

        tfWifiPort.setText(String.valueOf(properties.PORT));

        cbSerialPort.setItems(comPortOptions);
        cbSerialPort.setValue(properties.COMPORT);

        cdDecoder.setItems(decoderOptions);
        cdDecoder.setValue(properties.decoder);
    }

    public AxProperties getProperties() {
        return properties;
    }
}
