package ru.nsu.ccfit.damdinov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * Created by Arlis on 01.03.14.
 */
public class DrawingPanel extends JPanel implements MouseMotionListener, MouseWheelListener, KeyListener {
    Dimension fullscreen = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage bufferedImage;

    private int canvasWidth, canvasHeight;
    private int centerAxisX = 400, centerAxisY = 200;
    private int currentX, currentY;
    private int motionStep = 10, sclareStep = 2;
    private int parametrC = 2;

    Graphics2D graphics2D;
    private double length = 100;
    boolean drugAndDrop = true, scrollWheel = true;
    private int cay = 200;
    private int cax = 300;
    private double startLength = 100;
    private int startMS = 5;
    private int startSS = 2;
    private boolean startSW = true;
    private boolean startDND = true;

    DrawingPanel(){
        super();

        addMouseMotionListener(this);
        addMouseWheelListener(this);

        bufferedImage = new BufferedImage((int) fullscreen.getWidth(), (int) fullscreen.getHeight(), 1);
        graphics2D = bufferedImage.createGraphics();
//        graphics2D.setPaint( new Color(255, 255, 255));
    }

    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        canvasHeight = getHeight();
        canvasWidth = getWidth();

        drawAxis();
        drawFunction();

        graphics2D.drawImage(bufferedImage, null, 0, 0);
    }



    private void drawFunction() {
        double yD;
        int y, x, prevY = 0;
        for( double i = 0; i < 3 ; i += 1/length ){
            yD = calculateY(i);
            x = (int) (i * length);

            // если на окраинах. нужно соединить концы
            if (Double.isNaN(yD)){
                for (int counter = prevY; counter >= 0; counter--)
                    drawAllFourPoint(x, counter);
                break;
            }

            //поиск пиксела, в котором находится точка
            for ( double k = (int)yD;; k += 1/length){
                if ( yD >= k && yD < k+1/length){
                    yD = k;
                    break;
                }
            }

            y = (int) (yD * length);
            if (Math.abs(y - prevY) != 1){
                int counter, end;
                if ( y > prevY){
                    counter = prevY;
                    end = y;
                } else {
                    counter = y;
                    end = prevY;
                }

                for ( ; counter < end; counter++)
                    drawAllFourPoint(x,counter);
            }
            drawAllFourPoint(x,y);

            prevY = y;
        }

    }


    private void drawAllFourPoint( int x, int y){
        if (x+centerAxisX > 0 && x+centerAxisX < canvasWidth && -y+centerAxisY > 0 && -y+centerAxisY < canvasHeight)
            bufferedImage.setRGB( x+centerAxisX, -y+centerAxisY,0);

        if (x+centerAxisX > 0 && x+centerAxisX < canvasWidth && y+centerAxisY > 0 && y+centerAxisY < canvasHeight)
            bufferedImage.setRGB( x+centerAxisX, y+centerAxisY,0);

        if (-x+centerAxisX > 0 && -x+centerAxisX < canvasWidth && -y+centerAxisY > 0 && -y+centerAxisY < canvasHeight)
            bufferedImage.setRGB( -x+centerAxisX, -y+centerAxisY,0);

        if (-x+centerAxisX > 0 && -x+centerAxisX < canvasWidth && y+centerAxisY > 0 && y+centerAxisY < canvasHeight)
            bufferedImage.setRGB( -x+centerAxisX, y+centerAxisY,0);
    }


    private double calculateY(double x) {
        double y = Math.sqrt( Math.sqrt(Math.pow(parametrC, 4.0)  + 4 * x*x * parametrC*parametrC ) - x*x - parametrC*parametrC  );
        return y;
    }

    public void reset() {
        centerAxisX = canvasWidth/2;
        centerAxisY = canvasHeight/2;
        length = startLength;
        drugAndDrop = startDND;
        scrollWheel = startSW;
        sclareStep = startSS;
        motionStep = startMS;

        repaint();
    }

    private void drawAxis() {
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        if (centerAxisX < canvasWidth)
        for (int i = (int) (centerAxisX + length); i < canvasWidth; i+=length){
            for (int j = 0; j < canvasHeight; j++)
                if ( (i > 0 && i < canvasWidth) && ( j > 0 && j < canvasHeight)){
                    bufferedImage.setRGB( i, j,0xe4e9e4);
                }
        }
        if (centerAxisX > 0)
            for (int i = (int) (centerAxisX - length); i > 0; i-=length){
                for (int j = 0; j < canvasHeight; j++)
                    if ( (i > 0 && i < canvasWidth) && ( j > 0 && j < canvasHeight)){
                        bufferedImage.setRGB( i, j,0xe4e9e4);
                    }
            }
        if (centerAxisY < canvasHeight)
            for (int i = (int) (centerAxisY + length); i < canvasHeight; i+=length){
                for (int j = 0; j < canvasWidth; j++)
                    if ( (j > 0 && j < canvasWidth) && ( i > 0 && i < canvasHeight)){
                        bufferedImage.setRGB( j, i,0xe4e9e4);
                    }
            }
        if (centerAxisY > 0)
            for (int i = (int) (centerAxisY - length); i > 0; i-=length){
                for (int j = 0; j < canvasWidth; j++)
                    if ( (j > 0 && j < canvasWidth) && ( i > 0 && i < canvasHeight)){
                        bufferedImage.setRGB( j, i, 0xe4e9e4);
                    }
            }

        if (centerAxisY > 0 && centerAxisY < canvasHeight)
            for ( int i = 0; i < canvasWidth; i++)
                bufferedImage.setRGB(i,centerAxisY,0);
        if (centerAxisX > 0 && centerAxisX < canvasWidth)
            for ( int i = 0; i < canvasHeight; i++)
                bufferedImage.setRGB(centerAxisX,i,0);
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if (!drugAndDrop)
            return;
        centerAxisX += e.getX() - currentX;
        currentX = e.getX();
        centerAxisY += e.getY() - currentY;
        currentY = e.getY();

        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!scrollWheel)
            return;
        if (e.getWheelRotation() > 0)
            length *= sclareStep;
        else length /= sclareStep;
        if (length < 5){
            length = 5;
            return;
        }
        if (length > 800){
            length = 800;
            return;
        }

        // приближаем
        if (e.getWheelRotation() > 0){
            System.out.println("приближаем график");
            //  1-я четверть
            if (centerAxisX > currentX && centerAxisY < currentY){
                centerAxisY = centerAxisY - Math.abs(centerAxisY - currentY)*sclareStep;
                centerAxisX = centerAxisX + Math.abs(centerAxisX - currentX)*sclareStep;
            }
            //  2-я четверть
            else if (centerAxisX < currentX && centerAxisY < currentY){
                centerAxisY = centerAxisY - Math.abs(centerAxisY - currentY)*sclareStep;
                centerAxisX = centerAxisX - Math.abs(centerAxisX - currentX)*sclareStep;
            }
            //  3-я четверть
            else if (centerAxisX < currentX && centerAxisY > currentY){
                centerAxisY = centerAxisY + Math.abs(centerAxisY - currentY)*sclareStep;
                centerAxisX = centerAxisX - Math.abs(centerAxisX - currentX)*sclareStep;
            }
            //  4-я четверть
            else if (centerAxisX > currentX && centerAxisY > currentY){
                centerAxisY = centerAxisY + Math.abs(centerAxisY - currentY)*sclareStep;
                centerAxisX = centerAxisX + Math.abs(centerAxisX - currentX)*sclareStep;
            }
        }
        // отдаляем
        else {
            System.out.println("отдаляем график");
            // с какой стороны от курсора находися центр:
            //  1-я четверть
            if (centerAxisX > currentX && centerAxisY < currentY){
                System.out.println("    1-я");
                centerAxisY = centerAxisY + Math.abs(centerAxisY - currentY)/sclareStep;
                centerAxisX = centerAxisX - Math.abs(centerAxisX - currentX)/sclareStep;
            }
            //  2-я четверть
            else if (centerAxisX < currentX && centerAxisY < currentY){
                System.out.println("    2-я"+centerAxisY+"_"+currentY+"="+centerAxisX+"_"+currentX);
                centerAxisY = centerAxisY + Math.abs(centerAxisY - currentY)/sclareStep;
                centerAxisX = centerAxisX + Math.abs(centerAxisX - currentX)/sclareStep;
            }
            //  3-я четверть
            else if (centerAxisX < currentX && centerAxisY > currentY){
                System.out.println("    3-я");
                centerAxisY = centerAxisY - Math.abs(centerAxisY - currentY)/sclareStep;
                centerAxisX = centerAxisX + Math.abs(centerAxisX - currentX)/sclareStep;
            }
            //  4-я четверть
            else if (centerAxisX > currentX && centerAxisY > currentY){
                System.out.println("    4-я"+centerAxisY+"_"+currentY+"="+centerAxisX+"_"+currentX);
                centerAxisY = centerAxisY - Math.abs(centerAxisY - currentY)/sclareStep;
                centerAxisX = centerAxisX - Math.abs(centerAxisX - currentX)/sclareStep;
            }
        }

        repaint();
    }


    public void setSclareStep(Integer sclareStepValue) {
        this.sclareStep = sclareStepValue;
    }

    public void setMotionStep(Integer motionStep) {
        this.motionStep = motionStep;
    }

    public Integer getSclareStep() {
        return new Integer(sclareStep);
    }

    public Integer getMotionStep() {
        return new Integer(motionStep);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println(e.getExtendedKeyCode()+"!!!"+e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void moveUp() {
        centerAxisY += motionStep;
        repaint();
    }

    public void moveDown() {
        centerAxisY -= motionStep;
        repaint();
    }

    public void moveLeft() {
        centerAxisX += motionStep;
        repaint();
    }

    public void moveRight() {
        centerAxisX -= motionStep;
        repaint();
    }

    public void zoomIn() {
        length += sclareStep;
        repaint();
    }

    public void zoomOut() {
        length -= sclareStep;
        if (length<5)
            length = 5;
        repaint();
    }

    public void changeDND() {
        if (drugAndDrop)
            drugAndDrop = false;
        else drugAndDrop = true;
    }

    public void changeSW() {
        if( scrollWheel)
            scrollWheel = false;
        else scrollWheel = true;
    }
}