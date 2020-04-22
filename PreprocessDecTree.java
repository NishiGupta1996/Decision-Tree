import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import dataview.models.*;

public class PreprocessDecTree extends Task {
	private static int numOfRows;
	private static int numOfColumns;
	public PreprocessDecTree() 
	{
		super("PreprocessDecTree", "Preprocess decision tree");
		ins = new InputPort[1]; 
		outs = new OutputPort[1]; 
		ins[0] = new InputPort("in0", Port.DATAVIEW_BigFile,  "this is the input file which has no cluster number."); 
		outs[0] = new OutputPort("out0", Port.DATAVIEW_MathMatrix, "this is the output file which has cluster numbers, starting from 0 at the beginning");
		
	}
	public void run() {
		
		//Step -1: Read the input data file		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((String) ins[0].getFileName()));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Dataview.debugger.logException(e1);
		}
		String line = null;
		int rowNum = 0;
		int colNum = 0;
		ArrayList<ArrayList<Double>> tempList = new ArrayList<ArrayList<Double>>();
		try {
			while ((line = br.readLine()) != null) {
				System.out.println("line:" + line);
				double[] rowVal = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
				ArrayList<Double> newList = new ArrayList<Double>();
				for (double e : rowVal) {
					newList.add(e);
				}
				tempList.add(newList);
				rowNum++;
				colNum = rowVal.length;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // end while

		// create dataview_mathmatrix from templist;
		DATAVIEW_MathMatrix inputMatrix = new DATAVIEW_MathMatrix(rowNum, colNum);
		double[][] tempArray = new double[rowNum][colNum];
		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < colNum; j++) {
				tempArray[i][j] = tempList.get(i).get(j);
			}
		}
		inputMatrix.setElements(tempArray);
	 
		numOfRows = inputMatrix.getNumOfRows();
		numOfColumns = inputMatrix.getNumOfColumns();
		
		// step-2 normalize each continus column
		DATAVIEW_MathVector columnVector;
			
		for (int i = 0; i < numOfColumns - 1; i++) {
			// todo: add getColumns in matrix class to get whole value in column as vector
			columnVector = inputMatrix.getColumn(i);
			// check if column i is continuous or categorical
			boolean iscontinuous = columnVector.get(0) == 0 ? true : false;
			if (iscontinuous == true) {
				//find min & max
					double max = 0;  // max value of column
					double min =50;   //min value
					for(int j=1; j< numOfRows; j++ ) {
				      	 if (inputMatrix.get(j,i)> max) {
								max = inputMatrix.get(j,i);	
							 }
				   
				      	 if (inputMatrix.get(j,i)< min) {
								min = inputMatrix.get(j,i);
						}
					}
					
			//step-3 normalization
			inputMatrix.subtractColumn(i, min);
			inputMatrix.divColumn(i, max-min);
		 }
			
			else {
				
				continue;
			}
		}
			    		 
	    // step-3 write output data : matrix obj
	 	// outs[0]= DATAVIEW_MathMatrix();	*/
	 	outs[0].write(inputMatrix);		 
	}
	}
