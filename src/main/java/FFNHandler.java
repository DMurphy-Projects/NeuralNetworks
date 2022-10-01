import Model.UpdateStrategy.IUpdateStrategy;
import Model.Neuron;
import Model.Synapse;
import Model.UpdateStrategy.UpdateStrategyFixed;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class FFNHandler {

    FileVariables<Double> doubleVars;
    FileVariables<Integer> integerVars;

    String NEURON_INDEX = "NI", SYNAPSE_FROM = "SF", SYNAPSE_TO = "ST", SYNAPSE_WEIGHT = "SW",
            LAYER_IN = "LI", LAYER_HIDDEN = "LH", LAYER_OUT = "LO", NETWORK_META = "NM";

    Neuron[] in, out;
    Neuron[][] h;

    public FFNHandler(String path)
    {
        createFileVariables(path);

        load();
    }

    public abstract Integer[] createSpecification();

    private void createFromSpecification(Integer[] meta)
    {
        int ins = meta[0], hiddenLayers = meta[1], hiddens = meta[2], outs = meta[3];

        in = new Neuron[ins];
        h = new Neuron[hiddenLayers][hiddens];
        out = new Neuron[outs];

        int index = 0;

        for (int i=0;i<in.length;i++)
        {
            in[i] = new Neuron(index++);
            for (int j=0;j<h[0].length;j++)
            {
                if (h[0][j] == null) h[0][j] = new Neuron(index++);
                in[i].connectTo(h[0][j]);
//                in[i].setUpdateStrategy(new UpdateStrategyFixed());
            }
        }

        for (int i=0;i<hiddenLayers-1;i++)
        {
            for (int j=0;j<hiddens;j++)
            {
                if (h[i][j] == null) h[i][j] = new Neuron(index++);
                for (int k=0;k<hiddens;k++)
                {
                    if (h[i+1][k] == null) h[i+1][k] = new Neuron(index++);
                    h[i][j].connectTo(h[i+1][k]);
                }
            }
        }

        for (int i=0;i<h[hiddenLayers-1].length;i++)
        {
            for (int j=0;j<out.length;j++)
            {
                if (out[j] == null)
                {
                    out[j] = new Neuron(index++);
//                    out[j].setUpdateStrategy(new UpdateStrategyFixed());
                }
                h[hiddenLayers-1][i].connectTo(out[j]);
            }
        }
    }

    private void createFileVariables(String path)
    {
        doubleVars = new FileVariables<Double>(path + ".double") {
            @Override
            public Double parseType(String toParse) {
                return Double.parseDouble(toParse);
            }

            public Double[] createArray(int s) {
                return new Double[s];
            }
        };

        integerVars = new FileVariables<Integer>(path + ".integer") {
            @Override
            public Integer parseType(String toParse) {
                return Integer.parseInt(toParse);
            }

            public Integer[] createArray(int s) {
                return new Integer[s];
            }
        };
    }

    public void save()
    {
        saveLayer(LAYER_IN, in, h[0]);

        for (int i=0;i<h.length;i++)
        {
            if (i == h.length-1)
            {
                saveLayer(LAYER_HIDDEN + i, h[i], out);
            }
            else {
                saveLayer(LAYER_HIDDEN + i, h[i], h[i + 1]);
            }
        }

        saveLayer(LAYER_OUT, out, new Neuron[0]);

        integerVars.putArray(NETWORK_META, new Integer[]{in.length, h.length, h[0].length, out.length});

        integerVars.save();
        doubleVars.save();
    }

    private void saveLayer(String LAYER_KEY, Neuron[] layer, Neuron[] nextLayer)
    {
        Integer[] nIndex = new Integer[layer.length],
                sFIndex = new Integer[layer.length * nextLayer.length],
                sTIndex = new Integer[layer.length * nextLayer.length];
        Double[] sWeight = new Double[layer.length * nextLayer.length];

        int i = 0, j = 0;
        for (Neuron n: layer) {
            for (Synapse s : n.getEdges()) {
                sFIndex[j] = s.getFrom().getIndex();
                sTIndex[j] = s.getTo().getIndex();
                sWeight[j++] = s.getWeight();
            }

            nIndex[i++] = n.getIndex();
        }

        integerVars.putArray(LAYER_KEY + NEURON_INDEX, nIndex);

        if (nextLayer.length == 0) return;
        integerVars.putArray(LAYER_KEY + SYNAPSE_FROM, sFIndex);
        integerVars.putArray(LAYER_KEY + SYNAPSE_TO, sTIndex);
        doubleVars.putArray(LAYER_KEY + SYNAPSE_WEIGHT, sWeight);
    }

    public void load()
    {
        Integer[] networkMeta = integerVars.getArray(NETWORK_META);
        if (networkMeta == null)
        {
            createFromSpecification(createSpecification());

            save();
        }
        else
        {
            in = new Neuron[networkMeta[0]];
            h = new Neuron[networkMeta[1]][networkMeta[2]];
            out = new Neuron[networkMeta[3]];

            HashMap<Integer, Neuron> neuronMap = new HashMap<Integer, Neuron>();

            loadLayerNeurons(LAYER_IN, in, neuronMap);
//            setLayerUpdateStrategy(LAYER_IN, in, new UpdateStrategyFixed());

            for (int i=0;i<h.length;i++)
            {
                loadLayerNeurons(LAYER_HIDDEN + i, h[i], neuronMap);
            }

            loadLayerNeurons(LAYER_OUT, out, neuronMap);
//            setLayerUpdateStrategy(LAYER_OUT, out, new UpdateStrategyFixed());

            loadLayerSynapses(LAYER_IN, neuronMap);
            for (int i=0;i<h.length;i++)
            {
                loadLayerSynapses(LAYER_HIDDEN + i, neuronMap);
            }
        }
    }

    private void setLayerUpdateStrategy(String keyLayer, Neuron[] layer, IUpdateStrategy strategy)
    {
        int i = 0;
        for (Integer index: integerVars.getArray(keyLayer + NEURON_INDEX))
        {
            layer[i++].setUpdateStrategy(strategy);
        }
    }

    private void loadLayerNeurons(String keyLayer, Neuron[] layer, HashMap<Integer, Neuron> map)
    {
        int i = 0;
        for (Integer index: integerVars.getArray(keyLayer + NEURON_INDEX))
        {
            layer[i] = new Neuron(index);
            map.put(index, layer[i++]);
        }
    }

    private void loadLayerSynapses(String keyLayer, HashMap<Integer, Neuron> map)
    {
        Integer[] from = integerVars.getArray(keyLayer + SYNAPSE_FROM);
        Integer[] to = integerVars.getArray(keyLayer + SYNAPSE_TO);
        Double[] weight = doubleVars.getArray(keyLayer + SYNAPSE_WEIGHT);

        for (int i=0;i<from.length; i++)
        {
            Neuron nFrom = map.get(from[i]);
            Neuron nTo = map.get(to[i]);

            nFrom.connectTo(nTo, weight[i]);
        }
    }

    public Neuron[] getIn() {
        return in;
    }

    public Neuron[][] getHiddens() {
        return h;
    }

    public Neuron[] getOut() {
        return out;
    }

    public Neuron[] createNodeArray(int rowStart, int rowStep, int colStart, int colStep)
    {
        Neuron[] ins = getIn();
        Neuron[][] hiddens = getHiddens();
        Neuron[] outs = getOut();

        int hCount = 0;
        for (Neuron[] h: hiddens)
        {
            hCount += h.length;
        }

        int index = 0, row = rowStart, col = colStart;
        Neuron[] all = new Neuron[ins.length + hCount + outs.length];
        for (Neuron n: ins)
        {
            all[index++] = n;
            n.setCoordinate(new double[]{col, row += rowStep});
        }
        for (Neuron[] h: hiddens)
        {
            row = rowStart; col += colStep;
            for (Neuron n: h)
            {
                all[index++] = n;
                n.setCoordinate(new double[]{col, row += rowStep});
            }
        }
        row = rowStart; col += colStep;
        for (Neuron n: outs)
        {
            all[index++] = n;
            n.setCoordinate(new double[]{col, row += rowStep});
        }
        return all;
    }

    public Synapse[] createEdgeArray() {
        Neuron[] ins = getIn();
        Neuron[][] hiddens = getHiddens();
        Neuron[] outs = getOut();

        ArrayList<Synapse> edgeList = new ArrayList<>();
        for (Neuron n: ins)
        {
            edgeList.addAll(n.getEdges());
        }
        for (Neuron[] h: hiddens)
        {
            for (Neuron n: h)
            {
                edgeList.addAll(n.getEdges());
            }
        }
        for (Neuron n: outs)
        {
            edgeList.addAll(n.getEdges());
        }
        Synapse[] edges = new Synapse[edgeList.size()];
        edgeList.toArray(edges);
        return edges;
    }
}
