import com.digipay.cardmanager.model.Card;
import org.junit.Test;

import java.util.concurrent.ConcurrentSkipListMap;

public class MyTest {

    @Test
    public void test(){
        ConcurrentSkipListMap<Long, Card> cards = new ConcurrentSkipListMap<>();
//        cards.(10L, new Card(1L, 10000L));
        cards.putIfAbsent(10L, new Card(1L, 10000L));
    }
}
