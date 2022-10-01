import Controller.ForceController;
import Controller.NetworkController;
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
import java.util.ArrayList;

public class NetworkDraw {

    public static void main(String[] args) throws InterruptedException {
        FFNHandler handler = new FFNHandler("src/main/resources/draw") {
            public Integer[] createSpecification() {
                return new Integer[]{5, 3, 5, 5};
            }
        };

        NetworkController network = new NetworkController(handler.getIn()) {
            public void update() {

            }
        };

        network.evaluateNetwork();

        for (Neuron n: handler.getOut())
        {
            System.out.println(n.getOutput());
        }

        //draw start
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
                        new EdgeSpringRoutine(1000 ,0.1),
                        new NodeRepulsionRoutine(1000, 1)
                });

        while(true)
        {
            forceController.update();
            view.repaint();

            Thread.sleep(100);
        }
    }
}
