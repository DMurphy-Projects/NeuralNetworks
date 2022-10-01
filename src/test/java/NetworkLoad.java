import Controller.NetworkController;
import Model.*;

public class NetworkLoad {

    public static void main(String[] args) throws InterruptedException {
        FFNHandler handler = new FFNHandler("src/main/resources/ffn") {
            public Integer[] createSpecification() {
                return new Integer[]{10, 1, 10, 10};
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
    }
}
