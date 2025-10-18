package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.BaseException;
import io.hhplus.tdd.common.response.ErrorCode;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PointServiceIntegrationTest {
    @Autowired private PointService pointService;
    @Autowired private UserPointTable userPointTable;
    @Autowired private PointHistoryTable pointHistoryTable;

    @Nested
    @DisplayName("charge 메서드는")
    class Describe_of_charge {
        private static final long EXISTED_USER_ID = 1L;
        private static final long AMOUNT_50 = 50L;
        private static final long AMOUNT_3000 = 3000L;
        private static final long AMOUNT_100000 = 100000L;

        @BeforeEach
        void prepare() {
            userPointTable.delete(EXISTED_USER_ID);
            pointHistoryTable.deleteAll();
        }

        @Nested
        @DisplayName("만약 존재하는 사용자 식별자와 충전금액이 주어진다면")
        class Context_with_existed_user_id_and_amount {
            @Test
            @DisplayName("기존 금액과 요청 금액을 충전한 유저포인트를 리턴한다")
            void it_returns_charged_user_point() {
                UserPoint userPoint = pointService.charge(EXISTED_USER_ID, AMOUNT_3000);
                List<PointHistory> histories = pointService.history(EXISTED_USER_ID);

                Assertions.assertEquals(userPoint.point(), AMOUNT_3000,
                        "기존 0원에 충전 요청금액 3000원을 더하여 3000원을 리턴한다");
                Assertions.assertEquals(histories.size(), 1,
                        "포인트를 1번 충전하면 포인트 이력 조회는 1개를 리턴한다");
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
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                        });
            }
        }

        @Nested
        @DisplayName("만약 충전 후 금액이 최대를 초과한다면")
        class Context_with_amount_over_max_point {
            @Test
            @DisplayName("충전금액을 초과했다는 예외를 던진다")
            void it_throws_over_max_amount_exception() {
                pointService.charge(EXISTED_USER_ID, AMOUNT_3000);
                assertThatThrownBy(() -> pointService.charge(EXISTED_USER_ID, AMOUNT_100000))
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.POINT_BALANCE_OVER);
                        });
            }
        }

        @Nested
        @DisplayName("만약 100원 미만의 금액을 충전한다면")
        class Context_with_less_than_100_point {
            @Test
            @DisplayName("최소 100원 이상을 충전해야한다는 예외를 던진다")
            void it_throws_less_than_100_point_exception() {
                assertThatThrownBy(() -> pointService.charge(EXISTED_USER_ID, AMOUNT_50))
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.POINT_LESS_THAN_100);
                        });
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

        @BeforeEach
        void prepare() {
            userPointTable.delete(EXISTED_USER_ID);
            pointHistoryTable.deleteAll();
        }

        @Nested
        @DisplayName("만약 존재하는 사용자 식별자와 충전금액이 주어진다면")
        class Context_with_existed_user_id_and_amount {
            @Test
            @DisplayName("기존 금액에서 요청 금액을 뺀 유저포인트를 리턴한다")
            void it_returns_left_user_point_after_use() {
                UserPoint chargedUserPoint = pointService.charge(EXISTED_USER_ID, AMOUNT_4000);
                UserPoint userPoint = pointService.use(EXISTED_USER_ID, AMOUNT_3000);
                List<PointHistory> histories = pointService.history(EXISTED_USER_ID);

                Assertions.assertEquals(userPoint.point(), AMOUNT_1000,
                        "기존 4000원에 사용 금액 1000원을 뺀 3000원을 리턴한다");
                Assertions.assertEquals(histories.size(), 2,
                        "포인트를 1번 충전하고 1번 사용하면 포인트 이력 조회는 2개를 리턴한다");
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
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                        });
            }
        }

        @Nested
        @DisplayName("만약 사용 후 금액이 0원 미만이라면")
        class Context_with_left_point_less_than_zero {
            @Test
            @DisplayName("포인트 잔액은 0보다 커야한다는 예외를 던진다")
            void it_throws_over_using_possible_amount_exception() {
                assertThatThrownBy(() -> pointService.use(EXISTED_USER_ID, AMOUNT_100000))
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.POINT_BALANCE_NEGATIVE);
                        });
            }
        }

        @Nested
        @DisplayName("만약 100원 미만의 금액을 사용한다면")
        class Context_with_less_than_100_point {
            @Test
            @DisplayName("최소 100원 이상을 사용해야한다는 예외를 던진다")
            void it_throws_less_than_100_point_exception() {
                assertThatThrownBy(() -> pointService.use(EXISTED_USER_ID, AMOUNT_50))
                        .isInstanceOf(BaseException.class)
                        .satisfies(ex -> {
                            BaseException ce = (BaseException) ex;
                            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.POINT_LESS_THAN_100);
                        });
            }
        }
    }
}
