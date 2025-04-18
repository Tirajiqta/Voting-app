package com.tu.votingapp.analytics.voting;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration.GraphBuilder;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfig {
    @Bean
    public ComputationGraph analyticsModel() {
        int numFeatures = 10;      // feature vector size
        int numCandidates = 5;     // adjust per election
        int hiddenSize = 32;

        GraphBuilder gb = new NeuralNetConfiguration.Builder()
                .updater(new Adam(1e-3))
                .graphBuilder()
                .addInputs("features")
                // shared dense layer
                .addLayer("dense1", new DenseLayer.Builder()
                                .nIn(numFeatures)
                                .nOut(hiddenSize)
                                .activation(Activation.valueOf("relu")).build(),
                        "features")
                // Winner-probability output
                .addLayer("winnerOut",
                        new OutputLayer.Builder()
                                .nIn(hiddenSize).nOut(numCandidates)
                                .activation(Activation.valueOf("softmax"))
                                .lossFunction(org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MCXENT)
                                .build(),
                        "dense1")
                // Turnout prediction output
                .addLayer("turnoutOut",
                        new OutputLayer.Builder()
                                .nIn(hiddenSize).nOut(1)
                                .activation(Activation.valueOf("identity"))
                                .lossFunction(org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MSE)
                                .build(),
                        "dense1")
                .setOutputs("winnerOut", "turnoutOut");

        ComputationGraphConfiguration conf = gb.build();
        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));
        return model;
    }
}

