package Controller;

import Model.Neuron;

public class TrainingController {

    Neuron[] outputLayer;
    BackPropagationController network;

    public TrainingController(BackPropagationController network, Neuron[] outputLayer)
    {
        this.network = network;
        this.outputLayer = outputLayer;
    }

    public void train(int maxIteration, double maxError, int dataLength)
    {
        double error;
        int currentIteration = 0;
        do {
            error = 0;
            for (int i = 0; i < dataLength; i++) {
                network.evaluateNetwork();
                network.backPropagate(1);

                for (int j = 0; j < outputLayer.length; j++) {
                    error += outputLayer[j].getError();
                }
            }
            if (currentIteration % 1000 == 0) {
                System.out.println(String.format("Iteration: %s, Error: %s", currentIteration, error));
            }
            currentIteration++;
        } while (error > maxError && currentIteration < maxIteration);

        System.out.println(String.format("Iteration: %s, Error: %s", currentIteration, error));
    }
}
