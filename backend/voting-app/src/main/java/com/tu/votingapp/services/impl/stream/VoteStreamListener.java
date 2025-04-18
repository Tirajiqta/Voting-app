package com.tu.votingapp.services.impl.stream;

import com.tu.votingapp.analytics.voting.FeatureAggregator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VoteStreamListener {
    private final FeatureAggregator aggregator;

    public VoteStreamListener(FeatureAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @KafkaListener(topics = "votes", groupId = "voting-analytics-group")
    public void onVoteEvent(String message) {
        // message is JSON: {"electionId":...,"candidateId":...,"partyId":...,"timestamp":...}
        aggregator.aggregateEvent(message);
    }
}
