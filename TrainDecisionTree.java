import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dataview.models.DATAVIEW_MathMatrix;
import dataview.models.DATAVIEW_MathVector;
import dataview.models.InputPort;
import dataview.models.OutputPort;
import dataview.models.Port;
import dataview.models.Task;

public class TrainDecisionTree extends Task {

	private static int numOfRows;
	private static int numOfColumns;
	private static double continuousRule;
	private static int[] categoricalRule;

	public TrainDecisionTree() {
		super("TrainDecisionTree", "Train a decision tree and output a tree structure");
		ins = new InputPort[1];
		// ins = new InputPort[2];
		outs = new OutputPort[1];
		ins[0] = new InputPort("in0", Port.DATAVIEW_MathMatrix,
				"This contains the training dataset to train a decision tree model");
		// ins[1] = new InputPort("in1", Port.DATAVIEW_MathVector, "This contains
		// dimension of input matrix");
		outs[0] = new OutputPort("out0", Port.DATAVIEW_HashMap, "This will output the tree structure");
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitDatasetsByContinuousColumn(Integer colID,
			DATAVIEW_MathVector columnVector, DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		double columnMean = mean(columnVector.getAll());
		continuousRule = columnMean;
		DATAVIEW_MathMatrix data1 = new DATAVIEW_MathMatrix();
		DATAVIEW_MathMatrix data2 = new DATAVIEW_MathMatrix();

		for (int i = 1; i < data.getNumOfRows(); i++) {
			if (data.get(i, colID) < columnMean) {
				data1.addNewRow(data.getRow(i));
			} else {
				data2.addNewRow(data.getRow(i));
			}
		}
		splitdatasets.add(data1);
		splitdatasets.add(data2);
		return splitdatasets;
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitDatasetsByCatigoricalColumn(Integer colID,
			DATAVIEW_MathVector columnVector, DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		int[] uniqueV = checkUniqueV(columnVector.getElements());
		categoricalRule = uniqueV;

		for (int value : uniqueV) {
			DATAVIEW_MathMatrix data1 = new DATAVIEW_MathMatrix();
			for (int i = 1; i < data.getNumOfRows(); i++) {
				if (data.get(i, colID) == value) {
					data1.addNewRow(data.getRow(i));
				}
			}
			splitdatasets.add(data1);
		}

		return splitdatasets;
	}

	public static double calculateEntropy(ArrayList<DATAVIEW_MathMatrix> splitdatasets) {
		double entropy = 0;
		int totalrows = 0;
		List<Double> subentropy = new ArrayList<Double>();
		List<Integer> subsize = new ArrayList<Integer>();

		for (DATAVIEW_MathMatrix data : splitdatasets) {
			int tClass = 0;
			int fClass = 0;
			int rows = data.getNumOfRows();
			for (int i = 0; i < rows; i++) {
				if (data.get(i, numOfColumns) == 1) {
					tClass++;
				} else {
					fClass++;
				}
			}
			subentropy.add(-(tClass / rows) * Math.log(tClass / rows) - (fClass / rows) * Math.log(fClass / rows));
			subsize.add(rows);
		}

		for (Integer e : subsize) {
			totalrows += e.intValue();
		}

		for (int i = 0; i < subentropy.size(); i++) {
			entropy += (subsize.get(i) / totalrows) * subentropy.get(i);
		}

		return entropy;
	}

	public static double mean(double[] m) {
		double sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum / m.length;
	}

	public static int[] checkUniqueV(int[] m) {
		int[] unique;
		unique = Arrays.stream(m).distinct().toArray();

		return unique;
	}

	public void run() {

		// read the input
		// read dimension of matrix from input port 1
//		DATAVIEW_MathVector inputVector = new DATAVIEW_MathVector(2);
//		inputVector = (DATAVIEW_MathVector) ins[1].read();
//		numOfRows = (int) inputVector.get(0);
//		numOfColumns = (int) inputVector.get(1);
		// read matrix from input port 2
		DATAVIEW_MathMatrix inputMatrix = new DATAVIEW_MathMatrix();
		inputMatrix = (DATAVIEW_MathMatrix) ins[0].read();
		numOfRows = inputMatrix.getNumOfRows();
		numOfColumns = inputMatrix.getNumOfColumns();
		boolean isEnd = false;
		int level = 0;
		// the first row of dataset save the column type: continuous or categorical
		// DATAVIEW_MathVector columnsType = inputMatrix.getRow(0);
		int[] uniqueVal;
		ArrayList<DATAVIEW_MathMatrix> currentdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		ArrayList<DATAVIEW_MathMatrix> newdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		currentdatasets.add(inputMatrix);
		HashMap<DATAVIEW_MathMatrix, Double> datasetEntropy = new HashMap<DATAVIEW_MathMatrix, Double>();
		// save the split strategy
		ArrayList<HashMap<String, String>> splitRules = new ArrayList<HashMap<String, String>>();

		// recursively partition dataset, save dataset in each level in a ArrayList
		while (!isEnd) {
			// for each dataset in current level
			newdatasets.clear();

			for (DATAVIEW_MathMatrix data : currentdatasets) {
				double bestInfoGain = 0;
				isEnd = true;
				boolean stopSplit = false;
				int bestSplit;
				String splitCriteria;
				String bestColSplitCriteria;
				DATAVIEW_MathVector columnVector;
				double currentEntropy;
				ArrayList<DATAVIEW_MathMatrix> tempdata = new ArrayList<DATAVIEW_MathMatrix>();
				tempdata.add(data);
				currentEntropy = calculateEntropy(tempdata);
				// check if same dataset is in previous
				if (datasetEntropy.containsKey(data))
					stopSplit = true;

				if (currentEntropy != 1 && stopSplit == false) {

					// loop through all columns
					for (int i = 0; i < numOfColumns - 2; i++) {
						// todo: add getColumns in matrix class to get whole value in column as vector
						columnVector = inputMatrix.getColumn(i);
						// check if column i is continuous or categorical
						boolean iscontinuous = columnVector.get(0) == 0 ? true : false;
						double entropy;
						double infoGain;
						isEnd = false;

						if (iscontinuous == true) {
							ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitDatasetsByContinuousColumn(i,
									columnVector, data);
							splitCriteria = Double.toString(continuousRule);
							entropy = calculateEntropy(splitdatasets);

						} else {
							ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitDatasetsByCatigoricalColumn(i,
									columnVector, data);
							splitCriteria = Arrays.toString(categoricalRule);
							entropy = calculateEntropy(splitdatasets);

						}
						infoGain = currentEntropy - entropy;

						if (infoGain > bestInfoGain) {
							bestInfoGain = infoGain;
							bestSplit = i;
							bestColSplitCriteria = splitCriteria;
						}

						// remove the column to avoid it is re-selected in future split
						data.removeColumn(bestSplit);

						datasetEntropy.put(data, entropy);

					}
					columnVector = inputMatrix.getColumn(bestSplit);
					ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitDatasetsByCatigoricalColumn(bestSplit,
							columnVector, data);
					newdatasets.addAll(splitdatasets);

					// save the splitting rules
					HashMap<String, String> newrule = new HashMap<String, String>();
					newrule.put("splitCol", Integer.toString(bestSplit));
					newrule.put("splitCriteria", bestColSplitCriteria);
					splitRules.add(newrule);

				}

			}
			currentdatasets.clear();
			currentdatasets = newdatasets;

		}

	}
}
