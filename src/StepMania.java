
import java.awt.*;
import java.applet.*;
import java.util.*; 
import java.io.*;
import java.security.AccessControlException;
/********************************
 |   StepMania2                	|
 |   Started 4/9/15				|
 |   TEAM MEMBERS				|
 |   	Chimezie Iwuanyanwu		|
 ********************************/

public class StepMania extends Applet implements Runnable{
	//Start variables for double buffer
	Dimension d;
	Dimension dim;
	Graphics bufferGraphics;
	Image offscreen; 
	int frame;
	int sleepTime;
	Thread backAnimator;
	//End variables for double buffer
	
	//Start images for arrows
	Image leftArrow[] = new Image [4];
	Image upArrow[] = new Image [4];
	Image downArrow[] = new Image [4];
	Image rightArrow[] = new Image [4];
	Image rec[] = new Image [4];
	Image recPress[] = new Image [4];
	//End images for arrows
	
	Image numbers[] = new Image [10];
	Image bars[] = new Image [4];
	Image back;
	Image currBack;
	boolean keyOff[] = new boolean [1000];
	int tcommand = 0;
	int intKeyOff = 0;
	int yposMove;
	int barsFrame;
	int score;
	int songOnce = 1;
	int centerImage = 0;
	int arrowYPos;
	char ch;
	AudioClip currMusic;
	Font f;
	String text = "";
	ArrayList <Song> songList;
	ArrayList <Note> currSong;
	ArrayList <Character> chDown;
	public static void main(String args[]) {
		System.out.println("Convert to Java Application!");
	}
	
