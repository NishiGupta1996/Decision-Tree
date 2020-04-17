import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import dataview.models.JSONArray;
import dataview.models.JSONObject;
import dataview.models.JSONParser;
import dataview.models.JSONValue;

public class Test2 {

	public static void main(String[] args) {
		String configfileLocation = "/Users/changxinbai/Downloads/DATAVIEW/DATAVIEW/WebContent/workflowLibDir/DiagnosisRecommendation.json";
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
		System.out.println(content);
		JSONParser jsonParser = new JSONParser(content);
		JSONObject workflowobj = jsonParser.parseJSONObject();
		JSONObject taskobj = workflowobj.get("Execution").toJSONObject();
		//JSONArray jarraytasks = taskobj.get("Partitioner").toJSONArray();
		System.out.println(taskobj.keySet());
		for(String str: taskobj.keySet()){
			JSONArray jarraytasks = taskobj.get(str).toJSONArray();
			for(int i = 0; i<jarraytasks.size(); i++){
				JSONObject obj = jarraytasks.get(i).toJSONObject();
				String vm = (String) obj.keySet().toArray()[0];
				Double execTime = Double.parseDouble(obj.get(vm).toString().replace("\"", ""));
				System.out.println("the task: " +str +  " is running on vm "+ vm + " :" + execTime);
			}
		}
		JSONObject jsonedge = workflowobj.get("Tasktransfer").toJSONObject();
		System.out.println();
		for(String str: jsonedge.keySet()){
			JSONArray jarrayedge  = jsonedge.get(str).toJSONArray();
			for(int i = 0; i<jarrayedge.size(); i++){
				JSONObject obj = jarrayedge.get(i).toJSONObject();
				String childTask = obj.get("To").toString().replace("\"", "");
				Double transferTime = Double.parseDouble(obj.get("Trans").toString().replace("\"", ""));
				System.out.println(str +"----->" + childTask + " :"+ transferTime);
			}
			
		}

	}

}
