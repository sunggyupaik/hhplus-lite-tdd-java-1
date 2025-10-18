package io.hhplus.tdd.point;

import io.hhplus.tdd.common.response.ErrorCode;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint point(long id) {
        if (id <= 0L) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }

        return userPointTable.selectById(id);
    }

    public List<PointHistory> history(long id) {
        if (id <= 0L) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }

        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {
        if (id <= 0L) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }

        UserPoint userPoint = userPointTable.selectById(id);
        long chargedPoint = userPoint.chargedPoint(amount);
        UserPoint result = userPointTable.insertOrUpdate(id, chargedPoint);
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return result;
    }

    public UserPoint use(long id, long amount) {
        if (id <= 0L) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }

        UserPoint userPoint = userPointTable.selectById(id);
        long leftPoint = userPoint.leftPointAfterUse(amount);
        UserPoint result = userPointTable.insertOrUpdate(id, leftPoint);
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return result;
    }
}
