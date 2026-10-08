package gdg.challenge.poom.domain.member.converter;

import gdg.challenge.poom.domain.member.dto.response.CoupleResponseDTO;
import gdg.challenge.poom.domain.member.entity.Couple;
import gdg.challenge.poom.domain.member.entity.Member;

public class CoupleConverter {

//    public static Couple createCouple(Member memberA, Member memberB) {
//        return Couple.builder()
//                .memberA(memberA)
//                .memberB(memberB)
//                .build();
//    }

    public static CoupleResponseDTO.CreatedCouple toCreatedCouple(Couple couple){
        return CoupleResponseDTO.CreatedCouple.builder()
                .coupleId(couple.getId())
                .codeSubmitterId(couple.getMemberB().getId())
                .codeOwnerId(couple.getMemberA().getId())
                .codeSubmitterNickname(couple.getMemberB().getName())
                .codeOwnerNickname(couple.getMemberA().getName())
                .createdAt(couple.getCreatedAt())
                .build();
    }

    public static CoupleResponseDTO.ChangeStatusCouple toChangeStatusCouple(Long partnerId,Long memberId, Couple couple){
        return CoupleResponseDTO.ChangeStatusCouple.builder()
                .memberId(memberId)
                .partnerId(partnerId)
                .coupleStatus(couple.getCoupleStatus())
                .build();
    }

}
