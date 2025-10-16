package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.BaseException;
import io.hhplus.tdd.common.response.ErrorCode;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static final long MAX_POINT = 100000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public long chargedPoint(long amount) {
        if (this.point + amount > MAX_POINT) {
            throw new BaseException(ErrorCode.POINT_BALANCE_OVER);
        }

        return this.point + amount;
    }
}
