
import dataview.models.DATAVIEW_BigFile;
import dataview.models.Task;
import dataview.models.Workflow;

public class DecisionTreeWorkflow extends Workflow {

	public static int maxDepth = 5; // number of maximum depth in decistion tree

	public DecisionTreeWorkflow() {
		super("decision tree workflow", " train a decision tree model and test dataset on testing datasets");
		wins = new Object[4];
		wins[0] = new DATAVIEW_BigFile("InputDecTree_final.txt");
		wins[1] = new DATAVIEW_BigFile("DTTrainingSet.txt");
		wins[2] = new DATAVIEW_BigFile("DTTrainingOutput.txt");
		wins[3] = new DATAVIEW_BigFile("DTTestingSet.txt");
		wouts = new Object[4];
		wouts[0] = new DATAVIEW_BigFile("DTTrainingSet.txt");
		wouts[1] = new DATAVIEW_BigFile("DTTestingSet.txt");
		wouts[2] = new DATAVIEW_BigFile("DTTrainingOutput.txt");
		wouts[3] = new DATAVIEW_BigFile("DTTestingOutput.txt");
	}

	public void design() {

		Task T1 = addTask("PreprocessDecTree");
		Task T2 = addTask("DataSplitDecTree");
		Task T3 = addTask("TrainDecisionTree");
		Task T4 = addTask("TestDecisionTree");

		addEdge(0, T1, 0);
		addEdge(T1, T2);
		addEdge(T2, 0, 0);
		addEdge(T2, 1, 1);
		addEdge(1, T3, 0);
		addEdge(T3, 2);
		addEdge(2, T4, 0);
		addEdge(3, T4, 1);
		addEdge(T4, 3);

	}

}
