import com.sun.javafx.charts.Legend;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.sourceforge.jeval.Evaluator;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Kushan on 1/9/2017.
 */
public class MainApplication extends Application implements EventHandler{
    GridPane layout;
    Label yPrimeL, initXL, initYL, stepL, stopL;
    TextField yPrime, initX, initY, step, stop;
    Font prFont, ttFont;
    Image logo; ImageView logoIV;
    Color lC;
    Button generate, help;
    LineChart lineChart;
    TableView<Point> tView;
    ArrayList<XYChart.Data<Double, Double>> allData;
    Stage stage;
    boolean tableDrawn = false;
    boolean lineChartDrawn = false;
    public static void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage prStage) {
        this.stage=prStage;
        stage.setHeight(720); stage.setWidth(1360);
        stage.setResizable(false);
        layout = new GridPane(); layout.setAlignment(Pos.CENTER);

        init();
        add();
        Scene scene = new Scene(layout);

        stage.setTitle("Euler's Method Graphing Calculator");
        stage.setScene(scene);
        stage.show();
    }

    public void init(){
        int h = 77;
        prFont = new Font("Arial", 16); ttFont = new Font("Cambria Math", 16);

        logo = new Image(MainApplication.class.getResourceAsStream("logo.png"));

        logoIV = new ImageView(); logoIV.setFitHeight(h); logoIV.setFitWidth(h*2); logoIV.setImage(logo);

        layout = new GridPane(); layout.setHgap(15); layout.setVgap(15);
        layout.setStyle("-fx-background-color: rgb(179, 224, 225);");
//        layout.setStyle("-fx-grid-lines-visible: true;");

        yPrime = new TextField(); initX = new TextField(); initY = new TextField(); step = new TextField(); stop = new TextField();

        yPrimeL = new Label("dy/dx = "); initXL = new Label("Initial X Value: "); initYL = new Label("Initial Y Value: "); stepL = new Label("Step Value: ");
        stopL = new Label ("Stop Value: ");

        yPrimeL.setFont(prFont); initXL.setFont(prFont); initYL.setFont(prFont); stepL.setFont(prFont); stopL.setFont(prFont);

        lC = Color.rgb(79,141,158);
        yPrimeL.setTextFill(lC); initXL.setTextFill(lC); initYL.setTextFill(lC); stepL.setTextFill(lC); stopL.setTextFill(lC);


        generate = new Button("Generate");
        generate.setFont(prFont);
        generate.setTextFill(lC);
        generate.setOnAction(this);
        help = new Button("?");
        help.setFont(prFont);
        help.setTextFill(lC);
        help.setOnAction(this);
    }
    public void add(){
        int rowI=5; int jump = 2;
        layout.add(logoIV, 1, 1);

        layout.setHalignment(logoIV, HPos.CENTER);

        layout.setHalignment(yPrimeL, HPos.RIGHT);
        layout.setHalignment(initXL, HPos.RIGHT);
        layout.setHalignment(initYL, HPos.RIGHT);
        layout.setHalignment(stepL, HPos.RIGHT);
        layout.setHalignment(stopL, HPos.RIGHT);

        layout.add(yPrimeL, 1, rowI); layout.add(yPrime, 2, rowI);
        layout.add(initXL, 1, rowI+jump); layout.add(initX, 2, rowI+jump);
        layout.add(initYL, 1, rowI+jump*2); layout.add(initY, 2, rowI+jump*2);
        layout.add(stepL, 1, rowI+jump*3); layout.add(step, 2, rowI+jump*3);
        layout.add(stopL, 1, rowI+jump*4); layout.add(stop, 2, rowI+jump*4);

        layout.setColumnSpan(generate,4);
        layout.setHalignment(generate, HPos.CENTER);
        layout.add(generate, 1, rowI + jump*6);

        layout.setColumnSpan(help,1);
        layout.setHalignment(help, HPos.CENTER);
        layout.add(help, 2, rowI + jump*6 );
    }
    public void errorTrap(){
        boolean valid = true, expressionValid = true;
        String expression = null, untouchedExpression = null;
        double sXV = 0, sYV = 0, stepV = 0, stopV = 0;
        //YPrime
        try{
            expression = yPrime.getText();
            untouchedExpression = expression;
            for(int i=0;i< expression.length();i++){
                if(i!=expression.length()-1 && expression.charAt(i) == 'p' && expression.charAt(i+1) == 'i')
                    expression = expression.substring(0,i) + "3.14159265358" + expression.substring(i+2,expression.length());
                if(expression.charAt(i)=='e')
                    expression = expression.substring(0,i) + "2.71828" + expression.substring(i+1);
                if(expression.charAt(i) == 'X' || expression.charAt(i) == 'x')
                    expression = expression.substring(0,i) + "3.12" + expression.substring(i+1);
                if(expression.charAt(i) == 'Y' || expression.charAt(i) == 'y')
                    expression = expression.substring(0,i) + "3.12" + expression.substring(i+1);
            }
            net.sourceforge.jeval.Evaluator engine = new Evaluator();
            engine.evaluate(expression);
        }catch (Exception e) {
            expressionValid = false;
            yPrime.clear();
        }

        try{sXV = Double.parseDouble(initX.getText());}
        catch (Exception e) { valid = false; initX.clear();}

        try{ sYV = Double.parseDouble(initY.getText());}
        catch (Exception e) { valid = false; initY.clear();}

        try{stepV = Double.parseDouble(step.getText());}
        catch (Exception e) {valid = false; step.clear();}

        try{
            stopV = Double.parseDouble(stop.getText());
            if (sXV >= stopV)
                throw new Exception();

        }catch (Exception e) { valid = false; stop.clear();}

        if(valid && expressionValid) {
            try{
                DataGenerator.generateTable(untouchedExpression, sXV, sYV, stepV, stopV);

            }catch (Exception e){}
                drawLineChart();
                drawTable();
        }
        else{
            if(!valid && !expressionValid)
                JOptionPane.showMessageDialog(null,
                        "Please make sure all input are valid floating point numbers and the expression can be evaluated.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            else if(!valid)
                JOptionPane.showMessageDialog(null,
                        "Please make sure all input are valid floating point numbers and the initial condition is before the stop value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            else
                JOptionPane.showMessageDialog(null,
                        "Please make sure the expression can be evaluated.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            if(tableDrawn)
                layout.getChildren().remove(tView);
            if(lineChartDrawn)
                layout.getChildren().remove(lineChart);
        }
    }
    public void drawLineChart(){
        if(lineChartDrawn)
            layout.getChildren().remove(lineChart);
        allData = new ArrayList<>();
        for(Point p: DataGenerator.points)
            allData.add(new XYChart.Data(p.x,p.y));
        DataGenerator.points.clear();
        XYChart.Series<Double,Double> series = new XYChart.Series<>();

        for(XYChart.Data x: allData)
            series.getData().add(x);
        series.setName("Euler's Approximation of " + yPrime.getText());

        lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        lineChart.getData().add(series);
        ToolTipDefaultsFixer.setTooltipTimers(0,5000,200);
        for(XYChart.Data x: allData){
            x.getNode().setStyle("-fx-background-color: #4f8d9e;");
            Tooltip t = new Tooltip("("+x.getXValue().toString()+", " + x.getYValue().toString() + ")");
            t.setFont(ttFont);
            Tooltip.install(x.getNode(), t);

        }
        lineChart.setTitle("Euler's Approximation");
        lineChart.setCursor(Cursor.CROSSHAIR);
        lineChartDrawn = true;
        lineChart.setMinWidth(600); lineChart.setMaxWidth(800);
        lineChart.setMinHeight(450); lineChart.setMaxHeight(550);
        series.getNode().setStyle("-fx-stroke: #4f8d9e;");
        series.nodeProperty().get().setStyle("-fx-stroke: #4f8d9e; ");
        Legend legend = (Legend)lineChart.lookup(".chart-legend");
        legend.getItems().get(0).getSymbol().setStyle("-fx-background-color: #4f8d9e;");
        addLineChart();
    }
    public void drawTable(){
        int minWidth = 150;

        if(tableDrawn)
            layout.getChildren().remove(tView);
        tView = new TableView<Point>();
        ObservableList<Point> tableData = FXCollections.observableArrayList();
        for(XYChart.Data<Double, Double> x: allData) {
            Point p = new Point(x.getXValue(), x.getYValue());
            tableData.add(p);
        }
        TableColumn xCol = new TableColumn("X"), yCol = new TableColumn("Y");
        xCol.setMinWidth(minWidth); yCol.setMinWidth(minWidth);
        xCol.setResizable(false); yCol.setResizable(false);

        xCol.setCellValueFactory(new PropertyValueFactory<>("x"));
        yCol.setCellValueFactory(new PropertyValueFactory<>("y"));
        System.out.println(tableData);
        tView.setItems(tableData);
        tView.setFixedCellSize(25);
        tView.getColumns().setAll(xCol,yCol);
        tView.setEditable(false);
        tView.setMaxWidth(350);
        tView.setMaxHeight(500);
        tableDrawn = true;
        addTable();
    }
    public void addLineChart(){
        GridPane.setRowSpan(lineChart, 15);
        GridPane.setValignment(lineChart, VPos.BASELINE);
        layout.add(lineChart,5,5);
    }
    public void addTable(){
        GridPane.setRowSpan(tView, 15);

        layout.add(tView, 7, 5);
    }

    public void legend(){
        int width=930, height = 350;
        Stage lStage = new Stage();
        Scene lScene = new Scene(new Group());

        lStage.setTitle("Help");
        lStage.setAlwaysOnTop(true);
        lStage.setResizable(false); lStage.requestFocus(); lStage.setWidth(width); lStage.setHeight(height);
        lStage.setX(stage.getX()+stage.getWidth()/2-width/2);
        lStage.setY(stage.getY()+stage.getHeight()/2-height/2);

        VBox root = new VBox();
        // edit
        root.setStyle("-fx-background-color: rgb(179, 224, 225);");
        ImageView legendIV = new ImageView();
        Image legend = new Image(MainApplication.class.getResourceAsStream("legend.png"));

        legendIV.setImage(legend);

        root.getChildren().addAll(legendIV);

        lScene.setRoot(root);
        lStage.setScene(lScene);
        lStage.show();
    }


    @Override
    public void handle(Event event) {
        if(event.getSource()==generate)
            errorTrap();
        else
            legend();
    }

}
