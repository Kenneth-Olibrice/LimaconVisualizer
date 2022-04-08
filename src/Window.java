import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.lang.Math.*;


public class Window extends JFrame implements GLEventListener {
    private FPSAnimator animator;
    private GLCanvas canvas;
    private JPanel topButtonsHandler, topBarHandler, sincosHandler;
    private JButton[] topButtons, sincosButtons;
    private JTextField rInput;
    private static final int NUM_CLASSES = 4;
    private static final int NUM_FUNCTIONS = 2;
    private double r = 0;
    private double theta = 0;
    private final double increment = 0.0085;

    // We will use this enum to determine what shapes to draw.
    private enum States {
        FREE, LEMNISCATE, FLOWER, SPIRAL
    }

    // This will allow us to determine whether to draw the sin or cos variant of each limacon.
    private enum sincos {
        SIN, COS
    }

    private sincos currentFunc = null;
    private States currentState = null;

    public Window() {
        super();
    }

    // Because accessing member fields prior to object construction is risky,
    // we will implement our own method to do it immediataely following object construction.
    public void initWindow() {
        // Initialize field objects and properties.
        this.currentState = States.FREE;
        canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
        animator = new FPSAnimator(60);
        topButtonsHandler = new JPanel();
        topButtonsHandler.setLayout(new GridLayout(1, Window.NUM_CLASSES));

        sincosHandler = new JPanel();
        sincosHandler.setLayout(new GridLayout(1, 2));

        topBarHandler = new JPanel();
        topBarHandler.setLayout(new GridLayout(2, 1));

        // Then we start to set visible properties of this object.
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Limacon Visualizer Project");

        // Then we can add our components.
        addButtonsToTop();
        this.add(topBarHandler, BorderLayout.NORTH);
        topBarHandler.add(sincosHandler);
        topBarHandler.add(topButtonsHandler);

        canvas.addGLEventListener(this);
        animator.add(canvas);
        this.add(canvas);

        this.setVisible(true);
        animator.start();
    }

    private void changeState() {

    }

    private void pause() {

    }

    // Implementation for adding class buttons to top
    private void addButtonsToTop() {
        topButtons = new JButton[Window.NUM_CLASSES];
        sincosButtons = new JButton[2];

        // Create the sin/cos buttons.
        for(int i = 0; i < 2; i++) {
            sincosButtons[i] = new JButton();
            sincosButtons[i].setText(Window.sincos.values()[i].name());
            sincosButtons[i].setBackground(Color.WHITE);
            sincosButtons[i].addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    JButton ref = (JButton) e.getComponent();

                    for(int i = 0; i < Window.NUM_FUNCTIONS; i++) {
                        sincosButtons[i].setBackground(Color.WHITE);
                        Window.this.theta = 0;
                        Window.this.r = 0;
                    }

                    ref.setBackground(Color.BLUE);
                    Window.this.currentFunc = Window.sincos.valueOf(ref.getText());
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
            });
            sincosHandler.add(sincosButtons[i]);
        }

        // Create the different mode buttons.
        for(int i = 0; i < Window.NUM_CLASSES; i++) {
            topButtons[i] = new JButton();
            topButtons[i].setText(Window.States.values()[i].name());
            topButtons[i].setBackground(Color.WHITE);
            topButtons[i].addMouseListener(new MouseListener() {

                // When a button is pressed, we need to first check if there is already a button pressed.
                // If there is, we need to check if it is this button. If it is, we need to disable this button
                // Otherwise if there is no active button we need to activate the current button.
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    JButton ref = (JButton) e.getComponent();
                    Window.this.theta = 0;

                    // If the button pressed was the FREE button.
                    if(ref.getText().equals(Window.States.FREE.name())) {
                        for(int i = 0; i < Window.NUM_CLASSES; i++) {
                            Window.this.topButtons[i].setBackground(Color.WHITE);
                        }
                        Window.this.currentState = Window.States.FREE;
                    }

                    // Otherwise check if we are free to activate a button.
                    if(!ref.getText().equals(Window.States.FREE.name()) && Window.this.currentState == Window.States.FREE) {
                        ref.setBackground(Color.BLUE);
                        Window.this.currentState = Window.States.valueOf(ref.getText());
                    }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                }


            });
            topButtonsHandler.add(topButtons[i]);
        }
    }

    @Override
    public void init(GLAutoDrawable glad) {
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        if(this.currentState != Window.States.FREE && this.currentFunc != null) {
            this.setTitle("R = " + Double.toString(r) + " | Theta = " + Double.toString(theta * 180/PI));
            if(this.currentFunc == Window.sincos.SIN) sinDrawings(gl);
            if(this.currentFunc == Window.sincos.COS) cosDrawings(gl);
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
    }

    private void sinDrawings(GL2 gl) {
        switch(this.currentState.name()) {
            case "LEMNISCATE":

                double ar = 0;
                double ax = 0;
                double ay = 0;

                gl.glBegin(GL2.GL_LINE_LOOP);
                for(double i = 0; i < theta; i += increment / 100) {
                    ar = sqrt(sin(2*i));
                    ax = ar*cos(i);
                    ay = ar*sin(i);


                    gl.glColor3f(0f, 0f, 1.0f);
                    gl.glVertex2d(ax, ay);
                }
                gl.glEnd();

                r = sqrt(sin(2*theta));
                double x = r*cos(theta);
                double y = r*sin(theta);

                gl.glBegin(GL2.GL_LINES);

                gl.glColor3f(1f, 0f, 0f);
                gl.glVertex2d(0, 0);

                gl.glColor3f(1f, 0f, 0f);
                gl.glVertex2d(x, y);

                gl.glEnd();


                break;
        }

        // Increment the angle.
        theta += increment;
    }

    private void cosDrawings(GL2 gl) {
        switch(this.currentState.name()) {
            case "LEMNISCATE":

                double ar = 0;
                double ax = 0;
                double ay = 0;

                gl.glBegin(GL2.GL_LINE_LOOP);
                for(double i = 0; i < theta; i += increment / 100) {
                    ar = sqrt(cos(2*i));
                    ax = ar*cos(i);
                    ay = ar*sin(i);


                    gl.glColor3f(0f, 0f, 1.0f);
                    gl.glVertex2d(ax, ay);
                }
                gl.glEnd();

                r = sqrt(cos(2*theta));
                double x = r*cos(theta);
                double y = r*sin(theta);

                gl.glBegin(GL2.GL_LINES);

                gl.glColor3f(1f, 0f, 0f);
                gl.glVertex2d(0, 0);

                gl.glColor3f(1f, 0f, 0f);
                gl.glVertex2d(x, y);

                gl.glEnd();


                break;
        }

        // Increment the angle.
        theta += increment;
    }

}
