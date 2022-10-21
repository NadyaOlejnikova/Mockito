import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MockTest {

    private static final String USER_IP = "172.";
    private static final String USER_IP_NOT_RUS = "96.";
    /**
     * Аннотация @Mock эквивалетна записи Mockito.mock(PreferencesService.class).
     */
    @Mock
    private GeoService geoService;
    @Mock
    private LocalizationService localizationService;
    @Mock
    private MessageSender messageSender;
    Map<String, String> headers = new HashMap<>();
    String text;

    @BeforeEach
    void setUp() {
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        geoService = Mockito.mock(GeoServiceImpl.class);
        messageSender = new MessageSenderImpl(geoService, localizationService);
    }

    @Test
    @DisplayName("Русское сообщение по IP")
    void textForRussianIP() {
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, USER_IP);
        text = "Добро пожаловать";
        Mockito.when(geoService.byIp(USER_IP))
                .thenReturn(new Location("Moscow", Country.RUSSIA, "Leninskiy", 50));
        Mockito.when(localizationService.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");
        Assertions.assertEquals(text, messageSender.send(headers));
    }

    @Test
    @DisplayName("Не русское сообщение")
    void textForNotRUSIP() {
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, USER_IP_NOT_RUS);
        String text = "Welcome";
        Mockito.when(geoService.byIp(USER_IP_NOT_RUS))
                .thenReturn(new Location("New York", Country.USA, "BaykerStreet", 50));
        Mockito.when(localizationService.locale(Country.USA))
                .thenReturn("Welcome");
        Assertions.assertEquals(text, messageSender.send(headers));
    }

    @Test
    @DisplayName("проверка определения локации по ip (класс GeoServiceImpl)")
    void getLocation() {
        geoService = new GeoServiceImpl();
        Location location = new Location("Moskow", Country.RUSSIA, "Cadovaya-302 bis", 50);
        Country country = location.getCountry();
        Country resultTest = geoService.byIp(USER_IP).getCountry();
        Assertions.assertEquals(country, resultTest);
    }

    @Test
    @DisplayName("тест для проверки возвращаемого текста")
    void returnText() {
        localizationService = new LocalizationServiceImpl();
        String text = "Добро пожаловать";
        Assertions.assertEquals(text, localizationService.locale(Country.RUSSIA));
    }
}



