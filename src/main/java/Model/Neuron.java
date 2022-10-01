package Model;

public class Neuron extends ForceNode<Synapse> {

    boolean updated = false;
    double bias = 0.5, input = 0, output = 0, target = 0, error = 0;

    double partialDerivative, delta;

    public Neuron(int index) {
        super(index);
    }

    //neurons will only know about synapses they contribute to, ie forward connections
    //A -> B
    //A: A -> B
    //B:
    public void connectTo(Neuron to)
    {
        connect(new Synapse(this, to));
    }

    public void connectTo(Neuron to, double weight)
    {
        connect(new Synapse(this, to, weight));
    }

    public void reset()
    {
        updated = false;
        input = 0;
    }

    public void input(double value)
    {
        input += value;
    }

    public void output()
    {
        if (updated) return;
        updated = true;

        output = 1d / (1d + Math.exp(-(input + bias)));
        partialDerivative = output * (1d - output);
        delta =  partialDerivative * (target - output);
        error = Math.pow(target - output, 2) * 0.5;
    }

    public void setOutput(double output) {
        this.output = output;
        updated = true;
    }

    public double getOutput() {
        return output;
    }

    public double getDelta() {
        return delta;
    }

    public double getPartialDerivative() {
        return partialDerivative;
    }

    //should be set prior to output being calculated
    public void setTarget(double target) {
        this.target = target;
    }

    public double getTarget() {
        return target;
    }

    public double getError() {
        return error;
    }
}
