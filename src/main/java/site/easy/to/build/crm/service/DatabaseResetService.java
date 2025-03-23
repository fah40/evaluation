package site.easy.to.build.crm.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DatabaseResetService {

    private final JdbcTemplate jdbcTemplate;

    // Liste des tables à ne pas supprimer
    List<String> tablesToKeep = List.of("users", "user_profile", "user_roles", "oauth_users", "roles", "customer", "customer_login_info");

    // Liste ordonnée des tables à supprimer, en respectant les contraintes de clés étrangères
    List<String> tableNames = Arrays.asList(
   "customer",
        "customer_login_info",
        "Configuration",
        "type_config",
        "budget",
        "depense",
        "file",
        "google_drive_file",
        "lead_action",
        "lead_settings",
        "ticket_settings",
        "contract_settings",
        "trigger_ticket",
        "trigger_contract",
        "trigger_lead",
        "email_template",
        "employee"
    );

    public DatabaseResetService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void resetTables() {
        // Désactiver les contraintes de clé étrangère temporairement
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        try {
            if (!validateTablesExist(tableNames)) {
               return;
            }

            for (String tableName : tableNames) {
                // Vider la table
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
                // Réinitialiser la séquence AUTO_INCREMENT à 1
                jdbcTemplate.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
            }
        } finally {
            // Réactiver les contraintes de clé étrangère
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    // Méthode pour valider si les tables existent
    public boolean validateTablesExist(List<String> tableNames) {
        for (String tableName : tableNames) {
            String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'crm' AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);
            if (count == null || count == 0) {
                return false;
            }
        }
        return true;
    }
}