import Controller.ForceController;
import Controller.Routines.EdgeSpringRoutine;
import Controller.Routines.IRoutine;
import Controller.Routines.NodeRepulsionRoutine;
import Model.Neuron;
import Model.Synapse;
import View.ForceGraphView;
import View.NetworkDrawMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class NetworkRenderHelper {

    //quick way to visualise ANN with default settings
    public static void render(FFNHandler handler)
    {
        JFrame window = new JFrame("FD ANN Graph");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setSize(1000,600);

        Neuron[] nodes = handler.createNodeArray(0, 50, 0, 100);
        Synapse[] edges = handler.createEdgeArray();

        final int[] nodeSize = new int[]{30, 40};
        ForceGraphView view = new ForceGraphView<Neuron, Synapse>(nodes, edges) {
            @Override
            public void drawNode(Graphics g, Neuron n, int[] position) {
                String info = String.format("%.2f", n.getOutput());
                Rectangle2D stringRec = getFont().getStringBounds(info, getFontMetrics(getFont()).getFontRenderContext());

                NetworkDrawMethods.drawNode(g, n, position, nodeSize, info, stringRec);
            }

            @Override
            public void drawEde(Graphics g, Synapse e, int[] from, int[] to) {
                NetworkDrawMethods.drawSynapse(g, e, from, to);
            }

            @Override
            public void drawEdgeLabel(Graphics g, Synapse e, int[] from, int[] to) {
                String info = String.format("%.2f", e.getWeight());
                Rectangle2D stringRec = getFont().getStringBounds(info, getFontMetrics(getFont()).getFontRenderContext());

                NetworkDrawMethods.drawSynapseLabels(g, e, from, to, nodeSize, info, stringRec);
            }
        };

        window.add(view);
        window.setVisible(true);

        ForceController forceController = new ForceController(nodes, edges,
                new IRoutine[]{
                        new EdgeSpringRoutine(100 ,0.01),
                        new NodeRepulsionRoutine(100, 0.1)
                });

        while(true)
        {
            forceController.update();
            view.repaint();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
