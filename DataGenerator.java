import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;

class DataGenerator{
    static ArrayList<Point> points = new ArrayList<Point>();

    public static void generateTable (String expression, double sX, double sY, double step, double stop) throws Exception{

        for(int i=0;i< expression.length();i++){
            if(i!=expression.length()-1 && expression.charAt(i) == 'p' && expression.charAt(i+1) == 'i')
                expression = expression.substring(0,i) + "3.14159265358" + expression.substring(i+2,expression.length());
            if(expression.charAt(i)=='e'){
                expression = expression.substring(0,i) + "2.71828" + expression.substring(i+1);
            }
        }

        System.out.println(expression);

        net.sourceforge.jeval.Evaluator engine = new Evaluator();

        double oldX, oldY, currX,currY;
        currX = sX; currY = sY;
        points.add(new Point(currX,currY));
        do{
            oldX = currX; oldY = currY;
            currX+=step;
            String unreplacedExpression = expression;
            for(int i=0;i< expression.length();i++){
                if(expression.charAt(i) == 'X' || expression.charAt(i) == 'x')
                    expression = expression.substring(0,i) + oldX + expression.substring(i+1);
                if(expression.charAt(i) == 'Y' || expression.charAt(i) == 'y')
                    expression = expression.substring(0,i) + oldY + expression.substring(i+1);
            }
            currY = oldY + step * (Double.parseDouble(engine.evaluate(expression)));
            currX = Math.round (currX * 1e5)/1e5;
            currY = Math.round (currY * 1e5)/1e5;
            points.add(new Point(currX,currY));

            expression = unreplacedExpression;
        }while(currX<stop);
    }
}