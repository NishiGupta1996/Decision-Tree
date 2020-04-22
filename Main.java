import java.io.File;

import dataview.models.Dataview;

public class Main {

	public static void main1(String[] args) {
		// TODO Auto-generated method stub
		String configurefileLocation = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator
				+ "workflowTaskDir" + File.separator;

		String[] input = { configurefileLocation + "DTTrainingSet.txt" };
		String[] output = { configurefileLocation + "DTTrainingOutput.txt" };

		Dataview.debugger.logTestATask("TrainDecisionTree", input, output);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String configurefileLocation = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator
				+ "workflowTaskDir" + File.separator;

		String[] input = { configurefileLocation + "DTTrainingOutput.txt", configurefileLocation + "DTTestingSet.txt" };
		String[] output = { configurefileLocation + "DTTestingOutput.txt" };

		Dataview.debugger.logTestATask("TestDecisionTree", input, output);

	}

}
