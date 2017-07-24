import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.config.SubTaskManager

componentManager = ComponentManager.getInstance()
subTaskManager = componentManager.getSubTaskManager();
MutableIssue mutableIssue = issue

passesCondition = checkSubTasksStatus(mutableIssue) && checkPriority(mutableIssue)

def checkSubTasksStatus(MutableIssue mutableIssue){
	def subTasksResult = true
	if (subTaskManager.subTasksEnabled) {
		
		log.warn ("mutableIssue.statusObject.name: " + mutableIssue.statusObject.name)
		
		for (Issue subTask :  mutableIssue.getSubTaskObjects())
		{
			if (subTask.getStatusObject().name != "In Progress")
					subTasksResult = false
					
			log.warn("SUB TASK: " + subTask.getStatusObject().name)  
		}
   }
   else
   {
		subTasksResult = false
   }
   return subTasksResult
}

def checkPriority(MutableIssue mutableIssue)
{
	def priorityResult = true
	
	log.warn("Priority:" +  mutableIssue.getPriority().name)
	
	if (mutableIssue.getPriority().name == "Major")
	{
		priorityResult = false			
	}
	return priorityResult
}	
 