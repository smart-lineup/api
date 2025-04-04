package com.jun.smartlineup.payment.util;

import java.util.*;

public class TossFailUtil {
    public static boolean isFailBaseOnUser(String code) {
        Map<String, String> failMap = getFailMap();
        return failMap.containsKey(code);
    }

    public static String getMessageBasedOnCode(String code) {
        Map<String, String> failMap = getFailMap();
        return failMap.getOrDefault(code, "예기치 못한 에러가 발생하였습니다. 문의 부탁드립니다.");
    }

    private static Map<String, String> getFailMap() {
        Map<String, String> failMap = new HashMap<>();
        failMap.put("INVALID_REJECT_CARD", "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.");
        failMap.put("INVALID_CARD_EXPIRATION", "카드 정보를 다시 확인해주세요. (유효기간)");
        failMap.put("INVALID_STOPPED_CARD", "정지된 카드 입니다.");
        failMap.put("EXCEED_MAX_DAILY_PAYMENT_COUNT", "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.");
        failMap.put("EXCEED_MAX_PAYMENT_AMOUNT", "하루 결제 가능 금액을 초과했습니다.");
        failMap.put("INVALID_CARD_LOST_OR_STOLEN", "분실 혹은 도난 카드입니다.");
        failMap.put("INVALID_CARD_NUMBER", "카드번호를 다시 확인해주세요.");
        failMap.put("EXCEED_MAX_AMOUNT", "거래금액 한도를 초과했습니다.");
        failMap.put("EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT", "당월 결제 가능금액인 1,000,000원을 초과 하셨습니다.");
        failMap.put("REJECT_ACCOUNT_PAYMENT", "잔액부족으로 결제에 실패했습니다.");
        failMap.put("REJECT_CARD_PAYMENT", "한도초과 혹은 잔액부족으로 결제에 실패했습니다.");
        failMap.put("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다.");
        failMap.put("EXCEED_MAX_AUTH_COUNT", "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요.");
        failMap.put("EXCEED_MAX_ONE_DAY_AMOUNT", "일일 한도를 초과했습니다.");
        return failMap;
    }
}
