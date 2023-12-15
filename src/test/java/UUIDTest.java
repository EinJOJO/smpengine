import it.einjojo.smpengine.util.NameUUIDCache;

import java.util.UUID;

public class UUIDTest {


    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello, World!");
        for (int i = 0; i < 4; i++) {
            UUID uuid = NameUUIDCache.getUUID("Ein_JoJo");
            System.out.println(uuid);
            Thread.sleep(1000);
        }
    }

}
