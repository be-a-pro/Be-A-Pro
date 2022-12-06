package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JoinStatus {
    APPLY("지원"),
    EXAMINING("열람 중"),
    LEADER_APPROVAL("팀장 승인"),
    LEADER_REFUSAL("팀장 거절"),
    APPLICANT_REFUSAL("지원자 승인");

    private final String title;
}
