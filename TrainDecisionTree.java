import java.io.*;
import java.util.Vector;
import dataview.models.*;

public class TrainDecisionTree extends Task{

	private int numOfRows;  
	private int numOfColumns;
	
	private static int iteration = 1;

	public TrainDecisionTree()
	{
		super("TrainDecisionTree", "Train a decision tree and output a tree structure");
		ins = new InputPort[2];
		outs = new OutputPort[1];
		ins[0] = new InputPort("in0", Port.DATAVIEW_MathMatrix, 
				"This contains the training dataset to train a decision tree model");
		ins[1] = new InputPort("in1", Port.DATAVIEW_MathVector, 
				"This contains dimension of input matrix");
		outs[0] = new OutputPort("out0", Port.DATAVIEW_HashMap, "This will output the tree structure");
	}

	public double calculateEntropy(Vector<Double> data) {

		int numdata = data.size();

		if (numdata == 0) return 0;

			int attribute = numOfColumns-1;

			double sum = 0;

			for (int i=0; i< numvalues; i++) {

				int count=0;

				for (int j=0; j< numdata; j++) {

					DataPoint point = (DataPoint)data.elementAt(j);

					if (point.attributes[attribute] == i) count++;

				}

				double probability = 1.*count/numdata;

				if (count > 0) sum += -probability*Math.log(probability);

			}

		return sum;

		}
	

	public void run() {

		// read the input
		
		DATAVIEW_MathVector inputVector = new DATAVIEW_MathVector(2);
		inputVector = (DATAVIEW_MathVector) ins[1].read();
		DATAVIEW_MathMatrix inputMatrix = new DATAVIEW_MathMatrix((int)inputVector.get(0), (int)inputVector.get(1));
		inputMatrix = (DATAVIEW_MathMatrix) ins[0].read();
		Boolean isEnd = false;
		
		//recursively partition dataset, save dataset in each level in a hashmap
		while(!isEnd)
		{
		//step 1: calulate the mean of each column or categories of categorical column
			
			
			
		//loop through the rows except for last column, calculate the entropy value
		Double bestInfoGain;
		int bestSplit;
		for(i=0; i<numOfColumns-2; i++)
		{
			Double infoGain = calculateEntropyGain(i);
			
		}
		
		//partition the decision tree into two/mutiple dataset, save with depth number in hashmap
		
		}
		
		//find a way to locate all leaf nodes, may set flag until any dataset no longer patitioning

		
		
	}	
}
