package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Matrix {
    private final int mRows;
    private final int mCols;
    private final double[][] mMatrix;

    Matrix(int rows, int cols, double initial_value){
        this.mRows = rows;
        this.mCols = cols;
        this.mMatrix = new double[this.mRows][this.mCols];
        fillMatrix(initial_value);
    }

    Matrix(int rows, double initial_value){
        this.mRows = rows;
        this.mCols = 1;
        this.mMatrix = new double[this.mRows][this.mCols];
        fillMatrix(initial_value);
    }

    Matrix(String filename){
        int[] dimensions = setSizeFromFile(filename); // [0] is rows [1] is columns
        this.mRows = dimensions[0];
        this.mCols = dimensions[1];
        this.mMatrix = new double[this.mRows][this.mCols];
        this.fillMatrix(filename);
    }

    Matrix(double[][] array){
        this.mRows = array.length;
        this.mCols = array[0].length;
        this.mMatrix = array;
    }

    public double get(int row, int col){
        return this.mMatrix[row][col];
    }

    public int getRows() {
        return this.mRows;
    }

    public int getCols(){
        return this.mCols;
    }

    public void set(int row, int col, double value){
        this.mMatrix[row][col] = value;
    }
    private void changeRow(int row, double[] rowArr){
        this.mMatrix[row] = rowArr;
    }
    public Matrix add(Matrix matrix2){
        Matrix result = new Matrix(this.mRows, this.mCols, 0);
        for(int i = 0; i < this.mRows; i++){
            for(int j = 0; j < this.mCols; j++){
                double value = this.mMatrix[i][j] * matrix2.get(i, j);
                result.set(i, j, value);
            }
        }
        return result;
    }
    public double sum(){
        double result = 0;
        for(double[] row : this.mMatrix){
            for(double val : row){
                result += val;
            }
        }
        return result;
    }

    public Matrix multiple(Matrix m2){
        Matrix result = new Matrix(this.mRows, this.mCols, 0);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        if(this.mRows == m2.getCols() && this.mCols == m2.getRows()){
            for(int i = 0; i < this.mRows; i++){
                final int row = i;
                executorService.submit(() ->
                        result.changeRow(row, getRowInMultiplication(row, this, m2))
                        );
            }
        }
        else{
            System.out.println("Can't multiple");
        }
        executorService.shutdown();
        return result;
    }

    private static double[] getRowInMultiplication(int rowNum, Matrix m1, Matrix m2){
        int length = m1.getCols();
        double[] rowResult = new double[length];
        for(int i = 0; i < length; i++){
            double result = 0;
            for(int j = 0; j < length; j++){
                result += m1.get(rowNum, j) * m2.get(j, i);
            }
            rowResult[i] = result;
        }
        return rowResult;
    }
    private int[] setSizeFromFile(String filename){
        int[] result = new int[2];
        try {
            File file = new File(filename);
            int rows = 0;
            int columns = 0;
            Scanner rowReader = new Scanner(file);
            while(rowReader.hasNextLine()){
                String row = rowReader.nextLine();
                rows++;
            }
            result[0] = rows;
            rowReader = new Scanner(file);
            if(rowReader.hasNextLine()){
                String row = rowReader.nextLine();
                Scanner columnReader = new Scanner(row);
                while(columnReader.hasNextDouble()){
                    double val = columnReader.nextDouble();
                    columns++;
                }
            }
            result[1] = columns;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void fillMatrix(String filename){
        try{
            File file = new File(filename);
            Scanner fileReader = new Scanner(file);
            int curr_row = 0;
            while(fileReader.hasNextLine()){
                String row = fileReader.nextLine();
                Scanner rowReader = new Scanner(row);
                int curr_col = 0;
                while(rowReader.hasNextDouble()){
                    this.mMatrix[curr_row][curr_col] = rowReader.nextDouble();
                    curr_col ++;
                }
                curr_row ++;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    public void print(){
        String output = "";
        for(double[] row : this.mMatrix){
            for(double val : row){
                output += (val + "  ");
            }
            output += "\n";
        }
        System.out.println(output);
    }

    public void fillMatrix(double value){
        for(double[] row : this.mMatrix){
            for(int i = 0; i < this.mCols; i++){
                row[i] = value;
            }
        }
    }
}
