package com.tu.votingapp.services.interfaces.elections;

import com.tu.votingapp.dto.general.elections.VoteDTO;
import com.tu.votingapp.dto.request.elections.VoteRequestDTO;

public interface VoteService {
    VoteDTO castVote(VoteRequestDTO request);
}
