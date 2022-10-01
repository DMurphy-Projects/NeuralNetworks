import Controller.BackPropagationController;
import Controller.ForceController;
import Controller.Routines.EdgeSpringRoutine;
import Controller.Routines.IRoutine;
import Controller.Routines.NodeRepulsionRoutine;
import Controller.TrainingController;
import Model.ISynapseWeightStrategy;
import Model.Neuron;
import Model.Synapse;
import View.ForceGraphView;
import View.NetworkDrawMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BackPropagation {

    public static void printNetwork(BackPropagationController network, int dataLength, Neuron[] ins, Neuron[] outs)
    {
        for (int i=0;i<dataLength;i++)
        {
            network.evaluateNetwork();
            network.updateTargets();

            for (Neuron n: ins)
            {
                System.out.print(n.getOutput() + ", ");
            }
            System.out.print("-> ");
            for (Neuron n: outs)
            {
                System.out.print(n.getOutput()+" ("+n.getTarget()+")" + ", ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /*
        xor + or: require (2, 1, 2, 1) specification with minimal starting variation
        and: require {2, 1, 3, 1} specification with a more significant starting variation
         */
        Synapse.setAssignmentStrategy(new ISynapseWeightStrategy() {
            double w = 0;
            @Override
            public double assignWeight() {
                return w += 0.1;
            }
        });

        FFNHandler handler = new FFNHandler("src/main/resources/ffn") {
            public Integer[] createSpecification() {
                return new Integer[]{2, 1, 3, 1};
            }
        };

        final Neuron[] ins = handler.getIn(), outs = handler.getOut();

        final Double[][][] trainingSet = TrainingDataSample.and();
        final int dataLength = trainingSet.length, inputLength = trainingSet[0][0].length, outputLength = trainingSet[0][1].length;

        BackPropagationController network = new BackPropagationController(handler.getIn()) {

            int trainingIndex = 0;

            public void update() {
                for (int i=0;i<outputLength;i++)
                {
                    outs[i].setTarget(trainingSet[trainingIndex][1][i]);
                }

                for (int i=0;i<inputLength;i++)
                {
                    ins[i].setOutput(trainingSet[trainingIndex][0][i]);
                }
            }

            public void updateTargets() {
                trainingIndex++;
                if (trainingIndex > dataLength-1) trainingIndex = 0;
            }
        };

        System.out.println("Before");
        printNetwork(network, dataLength, ins, outs);
        System.out.println();

        TrainingController trainingController = new TrainingController(network, outs);
        trainingController.train(100000, 0.001, dataLength);

        System.out.println();
        System.out.println("After");
        printNetwork(network, dataLength, ins, outs);

        handler.save();

        NetworkRenderHelper.render(handler);
    }
}
