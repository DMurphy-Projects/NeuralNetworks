package View;

import Model.Neuron;
import Model.Synapse;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class NetworkDrawMethods {
    public static void drawNode(Graphics g, Neuron n, int[] position, int[] size, String string, Rectangle2D stringRec)
    {
        //should be in middle
        position[0] -= (size[0] / 2);
        position[1] -= (size[1] / 2);

        g.setColor(Color.white);
        g.fillRect(position[0], position[1], size[0], size[1]);
        g.setColor(Color.black);
        g.drawRect(position[0], position[1], size[0], size[1]);

        int a = (size[0] / 2) - (int)(stringRec.getWidth() / 2) - (int)stringRec.getX();
        int b = (size[1] / 2) - (int)(stringRec.getHeight() / 2) - (int)stringRec.getY();
        g.drawString(string, position[0] + a, position[1] + b);
    }

    public static void drawSynapse(Graphics g, Synapse e, int[] from, int[] to)
    {
        Color lineColor = Color.black;
        if (e.getWeight() > 0) lineColor = Color.green;
        else if (e.getWeight() < 0) lineColor = Color.red;

        g.setColor(lineColor);
        g.drawLine(from[0], from[1], to[0], to[1]);
    }

    public static void drawSynapseLabels(Graphics g, Synapse e, int[] from, int[] to, int[] size, String string, Rectangle2D stringRec)
    {
        int[] midpoint = {(from[0] + to[0]) / 2, (from[1] + to[1]) / 2};
        //should be in middle
        midpoint[0] -= (size[0] / 2);
        midpoint[1] -= (size[1] / 2);

        Color lineColor = Color.black;
        if (e.getWeight() > 0) lineColor = Color.green;
        else if (e.getWeight() < 0) lineColor = Color.red;

        g.setColor(Color.white);
        g.fillRect(midpoint[0], midpoint[1], size[0], size[1]);

        g.setColor(lineColor);
        g.drawRect(midpoint[0], midpoint[1], size[0], size[1]);

        int a = (size[0] / 2) - (int)(stringRec.getWidth() / 2) - (int)stringRec.getX();
        int b = (size[1] / 2) - (int)(stringRec.getHeight() / 2) - (int)stringRec.getY();
        g.drawString(string, midpoint[0] + a, midpoint[1] + b);
    }
}
