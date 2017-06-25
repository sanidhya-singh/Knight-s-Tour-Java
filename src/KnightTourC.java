import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;


/*****************************************************************************
 *                     JAVA Program to solve Knight Tour                     *
 *                      Author : Sanidhya Pratap Singh                       *
 *                             SNU ID : AAA0403                              *                                                                         
 *****************************************************************************/



class KnightTourC extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
                   
                    //2 possible move combinations for the knight
                    private final static int[][] MOVES = {{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
                    private final static int[][] MOVES2 = {{-2,-1},{-2,1},{2,-1},{2,1},{-1,-2},{-1,2},{1,-2},{1,2}};
                    
                    
                    private static boolean sides = false;           //determine if source is near sides or center
                    private static boolean center = false;
                    
                    private static int[][] theBoard;                //represents the chess board
                    static int boardLength = 0;                     //stores the board length (rows)
                    static int boardWidth = 0;                      //stores the board width (columns)
                    static int[][] buttonCenters;                   //stores the center of the buttons
                    static boolean boardPainted = false;            //boolean variable to determine whether the board is painted with lines
                    
                    //GUI elements
                    private JButton[][] p = new JButton[400][400]; 
                    private JButton clear = new JButton("Clear");
                    private JTextArea field = new JTextArea(8,20);
                    private JButton trace = new JButton("TraceRoute");
                    private JPanel bigPanel = new JPanel();
                    private JButton compute = new JButton("Compute");
                    private JTextField rowField = new JTextField(3);
                    private JTextField colField = new JTextField(3);
                    private JLabel rowLabel = new JLabel("Enter Row : ");
                    private JLabel colLabel = new JLabel("Enter Col : ");
                    
                    public KnightTourC(int a, int b) {
                                            //KnightTourC constructor to create the GUI
                                            boardLength = a;
                                            boardWidth = b;
                                            theBoard = new int[boardLength][boardWidth];                                            
                                            buttonCenters = new int[boardLength*boardWidth][2];
                                            field.setEditable(false);
                                            field.setBorder(new TitledBorder("Output"));
                                            field.setForeground(Color.BLUE);
                                            bigPanel.setLayout(new GridLayout(boardLength,boardWidth));
                                            clear.setBackground(Color.black);
                                            clear.setForeground(Color.yellow);
                                            trace.setBackground(Color.black);
                                            trace.setForeground(Color.yellow);
                                            compute.setBackground(Color.black);
                                            compute.setForeground(Color.yellow);
                                            setTitle("Board");
                                            
                                            //adding buttons to the frame and setting their color
                                            for(int i=0; i<boardLength; i++) {
                                                for(int j=0; j<boardWidth; j++) {
                                                    JButton temp = new JButton();
                                                    temp.setPreferredSize(new Dimension(50,50));
                                                    temp.setVisible(true);
                                                    temp.setLayout(new FlowLayout(FlowLayout.CENTER));
                                                    if((i+j) % 2 == 0)
                                                        temp.setBackground(Color.DARK_GRAY);
                                                    else
                                                        temp.setBackground(Color.black);
                                                    p[i][j] = temp;
                                                    p[i][j].setForeground(Color.yellow);
                                                    bigPanel.add(p[i][j]);                
                                                }
                                            }                                            
                                            add(bigPanel);
                                            
                                            //Adding the text area to a scroll panel to enable a vertical scrolling bar
                                            //when needed
                                            JScrollPane scroll = new JScrollPane(field);
                                            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                                            add(scroll); 
                                            JPanel tempPanel = new JPanel();
                                            tempPanel.add(rowLabel);
                                            tempPanel.add(rowField);
                                            tempPanel.add(colLabel);
                                            tempPanel.add(colField);
                                            tempPanel.add(compute);
                                            tempPanel.add(clear);
                                            tempPanel.add(trace);
                                            tempPanel.setLayout(new GridLayout(4,2));
                                            add(tempPanel);
                                            setLayout(new FlowLayout(FlowLayout.LEFT));
                                            setVisible(true);
                                            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                            
                                            //add mouse listeners on the buttons
                                            for(int i=0; i<boardLength; i++) {
                                                for(int j=0; j<boardWidth; j++) {
                                                    p[i][j].addMouseListener(this);
                                                    p[i][j].addMouseMotionListener(this);
                                                }
                                            }
                                            trace.setEnabled(false);
                                            
                                            //adding Listeners to the buttons, panels
                                            trace.addActionListener(this);
                                            clear.addActionListener(this);
                                            bigPanel.addMouseMotionListener(this);
                                            bigPanel.addMouseListener(this);
                                            compute.addActionListener(this);
                                            
                                            //enable text wrapping on the text area
                                            field.setLineWrap(true);
                                            
                                            //pack the frame
                                            pack();
                    }
                    
                    
                    //main function
                    public static void main (String[] str) {
                                    String size = JOptionPane.showInputDialog(null, "Enter the size of the chess board (eg. 8x8)");
                                    String[] split = size.split("x");
                                    int m = Integer.parseInt(split[0]);
                                    int n = Integer.parseInt(split[1]);
                                    JFrame frame = new KnightTourC(m, n);
                    }     
                    
                    
                    public void initialMove(int sRow, int sCol) {
                        theBoard = new int[boardLength][boardWidth];
                        
                        //Make all of the board -1 because we have not visited any square
                        for (int r=0; r<boardLength; r++) {
                            for (int c=0; c<boardWidth; c++) {
                                theBoard[r][c] = -1;
                            }
                        }
                        
                        //Determine whether the starting position is in the sides or the center
                        sidesOrCenter(sRow, sCol);
                        
                        //Set the starting location to 1 to denote first move
                        theBoard[sRow][sCol] = 1;
                        
                        //Call to recursive function to solve the Knight Tour
                        if (knightTour(sRow, sCol, 2)) {
                            printBoardOnButtons();
                        }
                        
                        //If Knight Tour is not possible, alert user of the same
                        else {
                            JOptionPane.showMessageDialog(null, "No Solution!");
                        }
                    }
                    
                    private boolean knightTour(int row, int col, int moveNumber) {
                        //Check if move number has exceeded number of possible moves
                        if (moveNumber > boardLength * boardWidth) {
                            return true;
                        }
                        
                        //Recursively call the knightTour function on all possible move combinations for the current position
                        for (int i=0; i<8; i++) {
                            int tempRow = 0;
                            int tempCol = 0;
                            
                            //If starting location is near the sides
                            if(sides) {
                                    tempRow = row + MOVES[i][0];
                                    tempCol = col + MOVES[i][1];
                            }
                            
                            //If starting location is farther away from the edges
                            else if(center) {
                                    tempRow = row + MOVES2[i][0];
                                    tempCol = col + MOVES2[i][1];
                            }
                            
                            //Checking if it is possible to move to the iterated position
                            if (isPossibleToMove(tempRow, tempCol)) {
                                
                                //Set moved postion to move number
                                theBoard[tempRow][tempCol] = moveNumber;
                                
                                //Increment move number by 1
                                moveNumber = moveNumber + 1;
                                
                                //Call knightTour function on new pair of row and columns
                                if (knightTour(tempRow, tempCol, moveNumber)) {
                                    return true;
                                }                                
                                //If the solution on new pair turns out to be wrong, we must backtrack and try other positions
                                //set the current board value to -1 (denote unvisited) and decrease move number
                                else {
                                    //printBoardOnTerminal();
                                    theBoard[tempRow][tempCol] = -1;
                                    moveNumber = moveNumber - 1;
                                }
                            }
                        }
                        return false;
                    }
                    
                    private boolean isPossibleToMove(int row, int col) {
                                //Function to determine if it is possible to move to passed location
                                return ((row >= 0 && row < boardLength) && (col >= 0 && col < boardWidth) && (theBoard[row][col] == -1));
                    }
                    
                    public void printBoardOnButtons() {
                                            //Function to print the board values on the corresponding buttons
                                            for (int i=0; i < boardLength ; i++){
                                                for (int j=0; j < boardWidth ; j++){
                                                    p[i][j].setText(String.valueOf(theBoard[i][j]));
                                                }                                                
                                            }
                    }
                    public void printBoardOnTerminal() {
                                            //Function to print the board on the terminal
                                            //Can be used to show backtracking on the terminal
                                            System.out.print("\n\nBacktracked : \n");
                                            for(int i=0; i<boardLength; i++) {
                                                for(int j=0; j<boardWidth; j++) {
                                                    System.out.print(theBoard[i][j] + " ");
                                                }
                                                System.out.println();
                                            }                                            
                    }
                    
                    public void sidesOrCenter(int i, int j) {
                                            //Function to determine if the passed location is near the edges of the board
                                            //or farther away from the edges
                                            if(i<=2 || j<=2 || (i<=boardLength-1 && i>=boardLength-3) || (j<=boardWidth-1 && j>=boardWidth-2)) {
                                                sides = true;
                                                center = false;
                                            }
                                            else 
                                            {    center = true;
                                                 sides = false;
                                            }
                    }                    
 
                    
                    public int getSizeOfBoard() {
                                            //Function returns size of the board
                                            return boardLength * boardWidth;
                    }
                    
                    
                    public void calculateTrace() {
                                            //Function to calculate the trace of the knight tour
                                            //and calculate the button centers of the corresponding trace at the same time
                                            int check = 1;
                                            String trace = "\n\nTrace : \n";         
                                            while(true) {
                                                for(int i=0; i<boardLength; i++) {
                                                    for(int j=0; j<boardWidth; j++) {
                                                        if(theBoard[i][j] == check) {
                                                            
                                                            //Calculating the button centers
                                                            buttonCenters[check-1][0] = j*50 + 36;
                                                            buttonCenters[check-1][1] = i*50 + 62;
                                                            
                                                            //Adding trace
                                                            trace += "(" + (i+1) + "," + (j+1) + ")";
                                                            if(check < (boardLength) * (boardWidth))
                                                                trace += " --> ";
                                                            check++;
                                                        }
                                                    }
                                                }
                                                if(check > boardWidth * boardLength)
                                                break;
                                            }
                                            String temp = field.getText();
                                            temp += trace;
                                            
                                            //The final trace is displayed in the text area
                                            field.setText(temp);
                                            
                                            //Once the button centers have been calculated, the lines are drawn
                                            drawLines();                                            
                    }                    
                    public void drawLines() { 
                        //Function to draw the lines on the Board in the order of the move number 1->2->3...
                        Graphics g = getGraphics();                        
                        for(int i=0; i<boardWidth*boardLength-1; i++) {
                            
                            //Induce delay in between drawing each line
                            if(!boardPainted) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                Logger.getLogger(KnightTourC.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            //Draw red circles at the starting and final position
                            if(i == 0)
                            {   g.setColor(Color.red);
                                g.drawOval(buttonCenters[i][0] - 14, buttonCenters[i][1] - 14, 30, 30);
                            }
                            if(i == boardWidth*boardLength - 2) {
                                   g.setColor(Color.red);
                                g.drawOval(buttonCenters[i+1][0] - 14, buttonCenters[i+1][1] - 14, 30, 30);
                            }
                            
                            //Draw gree lines
                            g.setColor(Color.green);
                            g.drawLine(buttonCenters[i][0], buttonCenters[i][1], buttonCenters[i+1][0], buttonCenters[i+1][1]);
                        }
                    }
                    
                    public void disableClick() {
                        //Function to disable the buttons in the JFrame
                        for(int i=0; i<boardWidth; i++) {
                            for(int j=0; j<boardLength; j++) {
                                p[i][j].setEnabled(false);
                            }
                        }
                    }
                    
                    
                    private void reset() {
                        //Function to reset the board and the text area and drawn lines
                        for (int r=0; r<boardLength; r++) {
                            for (int c=0; c<boardWidth; c++) {
                                p[r][c].setText(""); 
                                theBoard[r][c] = -1;
                            }
                        }
                        
                        //Reset all text areas and fields, reset the board, diable trace button
                        boardPainted = false;
                        field.setText("");
                        trace.setEnabled(false);
                        rowField.setText("");
                        colField.setText("");
                    }
                    
                   
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Overriden actionPerformed for the 2 buttons
                        //clicking trace prints the trace in text area and draws lines
                        if(e.getSource() == trace) {
                            calculateTrace();
                            boardPainted = true;
                        }
                        else if(e.getSource() == clear) {
                            reset();
                        }
                        else if(e.getSource() == compute) {
                            //Event listener to compute KnightTour on input from text field
                            try {
                                int x = Integer.parseInt(rowField.getText());
                                int y = Integer.parseInt(colField.getText());
                            
                                //Determine is source input is valid
                                if(x>0 && y>0 && x<=boardLength && y<=boardWidth) {                               
                                    reset();
                                    trace.setEnabled(true);
                                    field.setText("Board : " + boardLength + "x" + boardWidth + "\n\nSource : (" + (x) + "," + (y) + ")");
                                    rowField.setText(String.valueOf(x));
                                    colField.setText(String.valueOf(y));
                                    initialMove(x-1, y-1);      
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "Invalid source!");
                                }
                            } catch (NumberFormatException exp) { JOptionPane.showMessageDialog(null, "Input not in correct format");}
                              
                        }
                    }
                    
                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //MouseListener to allow the user to pass the starting location by clicking on
                        //the desired button
                        int[] position = new int[2];
                        for(int i=0; i<boardLength; i++) {
                            for(int j=0; j<boardWidth; j++) {
                                if(e.getSource() == p[i][j]) {   
                                    reset();
                                    position[0] = i;
                                    position[1] = j;    
                                    trace.setEnabled(true);
                                    field.setText("Board : " + boardLength + "x" + boardWidth + "\n\nSource : (" + (position[0]+1) + "," + (position[1]+1) + ")");
                                    initialMove(i,j);                    
                                }
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
    
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
  
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                            
                    }   

                    @Override
                    public void mouseExited(MouseEvent e) {
                            
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
     
                    }
                    
                    
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        //mouseMoved overriden to redraw lines if mouse moved over the buttons
                        for(int i=0; i<boardLength; i++) {
                            for(int j=0; j<boardWidth; j++) {
                                if(e.getSource() == p[i][j]) {
                                    if(boardPainted)   {    
                                        drawLines();
                                    }
                                }
                            }
                        }
                    }   
}
    
  
 
 


   