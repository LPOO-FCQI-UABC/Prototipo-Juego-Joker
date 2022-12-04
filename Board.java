package gameLibrary;

import gameLibrary.Player;
import gameLibrary.Tool;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms

    private int delay;
    // controls the size of the board
    public static int tileSize;
    public static int rows;
    public static int columns;
    private int numTools;
    private int numRocks;
    private int pointsToWin;
    private int pointsAdded;
    private int pointsSubstracted;
    private Font font;
    private String welcomeText;

    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    // objects that appear on the game board
    //arraylist of players
    private ArrayList<Player> players;
    private Plant plant;
    private ArrayList<Tool> currentTools;
    private ArrayList<String> toolNames;
    private ArrayList<String> rockNames;
    private ArrayList<String> plantPhases;

    private int begin=0;
    public Board(int tileSize, int rows, int columns, int numTools, int numRocks, ArrayList<Player> players, Plant plant,ArrayList<String> plantPhases,String welcomeText,
                 ArrayList<String> toolNames,ArrayList<String> rockNames,int pointsToWin,int pointsAdded,int pointsSubtracted) {
        this.tileSize = tileSize;
        this.rows = rows;
        this.columns = columns;
        this.numTools = numTools;
        this.numRocks = numRocks;
        this.players = players;
        this.plant = plant;
        this.plantPhases=plantPhases;
        this.toolNames=toolNames;
        this.rockNames=rockNames;
        this.pointsToWin=pointsToWin;
        this.pointsAdded=pointsAdded;
        this.pointsSubstracted=pointsSubtracted;

        //initialize players
        // set the size of the board
        setPreferredSize(new Dimension(tileSize * columns, tileSize * rows));
        // set the game board background color
        setBackground(new Color(232, 232, 232));
        // setBackground(new Color(109, 135, 100));
        // initialize the game state
        if(begin==0){
            JOptionPane.showMessageDialog(null,welcomeText);
            begin+=1;
        }
        currentTools=populateTools(toolNames,rockNames,numTools,numRocks);
        //tools = populateTools(numTools,numRocks);

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(delay, this);
        timer.start();
    }

    public Board(int tileSize, int rows, int columns) {
        this.tileSize = tileSize;
        this.rows = rows;
        this.columns = columns;
        // set the size of the board
        setPreferredSize(new Dimension(tileSize * columns, tileSize * rows));
        // set the game board background color
        setBackground(new Color(232, 232, 232));
        if(begin==0){
            JOptionPane.showMessageDialog(null,welcomeText);
            begin+=1;
        }
        currentTools=populateTools(toolNames,rockNames,numTools,numRocks);
        // initialize the game state
        timer = new Timer(delay, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.

        // prevent the player from disappearing off the board
        // player.tick();
        for (Player p:players){
            p.tick();
        }
        // give the player points for collecting coins
        collectTools();
        //check state of game
        checkState();

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver
        // because Component implements the ImageObserver interface, and JPanel
        // extends from Component. So "this" Board instance, as a Component, can
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        drawBackground(g);
        drawScore(g);
        for (Tool tool : currentTools) {
            tool.draw(g, this);
        }
        for (Player p:players){
            p.draw(g,this);
        }
        // player.draw(g, this);
        plant.draw(g,this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        for(Player p:players){
            p.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    public void drawBackground(Graphics g) {//antes private
        // draw a checkered background
        // g.setColor(new Color(144, 238, 144));
        g.setColor(new Color(214, 214, 214));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // only color every other tile
                if ((row + col) % 2 == 1) {
                    // draw a square tile at the current row/column position
                    g.fillRect(
                            col * tileSize,
                            row * tileSize,
                            tileSize, tileSize
                    );
                }
            }
        }
    }

    public void drawScore(Graphics g) {//antes private
        // set the text to be displayed
        String text = "TOTAL: "+Integer.toString(getTotalScore())+" points";
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(30, 201, 139));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, tileSize * (rows - 1), tileSize * columns, tileSize);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }
    public ArrayList<Tool> populateTools(ArrayList<String> toolNames,ArrayList<String> rockNames,int numTools,int numRocks) {//antes private
        ArrayList<Tool> tools = new ArrayList<>();
        ArrayList<Element> elements = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Point> points = new ArrayList<>();
        Random rand = new Random();
        int randomIndex ;
        String tool;

        names.addAll(toolNames);
        names.addAll(rockNames);

        for(Player p:players){
            elements.add(p);
        }

        for (int i = 0; i < numRocks+numTools; i++) {
            int posX = rand.nextInt(columns);
            int posY = rand.nextInt(rows);

            if(i<numTools){
                randomIndex = rand.nextInt(toolNames.size());
                tool= toolNames.get(randomIndex);
            }
            else{
                randomIndex = rand.nextInt(rockNames.size());
                tool = rockNames.get(randomIndex);
            }
            //check for repeating points
            // (que no se repitea con la posicion de jugdores, la planta, otros elementos ni con los puntos ya creados)
            while (points.contains(new Point(posX, posY))|| (posX==plant.getX() && posY==plant.getY())
                    || posX==rows && posY==columns/2|| posX==rows+1 && posY==columns/2) {
                posX = rand.nextInt(columns);
                posY = rand.nextInt(rows);
            }
            points.add(new Point(posX, posY));
            Tool t = new Tool(posX, posY, tool);
            tools.add(t);
        }
        return tools;
    }



    public void collectTools() {//antes private
        // allow player to pickup coins
        ArrayList<Tool> collectedTools= new ArrayList<>();
        boolean add=true;
        for (Tool tool : currentTools) {
            // if the player is on the same tile as a coin, collect it
            for(Player p:players){
                if (p.getPos().equals(tool.getPos())) {
                    // give the player some points for picking this up
                    //check for tool type
                    for(String r:rockNames){
                        if(tool.getType().equals(r)){
                            add=false;
                            break;
                        }
                    }
                    if(add==false){
                        p.addScore(pointsSubstracted);
                    }else {
                        p.addScore(pointsAdded);
                    }
                    //change image of plant
                    growingPlant();
                    collectedTools.add(tool);
                }
            }
        }
        // remove collected coins from the board
        currentTools.removeAll(collectedTools);

    }
    public void growingPlant(){//antes private
        int score=getTotalScore();
        int size=plantPhases.size();
        for(int i=1;i<=size;i++){
            int r1=(pointsToWin*(i))/(size-1);
            int r2=(pointsToWin*(i+1))/(size-1);
            if((score>=r1 && score<r2) || (score==pointsToWin && i==size)){
                plant.setImage(plantPhases.get(i-1));
            }
        }
    }
    public int getTotalScore(){//antes private
        int score=0;
        for (int i=0;i<players.size();i++){
            score+=Integer.parseInt(players.get(i).getScore());
        }
        return score;
    }

    public void checkState(){//antes private
        //check if there are no more rocks
        int rocks=0,numTools=0;
        for (Tool tool:currentTools){
            for(String s:rockNames){
                if(tool.getType().equals(s)){
                    rocks++;
                }
            }
            for(String s:toolNames){
                if(tool.getType().equals(s)){
                    numTools++;
                }
            }
        }
        if(rocks==0){
            JOptionPane.showMessageDialog(null,"....You collected all the wrong elements!You both lost \nFinal Score: "+getTotalScore()+"\nGame Over");
            System.exit(0);
        }
        if(numTools==0 && getTotalScore()<pointsToWin/2){
            JOptionPane.showMessageDialog(null,"YOU BOTH LOST! \nFinal Score: "+getTotalScore()+"\nGame Over.");
            System.exit(0);
        }
        if(getTotalScore()==pointsToWin|| (getTotalScore()>pointsToWin/2 && numTools==0)){
            JOptionPane.showMessageDialog(null," CONGRATULATIONS YOU BOTH WON! \n Good Job Team\n Final Score: "+getTotalScore()+"\nGame Over");
            System.exit(0);
        }
    }
    //getters and setters
    public int getColumns() {
        return columns;
    }
    public void setColumns(int columns) {
        this.columns = columns;
    }
    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    public int getTileSize() {
        return tileSize;
    }
    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public ArrayList<Tool> getCurrentTools() {
        return currentTools;
    }
    public void setCurrentTools(ArrayList<Tool> currentTools) {
        this.currentTools = currentTools;
    }
    public Plant getPlant() {
        return plant;
    }
    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    public int getPointsAdded() {
        return pointsAdded;
    }
    public void setPointsAdded(int pointsAdded) {
        this.pointsAdded = pointsAdded;
    }
    public int getPointsSubstracted() {
        return pointsSubstracted;
    }
    public void setPointsSubstracted(int pointsSubstracted) {
        this.pointsSubstracted = pointsSubstracted;
    }
    public int getNumTools() {
        return numTools;
    }
    public void setNumTools(int numTools) {
        this.numTools = numTools;
    }
    public int getNumRocks() {
        return numRocks;
    }
    public void setNumRocks(int numRocks) {
        this.numRocks = numRocks;
    }
    public int getPointsToWin() {
        return pointsToWin;
    }
    public void setPointsToWin(int pointsToWin) {
        this.pointsToWin = pointsToWin;
    }
    public ArrayList<String> getToolNames() {
        return toolNames;
    }
    public void setToolNames(ArrayList<String> toolNames) {
        this.toolNames = toolNames;
    }
    public ArrayList<String> getRockNames() {
        return rockNames;
    }
    public void setRockNames(ArrayList<String> rockNames) {
        this.rockNames = rockNames;
    }
    public ArrayList<String> getPlantPhases() {
        return plantPhases;
    }
    public void setPlantPhases(ArrayList<String> plantPhases) {
        this.plantPhases = plantPhases;
    }
    public int getDelay() {
        return delay;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public Font getFont() {
        return font;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public String getWelcomeText() {
        return welcomeText;
    }
    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

}