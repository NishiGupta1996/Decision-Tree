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

public class TestDecisionTree extends Task {

	private static int numOfRows;
	private static int numOfColumns;
	private static double continuousRule;
	private static int[] categoricalRule;

	public TestDecisionTree() {
		super("TrainDecisionTree", "Train a decision tree and output a tree structure");
		ins = new InputPort[2];
		// ins = new InputPort[1];
		outs = new OutputPort[1];
		ins[0] = new InputPort("in0", Port.DATAVIEW_BigFile, "This is the input testing criteria");
		ins[1] = new InputPort("in0", Port.DATAVIEW_BigFile, "This is the input matrix");
		outs[0] = new OutputPort("out0", Port.DATAVIEW_HashMap, "This will output the testing result");
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitTestDatasetsByContinuousColumn(int splitCol, double splitc,
			DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		double columnMean = splitc;
		continuousRule = columnMean;
		ArrayList<DATAVIEW_MathVector> tempdata1 = new ArrayList<DATAVIEW_MathVector>();
		ArrayList<DATAVIEW_MathVector> tempdata2 = new ArrayList<DATAVIEW_MathVector>();

		int data1RowDim = 0;
		int data2RowDim = 0;
		int dataColDim = data.getRow(0).length();
		for (int i = 1; i < data.getNumOfRows(); i++) {
			if (data.get(i, splitCol) < columnMean) {
				tempdata1.add(data.getRow(i));
				data1RowDim++;
			} else {
				tempdata2.add(data.getRow(i));
				data2RowDim++;
			}
		}
		DATAVIEW_MathMatrix data1 = new DATAVIEW_MathMatrix(data1RowDim, dataColDim);
		DATAVIEW_MathMatrix data2 = new DATAVIEW_MathMatrix(data2RowDim, dataColDim);
		int rowId = 0;
		for (DATAVIEW_MathVector v : tempdata1) {
			data1.addRow(rowId, v);
			rowId++;
		}
		rowId = 0;
		for (DATAVIEW_MathVector v : tempdata2) {
			data2.addRow(rowId, v);
			rowId++;
		}
		splitdatasets.add(data1);
		splitdatasets.add(data2);
		return splitdatasets;
	}

	public static ArrayList<DATAVIEW_MathMatrix> splitTestDatasetsByCatigoricalColumn(int splitCol, int[] splitc,
			DATAVIEW_MathMatrix data) {
		ArrayList<DATAVIEW_MathMatrix> splitdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		int[] uniqueV = splitc;
		categoricalRule = uniqueV;
		int dataColDim = data.getRow(0).length();

		for (int value : uniqueV) {
			ArrayList<DATAVIEW_MathVector> tempdata1 = new ArrayList<DATAVIEW_MathVector>();
			int data1RowDim = 0;
			for (int i = 1; i < data.getNumOfRows(); i++) {
				if (data.get(i, splitCol) == value) {
					tempdata1.add(data.getRow(i));
					data1RowDim++;
				}
			}
			DATAVIEW_MathMatrix data1 = new DATAVIEW_MathMatrix(data1RowDim, dataColDim);
			int rowId = 0;
			for (DATAVIEW_MathVector v : tempdata1) {
				data1.addRow(rowId, v);
				rowId++;
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
			// System.out.println(((double) tClass / rows) * Math.log((double) tClass /
			// rows));
			if (tClass != 0 && fClass != 0) {
				subentropy.add(-((double) tClass / rows) * Math.log((double) tClass / rows)
						- ((double) fClass / rows) * Math.log((double) fClass / rows));
				subsize.add(rows);
			} else {
				subentropy.add(1.0);
				subsize.add(rows);
			}

		}

		for (Integer e : subsize) {
			totalrows += e.intValue();
		}

		for (int i = 0; i < subentropy.size(); i++) {
			entropy += ((double) subsize.get(i) / totalrows) * subentropy.get(i);
		}

		return entropy;
	}

	public static double calculateAccuracy(ArrayList<DATAVIEW_MathMatrix> splitdatasets) {
		double accuracy = 0;
		int totalrows = 0;
		int trueClassified = 0;

		for (DATAVIEW_MathMatrix data : splitdatasets) {
			// get the most vote of each dataset
			int tClass = 0;
			int fClass = 0;
			int rows = data.getNumOfRows();
			totalrows += rows;
			for (int i = 0; i < rows; i++) {
				if (data.get(i, numOfColumns - 1) == 1) {
					tClass++;
				} else {
					fClass++;
				}
			}
			if (tClass > fClass) {
				trueClassified += tClass;
			} else {
				trueClassified += fClass;
			}
			// System.out.println(((double) tClass / rows) * Math.log((double) tClass /
			// rows));

		}

		accuracy = (double) trueClassified / totalrows;

		return accuracy;
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

	public DATAVIEW_MathMatrix removeColumn(int k, DATAVIEW_MathMatrix data) {
		int rowDim = data.getNumOfRows();
		int colDim = data.getNumOfColumns();

		double[][] newElements = new double[rowDim][colDim - 1];
		for (int i = 0; i < data.getNumOfRows(); i++) {
			for (int j = 0; j < data.getNumOfColumns(); j++) {
				if (k == j) {
					continue;
				} else {
					if (k < j) {
						newElements[i][j - 1] = data.get(i, j);
					} else {
						newElements[i][j] = data.get(i, j);
					}
				}

			}
		}
		DATAVIEW_MathMatrix newMatrix = new DATAVIEW_MathMatrix(newElements);

		return newMatrix;
	}

	public int[] stringToIntArray(String s) {
		String[] items = s.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

		int[] results = new int[items.length];

		for (int i = 0; i < items.length; i++) {
			try {
				results[i] = Integer.parseInt(items[i]);
			} catch (NumberFormatException nfe) {
				// NOTE: write something here if you need to recover from formatting errors
				System.out.println("failed to convert string to int array.");
			}
			;
		}

		return results;
	}

	public void run() {

		// read the input
		// read from the training split criteria file
		BufferedReader br0 = null;
		ArrayList<HashMap<String, String>> rulesHash = new ArrayList<HashMap<String, String>>();
		try {
			br0 = new BufferedReader(new FileReader((String) ins[0].getFileName()));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Dataview.debugger.logException(e1);
		}

		String line = null;
		int ruleNum = 0;
		try {
			while ((line = br0.readLine()) != null) {
				System.out.println("line:" + line);
				String[] rowVal = line.toString().split(",");
				if (rowVal.length == 3) {
					HashMap<String, String> newHash = new HashMap<String, String>();
					for (String e : rowVal) {
						String[] eVal = e.split(":");
						newHash.put((String) eVal[0], (String) eVal[1]);
					}
					rulesHash.add(newHash);
					ruleNum++;
				}

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // end while

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader((String) ins[1].getFileName()));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Dataview.debugger.logException(e1);
		}

		// read testing matrix from the testing matrix file
		line = null;
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
		int level = 0;
		boolean isEnd = false;
		int critieriaId = 0;
		double finalEntropy = 0;
		// the first row of dataset save the column type: continuous or categorical
		int[] uniqueVal;
		ArrayList<DATAVIEW_MathMatrix> currentdatasets = new ArrayList<DATAVIEW_MathMatrix>();
		currentdatasets.add(inputMatrix);
		HashMap<DATAVIEW_MathMatrix, Double> datasetEntropy = new HashMap<DATAVIEW_MathMatrix, Double>();

		// recursively partition dataset, save dataset in each level in a ArrayList
		double originEntropy = calculateEntropy(currentdatasets);

		while (!isEnd) {
			// for each dataset in current level
			level++;
			isEnd = true;
			System.out.println("current level is: level " + level);
			ArrayList<DATAVIEW_MathMatrix> newdatasets = new ArrayList<DATAVIEW_MathMatrix>();

			for (DATAVIEW_MathMatrix data : currentdatasets) {
				double bestInfoGain = 0;
				boolean stopSplit = false;
				DATAVIEW_MathVector columnVector;
				double currentEntropy;
				double entropy;
				ArrayList<DATAVIEW_MathMatrix> tempdata = new ArrayList<DATAVIEW_MathMatrix>();
				tempdata.add(data);
				currentEntropy = calculateEntropy(tempdata);

				if (rulesHash.get(critieriaId).get("splitCriteria") != null) {
					// partition the dataset base on the type of critieria
					isEnd = true;
					Boolean iscontinuous = Boolean.parseBoolean(rulesHash.get(critieriaId).get("iscontinuous")) == true
							? true
							: false;
					String splitCriteria = rulesHash.get(critieriaId).get("splitCriteria");
					int splitCol = Integer.parseInt(rulesHash.get(critieriaId).get("splitCol"));
					if (iscontinuous) {
						// split the dataset by split criteria into two dataset
						double splitc = Double.parseDouble(splitCriteria);
						ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitTestDatasetsByContinuousColumn(splitCol,
								splitc, data);
						entropy = calculateEntropy(splitdatasets);
						newdatasets.addAll(splitdatasets);
					} else {
						// split the dataset by split criteria into tow/multiple datasets
						int[] splitc = stringToIntArray(splitCriteria);
						ArrayList<DATAVIEW_MathMatrix> splitdatasets = splitTestDatasetsByCatigoricalColumn(splitCol,
								splitc, data);
						entropy = calculateEntropy(splitdatasets);
						newdatasets.addAll(splitdatasets);
					}

					// remove the column to avoid it is re-selected in future split
					data = removeColumn(splitCol, data);
					finalEntropy = entropy;

				} else {
					critieriaId++;
					continue;
				}

				currentdatasets = newdatasets;

			}
		}

		double calcAccuracy = calculateAccuracy(currentdatasets);

		// save the final result
		HashMap<String, String> finalResult = new HashMap<String, String>();
		finalResult.put("finalDepth", Integer.toString(level));
		finalResult.put("entropyDrop", Double.toString((double) (originEntropy - finalEntropy)));
		finalResult.put("accuracy", Double.toString(calcAccuracy));

		// step 3: write to the output
		File outputfile = new File((String) outs[0].getFileName());
		try {
			if (!outputfile.exists())
				outputfile.createNewFile();

			FileWriter fw = new FileWriter(outputfile.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("Testing tree depth:" + finalResult.get("finalDepth") + ", total entropy drop: "
					+ finalResult.get("entropyDrop") + ", testing accuracy: " + finalResult.get("accuracy"));
			bw.newLine();

			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Dataview.debugger.logException(e);
		}

	}
}
