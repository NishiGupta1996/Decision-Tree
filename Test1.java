import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import dataview.models.GlobalSchedule;
import dataview.models.JSONObject;
import dataview.models.JSONParser;
import dataview.models.Workflow;
import dataview.planners.WorkflowPlanner;
import dataview.planners.WorkflowPlanner_T_Cluster;
import dataview.workflowexecutors.WorkflowExecutor;
import dataview.workflowexecutors.WorkflowExecutor_Beta;

public class Test1 {
	
	public static void main(String[] args) {
		
		// Load the configuration file
		String configfileLocation = System.getProperty("user.dir") + File.separator + "WebContent" +File.separator + "config.json";
		long size = new File(configfileLocation).length()/(1024*1024);
		
		System.out.println(size);
		String content = null;
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(configfileLocation)))
	    {
	 
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null)
	        {
	        	contentBuilder.append(sCurrentLine).append("\n");
	        }
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
		content = contentBuilder.toString();
		JSONParser jsonParser = new JSONParser(content);
		JSONObject obj = jsonParser.parseJSONObject();
		
		
		
		// step 1: create a workflow
		WorkflowVisualization frame = new WorkflowVisualization();	
		Diagnosis w = new Diagnosis();
		
		// step 2: Design a workflow
		w.design();
		frame.drawWorkflowGraph(w);
		
		/*
		String workflowplanner = obj.get("WorkflowPlanner").toString();
		String workflowexecutor = obj.get("WorkflowExecutor").toString();
		
		System.out.println(workflowplanner);
		
		
		WorkflowPlanner wp = null;
		try {
			Class<?> clazz =  Class.forName("dataview.planners.WorkflowPlanner");
			
			Constructor<?> constructor=  clazz.getDeclaredConstructor(dataview.models.Workflow.class);
			wp = (WorkflowPlanner) constructor.newInstance(w);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		GlobalSchedule gsch = wp.plan();
		
//		System.out.println()
		
		System.out.println(gsch.getSpecification());
		*/

		// step 3: choose a workflow planner
		WorkflowPlanner wp = new WorkflowPlanner_T_Cluster(w);
		GlobalSchedule gsch = wp.plan();
		try {
		// step 4: choose a workflow executor	
			WorkflowExecutor we = new WorkflowExecutor_Beta(obj, gsch);
			we.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
