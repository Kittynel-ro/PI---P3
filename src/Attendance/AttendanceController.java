package Attendance;

import Classes.Student;
import Classes.Teacher;
import Login.LoginModel;
import com.jfoenix.controls.JFXButton;
import dbConnection.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static Attendance.GenerateQRCode.generateQRCode;
import static java.lang.Thread.sleep;

public class AttendanceController implements Initializable {

    @FXML
    private ImageView avatar;
    @FXML
    private JFXButton updtBtn;

    private ObservableList<Student> students = FXCollections.observableArrayList();
    private TableView<Student> list_table;

    private Teacher logged = LoginModel.getLogged();
    // get connection
    private Connection conns = Connect.getConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            String  qcip = "src/resources/Quote.png";
            String value = "https://docs.google.com/forms/d/e/1FAIpQLSdqi_gN8AZpKTA1RhxV6j7ZsM7O3IwJ_DBR0rTNPuB59jfSkQ/viewform";
            generateQRCode(value,200, 200, qcip);
        } catch (Exception ex) {
            System.out.println("Could not generate QR Code" + ex);
        }

        Image qrCode = new Image("resources/Quote.png");

        avatar.setImage(qrCode);

    }
    private void checkConn() {
        // if connection is closed get it again
        try {
            if (conns.isClosed()) conns = Connect.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addRow(int id, String name, String mail, String sub) { // add new student
        checkConn();
        String query = "insert into '" + logged.getID() + "' (ID, name, email, subjects) values(?, ?, ?, ?)";

        try {
            PreparedStatement pst = Objects.requireNonNull(conns).prepareStatement(query);
            // set the question marks in statement to these values
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, mail);
            //pst.setString(5, bar_field.getText());
            pst.setString(4, "'(" + sub + ")");
            pst.execute();
            pst.close(); // close statement
            conns.close(); // close connection
            //LoadData(list_table, students); // reload table after adding new student
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void clearTable(){
        checkConn();
        String query = "DELETE FROM '" + logged.getID() + "'";
        try {
            PreparedStatement pst = Objects.requireNonNull(conns).prepareStatement(query);
            pst.execute();
            pst.close(); // close statement
            conns.close(); // close connection
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updtBtn() throws IOException, InterruptedException {

        int index = 1;

        clearTable();

        File file = new File("C:\\Users\\crist\\Downloads\\prezente - Răspunsuri la formular 1.csv");
        if(file.exists())
            file.delete();

        try {
            Desktop.getDesktop().browse(new URL("https://docs.google.com/spreadsheets/d/1et7K0ImSaDzDCRe47jgqvwI-lg4KYSnn_oTzkbD0ybM//export?format=csv&usp=sharing&gid=1921622024").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

        sleep(2000);

        String csvFilePath = "C:\\Users\\crist\\Downloads\\prezente - Răspunsuri la formular 1.csv";

        BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));

        String line = "";
        String splitBy = ",";
        String skip = "";


            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));

            skip = br.readLine();

            line = br.readLine();
            while (line != null && !line.isEmpty())   //returns a Boolean value
            {
                String[] data = line.split(splitBy);    // use comma as separator
                addRow(index, data[1], data[2], data[3]);
                index++;
                line = br.readLine();
            }
        }
    }



