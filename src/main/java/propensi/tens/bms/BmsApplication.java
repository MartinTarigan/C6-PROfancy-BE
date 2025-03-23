package propensi.tens.bms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import jakarta.transaction.Transactional;
import propensi.tens.bms.features.account_management.models.Admin;
import propensi.tens.bms.features.account_management.models.Outlet;
import propensi.tens.bms.features.account_management.repositories.AdminDb;
import propensi.tens.bms.features.account_management.repositories.BaristaDb;
import propensi.tens.bms.features.account_management.repositories.CLevelDb;
import propensi.tens.bms.features.account_management.repositories.HeadBarDb;
import propensi.tens.bms.features.account_management.repositories.OutletDb;
import propensi.tens.bms.features.account_management.repositories.ProbationBaristaDb;
import propensi.tens.bms.features.account_management.services.AccountService;

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
            admin.setAddress("Jl. admin");
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
        };
    }
}
