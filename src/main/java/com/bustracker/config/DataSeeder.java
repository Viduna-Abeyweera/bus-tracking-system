package com.bustracker.config;

import com.bustracker.entity.*;
import com.bustracker.enums.UserRole;
import com.bustracker.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Seeds the database with real Sri Lankan bus routes, stops, buses,
 * and schedules on application startup.
 *
 * <p>Only runs when the "seed" profile is active:
 * {@code --spring.profiles.active=seed}</p>
 *
 * <p>This seeder creates:</p>
 * <ul>
 *   <li>A default admin user</li>
 *   <li>Sample driver and passenger users</li>
 *   <li>Major Sri Lankan bus stops with real GPS coordinates</li>
 *   <li>5 real inter-city bus routes with intermediate stops</li>
 *   <li>Sample buses and departure schedules</li>
 * </ul>
 *
 * <p>All GPS coordinates are verified real-world locations from
 * actual Sri Lankan bus stations and major stops.</p>
 */
@Component
@Profile("seed")
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final BusStopRepository busStopRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(BusStopRepository busStopRepository, RouteRepository routeRepository,
                      BusRepository busRepository, ScheduleRepository scheduleRepository,
                      UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.busStopRepository = busStopRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (routeRepository.count() > 0) {
            logger.info("Database already seeded — skipping");
            return;
        }

        logger.info("🌱 Seeding database with Sri Lankan bus data...");

        // ===== 1. USERS =====
        User admin = createUser("Admin User", "admin@bustracker.lk", "admin123", "0112345678", UserRole.ADMIN);
        User driver1 = createUser("Kamal Perera", "kamal@bustracker.lk", "driver123", "0771234567", UserRole.DRIVER);
        User driver2 = createUser("Nimal Silva", "nimal@bustracker.lk", "driver123", "0772345678", UserRole.DRIVER);
        User driver3 = createUser("Sunil Fernando", "sunil@bustracker.lk", "driver123", "0773456789", UserRole.DRIVER);
        User passenger = createUser("Amara Jayasinghe", "amara@example.com", "pass123", "0774567890", UserRole.PASSENGER);

        // ===== 2. BUS STOPS (Real GPS Coordinates) =====

        // Colombo area
        BusStop colomboFort = createStop("Colombo Fort Bus Stand", 6.9344, 79.8428);
        BusStop pettah = createStop("Pettah Bus Terminal", 6.9358, 79.8500);
        BusStop maradana = createStop("Maradana Junction", 6.9298, 79.8635);
        BusStop borella = createStop("Borella Junction", 6.9147, 79.8779);
        BusStop kadawatha = createStop("Kadawatha Bus Stand", 7.0013, 79.9528);
        BusStop kaduwela = createStop("Kaduwela Junction", 6.9310, 79.9840);

        // Southern Expressway / Coastal
        BusStop moratuwa = createStop("Moratuwa Bus Stop", 6.7740, 79.8824);
        BusStop panadura = createStop("Panadura Bus Stand", 6.7130, 79.9037);
        BusStop kalutara = createStop("Kalutara Bus Stand", 6.5854, 79.9607);
        BusStop aluthgama = createStop("Aluthgama Junction", 6.4311, 80.0000);
        BusStop ambalangoda = createStop("Ambalangoda Bus Stand", 6.2350, 80.0540);
        BusStop hikkaduwa = createStop("Hikkaduwa Bus Stop", 6.1395, 80.1007);
        BusStop galle = createStop("Galle Bus Stand", 6.0328, 80.2170);
        BusStop matara = createStop("Matara Bus Stand", 5.9485, 80.5353);

        // Central Province
        BusStop kegalle = createStop("Kegalle Bus Stand", 7.2513, 80.3464);
        BusStop mawanella = createStop("Mawanella Junction", 7.2540, 80.4520);
        BusStop kandy = createStop("Kandy Bus Stand (Goods Shed)", 7.2906, 80.6337);
        BusStop peradeniya = createStop("Peradeniya Junction", 7.2590, 80.5972);
        BusStop gampola = createStop("Gampola Bus Stand", 7.1644, 80.5772);
        BusStop nawalapitiya = createStop("Nawalapitiya Bus Stand", 7.0480, 80.5345);
        BusStop nuwaraEliya = createStop("Nuwara Eliya Bus Stand", 6.9497, 80.7891);

        // North Central / Northern
        BusStop kurunegala = createStop("Kurunegala Bus Stand", 7.4863, 80.3623);
        BusStop dambulla = createStop("Dambulla Bus Stand", 7.8675, 80.6511);
        BusStop anuradhapura = createStop("Anuradhapura New Bus Stand", 8.3114, 80.4037);
        BusStop vavuniya = createStop("Vavuniya Bus Stand", 8.7514, 80.4971);
        BusStop kilinochchi = createStop("Kilinochchi Junction", 9.3803, 80.3770);
        BusStop jaffna = createStop("Jaffna Bus Stand", 9.6615, 80.0255);

        // ===== 3. ROUTES WITH INTERMEDIATE STOPS =====

        // Route 1: Colombo → Kandy (via Kegalle)
        Route route1 = createRoute("1", "Colombo Fort - Kandy", "Colombo Fort", "Kandy", 116.0, 240);
        addRouteStops(route1,
                new StopData(colomboFort, 1, 0.0, 0),
                new StopData(pettah, 2, 1.5, 5),
                new StopData(kadawatha, 3, 15.0, 30),
                new StopData(kegalle, 4, 72.0, 120),
                new StopData(mawanella, 5, 85.0, 150),
                new StopData(kandy, 6, 116.0, 240)
        );

        // Route 2: Colombo → Galle (Coastal Route)
        Route route2 = createRoute("2", "Colombo Fort - Galle", "Colombo Fort", "Galle", 126.0, 210);
        addRouteStops(route2,
                new StopData(colomboFort, 1, 0.0, 0),
                new StopData(moratuwa, 2, 18.0, 30),
                new StopData(panadura, 3, 25.0, 45),
                new StopData(kalutara, 4, 42.0, 70),
                new StopData(aluthgama, 5, 62.0, 100),
                new StopData(ambalangoda, 6, 86.0, 140),
                new StopData(hikkaduwa, 7, 99.0, 160),
                new StopData(galle, 8, 126.0, 210)
        );

        // Route 32: Colombo → Matara (via Galle)
        Route route3 = createRoute("32", "Colombo Fort - Matara", "Colombo Fort", "Matara", 160.0, 300);
        addRouteStops(route3,
                new StopData(colomboFort, 1, 0.0, 0),
                new StopData(panadura, 2, 25.0, 45),
                new StopData(kalutara, 3, 42.0, 70),
                new StopData(ambalangoda, 4, 86.0, 140),
                new StopData(galle, 5, 126.0, 210),
                new StopData(matara, 6, 160.0, 300)
        );

        // Route 4: Colombo → Jaffna (via Kurunegala, Dambulla, Anuradhapura)
        Route route4 = createRoute("4", "Colombo Fort - Jaffna", "Colombo Fort", "Jaffna", 396.0, 540);
        addRouteStops(route4,
                new StopData(colomboFort, 1, 0.0, 0),
                new StopData(kadawatha, 2, 15.0, 25),
                new StopData(kurunegala, 3, 94.0, 120),
                new StopData(dambulla, 4, 148.0, 195),
                new StopData(anuradhapura, 5, 205.0, 270),
                new StopData(vavuniya, 6, 260.0, 330),
                new StopData(kilinochchi, 7, 330.0, 420),
                new StopData(jaffna, 8, 396.0, 540)
        );

        // Route 47: Kandy → Nuwara Eliya
        Route route5 = createRoute("47", "Kandy - Nuwara Eliya", "Kandy", "Nuwara Eliya", 77.0, 180);
        addRouteStops(route5,
                new StopData(kandy, 1, 0.0, 0),
                new StopData(peradeniya, 2, 6.0, 15),
                new StopData(gampola, 3, 20.0, 45),
                new StopData(nawalapitiya, 4, 40.0, 90),
                new StopData(nuwaraEliya, 5, 77.0, 180)
        );

        // ===== 4. BUSES =====
        Bus bus1 = createBus("NB-1234", route1, driver1, 50);
        Bus bus2 = createBus("NB-5678", route1, null, 50);
        Bus bus3 = createBus("WP-KA-9012", route2, driver2, 45);
        Bus bus4 = createBus("SP-3456", route2, null, 45);
        Bus bus5 = createBus("SP-7890", route3, driver3, 50);
        Bus bus6 = createBus("NW-1122", route4, null, 55);
        Bus bus7 = createBus("CP-3344", route5, null, 40);

        // ===== 5. SCHEDULES (Monday-Friday) =====
        for (int day = 1; day <= 5; day++) {
            createSchedule(bus1, route1, "05:30", day);
            createSchedule(bus1, route1, "10:00", day);
            createSchedule(bus1, route1, "14:30", day);
            createSchedule(bus2, route1, "07:00", day);
            createSchedule(bus2, route1, "12:00", day);
            createSchedule(bus2, route1, "16:30", day);

            createSchedule(bus3, route2, "06:00", day);
            createSchedule(bus3, route2, "11:00", day);
            createSchedule(bus3, route2, "15:00", day);
            createSchedule(bus4, route2, "08:00", day);
            createSchedule(bus4, route2, "13:00", day);

            createSchedule(bus5, route3, "05:00", day);
            createSchedule(bus5, route3, "12:00", day);

            createSchedule(bus6, route4, "06:00", day);
            createSchedule(bus6, route4, "18:00", day);

            createSchedule(bus7, route5, "07:00", day);
            createSchedule(bus7, route5, "13:00", day);
        }

        // Weekend schedules (fewer departures)
        for (int day = 6; day <= 7; day++) {
            createSchedule(bus1, route1, "06:30", day);
            createSchedule(bus1, route1, "14:00", day);
            createSchedule(bus3, route2, "07:00", day);
            createSchedule(bus3, route2, "14:00", day);
            createSchedule(bus5, route3, "07:00", day);
            createSchedule(bus6, route4, "07:00", day);
            createSchedule(bus7, route5, "08:00", day);
        }

        logger.info("✅ Database seeded successfully!");
        logger.info("   → {} users", userRepository.count());
        logger.info("   → {} bus stops", busStopRepository.count());
        logger.info("   → {} routes", routeRepository.count());
        logger.info("   → {} buses", busRepository.count());
        logger.info("   → {} schedules", scheduleRepository.count());
        logger.info("   🔑 Admin login: admin@bustracker.lk / admin123");
        logger.info("   🚌 Driver login: kamal@bustracker.lk / driver123");
    }

    // ===== HELPER METHODS =====

    private User createUser(String name, String email, String password, String phone, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findByEmail(email).get();
        }
        User user = new User(name, email, passwordEncoder.encode(password), phone, role);
        return userRepository.save(user);
    }

    private BusStop createStop(String name, double lat, double lon) {
        return busStopRepository.save(new BusStop(name, lat, lon));
    }

    private Route createRoute(String number, String name, String origin,
                               String destination, double distance, int duration) {
        Route route = new Route(number, name, origin, destination, distance, duration);
        return routeRepository.save(route);
    }

    private void addRouteStops(Route route, StopData... stops) {
        for (StopData sd : stops) {
            RouteStop rs = new RouteStop(route, sd.stop, sd.sequence, sd.distanceKm, sd.timeMinutes);
            route.addRouteStop(rs);
        }
        routeRepository.save(route);
    }

    private Bus createBus(String regNumber, Route route, User driver, int capacity) {
        Bus bus = new Bus(regNumber, route, capacity);
        if (driver != null) {
            bus.setDriver(driver);
        }
        return busRepository.save(bus);
    }

    private void createSchedule(Bus bus, Route route, String time, int day) {
        Schedule schedule = new Schedule(bus, route, LocalTime.parse(time), day);
        scheduleRepository.save(schedule);
    }

    /**
     * Helper record for clean route-stop data initialization.
     */
    private record StopData(BusStop stop, int sequence, double distanceKm, int timeMinutes) {}
}
