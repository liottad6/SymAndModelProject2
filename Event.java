
/**
 * Event Class
 * 
 * @James+David+Josh+
 * @4/9/19
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class Event
{
    public enum Order {FIRST, LAST, INCREASING, DECREASING};

    static private final int             MAX_QUEUES = 24;          
    static private double                simTime;
    static private int                   nextEventType;
    static private int                   tellerNumber;
    static private LinkedList<Event>     eventList;
    static private ArrayList<LinkedList<Event>> queueLists;

    private double                      eventSimTime;
    private int                         eventType;
    private int                         teller;

    static private double min = 999999;         // min queue length
    static private double max;                  // max queue length
    static private double nObservations;        // total num of delays
    static private double sum;                  // total delays
    static private double waiting_density;      // total area value for how many people are waiting
    static private double last_call = 0;        // time since the last call to Time_Update()


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

    // returns the total number of people in the system
    static private int Total_qsize(){
        int sum = 0;
        int iter = Bank.numTellers;
        while (iter < MAX_QUEUES+1) {
            sum += GetQueueSize(iter);
            iter ++;
        }
        return sum;
    }

    // collects data on the min/max delay time, total number of observations, and the total time delayed
    static public void Sampst(double val, int sampstvar){
        if (sampstvar == 1) {  //depart
            if (val < min)
                min = val;
            if (val > max)
                max = val;
            nObservations++;
            sum += val;
        }
    }

    // Outputs the data collected from Sampst()
    static public void OutSampst(int lowvar, int highvar){
        int iter = lowvar;
        while (iter <= highvar) {
            System.out.println("\n sampst                    Number");
            System.out.println("variable                     of  ");
            System.out.println(" number      average       values        Max              Min");
            System.out.println("-----------------------------------------------------------------");
            System.out.println(iter + "          " + (double)(Math.round(10000000*sum/nObservations))/10000000 + "         "
                    + nObservations + "      " + (double)(Math.round(100000*max))/100000 + "         " + min);
            System.out.println("-----------------------------------------------------------------");
            iter ++;
        }
    }

    // returns the average number of people waiting
    // (divided by the number of tellers because Bank expects it to be a sum)
    static public double Filest(int qNum){

        return waiting_density/(simTime*Bank.numTellers);
    }


    // Iterates through eventList
    // Adds the new event at the index where the Event eventSimTime is greater than this new one's eventSimTime.
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

    // Adds the event to the target queue.
    // If order == FIRST, add to the front of the queue.
    // If order == LAST, add to the end.
    // If order == INCREASING, add once a value with a higher eventSimTime is found.
    // if order == DECREASING, add once a value with a lower eventSimTime is found.
    static public void InsertInQueue(Event ev, Order order, int qNum)
    {
        if(order == Order.FIRST) {
            queueLists.get(qNum).addFirst(ev);
        } else if (order == Order.LAST){
            queueLists.get(qNum).addLast(ev);
        } else if (order == Order.INCREASING){
            int iter = 0;
            while (iter < queueLists.get(qNum).size() & ev.eventSimTime > queueLists.get(qNum).get(iter).eventSimTime) {
                iter ++;
            }
            queueLists.get(qNum).add(iter, ev);

        } else { //decreasing
            int iter = 0;
            while (iter < queueLists.get(qNum).size() & ev.eventSimTime < queueLists.get(qNum).get(iter).eventSimTime) {
                iter ++;
            }
            queueLists.get(qNum).add(iter, ev);
        }
    }

    // If qNum == 25, remove and return the next event from EventList.
    // Else, remove either the first or last Event in the given queue.
    static Object RemoveFromQueue(Order order, int qNum)
    {

        if (qNum == MAX_QUEUES+1){
            return eventList.remove(0);
        }
        if(order == Order.FIRST)
        {
            return queueLists.get(qNum).remove(0);
        }
        else //last
        {
            return queueLists.get(qNum).remove(queueLists.get(qNum).size()-1);
        }
    }

    // Creates data structures and sets variables to 0 for the next iteration of testing.
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
        waiting_density = 0;
        last_call = 0;
    }

    // If qNum == 25, return the event list length
    // Else, retur the queue size of the given line.
    // Tellers will only range from 0 to 1.
    static public int GetQueueSize(int qNum)
    {
        if (qNum == MAX_QUEUES+1){
            return eventList.size();

        }
        else
            return (queueLists.get(qNum).size());
    }

    // Timing() takes the next coming event information.
    // Calls Time_Update() to get stats on the average queue size.
    static public void Timing()
    {
        if (eventList.size() > 0) {
            Event ev = (Event) RemoveFromQueue(Order.FIRST, 25);
            simTime = ev.GetEventTime();
            nextEventType = ev.GetEventType();
            tellerNumber = ev.GetTeller();
        }
        Time_Update();
    }

    // Multiplies the time since last call and the queue size, and adds it to waiting_density.
    static void Time_Update(){
        waiting_density += ((simTime-last_call) * Total_qsize());
        last_call = simTime;
    }

    // Removes the first appearance of the given event from eventList.
    static public void EventCancel(int eventType){
        int iter = 0;
        while (iter < eventList.size()){
            if (eventList.get(iter).eventType == eventType){
                eventList.remove(iter);
                return;
            }
            iter ++;
        }
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
    public int GetEventType()
    {
        return eventType;
    }

    public int GetTeller()
    {
        return teller;
    }
    public double GetEventTime()
    {
        return eventSimTime;
    }

    // Unused
    static public void DisplayQueue(){
        //
    }
}
