package threadServices;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.management.BadAttributeValueExpException;

import org.jsoup.nodes.Document;

import model.StandardTimetable;
import webServices.ScrapeWebPage;

public class PopulateSTTRunnable implements Runnable {
	
	private StandardTimetable stt;
	
	public PopulateSTTRunnable(StandardTimetable stt) {
		this.stt = stt;
	}

	@Override
	public void run() {
		
		try {
			Document sttDocument = ScrapeWebPage.getDocument(stt.getSttUrl()).get();
			stt.populateCourses(sttDocument);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
