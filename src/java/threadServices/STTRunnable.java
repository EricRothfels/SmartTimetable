package threadServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.management.BadAttributeValueExpException;
import javax.swing.JComboBox;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import smartTimetableException.EmptySTTException;
import model.StandardTimetable;
import model.StandardTimetableContainer;
import webServices.ScrapeWebPage;

public class STTRunnable implements Runnable {


	/**
	 * checks to see if the future course(s) have gotten required information then adds the course(s)
	 */
    public void run() {
    	SmartTimetableGUI.statusField.setText("Please wait while your Standard Timetable is added. This can take a while" +
				" depending on its size.");
    	JComboBox<String> sttBox = SmartTimetableGUI.getSttBox();
		String sttUrl = SmartTimetable.getCurrentSTTUrl().replaceAll("sttdept", "sttspec") 
				+ "&spec=" + SmartTimetable.getStandardTimetableCode(sttBox.getSelectedIndex()-1).trim();
		
		String sttSpecializationAndYear = (String) sttBox.getSelectedItem();
		String session = SmartTimetableGUI.getSelectedSession();
		String campus = SmartTimetableGUI.getSelectedCampus();
		
		SmartTimetableGUI.addToFutureCourses(sttSpecializationAndYear);
		
		populateSTTs(sttUrl, sttSpecializationAndYear, session, campus);
    }

    
    /**
     * 
     * @param sttUrl
     * @param sttSpecializationAndYear
     * @param session
     * @param campus
     */
	private void populateSTTs(String sttUrl, String sttSpecializationAndYear, String session, String campus) {
		try {
    		Document document = ScrapeWebPage.getDocument(sttUrl).get();
    		
    		Elements elements = document.getElementsByClass("section1");
    		elements.addAll(document.getElementsByClass("section2"));
    		
    		List<StandardTimetable> standardTimetables = new ArrayList<StandardTimetable>();
   		
    		String baseUrl = sttUrl.replaceAll("sttspec", "sttcode");
/* 		
			// debug:
    		for(int i=0; i < elements.size(); i++)
    			System.out.println(i+":"+elements.get(i));
    		System.out.println("----------------------------------------------");   		
*/  		
    		for(Element element : elements) {
    			Elements tdElements = element.getElementsByTag("td");
    			
    			String elementString = element.toString();
    				
    			int sttUrlIndex = elementString.indexOf("sttcode=");
        		int sttUrlIndexEnd = elementString.indexOf("\"", sttUrlIndex);
        		String url = baseUrl + "&" + elementString.substring(sttUrlIndex, sttUrlIndexEnd);
        		
        		String status = tdElements.get(0).text();
        		String sttName = tdElements.get(1).text();        		
        		
        		StandardTimetable stt = new StandardTimetable(url, sttName, session, campus, status);
        		
    			standardTimetables.add(stt);
    			SmartTimetableGUI.addSTTToUI(stt);
    			
    			//System.out.println("sttUrl:"+url+ "\nsttName:"+ sttName +" status:"+ status); // debug
    		}
    		// add the stts to the GUI
    		if (standardTimetables.isEmpty()) {
    			throw new EmptySTTException("Add " + sttSpecializationAndYear + " unsuccessful.  No courses were"
    					+ " found.");
    		}
    		StandardTimetableContainer sttContainer = new StandardTimetableContainer(standardTimetables,
    				sttSpecializationAndYear, session, campus);
    		SmartTimetable.setStandardTimetableContainer(sttContainer);
    		SmartTimetableGUI.statusField.setText("Add " + sttSpecializationAndYear + " successful");
		}
    	catch (  NoSuchFieldException |  InterruptedException | EmptySTTException |
				 BadAttributeValueExpException | ExecutionException  | IOException exception ) {
    		SmartTimetableGUI.statusField.setText(exception.toString());
			exception.printStackTrace();
		}
	}
}