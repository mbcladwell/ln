package ln;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;
 
public class ProgressBar extends JPanel implements 
					    PropertyChangeListener {

    private JProgressBar progressBar;
    //private Task task;
 
 
    public ProgressBar() {
        super(new BorderLayout());
 
        //Create the demo's UI.
      
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
	progressBar.setIndeterminate(true);
       
        JPanel panel = new JPanel();
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
         setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
     
	
    }
 
  
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      
    }
 

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI(String frame_title) {
        //Create and set up the window.
        JFrame frame = new JFrame(frame_title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBar();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] frame_title) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI( frame_title[0]);
            }
        });
    }
}
