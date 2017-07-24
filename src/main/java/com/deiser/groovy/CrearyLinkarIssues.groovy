package com.deiser.jira.groovy

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.atlassian.jira.issue.security.IssueSecurityLevel
import com.atlassian.jira.project.Project
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.security.JiraAuthenticationContext

componentManager = ComponentManager.getInstance()

//Constantes
sourceIssueId = "SCRIP-10"


sourceIssue = componentManager.getIssueManager().getIssueObject(sourceIssueId)
projectKey = "SCRIP"


//Usuario actual
user = componentManager.getJiraAuthenticationContext().getLoggedInUser()

Issue newIssue = createIssue()

def result = linkIssues(newIssue)

if (result)
return ("La issue " + newIssue + " se ha creado correctamente y se ha linkado con la issue de origen")
else
return ("Ha ocurrido un error durante la creación o el enlace de las issues")

def createIssue()
{
    Project project = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey)
    
    
	//Asignamos las propiedades de la Issue a crear
    MutableIssue newIssue = componentManager.getIssueFactory().getIssue()
    newIssue.setSummary(sourceIssue.summary + " CLONED")
    newIssue.setProjectObject(project)
    newIssue.setIssueTypeId(sourceIssue.getIssueType().id)
    newIssue.setAssignee(user)	
    
    Map<String,Object> newIssueParams = ["issue" : newIssue] as Map<String,Object>

    //Creamos la issue
    Issue issue = componentManager.getIssueManager().createIssueObject(user, newIssueParams)
    
    return issue 
    
}

def linkIssues(Issue newIssue)
{
    def result = true
    try
    {
        def issueLinkManager = componentManager.getIssueLinkManager()
        def issueLinkTypeManager = componentManager.getComponent(IssueLinkTypeManager.class)
        def issueLinkType = issueLinkTypeManager.getIssueLinkType(10001L)
       
        //Creamos los links
        issueLinkManager.createIssueLink(newIssue.id, sourceIssue.id, issueLinkType.id, 1L, user)
        
    
        //Actualiza la sub tarea que se está tratando
        componentManager.getIssueManager().updateIssue(user, sourceIssue, EventDispatchOption.DO_NOT_DISPATCH, false)
    }
    catch (Exception e) {
    
        log.error("Link Issues:" + e)
        result = false

    }
    return result
}
