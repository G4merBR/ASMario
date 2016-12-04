import org.newdawn.slick.Color;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Principal extends BasicGame
{

	java.awt.Color color;
	ArrayList<Tile> elements,selected,tileclipboard;
	Stack<Tile> undo;
	boolean isErasing,eraseToggle,isDragging;
	int size,brushsize,xinitselection,yinitselection,count,generateSize;
	float scale;
	float offset;
	float offset_final;
	
	int xc,yc,collision,collisions;
	public class Tile{
		private int x,y,w,h;
		private Color c,originalcolor;
		public int collision;
		public Tile(int x,int y,int w,int h,Color c,int collision){
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
			this.c=c;
			this.originalcolor=c;
			this.collision=collision;
			
		}
		
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getW() {
			return w;
		}

		public void setW(int w) {
			this.w = w;
		}

		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}

		public Color getC() {
			return c;
		}
		public Color getOC() {
			return this.originalcolor;
		}

		public void setC(Color c) {
			this.c = c;
			this.originalcolor=c;
		}
		public void select(){
			this.c=new Color(this.originalcolor.r*1.9f,this.originalcolor.g*0.4f,this.originalcolor.b*0.4f,0.9f);

		}
		public void deselect(){
			this.c=this.originalcolor;
		}

		
	}
	public class customComparator implements Comparator<Tile>{

		@Override
		public int compare(Tile o1, Tile o2) {
		
			return o2.x-o1.x;
		}
		
	}
	public Principal(String gamename)
	{
		
		super(gamename);
		generateSize=13;
		color=new java.awt.Color(1f,1f,1f);
		elements=new ArrayList<Tile>();
		selected=new ArrayList<Tile>();
		tileclipboard=new ArrayList<Tile>();
		undo= new Stack<Tile>();
		isErasing=false;
		count=0;
		isDragging=false;
		offset=0;	
		size=10;
		eraseToggle=false;
		collision=1;
		//Conversao
		//320 escala padrao
		scale=320;
		offset_final=1f;
		brushsize=0;
		collisions=4;
		
	}

	@Override
	public void init(GameContainer gc) throws SlickException {}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}
	public void addTile(int xpos,int ypos,Color brushcolor,int collision){
		boolean existe=false;
		
		for(int i=0;i<elements.size();i++){
			Tile element=elements.get(i);
			if(element.getX()==xpos&&element.getY()==ypos){
				element.setC(brushcolor);
				element.collision=collision;
				existe=true;
				break;
			}
		}
		if(!existe){
			Tile t=new Tile(xpos, ypos, size, size,brushcolor,collision);
			undo.push(t);
			elements.add(t);
			
			//elements.sort(new customComparator());
		}
	}
	public boolean removeTile(Tile element){
		undo.push(element);
		return elements.remove(element);

	}
	public void removeTile(int element){
		undo.push(elements.get(element));
		

	}
	public String geraCodigo(){
		String Final="";
		for(int i=0;i<elements.size();i++){
			Tile element=elements.get(i);
			Color cor=element.getC();
			if(i!=0)
				Final+="\n";
		
			Final+="GameObject "+((element.getX()/scale)-offset_final)+","+(((-element.getY()/scale)+offset_final-0.03f))+","+element.getW()/scale+","+element.getH()/scale+","+(element.collision)+","+cor.getRed()/255f+","+cor.getGreen()/255f+","+cor.getBlue()/255f;
		}
		Final+="\nchaosize dd "+elements.size();
		return Final;
	}
	public void carregaCodigo(String load,int foffset){
		load=load.replaceAll("GameObject ", "");
		String[] toLoad=load.split("\n");
		for(int i=0;i<toLoad.length-1;i++){
			
			String[] elemento= toLoad[i].split(",");
			int tilex=(int)(Math.floor((Float.parseFloat(elemento[0])+offset_final)*scale-(int)foffset)/(int)size)*((int) size);
			int tiley=(int)-(Math.floor((Float.parseFloat(elemento[1])-offset_final+0.03f)*scale)/(int)size)*((int) size);
	
			
			addTile(tilex,tiley,new Color(Float.parseFloat(elemento[5]),Float.parseFloat(elemento[6]),Float.parseFloat(elemento[7])),Integer.parseInt(elemento[4]));
		}
		//System.out.print(load);
	}
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.setBackground(new Color(0.56f,0.72f,1.0f));
		Input input = gc.getInput();
		
		int xpos = (int)((Math.floor(input.getMouseX()/(int)size)*(int)size));
		int ypos = (int)((Math.floor(input.getMouseY()/(int)size)*(int)size));
		
		if (input.isMousePressed(1)) {
			java.awt.Color newColor = JColorChooser.showDialog(null,"Selecione a Cor",color);
			if(newColor!=null)
				color=newColor;
		}
		Color brushcolor=new Color(color.getRed(),color.getGreen(),color.getBlue());
		
		//Desenha Mapa
		for(int i=0;i<elements.size();i++){
			Tile element=elements.get(i);
			g.setColor(element.getC());
			g.fillRect(element.getX()+offset, element.getY(), element.getW(), element.getH());	
			if(element.collision>0){
				g.setColor(new Color(1.0f,0.0f,0.0f));
				g.drawRect(element.getX()+offset,  element.getY(),element.getW(), element.getH());
				g.drawString(""+element.collision, element.getX()+offset,  element.getY()-3);
			}
		}	

		
		if(input.isKeyDown(Input.KEY_LCONTROL)){
			//Muda Escala
			if (input.isKeyPressed(Input.KEY_SUBTRACT)){
				scale+=10;
			}
			if (input.isKeyPressed(Input.KEY_ADD)){
				scale-=10;
			}
			//Limpa Cena	
			if (input.isKeyDown(Input.KEY_E))
				elements=new ArrayList<Tile>();
			//Desloca
			if (input.isKeyPressed(Input.KEY_UP)){
				if(selected.isEmpty()){
					for(int i=0;i<elements.size();i++){
						Tile element=elements.get(i);
						element.setY(element.getY()-size);		
					}
				}else{
					for(int i=0;i<selected.size();i++){
						Tile element=selected.get(i);
						element.setY(element.getY()-size);		
					}
				}
			}
			if (input.isKeyPressed(Input.KEY_DOWN)){
				if(selected.isEmpty()){
					for(int i=0;i<elements.size();i++){
						Tile element=elements.get(i);
						element.setY(element.getY()+size);		
					}
				}else{
					for(int i=0;i<selected.size();i++){
						Tile element=selected.get(i);
						element.setY(element.getY()+size);		
					}
				}	
			}
			
			if (input.isKeyPressed(Input.KEY_LEFT)){
				if(selected.isEmpty()){
					for(int i=0;i<elements.size();i++){
						Tile element=elements.get(i);
						element.setX(element.getX()-size);		
					}
				}else{
					for(int i=0;i<selected.size();i++){
						Tile element=selected.get(i);
						element.setX(element.getX()-size);		
					}
				}	
			
			}
			if (input.isKeyPressed(Input.KEY_RIGHT)){
				if(selected.isEmpty()){
					for(int i=0;i<elements.size();i++){
						Tile element=elements.get(i);
						element.setX(element.getX()+size);		
					}
				}else{
					for(int i=0;i<selected.size();i++){
						Tile element=selected.get(i);
						element.setX(element.getX()+size);		
					}
				}
			}
			if (input.isMousePressed(0)) {
				
					xinitselection=xpos;
					yinitselection=ypos;
	
			}
			if(input.isKeyPressed(Input.KEY_C)){
				Tile ref=selected.get(0);
				int x,y;
				x=ref.getX();
				y=ref.getY();
				for(int i=0;i<selected.size();i++){
					Tile element= selected.get(i);
					tileclipboard.add(new Tile(-x+element.getX()+(int)offset,(-y+element.getY()),element.getH(),element.getW(),element.getOC(),element.collision));
				}
			}
			if(input.isKeyPressed(Input.KEY_V)){
				for(int i=0;i<tileclipboard.size();i++){
					Tile element= tileclipboard.get(i);
					addTile((int) (element.getX()+xpos-offset),element.getY()+ypos,element.getC(),element.collision);
				}
			}
			if(input.isKeyPressed(Input.KEY_Z)){
				Tile element=undo.pop();
				if(!elements.remove(element))
					elements.add(element);
				
			}
			//Gerador de Cenarios
			if(input.isKeyPressed(Input.KEY_S)){
				File folder = new File("structures/");
				File[] listOfFiles = folder.listFiles();
				
				PrintWriter writer;
				try {
					writer = new PrintWriter("structures/"+(listOfFiles.length-2)+".joe", "UTF-8");
					writer.write(geraCodigo());
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			if(input.isKeyPressed(Input.KEY_G)){
	
				File folder = new File("structures/");
				File[] listOfFiles = folder.listFiles();
				for(int i=0;i<listOfFiles.length;i++){
					
					int rand=i-1;
					if(i==(listOfFiles.length)-2)
						rand=-2;
					if(i==(listOfFiles.length)-1)
						rand=i-2;
					
					//int rand=ThreadLocalRandom.current().nextInt(0, listOfFiles.length-2 );
					/*if(i==0){
						rand=-1;
					}
					if(i==1){
						rand=0;
					}
					if(i==(generateSize)-2){
						rand=-2; 
					}*/
					
					try(BufferedReader br = new BufferedReader(new FileReader("structures/"+rand+".joe"))) {
					    StringBuilder sb = new StringBuilder();
					    String line = br.readLine();
	
					    while (line != null) {
					        sb.append(line);
					        sb.append(System.lineSeparator());
					        line = br.readLine();
					    }
					    carregaCodigo(sb.toString(),+i*-640);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (input.isMouseButtonDown(0)) {
				selected.clear();
				g.setColor(new Color(1.0f,1.0f,1.0f,0.6f));	
				g.drawRect(xinitselection, yinitselection, xpos-xinitselection, ypos-yinitselection);

					for(int k=0;k<elements.size();k++){
						Tile element=elements.get(k);
						if(element.getX()+size>=(xinitselection)&& xinitselection+(xpos-offset-xinitselection)-size>=element.getX() &&element.getY()+size>=yinitselection && yinitselection+(ypos-yinitselection)-size>=element.getY()){
							element.select();
							selected.add(element);
				
						}else if(!selected.contains(element)){
							element.deselect();
						}
				}


			}
		}else{

			if(input.isKeyPressed(Input.KEY_I)){
				try {
					 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					 List<File>  arquivos=(List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
					BufferedImage image=ImageIO.read( arquivos.get(0) );

					for (int y = 0; y < image.getHeight(); y++) {
					    for (int x = 0; x < image.getWidth(); x++) {
				
					          int  clr   = image.getRGB(x, y); 
					          int  red   = (clr & 0x00ff0000) >> 16;
					          int  green = (clr & 0x0000ff00) >> 8;
					          int  blue  =  clr & 0x000000ff;
					    
					          addTile((int)(xpos-offset)+x*size,ypos+y*size,new Color(red,green,blue),0);
					    }
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
			if (input.isKeyPressed(Input.KEY_DELETE)) {
				for(int k=0;k<selected.size();k++){
					Tile element=selected.get(k);
					removeTile(element);
				}
				
			}
			if (input.isMouseButtonDown(0)) {
				if(selected.isEmpty()){
					//Novo Tile
					if(isErasing){
						for(int i=0;i<(brushsize+size)/size;i++){
							for(int j=0;j<(brushsize+size)/size;j++){
								for(int k=0;k<elements.size();k++){
									Tile element=elements.get(k);
									if(element.getX()==((xpos-offset)+i*size)&&element.getY()==(ypos+j*size)){
										removeTile(element);
										break;
									}
								}
							}
						}
					}else{
					
						for(int i=0;i<(brushsize+size)/size;i++)
							for(int j=0;j<(brushsize+size)/size;j++)
								addTile((int)(xpos-offset)+i*size,ypos+j*size,brushcolor,collision);
					}
				}else{
					if(!isDragging){
						boolean fora=true;
					
						for(int k=0;k<selected.size();k++){
							Tile element=selected.get(k);
							if(element.getX()==((xpos-offset))&&element.getY()==(ypos)){
								fora=false;
								xc=element.getX();
								yc=element.getY();
		
							}
						}
						if(fora){
							for(int k=0;k<selected.size();k++){
								Tile element=selected.get(k);
								element.deselect();
							}
							selected= new ArrayList<Tile>();
						}else{
							isDragging=true;
						}
					
					}else{
					/*
						for(int k=0;k<selected.size();k++){
							Tile element=selected.get(k);
							
							
							//int newx=xpos+(-xc+element.getX()+(int)offset) ;
							//int newy=ypos+(-yc+element.getY()) ;	
							//System.out.println(newx+" "+newy);
							element.setX(element.getX()+((xpos-xc)));
							//element.setY(newy);
					
						}*/
					}
				}
			}else if(isDragging){
				isDragging=false;
			}
			//Liga/Desliga Borracha
			if (input.isKeyPressed(14)){ 
				isErasing=!isErasing;
				eraseToggle=true;
			}
			if(input.isKeyDown(Input.KEY_E)){
				isErasing=true;
				eraseToggle=false;
			}else if(!eraseToggle)
				isErasing=false;

			
			//Gera Codigo
			if (input.isKeyPressed(Input.KEY_S)){
				String Final=geraCodigo();
				StringSelection selection = new StringSelection(Final);
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			
			} 
			//Carrega Codigo	
			if (input.isKeyPressed(Input.KEY_L)){
				
				 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				 
				 try {
					 String load= (String) clipboard.getData(DataFlavor.stringFlavor);
					 carregaCodigo(load,(int)offset);
					
				} catch (UnsupportedFlavorException | IOException e) {
					
					e.printStackTrace();
				} 
			}
			//Colisao
			if (input.isKeyPressed(Input.KEY_X)){
				collision=(collision+1)%(collisions+1);
			}
			//Pega Cor
			if (input.isKeyPressed(Input.KEY_C)){
				for(int i=0;i<elements.size();i++){
					Tile element=elements.get(i);
					if(element.getX()==(xpos-offset)&&element.getY()==(ypos)){
						color=new java.awt.Color(element.getC().getRed(),element.getC().getGreen(),element.getC().getBlue());
						
					}
				}
			}
			//Move cena
			if (input.isKeyDown(Input.KEY_RIGHT)){
				if(count>=10){
					offset-=size;
					count=0;
				}
				count++;
			}
			else if(input.isKeyDown(Input.KEY_LEFT)){
				//if(offset<0){
					if(count>=10){
					offset+=size;
					count=0;
					}	
					count++;
				//}
			}
			else
				count=0;
			//Aumenta brush
			if (input.isKeyPressed(Input.KEY_ADD))
				brushsize+=size;
			if(input.isKeyPressed(Input.KEY_SUBTRACT))
				if(brushsize>0)
					brushsize-=size;
		}



		//Desenha Brush
		if(isErasing)
			brushcolor=new Color(0.56f,0.72f,1.0f);
		g.setColor(brushcolor);
		g.fillRect(xpos, ypos, size+brushsize, size+brushsize);
		if(collision>0 || isErasing)
			g.setColor(new Color(1.0f,0.0f,0.0f));
			g.drawRect(xpos, ypos, size+brushsize, size+brushsize);
		
		g.setColor(new Color(1.0f,0.0f,0.0f,0.5f));	
		g.drawString("\nx:"+xpos+" y:"+ypos+" colisao:"+collision, xpos, ypos);
		g.drawString("\nasm_x:"+((xpos/scale)-offset_final)+" asm_y:"+((-ypos/scale)+offset_final-0.03f), xpos, ypos+20);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Principal("Resource Editor"));
			appgc.setDisplayMode(640, 640, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}