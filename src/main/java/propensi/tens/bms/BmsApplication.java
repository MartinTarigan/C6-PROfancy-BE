package propensi.tens.bms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import jakarta.transaction.Transactional;
import propensi.tens.bms.features.account_management.models.Admin;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.CLevel;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.AdminDb;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.CLevelDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.account_management.services.AccountService;
import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class BmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BmsApplication.class, args);
    }

    @Autowired
    private AccountService userService;

    @Bean
    @Transactional
    CommandLineRunner run(AdminDb adminDb, OutletDb outletDb, BaristaDb baristaDb,
                           CLevelDb cLevelDb, HeadBarDb headBarDb, ProbationBaristaDb probationBaristaDb) {
        return args -> {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(userService.hashPassword("admin"));
            admin.setFullName("Administrator");
            admin.setGender(true);
            admin.setPhoneNumber("08123456789");
            admin.setStatus("Active");
            admin.setIsVerified(true);
            adminDb.save(admin);

            
            Outlet outlet1 = new Outlet();
            outlet1.setName("Tens Coffee Margonda");
            outlet1.setLocation("Jl. Margonda Raya No.519, Pondok Cina, Beji, Depok, Jawa Barat 16424");

            Outlet outlet2 = new Outlet();
            outlet2.setName("Tens Coffee Kantin Vokasi UI");
            outlet2.setLocation("Kantin Vokasi UI, Kukusan, Beji, Depok, Jawa Barat 16425");

            Outlet outlet3 = new Outlet();
            outlet3.setName("Tens Coffee UIN Ciputat");
            outlet3.setLocation("Jl. Tarumanegara, Pisangan, Ciputat Tim., Tangerang Selatan, Banten 15419");

            Outlet outlet4 = new Outlet();
            outlet4.setName("Tens Coffee Pamulang");
            outlet4.setLocation("Jl. Pajajaran No.8, Pamulang Bar., Pamulang, Tangerang Selatan, Banten 15417");

            Outlet outlet5 = new Outlet();
            outlet5.setName("Tens Coffee UPN Veteran Jakarta");
            outlet5.setLocation("Jl. Pangkalan Jati 1 No.1e, Cinere, Depok, Jawa Barat 16513");

            outletDb.save(outlet1);
            outletDb.save(outlet2);
            outletDb.save(outlet3);
            outletDb.save(outlet4);
            outletDb.save(outlet5);

            
            baristaDb.save(createBarista("skinny.pete", "Skinny Pete", true, "08111111111", outlet1));
            baristaDb.save(createBarista("brandon.mayhew", "Brandon Mayhew", false, "08222222222", outlet2));
            baristaDb.save(createBarista("christian.ortega", "Christian Ortega", true, "08333333333", outlet3));

            
            cLevelDb.save(createCLevel("gustavo.fring", "Gustavo Fring", true, "08444444444", "CEO"));
            cLevelDb.save(createCLevel("saul.goodman", "Saul Goodman", false, "08555555555", "CMO"));
            cLevelDb.save(createCLevel("mike.ehrmantraut", "Mike Ehrmantraut", true, "08666666666", "CIOO"));

            
			HeadBar headBar1 = createHeadBar("hank.schrader", "Hank Schrader", true, "08777777777", outlet4);
			headBar1 = headBarDb.save(headBar1); // Simpan HeadBar terlebih dahulu
			outlet4.setHeadbar(headBar1);
			outletDb.save(outlet4); // Perbarui outlet agar menyimpan id headbar
			
			HeadBar headBar2 = createHeadBar("steve.gomez", "Steve Gomez", false, "08888888888", outlet5);
			headBar2 = headBarDb.save(headBar2);
			outlet5.setHeadbar(headBar2);
			outletDb.save(outlet5);
			
			HeadBar headBar3 = createHeadBar("victor.sal", "Victor Sal", true, "08999999999", outlet1);
			headBar3 = headBarDb.save(headBar3);
			outlet1.setHeadbar(headBar3);
			outletDb.save(outlet1);

            
            probationBaristaDb.save(createProbationBarista("jesse.pinkman", "Jesse Pinkman", true, "08101010101", outlet1));
            probationBaristaDb.save(createProbationBarista("todd.alquist", "Todd Alquist", false, "08202020202", outlet1));
            probationBaristaDb.save(createProbationBarista("andrea.cantillo", "Andrea Cantillo", true, "08303030303", outlet1));
        };
    }

    private Barista createBarista(String username, String fullName, boolean gender, String phone, Outlet outlet) {
        Barista barista = new Barista();
        barista.setUsername(username);
        barista.setPassword(userService.hashPassword("barista123"));
        barista.setFullName(fullName);
        barista.setGender(gender);
        barista.setPhoneNumber(phone);
        barista.setStatus("Active");
        barista.setIsVerified(true);
        barista.setOutlet(outlet);
        barista.setIsTrainee(false);
        return barista;
    }

    private CLevel createCLevel(String username, String fullName, boolean gender, String phone, String cLevelType) {
        CLevel clevel = new CLevel();
        clevel.setUsername(username);
        clevel.setPassword(userService.hashPassword("clevel123"));
        clevel.setFullName(fullName);
        clevel.setGender(gender);
        clevel.setPhoneNumber(phone);
        clevel.setStatus("Active");
        clevel.setIsVerified(true);
        clevel.setCLevelType(cLevelType);
        return clevel;
    }

    private HeadBar createHeadBar(String username, String fullName, boolean gender, String phone, Outlet outlet) {
        HeadBar headBar = new HeadBar();
        headBar.setUsername(username);
        headBar.setPassword(userService.hashPassword("headbar123"));
        headBar.setFullName(fullName);
        headBar.setGender(gender);
        headBar.setPhoneNumber(phone);
        headBar.setStatus("Active");
        headBar.setIsVerified(true);
        headBar.setOutlet(outlet);
        return headBar;
    }

    private ProbationBarista createProbationBarista(String username, String fullName, boolean gender, String phone,  Outlet outlet) {
        ProbationBarista probationBarista = new ProbationBarista();
        probationBarista.setUsername(username);
        probationBarista.setPassword(userService.hashPassword("probation123"));
        probationBarista.setFullName(fullName);
        probationBarista.setGender(gender);
        probationBarista.setPhoneNumber(phone);
        probationBarista.setStatus("Active");
        probationBarista.setIsVerified(true);
        probationBarista.setProbationEndDate(addDays(new Date(), 30));
		probationBarista.setOutlet(outlet);
        return probationBarista;
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}
