package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.BaseException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PointService 클래스")
class PointServiceTest {
    private PointService pointService;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        userPointTable = mock(UserPointTable.class);
        pointHistoryTable = mock(PointHistoryTable.class);
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Nested
    @DisplayName("point 메서드는")
    class Describe_of_point {
        @Nested
        @DisplayName("만약 새로 생성된 사용자 식별자가 주어진다면")
        class Context_with_empty_user {
            private static final long EMPTY_USER_ID = 1L;
            private final UserPoint emptyUserPoint = UserPoint.empty(EMPTY_USER_ID);

            @BeforeEach
            void prepare() {
                when(userPointTable.selectById(EMPTY_USER_ID)).thenReturn(emptyUserPoint);
            }

            @Test
            @DisplayName("포인트 0을 리턴한다")
            void it_returns_zero_point() {
                UserPoint userPoint = pointService.point(EMPTY_USER_ID);

                Assertions.assertEquals(userPoint.point(), 0L);
            }
        }

        @Nested
        @DisplayName("만약 유효하지 않은 사용자 식별자가 주어진다면")
        class Context_with_invalid_user_id {
            private static final long INVALID_USER_ID = -1L;

            @Test
            @DisplayName("유효하지 않은 식별자라는 예외를 리턴한다")
            void it_throws_invalid_user_id() {
                assertThatThrownBy(() -> pointService.point(INVALID_USER_ID))
                        .isInstanceOf(BaseException.class);
            }
        }
    }

    @Nested
    @DisplayName("history 메서드는")
    class Describe_of_history {
        @Nested
        @DisplayName("만약 존재하는 사용자 식별자가 주어진다면")
        class Context_with_existed_user {
            private static final long EXISTED_POINT_HISTORY_1 = 1L;
            private static final long EXISTED_POINT_HISTORY_2 = 2L;
            private static final long EXISTED_USER_ID = 1L;
            private static final long AMOUNT_1000 = 1000L;
            private static final long AMOUNT_3000 = 3000L;
            private static final TransactionType TRANSACTION_TYPE_CHARGE = TransactionType.CHARGE;

            PointHistory pointHistory_1 = new PointHistory(
                    EXISTED_POINT_HISTORY_1, EXISTED_USER_ID, AMOUNT_1000, TRANSACTION_TYPE_CHARGE, System.currentTimeMillis()
            );

            PointHistory pointHistory_2 = new PointHistory(
                    EXISTED_POINT_HISTORY_2, EXISTED_USER_ID, AMOUNT_3000, TRANSACTION_TYPE_CHARGE, System.currentTimeMillis()
            );

            @BeforeEach
            void prepare() {
                List<PointHistory> pointHistories = new ArrayList<>();
                pointHistories.add(pointHistory_1);
                pointHistories.add(pointHistory_2);
                when(pointHistoryTable.selectAllByUserId(EXISTED_USER_ID)).thenReturn(pointHistories);
            }

            @Test
            @DisplayName("해당 사용자의 모든 포인트 내역을 리턴한다")
            void it_returns_zero_point() {
                List<PointHistory> histories = pointService.history(EXISTED_USER_ID);

                Assertions.assertEquals(histories.size(), 2);
                Assertions.assertEquals(histories.get(0).amount(), AMOUNT_1000);
                Assertions.assertEquals(histories.get(1).amount(), AMOUNT_3000);
            }
        }

        @Nested
        @DisplayName("만약 유효하지 않은 사용자 식별자가 주어진다면")
        class Context_with_invalid_user_id {
            private static final long INVALID_USER_ID = -1L;

            @Test
            @DisplayName("유효하지 않은 식별자라는 예외를 리턴한다")
            void it_throws_invalid_user_id() {
                assertThatThrownBy(() -> pointService.history(INVALID_USER_ID))
                        .isInstanceOf(BaseException.class);
            }
        }
    }
}
