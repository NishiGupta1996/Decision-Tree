import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataview.models.DATAVIEW_MathMatrix;
//import dataview.models.DATAVIEW_MathVector;
import dataview.models.Dataview;
import dataview.models.InputPort;
import dataview.models.OutputPort;
import dataview.models.Port;
import dataview.models.Task;

public class DataSplitDecTree extends Task{
/*
	 * The constructor will decide how many inputports and how many outputports and the detailed information of each port.
 */
		
public DataSplitDecTree()
{
	super("DataSplitDecTree", "This is a task that implements partition the dataset. It has one inputports and two outputports.");
	ins = new InputPort[1];
	outs = new OutputPort[2];
	ins[0] = new InputPort("in0", Port.DATAVIEW_MathMatrix, "This is the first number");
	outs[0] = new OutputPort("out0", Port.DATAVIEW_BigFile, "This is the output");	
	outs[1] = new OutputPort("out1", Port.DATAVIEW_BigFile, "This is the output");
}
		
public void run()
{
			// step 1: read from the input ports
			DATAVIEW_MathMatrix inputMatrix = (DATAVIEW_MathMatrix) ins[0].read();
				
		 	//step 2: slip matrix into 2 :
			int nColums= inputMatrix.getNumOfColumns();
			int nTrainRows= ((inputMatrix.getNumOfRows()) *2/3) +2;
			int nTestRows= ((inputMatrix.getNumOfRows()) *1/3) ;
			
			double[][] trainElements=new double[nTrainRows][nColums];
			double[][] testElements= new double[nTestRows][nColums];
			int trainElementsRowIndex=0;
			int testElementsRowIndex=0;
			
			// to get 1st row in both 
			for(int j=0; j < inputMatrix.getNumOfColumns(); j++) {
				trainElements[trainElementsRowIndex][j]= inputMatrix.get(0, j);
				//testElements[testElementsRowIndex][j]= inputMatrix.get(0, j);
				
			}
			trainElementsRowIndex++;
			//testElementsRowIndex++;
			
			//loop through remaining
			for (int i = 1; i < inputMatrix.getNumOfRows(); i++) {
				if ( i <= 1 + ((inputMatrix.getNumOfRows()-1) * 2 / 3)) {
					for(int j=0; j < inputMatrix.getNumOfColumns(); j++) {
						trainElements[trainElementsRowIndex][j]= inputMatrix.get(i, j);
						
					}
					trainElementsRowIndex++;
				} else {
					for(int j=1; j < inputMatrix.getNumOfColumns(); j++) {
						testElements[testElementsRowIndex][j]= inputMatrix.get(i, j);
						
					}
					testElementsRowIndex++;
				}
			}
			
			DATAVIEW_MathMatrix Train = new DATAVIEW_MathMatrix(trainElements);
			DATAVIEW_MathMatrix Test = new DATAVIEW_MathMatrix(testElements);
			
			//outs[0].write(Train);
			File outputfile = new File((String) outs[0].getFileName());
			try {
				if (!outputfile.exists())
					outputfile.createNewFile();

				FileWriter fw = new FileWriter(outputfile.getAbsoluteFile(), false);
				BufferedWriter bw = new BufferedWriter(fw);

				for(int i=0; i< Train.getNumOfRows(); i++) {
					bw.write(Train.getRow(i).toString());
					bw.newLine();	
				}

				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Dataview.debugger.logException(e);
			}
			
			//outs[1].write(Test);
			File outputfile1 = new File((String) outs[1].getFileName());
			try {
				if (!outputfile1.exists())
					outputfile1.createNewFile();

				FileWriter fw = new FileWriter(outputfile1.getAbsoluteFile(), false);
				BufferedWriter bw = new BufferedWriter(fw);

				for(int i=0; i< Test.getNumOfRows(); i++) {
					bw.write(Test.getRow(i).toString());
					bw.newLine();	
				}

				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Dataview.debugger.logException(e);
			}
}
}
	


