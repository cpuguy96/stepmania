package stepmania;
import java.awt.*;
import java.applet.*;
import java.util.*; 
import java.io.*;

public class Song {
	Image cover;
	Image backCover;
	AudioClip music;
	Scanner input;
	ArrayList <Note> notes;
	String name;
	Song(Image c, Image bc, String n, AudioClip m) {
		cover = c; 
		backCover = bc;
		music = m;
		name = n;
	}
	public ArrayList<Note> getSong() throws IOException{
		input = new Scanner (new File(name));	
		notes = new ArrayList <Note>();
		while(input.hasNext()){
			notes.add(new Note(input.next(), input.nextInt() * 100 - 20));
		}
		for(int i = 1; i<notes.size(); i++){
			notes.get(i).setDelay(notes.get(i-1).getDelay());
		}
		return notes;
	}
	public String getName() {return name;}
	public Image getCover(){return cover;}
	public Image getBackCover(){return backCover;}
	public AudioClip getMusic(){return music;}
}