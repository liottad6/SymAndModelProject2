
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

    /**
     */
    public double GetEventTime()
    {
        return eventSimTime;
    }

    /**
     */
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
        eventList.add(ev);
    }

    static public void InsertInQueue(Event ev, Order order, int qNum)
    {
        if(order == Order.FIRST) {
            queueLists.get(qNum).addFirst(ev);
        } else if (order == Order.LAST){
            queueLists.get(qNum).addLast(ev);
        } else if (order == Order.INCREASING){

        } else { //decreasing

        }
    }

    static Object RemoveFromQueue(Order order, int qNum)
    {
        if(order == Order.FIRST)
        {
            queueLists.get(qNum).removeFirst();
        }
        else //last
            {
            queueLists.get(qNum).removeLast();
            }
        return null;
    }


    static public void Initialize()
    {
        eventList = new LinkedList<>();
        queueLists = new ArrayList<>();
        int iter = 0;
        while (iter <= MAX_QUEUES) {
            LinkedList<Event> list = new LinkedList<>();
            queueLists.add(list);
            iter ++;
        }
        simTime = 0;
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
        int sum = 0;
        int iter = 0;
        while (iter < qNum) {
            if (!queueLists.get(iter).isEmpty())
                sum += queueLists.get(iter).size();
            iter ++;
        }
        return sum;
    }

    static public void Timing()
    {
        Event ev      = (Event)RemoveFromQueue(Order.FIRST, 25);
        simTime       = ev.GetEventTime();
        nextEventType = ev.GetEventType();
        tellerNumber  = ev.GetTeller();
        
    }
    static public void EventCancel(int event){
        eventList.remove(event);
    }
}
