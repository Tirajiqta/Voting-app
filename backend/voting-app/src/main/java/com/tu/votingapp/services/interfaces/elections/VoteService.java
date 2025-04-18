package com.tu.votingapp.services.interfaces.elections;

import com.tu.votingapp.dto.request.elections.VoteRequestDTO;
import com.tu.votingapp.dto.response.elections.VoteResponseDTO;

public interface VoteService {
    VoteResponseDTO castVote(VoteRequestDTO request);
}
