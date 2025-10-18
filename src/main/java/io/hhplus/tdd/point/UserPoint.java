package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.BaseException;
import io.hhplus.tdd.common.response.ErrorCode;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MAX_POINT = 100000L;
    private static final long MIN_CHARGE_AMOUNT = 100L;
    private static final long MIN_POINT = 0L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public long chargedPoint(long amount) {
        if (amount < MIN_CHARGE_AMOUNT) {
            throw new BaseException(ErrorCode.POINT_LESS_THAN_100);
        }

        if (this.point + amount > MAX_POINT) {
            throw new BaseException(ErrorCode.POINT_BALANCE_OVER);
        }

        return this.point + amount;
    }

    public long leftPointAfterUse(long amount) {
        if (amount < MIN_CHARGE_AMOUNT) {
            throw new BaseException(ErrorCode.POINT_LESS_THAN_100);
        }

        if (this.point - amount < MIN_POINT) {
            throw new BaseException(ErrorCode.POINT_BALANCE_NEGATIVE);
        }

        return this.point - amount;
    }
}
