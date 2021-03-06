
import java.io.File;

import dataview.models.GlobalSchedule;
import dataview.planners.WorkflowPlanner;
import dataview.planners.WorkflowPlanner_Naive2;
import dataview.workflowexecutors.WorkflowExecutor;
import dataview.workflowexecutors.WorkflowExecutor_Beta;
import dataview.workflowexecutors.WorkflowExecutor_Local;

/**
 * six steps shows the whole process to creat a workflow, design a workflow,
 * generate a workflow schedule, and execute a workfow in EC2.
 */

public class Test {

	public static void main(String[] args) throws Exception {

		// step 1: create a workflow
		WorkflowVisualization frame = new WorkflowVisualization();
		// W1 w = new W1();
		// SampleWorkflow w = new SampleWorkflow();
		// di er ci
		// Montage_workflow w = new Montage_workflow();
		// MR w = new MR();
		// Dummy_Workflow w = new Dummy_Workflow();
		// DisKMeansWorkflow w = new DisKMeansWorkflow();
		DecisionTreeWorkflow w = new DecisionTreeWorkflow();
		// WordCount_workflow w = new WordCount_workflow();
		// di san ci
		// Diagnosis w = new Diagnosis();
		// DummyWorkflow w = new DummyWorkflow();
		// WordCount_workflow w = new WordCount_workflow();
		// Ligo_workflow w = new Ligo_workflow();
		// step 2: design a workflow
		w.design();
		frame.drawWorkflowGraph(w);

		// step 3: choose a workflow planner
		// int whichplanner = WorkflowPlanner.WorkflowPlanner_LPOD;
		int whichplanner = WorkflowPlanner.WorkflowPlanner_Naive1;

		String configurefileLocation = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator
				+ "workflowLibDir" + File.separator;
		/*
		 * WorkflowPlanner wp = null; switch (whichplanner) { case
		 * WorkflowPlanner.WorkflowPlanner_Naive1: wp = new WorkflowPlanner_Naive1(w);
		 * break; case WorkflowPlanner.WorkflowPlanner_Naive2: wp = new
		 * WorkflowPlanner_Naive2(w); break; case
		 * WorkflowPlanner.WorkflowPlanner_T_Cluster: wp = new
		 * WorkflowPlanner_T_Cluster(w); break; case
		 * WorkflowPlanner.WorkflowPlanner_ICPCP: wp = new
		 * WorkflowPlanner_ICPCP(w,configurefileLocation); break; case
		 * WorkflowPlanner.WorkflowPlanner_LPOD: wp = new WorkflowPlanner_LPOD(w);
		 * default: wp = new WorkflowPlanner_T_Cluster(w); break; }
		 * 
		 */
		// WorkflowPlanner wp = new WorkflowPlanner_LPOD(w);
		// step 4: generate a workflow schedule
		WorkflowPlanner wp = new WorkflowPlanner_Naive2(w);

		// WorkflowPlanner wp = new WorkflowPlanner_ICPCP(w,configurefileLocation);
		// WorkflowPlanner wp = new WorkflowPlanner_LPOD(w,configurefileLocation);
		// WorkflowPlanner wp = new WorkflowPlanner_E2C2X(w,configurefileLocation);

		// WorkflowPlanner wp = new WorkflowPlanner_ICPCP(w);
		// WorkflowPlanner wp = new WorkflowPlanner_E2C2X(w);
		// WorkflowPlanner wp = new WorkflowPlanner_Random(w);

		// WorkflowPlanner wp = new WorkflowPLanner_E2C2D(w);
		// WorkflowPlanner wp = new WorkflowPlanner_T_Cluster(w);
		// WorkflowPlanner wp = new WorkflowPlanner_Naive2(w);

		GlobalSchedule gsch = wp.plan();
		System.out.println(gsch.getSpecification());
		// step 5: select a workflow executor

		String fileLocation = System.getProperty("user.dir") + File.separator + "WebContent" + File.separator;

		// int whichexecutor = WorkflowExecutor.WorkflowExecutor_Beta;
		int whichexecutor = WorkflowExecutor.WorkflowExecutor_Local;

		WorkflowExecutor we = null;

		switch (whichexecutor) {
		case WorkflowExecutor.WorkflowExecutor_Local:
			we = new WorkflowExecutor_Local(fileLocation + "workflowTaskDir" + File.separator,
					fileLocation + "workflowLibDir" + File.separator, gsch);
			break;
		case WorkflowExecutor.WorkflowExecutor_Beta:
			we = new WorkflowExecutor_Beta(fileLocation + "workflowTaskDir" + File.separator,
					fileLocation + "workflowLibDir" + File.separator, gsch);
			break;
		default:
			we = new WorkflowExecutor_Beta(fileLocation + "workflowTaskDir" + File.separator,
					fileLocation + "workflowLibDir" + File.separator, gsch);
		}

		// step 6: execute a workflow
		we.execute();

		// step 7: VM machine termination.
		// VMProvisioner.vmTerminationAndKeyDelete(fileLocation+"workflowLibDir"+
		// File.separator);

	}

}
