package com.zb.cinema.domain.admin.model.response;

import com.zb.cinema.domain.member.entity.Member;
import com.zb.cinema.domain.member.type.MemberType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberDto {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private MemberType type;
    private LocalDateTime regDt;
    private LocalDateTime updateDt;

    public static AdminMemberDto from(Member member) {
        return AdminMemberDto.builder()
            .id(member.getId())
            .name(member.getName())
            .phone(member.getPhone())
            .email(member.getEmail())
            .type(member.getType())
            .regDt(member.getRegDt())
            .updateDt(member.getUpdateDt()).build();
    }
}