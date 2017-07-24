package com.deiser.jira.groovy.transitioningIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.history.ChangeLogUtils
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.issue.search.SearchRequestManager
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.web.bean.PagerFilter
import com.opensymphony.workflow.WorkflowContext
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.bc.issue.IssueService

//CONSTANTES PARA DEFINIR ANTES DE EJECUTAR EL SCRIPT
FILTER_ID =  10000L
TRANSITION_ID = 4

def componentManager = ComponentManager.getInstance()

//Authenticated User in JIRA
user = componentManager.getJiraAuthenticationContext().getUser()

//Getting the requested filter
SearchRequestManager searchRequestManager = componentManager.getComponent(SearchRequestManager.class)
SearchRequest filter = searchRequestManager.getSearchRequestById(user, FILTER_ID)
issueService = componentManager.getComponent(IssueService.class)

//Searching the issues
SearchProvider searchProvider = componentManager.getOSGiComponentInstanceOfType(SearchProvider.class)
SearchResults issues
trace = ""

try
{
    issues = searchProvider.search(filter.getQuery(), user, PagerFilter.getUnlimitedFilter())
    if (issues.total  == 0)
    {
    	log.debug("No existen issues para ese filtro")
        throw new RuntimeException();
    }
         
    processIssues(issues.getIssues())
    
    return trace
}
catch (Exception e) {
    throw new RuntimeException("No se encuentran issues para la búsqueda:" + filter.getQuery(), e)
}

def processIssues(List<Issue> issueCollection)
{
    //Process the obtained issues
    for(def issue : issueCollection) {
        def targetIssueKey = "";
        try
        {
            targetIssueKey = issue.getKey()
            transitIssue(user, issue, TRANSITION_ID)
        }
        catch (Exception e) {
	    log.error("Error procesando las issues " + e)
        }
    }
}

def transitIssue(def user, def issue, def transitionId) {
try
{
	def validationResult = issueService.validateTransition(user, issue.id, transitionId, issueService.newIssueInputParameters());
        if (validationResult.isValid()) {
		IssueResult transitionResult = issueService.transition(user, validationResult);
		if (!transitionResult.isValid()) {
                	trace += "\n Error ejecutando la transición " + transitionId + " en la incidencia [" + issue.key + "]"
			log.error("Error ejecutando la transición " + transitionId + " en la incidencia [" + issue.key + "]", transitionResult.errorCollection)
		}
                else
                {
               		trace += "\n Incidencia [" + issue.key + "] transitada con éxito"
                }
	} else {
		log.error("Transición incorrecta " + transitionId + " para la incidencia [" + issue.key + "] !!", validationResult.errorCollection)
	}	
        }
         catch (Exception e) {
            log.error("Error transitando la issue" + issue.getKey(), e)
        }
}


