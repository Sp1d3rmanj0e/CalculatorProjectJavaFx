package hellofx;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Arc;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Controller implements Initializable {

    @FXML
    private Button calculateButton;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private AnchorPane circleAngle;

    @FXML
    private Label coordinateOutput;

    @FXML
    private TextField degreeInput;

    @FXML
    private Label degreeOutput;

    @FXML
    private Label degreeSimpleOutput;

    @FXML
    private Label radianOutput;

    @FXML
    private Label radianSimpleOutput;

    @FXML
    private Arc unitCircleArc;

    @FXML
    private TextField radDenInput;

    @FXML
    private TextField radNumInput;

    private String[] options = {"Degrees", "Radians"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        choiceBox.getItems().addAll(options); // Adds all values in options[] to the choicebox

        if (Main.getRoomName().equals("Degrees")) {
            choiceBox.setValue(options[0]); // Sets default value to degrees
        }
        else {
            choiceBox.setValue(options[1]); // Sets default value to radians
        }

        // Switches rooms based on the value given
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue == options[1]) {
                    try {
                        switchToRadians(new ActionEvent(choiceBox, null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        switchToDegrees(new ActionEvent(choiceBox, null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToDegrees(ActionEvent event) throws IOException {

        System.out.println("Switching to degrees");

        Main.setRoomName("Degrees");

        root = FXMLLoader.load(getClass().getResource("calcDegrees.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Unit Circle Calculator: Degrees");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToRadians(ActionEvent event) throws IOException {

        System.out.println("Switching to radians");

        Main.setRoomName("Radians");
        
        root = FXMLLoader.load(getClass().getResource("calcRadians.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Unit Circle Calculator: Radians");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    

    double degrees;
    double simpleDegrees;

    public void calculate(ActionEvent event) {
        
        try {

            if (Main.getRoomName().equals("Degrees")) {
                // Gets the input from the user
                degrees = Double.parseDouble(degreeInput.getText()); // Gets the raw degree angle
                
            }
            else { // "Radians"
                
                double numerator   = Double.parseDouble(radNumInput.getText()); // X * pi, where X is the numerator
                double denominator = Double.parseDouble(radDenInput.getText()); // (X * pi) / Y, where Y is the denominator

                double value = (numerator * Math.PI) / denominator;
                degrees = Math.toDegrees(value);
            }
          
        }
        catch (NumberFormatException e){
            System.out.println("Numbers only plz");
        }
        catch (Exception e){
            System.out.println(e);
        }

        simpleDegrees = findSimpleAngle(degrees); // Gets the 0 <= x <= 360 version of the angle

        // Draws the angle on the unit circle by rotating the arc
        Rotate rotate = new Rotate(-simpleDegrees, 0, 0); // Gets a rotation based on degree input
        unitCircleArc.getTransforms().clear();            // Removes all previous rotations on the arc
        unitCircleArc.getTransforms().add(rotate);        // Adds new rotation based on degree input

        // Sets the degrees section to the degrees input
        degreeOutput.setText(Double.toString(degrees)); // Adds the raw degree angle
        degreeSimpleOutput.setText(Double.toString(simpleDegrees)); // Adds the simplified degree angle

        // Sets the coordinates section
        String coords = degToCoord(degrees);
        coordinateOutput.setText(coords);

        // Sets the simple radians section
        radianSimpleOutput.setText(degToRad(simpleDegrees));
        
        // Sets the radians section (raw coterminal)
        String radiansOutputString = (findCoterminals(degrees) * 2) + "π" + " + " + degToRad(simpleDegrees);
        radianOutput.setText(radiansOutputString);

    }

    // Returns the number of coterminals in a given angle
    // Ex. 430 = +2
    // Ex. -710 = -2
    private int findCoterminals(double angle)
    {
        int coterminal = 0; 
        
        // Find the 0 < x < 360 coterminal of angle (if needed)
        if (angle < 0)
        {
            while(angle < 0)
            {
                angle += 360;
                coterminal--;
            }
        }
        
        if (angle > 360)
        {
            while(angle > 360)
            {
                angle -= 360;
                coterminal++;
            }
        }

        return coterminal;
    }

    // Scale up or down a given angle by 360 degree increments
    // Until the angle is within a single unit circle radius
    private double findSimpleAngle(double angle)
    {
        // Find the 0 < x < 360 angle
        if (angle < 0)
        {
            while(angle < 0)
            {
                angle += 360;
            }
        }
        
        if (angle > 360)
        {
            while(angle > 360)
            {
                angle -= 360;
            }
        }

        return angle;
    }

    // Returns if a double is an integer
    private boolean isInteger(double num)
    {
        return (num % 1 == 0);
    }
    
    // Pseudo rounding without fancy stuff
    // Ex. 89.9999 should be 90
    private double roundIfClose(double degree)
    {
        if ((int)(degree + 0.0001) % 1 == 0)
        {
            degree = Math.round(degree);
        }
        
        return degree;
    }
    
    // Turns a deg angle (say 45) into the coords of that angle (√2/2,√2/2)
    private String degToCoord(double degree)
    {
        degree = roundIfClose(degree);
        
        if (isInteger(degree))
        {
            // find a unit circle fraction if possible
            switch((int)degree)
            {
                case 0:   return coord("1", "0");
                case 30:  return coord("√3/2", "1/2");
                case 45:  return coord("√2/2", "√2/2");
                case 60:  return coord("1/2", "√3/2");
                case 90:  return coord("0", "1");
                case 120: return coord("-1/2", "√3/2");
                case 135: return coord("-√2/2", "√2/2");
                case 150: return coord("-√3/2", "1/2");
                case 180: return coord("-1", "0");
                case 210: return coord("-√3/2", "-1/2");
                case 225: return coord("-√2/2", "-√2/2");
                case 240: return coord("-1/2", "-√3/2");
                case 270: return coord("0", "-1");
                case 300: return coord("1/2", "-√3/2");
                case 315: return coord("√2/2", "-√2/2");
                case 330: return coord("√3/2", "-1/2");
                case 360: return coord("1", "0");
            }
        }
        
        // No unit circle matches found...
        //double radian = Math.toRadians(degree);
        DecimalFormat df = new DecimalFormat("###.###");
        
        // Get the x and y coordinates of the degree (in string form)
        double y = Math.sin(degree);
        String strY= df.format(y);
        
        double x = Math.cos(degree);
        String strX = df.format(x);
        
        return coord(strY, strX);
    }
    
    // Will take inputs between 0 and 360,
    // find a coterminal if that doesn't fit
    private String degToRad(double coterminal)
    {
        coterminal = roundIfClose(coterminal);
        
        if (isInteger(coterminal))
        {
            // Find a unit circle fraction if possible
            switch((int)coterminal)
            {
                case 0: return   fraction("0",  "0");
                case 30: return  fraction("π",  "6");
                case 45: return  fraction("π",  "4");
                case 60: return  fraction("π",  "3");
                case 90: return  fraction("π",  "2");
                case 120: return fraction("2π", "3");
                case 135: return fraction("3π", "4");
                case 150: return fraction("5π", "6");
                case 180: return fraction("π",  "1");
                case 210: return fraction("7π", "6");
                case 225: return fraction("5π", "4");
                case 240: return fraction("4π", "3");
                case 270: return fraction("3π", "2");
                case 300: return fraction("5π", "3");
                case 315: return fraction("7π", "4");
                case 330: return fraction("11π","6");
                case 360: return fraction("2π", "1");
            }
        }
        // No unit circle matches found...
        
        // Round to nearest thousandth
        //DecimalFormat df = new DecimalFormat("###.###");
        
        // Switches from degrees to radians
        double radians = Math.toRadians(coterminal);
        
        // returns radians as a string
        return Double.toString(radians);
        
        
    }
    
    /**
     * @param numer - numerator of a fraction
     * @param denom - denominator of a fraction
     * 
     * @returns:
     * 
     * numer
     * ---- <- Variable based on length of inputs
     * denom
     */
    private String fraction(String numer, String denom)
    {
        // Get the length of the numerator and denominator
        return numer + "/" + denom;
    }
    
    
    private String coord(String x, String y)
    {
        return "(" + x + ", " + y + ")";
    }

}
