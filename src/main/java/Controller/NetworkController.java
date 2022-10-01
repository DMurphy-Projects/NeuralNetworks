package Controller;

import Model.Neuron;
import Model.Synapse;

import java.util.*;

public abstract class NetworkController {

    Neuron[] network;
    ArrayList<ArrayList<Neuron>> neuronProgression;
    ArrayList<ArrayList<Synapse>> synapseProgression;

    public NetworkController(Neuron[] network)
    {
        this.network = network;

        createNetworkProgression();
    }

    //chance to update the graph before output is calculated
    public abstract void update();

    private void createNetworkProgression()
    {
        ArrayList<Neuron> queueNow = new ArrayList<Neuron>(), queueNext = new ArrayList<Neuron>();
        ArrayList<Synapse> layer = new ArrayList<Synapse>();
        LinkedHashSet<Integer> globalSeen = new LinkedHashSet<Integer>();

        neuronProgression = new ArrayList<ArrayList<Neuron>>();
        synapseProgression = new ArrayList<ArrayList<Synapse>>();

        queueNow.addAll(Arrays.asList(network));
        while(queueNow.size() > 0)
        {
            LinkedHashSet<Neuron> localSeen = new LinkedHashSet<Neuron>();

            for (Neuron n: queueNow)
            {
                localSeen.add(n);

                if (globalSeen.contains(n.getIndex())) continue;
                globalSeen.add(n.getIndex());

                for (Synapse s: n.getEdges())
                {
                    queueNext.add(s.getTo());
                }

                layer.addAll(n.getEdges());
            }

            queueNow.clear();
            queueNow.addAll(queueNext);
            queueNext.clear();

            if (localSeen.size() > 0) {
                ArrayList<Neuron> nodeLayer = new ArrayList<Neuron>(localSeen);
                neuronProgression.add(nodeLayer);
            }

            if (layer.size() > 0) {
                synapseProgression.add(layer);
                layer = new ArrayList<Synapse>();
            }
        }
    }

    protected void resetNetwork() {
        for (ArrayList<Synapse> list: synapseProgression) {
            for (Synapse s : list) {
                s.getTo().reset();
            }
        }

        for (Synapse s : synapseProgression.get(0)) {
            s.getFrom().reset();
        }
    }

    public void evaluateNetwork() {
        resetNetwork();
        update();

        for (int i=0;i<neuronProgression.size();i++)
        {
            for (Neuron n: neuronProgression.get(i))
            {
                n.output();
            }

            if (synapseProgression.size() <= i) continue;
            for (Synapse s: synapseProgression.get(i))
            {
                s.getTo().input(s.getFrom().getOutput() * s.getWeight());
            }
        }
    }
}
