import java.util.PriorityQueue;
import java.util.Comparator;

public class Simulation {
   int sysTime = 0;
   int fireSize = 0;
   PriorityQueue<Event> p;
   int availableTanks = 0;
   int alarmsSent = 0; //Used in the repeated dispatch subroutine.
   int engineOnScene = 0;
   int truckOnScene = 0;
   int rescueOnScene = 0;
   int activeFFOnScene = 0;
   int vehiclesOnScene = 0;
   int tankThreshhold = 1;
   StatDump conclusion = null;
   public Simulation() {
      p = new PriorityQueue<Event>(10, new EventComparator());
      schedule911Call();
   }
   
   public void tick() {
      Event e = p.poll();
      fireSize+=fireGrowth(e.getTime());      
      sysTime = e.getTime(); 
      if(fireSize<=0&&sysTime>0) {
         processFireOut();
      } else {
         switch(e.getType()) {
            case ENGINEARR:      processEngineArr();    break;  
            case TRUCKARR:       processTruckArr();     break;  
            case RESCUEARR:      processRescueArr();    break;  
            case TANKOUT:        processTankOut();      break;  
            case DISPATCH:       processDispatch(); break;  
            case NINEONEONECALL: process911Call();      break;  
            case FIREOUT:        processFireOut();      break;  
            case FFENTERREHAB:   processRehab();        break;
            case FFLEAVEREHAB:   processFFOnScene(1);    break;
            default: 
               System.out.println("ERROR: Unrecognized event type.");
               System.exit(1);
         }
      }   
   }
   
   public int fireGrowth(int newTime) throws Exception {
      int y = x+5; //Compilation error flag
      
      if(newTime<sysTime) { throw new Exception(); } //TODO?: Find a more specific exception?
      if(newTime==sysTime) { return 0; } 
       
      
      
      
      
   }
   public void scheduleEngineArr() {
      Event e = new Event();
      e.setType(EventTypes.Events.ENGINEARR);
      int newEventTime = sysTime + Miscellanious.normalDist(x,y);
      e.setTime(newEventTime);
      p.add(e);      
   }
   public void processEngineArr() {
      engineOnScene++;
      vehiclesOnScene++;
      Set s = numVehiclesPerAlarm(alarmsSent);
      if(vehiclesOnScene==(s.e+s.t+s.r)) {
         if(fireGrowth(sysTime+1)>0) {
            scheduleDispatch();
         }
      }
      availableTanks+=4;
      processFFOnScene(x);
   }
   public void scheduleTruckArr() {
      Event e = new Event();
      e.setType(EventTypes.Events.TRUCKARR);
      int newEventTime = sysTime + Miscellanious.normalDist(x,y);
      e.setTime(newEventTime);
      p.add(e);
   }
   public void processTruckArr() {
      engineOnScene++;
      vehiclesOnScene++;
      Set s = numVehiclesPerAlarm(alarmsSent);
      if(vehiclesOnScene==(s.e+s.t+s.r)) {
         if(fireGrowth(sysTime+1)>0) {
            scheduleDispatch();
         }
      }
      availableTanks+=4;  
      processFFOnScene(x); 
   }
   public void scheduleRescueArr() {
      Event e = new Event();
      e.setType(EventTypes.Events.RESCUEARR);
      int newEventTime = sysTime + Miscellanious.normalDist(x,y);
      e.setTime(newEventTime);
      p.add(e);
   }
   public void processRescueArr() {
      rescueOnScene++;
      vehiclesOnScene++;
      Set s = numVehiclesPerAlarm(alarmsSent);
      if(vehiclesOnScene==(s.e+s.t+s.r)) {
         if(fireGrowth(sysTime+1)>0) {
            scheduleDispatch();
         }
      }
      availableTanks+=4;
      processFFOnScene(x);
   }
   public void scheduleTankOut() {
      Event e = new Event();
      e.setType(EventTypes.Events.TANKOUT);
      int newEventTime = sysTime + Micellanious.normalDist(x,y);
      e.setTime(newEventTime);
      p.add(e);
   }
   public void processTankOut() {
      if(availableTanks<1) {
         activeFFOnScene--;
      } else {
         availableTanks--;
      }
      
      if(availableTanks==tankThreshold) {
         int y = x + 5; //Compilation flag, need to get more tanks.
      }
      
      
   }
   public void scheduleDispatch() {
      Event e = new event();
      e.setType(EventTypes.Events.DISPATCH);
      e.setTime(sysTime);
      p.add(e);
   }
   public void processDispatch() {
      alarmsSent++;
      switch(alarmsSent) {
         case 0: scheduleVehicles(a,b,c);
         case 1: schedeuleVehicles(a,b,c);
         default: scheduleVehicles(a,b,c);
      }
   }
   public void schedule911Call() {
      Event e = new Event();
      e.setType(EventTypes.Events.NINEONEONECALL);
      int newEventTime = sysTime+x;
      e.setTime(newEventTime);
      p.add(e);
   }
   public void process911Call() {
      scheduleDispatch();  
   
   }
   public void scheduleRehab() {
      Event e = new Event();
      e.setTime(sysTime+120);
      e.setType(EventTypes.Events.FFENTERREHAB);
      p.add(e);
   }
   public void processRehab() {
      activeFFOnScene--;
      if(activeFFOnScene==0) {          
      
      }
   }
   
   public void processFireOut() {
      
      //StatDump is more of a struct than an object
      conclusion = new StatDump();
      //Fill in stats accordingly
      conclusion.timeToFireOut = sysTime;
   }
   public void processFFOnScene(int repititions) {
      int i = 0;
      while(i<repititions) {
         activeFFOnScene++;
         scheduleRehab();
         i++;
      }
   }
   
   public class Set {
      int e, t, r;
      public Set(int e, int t, int r) {
         this.e = e;
         this.t = t;
         this.r = r;
      }
   }
   
   public static Set numVehiclesPerAlarm(int alarmNo) {
      switch(alarmNo) {
         case 1:  return new Set(4,2,2);
         default: return new Set(3,1,1);
         
      } 
   }
   public static int scheduleVehiclesPerAlarm(int alarmNo) {
      switch(alarmNo) {
         case 1:  return scheduleVehicles(a,b,c);
         case 2:  return scheduleVehicles(a,b,c);
         default: return scheduleVehicles(a,b,c); // alarms 2+ ?
      }
   }
   public void scheduleVehicles(int e, int t, int r) {
      for(int i = 0; i<Math.max(e,Math.max(t,r)); i++) {
         if(i<e){scheduleEngineArr(); }
         if(i<t){scheduleTruckArr();  }
         if(i<r){scheduleRescueArr(); }
      }
      
   }
   
}