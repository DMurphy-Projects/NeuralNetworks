package Model;

import java.util.Random;

public class Synapse extends Edge<Neuron> {

    double weight;

    static ISynapseWeightStrategy assignmentStrategy = new ISynapseWeightStrategy() {
        int min = -1, max = 1;
        Random r = new Random();
        @Override
        public double assignWeight() {
            return min + ((max - min) * r.nextDouble());
        }
    };

    public static void setAssignmentStrategy(ISynapseWeightStrategy strategy)
    {
        assignmentStrategy = strategy;
    }

    public Synapse(Neuron f, Neuron t)
    {
        super(f, t);

        weight = assignmentStrategy.assignWeight();
    }

    public Synapse(Neuron f, Neuron t, double w)
    {
        super(f, t);

        weight = w;
    }

    public double getWeight() {
        return weight;
    }

    public void addWeight(double delta)
    {
        weight += delta;
    }
}
