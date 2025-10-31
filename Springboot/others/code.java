//        System.out.println("general filter");
//        http.cors() // Enable CORS support
//                .and()
//                .csrf().disable() // Disable CSRF if not needed
//                .authorizeHttpRequests()
//                .anyRequest().permitAll(); // Adjust this to your needs


/*
public void testing() {
        String baseUrl = "https://api.coincap.io/v2/candles";
        String baseAssetId = "bitcoin";
        String quoteAssetId = "usd";
        LocalDate targetDate = LocalDate.of(2024, 11, 1);  // December 21, 2024
        LocalDate endDate = LocalDate.of(2024, 11, 30);
        // Start of the day (midnight UTC) in milliseconds
        long start = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        // End of the day (11:59:59 UTC) in milliseconds
        long end = endDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();

        // Construct the URL with the correct query parameters
        String url = String.format("%s?baseId=%s&quoteId=%s&interval=d1&start=%d&end=%d",
        baseUrl, baseAssetId, quoteAssetId, start, end);

        RestTemplate restTemplate = new RestTemplate();

        }
*/

import io.github.tiagofdev.applicationgla.dto.AlertsDTO;
import io.github.tiagofdev.applicationgla.model.AlertEntity;
import io.github.tiagofdev.applicationgla.model.PriceEntity;
import io.github.tiagofdev.applicationgla.model.UserEntity;
import io.github.tiagofdev.applicationgla.repository.AlertEntityRepository;
import io.github.tiagofdev.applicationgla.repository.PriceEntityRepository;
import io.github.tiagofdev.applicationgla.repository.UserEntityRepository;
import io.github.tiagofdev.applicationgla.service.ServiceAlert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServiceAlertTest {

    private ServiceAlert serviceAlert;
    private AlertEntityRepository alertEntityRepository;
    private UserEntityRepository userEntityRepository;
    private PriceEntityRepository priceEntityRepository;

    @Before
    public void setUp() {
        alertEntityRepository = mock(AlertEntityRepository.class);
        userEntityRepository = mock(UserEntityRepository.class);
        priceEntityRepository = mock(PriceEntityRepository.class);

        // Initialize the ServiceAlert instance
        serviceAlert = new ServiceAlert(alertEntityRepository, userEntityRepository, null, priceEntityRepository);
    }


}


/*
* //    @Bean
//    public JavaMailSender getJavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.mail.yahoo.com");
//        mailSender.setPort(465);
//
//        mailSender.setUsername("cryptosurge251@yahoo.com");
//        mailSender.setPassword("h;{p5@]%nK~yek62");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.debug", "true");
//        // should not be present in production
//        props.put("mail.smtp.ssl.trust", "*"); // Trust all certificates - Temporary for testing, insecure,
//        return mailSender;
//    }

* */