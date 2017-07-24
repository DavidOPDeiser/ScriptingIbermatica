import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.customfields.option.Option
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.issue.Issue

def ISSUE_TYPE_TASK = "Task"
def CUSTOM_FIELD_SOURCE_ID = 10102L
def CUSTOM_FIELD_VERSION_ID = 10103L
def SOURCE_EMAIL_DESCRIPTION = "Email"
def MESSAGE_ERROR_VERSION = "El campo versión actual es obligatorio"
def MESSAGE_ERROR_SOURCE = "El campo origen es obligatorio"

def componentManager = ComponentManager.getInstance()
def customFieldManager = componentManager.getCustomFieldManager()
def source = customFieldManager.getCustomFieldObject(CUSTOM_FIELD_SOURCE_ID)
def numVersion = customFieldManager.getCustomFieldObject(CUSTOM_FIELD_VERSION_ID)

def option = (Option)issue.getCustomFieldValue(source)

if(issue.getIssueType().name.equals(ISSUE_TYPE_TASK) && option.getValue().equals(SOURCE_EMAIL_DESCRIPTION))
{
    if(issue.getCustomFieldValue(numVersion) == null)
        throw new InvalidInputException(MESSAGE_ERROR_VERSION)
}
else
     throw new InvalidInputException(MESSAGE_ERROR_SOURCE)