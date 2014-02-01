package threadServices;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.management.BadAttributeValueExpException;
import javax.swing.JComboBox;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webServices.ScrapeWebPage;

/**
 * Class to populate standard timetable selection list
 * @author Eric Rothfels
 */
public class STTListOptionsRunnable implements Runnable {
	
	// string to check the department url for validity
	private static final String CHECK_EXISTS = 
			"The requested Standard Timetable program is either no longer offered at ";		

	
	/**
	 * checks to see if the future doc have gotten required information then adds stt data to GUI sttBox
	 * and SmartTimetable list
	 */
    public void run() {
    	SmartTimetableGUI.statusField.setText("Please wait while the list of Standard Timetables is populated");
		// Get course code input from user
		String department = SmartTimetableGUI.sttField.getText().toUpperCase();
		try {
			// parses course code input, checks validity 
			department = ScrapeWebPage.checkSTTInput(department);
							
			String session = SmartTimetableGUI.getSelectedSession();
			String campus = SmartTimetableGUI.getSelectedCampus();
			
			// get url for stt department request
			String sttUrl = ScrapeWebPage.getSTTUrl(department, session, campus);
			SmartTimetable.setCurrentSTTUrl(sttUrl);
			
			SmartTimetableGUI.addToFutureCourses(department);
			
			//add stt program options:
			populateProgramOptions(ScrapeWebPage.getDocument(sttUrl), department, campus);
		}
		catch (IOException | BadAttributeValueExpException exception) {
			SmartTimetableGUI.statusField.setText(exception.toString());
		} 
    }

    
    private void populateProgramOptions(Future<Document> sttDoc, String department, String campus) {
    	
    	try {
    		Document sttDocument = sttDoc.get();
    		
    		// check if webpage is a valid stt webpage
    		ScrapeWebPage.checkUrl(sttDocument.text(), CHECK_EXISTS, campus, department);
    		
    		JComboBox<String> sttBox = SmartTimetableGUI.getSttBox();
    		clearSTTOptionsList(sttBox, department);
   	
    		Elements elements = sttDocument.getElementsByAttribute("HREF");
    		
/*    		   
    		//Debug:
    		for (int i=0; i < elements.size(); i++) {
    			System.out.println(i+":zqx:" + elements.get(i));
    		}
*/  		
    		// add stt info to lists
    		for(Element element : elements) {
    			String stt = element.toString();
    			int specIndex = stt.indexOf("spec=");
    			if (specIndex != -1) {
	    			stt = stt.substring(specIndex+5, specIndex+10);
	    			SmartTimetable.addStandardTimetableCode(stt);
	    			sttBox.addItem(element.text());
    			}
    		}
    	SmartTimetableGUI.statusField.setText("");
    	SmartTimetableGUI.sttField.setText("");
    	
		} catch (InterruptedException | ExecutionException | BadAttributeValueExpException exception) {
			SmartTimetableGUI.statusField.setText(exception.toString());
			exception.printStackTrace();
		}
		SmartTimetableGUI.removeFromFutureCourses(department);   
	}

    
	/**
     * clear fields of the GUI related to standard timetables
     * @param sttBox 
	 * @param department 
     */
	private void clearSTTOptionsList(JComboBox<String> sttBox, String department) {
		sttBox.removeAllItems();
		sttBox.addItem(department + " " + SmartTimetableGUI.STT_NAME);
		SmartTimetable.clearStandardTimetableCodes();
	}
}