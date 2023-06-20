package com.artbridge.artwork.infrastructure.messaging;

public interface MemberInPort {
    void modifyMemberName(Long id, String name);
}
