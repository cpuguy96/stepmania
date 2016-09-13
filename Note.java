public class Note {
    String direction;
	int delay;
	Note(String dir, int del){
		direction = dir;
		delay = del;
	}
	public String getDirection(){return direction;}
	public int getDelay(){return delay;}
	public void setDelay(int add){delay+=add;}
}