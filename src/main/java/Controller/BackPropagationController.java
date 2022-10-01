package Controller;

import Model.Neuron;
import Model.Synapse;

public abstract class BackPropagationController extends NetworkController {

    public BackPropagationController(Neuron[] network) {
        super(network);
    }

    public abstract void updateTargets();

    public void backPropagate(double lRate)
    {
        for (int j=synapseProgression.size()-1;j>=0;j--)
        {
            for (Synapse s: synapseProgression.get(j))
            {
                //w_ji = lRate . delta_j . o_i
                double delta_w = lRate * calculateDelta(s) * s.getFrom().getOutput();
                s.addWeight(delta_w);
            }
        }

        updateTargets();
    }

    private double calculateDelta(Synapse s)
    {
        if (s.getTo().getEdges().size() == 0)
        {
            return calculateDeltaOutput(s);
        }
        else
        {
            return calculateDeltaHidden(s);
        }
    }

    private double calculateDeltaOutput(Synapse s)
    {
        //delta_j = o_j(1 - o_j)(t_j - o_j)
        return s.getTo().getDelta();
    }

    private double calculateDeltaHidden(Synapse s)
    {
        //delta_j = o_j(1-o_j)sum_k(delta_k . w_kj)
        double sum_k = 0;
        for (Synapse t: s.getTo().getEdges())
        {
            sum_k += calculateDelta(t) * t.getWeight();
        }

        return s.getTo().getPartialDerivative() * sum_k;
    }
}
