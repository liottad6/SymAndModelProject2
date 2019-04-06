
/**
 * Event Class
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Event
{
    public enum Order {FIRST, LAST, INCREASING, DECREASING};

    static private final int             MAX_QUEUES = 24;          
    static private double                simTime;
    static private int                   nextEventType;
    static private int                   tellerNumber;
    static private LinkedList<Event>     eventList;
    static private ArrayList<LinkedList> queueLists;

    private double                      eventSimTime;
    private int                         eventType;
    private int                         teller;

    static private double min = 999999;             //min queue length
    static private double max;             //max queue length
    static private double nObservations;   //total num of delays
    static private double sum;             //total delays

    private double [] areaTimest;
    private double [] tlvcTimest;
    private double [] prevalTimest;
    private double [] minTimest;
    private double [] maxTimest;
    /**
     * Constructor for objects of class Event
     */
    public Event()
    {
        this.eventSimTime = 0.0;
        this.eventType    = 1;
        this.teller       = 0;
    }

    /**
     * Constructor for objects of class Event
     */
    public Event(double eventSimTime, int eventType)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = 0;
    }
    
        /**
     * Constructor for objects of class Event
     */
    public Event(double eventSimTime, int eventType, int teller)
    {
        this.eventSimTime = eventSimTime;
        this.eventType    = eventType;
        this.teller       = teller;
    }

    //uses max, min, sum, sum, nObservations lists, increment when proper argument is called
    static public void Sampst(double val, int sampstvar){
        if (val < min)
            min = val;
        if (val > max)
            max = val;
        nObservations ++;
        sum += val;
    }

    //
    static public void OutSampst(int lowvar, int highvar){
        System.out.println("sampst var number: " + Integer.toString(1));
        System.out.println("avg: " + Double.toString(sum/nObservations));
        System.out.println("num of vals: " + Double.toString(nObservations));
        System.out.println("max: " + max);
        System.out.println("min: " + min);
    }

    static public double Filest(int qNum){
        return 0;
    }
    //double result = Timest(0.0, -(TIM_VAR + qNum));
    //return result;

    public double GetEventTime()
    {
        return eventSimTime;
    }

    /**
     */
    static double Timest(double value, int qNum){
        return 0.0;
    }
    public int GetEventType()
    {
        return eventType;
    }
    
    public int GetTeller()
    {
        return teller;
    }

    static public void EventSchedule(Event ev)
    {
        int current_index = 0;
        boolean added = false;
        while (current_index < eventList.size()) {
            if (ev.eventSimTime < eventList.get(current_index).eventSimTime) {
                eventList.add(current_index, ev);
                added = true;
                break;
            }
            current_index ++;
        }
        if (current_index == eventList.size() & !added)
            eventList.addLast(ev);
    }

    static public void InsertInQueue(Event ev, Order order, int qNum)
    {
        if(order == Order.FIRST) {
            queueLists.get(qNum).addFirst(ev);
        } else if (order == Order.LAST){
            queueLists.get(qNum).addLast(ev);
        } else if (order == Order.INCREASING){
            int iter = 0;
            while (iter < queueLists.get(qNum).size()) {

                iter ++;
            }
        } else { //decreasing, unused

        }
    }

    static Object RemoveFromQueue(Order order, int qNum)
    {
        if (qNum == MAX_QUEUES+1){
            return eventList.remove(0);
        }
        if(order == Order.FIRST)
        {
            //queueLists.get(qNum).removeFirst();
            return queueLists.get(qNum).remove(0);
        }
        else //last
        {
        //queueLists.get(qNum).removeLast();
            return queueLists.get(qNum).remove(queueLists.get(qNum).size()-1);
        }
    }


    static public void Initialize()
    {
        eventList = new LinkedList<>();
        queueLists = new ArrayList<>();
        int iter = 1;
        while (iter <= MAX_QUEUES+1) {
            LinkedList<Event> list = new LinkedList<>();
            queueLists.add(list);
            iter ++;
        }
        simTime = 0;
        min = 0;
        max = 0;
        nObservations = 0;
        sum = 0;
    }

    static public double GetSimTime()
    {
        return simTime;
    }
    
    static public int GetNextEventType()
    {
        return nextEventType;
    }
    
    static public int GetTellerNumber()
    {
        return tellerNumber;
    }

    static public int GetQueueSize(int qNum)
    {
        if (qNum == MAX_QUEUES+1){
            return eventList.size();

        }
        else
            return (queueLists.get(qNum).size());
    }

    static public void Timing()
    {
        if (eventList.size() > 0) {
            Event ev = (Event) RemoveFromQueue(Order.FIRST, 25);
            simTime = ev.GetEventTime();
            nextEventType = ev.GetEventType();
            tellerNumber = ev.GetTeller();
        }

        
    }
    static public void EventCancel(int eventType){
        //iterates eventlist and removes the first event with this type
        int iter = 0;
        while (iter < eventList.size()){
            if (eventList.get(iter).eventType == eventType){
                eventList.remove(iter);
                return;
            }
            iter ++;
        }
    }
    static public void DisplayQueue(){
        String mode = "x";
        if (mode.equals("EventList")) {
            int iter = 0;
            System.out.println("###DISPLAY_QUEUE###");
            while (iter < eventList.size()) {
                System.out.println(eventList.get(iter).eventType);
                iter++;
            }
            System.out.println("###END_DISPLAY###");
        } else if (mode.equals("QueueList")) {
            System.out.println("###DISPLAY_QUEUE###");
            System.out.println("SIM TIME: " + simTime);
            System.out.println("0:" + queueLists.get(0).toString());
            System.out.println("1:" + queueLists.get(1).toString());
            System.out.println("2:" + queueLists.get(2).toString());
            System.out.println("3:" + queueLists.get(3).toString());
            System.out.println("4:" + queueLists.get(4).toString());
            System.out.println("5:" + queueLists.get(5).toString());
            System.out.println("6:" + queueLists.get(6).toString());
            System.out.println("7:" + queueLists.get(7).toString());
            System.out.println("8:" + queueLists.get(8).toString());
            System.out.println("9:" + queueLists.get(9).toString());
            System.out.println("10:" + queueLists.get(10).toString());
            System.out.println("11:" + queueLists.get(11).toString());
            System.out.println("12:" + queueLists.get(12).toString());
            System.out.println("13:" + queueLists.get(13).toString());
            System.out.println("14:" + queueLists.get(14).toString());
            System.out.println("15:" + queueLists.get(15).toString());
            System.out.println("###END_DISPLAY###");
        }
    }
    public String toString(){
        return "<" + eventSimTime + ">";
    }
}
