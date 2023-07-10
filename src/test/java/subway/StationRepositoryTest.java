package subway;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestConstructor(autowireMode = AutowireMode.ALL)
@DataJpaTest
class StationRepositoryTest {

    //@Autowired
    private StationRepository stations;

    public StationRepositoryTest(final StationRepository stations) {
        this.stations = stations;
    }

    @Test
    void save() {
        Station expected = new Station("잠실역");
        Station actual = stations.save(expected);
        Station actual2 = stations.save(new Station("잠실역")); //키워드 : 영속성 컨텍스트
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName())
        );
    }

    @Test
    void findByName() {
        String expected = "잠실역";
        stations.save(new Station(expected));
        String actual = stations.findByName(expected).getName();
        assertThat(actual).isEqualTo(expected);
    }

    @Test //동일성 테스트
    void identity() {
        Station station1 = stations.save(new Station("잠실역"));
        //Station station2 = stations.findById(station1.getId()).get(); //1차 캐시.내가 데이베이스 왜가야함?하고 셀렉트 쿼리x
        Station station2 = stations.findByName(station1.getName()); //name은 키값이 아님. 데이터베이스를 갔다오는게 비용 더 좋다! id값이 아니면 무조건 데이터베이스 조회
        assertThat(station1 == station2).isTrue();
    }

    @Test
    void update() {
        Station station1 = stations.save(new Station("잠실역"));
        station1.changeName("선릉역"); //여기서 코드가 끝나면 update 쿼리가 나가지 않음. 어차피 테스트 끝나면 롤백한건데 왜 커밋해야함?하고 안나감
/*        station1.changeName("잠실역"); //최종상태는 잠실역이기 때문에 update 쿼리가 실행되지 않음
        Station station2 = stations.findByName("선릉역"); //디비를 조회하는 메서드 전에 flush 그래서 update 쿼리가 생성된 것임
        assertThat(station2).isNotNull();*/
    }
}
