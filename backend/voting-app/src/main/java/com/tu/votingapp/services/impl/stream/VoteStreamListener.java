package com.tu.votingapp.services.impl.stream;

import com.tu.votingapp.analytics.voting.FeatureAggregator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class VoteStreamListener {
    private final FeatureAggregator aggregator;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public VoteStreamListener(FeatureAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @KafkaListener(topics = "votes", groupId = "voting-analytics-group")
    public void onVoteEvent(String message) {
        logger.info(() -> "Received vote event message, length=" + message.length());
        try {
            aggregator.aggregateEvent(message);
            logger.fine(() -> "Successfully processed vote event");
        } catch (Exception ex) {
            logger.severe("Failed to process vote event, exception = " + ex);
        }
    }
}
