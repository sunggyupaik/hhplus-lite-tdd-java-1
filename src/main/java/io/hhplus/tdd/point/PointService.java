package io.hhplus.tdd.point;

import io.hhplus.tdd.common.response.ErrorCode;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;

    public UserPoint point(long id) {
        if (id <= 0L) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }

        return userPointTable.selectById(id);
    }
}
