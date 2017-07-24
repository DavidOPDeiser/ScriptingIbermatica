import groovy.json.StreamingJsonBuilder;
import org.apache.commons.codec.binary.Base64;

createIssueByRest()

def createIssueByRest()
{
	try{
          
	    def baseUrl = "http://localhost:8080/rest/api/2/issue"
            def CREDENTIALS = "admin:admin"
	    def body = [
                fields: [
                    project: [
                        key: "SCRIP"
                    ],
                    summary: "Task created via API",
                    issuetype: [
                        name: "Task"
                    ],
                    reporter: [
                        name: "admin"
                    ]
        
                ]
            ]
                        
            URL url;
     	    url = new URL(baseUrl);
            
            URLConnection connection = url.openConnection();
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
            connection.setRequestProperty('Authorization', 'Basic ' + CREDENTIALS.bytes.encodeBase64().toString())
            connection.outputStream.withWriter("UTF-8") { new StreamingJsonBuilder(it, body) }
            connection.connect();
            
            log.warn("status:" + connection.getResponseCode())
	    log.warn("message:" + connection.getResponseMessage())
        }
        catch(Exception ex)
        {
        	log.error("******* Se ha producido un error en la creación de la issue via rest:" + ex)
        }

}