import java.util.Scanner;
import java.util.ArrayList;

public class Bank
{
    static ArrayList<Double> timeArrival;

    /* Declare non-simlib global variables. */

    static int   minTellers, maxTellers, numTellers, shortestLength, shortestQueue;
    static double lengthDoorsOpen;

    static final int IDLE = 0;
    static final int BUSY = 1;

    static final int EVENT_ARRIVAL     = 1;  // Event type for arrival of a customer. 
    static final int EVENT_DEPARTURE   = 2;  // Event type for departure of a customer.
    static final int EVENT_CLOSE_DOORS = 3;  // Event type for closing doors at 5 P.M. 

    static final int STREAM_INTERARRIVAL = 1;
    static final int STREAM_SERVICE = 2;

    static double meanInterArrival;
    static double meanService;
    static int nDelaysRequired;

    static int nEvents;
    static int serverStatus;
    static int numInQ;
    static double timeLastEvent;
    static int nCustsDelayed;
    static double totalOfDelays; 
    static double areaNumInQ;
    static double areaServerStatus;
    static double[] timeNextEvent = new double[3];
    static double total_of_delays;

    /**
     * Main0
     * 
     * 1) The first arival event is created in Initialize
     * 2) The FIFO queue timeArrival holds "arrival times" that are waiting for server
     */
    public static void main(String[] args) 
    {
        nEvents = 2;

        minTellers = 5;
        // maxTellers = 7;
        maxTellers = 5;
        meanInterArrival = 1.0;
        meanService = 4.5;
        lengthDoorsOpen = 8.0;    // in hours

        // Write report heading and input parameters. 
        System.out.printf("Multiteller bank with separate queues & jockeying\n\n");
        System.out.printf("Number of tellers%16d to%3d\n\n",minTellers, maxTellers );
        System.out.printf("Mean interarrival time%11.3f minutes\n\n", meanInterArrival);
        System.out.printf("Mean service time%16.3f minutes\n\n", meanService);
        System.out.printf("Bank closes after%16.3f hours\n\n\n\n", lengthDoorsOpen);

        // Run the simulation while more delays are still needed 
//        for (numTellers = minTellers; numTellers <= maxTellers; ++numTellers) 
        for (numTellers = minTellers; numTellers <= minTellers; ++numTellers) 
        {
            Event.Initialize();

            //UpdateTimAvgStats();     // Update time-average statistical accumulators.

            // Schedule the first arrival. 
            double time = SimLib_Random.Expon(meanInterArrival, STREAM_INTERARRIVAL);
            Event ev = new Event(time, EVENT_ARRIVAL);
            //System.out.println("time generated " + time);
            Event.EventSchedule(ev);
            //Event.DisplayQueue();

            // Schedule the bank closing, in minutes
            ev = new Event(60.0 * lengthDoorsOpen, EVENT_CLOSE_DOORS);

            while (Event.GetQueueSize(25) != 0)
            {
                Event.Timing();                // Determine the next event.

                switch (Event.GetNextEventType())   // Invoke the appropriate event function.
                {
                    case EVENT_ARRIVAL:
                       Arrive();
                    break;
                    case EVENT_DEPARTURE:
                       Depart(Event.GetTellerNumber());
                    break;
                    case EVENT_CLOSE_DOORS:
                       int[] a = {1};
                       a[5] = 6;
                    break;
                }
                //System.out.println("sim Time = " + simTime + " Qsize = " + time_arrival.size() );
            }

            //Report();
        }
    }

    /*****************************************************************************************************************************
     * 
     * Arrive
     * 
     *  
     */
    static void Arrive()
    {   
        // Schedule the next arrival. 
        Event ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanInterArrival, STREAM_INTERARRIVAL), EVENT_ARRIVAL);
        Event.EventSchedule(ev);
//        Event.DisplayQueue();

        for (int teller = 1; teller <= numTellers; ++teller) 
        {
            if (Event.GetQueueSize(numTellers + teller) == 0)
            {
                ev = new Event(); // fake event

                // Make this teller busy (attributes are irrelevant). 
                Event.InsertInQueue(ev, Event.Order.FIRST, numTellers + teller);

                // Schedule a service completion. 
     
                ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE, teller);
                Event.EventSchedule(ev);

                return;
            }
        }

        // All tellers are busy, so find the shortest queue (leftmost shortest in case of ties).

        int shortestLength = Event.GetQueueSize(1);
        int shortestQueue = 1;

        for (int teller = 2; teller <= numTellers; ++teller)
        {
            if (Event.GetQueueSize(teller) < shortestLength) {
                shortestLength = Event.GetQueueSize(teller);
                shortestQueue  = teller;
            }
        }  

        // Place the customer at the end of the leftmost shortest queue. 
        //ev = new Event(Event.GetSimTime(), EVENT_ARRIVAL);
        Event.InsertInQueue(ev, Event.Order.LAST, shortestQueue);
    }
    
    /*****************************************************************************************************************************
     * 
     * Depart
     * 
     *  
     */
    static void Depart(int teller)
    {   
        // Check to see whether the queue for teller "teller" is empty.
        
        if (Event.GetQueueSize(teller) == 0)
        {
            // The queue is empty, so make the teller idle. 
            Event.RemoveFromQueue(Event.Order.FIRST, numTellers + teller);
        }
        else
        {
            // The queue is not empty, so start service on a customer. 
            Event.RemoveFromQueue(Event.Order.FIRST, teller);
            
            Event ev = new Event(Event.GetSimTime() + SimLib_Random.Expon(meanService, STREAM_SERVICE), EVENT_DEPARTURE);
            Event.EventSchedule(ev);
        }
    }
}