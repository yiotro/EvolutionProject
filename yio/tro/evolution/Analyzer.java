package yio.tro.evolution;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ivan on 13.04.2016.
 */
public class Analyzer {

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();

        analyzer.checkDistributionHypothesis();
    }


    public void checkDistributionHypothesis() {
        String listArray = "0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 4, 4, 5, 8, 10, 11, 15, 17, 19, 23, 27, 29, 31, 31, 30, 36, 31, 30, 32, 31, 26, 25, 23, 16, 12, 13, 8, 6, 5, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0";
        ArrayList<Integer> srcList = getListByArray(listArray);

        int medium = getMedium(getSingles(srcList));
        int deviation = getStandardDeviation(getSingles(srcList));
        YioGdxGame.say("medium: " + medium);
        YioGdxGame.say("deviation: " + deviation);

        ArrayList<Integer> normalDistribution = new ArrayList<>();
        for (int i = 0; i < srcList.size(); i++) {
            normalDistribution.add((int)(1000d * normalDistributionFunction(i, medium, deviation)));
        }

        double correlation = getCorrelation(srcList, normalDistribution);
        YioGdxGame.say("correlation: " + correlation);
    }


    public ArrayList<Integer> getSingles(ArrayList<Integer> srcList) {
        ArrayList<Integer> singles = new ArrayList<>();

        for (int i = 0; i < srcList.size(); i++) {
            for (int k = 0; k < srcList.get(i); k++) {
                singles.add(i);
            }
        }

        return singles;
    }


    public double normalDistributionFunction(double x, double medium, double deviation) {
        double f = x - medium;
        f *= f;
        f /= 2 * deviation * deviation;
        f = Math.exp(-f);
        f /= deviation * Math.sqrt(2 * Math.PI);
        return f;
    }


    public void correlationStuff() throws IOException {
//        YioGdxGame.say("Enter list 1:");
//        String listOneString = br.readLine();
//        YioGdxGame.say("Enter list 2:");
//        String listTwoString = br.readLine();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("data1.txt"), "Cp1251"));
        String listOneString = null, listTwoString = null;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            if (line.contains("population")) {
                line = reader.readLine();
                listOneString = line;
            }

            if (line.contains("temperature")) {
                line = reader.readLine();
                listTwoString = line;
            }

            if (listOneString != null && listTwoString != null) {
                analyzeCorrelation(listOneString, listTwoString);
                YioGdxGame.say(" ");
                listOneString = null;
                listTwoString = null;
            }
        }
    }


    private void analyzeCorrelation(String listOneString, String listTwoString) {
        ArrayList<Integer> list1 = getListFromString(listOneString);
        ArrayList<Integer> list2 = getListFromString(listTwoString);
        YioGdxGame.say("Correlation: " + YioGdxGame.roundUp(getCorrelation(list1, list2), 3));

        ArrayList<Integer> cutOffList1 = getCutOffList(list1, 100);
        ArrayList<Integer> cutOffList2 = getCutOffList(list2, 100);
        YioGdxGame.say("Cut off Correlation: " + YioGdxGame.roundUp(getCorrelation(cutOffList1, cutOffList2), 3));

        correlationFunction(cutOffList1, cutOffList2);
    }


    private void correlationFunction(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        int length = 50;
        ArrayList<Integer> cList2 = new ArrayList<>(list2.subList(0, list2.size() - length));
        double result[] = new double[length];
        for (int i = 0; i < length; i++) {
            ArrayList<Integer> cList1 = new ArrayList<>(list1.subList(i, i + list1.size() - length));
            result[i] = getCorrelation(cList1, cList2);
        }

        YioGdxGame.say("Correlation function: " + getStringByArray(result));
        YioGdxGame.say("Index of max: " + indexOfMax(result));
    }


    private String getStringByArray(double[] array) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            stringBuffer.append(YioGdxGame.roundUp(array[i], 2));
            if (i != array.length - 1)
                stringBuffer.append(", ");
        }
        return stringBuffer.toString();
    }


    private int indexOfMax(double[] list) {
        int maxIndex = 0;
        double maxElement = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i] > maxElement) {
                maxElement = list[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }


    private ArrayList<Integer> getCutOffList(ArrayList<Integer> src, int cutOff) {
        ArrayList<Integer> resultList = new ArrayList<>();
        for (int i = cutOff; i < src.size(); i++) {
            resultList.add(src.get(i));
        }
        return resultList;
    }


    private ArrayList<Integer> getListFromString(String src) {
        StringTokenizer tokenizer = new StringTokenizer(src, ", ");
        ArrayList<Integer> resultList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            resultList.add(Integer.valueOf(token));
        }
        return resultList;
    }


    public static double getCorrelation(ArrayList<Integer> A, ArrayList<Integer> B) {
        double cov = getCovariance(A, B);
        double devA = getStandardDeviation(A);
        double devB = getStandardDeviation(B);
        return cov / (devA * devB);
    }


    public static int getCovariance(ArrayList<Integer> A, ArrayList<Integer> B) {
        int medA = getMedium(A);
        int medB = getMedium(B);
        int sum = 0;
        for (int i = 0; i < A.size(); i++) {
            int devA = A.get(i) - medA;
            int devB = B.get(i) - medB;
            sum += devA * devB;
        }
        sum /= A.size();
        return sum;
    }


    public static int getStandardDeviation(ArrayList<Integer> list) {
        int medium = getMedium(list);
        double sum = 0;
        for (Integer integer : list) {
            int dev = (integer - medium) * (integer - medium);
            sum += dev;
        }
        sum /= list.size();
        return (int)Math.sqrt(sum);
    }


    public static ArrayList<Integer> getListByArray(String array) {
        StringTokenizer tokenizer = new StringTokenizer(array, ", ");
        ArrayList<Integer> result = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            result.add(Integer.valueOf(token));
        }
        return result;
    }


    public static Point getLinearApproximation(ArrayList<Integer> y) {
        Point result = new Point();
        ArrayList<Integer> x = new ArrayList<>();

        for (int i = 0; i < y.size(); i++) {
            x.add(i);
        }

        ArrayList<Integer> xy = new ArrayList<>();
        ArrayList<Integer> xx = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            xy.add(x.get(i) * y.get(i));
            xx.add(x.get(i) * x.get(i));
        }
        int sumXY = getSum(xy);
        int sumXX = getSum(xx);

        int sumX = getSum(x);
        int sumY = getSum(y);

        int n = y.size();

        int numerator = n * sumXY - sumX * sumY;
        int denominator = n * sumXX - sumX * sumX;

        result.x = (float) numerator / (float) denominator; // a
        result.y = (sumY - result.x * sumX) / n; // b
        return result;
    }


    private static int getSum(ArrayList<Integer> list) {
        int sum = 0;

        for (Integer integer : list) {
            sum += integer;
        }

        return sum;
    }


    public static int getMedium(ArrayList<Integer> list) {
        int medium = 0;
        for (Integer integer : list) {
            medium += integer;
        }
        medium /= list.size();
        return medium;
    }

}
