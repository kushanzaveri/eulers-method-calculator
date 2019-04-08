import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.sourceforge.jeval.Evaluator;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

public class Mainframe extends Application implements EventHandler{

    private Label yPrimePrompt;
    private TextField yPrime;

    private Label sXPrompt;
    private TextField sX;

    private Label sYPrompt;
    private TextField sY;

    private Label stepPrompt;
    private TextField step;

    private Label stopPrompt;
    private TextField stop;

    private Label title;

    private GridPane gridPane;
    private Font labelFont;
    private Font titleFont;
    private Font tooltipFont;
    private Button submit;
    private ArrayList<XYChart.Data<Double, Double>> allData;
    private LineChart lineChart;
    private TableView<Point2D.Double> table;
    private boolean lineChartDrawn;
    private boolean tableDrawn;
    Stage tableStage = new Stage();
    Image image;
    public void start(Stage primaryStage){
        primaryStage.setX(0);
        primaryStage.setY(0);
        Group root = new Group();
        Scene scene = new Scene(root, 1280, 720, Color.WHITE);
        labelFont = new Font("Calibri",20);
        titleFont = new Font("Cambria Math",30);
        tooltipFont = new Font("Cambria Math", 16);
        initialize();
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(5);
        gridPane.setVgap(10);
        ColumnConstraints column1 = new ColumnConstraints(300);
        ColumnConstraints column2 = new ColumnConstraints(50,150,300);
        ColumnConstraints column3 = new ColumnConstraints(750);
        column3.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(column1,column2,column3);


        addToGridpane();

        root.getChildren().add(gridPane);
        primaryStage.setTitle("Euler's Method Graphing Calculator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private void initialize(){
        yPrimePrompt = new Label("What is y': ");
        yPrimePrompt.setFont(labelFont);

        sXPrompt = new Label("Enter the initial X value: ");
        sXPrompt.setFont(labelFont);

        sYPrompt = new Label("Enter the initial Y value: ");
        sYPrompt.setFont(labelFont);


        stepPrompt = new Label ("What is the step: ");
        stepPrompt.setFont(labelFont);

        stopPrompt = new Label ("Where should the function stop: ");
        stopPrompt.setFont(labelFont);

        title = new Label("Euler's Method Graphing Calculator");
        title.setFont(titleFont);
        yPrime = new TextField();
        sX = new TextField();
        sY = new TextField();
        step = new TextField();
        stop = new TextField();
        submit = new Button("Submit");

        image = new Image("legend.png");
    }
    private void addToGridpane() {

        GridPane.setHalignment(title, HPos.CENTER);
        gridPane.add(title,0,0,10,1);

        GridPane.setHalignment(yPrimePrompt, HPos.CENTER);
        GridPane.setHalignment(yPrime, HPos.LEFT);
        gridPane.add(yPrimePrompt, 0, 1);
        gridPane.add(yPrime, 1, 1);

        GridPane.setHalignment(sXPrompt, HPos.CENTER);
        GridPane.setHalignment(sX, HPos.LEFT);
        gridPane.add(sXPrompt, 0, 6);
        gridPane.add(sX, 1, 6);

        GridPane.setHalignment(sYPrompt, HPos.CENTER);
        GridPane.setHalignment(sY, HPos.LEFT);
        gridPane.add(sYPrompt, 0, 11);
        gridPane.add(sY, 1, 11);

        GridPane.setHalignment(stepPrompt, HPos.CENTER);
        GridPane.setHalignment(step, HPos.LEFT);
        gridPane.add(stepPrompt, 0,16);
        gridPane.add(step, 1, 16);

        GridPane.setHalignment(stopPrompt, HPos.CENTER);
        GridPane.setHalignment(stop, HPos.LEFT);
        gridPane.add(stopPrompt, 0,21);
        gridPane.add(stop, 1, 21);

        GridPane.setHalignment(submit, HPos.RIGHT);

        gridPane.add(submit, 0, 24);
        submit.setOnAction(this);
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        gridPane.add(iv1,0,27);
    }

    private void addLineChart(){
        GridPane.setValignment(lineChart, VPos.BASELINE);
        GridPane.setFillWidth(lineChart,true);
        GridPane.setRowSpan(lineChart, 30);

        gridPane.add(lineChart,2,1);
    }

    private void addTable(){

        if(tableDrawn){
            tableStage.close();
        }
        Scene tableScene = new Scene(new Group(), 470, 500, Color.WHITE);
        tableStage.setResizable(false);
        tableStage.setY(0);
        tableStage.setX(1280);
        tableStage.setTitle("Table of Values");
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll( table);

        ((Group) tableScene.getRoot()).getChildren().addAll(vbox);

        tableStage.setScene(tableScene);
        tableStage.show();
        tableDrawn = true;
    }
    private void publishData(){
        boolean valid = true;
        boolean expressionValid = true;
        String expression = null;
        String untouchedExpression = null;

        double sXV = 0, sYV = 0, stepV = 0, stopV = 0;
        //YPrime
        try{
            expression = yPrime.getText();
            untouchedExpression = expression;
            for(int i=0;i< expression.length();i++){
                if(i!=expression.length()-1 && expression.charAt(i) == 'p' && expression.charAt(i+1) == 'i')
                    expression = expression.substring(0,i) + "3.14159265358" + expression.substring(i+2,expression.length());
                if(expression.charAt(i)=='e'){
                    expression = expression.substring(0,i) + "2.71828" + expression.substring(i+1);
                }
                if(expression.charAt(i) == 'X' || expression.charAt(i) == 'x')
                    expression = expression.substring(0,i) + "3.12" + expression.substring(i+1);
                if(expression.charAt(i) == 'Y' || expression.charAt(i) == 'y')
                    expression = expression.substring(0,i) + "3.12" + expression.substring(i+1);
            }
            net.sourceforge.jeval.Evaluator engine = new Evaluator();
            System.out.println(expression);
            engine.evaluate(expression);
        }catch (Exception e) {
            System.out.println ("Error found for yPrime");
            expressionValid = false;
            yPrime.clear();
        }
        //sX
        try{
            sXV = Double.parseDouble(sX.getText());
        }catch (Exception e) {
            System.out.println ("Error found for sX");

            valid = false;
            sX.clear();
        }
        //sY
        try{
            sYV = Double.parseDouble(sY.getText());
        }catch (Exception e) {
            System.out.println ("Error found for sY");
            valid = false;
            sY.clear();
        }
        //step
        try{
            stepV = Double.parseDouble(step.getText());
        }catch (Exception e) {
            System.out.println ("Error found for stepV");

            valid = false;
            step.clear();
        }
        //stop
        try{
            stopV = Double.parseDouble(stop.getText());
            if (sXV >= stopV) {
                throw new Exception();
            }
        }catch (Exception e) {
            System.out.println ("Error found for stopV");

            valid = false;
            stop.clear();
        }

        if(valid && expressionValid) {
            try{
                DataGenerator.generateTable(untouchedExpression, sXV, sYV, stepV, stopV);
            }catch (Exception e){}
            generateLineChart();
            generateTable();
        }
        else{
            if(!valid && !expressionValid) {
                JOptionPane.showMessageDialog(null,
                        "Please make sure all input are valid floating point numbers and the expression can be evaluated.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else if(!valid){
                JOptionPane.showMessageDialog(null,
                        "Please make sure all input are valid floating point numbers.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null,
                        "Please make sure the expression can be evaluated.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            if(tableDrawn){
                tableStage.close();
            }
            if(lineChartDrawn) {
                gridPane.getChildren().remove(lineChart);
            }
        }
    }

    private void generateTable() {

        table = new TableView<Point2D.Double>();
        ObservableList<Point2D.Double> obData = FXCollections.observableArrayList();
        for(int i=0;i<allData.size();i++){
            Point2D.Double point = new Point2D.Double(allData.get(i).getXValue(),allData.get(i).getYValue());
            obData.add(point);
        }
        TableColumn col1 = new TableColumn("X");
        col1.setMinWidth(225);
        col1.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn col2 = new TableColumn("Y");
        col2.setMinWidth(225);
        col2.setResizable(false);

        col2.setCellValueFactory(new PropertyValueFactory<>("y"));

        table.setItems(obData);
        table.setFixedCellSize(25);
        table.getColumns().setAll(col1, col2);
        table.setEditable(false);
        addTable();
    }

    public void generateLineChart(){
        if(lineChartDrawn) {
            gridPane.getChildren().remove(lineChart);
        }
        allData = new ArrayList<>();

        for(int i=0;i<DataGenerator.points.size();i++){
            allData.add(new XYChart.Data(DataGenerator.points.get(i).x, DataGenerator.points.get(i).y));
        }

        DataGenerator.points.clear();
        XYChart.Series<Double,Double> series = new XYChart.Series<>();

        for(int i=0;i<allData.size();i++){
            series.getData().add(allData.get(i));
        }
        series.setName("Euler's Approximation");


        lineChart = new LineChart(new NumberAxis(), new NumberAxis());

        lineChart.getData().add(series);
        for(int i=0;i<allData.size();i++){
            Tooltip t = new Tooltip("("+allData.get(i).getXValue().toString() + ", " + allData.get(i).getYValue().toString()+")");
            t.setFont(tooltipFont);
            Tooltip.install(allData.get(i).getNode(), t);
        }
        lineChart.setCursor(Cursor.CROSSHAIR);
        lineChartDrawn = true;

        addLineChart();
    }



    @Override
    public void handle(Event event) {
        // Generate
        publishData();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
