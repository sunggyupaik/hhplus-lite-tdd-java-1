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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        @DisplayName("만약 존재하지 않은 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final long NOT_EXISTED_USER_ID = -1L;

            @Test
            @DisplayName("유효하지 않은 식별자라는 예외를 리턴한다")
            void it_throws_not_existed_user_id_exception() {
                assertThatThrownBy(() -> pointService.point(NOT_EXISTED_USER_ID))
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
            void it_returns_all_histories() {
                List<PointHistory> histories = pointService.history(EXISTED_USER_ID);

                Assertions.assertEquals(histories.size(), 2);
                Assertions.assertEquals(histories.get(0).amount(), AMOUNT_1000);
                Assertions.assertEquals(histories.get(1).amount(), AMOUNT_3000);
            }
        }

        @Nested
        @DisplayName("만약 존재하지 않은 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final long NOT_EXISTED_USER_ID = -1L;

            @Test
            @DisplayName("유효하지 않은 식별자라는 예외를 리턴한다")
            void it_throws_not_existed_user_id_exception() {
                assertThatThrownBy(() -> pointService.history(NOT_EXISTED_USER_ID))
                        .isInstanceOf(BaseException.class);
            }
        }
    }

    @Nested
    @DisplayName("charge 메서드는")
    class Describe_of_charge {
        private static final long EXISTED_USER_ID = 1L;
        private static final long AMOUNT_50 = 50L;
        private static final long AMOUNT_1000 = 1000L;
        private static final long AMOUNT_3000 = 3000L;
        private static final long AMOUNT_4000 = 4000L;
        private static final long AMOUNT_100000 = 100000L;

        UserPoint initUserPoint = new UserPoint(EXISTED_USER_ID, AMOUNT_1000, System.currentTimeMillis());
        UserPoint chargedUserPoint = new UserPoint(EXISTED_USER_ID, AMOUNT_4000, System.currentTimeMillis());

        @BeforeEach
        void prepare() {
            when(userPointTable.selectById(EXISTED_USER_ID)).thenReturn(initUserPoint);
            when(userPointTable.insertOrUpdate(EXISTED_USER_ID, AMOUNT_4000)).thenReturn(chargedUserPoint);
        }

        @Nested
        @DisplayName("만약 존재하는 사용자 식별자와 충전금액이 주어진다면")
        class Context_with_existed_user_id_and_amount {
            @Test
            @DisplayName("기존 금액과 요청 금액을 충전한 유저포인트를 리턴한다")
            void it_returns_charged_user_point() {
                UserPoint userPoint = pointService.charge(EXISTED_USER_ID, AMOUNT_3000);

                Assertions.assertEquals(userPoint.point(), AMOUNT_4000,
                        "기존 1000원에 요청금액 3000원을 더하여 4000원을 리턴한다");

                verify(userPointTable, times(1)).selectById(EXISTED_USER_ID);
                verify(userPointTable, times(1)).insertOrUpdate(EXISTED_USER_ID, AMOUNT_4000);
            }
        }

        @Nested
        @DisplayName("만약 존재하지 않은 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final long NOT_EXISTED_USER_ID = -1L;

            @Test
            @DisplayName("존재하지 않는 식별자라는 예외를 리턴한다")
            void it_throws_not_existed_user_id_exception() {
                assertThatThrownBy(() -> pointService.charge(NOT_EXISTED_USER_ID, AMOUNT_3000))
                        .isInstanceOf(BaseException.class);
            }
        }

        @Nested
        @DisplayName("만약 충전 후 금액이 최대를 초과한다면")
        class Context_with_amount_over_max_point {
            @Test
            @DisplayName("충전금액을 초과했다는 예외를 던진다")
            void it_throws_over_max_amount_exception() {
                assertThatThrownBy(() -> pointService.charge(EXISTED_USER_ID, AMOUNT_100000))
                        .isInstanceOf(BaseException.class);
            }
        }

        @Nested
        @DisplayName("만약 100원 미만의 금액을 충전한다면")
        class Context_with_less_than_100_point {
            @Test
            @DisplayName("최소 100원 이상을 충전해야한다는 예외를 던진다")
            void it_throws_less_than_100_point_exception() {
                assertThatThrownBy(() -> pointService.charge(EXISTED_USER_ID, AMOUNT_50))
                        .isInstanceOf(BaseException.class);
            }
        }
    }

    @Nested
    @DisplayName("use 메서드는")
    class Describe_of_use {
        private static final long EXISTED_USER_ID = 1L;
        private static final long AMOUNT_50 = 50L;
        private static final long AMOUNT_1000 = 1000L;
        private static final long AMOUNT_3000 = 3000L;
        private static final long AMOUNT_4000 = 4000L;
        private static final long AMOUNT_100000 = 100000L;

        UserPoint initUserPoint = new UserPoint(EXISTED_USER_ID, AMOUNT_4000, System.currentTimeMillis());
        UserPoint leftUserPoint = new UserPoint(EXISTED_USER_ID, AMOUNT_1000, System.currentTimeMillis());

        @BeforeEach
        void prepare() {
            when(userPointTable.selectById(EXISTED_USER_ID)).thenReturn(initUserPoint);
            when(userPointTable.insertOrUpdate(EXISTED_USER_ID, AMOUNT_3000)).thenReturn(leftUserPoint);
        }

        @Nested
        @DisplayName("만약 존재하는 사용자 식별자와 충전금액이 주어진다면")
        class Context_with_existed_user_id_and_amount {
            @Test
            @DisplayName("기존 금액에서 요청 금액을 뺀 유저포인트를 리턴한다")
            void it_returns_left_user_point_after_use() {
                UserPoint userPoint = pointService.use(EXISTED_USER_ID, AMOUNT_1000);
                System.out.println(userPoint);

                Assertions.assertEquals(userPoint.point(), AMOUNT_1000,
                        "기존 4000원에 요청금액 3000원을 뺀 1000원을 리턴한다");

                verify(userPointTable, times(1)).selectById(EXISTED_USER_ID);
                verify(userPointTable, times(1)).insertOrUpdate(EXISTED_USER_ID, AMOUNT_3000);
            }
        }

        @Nested
        @DisplayName("만약 존재하지 않은 사용자 식별자가 주어진다면")
        class Context_with_not_existed_user_id {
            private static final long NOT_EXISTED_USER_ID = -1L;

            @Test
            @DisplayName("존재하지 않는 식별자라는 예외를 리턴한다")
            void it_throws_not_existed_user_id_exception() {
                assertThatThrownBy(() -> pointService.use(NOT_EXISTED_USER_ID, AMOUNT_3000))
                        .isInstanceOf(BaseException.class);
            }
        }

        @Nested
        @DisplayName("만약 사용 후 금액이 0원 미만이라면")
        class Context_with_left_point_less_than_zero {
            @Test
            @DisplayName("포인트 잔액은 0보다 커야한다는 예외를 던진다")
            void it_throws_over_using_possible_amount_exception() {
                assertThatThrownBy(() -> pointService.use(EXISTED_USER_ID, AMOUNT_100000))
                        .isInstanceOf(BaseException.class);
            }
        }

        @Nested
        @DisplayName("만약 100원 미만의 금액을 사용한다면")
        class Context_with_less_than_100_point {
            @Test
            @DisplayName("최소 100원 이상을 사용해야한다는 예외를 던진다")
            void it_throws_less_than_100_point_exception() {
                assertThatThrownBy(() -> pointService.use(EXISTED_USER_ID, AMOUNT_50))
                        .isInstanceOf(BaseException.class);
            }
        }
    }
}
