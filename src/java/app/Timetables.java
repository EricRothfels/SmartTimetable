/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.PriorityQueue;
import model.ValidCombination;

/**
 *
 * @author eric
 */
public class Timetables {
    
    private static Timetables tables = null;
    private static PriorityQueue<ValidCombination> timeTables;

    
    private Timetables() {
        timeTables = new PriorityQueue<ValidCombination>();
    }
    
    
    public static Timetables getInstance() {
        if (tables == null)
            tables = new Timetables();
        return tables;
    }
    
    public static PriorityQueue<ValidCombination> getTimetables() {
        return timeTables;
    }

    public static void setTimetables(PriorityQueue<ValidCombination> tables) {
        timeTables = tables;
    }
    
    
}