	public void init(){
		//Start variables for double buffer
		d = getSize();
		dim = getSize();
		sleepTime = 15;	
		offscreen = createImage(dim.width,dim.height);
		bufferGraphics = offscreen.getGraphics();
		//End variables for double buffer
		
		//Start images for receiving arrows
		rec[0] = getImage(getCodeBase(), "images/leftRec.PNG");
		rec[1] = getImage(getCodeBase(), "images/downRec.PNG");
		rec[2] = getImage(getCodeBase(), "images/upRec.PNG");
		rec[3] = getImage(getCodeBase(), "images/rightRec.PNG");
		recPress[0] = getImage(getCodeBase(), "images/leftRecPress.PNG");
		recPress[1] = getImage(getCodeBase(), "images/downRecPress.PNG");
		recPress[2] = getImage(getCodeBase(), "images/upRecPress.PNG");
		recPress[3] = getImage(getCodeBase(), "images/rightRecPress.PNG");
		//End images for receiving arrows
		
		//Start retrieving images for arrows
		leftArrow[0] = getImage(getCodeBase(), "images/leftArrow1.PNG");
		leftArrow[1] = getImage(getCodeBase(), "images/leftArrow2.PNG");
		leftArrow[2] = getImage(getCodeBase(), "images/leftArrow3.PNG");
		leftArrow[3] = getImage(getCodeBase(), "images/leftArrow4.PNG");
		downArrow[0] = getImage(getCodeBase(), "images/downArrow1.PNG");
		downArrow[1] = getImage(getCodeBase(), "images/downArrow2.PNG");
		downArrow[2] = getImage(getCodeBase(), "images/downArrow3.PNG");
		downArrow[3] = getImage(getCodeBase(), "images/downArrow4.PNG");
		upArrow[0] = getImage(getCodeBase(), "images/upArrow1.PNG");
		upArrow[1] = getImage(getCodeBase(), "images/upArrow2.PNG");
		upArrow[2] = getImage(getCodeBase(), "images/upArrow3.PNG");
		upArrow[3] = getImage(getCodeBase(), "images/upArrow4.PNG");
		rightArrow[0] = getImage(getCodeBase(), "images/rightArrow1.PNG");
		rightArrow[1] = getImage(getCodeBase(), "images/rightArrow2.PNG");
		rightArrow[2] = getImage(getCodeBase(), "images/rightArrow3.PNG");
		rightArrow[3] = getImage(getCodeBase(), "images/rightArrow4.PNG");
		//End retrieving images for arrows
		
		//Start retrieving images for numbers
		numbers[0] = getImage(getCodeBase(), "images/zero.GIF");
		numbers[1] = getImage(getCodeBase(), "images/one.GIF");
		numbers[2] = getImage(getCodeBase(), "images/two.GIF");
		numbers[3] = getImage(getCodeBase(), "images/three.GIF");
		numbers[4] = getImage(getCodeBase(), "images/four.GIF");
		numbers[5] = getImage(getCodeBase(), "images/five.GIF");
		numbers[6] = getImage(getCodeBase(), "images/six.GIF");
		numbers[7] = getImage(getCodeBase(), "images/seven.GIF");
		numbers[8] = getImage(getCodeBase(), "images/eight.GIF");
		numbers[9] = getImage(getCodeBase(), "images/nine.GIF");
		//End retrieving images for numbers
		
		//Start retrieving songs. Add a new Song here with Cover Image, Back Cover Image, Text Document with Notes, and Audio
		songList = new ArrayList<Song>();
		songList.add(new Song (getImage(getCodeBase(), "images/BlackRockCover.JPG"), getImage(getCodeBase(), "images/BlackRockBack.JPG"),
														 "key/BlackRockShooter.txt", getAudioClip(getCodeBase(), "songs/BlackRockShooter.wav")));											 
		songList.add(new Song (getImage(getCodeBase(), "images/ParadiseCover.JPG"), getImage(getCodeBase(), "images/ParadiseBack.JPG"),
														 "key/Paradise.txt", getAudioClip(getCodeBase(), "songs/Paradise.wav")));									 
		//End retrieving songs
		
		bars[0] = getImage(getCodeBase(), "images/bbar.JPG");
		bars[1] = getImage(getCodeBase(), "images/ybar.JPG");
		bars[2] = getImage(getCodeBase(), "images/gbar.JPG");
		bars[3] = getImage(getCodeBase(), "images/rbar.JPG");
		arrowYPos = d.height - 111;
		chDown = new ArrayList<Character>();
	}
	public Image getImage(String s){
		Image i = getImage(getCodeBase(), "images/"+s);
		return i;
	}
	public void drawString(Graphics g, String s, int size, int x, int y){
		Graphics2D g2d = (Graphics2D) g;
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	   	g2d.setColor(Color.white);
	   	f = new Font("SANS_SERIF", 0, size);
		g2d.setFont(f);
		g2d.drawString(s, x, y);
	}
	public void paintFrame(Graphics g) {
		backAnimator = new Thread (this);
		backAnimator.start();
		if(tcommand == 0)playStartScreen(g);
		if(tcommand == 1)playSelectionScreen(g);
		if(tcommand == 2)playCurrentSong(g, centerImage);
	}
	public void playStartScreen(Graphics g){
		g.drawImage(getImage("StartScreen.JPG"), 0, 0, d.width, d.height, this);
		if((frame/200)%2 == 0) drawString(g, "Press enter to start", 25, d.width-500, 290);
	   	if(ch == 10){tcommand = 1; intKeyOff++;}
	}
	public void playSelectionScreen(Graphics g){
		g.drawImage(getImage("SelectionScreen.JPG"), 0, 0, d.width, d.height, this);
	    if(ch == ']' && intKeyOff == 1){
	      	if(!(centerImage + 1 >= songList.size())) centerImage++;
	      	intKeyOff++;
	    }
		if((ch == 'q' || ch == 'Q') && intKeyOff == 1){
			if(!(centerImage - 1 < 0)) centerImage--;
	   		intKeyOff++;
	    }
		int imageLength = 400;
		int imageHeight = (int)(imageLength/1.6);
		int xpos = (d.width-(imageLength))/2;
		int temp = xpos/2;
		int ypos = (d.height-imageHeight)/2;
		int sel = centerImage;
		boolean reverse = true;
		for(int i = 0; i < songList.size(); i++){
			g.drawImage(songList.get(sel).getCover(), xpos, ypos, imageLength, imageHeight, this);
			if(reverse)
				if(sel + 1 >= songList.size()){
					reverse = false; 
					sel = centerImage - 1; 
					xpos = (temp * 3)/2 - imageLength;
				}
				else{xpos += temp + imageLength; sel++;}
		
			else
				if(sel - 1 < 0) reverse = true;
				else{xpos -= temp + imageLength; sel--;}	
		}
		g.setColor(Color.RED);
		g.fillRect(d.width - ((frame / 2) % (d.width*2 +100)) + 100, d.height-40, d.width, 16);
		g.setColor(Color.GREEN);
		g.fillRect(d.width - ((frame) % (d.width*2)), d.height-38, d.width, 16);
		g.setColor(Color.CYAN);
		g.fillRect(((frame / 2) % (d.width*2+50))- 50 - d.width, d.height-36, d.width, 16);
		g.setColor(Color.YELLOW);
		g.fillRect(((frame * (3/2)) % (d.width*2+70))- 70 - d.width, d.height-34, d.width, 16);
	   	drawString(g, "Use the Q W [ ] keys on the keyboard to key in the arrows as they come by!", 20, d.width/2 - 350, d.height - 70);
		if(ch == 10 && intKeyOff == 1) {tcommand = 2; intKeyOff++;}
		if(ch == 27 && intKeyOff == 1)tcommand = 0;

	}
	public void playCurrentSong(Graphics g, int index){
		//Plays the music, sets notes and background of song selected
		try {currSong = songList.get(index).getSong();}
		catch (IOException ex) {}
		currMusic = songList.get(index).getMusic();
		currBack = songList.get(index).getBackCover();
		//Resets the values of the notes so they can be selected again
		if(songOnce == 1){
			for(int m = 0; m<keyOff.length; m++){
				keyOff[m] = false;
			}
			currMusic.play();
			frame = 0;
			songOnce = 0;
		}
		yposMove = (frame);
		//Draws all permenant images (eg. background, receiving arrows)		
		g.drawImage(currBack, 0, 0, d.width, d.height, this);
		g.drawImage(rec[0], (d.width/2) - (256)/2, arrowYPos, this);
		g.drawImage(rec[1], (d.width/2) - (256)/2+64, arrowYPos, this);
	   	g.drawImage(rec[2], (d.width/2) - (256)/2+128, arrowYPos, this);
	   	g.drawImage(rec[3], (d.width/2) - (256)/2+192, arrowYPos, this);
	   	//Starts the method to calculate the score
	    scoreAlgo(g);
	    g.setColor(Color.RED);
	    int length = 400;
	   	int temp = 0;
	   	for(int direction = 0; direction < currSong.size(); direction++){
	   		
	   		int xpos = (d.width/2) - (256)/2;
	   		int delay = currSong.get(direction).getDelay();
	   		if(direction == 0) temp = delay;
	   		else temp = currSong.get(direction).getDelay() - currSong.get(direction-1).getDelay() - 80;
	   		int ypos = yposMove-delay;
	   		Image arrow = null;
	   		
	   		//Moves the arrows and defines which arrows appear depending on type
	   		if(currSong.get(direction).getDirection().equals("L") && keyOff[direction] == false){
	   			if(temp <= 100) arrow = leftArrow[0];
	   			else if(temp > 100 && temp <= 200) arrow = leftArrow[1];
	   			else if(temp > 200 && temp <= 300) arrow = leftArrow[2];
	   			else arrow = leftArrow[3];
	   			g.drawImage(arrow, xpos, ypos, this);
	   		}
	   		else if(currSong.get(direction).getDirection().equals("D") && keyOff[direction] == false){
	   			if(temp <= 100) arrow = downArrow[0];
	   			else if(temp > 100 && temp <= 200) arrow = downArrow[1];
	   			else if(temp > 200 && temp <= 300) arrow = downArrow[2];
		   		else arrow = downArrow[3];
		   		g.drawImage(arrow, xpos+64, ypos, this);
	   		}
	   		else if(currSong.get(direction).getDirection().equals("U") && keyOff[direction] == false){
	   			if(temp <= 100) arrow = upArrow[0];
	   			else if(temp > 100 && temp <= 200) arrow = upArrow[1];
		   		else if(temp > 200 && temp <= 300) arrow = upArrow[2];
	   			else arrow = upArrow[3];
	   			g.drawImage(arrow, xpos+128, ypos, this);
	   		}
	   		else if(currSong.get(direction).getDirection().equals("R") && keyOff[direction] == false){
	   			if(temp <= 100) arrow = rightArrow[0];
	   			else if(temp > 100 && temp <= 200) arrow = rightArrow[1];
	   			else if(temp > 200 && temp <= 300) arrow = rightArrow[2];
	   			else arrow = rightArrow[3];
		   		g.drawImage(arrow, xpos+192, ypos, this);
	   		}
	   		//End of code for arrows appearing
	   		char arrowKey = currSong.get(direction).getDirection().charAt(0);
		   	songPress(g, arrowKey, ypos, direction);
		   	if(yposMove - currSong.get(direction).getDelay() > d.height+70 || keyOff[direction])g.drawImage(getImage("ProgressBar.JPG"), (d.width/2)-(length/2), 100, (length)*(direction+1)/currSong.size(), 10, this);
	   		//Ends the thread and song and makes choosing a new song possible
	   		if(yposMove - currSong.get(currSong.size()-1).getDelay() > d.height+70 || keyOff[keyOff.length-1]){
	   			currMusic.stop();
	   			backAnimator.suspend();
	  			drawString(g, "Press esc to go back to the selection screen", 20, d.width/2-200, d.height/2-10);
	  			if(ch == 27){score = 0; songOnce = 1; tcommand = 1; intKeyOff ++;}
	  		}
	   	}
	}
	//Method for determining if a key was pressed and if the correct key was pressed
	public void songPress (Graphics g, char key, int ypos, int index){
		if(key == 'L') key = 'Q'; if(key == 'D') key = 'W'; if(key == 'U') key = '['; if(key == 'R') key = ']';
		for(Character t: chDown){
			if(t == 'q' || t == 'Q')g.drawImage(recPress[0], (d.width/2) - (256)/2, arrowYPos, this);
			if(t == 'w' || t == 'W')g.drawImage(recPress[1], (d.width/2) - (256)/2+64, arrowYPos, this);
			if(t == '[')g.drawImage(recPress[2], (d.width/2) - (256)/2+128, arrowYPos, this);
			if(t == ']')g.drawImage(recPress[3], (d.width/2) - (256)/2+192, arrowYPos, this);
		}
		if((Character.toUpperCase(ch) == key || Character.toLowerCase(ch) == key ) && ypos>=arrowYPos-29 && ypos<arrowYPos-19 && keyOff[index] == false && intKeyOff == 1){
			score+=1;
			keyOff[index]= true;
			intKeyOff ++;
		}
		else if((Character.toUpperCase(ch) == key || Character.toLowerCase(ch) == key ) && ypos>=arrowYPos-19 && ypos<arrowYPos-9 && keyOff[index] == false && intKeyOff == 1){
			score+=3;
			keyOff[index]= true;
			intKeyOff ++;
		}
		else if((Character.toUpperCase(ch) == key || Character.toLowerCase(ch) == key ) && ypos>=arrowYPos-9 && ypos<arrowYPos+41 && keyOff[index] == false && intKeyOff == 1){
			score+=5;
			keyOff[index]= true;
			intKeyOff ++;
		}
		else if((Character.toUpperCase(ch) == key || Character.toLowerCase(ch) == key ) && ypos>=arrowYPos+41 && ypos<arrowYPos+61 && keyOff[index] == false && intKeyOff == 1){
			score+=3;
			keyOff[index]= true;
			intKeyOff ++;
		}
		else if((Character.toUpperCase(ch) == key || Character.toLowerCase(ch) == key ) && ypos>=arrowYPos+61 && ypos<arrowYPos+71 && keyOff[index] == false && intKeyOff == 1){
			score+=1;
			keyOff[index]= true;
			intKeyOff ++;
		}
    }
    public void writeHighScore(int index) throws IOException{
    	
    	try {
			String content = score+"";
 
			File file = new File("scores/"+songList.get(index).getName().substring(4));
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				try{
					file.createNewFile();
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(content);
					bw.close();
				}
				catch (AccessControlException ie) {}
			}
			else{
				Scanner scoreInput = new Scanner (new File (songList.get(index).getName()));
				int highScore = scoreInput.nextInt();
				if(highScore < score){
					try{
						file.createNewFile();
						FileWriter fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(content);
						bw.close();
					}
					catch (AccessControlException ie) {}
				}
			}
			
 
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void scoreAlgo(Graphics g){//score algorithm
    	int yPos = arrowYPos + 61+ 5;
    	int xPos = d.width/2 - 23;
		int c = score/100;
		int b = (score-(c*100))/10;
		int a = score-(b*10+c*100);
		if(c == 1){
			g.drawImage(numbers[1], xPos - 26, yPos, this);
		}
		if(c == 2){
			g.drawImage(numbers[2], xPos - 26, yPos, this);
		}
		if(c == 3){
			g.drawImage(numbers[3], xPos - 26, yPos, this);
		}
		if(c == 4){
			g.drawImage(numbers[4], xPos - 26, yPos, this);
		}
		if(c == 5){
			g.drawImage(numbers[5], xPos - 26, yPos, this);
		}
		if(c == 6){
			g.drawImage(numbers[6], xPos - 26, yPos, this);
		}
		if(c == 7){
			g.drawImage(numbers[7], xPos - 26, yPos, this);
		}
		if(c == 8){
			g.drawImage(numbers[8], xPos - 26, yPos, this);
		}
		if(c == 9){
			g.drawImage(numbers[9], xPos - 26, yPos, this);
		}
		if(b == 0 && c > 0){
			g.drawImage(numbers[0], xPos, yPos, this);
		}
		if(b == 1){
			g.drawImage(numbers[1], xPos, yPos, this);
		}
		if(b == 2){
			g.drawImage(numbers[2], xPos, yPos, this);
		}
		if(b == 3){
			g.drawImage(numbers[3], xPos, yPos, this);
		}
		if(b == 4){
			g.drawImage(numbers[4], xPos, yPos, this);
		}
		if(b == 5){
			g.drawImage(numbers[5], xPos, yPos, this);
		}
		if(b == 6){
			g.drawImage(numbers[6], xPos, yPos, this);
		}
		if(b == 7){
			g.drawImage(numbers[7], xPos, yPos, this);
		}
		if(b == 8){
			g.drawImage(numbers[8], xPos, yPos, this);
		}
		if(b == 9){
			g.drawImage(numbers[9], xPos, yPos, this);
		}
		if(a == 0){
			g.drawImage(numbers[0], xPos + 26, yPos, this);
		}
		if(a == 1){
			g.drawImage(numbers[1], xPos + 26, yPos, this);
		}
		if(a == 2){
			g.drawImage(numbers[2], xPos + 26, yPos, this);
		}
		if(a == 3){
			g.drawImage(numbers[3], xPos + 26, yPos, this);
		}
		if(a == 4){
			g.drawImage(numbers[4], xPos + 26, yPos, this);
		}
		if(a == 5){
			g.drawImage(numbers[5], xPos + 26, yPos, this);
		}
		if(a == 6){
			g.drawImage(numbers[6], xPos + 26, yPos, this);
		}
		if(a == 7){
			g.drawImage(numbers[7], xPos + 26, yPos, this);
		}
		if(a == 8){
			g.drawImage(numbers[8], xPos + 26, yPos, this);
		}
		if(a == 9){
			g.drawImage(numbers[9], xPos + 26, yPos, this);
		}
    }
    public void run(){
		long startTime = System.currentTimeMillis();
		while (Thread.currentThread() == backAnimator) {
	   		repaint();
			try {
				startTime += sleepTime;
				long currentTime = System.currentTimeMillis();
				Thread.sleep( Math.max(0, startTime - currentTime) );
	   		}
			catch (InterruptedException e) {
			}
			frame++;
		}
	}
	public void update(Graphics g) {//double buffering
	Dimension d = getSize();
	this.resize(800, 450);
		if ( (bufferGraphics == null)|| (d.width != dim.width) || (d.height != dim.height)) {
	    	dim = d;
	    	offscreen = createImage(d.width, d.height);
	    	bufferGraphics = offscreen.getGraphics();
		}
		bufferGraphics.setColor(getBackground());
		bufferGraphics.fillRect(0, 0, d.width, d.height);
		bufferGraphics.setColor(Color.black);
		paintFrame(bufferGraphics);
		g.drawImage(offscreen, 0, 0, null);
	}
	public void paint(Graphics g){
		update(g);
	}
	public void stop(){
		backAnimator = null;
		offscreen = null;
		bufferGraphics = null;
	}	
    public boolean keyDown(Event e, int key){//keyboard event
      	ch = (char) key;
      	intKeyOff ++;
      	if(chDown.indexOf((Character)ch) == -1
      		&& (ch == 'Q' || ch == 'W' || ch == '[' || ch == ']' || ch == 'q' || ch == 'w')) 
      			chDown.add(ch);
      	repaint();
      	return false;
    }
    public boolean keyUp(Event e, int key){//keyboard event
      	ch = 'z';
      	intKeyOff = 0;
      	try {if(!chDown.isEmpty()) chDown.remove(chDown.indexOf((Character)(char)key));}
      	catch (ArrayIndexOutOfBoundsException ie){}
      	repaint();
      	return false;
    }
}