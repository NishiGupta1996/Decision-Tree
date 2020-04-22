import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dataview.models.DATAVIEW_MathMatrix;
import dataview.models.DATAVIEW_MathVector;
import dataview.models.Dataview;
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
		// ins[0] = new InputPort("in0", Port.DATAVIEW_MathMatrix, "This contains the
		// training dataset to train a decision tree model");
		// ins[1] = new InputPort("in1", Port.DATAVIEW_MathVector, "This contains
		// dimension of input matrix");
		ins[0] = new InputPort("in0", Port.DATAVIEW_BigFile, "This is the input matrix");
		outs[0] = new OutputPort("out0", Port.DATAVIEW_HashMap, "This will output the tree structure");
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitDatasetsByContinuousColumn(Integer colID,
			DATAVIEW_MathVector columnVector, DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		double columnMean = mean(columnVector.getAll());
		continuousRule = columnMean;
		ArrayList<DATAVIEW_MathVector> tempdata1 = new ArrayList<DATAVIEW_MathVector>();
		ArrayList<DATAVIEW_MathVector> tempdata2 = new ArrayList<DATAVIEW_MathVector>();

		for (int i = 1; i < data.getNumOfRows(); i++) {
			if (data.get(i, colID) < columnMean) {
				tempdata1.add(data.getRow(i));
			} else {
				tempdata2.add(data.getRow(i));
			}
		}
		DATAVIEW_MathMatrix data1;
		DATAVIEW_MathMatrix data2;
		for
		splitdatasets.add(data1);
		splitdatasets.add(data2);
		return splitdatasets;
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitDatasetsByCatigoricalColumn(Integer colID,
			DATAVIEW_MathVector columnVector, DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		double[] getAll = columnVector.getAll();
		int[] getAllInt = new int[getAll.length];
		for (int i = 0; i < getAll.length; i++) {
			getAllInt[i] = (int) getAll[i];
		}
		int[] uniqueV = checkUniqueV(getAllInt);
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
				if (data.get(i, numOfColumns - 1) == 1) {
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

		// read matrix from input port 1

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
				int bestSplit = 0;
				String splitCriteria;
				String bestColSplitCriteria = null;
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
					for (int i = 0; i < numOfColumns - 1; i++) {
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

						datasetEntropy.put(data, entropy);

					}
					columnVector = inputMatrix.getColumn(bestSplit);
					ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitDatasetsByCatigoricalColumn(bestSplit,
							columnVector, data);
					newdatasets.addAll(splitdatasets);

					// remove the column to avoid it is re-selected in future split
					data.removeColumn(bestSplit);

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

		// step 3: write to the output
		File outputfile = new File((String) outs[0].getFileName());
		try {
			if (!outputfile.exists())
				outputfile.createNewFile();

			FileWriter fw = new FileWriter(outputfile.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);

			for (HashMap<String, String> rule : splitRules) {
				bw.write("splitCol:" + rule.get("splitCol") + ", splitCriteria" + rule.get("splitCriteria"));
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
