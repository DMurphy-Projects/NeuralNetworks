public class TrainingDataSample {

    public static Double[][][] or()
    {
        return new Double[][][]
                {
                        {{0.01d, 0.01d}, {0.01d}},
                        {{0.99d, 0.01d}, {0.99d}},
                        {{0.01d, 0.99d}, {0.99d}},
                        {{0.99d, 0.99d}, {0.99d}},
                };
    }

    public static Double[][][] xor()
    {
        return new Double[][][]
                {
                        {{0.01d, 0.01d}, {0.01d}},
                        {{0.99d, 0.01d}, {0.99d}},
                        {{0.01d, 0.99d}, {0.99d}},
                        {{0.99d, 0.99d}, {0.01d}},
                };
    }

    public static Double[][][] and()
    {
        return new Double[][][]
                {
                        {{0.01d, 0.01d}, {0.01d}},
                        {{0.99d, 0.01d}, {0.01d}},
                        {{0.01d, 0.99d}, {0.01d}},
                        {{0.99d, 0.99d}, {0.99d}},
                };
    }
}
