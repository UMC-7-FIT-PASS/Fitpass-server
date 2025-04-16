package com.example.fitpassserver.global.common.support;

import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.domain.member.repository.MemberRepository;
import com.example.fitpassserver.owner.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginUserFinderImpl implements LoginUserFinder {

    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public Optional<? extends LoginUser> findByLoginId(String loginId) {
        Optional<Member> member = memberRepository.findByLoginId(loginId);
        if (member.isPresent()) {
            return member;
        }
        return ownerRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<? extends LoginUser> findByNameAndPhoneNumber(String name, String phoneNumber) {
        Optional<Member> member = memberRepository.findByNameAndPhoneNumber(name, phoneNumber);
        if (member.isPresent()) {
            return member;
        }
        return ownerRepository.findByNameAndPhoneNumber(name, phoneNumber);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId) || ownerRepository.existsByLoginId(loginId);
    }


    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber) || ownerRepository.existsByPhoneNumber(phoneNumber);
    }
}

