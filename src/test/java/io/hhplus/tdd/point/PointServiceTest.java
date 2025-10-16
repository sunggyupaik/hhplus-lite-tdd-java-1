package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PointService 클래스")
class PointServiceTest {
    private PointService pointService;
    private UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = mock(UserPointTable.class);
        pointService = new PointService(userPointTable);
    }

    @Nested
    @DisplayName("point 메서드는")
    class Describe_of {
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
    }
}
