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

import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class ModelConfig {
    private final Logger logger = Logger.getLogger(ModelConfig.class.getName());

    @Bean
    public ComputationGraph analyticsModel() {
        logger.info("Initializing analyticsModel computation graph configuration");
        int numFeatures = 10;      // feature vector size
        int numCandidates = 5;     // adjust per election
        int hiddenSize = 32;

        try {
            GraphBuilder gb = new NeuralNetConfiguration.Builder()
                    .updater(new Adam(1e-3))
                    .graphBuilder()
                    .addInputs("features")
                    // shared dense layer
                    .addLayer("dense1", new DenseLayer.Builder()
                                    .nIn(numFeatures)
                                    .nOut(hiddenSize)
                                    .activation(Activation.RELU)
                                    .build(),
                            "features")
                    // Winner-probability output
                    .addLayer("winnerOut",
                            new OutputLayer.Builder()
                                    .nIn(hiddenSize)
                                    .nOut(numCandidates)
                                    .activation(Activation.SOFTMAX)
                                    .lossFunction(
                                            org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MCXENT)
                                    .build(),
                            "dense1")
                    // Turnout prediction output
                    .addLayer("turnoutOut",
                            new OutputLayer.Builder()
                                    .nIn(hiddenSize)
                                    .nOut(1)
                                    .activation(Activation.IDENTITY)
                                    .lossFunction(
                                            org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MSE)
                                    .build(),
                            "dense1")
                    .setOutputs("winnerOut", "turnoutOut");

            ComputationGraphConfiguration conf = gb.build();
            logger.fine("Model configuration built successfully");
            ComputationGraph model = new ComputationGraph(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(10));
            logger.info("ComputationGraph initialized and listeners attached");
            return model;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize analyticsModel", e);
            throw new IllegalStateException("Could not create analyticsModel", e);
        }
    }
}