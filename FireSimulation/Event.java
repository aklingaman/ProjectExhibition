public class Event {
   private int time;
   private EventTypes.Events type;
   
   
   public Event() {}
   public Event(int time, EventTypes.Events type) {
      this.time = time;
      this.type = type;
   }
   
   public int getTime() { return this.time; }
   public EventTypes.Events getType() { return this.type; }
   
   public void setTime(int time) { this.time = time; }
   public void setType(EventTypes.Events type) { this.type = type; }
   
   public boolean equals(Event other) {
      boolean a = this.time==other.getTime();
      boolean b = this.type==other.getType();
      return a&&b;
   }
   



}